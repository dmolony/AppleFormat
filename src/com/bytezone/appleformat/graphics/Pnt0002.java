package com.bytezone.appleformat.graphics;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// $C0 (PNT) aux $0002 - Apple IIGS Super Hi-res Picture File (Apple Preferred Format) 
// -----------------------------------------------------------------------------------//
public class Pnt0002 extends Graphics
// -----------------------------------------------------------------------------------//
{
  private final List<Block> blocks = new ArrayList<> ();
  private Main mainBlock;
  private Multipal multipalBlock;

  private ColorTable defaultColorTable320 = new ColorTable (0, 0x00);
  private ColorTable defaultColorTable640 = new ColorTable (0, 0x80);

  // ---------------------------------------------------------------------------------//
  public Pnt0002 (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    byte[] buffer = dataRecord.data ();
    int offset = dataRecord.offset ();
    int length = dataRecord.length ();

    int ptr = offset;
    int maxLen = appleFile.getFileLength ();

    while (ptr < dataRecord.max ())
    {
      int len = Utility.getLong (buffer, ptr);

      if (len == 0 || len > ptr + maxLen)
      {
        System.out.printf ("Block length: %d%n", len);
        break;
      }

      String kind = HexFormatter.getPascalString (buffer, ptr + 4);
      byte[] data = new byte[Math.min (len, maxLen - ptr)];
      System.arraycopy (buffer, ptr, data, 0, data.length);

      switch (kind)
      {
        case "MAIN":
          mainBlock = new Main (kind, data);
          blocks.add (mainBlock);
          break;

        case "MULTIPAL":
          multipalBlock = new Multipal (kind, data);
          blocks.add (multipalBlock);
          break;

        case "PALETTES":
        case "MASK":
        case "PATS":
        case "SCIB":
          System.out.println (kind + " not written");
          blocks.add (new Block (kind, data));
          break;

        case "NOTE":                                  // Convert 3200
        case "SuperConvert":
        case "EOA ":                                  // DeluxePaint
        case "Platinum Paint":
        case "VSDV":
        case "VSMK":
        case "816/Paint":
        case "SHRConvert":
          blocks.add (new Block (kind, data));
          break;

        case "Nseq":
          blocks.add (new Nseq (kind, data));
          break;

        default:
          blocks.add (new Block (kind, data));
          System.out.printf ("Pnt0002 Unknown block type: %s in %s%n", kind, name);
          break;
      }

      ptr += len;
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    //    if (image == null)
    //      image = createColourImage ();
    //
    //    return image;
    return createColourImage ();
  }

  // ---------------------------------------------------------------------------------//
  private Image createMonochromeImage ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = dataRecord.data ();
    int offset = dataRecord.offset ();
    int length = dataRecord.length ();

    WritableImage image = new WritableImage (320, 200);
    PixelWriter pw = image.getPixelWriter ();

    int ptr = 0;

    for (int row = 0; row < 200; row++)
      for (int col = 0; col < 320; col += 2)
      {
        int pix1 = (buffer[ptr] & 0xF0) >> 4;
        int pix2 = buffer[ptr] & 0x0F;
        ptr++;

        if (pix1 > 0)
          pw.setColor (col, row, Color.BLACK);

        if (pix2 > 0)
          pw.setColor (col + 1, row, Color.BLACK);
      }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  private Image createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    if (mainBlock == null)
    {
      System.out.println ("No MAIN block in image file");
      return null;
    }

    WritableImage image =
        new WritableImage (mainBlock.pixelWidth, mainBlock.numScanLines);
    PixelWriter pixelWriter = image.getPixelWriter ();

    for (int row = 0; row < mainBlock.numScanLines; row++)
    {
      DirEntry dirEntry = mainBlock.scanLineDirectory[row];

      ColorTable colorTable = multipalBlock != null ?           //
          multipalBlock.colorTables[row] :                      //
          mainBlock.colorTables[dirEntry.colorTable];

      // CELTIC.32K has some lines with an SCB of $00FF, but setting those lines
      // to mode640 does not work correctly. Until I find out what is going on,
      // I will use the mainBlock.mode640 value.

      if (mainBlock.mode640)
        mode640Line (pixelWriter, row, colorTable);
      else
        mode320Line (pixelWriter, row, colorTable);
    }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  void mode320Line (PixelWriter pixelWriter, int row, ColorTable colorTable)
  // ---------------------------------------------------------------------------------//
  {
    if (colorTable == null)
      colorTable = defaultColorTable320;

    byte[] unpackedLine = mainBlock.unpackLine (row);

    int col = 0;
    int ptr = 0;

    for (int i = 0; i < unpackedLine.length; i++)
    {
      // get two indices from this byte
      int left = (unpackedLine[ptr] & 0xF0) >>> 4;
      int right = unpackedLine[ptr++] & 0x0F;

      // get pixel colors
      Color rgbLeft = colorTable.entries[left].color;
      Color rgbRight = colorTable.entries[right].color;

      // draw pixels
      pixelWriter.setColor (col++, row, rgbLeft);
      pixelWriter.setColor (col++, row, rgbRight);
    }
  }

  // ---------------------------------------------------------------------------------//
  void mode640Line (PixelWriter pixelWriter, int row, ColorTable colorTable)
  // ---------------------------------------------------------------------------------//
  {
    if (colorTable == null)
      colorTable = defaultColorTable640;

    byte[] unpackedLine = mainBlock.unpackLine (row);

    int col = 0;
    int ptr = 0;

    for (int i = 0; i < unpackedLine.length; i++)
    {
      // get four indices from this byte
      int p1 = (unpackedLine[ptr] & 0xC0) >>> 6;
      int p2 = (unpackedLine[ptr] & 0x30) >> 4;
      int p3 = (unpackedLine[ptr] & 0x0C) >> 2;
      int p4 = (unpackedLine[ptr++] & 0x03);

      // get pixel colors from mini-palette
      Color rgb3 = colorTable.entries[p3].color;
      Color rgb4 = colorTable.entries[p4 + 4].color;
      Color rgb1 = colorTable.entries[p1 + 8].color;
      Color rgb2 = colorTable.entries[p2 + 12].color;

      // draw pixels
      pixelWriter.setColor (col++, row, dither (rgb1, rgb2));
      pixelWriter.setColor (col++, row, dither (rgb3, rgb4));
    }
  }

  // ---------------------------------------------------------------------------------//
  private Color dither (Color left, Color right)
  // ---------------------------------------------------------------------------------//
  {
    if (left.equals (right))
      return left;

    // this is complete bollocks
    int red = (int) ((left.getRed () + right.getRed ()) / 2 * 255);
    int green = (int) ((left.getGreen () + right.getGreen ()) / 2 * 255);
    int blue = (int) ((left.getBlue () + right.getBlue ()) / 2 * 255);

    return Color.rgb (red, green, blue);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    if (mainBlock == null)
      text.append ("** Failure    : No MAIN block\n\n");

    for (Block block : blocks)
    {
      text.append (block);
      text.append ("\n\n");
    }

    return Utility.rtrim (text);
  }

  // -------------------------------------------------------------------------------//
  @Override
  public String buildExtras ()
  // -------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Kind ................. %s%n", mainBlock.kind));
    text.append (String.format ("MasterMode ........... %04X%n", mainBlock.masterMode));
    text.append (String.format ("PixelsPerScanLine .... %d / %d = %d bytes%n",
        mainBlock.pixelsPerScanLine, (mainBlock.mode640 ? 4 : 2), mainBlock.dataWidth));
    text.append (String.format ("NumColorTables ....... %d%n", mainBlock.numColorTables));
    text.append (String.format ("NumScanLines ......... %d%n%n", mainBlock.numScanLines));

    text.append ("Color Tables\n");
    text.append ("------------\n\n");

    text.append (" # ");
    for (int i = 0; i < 16; i++)
      text.append (String.format ("  %02X  ", i));
    text.deleteCharAt (text.length () - 1);
    text.deleteCharAt (text.length () - 1);
    text.append ("\n---");

    for (int i = 0; i < 16; i++)
      text.append (" ---- ");

    text.deleteCharAt (text.length () - 1);
    text.append ("\n");

    for (ColorTable colorTable : mainBlock.colorTables)
    {
      text.append (colorTable.toLine ());
      text.append ("\n");
    }

    text.append ("\nScan Lines\n");
    text.append ("----------\n\n");

    text.append (" #   Mode  Len       Packed Data\n");
    text.append ("---  ----  ---   ---------------------------------------");
    text.append ("--------------------------------\n");

    int lineSize = 24;
    for (int i = 0; i < mainBlock.scanLineDirectory.length; i++)
    {
      DirEntry dirEntry = mainBlock.scanLineDirectory[i];

      int ptr = mainBlock.dataOffsets[i];
      int max = ptr + dirEntry.numBytes;

      text.append (
          String.format ("%3d  %04X  %3d   ", i, dirEntry.mode, dirEntry.numBytes));

      int bytesRemaining = dirEntry.numBytes;

      while (true)
      {
        String hex = HexFormatter.getHexString (mainBlock.data, ptr,
            Math.min (lineSize, bytesRemaining));
        text.append (hex);

        ptr += lineSize;
        if (ptr >= max)
          break;

        bytesRemaining -= lineSize;
        text.append ("\n                 ");
      }

      text.append ("\n");

      if (true)
      {
        text.append ("\n");
        text.append (debug (mainBlock.data, mainBlock.dataOffsets[i], dirEntry.numBytes));
        text.append ("\n");
      }
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  String debug (byte[] buffer, int ptr, int length)
  // ---------------------------------------------------------------------------------//
  {
    int size = 0;
    int max = ptr + length;
    StringBuffer text = new StringBuffer ();

    while (ptr < max)
    {
      int type = (buffer[ptr] & 0xC0) >>> 6;        // 0-3
      int count = (buffer[ptr] & 0x3F) + 1;         // 1-64

      text.append (String.format ("%04X/%04d: %02X  (%d,%2d)  ", ptr, size, buffer[ptr],
          type, count));

      ptr++;

      if (type == 0)
      {
        text.append (
            String.format ("%s%n", HexFormatter.getHexString (buffer, ptr, count)));
        ptr += count;
        size += count;
      }
      else if (type == 1)
      {
        text.append (String.format ("%s%n", HexFormatter.getHexString (buffer, ptr, 1)));
        ptr++;
        size += count;
      }
      else if (type == 2)
      {
        text.append (String.format ("%s%n", HexFormatter.getHexString (buffer, ptr, 4)));
        ptr += 4;
        size += count * 4;
      }
      else
      {
        text.append (String.format ("%s%n", HexFormatter.getHexString (buffer, ptr, 1)));
        ptr++;
        size += count * 4;
      }
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  private class Block
  // ---------------------------------------------------------------------------------//
  {
    String kind;
    byte[] data;
    int size;

    // -------------------------------------------------------------------------------//
    public Block (String kind, byte[] data)
    // -------------------------------------------------------------------------------//
    {
      this.kind = kind;
      this.data = data;
      size = Utility.getLong (data, 0);
    }

    // -------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // -------------------------------------------------------------------------------//
    {
      StringBuilder text = new StringBuilder ();

      text.append (String.format ("Block ....................... %s%n", kind));
      text.append (String.format ("Size ........................ %04X  %<d%n%n", size));

      if (false)
      {
        int headerSize = 5 + kind.length ();
        text.append (HexFormatter.format (data, headerSize, data.length - headerSize));
      }

      return Utility.rtrim (text);
    }
  }

  // ---------------------------------------------------------------------------------//
  private class Main extends Block
  // ---------------------------------------------------------------------------------//
  {
    int masterMode;                     // 0 = Brooks, 0 = PNT 320 80 = PNT 640
    int pixelsPerScanLine;              // image width in pixels
    int numColorTables;                 // 1 = Brooks, 16 = Other (may be zero)
    int numScanLines;                   // image height in pixels

    ColorTable[] colorTables;           // [numColorTables]
    DirEntry[] scanLineDirectory;       // [numScanLines]

    boolean mode640;
    int dataWidth;                      // bytes per line
    int pixelWidth;                     // actual pixel width
    int[] dataOffsets;                  // pointer to each line of packed data

    // -------------------------------------------------------------------------------//
    public Main (String kind, byte[] data)
    // -------------------------------------------------------------------------------//
    {
      super (kind, data);

      int ptr = 5 + kind.length ();

      masterMode = Utility.getShort (data, ptr);
      pixelsPerScanLine = Utility.getShort (data, ptr + 2);
      numColorTables = Utility.getShort (data, ptr + 4);

      ptr += 6;

      mode640 = (masterMode & 0x80) != 0;
      dataWidth = pixelsPerScanLine / (mode640 ? 4 : 2);

      // this calculation assumes that in mode640 the screen has twice the
      // horizontal resolution, so the pixelsPerScanLine value is double the
      // actual number of pixels.
      pixelWidth = pixelsPerScanLine;
      if (mode640)
        pixelWidth /= 2;

      // create color tables
      colorTables = new ColorTable[numColorTables];
      for (int i = 0; i < numColorTables; i++)
      {
        colorTables[i] = new ColorTable (i, data, ptr);
        ptr += 32;
      }

      numScanLines = Utility.getShort (data, ptr);
      ptr += 2;

      // create directory entries
      scanLineDirectory = new DirEntry[numScanLines];
      for (int line = 0; line < numScanLines; line++)
      {
        DirEntry dirEntry = new DirEntry (data, ptr);
        scanLineDirectory[line] = dirEntry;
        ptr += 4;
      }

      // create array of offsets to unpacked data
      dataOffsets = new int[numScanLines];
      for (int line = 0; line < numScanLines; line++)
      {
        int numBytes = scanLineDirectory[line].numBytes;
        if (ptr + numBytes > data.length)
        {
          System.out.println ("breaking early");
          break;
        }

        dataOffsets[line] = ptr;
        ptr += numBytes;
      }
    }

    // -------------------------------------------------------------------------------//
    byte[] unpackLine (int line)
    // -------------------------------------------------------------------------------//
    {
      byte[] unpackedLine = new byte[dataWidth];
      DirEntry dirEntry = scanLineDirectory[line];

      int bytesUnpacked = Utility.unpackBytes (data, dataOffsets[line],
          dataOffsets[line] + dirEntry.numBytes, unpackedLine, 0);

      if (bytesUnpacked != dataWidth && true)
        System.out.printf ("Unexpected line width in %s - %3d  %3d  %3d%n", name, line,
            bytesUnpacked, dataWidth);

      return unpackedLine;
    }

    // -------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // -------------------------------------------------------------------------------//
    {
      StringBuilder text = new StringBuilder (super.toString ());

      text.append ("\n\n");
      text.append (String.format ("Master mode ................. %d%n", masterMode));
      text.append (
          String.format ("Pixels per scan line ........ %d%n", pixelsPerScanLine));
      text.append (String.format ("Num color tables ............ %d%n", numColorTables));
      text.append (String.format ("Num scan lines .............. %d%n", numScanLines));
      text.append (String.format ("Mode640 ..................... %s%n", mode640));
      text.append (String.format ("Data width .................. %d%n", dataWidth));

      return Utility.rtrim (text);
    }
  }

  // ---------------------------------------------------------------------------------//
  private class Nseq extends Block
  // ---------------------------------------------------------------------------------//
  {
    // -------------------------------------------------------------------------------//
    public Nseq (String kind, byte[] data)
    // -------------------------------------------------------------------------------//
    {
      super (kind, data);
    }

    // -------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // -------------------------------------------------------------------------------//
    {
      StringBuilder text = new StringBuilder (super.toString ());

      text.append ("\n\n");
      text.append (String.format ("Block ..... %s%n", kind));
      text.append (String.format ("Size ...... %04X  %<d%n%n", size));

      int ptr = 5 + kind.length ();
      while (ptr < data.length)
      {
        text.append (HexFormatter.format (data, ptr, 4) + "\n");
        ptr += 4;
      }

      return Utility.rtrim (text);
    }
  }

  //---------------------------------------------------------------------------------//
  private class Multipal extends Block
  //---------------------------------------------------------------------------------//
  {
    int numColorTables;
    ColorTable[] colorTables;

    // -------------------------------------------------------------------------------//
    public Multipal (String kind, byte[] data)
    // -------------------------------------------------------------------------------//
    {
      super (kind, data);

      int ptr = 5 + kind.length ();
      numColorTables = Utility.getShort (data, ptr);

      ptr += 2;
      colorTables = new ColorTable[numColorTables];

      for (int i = 0; i < numColorTables; i++)
      {
        if (ptr < data.length - 32)
          colorTables[i] = new ColorTable (i, data, ptr);
        else
          colorTables[i] = new ColorTable (i, 0x00);      // default empty table !! not
        // finished
        ptr += 32;
      }
    }
  }
}

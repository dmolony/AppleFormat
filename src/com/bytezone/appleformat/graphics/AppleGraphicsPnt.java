package com.bytezone.appleformat.graphics;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class AppleGraphicsPnt extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  private final List<Block> blocks = new ArrayList<> ();
  private Main mainBlock;
  private Multipal multipalBlock;
  private final boolean debug = false;

  private ColorTable defaultColorTable320 = new ColorTable (0, 0x00);
  private ColorTable defaultColorTable640 = new ColorTable (0, 0x80);
  private Image image;

  // ---------------------------------------------------------------------------------//
  public AppleGraphicsPnt (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    int ptr = 0;
    while (ptr < buffer.length)
    {
      int len = Utility.getLong (buffer, ptr);

      if (len == 0 || len > buffer.length)
      {
        //        System.out.printf ("Block length: %d%n", len);
        break;
      }

      String kind = HexFormatter.getPascalString (buffer, ptr + 4);
      byte[] data = new byte[Math.min (len, buffer.length - ptr)];
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
          if (debug)
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
          System.out.println ("Unknown block type: " + kind + " in " + name);
          break;
      }

      ptr += len;
    }

    if (false)
      for (int i = 0; i < 4; i++)
        for (int j = 4; j < 8; j++)
          System.out.printf ("%s %s  %s  %s%n", defaultColorTable640.entries[i].color,
              Utility.getColorName (defaultColorTable640.entries[i].color),
              defaultColorTable640.entries[j].color,
              Utility.getColorName (defaultColorTable640.entries[j].color));
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    if (image == null)
      image = createColourImage ();

    return image;
  }

  // ---------------------------------------------------------------------------------//
  private Image createMonochromeImage ()
  // ---------------------------------------------------------------------------------//
  {
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

    boolean mode320 = (mainBlock.masterMode & 0x80) == 0;

    // these calculations assume that in mode640 the screen has twice the
    // horizontal resolution, so the pixelsPerScanLine value is double the
    // actual number of pixels.
    int pixelWidth = mainBlock.pixelsPerScanLine;
    if (!mode320)
      pixelWidth /= 2;
    int dataWidth = pixelWidth / 2;           // two visible pixels per byte

    if (false)
    {
      System.out.printf ("Name                : %s%n", name);
      System.out.printf ("Mode                : %s%n", mode320 ? "mode320" : "mode640");
      System.out.printf ("Pixels per scan line: %3d%n", mainBlock.pixelsPerScanLine);
      System.out.printf ("Pixel width         : %3d%n", pixelWidth);
      System.out.printf ("Data width          : %3d%n", dataWidth);
    }

    WritableImage image = new WritableImage (pixelWidth, mainBlock.numScanLines);
    PixelWriter pw = image.getPixelWriter ();

    for (int row = 0; row < mainBlock.numScanLines; row++)
    {
      DirEntry dirEntry = mainBlock.scanLineDirectory[row];

      if (dirEntry.mode320 != mode320)        // haven't seen it happen yet
        System.out.printf ("mode mismatch: %02X  %02X  %s%n", dirEntry.mode,
            mainBlock.masterMode, name);

      ColorTable colorTable = multipalBlock != null ?           //
          multipalBlock.colorTables[row] :                      //
          mainBlock.colorTables[dirEntry.colorTable];

      if (dirEntry.mode320)                     // two pixels per byte
        mode320Line (pw, row, dataWidth, colorTable);
      else                                      // two dithered pixels per byte
        mode640Line (pw, row, dataWidth, colorTable);
    }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  void mode320Line (PixelWriter pw, int row, int dataWidth, ColorTable colorTable)
  // ---------------------------------------------------------------------------------//
  {
    if (colorTable == null)
      colorTable = defaultColorTable320;

    byte[] unpackedLine = mainBlock.unpackLine (row);

    int col = 0;
    int ptr = 0;

    for (int i = 0; i < dataWidth; i++)           // # of bytes per scanline
    {
      if (ptr >= unpackedLine.length)
      {
        System.out.printf ("too big: %d  %d  %s%n", ptr, unpackedLine.length, name);
        return;
      }

      // get two indices from this byte
      int left = (unpackedLine[ptr] & 0xF0) >>> 4;
      int right = unpackedLine[ptr++] & 0x0F;

      // get pixel colors
      Color rgbLeft = colorTable.entries[left].color;
      Color rgbRight = colorTable.entries[right].color;

      pw.setColor (col++, row, rgbLeft);
      pw.setColor (col++, row, rgbRight);
    }
  }

  // ---------------------------------------------------------------------------------//
  void mode640Line (PixelWriter pw, int row, int dataWidth, ColorTable colorTable)
  // ---------------------------------------------------------------------------------//
  {
    if (colorTable == null)
      colorTable = defaultColorTable640;

    byte[] unpackedLine = mainBlock.unpackLine (row);

    int col = 0;
    int ptr = 0;

    for (int i = 0; i < dataWidth; i++)           // # of bytes per scanline
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

      pw.setColor (col++, row, dither (rgb1, rgb2));
      pw.setColor (col++, row, dither (rgb3, rgb4));
    }
  }

  // ---------------------------------------------------------------------------------//
  private Color dither (Color left, Color right)
  // ---------------------------------------------------------------------------------//
  {
    if (left.equals (right))
      return left;

    int red = (int) ((left.getRed () + right.getRed ()) / 2 * 255);
    int green = (int) ((left.getGreen () + right.getGreen ()) / 2 * 255);
    int blue = (int) ((left.getBlue () + right.getBlue ()) / 2 * 255);

    return Color.rgb (red, green, blue);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
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
    ColorTable[] colorTables;           // [numColorTables]
    int numScanLines;                   // image height in pixels
    DirEntry[] scanLineDirectory;       // [numScanLines]
    boolean mode640;
    int dataWidth;                      // bytes per line
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
      mode640 = (masterMode & 0x80) != 0;
      dataWidth = pixelsPerScanLine / (mode640 ? 4 : 2);

      ptr += 6;
      colorTables = new ColorTable[numColorTables];
      for (int i = 0; i < numColorTables; i++)
      {
        colorTables[i] = new ColorTable (i, data, ptr);
        ptr += 32;
      }

      numScanLines = Utility.getShort (data, ptr);
      ptr += 2;

      scanLineDirectory = new DirEntry[numScanLines];
      dataOffsets = new int[numScanLines];

      for (int line = 0; line < numScanLines; line++)
      {
        DirEntry dirEntry = new DirEntry (data, ptr);
        scanLineDirectory[line] = dirEntry;
        ptr += 4;
      }

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
        System.out.printf ("Unexpected line width %3d  %5d  %3d  %3d  %s%n", line, 0,
            bytesUnpacked, dataWidth, name);

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

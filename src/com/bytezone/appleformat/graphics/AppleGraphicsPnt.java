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

  private byte[] fourBuf = new byte[4];
  private ColorTable defaultColorTable320 = new ColorTable (0, 0x00);
  private ColorTable defaultColorTable640 = new ColorTable (0, 0x80);

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

    this.buffer = mainBlock.unpackedBuffer;

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
    return createColourImage ();
    //    return createMonochromeImage ();
  }

  // ---------------------------------------------------------------------------------//
  Image createMonochromeImage ()
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
  Image createColourImage ()
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
    // visible pixels.
    int pixelWidth = mainBlock.pixelsPerScanLine / (mode320 ? 1 : 2);
    int dataWidth = pixelWidth / 2;

    if (false)
    {
      System.out.println (mode320 ? "mode320" : "mode640");
      System.out.printf ("Pixels per scan line: %3d%n", mainBlock.pixelsPerScanLine);
      System.out.printf ("Pixel width         : %3d%n", pixelWidth);
      System.out.printf ("Data width          : %3d%n", dataWidth);
    }

    WritableImage image = new WritableImage (pixelWidth, mainBlock.numScanLines);
    PixelWriter pw = image.getPixelWriter ();

    int ptr = 0;

    for (int row = 0; row < mainBlock.numScanLines; row++)
    {
      DirEntry dirEntry = mainBlock.scanLineDirectory[row];

      if (dirEntry.mode320 != mode320)        // haven't seen it happen yet
        System.out.printf ("mode mismatch: %02X  %02X%n", dirEntry.mode,
            mainBlock.masterMode);

      ColorTable colorTable = multipalBlock != null ?           //
          multipalBlock.colorTables[row] :                      //
          mainBlock.colorTables[dirEntry.colorTable];

      if (dirEntry.mode320)                     // two pixels per byte
        mode320Line (ptr, dataWidth, colorTable, pw, row);
      else                                      // two dithered pixels per byte
        mode640Line (ptr, dataWidth, colorTable, pw, row);

      ptr += dataWidth;
    }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  void mode320Line (int ptr, int dataWidth, ColorTable colorTable, PixelWriter pw,
      int row)
  // ---------------------------------------------------------------------------------//
  {
    if (colorTable == null)
      colorTable = defaultColorTable320;

    int col = 0;

    for (int i = 0; i < dataWidth; i++)           // # of bytes per scanline
    {
      if (ptr >= buffer.length)
      {
        System.out.printf ("too big: %d  %d%n", ptr, buffer.length);
        return;
      }

      // get two indices from this byte
      int left = (buffer[ptr] & 0xF0) >>> 4;
      int right = buffer[ptr++] & 0x0F;

      // get pixel colors
      Color rgbLeft = colorTable.entries[left].color;
      Color rgbRight = colorTable.entries[right].color;

      pw.setColor (col++, row, rgbLeft);
      pw.setColor (col++, row, rgbRight);
    }
  }

  // ---------------------------------------------------------------------------------//
  void mode640Line (int ptr, int dataWidth, ColorTable colorTable, PixelWriter pw,
      int row)
  // ---------------------------------------------------------------------------------//
  {
    if (colorTable == null)
      colorTable = defaultColorTable640;

    int col = 0;

    for (int i = 0; i < dataWidth; i++)           // # of bytes per scanline
    {
      // get four indices from this byte
      int p1 = (buffer[ptr] & 0xC0) >>> 6;
      int p2 = (buffer[ptr] & 0x30) >> 4;
      int p3 = (buffer[ptr] & 0x0C) >> 2;
      int p4 = (buffer[ptr++] & 0x03);

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
    if (left.equals (Color.BLACK) && right.equals (Color.WHITE))
      return Color.DARKGRAY;
    if (left.equals (Color.WHITE) && right.equals (Color.BLACK))
      return Color.DARKGRAY;
    if (left.equals (right))
      return left;

    int red = (int) (((left.getRed () + right.getRed ()) / 2) * 255);
    int green = (int) ((left.getGreen () + right.getGreen ()) / 2 * 255);
    int blue = (int) ((left.getBlue () + right.getBlue ()) / 2 * 255);

    return Color.rgb (red, green, blue);
  }

  // ---------------------------------------------------------------------------------//
  int unpack (byte[] buffer, int ptr, int max, byte[] newBuf, int newPtr)
  // ---------------------------------------------------------------------------------//
  {
    int savePtr = newPtr;

    while (ptr < max - 1)                 // minimum 2 bytes needed
    {
      int type = (buffer[ptr] & 0xC0) >>> 6;        // 0-3
      int count = (buffer[ptr++] & 0x3F) + 1;       // 1-64

      switch (type)
      {
        case 0:                                     // 2-65 bytes
          while (count-- != 0 && newPtr < newBuf.length && ptr < max)
            newBuf[newPtr++] = buffer[ptr++];
          break;

        case 1:                                     // 2 bytes
          byte b = buffer[ptr++];
          while (count-- != 0 && newPtr < newBuf.length)
            newBuf[newPtr++] = b;
          break;

        case 2:                                     // 5 bytes
          for (int i = 0; i < 4; i++)
            fourBuf[i] = ptr < max ? buffer[ptr++] : 0;

          while (count-- != 0)
            for (int i = 0; i < 4; i++)
              if (newPtr < newBuf.length)
                newBuf[newPtr++] = fourBuf[i];
          break;

        case 3:                                     // 2 bytes
          b = buffer[ptr++];
          count *= 4;
          while (count-- != 0 && newPtr < newBuf.length)
            newBuf[newPtr++] = b;
          break;
      }
    }

    return newPtr - savePtr;          // bytes unpacked
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append ("Paint");

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

      return text.toString ();
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
    byte[][] packedScanLines;
    boolean mode640;
    int dataWidth;
    byte[] unpackedBuffer;

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
      packedScanLines = new byte[numScanLines][];

      for (int line = 0; line < numScanLines; line++)
      {
        DirEntry dirEntry = new DirEntry (data, ptr);
        scanLineDirectory[line] = dirEntry;
        packedScanLines[line] = new byte[dirEntry.numBytes];
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

        System.arraycopy (data, ptr, packedScanLines[line], 0, numBytes);
        ptr += numBytes;
      }

      dataWidth = pixelsPerScanLine / (mode640 ? 4 : 2);

      unpackedBuffer = new byte[numScanLines * dataWidth];
      ptr = 0;
      for (int line = 0; line < numScanLines; line++)
      {
        // if (isOddAndEmpty (packedScanLines[line]))
        // {
        // System.out.println ("Odd number of bytes in empty buffer in " + name);
        // break;
        // }

        int bytesUnpacked = unpack (packedScanLines[line], 0,
            packedScanLines[line].length, unpackedBuffer, ptr);

        if (bytesUnpacked != dataWidth && false)
          System.out.printf ("Unexpected line width %3d  %5d  %3d  %3d%n", line, ptr,
              bytesUnpacked, dataWidth);

        ptr += dataWidth;
      }
    }

    // -------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // -------------------------------------------------------------------------------//
    {
      StringBuilder text = new StringBuilder (super.toString ());

      text.append (String.format ("Master mode ................. %d%n", masterMode));
      text.append (
          String.format ("Pixels per scan line ........ %d%n", pixelsPerScanLine));
      text.append (String.format ("Num color tables ............ %d%n", numColorTables));
      text.append (String.format ("Num scan lines .............. %d%n", numScanLines));
      text.append (String.format ("Mode640 ..................... %s%n", mode640));
      text.append (String.format ("Data width .................. %d%n", dataWidth));

      return text.toString ();
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
      StringBuilder text = new StringBuilder ();

      text.append (String.format ("Block ..... %s%n", kind));
      text.append (String.format ("Size ...... %04X  %<d%n%n", size));

      int ptr = 5 + kind.length ();
      while (ptr < data.length)
      {
        text.append (HexFormatter.format (data, ptr, 4) + "\n");
        ptr += 4;
      }

      text.deleteCharAt (text.length () - 1);

      return text.toString ();
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

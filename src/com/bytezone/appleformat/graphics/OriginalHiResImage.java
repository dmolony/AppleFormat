package com.bytezone.appleformat.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class OriginalHiResImage extends HiResImage
// -----------------------------------------------------------------------------------//
{
  private static final int WHITE = 0xFFFFFF;
  private static final int BLACK = 0x000000;
  private static final int[][] paletteTable = { { 9, 6 }, { 12, 3 } };

  private static boolean matchColourBits = false;

  private final int[] line = new int[280];
  private final int[] colourBits = new int[280];

  // ---------------------------------------------------------------------------------//
  public OriginalHiResImage (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);

    createImage ();
  }

  // ---------------------------------------------------------------------------------//
  public OriginalHiResImage (AppleFile appleFile, byte[] buffer, int offset, int length,
      int loadAddress)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length, loadAddress);

    createImage ();
  }

  // ---------------------------------------------------------------------------------//
  //  public OriginalHiResImage (String name, byte[] buffer, int fileType, int auxType, int eof)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    super (name, buffer, fileType, auxType, eof);
  //
  //    //    will call createImage () itself
  //  }

  // https://github.com/Michaelangel007/apple2_hgr_font_tutorial
  // hgr[ y ] = 0x2000 + (y/64)*0x28 + (y%8)*0x400 + ((y/8)&7)*0x80;
  // or... Y = aabbbccc
  //    address = BASE + aa * 0x28 + bb * 0x80 + ccc * 0x0400 + X

  // ---------------------------------------------------------------------------------//
  @Override
  protected void createMonochromeImage ()
  // ---------------------------------------------------------------------------------//
  {
    int rows = buffer.length <= 8192 ? 192 : 384;

    image = new BufferedImage (280, rows, BufferedImage.TYPE_BYTE_GRAY);
    DataBuffer dataBuffer = image.getRaster ().getDataBuffer ();

    int element = 0;

    for (int page = 0; page < rows / 192; page++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 8; j++)
          for (int k = 0; k < 8; k++)
          {
            int base = page * 0x2000 + i * 0x28 + j * 0x80 + k * 0x400;
            int max = Math.min (base + 40, buffer.length);
            for (int ptr = base; ptr < max; ptr++)
            {
              int value = buffer[ptr] & 0x7F;
              if ((buffer[ptr] & 0x80) != 0)
                System.out.printf ("bit shift pixel found in %s%n", name);
              for (int px = 0; px < 7; px++)
              {
                int val = (value >> px) & 0x01;
                dataBuffer.setElem (element++, val == 0 ? 0 : 255);
              }
            }
          }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected void createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ("creating colour");

    paletteIndex = paletteFactory.getCurrentPaletteIndex ();
    int rows = buffer.length <= 8192 ? 192 : 384;

    image = new BufferedImage (280, rows, BufferedImage.TYPE_INT_RGB);
    DataBuffer dataBuffer = image.getRaster ().getDataBuffer ();

    int element = 0;

    for (int page = 0; page < rows / 192; page++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 8; j++)
          for (int k = 0; k < 8; k++)
          {
            fillLine (page * 0x2000 + i * 0x28 + j * 0x80 + k * 0x400);
            for (int pixel : line)
              dataBuffer.setElem (element++, pixel);
          }
  }

  // ---------------------------------------------------------------------------------//
  private void fillLine (int base)
  // ---------------------------------------------------------------------------------//
  {
    Palette palette = paletteFactory.getCurrentPalette ();
    int[] colours = palette.getColours ();

    int max = Math.min (base + 40, buffer.length);
    int linePtr = 0;
    assert colourBits != null;

    for (int ptr = base; ptr < max; ptr++)
    {
      int colourBit = (buffer[ptr] & 0x80) >> 7;
      int value = buffer[ptr] & 0x7F;

      for (int px = 0; px < 7; px++)
      {
        colourBits[linePtr] = colourBit;        // store the colour bit
        int val = (value >> px) & 0x01;         // get the next pixel to draw
        int column = (ptr + px) % 2;            // is it in an odd or even column?
        line[linePtr++] = val == 0 ? 0 :        // black pixel
            colours[paletteTable[colourBit][column]]; // coloured pixel - use lookup table
      }
    }

    // convert consecutive ON pixels to white
    for (int x = 1; x < line.length; x++)       // skip first pixel, refer back
    {
      if (matchColourBits && colourBits[x - 1] != colourBits[x])
        continue;                   // only modify values with matching colour bits

      int px0 = line[x - 1];
      int px1 = line[x];
      if (px0 != BLACK && px1 != BLACK)
        line[x - 1] = line[x] = WHITE;
    }

    // optionally do physics
    if (colourQuirks)
      applyColourQuirks ();
  }

  // ---------------------------------------------------------------------------------//
  private boolean isColoured (int pixel)
  // ---------------------------------------------------------------------------------//
  {
    return pixel != BLACK && pixel != WHITE;
  }

  // ---------------------------------------------------------------------------------//
  private void applyColourQuirks ()
  // ---------------------------------------------------------------------------------//
  {
    for (int x = 3; x < line.length; x++)     // skip first three pixels, refer back
    {
      if (matchColourBits && colourBits[x - 2] != colourBits[x - 1])
        continue;                   // only modify values with matching colour bits

      int px0 = line[x - 3];
      int px1 = line[x - 2];
      int px2 = line[x - 1];
      int px3 = line[x];

      if (px1 == BLACK)
      {
        if (px3 == BLACK && px0 == px2 && isColoured (px0))           //     V-B-V-B
          line[x - 2] = px0;                                          // --> V-V-V-B
        else if (px3 == WHITE && px2 == WHITE && isColoured (px0))    //     V-B-W-W
          line[x - 2] = px0;                                          // --> V-V-W-W
      }
      else if (px2 == BLACK)
      {
        if (px0 == BLACK && px1 == px3 && isColoured (px3))           //     B-G-B-G 
          line[x - 1] = px3;                                          // --> B-G-G-G
        else if (px0 == WHITE && px1 == WHITE && isColoured (px3))    //     W-W-B-G
          line[x - 1] = px3;                                          // --> W-W-G-G
      }
    }
  }
}
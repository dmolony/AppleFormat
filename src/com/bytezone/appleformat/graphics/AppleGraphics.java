package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// $06 (BIN) aux $2000 or $4000
// -----------------------------------------------------------------------------------//
public class AppleGraphics extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static PaletteFactory paletteFactory = new PaletteFactory ();
  private static final int[][] paletteTable = { { 9, 6 }, { 12, 3 } };
  private static boolean matchColourBits = false;

  private final Color[] line = new Color[280];
  private final int[] colourBits = new int[280];

  private boolean colourQuirks = true;
  private boolean showColour = true;

  // ---------------------------------------------------------------------------------//
  public AppleGraphics (AppleFile appleFile, byte[] buffer, int offset, int length,
      int address)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    return showColour ? createColourImage () : createMonochromeImage ();
  }

  // ---------------------------------------------------------------------------------//
  protected Image createMonochromeImage ()
  // ---------------------------------------------------------------------------------//
  {
    int rows = length <= 8192 ? 192 : 384;
    int pages = rows / 192;

    WritableImage image = new WritableImage (280, rows);
    PixelWriter pw = image.getPixelWriter ();

    for (int page = 0; page < pages; page++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 8; j++)
          for (int k = 0; k < 8; k++)
          {
            int base = page * 0x2000 + i * 0x28 + j * 0x80 + k * 0x400 + offset;
            int max = Math.min (base + 40, offset + length);

            int row = page * 192 + i * 64 + j * 8 + k;
            int col = 0;

            for (int ptr = base; ptr < max; ptr++)
            {
              int value = buffer[ptr] & 0x7F;

              //    if ((buffer[ptr] & 0x80) != 0)
              //      System.out.printf ("bit shift pixel found in %s%n", name);

              for (int px = 0; px < 7; px++)
              {
                int val = (value >> px) & 0x01;
                pw.setColor (col, row, val == 0 ? Color.BLACK : Color.WHITE);
                ++col;
              }
            }
          }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  protected Image createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    int rows = length <= 8192 ? 192 : 384;

    WritableImage image = new WritableImage (280, rows);
    PixelWriter pw = image.getPixelWriter ();

    for (int page = 0; page < rows / 192; page++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 8; j++)
          for (int k = 0; k < 8; k++)
          {
            fillLine (page * 0x2000 + i * 0x28 + j * 0x80 + k * 0x400 + offset);

            int x = 0;
            int y = page * 192 + i * 64 + j * 8 + k;

            for (Color pixel : line)
              pw.setColor (x++, y, pixel);
          }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  private void fillLine (int base)
  // ---------------------------------------------------------------------------------//
  {
    Palette palette = paletteFactory.getCurrentPalette ();
    Color[] colours = palette.getColours ();

    int max = Math.min (base + 40, offset + length);
    int linePtr = 0;

    for (int ptr = base; ptr < max; ptr++)
    {
      int colourBit = (buffer[ptr] & 0x80) >> 7;
      int value = buffer[ptr] & 0x7F;

      for (int px = 0; px < 7; px++)
      {
        colourBits[linePtr] = colourBit;                // store the colour bit

        int val = (value >> px) & 0x01;                 // get the next pixel to draw
        int column = (ptr + px) % 2;                    // is column odd or even?

        line[linePtr++] = val == 0 ? Color.BLACK :      // black pixel
            colours[paletteTable[colourBit][column]];   // coloured pixel
      }
    }

    // convert consecutive ON pixels to white
    for (int x = 1; x < line.length; x++)       // skip first pixel, refer back
    {
      if (matchColourBits && colourBits[x - 1] != colourBits[x])
        continue;                   // only modify values with matching colour bits

      Color px0 = line[x - 1];
      Color px1 = line[x];
      if (px0 != Color.BLACK && px1 != Color.BLACK)
        line[x - 1] = line[x] = Color.WHITE;
    }

    if (colourQuirks)
      applyColourQuirks ();
  }

  // ---------------------------------------------------------------------------------//
  private boolean isColoured (Color pixel)
  // ---------------------------------------------------------------------------------//
  {
    return pixel != Color.BLACK && pixel != Color.WHITE;
  }

  // ---------------------------------------------------------------------------------//
  private void applyColourQuirks ()
  // ---------------------------------------------------------------------------------//
  {
    for (int x = 3; x < line.length; x++)       // skip first three pixels, refer back
    {
      if (matchColourBits && colourBits[x - 2] != colourBits[x - 1])
        continue;                       // only modify values with matching colour bits

      Color px0 = line[x - 3];
      Color px1 = line[x - 2];
      Color px2 = line[x - 1];
      Color px3 = line[x];

      if (px1 == Color.BLACK)
      {
        if (px3 == Color.BLACK && px0 == px2 && isColoured (px0))     //     V-B-V-B
          line[x - 2] = px0;                                          // --> V-V-V-B
        else if (px3 == Color.WHITE && px2 == Color.WHITE             //
            && isColoured (px0))                                      //     V-B-W-W
          line[x - 2] = px0;                                          // --> V-V-W-W
      }
      else if (px2 == Color.BLACK)
      {
        if (px0 == Color.BLACK && px1 == px3 && isColoured (px3))     //     B-G-B-G 
          line[x - 1] = px3;                                          // --> B-G-G-G
        else if (px0 == Color.WHITE && px1 == Color.WHITE             //
            && isColoured (px3))                                      //     W-W-B-G
          line[x - 1] = px3;                                          // --> W-W-G-G
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    return text.toString ();
  }
}

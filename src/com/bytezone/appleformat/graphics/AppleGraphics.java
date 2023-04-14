package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class AppleGraphics extends AbstractFormattedAppleFile
{
  // ---------------------------------------------------------------------------------//
  public AppleGraphics (AppleFile appleFile, byte[] buffer, int offset, int length,
      int address)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  //  @Override
  //  public void writeGraphics (GraphicsContext gc)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    Canvas canvas = gc.getCanvas ();
  //
  //    int rows = length <= 8192 ? 192 : 384;
  //
  //    canvas.setHeight (rows);
  //    canvas.setWidth (280);
  //
  //    gc.setFill (Color.GRAY);
  //    gc.fillRect (0, 0, canvas.getWidth (), canvas.getHeight ());
  //
  //    WritableImage wImage = new WritableImage (280, rows);
  //    PixelWriter pw2 = wImage.getPixelWriter ();
  //    ImageView imageView = new ImageView (wImage);
  //
  //    PixelWriter pw1 = canvas.getGraphicsContext2D ().getPixelWriter ();
  //    //    int element = 0;
  //
  //    for (int page = 0; page < rows / 192; page++)
  //      for (int i = 0; i < 3; i++)
  //        for (int j = 0; j < 8; j++)
  //          for (int k = 0; k < 8; k++)
  //          {
  //            int base = page * 0x2000 + i * 0x28 + j * 0x80 + k * 0x400 + offset;
  //            int max = Math.min (base + 40, offset + length);
  //
  //            int row = i * 64 + j * 8 + k;
  //            int col = 0;
  //
  //            for (int ptr = base; ptr < max; ptr++)
  //            {
  //              int value = buffer[ptr] & 0x7F;
  //
  //              //    if ((buffer[ptr] & 0x80) != 0)
  //              //      System.out.printf ("bit shift pixel found in %s%n", name);
  //
  //              for (int px = 0; px < 7; px++)
  //              {
  //                int val = (value >> px) & 0x01;
  //                pw1.setColor (col, row, val == 0 ? Color.BLACK : Color.WHITE);
  //                pw2.setColor (col, row, val == 0 ? Color.BLACK : Color.WHITE);
  //                ++col;
  //              }
  //            }
  //          }
  //  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image writeImage ()
  // ---------------------------------------------------------------------------------//
  {
    int rows = length <= 8192 ? 192 : 384;

    WritableImage image = new WritableImage (280, rows);
    PixelWriter pw = image.getPixelWriter ();
    //    imageView.setImage (image);

    for (int page = 0; page < rows / 192; page++)
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 8; j++)
          for (int k = 0; k < 8; k++)
          {
            int base = page * 0x2000 + i * 0x28 + j * 0x80 + k * 0x400 + offset;
            int max = Math.min (base + 40, offset + length);

            int row = i * 64 + j * 8 + k;
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
  protected void createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ("creating colour");

    //    paletteIndex = paletteFactory.getCurrentPaletteIndex ();
    //    int rows = buffer.length <= 8192 ? 192 : 384;
    //
    //    image = new BufferedImage (280, rows, BufferedImage.TYPE_INT_RGB);
    //    DataBuffer dataBuffer = image.getRaster ().getDataBuffer ();
    //
    //    int element = 0;
    //
    //    for (int page = 0; page < rows / 192; page++)
    //      for (int i = 0; i < 3; i++)
    //        for (int j = 0; j < 8; j++)
    //          for (int k = 0; k < 8; k++)
    //          {
    //            fillLine (page * 0x2000 + i * 0x28 + j * 0x80 + k * 0x400);
    //            for (int pixel : line)
    //              dataBuffer.setElem (element++, pixel);
    //          }
  }

  // ---------------------------------------------------------------------------------//
  private void fillLine (int base)
  // ---------------------------------------------------------------------------------//
  {
    //    Palette palette = paletteFactory.getCurrentPalette ();
    //    int[] colours = palette.getColours ();
    //
    //    int max = Math.min (base + 40, buffer.length);
    //    int linePtr = 0;
    //    assert colourBits != null;
    //
    //    for (int ptr = base; ptr < max; ptr++)
    //    {
    //      int colourBit = (buffer[ptr] & 0x80) >> 7;
    //      int value = buffer[ptr] & 0x7F;
    //
    //      for (int px = 0; px < 7; px++)
    //      {
    //        colourBits[linePtr] = colourBit;        // store the colour bit
    //        int val = (value >> px) & 0x01;         // get the next pixel to draw
    //        int column = (ptr + px) % 2;            // is it in an odd or even column?
    //        line[linePtr++] = val == 0 ? 0 :        // black pixel
    //            colours[paletteTable[colourBit][column]]; // coloured pixel - use lookup table
    //      }
    //    }

    // convert consecutive ON pixels to white
    //    for (int x = 1; x < line.length; x++)       // skip first pixel, refer back
    //    {
    //      if (matchColourBits && colourBits[x - 1] != colourBits[x])
    //        continue;                   // only modify values with matching colour bits
    //
    //      int px0 = line[x - 1];
    //      int px1 = line[x];
    //      if (px0 != BLACK && px1 != BLACK)
    //        line[x - 1] = line[x] = WHITE;
    //    }

    // optionally do physics
    //    if (colourQuirks)
    //      applyColourQuirks ();
  }

  // ---------------------------------------------------------------------------------//
  //  private boolean isColoured (int pixel)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    return pixel != BLACK && pixel != WHITE;
  //  }

  // ---------------------------------------------------------------------------------//
  private void applyColourQuirks ()
  // ---------------------------------------------------------------------------------//
  {
    //    for (int x = 3; x < line.length; x++)     // skip first three pixels, refer back
    //    {
    //      if (matchColourBits && colourBits[x - 2] != colourBits[x - 1])
    //        continue;                   // only modify values with matching colour bits
    //
    //      int px0 = line[x - 3];
    //      int px1 = line[x - 2];
    //      int px2 = line[x - 1];
    //      int px3 = line[x];
    //
    //      if (px1 == BLACK)
    //      {
    //        if (px3 == BLACK && px0 == px2 && isColoured (px0))           //     V-B-V-B
    //          line[x - 2] = px0;                                          // --> V-V-V-B
    //        else if (px3 == WHITE && px2 == WHITE && isColoured (px0))    //     V-B-W-W
    //          line[x - 2] = px0;                                          // --> V-V-W-W
    //      }
    //      else if (px2 == BLACK)
    //      {
    //        if (px0 == BLACK && px1 == px3 && isColoured (px3))           //     B-G-B-G 
    //          line[x - 1] = px3;                                          // --> B-G-G-G
    //        else if (px0 == WHITE && px1 == WHITE && isColoured (px3))    //     W-W-B-G
    //          line[x - 1] = px3;                                          // --> W-W-G-G
    //      }
    //    }
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

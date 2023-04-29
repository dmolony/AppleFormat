package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// C0 aux 0000
// -----------------------------------------------------------------------------------//
public class Pnt0000 extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  private ColorTable[] colorTables;
  byte[] unpackedBuffer;

  private Image image;

  // ---------------------------------------------------------------------------------//
  public Pnt0000 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    colorTables = new ColorTable[1];
    colorTables[0] = new ColorTable (0, this.buffer, 0);

    unpackedBuffer = new byte[Utility.calculateBufferSize (buffer, 0x222)];
    Utility.unpackBytes (buffer, 0x222, buffer.length, unpackedBuffer, 0);
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
  private Image createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    WritableImage image = new WritableImage (320, 200);
    PixelWriter pixelWriter = image.getPixelWriter ();

    for (int row = 0; row < 200; row++)
      mode320Line (pixelWriter, row);

    return image;
  }

  // ---------------------------------------------------------------------------------//
  void mode320Line (PixelWriter pixelWriter, int row)
  // ---------------------------------------------------------------------------------//
  {
    ColorTable colorTable = colorTables[0];

    int col = 0;
    int ptr = row * 160;

    for (int i = 0; i < 160; i++)
    {
      // get two indices from this byte
      int left = (unpackedBuffer[ptr] & 0xF0) >>> 4;
      int right = unpackedBuffer[ptr++] & 0x0F;

      // get pixel colors
      Color rgbLeft = colorTable.entries[left].color;
      Color rgbRight = colorTable.entries[right].color;

      // draw pixels
      pixelWriter.setColor (col++, row, rgbLeft);
      pixelWriter.setColor (col++, row, rgbRight);
    }
  }
}

package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class AppleGraphics3201 extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static final int COLOR_TABLE_SIZE = 32;

  private Image image;
  private ColorTable[] colorTables;
  private byte[] unpackedBuffer;

  // ---------------------------------------------------------------------------------//
  public AppleGraphics3201 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    colorTables = new ColorTable[200];
    for (int i = 0; i < colorTables.length; i++)
    {
      colorTables[i] = new ColorTable (i, this.buffer, 4 + i * COLOR_TABLE_SIZE);
      colorTables[i].reverse ();
    }

    unpackedBuffer = new byte[calculateBufferSize (buffer, 6404)];
    Utility.unpackBytes (buffer, 6404, buffer.length, unpackedBuffer, 0);
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

    for (int row = 0; row < colorTables.length; row++)
      mode320Line (pixelWriter, row);

    return image;
  }

  // ---------------------------------------------------------------------------------//
  void mode320Line (PixelWriter pixelWriter, int row)
  // ---------------------------------------------------------------------------------//
  {
    ColorTable colorTable = colorTables[row];

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

  // ---------------------------------------------------------------------------------//
  int calculateBufferSize (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    // int ptr = 0;
    int size = 0;
    while (ptr < buffer.length)
    {
      int type = (buffer[ptr] & 0xC0) >>> 6;        // 0-3
      int count = (buffer[ptr++] & 0x3F) + 1;       // 1-64

      if (type == 0)
      {
        ptr += count;
        size += count;
      }
      else if (type == 1)
      {
        ptr++;
        size += count;
      }
      else if (type == 2)
      {
        ptr += 4;
        size += count * 4;
      }
      else
      {
        ptr++;
        size += count * 4;
      }
    }

    return size;
  }
}

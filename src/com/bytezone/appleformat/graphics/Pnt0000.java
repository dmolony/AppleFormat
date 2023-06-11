package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// C0 (PNT) aux 0000 Paintworks SHR (packed pixel data)
// C0 (PNT) aux 8000 Paintworks Gold (packed pixel data)
// -----------------------------------------------------------------------------------//
public class Pnt0000 extends Graphics
// -----------------------------------------------------------------------------------//
{
  private ColorTable colorTable;
  byte[] unpackedBuffer;

  //  private Image image;
  int rows;

  // ---------------------------------------------------------------------------------//
  public Pnt0000 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    // 00000 - 00032  1 Color table
    // 00033 - 00545  empty
    // 00546 - eof    packed pixel data

    colorTable = new ColorTable (0, this.buffer, 0);

    unpackedBuffer = new byte[Utility.calculateBufferSize (buffer, 0x222)];
    Utility.unpackBytes (buffer, 0x222, buffer.length, unpackedBuffer, 0);
    rows = unpackedBuffer.length / 160;
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
  private Image createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    WritableImage image = new WritableImage (320, rows);
    PixelWriter pixelWriter = image.getPixelWriter ();

    for (int row = 0; row < rows; row++)
      mode320Line (pixelWriter, row);

    return image;
  }

  // ---------------------------------------------------------------------------------//
  void mode320Line (PixelWriter pixelWriter, int row)
  // ---------------------------------------------------------------------------------//
  {
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
  @Override
  public String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append ("Color Table\n\n #");
    for (int i = 0; i < 16; i++)
      text.append (String.format ("   %02X ", i));
    text.append ("\n--");
    for (int i = 0; i < 16; i++)
      text.append ("  ----");
    text.append ("\n");

    text.append (colorTable.toLine ());
    text.append ("\n");

    text.append ("\nScreen lines\n\n");
    for (int i = 0; i < rows; i++)
    {
      text.append (String.format ("Line: %02X  %<3d%n", i));
      text.append (HexFormatter.format (unpackedBuffer, i * 160, 160));
      text.append ("\n\n");
    }

    return Utility.rtrim (text);
  }
}

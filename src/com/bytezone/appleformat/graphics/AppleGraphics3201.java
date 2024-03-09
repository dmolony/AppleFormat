package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.FileProdos;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// 06 (BIN) aux ?     .3201
// -----------------------------------------------------------------------------------//
public class AppleGraphics3201 extends Graphics
// -----------------------------------------------------------------------------------//
{
  static final int COLOR_TABLE_SIZE = 32;

  private final ColorTable[] colorTables = new ColorTable[200];
  private byte[] unpackedBuffer;

  // ---------------------------------------------------------------------------------//
  public AppleGraphics3201 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    //     0 -     3  APP/0
    //     4 - 06403  200 color tables
    // 06404 - eof    packed pixel data

    int ptr = 4;
    for (int i = 0; i < colorTables.length; i++)
    {
      colorTables[i] = new ColorTable (i, this.buffer, ptr);
      colorTables[i].reverse ();
      ptr += COLOR_TABLE_SIZE;
    }

    unpackedBuffer = Utility.unpackBytes (buffer, ptr);
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
    WritableImage image = new WritableImage (320, 200);
    PixelWriter pixelWriter = image.getPixelWriter ();

    for (int row = 0; row < colorTables.length; row++)
      mode320Line (pixelWriter, row);

    return image;
  }

  // ---------------------------------------------------------------------------------//
  private void mode320Line (PixelWriter pixelWriter, int row)
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
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    FileProdos file = (FileProdos) appleFile;
    int aux = file.getAuxType ();
    String auxText = "";

    text.append (String.format ("Image File : %s%n", name));
    text.append (String.format ("File type  : $%02X    %s%n", file.getFileType (),
        file.getFileTypeText ()));
    text.append (String.format ("Aux type   : $%04X  %s%n", aux, auxText));
    text.append (String.format ("File size  : %,d%n", buffer.length));
    text.append (String.format ("EOF        : %,d%n", file.getFileLength ()));
    //    if (!failureReason.isEmpty ())
    //      text.append (String.format ("Failure    : %s%n", failureReason));

    return Utility.rtrim (text);
  }
}

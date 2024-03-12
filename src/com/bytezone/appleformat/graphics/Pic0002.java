package com.bytezone.appleformat.graphics;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.FileProdos;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// $C1 (PIC) aux $0002 - Super Hi-res 3200 color screen image (Brooks)
// -----------------------------------------------------------------------------------//
public class Pic0002 extends Graphics
// -----------------------------------------------------------------------------------//
{
  static final int COLOR_TABLE_SIZE = 32;
  static final int COLOR_TABLE_OFFSET_AUX_2 = 32_000;
  static final int NUM_COLOR_TABLES = 200;

  final ColorTable[] colorTables = new ColorTable[NUM_COLOR_TABLES];

  //  private Image image;

  // ---------------------------------------------------------------------------------//
  public Pic0002 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    // 00000 - 31999  pixel data 32,000 bytes
    // 32000 - 38400  200 color tables

    int ptr = COLOR_TABLE_OFFSET_AUX_2;
    for (int i = 0; i < NUM_COLOR_TABLES; i++)
    {
      colorTables[i] = new ColorTable (i, buffer, ptr);
      colorTables[i].reverse ();
      ptr += COLOR_TABLE_SIZE;
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
  private Image createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    WritableImage image = new WritableImage (320, NUM_COLOR_TABLES);
    PixelWriter pixelWriter = image.getPixelWriter ();

    for (int row = 0; row < NUM_COLOR_TABLES; row++)
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
      int left = (buffer[ptr] & 0xF0) >>> 4;
      int right = buffer[ptr++] & 0x0F;

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
    for (ColorTable colorTable : colorTables)
    {
      text.append (colorTable.toLine ());
      text.append ("\n");
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    FileProdos file = (FileProdos) appleFile;
    int aux = file.getAuxType ();
    String auxText = "";
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Image File : %s%n", name));
    text.append (String.format ("File type  : $%02X    %s%n", file.getFileType (),
        file.getFileTypeText ()));

    auxText = "Super Hi-Res 3200 color image";

    if (!auxText.isEmpty ())
      text.append (String.format ("Aux type   : $%04X  %s%n", aux, auxText));

    text.append (String.format ("File size  : %,d%n", buffer.length));
    text.append (String.format ("EOF        : %,d%n", file.getFileLength ()));
    if (!failureReason.isEmpty ())
      text.append (String.format ("Failure    : %s%n", failureReason));

    text.deleteCharAt (text.length () - 1);
    return text.toString ();
  }
}

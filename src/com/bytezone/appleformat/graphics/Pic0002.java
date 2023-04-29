package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.appleformat.ProdosConstants;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.FileProdos;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// $C1 (PIC) aux $0002 - Super Hi-res 3200 color screen image
// -----------------------------------------------------------------------------------//
public class Pic0002 extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static final int COLOR_TABLE_SIZE = 32;
  static final int COLOR_TABLE_OFFSET_AUX_2 = 32_000;

  final ColorTable[] colorTables = new ColorTable[200];

  String failureReason = "";
  private Image image;

  // ---------------------------------------------------------------------------------//
  public Pic0002 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    for (int i = 0; i < colorTables.length; i++)
    {
      colorTables[i] =
          new ColorTable (i, buffer, COLOR_TABLE_OFFSET_AUX_2 + i * COLOR_TABLE_SIZE);
      colorTables[i].reverse ();
    }
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
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append ("Pic");

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    FileProdos file = (FileProdos) appleFile;
    int aux = file.getAuxType ();
    String auxText = "";
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Image File : %s%nFile type  : $%02X    %s%n", name,
        file.getFileType (), ProdosConstants.fileTypes[file.getFileType ()]));

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

package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// C0 (PNT) aux 0001 IIGS Super Hi-Res Graphics Screen Image (packed)
// C0 (PNT) aux 1000 IIGS Super Hi-Res Graphics Screen Image (unpacked)
// C1 (PIC) aux 0000 IIGS Super Hi-Res Graphics Screen Image (unpacked)
// -----------------------------------------------------------------------------------//
public class Pic0000 extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static final int COLOR_TABLE_OFFSET_AUX_0 = 32_256;
  static final int COLOR_TABLE_SIZE = 32;
  static final int SCAN_LINES = 200;

  ColorTable[] colorTables;
  byte[] controlBytes;

  //  private Image image;

  // ---------------------------------------------------------------------------------//
  public Pic0000 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    //      0 - 31,999  pixel data 32,000 bytes
    // 32,000 - 32,199  200 control bytes (one per scan line)
    // 32,200 - 32,255  empty
    // 32,256 - 32,767  16 color tables of 32 bytes each

    controlBytes = new byte[SCAN_LINES];
    System.arraycopy (buffer, 32000, controlBytes, 0, controlBytes.length);

    colorTables = new ColorTable[16];
    int ptr = COLOR_TABLE_OFFSET_AUX_0;
    for (int i = 0; i < colorTables.length; i++)
    {
      colorTables[i] = new ColorTable (i, buffer, ptr);
      ptr += COLOR_TABLE_SIZE;
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    //    if (image == null)
    //    image = createColourImage ();

    //    return image;

    return createColourImage ();
  }

  // ---------------------------------------------------------------------------------//
  private Image createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    WritableImage image = new WritableImage (320, SCAN_LINES);
    PixelWriter pixelWriter = image.getPixelWriter ();

    ColorTable colorTable;
    boolean mode320 = true;

    for (int row = 0; row < SCAN_LINES; row++)
    {
      int controlByte = controlBytes[row] & 0xFF;
      colorTable = colorTables[controlByte & 0x0F];

      mode320 = (controlByte & 0x80) == 0;
      //        fillMode = (controlByte & 0x20) != 0;

      if (mode320)
        mode320Line (pixelWriter, row, colorTable);
      else
        mode640Line (pixelWriter, row, colorTable);
    }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  void mode320Line (PixelWriter pixelWriter, int row, ColorTable colorTable)
  // ---------------------------------------------------------------------------------//
  {
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
  void mode640Line (PixelWriter pixelWriter, int row, ColorTable colorTable)
  // ---------------------------------------------------------------------------------//
  {
    int col = 0;
    int ptr = row * 160;

    for (int i = 0; i < 160; i++)
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

      // draw pixels
      pixelWriter.setColor (col++, row, dither (rgb1, rgb2));
      pixelWriter.setColor (col++, row, dither (rgb3, rgb4));
    }
  }

  // ---------------------------------------------------------------------------------//
  private Color dither (Color left, Color right)
  // ---------------------------------------------------------------------------------//
  {
    if (left.equals (right))
      return left;

    int red = (int) ((left.getRed () + right.getRed ()) / 2 * 255);
    int green = (int) ((left.getGreen () + right.getGreen ()) / 2 * 255);
    int blue = (int) ((left.getBlue () + right.getBlue ()) / 2 * 255);

    return Color.rgb (red, green, blue);
  }
}

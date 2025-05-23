package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// C0 (PNT) aux 0001 IIGS Super Hi-Res Graphics Screen Image (packed)
// C0 (PNT) aux 1000 IIGS Super Hi-Res Graphics Screen Image (unpacked)
// C1 (PIC) aux 0000 IIGS Super Hi-Res Graphics Screen Image (unpacked)
// -----------------------------------------------------------------------------------//
public class Pic0000 extends Graphics
// -----------------------------------------------------------------------------------//
{
  static final int COLOR_TABLE_OFFSET_AUX_0 = 32_256;
  static final int COLOR_TABLE_SIZE = 32;
  static final int SCAN_LINES = 200;

  ColorTable[] colorTables;
  byte[] controlBytes;

  // ---------------------------------------------------------------------------------//
  public Pic0000 (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    setup ();
  }

  // ---------------------------------------------------------------------------------//
  public Pic0000 (AppleFile appleFile, Buffer dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

    setup ();
  }

  // ---------------------------------------------------------------------------------//
  private void setup ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = dataBuffer.data ();
    int offset = dataBuffer.offset ();
    int length = dataBuffer.length ();

    //      0 - 31,999  pixel data 32,000 bytes
    // 32,000 - 32,199  200 control bytes (one per scan line)
    // 32,200 - 32,255  empty
    // 32,256 - 32,767  16 color tables of 32 bytes each

    controlBytes = new byte[SCAN_LINES];
    System.arraycopy (buffer, offset + 32000, controlBytes, 0, controlBytes.length);

    colorTables = new ColorTable[16];
    int ptr = offset + COLOR_TABLE_OFFSET_AUX_0;

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
    byte[] buffer = dataBuffer.data ();
    int offset = dataBuffer.offset ();
    int length = dataBuffer.length ();

    int col = 0;
    int ptr = offset + row * 160;

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
    byte[] buffer = dataBuffer.data ();
    int offset = dataBuffer.offset ();
    int length = dataBuffer.length ();

    int col = 0;
    int ptr = offset + row * 160;

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

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    int aux = appleFile.getAuxType ();
    String auxText = "";
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Image File : %s%n", name));
    text.append (String.format ("File type  : $%02X    %s%n", appleFile.getFileType (),
        appleFile.getFileTypeText ()));

    auxText = "Super Hi-Res color image";

    if (!auxText.isEmpty ())
      text.append (String.format ("Aux type   : $%04X  %s%n", aux, auxText));

    text.append (String.format ("File size  : %,d%n", dataBuffer.data ().length));
    text.append (String.format ("EOF        : %,d%n", appleFile.getFileLength ()));
    if (!failureReason.isEmpty ())
      text.append (String.format ("Failure    : %s%n", failureReason));

    text.append (getDebugText ());

    text.deleteCharAt (text.length () - 1);
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  private String getDebugText ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = dataBuffer.data ();
    int offset = dataBuffer.offset ();
    int length = dataBuffer.length ();

    StringBuilder text = new StringBuilder ();
    text.append ("\n");

    if (controlBytes != null)
    {
      text.append ("Control Bytes\n-------------\n");
      for (int i = 0; i < controlBytes.length; i += 8)
      {
        for (int j = 0; j < 8; j++)
        {
          if (i + j >= controlBytes.length)
            break;
          text.append (String.format ("  %3d:  %02X  ", i + j, controlBytes[i + j]));
        }
        text.append ("\n");
      }
      text.append ("\n");
    }

    if (colorTables != null)
    {
      text.append ("Color Table\n-----------\n #");
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
    }

    text.append ("\nScreen lines\n------------\n");
    for (int i = 0; i < 200; i++)
    {
      text.append (String.format ("Line: %02X  %<3d%n", i));
      text.append (HexFormatter.format (buffer, i * 160, 160));
      text.append ("\n\n");
    }

    text.deleteCharAt (text.length () - 1);
    text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }
}

package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

//C0 (PNT) aux 8005 Dreamworld (packed pixel data)
//-----------------------------------------------------------------------------------//
public class Pnt8005 extends Graphics
//-----------------------------------------------------------------------------------//
{
  static final int COLOR_TABLE_SIZE = 32;

  private ColorTable[] colorTables;
  byte[] unpackedBuffer;

  //  private Image image;
  int rows;

  int imageType;
  int imageHeight;
  int imageWidth;

  // ---------------------------------------------------------------------------------//
  public Pnt8005 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    int ptr = buffer.length - 17;

    imageType = Utility.getShort (buffer, ptr);
    imageHeight = Utility.getShort (buffer, ptr + 2);
    imageWidth = Utility.getShort (buffer, ptr + 4);

    String id = Utility.getPascalString (buffer, ptr + 6);
    assert "DreamWorld".equals (id);

    int expectedLen = 32000 + 512;
    if (imageType == 0)                 // 256 colours
      expectedLen += (256 + 512);
    else                                // 3200 colours
      expectedLen += 6400;

    unpackedBuffer = new byte[expectedLen + 1024];
    LZW3 lzw3 = new LZW3 ();
    int bytes = lzw3.unpack (buffer, unpackedBuffer, expectedLen);

    colorTables = new ColorTable[imageHeight];

    ptr = 32000;
    for (int i = 0; i < colorTables.length; i++)
    {
      colorTables[i] = new ColorTable (i, this.buffer, ptr);
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
    WritableImage image = new WritableImage (imageWidth, imageHeight);
    PixelWriter pixelWriter = image.getPixelWriter ();

    return image;
  }
}

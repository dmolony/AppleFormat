package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.DataRecord;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

// C0 (PNT) aux 0003 (packed)
// C1 (PIC) aux 0001 (unpacked)
// -----------------------------------------------------------------------------------//
public class Pic0001 extends Graphics
// -----------------------------------------------------------------------------------//
{
  //  private Image image;

  // ---------------------------------------------------------------------------------//
  public Pic0001 (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    setup ();
  }

  // ---------------------------------------------------------------------------------//
  public Pic0001 (AppleFile appleFile, DataRecord dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

    setup ();
  }

  // ---------------------------------------------------------------------------------//
  private void setup ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = dataRecord.data ();
    int ptr = dataRecord.offset ();
    int length = dataRecord.length ();

    int mode = Utility.getShort (buffer, ptr);
    int rect1 = Utility.getLong (buffer, ptr + 2);
    int rect2 = Utility.getLong (buffer, ptr + 6);
    int version = Utility.getShort (buffer, ptr + 10);    // $8211

    System.out.printf ("Version: %04X%n", version);
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

    return image;
  }
}

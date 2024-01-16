package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.Utility;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

// C0 (PNT) aux 0003 (packed)
// C1 (PIC) aux 0001 (unpacked)
// -----------------------------------------------------------------------------------//
public class Pic0001 extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  //  private Image image;

  // ---------------------------------------------------------------------------------//
  public Pic0001 (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    int mode = Utility.getShort (this.buffer, 0);
    int rect1 = Utility.getLong (this.buffer, 2);
    int rect2 = Utility.getLong (this.buffer, 6);
    int version = Utility.getShort (this.buffer, 10);    // $8211

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

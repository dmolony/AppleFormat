package com.bytezone.appleformat.graphics;

import java.io.ByteArrayInputStream;

import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public class AppleImage extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static final byte[] pngHeader =
      { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

  Image image;

  // ---------------------------------------------------------------------------------//
  public AppleImage (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    image = new Image (new ByteArrayInputStream (buffer, 0, appleFile.getFileLength ()));
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
    return image;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Standard format image";
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isTiff (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length < 3)
      return false;

    String text = new String (buffer, 0, 2);
    if (!"II".equals (text) && !"MM".equals (text))
      return false;

    if (buffer[2] != 0x2A)
      return false;

    return true;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isGif (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length < 6)
      return false;

    String text = new String (buffer, 0, 6);
    return text.equals ("GIF89a") || text.equals ("GIF87a");
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isPng (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length < pngHeader.length)
      return false;

    for (int i = 0; i < pngHeader.length; i++)
      if (pngHeader[i] != buffer[i])
        return false;

    return true;
  }
}

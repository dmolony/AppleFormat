package com.bytezone.appleformat.file;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.filesystem.Buffer;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public class FileContainer implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected final FormattedAppleFile formattedAppleFile;

  // ---------------------------------------------------------------------------------//
  public FileContainer (FormattedAppleFile formattedAppleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.formattedAppleFile = formattedAppleFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getText ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getExtras ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getImage ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Buffer getDataBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getDataBuffer ();
  }

  // ---------------------------------------------------------------------------------//
  //  @Override
  //  public byte[] getBuffer ()
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    return formattedAppleFile.getBuffer ();
  //  }

  // ---------------------------------------------------------------------------------//
  //  @Override
  //  public int getOffset ()
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    return formattedAppleFile.getOffset ();
  //  }

  // ---------------------------------------------------------------------------------//
  //  @Override
  //  public int getLength ()
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    return formattedAppleFile.getLength ();
  //  }

  // ---------------------------------------------------------------------------------//
  @Override
  public ApplePreferences getPreferences ()
  // ---------------------------------------------------------------------------------//
  {
    return null;
  }
}

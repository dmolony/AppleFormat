package com.bytezone.appleformat.file;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public class DataFile extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public DataFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  public DataFile (AppleFile appleFile, Buffer dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    return emptyImage;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (!appleFile.hasData ())
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    return String.format ("File type: %02X  %<,d  %s", appleFile.getFileType (),
        appleFile.getFileTypeText ());
  }
}

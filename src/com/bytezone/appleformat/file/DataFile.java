package com.bytezone.appleformat.file;

import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public class DataFile extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  int fileType;

  // ---------------------------------------------------------------------------------//
  public DataFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, appleFile.read ());
  }

  // ---------------------------------------------------------------------------------//
  public DataFile (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public DataFile (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);

    this.fileType = appleFile.getFileType ();
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
    if (buffer == null)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    return String.format ("File type: %02X  %<,d", fileType);
  }
}

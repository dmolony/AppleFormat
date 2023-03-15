package com.bytezone.appleformat;

import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class DataFile extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  int fileType;

  // ---------------------------------------------------------------------------------//
  public DataFile (AppleFile appleFile, int type, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, type, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public DataFile (AppleFile appleFile, int type, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);

    this.fileType = type;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    return String.format ("File type: %02X  %<,d", fileType);
  }
}

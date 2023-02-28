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
    return "File type: " + fileType;
  }
}

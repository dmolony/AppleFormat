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
    super (appleFile, buffer);

    this.fileType = type;
  }

  // ---------------------------------------------------------------------------------//
  public DataFile (AppleFile appleFile, byte[] buffer, int offset, int length, int type)
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

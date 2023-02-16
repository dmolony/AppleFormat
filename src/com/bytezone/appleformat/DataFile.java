package com.bytezone.appleformat;

// -----------------------------------------------------------------------------------//
public class DataFile extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  int fileType;

  // ---------------------------------------------------------------------------------//
  public DataFile (String name, int type, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer);

    this.fileType = type;
  }

  // ---------------------------------------------------------------------------------//
  public DataFile (String name, byte[] buffer, int offset, int length, int type)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer, offset, length);

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

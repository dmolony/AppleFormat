package com.bytezone.appleformat;

public class DataFile extends AbstractFormattedAppleFile
{

  // ---------------------------------------------------------------------------------//
  public DataFile (String name, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer);
  }

  // ---------------------------------------------------------------------------------//
  public DataFile (String name, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer, offset, length);
  }
}

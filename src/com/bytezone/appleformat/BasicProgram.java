package com.bytezone.appleformat;

// -----------------------------------------------------------------------------------//
public abstract class BasicProgram
// -----------------------------------------------------------------------------------//
{
  static BasicPreferences basicPreferences;     // set by MenuHandler

  String name;
  byte[] buffer;
  int offset;
  int length;

  // ---------------------------------------------------------------------------------//
  public static void setBasicPreferences (BasicPreferences basicPreferences)
  // ---------------------------------------------------------------------------------//
  {
    BasicProgram.basicPreferences = basicPreferences;
  }

  // ---------------------------------------------------------------------------------//
  public BasicProgram (String name, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
  }

  // ---------------------------------------------------------------------------------//
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  public int getOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return offset;
  }

  // ---------------------------------------------------------------------------------//
  public int getLength ()
  // ---------------------------------------------------------------------------------//
  {
    return length;
  }
}

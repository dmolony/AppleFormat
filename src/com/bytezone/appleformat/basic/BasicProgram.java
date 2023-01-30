package com.bytezone.appleformat.basic;

import com.bytezone.appleformat.AbstractFormattedAppleFile;

// -----------------------------------------------------------------------------------//
public abstract class BasicProgram extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static BasicPreferences basicPreferences;     // set by MenuHandler

  //  protected final String name;
  //  protected final byte[] buffer;
  //  protected final int offset;
  //  protected final int length;

  // ---------------------------------------------------------------------------------//
  public BasicProgram (String name, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer, offset, length);

    //    this.name = name;
    //    this.buffer = buffer;
    //    this.offset = offset;
    //    this.length = length;
  }

  // ---------------------------------------------------------------------------------//
  public static void setBasicPreferences (BasicPreferences basicPreferences)
  // ---------------------------------------------------------------------------------//
  {
    BasicProgram.basicPreferences = basicPreferences;
  }
}

package com.bytezone.appleformat.basic;

import com.bytezone.appleformat.AbstractFormattedAppleFile;

// -----------------------------------------------------------------------------------//
public abstract class BasicProgram extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static BasicPreferences basicPreferences;     // set by MenuHandler

  // ---------------------------------------------------------------------------------//
  public BasicProgram (String name, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  public static void setBasicPreferences (BasicPreferences basicPreferences)
  // ---------------------------------------------------------------------------------//
  {
    BasicProgram.basicPreferences = basicPreferences;
  }
}

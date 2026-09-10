package com.bytezone.appleformat.file;

import com.bytezone.filesystem.AppleFile;

//-----------------------------------------------------------------------------------//
public class ZeroPage extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public ZeroPage (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append ("I'm a zero page");

    return text.toString ();
  }
}

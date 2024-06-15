package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

public class PascalSegment extends AbstractFormattedAppleFile
{
  // ---------------------------------------------------------------------------------//
  public PascalSegment (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    StringBuilder text = new StringBuilder ();

    text.append (appleFile);

    return Utility.rtrim (text);
  }
}

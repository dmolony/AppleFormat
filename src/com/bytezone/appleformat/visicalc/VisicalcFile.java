package com.bytezone.appleformat.visicalc;

import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

// -----------------------------------------------------------------------------------//
public class VisicalcFile extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  private Sheet sheet;

  // ---------------------------------------------------------------------------------//
  public VisicalcFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (sheet == null)
      sheet = new Sheet (dataBuffer.data ());

    StringBuilder text = new StringBuilder ();

    //    text.append ("Visicalc : " + name + "\n\n");
    text.append (sheet.getTextDisplay (false));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isVisicalcFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    Buffer fileBuffer = appleFile.getRawFileBuffer ();
    byte[] buffer = fileBuffer.data ();
    int ptr = fileBuffer.offset ();
    int max = fileBuffer.max ();

    int firstByte = buffer[ptr] & 0xFF;
    if (firstByte != 0xBE && firstByte != 0xAF)
      return false;

    //    int last = buffer.length - 1;
    int last = max - 1;

    while (buffer[last] == 0)
      last--;

    if (buffer[last] != (byte) 0x8D)
      return false;

    return true;
  }
}
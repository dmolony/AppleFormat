package com.bytezone.appleformat.text;

import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.FileDos;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class DosText extends Text
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public DosText (FileDos appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  public DosText (FileDos appleFile, Buffer fileBuffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, fileBuffer);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    Buffer fileBuffer = getDataBuffer ();
    int ptr = fileBuffer.offset ();
    byte[] buffer = fileBuffer.data ();
    int max = fileBuffer.max ();

    while (ptr < max)
    {
      int value = buffer[ptr++] & 0x7F;
      if (value == 0)
        break;
      text.append ((char) value);
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (
        String.format ("File length ................ %,9d%n", dataBuffer.length ()));

    return Utility.rtrim (text);
  }
}

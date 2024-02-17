package com.bytezone.appleformat.text;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class CpmText extends Text
// -----------------------------------------------------------------------------------//
{

  // ---------------------------------------------------------------------------------//
  public CpmText (AppleFile file, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (file, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int ptr = 0;
    while (ptr < buffer.length && buffer[ptr] != (byte) 0x1A)
    {
      String line = getLine (ptr);
      text.append (line + "\n");
      ptr += line.length () + 1;
      if (ptr < buffer.length && buffer[ptr - 1] == 0x0D && buffer[ptr] == 0x0A)
        ++ptr;

      while (ptr < buffer.length && buffer[ptr] == 0)
        ++ptr;
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  private String getLine (int ptr)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder line = new StringBuilder ();

    while (ptr < buffer.length && buffer[ptr] != 0x0D && buffer[ptr] != 0x0A)
      line.append ((char) (buffer[ptr++] & 0x7F));

    return line.toString ();
  }
}

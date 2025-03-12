package com.bytezone.appleformat.text;

import com.bytezone.appleformat.PreferencesFactory;
import com.bytezone.filesystem.Buffer;

// -----------------------------------------------------------------------------------//
public class TextFormatter
// -----------------------------------------------------------------------------------//
{
  Text text;
  TextPreferences textPreferences = PreferencesFactory.textPreferences;

  byte[] buffer;
  int offset;
  int length;

  Buffer dataBuffer;

  // ---------------------------------------------------------------------------------//
  public TextFormatter (Text text)
  // ---------------------------------------------------------------------------------//
  {
    this.text = text;
    //    this.buffer = text.getBuffer ();
    dataBuffer = text.getDataBuffer ();
    if (dataBuffer != null)
    {
      buffer = text.getDataBuffer ().data ();
      offset = text.getDataBuffer ().offset ();
      length = text.getDataBuffer ().length ();
    }
  }

  // ---------------------------------------------------------------------------------//
  public void append (StringBuilder text)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = offset;
    int max = offset + length;
    //    int length = this.text.getLength ();

    while (ptr < max)
    {
      if (buffer[ptr] == 0x00)
        break;

      String line = getLine (ptr);
      text.append (line + "\n");
      ptr += line.length () + 1;
      if (ptr < buffer.length && (buffer[ptr] & 0x7F) == 0x0A)
        ptr++;
    }
  }

  // ---------------------------------------------------------------------------------//
  private String getLine (int ptr)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder line = new StringBuilder ();
    //    int length = this.text.getLength ();
    int max = offset + length;

    while (ptr < max)
    {
      int c = buffer[ptr++] & 0x7F;
      if (c == 0x0D || c == 0x00)
        break;
      line.append ((char) c);
    }

    return line.toString ();
  }
}

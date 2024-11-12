package com.bytezone.appleformat.text;

import com.bytezone.appleformat.PreferencesFactory;

// -----------------------------------------------------------------------------------//
public class TextFormatter
// -----------------------------------------------------------------------------------//
{
  Text text;
  TextPreferences textPreferences = PreferencesFactory.textPreferences;

  byte[] buffer;
  int offset;
  int length;

  // ---------------------------------------------------------------------------------//
  public TextFormatter (Text text)
  // ---------------------------------------------------------------------------------//
  {
    this.text = text;
    //    this.buffer = text.getBuffer ();
    buffer = text.getDataRecord ().data ();
    offset = text.getDataRecord ().offset ();
    length = text.getDataRecord ().length ();
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

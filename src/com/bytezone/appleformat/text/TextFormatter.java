package com.bytezone.appleformat.text;

// -----------------------------------------------------------------------------------//
public class TextFormatter
// -----------------------------------------------------------------------------------//
{
  Text text;
  TextPreferences textPreferences;
  byte[] buffer;

  // ---------------------------------------------------------------------------------//
  public TextFormatter (Text text, TextPreferences textPreferences)
  // ---------------------------------------------------------------------------------//
  {
    this.text = text;
    this.textPreferences = textPreferences;
    this.buffer = text.getBuffer ();
  }

  // ---------------------------------------------------------------------------------//
  public void append (StringBuilder text)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = 0;
    while (ptr < buffer.length)
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

    while (ptr < buffer.length)
    {
      int c = buffer[ptr++] & 0x7F;
      if (c == 0x0D || c == 0x00)
        break;
      line.append ((char) c);
    }

    return line.toString ();
  }
}

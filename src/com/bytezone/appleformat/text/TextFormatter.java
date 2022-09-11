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
    while (ptr < buffer.length && buffer[ptr] != 0x00)
    {
      String line = getLine (ptr);
      text.append (line + "\n");
      ptr += line.length () + 1;
      if (ptr < buffer.length && buffer[ptr] == 0x0A)
        ptr++;
    }
  }

  // ---------------------------------------------------------------------------------//
  private String getLine (int ptr)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder line = new StringBuilder ();

    // added check for 0x00 eol 17/01/17
    while (ptr < buffer.length && buffer[ptr] != 0x0D && buffer[ptr] != 0x00)
      line.append ((char) (buffer[ptr++] & 0x7F));

    return line.toString ();
  }
}

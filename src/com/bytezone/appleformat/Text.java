package com.bytezone.appleformat;

// -----------------------------------------------------------------------------------//
public class Text
// -----------------------------------------------------------------------------------//
{
  static TextPreferences textPreferences;     // set by MenuHandler

  String name;
  byte[] buffer;
  String output;

  private final TextFormatter textFormatter;

  // ---------------------------------------------------------------------------------//
  public static void setTextPreferences (TextPreferences textPreferences)
  // ---------------------------------------------------------------------------------//
  {
    Text.textPreferences = textPreferences;
  }

  // ---------------------------------------------------------------------------------//
  public Text (String name, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
    this.buffer = buffer;

    textFormatter = new TextFormatter (this, textPreferences);
  }

  // ---------------------------------------------------------------------------------//
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    if (output == null)
    {
      StringBuilder text = new StringBuilder ();

      if (textPreferences.showHeader)
      {
        text.append ("Name : " + name + "\n");
        text.append (String.format ("End of file   : %,8d%n%n", buffer.length));
      }

      textFormatter.append (text);

      output = text.toString ();
    }

    return output;
  }
}

package com.bytezone.appleformat.text;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class Text extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static TextPreferences textPreferences;     // set by MenuHandler

  //  String name;
  //  byte[] buffer;
  String output;

  private final TextFormatter textFormatter;

  // ---------------------------------------------------------------------------------//
  public static void setTextPreferences (TextPreferences textPreferences)
  // ---------------------------------------------------------------------------------//
  {
    Text.textPreferences = textPreferences;
  }

  // ---------------------------------------------------------------------------------//
  public Text (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
    //    this.name = name;
    //    this.buffer = buffer;

    textFormatter = new TextFormatter (this, textPreferences);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    if (output == null)
    {
      StringBuilder text = new StringBuilder ();

      //      if (textPreferences.showHeader)
      //      {
      //        text.append ("Name : " + name + "\n");
      //        text.append (String.format ("End of file   : %,8d%n%n", length));
      //      }

      textFormatter.append (text);

      output = text.toString ();
    }

    return output;
  }
}

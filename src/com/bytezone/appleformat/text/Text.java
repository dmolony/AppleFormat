package com.bytezone.appleformat.text;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.appleformat.FormattedAppleFileFactory;
import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class Text extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  String output;

  private final TextFormatter textFormatter;

  // ---------------------------------------------------------------------------------//
  public Text (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);

    textFormatter = new TextFormatter (this);
    preferences = FormattedAppleFileFactory.textPreferences;
    optionsType = OptionsType.TEXT;
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
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (output == null)
    {
      StringBuilder text = new StringBuilder ();

      textFormatter.append (text);

      output = text.toString ();
    }

    return output;
  }
}

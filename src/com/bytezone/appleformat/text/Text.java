package com.bytezone.appleformat.text;

import com.bytezone.appleformat.FormattedAppleFileFactory;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class Text extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  String output;

  private final TextFormatter textFormatter;

  // ---------------------------------------------------------------------------------//
  public Text (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public Text (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);

    textFormatter = new TextFormatter (this);
    preferences = FormattedAppleFileFactory.textPreferences;
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

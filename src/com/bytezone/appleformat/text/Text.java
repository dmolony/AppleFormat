package com.bytezone.appleformat.text;

import java.util.List;

import com.bytezone.appleformat.PreferencesFactory;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.TextBlock;

// -----------------------------------------------------------------------------------//
public class Text extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  String output;

  private final TextFormatter textFormatter;

  // ---------------------------------------------------------------------------------//
  public Text (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    textFormatter = new TextFormatter (this);
    preferences = PreferencesFactory.textPreferences;
  }

  // ---------------------------------------------------------------------------------//
  public Text (AppleFile appleFile, Buffer dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

    textFormatter = new TextFormatter (this);
    preferences = PreferencesFactory.textPreferences;
  }

  // ---------------------------------------------------------------------------------//
  public Text (AppleFile appleFile, List<? extends TextBlock> textBlocks)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, textBlocks);

    textFormatter = new TextFormatter (this);
    preferences = PreferencesFactory.textPreferences;
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

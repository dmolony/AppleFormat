package com.bytezone.appleformat.text;

import java.util.List;

import com.bytezone.appleformat.PreferencesFactory;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.TextBlock;
import com.bytezone.filesystem.TextBlock.TextRecord;

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

  // ---------------------------------------------------------------------------------//
  protected String getRecordData (byte[] buffer, TextRecord record, byte mask)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int ptr = record.offset ();
    int length = record.length ();
    int totSeparators = 0;

    while (length-- > 0)
    {
      int value = buffer[ptr++] & mask;
      if (value == 0x0D)
      {
        text.append ((char) 0x2C);      // comma
        text.append ((char) 0x20);      // space
        totSeparators += 2;
      }
      else
      {
        text.append ((char) value);
        totSeparators = 0;
      }
    }

    for (int i = 0; i < totSeparators; i++)
      text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }
}

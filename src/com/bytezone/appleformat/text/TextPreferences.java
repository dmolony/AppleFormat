package com.bytezone.appleformat.text;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.ApplePreferences;

// -----------------------------------------------------------------------------------//
public class TextPreferences extends ApplePreferences
//-----------------------------------------------------------------------------------//
{
  public boolean showTextOffsets;
  public boolean merlinFormat = true;

  // ---------------------------------------------------------------------------------//
  public TextPreferences (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    super ("Text Preferences");
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append (String.format ("Show offsets .......... %s%n", showTextOffsets));
    text.append (String.format ("Show .S as Merlin ..... %s", merlinFormat));

    return text.toString ();
  }
}

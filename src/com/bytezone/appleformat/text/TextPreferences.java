package com.bytezone.appleformat.text;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.ApplePreferences;

// -----------------------------------------------------------------------------------//
public class TextPreferences extends ApplePreferences
//-----------------------------------------------------------------------------------//
{
  private static String PREFS_SHOW_OFFSETS = "ShowOffsets";
  private static String PREFS_MERLIN_FORMAT = "MerlinFormat";

  public boolean showTextOffsets;
  public boolean merlinFormat;

  // ---------------------------------------------------------------------------------//
  public TextPreferences (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    super ("Text Preferences", prefs);

    showTextOffsets = prefs.getBoolean (PREFS_SHOW_OFFSETS, false);
    merlinFormat = prefs.getBoolean (PREFS_MERLIN_FORMAT, false);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    prefs.putBoolean (PREFS_SHOW_OFFSETS, showTextOffsets);
    prefs.putBoolean (PREFS_MERLIN_FORMAT, merlinFormat);
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

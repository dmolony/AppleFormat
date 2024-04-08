package com.bytezone.appleformat.assembler;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class AssemblerPreferences extends ApplePreferences
// -----------------------------------------------------------------------------------//
{
  private static String PREFS_SHOW_TARGETS = "ShowTargets";
  private static String PREFS_SHOW_STRINGS = "ShowStrings";
  private static String PREFS_ZERO_OFFSET = "ZeroOffset";

  public boolean showTargets;
  public boolean showStrings;
  public boolean offsetFromZero;

  // ---------------------------------------------------------------------------------//
  public AssemblerPreferences (Preferences preferences)
  // ---------------------------------------------------------------------------------//
  {
    super ("Assembler Preferences", preferences, OptionsType.ASSEMBLER);

    showTargets = preferences.getBoolean (PREFS_SHOW_TARGETS, false);
    showStrings = preferences.getBoolean (PREFS_SHOW_STRINGS, false);
    offsetFromZero = preferences.getBoolean (PREFS_ZERO_OFFSET, false);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    preferences.putBoolean (PREFS_SHOW_TARGETS, showTargets);
    preferences.putBoolean (PREFS_SHOW_STRINGS, showStrings);
    preferences.putBoolean (PREFS_ZERO_OFFSET, offsetFromZero);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append (String.format ("Show targets .......... %s%n", showTargets));
    text.append (String.format ("Show strings .......... %s%n", showStrings));
    text.append (String.format ("Offset from zero ...... %s%n", offsetFromZero));

    return Utility.rtrim (text);
  }
}

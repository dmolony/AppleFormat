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
  public AssemblerPreferences (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    super ("Assembler Preferences", prefs);

    showTargets = prefs.getBoolean (PREFS_SHOW_TARGETS, false);
    showStrings = prefs.getBoolean (PREFS_SHOW_STRINGS, false);
    offsetFromZero = prefs.getBoolean (PREFS_ZERO_OFFSET, false);
  }

  // ---------------------------------------------------------------------------------//
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    prefs.putBoolean (PREFS_SHOW_TARGETS, showTargets);
    prefs.putBoolean (PREFS_SHOW_STRINGS, showStrings);
    prefs.putBoolean (PREFS_ZERO_OFFSET, offsetFromZero);
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

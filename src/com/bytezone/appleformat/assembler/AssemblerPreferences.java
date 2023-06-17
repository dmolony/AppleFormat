package com.bytezone.appleformat.assembler;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.ApplePreferences;

// -----------------------------------------------------------------------------------//
public class AssemblerPreferences extends ApplePreferences
// -----------------------------------------------------------------------------------//
{
  public boolean showTargets = true;
  public boolean showStrings = true;
  public boolean offsetFromZero = false;
  public boolean showHeader = true;

  // ---------------------------------------------------------------------------------//
  public AssemblerPreferences (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    super ("Assembler Preferences");
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
    text.append (String.format ("Show header ........... %s%n", showHeader));

    return text.toString ();
  }
}

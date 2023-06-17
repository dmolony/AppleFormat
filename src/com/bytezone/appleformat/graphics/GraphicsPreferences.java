package com.bytezone.appleformat.graphics;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.ApplePreferences;

// -----------------------------------------------------------------------------------//
public class GraphicsPreferences extends ApplePreferences
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public GraphicsPreferences (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    super ("Graphics Preferences", prefs);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    //    prefs.putBoolean (PREFS_SHOW_OFFSETS, showTextOffsets); 
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    return text.toString ();
  }
}

package com.bytezone.appleformat.graphics;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.ApplePreferences;

// -----------------------------------------------------------------------------------//
public class GraphicsPreferences extends ApplePreferences
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public GraphicsPreferences (Preferences preferences)
  // ---------------------------------------------------------------------------------//
  {
    super ("Graphics Preferences", preferences);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
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

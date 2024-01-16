package com.bytezone.appleformat;

import java.util.prefs.Preferences;

// -----------------------------------------------------------------------------------//
public abstract class ApplePreferences
// -----------------------------------------------------------------------------------//
{
  protected String name;
  protected Preferences preferences;

  // ---------------------------------------------------------------------------------//
  public ApplePreferences (String name, Preferences preferences)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
    this.preferences = preferences;
  }

  // ---------------------------------------------------------------------------------//
  public String getName ()
  // ---------------------------------------------------------------------------------//
  {
    return name;
  }

  // ---------------------------------------------------------------------------------//
  public abstract void save ();
  // ---------------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("Name ..................... %s%n", name);
  }
}
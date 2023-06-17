package com.bytezone.appleformat;

import java.util.prefs.Preferences;

// -----------------------------------------------------------------------------------//
public abstract class ApplePreferences
{
  protected String name;
  protected Preferences prefs;

  // ---------------------------------------------------------------------------------//
  public ApplePreferences (String name, Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
    this.prefs = prefs;
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

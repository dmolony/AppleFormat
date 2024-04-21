package com.bytezone.appleformat;

import java.util.prefs.Preferences;

// -----------------------------------------------------------------------------------//
public abstract class ApplePreferences
// -----------------------------------------------------------------------------------//
{
  protected String name;
  protected Preferences preferences;
  protected OptionsType optionsType;

  public enum OptionsType
  {
    APPLESOFT, ASSEMBLER, GRAPHICS, TEXT
  }

  // ---------------------------------------------------------------------------------//
  public ApplePreferences (String name, Preferences preferences, OptionsType optionsType)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
    this.preferences = preferences;
    this.optionsType = optionsType;
  }

  // ---------------------------------------------------------------------------------//
  public String getName ()
  // ---------------------------------------------------------------------------------//
  {
    return name;
  }

  // ---------------------------------------------------------------------------------//
  public OptionsType getOptionsType ()
  // ---------------------------------------------------------------------------------//
  {
    return optionsType;
  }

  // ---------------------------------------------------------------------------------//
  public abstract void save ();
  // ---------------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Name ..................... %s%n", name));
    text.append (String.format ("Type ..................... %s", optionsType));

    return text.toString ();
  }
}

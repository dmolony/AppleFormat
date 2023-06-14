package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.Preferences;

// -----------------------------------------------------------------------------------//
public class GraphicsPreferences extends Preferences
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public GraphicsPreferences ()
  // ---------------------------------------------------------------------------------//
  {
    super ("Graphics Preferences");
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

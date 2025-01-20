package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.PreferencesFactory;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

// -----------------------------------------------------------------------------------//
public abstract class Graphics extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected static PaletteFactory paletteFactory = new PaletteFactory ();
  protected String failureReason = "";

  // ---------------------------------------------------------------------------------//
  public Graphics (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    preferences = PreferencesFactory.graphicsPreferences;
  }

  // ---------------------------------------------------------------------------------//
  public Graphics (AppleFile appleFile, Buffer dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

    preferences = PreferencesFactory.graphicsPreferences;
  }
}

package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.FormattedAppleFileFactory;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public abstract class Graphics extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected static PaletteFactory paletteFactory = new PaletteFactory ();
  protected String failureReason = "";

  // ---------------------------------------------------------------------------------//
  public Graphics (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public Graphics (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);

    preferences = FormattedAppleFileFactory.graphicsPreferences;
  }
}

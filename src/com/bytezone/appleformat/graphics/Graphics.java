package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.FormattedAppleFileFactory;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public abstract class Graphics extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  //  public static GraphicsPreferences graphicsPreferences =
  //      FormattedAppleFileFactory.graphicsPreferences;

  // ---------------------------------------------------------------------------------//
  public Graphics (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    preferences = FormattedAppleFileFactory.graphicsPreferences;
    optionsType = OptionsType.GRAPHICS;
  }

  // ---------------------------------------------------------------------------------//
  public Graphics (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
  }
}

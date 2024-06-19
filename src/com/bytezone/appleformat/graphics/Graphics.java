package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.FormattedAppleFileFactory;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.DataRecord;

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

    preferences = FormattedAppleFileFactory.graphicsPreferences;
  }

  // ---------------------------------------------------------------------------------//
  public Graphics (AppleFile appleFile, DataRecord dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

    preferences = FormattedAppleFileFactory.graphicsPreferences;
  }
}

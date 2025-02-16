package com.bytezone.appleformat.file;

import com.bytezone.filesystem.AppleFile;

public class PascalCode extends AbstractFormattedAppleFile
{
  // ---------------------------------------------------------------------------------//
  public PascalCode (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Pascal Code File";
    //    return ((FilePascalCode) appleFile).getCatalogText ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return "No additional bollocks";
  }
}

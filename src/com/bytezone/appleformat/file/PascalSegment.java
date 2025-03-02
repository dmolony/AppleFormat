package com.bytezone.appleformat.file;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.FilePascalSegment;

public class PascalSegment extends AbstractFormattedAppleFile
{
  // ---------------------------------------------------------------------------------//
  public PascalSegment (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (dataBuffer.length () == 0)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    return ((FilePascalSegment) appleFile).getCatalogText ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return "No additional hairy bollocks";
  }
}

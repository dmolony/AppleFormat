package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.DataRecord;

public class PascalSegment extends AbstractFormattedAppleFile
{
  // ---------------------------------------------------------------------------------//
  public PascalSegment (AppleFile appleFile, DataRecord dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (dataRecord.length () == 0)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    StringBuilder text = new StringBuilder ();

    text.append (appleFile);

    return Utility.rtrim (text);
  }
}

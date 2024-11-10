package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.FilePascalCodeSegment;
import com.bytezone.filesystem.PascalProcedure;

public class CodeFilePascal extends AbstractFormattedAppleFile
{
  // ---------------------------------------------------------------------------------//
  //  public CodeFilePascal (FilePascalCodeSegment appleFile, byte[] buffer, int offset,
  //      int length)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    super (appleFile, buffer, offset, length);
  //  }

  // ---------------------------------------------------------------------------------//
  public CodeFilePascal (FilePascalCodeSegment appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (!appleFile.hasData ())
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    StringBuilder text = new StringBuilder ();

    for (PascalProcedure procedure : ((FilePascalCodeSegment) appleFile))
    {
      text.append (procedure.getCatalogLine ());
      text.append ("\n");
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return "No additional bollocks";
  }
}

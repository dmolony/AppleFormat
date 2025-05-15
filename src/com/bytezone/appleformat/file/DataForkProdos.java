package com.bytezone.appleformat.file;

import static com.bytezone.utility.Utility.formatMeta;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

// apparently unused - a data fork will be an actual prodos file
//-----------------------------------------------------------------------------------//
public class DataForkProdos extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public DataForkProdos (AppleFile appleFile)
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

    formatMeta (text, "File name", appleFile.getFileName ());
    formatMeta (text, "File type", 2, appleFile.getFileType (),
        appleFile.getFileTypeText ());
    formatMeta (text, "Aux", 4, appleFile.getAuxType ());
    formatMeta (text, "Blocks", 6, appleFile.getTotalBlocks ());
    formatMeta (text, "EOF", 8, appleFile.getFileLength ());

    return Utility.rtrim (text);
  }
}

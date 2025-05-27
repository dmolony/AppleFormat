package com.bytezone.appleformat.file;

import static com.bytezone.utility.Utility.formatText;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

//-----------------------------------------------------------------------------------//
public class UnknownFile extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public UnknownFile (AppleFile appleFile)
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

    return this.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    formatText (text, "File name", appleFile.getFileName ());
    formatText (text, "File type", 2, appleFile.getFileType (),
        appleFile.getFileTypeText ());
    formatText (text, "Aux", 4, appleFile.getAuxType ());
    formatText (text, "Blocks", 6, appleFile.getTotalBlocks ());
    formatText (text, "EOF", 8, appleFile.getFileLength ());

    return Utility.rtrim (text);
  }
}

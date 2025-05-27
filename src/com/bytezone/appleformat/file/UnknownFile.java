package com.bytezone.appleformat.file;

import static com.bytezone.utility.Utility.formatText;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.ForkNuFX;
import com.bytezone.filesystem.ForkProdos;

//-----------------------------------------------------------------------------------//
public class UnknownFile extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  String fileName;

  // ---------------------------------------------------------------------------------//
  public UnknownFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    if (appleFile instanceof ForkNuFX fork)
      fileName = fork.getParentFile ().getFileName ();
    else if (appleFile instanceof ForkProdos fork)
      fileName = fork.getParentFile ().getFileName ();
    else
      fileName = appleFile.getFileName ();
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

    formatText (text, "File name", fileName);
    formatText (text, "File type", 2, appleFile.getFileType (),
        appleFile.getFileTypeText ());
    formatText (text, "Aux", 4, appleFile.getAuxType ());
    formatText (text, "Blocks", 6, appleFile.getTotalBlocks ());
    formatText (text, "EOF", 8, appleFile.getFileLength ());

    return Utility.rtrim (text);
  }
}

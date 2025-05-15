package com.bytezone.appleformat.file;

import static com.bytezone.utility.Utility.formatMeta;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

//-----------------------------------------------------------------------------------//
public class UnknownFile extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  int aux;

  // ---------------------------------------------------------------------------------//
  public UnknownFile (AppleFile appleFile, Buffer dataBuffer, int aux)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataBuffer);

    this.aux = aux;
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

    formatMeta (text, "File name", appleFile.getFileName ());
    formatMeta (text, "File type", 2, appleFile.getFileType (),
        appleFile.getFileTypeText ());
    formatMeta (text, "Aux", 4, aux);
    formatMeta (text, "Blocks", 6, appleFile.getTotalBlocks ());
    formatMeta (text, "EOF", 8, appleFile.getFileLength ());

    return Utility.rtrim (text);
  }
}

package com.bytezone.appleformat.file;

import static com.bytezone.utility.Utility.formatMeta;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

//-----------------------------------------------------------------------------------//
public class DataFileProdos extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  int aux;

  // ---------------------------------------------------------------------------------//
  public DataFileProdos (AppleFile appleFile, Buffer dataRecord, int aux)
  // ---------------------------------------------------------------------------------//
  {
    //    super (appleFile, buffer, 0, buffer.length);
    super (appleFile);

    this.aux = aux;
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
    formatMeta (text, "Aux", 4, aux);
    formatMeta (text, "Blocks", 4, appleFile.getTotalBlocks ());
    formatMeta (text, "EOF", 4, appleFile.getFileLength ());

    return Utility.rtrim (text);
  }
}

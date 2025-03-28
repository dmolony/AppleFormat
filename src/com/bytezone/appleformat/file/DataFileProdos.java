package com.bytezone.appleformat.file;

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

    text.append (
        String.format ("File name .............. %-15s%n", appleFile.getFileName ()));
    text.append (String.format ("File type .............. %02X         %<3d  %s%n",
        appleFile.getFileType (), appleFile.getFileTypeText ()));
    text.append (String.format ("Aux .................... %04X%n", aux));
    text.append (String.format ("Blocks ................. %04X %<,9d%n",
        appleFile.getTotalBlocks ()));
    text.append (String.format ("EOF .................... %04X %<,9d%n",
        appleFile.getFileLength ()));

    return Utility.rtrim (text);
  }
}

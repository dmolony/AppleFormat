package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

//-----------------------------------------------------------------------------------//
public class DataFileProdos extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  int aux;

  // ---------------------------------------------------------------------------------//
  public DataFileProdos (AppleFile appleFile, byte[] buffer, int aux)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, 0, buffer.length);

    this.aux = aux;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    StringBuilder text = new StringBuilder ();

    text.append (
        String.format ("File name .............. %-15s%n", appleFile.getFileName ()));
    text.append (String.format ("File type .............. %02X  %s%n",
        appleFile.getFileType (), appleFile.getFileTypeText ()));
    text.append (String.format ("Aux .................... %04X%n", aux));
    text.append (
        String.format ("Blocks ................. %,9d%n", appleFile.getTotalBlocks ()));
    text.append (
        String.format ("EOF .................... %,9d%n", appleFile.getFileLength ()));

    return Utility.rtrim (text);
  }
}

package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

//-----------------------------------------------------------------------------------//
public class UnknownFile extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  int aux;

  // ---------------------------------------------------------------------------------//
  public UnknownFile (AppleFile appleFile, Buffer dataRecord, int aux)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

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

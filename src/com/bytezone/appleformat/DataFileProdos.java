package com.bytezone.appleformat;

import com.bytezone.filesystem.FileProdos;

//-----------------------------------------------------------------------------------//
public class DataFileProdos extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{

  // ---------------------------------------------------------------------------------//
  public DataFileProdos (FileProdos appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public DataFileProdos (FileProdos appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    FileProdos file = (FileProdos) appleFile;

    StringBuilder text = new StringBuilder ();

    text.append (String.format ("File name .............. %-15s%n", file.getFileName ()));
    text.append (String.format ("File type .............. %02X  %s%n",
        file.getFileType (), file.getFileTypeText ()));
    text.append (String.format ("Aux .................... %04X%n", file.getAuxType ()));
    text.append (
        String.format ("Blocks ................. %,9d%n", file.getTotalBlocks ()));
    text.append (
        String.format ("EOF .................... %,9d%n", file.getFileLength ()));

    return Utility.rtrim (text);
  }
}

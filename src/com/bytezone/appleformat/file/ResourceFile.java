package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.ForkProdos;

// -----------------------------------------------------------------------------------//
public class ResourceFile extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public ResourceFile (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public ResourceFile (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    StringBuilder text = new StringBuilder ();
    FileProdos file;
    if (appleFile instanceof ForkProdos forkProdos)
      file = forkProdos.getParentFile ();
    else
      file = (FileProdos) appleFile;

    text.append (
        String.format ("File name .............. %-15s%n", appleFile.getFileName ()));
    text.append (String.format ("File type .............. %02X  %s%n",
        appleFile.getFileType (), appleFile.getFileTypeText ()));
    text.append (String.format ("Aux .................... %04X%n", file.getAuxType ()));
    text.append (
        String.format ("Blocks ................. %,9d%n", appleFile.getTotalBlocks ()));
    text.append (
        String.format ("EOF .................... %,9d%n", appleFile.getFileLength ()));

    return Utility.rtrim (text);
  }
}

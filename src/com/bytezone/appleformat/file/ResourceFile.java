package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class ResourceFile extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public ResourceFile (AppleFile appleFile)
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

    StringBuilder text = new StringBuilder ();

    text.append (
        String.format ("File name .............. %-15s%n", appleFile.getFileName ()));
    text.append (String.format ("File type .............. %02X  %s%n",
        appleFile.getFileType (), appleFile.getFileTypeText ()));
    text.append (
        String.format ("Aux .................... %04X%n", appleFile.getAuxType ()));
    text.append (
        String.format ("Blocks ................. %,9d%n", appleFile.getTotalBlocks ()));
    text.append (
        String.format ("EOF .................... %,9d%n", appleFile.getFileLength ()));

    return Utility.rtrim (text);
  }
}

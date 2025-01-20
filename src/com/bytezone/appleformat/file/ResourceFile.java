package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

// -----------------------------------------------------------------------------------//
public class ResourceFile extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  int aux;

  // ---------------------------------------------------------------------------------//
  public ResourceFile (AppleFile appleFile, Buffer dataRecord, int aux)
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
    if (dataRecord.length () == 0)
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

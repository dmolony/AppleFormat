package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

// $AB GSB Apple IIgs BASIC Program
// $AC TDF Apple IIgs BASIC TDF           Toolbox Definition File
// $AD BDF Apple IIgs BASIC Data
//-----------------------------------------------------------------------------------//
public class TdfFile extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  int aux;
  int length;

  // ---------------------------------------------------------------------------------//
  public TdfFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    byte[] buffer = dataBuffer.data ();
    int ptr = dataBuffer.offset ();

    length = Utility.getShort (buffer, ptr);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (dataBuffer.length () == 0)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    StringBuilder text = new StringBuilder ();

    byte[] buffer = dataBuffer.data ();
    int ptr = dataBuffer.offset ();
    int max = dataBuffer.max ();

    String name = new String (buffer, ptr + 0x47, 20);
    text.append (String.format ("Toolbox Definition: %s%n%n", name));

    ptr += 0x5B;

    while (ptr < max)
    {
      text.append (getLine (buffer, ptr));
      text.append ("\n");

      int reclen1 = buffer[ptr + 11] & 0xFF;
      int reclen2 = buffer[ptr + 12] & 0xFF;
      ptr += reclen1 + reclen2;
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  private String getLine (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (int i = 0; i < 13; i++)
      text.append (String.format (" %02X", buffer[offset + i]));

    String name = Utility.getPascalString (buffer, offset + 13);
    text.append (String.format (" %-20s ", name));

    int extra = buffer[offset + 11] & 0xFF;
    offset += name.length () + 14;

    for (int i = 0; i < extra; i++)
      text.append (String.format (" %02X", buffer[offset + i]));

    return Utility.rtrim (text);
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
    text.append (String.format ("Length ................. %,9d%n", length));

    return Utility.rtrim (text);
  }
}

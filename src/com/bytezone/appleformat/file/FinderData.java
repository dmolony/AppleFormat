package com.bytezone.appleformat.file;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class FinderData extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  int version;

  // ---------------------------------------------------------------------------------//
  public FinderData (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    byte[] buffer = dataBuffer.data ();
    int ptr = dataBuffer.offset ();
    int max = dataBuffer.max ();

    version = buffer[ptr];

    text.append ("Name : " + name + "\n\n");

    if (version == 1)
    {
      ptr += 16;
      text.append (HexFormatter.getHexString (buffer, 0, ptr));
      text.append ("\n\n");
      while (buffer[ptr] != 0)
      {
        String line = HexFormatter.getHexString (buffer, ptr, 6);
        text.append (line + "  ");

        String name = HexFormatter.getPascalString (buffer, ptr + 6);
        text.append (name + "\n");

        ptr += 22;
      }
    }
    else if (version == 2)
    {
      int totFiles = buffer[34];
      ptr += 42;
      text.append (HexFormatter.format (buffer, 0, ptr));
      text.append ("\n\n");

      for (int i = 0; i < totFiles; i++)
      {
        String line = HexFormatter.getHexString (buffer, ptr, 8);
        text.append (line + "  ");

        ptr += 8;
        String name = HexFormatter.getPascalString (buffer, ptr);
        text.append (String.format ("%-20s ", name));

        ptr += name.length () + 1;
        text.append (String.format ("%02X%n", buffer[ptr++]));
      }
    }
    else
      text.append (String.format ("Unknown finder data version: %d%n", version));

    return Utility.rtrim (text);
  }
}

package com.bytezone.appleformat.text;

import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.FileDos;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class DosText extends Text
// -----------------------------------------------------------------------------------//
{
  //  private static String underline = "------------------------------------------"
  //      + "------------------------------------\n";

  //  boolean showTextOffsets = true;
  //  int gcd;
  //  int records;
  //  int textGaps;

  // ---------------------------------------------------------------------------------//
  public DosText (FileDos appleFile)
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

    Buffer fileBuffer = appleFile.getFileBuffer ();
    int ptr = fileBuffer.offset ();
    byte[] buffer = fileBuffer.data ();
    int max = fileBuffer.max ();

    while (ptr < max)
    {
      int value = buffer[ptr++] & 0x7F;
      if (value == 0)
        break;
      text.append ((char) value);
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (
        String.format ("File length ................ %,9d%n", dataBuffer.length ()));
    //    text.append (String.format ("Greatest Common Divisor .... %,9d%n", gcd));
    //    text.append (String.format ("Records .................... %,9d%n", records));
    //    text.append (String.format ("Text gaps .................. %,9d%n", textGaps));

    return Utility.rtrim (text);
  }
}

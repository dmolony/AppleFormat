package com.bytezone.appleformat.graphics;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class DosCharacterSet extends Graphics
// -----------------------------------------------------------------------------------//
{

  // ---------------------------------------------------------------------------------//
  public DosCharacterSet (AppleFile appleFile, Buffer dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int ptr = 4;
    byte[] buffer = dataBuffer.data ();

    for (int i = 32; i < 127; i++)
    {
      for (int j = 0; j < 8; j++)
      {
        int val = buffer[ptr++] & 0xFF;
        text.append (String.format ("%02X  ", val));

        if ((val & 0x80) != 0)          // half dot shift
          text.append (" ");

        for (int bit = 0; bit < 7; bit++)
        {
          text.append (String.format ("%s ", (val & 0x01) != 0 ? "o" : " "));
          val >>>= 1;
        }
        text.append ("\n");
      }
      text.append ("\n");
    }

    return Utility.rtrim (text);
  }
}

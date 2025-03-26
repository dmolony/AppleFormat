package com.bytezone.appleformat.text;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class MerlinText extends Text
// -----------------------------------------------------------------------------------//
{
  static int[] tabStops = { 10, 15, 30 };

  // ---------------------------------------------------------------------------------//
  public MerlinText (AppleFile appleFile, Buffer dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int ptr = dataBuffer.offset ();
    byte[] buffer = dataBuffer.data ();
    int max = dataBuffer.max ();

    while (ptr < max)
    {
      AssemblerLine line = new AssemblerLine (buffer, ptr, max);
      text.append (line.textLine);
      text.append ("\n");
      ptr += line.bufferLength;
    }

    return Utility.rtrim (text);
  }
}

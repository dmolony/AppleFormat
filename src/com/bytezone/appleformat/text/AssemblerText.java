package com.bytezone.appleformat.text;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class AssemblerText extends Text
// -----------------------------------------------------------------------------------//
{
  static int[] tabStops = { 10, 15, 30 };

  // ---------------------------------------------------------------------------------//
  public AssemblerText (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  public AssemblerText (AppleFile appleFile, Buffer dataRecord)
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

    while (ptr < max && buffer[ptr] != 0x00)
    {
      AssemblerLine line = new AssemblerLine (buffer, ptr, max);

      text.append (line.text ());
      text.append ("\n");

      ptr += line.bufferLength ();
    }

    if (ptr < max)
      text.append (String.format ("%n%n..... plus %,d more bytes", max - ptr));

    return Utility.rtrim (text);
  }
}

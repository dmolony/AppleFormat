package com.bytezone.appleformat.text;

import com.bytezone.filesystem.FileDos;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class DosText extends Text
// -----------------------------------------------------------------------------------//
{
  private static String underline = "------------------------------------------"
      + "------------------------------------\n";

  boolean showTextOffsets = true;
  int gcd;
  int records;
  int textGaps;

  // ---------------------------------------------------------------------------------//
  public DosText (FileDos appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    analyse ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    unknownLength (text);

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
    text.append (String.format ("Greatest Common Divisor .... %,9d%n", gcd));
    text.append (String.format ("Records .................... %,9d%n", records));
    text.append (String.format ("Text gaps .................. %,9d%n", textGaps));

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  private StringBuilder unknownLength (StringBuilder text)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = dataBuffer.data ();

    int ptr = 0;
    int size = buffer.length;
    int prevVal = 0;
    int nulls = 0;

    if (showTextOffsets)
    {
      text.append ("  Offset    Text values\n");
      text.append ("----------  " + underline);
    }
    else
    {
      text.append (" Text values\n");
      text.append (underline);
    }

    if (buffer.length == 0)
      return text;

    if (buffer[0] != 0 && showTextOffsets)
      text.append (String.format (" %,9d  ", ptr));   // zero, obviously

    while (ptr < size)
    {
      int val = buffer[ptr] & 0x7F;                   // strip hi-order bit

      if (val == 0)
        ++nulls;
      else
      {
        if (nulls > 0)                                // start of new record
        {
          nulls = 0;

          if (showTextOffsets)
          {
            char m = (ptr % gcd == 0) ? '*' : ' ';
            text.append (String.format ("%s%,9d  ", m, ptr));
          }
        }

        if (prevVal == 0x0D && showTextOffsets)        // end of field
          text.append (String.format ("%,10d  ", ptr));

        text.append ((char) val);
      }

      prevVal = val;
      ++ptr;
    }

    while (text.length () > 0 && text.charAt (text.length () - 1) == 0x0D)
      text.deleteCharAt (text.length () - 1);

    return text;
  }

  // ---------------------------------------------------------------------------------//
  private void analyse ()
  // ---------------------------------------------------------------------------------//
  {
    gcd = 0;
    records = 0;
    textGaps = ((FileDos) appleFile).getTextFileGaps ();

    int ptr = 0;
    int nulls = 0;

    byte[] buffer = dataBuffer.data ();

    while (ptr < buffer.length)
    {
      int val = buffer[ptr] & 0x7F;             // strip hi-order bit

      if (val == 0)
        ++nulls;
      else if (nulls > 0)
      {
        nulls = 0;
        gcd = gcd == 0 ? ptr : gcd (gcd, ptr);
        ++records;
      }

      ++ptr;
    }

    if (buffer[0] != 0)
      ++records;
  }

  // ---------------------------------------------------------------------------------//
  private int gcd (int a, int b)
  // ---------------------------------------------------------------------------------//
  {
    return a == 0 ? b : gcd (b % a, a);
  }
}

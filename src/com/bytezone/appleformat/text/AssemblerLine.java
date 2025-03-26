package com.bytezone.appleformat.text;

// -----------------------------------------------------------------------------------//
public class AssemblerLine
// -----------------------------------------------------------------------------------//
{
  static int[] tabStops = { 10, 15, 30 };

  int bufferLength;
  String textLine;

  // ---------------------------------------------------------------------------------//
  AssemblerLine (byte[] buffer, int ptr, int max)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    int spaceCount = 0;
    int start = ptr;

    int firstChar = buffer[ptr] & 0x7F;
    boolean inComment = firstChar == 0x2A || firstChar == 0x3B;

    while (ptr < max)
    {
      int value = buffer[ptr++] & 0x7F;
      if (value == 0)
        break;

      if (value == 0x0D)
        break;

      if (value == 0x20 && !inComment)
      {
        if (spaceCount < tabStops.length)
        {
          tab (text, spaceCount);
          ++spaceCount;
        }
      }

      text.append ((char) value);
    }

    textLine = text.toString ();
    bufferLength = ptr - start;
  }

  // ---------------------------------------------------------------------------------//
  private void tab (StringBuilder text, int count)
  // ---------------------------------------------------------------------------------//
  {
    int max = tabStops[count];

    while (text.length () < max)
      text.append (" ");
  }
}

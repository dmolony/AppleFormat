package com.bytezone.appleformat.text;

import static com.bytezone.appleformat.Utility.ASCII_ASTERISK;
import static com.bytezone.appleformat.Utility.ASCII_CR;
import static com.bytezone.appleformat.Utility.ASCII_DOUBLE_QUOTE;
import static com.bytezone.appleformat.Utility.ASCII_SEMI_COLON;
import static com.bytezone.appleformat.Utility.ASCII_SINGLE_QUOTE;
import static com.bytezone.appleformat.Utility.ASCII_SPACE;

// -----------------------------------------------------------------------------------//
public class AssemblerLine
// -----------------------------------------------------------------------------------//
{
  static int[] tabStops = { 10, 15, 30 };

  private final int bufferLength;
  private final String textLine;

  // ---------------------------------------------------------------------------------//
  AssemblerLine (byte[] buffer, int ptr, int max)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    int spaceCount = 0;
    int start = ptr;

    int firstChar = buffer[ptr] & 0x7F;
    boolean inComment = firstChar == ASCII_ASTERISK || firstChar == ASCII_SEMI_COLON;
    boolean inDoubleQuote = false;
    boolean inSingleQuote = false;

    while (ptr < max)
    {
      int value = buffer[ptr++] & 0x7F;
      if (value == 0)
        break;

      if (value == ASCII_CR || value == 0x7F)
        break;

      if (value == ASCII_DOUBLE_QUOTE)
        inDoubleQuote = !inDoubleQuote;

      if (value == ASCII_SINGLE_QUOTE)
        inSingleQuote = !inSingleQuote;

      if (value == ASCII_SPACE && !inComment & !inDoubleQuote & !inSingleQuote)
        tab (text, spaceCount++);

      if (value == ASCII_SEMI_COLON && !inComment && spaceCount < tabStops.length)
      {
        spaceCount = tabStops.length - 1;
        tab (text, spaceCount++);
        inComment = true;
        text.append (" ");
      }

      text.append ((char) value);
    }

    textLine = text.toString ();
    bufferLength = ptr - start;
  }

  // ---------------------------------------------------------------------------------//
  String text ()
  // ---------------------------------------------------------------------------------//
  {
    return textLine;
  }

  // ---------------------------------------------------------------------------------//
  int bufferLength ()
  // ---------------------------------------------------------------------------------//
  {
    return bufferLength;
  }

  // ---------------------------------------------------------------------------------//
  private void tab (StringBuilder text, int count)
  // ---------------------------------------------------------------------------------//
  {
    if (count >= tabStops.length)
      return;

    int max = tabStops[count];

    while (text.length () < max)
      text.append (" ");
  }
}

package com.bytezone.appleformat.text;

import static com.bytezone.appleformat.Utility.ASCII_ASTERISK;
import static com.bytezone.appleformat.Utility.ASCII_CR;
import static com.bytezone.appleformat.Utility.ASCII_DOUBLE_QUOTE;
import static com.bytezone.appleformat.Utility.ASCII_LF;
import static com.bytezone.appleformat.Utility.ASCII_SEMI_COLON;
import static com.bytezone.appleformat.Utility.ASCII_SINGLE_QUOTE;
import static com.bytezone.appleformat.Utility.ASCII_SPACE;
import static com.bytezone.appleformat.Utility.ASCII_TAB;

// -----------------------------------------------------------------------------------//
public class AssemblerLine
// -----------------------------------------------------------------------------------//
{
  static int[] tabStops = { 10, 16, 26 };

  private final int bufferLength;
  private final String textLine;

  // ---------------------------------------------------------------------------------//
  AssemblerLine (byte[] buffer, int ptr, int max)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int start = ptr;

    int firstChar = buffer[ptr] & 0x7F;
    boolean inComment = (firstChar == ASCII_ASTERISK || firstChar == ASCII_SEMI_COLON);
    boolean inDoubleQuote = false;
    boolean inSingleQuote = false;
    int fieldNo = 0;                              // label field

    while (ptr < max)
    {
      int value = buffer[ptr++] & 0x7F;           // remove hi bit

      if (value == ASCII_CR || value == ASCII_LF || value == 0x7F || value == 0)
        break;

      if (value == ASCII_DOUBLE_QUOTE && !inSingleQuote)
        inDoubleQuote = !inDoubleQuote;

      if (value == ASCII_SINGLE_QUOTE && !inDoubleQuote)
        inSingleQuote = !inSingleQuote;

      if (value == ASCII_SEMI_COLON && !inComment && !inDoubleQuote && !inSingleQuote)
        inComment = true;

      // spaces and tabs need to be padded to the naext tab stop
      if ((value == ASCII_SPACE || value == ASCII_TAB) && !inComment && !inDoubleQuote
          && !inSingleQuote)
      {
        while (text.length () < tabStops[fieldNo])
          text.append (' ');
        fieldNo++;
      }
      else
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

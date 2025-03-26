package com.bytezone.appleformat.text;

// -----------------------------------------------------------------------------------//
public class AssemblerLine
// -----------------------------------------------------------------------------------//
{
  static int[] tabStops = { 10, 15, 30 };
  static final int SPACE = 0x20;
  static final int DOUBLE_QUOTE = 0x22;
  static final int SINGLE_QUOTE = 0x27;
  static final int RETURN = 0x0D;
  static final int SEMI_COLON = 0x3B;
  static final int ASTERISK = 0x2A;

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
    boolean inComment = firstChar == ASTERISK || firstChar == SEMI_COLON;
    boolean inDoubleQuote = false;
    boolean inSingleQuote = false;

    while (ptr < max)
    {
      int value = buffer[ptr++] & 0x7F;
      if (value == 0)
        break;

      if (value == RETURN || value == 0x7F)
        break;

      if (value == DOUBLE_QUOTE)
        inDoubleQuote = !inDoubleQuote;

      if (value == SINGLE_QUOTE)
        inSingleQuote = !inSingleQuote;

      if (value == SPACE && !inComment & !inDoubleQuote & !inSingleQuote)
        tab (text, spaceCount++);

      if (value == SEMI_COLON && !inComment && spaceCount < tabStops.length)
      {
        spaceCount = tabStops.length - 1;
        tab (text, spaceCount++);
        inComment = true;
        text.append (" ");
      }

      //      if (value == 0x20)
      //        text.append ("@");
      //      else
      text.append ((char) value);
    }

    textLine = text.toString ();
    bufferLength = ptr - start;
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

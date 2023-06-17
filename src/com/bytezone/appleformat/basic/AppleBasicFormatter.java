package com.bytezone.appleformat.basic;

import static com.bytezone.appleformat.Utility.ASCII_BACKSPACE;
import static com.bytezone.appleformat.Utility.ASCII_CR;
import static com.bytezone.appleformat.Utility.ASCII_LF;
import static com.bytezone.appleformat.Utility.getIndent;
import static com.bytezone.appleformat.Utility.getShort;
import static com.bytezone.appleformat.Utility.isHighBitSet;

// -----------------------------------------------------------------------------------//
public class AppleBasicFormatter extends BasicFormatter
// -----------------------------------------------------------------------------------//
{
  private final LineFormatter flatFormatter = new FlatLine ();
  private final LineFormatter wrapFormatter = new WrapLine ();

  // ---------------------------------------------------------------------------------//
  public AppleBasicFormatter (ApplesoftBasicProgram program,
      ApplesoftBasicPreferences basicPreferences)
  // ---------------------------------------------------------------------------------//
  {
    super (program, basicPreferences);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void append (StringBuilder fullText)
  // ---------------------------------------------------------------------------------//
  {
    int loadAddress = getLoadAddress ();
    int ptr = offset;
    int linkField;

    StringBuilder currentLine = new StringBuilder ();
    LineFormatter formatter =
        basicPreferences.displayFormat == 2 ? wrapFormatter : flatFormatter;

    while ((linkField = getShort (buffer, ptr)) != 0)
    {
      int lineNumber = getShort (buffer, ptr + 2);
      currentLine.append (String.format (" %d ", lineNumber));
      ptr += 4;

      ptr = formatter.formatLine (currentLine, ptr);

      if (ptr != (linkField - loadAddress))
        System.out.printf ("%s: ptr: %04X, nextLine: %04X%n", program.getName (),
            ptr + loadAddress, linkField);

      currentLine.append (NEWLINE);
      fullText.append (currentLine);
      currentLine.setLength (0);
    }
  }

  // ---------------------------------------------------------------------------------//
  interface LineFormatter
  // ---------------------------------------------------------------------------------//
  {
    abstract int formatLine (StringBuilder currentLine, int ptr);
  }

  // ---------------------------------------------------------------------------------//
  class FlatLine implements LineFormatter
  // ---------------------------------------------------------------------------------//
  {
    // -------------------------------------------------------------------------------//
    @Override
    public int formatLine (StringBuilder currentLine, int ptr)
    // -------------------------------------------------------------------------------//
    {
      byte b;

      while ((b = buffer[ptr++]) != 0)
        if (isHighBitSet (b))
        {
          String token = String.format (" %s ", ApplesoftConstants.tokens[b & 0x7F]);
          currentLine.append (token);
        }
        else
          switch (b)
          {
            case ASCII_CR:
              currentLine.append (NEWLINE);
              break;

            case ASCII_BACKSPACE:
              if (currentLine.length () > 0)
                currentLine.deleteCharAt (currentLine.length () - 1);
              break;

            case ASCII_LF:
              int indent = getIndent (currentLine);
              currentLine.append ("\n");
              for (int i = 0; i < indent; i++)
                currentLine.append (" ");
              break;

            default:
              currentLine.append ((char) b);
          }

      return ptr;
    }
  }

  // ---------------------------------------------------------------------------------//
  class WrapLine implements LineFormatter
  // ---------------------------------------------------------------------------------//
  {
    private static final int LEFT_MARGIN = 5;
    private static final int RIGHT_MARGIN = 33;

    // -------------------------------------------------------------------------------//
    @Override
    public int formatLine (StringBuilder currentLine, int ptr)
    // -------------------------------------------------------------------------------//
    {
      byte b;
      int cursor = currentLine.length ();

      while ((b = buffer[ptr++]) != 0)
        if (isHighBitSet (b))
        {
          String token = String.format (" %s ", ApplesoftConstants.tokens[b & 0x7F]);
          currentLine.append (token);
          cursor = incrementCursor (currentLine, cursor, token.length ());
        }
        else
          switch (b)
          {
            case ASCII_CR:
              currentLine.append (NEWLINE);
              cursor = 0;
              break;

            case ASCII_BACKSPACE:
              if (cursor > 0)
              {
                currentLine.deleteCharAt (currentLine.length () - 1);
                --cursor;
              }
              break;

            case ASCII_LF:
              currentLine.append ("\n");
              for (int i = 0; i < cursor; i++)
                currentLine.append (" ");
              break;

            default:
              currentLine.append ((char) b);
              cursor = incrementCursor (currentLine, cursor, 1);
          }

      return ptr;
    }

    // -------------------------------------------------------------------------------//
    private int incrementCursor (StringBuilder currentLine, int cursor, int size)
    // -------------------------------------------------------------------------------//
    {
      assert size <= 9;           // longest token possible (7 plus 2 spaces)
      cursor += size;

      if ((cursor) >= RIGHT_MARGIN)
      {
        cursor = cursor >= 40 ? cursor - 40 : LEFT_MARGIN;
        currentLine.append ("\n     ".substring (0, cursor + 1));
      }

      return cursor;
    }
  }
}

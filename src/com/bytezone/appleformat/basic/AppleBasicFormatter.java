package com.bytezone.appleformat.basic;

import static com.bytezone.appleformat.Utility.ASCII_BACKSPACE;
import static com.bytezone.appleformat.Utility.ASCII_COLON;
import static com.bytezone.appleformat.Utility.ASCII_CR;
import static com.bytezone.appleformat.Utility.ASCII_DOUBLE_QUOTE;
import static com.bytezone.appleformat.Utility.ASCII_LF;
import static com.bytezone.appleformat.Utility.getIndent;
import static com.bytezone.appleformat.Utility.getShort;
import static com.bytezone.appleformat.Utility.isHighBitSet;

import com.bytezone.appleformat.HexFormatter;

// -----------------------------------------------------------------------------------//
public class AppleBasicFormatter extends BasicFormatter
// -----------------------------------------------------------------------------------//
{
  private final LineFormatter flatFormatter = new FlatLine ();
  private final LineFormatter wrapFormatter = new WrapLine ();
  private final LineFormatter hexFormatter = new HexLine ();

  private int loadAddress;

  // ---------------------------------------------------------------------------------//
  public AppleBasicFormatter (ApplesoftBasicProgram program,
      ApplesoftBasicPreferences basicPreferences)
  // ---------------------------------------------------------------------------------//
  {
    super (program, basicPreferences);

    loadAddress = getLoadAddress ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void append (StringBuilder fullText)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = offset;
    int linkField;

    StringBuilder currentLine = new StringBuilder ();
    LineFormatter formatter = switch (basicPreferences.displayFormat)
    {
      case 0 -> flatFormatter;
      case 1 -> wrapFormatter;
      case 2 -> hexFormatter;
      default -> flatFormatter;         // should be impossible
    };

    while ((linkField = getShort (buffer, ptr)) != 0)
    {
      ptr = formatter.formatLine (currentLine, ptr);

      if (ptr != (linkField - loadAddress + offset))
        System.out.printf ("%s: ptr: %04X, nextLine: %04X%n", program.getName (),
            ptr + loadAddress, linkField);

      fullText.append (currentLine);
      fullText.append (NEWLINE);

      currentLine.setLength (0);
    }

    // add the final zero bytes
    if (basicPreferences.displayFormat == 2)
    {
      ((HexLine) hexFormatter).finish (currentLine, ptr);
      fullText.append (currentLine);
    }
  }

  // ---------------------------------------------------------------------------------//
  interface LineFormatter
  // ---------------------------------------------------------------------------------//
  {
    abstract int formatLine (StringBuilder currentLine, int ptr);
  }

  // Displays the hex values for each subline
  // ---------------------------------------------------------------------------------//
  class HexLine implements LineFormatter
  // ---------------------------------------------------------------------------------//
  {
    // -------------------------------------------------------------------------------//
    @Override
    public int formatLine (StringBuilder currentLine, int ptr)
    // -------------------------------------------------------------------------------//
    {
      int hexDisplay = loadAddress - 2;                         // ignore linkField

      String header = HexFormatter.formatNoHeader (buffer, ptr, 4, hexDisplay + ptr);
      currentLine.append (String.format ("               %s\n", header));
      String lineNumber = String.format ("%5d", getShort (buffer, ptr + 2));

      ptr += 4;
      String token = "";

      while (true)
      {
        int b = buffer[ptr];
        if ((b & 0x80) != 0)
          token = ApplesoftConstants.tokens[b & 0x7F];

        int len = getLineLength (buffer, ptr);
        String formattedHex =
            HexFormatter.formatNoHeader (buffer, ptr, len, hexDisplay + ptr);

        for (String line : formattedHex.split (NEWLINE))
        {
          currentLine.append (String.format ("%-5s  %-7s %s%n", lineNumber, token, line));
          token = "";
          lineNumber = "";
        }

        ptr += len;

        if (buffer[ptr - 1] == 0)
          return ptr;
      }
    }

    // -------------------------------------------------------------------------------//
    public void finish (StringBuilder currentLine, int ptr)
    // -------------------------------------------------------------------------------//
    {
      int hexDisplay = loadAddress - 2;                         // ignore linkField
      String formattedHex =
          HexFormatter.formatNoHeader (buffer, ptr, 2, hexDisplay + ptr);

      currentLine.append (String.format ("               %s", formattedHex));
    }

    // -------------------------------------------------------------------------------//
    private int getLineLength (byte[] buffer, int ptr)
    // -------------------------------------------------------------------------------//
    {
      int start = ptr;
      boolean inQuote = false;

      while (true)
      {
        byte b = buffer[ptr++];
        if (b == ASCII_DOUBLE_QUOTE)
          inQuote = !inQuote;
        if (b == 0 || b == TOKEN_THEN || (b == ASCII_COLON && !inQuote))
          return ptr - start;
      }
    }
  }

  // Lists all sublines on a single line
  // ---------------------------------------------------------------------------------//
  class FlatLine implements LineFormatter
  // ---------------------------------------------------------------------------------//
  {
    // -------------------------------------------------------------------------------//
    @Override
    public int formatLine (StringBuilder currentLine, int ptr)
    // -------------------------------------------------------------------------------//
    {
      int lineNumber = getShort (buffer, ptr + 2);
      currentLine.append (String.format (" %d ", lineNumber));
      ptr += 4;

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

  // mimics the Applesoft display
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
      int lineNumber = getShort (buffer, ptr + 2);
      currentLine.append (String.format (" %d ", lineNumber));
      ptr += 4;

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

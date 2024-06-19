package com.bytezone.appleformat.basic;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.Utility;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.DataRecord;

// -----------------------------------------------------------------------------------//
public class BasicCpmProgram extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  String[] tokens = { //
      "", "END", "FOR", "NEXT", "DATA", "INPUT", "DIM", "READ",           // 0x80
      "LET", "GOTO", "RUN", "IF", "RESTORE", "GOSUB", "RETURN", "REM",    // 0x88
      "STOP", "PRINT", "CLEAR", "LIST", "NEW", "ON", "DEF", "POKE",       // 0x90
      "", "", "", "LPRINT", "LLIST", "WIDTH", "ELSE", "",                 // 0x98
      "", "SWAP", "ERASE", "", "ERROR", "RESUME", "DELETE", "",           // 0xA0
      "RENUM", "DEFSTR", "DEFINT", "", "DEFDBL", "LINE", "", "WHILE",     // 0xA8
      "WEND", "CALL", "WRITE", "COMMON", "CHAIN",                         // 0xB0
      "OPTION", "RANDOMIZE", "SYSTEM",                                    // 0xB5
      "OPEN", "FIELD", "GET", "PUT", "CLOSE", "LOAD", "MERGE", "",        // 0xB8
      "NAME", "KILL", "LSET", "RSET", "SAVE", "RESET", "TEXT", "HOME",    // 0xC0
      "VTAB", "HTAB", "INVERSE", "NORMAL", "", "", "", "",                // 0xC8
      "", "", "", "", "", "WAIT", "", "",                                 // 0xD0
      "", "", "", "", "", "TO", "THEN", "TAB(",                           // 0xD8
      "STEP", "USR", "FN", "SPC(", "", "ERL", "ERR", "STRING$",           // 0xE0
      "USING", "INSTR", "'", "VARPTR", "", "", "INKEY$", ">",             // 0xE8
      "=", "<", "+", "-", "*", "/", "", "AND",                            // 0xF0
      "OR", "", "", "", "MOD", "/", "", "",                               // 0xF8
  };

  String[] functions = { //
      "", "LEFT$", "RIGHT$", "MID$", "SGN", "INT", "ABS", "SQR",          // 0x80
      "RND", "SIN", "LOG", "EXP", "COS", "TAN", "ATN", "FRE",             // 0x88
      "POS", "LEN", "STR$", "VAL", "ASC", "CHR$", "PEEK", "SPACE$",       // 0x90
      "OCT$", "HEX$", "LPOS", "CINT", "CSNG", "CDBL", "FIX", "",          // 0x98
      "", "", "", "", "", "", "", "",                                     // 0xA0
      "", "", "CVI", "CVS", "CVD", "", "EOF", "LOC",                      // 0xA8
      "", "MKI$", "MKS$", "MKD$",                                         // 0xB0
  };

  // ---------------------------------------------------------------------------------//
  public BasicCpmProgram (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  public BasicCpmProgram (AppleFile appleFile, DataRecord dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);
  }

  // ---------------------------------------------------------------------------------//
  //  public BasicCpmProgram (AppleFile appleFile, byte[] buffer)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    super (appleFile, buffer, 0, buffer.length);
  //  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    //    if (basicPreferences.showHeader)
    //      text.append ("Name : " + name + "\n\n");

    byte[] buffer = dataRecord.data ();
    //    int ptr = dataRecord.offset () + 5;

    //    while (buffer[ptr] != 0)
    //      ptr++;

    //    if (showDebugText)
    //      return debugText ();

    //    ptr = 1;

    int ptr = dataRecord.offset () + 1;
    while (ptr < dataRecord.max ())
    {
      int nextAddress = Utility.getShort (buffer, ptr);

      if (nextAddress == 0)
        break;

      int lineNumber = Utility.getShort (buffer, ptr + 2);

      text.append (String.format (" %d ", lineNumber));
      ptr += 4;

      while (true)
      {
        int val = buffer[ptr++] & 0xFF;

        if (val == 0)
          break;

        if ((val & 0x80) != 0)
        {
          if (val == 0xFF)
          {
            val = buffer[ptr++] & 0xFF;
            String token = functions[val & 0x7F];
            if (token.length () == 0)
              token = String.format ("<FF %02X>", val);
            text.append (token);
          }
          else
          {
            String token = tokens[val & 0x7F];
            if (token.length () == 0)
              token = String.format ("<%02X>", val);
            text.append (token);
          }
          continue;
        }

        if (val >= 0x20 && val <= 0x7E)              // printable
        {
          // check for stupid apostrophe comment
          if (val == 0x3A && ptr + 1 < buffer.length && buffer[ptr] == (byte) 0x8F
              && buffer[ptr + 1] == (byte) 0xEA)
          {
            text.append ("'");
            ptr += 2;
          }
          else if (val == 0x3A && ptr < buffer.length && buffer[ptr] == (byte) 0x9E)
          {
            // ignore colon before ELSE
          }
          else
            text.append (String.format ("%s", (char) val));
          continue;
        }

        if (val >= 0x11 && val <= 0x1A)               // inline numbers
        {
          text.append (val - 0x11);
          continue;
        }

        switch (val)
        {
          case 0x07:
            text.append ("<BELL>");
            break;

          case 0x09:
            text.append ("        ");
            break;

          case 0x0A:
            text.append ("\n ");
            break;

          case 0x0C:
            text.append ("&H" + String.format ("%X", Utility.getShort (buffer, ptr)));
            ptr += 2;
            break;

          case 0x0E:                                // same as 0x1C ??
            text.append (Utility.getShort (buffer, ptr));
            ptr += 2;
            break;

          case 0x0F:
            text.append (buffer[ptr++] & 0xFF);
            break;

          case 0x1C:                                // same as 0x0E ??
            text.append (Utility.getShort (buffer, ptr));
            ptr += 2;
            break;

          case 0x1D:
            String d4 = Utility.floatValueMS4 (buffer, ptr) + "";
            if (d4.endsWith (".0"))
              d4 = d4.substring (0, d4.length () - 2);
            text.append (d4 + "!");
            ptr += 4;
            break;

          case 0x1F:
            String d8 = Utility.floatValueMS8 (buffer, ptr) + "";
            if (d8.endsWith (".0"))
              d8 = d8.substring (0, d8.length () - 2);
            text.append (d8 + "#");
            ptr += 8;
            break;

          default:
            text.append (String.format ("<%02X>", val));
        }
      }

      text.append ("\n");
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  private String debugText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    byte[] buffer = dataRecord.data ();

    int ptr = dataRecord.offset () + 1;
    int lastPtr;

    while (ptr < dataRecord.max ())
    {
      int nextAddress = Utility.getShort (buffer, ptr);
      if (nextAddress == 0)
        break;

      int lineNumber = Utility.getShort (buffer, ptr + 2);
      lastPtr = ptr;

      ptr += 4;

      int val;
      while ((val = buffer[ptr++]) != 0)
      {
        ptr += switch (val)
        {
          case 0x0C, 0x0E, 0x1C -> 2;   // 2 byte numeric
          case 0x1D -> 4;               // 4 byte single precision
          case 0x1F -> 8;               // 8 byte double precision
          case 0x0F, 0xFF -> 1;         // 1 byte numeric, function table entry
          default -> 0;
        };
      }

      text.append (String.format (" %d  %s%n", lineNumber,
          HexFormatter.getHexString (buffer, lastPtr + 4, ptr - lastPtr - 4)));
    }

    return Utility.rtrim (text);
  }
}

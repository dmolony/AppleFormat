package com.bytezone.appleformat.basic;

import static com.bytezone.appleformat.Utility.ASCII_COLON;
import static com.bytezone.appleformat.Utility.isDigit;
import static com.bytezone.appleformat.Utility.isHighBitSet;
import static com.bytezone.appleformat.Utility.isLetter;

import com.bytezone.appleformat.HexFormatter;

// -----------------------------------------------------------------------------------//
public class DebugBasicFormatter extends BasicFormatter
// -----------------------------------------------------------------------------------//
{
  int endPtr;

  // ---------------------------------------------------------------------------------//
  public DebugBasicFormatter (ApplesoftBasicProgram program, ApplesoftBasicPreferences basicPreferences)
  // ---------------------------------------------------------------------------------//
  {
    super (program, basicPreferences);

    endPtr = program.getEndPtr ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void append (StringBuilder text)
  // ---------------------------------------------------------------------------------//
  {
    int loadAddress = getLoadAddress ();

    for (SourceLine sourceLine : sourceLines)
    {
      text.append (String.format ("%5d            %s%n", sourceLine.lineNumber, HexFormatter
          .formatNoHeader (buffer, sourceLine.linePtr, 4, loadAddress + sourceLine.linePtr)));
      for (SubLine subline : sourceLine.sublines)
      {
        String token = getDisplayToken (buffer[subline.startPtr]);
        String formattedHex = HexFormatter.formatNoHeader (buffer, subline.startPtr, subline.length,
            loadAddress + subline.startPtr);

        for (String bytes : formattedHex.split (NEWLINE))
        {
          text.append (String.format ("        %-8s %s%n", token, bytes));
          token = "";
        }
      }
      text.append (NEWLINE);
    }

    // check for assembler routines after the basic code
    if (endPtr < buffer.length)
    {
      int length = buffer.length - endPtr;
      int ptr = endPtr;

      if (length >= 2)
      {
        text.append ("                 ");
        text.append (HexFormatter.formatNoHeader (buffer, endPtr, 2, loadAddress + ptr));
        text.append ("\n\n");
        ptr += 2;
        length -= 2;
      }

      if (length > 0)
      {
        // show the extra bytes as a hex dump
        String formattedHex =
            HexFormatter.formatNoHeader (buffer, ptr, buffer.length - ptr, loadAddress + ptr);
        for (String bytes : formattedHex.split (NEWLINE))
          text.append (String.format ("                 %s%n", bytes));
      }

      //      if (length > 1)
      //      {
      //        // show the extra bytes as a disassembly
      //        byte[] extraBuffer = new byte[length];
      //        System.arraycopy (buffer, ptr, extraBuffer, 0, extraBuffer.length);
      //        AssemblerProgram assemblerProgram =
      //            new AssemblerProgram ("extra", extraBuffer, loadAddress + ptr);
      //        text.append ("\n");
      //        text.append (assemblerProgram.getText ());
      //      }
    }
  }

  // ---------------------------------------------------------------------------------//
  private String getDisplayToken (byte b)
  // ---------------------------------------------------------------------------------//
  {
    if (isHighBitSet (b))
      return ApplesoftConstants.tokens[b & 0x7F];

    if (isDigit (b) || isLetter (b) || b == ASCII_COLON || b == 0)
      return "";

    return "*******";
  }
}

package com.bytezone.appleformat.basic;

import java.util.List;

import com.bytezone.appleformat.Utility;

// -----------------------------------------------------------------------------------//
public abstract class BasicFormatter implements ApplesoftConstants
// -----------------------------------------------------------------------------------//
{
  static final String NEWLINE = "\n";

  ApplesoftBasicProgram program;
  ApplesoftBasicPreferences basicPreferences;

  byte[] buffer;
  int offset;

  List<SourceLine> sourceLines;

  // ---------------------------------------------------------------------------------//
  public BasicFormatter (ApplesoftBasicProgram program,
      ApplesoftBasicPreferences basicPreferences)
  // ---------------------------------------------------------------------------------//
  {
    this.program = program;
    this.basicPreferences = basicPreferences;
    this.buffer = program.getBuffer ();
    this.offset = program.getOffset ();
    this.sourceLines = program.getSourceLines ();
  }

  // ---------------------------------------------------------------------------------//
  public abstract void append (StringBuilder fullText);
  // ---------------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------------//
  int getLoadAddress ()
  // ---------------------------------------------------------------------------------//
  {
    return (buffer.length > 3) ?                                          //
        Utility.getShort (buffer, offset) - getFirstLineLength () :       //
        0;
  }

  // ---------------------------------------------------------------------------------//
  private int getFirstLineLength ()
  // ---------------------------------------------------------------------------------//
  {
    int linkField = Utility.getShort (buffer, offset);
    if (linkField == 0)
      return 2;

    int ptr = offset + 4;               // skip link field and line number

    while (ptr < buffer.length && buffer[ptr++] != 0)
      ;

    return ptr - offset;
  }
}

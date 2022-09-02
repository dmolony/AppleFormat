package com.bytezone.appleformat;

import java.util.ArrayList;
import java.util.List;

// -----------------------------------------------------------------------------------//
public class ApplesoftBasicProgram extends BasicProgram implements ApplesoftConstants
// -----------------------------------------------------------------------------------//
{
  private final List<SourceLine> sourceLines = new ArrayList<> ();

  private final UserBasicFormatter userBasicFormatter;
  private final AppleBasicFormatter appleBasicFormatter;
  private final DebugBasicFormatter debugBasicFormatter;
  private final XrefFormatter xrefFormatter;
  private final BasicHeaderFormatter headerFormatter;

  boolean showDebugText;
  private int endPtr;

  // ---------------------------------------------------------------------------------//
  public ApplesoftBasicProgram (String name, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer, offset, length);

    int ptr = offset;
    while (buffer[ptr + 1] != 0)    // msb of link field
    {
      SourceLine line = new SourceLine (this, buffer, ptr);
      sourceLines.add (line);
      ptr += line.length;           // assumes lines are contiguous
    }

    endPtr = ptr;

    userBasicFormatter = new UserBasicFormatter (this, basicPreferences);
    appleBasicFormatter = new AppleBasicFormatter (this, basicPreferences);
    debugBasicFormatter = new DebugBasicFormatter (this, basicPreferences);
    xrefFormatter = new XrefFormatter (this, basicPreferences);
    headerFormatter = new BasicHeaderFormatter (this, basicPreferences);
  }

  // ---------------------------------------------------------------------------------//
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    if (basicPreferences.showHeader)
      headerFormatter.append (text);

    if (showDebugText)
    {
      debugBasicFormatter.append (text);
      return Utility.rtrim (text);
    }

    if (sourceLines.size () == 0)
    {
      text.append ("\n\nThis page intentionally left blank");
      return text.toString ();
    }

    if (basicPreferences.userFormat)
      userBasicFormatter.append (text);
    else
      appleBasicFormatter.append (text);

    if (basicPreferences.showAllXref)
      xrefFormatter.append (text);

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  List<SourceLine> getSourceLines ()
  // ---------------------------------------------------------------------------------//
  {
    return sourceLines;
  }

  // ---------------------------------------------------------------------------------//
  int getEndPtr ()
  // ---------------------------------------------------------------------------------//
  {
    return endPtr;
  }
}
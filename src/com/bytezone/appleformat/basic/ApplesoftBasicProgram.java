package com.bytezone.appleformat.basic;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.PreferencesFactory;
import com.bytezone.appleformat.Utility;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.DataRecord;

// -----------------------------------------------------------------------------------//
public class ApplesoftBasicProgram extends AbstractFormattedAppleFile
    implements ApplesoftConstants
// -----------------------------------------------------------------------------------//
{
  private final List<SourceLine> sourceLines = new ArrayList<> ();

  ApplesoftBasicPreferences basicPreferences = PreferencesFactory.basicPreferences;

  private final UserBasicFormatter userBasicFormatter =
      new UserBasicFormatter (this, basicPreferences);
  private final AppleBasicFormatter appleBasicFormatter =
      new AppleBasicFormatter (this, basicPreferences);
  private final DebugBasicFormatter debugBasicFormatter =
      new DebugBasicFormatter (this, basicPreferences);
  private final XrefFormatter xrefFormatter = new XrefFormatter (this, basicPreferences);

  boolean showDebugText;
  private int endPtr;

  // ---------------------------------------------------------------------------------//
  public ApplesoftBasicProgram (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    setup ();
  }

  // ---------------------------------------------------------------------------------//
  public ApplesoftBasicProgram (AppleFile appleFile, DataRecord dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

    setup ();
  }

  // ---------------------------------------------------------------------------------//
  private void setup ()
  // ---------------------------------------------------------------------------------//
  {
    preferences = basicPreferences;

    int ptr = dataRecord.offset ();
    byte[] buffer = dataRecord.data ();

    while (buffer[ptr + 1] != 0)    // msb of link field
    {
      SourceLine line = new SourceLine (this, buffer, ptr);
      sourceLines.add (line);
      ptr += line.length;           // assumes lines are contiguous
    }

    endPtr = ptr;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    if (sourceLines.size () == 0)
    {
      text.append ("\n\nThis page intentionally left blank");
      return text.toString ();
    }

    if (((ApplesoftBasicPreferences) preferences).displayFormat == 1)
      userBasicFormatter.append (text);
    else
      appleBasicFormatter.append (text);

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    if (sourceLines.size () == 0)
    {
      text.append ("\n\nThis page intentionally left blank");
      return text.toString ();
    }

    if (showDebugText)
    {
      debugBasicFormatter.append (text);
      return Utility.rtrim (text);
    }

    //    if (((ApplesoftBasicPreferences) preferences).showAllXref)
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
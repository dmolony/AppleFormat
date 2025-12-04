package com.bytezone.appleformat.basic;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.PreferencesFactory;
import com.bytezone.appleformat.Utility;
import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

// -----------------------------------------------------------------------------------//
public class ApplesoftBasicProgram extends AbstractFormattedAppleFile
    implements ApplesoftConstants
// -----------------------------------------------------------------------------------//
{
  private final List<SourceLine> sourceLines = new ArrayList<> ();

  ApplesoftBasicPreferences basicPreferences = PreferencesFactory.basicPreferences;

  private UserBasicFormatter userBasicFormatter;
  private AppleBasicFormatter appleBasicFormatter;
  private XrefFormatter xrefFormatter;

  private int endPtr;
  private List<String> dataItems = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public ApplesoftBasicProgram (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    setup ();
  }

  // ---------------------------------------------------------------------------------//
  public ApplesoftBasicProgram (AppleFile appleFile, Buffer dataBuffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataBuffer);

    setup ();
  }

  // ---------------------------------------------------------------------------------//
  private void setup ()
  // ---------------------------------------------------------------------------------//
  {
    preferences = basicPreferences;

    int ptr = dataBuffer.offset ();
    byte[] buffer = dataBuffer.data ();
    int max = dataBuffer.max () - 1;

    while (ptr < max && buffer[ptr + 1] != 0)    // msb of link field
    {
      SourceLine line = new SourceLine (this, buffer, ptr);
      sourceLines.add (line);
      dataItems.addAll (line.getDataItems ());
      ptr += line.length;           // assumes lines are contiguous
    }

    endPtr = ptr;

    userBasicFormatter = new UserBasicFormatter (this, basicPreferences);
    appleBasicFormatter = new AppleBasicFormatter (this, basicPreferences);
    xrefFormatter = new XrefFormatter (this, basicPreferences);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    if (sourceLines.size () == 0)
    {
      text.append ("\nNo source");
      return text.toString ();
    }

    if (((ApplesoftBasicPreferences) preferences).displayFormat == 3)
      userBasicFormatter.append (text);
    else
      appleBasicFormatter.append (text);

    if (extraFile != null)
    {
      text.append ("\nPossible extra assembler code:\n\n");
      text.append (extraFile.getText ());
    }

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
  public List<String> getDataItems ()
  // ---------------------------------------------------------------------------------//
  {
    return dataItems;
  }

  // ---------------------------------------------------------------------------------//
  public int getEndPtr ()
  // ---------------------------------------------------------------------------------//
  {
    return endPtr;
  }
}
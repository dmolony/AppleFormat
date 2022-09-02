package com.bytezone.appleformat.basic;

// -----------------------------------------------------------------------------------//
public class BasicHeaderFormatter extends BasicFormatter
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public BasicHeaderFormatter (ApplesoftBasicProgram program, BasicPreferences basicPreferences)
  // ---------------------------------------------------------------------------------//
  {
    super (program, basicPreferences);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void append (StringBuilder fullText)
  // ---------------------------------------------------------------------------------//
  {
    fullText.append ("Name    : " + program.name + "\n");
    fullText.append (String.format ("Length  : $%04X (%<,d)%n", buffer.length));
    fullText.append (String.format ("Load at : $%04X (%<,d)%n%n", getLoadAddress ()));
  }
}

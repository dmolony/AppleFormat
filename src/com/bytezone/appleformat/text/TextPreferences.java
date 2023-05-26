package com.bytezone.appleformat.text;

// -----------------------------------------------------------------------------------//
public class TextPreferences
//-----------------------------------------------------------------------------------//
{
  public boolean showTextOffsets;
  public boolean merlinFormat = true;

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Show offsets .......... %s%n", showTextOffsets));
    text.append (String.format ("Show .S as Merlin ..... %s", merlinFormat));

    return text.toString ();
  }
}

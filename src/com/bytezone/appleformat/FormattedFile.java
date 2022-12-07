package com.bytezone.appleformat;

public interface FormattedFile
{
  public String getFormattedText ();

  public String getAlternateText ();

  public String[] getFormattedLines ();

  public String[] getAlternateLines ();
}

package com.bytezone.appleformat;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public interface FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  public String getText ();

  public String getExtras ();

  public Image getImage ();

  public byte[] getBuffer ();

  public int getOffset ();

  public int getLength ();

  public ApplePreferences getPreferences ();

  public OptionsType getOptionsType ();

  public enum OptionsType
  {
    APPLESOFT, ASSEMBLER, GRAPHICS, TEXT
  }
}

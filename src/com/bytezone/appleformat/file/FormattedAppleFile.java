package com.bytezone.appleformat.file;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.filesystem.DataRecord;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public interface FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  public String getText ();

  public String getExtras ();

  public Image getImage ();

  public DataRecord getDataRecord ();

  public byte[] getBuffer ();

  public int getOffset ();

  public int getLength ();

  public ApplePreferences getPreferences ();
}

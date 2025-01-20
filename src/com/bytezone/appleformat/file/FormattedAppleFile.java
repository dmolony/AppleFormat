package com.bytezone.appleformat.file;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.filesystem.Buffer;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public interface FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  public String getText ();

  public String getExtras ();

  public Image getImage ();

  public Buffer getDataRecord ();

  public ApplePreferences getPreferences ();
}

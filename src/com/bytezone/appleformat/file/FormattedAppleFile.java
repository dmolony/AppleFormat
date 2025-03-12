package com.bytezone.appleformat.file;

import java.util.List;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.filesystem.Buffer;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public interface FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  public String getText ();

  public List<String> getHex (int maxLines);

  public String getExtras ();

  public Image getImage ();

  public Buffer getDataBuffer ();

  public ApplePreferences getPreferences ();

  public void append (FormattedAppleFile formattedAppleFile);
}

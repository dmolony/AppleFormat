package com.bytezone.appleformat.file;

import java.util.List;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.filesystem.Buffer;

import javafx.scene.image.Image;

// what is this used for?
// -----------------------------------------------------------------------------------//
public class FileContainer implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected final FormattedAppleFile formattedAppleFile;
  protected FormattedAppleFile extraFile;

  // ---------------------------------------------------------------------------------//
  public FileContainer (FormattedAppleFile formattedAppleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.formattedAppleFile = formattedAppleFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getText ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public List<String> getHex (int maxLines)
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getHex (maxLines);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getExtras ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getImage ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Buffer getDataBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getDataBuffer ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public ApplePreferences getPreferences ()
  // ---------------------------------------------------------------------------------//
  {
    return null;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void append (FormattedAppleFile formattedAppleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.extraFile = formattedAppleFile;
  }
}

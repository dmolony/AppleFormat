package com.bytezone.appleformat;

import java.util.List;

import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.filesystem.Buffer;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

//-----------------------------------------------------------------------------------//
public class FormatError implements FormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  protected static final WritableImage emptyImage = new WritableImage (1, 1);

  Exception exception;

  // ---------------------------------------------------------------------------------//
  public FormatError (Exception e)
  // ---------------------------------------------------------------------------------//
  {
    this.exception = e;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Error - " + exception.getLocalizedMessage ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public List<String> getHex (int maxLines)
  // ---------------------------------------------------------------------------------//
  {
    return null;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return getText ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    return emptyImage;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Buffer getDataBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return null;
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
  }
}
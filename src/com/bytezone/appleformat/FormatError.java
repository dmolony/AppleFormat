package com.bytezone.appleformat;

import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.filesystem.DataRecord;

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
  public DataRecord getDataRecord ()
  // ---------------------------------------------------------------------------------//
  {
    return null;
  }

  // ---------------------------------------------------------------------------------//
  //  @Override
  //  public byte[] getBuffer ()
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    return null;
  //  }

  // ---------------------------------------------------------------------------------//
  //  @Override
  //  public int getOffset ()
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    return 0;
  //  }

  // ---------------------------------------------------------------------------------//
  //  @Override
  //  public int getLength ()
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    return 0;
  //  }

  // ---------------------------------------------------------------------------------//
  @Override
  public ApplePreferences getPreferences ()
  // ---------------------------------------------------------------------------------//
  {
    return null;
  }
}
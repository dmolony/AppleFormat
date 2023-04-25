package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public class AppleGraphicsPic extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{

  // ---------------------------------------------------------------------------------//
  public AppleGraphicsPic (AppleFile appleFile, byte[] buffer, int aux)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    return null;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append ("Pic");

    return text.toString ();
  }
}

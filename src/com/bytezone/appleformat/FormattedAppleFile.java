package com.bytezone.appleformat;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public interface FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  public String getText ();

  public String getExtras ();

  //  public void writeGraphics (GraphicsContext graphicsContext);

  public Image writeImage ();

  public byte[] getBuffer ();

  public int getOffset ();

  public int getLength ();
}

package com.bytezone.appleformat;

import javafx.scene.canvas.GraphicsContext;

// -----------------------------------------------------------------------------------//
public interface FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  public String getText ();

  public String getExtras ();

  public void writeGraphics (GraphicsContext graphicsContext);

  public byte[] getBuffer ();

  public int getOffset ();

  public int getLength ();

  //  public AppleFile getAppleFile ();
}

package com.bytezone.appleformat;

import com.bytezone.filesystem.AppleFile;

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

  public void setAppleFile (AppleFile appleFile);

  public AppleFile getAppleFile ();
}

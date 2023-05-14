package com.bytezone.appleformat;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public interface FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  public String getText ();

  public String getExtras ();

  public Image getImage ();

  public Canvas getCanvas ();

  public byte[] getBuffer ();

  public int getOffset ();

  public int getLength ();
}

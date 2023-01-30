package com.bytezone.appleformat;

import javafx.scene.canvas.GraphicsContext;

// -----------------------------------------------------------------------------------//
public interface FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  public String getText ();

  //  public String getAlternateText ();
  //
  //  public String[] getFormattedLines ();
  //
  //  public String[] getAlternateLines ();

  public void writeGraphics (GraphicsContext graphicsContext);

  public String getHex ();

  public String getMeta ();
}

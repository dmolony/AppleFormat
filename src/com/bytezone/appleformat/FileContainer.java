package com.bytezone.appleformat;

import javafx.scene.canvas.GraphicsContext;

// -----------------------------------------------------------------------------------//
public class FileContainer implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected final FormattedAppleFile formattedAppleFile;

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
  public void writeGraphics (GraphicsContext graphicsContext)
  // ---------------------------------------------------------------------------------//
  {
    formattedAppleFile.writeGraphics (graphicsContext);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getHex ()
  // ---------------------------------------------------------------------------------//
  {
    return "formatted apple file hex";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getMeta ()
  // ---------------------------------------------------------------------------------//
  {
    return "formatted apple file meta";
  }
}

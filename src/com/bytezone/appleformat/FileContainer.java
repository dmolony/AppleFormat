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
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getBuffer ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getOffset ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getLength ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getLength ();
  }
}

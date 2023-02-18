package com.bytezone.appleformat;

import com.bytezone.filesystem.AppleFile;

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
  public AppleFile getAppleFile ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getAppleFile ();
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
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getExtras ();
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

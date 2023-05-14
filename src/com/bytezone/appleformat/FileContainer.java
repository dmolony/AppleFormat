package com.bytezone.appleformat;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

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
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getExtras ();
  }

  // ---------------------------------------------------------------------------------//
  //  @Override
  //  public void writeGraphics (GraphicsContext graphicsContext)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    formattedAppleFile.writeGraphics (graphicsContext);
  //  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    return formattedAppleFile.getImage ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Canvas getCanvas ()
  // ---------------------------------------------------------------------------------//
  {
    return null;
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

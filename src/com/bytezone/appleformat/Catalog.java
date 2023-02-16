package com.bytezone.appleformat;

import com.bytezone.filesystem.AppleFile;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class Catalog implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  AppleFile appleFile;

  // ---------------------------------------------------------------------------------//
  public Catalog (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    assert appleFile.isFileSystem () || appleFile.isFolder ();

    this.appleFile = appleFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void setAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    assert this.appleFile == appleFile;
    //    this.appleFile = appleFile;       // pointless
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public AppleFile getAppleFile ()
  // ---------------------------------------------------------------------------------//
  {
    return appleFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return appleFile.catalog ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return "";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void writeGraphics (GraphicsContext gc)
  // ---------------------------------------------------------------------------------//
  {
    Canvas canvas = gc.getCanvas ();

    canvas.setWidth (1);
    canvas.setHeight (1);

    gc.setFill (Color.WHITE);
    gc.fillRect (0, 0, 1, 1);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    // return catalog blocks?
    return null;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return 0;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getLength ()
  // ---------------------------------------------------------------------------------//
  {
    return 0;
  }
}

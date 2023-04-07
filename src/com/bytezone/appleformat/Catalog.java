package com.bytezone.appleformat;

import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.ForkedFile;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class Catalog implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  AppleContainer container;
  ForkedFile forkedFile;

  // ---------------------------------------------------------------------------------//
  public Catalog (AppleContainer appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.container = appleFile;
  }

  // ---------------------------------------------------------------------------------//
  public Catalog (ForkedFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.forkedFile = appleFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return forkedFile != null ? forkedFile.getCatalog () : container.getCatalog ();
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

package com.bytezone.appleformat;

import java.io.File;
import java.util.Arrays;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class LocalFolder implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  File localFile;

  // ---------------------------------------------------------------------------------//
  public LocalFolder (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    this.localFile = localFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ("  File size    File name\n");

    text.append ("------------  ---------------------------------"
        + "------------------------------------------\n");

    File[] files = localFile.listFiles ();
    Arrays.sort (files);

    for (File file : files)
      if (!file.isHidden () && !file.isDirectory ())
        text.append (String.format ("%,12d  %s %n", file.length (), file.getName ()));

    return text.toString ();
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

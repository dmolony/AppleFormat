package com.bytezone.appleformat;

import java.io.File;

import com.bytezone.filesystem.AppleFile;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public abstract class AbstractFormattedAppleFile implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected final File localFile;

  protected final AppleFile appleFile;

  protected final String name;
  protected final byte[] buffer;
  protected final int offset;
  protected final int length;

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile, byte[] buffer, int offset,
      int length)
  // ---------------------------------------------------------------------------------//
  {
    this.appleFile = appleFile;
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;

    name = appleFile.getFileName ();

    localFile = null;
  }

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    this.appleFile = null;
    this.buffer = null;
    this.offset = 0;
    this.length = 0;

    name = localFile.getName ();

    this.localFile = localFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Unknown file type";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return "No additional information";
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
  public String getName ()
  // ---------------------------------------------------------------------------------//
  {
    return name;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return offset;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getLength ()
  // ---------------------------------------------------------------------------------//
  {
    return length;
  }
}

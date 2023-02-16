package com.bytezone.appleformat;

import com.bytezone.filesystem.AppleFile;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public abstract class AbstractFormattedAppleFile implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected final String name;
  protected final byte[] buffer;
  protected final int offset;
  protected final int length;
  protected AppleFile appleFile;          // optional, but nearly always there

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (String name, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (name, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (String name, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void setAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.appleFile = appleFile;
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
    return "Unknown file type";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return "no additional information";
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

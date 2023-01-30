package com.bytezone.appleformat;

import javafx.scene.canvas.GraphicsContext;

// -----------------------------------------------------------------------------------//
public abstract class AbstractFormattedAppleFile implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected final String name;
  protected final byte[] buffer;
  protected final int offset;
  protected final int length;

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
  public String getName ()
  // ---------------------------------------------------------------------------------//
  {
    return name;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getMeta ()
  // ---------------------------------------------------------------------------------//
  {
    return "meta";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getHex ()
  // ---------------------------------------------------------------------------------//
  {
    return "hex";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "text";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void writeGraphics (GraphicsContext graphicsContext)
  // ---------------------------------------------------------------------------------//
  {

  }
}

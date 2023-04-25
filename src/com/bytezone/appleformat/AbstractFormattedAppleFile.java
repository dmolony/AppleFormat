package com.bytezone.appleformat;

import java.io.File;

import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.ForkedFile;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

// -----------------------------------------------------------------------------------//
public abstract class AbstractFormattedAppleFile implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected final File localFile;

  protected final AppleFile appleFile;

  protected final String name;
  protected byte[] buffer;
  protected final int offset;
  protected final int length;

  protected final AppleContainer container;
  protected final ForkedFile forkedFile;

  protected final WritableImage emptyImage = new WritableImage (1, 1);

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleContainer appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.container = appleFile;
    name = "";

    forkedFile = null;
    this.appleFile = null;
    this.buffer = null;
    this.offset = 0;
    this.length = 0;
    localFile = null;
  }

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (ForkedFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.forkedFile = appleFile;
    name = "";

    container = null;
    this.appleFile = null;
    this.buffer = null;
    this.offset = 0;
    this.length = 0;
    localFile = null;
  }

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
    forkedFile = null;
    container = null;
  }

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    this.appleFile = null;
    this.buffer = null;
    this.offset = 0;
    this.length = 0;

    forkedFile = null;
    container = null;

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
  public Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    return emptyImage;
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

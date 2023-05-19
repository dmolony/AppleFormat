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
  protected static final WritableImage emptyImage = new WritableImage (1, 1);

  protected final File localFile;
  protected final AppleFile appleFile;
  protected final AppleContainer container;
  protected final ForkedFile forkedFile;

  protected final String name;
  protected final byte[] buffer;
  protected final int offset;
  protected final int length;

  private Image image;
  private String text;
  private String extra;

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    this.localFile = localFile;
    appleFile = null;
    forkedFile = null;
    container = null;

    name = localFile.getName ();
    buffer = null;
    offset = 0;
    length = 0;
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
    localFile = null;
    this.appleFile = appleFile;
    forkedFile = null;
    container = null;

    name = appleFile.getFileName ();
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
  }

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (ForkedFile forkedFile)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    appleFile = null;
    this.forkedFile = forkedFile;
    container = null;

    name = "";
    buffer = null;
    offset = 0;
    length = 0;
  }

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleContainer container)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    appleFile = null;
    forkedFile = null;
    this.container = container;

    name = "";
    buffer = null;
    offset = 0;
    length = 0;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public final String getText ()
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      if (text == null)
        text = buildText ();

      return buildText ();
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      return e.toString ();
    }
  }

  // ---------------------------------------------------------------------------------//
  protected String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    return "No text available";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public final String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      if (extra == null)
        extra = buildExtras ();

      return extra;
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      return e.getLocalizedMessage ();
    }
  }

  // ---------------------------------------------------------------------------------//
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return "No additional information";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public final Image getImage ()
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      if (image == null)
        image = buildImage ();

      return image == null ? emptyImage : image;
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      return emptyImage;
    }
  }

  // ---------------------------------------------------------------------------------//
  protected Image buildImage ()
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

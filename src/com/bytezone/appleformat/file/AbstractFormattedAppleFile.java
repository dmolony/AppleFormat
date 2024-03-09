package com.bytezone.appleformat.file;

import java.io.File;
import java.util.Objects;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleForkedFile;

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
  protected final AppleForkedFile forkedFile;

  protected final String name;
  protected final byte[] buffer;
  protected final int offset;
  protected final int length;

  private Image image;
  private String text;
  private String extra;

  protected ApplePreferences preferences;
  protected OptionsType optionsType;

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    this.localFile = Objects.requireNonNull (localFile);

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
    this.appleFile = Objects.requireNonNull (appleFile);
    forkedFile = null;
    container = null;

    name = appleFile.getFileName ();
    this.buffer = Objects.requireNonNull (buffer);
    this.offset = offset;
    this.length = length;
  }

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleForkedFile forkedFile)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    appleFile = null;
    this.forkedFile = Objects.requireNonNull (forkedFile);
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
    this.container = Objects.requireNonNull (container);

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
      text = buildText ();

      return text;
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

  // ---------------------------------------------------------------------------------//
  @Override
  public ApplePreferences getPreferences ()
  // ---------------------------------------------------------------------------------//
  {
    return preferences;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public OptionsType getOptionsType ()
  // ---------------------------------------------------------------------------------//
  {
    return optionsType;
  }
}

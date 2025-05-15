package com.bytezone.appleformat.file;

import java.io.File;
import java.util.List;
import java.util.Objects;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.AppleForkedFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.TextBlock;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

// -----------------------------------------------------------------------------------//
public abstract class AbstractFormattedAppleFile implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  protected static final WritableImage emptyImage = new WritableImage (1, 1);
  private static final int MAX_HEX_BYTES = 0x10_000;

  protected final File localFile;
  protected final AppleFile appleFile;
  protected final AppleContainer container;
  protected final AppleForkedFile forkedFile;

  protected final String name;
  protected final List<? extends TextBlock> textBlocks;

  // Items shown in the output tabs
  private String text;                  // Data tab
  private Image image;                  // Graphics tab
  private List<String> hex;             // Hex tab
  private String extra;                 // Extras tab

  protected ApplePreferences preferences;
  protected FormattedAppleFile extraFile;     // eg AppleSoft with Binary

  // Usually the FileBuffer, but could be an unpacked buffer
  protected final Buffer dataBuffer;

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (File localFile)          // local folder
  // ---------------------------------------------------------------------------------//
  {
    this.localFile = Objects.requireNonNull (localFile);

    appleFile = null;
    forkedFile = null;
    container = null;

    name = localFile.getName ();
    dataBuffer = null;
    textBlocks = null;
  }

  // Pascal proc (inside segment)
  // Data fork
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, appleFile.getFileBuffer ());
  }

  // Resource fork
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile, Buffer dataBuffer)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    this.appleFile = Objects.requireNonNull (appleFile);
    forkedFile = null;
    container = null;
    textBlocks = null;

    name = appleFile.getFileName ();
    this.dataBuffer = Objects.requireNonNull (dataBuffer);
  }

  // text file using records
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile,
      List<? extends TextBlock> textBlocks)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    this.appleFile = Objects.requireNonNull (appleFile);
    forkedFile = null;
    container = null;
    this.textBlocks = textBlocks;

    name = appleFile.getFileName ();
    this.dataBuffer = null;
  }

  // Prodos/NuFX file with forks
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleForkedFile forkedFile)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    appleFile = null;
    this.forkedFile = Objects.requireNonNull (forkedFile);
    container = null;
    textBlocks = null;

    name = "";
    dataBuffer = ((AppleFile) forkedFile).getFileBuffer ();
  }

  // Disk file - file system
  // Disk file - hybrid disk
  // Pascal code file
  // Pascal segment (inside code file)
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleContainer container)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    forkedFile = null;
    this.container = Objects.requireNonNull (container);
    textBlocks = null;

    if (container instanceof AppleFile af)
    {
      appleFile = af;
      dataBuffer = af.getFileBuffer ();
    }
    else if (container instanceof AppleFileSystem afs)
    {
      appleFile = null;
      dataBuffer = afs.getDiskBuffer ();
    }
    else
    {
      appleFile = null;
      dataBuffer = null;
    }

    name = "";
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

  // Override this
  // ---------------------------------------------------------------------------------//
  protected String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    return "No text available";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public List<String> getHex (int maxLines)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      hex = buildHex ();

      return hex;
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      return null;
    }
  }

  // ---------------------------------------------------------------------------------//
  protected List<String> buildHex ()
  // ---------------------------------------------------------------------------------//
  {
    return Utility.getHexDumpLines (dataBuffer.data (), dataBuffer.offset (),
        Math.min (MAX_HEX_BYTES, dataBuffer.length ()));
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
  public Buffer getDataBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return dataBuffer;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public ApplePreferences getPreferences ()
  // ---------------------------------------------------------------------------------//
  {
    return preferences;
  }

  // Used when an applesoft program has an assembler routine attached
  // ---------------------------------------------------------------------------------//
  @Override
  public void append (FormattedAppleFile formattedAppleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.extraFile = formattedAppleFile;
  }
}

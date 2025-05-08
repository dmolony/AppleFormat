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
  protected Buffer dataBuffer;
  protected List<? extends TextBlock> textBlocks;

  private Image image;
  private String text;
  private List<String> hex;
  private String extra;

  protected ApplePreferences preferences;
  protected FormattedAppleFile extraFile;

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (File localFile)          // local folder
  // ---------------------------------------------------------------------------------//
  {
    this.localFile = Objects.requireNonNull (localFile);

    appleFile = null;
    forkedFile = null;
    container = null;
    //    System.out.println ("local folder - " + localFile.getName ());

    name = localFile.getName ();
    dataBuffer = null;
  }

  // Pascal proc (inside segment)
  // Data fork
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, appleFile.getFileBuffer ());
    //    System.out.println ("AF");
  }

  // Resource fork
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile, Buffer dataBuffer)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    //    System.out.printf ("AF, DR - %s%n", appleFile.getFileName ());
    this.appleFile = Objects.requireNonNull (appleFile);
    forkedFile = null;
    container = null;

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
    //    System.out.printf ("AF, DR - %s%n", appleFile.getFileName ());
    this.appleFile = Objects.requireNonNull (appleFile);
    forkedFile = null;
    container = null;

    name = appleFile.getFileName ();
    this.dataBuffer = null;
    this.textBlocks = textBlocks;
  }

  // Prodos file with forks
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleForkedFile forkedFile)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    appleFile = null;
    this.forkedFile = Objects.requireNonNull (forkedFile);
    container = null;
    //    System.out.printf ("FF - %s%n", ((AppleFile) forkedFile).getFileName ());

    name = "";
    dataBuffer = null;
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

    if (container instanceof AppleFile af)
    {
      appleFile = af;
      //      System.out.printf ("AC -> AF - %s%n", af.getFileName ());

      int eof = af.getFileLength ();
      Buffer temp = af.getRawFileBuffer ();

      dataBuffer =
          temp.length () == eof ? temp : new Buffer (temp.data (), temp.offset (), eof);
    }
    else if (container instanceof AppleFileSystem afs)
    {
      appleFile = null;
      dataBuffer = afs.getDiskBuffer ();
      //      System.out.printf ("AC -> AFS - %s%n", afs.getFileName ());
    }
    else
    {
      appleFile = null;
      dataBuffer = null;
      //      System.out.printf ("AC -> null - %s%n", "??");
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
    Buffer dataBuffer = getDataBuffer ();

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

  // ---------------------------------------------------------------------------------//
  @Override
  public void append (FormattedAppleFile formattedAppleFile)
  // ---------------------------------------------------------------------------------//
  {
    this.extraFile = formattedAppleFile;
  }
}

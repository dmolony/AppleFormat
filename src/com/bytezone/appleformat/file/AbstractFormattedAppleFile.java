package com.bytezone.appleformat.file;

import java.io.File;
import java.util.Objects;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.AppleForkedFile;
import com.bytezone.filesystem.DataRecord;

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
  protected DataRecord dataRecord;      // would be final except for FaddenHiResImage

  private Image image;
  private String text;
  private String extra;

  protected ApplePreferences preferences;

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
    dataRecord = null;
  }

  // Pascal proc (inside segment)
  // Data fork
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, appleFile.getDataRecord ());
    //    System.out.println ("AF");
  }

  // Resource fork
  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleFile (AppleFile appleFile, DataRecord dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    localFile = null;
    //    System.out.printf ("AF, DR - %s%n", appleFile.getFileName ());
    this.appleFile = Objects.requireNonNull (appleFile);
    forkedFile = null;
    container = null;

    name = appleFile.getFileName ();
    this.dataRecord = Objects.requireNonNull (dataRecord);
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
    dataRecord = null;
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
      DataRecord temp = af.getDataRecord ();

      dataRecord = temp.length () == eof ? temp
          : new DataRecord (temp.data (), temp.offset (), eof);
    }
    else if (container instanceof AppleFileSystem afs)
    {
      appleFile = null;
      dataRecord = afs.getDataRecord ();
      //      System.out.printf ("AC -> AFS - %s%n", afs.getFileName ());
    }
    else
    {
      appleFile = null;
      dataRecord = null;
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
  public DataRecord getDataRecord ()
  // ---------------------------------------------------------------------------------//
  {
    return dataRecord;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public ApplePreferences getPreferences ()
  // ---------------------------------------------------------------------------------//
  {
    return preferences;
  }
}

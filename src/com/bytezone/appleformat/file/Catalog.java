package com.bytezone.appleformat.file;

import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.AppleForkedFile;
import com.bytezone.filesystem.FsProdos;
import com.bytezone.utility.Utility;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public class Catalog extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  private static final String line =
      "-------------------------------------------------------"
          + "----------------------------\n";
  private int lastDepth;

  // ---------------------------------------------------------------------------------//
  public Catalog (AppleContainer appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  public Catalog (AppleForkedFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    return emptyImage;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    return forkedFile != null ? forkedFile.getCatalogText ()
        : container.getCatalogText ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    lastDepth = 0;

    if (container instanceof FsProdos fs)
    {
      StringBuilder text = new StringBuilder ();

      String name = fs.getHeaderName ();
      lastDepth = 0;

      text.append ("/" + name + "\n");
      text.append (listContents (1, container));

      return Utility.rtrim (text);
    }

    return "";
  }

  // ---------------------------------------------------------------------------------//
  private String listContents (int depth, AppleContainer container)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : container.getFiles ())
    {
      if (depth != lastDepth || file instanceof AppleContainer
          || file.hasEmbeddedFileSystem ())
      {
        lastDepth = depth;
        text.append (line);
      }

      text.append (String.format ("%2d  %s%n", depth, file.getCatalogLine ()));

      if (file instanceof AppleContainer folder)
        text.append (listContents (depth + 1, folder));

      if (file.hasEmbeddedFileSystem ())
        for (AppleFileSystem fs : file.getEmbeddedFileSystems ())
          text.append (listContents (depth + 1, fs));
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return forkedFile != null ? forkedFile.toString () : container.toString ();
  }
}

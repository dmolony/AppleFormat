package com.bytezone.appleformat.file;

import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleForkedFile;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public class Catalog extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
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
    //    return forkedFile != null ? ((AppleFile) forkedFile).getFileName () : "who?";
    return "";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return forkedFile != null ? forkedFile.toString () : container.toString ();
  }
}

package com.bytezone.appleformat.file;

import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleForkedFile;
import com.bytezone.filesystem.DataRecord;

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

    if (appleFile instanceof AppleFile af)
    {
      DataRecord dataRecord = af.getDataRecord ();
      int eof = af.getFileLength ();
      if (dataRecord.length () != eof)
        this.dataRecord = new DataRecord (dataRecord.data (), dataRecord.offset (), eof);
    }
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
    return forkedFile != null ? forkedFile.getCatalog () : container.getCatalogText ();
  }
}

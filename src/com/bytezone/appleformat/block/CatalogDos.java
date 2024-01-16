package com.bytezone.appleformat.block;

import com.bytezone.appleformat.AbstractFormattedAppleBlock;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.FsDos;

// -----------------------------------------------------------------------------------//
public class CatalogDos extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public CatalogDos (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleBlock.read ();
    FsDos fs = (FsDos) appleBlock.getFileSystem ();

    StringBuilder text = getHeader ("DOS Catalog Sector");

    return Utility.rtrim (text);
  }
}

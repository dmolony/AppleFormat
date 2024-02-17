package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;

public class CatalogLbr extends AbstractFormattedAppleBlock
{

  // ---------------------------------------------------------------------------------//
  public CatalogLbr (AppleBlock appleBlock)
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
    //    FsCpm fs = (FsCpm) appleBlock.getFileSystem ();

    StringBuilder text = getHeader ("CPM Catalog Sector");

    return Utility.rtrim (text);
  }
}

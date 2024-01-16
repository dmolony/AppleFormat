package com.bytezone.appleformat.block;

import com.bytezone.appleformat.AbstractFormattedAppleBlock;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.FsProdos;

// -----------------------------------------------------------------------------------//
public class CatalogProdos extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public CatalogProdos (AppleBlock appleBlock)
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
    FsProdos fs = (FsProdos) appleBlock.getFileSystem ();

    StringBuilder text = getHeader ("Prodos Catalog Sector");

    return Utility.rtrim (text);
  }
}

package com.bytezone.appleformat.block;

import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.FsPascalCode;

// -----------------------------------------------------------------------------------//
public class CatalogPascalCode extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{

  // ---------------------------------------------------------------------------------//
  public CatalogPascalCode (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    FsPascalCode fsPascalCode = (FsPascalCode) appleBlock.getFileSystem ();

    return fsPascalCode.getCatalogText ();
  }
}

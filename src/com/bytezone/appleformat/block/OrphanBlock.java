package com.bytezone.appleformat.block;

import com.bytezone.appleformat.AbstractFormattedAppleBlock;
import com.bytezone.filesystem.AppleBlock;

//-----------------------------------------------------------------------------------//
public class OrphanBlock extends AbstractFormattedAppleBlock
//-----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public OrphanBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Orphan Block";
  }
}
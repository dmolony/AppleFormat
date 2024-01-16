package com.bytezone.appleformat.block;

import com.bytezone.appleformat.AbstractFormattedAppleBlock;
import com.bytezone.filesystem.AppleBlock;

//-----------------------------------------------------------------------------------//
public class DosBlock extends AbstractFormattedAppleBlock
//-----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public DosBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "DOS Block";
  }
}
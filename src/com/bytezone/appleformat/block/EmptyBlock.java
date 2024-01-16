package com.bytezone.appleformat.block;

import com.bytezone.appleformat.AbstractFormattedAppleBlock;
import com.bytezone.filesystem.AppleBlock;

//-----------------------------------------------------------------------------------//
public class EmptyBlock extends AbstractFormattedAppleBlock
//-----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public EmptyBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Empty Block";
  }
}

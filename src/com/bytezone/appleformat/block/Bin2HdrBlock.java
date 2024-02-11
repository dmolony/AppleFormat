package com.bytezone.appleformat.block;

import com.bytezone.filesystem.AppleBlock;

// -----------------------------------------------------------------------------------//
public class Bin2HdrBlock extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public Bin2HdrBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Binary2 Header Block";
  }
}

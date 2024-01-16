package com.bytezone.appleformat;

import com.bytezone.filesystem.AppleBlock;

// -----------------------------------------------------------------------------------//
public class DataBlock extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public DataBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Data Block : " + appleBlock.getFileOwner ().getFileName ();
  }
}

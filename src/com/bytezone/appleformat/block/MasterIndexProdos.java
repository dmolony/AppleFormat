package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;

// -----------------------------------------------------------------------------------//
public class MasterIndexProdos extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public MasterIndexProdos (AppleBlock appleBlock)
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
    StringBuilder text =
        getHeader ("Prodos Master Index : " + appleBlock.getFileOwner ().getFileName ());
    return Utility.rtrim (text);
  }
}

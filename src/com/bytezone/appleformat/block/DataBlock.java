package com.bytezone.appleformat.block;

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
    StringBuilder text = new StringBuilder ();

    text.append (
        String.format ("Data Block : %s%n", appleBlock.getFileOwner ().getFileName ()));
    text.append (String.format ("Block type : %s", appleBlock.getBlockSubType ()));

    return text.toString ();
  }
}

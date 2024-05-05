package com.bytezone.appleformat.block;

import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.ForkProdos;

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

    AppleFile appleFile = appleBlock.getFileOwner ();
    String name = appleFile.isFork ()
        ? ((ForkProdos) appleFile).getParentFile ().getFileName () + " : " : "";

    text.append (String.format ("Data Block : %s%s%n", name, appleFile.getFileName ()));
    text.append (String.format ("Block type : %s%n", appleBlock.getBlockSubType ()));

    return text.toString ();
  }
}

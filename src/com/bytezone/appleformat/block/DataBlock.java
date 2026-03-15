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

    if (appleFile == null)                    // DosMaster data has no parent file
      text.append ("File name       :\n");
    else
    {
      String name = appleFile.isFork ()
          ? ((ForkProdos) appleFile).getParentFile ().getFileName () + " : " : "";

      text.append (
          String.format ("File name       : %s%s%n", name, appleFile.getFileName ()));
    }
    text.append (String.format ("Block type      : %s%n", appleBlock.getBlockType ()));
    text.append (String.format ("Block subtype   : %s%n", appleBlock.getBlockSubType ()));

    return text.toString ();
  }
}

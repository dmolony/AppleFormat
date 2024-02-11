package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;

// -----------------------------------------------------------------------------------//
public class IndexProdosBlock extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public IndexProdosBlock (AppleBlock appleBlock)
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
    String subType = appleBlock.getBlockSubType ();
    String subTypeText = subType.equals ("M-INDEX") ? "Master " : "";
    String fileName = appleBlock.getFileOwner ().getFileName ();

    StringBuilder text =
        getHeader (String.format ("Prodos %sIndex : %s", subTypeText, fileName));

    for (int i = 0; i < 256; i++)
    {
      text.append (
          String.format ("%02X        %02X %02X", i, buffer[i], buffer[i + 256]));
      if (buffer[i] != 0 || buffer[i + 256] != 0)
      {
        int blockNo = Utility.intValue (buffer[i], buffer[i + 256]);
        //        String valid = disk.isValidAddress (blockNo) ? "" : " *** invalid ***";
        text.append (String.format ("         block %,6d%n", blockNo));
      }
      else
        text.append ("\n");
    }

    return Utility.rtrim (text);
  }
}

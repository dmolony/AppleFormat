package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.ForkProdos;

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
    AppleFile appleFile = appleBlock.getFileOwner ();
    byte[] buffer = appleBlock.read ();
    String subType = appleBlock.getBlockSubType ();
    String subTypeText = subType.equals ("M-INDEX") ? "Master " : "";
    String fileName = appleFile.getFileName ();

    String name = appleFile.isFork ()
        ? ((ForkProdos) appleFile).getParentFile ().getFileName () + " : " : "";

    StringBuilder text =
        getHeader (String.format ("Prodos %sIndex : %s %s", subTypeText, name, fileName));

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

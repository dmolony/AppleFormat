package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
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
    byte[] buffer = appleBlock.getBuffer ();
    String subType = appleBlock.getBlockSubType ();
    String subTypeText = subType.equals ("M-INDEX") ? "Master " : "";
    String fileName = appleFile.getFileName ();

    String name = appleFile.isFork ()
        ? ((ForkProdos) appleFile).getParentFile ().getFileName () + " : " : "";

    StringBuilder text =
        getHeader (String.format ("Prodos %sIndex : %s %s", subTypeText, name, fileName));
    AppleFileSystem fs = appleFile.getParentFileSystem ();

    for (int i = 0; i < 256; i++)
    {
      text.append (
          String.format ("%02X        %02X %02X", i, buffer[i], buffer[i + 256]));
      int blockNo = Utility.intValue (buffer[i], buffer[i + 256]);
      if (blockNo != 0)
      {
        String valid = fs.isValidAddress (blockNo) ? "" : " *** invalid ***";
        text.append (String.format ("         block %,6d  %s%n", blockNo, valid));
      }
      else
        text.append ("\n");
    }

    return Utility.rtrim (text);
  }
}

package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.FsProdos;

// -----------------------------------------------------------------------------------//
public class VolumeBitmapBlock extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public VolumeBitmapBlock (AppleBlock appleBlock)
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
    FsProdos fs = (FsProdos) appleBlock.getFileSystem ();

    StringBuilder text = getHeader ("Volume Bitmap");

    int ptr = 0;
    int address = (appleBlock.getBlockNo () - fs.getBitmapBlockNo ()) * 0x1000;

    while (ptr < 512)
    {
      int val = buffer[ptr] & 0xFF;
      text.append (String.format ("  %5d   %02X            %04X : ", ptr, val, address));
      ptr++;
      address += 8;

      for (int i = 0; i < 8; i++)
      {
        text.append ((val & 0x80) == 0 ? "X " : ". ");
        val <<= 1;
      }

      text.append ("\n");
    }

    return Utility.rtrim (text);
  }
}

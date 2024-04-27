package com.bytezone.appleformat.block;

import com.bytezone.filesystem.AppleBlock;

// -----------------------------------------------------------------------------------//
public abstract class CatalogDosBlock extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  protected static int CATALOG_ENTRY_SIZE = 35;
  protected static final String[] fileTypes =
      { "Text file", "Integer Basic program", "Applesoft Basic program", "Binary file",
          "SS file", "Relocatable file", "AA file", "Lisa file" };

  // ---------------------------------------------------------------------------------//
  public CatalogDosBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  static String getName (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    //    int max = buffer[offset] == (byte) 0xFF ? 32 : 33;
    for (int i = offset; i < offset + length; i++)
    {
      int c = buffer[i] & 0xFF;
      if (c == 136)
      {
        if (text.length () > 0)
          text.deleteCharAt (text.length () - 1);
        continue;
      }
      if (c > 127)
        c -= c < 160 ? 64 : 128;
      if (c < 32)                                 // non-printable
        text.append ("^" + (char) (c + 64));
      else
        text.append ((char) c);                   // standard ascii
    }
    return text.toString ();
  }
}

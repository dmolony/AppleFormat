package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;

public class CatalogLbr extends AbstractFormattedAppleBlock
{
  private static int CATALOG_ENTRY_SIZE = 32;

  // ---------------------------------------------------------------------------------//
  public CatalogLbr (AppleBlock appleBlock)
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
    //    FsLbr fs = (FsLbr) appleBlock.getFileSystem ();

    StringBuilder text = getHeader ("LBR Catalog Sector");

    for (int i = 0; i < buffer.length; i += CATALOG_ENTRY_SIZE)
    {
      addText (text, buffer, i, 1, "Status");
      addText (text, buffer, i + 1, 4, "File name : " + new String (buffer, i + 1, 8));
      addText (text, buffer, i + 5, 4, "");
      addText (text, buffer, i + 9, 3, "Extension : " + new String (buffer, i + 9, 3));
      addText (text, buffer, i + 12, 2,
          "Index     : " + Utility.getShort (buffer, i + 12));
      addText (text, buffer, i + 14, 2,
          "Length    : " + Utility.getShort (buffer, i + 14));
      addText (text, buffer, i + 16, 2,
          "CRC       : " + Utility.getShort (buffer, i + 16));
      addText (text, buffer, i + 18, 2,
          "Created   : " + Utility.getShort (buffer, i + 18));
      addText (text, buffer, i + 20, 2,
          "Modified  : " + Utility.getShort (buffer, i + 20));
      addText (text, buffer, i + 22, 2,
          "Cr Time   : " + Utility.getShort (buffer, i + 22));
      addText (text, buffer, i + 24, 2,
          "Mod time  : " + Utility.getShort (buffer, i + 24));
      addText (text, buffer, i + 26, 2,
          "Pad       : " + Utility.getShort (buffer, i + 26));
      addText (text, buffer, i + 28, 4, "");
      text.append ("\n");
    }

    return Utility.rtrim (text);
  }
}

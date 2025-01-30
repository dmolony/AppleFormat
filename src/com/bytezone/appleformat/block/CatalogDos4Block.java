package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.FsDos;

// -----------------------------------------------------------------------------------//
public class CatalogDos4Block extends CatalogDosBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public CatalogDos4Block (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleBlock.getBuffer ();
    FsDos fs = (FsDos) appleBlock.getFileSystem ();

    StringBuilder text = getHeader (fs.getFileSystemType () + " Catalog Sector");
    addText (text, buffer, 0, 1, "Not used");
    addText (text, buffer, 1, 2, "Next catalog track/sector");
    addText (text, buffer, 3, 4, "Not used");
    addText (text, buffer, 7, 4, "Not used");

    for (int i = 11; i <= 255; i += CATALOG_ENTRY_SIZE)
    {
      boolean deleted = (buffer[i] & 0x80) != 0;
      boolean zero = (buffer[i] & 0x40) != 0;
      int track = buffer[i] & 0x3F;
      int sector = buffer[i + 1] & 0x1F;
      boolean locked = (buffer[i + 2] & 0x80) != 0;
      int type = buffer[i + 2] & 0x7F;

      if (deleted)
      {
        addText (text, buffer, i, 2, String.format ("DEL: file @ %02X %02X",
            buffer[i] & 0x7F, buffer[i + 1] & 0x1F));
        addText (text, buffer, i + 2, 1, "DEL: File type " + getType (buffer[i + 2]));
        if (buffer[i + 3] == 0)
          addText (text, buffer, i + 3, 4, "");
        else
          addText (text, buffer, i + 3, 4, "DEL: " + getName (buffer, i + 3, 24));
        addTextAndDecimal (text, buffer, i + 33, 2, "DEL: Sector count");
      }
      else if (track > 0 || zero)               // file exists
      {
        addText (text, buffer, i, 2, "TS list track/sector");
        addText (text, buffer, i + 2, 1, "File type " + getType (buffer[i + 2]));

        addText (text, buffer, i + 3, 4, getName (buffer, i + 3, 24));
        for (int j = 0; j < 20; j += 4)
          addText (text, buffer, i + j + 7, 4, "");
        addText (text, buffer, i + 27, 4,
            "Modified : " + Utility.getDos4LocalDateTime (buffer, i + 27));
        addText (text, buffer, i + 31, 2, "");

        addTextAndDecimal (text, buffer, i + 33, 2, "Sector count");
      }
      else                                  // no file
      {
        addText (text, buffer, i + 0, 2, "");
        addText (text, buffer, i + 2, 1, "");
        addText (text, buffer, i + 3, 4, "");
        addText (text, buffer, i + 33, 2, "");
      }
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  private String getType (byte value)
  // ---------------------------------------------------------------------------------//
  {
    int type = value & 0x7F;
    boolean locked = (value & 0x80) != 0;
    int val = 7;
    for (int i = 64; i > type; val--, i /= 2)
      ;
    return "(" + fileTypes[val] + (locked ? ", locked)" : ", unlocked)");
  }
}

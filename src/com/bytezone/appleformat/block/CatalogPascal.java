package com.bytezone.appleformat.block;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.FsPascal;

// -----------------------------------------------------------------------------------//
public class CatalogPascal extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  private final DateTimeFormatter dtf =
      DateTimeFormatter.ofLocalizedDate (FormatStyle.SHORT);
  private static String[] fileTypes =
      { "Volume", "Bad", "Code", "Text", "Info", "Data", "Graf", "Foto", "SecureDir" };
  static final int CATALOG_ENTRY_SIZE = 26;

  // ---------------------------------------------------------------------------------//
  public CatalogPascal (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    FsPascal fsPascal = (FsPascal) appleBlock.getFileSystem ();
    //    List<AppleBlock> blocks = fsPascal.getCatalogBlocks ();
    byte[] buffer = fsPascal.getCatalogBuffer ();

    StringBuilder text = getHeader ("Pascal Catalog Sector");

    addTextAndDecimal (text, buffer, 0, 2, "First directory block");
    addTextAndDecimal (text, buffer, 2, 2, "Last directory block");

    addText (text, buffer, 4, 2, "File type : " + fileTypes[buffer[5]]);

    String name = HexFormatter.getPascalString (buffer, 6);
    addText (text, buffer, 6, 4, "");
    addText (text, buffer, 10, 4, "Volume name : " + name);
    addTextAndDecimal (text, buffer, 14, 2, "Blocks on disk");
    addTextAndDecimal (text, buffer, 16, 2, "Files on disk");
    addTextAndDecimal (text, buffer, 18, 2, "First block of volume");

    LocalDate localDate = Utility.getPascalLocalDate (buffer, 20);
    String date = localDate == null ? "--" : localDate.format (dtf);

    addText (text, buffer, 20, 2, "Most recent date setting : " + date);
    addTextAndDecimal (text, buffer, 22, 4, "Reserved");

    int ptr = CATALOG_ENTRY_SIZE;                     // skip the volume entry
    int totalFiles = Utility.getShort (buffer, 16);

    while (ptr < buffer.length && totalFiles > 0)
    {
      if (buffer[ptr + 6] == 0)                           // no name
      {
        ptr += CATALOG_ENTRY_SIZE;
        continue;
      }

      text.append ("\n");

      addTextAndDecimal (text, buffer, ptr + 0, 2, "File's first block");
      addTextAndDecimal (text, buffer, ptr + 2, 2, "File's last block");

      int type = buffer[ptr + 4] & 0x0F;
      if (type < fileTypes.length)
        addText (text, buffer, ptr + 4, 1, "File type : " + fileTypes[type]);

      int wildcard = buffer[ptr + 4] & 0xC0;
      addText (text, buffer, ptr + 5, 1, "Wildcard : " + wildcard);

      name = HexFormatter.getPascalString (buffer, ptr + 6);
      addText (text, buffer, ptr + 6, 4, "");
      addText (text, buffer, ptr + 10, 4, "");
      addText (text, buffer, ptr + 14, 4, "");
      addText (text, buffer, ptr + 18, 4, "File name : " + name);

      addTextAndDecimal (text, buffer, ptr + 22, 2, "Bytes in file's last block");

      localDate = Utility.getPascalLocalDate (buffer, ptr + 24);
      date = localDate == null ? "--" : localDate.format (dtf);
      addText (text, buffer, ptr + 24, 2, "Date : " + date);

      int firstBlock = Utility.getShort (buffer, ptr);
      if (firstBlock > 0)                                 // deleted files don't count
        --totalFiles;

      ptr += CATALOG_ENTRY_SIZE;
    }

    return Utility.rtrim (text);
  }
}

package com.bytezone.appleformat.block;

import static com.bytezone.appleformat.ProdosConstants.ENTRY_SIZE;
import static com.bytezone.appleformat.ProdosConstants.FREE;
import static com.bytezone.appleformat.ProdosConstants.GSOS_EXTENDED_FILE;
import static com.bytezone.appleformat.ProdosConstants.PASCAL_ON_PROFILE;
import static com.bytezone.appleformat.ProdosConstants.SAPLING;
import static com.bytezone.appleformat.ProdosConstants.SEEDLING;
import static com.bytezone.appleformat.ProdosConstants.SUBDIRECTORY;
import static com.bytezone.appleformat.ProdosConstants.SUBDIRECTORY_HEADER;
import static com.bytezone.appleformat.ProdosConstants.TREE;
import static com.bytezone.appleformat.ProdosConstants.VOLUME_HEADER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.ProdosConstants;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.FsProdos;

// -----------------------------------------------------------------------------------//
public class CatalogProdosBlock extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  static final DateTimeFormatter df = DateTimeFormatter.ofPattern ("d-LLL-yy");
  static final DateTimeFormatter tf = DateTimeFormatter.ofPattern ("H:mm");

  // ---------------------------------------------------------------------------------//
  public CatalogProdosBlock (AppleBlock appleBlock)
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
    FsProdos fs = (FsProdos) appleBlock.getFileSystem ();

    StringBuilder text = getHeader ("Prodos Catalog Sector");
    //    StringBuilder text = getHeader ("Volume Directory Block");

    addTextAndDecimal (text, buffer, 0, 2, "Previous block");
    addTextAndDecimal (text, buffer, 2, 2, "Next block");

    for (int i = 4, max = buffer.length - ENTRY_SIZE; i <= max; i += ENTRY_SIZE)
    {
      if (buffer[i] == 0 && buffer[i + 1] == 0)
        break;
      text.append ("\n");

      // first byte contains the file type (left nybble) and name length (right nybble)
      int fileType = (buffer[i] & 0xF0) >> 4;
      int nameLength = buffer[i] & 0x0F;
      String hex1 = String.format ("%02X", buffer[i] & 0xF0);
      String hex2 = String.format ("%02X", nameLength);

      // deleted files set file type and name length to zero, but the file 
      // name is still valid
      String typeText = hex1 + " = " + getType (buffer[i]);
      if (fileType == 0)
        addText (text, buffer, i, 1, typeText + " : " + getDeletedName (buffer, i + 1));
      else
        addText (text, buffer, i, 1, typeText + ", " + hex2 + " = Name length");

      addText (text, buffer, i + 1, 4,
          HexFormatter.getString (buffer, i + 1, nameLength));

      switch (fileType)
      {
        case FREE:
        case SEEDLING:
        case SAPLING:
        case TREE:
        case PASCAL_ON_PROFILE:
        case GSOS_EXTENDED_FILE:
        case SUBDIRECTORY:
          text.append (doFileDescription (buffer, i));
          break;
        case SUBDIRECTORY_HEADER:
          text.append (doSubdirectoryHeader (buffer, i));
          break;
        case VOLUME_HEADER:
          text.append (doVolumeDirectoryHeader (buffer, i));
          break;
        default:
          text.append ("Unknown\n");
      }
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  private String doFileDescription (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    int fileType = buffer[offset + 16] & 0xFF;
    int auxType = Utility.getShort (buffer, offset + 31);
    addText (text, buffer, offset + 16, 1,
        "File type (" + ProdosConstants.fileTypes[fileType] + ")");
    addTextAndDecimal (text, buffer, offset + 17, 2, "Key pointer");
    addTextAndDecimal (text, buffer, offset + 19, 2, "Blocks used");
    addTextAndDecimal (text, buffer, offset + 21, 3, "EOF");

    LocalDateTime created = Utility.getAppleDate (buffer, offset + 24);
    String dateC = created == null ? "" : created.format (df);
    addText (text, buffer, offset + 24, 4, "Creation date : " + dateC);

    addTextAndDecimal (text, buffer, offset + 28, 1, "Version");
    addText (text, buffer, offset + 29, 1, "Minimum version");
    addText (text, buffer, offset + 30, 1, "Access");
    addTextAndDecimal (text, buffer, offset + 31, 2,
        "Auxilliary type - " + getAuxilliaryText (fileType, auxType));

    LocalDateTime modified = Utility.getAppleDate (buffer, offset + 33);
    String dateM = modified == null ? "" : modified.format (df);
    addText (text, buffer, offset + 33, 4, "Modification date : " + dateM);

    addTextAndDecimal (text, buffer, offset + 37, 2, "Header pointer");
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  private String doVolumeDirectoryHeader (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    addText (text, buffer, offset + 16, 4, "Not used");
    text.append (getCommonHeader (buffer, offset));
    addTextAndDecimal (text, buffer, offset + 35, 2, "Bit map pointer");
    addTextAndDecimal (text, buffer, offset + 37, 2, "Total blocks");

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  private String doSubdirectoryHeader (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    addText (text, buffer, offset + 16, 1, "Hex $75");
    addText (text, buffer, offset + 17, 3, "Not used");

    text.append (getCommonHeader (buffer, offset));

    addTextAndDecimal (text, buffer, offset + 35, 2, "Parent block");
    addTextAndDecimal (text, buffer, offset + 37, 1, "Parent entry number");
    addTextAndDecimal (text, buffer, offset + 38, 1, "Parent entry length");

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  private String getCommonHeader (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    addText (text, buffer, offset + 20, 4, "Not used");
    LocalDateTime created = Utility.getAppleDate (buffer, offset + 24);
    String dateC = created == null ? "" : created.format (df);
    addText (text, buffer, offset + 24, 4, "Creation date : " + dateC);

    addText (text, buffer, offset + 28, 1, "Prodos version");
    addText (text, buffer, offset + 29, 1, "Minimum version");
    addText (text, buffer, offset + 30, 1, "Access");

    addTextAndDecimal (text, buffer, offset + 31, 1, "Entry length");
    addTextAndDecimal (text, buffer, offset + 32, 1, "Entries per block");
    addTextAndDecimal (text, buffer, offset + 33, 2, "File count");

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  private String getAuxilliaryText (int fileType, int auxType)
  // ---------------------------------------------------------------------------------//
  {
    switch (fileType)
    {
      case 0x04:
        return "record length";
      case 0xFD:
        return "address of stored variables";
      case 0x06:
      case 0xFC:
      case 0xFF:
        return "load address";
      case 0xB3:
        return "forked";
      case 0xC1:
        return "PIC";
      case 0xC8:
        return auxType == 0 ? "QuickDraw bitmap font"
            : auxType == 1 ? "Pointless truetype font" : "??";
      default:
        return "???";
    }
  }

  // ---------------------------------------------------------------------------------//
  private String getType (byte flag)
  // ---------------------------------------------------------------------------------//
  {
    switch ((flag & 0xF0) >> 4)
    {
      case FREE:
        return "Deleted";
      case SEEDLING:
        return "Seedling";
      case SAPLING:
        return "Sapling";
      case TREE:
        return "Tree";
      case PASCAL_ON_PROFILE:
        return "Pascal area on a Profile HD";
      case GSOS_EXTENDED_FILE:
        return "GS/OS extended file";
      case SUBDIRECTORY:
        return "Subdirectory";
      case SUBDIRECTORY_HEADER:
        return "Subdirectory Header";
      case VOLUME_HEADER:
        return "Volume Directory Header";
      default:
        return "???";
    }
  }

  // Deleted files leave the name intact, but set the name length to zero
  // Also - the pointers in the master blocks of a sapling or tree file are 
  // swapped when the file is deleted.

  // ---------------------------------------------------------------------------------//
  private String getDeletedName (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (int i = offset, max = offset + 15; i < max && buffer[i] != 0; i++)
      text.append ((char) (buffer[i] & 0xFF));

    return text.toString ();
  }
}

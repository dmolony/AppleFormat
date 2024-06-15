package com.bytezone.appleformat;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.block.Bin2HdrBlock;
import com.bytezone.appleformat.block.CatalogCpm;
import com.bytezone.appleformat.block.CatalogDos3Block;
import com.bytezone.appleformat.block.CatalogDos4Block;
import com.bytezone.appleformat.block.CatalogLbr;
import com.bytezone.appleformat.block.CatalogPascal;
import com.bytezone.appleformat.block.CatalogPascalCode;
import com.bytezone.appleformat.block.CatalogProdosBlock;
import com.bytezone.appleformat.block.DataBlock;
import com.bytezone.appleformat.block.DosBlock;
import com.bytezone.appleformat.block.EmptyBlock;
import com.bytezone.appleformat.block.ForkProdosBlock;
import com.bytezone.appleformat.block.FormattedAppleBlock;
import com.bytezone.appleformat.block.IndexProdosBlock;
import com.bytezone.appleformat.block.OrphanBlock;
import com.bytezone.appleformat.block.TsListBlock;
import com.bytezone.appleformat.block.VolumeBitmapBlock;
import com.bytezone.appleformat.block.VtocBlock;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.AppleBlock.BlockType;
import com.bytezone.filesystem.AppleFileSystem;

// -----------------------------------------------------------------------------------//
public class FormattedAppleBlockFactory
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public FormattedAppleBlockFactory (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {

  }

  // ---------------------------------------------------------------------------------//
  public FormattedAppleBlock getFormattedAppleBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockType ())
    {
      case BlockType.FILE_DATA -> new DataBlock (appleBlock);
      case BlockType.ORPHAN -> new OrphanBlock (appleBlock);
      case BlockType.EMPTY -> new EmptyBlock (appleBlock);
      case BlockType.FS_DATA -> getFormattedFsDataBlock (appleBlock);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedFsDataBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    AppleFileSystem fs = appleBlock.getFileSystem ();

    return switch (fs.getFileSystemType ())
    {
      case DOS3 -> getFormattedAppleBlockDos3 (appleBlock);
      case DOS4 -> getFormattedAppleBlockDos4 (appleBlock);
      case PRODOS -> getFormattedAppleBlockProdos (appleBlock);
      case PASCAL -> getFormattedAppleBlockPascal (appleBlock);
      case PASCAL_CODE -> getFormattedAppleBlockPascalCode (appleBlock);
      case CPM -> getFormattedAppleBlockCpm (appleBlock);
      case BIN2 -> getFormattedAppleBlockBin2 (appleBlock);
      case LBR -> getFormattedAppleBlockLbr (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + fs.getFileSystemType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockDos3 (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "DOS" -> new DosBlock (appleBlock);
      case "VTOC" -> new VtocBlock (appleBlock);
      case "CATALOG" -> new CatalogDos3Block (appleBlock);
      case "TSLIST" -> new TsListBlock (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockDos4 (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "DOS" -> new DosBlock (appleBlock);
      case "VTOC" -> new VtocBlock (appleBlock);
      case "CATALOG" -> new CatalogDos4Block (appleBlock);
      case "TSLIST" -> new TsListBlock (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockProdos (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "CATALOG", "FOLDER" -> new CatalogProdosBlock (appleBlock);
      case "INDEX", "M-INDEX" -> new IndexProdosBlock (appleBlock);
      case "V-BITMAP" -> new VolumeBitmapBlock (appleBlock);
      case "FORK" -> new ForkProdosBlock (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockPascal (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "CATALOG" -> new CatalogPascal (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockPascalCode (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "CATALOG" -> new CatalogPascalCode (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockCpm (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "CATALOG" -> new CatalogCpm (appleBlock);
      case "DOS" -> new DosBlock (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockBin2 (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "BIN2 HDR" -> new Bin2HdrBlock (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockLbr (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "CATALOG" -> new CatalogLbr (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }
}

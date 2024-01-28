package com.bytezone.appleformat;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.block.CatalogCpm;
import com.bytezone.appleformat.block.CatalogDos;
import com.bytezone.appleformat.block.CatalogPascal;
import com.bytezone.appleformat.block.CatalogProdos;
import com.bytezone.appleformat.block.DataBlock;
import com.bytezone.appleformat.block.DosBlock;
import com.bytezone.appleformat.block.EmptyBlock;
import com.bytezone.appleformat.block.FormattedAppleBlock;
import com.bytezone.appleformat.block.IndexProdos;
import com.bytezone.appleformat.block.OrphanBlock;
import com.bytezone.appleformat.block.TsList;
import com.bytezone.appleformat.block.VolumeBitmap;
import com.bytezone.appleformat.block.Vtoc;
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
      case DOS -> getFormattedAppleBlockDos (appleBlock);
      case PRODOS -> getFormattedAppleBlockProdos (appleBlock);
      case PASCAL -> getFormattedAppleBlockPascal (appleBlock);
      case CPM -> getFormattedAppleBlockCpm (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + fs.getFileSystemType ());
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleBlock getFormattedAppleBlockDos (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "DOS" -> new DosBlock (appleBlock);
      case "VTOC" -> new Vtoc (appleBlock);
      case "CATALOG" -> new CatalogDos (appleBlock);
      case "TSLIST" -> new TsList (appleBlock);
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
      case "CATALOG" -> new CatalogProdos (appleBlock);
      case "FOLDER" -> new CatalogProdos (appleBlock);
      case "INDEX" -> new IndexProdos (appleBlock);
      case "M-INDEX" -> new IndexProdos (appleBlock);
      case "V-BITMAP" -> new VolumeBitmap (appleBlock);
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
  private FormattedAppleBlock getFormattedAppleBlockCpm (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleBlock.getBlockSubType ())
    {
      case "CATALOG" -> new CatalogCpm (appleBlock);
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }
}

package com.bytezone.appleformat;

import java.util.prefs.Preferences;

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
    if (appleBlock.getBlockType () == BlockType.FILE_DATA)
      return new DataBlock (appleBlock);

    AppleFileSystem fs = appleBlock.getFileSystem ();

    return switch (fs.getFileSystemType ())
    {
      case DOS -> getFormattedAppleBlockDos (appleBlock);
      case PRODOS -> getFormattedAppleBlockProdos (appleBlock);
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
      default -> throw new IllegalArgumentException (
          "Unexpected value: " + appleBlock.getBlockSubType ());
    };
  }
}

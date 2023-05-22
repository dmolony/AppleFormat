package com.bytezone.appleformat;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.ForkProdos;

import javafx.scene.image.Image;

//-----------------------------------------------------------------------------------//
public class DataFileProdos extends AbstractFormattedAppleFile
//-----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public DataFileProdos (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this (appleFile, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  public DataFileProdos (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    return emptyImage;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    FileProdos file;
    if (appleFile instanceof ForkProdos forkProdos)
      file = forkProdos.getParentFile ();
    else
      file = (FileProdos) appleFile;

    StringBuilder text = new StringBuilder ();

    text.append (
        String.format ("File name .............. %-15s%n", appleFile.getFileName ()));
    text.append (String.format ("File type .............. %02X  %s%n",
        appleFile.getFileType (), appleFile.getFileTypeText ()));
    text.append (String.format ("Aux .................... %04X%n", file.getAuxType ()));
    text.append (
        String.format ("Blocks ................. %,9d%n", appleFile.getTotalBlocks ()));
    text.append (
        String.format ("EOF .................... %,9d%n", appleFile.getFileLength ()));

    return Utility.rtrim (text);
  }
}

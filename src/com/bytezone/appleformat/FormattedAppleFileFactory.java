package com.bytezone.appleformat;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.graphics.OriginalHiResImage;
import com.bytezone.appleformat.graphics.ShapeTable;
import com.bytezone.appleformat.text.Text;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem.FileSystemType;
import com.bytezone.filesystem.FileProdos;

// -----------------------------------------------------------------------------------//
public class FormattedAppleFileFactory
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    if (appleFile.isFileSystem () || appleFile.isFolder ())
      return new Catalog (appleFile);

    byte[] buffer = appleFile.read ();
    int type = appleFile.getFileType ();
    FileSystemType fileSystemType = appleFile.getFileSystemType ();

    if (fileSystemType == null)         // shouldn't happen
    {
      System.out.println ("FormattedAppleFileFactory cannot determine the FileSystemType");
      return new DataFile (appleFile, type, buffer);
    }

    FormattedAppleFile formattedAppleFile = switch (fileSystemType)
    {
      case DOS -> getFormattedDosFile (appleFile, type, buffer);
      case PRODOS -> getFormattedProdosFile (appleFile, type, buffer, appleFile.getLength (),
          ((FileProdos) appleFile).getAuxType ());
      case PASCAL -> getFormattedPascalFile (appleFile, type, buffer);
      case CPM -> getFormattedCpmFile (appleFile, type, buffer);
      case NUFX -> getFormattedNufxFile (appleFile, type, buffer);
      default -> new DataFile (appleFile, type, buffer);
    };

    return formattedAppleFile;
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedDosFile (AppleFile appleFile, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      case 0 -> new Text (appleFile, buffer, 0, buffer.length);
      case 1 -> new IntegerBasicProgram (appleFile, buffer, 2, Utility.getShort (buffer, 0));
      case 2 -> new ApplesoftBasicProgram (appleFile, buffer, 2, Utility.getShort (buffer, 0));
      case 4, 16 -> checkDosBinary (appleFile, fileType, buffer);
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosBinary (AppleFile appleFile, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length > 4)
    {
      int address = Utility.getShort (buffer, 0);
      int length = Utility.getShort (buffer, 2);

      if (ShapeTable.isShapeTable (buffer, 4, length))
        return new ShapeTable (appleFile, buffer, 4, length);

      if (address == 0x2000 || address == 0x4000)
      {
        if (length > 0x1F00 && length <= 0x4000)
          return new OriginalHiResImage (appleFile, buffer, 4, length, address);

        //        if (isScrunched (fileName, length))
        //          return new OriginalHiResImage (fileName, buffer, address, true);
      }

      return new AssemblerProgram (appleFile, buffer, 4, length, address);
    }

    return new DataFile (appleFile, fileType, buffer);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedProdosFile (AppleFile appleFile, int fileType,
      byte[] buffer, int length, int aux)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      case 0x04 -> new Text (appleFile, buffer, 0, length);
      case 0x06 -> checkProdosBinary (appleFile, buffer, length, aux);
      case 0xFC -> new ApplesoftBasicProgram (appleFile, buffer, 0, length);
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkProdosBinary (AppleFile appleFile, byte[] buffer, int length,
      int aux)
  // ---------------------------------------------------------------------------------//
  {
    if (ShapeTable.isShapeTable (buffer, 0, length))
      return new ShapeTable (appleFile, buffer, 0, length);

    return new AssemblerProgram (appleFile, buffer, 0, length, aux);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedPascalFile (AppleFile appleFile, int fileType,
      byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (AppleFile appleFile, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedNufxFile (AppleFile appleFile, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private boolean isScrunched (String name, int length)
  // ---------------------------------------------------------------------------------//
  {
    if ((name.equals ("FLY LOGO") || name.equals ("FLY LOGO SCRUNCHED")) && length == 0x14FA)
      return true;

    if (name.equals ("BBROS LOGO SCRUNCHED") && length == 0x0FED)
      return true;

    return false;
  }
}

package com.bytezone.appleformat;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.graphics.ShapeTable;
import com.bytezone.appleformat.text.Text;
import com.bytezone.filesystem.AppleFile;
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
    String fileName = appleFile.getFileName ();
    int type = appleFile.getFileType ();

    if (appleFile.getFileSystem ().getFileSystemType () == null)         // unfinished - NuFX etc
    {
      System.out.println ("FormattedAppleFileFactory cannot determine the FileSystemType");
      return new DataFile (fileName, type, buffer);
    }

    FormattedAppleFile formattedAppleFile = switch (appleFile.getFileSystem ().getFileSystemType ())
    {
      case DOS -> getFormattedDosFile (fileName, type, buffer);
      case PRODOS -> getFormattedProdosFile (fileName, type, buffer, appleFile.getLength (),
          ((FileProdos) appleFile).getAuxType ());
      case PASCAL -> getFormattedPascalFile (fileName, type, buffer);
      case CPM -> getFormattedCpmFile (fileName, type, buffer);
      case NUFX -> getFormattedNufxFile (fileName, type, buffer);
      default -> new DataFile (fileName, type, buffer);
    };

    formattedAppleFile.setAppleFile (appleFile);
    return formattedAppleFile;
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedDosFile (String fileName, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      case 0 -> new Text (fileName, buffer, 0, buffer.length);
      case 1 -> new IntegerBasicProgram (fileName, buffer, 2, Utility.getShort (buffer, 0));
      case 2 -> new ApplesoftBasicProgram (fileName, buffer, 2, Utility.getShort (buffer, 0));
      case 4, 16 -> checkDosBinary (fileName, fileType, buffer);
      default -> new DataFile (fileName, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosBinary (String fileName, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length > 4)
    {
      int address = Utility.getShort (buffer, 0);
      int length = Utility.getShort (buffer, 2);
      if (ShapeTable.isShapeTable (buffer, 4, length))
        return new ShapeTable (fileName, buffer, 4, length);

      return new AssemblerProgram (fileName, buffer, 4, length, address);
    }

    return new DataFile (fileName, fileType, buffer);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedProdosFile (String fileName, int fileType, byte[] buffer,
      int length, int aux)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      case 0x04 -> new Text (fileName, buffer, 0, length);
      case 0x06 -> checkProdosBinary (fileName, buffer, length, aux);
      case 0xFC -> new ApplesoftBasicProgram (fileName, buffer, 0, length);
      default -> new DataFile (fileName, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkProdosBinary (String fileName, byte[] buffer, int length, int aux)
  // ---------------------------------------------------------------------------------//
  {
    if (ShapeTable.isShapeTable (buffer, 0, length))
      return new ShapeTable (fileName, buffer, 0, length);

    return new AssemblerProgram (fileName, buffer, 0, length, aux);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedPascalFile (String fileName, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      default -> new DataFile (fileName, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (String fileName, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      default -> new DataFile (fileName, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedNufxFile (String fileName, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      default -> new DataFile (fileName, fileType, buffer);
    };
  }
}

package com.bytezone.appleformat;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.graphics.OriginalHiResImage;
import com.bytezone.appleformat.graphics.ShapeTable;
import com.bytezone.appleformat.text.Text;
import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.FileBinary2;
import com.bytezone.filesystem.FileCpm;
import com.bytezone.filesystem.FileDos;
import com.bytezone.filesystem.FileNuFX;
import com.bytezone.filesystem.FilePascal;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.ForkNuFX;
import com.bytezone.filesystem.ForkProdos;
import com.bytezone.filesystem.ForkedFile;

// -----------------------------------------------------------------------------------//
public class FormattedAppleFileFactory
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    if (appleFile instanceof AppleContainer container)
      return new Catalog (container);

    if (appleFile.isEmbeddedFileSystem ())
      return new Catalog (appleFile.getEmbeddedFileSystem ());

    if (appleFile.isForkedFile ())
      return new Catalog ((ForkedFile) appleFile);

    FormattedAppleFile formattedAppleFile = switch (appleFile.getFileSystemType ())
    {
      case DOS -> getFormattedDosFile ((FileDos) appleFile);
      case PRODOS -> getFormattedProdosFile (appleFile);
      case PASCAL -> getFormattedPascalFile ((FilePascal) appleFile);
      case CPM -> getFormattedCpmFile ((FileCpm) appleFile);
      case NUFX -> getFormattedNufxFile (appleFile);
      case BIN2 -> getFormattedBin2File ((FileBinary2) appleFile);
      default -> new DataFile (appleFile, appleFile.getFileType (), appleFile.read ());
    };

    return formattedAppleFile;
  }

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFileSystem appleFileSystem)
  // ---------------------------------------------------------------------------------//
  {
    return new Catalog (appleFileSystem);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedDosFile (FileDos appleFile)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleFile.read ();
    int fileType = appleFile.getFileType ();

    return switch (fileType)
    {
      case 0 -> new Text (appleFile, buffer, 0, buffer.length);
      case 1 -> new IntegerBasicProgram (appleFile, buffer, 2,
          Utility.getShort (buffer, 0));
      case 2 -> new ApplesoftBasicProgram (appleFile, buffer, 2,
          Utility.getShort (buffer, 0));
      case 4, 16 -> checkDosBinary (appleFile, fileType, buffer);
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosBinary (FileDos appleFile, int fileType,
      byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length <= 4)
      return new DataFile (appleFile, fileType, buffer);

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

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedProdosFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    int length = 0;
    byte[] buffer;
    int auxType;

    if (appleFile instanceof ForkProdos fork)
    {
      length = fork.getFileLength ();
      buffer = fork.read ();
      auxType = fork.getParentFile ().getAuxType ();
    }
    else
    {
      length = appleFile.getFileLength ();
      buffer = appleFile.read ();
      auxType = ((FileProdos) appleFile).getAuxType ();
    }

    int fileType = appleFile.getFileType ();

    return switch (fileType)
    {
      case 0x04 -> new Text (appleFile, buffer, 0, length);
      case 0x06 -> checkProdosBinary (appleFile, buffer, length, auxType);
      case 0xFC -> new ApplesoftBasicProgram (appleFile, buffer, 0, length);
      case 0xFA -> new IntegerBasicProgram (appleFile, buffer, 0, length);
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkProdosBinary (AppleFile appleFile, byte[] buffer,
      int length, int aux)
  // ---------------------------------------------------------------------------------//
  {
    if (ShapeTable.isShapeTable (buffer, 0, length))
      return new ShapeTable (appleFile, buffer, 0, length);

    return new AssemblerProgram (appleFile, buffer, 0, length, aux);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedPascalFile (FilePascal appleFile)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleFile.read ();
    int fileType = appleFile.getFileType ();

    return switch (fileType)
    {
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (FileCpm appleFile)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleFile.read ();
    int fileType = appleFile.getFileType ();

    return switch (fileType)
    {
      default -> new DataFile (appleFile, fileType, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedBin2File (FileBinary2 appleFile)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleFile.read ();
    int fileType = appleFile.getFileType ();
    int length = appleFile.getEof ();
    int auxType = appleFile.getAuxType ();

    switch (appleFile.getOsType ())
    {
      case 0:                                           // Prodos
        return switch (fileType)
        {
          case 0x04 -> new Text (appleFile, buffer, 0, length);
          case 0x06 -> checkProdosBinary (appleFile, buffer, length, auxType);
          case 0xFC -> new ApplesoftBasicProgram (appleFile, buffer, 0, length);
          case 0xFA -> new IntegerBasicProgram (appleFile, buffer, 0, length);
          default -> new DataFile (appleFile, fileType, buffer);
        };

      case 1:                                           // Dos 3.3
      case 2:                                           // Dos 3.2 or 3.1
        return new DataFile (appleFile, fileType, buffer);

      case 3:                                           // Pascal
        return new DataFile (appleFile, fileType, buffer);
    }

    return new DataFile (appleFile, fileType, buffer);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedNufxFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    int fileSystemId = 0;
    byte[] buffer = null;
    int length = 0;
    int auxType = 0;

    if (appleFile instanceof ForkNuFX fork)
    {
      fork.getFileSystemId ();
      buffer = fork.read ();
      length = fork.getFileLength ();
      //      auxType = ???
    }
    else
    {
      FileNuFX file = (FileNuFX) appleFile;
      fileSystemId = file.getFileSystemId ();
      buffer = file.read ();
      length = file.getFileLength ();
      auxType = file.getAuxType ();
    }

    int fileType = appleFile.getFileType ();

    if (buffer == null)
      return new DataFile (appleFile, fileType, buffer, 0, 0);

    switch (fileSystemId)
    {
      case 1:                                     // Prodos/Sos
        return switch (fileType)
        {
          case 0x04 -> new Text (appleFile, buffer, 0, length);
          case 0x06 -> checkProdosBinary (appleFile, buffer, length, auxType);
          case 0xFC -> new ApplesoftBasicProgram (appleFile, buffer, 0, length);
          case 0xFA -> new IntegerBasicProgram (appleFile, buffer, 0, length);
          default -> new DataFile (appleFile, fileType, buffer, 0, length);
        };

      case 2:                                     // Dos 3.3
      case 3:                                     // Dos 3.2
      case 4:                                     // Pascal
      case 8:                                     // CPM
        return new DataFile (appleFile, fileType, buffer, 0, length);

      default:
        return new DataFile (appleFile, fileType, buffer, 0, length);
    }
  }

  // ---------------------------------------------------------------------------------//
  private boolean isScrunched (String name, int length)
  // ---------------------------------------------------------------------------------//
  {
    if ((name.equals ("FLY LOGO") || name.equals ("FLY LOGO SCRUNCHED"))
        && length == 0x14FA)
      return true;

    if (name.equals ("BBROS LOGO SCRUNCHED") && length == 0x0FED)
      return true;

    return false;
  }
}

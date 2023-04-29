package com.bytezone.appleformat;

import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_APPLESOFT_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BINARY;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_INTEGER_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PNT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_TEXT;

import java.io.File;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.graphics.AppleGraphics;
import com.bytezone.appleformat.graphics.AppleGraphics3201;
import com.bytezone.appleformat.graphics.AppleGraphicsPic0000;
import com.bytezone.appleformat.graphics.AppleGraphicsPic0001;
import com.bytezone.appleformat.graphics.AppleGraphicsPic0002;
import com.bytezone.appleformat.graphics.AppleGraphicsPnt0000;
import com.bytezone.appleformat.graphics.AppleGraphicsPnt0002;
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
  public FormattedAppleFile getFormattedAppleFile (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    return new LocalFolder (localFile);
  }

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
        return new AppleGraphics (appleFile, buffer, 4, length, address);

      //        if (isScrunched (fileName, length))
      //          return new OriginalHiResImage (fileName, buffer, address, true);
    }

    return new AssemblerProgram (appleFile, buffer, 4, length, address);
  }

  // http://www.1000bit.it/support/manuali/apple/technotes/ftyp/ft.about.html
  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedProdosFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    int length = 0;
    byte[] buffer;
    int aux;

    if (appleFile instanceof ForkProdos fork)
    {
      length = fork.getFileLength ();
      buffer = fork.read ();
      aux = fork.getParentFile ().getAuxType ();
    }
    else
    {
      length = appleFile.getFileLength ();
      buffer = appleFile.read ();
      aux = ((FileProdos) appleFile).getAuxType ();
    }

    int fileType = appleFile.getFileType ();

    return switch (fileType)
    {
      case FILE_TYPE_TEXT -> new Text (appleFile, buffer, 0, length);
      case FILE_TYPE_BINARY -> checkProdosBinary (appleFile, buffer, length, aux);
      case FILE_TYPE_PNT -> checkGraphics (appleFile, fileType, aux, buffer);
      case FILE_TYPE_PIC -> checkGraphics (appleFile, fileType, aux, buffer);
      case FILE_TYPE_APPLESOFT_BASIC -> new ApplesoftBasicProgram (appleFile, buffer, 0,
          length);
      case FILE_TYPE_INTEGER_BASIC -> new IntegerBasicProgram (appleFile, buffer, 0,
          length);
      default -> new DataFileProdos ((FileProdos) appleFile, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  // BIN
  // 06  0000  AppleGraphicsPic0002   .3200 (C1 0002)
  // 06  0000  AppleGraphics3201      .3201 
  // 06  2000  AppleGraphics
  // 06  4000  AppleGraphics

  // FOT
  // 08  ....                         see File Type Note 8
  // 08  4000                         Hi-Res (packed)
  // 08  4001                         Double Hi-Res (packed)

  // PNT
  // C0  0000  AppleGraphicsPnt0000   Paintworks SHR (packed)
  // C0  0001  AppleGraphicsPic0000   IIGS Super Hi-Res Graphics Screen Image (packed)
  // C0  0002  AppleGraphicsPnt0002
  // C0  0003  AppleGraphicsPic0001   IIGS QuickDraw II Picture File (packed)
  // C0  1000  AppleGraphicsPic0000
  // C0  8000  AppleGraphicsPnt0000   Paintworks Gold

  // PIC
  // C1  0000  AppleGraphicsPic0000   IIGS Super Hi-Res Graphics Screen Image (unpacked)
  // C1  0001  AppleGraphicsPic0001   IIGS QuickDraw II Picture File (unpacked)
  // C1  0002  AppleGraphicsPic0002
  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkGraphics (AppleFile appleFile, int fileType, int aux,
      byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (fileType == FILE_TYPE_PNT && (aux == 0 || aux == 0x8000))
      return new AppleGraphicsPnt0000 (appleFile, buffer);

    if (fileType == FILE_TYPE_PNT && aux == 1)
    {
      int size = Utility.calculateBufferSize (buffer, 0);
      byte[] unpackedBuffer = new byte[size];
      Utility.unpackBytes (buffer, 0, buffer.length, unpackedBuffer, 0);
      return new AppleGraphicsPic0000 (appleFile, unpackedBuffer);
    }

    if (fileType == FILE_TYPE_PNT && aux == 2)
      return new AppleGraphicsPnt0002 (appleFile, buffer);

    if (fileType == FILE_TYPE_PNT && aux == 3)
    {
      System.out.printf ("Found PNT aux 0003 : %s%n", appleFile.getFileName ());

      int size = Utility.calculateBufferSize (buffer, 0);
      byte[] unpackedBuffer = new byte[size];
      Utility.unpackBytes (buffer, 0, buffer.length, unpackedBuffer, 0);
      return new AppleGraphicsPic0001 (appleFile, unpackedBuffer);
    }

    if (fileType == FILE_TYPE_PNT && aux == 0x1000)             // same as PIC/0000
      return new AppleGraphicsPic0000 (appleFile, buffer);

    if (fileType == FILE_TYPE_PIC && aux == 0)                  // same as PNT/1000
      return new AppleGraphicsPic0000 (appleFile, buffer);

    if (fileType == FILE_TYPE_PIC && aux == 1)
    {
      System.out.printf ("Found PIC aux 0001 : %s%n", appleFile.getFileName ());
      return new AppleGraphicsPic0001 (appleFile, buffer);
    }

    if (fileType == FILE_TYPE_PIC && aux == 2)
      return new AppleGraphicsPic0002 (appleFile, buffer);

    return new DataFileProdos ((FileProdos) appleFile, buffer);
  }

  // Another notable exception is the Fotofile (FOT) format inherited by ProDOS
  // from Apple SOS, which included metadata in the 121st byte (the first byte of
  // the first hole) indicating how it should be displayed (color mode, resolution),
  // or converted to other graphics formats.

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkProdosBinary (AppleFile appleFile, byte[] buffer,
      int length, int aux)
  // ---------------------------------------------------------------------------------//
  {
    if (ShapeTable.isShapeTable (buffer, 0, length))
      return new ShapeTable (appleFile, buffer, 0, length);

    if (aux == 0x2000 || aux == 0x4000)
    {
      if (length > 0x1F00 && length <= 0x4000)
        return new AppleGraphics (appleFile, buffer, 0, length, aux);
    }

    String name = appleFile.getFileName ();
    if (name.endsWith (".3200") && length < 38400)
    {
      name = name.replace (".3200", ".3201");
      System.out.printf ("Assuming %s should be %s%n", appleFile.getFileName (), name);
    }

    if (aux == 0 && name.endsWith (".3200"))
      return new AppleGraphicsPic0002 (appleFile, buffer);

    if (aux == 0 && name.endsWith (".3201"))
      return new AppleGraphics3201 (appleFile, buffer);

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

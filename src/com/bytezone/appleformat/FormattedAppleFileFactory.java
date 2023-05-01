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
import com.bytezone.appleformat.graphics.Pic0000;
import com.bytezone.appleformat.graphics.Pic0001;
import com.bytezone.appleformat.graphics.Pic0002;
import com.bytezone.appleformat.graphics.Pnt0000;
import com.bytezone.appleformat.graphics.Pnt0002;
import com.bytezone.appleformat.graphics.Pnt8005;
import com.bytezone.appleformat.graphics.ShapeTable;
import com.bytezone.appleformat.text.PascalText;
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
  // 06 BIN
  // 0000  Pic0002                .3200 (C1 0002) (unpacked Brooks)
  // 0000  AppleGraphics3201      .3201           (packed Brooks?)
  // 2000  AppleGraphics
  // 4000  AppleGraphics

  // 08 FOT
  // ....                         see File Type Note 8
  // 4000                         Hi-Res (packed)
  // 4001                         Double Hi-Res (packed)

  // C0 PNT
  // 0000  Pnt0000   Paintworks SHR (packed)
  // 0001  Pic0000   IIGS Super Hi-Res Graphics Screen Image (packed)
  // 0002  Pnt0002   IIGS Super HiRes Picture File (Apple Preferred Format)
  // 0003  Pic0001   IIGS QuickDraw II Picture File (packed)
  // 0004            packed Brooks .3201?
  // 1000  Pic0000   IIGS Super Hi-Res Graphics Screen Image (unpacked)
  // 8000  Pnt0000   Paintworks Gold (packed)
  // 8005  Pnt8005   Dreamworld

  // C1 PIC
  // 0000  Pic0000   IIGS Super Hi-Res Graphics Screen Image (unpacked)
  // 0001  Pic0001   IIGS QuickDraw II Picture File (unpacked)
  // 0002  Pic0002   Super HiRes 3200 color screen image (unpacked) (Brooks)
  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkGraphics (AppleFile appleFile, int fileType, int aux,
      byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    switch (fileType)
    {
      case FILE_TYPE_PNT:
        switch (aux)
        {
          case 0x0000:
          case 0x8000:
            return new Pnt0000 (appleFile, buffer);

          case 0x0001:
            return new Pic0000 (appleFile, Utility.unpackBytes (buffer));

          case 0x0002:
            return new Pnt0002 (appleFile, buffer);

          case 0x0003:
            System.out.printf ("*** Found PNT aux 0003 : %s%n", appleFile.getFileName ());
            return new Pic0001 (appleFile, Utility.unpackBytes (buffer));

          case 0x0004:
            System.out.printf ("*** Found PNT aux 0004 : %s%n", appleFile.getFileName ());
            return new AppleGraphics3201 (appleFile, buffer);

          case 0x1000:
            return new Pic0000 (appleFile, buffer);

          case 0x8005:
            System.out.printf ("*** Found PNT aux 8005 : %s%n", appleFile.getFileName ());
            return new Pnt8005 (appleFile, buffer);
        }
        break;

      case FILE_TYPE_PIC:
        switch (aux)
        {
          case 0x0000:
            return new Pic0000 (appleFile, buffer);

          case 0x0001:
            System.out.printf ("*** Found PIC aux 0001 : %s%n", appleFile.getFileName ());
            return new Pic0001 (appleFile, buffer);

          case 0x0002:
            return new Pic0002 (appleFile, buffer);
        }
        break;
    }

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

    if (isAPP (buffer))
      return new AppleGraphics3201 (appleFile, buffer);

    if (aux == 0x2000 || aux == 0x4000)
    {
      if (length > 0x1F00 && length <= 0x4000)
        return new AppleGraphics (appleFile, buffer, 0, length, aux);
    }

    String name = appleFile.getFileName ();
    if (name.endsWith (".3200") && length != 38400 && isAPP (buffer))
    {
      name = name.replace (".3200", ".3201");
      System.out.printf ("Assuming %s should be %s%n", appleFile.getFileName (), name);
    }

    if (name.endsWith (".3200") && (aux == 0 || aux == 0x1300))
      return new Pic0002 (appleFile, buffer);

    if (name.endsWith (".3201") && aux == 0)
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
      case 3 -> new PascalText (appleFile, buffer);
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
  private boolean isAPP (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length < 4)
      return false;

    return buffer[0] == (byte) 0xC1 && buffer[1] == (byte) 0xD0
        && buffer[2] == (byte) 0xD0 && buffer[3] == 0;
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

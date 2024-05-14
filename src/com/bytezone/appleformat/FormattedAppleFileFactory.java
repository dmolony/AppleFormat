package com.bytezone.appleformat;

import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ADB;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ANI;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_APPLESOFT_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ASP;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_AWP;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BAT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BINARY;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FNT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FONT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FOT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ICN;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_INTEGER_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_NON;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PNT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_TEXT;

import java.io.File;
import java.util.prefs.Preferences;

import com.bytezone.appleformat.appleworks.AppleworksADBFile;
import com.bytezone.appleformat.appleworks.AppleworksSSFile;
import com.bytezone.appleformat.appleworks.AppleworksWPFile;
import com.bytezone.appleformat.assembler.AssemblerPreferences;
import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicPreferences;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.BasicCpmProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.file.Catalog;
import com.bytezone.appleformat.file.DataFile;
import com.bytezone.appleformat.file.DataFileProdos;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.file.LocalFolder;
import com.bytezone.appleformat.file.ResourceFile;
import com.bytezone.appleformat.fonts.FontFile;
import com.bytezone.appleformat.fonts.FontValidationException;
import com.bytezone.appleformat.fonts.QuickDrawFont;
import com.bytezone.appleformat.graphics.Animation;
import com.bytezone.appleformat.graphics.AppleGraphics;
import com.bytezone.appleformat.graphics.AppleGraphics3201;
import com.bytezone.appleformat.graphics.AppleGraphicsA2FC;
import com.bytezone.appleformat.graphics.AppleImage;
import com.bytezone.appleformat.graphics.FaddenHiResImage;
import com.bytezone.appleformat.graphics.GraphicsPreferences;
import com.bytezone.appleformat.graphics.IconFile;
import com.bytezone.appleformat.graphics.Pic0000;
import com.bytezone.appleformat.graphics.Pic0001;
import com.bytezone.appleformat.graphics.Pic0002;
import com.bytezone.appleformat.graphics.Pnt0000;
import com.bytezone.appleformat.graphics.Pnt0002;
import com.bytezone.appleformat.graphics.Pnt8005;
import com.bytezone.appleformat.graphics.ShapeTable;
import com.bytezone.appleformat.text.CpmText;
import com.bytezone.appleformat.text.PascalText;
import com.bytezone.appleformat.text.Text;
import com.bytezone.appleformat.text.TextPreferences;
import com.bytezone.appleformat.visicalc.VisicalcFile;
import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.AppleForkedFile;
import com.bytezone.filesystem.FileBinary2;
import com.bytezone.filesystem.FileCpm;
import com.bytezone.filesystem.FileNuFX;
import com.bytezone.filesystem.FilePascal;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.FileProdos.ForkType;
import com.bytezone.filesystem.ForkNuFX;
import com.bytezone.filesystem.ForkProdos;

// -----------------------------------------------------------------------------------//
public class FormattedAppleFileFactory
// -----------------------------------------------------------------------------------//
{
  public static ApplesoftBasicPreferences basicPreferences;
  public static AssemblerPreferences assemblerPreferences;
  public static GraphicsPreferences graphicsPreferences;
  public static TextPreferences textPreferences;

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFileFactory (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    basicPreferences = new ApplesoftBasicPreferences (prefs);
    assemblerPreferences = new AssemblerPreferences (prefs);
    graphicsPreferences = new GraphicsPreferences (prefs);
    textPreferences = new TextPreferences (prefs);
  }

  // ---------------------------------------------------------------------------------//
  public ApplePreferences getPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileSystemType ())
    {
      case DOS3, DOS4 -> getDosPreferences (appleFile);
      case PRODOS -> getProdosPreferences (appleFile);
      case PASCAL -> getPascalPreferences (appleFile);
      case CPM -> getCpmPreferences (appleFile);
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getDosPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case 0 -> textPreferences;
      case 1 -> null;
      case 2, 32 -> basicPreferences;
      case 4, 16, 64 -> getDosBinaryPreferences (appleFile);
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getDosBinaryPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleFile.read ();

    int address = Utility.getShort (buffer, 0);
    int length = Utility.getShort (buffer, 2);

    if ((address == 0x2000 || address == 0x4000)        // hi-res page address
        && (length > 0x1F00 && length <= 0x4000))
      return graphicsPreferences;

    return assemblerPreferences;
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getProdosPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case FILE_TYPE_TEXT -> textPreferences;
      case FILE_TYPE_BINARY -> assemblerPreferences;
      case FILE_TYPE_PNT -> graphicsPreferences;
      case FILE_TYPE_PIC -> graphicsPreferences;
      case FILE_TYPE_ANI -> graphicsPreferences;
      case FILE_TYPE_FNT -> graphicsPreferences;
      case FILE_TYPE_FONT -> graphicsPreferences;
      case FILE_TYPE_APPLESOFT_BASIC -> basicPreferences;
      case FILE_TYPE_INTEGER_BASIC -> null;
      case FILE_TYPE_ASP -> null;
      case FILE_TYPE_AWP -> textPreferences;
      case FILE_TYPE_ADB -> null;
      case FILE_TYPE_ICN -> graphicsPreferences;
      case FILE_TYPE_BAT -> textPreferences;
      case FILE_TYPE_NON -> null;
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getPascalPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case 3 -> textPreferences;
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getCpmPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileTypeText ())
    {
      case "DOC" -> textPreferences;
      case "HLP" -> textPreferences;
      case "TXT" -> textPreferences;
      case "ASM" -> textPreferences;
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    assert localFile.isDirectory ();
    return new LocalFolder (localFile);
  }

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      if (appleFile instanceof AppleContainer container)
        return new Catalog (container);

      if (appleFile.hasEmbeddedFileSystem ())
        return new Catalog (appleFile.getEmbeddedFileSystem ());

      if (appleFile.isForkedFile ())
        return new Catalog ((AppleForkedFile) appleFile);

      return switch (appleFile.getFileSystemType ())
      {
        case DOS3 -> getFormattedDosFile (appleFile);
        case DOS4 -> getFormattedDosFile (appleFile);
        case PRODOS -> getFormattedProdosFile (appleFile);
        case PASCAL -> getFormattedPascalFile ((FilePascal) appleFile);
        case CPM -> getFormattedCpmFile ((FileCpm) appleFile);
        case NUFX -> getFormattedNufxFile (appleFile);
        case BIN2 -> getFormattedBin2File ((FileBinary2) appleFile);
        default -> new DataFile (appleFile);
      };
    }
    catch (Exception e)
    {
      return new FormatError (e);
    }
  }

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFileSystem appleFileSystem)
  // ---------------------------------------------------------------------------------//
  {
    return new Catalog (appleFileSystem);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedDosFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleFile.read ();              // multiple of 256

    return switch (appleFile.getFileType ())
    {
      case 0 -> checkDosText (appleFile, buffer);
      case 1 -> new IntegerBasicProgram (appleFile, buffer, 2,
          Utility.getShort (buffer, 0));
      case 2, 32 -> new ApplesoftBasicProgram (appleFile, buffer, 2,
          Utility.getShort (buffer, 0));
      case 4, 16, 64 -> checkDosBinary (appleFile, buffer);
      default -> new DataFile (appleFile, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosText (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (VisicalcFile.isVisicalcFile (buffer))
      return new VisicalcFile (appleFile, buffer);

    return new Text (appleFile, buffer);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosBinary (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length <= 4)
      return new DataFile (appleFile, buffer);

    int address = Utility.getShort (buffer, 0);
    int length = Utility.getShort (buffer, 2);

    if (ShapeTable.isShapeTable (buffer, 4, length))
      return new ShapeTable (appleFile, buffer, 4, length);

    if (address == 0x2000 || address == 0x4000)
    {
      if (length > 0x1F00 && length <= 0x4000)
        return new AppleGraphics (appleFile, buffer, 4, length);

      //        if (isScrunched (fileName, length))
      //          return new OriginalHiResImage (fileName, buffer, address, true);
    }

    return new AssemblerProgram (appleFile, buffer, 4, length, address);
  }

  // http://www.1000bit.it/support/manuali/apple/technotes/ftyp/ft.about.html
  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedProdosFile (AppleFile appleFile)
      throws FontValidationException
  // ---------------------------------------------------------------------------------//
  {
    int eof = 0;
    byte[] buffer;
    int aux;

    if (appleFile instanceof ForkProdos fork)
    {
      eof = fork.getFileLength ();
      buffer = fork.read ();
      aux = fork.getParentFile ().getAuxType ();

      if (fork.getForkType () == ForkType.RESOURCE)
        return new ResourceFile (appleFile, buffer, aux);
    }
    else
    {
      eof = appleFile.getFileLength ();
      buffer = appleFile.read ();
      aux = ((FileProdos) appleFile).getAuxType ();
    }

    return switch (appleFile.getFileType ())
    {
      case FILE_TYPE_TEXT -> new Text (appleFile, buffer, 0, eof);
      case FILE_TYPE_BINARY -> checkProdosBinary (appleFile, buffer, eof, aux);
      case FILE_TYPE_PNT -> checkGraphics (appleFile, buffer, aux);
      case FILE_TYPE_PIC -> checkGraphics (appleFile, buffer, aux);
      case FILE_TYPE_ANI -> checkGraphics (appleFile, buffer, aux);
      case FILE_TYPE_FOT -> checkGraphics (appleFile, buffer, aux);
      case FILE_TYPE_FNT -> new FontFile (appleFile, buffer, aux);
      case FILE_TYPE_FONT -> new QuickDrawFont (appleFile, buffer);
      case FILE_TYPE_APPLESOFT_BASIC -> new ApplesoftBasicProgram (appleFile, buffer, 0,
          eof);
      case FILE_TYPE_INTEGER_BASIC -> new IntegerBasicProgram (appleFile, buffer, 0, eof);
      case FILE_TYPE_ASP -> new AppleworksSSFile (appleFile, buffer);
      case FILE_TYPE_AWP -> new AppleworksWPFile (appleFile, buffer);
      case FILE_TYPE_ADB -> new AppleworksADBFile (appleFile, buffer);
      case FILE_TYPE_ICN -> new IconFile (appleFile, buffer);
      case FILE_TYPE_NON -> checkNon (appleFile, buffer, eof, aux);
      case FILE_TYPE_BAT -> new Text (appleFile, buffer, 0, eof);
      default -> new DataFileProdos (appleFile, buffer, aux);
    };
  }

  // ---------------------------------------------------------------------------------//
  // 00 NON
  //       AppleData              .TIFF && isTiff()  not supported by JavaFX

  // 06 BIN
  // 0000  Pic0002                .3200 (C1 0002) (unpacked Brooks)
  // 1300  Pic0002                .3200 (C1 0002) (unpacked Brooks)
  // 0000  AppleGraphics3201      .3201           (packed Brooks?)
  // 2000  AppleGraphics
  // 2000  AppleGraphicsA2FC      .A2FC           (double hires)
  // 2000  AppleGraphicsA2FC      .PAC            (double hires)
  // 4000  AppleGraphics
  //       AppleImage             isPng()
  //       AppleImage             isGif()

  // 08 FOT
  // ....                         see File Type Note 8
  // 4000                         Hi-Res (packed)
  // 4001                         Double Hi-Res (packed)
  // 8066  FaddenHiResImage

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
  // 4100  Pic0000

  // C2 ANI
  // 0000  Animation (Pic0000)

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkGraphics (AppleFile appleFile, byte[] buffer, int aux)
  // ---------------------------------------------------------------------------------//
  {
    switch (appleFile.getFileType ())
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
          case 0x4100:
            return new Pic0000 (appleFile, buffer);

          case 0x0001:
            System.out.printf ("*** Found PIC aux 0001 : %s%n", appleFile.getFileName ());
            return new Pic0001 (appleFile, buffer);

          case 0x0002:
            return new Pic0002 (appleFile, buffer);
        }
        break;

      case FILE_TYPE_FOT:
        switch (aux)
        {
          case 0x4000:
            System.out.printf ("*** Found FOT aux 4000 : %s%n", appleFile.getFileName ());
            break;

          case 0x4001:
            System.out.printf ("*** Found FOT aux 4001 : %s%n", appleFile.getFileName ());
            break;

          case 0x8066:
            return new FaddenHiResImage (appleFile, buffer);

          default:
            System.out.printf ("*** Found FOT : %s%n", appleFile.getFileName ());
            break;
        }
        break;

      case FILE_TYPE_ANI:
        return new Animation (appleFile, buffer);
    }

    return new DataFileProdos (appleFile, buffer, aux);
  }

  // Another notable exception is the Fotofile (FOT) format inherited by ProDOS
  // from Apple SOS, which included metadata in the 121st byte (the first byte of
  // the first hole) indicating how it should be displayed (color mode, resolution),
  // or converted to other graphics formats.

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkProdosBinary (AppleFile appleFile, byte[] buffer,
      int eof, int aux)
  // ---------------------------------------------------------------------------------//
  {
    if (ShapeTable.isShapeTable (buffer, 0, eof))
      return new ShapeTable (appleFile, buffer, 0, eof);

    if (isAPP (buffer))
      return new AppleGraphics3201 (appleFile, buffer);

    String name = appleFile.getFileName ();

    if (AppleImage.isGif (buffer) || AppleImage.isPng (buffer))
      return new AppleImage (appleFile, buffer);

    if (aux == 0x2000 || aux == 0x4000)
    {
      if (name.endsWith (".A2FC") || name.endsWith (".PAC"))
        return new AppleGraphicsA2FC (appleFile, buffer);

      if (eof > 0x1F00 && eof <= 0x4000)
        return new AppleGraphics (appleFile, buffer, 0, eof);
    }

    if (name.endsWith (".3200") && eof != 38400 && isAPP (buffer))
    {
      name = name.replace (".3200", ".3201");
      System.out.printf ("Assuming %s should be %s%n", appleFile.getFileName (), name);
    }

    if (name.endsWith (".3200") && (aux == 0 || aux == 0x1300))
      return new Pic0002 (appleFile, buffer);

    if (name.endsWith (".3201") && aux == 0)
      return new AppleGraphics3201 (appleFile, buffer);

    return new AssemblerProgram (appleFile, buffer, 0, eof, aux);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkNon (AppleFile appleFile, byte[] buffer, int eof,
      int aux)
  // ---------------------------------------------------------------------------------//
  {
    String name = appleFile.getFileName ();
    if (name.endsWith (".TIFF") && AppleImage.isTiff (buffer))
    {
      return new DataFile (appleFile, buffer, 0, eof);    // JavaFX doesn't support TIFF
      //      return new AppleImage (appleFile, buffer);
    }

    return new DataFile (appleFile, buffer, 0, eof);
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
      default -> new DataFile (appleFile, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (FileCpm appleFile)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleFile.read ();

    return switch (appleFile.getFileTypeText ())
    {
      case "DOC" -> new CpmText (appleFile, buffer);
      case "HLP" -> new CpmText (appleFile, buffer);
      case "TXT" -> new CpmText (appleFile, buffer);
      case "ASM" -> new CpmText (appleFile, buffer);
      case "BAS" -> new BasicCpmProgram (appleFile, buffer);
      default -> new DataFile (appleFile, buffer);
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
          default -> new DataFile (appleFile, buffer);
        };

      case 1:                                           // Dos 3.3
      case 2:                                           // Dos 3.2 or 3.1
        System.out.printf ("Bin2 file system: %d not written%n", appleFile.getOsType ());
        return new DataFile (appleFile, buffer);

      case 3:                                           // Pascal
        System.out.printf ("Bin2 file system: %d not written%n", appleFile.getOsType ());
        return new DataFile (appleFile, buffer);
    }

    System.out.printf ("Bin2 unknown file system: %d%n", appleFile.getOsType ());
    return new DataFile (appleFile, buffer);
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
      return new DataFile (appleFile, buffer, 0, 0);

    switch (fileSystemId)
    {
      case 1:                                     // Prodos/Sos
        return switch (fileType)
        {
          case 0x04 -> new Text (appleFile, buffer, 0, length);
          case 0x06 -> checkProdosBinary (appleFile, buffer, length, auxType);
          case 0xFC -> new ApplesoftBasicProgram (appleFile, buffer, 0, length);
          case 0xFA -> new IntegerBasicProgram (appleFile, buffer, 0, length);
          default -> new DataFile (appleFile, buffer);
        };

      case 2:                                     // Dos 3.3
      case 3:                                     // Dos 3.2
      case 4:                                     // Pascal
      case 8:                                     // CPM
        System.out.printf ("NuFX file system: %d not written%n", fileSystemId);
        return new DataFile (appleFile, buffer);

      default:
        System.out.printf ("NuFX unknown file system: %d%n", fileSystemId);
        return new DataFile (appleFile, buffer);
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

  // ---------------------------------------------------------------------------------//
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    basicPreferences.save ();
    assemblerPreferences.save ();
    textPreferences.save ();
    graphicsPreferences.save ();
  }
}

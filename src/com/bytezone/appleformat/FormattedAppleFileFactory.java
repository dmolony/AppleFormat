package com.bytezone.appleformat;

import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ADB;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ANI;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_APPLESOFT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_APPLESOFT_VARS;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ASP;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_AWP;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BAT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BINARY;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_CMD;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FNT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FONT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FOT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_GS_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_GWP;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ICN;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_INTEGER_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_NON;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PNT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_SRC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_SYS;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_TDF;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_TEXT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.bytezone.appleformat.appleworks.AppleworksADBFile;
import com.bytezone.appleformat.appleworks.AppleworksSSFile;
import com.bytezone.appleformat.appleworks.AppleworksWPFile;
import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.BasicCpmProgram;
import com.bytezone.appleformat.basic.BasicProgramGS;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.file.Catalog;
import com.bytezone.appleformat.file.DataFile;
import com.bytezone.appleformat.file.DataFileProdos;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.file.LocalFolder;
import com.bytezone.appleformat.file.PascalCode;
import com.bytezone.appleformat.file.PascalProcedure;
import com.bytezone.appleformat.file.PascalSegment;
import com.bytezone.appleformat.file.ResourceFile;
import com.bytezone.appleformat.file.StoredVariables;
import com.bytezone.appleformat.file.TdfFile;
import com.bytezone.appleformat.file.UnknownFile;
import com.bytezone.appleformat.fonts.DosCharacterSet;
import com.bytezone.appleformat.fonts.FontFile;
import com.bytezone.appleformat.fonts.FontValidationException;
import com.bytezone.appleformat.fonts.QuickDrawFont;
import com.bytezone.appleformat.graphics.Animation;
import com.bytezone.appleformat.graphics.AppleGraphics;
import com.bytezone.appleformat.graphics.AppleGraphics3201;
import com.bytezone.appleformat.graphics.AppleGraphicsA2FC;
import com.bytezone.appleformat.graphics.AppleImage;
import com.bytezone.appleformat.graphics.FaddenHiResImage;
import com.bytezone.appleformat.graphics.IconFile;
import com.bytezone.appleformat.graphics.Pic0000;
import com.bytezone.appleformat.graphics.Pic0001;
import com.bytezone.appleformat.graphics.Pic0002;
import com.bytezone.appleformat.graphics.Pnt0000;
import com.bytezone.appleformat.graphics.Pnt0002;
import com.bytezone.appleformat.graphics.Pnt8005;
import com.bytezone.appleformat.graphics.ShapeTable;
import com.bytezone.appleformat.text.AssemblerText;
import com.bytezone.appleformat.text.CpmText;
import com.bytezone.appleformat.text.DosText;
import com.bytezone.appleformat.text.DosText2;
import com.bytezone.appleformat.text.PascalText;
import com.bytezone.appleformat.text.ProdosText;
import com.bytezone.appleformat.text.Text;
import com.bytezone.appleformat.visicalc.VisicalcFile;
import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFile.ForkType;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.AppleForkedFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.FileBinary2;
import com.bytezone.filesystem.FileCpm;
import com.bytezone.filesystem.FileDos;
import com.bytezone.filesystem.FileNuFX;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.ForkNuFX;
import com.bytezone.filesystem.ForkProdos;
import com.bytezone.filesystem.FsDos;
import com.bytezone.filesystem.TextBlock;

// -----------------------------------------------------------------------------------//
public class FormattedAppleFileFactory
// -----------------------------------------------------------------------------------//
{
  static final int RETURN = 0x0D;
  static final int SEMI_COLON = 0x3B;
  static final int ASTERISK = 0x2A;
  static final int SPACE = 0x20;
  static final int DOUBLE_QUOTE = 0x22;
  static final int SINGLE_QUOTE = 0x27;

  PreferencesFactory preferencesFactory;

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFileFactory (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    preferencesFactory = new PreferencesFactory (prefs);
  }

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    assert localFile.isDirectory ();
    return new LocalFolder (localFile);
  }

  // NB - whenever an AppleFile's getFileBuffer() is called a Buffer is returned
  //      based on all of its data blocks but with length() equal to its EOF. If
  //      there is no EOF set, then length() is set to buffer.length().
  //      Random-access files will use TextBlocks instead.
  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      if (appleFile instanceof AppleContainer container    //
          && (appleFile.getFileType () == 0                // ??
              | appleFile.getFileType () == 15))           // directory file
        return new Catalog (container);

      if (appleFile.hasEmbeddedFileSystem ())
        return new Catalog (appleFile.getEmbeddedFileSystem ());

      if (appleFile.isForkedFile ())
        return new Catalog ((AppleForkedFile) appleFile);

      return switch (appleFile.getFileSystemType ())
      {
        case DOS3 -> getFormattedDosFile ((FileDos) appleFile);
        case DOS4 -> getFormattedDosFile ((FileDos) appleFile);
        case PRODOS -> getFormattedProdosFile (appleFile);
        case PASCAL -> getFormattedPascalFile (appleFile);
        case CPM -> getFormattedCpmFile ((FileCpm) appleFile);
        case NUFX -> getFormattedNufxFile (appleFile);
        case BIN2 -> getFormattedBin2File ((FileBinary2) appleFile);
        default -> new DataFile (appleFile);
      };
    }
    catch (Exception e)
    {
      e.printStackTrace ();
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
  private FormattedAppleFile getFormattedDosFile (FileDos appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case FsDos.FILE_TYPE_TEXT -> checkDosText (appleFile);
      case FsDos.FILE_TYPE_INTEGER_BASIC -> checkDosIntegerBasic (appleFile);
      case FsDos.FILE_TYPE_APPLESOFT, 32 -> checkDosApplesoft (appleFile);
      case FsDos.FILE_TYPE_BINARY, 16, 64 -> checkDosBinary (appleFile);
      default -> new DataFile (appleFile);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosIntegerBasic (FileDos fileDos)
  // ---------------------------------------------------------------------------------//
  {
    return new IntegerBasicProgram (fileDos, fileDos.getFileBuffer ());
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosApplesoft (FileDos fileDos)
  // ---------------------------------------------------------------------------------//
  {
    Buffer fileBuffer = fileDos.getFileBuffer ();

    ApplesoftBasicProgram basicProgram = new ApplesoftBasicProgram (fileDos, fileBuffer);

    // check for excessive space at the end of the basic program
    int endPtr = basicProgram.getEndPtr ();
    int eof = fileDos.getFileLength ();           // claimed, but could be wrong
    if (eof <= fileBuffer.max ())
    {
      int unusedSpace = eof - endPtr - 1;

      if (unusedSpace > 2)
      {
        byte[] buffer = fileBuffer.data ();
        int address = Utility.getApplesoftLoadAddress (buffer);
        Buffer dataBufferAsm = new Buffer (buffer, endPtr, unusedSpace);
        AssemblerProgram assemblerProgram =
            new AssemblerProgram (fileDos, dataBufferAsm, address + endPtr + 2);

        basicProgram.append (assemblerProgram);
      }
    }

    return basicProgram;
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosText (FileDos fileDos)
  // ---------------------------------------------------------------------------------//
  {
    // avoid the DataBuffer if using TextBlocks
    if (fileDos.isRandomAccess ())
      return new DosText2 (fileDos, fileDos.getTextBlocks ());

    if (VisicalcFile.isVisicalcFile (fileDos))
      return new VisicalcFile (fileDos);

    List<String> parameters = checkZardax (fileDos);      // not exhaustive
    if (parameters.size () > 0)
      return new DosText (fileDos);                       // eventually ZardaxText

    if (fileDos.getFileName ().endsWith (".S"))
      return new AssemblerText (fileDos);

    return new DosText (fileDos);
  }

  // for now this is just to stop some of the zardax files being treated as 
  // random-access files
  // ---------------------------------------------------------------------------------//
  private List<String> checkZardax (FileDos fileDos)
  // ---------------------------------------------------------------------------------//
  {
    Buffer dataBuffer = fileDos.getRawFileBuffer ();
    byte[] buffer = dataBuffer.data ();
    int ptr = dataBuffer.offset ();
    int max = dataBuffer.max ();
    List<String> parameters = new ArrayList<> ();

    if (buffer[ptr] != (byte) 0xDF)
      return parameters;

    while (ptr < max)
    {
      if (buffer[ptr] == (byte) 0xDF)
        parameters.add (getZardaxFormat (buffer, ptr).toUpperCase ());

      ++ptr;
    }

    return parameters;
  }

  // ---------------------------------------------------------------------------------//
  private String getZardaxFormat (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    String name = Utility.string (buffer, ptr + 1, 2);

    int value = 0;
    ptr += 3;

    while (buffer[ptr] >= (byte) 0xB0 && buffer[ptr] <= (byte) 0xB9)
      value = value * 10 + buffer[ptr++] - (byte) 0xB0;

    return value == 0 ? name : name + value;
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkDosBinary (FileDos appleFile)
  // ---------------------------------------------------------------------------------//
  {
    Buffer fileBuffer = appleFile.getFileBuffer ();
    byte[] buffer = fileBuffer.data ();

    if (buffer.length <= 4)
      return new DataFile (appleFile);

    int address = appleFile.getLoadAddress ();
    int length = fileBuffer.length ();
    String fileName = appleFile.getFileName ();

    if (fileName.endsWith (".SET"))
      return new DosCharacterSet (appleFile, fileBuffer);
    if (fileName.endsWith (".S"))
      return new AssemblerText (appleFile, fileBuffer);
    if (fileName.endsWith (".SOURCE"))
      return new AssemblerText (appleFile, fileBuffer);     // wrong but better

    //    if (fileName.endsWith (".L") && appleFile.getFileType () == 64)
    //      return new AssemblerText (appleFile, dataRecord);

    if (ShapeTable.isShapeTable (fileBuffer))
      return new ShapeTable (appleFile, fileBuffer);

    if (address == 0x2000 || address == 0x4000)
    {
      if (length > 0x1F00 && length <= 0x4000)
        return new AppleGraphics (appleFile, fileBuffer);

      //        if (isScrunched (fileName, length))
      //          return new OriginalHiResImage (fileName, buffer, address, true);
    }

    return new AssemblerProgram (appleFile, fileBuffer, address);
  }

  // http://www.1000bit.it/support/manuali/apple/technotes/ftyp/ft.about.html
  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedProdosFile (AppleFile appleFile)
      throws FontValidationException
  // ---------------------------------------------------------------------------------//
  {
    if (appleFile instanceof FileProdos fp)
      appleFile = fp.getForks ().get (0);             // data fork

    assert appleFile instanceof ForkProdos;

    int aux = appleFile.getAuxType ();

    // avoid the DataBuffer if using TextBlocks
    if (appleFile.isRandomAccess ())
    {
      List<? extends TextBlock> textBlocks = ((ForkProdos) appleFile).getTextBlocks ();
      return new ProdosText ((ForkProdos) appleFile, textBlocks, aux);
    }

    Buffer dataBuffer = appleFile.getFileBuffer ();

    if (((ForkProdos) appleFile).getForkType () == ForkType.RESOURCE)
      return new ResourceFile (appleFile);

    return switch (appleFile.getFileType ())
    {
      case FILE_TYPE_TEXT -> checkText (appleFile, dataBuffer, aux);
      case FILE_TYPE_GWP -> new Text (appleFile, dataBuffer);
      case FILE_TYPE_SYS -> new AssemblerProgram (appleFile, dataBuffer, aux);
      case FILE_TYPE_CMD -> new AssemblerProgram (appleFile, dataBuffer, aux);
      case FILE_TYPE_BINARY -> checkProdosBinary (appleFile, dataBuffer, aux);
      case FILE_TYPE_PNT -> checkGraphics (appleFile);
      case FILE_TYPE_PIC -> checkGraphics (appleFile);
      case FILE_TYPE_ANI -> checkGraphics (appleFile);
      case FILE_TYPE_FOT -> checkGraphics (appleFile);
      case FILE_TYPE_FNT -> new FontFile (appleFile, dataBuffer, aux);
      case FILE_TYPE_FONT -> new QuickDrawFont (appleFile);
      case FILE_TYPE_GS_BASIC -> new BasicProgramGS (appleFile, dataBuffer);
      case FILE_TYPE_TDF -> new TdfFile (appleFile, dataBuffer, aux);
      case FILE_TYPE_APPLESOFT -> new ApplesoftBasicProgram (appleFile);
      case FILE_TYPE_INTEGER_BASIC -> new IntegerBasicProgram (appleFile);
      case FILE_TYPE_ASP -> new AppleworksSSFile (appleFile, dataBuffer);
      case FILE_TYPE_AWP -> new AppleworksWPFile (appleFile, dataBuffer);
      case FILE_TYPE_ADB -> new AppleworksADBFile (appleFile, dataBuffer);
      case FILE_TYPE_ICN -> new IconFile (appleFile, dataBuffer);
      case FILE_TYPE_NON -> checkNon (appleFile, dataBuffer, aux);
      case FILE_TYPE_BAT -> new Text (appleFile, dataBuffer);
      case FILE_TYPE_SRC -> new Text (appleFile, dataBuffer);
      case FILE_TYPE_APPLESOFT_VARS -> new StoredVariables (appleFile);
      default -> new UnknownFile (appleFile, dataBuffer, aux);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkText (AppleFile appleFile, Buffer dataBuffer, int aux)
  // ---------------------------------------------------------------------------------//
  {
    String fileName = appleFile.getFileName ();

    if (fileName.endsWith (".S") && countConsecutiveSpaces (dataBuffer) < 4)
      return new AssemblerText (appleFile, dataBuffer);
    if (fileName.endsWith (".SRC"))
      return new AssemblerText (appleFile, dataBuffer);

    return new Text (appleFile, dataBuffer);
  }

  // ---------------------------------------------------------------------------------//
  private int countConsecutiveSpaces (Buffer dataBuffer)
  // ---------------------------------------------------------------------------------//
  {
    int maxCount = 0;
    int count = 0;

    int ptr = dataBuffer.offset ();
    byte[] buffer = dataBuffer.data ();
    int max = dataBuffer.max ();

    while (ptr < max)
    {
      int value = buffer[ptr++] & 0x7F;
      //      System.out.printf ("%3d  %02X  %2d  %2d%n", ptr, value, count, maxCount);

      if (value == RETURN)
      {
        count = 0;
        continue;
      }

      // ignore any line that contains *;"'
      if (value == ASTERISK || value == SINGLE_QUOTE || value == DOUBLE_QUOTE
          || value == SEMI_COLON)
      {
        while (++ptr < max && (buffer[ptr] & 0x7F) != RETURN)
          ;
        continue;
      }

      if (value == SPACE)
      {
        count++;
        if (count > maxCount)
          maxCount = count;
      }
      else
        count = 0;
    }

    return maxCount;
  }

  // ---------------------------------------------------------------------------------//
  private boolean checkExclusions (int aux, String fileName)
  // ---------------------------------------------------------------------------------//
  {
    if (aux == 3 && (fileName.endsWith (".ASM") || fileName.endsWith (".S")))
      return false;
    if (aux == 8192 && fileName.endsWith (".S"))
      return false;
    if (aux > 1000)
      return false;

    return true;
  }

  // https://nicole.express/2024/phasing-in-and-out-of-existence.html
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
  private FormattedAppleFile checkGraphics (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    switch (appleFile.getFileType ())
    {
      case FILE_TYPE_PNT:
        switch (appleFile.getAuxType ())
        {
          case 0x0000:
          case 0x8000:
            return new Pnt0000 (appleFile);

          case 0x0001:
            return new Pic0000 (appleFile, unpackBuffer (appleFile));

          case 0x0002:
            return new Pnt0002 (appleFile);

          case 0x0003:
            System.out.printf ("*** Found PNT aux 0003 : %s%n", appleFile.getFileName ());
            return new Pic0001 (appleFile, unpackBuffer (appleFile));

          case 0x0004:
            System.out.printf ("*** Found PNT aux 0004 : %s%n", appleFile.getFileName ());
            return new AppleGraphics3201 (appleFile);

          case 0x1000:
            return new Pic0000 (appleFile);

          case 0x8005:
            System.out.printf ("*** Found PNT aux 8005 : %s%n", appleFile.getFileName ());
            return new Pnt8005 (appleFile);
        }
        break;

      case FILE_TYPE_PIC:
        switch (appleFile.getAuxType ())
        {
          case 0x0000:
          case 0x4100:
            return new Pic0000 (appleFile);

          case 0x0001:
            System.out.printf ("*** Found PIC aux 0001 : %s%n", appleFile.getFileName ());
            return new Pic0001 (appleFile);

          case 0x0002:
            return new Pic0002 (appleFile);
        }
        break;

      case FILE_TYPE_FOT:
        switch (appleFile.getAuxType ())
        {
          case 0x4000:
            System.out.printf ("*** Found FOT aux 4000 : %s%n", appleFile.getFileName ());
            break;

          case 0x4001:
            System.out.printf ("*** Found FOT aux 4001 : %s%n", appleFile.getFileName ());
            break;

          case 0x8066:
            return new FaddenHiResImage (appleFile);

          default:
            System.out.printf ("*** Found FOT : %s%n", appleFile.getFileName ());
            break;
        }
        break;

      case FILE_TYPE_ANI:
        return new Animation (appleFile);
    }

    return new DataFileProdos (appleFile);
  }

  // Another notable exception is the Fotofile (FOT) format inherited by ProDOS
  // from Apple SOS, which included metadata in the 121st byte (the first byte of
  // the first hole) indicating how it should be displayed (color mode, resolution),
  // or converted to other graphics formats. See File Type Note 8.
  // Mode                         Page 1    Page 2
  // 280 x 192 Black & White        0         4
  // 280 x 192 Limited Color        1         5
  // 560 x 192 Black & White        2         6
  // 140 x 192 Full Color           3         7

  // ---------------------------------------------------------------------------------//
  private Buffer unpackBuffer (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    byte[] unpackedBuffer = Utility.unpackBytes (appleFile.getFileBuffer ().data ());
    return new Buffer (unpackedBuffer, 0, unpackedBuffer.length);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkProdosBinary (AppleFile appleFile, Buffer dataBuffer,
      int aux)
  // ---------------------------------------------------------------------------------//
  {
    Buffer fileBuffer = appleFile.getFileBuffer ();
    byte[] buffer = fileBuffer.data ();
    int eof = fileBuffer.length ();

    if (ShapeTable.isShapeTable (fileBuffer))
      return new ShapeTable (appleFile, new Buffer (buffer, 0, eof));

    if (isAPP (buffer))
      return new AppleGraphics3201 (appleFile);

    String name = appleFile.getFileName ();

    if (AppleImage.isGif (buffer) || AppleImage.isPng (buffer))
      return new AppleImage (appleFile);

    if (aux == 0x2000 || aux == 0x4000)
    {
      if (name.endsWith (".A2FC") || name.endsWith (".PAC"))
        return new AppleGraphicsA2FC (appleFile);

      if (eof > 0x1F00 && eof <= 0x4000)
        return new AppleGraphics (appleFile, new Buffer (buffer, 0, eof));
    }

    if (name.endsWith (".3200") && eof != 38400 && isAPP (buffer))
    {
      name = name.replace (".3200", ".3201");
      System.out.printf ("Assuming %s should be %s%n", appleFile.getFileName (), name);
    }

    if (name.endsWith (".3200") && (aux == 0 || aux == 0x1300))
      return new Pic0002 (appleFile);

    if (name.endsWith (".3201") && aux == 0)
      return new AppleGraphics3201 (appleFile);

    if (eof == 32768)
      return new Pic0000 (appleFile);

    try
    {
      return new AssemblerProgram (appleFile, new Buffer (buffer, 0, eof), aux);
    }
    catch (Exception e)
    {
      System.out.println ("bad " + appleFile.getFileName ());
      return new DataFile (appleFile, fileBuffer);
    }
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkNon (AppleFile appleFile, Buffer dataRecord, int aux)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = dataRecord.data ();

    String name = appleFile.getFileName ();
    if (name.endsWith (".TIFF") && AppleImage.isTiff (buffer))
      return new DataFile (appleFile);    // JavaFX doesn't support TIFF

    return new DataFile (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedPascalFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case 3 -> new PascalText (appleFile);
      case 2 -> new PascalCode (appleFile);
      case 98 -> new PascalSegment (appleFile);         // not a real file type
      case 99 -> new PascalProcedure (appleFile);       // not a real file type
      default -> new DataFile (appleFile);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (FileCpm appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileTypeText ())
    {
      case "DOC" -> new CpmText (appleFile);
      case "HLP" -> new CpmText (appleFile);
      case "TXT" -> new CpmText (appleFile);
      case "ASM" -> new CpmText (appleFile);
      case "BAS" -> new BasicCpmProgram (appleFile);
      default -> new DataFile (appleFile);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedBin2File (FileBinary2 appleFile)
  // ---------------------------------------------------------------------------------//
  {
    Buffer dataBuffer = appleFile.getFileBuffer ();
    int fileType = appleFile.getFileType ();
    int auxType = appleFile.getAuxType ();

    switch (appleFile.getOsType ())
    {
      case 0:                                           // Prodos
        return switch (fileType)
        {
          case FILE_TYPE_TEXT -> checkText (appleFile, dataBuffer, auxType);
          case FILE_TYPE_BINARY -> checkProdosBinary (appleFile, dataBuffer, auxType);
          case FILE_TYPE_APPLESOFT -> new ApplesoftBasicProgram (appleFile);
          case FILE_TYPE_INTEGER_BASIC -> new IntegerBasicProgram (appleFile);
          default -> new DataFile (appleFile);
        };

      case 1:                                           // Dos 3.3
      case 2:                                           // Dos 3.2 or 3.1
        System.out.printf ("Bin2 file system: %d not written%n", appleFile.getOsType ());
        return new DataFile (appleFile);

      case 3:                                           // Pascal
        System.out.printf ("Bin2 file system: %d not written%n", appleFile.getOsType ());
        return new DataFile (appleFile);
    }

    System.out.printf ("Bin2 unknown file system: %d%n", appleFile.getOsType ());
    return new DataFile (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedNufxFile (AppleFile appleFile)
      throws FontValidationException
  // ---------------------------------------------------------------------------------//
  {
    int fileSystemId = 0;
    byte[] buffer = null;
    int aux = 0;
    Buffer dataBuffer;

    if (appleFile instanceof ForkNuFX fork)
    {
      fileSystemId = fork.getFileSystemId ();
      dataBuffer = fork.getFileBuffer ();
      buffer = dataBuffer.data ();
      aux = fork.getAuxType ();
    }
    else
    {
      FileNuFX file = (FileNuFX) appleFile;
      fileSystemId = file.getFileSystemId ();
      dataBuffer = file.getFileBuffer ();
      buffer = dataBuffer.data ();
      aux = file.getAuxType ();
    }

    int fileType = appleFile.getFileType ();

    if (buffer == null)
      return new DataFile (appleFile, new Buffer (buffer, 0, 0));

    switch (fileSystemId)
    {
      case 1:                                     // Prodos/Sos
        return switch (fileType)
        {
          case FILE_TYPE_TEXT -> checkText (appleFile, dataBuffer, aux);
          case FILE_TYPE_BINARY -> checkProdosBinary (appleFile, dataBuffer, aux);
          case FILE_TYPE_APPLESOFT -> new ApplesoftBasicProgram (appleFile);
          case FILE_TYPE_INTEGER_BASIC -> new IntegerBasicProgram (appleFile);
          case FILE_TYPE_SYS -> new AssemblerProgram (appleFile, dataBuffer, aux);
          case FILE_TYPE_GWP -> new Text (appleFile, dataBuffer);
          case FILE_TYPE_FONT -> new QuickDrawFont (appleFile);
          default -> new UnknownFile (appleFile, dataBuffer, aux);
        };

      case 2:                                     // Dos 3.3
      case 3:                                     // Dos 3.2
      case 4:                                     // Pascal
      case 8:                                     // CPM
        System.out.printf ("NuFX file system: %d not written%n", fileSystemId);
        return new UnknownFile (appleFile, new Buffer (buffer, 0, buffer.length), aux);

      default:
        System.out.printf ("NuFX unknown file system: %d%n", fileSystemId);
        return new UnknownFile (appleFile, new Buffer (buffer, 0, buffer.length), aux);
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
  public ApplePreferences getPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return preferencesFactory.getPreferences (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    preferencesFactory.save ();
  }
}

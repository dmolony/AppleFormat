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
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FND;
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

import java.util.List;

import com.bytezone.appleformat.appleworks.AppleworksADBFile;
import com.bytezone.appleformat.appleworks.AppleworksSSFile;
import com.bytezone.appleformat.appleworks.AppleworksWPFile;
import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.BasicProgramGS;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.file.DataFile;
import com.bytezone.appleformat.file.DataForkProdos;
import com.bytezone.appleformat.file.FinderData;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.file.ResourceForkProdos;
import com.bytezone.appleformat.file.StoredVariables;
import com.bytezone.appleformat.file.TdfFile;
import com.bytezone.appleformat.file.UnknownFile;
import com.bytezone.appleformat.fonts.FontFile;
import com.bytezone.appleformat.fonts.FontValidationException;
import com.bytezone.appleformat.fonts.QuickDrawFont;
import com.bytezone.appleformat.graphics.Animation;
import com.bytezone.appleformat.graphics.AppleGraphics;
import com.bytezone.appleformat.graphics.AppleGraphics3201;
import com.bytezone.appleformat.graphics.AppleGraphicsA2FC;
import com.bytezone.appleformat.graphics.AppleImage;
import com.bytezone.appleformat.graphics.IconFile;
import com.bytezone.appleformat.graphics.Pic0000;
import com.bytezone.appleformat.graphics.Pic0001;
import com.bytezone.appleformat.graphics.Pic0002;
import com.bytezone.appleformat.graphics.Pnt0000;
import com.bytezone.appleformat.graphics.Pnt0002;
import com.bytezone.appleformat.graphics.Pnt8005;
import com.bytezone.appleformat.text.ProdosText;
import com.bytezone.appleformat.text.Text;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFile.ForkType;
import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.ForkProdos;
import com.bytezone.filesystem.TextBlock;

// ---------------------------------------------------------------------------------//
class FactoryProdos extends FactoryProdosCommon
// ---------------------------------------------------------------------------------//
{
  // http://www.1000bit.it/support/manuali/apple/technotes/ftyp/ft.about.html
  // ---------------------------------------------------------------------------------//
  FormattedAppleFile getFormattedProdosFile (AppleFile appleFile)
      throws FontValidationException
  // ---------------------------------------------------------------------------------//
  {
    if (appleFile instanceof FileProdos fp)
      appleFile = fp.getForks ().get (0);             // data fork

    assert appleFile instanceof ForkProdos;

    // avoid the DataBuffer if using TextBlocks
    if (appleFile.isRandomAccess ())
    {
      List<? extends TextBlock> textBlocks = ((ForkProdos) appleFile).getTextBlocks ();
      return new ProdosText ((ForkProdos) appleFile, textBlocks);
    }

    Buffer dataBuffer = appleFile.getFileBuffer ();
    int aux = appleFile.getAuxType ();

    if (((ForkProdos) appleFile).getForkType () == ForkType.RESOURCE)
      return new ResourceForkProdos (appleFile);

    return switch (appleFile.getFileType ())
    {
      case FILE_TYPE_TEXT -> checkText (appleFile);
      case FILE_TYPE_GWP -> new Text (appleFile);
      case FILE_TYPE_SYS -> new AssemblerProgram (appleFile, dataBuffer, aux);
      case FILE_TYPE_CMD -> new AssemblerProgram (appleFile, dataBuffer, aux);
      case FILE_TYPE_BINARY -> checkProdosBinary (appleFile);
      case FILE_TYPE_PNT -> checkGraphics (appleFile);
      case FILE_TYPE_PIC -> checkGraphics (appleFile);
      case FILE_TYPE_ANI -> checkGraphics (appleFile);
      case FILE_TYPE_FND -> new FinderData (appleFile);
      case FILE_TYPE_FOT -> checkGraphics (appleFile);
      case FILE_TYPE_FNT -> new FontFile (appleFile);
      case FILE_TYPE_FONT -> new QuickDrawFont (appleFile);
      case FILE_TYPE_GS_BASIC -> new BasicProgramGS (appleFile);
      case FILE_TYPE_TDF -> new TdfFile (appleFile);
      case FILE_TYPE_APPLESOFT -> new ApplesoftBasicProgram (appleFile);
      case FILE_TYPE_INTEGER_BASIC -> new IntegerBasicProgram (appleFile);
      case FILE_TYPE_ASP -> new AppleworksSSFile (appleFile);
      case FILE_TYPE_AWP -> new AppleworksWPFile (appleFile);
      case FILE_TYPE_ADB -> new AppleworksADBFile (appleFile);
      case FILE_TYPE_ICN -> new IconFile (appleFile);
      case FILE_TYPE_NON -> checkNon (appleFile);
      case FILE_TYPE_BAT -> new Text (appleFile);
      case FILE_TYPE_SRC -> new Text (appleFile);
      case FILE_TYPE_APPLESOFT_VARS -> new StoredVariables (appleFile);
      default -> new UnknownFile (appleFile);
    };
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
            return new AppleGraphics (appleFile, getFaddenBuffer (appleFile));

          default:
            System.out.printf ("*** Found FOT : %s%n", appleFile.getFileName ());
            return new AppleGraphics (appleFile);       // see below
        }
        break;

      case FILE_TYPE_ANI:
        return new Animation (appleFile);
    }

    return new DataForkProdos (appleFile);
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
  private FormattedAppleFile checkNon (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    Buffer fileBuffer = appleFile.getFileBuffer ();
    byte[] buffer = fileBuffer.data ();

    String fileName = appleFile.getFileName ();
    if (fileName.endsWith (".TIFF") && AppleImage.isTiff (buffer))
      return new DataFile (appleFile);    // JavaFX doesn't support TIFF

    // check for pitchdark errors
    int eof = fileBuffer.length ();
    if (eof == 0x4000 && fileName.equals ("ZORK.ZERO"))
      return new AppleGraphicsA2FC (appleFile);
    else if (eof == 0x8000 && fileName.equals ("ZORK.ZERO"))
      return new Pic0000 (appleFile);

    return new DataFile (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  private Buffer getFaddenBuffer (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    Buffer dataBuffer = appleFile.getFileBuffer ();
    byte[] buffer = dataBuffer.data ();
    byte[] outBuffer = new byte[0x2000];

    int ptr = dataBuffer.offset ();
    int outPtr = 0;

    assert buffer[ptr++] == 0x66;

    while (ptr < buffer.length)
    {
      int literalLen = (buffer[ptr] & 0xF0) >>> 4;
      int matchLen = (buffer[ptr++] & 0x0F) + 4;

      if (literalLen == 15)
        literalLen = (buffer[ptr++] & 0xFF) + 15;

      if (literalLen > 0)
      {
        System.arraycopy (buffer, ptr, outBuffer, outPtr, literalLen);
        ptr += literalLen;
        outPtr += literalLen;
      }

      if (matchLen == 19)           // 15 + 4
      {
        matchLen = (buffer[ptr++] & 0xFF);
        if (matchLen == 254)        // eof
          break;
        if (matchLen == 253)        // no match
          continue;
        matchLen += 19;
      }

      int offset2 = (buffer[ptr++] & 0xFF) | ((buffer[ptr++] & 0xFF) << 8);
      while (matchLen-- > 0)
        outBuffer[outPtr++] = outBuffer[offset2++];
    }

    return new Buffer (outBuffer, 0, outBuffer.length);
  }
}

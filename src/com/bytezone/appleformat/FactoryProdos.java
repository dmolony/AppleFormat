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
import com.bytezone.appleformat.file.FinderData;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.file.ResourceForkProdos;
import com.bytezone.appleformat.file.StoredVariables;
import com.bytezone.appleformat.file.TdfFile;
import com.bytezone.appleformat.file.UnknownFile;
import com.bytezone.appleformat.fonts.FontFile;
import com.bytezone.appleformat.fonts.FontValidationException;
import com.bytezone.appleformat.fonts.QuickDrawFont;
import com.bytezone.appleformat.graphics.AppleGraphicsA2FC;
import com.bytezone.appleformat.graphics.AppleImage;
import com.bytezone.appleformat.graphics.IconFile;
import com.bytezone.appleformat.graphics.Pic0000;
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
      case FILE_TYPE_SYS -> checkSys (appleFile);
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

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile checkSys (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    Buffer dataBuffer = appleFile.getFileBuffer ();
    int aux = appleFile.getAuxType ();

    return new AssemblerProgram (appleFile, dataBuffer, aux);
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
}

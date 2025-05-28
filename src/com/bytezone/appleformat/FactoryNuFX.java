package com.bytezone.appleformat;

import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_APPLESOFT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BINARY;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FONT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_GWP;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_INTEGER_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_SRC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_SYS;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_TEXT;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.file.UnknownFile;
import com.bytezone.appleformat.fonts.FontValidationException;
import com.bytezone.appleformat.fonts.QuickDrawFont;
import com.bytezone.appleformat.text.Text;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.FileNuFX;
import com.bytezone.filesystem.ForkNuFX;

// ---------------------------------------------------------------------------------//
class FactoryNuFX extends FactoryProdosCommon
// ---------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  FormattedAppleFile getFormattedNufxFile (AppleFile appleFile)
      throws FontValidationException
  // ---------------------------------------------------------------------------------//
  {
    if (appleFile instanceof FileNuFX file)       // just one resource
      appleFile = file.getForks ().get (0);

    assert appleFile instanceof ForkNuFX;

    int fileSystemId = ((ForkNuFX) appleFile).getFileSystemId ();
    int fileType = appleFile.getFileType ();

    switch (fileSystemId)
    {
      case 1:                                     // Prodos/Sos
        return switch (fileType)
        {
          case FILE_TYPE_TEXT -> checkText (appleFile);
          case FILE_TYPE_BINARY -> checkProdosBinary (appleFile);
          case FILE_TYPE_APPLESOFT -> new ApplesoftBasicProgram (appleFile);
          case FILE_TYPE_INTEGER_BASIC -> new IntegerBasicProgram (appleFile);
          case FILE_TYPE_SYS -> new AssemblerProgram (appleFile,
              appleFile.getFileBuffer (), appleFile.getAuxType ());
          case FILE_TYPE_GWP -> new Text (appleFile);
          case FILE_TYPE_FONT -> new QuickDrawFont (appleFile);
          case FILE_TYPE_SRC -> new Text (appleFile);
          default -> new UnknownFile (appleFile);
        };

      case 2:                                     // Dos 3.3
      case 3:                                     // Dos 3.2
      case 4:                                     // Pascal
      case 8:                                     // CPM
        System.out.printf ("NuFX file system: %d not written%n", fileSystemId);
        return new UnknownFile (appleFile);

      default:
        System.out.printf ("NuFX unknown file system: %d%n", fileSystemId);
        return new UnknownFile (appleFile);
    }
  }
}

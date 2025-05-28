package com.bytezone.appleformat;

import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_APPLESOFT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BINARY;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_INTEGER_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_TEXT;

import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.file.DataFile;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.FileBinary2;

// ---------------------------------------------------------------------------------//
class FactoryBin2 extends FactoryProdosCommon
// ---------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  FormattedAppleFile getFormattedBin2File (FileBinary2 appleFile)
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
          case FILE_TYPE_TEXT -> checkText (appleFile);
          case FILE_TYPE_BINARY -> checkProdosBinary (appleFile);
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
}

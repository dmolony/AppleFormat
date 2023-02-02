package com.bytezone.appleformat;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.text.Text;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.FileCpm;
import com.bytezone.filesystem.FileDos;
import com.bytezone.filesystem.FilePascal;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.FsCpm;
import com.bytezone.filesystem.FsDos;
import com.bytezone.filesystem.FsPascal;
import com.bytezone.filesystem.FsProdos;

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

    AppleFileSystem fileSystem = appleFile.getFileSystem ();

    if (fileSystem instanceof FsDos)
      return getFormattedDosFile ((FileDos) appleFile);
    else if (fileSystem instanceof FsProdos)
      return getFormattedProdosFile ((FileProdos) appleFile);
    else if (fileSystem instanceof FsPascal)
      return getFormattedPascalFile ((FilePascal) appleFile);
    else if (fileSystem instanceof FsCpm)
      return getFormattedCpmFile ((FileCpm) appleFile);

    return new DataFile (appleFile.getFileName (), appleFile.read ());
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedDosFile (FileDos file)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = file.read ();
    String fileName = file.getFileName ();

    switch (file.getFileType ())
    {
      case 0:
        Text text = new Text (fileName, buffer, 0, buffer.length);

        return text;

      case 1:
        return new IntegerBasicProgram (fileName, buffer, 2, Utility.getShort (buffer, 0));

      case 2:
        return new ApplesoftBasicProgram (fileName, buffer, 2, Utility.getShort (buffer, 0));

      case 4:
      case 16:
        return new AssemblerProgram (fileName, buffer, 4, Utility.getShort (buffer, 2),
            Utility.getShort (buffer, 0));
    }

    return new DataFile (fileName, buffer);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedProdosFile (FileProdos file)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = file.read ();
    String fileName = file.getFileName ();

    switch (file.getFileType ())
    {
      case 0x04:
        return new Text (fileName, buffer, 0, buffer.length);

      case 0xFC:
        return new ApplesoftBasicProgram (fileName, buffer, 0, file.getLength ());
    }

    return new DataFile (fileName, buffer);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedPascalFile (FilePascal file)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = file.read ();
    String fileName = file.getFileName ();

    return new DataFile (fileName, buffer);
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (FileCpm file)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = file.read ();
    String fileName = file.getFileName ();

    return new DataFile (fileName, buffer);
  }
}

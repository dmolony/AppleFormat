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
{

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    AppleFileSystem fileSystem = appleFile.getFileSystem ();

    if (fileSystem instanceof FsDos)
      return getFormattedDosFile ((FileDos) appleFile);
    else if (fileSystem instanceof FsProdos)
      return getFormattedProdosFile ((FileProdos) appleFile);
    else if (fileSystem instanceof FsPascal)
      return getFormattedPascalFile ((FilePascal) appleFile);
    else if (fileSystem instanceof FsCpm)
      return getFormattedCpmFile ((FileCpm) appleFile);

    return null;
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
        int length = Utility.getShort (buffer, 0);

        IntegerBasicProgram integerBasicProgram =
            new IntegerBasicProgram (fileName, buffer, 2, length);

        return integerBasicProgram;

      case 2:
        length = Utility.getShort (buffer, 0);

        ApplesoftBasicProgram applesoftBasicProgram =
            new ApplesoftBasicProgram (fileName, buffer, 2, length);

        return applesoftBasicProgram;

      case 4:
      case 16:

        int address = Utility.getShort (buffer, 0);
        length = Utility.getShort (buffer, 2);

        AssemblerProgram assembler = new AssemblerProgram (fileName, buffer, 4, length, address);

        return assembler;
    }

    return null;
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
        Text text = new Text (fileName, buffer, 0, buffer.length);

        return text;

      case 0xFC:
        int length = file.getLength ();
        ApplesoftBasicProgram basic = new ApplesoftBasicProgram (fileName, buffer, 0, length);

        return basic;
    }

    return null;
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedPascalFile (FilePascal file)
  // ---------------------------------------------------------------------------------//
  {
    return null;
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (FileCpm file)
  // ---------------------------------------------------------------------------------//
  {
    return null;
  }
}

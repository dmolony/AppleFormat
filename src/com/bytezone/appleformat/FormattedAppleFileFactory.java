package com.bytezone.appleformat;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.text.Text;
import com.bytezone.filesystem.AppleFile;

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

    byte[] buffer = appleFile.read ();
    String fileName = appleFile.getFileName ();
    int type = appleFile.getFileType ();

    return switch (appleFile.getFileSystem ().getFileSystemType ())
    {
      case DOS -> getFormattedDosFile (fileName, type, buffer);
      case PRODOS -> getFormattedProdosFile (fileName, type, buffer, appleFile.getLength ());
      case PASCAL -> getFormattedPascalFile (fileName, type, buffer);
      case CPM -> getFormattedCpmFile (fileName, type, buffer);
      default -> new DataFile (fileName, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedDosFile (String fileName, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      case 0 -> new Text (fileName, buffer, 0, buffer.length);
      case 1 -> new IntegerBasicProgram (fileName, buffer, 2, Utility.getShort (buffer, 0));
      case 2 -> new ApplesoftBasicProgram (fileName, buffer, 2, Utility.getShort (buffer, 0));
      case 4, 16 -> new AssemblerProgram (fileName, buffer, 4, Utility.getShort (buffer, 2),
          Utility.getShort (buffer, 0));
      default -> new DataFile (fileName, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedProdosFile (String fileName, int fileType, byte[] buffer,
      int length)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      case 0x04 -> new Text (fileName, buffer, 0, buffer.length);
      case 0xFC -> new ApplesoftBasicProgram (fileName, buffer, 0, length);
      default -> new DataFile (fileName, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedPascalFile (String fileName, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      default -> new DataFile (fileName, buffer);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (String fileName, int fileType, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return switch (fileType)
    {
      default -> new DataFile (fileName, buffer);
    };
  }
}

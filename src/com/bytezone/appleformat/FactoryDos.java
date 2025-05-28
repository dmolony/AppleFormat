package com.bytezone.appleformat;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.appleformat.basic.IntegerBasicProgram;
import com.bytezone.appleformat.file.DataFile;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.fonts.DosCharacterSet;
import com.bytezone.appleformat.graphics.AppleGraphics;
import com.bytezone.appleformat.graphics.ShapeTable;
import com.bytezone.appleformat.text.AssemblerText;
import com.bytezone.appleformat.text.DosText;
import com.bytezone.appleformat.text.DosText2;
import com.bytezone.appleformat.visicalc.VisicalcFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.FileDos;
import com.bytezone.filesystem.FsDos;

// ---------------------------------------------------------------------------------//
class FactoryDos
// ---------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  FormattedAppleFile getFormattedDosFile (FileDos appleFile)
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

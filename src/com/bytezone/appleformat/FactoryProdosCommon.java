package com.bytezone.appleformat;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.file.DataFile;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.graphics.AppleGraphics;
import com.bytezone.appleformat.graphics.AppleGraphics3201;
import com.bytezone.appleformat.graphics.AppleGraphicsA2FC;
import com.bytezone.appleformat.graphics.AppleImage;
import com.bytezone.appleformat.graphics.Pic0000;
import com.bytezone.appleformat.graphics.Pic0002;
import com.bytezone.appleformat.graphics.ShapeTable;
import com.bytezone.appleformat.text.AssemblerText;
import com.bytezone.appleformat.text.Text;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

// ---------------------------------------------------------------------------------//
class FactoryProdosCommon
// ---------------------------------------------------------------------------------//
{
  static final int RETURN = 0x0D;
  static final int SEMI_COLON = 0x3B;
  static final int ASTERISK = 0x2A;
  static final int SPACE = 0x20;
  static final int DOUBLE_QUOTE = 0x22;
  static final int SINGLE_QUOTE = 0x27;

  // ---------------------------------------------------------------------------------//
  FormattedAppleFile checkProdosBinary (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    Buffer fileBuffer = appleFile.getFileBuffer ();       // exactBuffer
    byte[] buffer = fileBuffer.data ();
    int eof = fileBuffer.length ();

    if (ShapeTable.isShapeTable (fileBuffer))
      return new ShapeTable (appleFile, new Buffer (buffer, 0, eof));

    if (isAPP (buffer))
      return new AppleGraphics3201 (appleFile);

    if (AppleImage.isGif (buffer) || AppleImage.isPng (buffer))
      return new AppleImage (appleFile);

    int aux = appleFile.getAuxType ();
    String name = appleFile.getFileName ();

    if (aux == 0x2000 || aux == 0x4000)
    {
      if (name.endsWith (".A2FC") || name.endsWith (".PAC"))
        return new AppleGraphicsA2FC (appleFile);

      if (eof == 0x4000 && aux == 0x2000)
        return new AppleGraphicsA2FC (appleFile);

      if (eof > 0x1F00 && eof <= 0x4000)
        return new AppleGraphics (appleFile, new Buffer (buffer, 0, eof));
    }

    if (name.endsWith (".3200") && eof != 38400)
    {
      name = name.replace (".3200", ".3201");
      System.out.printf ("Assuming %s should be %s%n", appleFile.getFileName (), name);
    }

    if (name.endsWith (".3200") && (aux == 0 || aux == 0x1300))
      return new Pic0002 (appleFile);

    if (name.endsWith (".3201") && aux == 0)
      return new AppleGraphics3201 (appleFile);

    if (eof == 0x8000)
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
  FormattedAppleFile checkText (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    String fileName = appleFile.getFileName ();
    Buffer dataBuffer = appleFile.getFileBuffer ();

    if (fileName.endsWith (".S") && countConsecutiveSpaces (dataBuffer) < 4)
      return new AssemblerText (appleFile, dataBuffer);
    if (fileName.endsWith (".SRC"))
      return new AssemblerText (appleFile, dataBuffer);

    return new Text (appleFile);
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
  private boolean isAPP (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length < 4)
      return false;

    return buffer[0] == (byte) 0xC1 && buffer[1] == (byte) 0xD0
        && buffer[2] == (byte) 0xD0 && buffer[3] == 0;
  }
}

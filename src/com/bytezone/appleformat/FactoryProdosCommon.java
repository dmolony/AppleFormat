package com.bytezone.appleformat;

import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ANI;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FOT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PNT;

import com.bytezone.appleformat.assembler.AssemblerProgram;
import com.bytezone.appleformat.file.DataFile;
import com.bytezone.appleformat.file.DataForkProdos;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.graphics.Animation;
import com.bytezone.appleformat.graphics.AppleGraphics;
import com.bytezone.appleformat.graphics.AppleGraphics3201;
import com.bytezone.appleformat.graphics.AppleGraphicsA2FC;
import com.bytezone.appleformat.graphics.AppleImage;
import com.bytezone.appleformat.graphics.Pic0000;
import com.bytezone.appleformat.graphics.Pic0001;
import com.bytezone.appleformat.graphics.Pic0002;
import com.bytezone.appleformat.graphics.Pnt0000;
import com.bytezone.appleformat.graphics.Pnt0002;
import com.bytezone.appleformat.graphics.Pnt8005;
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

  // https://ciderpress2.com/formatdoc/PrintShop-notes.html

  // ---------------------------------------------------------------------------------//
  FormattedAppleFile checkGraphics (AppleFile appleFile)
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

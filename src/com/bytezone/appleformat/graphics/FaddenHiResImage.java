package com.bytezone.appleformat.graphics;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

// https://github.com/fadden/fhpack/blob/master/fhpack.cpp
// -----------------------------------------------------------------------------------//
public class FaddenHiResImage extends AppleGraphics
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public FaddenHiResImage (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

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

      this.dataBuffer = new Buffer (outBuffer, 0, outBuffer.length);
    }
  }
}

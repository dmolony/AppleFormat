package com.bytezone.appleformat.graphics;

// DreamGraphix LZW
// code ported from CiderPress' DreamGraphix::UnpackDG()
// -----------------------------------------------------------------------------------//
public class LZW3
// -----------------------------------------------------------------------------------//
{
  private static final int[] bitMasks =
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x1FF, 0x3FF, 0x7FF, 0xFFF };
  private static final int CLEAR_CODE = 256;
  private static final int EOF_CODE = 257;
  private static final int FIRST_FREE_CODE = 258;

  private int nBitMod1;
  private int nBitMask;
  private int finChar;
  private int oldCode;
  private int inCode;
  private int freeCode;
  private int maxCode;
  private int k;

  private int bitOffset;

  private int[] hashNext = new int[4096];
  private int[] hashChar = new int[4096];

  private int ptr = 0;
  private int iCode;
  private int[] stack = new int[32768];
  private int stackIdx = 0;

  private byte[] srcBuf;
  private byte[] dstBuf;

  // ---------------------------------------------------------------------------------//
  public int unpack (byte[] src, byte[] dst, int max)
  // ---------------------------------------------------------------------------------//
  {
    assert max <= dst.length;

    srcBuf = src;
    dstBuf = dst;

    initTable ();

    int a;
    int y;

    bitOffset = 0;

    while (true)
    {
      if (ptr > max)
      {
        System.out.println ("LZW3 overrun");
        return -1;
      }

      iCode = readCode ();

      if (iCode == EOF_CODE)
        break;

      if (iCode == CLEAR_CODE)
      {
        initTable ();
        iCode = readCode ();

        oldCode = iCode;
        k = iCode;
        finChar = iCode;
        dstBuf[ptr++] = (byte) iCode;
        continue;
      }

      a = inCode = iCode;

      if (iCode >= freeCode)
      {
        stack[stackIdx++] = finChar;
        a = oldCode;
      }

      while (a >= 256)
      {
        y = a;
        a = hashChar[y];
        stack[stackIdx++] = a;
        a = hashNext[y];
      }

      finChar = a;
      k = a;
      y = 0;

      dstBuf[ptr + y++] = (byte) a;

      while (stackIdx > 0)
      {
        a = stack[--stackIdx];
        dstBuf[ptr + y++] = (byte) a;
      }

      ptr += y;

      addCode ();

      oldCode = inCode;

      if (freeCode < maxCode)
        continue;

      if (nBitMod1 == 12)
        continue;

      nBitMod1++;
      nBitMask = bitMasks[nBitMod1];
      maxCode <<= 1;
    }

    return ptr;
  }

  // ---------------------------------------------------------------------------------//
  private void initTable ()
  // ---------------------------------------------------------------------------------//
  {
    nBitMod1 = 9;
    nBitMask = bitMasks[nBitMod1];
    maxCode = 1 << nBitMod1;
    freeCode = FIRST_FREE_CODE;
  }

  // ---------------------------------------------------------------------------------//
  private int readCode ()
  // ---------------------------------------------------------------------------------//
  {
    int bitIdx = bitOffset & 0x07;
    int byteIdx = bitOffset >>> 3;          // no sign extension

    int iCode = srcBuf[byteIdx] & 0xFF | (srcBuf[byteIdx + 1] & 0xFF) << 8
        | (srcBuf[byteIdx + 2] & 0xFF) << 16;

    iCode >>>= bitIdx;
    iCode &= nBitMask;

    bitOffset += nBitMod1;

    return iCode;
  }

  // ---------------------------------------------------------------------------------//
  private void addCode ()
  // ---------------------------------------------------------------------------------//
  {
    hashChar[freeCode] = k;
    hashNext[freeCode] = oldCode;
    freeCode++;
  }
}

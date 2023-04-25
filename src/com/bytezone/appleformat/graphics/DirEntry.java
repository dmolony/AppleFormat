package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.Utility;

// ---------------------------------------------------------------------------------//
class DirEntry
// ---------------------------------------------------------------------------------//
{
  int numBytes;
  int mode;

  // -------------------------------------------------------------------------------//
  public DirEntry (byte[] data, int offset)
  // -------------------------------------------------------------------------------//
  {
    numBytes = Utility.getShort (data, offset);
    mode = Utility.getShort (data, offset + 2);
  }

  // -------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // -------------------------------------------------------------------------------//
  {
    return String.format ("Bytes: %5d, mode: %02X", numBytes, mode);
  }
}
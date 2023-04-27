package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.Utility;

// ---------------------------------------------------------------------------------//
class DirEntry
// ---------------------------------------------------------------------------------//
{
  int numBytes;                 // this seems to be ignored
  int mode;

  boolean mode320;
  boolean mode640;
  boolean fill;
  int colorTable;

  // -------------------------------------------------------------------------------//
  public DirEntry (byte[] data, int offset)
  // -------------------------------------------------------------------------------//
  {
    numBytes = Utility.getShort (data, offset);
    mode = Utility.getShort (data, offset + 2);

    mode320 = (mode & 0x80) == 0;
    mode640 = (mode & 0x80) != 0;
    fill = (mode & 0x20) != 0;
    colorTable = mode & 0x0F;
  }

  // -------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // -------------------------------------------------------------------------------//
  {
    return String.format ("Bytes: %5d, mode: %04X, line: %s", numBytes, mode,
        mode320 ? "mode320" : "mode640");
  }
}
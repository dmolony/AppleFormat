package com.bytezone.appleworks;

import com.bytezone.appleformat.Utility;

// -----------------------------------------------------------------------------------//
class CellAddress
// -----------------------------------------------------------------------------------//
{
  int colRef;
  int rowRef;

  // ---------------------------------------------------------------------------------//
  CellAddress (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    colRef = buffer[offset];
    rowRef = Utility.getShort (buffer, offset + 1);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("[Row=%04d, Col=%04d]", rowRef, colRef);
  }
}
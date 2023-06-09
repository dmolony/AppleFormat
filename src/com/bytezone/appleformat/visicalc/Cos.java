package com.bytezone.appleformat.visicalc;

// ---------------------------------------------------------------------------------//
class Cos extends ValueFunction
// ---------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  Cos (Cell cell, String text)
  // ---------------------------------------------------------------------------------//
  {
    super (cell, text);
    assert text.startsWith ("@COS(") : text;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public double calculateValue ()
  // ---------------------------------------------------------------------------------//
  {
    return Math.cos (source.getDouble ());
  }
}
package com.bytezone.appleformat.visicalc;

// -----------------------------------------------------------------------------------//
class False extends ConstantFunction
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  False (Cell cell, String text)
  // ---------------------------------------------------------------------------------//
  {
    super (cell, text);

    assert text.equals ("@FALSE") : text;

    bool = false;
    valueType = ValueType.BOOLEAN;
  }
}
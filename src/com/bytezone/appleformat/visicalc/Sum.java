package com.bytezone.appleformat.visicalc;

// -----------------------------------------------------------------------------------//
class Sum extends ValueListFunction
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  Sum (Cell cell, String text)
  // ---------------------------------------------------------------------------------//
  {
    super (cell, text);

    assert text.startsWith ("@SUM(") : text;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void calculate ()
  // ---------------------------------------------------------------------------------//
  {
    value = 0;
    valueResult = ValueResult.VALID;

    for (Value v : list)
    {
      v.calculate ();

      if (v.getValueResult () == ValueResult.NA)
        continue;

      if (!v.isValid ())
      {
        valueResult = v.getValueResult ();
        return;
      }

      value += v.getDouble ();
    }
  }
}
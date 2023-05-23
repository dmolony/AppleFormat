package com.bytezone.appleformat.visicalc;

// -----------------------------------------------------------------------------------//
class Number extends AbstractValue
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  Number (Cell cell, String text)
  // ---------------------------------------------------------------------------------//
  {
    super (cell, text);

    try
    {
      valueType = ValueType.NUMBER;
      valueResult = ValueResult.VALID;
      value = Double.parseDouble (text);
    }
    catch (NumberFormatException e)
    {
      valueResult = ValueResult.ERROR;
      e.printStackTrace ();
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getType ()
  // ---------------------------------------------------------------------------------//
  {
    return "Constant";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append (String.format ("%s%n", LINE));
    attach (text, getType (), getFullText (), this);
    return text.toString ();
  }
}
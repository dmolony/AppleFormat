package com.bytezone.appleformat.visicalc;

// -----------------------------------------------------------------------------------//
abstract class Function extends AbstractValue
// -----------------------------------------------------------------------------------//
{
  static final String[] functionList =
      { "@ABS(", "@ACOS(", "@AND(", "@ASIN(", "@ATAN(", "@AVERAGE(", "@COUNT(",
        "@CHOOSE(", "@COS(", "@ERROR", "@EXP(", "@FALSE", "@IF(", "@INT(", "@ISERROR(",
        "@ISNA(", "@LOG10(", "@LOOKUP(", "@LN(", "@MIN(", "@MAX(", "@NA", "@NOT(",
        "@NPV(", "@OR(", "@PI", "@SIN(", "@SUM(", "@SQRT(", "@TAN(", "@TRUE" };

  protected final String functionName;
  protected final String functionText;

  // ---------------------------------------------------------------------------------//
  Function (Cell cell, String text)
  // ---------------------------------------------------------------------------------//
  {
    super (cell, text);

    // get function's parameter string
    int pos = text.indexOf ('(');
    if (pos >= 0)
    {
      functionName = text.substring (0, pos);
      functionText = text.substring (pos + 1, text.length () - 1);
    }
    else
    {
      functionName = text;
      functionText = "";
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append (String.format ("%s%n", LINE));
    text.append (
        String.format (FMT4, "Function", getFullText (), valueType, getValueText (this)));
    for (Value value : values)
    {
      text.append (String.format (FMT4, value.getType (), value.getFullText (),
          value.getValueType (), getValueText (value)));
    }
    return text.toString ();
  }
}
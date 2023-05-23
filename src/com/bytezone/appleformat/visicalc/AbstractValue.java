package com.bytezone.appleformat.visicalc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// -----------------------------------------------------------------------------------//
abstract class AbstractValue implements Value
// -----------------------------------------------------------------------------------//
{
  protected static final String FMT2 = "| %-9.9s : %-70.70s|%n";
  protected static final String FMT4 = "| %-9.9s : %-50.50s %-8.8s %-10.10s|%n";
  protected static final String FMT5 = "| %-9.9s : %-39.39s %-10.10s %-8.8s %-10.10s|%n";
  protected static final String LINE = "+--------------------------------------------"
      + "---------------------------------------+";

  protected final Cell cell;
  protected final String fullText;

  protected ValueType valueType;    // = ValueType.NUMBER;         // could be BOOLEAN
  protected double value;
  protected boolean bool;

  protected ValueResult valueResult = ValueResult.VALID;
  protected List<Value> values = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  AbstractValue (Cell cell, String text)
  // ---------------------------------------------------------------------------------//
  {
    this.cell = cell;
    this.fullText = text;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getFullText ()
  // ---------------------------------------------------------------------------------//
  {
    return fullText;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public ValueType getValueType ()
  // ---------------------------------------------------------------------------------//
  {
    return valueType;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public ValueResult getValueResult ()
  // ---------------------------------------------------------------------------------//
  {
    return valueResult;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean isValid ()
  // ---------------------------------------------------------------------------------//
  {
    return valueResult == ValueResult.VALID;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public double getDouble ()
  // ---------------------------------------------------------------------------------//
  {
    assert valueType == ValueType.NUMBER;
    return value;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean getBoolean ()
  // ---------------------------------------------------------------------------------//
  {
    assert valueType == ValueType.BOOLEAN;
    return bool;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int size ()
  // ---------------------------------------------------------------------------------//
  {
    return values.size ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return valueResult == ValueResult.VALID ? getValueText (this) : valueResult + "";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void calculate ()
  // ---------------------------------------------------------------------------------//
  {
    //    System.out.println ("calculate not overridden: " + cell);
  }

  // ---------------------------------------------------------------------------------//
  protected void attach (StringBuilder text, String title, String textValue, Value value)
  // ---------------------------------------------------------------------------------//
  {
    text.append (String.format (FMT4, title, textValue, value.getValueType (),
        getValueText (value)));
    for (Value v : value)
      text.append (v);
  }

  // ---------------------------------------------------------------------------------//
  protected String getValueText (Value value)
  // ---------------------------------------------------------------------------------//
  {
    if (value.getValueType () == ValueType.NUMBER)
      return value.getDouble () + "";
    if (value.getValueType () == ValueType.BOOLEAN)
      return value.getBoolean () ? "TRUE" : "FALSE";
    return "??*??";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Iterator<Value> iterator ()
  // ---------------------------------------------------------------------------------//
  {
    return values.iterator ();
  }
}
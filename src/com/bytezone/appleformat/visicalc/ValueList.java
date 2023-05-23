package com.bytezone.appleformat.visicalc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// -----------------------------------------------------------------------------------//
class ValueList implements Iterable<Value>
// -----------------------------------------------------------------------------------//
{
  private final List<Value> values = new ArrayList<> ();
  private boolean hasRange;

  // ---------------------------------------------------------------------------------//
  ValueList (Cell cell, String text)
  // ---------------------------------------------------------------------------------//
  {
    String remainder = text;

    while (true)
    {
      String parameter = Expression.getParameter (remainder);

      if (Range.isRange (parameter))
      {
        hasRange = true;
        for (Address address : new Range (cell, parameter))
          values.add (cell.getCell (address));
      }
      else
        values.add (new Expression (cell, parameter).reduce ());

      if (remainder.length () == parameter.length ())
        break;

      remainder = remainder.substring (parameter.length () + 1);
    }
  }

  // ---------------------------------------------------------------------------------//
  public boolean hasRange ()
  // ---------------------------------------------------------------------------------//
  {
    return hasRange;
  }

  // ---------------------------------------------------------------------------------//
  public Value get (int index)
  // ---------------------------------------------------------------------------------//
  {
    return values.get (index);
  }

  // ---------------------------------------------------------------------------------//
  public int size ()
  // ---------------------------------------------------------------------------------//
  {
    return values.size ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Iterator<Value> iterator ()
  // ---------------------------------------------------------------------------------//
  {
    return values.iterator ();
  }
}
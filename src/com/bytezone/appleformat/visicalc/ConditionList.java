package com.bytezone.appleformat.visicalc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// -----------------------------------------------------------------------------------//
class ConditionList implements Iterable<Value>
// -----------------------------------------------------------------------------------//
{
  private final List<Value> conditions = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  ConditionList (Cell cell, String text)
  // ---------------------------------------------------------------------------------//
  {
    String remainder = text;

    while (true)
    {
      String parameter = Expression.getParameter (remainder);
      if (Range.isRange (parameter))
        for (Address address : new Range (cell, parameter))
        {
          Cell target = cell.getCell (address);
          conditions.add (target);
        }
      else
        conditions.add (new Condition (cell, parameter));

      if (remainder.length () == parameter.length ())
        break;
      remainder = remainder.substring (parameter.length () + 1);
    }
  }

  // ---------------------------------------------------------------------------------//
  public Value get (int index)
  // ---------------------------------------------------------------------------------//
  {
    return conditions.get (index);
  }

  // ---------------------------------------------------------------------------------//
  public int size ()
  // ---------------------------------------------------------------------------------//
  {
    return conditions.size ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Iterator<Value> iterator ()
  // ---------------------------------------------------------------------------------//
  {
    return conditions.iterator ();
  }
}
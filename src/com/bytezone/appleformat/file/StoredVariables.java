package com.bytezone.appleformat.file;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class StoredVariables extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public StoredVariables (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    // test the offset code
    //    dataBuffer = Utility.getTestBuffer (dataBuffer);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    byte[] buffer = dataBuffer.data ();
    int ptr = dataBuffer.offset ();

    String strValue = null;
    int intValue = 0;
    //		double doubleValue = 0.0;
    int strPtr = dataBuffer.max ();

    text.append ("File length  : " + HexFormatter.format4 (dataBuffer.max ()));
    int totalLength = Utility.getShort (buffer, ptr);
    text.append ("\nTotal length : " + HexFormatter.format4 (totalLength));

    int varLength = Utility.getShort (buffer, ptr + 2);
    text.append ("\nVar length   : " + HexFormatter.format4 (varLength));
    text.append ("\n\n");

    // list simple variables

    ptr += 5;
    int max = dataBuffer.offset () + varLength + 5;
    while (ptr < max)
    {
      String variableName = getVariableName (buffer[ptr], buffer[ptr + 1]);
      text.append (variableName);

      char suffix = variableName.charAt (variableName.length () - 1);
      if (suffix == '$')
      {
        int strLength = buffer[ptr + 2] & 0xFF;
        strPtr -= strLength;
        strValue = HexFormatter.getString (buffer, strPtr, strLength);
        text.append (" = " + strValue);
      }
      else if (suffix == '%')
      {
        intValue = Utility.intValue (buffer[ptr + 3], buffer[ptr + 2]);   // backwards!
        if ((buffer[ptr + 2] & 0x80) != 0)
          intValue -= 65536;
        text.append (" = " + intValue);
      }
      else
      {
        if (hasValue (ptr + 2))
        {
          String value = Utility.floatValue (buffer, ptr + 2) + "";
          if (value.endsWith (".0"))
            text.append (" = " + value.substring (0, value.length () - 2));
          else
            text.append (" = " + value);
        }
      }

      text.append ("\n");
      ptr += 7;
    }

    listArrays (text, ptr, totalLength, strPtr);

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  private String getVariableName (byte b1, byte b2)
  // ---------------------------------------------------------------------------------//
  {
    char c1, c2, suffix;

    if ((b1 & 0x80) != 0)              // integer
    {
      c1 = (char) (b1 & 0x7F);
      c2 = (char) (b2 & 0x7F);
      suffix = '%';
    }
    else if ((b2 & 0x80) != 0)         // string
    {
      c1 = (char) b1;
      c2 = (char) (b2 & 0x7F);
      suffix = '$';
    }
    else
    {
      c1 = (char) b1;
      c2 = (char) b2;
      suffix = ' ';
    }

    StringBuilder variableName = new StringBuilder ();
    variableName.append (c1);
    if (c2 > 32)
      variableName.append (c2);
    if (suffix != ' ')
      variableName.append (suffix);

    return variableName.toString ();
  }

  // ---------------------------------------------------------------------------------//
  private String getDimensionText (int[] values)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ("(");

    for (int i = 0; i < values.length; i++)
    {
      text.append (values[i]);
      if (i < values.length - 1)
        text.append (',');
    }

    return text.append (')').toString ();
  }

  // ---------------------------------------------------------------------------------//
  private void listArrays (StringBuilder text, int ptr, int totalLength, int strPtr)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = dataBuffer.data ();

    while (ptr < totalLength + 5)
    {
      String variableName = getVariableName (buffer[ptr], buffer[ptr + 1]);
      text.append ("\n");

      int offset = Utility.getShort (buffer, ptr + 2);
      int dimensions = buffer[ptr + 4] & 0xFF;
      int[] dimensionSizes = new int[dimensions];
      int totalElements = 0;

      for (int i = 0; i < dimensions; i++)
      {
        int p = i * 2 + 5 + ptr;
        int elements = Utility.intValue (buffer[p + 1], buffer[p]);       // backwards!
        dimensionSizes[dimensions - i - 1] = elements - 1;

        if (totalElements == 0)
          totalElements = elements;
        else
          totalElements *= elements;
      }

      int headerSize = 5 + dimensions * 2;
      int elementSize = (offset - headerSize) / totalElements;

      int p = ptr + headerSize;
      int[] values = new int[dimensions];

      for (int i = 0; i < values.length; i++)
        values[i] = 0;

      out: while (true)
      {
        text.append (variableName + " " + getDimensionText (values) + " = ");
        if (elementSize == 2)
        {
          int intValue = Utility.intValue (buffer[p + 1], buffer[p]);     // backwards!
          if ((buffer[p] & 0x80) != 0)
            intValue -= 65536;
          text.append (intValue + "\n");
        }
        else if (elementSize == 3)
        {
          int strLength = buffer[p] & 0xFF;
          if (strLength > 0)
          {
            strPtr -= strLength;
            text.append (HexFormatter.getString (buffer, strPtr, strLength));
          }
          text.append ("\n");
        }
        else if (elementSize == 5)
        {
          if (hasValue (p))
            text.append (Utility.floatValue (buffer, p));
          text.append ("\n");
        }

        p += elementSize;
        int cp = 0;

        while (++values[cp] > dimensionSizes[cp])
        {
          values[cp++] = 0;
          if (cp >= values.length)
            break out;
        }
      }

      ptr += offset;
    }
  }

  // ---------------------------------------------------------------------------------//
  private boolean hasValue (int p)
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = dataBuffer.data ();

    for (int i = 0; i < 5; i++)
      if (buffer[p + i] != 0)
        return true;

    return false;
  }

  // ---------------------------------------------------------------------------------//
  public String getHexDump ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    byte[] buffer = dataBuffer.data ();
    int ptr = dataBuffer.offset ();

    text.append ("File length  : " + HexFormatter.format4 (dataBuffer.length ()));
    int totalLength = Utility.getShort (buffer, ptr);
    text.append ("\nTotal length : " + HexFormatter.format4 (totalLength));

    int varLength = Utility.getShort (buffer, ptr + 2);
    text.append ("\nVar length   : " + HexFormatter.format4 (varLength));

    int unknown = buffer[ptr + 4] & 0xFF;
    text.append ("\nUnknown      : " + HexFormatter.format2 (unknown));
    text.append ("\n\n");

    ptr += 5;
    text.append ("Simple variables : \n\n");

    while (ptr < varLength + 5)
    {
      text.append (HexFormatter.format (buffer, ptr, 7, false, 0) + "\n");
      ptr += 7;
    }

    text.append ("\nArrays : \n\n");
    while (ptr < totalLength + 5)
    {
      int offset = Utility.getShort (buffer, ptr + 2);
      int dimensions = buffer[ptr + 4] & 0xFF;
      int[] dimensionSizes = new int[dimensions];
      int totalElements = 0;

      for (int i = 0; i < dimensions; i++)
      {
        int p = i * 2 + 5 + ptr;
        int elements = Utility.intValue (buffer[p + 1], buffer[p]);   // backwards!
        dimensionSizes[dimensions - i - 1] = elements;
        if (totalElements == 0)
          totalElements = elements;
        else
          totalElements *= elements;
      }

      int headerSize = 5 + dimensions * 2;
      text.append (HexFormatter.format (buffer, ptr, headerSize, false, 0) + "\n\n");
      text.append (
          HexFormatter.format (buffer, ptr + headerSize, offset - headerSize, false, 0)
              + "\n\n");
      ptr += offset;
    }

    text.append ("Strings : \n\n");
    int length = buffer.length - ptr;
    text.append (HexFormatter.format (buffer, ptr, length, false, 0) + "\n\n");

    return text.toString ();
  }
}
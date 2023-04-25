package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.Utility;

import javafx.scene.paint.Color;

// ---------------------------------------------------------------------------------//
class ColorEntry
// ---------------------------------------------------------------------------------//
{
  int value;          // 0RGB
  Color color;

  // -------------------------------------------------------------------------------//
  public ColorEntry (int red, int green, int blue)
  // -------------------------------------------------------------------------------//
  {
    value = (red << 8) | (green << 4) | blue;
    color = Color.rgb (red, green, blue);
  }

  // -------------------------------------------------------------------------------//
  public ColorEntry (byte[] data, int offset)
  // -------------------------------------------------------------------------------//
  {
    value = Utility.getShort (data, offset);

    int red = ((value >> 8) & 0x0f) * 17;
    int green = ((value >> 4) & 0x0f) * 17;
    int blue = (value & 0x0f) * 17;

    color = Color.rgb (red, green, blue);
  }

  // -------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // -------------------------------------------------------------------------------//
  {
    return String.format ("ColorEntry: %04X", value);
  }
}

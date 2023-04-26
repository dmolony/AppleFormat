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

    int r1 = red | (red << 4);
    int g1 = green | (green << 4);
    int b1 = blue | (blue << 4);

    color = Color.rgb (r1, g1, b1);
    //    color = Color.rgb (red, green, blue);
  }

  // -------------------------------------------------------------------------------//
  public ColorEntry (byte[] data, int offset)
  // -------------------------------------------------------------------------------//
  {
    value = Utility.getShort (data, offset);

    int red = ((value >> 8) & 0x0f) * 17;
    int green = ((value >> 4) & 0x0f) * 17;
    int blue = (value & 0x0f) * 17;
    //    int red = ((value >> 8) & 0x0f) | ((value >> 4) & 0xF0);
    //    int green = ((value >> 4) & 0x0f) | (value & 0xF0);
    //    int blue = (value & 0x0f) | ((value << 4) & 0xF0);

    color = Color.rgb (red, green, blue);
  }

  // -------------------------------------------------------------------------------//
  public ColorEntry (Color color)
  // -------------------------------------------------------------------------------//
  {
    this.color = color;

    int red = (int) (color.getRed () * 15);
    int green = (int) (color.getGreen () * 15);
    int blue = (int) (color.getBlue () * 15);

    value = (red << 8) | (green << 4) | blue;
  }

  // -------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // -------------------------------------------------------------------------------//
  {
    return String.format ("ColorEntry: %04X", value);
  }
}

package com.bytezone.appleformat.graphics;

import javafx.scene.paint.Color;

// ---------------------------------------------------------------------------------//
class ColorTable
// ---------------------------------------------------------------------------------//
{
  private int id;
  ColorEntry[] entries = new ColorEntry[16];

  // -------------------------------------------------------------------------------//
  public ColorTable (int id, int mode)
  // -------------------------------------------------------------------------------//
  {
    // default empty table
    this.id = id;

    if ((mode & 0x80) == 0)             // mode 320
    {
      entries[0] = new ColorEntry (Color.BLACK);
      entries[1] = new ColorEntry (Color.DARKGRAY);
      entries[2] = new ColorEntry (Color.BROWN);
      entries[3] = new ColorEntry (Color.PURPLE);
      entries[4] = new ColorEntry (Color.BLUE);
      entries[5] = new ColorEntry (Color.DARKGREEN);
      entries[6] = new ColorEntry (Color.ORANGE);
      entries[7] = new ColorEntry (Color.RED);

      entries[8] = new ColorEntry (Color.BEIGE);
      entries[9] = new ColorEntry (Color.YELLOW);
      entries[10] = new ColorEntry (Color.GREEN);
      entries[11] = new ColorEntry (Color.LIGHTBLUE);
      entries[12] = new ColorEntry (0x0D, 0x0A, 0x0F);    // lilac
      entries[13] = new ColorEntry (0x07, 0x08, 0x0F);    // periwinkle blue
      entries[14] = new ColorEntry (Color.LIGHTGRAY);
      entries[15] = new ColorEntry (Color.WHITE);
    }
    else                                // mode640
    {
      entries[0] = new ColorEntry (Color.BLACK);
      entries[1] = new ColorEntry (Color.BLUE);
      entries[2] = new ColorEntry (Color.YELLOW);
      entries[3] = new ColorEntry (Color.WHITE);

      entries[4] = entries[0];
      entries[5] = new ColorEntry (Color.RED);
      entries[6] = new ColorEntry (Color.GREEN);
      entries[7] = entries[3];

      entries[8] = entries[0];
      entries[9] = entries[1];
      entries[10] = entries[2];
      entries[11] = entries[3];

      entries[12] = entries[4];
      entries[13] = entries[5];
      entries[14] = entries[6];
      entries[15] = entries[7];
    }
  }

  // -------------------------------------------------------------------------------//
  public ColorTable (int id, byte[] data, int offset)
  // -------------------------------------------------------------------------------//
  {
    this.id = id;
    for (int i = 0; i < 16; i++)
    {
      entries[i] = new ColorEntry (data, offset);
      offset += 2;
    }
  }

  // -------------------------------------------------------------------------------//
  String toLine ()
  // -------------------------------------------------------------------------------//
  {

    StringBuilder text = new StringBuilder ();

    text.append (String.format ("%02X", id));
    for (int i = 0; i < 16; i++)
      text.append (String.format ("  %04X", entries[i].value));

    return text.toString ();
  }

  // -------------------------------------------------------------------------------//
  void reverse ()
  // -------------------------------------------------------------------------------//
  {
    for (int i = 0; i < 8; i++)
    {
      ColorEntry temp = entries[i];
      entries[i] = entries[15 - i];
      entries[15 - i] = temp;
    }
  }

  // -------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // -------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("%3d ColorTable%n", id));
    for (int i = 0; i < 8; i++)
      text.append (String.format ("  %2d: %04X", i, entries[i].value));
    text.append ("\n");
    for (int i = 8; i < 16; i++)
      text.append (String.format ("  %2d: %04X", i, entries[i].value));

    return text.toString ();
  }
}
package com.bytezone.appleformat.graphics;

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

    if ((mode & 0x80) == 0)
    {
      entries[0] = new ColorEntry (0x00, 0x00, 0x00);
      entries[1] = new ColorEntry (0x07, 0x07, 0x07);
      entries[2] = new ColorEntry (0x08, 0x04, 0x01);
      entries[3] = new ColorEntry (0x07, 0x02, 0x0C);
      entries[4] = new ColorEntry (0x00, 0x00, 0x0F);
      entries[5] = new ColorEntry (0x00, 0x08, 0x00);
      entries[6] = new ColorEntry (0x0F, 0x07, 0x00);
      entries[7] = new ColorEntry (0x0D, 0x00, 0x00);

      entries[8] = new ColorEntry (0x0F, 0x0A, 0x09);
      entries[9] = new ColorEntry (0x0F, 0x0F, 0x00);
      entries[10] = new ColorEntry (0x00, 0x0E, 0x00);
      entries[11] = new ColorEntry (0x04, 0x0D, 0x0F);
      entries[12] = new ColorEntry (0x0D, 0x0A, 0x0F);
      entries[13] = new ColorEntry (0x07, 0x08, 0x0F);
      entries[14] = new ColorEntry (0x0C, 0x0C, 0x0C);
      entries[15] = new ColorEntry (0x0F, 0x0F, 0x0F);
    }
    else
    {
      entries[0] = new ColorEntry (0x00, 0x00, 0x00);
      entries[1] = new ColorEntry (0x00, 0x00, 0x0F);
      entries[2] = new ColorEntry (0x0F, 0x0F, 0x00);
      entries[3] = new ColorEntry (0x0F, 0x0F, 0x0F);

      entries[4] = new ColorEntry (0x00, 0x00, 0x00);
      entries[5] = new ColorEntry (0x0D, 0x00, 0x00);
      entries[6] = new ColorEntry (0x00, 0x0E, 0x00);
      entries[7] = new ColorEntry (0x0F, 0x0F, 0x0F);

      entries[0] = new ColorEntry (0x00, 0x00, 0x00);
      entries[1] = new ColorEntry (0x00, 0x00, 0x0F);
      entries[2] = new ColorEntry (0x0F, 0x0F, 0x00);
      entries[3] = new ColorEntry (0x0F, 0x0F, 0x0F);

      entries[4] = new ColorEntry (0x00, 0x00, 0x00);
      entries[5] = new ColorEntry (0x0D, 0x00, 0x00);
      entries[6] = new ColorEntry (0x00, 0x0E, 0x00);
      entries[7] = new ColorEntry (0x0F, 0x0F, 0x0F);
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
package com.bytezone.appleformat.fonts;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class FontFile extends CharacterList
// -----------------------------------------------------------------------------------//
{
  private static final int charsX = 16;

  // ---------------------------------------------------------------------------------//
  public FontFile (AppleFile file, Buffer dataRecord, int address)
  // ---------------------------------------------------------------------------------//
  {
    super (file, dataRecord);

    byte[] buffer = dataRecord.data ();
    int offset = dataRecord.offset ();
    int length = dataRecord.length ();

    loadAddress = address;
    int ptr = offset;

    while (ptr < buffer.length)
    {
      characters.add (new FontFileCharacter (buffer, ptr));
      ptr += sizeY;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isFont (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length % 8 != 0)
      return false;

    for (int i = 0; i < 8; i++)
      if (buffer[i] != 0 && buffer[i] != 0x7F)
        return false;

    return true;
  }

  // ---------------------------------------------------------------------------------//
  class FontFileCharacter extends Character
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer;
    int ptr;

    // -------------------------------------------------------------------------------//
    public FontFileCharacter (byte[] buffer, int ptr)
    // -------------------------------------------------------------------------------//
    {
      super (sizeX, sizeY);

      this.buffer = buffer;
      this.ptr = ptr;
    }

    // -------------------------------------------------------------------------------//
    @Override
    void draw (PixelWriter pixelWriter, int x, int y)
    // -------------------------------------------------------------------------------//
    {
      int ptr2 = ptr;
      for (int i = 0; i < sizeY; i++)
      {
        int value = buffer[ptr2++] & 0xFF;
        for (int j = 0; j < sizeX; j++)
        {
          pixelWriter.setColor (x + j, y + i,
              (value & 0x01) == 0 ? Color.BLACK : Color.WHITE);
          value >>>= 1;
        }
      }
    }

    // -------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // -------------------------------------------------------------------------------//
    {
      StringBuilder text = new StringBuilder ();
      int ptr2 = ptr;
      for (int i = 0; i < sizeY; i++)
      {
        int value = buffer[ptr2++] & 0xFF;
        for (int j = 0; j < sizeX; j++)
        {
          text.append (String.format ((value & 0x01) == 0 ? "." : "O"));
          value >>>= 1;
        }
        text.append ("\n");
      }
      return text.toString ();
    }
  }
}
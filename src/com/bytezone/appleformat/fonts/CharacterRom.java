package com.bytezone.appleformat.fonts;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// see graffidisk.v1.0.2mg
// -----------------------------------------------------------------------------------//
public class CharacterRom extends CharacterList
// -----------------------------------------------------------------------------------//
{
  private static final int charsX = 16;
  private static final int HEADER_LENGTH = 0x100;

  String description;

  // ---------------------------------------------------------------------------------//
  public CharacterRom (AppleFile file, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (file, buffer);

    description = HexFormatter.getCString (buffer, 16);
    int ptr = HEADER_LENGTH;

    while (ptr < buffer.length)
    {
      characters.add (new CharacterRomCharacter (buffer, ptr));
      ptr += sizeY;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isRom (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    if (buffer.length != 0x400)
      return false;

    // see CHARROM.S on graffidisk
    // BD 41 53 10 A0 07 08
    return buffer[0] == (byte) 0xBD && buffer[1] == (byte) 0x41
        && buffer[2] == (byte) 0x53 && buffer[4] == (byte) 0xA0
        && buffer[5] == (byte) 0x07 && buffer[6] == (byte) 0x08;
  }

  // ---------------------------------------------------------------------------------//
  class CharacterRomCharacter extends Character
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer;
    int ptr;

    // -------------------------------------------------------------------------------//
    public CharacterRomCharacter (byte[] buffer, int ptr)
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
      int ptr = this.ptr;

      for (int i = 0; i < sizeY; i++)
      {
        int value = buffer[ptr++] & 0xFF;
        for (int j = 0; j < sizeX; j++)
        {
          pixelWriter.setColor (x, y, (value & 0x80) == 0 ? Color.WHITE : Color.BLACK);
          value <<= 1;
        }
      }
    }
  }
}
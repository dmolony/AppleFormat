package com.bytezone.appleformat.fonts;

import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// Found on Pascal disks
// -----------------------------------------------------------------------------------//
public class Charset extends CharacterList
// -----------------------------------------------------------------------------------//
{
  private static final int charsX = 16;

  // ---------------------------------------------------------------------------------//
  public Charset (AppleFile file)
  // ---------------------------------------------------------------------------------//
  {
    super (file);

    byte[] buffer = dataBuffer.data ();
    int offset = dataBuffer.offset ();
    int length = dataBuffer.length ();

    int ptr = offset;

    while (ptr < dataBuffer.max ())
    {
      characters.add (new CharsetCharacter (buffer, ptr));
      ptr += sizeY;
    }
  }

  // ---------------------------------------------------------------------------------//
  class CharsetCharacter extends Character
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer;
    int ptr;

    // -------------------------------------------------------------------------------//
    public CharsetCharacter (byte[] buffer, int ptr)
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
      ptr += sizeY;         // start at the end and move backwards

      for (int i = 0; i < sizeY; i++)
      {
        int value = buffer[--ptr] & 0xFF;
        for (int j = 0; j < sizeX; j++)
        {
          pixelWriter.setColor (x, y, (value & 0x01) == 0 ? Color.WHITE : Color.BLACK);
          value >>>= 1;
        }
      }
    }
  }
}
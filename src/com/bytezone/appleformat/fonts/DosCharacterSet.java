package com.bytezone.appleformat.fonts;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;
import com.bytezone.utility.Utility;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class DosCharacterSet extends CharacterList
// -----------------------------------------------------------------------------------//
{
  static final int borderX = 15;
  static final int borderY = 15;
  static final int gapX = 15;
  static final int gapY = 15;

  static final int sizeX = 7;
  static final int sizeY = 8;

  private List<Character> characters = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public DosCharacterSet (AppleFile appleFile, Buffer dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

    byte[] buffer = dataBuffer.data ();
    int ptr = 4;                                  // skip load address/length

    for (int i = 32; i < 127; i++)                // characters
    {
      characters.add (new Character (buffer, ptr));
      ptr += 8;
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (Character character : characters)
    {
      text.append (character.toString ());
      text.append ("\n");                         // blank line between characters
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    if (characters.size () == 0)
      return null;

    int charsX = (int) Math.sqrt (characters.size ());
    int charsY = (characters.size () - 1) / charsX + 1;

    WritableImage image = new WritableImage (   //
        dimension (charsX, borderX, sizeX, gapX),
        dimension (charsY, borderY, sizeY, gapY));

    PixelWriter pixelWriter = image.getPixelWriter ();

    int count = 0;
    int x = borderX;
    int y = borderY;

    for (Character character : characters)
    {
      character.draw (pixelWriter, x, y);

      if (++count % charsX == 0)
      {
        x = borderX;
        y += sizeY + gapY;
      }
      else
        x += sizeX + gapX;
    }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  private int dimension (int chars, int border, int size, int gap)
  // ---------------------------------------------------------------------------------//
  {
    return border * 2 + chars * (size * 2 + gap) - gap;
  }

  // ---------------------------------------------------------------------------------//
  class Character
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer;
    int ptr;

    Character (byte[] buffer, int ptr)
    {
      this.buffer = buffer;
      this.ptr = ptr;
    }

    @Override
    public String toString ()
    {
      StringBuilder text = new StringBuilder ();
      int ptr2 = ptr;

      for (int j = 0; j < 8; j++)                 // 8 rows of 7 pixels
      {
        int val = buffer[ptr2++] & 0xFF;
        text.append (String.format ("%02X  ", val));

        if ((val & 0x80) != 0)                    // half pixel shift
          text.append (" ");

        for (int bit = 0; bit < 7; bit++)         // 7 displayable pixels
        {
          text.append (String.format ("%s ", (val & 0x01) != 0 ? "o" : " "));
          val >>>= 1;
        }
        text.append ("\n");                       // end of row
      }
      text.append ("\n");                         // blank line between characters

      return text.toString ();
    }

    // -------------------------------------------------------------------------------//
    void draw (PixelWriter pixelWriter, int x, int y)
    // -------------------------------------------------------------------------------//
    {
      int ptr2 = this.ptr;
      int left = x;

      for (int i = 0; i < 8; i++)                   // 8 rows of 7 pixels
      {
        int value = buffer[ptr2++] & 0xFF;
        if ((value & 0x80) != 0)                    // half pixel shift
        {
          pixelWriter.setColor (x, y, Color.WHITE);
          x++;
        }

        for (int j = 0; j < 7; j++)                 // 7 displayable pixels
        {
          Color pixelColor = (value & 0x01) == 0 ? Color.WHITE : Color.BLACK;
          pixelWriter.setColor (x, y, pixelColor);
          pixelWriter.setColor (x, y + 1, pixelColor);
          pixelWriter.setColor (x + 1, y, pixelColor);
          pixelWriter.setColor (x + 1, y + 1, pixelColor);

          value >>>= 1;
          x += 2;
        }

        x = left;
        y += 2;
      }
    }
  }
}

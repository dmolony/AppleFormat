package com.bytezone.appleformat.fonts;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.file.AbstractFormattedAppleFile;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.DataRecord;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

// -----------------------------------------------------------------------------------//
abstract class CharacterList extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  static final int borderX = 3;
  static final int borderY = 3;
  static final int gapX = 3;
  static final int gapY = 3;

  static final int sizeX = 7;
  static final int sizeY = 8;

  List<Character> characters = new ArrayList<> ();
  int loadAddress;

  // ---------------------------------------------------------------------------------//
  public CharacterList (AppleFile file)
  // ---------------------------------------------------------------------------------//
  {
    super (file);
  }

  // ---------------------------------------------------------------------------------//
  public CharacterList (AppleFile file, DataRecord dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (file, dataRecord);
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
    return border * 2 + chars * (size + gap) - gap;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ("Name : " + name + "\n\n");
    int count = 0;

    for (Character character : characters)
    {
      text.append (String.format ("Character: %d%n%n", count++));
      text.append (character.toString ());
      text.append ("\n");
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  class Character
  // ---------------------------------------------------------------------------------//
  {
    int height;
    int width;

    // -------------------------------------------------------------------------------//
    public Character (int sizeX, int sizeY)
    // -------------------------------------------------------------------------------//
    {
      width = sizeX;
      height = sizeY;
    }

    // -------------------------------------------------------------------------------//
    void draw (PixelWriter pixelWriter, int x, int y)
    // -------------------------------------------------------------------------------//
    {
      // override
    }
  }
}

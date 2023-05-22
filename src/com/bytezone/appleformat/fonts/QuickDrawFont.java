package com.bytezone.appleformat.fonts;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.ProdosConstants;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.FileProdos;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// see IIGS System 6.0.1 - Disk 5 Fonts.po
// -----------------------------------------------------------------------------------//
public class QuickDrawFont extends CharacterList
// -----------------------------------------------------------------------------------//
{
  Map<Integer, QuickDrawCharacter> qdCharacters = new HashMap<> ();

  private boolean corrupt;
  private final String fontName;
  private final int offsetToMF;
  private final int fontSize;
  private final int fontFamily;
  private final int fontStyle;
  private final int versionMajor;
  private final int versionMinor;
  private final int fbrExtent;
  private final int fontType;
  private final int firstChar;
  private final int lastChar;
  private final int widMax;
  private final short kernMax;
  private final short nDescent;
  private final int fRectWidth;
  private final int fRectHeight;
  private final int owTLoc;
  private final int ascent;
  private final int descent;
  private final int leading;
  private final int rowWords;
  private final int[] unknownValues;

  private final int totalCharacters;

  //  private final int locationTableOffset;
  //  private final int offsetWidthTableOffset;
  //  private int offsetWidthTableSize;

  private final int locTablePtr;
  private final int owTablePtr;

  private int widestCharacter;

  private BitSet[] strike;        // bit image of all characters

  // ---------------------------------------------------------------------------------//
  public QuickDrawFont (AppleFile file, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (file, buffer);

    assert file.getFileType () == ProdosConstants.FILE_TYPE_FONT;

    if (((FileProdos) file).getAuxType () != 0)
      System.out.printf ("Font aux: %04X%n", ((FileProdos) file).getAuxType ());

    fontName = HexFormatter.getPascalString (buffer, 0);
    int nameLength = (buffer[0] & 0xFF);

    int ptr = nameLength + 1;         // start of header record

    offsetToMF = Utility.getShort (buffer, ptr);

    fontFamily = Utility.getShort (buffer, ptr + 2);
    fontStyle = Utility.getShort (buffer, ptr + 4);
    fontSize = Utility.getShort (buffer, ptr + 6);
    versionMajor = buffer[ptr + 8] & 0xFF;
    versionMinor = buffer[ptr + 9] & 0xFF;
    fbrExtent = Utility.getShort (buffer, ptr + 10);     // font bounds rectangle extent

    unknownValues = new int[offsetToMF - 6];
    int gapPtr = ptr + 12;
    ptr = nameLength + 1 + offsetToMF * 2;

    int ndx = 0;
    while (gapPtr < ptr)
    {
      unknownValues[ndx++] = Utility.getShort (buffer, gapPtr);
      //      System.out.println (val);
      gapPtr += 2;
    }

    fontType = Utility.getShort (buffer, ptr);
    firstChar = Utility.getShort (buffer, ptr + 2);       // ascii code of first char
    lastChar = Utility.getShort (buffer, ptr + 4);        // ascii code of last char
    widMax = Utility.getShort (buffer, ptr + 6);
    kernMax = (short) Utility.getSignedShort (buffer, ptr + 8);
    nDescent = (short) Utility.getSignedShort (buffer, ptr + 10);
    fRectWidth = Utility.getShort (buffer, ptr + 12);
    fRectHeight = Utility.getShort (buffer, ptr + 14);
    owTLoc = Utility.getShort (buffer, ptr + 16);
    ascent = Utility.getShort (buffer, ptr + 18);
    descent = Utility.getShort (buffer, ptr + 20);
    leading = Utility.getShort (buffer, ptr + 22);
    rowWords = Utility.getShort (buffer, ptr + 24);

    totalCharacters = lastChar - firstChar + 1;

    owTablePtr = ptr + 16 + owTLoc * 2;
    ptr += 26;                                            // ptr to bitImage
    locTablePtr = ptr + rowWords * 2 * fRectHeight;       // end of bitImage

    strike = createStrike (buffer, ptr, fRectHeight, rowWords * 2);
    createCharacters (buffer, locTablePtr, owTablePtr);
  }

  // ---------------------------------------------------------------------------------//
  private BitSet[] createStrike (byte[] buffer, int ptr, int rows, int rowLength)
  // ---------------------------------------------------------------------------------//
  {
    BitSet[] strike = new BitSet[rows];

    for (int row = 0; row < strike.length; row++)
    {
      strike[row] = new BitSet (rowLength * 8);

      int bitNo = 0;
      for (int i = 0; i < rowLength; i++)
      {
        byte b = buffer[ptr++];
        for (int j = 0; j < 8; j++)
        {
          strike[row].set (bitNo++, (b & 0x80) != 0);
          b <<= 1;
        }
      }
    }

    return strike;
  }

  // ---------------------------------------------------------------------------------//
  private void createCharacters (byte[] buffer, int locTablePtr, int owtPtr)
  // ---------------------------------------------------------------------------------//
  {
    int code = firstChar;
    for (int i = 0; i <= totalCharacters; i++)          // includes 'missing' character
    {
      // offset into the strike
      int location = Utility.getShort (buffer, locTablePtr);   // location table entry
      locTablePtr += 2;

      int offsetWidth = Utility.getSignedShort (buffer, owtPtr);
      int offset = buffer[owtPtr++] & 0xFF;       // 0:254
      int width = buffer[owtPtr++] & 0xFF;        // 0:254

      if (offsetWidth >= 0)
      {
        int nextLocation = Utility.getShort (buffer, locTablePtr);
        int pixelWidth = nextLocation - location;

        if (pixelWidth > 0)
        {
          QuickDrawCharacter c =
              new QuickDrawCharacter (location, pixelWidth, fRectHeight, offset, width);
          qdCharacters.put (i, c);
          characters.add (c);
          widestCharacter = Math.max (widestCharacter, pixelWidth);
        }
      }

      code++;
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    int inset = 10;
    int spacing = 5;

    int charsWide = (int) (Math.sqrt (totalCharacters + 1) + .5);
    int charsHigh = totalCharacters / charsWide + 1;

    WritableImage image = new WritableImage (charsWide * (widMax + spacing) + inset * 2,
        charsHigh * (fRectHeight + spacing) + inset * 2);
    PixelWriter pixelWriter = image.getPixelWriter ();

    int x = inset;
    int y = inset;
    int count = 0;

    for (int i = 0; i <= totalCharacters; i++)        // includes 'missing' character
    {
      int pos = qdCharacters.containsKey (i) ? i : lastChar + 1;
      QuickDrawCharacter character = qdCharacters.get (pos);

      // how the character image to be drawn should be positioned with
      // respect to the current pen location
      //      int offset = buffer[offsetWidthTableOffset + i * 2 + 1];
      // how far the pen should be advanced after the character is drawn
      //      int width = buffer[offsetWidthTableOffset + i * 2] & 0xFF;

      if (character != null)
        character.draw (pixelWriter, x, y);

      x += (widMax + spacing);
      if (++count % charsWide == 0)
      {
        x = inset;
        y += (fRectHeight + spacing);
      }
    }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    FileProdos file = (FileProdos) appleFile;

    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Name ...................... %s%n", name));
    text.append (String.format ("File type ................. %02X    %s%n",
        file.getFileType (), file.getFileTypeText ()));
    String auxTypeText = file.getAuxType () == 0 ? "QuickDraw Font File"
        : file.getAuxType () == 1 ? "Truetype Font" : "??";
    text.append (String.format ("Aux type .................. %04X  %s%n%n",
        file.getAuxType (), auxTypeText));
    text.append (String.format ("Font name ................. %s%n", fontName));
    text.append (String.format ("Offset to MF part ......... %d%n", offsetToMF));
    text.append (String.format ("Font family number ........ %d%n", fontFamily));
    text.append (String.format ("Style ..................... %d%n", fontStyle));
    text.append (String.format ("Point size .................%d%n", fontSize));
    text.append (String.format ("Font version .............. %d.%d%n", versionMajor,
        versionMinor));
    text.append (String.format ("Font bounds rect extent ... %d%n", fbrExtent));

    for (int i = 0; i < unknownValues.length; i++)
      text.append (
          String.format ("Additional field .......... %04X  %<,6d%n", unknownValues[i]));

    text.append (String.format (
        "%nFont type ................. %04X  %<,6d  (ignored on IIgs)%n", fontType));
    text.append (String.format ("Ascii code of first char .. %04X  %<,6d%n", firstChar));
    text.append (String.format ("Ascii code of last char ... %04X  %<,6d%n", lastChar));
    text.append (String.format ("Maximum character width ... %04X  %<,6d%n", widMax));
    text.append (String.format ("Maximum leftward kern ..... %04X  %<,6d%n", kernMax));
    text.append (String.format ("Negative of descent ....... %04X  %<,6d%n%n", nDescent));
    text.append (String.format ("Width of font rectangle ... %04X  %<,6d%n", fRectWidth));
    text.append (
        String.format ("Height of font rectangle .. %04X  %<,6d%n%n", fRectHeight));
    text.append (String.format ("Offset to O/W table ....... %04X  %<,6d%n%n", owTLoc));
    text.append (String.format ("Ascent .................... %04X  %<,6d%n", ascent));
    text.append (String.format ("Descent ................... %04X  %<,6d%n", descent));
    text.append (String.format ("Leading ................... %04X  %<,6d%n%n", leading));
    text.append (String.format ("Width of font strike ...... %04X  %<,6d  (%,d bits)%n%n",
        rowWords, rowWords * 16));

    text.append (String.format ("Total characters .......... %d%n", totalCharacters));
    text.append (String.format ("Widest character .......... %d%n%n", widestCharacter));

    if (corrupt)
    {
      text.append ("\nCannot interpret Font file");
      return text.toString ();
    }

    text.append ("Char   Loc table   Offset   Width\n");
    text.append (" ---   ---------   ------   -----\n");

    for (int i = 0; i <= totalCharacters; i++)
    {
      int width = buffer[owTablePtr + i * 2] & 0xFF;
      int offset = buffer[owTablePtr + i * 2 + 1] & 0xFF;

      int location = Utility.getShort (buffer, locTablePtr + i * 2);
      int nextLocation = Utility.getShort (buffer, locTablePtr + (i + 1) * 2);
      int pixelWidth = nextLocation - location;

      text.append (String.format (" %3d  %,5d (%2d)      %3d     %3d%n", i, location,
          pixelWidth, offset, width));
    }

    int lastLoc =
        Utility.getSignedShort (buffer, locTablePtr + (totalCharacters + 1) * 2);
    text.append (String.format (" %3d  %,5d%n", lastChar + 2, lastLoc));

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (BitSet bitSet : strike)
    {
      for (int i = 0; i < bitSet.size (); i++)
        text.append (bitSet.get (i) ? "#" : ".");
      text.append ("\n");
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  class QuickDrawCharacter extends Character
  // ---------------------------------------------------------------------------------//
  {
    int strikeOffset;
    int strikeWidth;
    int offset;
    int width;

    // -------------------------------------------------------------------------------//
    public QuickDrawCharacter (int strikeOffset, int strikeWidth, int height, int offset,
        int width)
    // -------------------------------------------------------------------------------//
    {
      super (strikeWidth, height);

      this.strikeOffset = strikeOffset;
      this.strikeWidth = strikeWidth;
      this.offset = offset;
      this.width = width;
    }

    // -------------------------------------------------------------------------------//
    @Override
    void draw (PixelWriter pixelWriter, int x, int y)
    // -------------------------------------------------------------------------------//
    {
      for (int row = 0; row < height; row++)
      {
        int col = 0;
        for (int j = strikeOffset; j < strikeOffset + strikeWidth; j++)
        {
          if (strike[row].get (j))
            pixelWriter.setColor (x + col, y + row, Color.BLACK);

          col++;
        }
      }
    }

    // -------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // -------------------------------------------------------------------------------//
    {
      StringBuilder text = new StringBuilder ();

      for (int row = 0; row < fRectHeight; row++)
      {
        for (int j = strikeOffset; j < strikeOffset + strikeWidth; j++)
          text.append ((strike[row].get (j) ? "X" : "."));
        text.append ("\n");
      }

      return Utility.rtrim (text);
    }
  }
}
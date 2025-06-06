package com.bytezone.appleformat.fonts;

import static com.bytezone.utility.Utility.formatText;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.ProdosConstants;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

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

  private final int locTablePtr;
  private final int owTablePtr;

  private int widestCharacter;
  private int lastStrikeLocation;

  private BitSet[] strike;        // bit image of all characters

  private int fileType;

  // ---------------------------------------------------------------------------------//
  public QuickDrawFont (AppleFile file) throws FontValidationException
  // ---------------------------------------------------------------------------------//
  {
    super (file);

    fileType = file.getFileType ();

    byte[] buffer = dataBuffer.data ();
    int offset = dataBuffer.offset ();
    int length = dataBuffer.length ();

    assert file.getFileType () == ProdosConstants.FILE_TYPE_FONT;

    if (file.getAuxType () != 0)
      System.out.printf ("Font aux: %04X%n", file.getAuxType ());

    fontName = HexFormatter.getPascalString (buffer, 0);
    int nameLength = (buffer[0] & 0xFF);

    if (nameLength == 0)
      throw new FontValidationException ("No font name");

    int ptr = nameLength + 1;         // start of header record

    offsetToMF = Utility.getShort (buffer, ptr);

    fontFamily = Utility.getShort (buffer, ptr + 2);
    fontStyle = Utility.getShort (buffer, ptr + 4);
    fontSize = Utility.getShort (buffer, ptr + 6);
    versionMajor = buffer[ptr + 8] & 0xFF;
    versionMinor = buffer[ptr + 9] & 0xFF;
    fbrExtent = Utility.getShort (buffer, ptr + 10);     // font bounds rectangle extent

    if (offsetToMF >= 6)
      unknownValues = new int[offsetToMF - 6];
    else
      unknownValues = new int[0];
    int gapPtr = ptr + 12;
    ptr = nameLength + 1 + offsetToMF * 2;

    int ndx = 0;
    while (gapPtr < ptr)
    {
      unknownValues[ndx++] = Utility.getShort (buffer, gapPtr);
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

    lastStrikeLocation =
        Utility.getShort (buffer, locTablePtr + (totalCharacters + 1) * 2) - 1;

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
          QuickDrawCharacter c = new QuickDrawCharacter (code, location, pixelWidth,
              fRectHeight, offset, width);
          qdCharacters.put (code, c);
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

    WritableImage image =
        new WritableImage (charsWide * (widestCharacter + spacing) + inset * 2,
            charsHigh * (fRectHeight + spacing) + inset * 2);
    PixelWriter pixelWriter = image.getPixelWriter ();

    int x = inset;
    int y = inset;
    int count = 0;

    int code = firstChar;
    for (int i = 0; i <= totalCharacters; i++)        // includes 'missing' character
    {
      int pos = qdCharacters.containsKey (code) ? code : lastChar + 1;
      QuickDrawCharacter character = qdCharacters.get (pos);
      code++;

      // how the character image to be drawn should be positioned with
      // respect to the current pen location
      //      int offset = buffer[offsetWidthTableOffset + i * 2 + 1];
      // how far the pen should be advanced after the character is drawn
      //      int width = buffer[offsetWidthTableOffset + i * 2] & 0xFF;

      if (character != null)
        character.draw (pixelWriter, x, y);

      x += (widestCharacter + spacing);
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
    StringBuilder text = new StringBuilder ();

    String version = versionMajor + "." + versionMinor;

    formatText (text, "Name", name);
    formatText (text, "File type", 2, fileType, ProdosConstants.fileTypes[fileType]);

    int aux = appleFile.getAuxType ();
    String auxTypeText =
        aux == 0 ? "QuickDraw Font File" : aux == 1 ? "Truetype Font" : "??";
    formatText (text, "Aux type", 4, aux, auxTypeText);
    formatText (text, "Font name", fontName);
    formatText (text, "Offset to MF part", 2, offsetToMF);
    formatText (text, "Font family number", 4, fontFamily);
    formatText (text, "Style", 4, fontStyle);
    formatText (text, "Point size", 2, fontSize);
    formatText (text, "Font version", version);
    formatText (text, "Font bounds rect ext", 4, fbrExtent);

    for (int i = 0; i < unknownValues.length; i++)
      formatText (text, "Additional field", 4, unknownValues[i]);

    text.append ("\n");
    formatText (text, "Font type", 4, fontType, " (ignored on IIgs)");

    formatText (text, "Ascii code of first char", 4, firstChar);
    formatText (text, "Ascii code of last char", 4, lastChar);
    formatText (text, "Maximum character width", 4, widMax);
    formatText (text, "Maximum leftward kern", 4, kernMax);
    formatText (text, "Negative of descent", 8, nDescent);
    text.append ("\n");
    formatText (text, "Width of font rectangle", 4, fRectWidth);
    formatText (text, "Height of font rectangle", 4, fRectHeight);
    formatText (text, "Offset to O/W table", 4, owTLoc);
    formatText (text, "Ascent", 4, ascent);
    formatText (text, "Descent", 4, descent);
    formatText (text, "Leading", 4, leading);
    formatText (text, "Width of font strike", 4, rowWords,
        String.format (" (%,d bits)", rowWords * 16));

    formatText (text, "Total characters", 4, totalCharacters);
    formatText (text, "Widest character", 4, widestCharacter);
    text.append ("\n");

    if (corrupt)
    {
      text.append ("\nCannot interpret Font file");
      return text.toString ();
    }

    byte[] buffer = dataBuffer.data ();
    //    int offset = dataRecord.offset ();
    //    int length = dataRecord.length ();

    text.append ("Pos   Ascii   Loc table   Offset   Width\n");
    text.append ("---   -----   ---------   ------   -----\n");

    int code = firstChar;
    for (int i = 0; i <= totalCharacters; i++)
    {
      int width = buffer[owTablePtr + i * 2] & 0xFF;
      int offset = buffer[owTablePtr + i * 2 + 1] & 0xFF;

      int location = Utility.getShort (buffer, locTablePtr + i * 2);
      int nextLocation = Utility.getShort (buffer, locTablePtr + (i + 1) * 2);
      int pixelWidth = nextLocation - location;
      String flag = pixelWidth == widestCharacter ? "*" : " ";

      text.append (String.format ("%3d     %3d  %,5d (%2d) %s    %3d     %3d%n", i, code,
          location, pixelWidth, flag, offset, width));

      code++;
    }

    int lastLoc = Utility.getShort (buffer, locTablePtr + (totalCharacters + 1) * 2);
    text.append (String.format ("             %,5d%n", lastLoc));

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int row = 0;
    char c = '#';
    char b = '.';

    for (BitSet bitSet : strike)
    {
      for (int i = 0; i <= lastStrikeLocation; i++)
        text.append (bitSet.get (i) ? c : b);
      text.append ("\n");

      if (++row >= ascent)
        c = '*';
    }

    int pos = 0;

    for (QuickDrawCharacter character : qdCharacters.values ())
    {
      while (pos < character.strikeOffset)
      {
        text.append (' ');
        pos++;
      }

      text.append ('+');
      pos++;
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  class QuickDrawCharacter extends Character
  // ---------------------------------------------------------------------------------//
  {
    int asciiCode;
    int strikeOffset;
    int strikeWidth;
    int offset;
    int width;

    // -------------------------------------------------------------------------------//
    public QuickDrawCharacter (int asciiCode, int strikeOffset, int strikeWidth,
        int height, int offset, int width)
    // -------------------------------------------------------------------------------//
    {
      super (strikeWidth, height);

      this.asciiCode = asciiCode;
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
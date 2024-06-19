package com.bytezone.appleformat.graphics;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.appleformat.ProdosConstants;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.DataRecord;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class IconFile extends Graphics
// -----------------------------------------------------------------------------------//
{
  static PaletteFactory paletteFactory = new PaletteFactory ();

  private final int iBlkNext;
  private final int iBlkID;
  private final int iBlkPath;
  private final String iBlkName;
  private final List<Icon> icons = new ArrayList<> ();
  private final boolean debug = false;

  // ---------------------------------------------------------------------------------//
  public IconFile (AppleFile appleFile, DataRecord dataRecord)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, dataRecord);

    byte[] buffer = dataRecord.data ();

    iBlkNext = Utility.getLong (buffer, 0);
    iBlkID = Utility.getShort (buffer, 4);
    iBlkPath = Utility.getLong (buffer, 6);
    iBlkName = HexFormatter.getHexString (buffer, 10, 16);

    int ptr = 26;

    while (true)
    {
      int dataLen = Utility.getShort (buffer, ptr);
      if (dataLen == 0 || (dataLen + ptr) > buffer.length)
        break;

      Icon icon = new Icon (buffer, ptr);
      if (icon.smallImage != null)            // didn't have an exception
        icons.add (icon);
      ptr += dataLen;
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    if (icons.size () == 0)
      return emptyImage;

    // calculate maximum width and height for every icon
    int maxHeight = 0;
    int maxWidth = 0;

    for (Icon icon : icons)
    {
      maxHeight = Math.max (maxHeight, icon.largeImage.iconHeight);
      maxWidth = Math.max (maxWidth, icon.largeImage.iconWidth);
    }

    int base = 10;
    int x = base;
    int y = base;
    int gap = 5;

    int columns = Math.min (icons.size (), 4);
    int rows = (icons.size () - 1) / columns + 1;

    WritableImage image = new WritableImage (                       //
        Utility.dimension (columns, base, maxWidth, gap),           //
        Utility.dimension (rows, base, maxHeight, gap));

    PixelWriter pixelWriter = image.getPixelWriter ();

    int count = 0;
    for (Icon icon : icons)
    {
      icon.largeImage.draw (pixelWriter, x, y);
      x += maxWidth + gap;
      count++;
      if (count % columns == 0)
      {
        x = base;
        y += maxHeight + gap;
      }
    }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ("Name : " + name + "\n\n");

    text.append (String.format ("Next Icon file .. %d%n", iBlkNext));
    text.append (String.format ("Block ID ........ %d%n", iBlkID));
    text.append (String.format ("Block path ...... %d%n", iBlkPath));
    text.append (String.format ("Block name ...... %s%n", iBlkName));

    text.append ("\n");
    for (Icon icon : icons)
    {
      text.append (icon);
      text.append ("\n\n");
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  class Icon
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer;
    int iDataLen;
    String pathName;
    String dataName;
    int iDataType;
    int iDataAux;
    IconImage largeImage;
    IconImage smallImage;

    // -------------------------------------------------------------------------------//
    public Icon (byte[] fullBuffer, int ptr)
    // -------------------------------------------------------------------------------//
    {
      iDataLen = Utility.getShort (fullBuffer, ptr);

      buffer = new byte[iDataLen];
      System.arraycopy (fullBuffer, ptr, buffer, 0, buffer.length);

      pathName = Utility.getPascalString (buffer, 2);
      dataName = Utility.getPascalString (buffer, 66);

      iDataType = Utility.getShort (buffer, 82);
      iDataAux = Utility.getShort (buffer, 84);

      if (debug)
      {
        System.out.printf ("Len    %,d%n", iDataLen);
        System.out.printf ("Path   %s%n", pathName);
        System.out.printf ("Data   %s%n", dataName);
        System.out.printf ("Type   %04X  %s%n", iDataType,
            ProdosConstants.fileTypes[iDataType]);
        System.out.printf ("Aux    %04X%n", iDataAux);
      }

      try
      {
        largeImage = new IconImage (buffer, 86);
        smallImage = new IconImage (buffer, 86 + largeImage.size ());
      }
      catch (InvalidImageException e)
      {
        System.out.println (e.getMessage ());
      }
    }

    // -------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // -------------------------------------------------------------------------------//
    {
      StringBuilder text = new StringBuilder ();
      text.append (String.format ("Data length .. %04X%n", iDataLen));
      text.append (String.format ("Path name .... %s%n", pathName));
      text.append (String.format ("Data name .... %s%n", dataName));
      text.append ("\n");
      text.append (largeImage);
      text.append ("\n");
      text.append ("\n");
      text.append (smallImage);
      return text.toString ();
    }
  }

  // ---------------------------------------------------------------------------------//
  class IconImage
  // ---------------------------------------------------------------------------------//
  {
    int iconType;
    int iconSize;
    int iconHeight;
    int iconWidth;
    byte[] iconImage;
    byte[] iconMask;
    boolean colour;

    // -------------------------------------------------------------------------------//
    public IconImage (byte[] buffer, int ptr) throws InvalidImageException
    // -------------------------------------------------------------------------------//
    {
      iconType = Utility.getShort (buffer, ptr);
      iconSize = Utility.getShort (buffer, ptr + 2);
      iconHeight = Utility.getShort (buffer, ptr + 4);
      iconWidth = Utility.getShort (buffer, ptr + 6);

      if (debug)
      {
        System.out.printf ("Icon type ... %04X %<d%n", iconType);
        System.out.printf ("Icon size ... %d%n", iconSize);
        System.out.printf ("Icon height . %d%n", iconHeight);
        System.out.printf ("Icon width .. %d%n", iconWidth);
        System.out.println ();
      }

      if (iconWidth == 0 || iconHeight == 0)
        throw new InvalidImageException (
            String.format ("Invalid icon: Height: %d, Width: %d", iconHeight, iconWidth));

      if (iconType != 0 && iconType != 0x8000 && iconType != 0xFFFF && iconType != 0x00FF)
        throw new InvalidImageException (String.format ("Bad icon type: %04X", iconType));
      // have also seen 0x7FFF and 0x8001

      iconImage = new byte[iconSize];
      iconMask = new byte[iconSize];

      colour = (iconType & 0x80) != 0;

      System.arraycopy (buffer, ptr + 8, iconImage, 0, iconSize);
      System.arraycopy (buffer, ptr + 8 + iconSize, iconMask, 0, iconSize);
    }

    // -------------------------------------------------------------------------------//
    private void draw (PixelWriter pixelWriter, int x, int y)
    // -------------------------------------------------------------------------------//
    {
      Color[] colours = paletteFactory.get (0).getColours ();

      int rowBytes = (iconWidth - 1) / 2 + 1;
      int row = 0;

      for (int i = 0; i < iconImage.length; i += rowBytes)
      {
        int col = 0;
        for (int j = i, max = i + rowBytes; j < max; j++)
        {
          int left = (byte) ((iconImage[j] & 0xF0) >>> 4);
          int right = (byte) (iconImage[j] & 0x0F);
          int maskLeft = (byte) ((iconMask[j] & 0xF0) >>> 4);
          int maskRight = (byte) (iconMask[j] & 0x0F);

          pixelWriter.setColor (x + col++, y + row, colours[left & maskLeft]);
          pixelWriter.setColor (x + col++, y + row, colours[right & maskRight]);

          // see WhatIsThe2gs/System 6 and Free Games.hdv/SWAREGAME.ICONS
        }
        row++;
      }
    }

    // -------------------------------------------------------------------------------//
    public int size ()
    // -------------------------------------------------------------------------------//
    {
      return 8 + iconSize * 2;
    }

    // -------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // -------------------------------------------------------------------------------//
    {
      StringBuilder text = new StringBuilder ();

      text.append (String.format ("Icon type .... %04X%n", iconType));
      text.append (String.format ("Icon size .... %d%n", iconSize));
      text.append (String.format ("Icon height .. %d%n", iconHeight));
      text.append (String.format ("Icon width ... %d%n%n", iconWidth));
      appendIcon (text, iconImage);
      text.append ("\n\n");
      appendIcon (text, iconMask);

      return text.toString ();
    }

    /*
        Offset  Color   RGB  Mini-Palette
    
        0       Black   000    0
        1       Blue    00F    1
        2       Yellow  FF0    2
        3       White   FFF    3
        4       Black   000    0
        5       Red     D00    1
        6       Green   0E0    2
        7       White   FFF    3
    
        8       Black   000    0
        9       Blue    00F    1
        10      Yellow  FF0    2
        11      White   FFF    3
        12      Black   000    0
        13      Red     D00    1
        14      Green   0E0    2
        15      White   FFF    3
    
    The displayMode word bits are defined as:
    
    Bit 0       selectedIconBit    1 = invert image before copying
    Bit 1       openIconBit        1 = copy light-gray pattern instead of image
    Bit 2       offLineBit         1 = AND light-gray pattern to image being copied
    Bits 3-7    reserved.
    Bits 8-11   foreground color to apply to black part of black & white icons
    Bits 12-15  background color to apply to white part of black & white icons
    
    Bits 0-2 can occur at once and are tested in the order 1-2-0.
    
    "Color is only applied to the black and white icons if bits 15-8 are not all 0.
    Colored pixels in an icon are inverted by black pixels becoming white and any
    other color of pixel becoming black."
    */

    // -------------------------------------------------------------------------------//
    private void appendIcon (StringBuilder text, byte[] buffer)
    // -------------------------------------------------------------------------------//
    {
      int rowBytes = (iconWidth - 1) / 2 + 1;
      for (int i = 0; i < iconImage.length; i += rowBytes)
      {
        for (int ptr = i, max = i + rowBytes; ptr < max; ptr++)
        {
          int left = (byte) ((buffer[ptr] & 0xF0) >>> 4);
          int right = (byte) (buffer[ptr] & 0x0F);
          text.append (String.format ("%X %X ", left, right));
        }
        text.append ("\n");
      }
      if (text.length () > 0)
        text.deleteCharAt (text.length () - 1);
    }
  }

  // ---------------------------------------------------------------------------------//
  class InvalidImageException extends Exception
  // ---------------------------------------------------------------------------------//
  {
    // -------------------------------------------------------------------------------//
    public InvalidImageException (String message)
    // -------------------------------------------------------------------------------//
    {
      super (message);
    }
  }
}
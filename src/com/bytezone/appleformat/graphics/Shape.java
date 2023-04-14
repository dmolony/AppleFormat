package com.bytezone.appleformat.graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.HexFormatter;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
class Shape
// -----------------------------------------------------------------------------------//
{
  private static final int SIZE = 400;
  static final int ORIGIN = SIZE / 2;

  private final byte[] buffer;
  private final int index;
  private final int shapeOffset;

  int actualLength;

  int startRow = ORIGIN;
  int startCol = ORIGIN;
  int[][] grid = new int[SIZE][SIZE];
  int[][] displayGrid;
  boolean valid;

  int minY = startRow;
  int maxY = startRow;
  int minX = startCol;
  int maxX = startCol;

  BufferedImage image;

  // ---------------------------------------------------------------------------------//
  public Shape (byte[] buffer, int shapeOffset, int max, int index)
  // ---------------------------------------------------------------------------------//
  {
    this.index = index;
    this.buffer = buffer;
    this.shapeOffset = shapeOffset;

    int row = startRow;
    int col = startCol;

    int ptr = shapeOffset;
    while (ptr < max)
    {
      int value = buffer[ptr++] & 0xFF;

      if (value == 0)
        break;

      // P  = plot
      // DD = direction to move
      int v1 = value >>> 6;                     //  DD......
      int v2 = (value & 0x38) >>> 3;            //  ..PDD...
      int v3 = value & 0x07;                    //  .....PDD

      // rightmost 3 bits
      if (v3 >= 4 && !plot (row, col))
        return;

      if (v3 == 0 || v3 == 4)
        row--;
      else if (v3 == 1 || v3 == 5)
        col++;
      else if (v3 == 2 || v3 == 6)
        row++;
      else
        col--;

      // middle 3 bits
      if (v2 >= 4 && !plot (row, col))
        return;

      // cannot move up without plotting if v1 is zero
      if ((v2 == 0 && v1 != 0) || v2 == 4)
        row--;
      else if (v2 == 1 || v2 == 5)
        col++;
      else if (v2 == 2 || v2 == 6)
        row++;
      else if (v2 == 3 || v2 == 7)
        col--;

      // leftmost 2 bits (cannot plot or move up)
      if (v1 == 1)
        col++;
      else if (v1 == 2)
        row++;
      else if (v1 == 3)
        col--;
    }

    actualLength = ptr - shapeOffset;

    valid = true;         // all points are within grid
  }

  // ---------------------------------------------------------------------------------//
  void convertGrid (int offsetRows, int offsetColumns, int rows, int columns)
  // ---------------------------------------------------------------------------------//
  {
    displayGrid = new int[rows][columns];

    for (int row = 0; row < rows; row++)
      for (int col = 0; col < columns; col++)
        displayGrid[row][col] = grid[offsetRows + row][offsetColumns + col];

    grid = null;

    startRow -= offsetRows;
    startCol -= offsetColumns;
  }

  // ---------------------------------------------------------------------------------//
  void draw (PixelWriter pw, int x, int y)
  // ---------------------------------------------------------------------------------//
  {
    for (int row = 0; row < displayGrid.length; row++)
      for (int col = 0; col < displayGrid[0].length; col++)
      {
        Color color = displayGrid[row][col] == 0 ? Color.ALICEBLUE : Color.GREEN;
        pw.setColor (x + col, y + row, color);
      }
  }

  // ---------------------------------------------------------------------------------//
  private boolean plot (int row, int col)
  // ---------------------------------------------------------------------------------//
  {
    if (row < 0 || row >= SIZE || col < 0 || col >= SIZE)
    {
      System.out.printf ("Shape table out of range: %d, %d%n", row, col);
      return false;
    }

    grid[row][col] = 1;       // plot

    minX = Math.min (col, minX);
    minY = Math.min (row, minY);
    maxX = Math.max (col, maxX);
    maxY = Math.max (row, maxY);

    return true;
  }

  // ---------------------------------------------------------------------------------//
  public void drawText (StringBuilder text)
  // ---------------------------------------------------------------------------------//
  {
    text.append (String.format ("Shape  : %d%n", index));
    text.append (String.format ("Size   : %d%n", actualLength));
    //      text.append (String.format ("Width  : %d%n", width));
    //      text.append (String.format ("Height : %d%n", height));

    // append the shape's data
    int offset = shapeOffset;
    for (String hexLine : split (
        HexFormatter.getHexString (buffer, shapeOffset, actualLength)))
    {
      text.append (String.format ("  %04X : %s%n", offset, hexLine));
      offset += 16;
    }
    text.append ("\n");

    // append the shape
    for (int row = 0; row < displayGrid.length; row++)
    {
      for (int col = 0; col < displayGrid[0].length; col++)
        if (col == startCol && row == startRow)
          text.append (displayGrid[row][col] > 0 ? " @" : " .");
        else if (displayGrid[row][col] == 0)
          text.append ("  ");
        else
          text.append (" X");

      text.append ("\n");
    }

    //    text.append ("\n");
  }

  // ---------------------------------------------------------------------------------//
  private List<String> split (String line)
  // ---------------------------------------------------------------------------------//
  {
    List<String> list = new ArrayList<> ();
    while (line.length () > 48)
    {
      list.add (line.substring (0, 47));
      line = line.substring (48);
    }
    list.add (line);
    return list;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%3d  %3d  %3d  %3d  %3d", index, minY, maxY, minX, maxX);
  }
}

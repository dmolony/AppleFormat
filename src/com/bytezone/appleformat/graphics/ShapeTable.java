package com.bytezone.appleformat.graphics;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/*-
 *  Offset     Meaning
 *    0     # of shapes
 *    2     offset to shape #1 (S1)
 *    4     offset to shape #2 (S2)
 *    S1    shape definition #1
 *    S1+n  last byte = 0
 *    S2    shape definition #1
 *    S2+n  last byte = 0
 */

// -----------------------------------------------------------------------------------//
public class ShapeTable extends Graphics
// -----------------------------------------------------------------------------------//
{
  private final List<Integer> index;
  private final List<Shape> shapes;

  private final int maxShapeWidth;
  private final int maxShapeHeight;

  // ---------------------------------------------------------------------------------//
  public ShapeTable (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);

    int minY = Shape.ORIGIN;
    int minX = Shape.ORIGIN;
    int maxY = Shape.ORIGIN;
    int maxX = Shape.ORIGIN;

    int totalShapes = Utility.getShort (buffer, offset);
    index = new ArrayList<> (totalShapes);
    shapes = new ArrayList<> (totalShapes);

    for (int i = 0; i < totalShapes; i++)
    {
      int indexOffset = offset + i * 2 + 2;
      int shapeOffset = offset + Utility.getShort (buffer, indexOffset);

      Shape shape = new Shape (buffer, shapeOffset, offset + length, i);

      if (!shape.valid)
      {
        System.out.println ("Warning: shape ignored for plotting outside grid");
        continue;
      }

      index.add (shapeOffset);
      shapes.add (shape);

      minY = Math.min (minY, shape.minY);
      minX = Math.min (minX, shape.minX);
      maxY = Math.max (maxY, shape.maxY);
      maxX = Math.max (maxX, shape.maxX);
    }

    maxShapeHeight = maxY - minY + 1;
    maxShapeWidth = maxX - minX + 1;

    for (Shape shape : shapes)
      shape.convertGrid (minY, minX,              // offset coordinates
          maxShapeHeight, maxShapeWidth);         // dimensions of new grid
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    int cols = (int) Math.sqrt (shapes.size ()) + 1;
    int rows = (shapes.size () - 1) / cols + 1;

    int gapPixels = 2;
    int inset = 10;

    WritableImage image = new WritableImage ( //
        inset * 2 + cols * (maxShapeWidth + gapPixels) - gapPixels,
        inset * 2 + rows * (maxShapeHeight + gapPixels) - gapPixels);

    PixelWriter pw = image.getPixelWriter ();

    int x = inset;
    int y = inset;
    int count = 0;

    for (Shape shape : shapes)
    {
      shape.draw (pw, x, y);

      x += (maxShapeWidth + gapPixels);

      if (++count % cols == 0)
      {
        x = inset;
        y += (maxShapeHeight + gapPixels);
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

    text.append (String.format ("Total shapes   : %d%n", shapes.size ()));
    text.append (
        String.format ("Max dimensions : %d x %d%n%n", maxShapeWidth, maxShapeHeight));

    for (Shape shape : shapes)
    {
      shape.drawText (text);
      text.append ("\n");
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isShapeTable (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    if (length < 2)
      return false;

    int totalShapes = Utility.getSignedShort (buffer, offset);
    if (totalShapes <= 0 || totalShapes > 255)
      return false;

    int min = offset + totalShapes * 2 + 2;       // skip index
    int max = offset + length;

    if (min >= max)
      return false;

    // this flags large files that start with a very small value
    if (totalShapes * 500 < max)
      return false;

    int lo = 99999;
    int hi = 0;

    // check each index entry points inside the file (and after the index)
    for (int i = 0; i < totalShapes; i++)
    {
      int ptr = offset + Utility.getShort (buffer, offset + i * 2 + 2);
      //      System.out.printf ("%3d  %04X  %04X  %04X%n", i, min, ptr, max);
      if (ptr < min || ptr >= max)
        return false;

      if (ptr < lo)
        lo = ptr;
      if (ptr > hi)
        hi = ptr;
    }

    //    System.out.printf ("lo: %04X, hi: %04X%n", lo, hi);
    int unusedLo = lo - min;
    int unusedHi = max - hi;
    //    System.out.printf ("Unused: %d, %d%n", unusedLo, unusedHi);

    if (unusedLo + unusedHi > 200)
      return false;

    // nibble0489.po SMALL.LETTERS is clearly a shape table, 
    // but some index entries are invalid

    return true;
  }
}
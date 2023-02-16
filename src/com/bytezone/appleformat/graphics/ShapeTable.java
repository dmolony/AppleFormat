package com.bytezone.appleformat.graphics;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.appleformat.Utility;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
public class ShapeTable extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  private final List<Integer> index;
  private final List<Shape> shapes;

  private final int maxShapeWidth;
  private final int maxShapeHeight;

  // ---------------------------------------------------------------------------------//
  public ShapeTable (String name, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer, offset, length);

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
  public void writeGraphics (GraphicsContext gc)
  // ---------------------------------------------------------------------------------//
  {
    int cols = (int) Math.sqrt (shapes.size ());
    int rows = (shapes.size () - 1) / cols + 1;
    int pixelSize = 6;

    Canvas canvas = gc.getCanvas ();

    canvas.setHeight (rows * (pixelSize * maxShapeHeight + 10));
    canvas.setWidth (cols * (pixelSize * maxShapeHeight + 10));

    gc.setFill (Color.WHITE);
    gc.fillRect (0, 0, canvas.getWidth (), canvas.getHeight ());
    gc.setFill (Color.BLACK);

    int x = 10;
    int y = 10;
    int count = 0;

    for (Shape shape : shapes)
    {
      shape.draw (gc, x, y);

      x += (maxShapeWidth + 1) * pixelSize;

      if (++count % cols == 0)
      {
        x = 10;
        y += (maxShapeHeight + 1) * pixelSize;
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Total shapes   : %d%n", shapes.size ()));
    text.append (String.format ("Max dimensions : %d x %d%n%n", maxShapeWidth, maxShapeHeight));

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
    if (totalShapes <= 0)
      return false;

    int min = offset + totalShapes * 2 + 2;       // skip index
    int max = offset + length;

    if (min >= max)
      return false;

    // this flags large files that start with a very small value
    if (totalShapes * 500 < max)
      return false;

    // check each index entry points inside the file (and after the index)
    for (int i = 0; i < totalShapes; i++)
    {
      int ptr = offset + Utility.getShort (buffer, offset + i * 2 + 2);
      //      System.out.printf ("%3d  %04X  %04X  %04X%n", i, min, ptr, max);
      if (ptr < min || ptr >= max)
        return false;
    }

    // nibble0489.po SMALL.LETTERS is clearly a shape table, but some index entries are invalid

    return true;
  }
}
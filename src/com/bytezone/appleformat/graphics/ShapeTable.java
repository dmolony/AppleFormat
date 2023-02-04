package com.bytezone.appleformat.graphics;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.AbstractFormattedAppleFile;
import com.bytezone.appleformat.Utility;

/*-
 *  Offset     Meaning
 *    0        # of shapes
 *    1        unused
 *    2-3      offset to shape #1 (S1)
 *    3-4      offset to shape #2 (S2)
 *    S1-S1+1  shape definition #1
 *    S1+n     last byte = 0
 *    S2-S2+1  shape definition #1
 *    S2+n     last byte = 0
 */

// -----------------------------------------------------------------------------------//
public class ShapeTable extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  private final List<Shape> shapes = new ArrayList<> ();
  int maxWidth = 0;
  int maxHeight = 0;

  // ---------------------------------------------------------------------------------//
  public ShapeTable (String name, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (name, buffer);

    int minRow = 200;
    int minCol = 200;
    int maxRow = 200;
    int maxCol = 200;

    int totalShapes = Utility.getShort (buffer, offset);
    for (int i = 0; i < totalShapes; i++)
    {
      Shape shape = new Shape (buffer, offset, length, i);
      if (!shape.valid)
        continue;                   // shape table should be abandoned

      shapes.add (shape);

      minRow = Math.min (minRow, shape.minRow);
      minCol = Math.min (minCol, shape.minCol);
      maxRow = Math.max (maxRow, shape.maxRow);
      maxCol = Math.max (maxCol, shape.maxCol);
    }

    maxHeight = maxRow - minRow + 1;
    maxWidth = maxCol - minCol + 1;

    for (Shape shape : shapes)
      shape.convertGrid (minRow, minCol, maxHeight, maxWidth);

    int cols = (int) Math.sqrt (shapes.size ());
    int rows = (shapes.size () - 1) / cols + 1;

    //    image = new BufferedImage ((cols + 1) * (maxWidth + 5), (rows + 1) * (maxHeight + 5),
    //        BufferedImage.TYPE_BYTE_GRAY);
    //
    //    int x = 10;
    //    int y = 10;
    //    int count = 0;
    //    Graphics2D g2d = image.createGraphics ();
    //    g2d.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, (float) 1.0));
    //
    //    for (Shape shape : shapes)
    //    {
    //      g2d.drawImage (shape.image, x, y, null);
    //      x += maxWidth + 5;
    //      if (++count % cols == 0)
    //      {
    //        x = 10;
    //        y += maxHeight + 5;
    //      }
    //    }
    //    g2d.dispose ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("File Name      : %s%n", name));
    text.append (String.format ("File size      : %,d%n", buffer.length));
    text.append (String.format ("Total shapes   : %d%n", shapes.size ()));
    text.append (String.format ("Max dimensions : %d x %d%n%n", maxWidth, maxHeight));

    for (Shape shape : shapes)
    {
      shape.drawText (text);
      text.append ("\n");
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isShapeTable (byte[] buffer, int bufferOffset, int bufferLength)
  // ---------------------------------------------------------------------------------//
  {
    if (bufferLength < 2)
      return false;

    int totalShapes = Utility.getSignedShort (buffer, bufferOffset);
    if (totalShapes <= 0)
      return false;

    int min = bufferOffset + totalShapes * 2 + 2;       // skip index
    int max = bufferOffset + bufferLength;

    if (min >= max)
      return false;

    // this flags large files that start with a very small value
    if (totalShapes * 500 < max)
      return false;

    // check each index entry points inside the file (and after the index)
    for (int i = 0; i < totalShapes; i++)
    {
      int ptr = bufferOffset + Utility.getShort (buffer, bufferOffset + i * 2 + 2);
      if (ptr < min || ptr >= max)
        return false;
    }

    return true;
  }
}
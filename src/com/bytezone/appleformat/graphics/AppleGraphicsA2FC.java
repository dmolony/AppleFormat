package com.bytezone.appleformat.graphics;

import com.bytezone.appleformat.HexFormatter;
import com.bytezone.filesystem.AppleFile;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// FOT/$08, PIC ($C1/$0000), files with ".A2FC",  ".A2FM", ".A2LC", and ".A2HR" ??

//$06 (BIN) aux $2000 .A2FC
//$06 (BIN) aux ?     .PAC
// -----------------------------------------------------------------------------------//
public class AppleGraphicsA2FC extends Graphics
// -----------------------------------------------------------------------------------//
{
  private static int[] swap = { 0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15 };

  private final byte[] primaryBuffer;
  private final byte[] auxBuffer;

  private int primaryBufferOffset;
  private int auxBufferOffset;

  // ---------------------------------------------------------------------------------//
  //  public AppleGraphicsA2FC (AppleFile appleFile, byte[] buffer, byte[] auxBuffer)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    super (appleFile, buffer);
  //
  //    primaryBuffer = buffer;
  //    this.auxBuffer = Objects.requireNonNull (auxBuffer);
  //
  //    buildImage ();
  //  }

  // ---------------------------------------------------------------------------------//
  public AppleGraphicsA2FC (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    byte[] buffer = dataRecord.data ();
    int offset = dataRecord.offset ();
    int length = dataRecord.length ();

    if (name.endsWith (".PAC"))
    {
      DoubleScrunch doubleScrunch = new DoubleScrunch (buffer);     // fix this
      auxBuffer = doubleScrunch.memory[0];
      primaryBuffer = doubleScrunch.memory[1];
    }
    else if (name.endsWith (".A2FC"))
    {
      auxBuffer = buffer;
      primaryBuffer = buffer;
      primaryBufferOffset = 0x2000;
    }
    else
      throw new IllegalArgumentException ("Filename not .PAC or .A2FC : " + name);

    buildImage ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    //    if (image == null)
    //      image = createColourImage ();
    //
    //    return image;
    return createColourImage ();
  }

  // ---------------------------------------------------------------------------------//
  private Image createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    Palette palette = paletteFactory.getCurrentPalette ();
    Color[] colours = palette.getColours ();

    // image will be doubled horizontally
    WritableImage image = new WritableImage (140 * 2, 192);
    PixelWriter pixelWriter = image.getPixelWriter ();

    int y = 0;

    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 8; j++)
        for (int k = 0; k < 8; k++)
        {
          int base = i * 0x28 + j * 0x80 + k * 0x400;

          int primaryPtr = primaryBufferOffset + base;
          int auxPtr = auxBufferOffset + base;

          int x = 0;

          for (int count = 0; count < 20; count++)
          {
            // remove high bit from 4 bytes
            int v1 = auxBuffer[auxPtr++] & 0x7F;
            int v2 = primaryBuffer[primaryPtr++] & 0x7F;
            int v3 = auxBuffer[auxPtr++] & 0x7F;
            int v4 = primaryBuffer[primaryPtr++] & 0x7F;

            // create 28 consecutive bits
            int v5 = v1 | v2 << 7 | v3 << 14 | v4 << 21;

            // loop through 4 bits at a time
            for (int px = 0; px < 7; px++)
            {
              int val = swap[v5 & 0x0F];
              v5 >>>= 4;

              pixelWriter.setColor (x++, y, colours[val]);
              pixelWriter.setColor (x++, y, colours[val]);
            }
          }
          y++;
        }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    //    if (packedBuffer != null)
    //    {
    //      text.append ("Packed buffer:\n\n");
    //      text.append (HexFormatter.format (packedBuffer));
    //    }

    text.append ("\n\nAuxilliary buffer:\n\n");
    text.append (HexFormatter.format (auxBuffer));

    text.append ("\n\nPrimary buffer:\n\n");
    text.append (HexFormatter.format (primaryBuffer));

    return text.toString ();
  }
}
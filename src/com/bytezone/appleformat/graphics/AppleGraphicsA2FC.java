package com.bytezone.appleformat.graphics;

import java.util.Objects;

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

  private DoubleScrunch doubleScrunch;
  byte[] packedBuffer;
  int paletteIndex;

  // ---------------------------------------------------------------------------------//
  public AppleGraphicsA2FC (AppleFile appleFile, byte[] buffer, byte[] auxBuffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    primaryBuffer = buffer;
    this.auxBuffer = Objects.requireNonNull (auxBuffer);

    buildImage ();
  }

  // ---------------------------------------------------------------------------------//
  public AppleGraphicsA2FC (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    if (name.endsWith (".PAC"))
    {
      packedBuffer = buffer;
      doubleScrunch = new DoubleScrunch (buffer);
      auxBuffer = doubleScrunch.memory[0];
      primaryBuffer = doubleScrunch.memory[1];
    }
    else if (name.endsWith (".A2FC"))
    {
      auxBuffer = new byte[0x2000];
      primaryBuffer = new byte[0x2000];

      System.arraycopy (buffer, 0, auxBuffer, 0, 0x2000);
      System.arraycopy (buffer, 0x2000, this.buffer, 0, 0x2000);
    }
    else
      throw new UnsupportedOperationException ("Filename not .PAC or .A2FC : " + name);

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
  private Image createMonochromeImage ()
  // ---------------------------------------------------------------------------------//
  {
    // image will be doubled vertically
    int WIDTH = 280 * 2;
    int HEIGHT = 192 * 2;

    //    image = new BufferedImage (WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
    //    DataBuffer dataBuffer = image.getRaster ().getDataBuffer ();
    WritableImage image = new WritableImage (WIDTH, HEIGHT);
    PixelWriter pw = image.getPixelWriter ();
    int ndx = 0;

    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 8; j++)
        for (int k = 0; k < 8; k++)
        {
          int base = i * 0x28 + j * 0x80 + k * 0x400;
          int max = Math.min (base + 40, buffer.length);

          for (int ptr = base; ptr < max; ptr += 2)
          {
            int value = auxBuffer[ptr] & 0x7F | ((buffer[ptr] & 0x7F) << 7)
                | ((auxBuffer[ptr + 1] & 0x7F) << 14) | ((buffer[ptr + 1] & 0x7F) << 21);
            for (int px = 0; px < 28; px++)
            {
              int val = (value >> px) & 0x01;
              int pixel = val == 0 ? 0 : 255;
              //  dataBuffer.setElem (ndx, pixel);
              //  dataBuffer.setElem (ndx + WIDTH, pixel);  // repeat pixel one line on
              ++ndx;
            }
          }
          ndx += WIDTH;                                 // skip past repeated line
        }

    return image;
  }

  // ---------------------------------------------------------------------------------//
  private Image createColourImage ()
  // ---------------------------------------------------------------------------------//
  {
    paletteIndex = paletteFactory.getCurrentPaletteIndex ();
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
          int max = Math.min (base + 40, buffer.length);

          int x = 0;

          for (int ptr = base; ptr < max; ptr += 2)       // 20 times
          {
            // remove high bit from 4 bytes
            int v1 = auxBuffer[ptr] & 0x7F;
            int v2 = buffer[ptr] & 0x7F;
            int v3 = auxBuffer[ptr + 1] & 0x7F;
            int v4 = buffer[ptr + 1] & 0x7F;

            // smoosh them together
            int v5 = v1 | (v2 << 7) | (v3 << 14) | (v4 << 21);

            // loop through 4 bits at a time
            for (int px = 0; px < 28; px += 4)            // 7 times
            {
              int val = (v5 >>> px) & 0x0F;
              int val2 = swap[val];
              pixelWriter.setColor (x++, y, colours[val2]);
              pixelWriter.setColor (x++, y, colours[val2]);
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

    if (packedBuffer != null)
    {
      text.append ("Packed buffer:\n\n");
      text.append (HexFormatter.format (packedBuffer));
    }

    text.append ("\n\nAuxilliary buffer:\n\n");
    text.append (HexFormatter.format (auxBuffer));

    text.append ("\n\nPrimary buffer:\n\n");
    text.append (HexFormatter.format (primaryBuffer));

    return text.toString ();
  }
}
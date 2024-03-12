package com.bytezone.appleformat.graphics;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class Animation extends Pic0000
// -----------------------------------------------------------------------------------//
{
  List<Integer> framePointers = new ArrayList<> ();
  int frameNumber;
  int delay;

  // ---------------------------------------------------------------------------------//
  public Animation (AppleFile appleFile, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer);

    //    int len = HexFormatter.getLong (buffer, 0x8000);
    delay = Utility.getLong (buffer, 0x8004);
    if (delay > 60)
      delay = 10;
    //    delay = delay * 1000 / 60;

    //    int offset = HexFormatter.getLong (buffer, 0x8008);
    //    int blockLen = eof - 0x8008;

    //    System.out.printf ("Delay: %,d%n", delay);
    //    System.out.printf ("Blocklen: %,d%n", blockLen);
    //    System.out.printf ("Offset: %,d%n", offset);
    //    System.out.printf ("Len: %,d%n", len);
    //    System.out.println ();
    int ptr = 0x800C;

    int start = ptr;
    while (ptr < appleFile.getFileLength ())
    {
      int off = Utility.getShort (buffer, ptr);

      ptr += 4;
      if (off == 0)
      {
        framePointers.add (start);
        start = ptr;
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  public void nextFrame ()
  // ---------------------------------------------------------------------------------//
  {
    int ptr = framePointers.get (frameNumber++);
    frameNumber %= framePointers.size ();

    while (true)
    {
      int offset = Utility.getShort (buffer, ptr);
      if (offset == 0)
        break;

      buffer[offset] = buffer[ptr + 2];
      buffer[offset + 1] = buffer[ptr + 3];

      ptr += 4;
    }

    buildImage ();
  }

  // ---------------------------------------------------------------------------------//
  public int getDelay ()
  // ---------------------------------------------------------------------------------//
  {
    return delay;
  }

  // ---------------------------------------------------------------------------------//
  public int getSize ()
  // ---------------------------------------------------------------------------------//
  {
    return framePointers.size ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.buildText ());

    text.append ("\n\n");
    text.append ("Delay ............ %d%n".formatted (delay));
    text.append ("Frames ........... %d%n".formatted (framePointers.size ()));

    return text.toString ();
  }
}

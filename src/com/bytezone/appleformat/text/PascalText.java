package com.bytezone.appleformat.text;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class PascalText extends Text
// -----------------------------------------------------------------------------------//
{
  private final static int PAGE_SIZE = 1024;

  // ---------------------------------------------------------------------------------//
  public PascalText (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    // Text files are broken up into 1024-byte pages.
    //    [DLE] [indent] [text] [CR] ... [nulls]

    StringBuilder text = new StringBuilder ();

    byte[] buffer = dataBuffer.data ();
    int ptr = dataBuffer.offset () + PAGE_SIZE;         // skip text editor header

    while (ptr < dataBuffer.max ())
    {
      if (buffer[ptr] == 0x00)                          // padding to page boundary
      {
        ptr = (ptr / PAGE_SIZE + 1) * PAGE_SIZE;        // skip to next page
        continue;
      }

      if (buffer[ptr] == 0x10)                          // Data Link Escape code
      {
        int tab = (buffer[ptr + 1] & 0xFF) - 32;        // indent amaount
        while (tab-- > 0)
          text.append (" ");
        ptr += 2;
      }

      while (buffer[ptr] != 0x0D)
        text.append ((char) buffer[ptr++]);

      text.append ("\n");
      ptr++;
    }

    return Utility.rtrim (text);
  }
}
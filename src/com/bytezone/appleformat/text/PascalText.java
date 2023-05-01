package com.bytezone.appleformat.text;

import com.bytezone.filesystem.AppleFile;

// -----------------------------------------------------------------------------------//
public class PascalText extends Text
// -----------------------------------------------------------------------------------//
{
  private final static int PAGE_SIZE = 1024;

  // ---------------------------------------------------------------------------------//
  public PascalText (AppleFile file, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    super (file, buffer, 0, buffer.length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    // Text files are broken up into 1024-byte pages.
    //    [DLE] [indent] [text] [CR] ... [nulls]

    StringBuilder text = new StringBuilder ();

    int ptr = PAGE_SIZE;                                // skip text editor header

    while (ptr < buffer.length)
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

    if (text.length () > 0)
      text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }
}
package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;

// -----------------------------------------------------------------------------------//
public abstract class AbstractFormattedAppleBlock implements FormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  protected final AppleBlock appleBlock;

  // ---------------------------------------------------------------------------------//
  public AbstractFormattedAppleBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    this.appleBlock = appleBlock;
  }

  // ---------------------------------------------------------------------------------//
  protected StringBuilder getHeader (String title)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (title + "\n\n");
    text.append ("Offset    Value         Description\n");
    text.append ("=======   ===========   "
        + "===============================================================\n");

    return text;
  }

  // ---------------------------------------------------------------------------------//
  protected void addText (StringBuilder text, byte[] buffer, int offset, int size,
      String desc)
  // ---------------------------------------------------------------------------------//
  {
    if ((offset + size - 1) > buffer.length)
      return;

    switch (size)
    {
      case 1:
        text.append (String.format ("%03X       %02X            %s%n", offset,
            buffer[offset], desc));
        break;
      case 2:
        text.append (String.format ("%03X-%03X   %02X %02X         %s%n", offset,
            offset + 1, buffer[offset], buffer[offset + 1], desc));
        break;
      case 3:
        text.append (String.format ("%03X-%03X   %02X %02X %02X      %s%n", offset,
            offset + 2, buffer[offset], buffer[offset + 1], buffer[offset + 2], desc));
        break;
      case 4:
        text.append (String.format ("%03X-%03X   %02X %02X %02X %02X   %s%n", offset,
            offset + 3, buffer[offset], buffer[offset + 1], buffer[offset + 2],
            buffer[offset + 3], desc));
        break;
      default:
        System.out.println ("Invalid length : " + size);
    }
  }

  // ---------------------------------------------------------------------------------//
  protected void addTextAndDecimal (StringBuilder text, byte[] b, int offset, int size,
      String desc)
  // ---------------------------------------------------------------------------------//
  {
    desc += switch (size)
    {
      case 1 -> " (" + (b[offset] & 0xFF) + ")";
      case 2 -> String.format (" (%,d)", Utility.getShort (b, offset));
      case 3 -> String.format (" (%,d)", Utility.readTriple (b, offset));
      default -> "";
    };

    addText (text, b, offset, size, desc);
  }
}

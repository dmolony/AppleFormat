package com.bytezone.appleformat.block;

import com.bytezone.appleformat.AbstractFormattedAppleBlock;
import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.AppleBlock.BlockType;
import com.bytezone.filesystem.FsDos;

// -----------------------------------------------------------------------------------//
public class Vtoc extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public Vtoc (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleBlock.read ();

    int maxTracks = buffer[52] & 0xFF;
    int maxSectors = buffer[53] & 0xFF;

    FsDos fs = (FsDos) appleBlock.getFileSystem ();

    StringBuilder text = getHeader ("DOS VTOC Sector");
    addText (text, buffer, 0, 1, "Not used");
    addText (text, buffer, 1, 2, "First directory track/sector");
    addText (text, buffer, 3, 1, "DOS release number");
    addText (text, buffer, 4, 2, "Not used");
    addTextAndDecimal (text, buffer, 6, 1, "Diskette volume");
    addText (text, buffer, 7, 4, "Not used");
    addText (text, buffer, 11, 4, "Not used");
    addText (text, buffer, 15, 4, "Not used");
    addText (text, buffer, 19, 4, "Not used");
    addText (text, buffer, 23, 4, "Not used");
    addText (text, buffer, 27, 4, "Not used");
    addText (text, buffer, 31, 4, "Not used");
    addText (text, buffer, 35, 4, "Not used");
    addTextAndDecimal (text, buffer, 39, 1, "Maximum TS pairs");
    addText (text, buffer, 40, 4, "Not used");
    addText (text, buffer, 44, 4, "Not used");
    addTextAndDecimal (text, buffer, 48, 1, "Last allocated track");
    addText (text, buffer, 49, 1, "Direction to look when allocating the next file");
    addText (text, buffer, 50, 2, "Not used");

    addTextAndDecimal (text, buffer, 52, 1, "Maximum tracks");

    if (maxTracks != fs.getTracksPerDisk ())
    {
      text.deleteCharAt (text.length () - 1);
      text.append (String.format ("            <-- Should be 0x%02X !!%n",
          fs.getTracksPerDisk ()));
    }

    addTextAndDecimal (text, buffer, 53, 1, "Maximum sectors");
    addTextAndDecimal (text, buffer, 54, 2, "Bytes per sector");

    AppleBlock bootSector = fs.getSector (0, 0);
    int firstSector = 0x38;
    int max = maxTracks * 4 + firstSector;

    for (int i = firstSector; i < max; i += 4)
    {
      String extra = "";
      if (i == firstSector && bootSector.getBlockType () == BlockType.EMPTY)
        extra = "(unusable)";
      String bits = getBitmap (buffer, i, maxSectors);
      int track = (i - firstSector) / 4;
      addText (text, buffer, i, 4,
          String.format ("Track %02X  %s  %s", track, bits, extra));
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  private String getBitmap (byte[] buffer, int offset, int maxSectors)
  // ---------------------------------------------------------------------------------//
  {
    int value = Utility.getLongBigEndian (buffer, offset);

    String bits = "0000000000000000000000000000000" + Integer.toBinaryString (value);
    bits = bits.substring (bits.length () - 32);
    bits = bits.substring (0, maxSectors);
    bits = bits.replace ('0', 'X');
    bits = bits.replace ('1', '.');

    return new StringBuilder (bits).reverse ().toString ();
  }
}

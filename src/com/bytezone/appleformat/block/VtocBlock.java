package com.bytezone.appleformat.block;

import static com.bytezone.filesystem.AppleFileSystem.FileSystemType.DOS4;

import java.time.LocalDateTime;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.AppleBlock.BlockType;
import com.bytezone.filesystem.FsDos;;

// -----------------------------------------------------------------------------------//
public class VtocBlock extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public VtocBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = appleBlock.getBuffer ();

    int maxTracks = buffer[52] & 0xFF;
    int maxSectors = buffer[53] & 0xFF;

    FsDos fs = (FsDos) appleBlock.getFileSystem ();
    boolean dos4 = fs.getFileSystemType () == DOS4;

    StringBuilder text = getHeader (fs.getFileSystemType () + " VTOC Sector");

    if (dos4)
      addText (text, buffer, 0, 1, "VTOC structure");
    else
      addText (text, buffer, 0, 1, "Not used");

    addText (text, buffer, 1, 2, "First directory track/sector");
    addText (text, buffer, 3, 1, "DOS release number");

    if (dos4)
    {
      addText (text, buffer, 4, 1, "Build number");
      addText (text, buffer, 5, 1,
          String.format ("RAM Dos (%s)", (char) (buffer[5] & 0x7F)));
    }
    else
      addText (text, buffer, 4, 2, "Not used");

    addTextAndDecimal (text, buffer, 6, 1, "Diskette volume");

    if (dos4)
    {
      addText (text, buffer, 7, 1,
          String.format ("Volume type (%s)", (char) (buffer[7] & 0x7F)));
      addText (text, buffer, 8, 4, "Volume name : " + Utility.string (buffer, 8, 24));
      addText (text, buffer, 12, 4, "");
      addText (text, buffer, 16, 4, "");
      addText (text, buffer, 20, 4, "");
      addText (text, buffer, 24, 4, "");
      addText (text, buffer, 28, 4, "");
      LocalDateTime initTime = Utility.getDos4LocalDateTime (buffer, 32);
      addText (text, buffer, 32, 4,
          "Initialised : " + initTime == null ? "" : initTime.toString ());
      addText (text, buffer, 36, 2, "");
      addText (text, buffer, 38, 1, "Not used");
    }
    else
    {
      addText (text, buffer, 7, 4, "Not used");
      addText (text, buffer, 11, 4, "Not used");
      addText (text, buffer, 15, 4, "Not used");
      addText (text, buffer, 19, 4, "Not used");
      addText (text, buffer, 23, 4, "Not used");
      addText (text, buffer, 27, 4, "Not used");
      addText (text, buffer, 31, 4, "Not used");
      addText (text, buffer, 35, 4, "Not used");
    }

    addTextAndDecimal (text, buffer, 39, 1, "Maximum TS pairs");

    if (dos4)
    {
      int volumeLibrary = Utility.getShort (buffer, 40);
      LocalDateTime vtocTime = Utility.getDos4LocalDateTime (buffer, 42);

      addText (text, buffer, 40, 2, "Volume library " + volumeLibrary);
      addText (text, buffer, 42, 4,
          "Modified : " + vtocTime == null ? "" : vtocTime.toString ());
      addText (text, buffer, 46, 2, "");
    }
    else
    {
      addText (text, buffer, 40, 4, "Not used");
      addText (text, buffer, 44, 4, "Not used");
    }

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

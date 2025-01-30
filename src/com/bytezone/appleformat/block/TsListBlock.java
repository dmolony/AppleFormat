package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.AppleFileSystem.FileSystemType;

// -----------------------------------------------------------------------------------//
public class TsListBlock extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public TsListBlock (AppleBlock appleBlock)
  // ---------------------------------------------------------------------------------//
  {
    super (appleBlock);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    FileSystemType fileSystemType = appleBlock.getFileSystem ().getFileSystemType ();
    byte[] buffer = appleBlock.getBuffer ();

    StringBuilder text = getHeader (fileSystemType + " Track/Sector List : "
        + appleBlock.getFileOwner ().getFileName ());

    int nextTrack = buffer[1] & 0xFF;
    int nextSector = buffer[2] & 0xFF;

    String msg =
        appleBlock.getTrackNo () == nextTrack && appleBlock.getSectorNo () == nextSector
            ? " (circular reference)" : "";

    addText (text, buffer, 0, 1, "Not used");
    addText (text, buffer, 1, 2, "Next TS list track/sector" + msg);

    //    if ((buffer[3] != 0 || buffer[4] != 0)         // not supposed to be used
    //        // Diags2E.dsk stores its own sector address here
    //        && (diskAddress.getTrackNo () == (buffer[3] & 0xFF)
    //            && diskAddress.getSectorNo () == (buffer[4] & 0xFF)))
    //      addText (text, buffer, 3, 2, "Self-reference");
    //    else
    addText (text, buffer, 3, 2, "Not used");

    addTextAndDecimal (text, buffer, 5, 2, "Sector base number");
    addText (text, buffer, 7, 4, "Not used");
    addText (text, buffer, 11, 1, "Not used");

    int blockNo = Utility.getShort (buffer, 5);

    for (int i = 12; i <= 255; i += 2)
    {
      if (buffer[i] == 0 && buffer[i + 1] == 0)
        msg = "";
      else
      {
        String msg2 = fileSystemType == FileSystemType.DOS4 && (buffer[i] & 0x40) != 0
            ? "  - track zero" : "";
        msg = String.format ("Track/sector of file sector %04X (%<d)%s", blockNo, msg2);
      }
      blockNo++;
      addText (text, buffer, i, 2, msg);
    }

    return Utility.rtrim (text);
  }
}

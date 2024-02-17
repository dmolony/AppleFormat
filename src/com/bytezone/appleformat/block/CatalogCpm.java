package com.bytezone.appleformat.block;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleBlock;

// -----------------------------------------------------------------------------------//
public class CatalogCpm extends AbstractFormattedAppleBlock
// -----------------------------------------------------------------------------------//
{
  private static int CATALOG_ENTRY_SIZE = 32;
  private static final byte EMPTY = (byte) 0xE5;

  // ---------------------------------------------------------------------------------//
  public CatalogCpm (AppleBlock appleBlock)
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
    //    FsCpm fs = (FsCpm) appleBlock.getFileSystem ();

    StringBuilder text = getHeader ("CPM Catalog Sector");

    for (int i = 0; i < buffer.length; i += CATALOG_ENTRY_SIZE)
    {
      if (buffer[i] == EMPTY && buffer[i + 1] == EMPTY)
        break;

      int userNumber = buffer[i] & 0xFF;
      if (userNumber > 31 && userNumber != EMPTY)
        break;

      boolean readOnly = (buffer[i + 9] & 0x80) != 0;
      boolean systemFile = (buffer[i + 10] & 0x80) != 0;
      boolean unknown = (buffer[i + 11] & 0x80) != 0;
      String type;
      String extra;

      if (readOnly || systemFile || unknown)
      {
        byte[] typeBuffer = new byte[3];
        typeBuffer[0] = (byte) (buffer[i + 9] & 0x7F);
        typeBuffer[1] = (byte) (buffer[i + 10] & 0x7F);
        typeBuffer[2] = (byte) (buffer[i + 11] & 0x7F);
        type = new String (typeBuffer).trim ();
        extra = String.format (" (%s%s%s)", readOnly ? "read only" : "",
            systemFile ? "system file" : "", unknown ? "unknown" : "");
      }
      else
      {
        type = new String (buffer, i + 9, 3).trim ();
        extra = "";
      }

      if (buffer[i] == (byte) 0xE5)
        addText (text, buffer, i, 1, "Deleted file?");
      else
        addText (text, buffer, i, 1, "User number");

      if (buffer[i + 1] == 0)
        addText (text, buffer, i + 1, 4, "File name : ");
      else
        addText (text, buffer, i + 1, 4, "File name : " + new String (buffer, i + 1, 8));

      addText (text, buffer, i + 5, 4, "");
      addText (text, buffer, i + 9, 3, "File type : " + type + extra);
      addText (text, buffer, i + 12, 1, "Extent counter LO");
      addText (text, buffer, i + 13, 1, "Reserved");
      addText (text, buffer, i + 14, 1, "Extent counter HI");
      addText (text, buffer, i + 15, 1, "Record count");

      for (int j = 0; j < 4; j++)
        addText (text, buffer, i + 16 + j * 4, 4, "");

      text.append ("\n");
    }

    return Utility.rtrim (text);
  }
}

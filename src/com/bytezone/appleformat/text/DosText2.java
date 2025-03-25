package com.bytezone.appleformat.text;

import java.util.List;

import com.bytezone.filesystem.FileDos;
import com.bytezone.filesystem.TextBlock;
import com.bytezone.filesystem.TextBlock.TextRecord;
import com.bytezone.filesystem.TextBlockDos;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class DosText2 extends Text
// -----------------------------------------------------------------------------------//
{
  private static String underline = "------------------------------------------"
      + "------------------------------------\n";
  private static String fullUnderline = "----------  --------  " + underline;

  boolean showTextOffsets = true;

  // ---------------------------------------------------------------------------------//
  public DosText2 (FileDos appleFile, List<? extends TextBlock> textBlocks)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, textBlocks);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (appleFile.getCatalogLine ());
    text.append ("\n\n");

    if (showTextOffsets)
    {
      text.append ("    Offset   Record#  Text values\n");
      text.append (fullUnderline);
    }
    else
    {
      text.append ("Text values\n");
      text.append (underline);
    }

    for (TextBlock textBlock : textBlocks)
    {
      byte[] buffer = textBlock.getBuffer ();
      int recordLength = ((TextBlockDos) textBlock).getProbableRecordLength ();

      for (TextRecord record : textBlock)
      {
        int offset = record.offset () + textBlock.getStartByte ();
        int recordNo = recordLength == 0 ? 0 : offset / recordLength;

        text.append (
            String.format (" %9d %9d  %s", offset, recordNo, getData (buffer, record)));
        text.append ("\n");
      }
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  private String getData (byte[] buffer, TextRecord record)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int ptr = record.offset ();
    int length = record.length ();
    while (length-- > 0)
    {
      int value = buffer[ptr++] & 0x7F;
      if (value == 0x0D)
      {
        text.append ((char) 0x2C);
        text.append ((char) 0x20);
      }
      else
        text.append ((char) value);
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected List<String> buildHex ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    int count = 0;

    for (TextBlock textBlock : textBlocks)
    {
      text.append (String.format ("Text Block #%d%n%n", count++));
      byte[] buffer = textBlock.getBuffer ();
      text.append (
          Utility.format (buffer, 0, buffer.length, true, textBlock.getStartByte ()));
      text.append ("\n\n");
    }

    return List.of (text.toString ().split ("\n"));
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (TextBlock textBlock : textBlocks)
      text.append (textBlock);

    return Utility.rtrim (text);
  }
}

package com.bytezone.appleformat.text;

import java.util.List;

import com.bytezone.filesystem.ForkProdos;
import com.bytezone.filesystem.TextBlock;
import com.bytezone.filesystem.TextBlock.TextRecord;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class ProdosText extends Text
// -----------------------------------------------------------------------------------//
{
  private static String underline = "------------------------------------------"
      + "------------------------------------\n";
  private static String fullUnderline = "----------  --------  " + underline;

  boolean showTextOffsets = true;         // will be a preference
  int recordLength;

  // ---------------------------------------------------------------------------------//
  public ProdosText (ForkProdos appleFile, List<? extends TextBlock> textBlocks, int aux)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, textBlocks);

    this.recordLength = aux;
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

      for (TextRecord record : textBlock)
        if (showTextOffsets)
        {
          int offset = record.offset () + textBlock.getStartByte ();
          int recordNo = offset / recordLength;
          text.append (String.format (" %,9d %,9d  %s%n", offset, recordNo,
              getRecordData (buffer, record, (byte) 0x7F)));
        }
        else
          text.append (
              String.format ("%s%n", getRecordData (buffer, record, (byte) 0x7F)));
    }

    return Utility.rtrim (text);
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

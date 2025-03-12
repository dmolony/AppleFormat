package com.bytezone.appleformat.text;

import java.util.List;

import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.FsProdos;
import com.bytezone.filesystem.TextBlock;
import com.bytezone.utility.Utility;

// Prodos text files
// - seedling         1 block             512 bytes
// - sapling        256 blocks        131,072 bytes 
// - tree        65,536 blocks     33,554,432 bytes
// 
// aux = record length
// eof = file length
// -----------------------------------------------------------------------------------//
public class ProdosText extends Text
// -----------------------------------------------------------------------------------//
{
  private static String underline = "------------------------------------------"
      + "------------------------------------\n";
  private static String fullUnderline = "----------  --------  " + underline;

  boolean showTextOffsets = true;
  FsProdos fs = (FsProdos) appleFile.getParentFileSystem ();
  //  List<TextBlock> textBlocks = new ArrayList<> ();
  int aux;

  // ---------------------------------------------------------------------------------//
  public ProdosText (FileProdos appleFile, int aux)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, appleFile.getTextBlocks ());

    this.aux = aux;
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
      text.append (textBlock.getText ());

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

    List<String> lines = List.of (text.toString ().split ("\n"));
    return lines;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    return Utility.rtrim (text);
  }
}

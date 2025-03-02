package com.bytezone.appleformat.text;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.Buffer;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.FsProdos;
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
  List<TextBlock> textBlocks = new ArrayList<> ();
  int aux;

  // note dataBuffer is not needed
  // ---------------------------------------------------------------------------------//
  public ProdosText (FileProdos appleFile, Buffer dataBuffer, int aux)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile);

    this.aux = aux;
    if (aux == 0)
      return;

    // collect contiguous blocks into TextBlocks
    List<AppleBlock> dataBlocks = new ArrayList<> ();
    int logicalBlockNo = 0;
    int startBlock = -1;

    for (AppleBlock block : appleFile.getDataBlocks ())
    {
      if (block == null)
      {
        if (dataBlocks.size () > 0)
        {
          TextBlock textBlock =
              new TextBlock (fs, new ArrayList<> (dataBlocks), startBlock, aux);
          textBlocks.add (textBlock);
          dataBlocks.clear ();
        }
      }
      else
      {
        if (dataBlocks.size () == 0)
          startBlock = logicalBlockNo;
        dataBlocks.add (block);
      }

      ++logicalBlockNo;
    }

    if (dataBlocks.size () > 0)
    {
      TextBlock textBlock = new TextBlock (fs, new ArrayList<> (dataBlocks), startBlock,
          appleFile.getAuxType ());
      textBlocks.add (textBlock);
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

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
      text.append (textBlock);

    return Utility.rtrim (text);
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

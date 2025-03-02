package com.bytezone.appleformat.text;

import java.util.List;

import com.bytezone.filesystem.AppleBlock;
import com.bytezone.filesystem.FsProdos;

// A TextBlock is a list of consecutive disk blocks that make up a part of a prodos
// text file. It represents an island of records somewhere within the text file.
//-------------------------------------------------------------------------------------//
public class TextBlock
//-------------------------------------------------------------------------------------//
{
  List<AppleBlock> blocks;            // an island of data blocks within the file
  int startBlockNo;                   // block number within the file
  int recordLength;                   // aux

  int firstLogicalRecordNo;           // first complete record number
  int offsetToFirstRecord;            // skip incomplete record if present
  int maxRecords;                     // possible # full records in this island
  int totalRecords;                   // # full records in this island with data

  FsProdos fs;

  //-----------------------------------------------------------------------------------//
  public TextBlock (FsProdos fs, List<AppleBlock> blocks, int startBlockNo, int recLen)
  //-----------------------------------------------------------------------------------//
  {
    this.fs = fs;
    this.blocks = blocks;
    this.startBlockNo = startBlockNo;
    this.recordLength = recLen;

    int blockSize = fs.getBlockSize ();

    int firstLogicalByte = startBlockNo * blockSize;
    int skipped = firstLogicalByte % recLen;

    offsetToFirstRecord = skipped == 0 ? 0 : recLen - skipped;
    firstLogicalRecordNo = (firstLogicalByte + offsetToFirstRecord) / recLen;

    int dataSize = blocks.size () * blockSize - offsetToFirstRecord;
    maxRecords = dataSize / recLen;
  }

  //-----------------------------------------------------------------------------------//
  @Override
  public String toString ()
  //-----------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int ptr = offsetToFirstRecord;
    int recordNo = firstLogicalRecordNo;
    byte[] buffer = fs.readBlocks (blocks);
    boolean showTextOffsets = true;
    int blockSize = fs.getBlockSize ();

    int firstLogicalByte = startBlockNo * blockSize;

    // check each full record in the island

    for (int i = 0; i < maxRecords; i++)
    {
      if (buffer[ptr] != 0)
      {
        if (showTextOffsets)
          text.append (String.format (" %,9d %,9d  ", firstLogicalByte + ptr, recordNo));

        ++totalRecords;
        //        text.append (String.format ("Offset: %,6d  Record: %,6d  Blocks: %3d%n", ptr,
        //            recordNo, blocks.size ()));

        int ptr2 = ptr;
        int max = ptr + recordLength;

        while (ptr2 < max)
        {
          int val = buffer[ptr2++] & 0x7F;                   // strip hi-order bit

          if (val == 0)
            break;

          text.append ((char) val);
        }
      }
      //      else
      //        text.append ("\n");

      ptr += recordLength;
      recordNo++;
    }

    return text.toString ();
  }
}

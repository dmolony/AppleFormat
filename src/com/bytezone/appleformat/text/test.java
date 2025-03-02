package com.bytezone.appleformat.text;

public class test
{
  public static void main (String[] args)
  {
    int blockSize = 51;
    int recordLength = 10;

    for (int blockNo = 0; blockNo < 12; blockNo++)
    {
      int firstLogicalByte = blockNo * blockSize;
      int skipped = firstLogicalByte % recordLength;
      int offset = skipped == 0 ? 0 : recordLength - skipped;

      int firstLogicalRecordNo = (firstLogicalByte + offset) / recordLength;

      int dataSize = 1 * blockSize - offset;
      int maxRecords = dataSize / recordLength;

      System.out.printf ("%4d  %4d  %4d  %4d  %4d%n", blockNo, firstLogicalByte,
          firstLogicalRecordNo, offset, maxRecords);

      if (false)
      {
        int ptr = offset;
        int recordNo = firstLogicalRecordNo;

        for (int rec = 0; rec < maxRecords; rec++)
        {
          System.out.printf ("Offset: %6d  Record: %6d%n", ptr, recordNo);
          recordNo++;
          ptr += recordLength;
        }
      }
    }
  }
}

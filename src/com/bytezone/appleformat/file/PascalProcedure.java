package com.bytezone.appleformat.file;

import com.bytezone.appleformat.Utility;
import com.bytezone.filesystem.AppleFile;

public class PascalProcedure extends AbstractFormattedAppleFile
{
  // ---------------------------------------------------------------------------------//
  public PascalProcedure (AppleFile appleFile, byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    super (appleFile, buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null)
      return "This file has no data\n\n" + appleFile.getErrorMessage ();

    StringBuilder text = new StringBuilder ();

    text.append (appleFile);

    return Utility.rtrim (text);
  }

  /*
  // ---------------------------------------------------------------------------------//
  private void decode ()
  // ---------------------------------------------------------------------------------//
  {
    if (statements.size () > 0 || assembler != null)
      return;
  
    int ptr = procOffset - codeStart - 2;
    int max = procOffset + jumpTable;
  
    if (codeEnd == 0)
    {
      int len = codeStart + jumpTable + 2;
      if (len > 0)
      {
        byte[] asmBuf = new byte[len];
        System.arraycopy (buffer, ptr, asmBuf, 0, len);
        assembler = new AssemblerProgram ("Proc", asmBuf, ptr);
      }
      return;
    }
  
    while (ptr < max)
    {
      //      System.out.printf ("ptr:%d, max:%d, buf:%d %n", ptr, max, buffer.length);
      if (ptr >= buffer.length || ptr < 0)
      {
        System.out.printf ("Ptr outside buffer: %d %d%n", ptr, buffer.length);
        break;
      }
      PascalCodeStatement cs = new PascalCodeStatement (buffer, ptr, procOffset);
      if (cs.length <= 0)
      {
        System.out.println ("error - length <= 0 : " + cs);
        break;
      }
      statements.add (cs);
      if (cs.val == 185 || cs.val == 161)
        if (cs.p1 < jumpTable)
        {
          jumpTable = cs.p1;
          max = procOffset + jumpTable;
        }
      ptr += cs.length;
    }
  
    // Tidy up left-over bytes at the end
    if (statements.size () > 1)
    {
      PascalCodeStatement lastStatement = statements.get (statements.size () - 1);
      PascalCodeStatement secondLastStatement = statements.get (statements.size () - 2);
      if (lastStatement.val == 0 && (secondLastStatement.val == 0xD6
          || secondLastStatement.val == 0xC1 || secondLastStatement.val == 0xAD))
        statements.remove (statements.size () - 1);
    }
  
    // Mark statements that are jump targets
    int actualEnd = procOffset - codeEnd - 4;
    for (PascalCodeStatement cs : statements)
    {
      if (cs.ptr == actualEnd)
      {
        cs.jumpTarget = true;
        continue;
      }
      for (Jump cj : cs.jumps)
        for (PascalCodeStatement cs2 : statements)
          if (cs2.ptr == cj.addressTo)
          {
            cs2.jumpTarget = true;
            break;
          }
    }
  }
  */
}

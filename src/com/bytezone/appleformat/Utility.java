package com.bytezone.appleformat;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public final class Utility
// -----------------------------------------------------------------------------------//
{
  private static String[] hex =
      { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

  public static final byte ASCII_BACKSPACE = 0x08;
  public static final byte ASCII_LF = 0x0A;
  public static final byte ASCII_CR = 0x0D;
  public static final byte ASCII_SPACE = 0x20;
  public static final byte ASCII_DOUBLE_QUOTE = 0x22;
  public static final byte ASCII_DOLLAR = 0x24;
  public static final byte ASCII_PERCENT = 0x25;
  public static final byte ASCII_SINGLE_QUOTE = 0x27;
  public static final byte ASCII_LEFT_BRACKET = 0x28;
  public static final byte ASCII_RIGHT_BRACKET = 0x29;
  public static final byte ASCII_ASTERISK = 0x2A;
  public static final byte ASCII_COMMA = 0x2C;
  public static final byte ASCII_MINUS = 0x2D;
  public static final byte ASCII_DOT = 0x2E;
  public static final byte ASCII_COLON = 0x3A;
  public static final byte ASCII_SEMI_COLON = 0x3B;
  public static final byte ASCII_EQUALS = 0x3D;
  public static final byte ASCII_CARET = 0x5E;

  private static MathContext mathContext = new MathContext (9);
  private static MathContext mathContext4 = new MathContext (6);
  private static MathContext mathContext8 = new MathContext (15);

  private static final List<String> suffixes = Arrays.asList ("po", "dsk", "do", "hdv",
      "2mg", "d13", "sdk", "shk", "bxy", "bny", "bqy", "woz", "img", "dimg");

  private static final byte[] fourBuf = new byte[4];

  record ColorRecord (Color color, String name)
  {
  };

  //  static List<ColorRecord> colorRecords = getColorRecords ();

  // ---------------------------------------------------------------------------------//
  private Utility ()
  // ---------------------------------------------------------------------------------//
  {
  }

  // ---------------------------------------------------------------------------------//
  public static int getTotalSuffixes ()
  // ---------------------------------------------------------------------------------//
  {
    return suffixes.size ();
  }

  // ---------------------------------------------------------------------------------//
  public static String getSuffix (int index)
  // ---------------------------------------------------------------------------------//
  {
    return suffixes.get (index);
  }

  // ---------------------------------------------------------------------------------//
  public static boolean test (Graphics2D g)
  // ---------------------------------------------------------------------------------//
  {
    return g.getFontRenderContext ().getTransform ()
        .equals (AffineTransform.getScaleInstance (2.0, 2.0));
  }

  // ---------------------------------------------------------------------------------//
  public static void printStackTrace ()
  // ---------------------------------------------------------------------------------//
  {
    for (StackTraceElement ste : java.lang.Thread.currentThread ().getStackTrace ())
      System.out.println (ste);
  }

  // ---------------------------------------------------------------------------------//
  public static String rtrim (StringBuilder text)
  // ---------------------------------------------------------------------------------//
  {
    while (text.length () > 0 && text.charAt (text.length () - 1) == '\n')
      text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public static int getIndent (StringBuilder fullText)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = fullText.length () - 1;
    int indent = 0;
    while (ptr >= 0 && fullText.charAt (ptr) != '\n')
    {
      --ptr;
      ++indent;
    }
    return indent;
  }

  // ---------------------------------------------------------------------------------//
  public static List<String> getHexDumpLines (byte[] b, int displayOffset)
  // ---------------------------------------------------------------------------------//
  {
    return getHexDumpLines (b, 0, b.length, displayOffset);
  }

  // ---------------------------------------------------------------------------------//
  public static List<String> getHexDumpLines (byte[] b, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    return getHexDumpLines (b, offset, length, 0);
  }

  // ---------------------------------------------------------------------------------//
  public static List<String> getHexDumpLines (byte[] b, int offset, int length,
      int displayOffset)
  // ---------------------------------------------------------------------------------//
  {
    final int lineSize = 16;

    List<String> lines = new ArrayList<> ();
    final StringBuilder hexLine = new StringBuilder ();
    final StringBuilder textLine = new StringBuilder ();

    for (int ptr = offset, max = offset + length; ptr < max; ptr += lineSize)
    {
      hexLine.setLength (0);
      textLine.setLength (0);

      for (int linePtr = 0; linePtr < lineSize; linePtr++)
      {
        int z = ptr + linePtr;
        if (z >= max)
          break;

        hexLine.append (String.format ("%02X ", b[z]));

        int c = b[z] & 0x7F;

        if (c > 127)
          c -= c < 160 ? 64 : 128;

        if (c < 32 || c == 127)         // non-printable
          textLine.append (".");
        else                            // standard ascii
          textLine.append ((char) c);
      }

      lines.add (String.format ("%06X  %-48s %s", displayOffset + ptr,
          hexLine.toString (), textLine.toString ()));
    }

    return lines;
  }

  // ---------------------------------------------------------------------------------//
  public static String format (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    return format (buffer, 0, buffer.length, true, 0);
  }

  // ---------------------------------------------------------------------------------//
  public static String format (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    return format (buffer, offset, buffer.length - offset, true, 0);
  }

  // ---------------------------------------------------------------------------------//
  public static String format (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    return format (buffer, offset, length, true, 0);
  }

  // ---------------------------------------------------------------------------------//
  public static String format (byte[] buffer, int offset, int length, boolean header,
      int startingAddress)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder line = new StringBuilder ();
    int[] freq = new int[256];
    boolean startedOnBoundary = offset % 0x100 == 0;

    if (header)
    {
      line.append ("      ");
      for (int i = 0; i < 16; i++)
        line.append ("  " + hex[i]);
      if (offset == 0)
        line.append ("\n");
    }

    for (int i = offset; i < offset + length; i += 16)
    {
      if (line.length () > 0 && i > 0)
        line.append ("\n");
      if (i > offset && startedOnBoundary && (i % 0x200) == 0)
        line.append ("\n");

      // print offset
      line.append (String.format ("%05X : ", (startingAddress + i - offset)));

      // print hex values
      StringBuffer trans = new StringBuffer ();
      StringBuffer hexLine = new StringBuffer ();

      int max = Math.min (i + 16, offset + length);
      max = Math.min (max, buffer.length);
      for (int j = i; j < max; j++)
      {
        int c = buffer[j] & 0xFF;
        freq[c]++;
        hexLine.append (String.format ("%02X ", c));

        if (c > 127)
          c -= c < 160 ? 64 : 128;

        if (c < 32 || c == 127)         // non-printable
          trans.append (".");
        else                            // standard ascii
          trans.append ((char) c);
      }
      while (hexLine.length () < 48)
        hexLine.append (" ");

      line.append (hexLine.toString () + ": " + trans.toString ());
    }

    if (false)
    {
      line.append ("\n\n");
      int totalBits = 0;
      for (int i = 0; i < freq.length; i++)
        if (freq[i] > 0)
        {
          totalBits += (Integer.bitCount (i) * freq[i]);
          line.append (
              String.format ("%02X  %3d   %d%n", i, freq[i], Integer.bitCount (i)));
        }
      line.append (String.format ("%nTotal bits : %d%n", totalBits));
    }
    return line.toString ();
  }

  // ---------------------------------------------------------------------------------//
  // public static int getLong (byte[] buffer, int ptr)
  // //
  // ---------------------------------------------------------------------------------//
  // {
  // return getWord (buffer, ptr) + getWord (buffer, ptr + 2) * 0x10000;
  // }

  // ---------------------------------------------------------------------------------//
  // public static int getWord (byte[] buffer, int ptr)
  // //
  // ---------------------------------------------------------------------------------//
  // {
  // int a = (buffer[ptr + 1] & 0xFF) << 8;
  // int b = buffer[ptr] & 0xFF;
  // return a + b;
  // }

  // ---------------------------------------------------------------------------------//
  public static int intValue (byte b1, byte b2)
  // ---------------------------------------------------------------------------------//
  {
    return (b1 & 0xFF) | ((b2 & 0xFF) << 8);
  }

  // ---------------------------------------------------------------------------------//
  public static int intValue (byte b1, byte b2, byte b3)
  // ---------------------------------------------------------------------------------//
  {
    return (b1 & 0xFF) | ((b2 & 0xFF) << 8) | ((b3 & 0xFF) << 16);
  }

  // ---------------------------------------------------------------------------------//
  public static int getLong (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      int val = 0;
      for (int i = 3; i >= 0; i--)
      {
        val <<= 8;
        val += buffer[ptr + i] & 0xFF;
      }
      return val;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      System.out.printf ("Index out of range (getLong): %08X  %<d%n", ptr);
      return 0;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static int getLongBigEndian (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int val = 0;
    for (int i = 0; i < 4; i++)
    {
      val <<= 8;
      val += buffer[ptr + i] & 0xFF;
    }
    return val;
  }

  // ---------------------------------------------------------------------------------//
  // public static int getWordBigEndian (byte[] buffer, int ptr)
  // //
  // ---------------------------------------------------------------------------------//
  // {
  // int val = 0;
  // for (int i = 0; i < 2; i++)
  // {
  // val <<= 8;
  // val += buffer[ptr + i] & 0xFF;
  // }
  // return val;
  // }

  // ---------------------------------------------------------------------------------//
  public static int getShort (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    if (ptr < 0 || ptr + 1 >= buffer.length)
    {
      System.out.printf ("Index out of range (getShort): %04X  %<d%n", ptr);
      return 0;
    }

    return ((buffer[ptr] & 0xFF) | ((buffer[ptr + 1] & 0xFF) << 8));
  }

  // ---------------------------------------------------------------------------------//
  public static int getSignedShort (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      return (short) ((buffer[ptr] & 0xFF) | ((buffer[ptr + 1] & 0xFF) << 8));
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      System.out.printf ("Index out of range (signedShort): %04X  %<d%n", ptr);
      return 0;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static int getShortBigEndian (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int val = 0;
    for (int i = 0; i < 2; i++)
    {
      val <<= 8;
      val |= buffer[ptr + i] & 0xFF;
    }
    return val;
  }

  // ---------------------------------------------------------------------------------//
  public static int unsignedTriple (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      return (buffer[ptr] & 0xFF)             //
          | (buffer[ptr + 1] & 0xFF) << 8     //
          | (buffer[ptr + 2] & 0xFF) << 16;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      System.out.printf ("Index out of range (unsignedTriple): %d > %d%n", ptr,
          buffer.length);
      return 0;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static LocalDateTime getAppleDate (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int yymmdd = getShort (buffer, offset);
    if (yymmdd != 0)
    {
      int year = (yymmdd & 0xFE00) >> 9;
      int month = (yymmdd & 0x01E0) >> 5;
      int day = yymmdd & 0x001F;

      int minute = buffer[offset + 2] & 0x3F;
      int hour = buffer[offset + 3] & 0x1F;

      if (hour > 23)
        hour = 0;
      if (minute > 59)
        minute = 0;

      if (year < 70)
        year += 2000;
      else
        year += 1900;

      try
      {
        return LocalDateTime.of (year, month, day, hour, minute);
      }
      catch (DateTimeException e)
      {
        System.out.printf ("Bad date/time: %d %d %d %d %d %n", year, month, day, hour,
            minute);
      }
    }

    return null;
  }

  // ---------------------------------------------------------------------------------//
  public static void putAppleDate (byte[] buffer, int offset, LocalDateTime date)
  // ---------------------------------------------------------------------------------//
  {
    if (date != null)
    {
      int year = date.getYear ();
      int month = date.getMonthValue ();
      int day = date.getDayOfMonth ();
      int hour = date.getHour ();
      int minute = date.getMinute ();

      if (year < 2000)
        year -= 1900;
      else
        year -= 2000;

      int val1 = year << 9 | month << 5 | day;
      writeShort (buffer, offset, val1);
      buffer[offset + 2] = (byte) minute;
      buffer[offset + 3] = (byte) hour;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static void writeShort (byte[] buffer, int ptr, int value)
  // ---------------------------------------------------------------------------------//
  {
    buffer[ptr] = (byte) (value & 0xFF);
    buffer[ptr + 1] = (byte) ((value & 0xFF00) >>> 8);
  }

  // ---------------------------------------------------------------------------------//
  public static void writeTriple (byte[] buffer, int ptr, int value)
  // ---------------------------------------------------------------------------------//
  {
    buffer[ptr] = (byte) (value & 0xFF);
    buffer[ptr + 1] = (byte) ((value & 0xFF00) >>> 8);
    buffer[ptr + 2] = (byte) ((value & 0xFF0000) >>> 16);
  }

  // ---------------------------------------------------------------------------------//
  // public static int readShort (byte[] buffer, int ptr)
  // //
  // ---------------------------------------------------------------------------------//
  // {
  // return (buffer[ptr] & 0xFF) | (buffer[ptr + 1] & 0xFF) << 8;
  // }

  // ---------------------------------------------------------------------------------//
  public static int readTriple (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    return (buffer[ptr] & 0xFF) | (buffer[ptr + 1] & 0xFF) << 8
        | (buffer[ptr + 2] & 0xFF) << 16;
  }

  // ---------------------------------------------------------------------------------//
  public static LocalDateTime getDos4LocalDateTime (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      int[] val = new int[6];
      for (int i = 0; i < 6; i++)
      {
        int tmp = buffer[offset++] & 0xFF;
        val[i] = tmp / 16 * 10 + tmp % 16;
      }

      return LocalDateTime.of (val[3] + 2000, val[5], val[4], val[2], val[1], val[0]);
    }
    catch (DateTimeException e)
    {
      return null;
    }
    catch (NumberFormatException e2)
    {
      return null;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static String matchFlags (int flag, String[] chars)
  // ---------------------------------------------------------------------------------//
  {
    int weight = (int) Math.pow (2, chars.length - 1);
    StringBuilder text = new StringBuilder ();

    for (int i = 0; i < chars.length; i++)
    {
      if ((flag & weight) != 0)
        text.append (chars[i]);
      else
        text.append (".");
      weight >>>= 1;
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public static double getSANEDouble (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    long bits = 0;
    for (int i = 7; i >= 0; i--)
    {
      bits <<= 8;
      bits |= buffer[offset + i] & 0xFF;
    }

    return Double.longBitsToDouble (bits);
  }

  // ---------------------------------------------------------------------------------//
  public static double floatValue (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int exponent = buffer[ptr] & 0x7F;                      // biased 128
    if (exponent == 0)
      return 0.0;

    int mantissa = (buffer[ptr + 1] & 0x7F) << 24 | (buffer[ptr + 2] & 0xFF) << 16
        | (buffer[ptr + 3] & 0xFF) << 8 | (buffer[ptr + 4] & 0xFF);
    boolean negative = (buffer[ptr + 1] & 0x80) != 0;
    double value = 0.5;

    for (int i = 2, weight = 0x40_00_00_00; i <= 32; i++, weight >>>= 1)
      if ((mantissa & weight) > 0)
        value += Math.pow (0.5, i);

    value *= Math.pow (2, exponent);
    BigDecimal bd = new BigDecimal (value);
    double rounded = bd.round (mathContext).doubleValue ();

    return negative ? rounded * -1 : rounded;
  }

  // https://en.wikipedia.org/wiki/Microsoft_Binary_Format
  // ---------------------------------------------------------------------------------//
  public static double floatValueMS4 (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int exponent = buffer[ptr + 3] & 0x7F;                      // biased 128
    if (exponent == 0)
      return 0.0;

    int mantissa = (buffer[ptr + 2] & 0x7F) << 16 | (buffer[ptr + 1] & 0xFF) << 8
        | (buffer[ptr] & 0xFF);
    boolean negative = (buffer[ptr + 2] & 0x80) != 0;
    double value = 0.5;

    for (int i = 2, weight = 0x40_00_00; i <= 23; i++, weight >>>= 1)
      if ((mantissa & weight) != 0)
        value += Math.pow (0.5, i);

    value *= Math.pow (2, exponent);
    BigDecimal bd = new BigDecimal (value);
    double rounded = bd.round (mathContext4).doubleValue ();

    return negative ? rounded * -1 : rounded;
  }

  // ---------------------------------------------------------------------------------//
  public static double floatValueMS8 (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int exponent = buffer[ptr + 7] & 0x7F;                      // biased 128
    if (exponent == 0)
      return 0.0;

    long mantissa = (long) (buffer[ptr + 6] & 0x7F) << 48
        | (long) (buffer[ptr + 5] & 0xFF) << 40 | (long) (buffer[ptr + 4] & 0xFF) << 32
        | (long) (buffer[ptr + 3] & 0xFF) << 24 | (buffer[ptr + 2] & 0xFF) << 16
        | (buffer[ptr + 1] & 0xFF) << 8 | (buffer[ptr] & 0xFF);
    boolean negative = (buffer[ptr + 6] & 0x80) != 0;
    double value = 0.5;

    long weight = 0x40_00_00_00_00_00_00L;
    for (int i = 2; i <= 55; i++, weight >>>= 1)
      if ((mantissa & weight) != 0)
        value += Math.pow (0.5, i);

    value *= Math.pow (2, exponent);
    BigDecimal bd = new BigDecimal (value);
    double rounded = bd.round (mathContext8).doubleValue ();

    return negative ? rounded * -1 : rounded;
  }

  // ---------------------------------------------------------------------------------//
  public static LocalDate getPascalLocalDate (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int date = Utility.getShort (buffer, offset);

    if (date == 0)
      return null;

    int month = date & 0x0F;
    int day = (date & 0x1F0) >>> 4;
    int year = (date & 0xFE00) >>> 9;

    if (year < 70)
      year += 2000;
    else
      year += 1900;

    try
    {
      return LocalDate.of (year, month, day);
    }
    catch (DateTimeException e)
    {
      return null;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static String string (byte[] buffer, int ptr, int nameLength)
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    for (int i = 0; i < nameLength; i++)
    {
      int c = buffer[ptr + i] & 0x7F;
      if (c < 32)
        c += 64;
      //      if (c >= 32)
      text.append ((char) c);
      //      else
      //        text.append (".");
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public static String getPascalString (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int length = buffer[offset] & 0xFF;
    return HexFormatter.getString (buffer, offset + 1, length);
  }

  // ---------------------------------------------------------------------------------//
  public static String getCString (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int length = 0;
    int ptr = offset;

    while (buffer[ptr++] != 0)
      ++length;

    return new String (buffer, offset, length);
  }

  // ---------------------------------------------------------------------------------//
  public static int dimension (int chars, int border, int size, int gap)
  // ---------------------------------------------------------------------------------//
  {
    return border * 2 + chars * (size + gap) - gap;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean find (byte[] buffer, byte[] key)
  // ---------------------------------------------------------------------------------//
  {
    for (int i = 0; i < buffer.length; i++)
      if (matches (buffer, i, key))
      {
        System.out.printf ("Matches at %04X%n", i);
        return true;
      }

    return false;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean matches (byte[] buffer, int offset, byte[] key)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = 0;
    while (offset < buffer.length && ptr < key.length)
      if (buffer[offset++] != key[ptr++])
        return false;

    return true;
  }

  // ---------------------------------------------------------------------------------//
  public static LocalDateTime getDateTime (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      int[] val = new int[6];
      for (int i = 0; i < 6; i++)
        //        val[i] = Integer.parseInt (String.format ("%02X", buffer[ptr + i] & 0xFF));
        val[i] = buffer[ptr + i] & 0xFF;

      LocalDateTime date =
          LocalDateTime.of (val[3] + 2000, val[5], val[4], val[2], val[1], val[0]);
      return date;
    }
    //    catch (DateTimeException | NumberFormatException e)
    catch (DateTimeException e)
    {
      return null;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static int getSuffixNo (String filename)
  // ---------------------------------------------------------------------------------//
  {
    return suffixes.indexOf (getSuffix (filename));
  }

  // ---------------------------------------------------------------------------------//
  public static String getSuffix (String filename)
  // ---------------------------------------------------------------------------------//
  {
    String lcFilename = filename.toLowerCase ();

    if (lcFilename.endsWith (".gz"))
      lcFilename = lcFilename.substring (0, lcFilename.length () - 3);
    else if (lcFilename.endsWith (".zip"))
      lcFilename = lcFilename.substring (0, lcFilename.length () - 4);

    int dotPos = lcFilename.lastIndexOf ('.');
    if (dotPos < 0)
      return "";

    return lcFilename.substring (dotPos + 1);
  }

  // ---------------------------------------------------------------------------------//
  public static boolean validFileType (String filename)
  // ---------------------------------------------------------------------------------//
  {
    if (filename.startsWith ("."))          // ignore invisible files
      return false;
    return suffixes.contains (getSuffix (filename));
  }

  // ---------------------------------------------------------------------------------//
  public static void reverse (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    int lo = 0;
    int hi = buffer.length - 1;

    while (lo < hi)
    {
      byte temp = buffer[lo];
      buffer[lo++] = buffer[hi];
      buffer[hi--] = temp;
    }
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isHighBitSet (byte value)
  // ---------------------------------------------------------------------------------//
  {
    return (value & 0x80) != 0;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isControlCharacter (byte value)
  // ---------------------------------------------------------------------------------//
  {
    int val = value & 0xFF;
    return val > 0 && val < 32;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isDigit (byte value)
  // ---------------------------------------------------------------------------------//
  {
    return value >= 0x30 && value <= 0x39;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isLetter (byte value)
  // ---------------------------------------------------------------------------------//
  {
    return value >= 0x41 && value <= 0x5A;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isPossibleVariable (byte value)
  // ---------------------------------------------------------------------------------//
  {
    return isDigit (value) || isLetter (value) || value == ASCII_DOLLAR
        || value == ASCII_PERCENT;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isPossibleNumber (byte value)
  // ---------------------------------------------------------------------------------//
  {
    return isDigit (value) || value == ASCII_DOT;
  }

  // ---------------------------------------------------------------------------------//
  public static boolean isMagic (byte[] buffer, int ptr, byte[] magic)
  // ---------------------------------------------------------------------------------//
  {
    if (ptr + magic.length >= buffer.length)
      return false;

    for (int i = 0; i < magic.length; i++)
      if (buffer[ptr + i] != magic[i])
        return false;

    return true;
  }

  // ---------------------------------------------------------------------------------//
  public static byte[] unpackBytes (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    byte[] unpackedBuffer = new byte[calculateBufferSize (buffer, 0)];
    unpackBytes (buffer, 0, buffer.length, unpackedBuffer, 0);

    return unpackedBuffer;
  }

  // ---------------------------------------------------------------------------------//
  public static byte[] unpackBytes (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    byte[] unpackedBuffer = new byte[calculateBufferSize (buffer, offset)];
    unpackBytes (buffer, offset, buffer.length, unpackedBuffer, 0);

    return unpackedBuffer;
  }

  // ---------------------------------------------------------------------------------//
  public static int calculateBufferSize (byte[] buffer, int ptr)
  // ---------------------------------------------------------------------------------//
  {
    int size = 0;
    while (ptr < buffer.length)
    {
      int type = (buffer[ptr] & 0xC0) >>> 6;        // 0-3
      int count = (buffer[ptr++] & 0x3F) + 1;       // 1-64

      if (type == 0)
      {
        ptr += count;
        size += count;
      }
      else if (type == 1)
      {
        ptr++;
        size += count;
      }
      else if (type == 2)
      {
        ptr += 4;
        size += count * 4;
      }
      else
      {
        ptr++;
        size += count * 4;
      }
    }

    return size;
  }

  // ---------------------------------------------------------------------------------//
  public static int unpackBytes (byte[] buffer, int ptr, int max, byte[] newBuffer,
      int newPtr)
  // ---------------------------------------------------------------------------------//
  {
    int savePtr = newPtr;

    while (ptr < max - 1)                           // minimum 2 bytes needed
    {
      int type = (buffer[ptr] & 0xC0) >>> 6;        // 0-3
      int count = (buffer[ptr++] & 0x3F) + 1;       // 1-64

      switch (type)
      {
        case 0:                                     // 2-65 bytes
          while (count-- != 0 && newPtr < newBuffer.length && ptr < max)
            newBuffer[newPtr++] = buffer[ptr++];
          break;

        case 1:                                     // 2 bytes
          byte b = buffer[ptr++];
          while (count-- != 0 && newPtr < newBuffer.length)
            newBuffer[newPtr++] = b;
          break;

        case 2:                                     // 5 bytes
          for (int i = 0; i < 4; i++)
            fourBuf[i] = ptr < max ? buffer[ptr++] : 0;

          while (count-- != 0)
            for (int i = 0; i < 4; i++)
              if (newPtr < newBuffer.length)
                newBuffer[newPtr++] = fourBuf[i];
          break;

        case 3:                                     // 2 bytes
          b = buffer[ptr++];
          count *= 4;
          while (count-- != 0 && newPtr < newBuffer.length)
            newBuffer[newPtr++] = b;
          break;
      }
    }

    return newPtr - savePtr;                        // bytes unpacked
  }

  // ---------------------------------------------------------------------------------//
  public static int getApplesoftLoadAddress (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    int nextLine = Utility.getShort (buffer, 2);
    int ptr = 6;            // skip load address, next line number, this line number

    while (ptr < buffer.length && buffer[ptr] != 0)
      ++ptr;

    return nextLine - ptr + 1;
  }

  // ---------------------------------------------------------------------------------//
  //  public static List<ColorRecord> getColorRecords ()
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    List<ColorRecord> colorRecords = new ArrayList<> ();
  //
  //    final Field[] fields = Color.class.getFields ();      // only want public
  //    for (final Field field : fields)
  //    {
  //      if (field.getType () == Color.class)
  //      {
  //        try
  //        {
  //          final Color color = (Color) field.get (null);
  //          final String colorName = field.getName ();
  //          //          System.out.printf ("%-15s  %s%n", colorName, color);
  //          colorRecords.add (new ColorRecord (color, colorName));
  //        }
  //        catch (IllegalAccessException illegalAccessEx)
  //        {
  //          System.out.println ("Security Manager does not allow access of field '"
  //              + field.getName () + "'.");
  //        }
  //      }
  //    }
  //
  //    return colorRecords;
  //  }

  // ---------------------------------------------------------------------------------//
  //  public static String getColorName (Color color)
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    for (ColorRecord colorRecord : colorRecords)
  //      if (colorRecord.color.equals (color))
  //        return colorRecord.name;
  //
  //    return "<not found>";
  //  }

  // ---------------------------------------------------------------------------------//
  public static long getChecksumValue (File file)
  // ---------------------------------------------------------------------------------//
  {
    Checksum checksum = new CRC32 ();
    try
    {
      BufferedInputStream is =
          new BufferedInputStream (new FileInputStream (file.getAbsolutePath ()));
      byte[] bytes = new byte[1024];
      int len = 0;

      while ((len = is.read (bytes)) >= 0)
        checksum.update (bytes, 0, len);

      is.close ();
    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
    return checksum.getValue ();
  }

  // ---------------------------------------------------------------------------------//
  public static int getCRC (final byte[] buffer, int length, int initialValue)
  // ---------------------------------------------------------------------------------//
  {
    int crc = initialValue;
    for (int j = 0; j < length; j++)
    {
      crc = ((crc >>> 8) | (crc << 8)) & 0xFFFF;
      crc ^= (buffer[j] & 0xFF);
      crc ^= ((crc & 0xFF) >>> 4);
      crc ^= (crc << 12) & 0xFFFF;
      crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
    }

    crc &= 0xFFFF;

    return crc;
  }

  // ---------------------------------------------------------------------------------//
  public static int crc32 (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    int crc = 0xFFFFFFFF;        // one's complement of zero
    int eof = offset + length;

    for (int i = offset; i < eof; i++)
      crc = crc32_tab[(crc ^ buffer[i]) & 0xFF] ^ (crc >>> 8);

    return ~crc;                 // one's complement
  }

  static int[] crc32_tab = { //
      0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f, 0xe963a535,
      0x9e6495a3, 0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988, 0x09b64c2b, 0x7eb17cbd,
      0xe7b82d07, 0x90bf1d91, 0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de, 0x1adad47d,
      0x6ddde4eb, 0xf4d4b551, 0x83d385c7, 0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec,
      0x14015c4f, 0x63066cd9, 0xfa0f3d63, 0x8d080df5, 0x3b6e20c8, 0x4c69105e, 0xd56041e4,
      0xa2677172, 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b, 0x35b5a8fa, 0x42b2986c,
      0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59, 0x26d930ac,
      0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423, 0xcfba9599, 0xb8bda50f,
      0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924, 0x2f6f7c87, 0x58684c11, 0xc1611dab,
      0xb6662d3d, 0x76dc4190, 0x01db7106, 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f,
      0x9fbfe4a5, 0xe8b8d433, 0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb,
      0x086d3d2d, 0x91646c97, 0xe6635c01, 0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e,
      0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457, 0x65b0d9c6, 0x12b7e950, 0x8bbeb8ea,
      0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65, 0x4db26158, 0x3ab551ce,
      0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7, 0xa4d1c46d, 0xd3d6f4fb, 0x4369e96a,
      0x346ed9fc, 0xad678846, 0xda60b8d0, 0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9,
      0x5005713c, 0x270241aa, 0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409,
      0xce61e49f, 0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81,
      0xb7bd5c3b, 0xc0ba6cad, 0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a, 0xead54739,
      0x9dd277af, 0x04db2615, 0x73dc1683, 0xe3630b12, 0x94643b84, 0x0d6d6a3e, 0x7a6a5aa8,
      0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1, 0xf00f9344, 0x8708a3d2, 0x1e01f268,
      0x6906c2fe, 0xf762575d, 0x806567cb, 0x196c3671, 0x6e6b06e7, 0xfed41b76, 0x89d32be0,
      0x10da7a5a, 0x67dd4acc, 0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5, 0xd6d6a3e8,
      0xa1d1937e, 0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
      0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55, 0x316e8eef,
      0x4669be79, 0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236, 0xcc0c7795, 0xbb0b4703,
      0x220216b9, 0x5505262f, 0xc5ba3bbe, 0xb2bd0b28, 0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7,
      0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d, 0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a,
      0x9c0906a9, 0xeb0e363f, 0x72076785, 0x05005713, 0x95bf4a82, 0xe2b87a14, 0x7bb12bae,
      0x0cb61b38, 0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21, 0x86d3d2d4, 0xf1d4e242,
      0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777, 0x88085ae6,
      0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69, 0x616bffd3, 0x166ccf45,
      0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2, 0xa7672661, 0xd06016f7, 0x4969474d,
      0x3e6e77db, 0xaed16a4a, 0xd9d65adc, 0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5,
      0x47b2cf7f, 0x30b5ffe9, 0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605,
      0xcdd70693, 0x54de5729, 0x23d967bf, 0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94,
      0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d };
}
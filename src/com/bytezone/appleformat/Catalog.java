package com.bytezone.appleformat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.FileCpm;
import com.bytezone.filesystem.FileDos;
import com.bytezone.filesystem.FilePascal;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.FsCpm;
import com.bytezone.filesystem.FsDos;
import com.bytezone.filesystem.FsPascal;
import com.bytezone.filesystem.FsProdos;
import com.bytezone.filesystem.ProdosConstants;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// -----------------------------------------------------------------------------------//
public class Catalog implements FormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  private static final DateTimeFormatter sdf = DateTimeFormatter.ofPattern ("d-LLL-yy");
  private static final DateTimeFormatter stf = DateTimeFormatter.ofPattern ("H:mm");
  private static final String NO_DATE = "<NO DATE>";

  private static final DateTimeFormatter dtf =
      DateTimeFormatter.ofLocalizedDate (FormatStyle.SHORT);

  AppleFile appleFile;

  // ---------------------------------------------------------------------------------//
  public Catalog (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    assert appleFile.isContainer ();

    this.appleFile = appleFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public AppleFile getAppleFile ()
  // ---------------------------------------------------------------------------------//
  {
    return appleFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileSystemType ())
    {
      case DOS -> getDosCatalog ();
      case PRODOS -> getProdosCatalog ();
      case PASCAL -> getPascalCatalog ();
      case CPM -> getCpmCatalog ();
      case DOS4 -> getDos4Catalog ();
      case IMG2 -> getImg2Catalog ();
      case GZIP -> getGzipCatalog ();
      case ZIP -> getZipCatalog ();
      case LBR -> getLbrCatalog ();
      case NUFX -> getNuFXCatalog ();
      default -> "dunno";
    };
  }

  // ---------------------------------------------------------------------------------//
  private String getSubType (FileProdos file)
  // ---------------------------------------------------------------------------------//
  {
    switch (file.getFileType ())
    {
      case ProdosConstants.FILE_TYPE_TEXT:
        return String.format ("R=%5d", file.getAuxType ());

      case ProdosConstants.FILE_TYPE_BINARY:
      case ProdosConstants.FILE_TYPE_PNT:
      case ProdosConstants.FILE_TYPE_PIC:
      case ProdosConstants.FILE_TYPE_FOT:
        return String.format ("A=$%4X", file.getAuxType ());

      //      case FILE_TYPE_AWP:
      //        int flags = Utility.intValue (buffer[i + 32], buffer[i + 31]);  // aux backwards!
      //        if (flags != 0)
      //          filename = convert (filename, flags);
      //        break;
    }

    return "";
  }

  // ---------------------------------------------------------------------------------//
  public String getCpmCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    FsCpm fs = (FsCpm) appleFile;

    String line = "----  ---------  ---  - -  ----\n";

    text.append ("User  Name       Typ  R S  Size\n");
    text.append (line);

    for (AppleFile file1 : fs.getFiles ())
    {
      FileCpm file = (FileCpm) file1;

      char ro = file.isReadOnly () ? '*' : ' ';
      char sf = file.isSystemFile () ? '*' : ' ';

      text.append (String.format ("%3d   %-8s   %-3s  %s %s   %03d%n",
          file.getUserNumber (), file.getShortName (), file.getFileTypeText (), ro, sf,
          file.getTotalBlocks ()));
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  public String getPascalCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    FsPascal fs = (FsPascal) appleFile;

    String line = "----   ---------------   ----   --------  -------   ----   ----";

    String date = fs.getDate () == null ? "--" : fs.getDate ().format (dtf);

    text.append (String.format ("Volume : %s%n", fs.getVolumeName ()));
    text.append (String.format ("Date   : %s%n%n", date));
    text.append ("Blks   Name              Type     Date     Length   Frst   Last\n");
    text.append (line);
    text.append ("\n");

    for (AppleFile file : fs.getFiles ())
    {
      text.append (String.format ("%4d   %-15s   %-4s   %8s  %,7d   %4d   %4d%n",
          file.getTotalBlocks (), file.getFileName (), file.getFileTypeText (),
          ((FilePascal) file).getDate ().format (dtf), file.getFileLength (),
          ((FilePascal) file).getFirstBlock (), ((FilePascal) file).getLastBlock ()));
    }

    text.append (line);
    text.append (String.format (
        "%nBlocks free : %3d  Blocks used : %3d  Total blocks : %3d", fs.getFreeBlocks (),
        fs.getTotalBlocks () - fs.getFreeBlocks (), fs.getTotalBlocks ()));

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  public String getDosCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append (getCatalog ());
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    String underline = "- --- ---  ------------------------------  -----  -------------"
        + "  -- ----  -------------------\n";

    StringBuilder text = new StringBuilder ();

    text.append ("L Typ Len  Name                            Addr"
        + "   Length         TS Data  Comment\n");
    text.append (underline);

    FsDos fs = (FsDos) appleFile;

    for (AppleFile file : fs.getFiles ())
    {
      String entry = getDetails ((FileDos) file);
      //      if (countEntries (fileEntry) > 1)
      //        entry += "** duplicate **";
      text.append (entry);
      text.append ("\n");
    }

    int totalSectors = fs.getTotalBlocks ();
    int freeSectors = fs.getFreeBlocks ();

    text.append (underline);
    text.append (String.format (
        "           Free sectors: %3d    " + "Used sectors: %3d    Total sectors: %3d",
        freeSectors, totalSectors - freeSectors, totalSectors));

    if (false)
      text.append (String.format (
          "%nActual:    Free sectors: %3d    "
              + "Used sectors: %3d    Total sectors: %3d",
          freeSectors, totalSectors - freeSectors, totalSectors));

    //    String volumeText = volumeNo == 0 ? "" : "Side " + volumeNo + " ";

    //    return new DefaultAppleFileSource (volumeText + "DOS Volume " + dosVTOCSector.volume,
    //        text.toString (), this);
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  String getDetails (FileDos file)
  // ---------------------------------------------------------------------------------//
  {
    int actualSize = file.getTotalIndexSectors () + file.getTotalDataSectors ()
        - file.getTextFileGaps ();

    String addressText =
        file.getAddress () == 0 ? "" : String.format ("$%4X", file.getAddress ());

    String lengthText = file.getFileLength () == 0 ? ""
        : String.format ("$%4X  %<,6d", file.getFileLength ());

    String message = "";
    String lockedFlag = (file.isLocked ()) ? "*" : " ";

    if (file.getSectorCount () != actualSize)
      message += "Actual size (" + actualSize + ") ";
    if (file.getTotalDataSectors () == 0)
      message += "No data ";
    if (file.getSectorCount () > 999)
      message += "Reported " + file.getSectorCount ();

    String text = String.format ("%1s  %1s  %03d  %-30.30s  %-5s  %-13s %3d %3d   %s",
        lockedFlag, file.getFileTypeText (), file.getSectorCount () % 1000,
        file.getFileName (), addressText, lengthText, file.getTotalIndexSectors (),
        (file.getTotalDataSectors () - file.getTextFileGaps ()), message.trim ());
    //    if (actualSize == 0)
    //      text = text.substring (0, 50);

    return text;
  }

  // ---------------------------------------------------------------------------------//
  public String getDos4Catalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append ("Still working on it");
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getGzipCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append ("Still working on it");
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getZipCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append ("Still working on it");
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getImg2Catalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append ("Still working on it");
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getLbrCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append ("Still working on it");
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getNuFXCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    text.append ("Still working on it");
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getProdosCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    FsProdos fs = null;

    if (appleFile.isFileSystem ())
    {
      fs = (FsProdos) appleFile;     // could be FolderProdos!!
      String root = appleFile.isFileSystem () ? "/" : "";
      text.append (root + fs.getVolumeName () + "\n\n");
    }
    else
      fs = (FsProdos) appleFile.getFileSystem ();

    text.append (" NAME           TYPE  BLOCKS  "
        + "MODIFIED         CREATED          ENDFILE SUBTYPE" + "\n\n");

    for (AppleFile file : appleFile.getFiles ())
    {
      if (file.isFile ())
      {
        LocalDateTime created = ((FileProdos) file).getCreated ();
        LocalDateTime modified = ((FileProdos) file).getModified ();

        String dateCreated = created == null ? NO_DATE : created.format (sdf);
        String timeCreated = created == null ? "" : created.format (stf);
        String dateModified = modified == null ? NO_DATE : modified.format (sdf);
        String timeModified = modified == null ? "" : modified.format (stf);

        String forkFlag = file.isForkedFile () ? "+" : " ";

        text.append (
            String.format ("%s%-15s %3s%s  %5d  %9s %5s  %9s %5s %8d %7s    %04X%n",
                file.isLocked () ? "*" : " ", file.getFileName (),
                file.getFileTypeText (), forkFlag, file.getTotalBlocks (), dateModified,
                timeModified, dateCreated, timeCreated, file.getFileLength (),
                getSubType ((FileProdos) file), ((FileProdos) file).getAuxType ()));
      }
      else if (file.isFolder ())
      {
        text.append (file.getFileName () + "\n");
      }
      else if (file.isForkedFile ())
      {
        text.append (file.getFileName () + "\n");
      }
      else if (file.isFileSystem ())
        text.append (((AppleFileSystem) file).getAppleFile ().getFileName () + "\n");
    }

    int totalBlocks = fs.getTotalBlocks ();
    int freeBlocks = fs.getFreeBlocks ();

    text.append (
        String.format ("%nBLOCKS FREE:%5d     BLOCKS USED:%5d     TOTAL BLOCKS:%5d%n",
            freeBlocks, totalBlocks - freeBlocks, totalBlocks));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return "";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void writeGraphics (GraphicsContext gc)
  // ---------------------------------------------------------------------------------//
  {
    Canvas canvas = gc.getCanvas ();

    canvas.setWidth (1);
    canvas.setHeight (1);

    gc.setFill (Color.WHITE);
    gc.fillRect (0, 0, 1, 1);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    // return catalog blocks?
    return null;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return 0;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int getLength ()
  // ---------------------------------------------------------------------------------//
  {
    return 0;
  }
}

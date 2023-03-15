package com.bytezone.appleformat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.FileCpm;
import com.bytezone.filesystem.FileDos;
import com.bytezone.filesystem.FileNuFX;
import com.bytezone.filesystem.FilePascal;
import com.bytezone.filesystem.FileProdos;
import com.bytezone.filesystem.FolderNuFX;
import com.bytezone.filesystem.FolderProdos;
import com.bytezone.filesystem.ForkProdos;
import com.bytezone.filesystem.FsCpm;
import com.bytezone.filesystem.FsDos;
import com.bytezone.filesystem.FsNuFX;
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

  private static final String UNDERLINE_NUFX =
      "------------------------------------------------------"
          + "-----------------------";

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
      case UNIDOS -> getUnidosCatalog ();
      case HYBRID -> getHybridCatalog ();
      case BIN2 -> getBin2Catalog ();
      case WOZ1 -> getWoz1Catalog ();
      case WOZ2 -> getWoz2Catalog ();
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

    String underline = "- --- ---  ------------------------------  -----  -------------"
        + "  -- ----  -------------------\n";

    text.append ("L Typ Len  Name                            Addr"
        + "   Length         TS Data  Comment\n");
    text.append (underline);

    FsDos fs = (FsDos) appleFile;

    for (AppleFile file : fs.getFiles ())
    {
      //      if (countEntries (fileEntry) > 1)
      //        entry += "** duplicate **";
      text.append (getFileDetails ((FileDos) file));
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

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  String getFileDetails (FileDos file)
  // ---------------------------------------------------------------------------------//
  {
    int actualSize = file.getTotalIndexSectors () + file.getTotalDataSectors ();

    String addressText =
        file.getAddress () == 0 ? "" : String.format ("$%4X", file.getAddress ());

    String lengthText = file.getFileLength () == 0 ? ""
        : String.format ("$%4X  %<,6d", file.getFileLength ());

    String message = "";
    String lockedFlag = (file.isLocked ()) ? "*" : " ";

    if (file.getSectorCount () != actualSize)
      message += "Actual size (" + actualSize + ") ";
    //    if (file.getTotalDataSectors () == 0)
    //      message += "No data ";
    if (file.getSectorCount () > 999)
      message += "Reported " + file.getSectorCount ();

    String text = String.format ("%1s  %1s  %03d  %-30.30s  %-5s  %-13s %3d %3d   %s",
        lockedFlag, file.getFileTypeText (), file.getSectorCount () % 1000,
        file.getFileName (), addressText, lengthText, file.getTotalIndexSectors (),
        file.getTotalDataSectors (), message.trim ());

    //    if (actualSize == 0)
    //      text = text.substring (0, 50);

    return text;
  }

  // ---------------------------------------------------------------------------------//
  public String getDos4Catalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getGzipCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getZipCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getImg2Catalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getLbrCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getNuFXCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();
    String threadFormats[] = { "unc", "sq ", "lz1", "lz2", "", "" };

    if (appleFile instanceof FileNuFX)          // forked file
    {
      for (AppleFile file2 : appleFile.getFiles ())
        text.append (file2.getFileName () + "\n");
      return text.toString ();
    }

    if (appleFile instanceof FolderNuFX)
    {
      for (AppleFile file2 : appleFile.getFiles ())
        text.append (file2.getFileName () + "\n");
      return text.toString ();
    }

    if (appleFile instanceof FsNuFX fs)
    {
      text.append (String.format (" %-15.15s Created:%-17s Mod:%-17s   Recs:%5d%n%n",
          fs.getFileName (), fs.getCreated ().format2 (), fs.getModified ().format2 (),
          fs.getFiles ().size ()));

      text.append (" Name                        Type Auxtyp Archived"
          + "         Fmat Size Un-Length\n");

      text.append (String.format ("%s%n", UNDERLINE_NUFX));
    }

    int totalUncompressedSize = 0;
    int totalCompressedSize = 0;

    for (AppleFile file : appleFile.getFiles ())
    {
      if (file instanceof FileNuFX file2)
      {
        String lockedFlag = (file2.getAccess () | 0xC3) == 1 ? "+" : " ";
        String forkedFlag = file2.hasResource () ? "+" : " ";

        if (file2.hasDisk ())
          return String.format ("%s%-27.27s %-4s %-6s %-15s  %s  %3.0f%%   %7d%n",
              lockedFlag, file.getFileName (), "Disk",
              (file2.getUncompressedSize () / 1024) + "k",
              file2.getArchived ().format2 (), threadFormats[file2.getThreadFormat ()],
              file2.getCompressedPct (), file2.getUncompressedSize ());
        else
          text.append (String.format ("%s%-27.27s %s%s $%04X  %-15s  %s  %3.0f%%   %7d%n",
              lockedFlag, file2.getFullFileName (), file.getFileTypeText (), forkedFlag,
              file2.getAuxType (), file2.getArchived ().format2 (),
              threadFormats[file2.getThreadFormat ()], file2.getCompressedPct (),
              file2.getUncompressedSize ()));

        totalUncompressedSize += file2.getUncompressedSize ();
        totalCompressedSize += file2.getCompressedSize ();
      }
      else
        text.append (file.getFileName () + "\n");
    }

    if (appleFile instanceof FsNuFX)
    {
      text.append (String.format ("%s%n", UNDERLINE_NUFX));

      float pct = 0;
      if (totalUncompressedSize > 0)
        pct = totalCompressedSize * 100 / totalUncompressedSize;
      text.append (String.format (" Uncomp:%7d  Comp:%7d  %%of orig:%3.0f%%%n%n",
          totalUncompressedSize, totalCompressedSize, pct));
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getHybridCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getUnidosCatalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (String.format ("%s%n", file.getFileName ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getBin2Catalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getWoz1Catalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  public String getWoz2Catalog ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (AppleFile file : appleFile.getFiles ())
      text.append (
          String.format ("%-15s %s%n", file.getFileName (), file.getFileSystemType ()));

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
      fs = (FsProdos) appleFile;
      String root = appleFile.isFileSystem () ? "/" : "";
      text.append (root + fs.getVolumeName () + "\n\n");
    }
    else
      fs = (FsProdos) appleFile.getFileSystem ();

    text.append (" NAME           TYPE  BLOCKS  "
        + "MODIFIED         CREATED          ENDFILE SUBTYPE" + "\n\n");

    for (AppleFile file : appleFile.getFiles ())
    {
      if (file.isFileSystem ())                             // LBR or PAR
        file = ((AppleFileSystem) file).getAppleFile ();

      if (file.isFork ())
      {
        FileProdos parent = ((ForkProdos) file).getParentFile ();

        LocalDateTime created = parent.getCreated ();
        LocalDateTime modified = parent.getModified ();

        String dateCreated = created == null ? NO_DATE : created.format (sdf);
        String timeCreated = created == null ? "" : created.format (stf);
        String dateModified = modified == null ? NO_DATE : modified.format (sdf);
        String timeModified = modified == null ? "" : modified.format (stf);

        text.append (String.format (" %-15s       %5d  %9s %5s  %9s %5s %8d%n",
            file.getFileName (), file.getTotalBlocks (), dateModified, timeModified,
            dateCreated, timeCreated, file.getFileLength (), file.getFileLength ()));
      }
      else if (file.isFile ())
      {
        FileProdos prodos = (FileProdos) file;

        LocalDateTime created = prodos.getCreated ();
        LocalDateTime modified = prodos.getModified ();

        int fileLength = file.isForkedFile () ? 0 : file.getFileLength ();

        String dateCreated = created == null ? NO_DATE : created.format (sdf);
        String timeCreated = created == null ? "" : created.format (stf);
        String dateModified = modified == null ? NO_DATE : modified.format (sdf);
        String timeModified = modified == null ? "" : modified.format (stf);

        String forkFlag = file.isForkedFile () ? "+" : " ";

        text.append (String.format (
            "%s%-15s %3s%s  %5d  %9s %5s  %9s %5s %8d %7s    %04X%n",
            file.isLocked () ? "*" : " ", file.getFileName (), file.getFileTypeText (),
            forkFlag, file.getTotalBlocks (), dateModified, timeModified, dateCreated,
            timeCreated, fileLength, getSubType (prodos), prodos.getAuxType ()));
      }
      else if (file.isFolder ())
      {
        LocalDateTime created = ((FolderProdos) file).getCreated ();
        LocalDateTime modified = ((FolderProdos) file).getModified ();

        String dateCreated = created == null ? NO_DATE : created.format (sdf);
        String timeCreated = created == null ? "" : created.format (stf);
        String dateModified = modified == null ? NO_DATE : modified.format (sdf);
        String timeModified = modified == null ? "" : modified.format (stf);

        text.append (String.format ("%s%-15s %3s   %5d  %9s %5s  %9s %5s %8d %n",
            file.isLocked () ? "*" : " ", file.getFileName (), file.getFileTypeText (),
            file.getTotalBlocks (), dateModified, timeModified, dateCreated, timeCreated,
            file.getFileLength ()));
      }
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

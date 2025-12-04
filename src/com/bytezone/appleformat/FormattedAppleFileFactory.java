package com.bytezone.appleformat;

import java.io.File;
import java.util.prefs.Preferences;

import com.bytezone.appleformat.basic.BasicCpmProgram;
import com.bytezone.appleformat.file.Catalog;
import com.bytezone.appleformat.file.DataFile;
import com.bytezone.appleformat.file.DosMaster;
import com.bytezone.appleformat.file.FormattedAppleFile;
import com.bytezone.appleformat.file.LocalFolder;
import com.bytezone.appleformat.file.PascalCode;
import com.bytezone.appleformat.file.PascalProcedure;
import com.bytezone.appleformat.file.PascalSegment;
import com.bytezone.appleformat.text.CpmText;
import com.bytezone.appleformat.text.PascalText;
import com.bytezone.filesystem.AppleContainer;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.FileBinary2;
import com.bytezone.filesystem.FileCpm;
import com.bytezone.filesystem.FileDos;
import com.bytezone.filesystem.FileDosMaster;

// -----------------------------------------------------------------------------------//
public class FormattedAppleFileFactory
// -----------------------------------------------------------------------------------//
{
  static final int RETURN = 0x0D;
  static final int SEMI_COLON = 0x3B;
  static final int ASTERISK = 0x2A;
  static final int SPACE = 0x20;
  static final int DOUBLE_QUOTE = 0x22;
  static final int SINGLE_QUOTE = 0x27;

  PreferencesFactory preferencesFactory;

  FactoryDos factoryDos = new FactoryDos ();
  FactoryProdos factoryProdos = new FactoryProdos ();
  FactoryNuFX factoryNuFX = new FactoryNuFX ();
  FactoryBin2 factoryBin2 = new FactoryBin2 ();

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFileFactory (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    preferencesFactory = new PreferencesFactory (prefs);
  }

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    assert localFile.isDirectory ();

    return new LocalFolder (localFile);
  }

  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFileSystem appleFileSystem)
  // ---------------------------------------------------------------------------------//
  {
    return new Catalog (appleFileSystem);
  }

  // NB - whenever an AppleFile's getFileBuffer() is called a Buffer is returned
  //      based on all of its data blocks but with length() equal to its EOF. If
  //      there is no EOF set, then length() is set to buffer.length().
  //      Random-access files will use TextBlocks instead.
  // ---------------------------------------------------------------------------------//
  public FormattedAppleFile getFormattedAppleFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      //      if (appleFile.isForkedFile ())
      //        return new Catalog ((AppleForkedFile) appleFile);

      //      System.out.printf ("%s %d %n", appleFile instanceof AppleContainer,
      //          appleFile.getFileType ());

      if (appleFile instanceof AppleContainer container)    //
      {
        if (appleFile instanceof FileDosMaster dosMaster)
          return new DosMaster (appleFile);

        if (appleFile.getFileType () == 0                // ??
            | appleFile.getFileType () == 15)           // directory file
          return new Catalog (container);
      }

      return switch (appleFile.getFileSystemType ())
      {
        case DOS3, DOS4 -> factoryDos.getFormattedDosFile ((FileDos) appleFile);
        case PRODOS -> factoryProdos.getFormattedProdosFile (appleFile);
        case PASCAL -> getFormattedPascalFile (appleFile);
        case CPM -> getFormattedCpmFile ((FileCpm) appleFile);
        case NUFX -> factoryNuFX.getFormattedNufxFile (appleFile);
        case BIN2 -> factoryBin2.getFormattedBin2File ((FileBinary2) appleFile);
        default -> new DataFile (appleFile);
      };
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      return new FormatError (e);
    }
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedPascalFile (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case 3 -> new PascalText (appleFile);
      case 2 -> new PascalCode (appleFile);
      case 98 -> new PascalSegment (appleFile);         // not a real file type
      case 99 -> new PascalProcedure (appleFile);       // not a real file type
      default -> new DataFile (appleFile);
    };
  }

  // ---------------------------------------------------------------------------------//
  private FormattedAppleFile getFormattedCpmFile (FileCpm appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileTypeText ())
    {
      case "DOC" -> new CpmText (appleFile);
      case "HLP" -> new CpmText (appleFile);
      case "TXT" -> new CpmText (appleFile);
      case "ASM" -> new CpmText (appleFile);
      case "BAS" -> new BasicCpmProgram (appleFile);
      default -> new DataFile (appleFile);
    };
  }

  // ---------------------------------------------------------------------------------//
  public ApplePreferences getPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return preferencesFactory.getPreferences (appleFile);
  }

  // ---------------------------------------------------------------------------------//
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    preferencesFactory.save ();
  }
}

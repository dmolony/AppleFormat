package com.bytezone.appleformat;

import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ADB;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ANI;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_APPLESOFT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ASP;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_AWP;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BAT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_BINARY;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FNT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_FONT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_ICN;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_INTEGER_BASIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_NON;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PIC;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_PNT;
import static com.bytezone.appleformat.ProdosConstants.FILE_TYPE_TEXT;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.assembler.AssemblerPreferences;
import com.bytezone.appleformat.basic.ApplesoftBasicPreferences;
import com.bytezone.appleformat.graphics.GraphicsPreferences;
import com.bytezone.appleformat.text.TextPreferences;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.Buffer;

// -----------------------------------------------------------------------------------//
public class PreferencesFactory
// -----------------------------------------------------------------------------------//
{
  public static ApplesoftBasicPreferences basicPreferences;
  public static AssemblerPreferences assemblerPreferences;
  public static GraphicsPreferences graphicsPreferences;
  public static TextPreferences textPreferences;

  // ---------------------------------------------------------------------------------//
  public PreferencesFactory (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    basicPreferences = new ApplesoftBasicPreferences (prefs);
    assemblerPreferences = new AssemblerPreferences (prefs);
    graphicsPreferences = new GraphicsPreferences (prefs);
    textPreferences = new TextPreferences (prefs);
  }

  // ---------------------------------------------------------------------------------//
  public ApplePreferences getPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileSystemType ())
    {
      case DOS3, DOS4 -> getDosPreferences (appleFile);
      case PRODOS -> getProdosPreferences (appleFile);
      case PASCAL -> getPascalPreferences (appleFile);
      case CPM -> getCpmPreferences (appleFile);
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getDosPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case 0 -> textPreferences;
      case 1 -> null;
      case 2, 32 -> basicPreferences;
      case 4, 16, 64 -> getDosBinaryPreferences (appleFile);
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getDosBinaryPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    Buffer dataRecord = appleFile.getRawFileBuffer ();
    byte[] buffer = dataRecord.data ();

    int address = Utility.getShort (buffer, 0);
    int length = Utility.getShort (buffer, 2);

    if ((address == 0x2000 || address == 0x4000)        // hi-res page address
        && (length > 0x1F00 && length <= 0x4000))
      return graphicsPreferences;

    return assemblerPreferences;
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getProdosPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case FILE_TYPE_TEXT -> textPreferences;
      case FILE_TYPE_BINARY -> assemblerPreferences;
      case FILE_TYPE_PNT -> graphicsPreferences;
      case FILE_TYPE_PIC -> graphicsPreferences;
      case FILE_TYPE_ANI -> graphicsPreferences;
      case FILE_TYPE_FNT -> graphicsPreferences;
      case FILE_TYPE_FONT -> graphicsPreferences;
      case FILE_TYPE_APPLESOFT -> basicPreferences;
      case FILE_TYPE_INTEGER_BASIC -> null;
      case FILE_TYPE_ASP -> null;
      case FILE_TYPE_AWP -> textPreferences;
      case FILE_TYPE_ADB -> null;
      case FILE_TYPE_ICN -> graphicsPreferences;
      case FILE_TYPE_BAT -> textPreferences;
      case FILE_TYPE_NON -> null;
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getPascalPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileType ())
    {
      case 3 -> textPreferences;
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  private ApplePreferences getCpmPreferences (AppleFile appleFile)
  // ---------------------------------------------------------------------------------//
  {
    return switch (appleFile.getFileTypeText ())
    {
      case "DOC" -> textPreferences;
      case "HLP" -> textPreferences;
      case "TXT" -> textPreferences;
      case "ASM" -> textPreferences;
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------//
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    basicPreferences.save ();
    assemblerPreferences.save ();
    textPreferences.save ();
    graphicsPreferences.save ();
  }
}

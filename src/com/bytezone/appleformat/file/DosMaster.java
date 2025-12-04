package com.bytezone.appleformat.file;

import java.util.List;
import java.util.Optional;

import com.bytezone.appleformat.Utility;
import com.bytezone.appleformat.basic.ApplesoftBasicProgram;
import com.bytezone.filesystem.AppleFile;
import com.bytezone.filesystem.AppleFileSystem;
import com.bytezone.filesystem.FileDosMaster;

// -----------------------------------------------------------------------------------//
public class DosMaster extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  private final static String NO_MENU = "Menu not found";
  // ---------------------------------------------------------------------------------//
  public DosMaster (AppleFile dosMaster)
  // ---------------------------------------------------------------------------------//
  {
    super (dosMaster);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    List<AppleFileSystem> fileSystems = ((FileDosMaster) appleFile).getFileSystems ();

    if (fileSystems.size () == 0)
      return NO_MENU;

    Optional<AppleFile> opt = fileSystems.get (0).getFile ("HELLO");
    if (opt.isEmpty ())
      return NO_MENU;

    ApplesoftBasicProgram hello = new ApplesoftBasicProgram (opt.get ());
    List<String> dataItems = hello.getDataItems ();
    if (dataItems.size () % 3 != 0)
      return NO_MENU;

    text.append ("#    GAME TITLE                       VOLUME\n");
    text.append ("--------------------------------------------\n");

    int count = 1;
    for (int i = 0; i < dataItems.size (); i += 3)
    {
      String title = dataItems.get (i);
      String program = dataItems.get (i + 1);
      String volume = dataItems.get (i + 2);

      if (title.startsWith ("\"") && title.endsWith ("\""))
        title = title.substring (1, title.length () - 1);

      if (Utility.isPossibleNumber (title.getBytes ()[0]))
        return "";
      if (Utility.isPossibleNumber (program.getBytes ()[0]))
        return "";

      text.append (String.format ("%-3d  %-32s %s%n", count++, title, volume));
    }

    return Utility.rtrim (text);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected String buildExtras ()
  // ---------------------------------------------------------------------------------//
  {
    return ((FileDosMaster) appleFile).getCatalogText ();
  }
}

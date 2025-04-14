package com.bytezone.appleformat.file;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.scene.image.Image;

// -----------------------------------------------------------------------------------//
public class LocalFolder extends AbstractFormattedAppleFile
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public LocalFolder (File localFile)
  // ---------------------------------------------------------------------------------//
  {
    super (localFile);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected Image buildImage ()
  // ---------------------------------------------------------------------------------//
  {
    return emptyImage;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String buildText ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ("  File size    File name\n");

    text.append ("------------  ---------------------------------"
        + "------------------------------------------\n");

    File[] files = localFile.listFiles ();
    Arrays.sort (files);

    for (File file : files)
      if (file.isDirectory ())
        text.append (String.format ("              %s %n", file.getName ()));
      else
      {
        String fileName = file.getName ();
        if (!file.isHidden () && !fileName.startsWith ("."))
          text.append (String.format ("%,12d  %s \n", file.length (), fileName));
      }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected List<String> buildHex ()
  // ---------------------------------------------------------------------------------//
  {
    return List.of ("No hex");
  }
}

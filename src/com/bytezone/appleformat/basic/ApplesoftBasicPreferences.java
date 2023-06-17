package com.bytezone.appleformat.basic;

import java.util.prefs.Preferences;

import com.bytezone.appleformat.ApplePreferences;
import com.bytezone.utility.Utility;

// -----------------------------------------------------------------------------------//
public class ApplesoftBasicPreferences extends ApplePreferences
// -----------------------------------------------------------------------------------//
{
  private static String PREFS_DISPLAY_FORMAT = "DisplayFormat";
  private static String PREFS_SPLIT_REM = "SplitRem";
  private static String PREFS_SPLIT_DIM = "SplitDim";
  private static String PREFS_ALIGN_ASSIGN = "AlignAssign";
  private static String PREFS_SHOW_CARET = "ShowCaret";
  private static String PREFS_SHOW_THEN = "ShowThen";
  private static String PREFS_SHOW_GOSUB = "ShowGoto";
  private static String PREFS_SHOW_CALLS = "ShowCalls";
  private static String PREFS_SHOW_SYMBOLS = "ShowSymbols";
  private static String PREFS_SHOW_CONSTANTS = "ShowConstants";
  private static String PREFS_SHOW_FUNCTIONS = "ShowFunctions";
  private static String PREFS_SHOW_DUPLICATES = "ShowDuplicates";
  private static String PREFS_BLANK_AFTER_RETURN = "BlankAfterReturn";
  private static String PREFS_FORMAT_REM = "FormatRem";
  private static String PREFS_DELETE_EXTRA_SPACE = "DeleteExtraSpace";

  public int displayFormat;              // 0 = No format, 1 = User, 2 = 40 col

  public boolean splitRem;
  public boolean splitDim;
  public boolean alignAssign;
  public boolean showCaret;
  public boolean showThen;
  public boolean blankAfterReturn;
  public boolean formatRem;
  public boolean deleteExtraDataSpace;

  public boolean showGosubGoto;
  public boolean showCalls;
  public boolean showSymbols;
  public boolean showFunctions;
  public boolean showConstants;
  public boolean showDuplicateSymbols;

  public int wrapPrintAt = 80;
  public int wrapRemAt = 80;
  public int wrapDataAt = 80;

  // ---------------------------------------------------------------------------------//
  public ApplesoftBasicPreferences (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    super ("Applesoft Basic Preferences", prefs);

    boolean defaultValue = false;

    displayFormat = prefs.getInt (PREFS_DISPLAY_FORMAT, 1);
    splitRem = prefs.getBoolean (PREFS_SPLIT_REM, defaultValue);
    splitDim = prefs.getBoolean (PREFS_SPLIT_DIM, defaultValue);
    alignAssign = prefs.getBoolean (PREFS_ALIGN_ASSIGN, defaultValue);

    showCaret = prefs.getBoolean (PREFS_SHOW_CARET, defaultValue);
    showThen = prefs.getBoolean (PREFS_SHOW_THEN, defaultValue);
    blankAfterReturn = prefs.getBoolean (PREFS_BLANK_AFTER_RETURN, defaultValue);
    formatRem = prefs.getBoolean (PREFS_FORMAT_REM, defaultValue);
    deleteExtraDataSpace = prefs.getBoolean (PREFS_DELETE_EXTRA_SPACE, defaultValue);

    showSymbols = prefs.getBoolean (PREFS_SHOW_SYMBOLS, defaultValue);
    showDuplicateSymbols = prefs.getBoolean (PREFS_SHOW_DUPLICATES, defaultValue);
    showFunctions = prefs.getBoolean (PREFS_SHOW_FUNCTIONS, defaultValue);
    showConstants = prefs.getBoolean (PREFS_SHOW_CONSTANTS, defaultValue);
    showGosubGoto = prefs.getBoolean (PREFS_SHOW_GOSUB, defaultValue);
    showCalls = prefs.getBoolean (PREFS_SHOW_CALLS, defaultValue);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    prefs.putInt (PREFS_DISPLAY_FORMAT, displayFormat);
    prefs.putBoolean (PREFS_SPLIT_REM, splitRem);
    prefs.putBoolean (PREFS_SPLIT_DIM, splitDim);
    prefs.putBoolean (PREFS_ALIGN_ASSIGN, alignAssign);

    prefs.putBoolean (PREFS_SHOW_CARET, showCaret);
    prefs.putBoolean (PREFS_SHOW_THEN, showThen);
    prefs.putBoolean (PREFS_BLANK_AFTER_RETURN, blankAfterReturn);
    prefs.putBoolean (PREFS_FORMAT_REM, formatRem);
    prefs.putBoolean (PREFS_DELETE_EXTRA_SPACE, deleteExtraDataSpace);

    prefs.putBoolean (PREFS_SHOW_SYMBOLS, showSymbols);
    prefs.putBoolean (PREFS_SHOW_DUPLICATES, showDuplicateSymbols);
    prefs.putBoolean (PREFS_SHOW_FUNCTIONS, showFunctions);
    prefs.putBoolean (PREFS_SHOW_CONSTANTS, showConstants);
    prefs.putBoolean (PREFS_SHOW_GOSUB, showGosubGoto);
    prefs.putBoolean (PREFS_SHOW_CALLS, showCalls);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder (super.toString ());

    text.append (String.format ("Display format ........... %d%n", displayFormat));
    text.append (String.format ("Split REM ................ %s%n", splitRem));
    text.append (String.format ("Split DIM ................ %s%n", splitDim));
    text.append (String.format ("Align assign ............. %s%n", alignAssign));

    text.append (String.format ("Show caret ............... %s%n", showCaret));
    text.append (String.format ("Show THEN ................ %s%n", showThen));
    text.append (String.format ("Blank after RETURN ....... %s%n", blankAfterReturn));
    text.append (String.format ("Format REM ............... %s%n", formatRem));
    text.append (String.format ("Delete extra DATA space .. %s%n", deleteExtraDataSpace));

    text.append (String.format ("Show symbols ............. %s%n", showSymbols));
    text.append (String.format ("Show duplicate symbols ... %s%n", showDuplicateSymbols));
    text.append (String.format ("Show functions ........... %s%n", showFunctions));
    text.append (String.format ("Show constants ........... %s%n", showConstants));
    text.append (String.format ("Show GOTO/GOSUB .......... %s%n", showGosubGoto));
    text.append (String.format ("Show CALL ................ %s%n", showCalls));

    text.append (String.format ("Wrap PRINT at ............ %d%n", wrapPrintAt));
    text.append (String.format ("Wrap REM at .............. %d%n", wrapRemAt));
    text.append (String.format ("Wrap DATA at ............. %d%n", wrapDataAt));

    return Utility.rtrim (text);
  }
}

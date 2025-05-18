package com.bytezone.appleformat.basic;

import static com.bytezone.utility.Utility.formatText;

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
  private static String PREFS_WRAP_PRINT = "WrapPrint";
  private static String PREFS_WRAP_PRINT_AT = "WrapPrintAt";
  private static String PREFS_HIDE_LET = "HideLet";
  private static String PREFS_SHOW_UNREACHABLE_CODE = "ShowUnreachableCode";

  public int displayFormat;          // 0 = No format, 1 = 40 col, 2 = Hex, 3 = User

  public boolean splitRem;
  public boolean splitDim;
  public boolean alignAssign;
  public boolean showCaret;
  public boolean showThen;
  public boolean blankAfterReturn;
  public boolean formatRem;
  public boolean deleteExtraDataSpace;
  public boolean hideLet;
  public boolean wrapPrint;

  public boolean showGosubGoto;
  public boolean showCalls;
  public boolean showSymbols;
  public boolean showFunctions;
  public boolean showConstants;
  public boolean showDuplicateSymbols;
  public boolean showUnreachableCode;

  public int wrapPrintAt = 40;
  public int wrapRemAt = 80;
  public int wrapDataAt = 80;

  // ---------------------------------------------------------------------------------//
  public ApplesoftBasicPreferences (Preferences preferences)
  // ---------------------------------------------------------------------------------//
  {
    super ("Applesoft Basic Preferences", preferences, OptionsType.APPLESOFT);

    boolean defaultValue = true;

    displayFormat = preferences.getInt (PREFS_DISPLAY_FORMAT, 3);

    splitRem = preferences.getBoolean (PREFS_SPLIT_REM, defaultValue);
    splitDim = preferences.getBoolean (PREFS_SPLIT_DIM, defaultValue);
    alignAssign = preferences.getBoolean (PREFS_ALIGN_ASSIGN, defaultValue);

    showCaret = preferences.getBoolean (PREFS_SHOW_CARET, defaultValue);
    showThen = preferences.getBoolean (PREFS_SHOW_THEN, defaultValue);
    blankAfterReturn = preferences.getBoolean (PREFS_BLANK_AFTER_RETURN, defaultValue);
    formatRem = preferences.getBoolean (PREFS_FORMAT_REM, defaultValue);
    deleteExtraDataSpace =
        preferences.getBoolean (PREFS_DELETE_EXTRA_SPACE, defaultValue);

    showSymbols = preferences.getBoolean (PREFS_SHOW_SYMBOLS, defaultValue);
    showDuplicateSymbols = preferences.getBoolean (PREFS_SHOW_DUPLICATES, defaultValue);
    showFunctions = preferences.getBoolean (PREFS_SHOW_FUNCTIONS, defaultValue);
    showConstants = preferences.getBoolean (PREFS_SHOW_CONSTANTS, defaultValue);
    showGosubGoto = preferences.getBoolean (PREFS_SHOW_GOSUB, defaultValue);
    showCalls = preferences.getBoolean (PREFS_SHOW_CALLS, defaultValue);

    wrapPrintAt = preferences.getInt (PREFS_WRAP_PRINT_AT, 40);
    hideLet = preferences.getBoolean (PREFS_HIDE_LET, defaultValue);
    wrapPrint = preferences.getBoolean (PREFS_WRAP_PRINT, defaultValue);
    showUnreachableCode =
        preferences.getBoolean (PREFS_SHOW_UNREACHABLE_CODE, defaultValue);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void save ()
  // ---------------------------------------------------------------------------------//
  {
    preferences.putInt (PREFS_DISPLAY_FORMAT, displayFormat);
    preferences.putBoolean (PREFS_SPLIT_REM, splitRem);
    preferences.putBoolean (PREFS_SPLIT_DIM, splitDim);
    preferences.putBoolean (PREFS_ALIGN_ASSIGN, alignAssign);

    preferences.putBoolean (PREFS_SHOW_CARET, showCaret);
    preferences.putBoolean (PREFS_SHOW_THEN, showThen);
    preferences.putBoolean (PREFS_BLANK_AFTER_RETURN, blankAfterReturn);
    preferences.putBoolean (PREFS_FORMAT_REM, formatRem);
    preferences.putBoolean (PREFS_DELETE_EXTRA_SPACE, deleteExtraDataSpace);

    preferences.putBoolean (PREFS_SHOW_SYMBOLS, showSymbols);
    preferences.putBoolean (PREFS_SHOW_DUPLICATES, showDuplicateSymbols);
    preferences.putBoolean (PREFS_SHOW_FUNCTIONS, showFunctions);
    preferences.putBoolean (PREFS_SHOW_CONSTANTS, showConstants);
    preferences.putBoolean (PREFS_SHOW_GOSUB, showGosubGoto);
    preferences.putBoolean (PREFS_SHOW_CALLS, showCalls);

    preferences.putInt (PREFS_WRAP_PRINT_AT, wrapPrintAt);
    preferences.putBoolean (PREFS_HIDE_LET, hideLet);
    preferences.putBoolean (PREFS_WRAP_PRINT, wrapPrint);
    preferences.putBoolean (PREFS_SHOW_UNREACHABLE_CODE, showUnreachableCode);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    formatText (text, "Display format", 2, displayFormat);
    formatText (text, "Split REM", splitRem);
    formatText (text, "Split DIM", splitDim);
    formatText (text, "Align assign", alignAssign);

    formatText (text, "Show caret", showCaret);
    formatText (text, "Show THEN", showThen);
    formatText (text, "Blank after RETURN", blankAfterReturn);
    formatText (text, "Format REM", formatRem);
    formatText (text, "Delete DATA space", deleteExtraDataSpace);

    formatText (text, "Show symbols", showSymbols);
    formatText (text, "Show duplicate symbols", showDuplicateSymbols);
    formatText (text, "Show functions", showFunctions);
    formatText (text, "Show constants", showConstants);
    formatText (text, "Show GOTO/GOSUB", showGosubGoto);
    formatText (text, "Show CALL", showCalls);
    formatText (text, "Show unreachable", showUnreachableCode);

    formatText (text, "Wrap PRINT", wrapPrint);
    formatText (text, "Wrap PRINT at", 2, wrapPrintAt);
    formatText (text, "Wrap REM at", 2, wrapRemAt);
    formatText (text, "Wrap DATA at", 2, wrapDataAt);
    formatText (text, "Hide LET", hideLet);

    return Utility.rtrim (text);
  }
}

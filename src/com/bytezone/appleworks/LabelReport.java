package com.bytezone.appleworks;

// -----------------------------------------------------------------------------------//
class LabelReport extends Report
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  LabelReport (AppleworksADBFile parent, byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    super (parent, buffer, offset);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText ()
  // ---------------------------------------------------------------------------------//
  {
    return "Skipping vertical report\n";
  }
}

package com.optimasc.text.html;

import java.awt.Font;

public class FontHelper
{
  /** Default or unspecified font family. */
  public static final String FONT_FAMILY_LABEL_DEFAULT = "Default";
  /** Serif font family */
  public static final String FONT_FAMILY_LABEL_SERIF   = Font.SERIF;
  /** Sans serif font family. */
  public static final String FONT_FAMILY_LABEL_SANS_SERIF = Font.SANS_SERIF;
  /** Monospace font family. */
  public static final String FONT_FAMILY_LABEL_MONOSPACE = Font.MONOSPACED;

  public static final int FONT_SIZE_SMALL = 0;
  public static final int FONT_SIZE_MEDIUM = 1;
  public static final int FONT_SIZE_LARGE = 2;
  
  public static final String FONT_SIZE_LABEL_SMALL = "small";
  public static final String FONT_SIZE_LABEL_MEDIUM = "medium";
  public static final String FONT_SIZE_LABEL_LARGE = "large";
  
  public static final int FONT_SIZES[] = {FONT_SIZE_SMALL, FONT_SIZE_MEDIUM, FONT_SIZE_LARGE};
      
  public static final String FONT_SIZE_LABELS[] =
  {
      FONT_SIZE_LABEL_SMALL, FONT_SIZE_LABEL_MEDIUM, FONT_SIZE_LABEL_LARGE
  };

  
}

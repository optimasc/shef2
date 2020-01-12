package com.optimasc.text;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

/** Interface used to set and clear standard attributes. */
public interface AttributeHelper
{
  /** Returns the specified alignment, as defined in 
   *  {@link javax.swing.text.StyleConstants} class, or -1 if not set. 
  */
  public int getAlignment(AttributeSet a);
  /** Returns the specified background color, or null if not set. */
  public Color getBackground(AttributeSet a); 
  public String getFontFamily(AttributeSet a); 
  /** Returns the specified foreground color, or null if not set. */
  public Color getForeground(AttributeSet a); 
  public boolean isBold(AttributeSet a); 
  public boolean isItalic(AttributeSet a); 
  public boolean isStrikeThrough(AttributeSet a); 
  public boolean isSubscript(AttributeSet a); 
  public boolean isSuperscript(AttributeSet a);
  public boolean isUnderline(AttributeSet a); 
  public void setAlignment(MutableAttributeSet a, int align); 
  public void setBackground(MutableAttributeSet a, Color fg); 
  public void setBold(MutableAttributeSet a, boolean b);
  public void setUnderline(MutableAttributeSet a, boolean b); 
  /** Sets the font family. Font family must be one of the
   *  font family supported by the {@link java.awt.Font} class. The following
   *  values are allowed:
   *  <ul>
   *   <li>{@link java.awt.Font#MONOSPACED}</li>
   *   <li>{@link java.awt.Font#SERIF}</li>
   *   <li>{@link java.awt.Font#SANS_SERIF}</li>
   *  </ul>
   * 
   * @param a
   * @param fam The font family name. <code>null</code> if
   *  the font definition should be removed.
   */
  public void setFontFamily(MutableAttributeSet a, String fam); 
  public void setForeground(MutableAttributeSet a, Color fg); 
  public void setItalic(MutableAttributeSet a, boolean b); 
  public void setStrikeThrough(MutableAttributeSet a, boolean b); 
  public void setSubscript(MutableAttributeSet a, boolean b); 
  public void setSuperscript(MutableAttributeSet a, boolean b); 
}

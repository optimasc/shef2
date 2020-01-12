package com.optimasc.text.html;

import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

/** This is the class that needs to be implemented to
 *  convert attributes to and from a different format.
 *  
 * @author Carl Eric Codere
 *
 */
public abstract class AttributeConverter
{
  /** Converts from src attribute to destination attribute. */
  public abstract void convert(MutableAttributeSet a);
  
  /** Copies all attributes from source attribute set to destination
   *  mutable attribute set, except for the attributes defined in 
   *  exclusions.
   * 
   * @param src Source attributes to copy.
   * @param dst Destination attributes.
   * @return exclusions List of the attributes that should not be copied.
   */
  public void copyExclude(AttributeSet src, MutableAttributeSet dst, Object exclusions[])
  {
    boolean exclude = false;
    Enumeration names = src.getAttributeNames();
    while (names.hasMoreElements())
    {
        Object name = names.nextElement();
        exclude = false;
        /* Search for all exclusions */
        for (int i = 0; i < exclusions.length; i++)
        {
          if (name.equals(exclusions[i]))
          {
            /* Indicates this must be excluded from the copy. */
            exclude = true;
            break;
          }
        }
        /* Excluding copy */
        if (exclude)
        {
          continue;
        }
        dst.addAttribute(name, src.getAttribute(name));
    }    
  }
  
  public void deleteAttributes(MutableAttributeSet dst, Object exclusions[])
  {
     for (int i = 0; i < exclusions.length; i++)
     {
        dst.removeAttribute(exclusions[i]);
     }
  }

}

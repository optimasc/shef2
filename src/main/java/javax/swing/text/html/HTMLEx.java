package javax.swing.text.html;

import javax.swing.text.AttributeSet;

public class HTMLEx extends HTML
{
  /** Horrible hack associated with a bug in JDK 1.6
   *  where HTML.Attribute.MEDIA is not made public, so
   *  we extend it here to use it.
   * 
   */
  public static String getMediaAttribute(AttributeSet attr)
  {
    return (String)attr.getAttribute(HTML.Attribute.MEDIA);
  }
}

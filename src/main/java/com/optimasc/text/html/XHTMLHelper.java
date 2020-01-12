package com.optimasc.text.html;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.atlanticbb.tantlinger.ui.text.HTMLUtils;

public class XHTMLHelper
{
  /** Mapping between 1 to7 and CSS2.1 font sizes. */
  protected static final String fontSizes[] =
    {
     "xx-small",
     "x-small",
     "small",
     "medium",
     "large",
     "x-large",
     "xx-large"
    };
  
  /* Document signature */
  public static final String DTD = 
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \n"+ 
      "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";      
  
  public static final String HTML_NS = "http://www.w3.org/1999/xhtml";
  
  
  /* HTML 4.01 / CSS 2.1 Alignment */
  public static final String ALIGN_LEFT = "left";
  public static final String ALIGN_JUSTIFIED = "justify";
  public static final String ALIGN_RIGHT = "right";
  public static final String ALIGN_CENTER = "center";
  
  
  /* HTML 4.01 color names, not case sensitive. */
  
  public static final Color colorWhite = new Color(255,255,255);
  public static final Color colorSilver = new Color(192,192,192);
  public static final Color colorGray = new Color(128,128,128);
  public static final Color colorBlack = new Color(0,0,0);
  public static final Color colorNavy = new Color(0,0,128);
  public static final Color colorBlue = new Color(0,0,255);
  public static final Color colorAqua = new Color(0,255,255);
  public static final Color colorTeal = new Color(0,128,128);
  public static final Color colorLime = new Color(0,255,0);
  public static final Color colorFuchsia = new Color(255,0,255);
  public static final Color colorPurple = new Color(128,0,128);
  public static final Color colorGreen = new Color(0,128,0);
  public static final Color colorOlive = new Color(128,128,0);
  public static final Color colorYellow = new Color(255,255,0);
/*  public static final Color colorOrange = new Color(255,165,0); Not in original HTML 4.01 specification */
  public static final Color colorRed = new Color(255,0,0);
  public static final Color colorMaroon = new Color(128,0,0);
  
  
  
  public abstract class AttributeAction
  {
    public abstract HTML.Tag doAction(AttributeSet in, MutableAttributeSet out);
  }

  /** <APPLET> tag converter. */
  public class ActionApplet extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      return HTML.Tag.OBJECT;
    }
  }

  /** <LISTING> tag converter. */
  public class ActionListing extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      return HTML.Tag.PRE; 
    }
  }

  /** <PLAINTEXT> tag converter. */
  public class ActionPlainText extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      return HTML.Tag.PRE; 
    }
  }

  /** <XMP> tag converter. */
  public class ActionXMP extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      return HTML.Tag.PRE; 
    }
  }

  /** <DIR> tag converter. */
  public class ActionDir extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      return HTML.Tag.UL;
    }
  }

  /** <MENU> tag converter. */
  public class ActionMenu extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      return HTML.Tag.UL;
    }
  }

  /** <U> tag converter. */
  public class ActionU extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      out.addAttribute(CSS.Attribute.TEXT_DECORATION, "underline");
      out.addAttribute(StyleConstants.Underline, Boolean.TRUE);
      return HTML.Tag.SPAN;
    }
  }
  
  /** Parse an old stype FONT element size attribute and
   *  converts to a style compatible with CSS2.1
   *  
   *  Does *not* support relative sizes conversions.
   *  
   * @param value An Integer or String represents a 
   *  value from 1 to 7.
   * @return null if the value is invalid.
   */
  public String parseFontSize(Object value)
  {
    int level = 4;
    if (value instanceof Integer)
    {
      level = ((Integer) value).intValue();
    } else
    if (value instanceof String)
    {
      String strVal = (String)value;
      /* relative values, not supported in current implementation. */
      if ((strVal.indexOf('+')==0) || (strVal.indexOf('-')==0))
      {
        System.err.println("Warning: Relative font size not supported.");
        return null;
      }
      level = Integer.parseInt((String)value);
    }
    /** Allowed values for HTML 4.01 specification are from 1 to 7. */
    if ((level > 7) || (level < 1))
    {
      System.err.println("Warning: Illegal font-size in FONT element.");
      return null;
    }
    return fontSizes[level-1];
  }
  
  /** <FONT> tag converter. */
  public class ActionFont extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      Enumeration names = in.getAttributeNames();
      while (names.hasMoreElements())
      {
          Object name = names.nextElement();
          Object value = in.getAttribute(name);
          if (name.equals(HTML.Attribute.FACE))
          {
            out.addAttribute(CSS.Attribute.FONT_FAMILY,value);
          }
          if (name.equals(HTML.Attribute.SIZE))
          {
            String s = parseFontSize(value);
            if (s != null)
            {
              out.addAttribute(CSS.Attribute.FONT_SIZE,s);
            }
          }
          if (name.equals(HTML.Attribute.COLOR))
          {
            out.addAttribute(CSS.Attribute.COLOR,value);
            out.addAttribute(StyleConstants.Foreground, Color.decode(value.toString()));
          }
      }
      return HTML.Tag.SPAN;
    }
  }

  /* <S> and <STRIKE> element. */
  public class ActionStrike extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      out.addAttribute(CSS.Attribute.TEXT_DECORATION, "line-through");
      out.addAttribute(StyleConstants.StrikeThrough, Boolean.TRUE);
      return HTML.Tag.SPAN;
    }
  }

  /* <CENTER> element, not really supported now. */
  public class ActionCenter extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      return HTML.Tag.SPAN;
    }
  }
  
  /* <BIG> element. */
  public class ActionBig extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      out.addAttribute(CSS.Attribute.FONT_SIZE,"larger");
      return HTML.Tag.SPAN;
    }
  }
  
  /* <SMALL> element. */
  public class ActionSmall extends AttributeAction
  {
    @Override
    public HTML.Tag doAction(AttributeSet in, MutableAttributeSet out)
    {
      out.addAttributes(in.copyAttributes());
      out.addAttribute(CSS.Attribute.FONT_SIZE,"smaller");
      return HTML.Tag.SPAN;
    }
  }
  
  

  /** Mapping between CSS and StyleConstant */
  protected HashMap mapping;

  /** StyleSheet */
  protected StyleSheet styleSheet = new StyleSheet();


  /** Mapping from tag to style constants. */
  protected HashMap<HTML.Tag, Object> tagMapping;
  /** Mapping from style constants to tag. */
  protected HashMap<Object, HTML.Tag> styleMapping;

  public XHTMLHelper()
  {
    super();
    MutableAttributeSet attr;
    mapping = new HashMap();

    tagMapping = new HashMap<HTML.Tag, Object>();
    tagMapping.put(HTML.Tag.U, StyleConstants.Underline);
    tagMapping.put(HTML.Tag.SUP, StyleConstants.Superscript);
    tagMapping.put(HTML.Tag.SUB, StyleConstants.Subscript);
    tagMapping.put(HTML.Tag.STRIKE, StyleConstants.StrikeThrough);
    tagMapping.put(HTML.Tag.I, StyleConstants.Italic);
    tagMapping.put(HTML.Tag.B, StyleConstants.Bold);

    styleMapping = new HashMap<Object, HTML.Tag>();
    styleMapping.put(StyleConstants.Underline, HTML.Tag.U);
    styleMapping.put(StyleConstants.Superscript, HTML.Tag.SUP);
    styleMapping.put(StyleConstants.Subscript, HTML.Tag.SUB);
    styleMapping.put(StyleConstants.StrikeThrough, HTML.Tag.STRIKE);
    styleMapping.put(StyleConstants.Italic, HTML.Tag.I);
    styleMapping.put(StyleConstants.Bold, HTML.Tag.B);

  }

  /**
   * Return the associated tag from the specified pre-defined Style attributes.
   * Returns null if not found.
   */
  public HTML.Tag getTagFromStyle(Object cs)
  {
    return styleMapping.get(cs);
  }

  /**
   * Return the associated pre-defined Style attributes from the specified tag.
   * Returns null if not found.
   * 
   * @param tag
   * @return
   */
  public Object getStyleFromTag(HTML.Tag tag)
  {
    return tagMapping.get(tag);
  }


  

}

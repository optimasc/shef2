package com.optimasc.text.html;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import net.atlanticbb.tantlinger.ui.text.HTMLUtils;

import com.optimasc.text.AttributeHelper;
import com.optimasc.text.html.XHTMLHelper.AttributeAction;

/**
 * Converts HTML Attributes to CSS2.1 compliant attributes.
 * 
 * @author carl
 * 
 */
public class HTMLHelper implements AttributeHelper
{
  /**
   * According to HTML5, this is the <code>name</code> of the <code>meta</code>
   * tag representing the author of the document.
   */
  public static final String META_NAME_CREATOR = "author";
  /**
   * According to HTML5, this is the <code>name</code> of the <code>meta</code>
   * tag representing the keywords associated with the document.
   */
  public static final String META_NAME_SUBJECT = "keywords";
  /**
   * According to HTML5, this is the <code>name</code> of the <code>meta</code>
   * tag representing the description associated with the document.
   */
  public static final String META_NAME_DESCRIPTION = "description";
  /**
   * According to HTML5, this is the <code>name</code> of the <code>meta</code>
   * tag representing the application that generated the document. It should not
   * be set for documents that are manually created.
   */
  public static final String META_NAME_GENERATOR = "generator";

  /** Default foreground color. */
  public static Color DEFAULT_FOREGROUND_COLOR = Color.BLACK;
  public static int DEFAULT_ALIGNMENT = StyleConstants.ALIGN_LEFT;

  public static final HTMLHelper.Tag THEAD = new HTMLHelper.Tag("thead", false, false);
  public static final HTMLHelper.Tag TBODY = new HTMLHelper.Tag("tbody", false, false);
  public static final HTMLHelper.Tag ADDRESS_X = new HTMLHelper.Tag("address", true, true);
  
  
  /**
   * Extends the HTML.Tag class to support missing HTML 4.01 tags for table
   * creation
   */
  public static class Tag extends HTML.Tag
  {
    protected Tag(String id)
    {
      this(id, false, false);
    }

    protected Tag(String id, boolean causesBreak, boolean isBlock)
    {
      super(id, causesBreak, isBlock);
    }
    
    
  } /* end class */

  /** Represent the HTML Attributes as a string representation */
  public static class Attributes
  {
    public static final String SIZE = HTML.Attribute.SIZE.toString();
    public static final String COLOR = HTML.Attribute.COLOR.toString();
    public static final String CLEAR = HTML.Attribute.CLEAR.toString();
    public static final String BACKGROUND = HTML.Attribute.BACKGROUND.toString();
    public static final String BGCOLOR = HTML.Attribute.BGCOLOR.toString();
    public static final String TEXT = HTML.Attribute.TEXT.toString();
    public static final String LINK = HTML.Attribute.LINK.toString();
    public static final String VLINK = HTML.Attribute.VLINK.toString();
    public static final String ALINK = HTML.Attribute.ALINK.toString();
    public static final String WIDTH = HTML.Attribute.WIDTH.toString();
    public static final String HEIGHT = HTML.Attribute.HEIGHT.toString();
    public static final String ALIGN = HTML.Attribute.ALIGN.toString();
    public static final String NAME = HTML.Attribute.NAME.toString();
    public static final String HREF = HTML.Attribute.HREF.toString();
    public static final String REL = HTML.Attribute.REL.toString();
    public static final String REV = HTML.Attribute.REV.toString();
    public static final String TITLE = HTML.Attribute.TITLE.toString();
    public static final String TARGET = HTML.Attribute.TARGET.toString();
    public static final String SHAPE = HTML.Attribute.SHAPE.toString();
    public static final String COORDS = HTML.Attribute.COORDS.toString();
    public static final String ISMAP = HTML.Attribute.ISMAP.toString();
    public static final String NOHREF = HTML.Attribute.NOHREF.toString();
    public static final String ALT = HTML.Attribute.ALT.toString();
    public static final String ID = HTML.Attribute.ID.toString();
    public static final String SRC = HTML.Attribute.SRC.toString();
    public static final String HSPACE = HTML.Attribute.HSPACE.toString();
    public static final String VSPACE = HTML.Attribute.VSPACE.toString();
    public static final String USEMAP = HTML.Attribute.USEMAP.toString();
    public static final String LOWSRC = HTML.Attribute.LOWSRC.toString();
    public static final String CODEBASE = HTML.Attribute.CODEBASE.toString();
    public static final String CODE = HTML.Attribute.CODE.toString();
    public static final String ARCHIVE = HTML.Attribute.ARCHIVE.toString();
    public static final String VALUE = HTML.Attribute.VALUE.toString();
    public static final String VALUETYPE = HTML.Attribute.VALUETYPE.toString();
    public static final String TYPE = HTML.Attribute.TYPE.toString();
    public static final String CLASS = HTML.Attribute.CLASS.toString();
    public static final String STYLE = HTML.Attribute.STYLE.toString();
    public static final String LANG = HTML.Attribute.LANG.toString();
    public static final String FACE = HTML.Attribute.FACE.toString();
    public static final String DIR = HTML.Attribute.DIR.toString();
    public static final String DECLARE = HTML.Attribute.DECLARE.toString();
    public static final String CLASSID = HTML.Attribute.CLASSID.toString();
    public static final String DATA = HTML.Attribute.DATA.toString();
    public static final String CODETYPE = HTML.Attribute.CODETYPE.toString();
    public static final String STANDBY = HTML.Attribute.STANDBY.toString();
    public static final String BORDER = HTML.Attribute.BORDER.toString();
    public static final String SHAPES = HTML.Attribute.SHAPES.toString();
    public static final String NOSHADE = HTML.Attribute.NOSHADE.toString();
    public static final String COMPACT = HTML.Attribute.COMPACT.toString();
    public static final String START = HTML.Attribute.START.toString();
    public static final String ACTION = HTML.Attribute.ACTION.toString();
    public static final String METHOD = HTML.Attribute.METHOD.toString();
    public static final String ENCTYPE = HTML.Attribute.ENCTYPE.toString();
    public static final String CHECKED = HTML.Attribute.CHECKED.toString();
    public static final String MAXLENGTH = HTML.Attribute.MAXLENGTH.toString();
    public static final String MULTIPLE = HTML.Attribute.MULTIPLE.toString();
    public static final String SELECTED = HTML.Attribute.SELECTED.toString();
    public static final String ROWS = HTML.Attribute.ROWS.toString();
    public static final String COLS = HTML.Attribute.COLS.toString();
    public static final String DUMMY = HTML.Attribute.DUMMY.toString();
    public static final String CELLSPACING = HTML.Attribute.CELLSPACING.toString();
    public static final String CELLPADDING = HTML.Attribute.CELLPADDING.toString();
    public static final String VALIGN = HTML.Attribute.VALIGN.toString();
    public static final String HALIGN = HTML.Attribute.HALIGN.toString();
    public static final String NOWRAP = HTML.Attribute.NOWRAP.toString();
    public static final String ROWSPAN = HTML.Attribute.ROWSPAN.toString();
    public static final String COLSPAN = HTML.Attribute.COLSPAN.toString();
    public static final String PROMPT = HTML.Attribute.PROMPT.toString();
    public static final String HTTPEQUIV = HTML.Attribute.HTTPEQUIV.toString();
    public static final String CONTENT = HTML.Attribute.CONTENT.toString();
    public static final String LANGUAGE = HTML.Attribute.LANGUAGE.toString();
    public static final String VERSION = HTML.Attribute.VERSION.toString();
    public static final String N = HTML.Attribute.N.toString();
    public static final String FRAMEBORDER = HTML.Attribute.FRAMEBORDER.toString();
    public static final String MARGINWIDTH = HTML.Attribute.MARGINWIDTH.toString();
    public static final String MARGINHEIGHT = HTML.Attribute.MARGINHEIGHT.toString();
    public static final String SCROLLING = HTML.Attribute.SCROLLING.toString();
    public static final String NORESIZE = HTML.Attribute.NORESIZE.toString();
    public static final String ENDTAG = HTML.Attribute.ENDTAG.toString();
    public static final String COMMENT = HTML.Attribute.COMMENT.toString();
  }

  public static HTML.Tag getTag(String qName)
  {
    HTML.Tag t = HTML.getTag(qName);
    if (t != null)
    {
      if (t.equals(HTML.Tag.ADDRESS))
      {
        t = HTMLHelper.ADDRESS_X;
      }
    }
    return t;
  }
  

  /**
   * Map of allowed and conversion attributes for each supported HTML Tag. Only
   * the attributes allowed in this map are allowed for the specified tags.
   * 
   * For each tag, a hashmap of attributes exist, and defines a conversion to no
   * format, or simply removes the attribute.
   * 
   */
  protected HashMap<HTML.Attribute, HashMap<HTML.Attribute, AttributeAction>> attributeMap;

  /** Mapping between HTML Tags and CSS Styles */
  protected HashMap<HTML.Tag, AttributeAction> cssTagMapping;

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

  /* Internal stylesheet */
  protected StyleSheet styleSheet = new StyleSheet();

  /*---------------- Convert deprecated attributes of HTML Elements to CSS2.1 Attributes --------------*/

  public class AttributeAlignConvert extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      String value = a.getAttribute(HTML.Attribute.ALIGN).toString();
      int align = toJavaTextAlignment(value);
      if (align != -1)
      {
        setAlignment(a, toJavaTextAlignment(value));
      }
    }
  }

  public class AttributeColorConvert extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      String value = a.getAttribute(HTML.Attribute.COLOR).toString();
      if (value != null)
      {
        setForeground(a, styleSheet.stringToColor(value));
      }
    }
  }

  public class AttributeBorderConvert extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      String value = a.getAttribute(HTML.Attribute.BORDER).toString();
      a.addAttribute(CSS.Attribute.BORDER_WIDTH, value + "px");
    }
  }

  /* Lists : Value currently ignored. */
  public class TypeToCSS extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      a.removeAttribute(HTML.Attribute.TYPE);
    }
  }

  /* Lists : Value currently ignored. */
  public class StartToCSS extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      a.removeAttribute(HTML.Attribute.START);
    }
  }

  /* Lists : Value currently ignored. */
  public class ValueToCSS extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      a.removeAttribute(HTML.Attribute.VALUE);
    }
  }

  /* Lists : Value currently ignored. */
  public class CompactToCSS extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      a.removeAttribute(HTML.Attribute.COMPACT);
    }
  }

  /* Images/Tables : Value currently ignored. */
  public class HeightToCSS extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      a.removeAttribute(HTML.Attribute.HEIGHT);
    }
  }

  /* Tables */
  public class NoWrapToCSS extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      String value = a.getAttribute(HTML.Attribute.NOWRAP).toString();
      a.removeAttribute(HTML.Attribute.NOWRAP);
      if (value != null)
      {
        if (value.toLowerCase().equals(Boolean.TRUE.toString()))
        {
          a.addAttribute(CSS.Attribute.WHITE_SPACE, "nowrap");
        }
      }
    }
  }

  /* Images/Objects. */
  public class HSpaceToCSS extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      String value = a.getAttribute(HTML.Attribute.HSPACE).toString();
      //  copyExclude(src, dst, new Object[] { HTML.Attribute.HSPACE, 
      //      CSS.Attribute.MARGIN_LEFT, CSS.Attribute.MARGIN_RIGHT});
      a.addAttribute(CSS.Attribute.MARGIN_LEFT, value + "px");
      a.addAttribute(CSS.Attribute.MARGIN_RIGHT, value + "px");
    }
  }

  /* Images/Objects. */
  public class VSpaceToCSS extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      /*      String value = src.getAttribute(HTML.Attribute.VSPACE).toString();
            dst.addAttribute(CSS.Attribute.MARGIN_TOP, value + "px");
            dst.addAttribute(CSS.Attribute.MARGIN_BOTTOM, value + "px");*/
    }
  }

  /* Background color */
  public class AttributeBackgroundColorConvert extends AttributeConverter
  {
    @Override
    public void convert(MutableAttributeSet a)
    {
      /*      String value = a.getAttribute(HTML.Attribute.BGCOLOR).toString();
            if (value != null)
            {
              setBackground(a, styleSheet.stringToColor(value));
            }*/
    }
  }

  /**
   * Converts all deprecated HTML Attributes to CSS2.1 attributes. The methods
   * removes the deprecated HTML Attributes and replaces them by CSS.Attribute
   * equivalents.
   * */
  public void convertHTMLAttributes(AttributeSet src, MutableAttributeSet dst)
  {
    Enumeration names = src.getAttributeNames();
    while (names.hasMoreElements())
    {
      Object name = names.nextElement();
      if (name instanceof HTML.Attribute)
      {
        if ((attributeMap.get(name)) != null)
        {
          /*          attributeMap.get(name).convert(dst); */
        }
      }
    }
  }

  /*---------------- Convert deprecated HTML Elements to CSS2.1 Attributes --------------*/

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

  /**
   * Parse an old stype FONT element size attribute and converts to a style
   * compatible with CSS2.1
   * 
   * Does *not* support relative sizes conversions.
   * 
   * @param value
   *          An Integer or String represents a value from 1 to 7.
   * @return null if the value is invalid.
   */
  public String parseFontSize(Object value)
  {
    int level = 4;
    if (value instanceof Integer)
    {
      level = ((Integer) value).intValue();
    } else if (value instanceof String)
    {
      String strVal = (String) value;
      /* relative values, not supported in current implementation. */
      if ((strVal.indexOf('+') == 0) || (strVal.indexOf('-') == 0))
      {
        System.err.println("Warning: Relative font size not supported.");
        return null;
      }
      level = Integer.parseInt((String) value);
    }
    /** Allowed values for HTML 4.01 specification are from 1 to 7. */
    if ((level > 7) || (level < 1))
    {
      System.err.println("Warning: Illegal font-size in FONT element.");
      return null;
    }
    return fontSizes[level - 1];
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
          out.addAttribute(CSS.Attribute.FONT_FAMILY, value);
        }
        if (name.equals(HTML.Attribute.SIZE))
        {
          String s = parseFontSize(value);
          if (s != null)
          {
            out.addAttribute(CSS.Attribute.FONT_SIZE, s);
          }
        }
        if (name.equals(HTML.Attribute.COLOR))
        {
          out.addAttribute(CSS.Attribute.COLOR, value);
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
      out.addAttribute(CSS.Attribute.FONT_SIZE, "larger");
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
      out.addAttribute(CSS.Attribute.FONT_SIZE, "smaller");
      return HTML.Tag.SPAN;
    }
  }

  public HTMLHelper()
  {
    attributeMap = new HashMap();
    cssTagMapping = new HashMap();

    /*    attributeMap.put(HTML.Tag.P, new HashMap<HTML.Attribute,AttributeConverter>)
        
        attributeMap.put(HTML.Attribute.ALIGN, new AttributeAlignConvert());
        attributeMap.put(HTML.Attribute.COLOR, new AttributeColorConvert());
        attributeMap.put(HTML.Attribute.BORDER, new AttributeBorderConvert());
        attributeMap.put(HTML.Attribute.TYPE, new TypeToCSS());
        attributeMap.put(HTML.Attribute.START, new StartToCSS());
        attributeMap.put(HTML.Attribute.VALUE, new ValueToCSS());
        attributeMap.put(HTML.Attribute.COMPACT, new CompactToCSS());
        attributeMap.put(HTML.Attribute.HEIGHT, new HeightToCSS());
        attributeMap.put(HTML.Attribute.NOWRAP, new NoWrapToCSS());
        attributeMap.put(HTML.Attribute.HSPACE, new HSpaceToCSS());
        attributeMap.put(HTML.Attribute.VSPACE, new VSpaceToCSS());
        attributeMap.put(HTML.Attribute.BGCOLOR, new AttributeBackgroundColorConvert());

        cssTagMapping.put(HTML.Tag.APPLET, new ActionApplet());
        cssTagMapping.put(HTML.Tag.DIR, new ActionDir());
        cssTagMapping.put(HTML.Tag.MENU, new ActionMenu());
        //    cssMapping.put(HTML.Tag.BASEFONT, new ActionBasefont());
        cssTagMapping.put(HTML.Tag.U, new ActionU());
        cssTagMapping.put(HTML.Tag.FONT, new ActionFont());
        //    cssTagMapping.put(HTML.Tag.S, new ActionStrike());
        //    cssTagMapping.put(HTML.Tag.STRIKE, new ActionStrike());
        cssTagMapping.put(HTML.Tag.CENTER, new ActionCenter());
        //    cssTagMapping.put(HTML.Tag.BIG, new ActionBig());
        //    cssTagMapping.put(HTML.Tag.SMALL, new ActionSmall());
    */
  }

  /** Convert attributes from old style HTML 3.2 to XHTML Strict 1.0 version. */
  public AttributeSet convertToXHTML(AttributeSet attr)
  {
    AttributeAction action;
    MutableAttributeSet out;
    MutableAttributeSet finalAttrs = new SimpleAttributeSet();
    Enumeration names = attr.getAttributeNames();
    while (names.hasMoreElements())
    {
      Object name = names.nextElement();
      Object value = attr.getAttribute(name);

      if (name instanceof HTML.Tag)
      {
        HTML.Tag tag = (HTML.Tag) name;
        action = cssTagMapping.get(tag);
        if ((action != null) && (value instanceof AttributeSet))
        {
          out = new SimpleAttributeSet();
          /* Replace the name to use */
          name = action.doAction((AttributeSet) value, out);
          /* Replace the value of these attributes */
          value = out;
        }
        /* Copy the value */
        finalAttrs.addAttribute(name, value);
      }
      else if (name instanceof HTML.Attribute)
      {
        /* We have a conversion to CSS Attributes */
        if ((attributeMap.get(name)) != null)
        {
          out = new SimpleAttributeSet();
          /*        attributeMap.get(name).convert(out); */
          finalAttrs.addAttributes(out);
        } else
        /* Directly map to HTML Attribute */
        {
          finalAttrs.addAttribute(name, value);
        }
      }
      else
      {
        /* Copy the value */
        finalAttrs.addAttribute(name, value);
      }
    }
    return finalAttrs;
  }

  /**
   * Set's the font family at the {@link JEditorPane}'s current caret position,
   * or for the current selection (if there is one).
   * <p>
   * If the fontName parameter is null, any currently set font family is
   * removed.
   * </p>
   * 
   * @param editor
   * @param fontName
   */
  public static void setFontFamily(JEditorPane editor, String fontName)
  {
    AttributeSet attr = HTMLUtils.getCharacterAttributes(editor);
    if (attr == null)
      return;

    if (fontName == null) //we're removing the font
    {

      //the font might be defined as a font tag
      Object val = attr.getAttribute(HTML.Tag.FONT);
      if (val != null && val instanceof AttributeSet)
      {
        MutableAttributeSet set = new SimpleAttributeSet((AttributeSet) val);
        val = set.getAttribute(HTML.Attribute.FACE); //does it have a FACE attrib?
        if (val != null)
        {
          set.removeAttribute(HTML.Attribute.FACE);
          HTMLUtils.removeCharacterAttribute(editor, HTML.Tag.FONT); //remove the current font tag
          if (set.getAttributeCount() > 0)
          {
            //it's not empty so replace the other font attribs
            SimpleAttributeSet fontSet = new SimpleAttributeSet();
            fontSet.addAttribute(HTML.Tag.FONT, set);
            HTMLUtils.setCharacterAttributes(editor, set);
          }
        }
      }
      //also remove these for good measure
      HTMLUtils.removeCharacterAttribute(editor, StyleConstants.FontFamily);
      HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.FONT_FAMILY);
    }
    else
    //adding the font family
    {
      MutableAttributeSet tagAttrs = new SimpleAttributeSet();
      HTMLUtils.attributeHelper.setFontFamily(tagAttrs, fontName);
      HTMLUtils.setCharacterAttributes(editor, tagAttrs);
    }
  }

  public static void setFontSize(JEditorPane editor, String size)
  {
    HTML.Tag tag = null;
    HTMLDocument doc = (HTMLDocument) editor.getDocument();
    Element chElem = doc.getCharacterElement(editor.getCaretPosition());
    AttributeSet sas = chElem.getAttributes();

    MutableAttributeSet tagAttrs = new SimpleAttributeSet();
    tagAttrs.addAttributes(sas.copyAttributes());

    /* Remove all attributes related to font-size. */
    tagAttrs.removeAttribute(StyleConstants.FontSize);
    tagAttrs.removeAttribute(CSS.Attribute.FONT_SIZE);
    tagAttrs.removeAttribute(HTML.Tag.BIG);
    tagAttrs.removeAttribute(HTML.Tag.SMALL);

    if (size != null)
    {

      if (size.equals(FontHelper.FONT_SIZE_LABEL_LARGE))
      {
        tag = HTML.Tag.BIG;
        tagAttrs.addAttribute(tag, new SimpleAttributeSet());
      } else if (size.equals(FontHelper.FONT_SIZE_LABEL_SMALL))
      {
        tag = HTML.Tag.SMALL;
        tagAttrs.addAttribute(tag, new SimpleAttributeSet());
      }
    }
    HTMLUtils.setCharacterAttributes(editor, tagAttrs, true);
  }

  @Override
  public int getAlignment(AttributeSet a)
  {
    Object o;
    int align = -1;
    String strAlignment = null;
    Integer intAlign = (Integer) a.getAttribute(StyleConstants.Alignment);
    if (intAlign != null)
    {
      align = intAlign.intValue();
    }
    o = a.getAttribute(CSS.Attribute.TEXT_ALIGN);
    if (o != null)
    {
      strAlignment = o.toString();
      align = toCSSTextAlignInteger(strAlignment);
    }
    /* Last priority one for backward compatibility. */
    o = a.getAttribute(HTML.Attribute.ALIGN);
    if (o != null)
    {
      strAlignment = o.toString();
      align = toCSSTextAlignInteger(strAlignment);
    }
    return align;
  }

  @Override
  public Color getBackground(AttributeSet a)
  {
    return null;
  }

  @Override
  public String getFontFamily(AttributeSet a)
  {
    if (a.isDefined(StyleConstants.FontFamily))
    {
      return StyleConstants.getFontFamily(a);
    }
    if (a.isDefined(CSS.Attribute.FONT_FAMILY))
    {
      return toJavaFontFamily(a.getAttribute(CSS.Attribute.FONT_FAMILY).toString());
    }
    if (a.isDefined(HTML.Tag.FONT))
    {
      Object val = a.getAttribute(HTML.Tag.FONT);
      if (val != null && val instanceof AttributeSet)
      {
        MutableAttributeSet set = new SimpleAttributeSet((AttributeSet) val);
        val = set.getAttribute(HTML.Attribute.FACE); //does it have a FACE attrib?
        if (val != null)
        {
          return toJavaFontFamily(val.toString());
        }
      }
    }
    return null;
  }

  /**
   * Returns the foreground color, or null if the color is the default color.
   * 
   */
  @Override
  public Color getForeground(AttributeSet a)
  {
    if (a.isDefined(StyleConstants.Foreground))
    {
      return StyleConstants.getForeground(a);
    }
    if (a.isDefined(CSS.Attribute.COLOR))
    {
      return styleSheet.stringToColor(a.getAttribute(CSS.Attribute.COLOR).toString());
    }
    return null;
  }

  @Override
  public boolean isBold(AttributeSet a)
  {
    String weight;
    Object o;
    int intWeight;
    if (StyleConstants.isBold(a))
    {
      return true;
    }
    if (a.getAttribute(HTML.Tag.B) != null)
    {
      return true;
    }
    o = a.getAttribute(CSS.Attribute.FONT_WEIGHT);
    if (o != null)
    {
      weight = o.toString();
      if (weight.equals("bold"))
      {
        return true;
      }
      if (weight.equals("bolder"))
      {
        return true;
      }
      try
      {
        intWeight = Integer.parseInt(weight);
        if (intWeight > 400)
        {
          return true;
        }
      } catch (NumberFormatException e)
      {
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean isItalic(AttributeSet a)
  {
    Object o;
    if (StyleConstants.isItalic(a))
    {
      return true;
    }
    if (a.getAttribute(HTML.Tag.I) != null)
    {
      return true;
    }
    o = a.getAttribute(CSS.Attribute.FONT_STYLE);
    if (o != null)
    {
      if (o.toString().equals("italic"))
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isStrikeThrough(AttributeSet a)
  {
    Object o;
    if (StyleConstants.isStrikeThrough(a))
    {
      return true;
    }
    if (a.getAttribute(HTML.Tag.S) != null)
    {
      return true;
    }
    if (a.getAttribute(HTML.Tag.STRIKE) != null)
    {
      return true;
    }
    o = a.getAttribute(CSS.Attribute.TEXT_DECORATION);
    if (o != null)
    {
      if (o.toString().contains("line-through"))
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isSubscript(AttributeSet a)
  {
    if (StyleConstants.isSubscript(a))
    {
      return true;
    }
    if (a.getAttribute(HTML.Tag.SUB) != null)
    {
      return true;
    }
    return false;
  }

  @Override
  public boolean isSuperscript(AttributeSet a)
  {
    if (StyleConstants.isSuperscript(a))
    {
      return true;
    }
    if (a.getAttribute(HTML.Tag.SUP) != null)
    {
      return true;
    }
    return false;
  }

  @Override
  public boolean isUnderline(AttributeSet a)
  {
    Object o;
    if (StyleConstants.isUnderline(a))
    {
      return true;
    }
    o = a.getAttribute(CSS.Attribute.TEXT_DECORATION);
    if (o != null)
    {
      if (o.toString().contains("underline"))
      {
        return true;
      }
    }
    if (a.getAttribute(HTML.Tag.U) != null)
    {
      return true;
    }

    return false;
  }

  /**
   * Returns the CSS/HTML Attributes alignment to a StyleConstants alignment.
   * 
   * @param str
   *          The CSS/HTML Attribute alignment value
   * @return -1 if not defined or invalid, otherwise one of the alignment
   *         constants defined in {@link javax.swing.text.StyleConstants}.
   * 
   */
  public static final int toJavaTextAlignment(String str)
  {
    if (str == null)
    {
      return -1;
    }
    if (str.equalsIgnoreCase("right"))
    {
      return StyleConstants.ALIGN_RIGHT;
    }
    if (str.equalsIgnoreCase("left"))
    {
      return StyleConstants.ALIGN_LEFT;
    }
    if (str.equalsIgnoreCase("center"))
    {
      return StyleConstants.ALIGN_CENTER;
    }
    if (str.equalsIgnoreCase("justify"))
    {
      return StyleConstants.ALIGN_JUSTIFIED;
    }
    return -1;
  }

  @Override
  public void setAlignment(MutableAttributeSet a, int align)
  {
    String strAlignment = null;
    /* Always remove this attribute, it is not used here. */
    a.removeAttribute(HTML.Attribute.ALIGN);
    StyleConstants.setAlignment(a, align);
    switch (align)
    {
      case StyleConstants.ALIGN_LEFT:
        strAlignment = "left";
        break;
      case StyleConstants.ALIGN_RIGHT:
        strAlignment = "right";
        break;
      case StyleConstants.ALIGN_CENTER:
        strAlignment = "center";
        break;
      case StyleConstants.ALIGN_JUSTIFIED:
        strAlignment = "justify";
        break;
    }
    styleSheet.addCSSAttribute(a, CSS.Attribute.TEXT_ALIGN, strAlignment);
  }

  @Override
  public void setBackground(MutableAttributeSet a, Color fg)
  {
  }

  @Override
  public void setBold(MutableAttributeSet a, boolean b)
  {
    StyleConstants.setBold(a, b);
    if (b == false)
    {
      a.removeAttribute(HTML.Tag.B);
      a.removeAttribute(CSS.Attribute.FONT_WEIGHT);
    } else
    {
      a.addAttribute(HTML.Tag.B, new SimpleAttributeSet());
      styleSheet.addCSSAttribute(a, CSS.Attribute.FONT_WEIGHT, "bold");
    }
  }

  @Override
  public void setFontFamily(MutableAttributeSet a, String family)
  {
    if (family == null) //we're removing the font
    {
      //the font might be defined as a font tag
      Object val = a.getAttribute(HTML.Tag.FONT);
      if (val != null && val instanceof AttributeSet)
      {
        MutableAttributeSet set = new SimpleAttributeSet((AttributeSet) val);
        val = set.getAttribute(HTML.Attribute.FACE); //does it have a FACE attrib?
        if (val != null)
        {
          set.removeAttribute(HTML.Attribute.FACE);
          a.removeAttribute(HTML.Tag.FONT);
          if (set.getAttributeCount() > 0)
          {
            //it's not empty so replace the other font attribs
            SimpleAttributeSet fontSet = new SimpleAttributeSet();
            fontSet.addAttribute(HTML.Tag.FONT, set);
            a.addAttribute(HTML.Tag.FONT, set);
          }
        }
      }
      //also remove these for good measure
      a.removeAttribute(CSS.Attribute.FONT_FAMILY);
      a.removeAttribute(StyleConstants.FontFamily);
    }
    else
    //adding the font family
    {
      StyleConstants.setFontFamily(a, family);
      styleSheet.addCSSAttribute(a, CSS.Attribute.FONT_FAMILY, toCSSFontFamily(family));
    }

  }

  /**
   * Sets the foreground color. Does nothing if the color to be set is
   * Color.Black.
   * 
   */
  @Override
  public void setForeground(MutableAttributeSet a, Color fg)
  {
    Object o;
    o = getForeground(a);
    if ((o != null) && (o.equals(DEFAULT_FOREGROUND_COLOR)))
    {
      if ((fg == null) || (fg.equals(DEFAULT_FOREGROUND_COLOR)))
      {
        return;
      }
    }
    StyleConstants.setForeground(a, fg);
    styleSheet.addCSSAttribute(a, CSS.Attribute.COLOR, toCSSRGBString(fg));
  }

  @Override
  public void setItalic(MutableAttributeSet a, boolean b)
  {
    StyleConstants.setItalic(a, b);
    if (b == false)
    {
      a.removeAttribute(HTML.Tag.I);
      a.removeAttribute(CSS.Attribute.FONT_STYLE);
    } else
    {
      a.addAttribute(HTML.Tag.I, new SimpleAttributeSet());
      styleSheet.addCSSAttribute(a, CSS.Attribute.FONT_STYLE, "italic");
    }
  }

  @Override
  public void setStrikeThrough(MutableAttributeSet a, boolean b)
  {
    StyleConstants.setStrikeThrough(a, b);
    if (b == false)
    {
      a.removeAttribute(HTML.Tag.STRIKE);
      a.removeAttribute(HTML.Tag.S);
      if (a.isDefined(CSS.Attribute.TEXT_DECORATION))
      {
        /* Several attributes are allowed for TEXT_DECORATION */
        String object = a.getAttribute(CSS.Attribute.TEXT_DECORATION).toString();
        object = object.replace("line-through", "");
        a.removeAttribute(CSS.Attribute.TEXT_DECORATION);
        styleSheet.addCSSAttribute(a, CSS.Attribute.TEXT_DECORATION, object);
      }
    } else
    {
      a.addAttribute(HTML.Tag.S, new SimpleAttributeSet());
      /* Several attributes are allowed for TEXT_DECORATION */
      if (a.isDefined(CSS.Attribute.TEXT_DECORATION))
      {
        String object = a.getAttribute(CSS.Attribute.TEXT_DECORATION).toString();
        if (object.contains("line-therough") == false)
        {
          a.removeAttribute(CSS.Attribute.TEXT_DECORATION);
          object = object + " line-through";
          styleSheet.addCSSAttribute(a, CSS.Attribute.TEXT_DECORATION, object);
        }
      } else
      {
        styleSheet.addCSSAttribute(a, CSS.Attribute.TEXT_DECORATION, "line-through");
      }
    }
  }

  @Override
  public void setUnderline(MutableAttributeSet a, boolean b)
  {
    StyleConstants.setUnderline(a, b);
    if (b == false)
    {
      a.removeAttribute(HTML.Tag.U);
      if (a.isDefined(CSS.Attribute.TEXT_DECORATION))
      {
        /* Several attributes are allowed for TEXT_DECORATION */
        String object = a.getAttribute(CSS.Attribute.TEXT_DECORATION).toString();
        object = object.replace("underline", "");
        a.removeAttribute(CSS.Attribute.TEXT_DECORATION);
        styleSheet.addCSSAttribute(a, CSS.Attribute.TEXT_DECORATION, object);
      }
    } else
    {
      a.addAttribute(HTML.Tag.U, new SimpleAttributeSet());
      if (a.isDefined(CSS.Attribute.TEXT_DECORATION))
      {
        /* Several attributes are allowed for TEXT_DECORATION */
        String object = a.getAttribute(CSS.Attribute.TEXT_DECORATION).toString();
        if (object.contains("underline") == false)
        {
          a.removeAttribute(CSS.Attribute.TEXT_DECORATION);
          object = object + " underline";
          styleSheet.addCSSAttribute(a, CSS.Attribute.TEXT_DECORATION, object);
        }
      } else
      {
        styleSheet.addCSSAttribute(a, CSS.Attribute.TEXT_DECORATION, "underline");
      }
    }
  }

  @Override
  public void setSubscript(MutableAttributeSet a, boolean b)
  {
    StyleConstants.setSubscript(a, b);
    if (b == false)
    {
      a.removeAttribute(HTML.Tag.SUB);
    } else
    {
      a.addAttribute(HTML.Tag.SUB, new SimpleAttributeSet());
    }
  }

  @Override
  public void setSuperscript(MutableAttributeSet a, boolean b)
  {
    StyleConstants.setSuperscript(a, b);
    if (b == false)
    {
      a.removeAttribute(HTML.Tag.SUP);
    } else
    {
      a.addAttribute(HTML.Tag.SUP, new SimpleAttributeSet());
    }
  }

  /**
   * Converts a java.awt.Color to a HTML color string representation.
   * 
   * @param c
   *          Color to convert
   * @return A HTML color string representation.
   */
  public static final String toCSSRGBString(Color c)
  {
    int value = 0;
    StringBuilder sb = new StringBuilder();
    value = c.getRed();
    sb.append('#');
    sb.append(String.format("%02X", value & 0xFF));
    value = c.getGreen();
    sb.append(String.format("%02X", value & 0xFF));
    value = c.getBlue();
    sb.append(String.format("%02X", value & 0xFF));
    return sb.toString();
  }

  /**
   * Converts a HTML/CSS.Attribute.TEXT_ALIGN alignment value to a
   * javax.swing.text.StyleConstants values.
   * 
   * @param align
   * @return -1 if invalid, otherwise on the alignment constants defined in
   *         {@link javax.swing.text.StyleConstants}
   */
  public static final int toCSSTextAlignInteger(String align)
  {
    if (align == null)
    {
      return -1;
    }
    if (align.equalsIgnoreCase("left"))
    {
      return StyleConstants.ALIGN_LEFT;
    }
    if (align.equalsIgnoreCase("right"))
    {
      return StyleConstants.ALIGN_RIGHT;
    }
    if (align.equalsIgnoreCase("center"))
    {
      return StyleConstants.ALIGN_CENTER;
    }
    if (align.equalsIgnoreCase("justify"))
    {
      return StyleConstants.ALIGN_JUSTIFIED;
    }
    return -1;
  }

  /**
   * Converts a {@link java.awt.Font} font family names to a CSS compatible
   * generic family name.
   * 
   * There seems to a bug in the Sun parser, it does not map correctly the fonts
   * to the W3C CSS2.1 font family recommendations as defined here:
   * https://www.w3.org/TR/CSS22/fonts.html#value-def-generic-family.
   * 
   * 
   * 
   * @param family
   *          Family name.
   * @return The CSS compatible generic family name.
   */
  public static final String toCSSFontFamily(String family)
  {
    if (family.equals(Font.SANS_SERIF))
    {
      return "sans-serif";
    }
    if (family.equals(Font.SERIF))
    {
      return "serif";
    }
    if (family.equals(Font.MONOSPACED))
    {
      return "monospace";
    }
    return family;
  }

  /**
   * Converts a CSS Generic font family to a java style font family.
   * 
   * @param family
   * @return
   */
  public static final String toJavaFontFamily(String family)
  {
    if (family.equalsIgnoreCase("sans-serif"))
    {
      return Font.SANS_SERIF;
    }
    if (family.equalsIgnoreCase("serif"))
    {
      return Font.SERIF;
    }
    if (family.equalsIgnoreCase("monospace"))
    {
      return Font.MONOSPACED;
    }
    return family;
  }

  /**
   * Converts all required XML characters that need to be escaped to their
   * equivalent version for writing.
   * 
   * @return
   */
  public static String escape(String s)
  {
    s = s.replace("&", "&amp;");
    s = s.replace("\"", "&quot;");
    s = s.replace("'", "&apos;");
    s = s.replace("<", "&lt;");
    s = s.replace(">", "&gt;");
    return s;
  }
  
  public static boolean isParagraph(HTML.Tag tag) 
  {
      return (
          tag == HTML.Tag.P
             || tag == HTML.Tag.IMPLIED
             || tag == HTML.Tag.DT
             || tag == HTML.Tag.H1
             || tag == HTML.Tag.H2
             || tag == HTML.Tag.H3
             || tag == HTML.Tag.H4
             || tag == HTML.Tag.H5
             || tag == HTML.Tag.H6
      );
  }

}

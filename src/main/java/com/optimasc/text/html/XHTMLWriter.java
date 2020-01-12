package com.optimasc.text.html;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AbstractDocument.AbstractElement;
import javax.swing.text.AbstractWriter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;

import net.atlanticbb.tantlinger.ui.text.dialogs.PropertiesPanel.LanguageSelector;

import com.optimasc.text.DocumentProperties;
import com.optimasc.text.WriterConfiguration;
import com.optimasc.text.html.HTMLHelper.Tag;

/**
 * Writes XHTML 1.1 STRICT compliant documents (thus replacing and ignoring
 * unsupported elements). It currently supports any StyledDocument as input and
 * will correctly manage the Presentation module attributes from Styled
 * Documents.
 * 
 * Limitations:
 * <ul>
 * <li>underline html element is ignored, raising a warning instead.</li>
 * </ul>
 * 
 * It can be configured to define how the output will be written:
 * 
 * <dl>
 * 
 * <dt><code>"style_support"</code></dt>
 *  <dd>
 *      <dl>
 *      <dt><code>{@link java.lang.Boolean#TRUE}</code></dt>
 *      <dd>(<em>default</em>) Activates the Style Attribute Module</dd>
 *      <dt><code>{@link java.lang.Boolean#FALSE}</code></dt>
 *      <dd>Disable style attributes</dd>
 *      </dl>
 * </dd>
 * <dt><code>"comments"</code></dt>
 * <dd>
 *      <dl>
 *      <dt><code>{@link java.lang.Boolean#TRUE}</code></dt>
 *      <dd>(<em>default</em>) Write out comments</dd>
 *      <dt><code>{@link java.lang.Boolean#FALSE}</code></dt>
 *      <dd>Disable comment output</dd>
 *      </dl>
 * </dd>
 * <dt><code>"write-header"</code></dt>
 * <dd>
 *  <dl>
 *      <dt><code>{@link java.lang.Boolean#TRUE}</code></dt>
 *      <dd>Write out DTD, header and properties information</dd>
 *      <dt><code>{@link java.lang.Boolean#FALSE}</code></dt>
 *      <dd>(<em>default</em>) Only <var>html</var> and children 
 *      elements shall be written</dd>
 * </dl>
 * </dd>
 * <dt><code>"write-full-table"</code></dt>
 * <dd>
 *  <dl>
 *      <dt><code>{@link java.lang.Boolean#TRUE}</code></dt>
 *      <dd>Write out <var>thead</var> and <var>tbody</var> elements when writing out
 *      tables</dd>
 *      <dt><code>{@link java.lang.Boolean#FALSE}</code></dt>
 *      <dd>(<em>default</em>) Only Basic Table Module element information is written
 *  </dl>    
 * </dd>
 * 
 * <dt><code>"css"</code></dt>
 * <dd>Represents the CSS Style to be inlined in the web page. By default
 *  this value is <code>null</code> which indicates no style information
 *  is supposed to be output.</dd>
 * </dd>
 *
 * <dt><code>"heading_anchors"</dt>
 * <dd>
 *   <dl>
 *      <dt><code>{@link java.lang.Boolean#TRUE}</code></dt>
 *      <dd>Write out heading anchors in front of <var>h2..h6</var> elements. 
 *      <var>h1</var> is considered the title of the document and 
 *      does not contain heading anchors.</dd>
 *      <dt><code>{@link java.lang.Boolean#FALSE}</code></dt>
 *      <dd>(<em>default</em>) The headings are written as is with no anchors.
 *   </dl>   
 * </dd>
 * </dl>
 * 
 * 
 * @author Carl Eric Codere
 * 
 */
public class XHTMLWriter extends AbstractWriter implements WriterConfiguration
{
  protected HTMLHelper htmlHelper;

  protected HashMap<String, Object> config = new HashMap<String, Object>();

  /** Configuration element : <code>"write-header"<code> */
  public static final String CONFIG_WRITE_HEADER = "write-header";
  /** Configuration element : <code>"comments"<code> */
  public static final String CONFIG_COMMENTS = "comments";
  /** Configuration element : <code>"style_support"<code> */
  public static final String CONFIG_STYLES = "style_support";
  /** Configuration element : <code>"write-full-table"<code> */
  public static final String CONFIG_WRITE_FULL_TABLE = "write-full-table";
  /** Configuration element : <code>"css"<code> */
  public static final String CONFIG_CSS = "css";
  /** Configuration element : <code>"heanding_numbering"<code> */
  public static final String CONFIG_HEADING_ANCHORS = "heading_anchors";

  /**
   * Maps from style name as held by the Document, to the archived style name
   * (style name written out). These may differ.
   */
  private Hashtable styleNameMapping;

  /**
   * Indicate if we are in a <code>pre</code> element.
   */
  protected boolean inPre = false;

  int startOffset = 0;
  int endOffset = 0;

  /**
   * Contains the list of tags that are already managed manually, mostly those
   * that are managed through AttributeHelper procedures.
   */
  protected Vector<HTML.Tag> ignoredTags = new Vector<HTML.Tag>();

  /** List of supported tags, all other tags are simply ignored!! */
  protected Vector<HTML.Tag> supportedTags = new Vector<HTML.Tag>();

  /**
   * These are elements that should be separated from other elements physically
   * by a newline character.
   */
  protected Vector<HTML.Tag> requiresLineSeparator = new Vector<HTML.Tag>();

  /**
   * These are parent elements that should have after their start tag a newline.
   */
  protected Vector<HTML.Tag> requiresLineSeparatorAfterTagStart = new Vector<HTML.Tag>();

  /**
   * List of attributes that are replaced by their StyleConstants/CSS
   * equivalents.
   */
  protected Vector<HTML.Attribute> ignoredAttributes = new Vector<HTML.Attribute>();

  protected boolean indented = false;
  
  /** State machine to indicate if we are in a heading or not.
   *  The number indicates the heading number from 0 to 5
   *  representing h1..h6. -1 indicates not in heading.
   **/
  protected int inHeading = -1;
  /** Current start heading level */
  protected int[] headings = {1,1,1,1,1,1};

  public XHTMLWriter(Writer w, Document doc, int pos, int len)
  {
    super(w, doc, pos, len);
    setLineLength(80);
    htmlHelper = new HTMLHelper();

    /** Add tags that are manually managed */
    ignoredTags.add(HTML.Tag.B);
    ignoredTags.add(HTML.Tag.I);
    ignoredTags.add(HTML.Tag.STRIKE);
    ignoredTags.add(HTML.Tag.S);
    ignoredTags.add(HTML.Tag.U);
    ignoredTags.add(HTML.Tag.SUB);
    ignoredTags.add(HTML.Tag.SUP);
    ignoredTags.add(HTML.Tag.SPAN);
    ignoredTags.add(HTML.Tag.FONT);
    /** All deprecated elements are removed. */
    ignoredTags.add(HTML.Tag.APPLET);
    ignoredTags.add(HTML.Tag.BASEFONT);
    ignoredTags.add(HTML.Tag.CENTER);
    ignoredTags.add(HTML.Tag.DIR);
    ignoredTags.add(HTML.Tag.ISINDEX);
    ignoredTags.add(HTML.Tag.MENU);

    supportedTags.add(HTML.Tag.A);
    /* Does not exist in HTML.Tag enumeration */
    /*      supportedTags.add(new HTML.Tag.ABBR); */
    /*      supportedTags.add(HTML.Tag.ACRONYM);  */
    supportedTags.add(HTML.Tag.ADDRESS);
    supportedTags.add(HTMLHelper.ADDRESS_X);
    supportedTags.add(HTML.Tag.B);
    supportedTags.add(HTML.Tag.BIG);
    supportedTags.add(HTML.Tag.BLOCKQUOTE);
    supportedTags.add(HTML.Tag.BODY);
    supportedTags.add(HTML.Tag.BR);
    supportedTags.add(HTML.Tag.CAPTION);
    supportedTags.add(HTML.Tag.CITE);
    supportedTags.add(HTML.Tag.CODE);
    /*      supportedTags.add(HTML.Tag.COL); */
    /*      supportedTags.add(HTML.Tag.COLGROUP); */
    supportedTags.add(HTML.Tag.DD);
    supportedTags.add(HTML.Tag.DFN);
    supportedTags.add(HTML.Tag.DIV);
    supportedTags.add(HTML.Tag.DL);
    supportedTags.add(HTML.Tag.DT);
    supportedTags.add(HTML.Tag.EM);
    supportedTags.add(HTML.Tag.H1);
    supportedTags.add(HTML.Tag.H2);
    supportedTags.add(HTML.Tag.H3);
    supportedTags.add(HTML.Tag.H4);
    supportedTags.add(HTML.Tag.H5);
    supportedTags.add(HTML.Tag.H6);
    supportedTags.add(HTML.Tag.HEAD);
    supportedTags.add(HTML.Tag.HR);
    supportedTags.add(HTML.Tag.HTML);
    supportedTags.add(HTML.Tag.I);
    supportedTags.add(HTML.Tag.IMG);
    supportedTags.add(HTML.Tag.KBD);
    supportedTags.add(HTML.Tag.LI);
    supportedTags.add(HTML.Tag.META);
    supportedTags.add(HTML.Tag.OL);
    supportedTags.add(HTML.Tag.P);
    supportedTags.add(HTML.Tag.PRE);
    /*      supportedTags.add(HTML.Tag.Q); */
    supportedTags.add(HTML.Tag.S);
    supportedTags.add(HTML.Tag.SAMP);
    supportedTags.add(HTML.Tag.SMALL);
    supportedTags.add(HTML.Tag.SPAN);
    supportedTags.add(HTML.Tag.STRIKE);
    supportedTags.add(HTML.Tag.STRONG);
    supportedTags.add(HTML.Tag.STYLE);
    supportedTags.add(HTML.Tag.SUB);
    supportedTags.add(HTML.Tag.SUP);
    supportedTags.add(HTML.Tag.TABLE);
    /*      supportedTags.add(HTML.Tag.TBODY); */
    supportedTags.add(HTML.Tag.TD);
    /*      supportedTags.add(HTML.Tag.TFOOT); */
    supportedTags.add(HTML.Tag.TH);
    /*      supportedTags.add(HTML.Tag.THEAD); */
    supportedTags.add(HTML.Tag.TITLE);
    supportedTags.add(HTML.Tag.TR);
    supportedTags.add(HTML.Tag.TT);
    supportedTags.add(HTML.Tag.UL);
    supportedTags.add(HTML.Tag.VAR);

    /** Attributes that are converted to other attributes. */
    ignoredAttributes.add(HTML.Attribute.ALIGN);
    ignoredAttributes.add(HTML.Attribute.BGCOLOR);

    config.put(CONFIG_COMMENTS, Boolean.TRUE);
    config.put(CONFIG_WRITE_HEADER, Boolean.FALSE);
    config.put(CONFIG_STYLES, Boolean.TRUE);
    config.put(CONFIG_WRITE_FULL_TABLE, Boolean.FALSE);
    config.put(CONFIG_CSS, null);

    /* Those who require newlines for the pretty printer as
     * they are considered complete independent blocks */
    requiresLineSeparator.add(HTML.Tag.BODY);
    requiresLineSeparator.add(HTML.Tag.BLOCKQUOTE);
    requiresLineSeparator.add(HTML.Tag.CAPTION);
    requiresLineSeparator.add(HTML.Tag.DL);
    requiresLineSeparator.add(HTML.Tag.H1);
    requiresLineSeparator.add(HTML.Tag.H2);
    requiresLineSeparator.add(HTML.Tag.H3);
    requiresLineSeparator.add(HTML.Tag.H4);
    requiresLineSeparator.add(HTML.Tag.H5);
    requiresLineSeparator.add(HTML.Tag.H6);
    requiresLineSeparator.add(HTML.Tag.HEAD);
    requiresLineSeparator.add(HTML.Tag.HR);
    requiresLineSeparator.add(HTML.Tag.HTML);
    requiresLineSeparator.add(HTML.Tag.META);
    requiresLineSeparator.add(HTML.Tag.OL);
    requiresLineSeparator.add(HTML.Tag.P);
    requiresLineSeparator.add(HTML.Tag.PRE);
    /*      supportedTags.add(HTML.Tag.Q); */
    requiresLineSeparator.add(HTML.Tag.TABLE);
    requiresLineSeparator.add(HTMLHelper.TBODY);
    /*      supportedTags.add(HTML.Tag.TBODY); */
    //      supportedTags.add(HTML.Tag.TD);
    /*      supportedTags.add(HTML.Tag.TFOOT); */
    requiresLineSeparator.add(HTML.Tag.TR);
    requiresLineSeparator.add(HTMLHelper.THEAD);
    requiresLineSeparator.add(HTML.Tag.TITLE);
    requiresLineSeparator.add(HTML.Tag.UL);

    //    requiresLineSeparatorAfterTagStart.add(HTML.Tag.BODY);
    requiresLineSeparatorAfterTagStart.add(HTML.Tag.DL);
    //    requiresLineSeparatorAfterTagStart.add(HTML.Tag.HEAD);
    //    requiresLineSeparatorAfterTagStart.add(HTML.Tag.HTML);
    requiresLineSeparatorAfterTagStart.add(HTML.Tag.OL);
    requiresLineSeparatorAfterTagStart.add(HTML.Tag.TABLE);
    requiresLineSeparatorAfterTagStart.add(HTML.Tag.TR);
    requiresLineSeparatorAfterTagStart.add(HTML.Tag.UL);

  }

  public XHTMLWriter(Writer w, Document doc)
  {
    this(w, doc, 0, doc.getLength());
  }

  /**
   * Add an attribute only if it doesn't exist so that we don't loose
   * information replacing it with SimpleAttributeSet.EMPTY
   */
  protected void addAttribute(MutableAttributeSet to, Object key, Object value)
  {
    Object attr = to.getAttribute(key);
    if (attr == null || attr == SimpleAttributeSet.EMPTY)
    {
      to.addAttribute(key, value);
    } else
    {
      if (attr instanceof MutableAttributeSet
          && value instanceof AttributeSet)
      {
        ((MutableAttributeSet) attr).addAttributes((AttributeSet) value);
      }
    }
  }

  @Override
  protected void writeAttributes(AttributeSet attr) throws IOException
  {
    if (attr == null)
      return;
    Enumeration names = attr.getAttributeNames();
    String value = "";
    while (names.hasMoreElements())
    {
      Object name = names.nextElement();
      if (name instanceof HTML.Tag || name instanceof StyleConstants
          || name == HTML.Attribute.ENDTAG)
      {
        continue;
      }
      /*
       * The style attribute string already exists, then add it to current
       * style string.
       */
      if (name.equals(HTML.Attribute.STYLE))
      {
        value = value + " " + (String) attr.getAttribute(name);
        continue;
      }
      if (name instanceof CSS.Attribute)
      {
        // default is to store in a HTML style attribute
        if (value.length() > 0)
        {
          value = value + "; ";
        }
        value = value + name + ":" + attr.getAttribute(name);
        continue;
      }
      write(" " + name + "=\"" + HTMLHelper.escape(attr.getAttribute(name).toString()) + "\"");
    }
    /* Write style information if needed. */
    if (value.length() > 0)
    {
      /* Only write STYLE attribute if configured as such. */
      if (config.get(CONFIG_STYLES).equals(Boolean.TRUE))
      {
        write(" " + HTML.Attribute.STYLE + "=\"" + value + "\"");
      }
    }
  }

  /**
   * Writes a start tag appropriately, without any indents and inline.
   * 
   * 
   * @param tag
   * @param attr
   * @throws IOException
   */
  protected void writeInlineStartTag(HTML.Tag tag, AttributeSet attr) throws IOException
  {
    if (attr != null)
    {
      {
        /*
         * Do not write attributes for some specific tags that should never
         * be output!
         */
        if (tag.equals(HTML.Tag.IMPLIED)
            || (tag.equals(HTML.Tag.CONTENT))
            || (tag.equals(HTML.Tag.COMMENT)))
        {
          return;
        }
        /* Already managed somewhere else  */
        if (ignoredTags.contains(tag) == false)
        {
          startTag(tag, (AttributeSet) attr);
        }
      }
    } else
    {
      startTag(tag, null);
    }
  }

  protected void writeInlineEndTag(HTML.Tag endTag) throws IOException
  {
    if (endTag.equals(HTML.Tag.IMPLIED)
        || (endTag.equals(HTML.Tag.CONTENT))
        || (endTag.equals(HTML.Tag.COMMENT)))
    {
      return;
    }
    if (ignoredTags.contains(endTag) == false)
    {
      endTag(endTag);
    }
  }

  /**
   * Generates HTML output from a StyledDocument or an HTMLDocument conforming
   * to XHTML 1.0 Strict or XHTML 1.1. It supports StyledDocument by adding
   * appropriate header tags appropriately.
   * 
   * @exception IOException
   *              on any I/O error
   * @exception BadLocationException
   *              if pos represents an invalid location within the document.
   * 
   */
  public void write() throws IOException, BadLocationException
  {
    Object o;
    MutableAttributeSet attributeSet = new SimpleAttributeSet();
    Document doc = getDocument();
    styleNameMapping = new Hashtable();
    if (config.get(CONFIG_WRITE_HEADER).equals(Boolean.TRUE))
    {
      write(XHTMLHelper.DTD);
    }
    attributeSet.addAttribute("xmlns", XHTMLHelper.HTML_NS);
    /* Check if we need to add the language attribute */
    o = doc.getProperty(DocumentProperties.LanguageProperty);
    if ((o != null) && (o instanceof LanguageSelector))
    {
      attributeSet.addAttribute("xml:lang", ((LanguageSelector) o).getLocale().toString());
    }

    startTag(HTML.Tag.HTML, attributeSet);

    /* Verify that the root element is html - for example if from a generic StyledDocument */
    Element elem = doc.getDefaultRootElement();
    if ((elem.getName().equalsIgnoreCase(HTML.Tag.HTML.toString()) == false))
    {
      startTag(HTML.Tag.BODY, null);
      writeHeader();
      writeBody();
      endTag(HTML.Tag.BODY);
      return;
    }

    /* Already a valid HTML document */
    if (elem instanceof AbstractDocument.AbstractElement)
    {
      writeHeader();
      writeBody();
    }
    endTag(HTML.Tag.HTML);
  }

  /**
   * Writes out the &lt;head&gt; and &lt;style&gt; tags, and then invokes
   * writeStyles() to write out all the named styles as the content of the
   * &lt;style&gt; tag. The content is surrounded by valid HTML comment markers
   * to ensure that the document is viewable in applications/browsers that do
   * not support the tag.
   * 
   * @exception IOException
   *              on any I/O error
   */
  protected void writeHeader() throws IOException
  {
    MutableAttributeSet a;
    Document doc = getDocument();
    startTag(HTML.Tag.HEAD, null);
    if (config.get(CONFIG_CSS)!=null)
    {
      startTag(HTML.Tag.STYLE, null);
      startTag(HTML.Tag.COMMENT, null);
//      writeStyles();
      write(config.get(CONFIG_CSS).toString());
      endTag(HTML.Tag.COMMENT);
      endTag(HTML.Tag.STYLE);
    }
    incrIndent();
    incrIndent();
    startTag(HTML.Tag.TITLE, null);
    if (doc.getProperty(Document.TitleProperty) != null)
    {
      write(doc.getProperty(Document.TitleProperty).toString());
    }
    endTag(HTML.Tag.TITLE);
    writeLineSeparator();

    if (doc.getProperty(DocumentProperties.CreatorProperty) != null)
    {
      a = new SimpleAttributeSet();
      a.addAttribute(HTML.Attribute.CONTENT, doc.getProperty(DocumentProperties.CreatorProperty));
      a.addAttribute(HTML.Attribute.NAME, HTMLHelper.META_NAME_CREATOR);
      startTag(HTML.Tag.META, a);
      endTag(HTML.Tag.META);
      writeLineSeparator();
    }
    if (doc.getProperty(DocumentProperties.GeneratorProperty) != null)
    {
      a = new SimpleAttributeSet();
      a.addAttribute(HTML.Attribute.CONTENT, doc.getProperty(DocumentProperties.GeneratorProperty));
      a.addAttribute(HTML.Attribute.NAME, HTMLHelper.META_NAME_GENERATOR);
      startTag(HTML.Tag.META, a);
      endTag(HTML.Tag.META);
      writeLineSeparator();
    }

    if (doc.getProperty(DocumentProperties.SubjectProperty) != null)
    {
      a = new SimpleAttributeSet();
      a.addAttribute(HTML.Attribute.CONTENT, doc.getProperty(DocumentProperties.SubjectProperty));
      a.addAttribute(HTML.Attribute.NAME, HTMLHelper.META_NAME_SUBJECT);
      startTag(HTML.Tag.META, a);
      endTag(HTML.Tag.META);
      writeLineSeparator();
    }
    if (doc.getProperty(DocumentProperties.DescriptionProperty) != null)
    {
      a = new SimpleAttributeSet();
      a.addAttribute(HTML.Attribute.CONTENT,
          doc.getProperty(DocumentProperties.DescriptionProperty));
      a.addAttribute(HTML.Attribute.NAME, HTMLHelper.META_NAME_DESCRIPTION);
      startTag(HTML.Tag.META, a);
      endTag(HTML.Tag.META);
      writeLineSeparator();
    }

    decrIndent();
    decrIndent();
    endTag(HTML.Tag.HEAD);
  }

  /**
   * Writes out all the named styles as the content of the &lt;style&gt; tag.
   * 
   * @exception IOException
   *              on any I/O error
   */
  protected void writeStyles() throws IOException
  {
    /*
     * Access to DefaultStyledDocument done to workaround a missing API in
     * styled document to access the stylenames.
     */
    DefaultStyledDocument styledDoc = ((DefaultStyledDocument) getDocument());
    Enumeration styleNames = styledDoc.getStyleNames();

    while (styleNames.hasMoreElements())
    {
      Style s = styledDoc.getStyle((String) styleNames.nextElement());

      /**
       * PENDING: Once the name attribute is removed from the list we check
       * check for 0.
       **/
      if (s.getAttributeCount() == 1
          && s.isDefined(StyleConstants.NameAttribute))
      {
        continue;
      }
      indent();
      write("p." + addStyleName(s.getName()));
      write(" {\n");
      incrIndent();
      writeAttributes(s);
      decrIndent();
      indent();
      write("}\n");
    }
  }

  /**
   * Emits the start tag for a paragraph. If the paragraph has a named style
   * associated with it, then this method also generates a class attribute for
   * the &lt;p&gt; tag and sets its value to be the name of the style.
   * 
   * @exception IOException
   *              on any I/O error
   * @throws BadLocationException
   */
  protected void writeBranch(Element elem)
      throws IOException, BadLocationException
  {
    AbstractDocument.BranchElement branchElement;
    String elementName = null;

    /*      if (!inRange(elem))
             return;*/

    branchElement = (AbstractDocument.BranchElement) elem;
    /*      ((AbstractElement)elem).dump(System.out,1); */

    AttributeSet attr = elem.getAttributes();
    Object t = attr.getAttribute(StyleConstants.NameAttribute);

    Object resolveAttr = attr.getAttribute(StyleConstants.ResolveAttribute);

    if (t.equals(AbstractDocument.ParagraphElementName))
    {
      t = HTML.Tag.P;
    }

    /* This is an implied paragraph, no tag associated with it. */
    //      if (elem.getName().equals(HTML.Tag.IMPLIED.toString()) == false)
    {
      if (resolveAttr instanceof StyleContext.NamedStyle)
      {
        MutableAttributeSet cssClass = new SimpleAttributeSet();
        cssClass.addAttribute(HTML.Attribute.CLASS, mapStyleName(
            ((StyleContext.NamedStyle) resolveAttr).getName()));
        startTag((HTML.Tag) t, cssClass);
      } else
      {
        startTag((HTML.Tag) t, attr);
      }
    }

    Enumeration<Element> elements = branchElement.children();

    /** Special state machine for writing tables */
    boolean bodyWritten = false; /* tbody has been written */

    while (elements.hasMoreElements())
    {
      Element element = elements.nextElement();
      /*  if (!inRange(element))
           break; */
      if (element instanceof AbstractDocument.BranchElement)
      {
        AttributeSet elementAttr = element.getAttributes();
        Object tag = elementAttr.getAttribute(StyleConstants.NameAttribute);
        
        if (config.get(CONFIG_WRITE_FULL_TABLE).equals(Boolean.TRUE))
        {
          /* Special case for converting the table to a full table format
           * with header and body information.
           */
          if (tag.equals(HTMLHelper.Tag.TR))
          {
            boolean headerWritten = false; /* thead has been written */
            /* Check if the child is a TH or TD to determine the
             * information that needs to be written.
             */
            if (element instanceof AbstractDocument.BranchElement)
            {
              /* Get the first child of TR */
              Enumeration<Element> tableElements = ((AbstractDocument.BranchElement) element)
                  .children();
              Element tableChildElement = tableElements.nextElement();
              AttributeSet rowAttr = tableChildElement.getAttributes();
              Object rowTag = rowAttr.getAttribute(StyleConstants.NameAttribute);
              /* Data row, write TBODY if not already written */
              if ((rowTag.equals(HTMLHelper.Tag.TD)) && (bodyWritten == false))
              {
                startTag(HTMLHelper.TBODY, null);
                bodyWritten = true;
              } else
              /* Header row, write THEAD if not already written */
              if ((rowTag.equals(HTMLHelper.Tag.TH)) && (headerWritten == false))
              {
                startTag(HTMLHelper.THEAD, null);
                headerWritten = true;
              }
            }
            writeBranch(element);
            if (headerWritten == true)
            {
              endTag(HTMLHelper.THEAD);
            }
          } else
          {
            writeBranch(element);
          }
        } else
        /* Not writing full table headers */
        {
          writeBranch(element);
        }
      } else if (isText(element))
      {
        writeContent(element, true);
      } else
      {
        writeLeaf(element);
      }
    } /* end getting through all children elements */
    if (config.get(CONFIG_WRITE_FULL_TABLE).equals(Boolean.TRUE))
    {
      if (bodyWritten == true)
      {
        endTag(HTMLHelper.TBODY);
      }
    }
    endTag((HTML.Tag) t);
  }

  protected void writeComment(String comment) throws IOException
  {
    write("<!--");
    if (comment != null)
    {
      write(comment);
    }
    write("-->");
    writeLineSeparator();
    indentSmart();
  }

  protected void comment(Element elem) throws BadLocationException, IOException
  {
    AttributeSet as = elem.getAttributes();
    if (HTML.Tag.COMMENT.equals(as.getAttribute(StyleConstants.NameAttribute)))
    {
      Object comment = as.getAttribute(HTML.Attribute.COMMENT);
      if (comment instanceof String)
      {
        writeComment(comment.toString());
      }
    }
  }

  /**
   * Responsible for writing out other non-text leaf elements.
   * 
   * @exception IOException
   *              on any I/O error
   * @throws BadLocationException
   */
  protected void writeLeaf(Element elem) throws IOException, BadLocationException
  {
    indent();
    if (elem.getName() == StyleConstants.IconElementName)
    {
      writeImage(elem);
    } else if (elem.getName() == StyleConstants.ComponentElementName)
    {
      writeComponent(elem);
    } else if (elem.getName().equals(HTML.Tag.COMMENT.toString()))
    {
      comment(elem);
    }
    else
    {
      String elementName = elem.getName();
      if (elementName.equals(HTML.Tag.DIV.toString()) == false)
      {
        HTML.Tag t = (HTML.Tag) elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
        startTag(t, elem.getAttributes());
        endTag(t);
      }
    }
  }

  /**
   * Responsible for handling Icon Elements; deliberately unimplemented. How to
   * implement this method is an issue of policy. For example, if you're
   * generating an &lt;img&gt; tag, how should you represent the src attribute
   * (the location of the image)? In certain cases it could be a URL, in others
   * it could be read from a stream.
   * 
   * @param elem
   *          element of type StyleConstants.IconElementName
   */
  protected void writeImage(Element elem) throws IOException
  {
  }

  /**
   * Responsible for handling Component Elements; deliberately unimplemented.
   * How this method is implemented is a matter of policy.
   */
  protected void writeComponent(Element elem) throws IOException
  {
  }

  /**
   * Returns true if the element is a text element.
   * 
   */
  protected boolean isText(Element elem)
  {
    return (elem.getName() == AbstractDocument.ContentElementName);
  }

  /**
   * Write the specified element.
   * 
   * @param elem
   *          The element to write
   * @throws IOException
   * @throws BadLocationException
   */
  protected void writeElement(Element elem) throws IOException, BadLocationException
  {
    if (elem instanceof AbstractDocument.BranchElement)
    {
      writeBranch(elem);
    } else if (isText(elem))
    {
      writeContent(elem, false);
    } else
    {
      writeLeaf(elem);
    }
  }

  /**
   * Iterates over the elements in the document and processes elements based on
   * whether they are branch elements or leaf elements. This method specially
   * handles leaf elements that are text.
   * 
   * @exception IOException
   *              on any I/O error
   */
  protected void writeBody() throws IOException, BadLocationException
  {

    Element next = null;
    Element rootElement = null;

    /* Find and verify if the root element is a valid SectionElement */
    rootElement = getDocument().getDefaultRootElement();

    /*
     * Normally this needs to be a SectionElement (BranchElement), otherwise
     * do nothing
     */
    if ((rootElement instanceof AbstractDocument.BranchElement) == false)
    {
      return;
    }

    /* Skip some elements that are written manually here. */
    for (int i = 0; i < rootElement.getElementCount(); i++)
    {
      Element element = rootElement.getElement(i);
      /*  Strangely enough, on empty documents, body is not written.        
       *       if (!inRange(element))
                  break; */
      if (element.getName().equals(HTML.Tag.HTML.toString()))
      {
        throw new IllegalArgumentException("HTML Tag found where it should not be.");
      }
      if (element.getName().equals(HTML.Tag.HEAD.toString()))
      {
        continue;
      }
      next = element;
      break;
    }

    if (next != null)
    {
      writeElement(next);
    }
  }

  /**
   * Writes out the attribute set in an HTML-compliant manner.
   * 
   * @exception IOException
   *              on any I/O error
   * @exception BadLocationException
   *              if pos represents an invalid location within the document.
   */
  protected void writeContent(Element elem, boolean needsIndenting)
      throws IOException, BadLocationException
  {
    String elementName;
    Stack<HTML.Tag> stack;
    AttributeSet attr = elem.getAttributes();

    /*     System.err.println(elem.getName());
         HTMLUtils.printAttribs(attr);*/
    /*
     * HTMLUtils.printAttribs(attr); HTMLUtils.printText(elem);
     */
    // writeNonHTMLAttributes(attr);

    /*
     * System.err.println(elem.getName()); HTMLUtils.printAttribs(attr);
     */

    /*
     * Convert HTML Attributes to CSS Attributes Convert StyleConstants
     * Attributes to CSS Attributes
     */
    //  to = htmlHelper.convertToXHTML(attr);

    //      System.err.println(elem.getName()); 
    //      HTMLUtils.printAttribs(to);

    elementName = attr.getAttribute(StyleConstants.NameAttribute).toString();
    /* Write HTML tags. */
    stack = writeContentStartTag(attr);
    text(elem);
    writeContentEndTag(stack);
  }

  /** Returns CSS attributes from the specified attributes. */
  protected AttributeSet getCSSAttributes(AttributeSet in)
  {
    Object o;
    Color c;
    MutableAttributeSet styleAttributes = new SimpleAttributeSet();

    o = htmlHelper.getFontFamily(in);
    if (o != null)
    {
      htmlHelper.setFontFamily(styleAttributes, o.toString());
    }
    c = htmlHelper.getForeground(in);
    if (c != null)
    {
      htmlHelper.setForeground(styleAttributes, c);
    }

    c = htmlHelper.getBackground(in);
    if (c != null)
    {
      htmlHelper.setBackground(styleAttributes, c);
    }
    if (htmlHelper.isStrikeThrough(in))
    {
      htmlHelper.setStrikeThrough(styleAttributes, true);
    }
    if (htmlHelper.isUnderline(in))
    {
      htmlHelper.setUnderline(styleAttributes, true);
    }
    return styleAttributes;
  }

  @Override
  protected void text(Element elem) throws BadLocationException, IOException
  {
    Segment segment = null;
    int start = Math.max(getStartOffset(), elem.getStartOffset());
    int end = Math.min(getEndOffset(), elem.getEndOffset());
    if (start < end)
    {
      if (segment == null)
      {
        segment = new Segment();
      }
      String str = getDocument().getText(start, end - start);
      if (str.length() > 0)
      {
        setCanWrapLines(!inPre);
        if (str.length() > 0)
        {
          if (inPre == false)
          {
            str = str.replaceAll("\n", "");
            str = HTMLHelper.escape(str);
            /* Replace multiple whitespace with a single space. */
            str = str.replaceAll(" +", " ");
          }
          segment = new Segment(str.toCharArray(), 0, str.length());
          write(segment.array, segment.offset, segment.count);
        }
        setCanWrapLines(false);
      }
    }
  }

  /*
    * The HTML parser creates a content, that can contain multiple HTML Tags
    * where each one can have attributes, so manage them here.
    */
  protected Stack<HTML.Tag> writeContentStartTag(AttributeSet attr) throws IOException
  {
    AttributeSet styleAttributes = getCSSAttributes(attr);
    Stack<HTML.Tag> stack = new Stack<HTML.Tag>();

    if (htmlHelper.isBold(attr))
    {
      stack.push(HTML.Tag.B);
      startTag(HTML.Tag.B, null);
    }
    if (htmlHelper.isItalic(attr))
    {
      stack.push(HTML.Tag.I);
      startTag(HTML.Tag.I, null);
    }
    /*      if (htmlHelper.isStrikeThrough(attr))
          {
            stack.push(HTML.Tag.S);
            write("<s>");
          }*/
    if (htmlHelper.isSubscript(attr))
    {
      stack.push(HTML.Tag.SUB);
      startTag(HTML.Tag.SUB, null);
    }
    if (htmlHelper.isSuperscript(attr))
    {
      stack.push(HTML.Tag.SUP);
      startTag(HTML.Tag.SUP, null);
    }

    /* Only span information if style attributes are supported. */
    if ((styleAttributes.getAttributeCount() > 0)
        && (config.get(CONFIG_STYLES).equals(Boolean.TRUE)))
    {
      stack.push(HTML.Tag.SPAN);
      startTag(HTML.Tag.SPAN, styleAttributes);
    }

    Enumeration names = attr.getAttributeNames();
    while (names.hasMoreElements())
    {
      Object name = names.nextElement();
      Object value = attr.getAttribute(name);
      if (name instanceof HTML.Tag)
      {
        /* Already managed somewhere else. */
        if (ignoredTags.contains(name) == false)
        {
          stack.push((HTML.Tag) name);
          if (value instanceof AttributeSet)
          {
            startTag((HTML.Tag) name, (AttributeSet) value);
          } else
          {
            startTag((HTML.Tag) name, null);
          }
        }
      }
    }
    return stack;
  }

  protected void writeContentEndTag(Stack<HTML.Tag> tags) throws IOException
  {
    while (tags.isEmpty() == false)
    {
      endTag(tags.pop());
    }
  }

  /**
   * Adds the style named <code>style</code> to the style mapping. This returns
   * the name that should be used when outputting. CSS does not allow the full
   * Unicode set to be used as a style name.
   */
  private String addStyleName(String style)
  {
    if (styleNameMapping == null)
    {
      return style;
    }
    StringBuffer sb = null;
    for (int counter = style.length() - 1; counter >= 0; counter--)
    {
      if (!isValidCharacter(style.charAt(counter)))
      {
        if (sb == null)
        {
          sb = new StringBuffer(style);
        }
        sb.setCharAt(counter, 'a');
      }
    }
    String mappedName = (sb != null) ? sb.toString() : style;
    while (styleNameMapping.get(mappedName) != null)
    {
      mappedName = mappedName + 'x';
    }
    styleNameMapping.put(style, mappedName);
    return mappedName;
  }

  /**
   * Returns the mapped style name corresponding to <code>style</code>.
   */
  private String mapStyleName(String style)
  {
    if (styleNameMapping == null)
    {
      return style;
    }
    String retValue = (String) styleNameMapping.get(style);
    return (retValue == null) ? style : retValue;
  }

  private boolean isValidCharacter(char character)
  {
    return ((character >= 'a' && character <= 'z')
    || (character >= 'A' && character <= 'Z'));
  }

  /** Set configuration parameters */
  public void setParameter(String name, Object value)
  {
    config.put(name, value);
  }

  /** Get configuration parameter. */
  @Override
  public Object getParameter(String name)
  {
    return config.get(name);
  }

  /**
   * Writes out a start tag for the element. Ignores all synthesized elements.
   * 
   * @param elem
   *          an Element
   * @exception IOException
   *              on any I/O error
   */
  protected void startTag(HTML.Tag t, AttributeSet attr) throws IOException
  {
    /* Do not write synthesized elements */
    if (t.equals(HTML.Tag.IMPLIED))
      return;

    if (t.equals(HTML.Tag.PRE))
    {
      inPre = true;
    }

    if (t.isBlock())
    {
      incrIndent();
      indent();
    }
    if (requiresLineSeparator.contains(t) == true)
    {
      writeLineSeparator();
    }
    //  indentSmart();
    write('<');
    write(t.toString());
    writeAttributes(attr);
    write('>');

    /** Verify if a new line is required after the start of the element. */
    if (requiresLineSeparatorAfterTagStart.contains(t) == true)
    {
      writeLineSeparator();
    }

    /*  if (name == HTML.Tag.TEXTAREA) 
      {
          textAreaContent(elem.getAttributes());
      } 
      else if (name == HTML.Tag.SELECT) {
          selectContent(elem.getAttributes());
      }*/
    if (t.isBlock())
    {
      decrIndent();
    }

  }

  protected void endTag(HTML.Tag t) throws IOException
  {
    if (t.equals(HTML.Tag.IMPLIED))
      return;

    if (!inPre)
    {
      // indentSmart();
    }

    if (t.isPreformatted())
    {
      inPre = false;
    }
    write('<');
    write('/');
    write(t.toString());
    write('>');
    if (t.isBlock() == true)
    {
      writeLineSeparator();
    } else if (requiresLineSeparator.contains(t) == true)
    {
      writeLineSeparator();
    }
    /* if (t.breaksFlow() == true)
     {
       writeLineSeparator();
     }*/
  }

  protected void indentSmart() throws IOException
  {
    if (!indented)
    {
      indent();
      indented = true;
    }
  }

  @Override
  protected void writeLineSeparator() throws IOException
  {
    super.writeLineSeparator();
    indented = false;
  }

  protected void write(char[] chars, int startIndex, int length)
      throws IOException
  {
    if (!getCanWrapLines())
    {
      // We can not break string, just track if a newline
      // is in it.
      int lastIndex = startIndex;
      int endIndex = startIndex + length;
      int newlineIndex = indexOf(chars, NEWLINE, startIndex, endIndex);
      while (newlineIndex != -1)
      {
        if (newlineIndex > lastIndex)
        {
          output(chars, lastIndex, newlineIndex - lastIndex);
        }
        writeLineSeparator();
        lastIndex = newlineIndex + 1;
        newlineIndex = indexOf(chars, '\n', lastIndex, endIndex);
      }
      if (lastIndex < endIndex)
      {
        output(chars, lastIndex, endIndex - lastIndex);
      }
    }
    else
    {
      // We can break chars if the length exceeds maxLength.
      int lastIndex = startIndex;
      int endIndex = startIndex + length;
      int lineLength = getCurrentLineLength();
      int maxLength = getLineLength();

      while (lastIndex < endIndex)
      {
        int newlineIndex = indexOf(chars, NEWLINE, lastIndex,
            endIndex);
        boolean needsNewline = false;
        boolean forceNewLine = false;

        lineLength = getCurrentLineLength();
        if (newlineIndex != -1 && (lineLength +
            (newlineIndex - lastIndex)) < maxLength)
        {
          if (newlineIndex > lastIndex)
          {
            output(chars, lastIndex, newlineIndex - lastIndex);
          }
          lastIndex = newlineIndex + 1;
          forceNewLine = true;
        }
        else if (newlineIndex == -1 && (lineLength +
            (endIndex - lastIndex)) < maxLength)
        {
          if (endIndex > lastIndex)
          {
            output(chars, lastIndex, endIndex - lastIndex);
          }
          lastIndex = endIndex;
        }
        else
        {
          // Need to break chars, find a place to split chars at,
          // from lastIndex to endIndex,
          // or maxLength - lineLength whichever is smaller
          int breakPoint = -1;
          int maxBreak = Math.min(endIndex - lastIndex,
              maxLength - lineLength - 1);
          int counter = 0;
          while (counter < maxBreak)
          {
            if (Character.isWhitespace(chars[counter +
                lastIndex]))
            {
              breakPoint = counter;
            }
            counter++;
          }
          if (breakPoint != -1)
          {
            // Found a place to break at.
            breakPoint += lastIndex + 1;
            output(chars, lastIndex, breakPoint - lastIndex);
            lastIndex = breakPoint;
            needsNewline = true;
          }
          else
          {
            // No where good to break.

            // find the next whitespace, or write out the
            // whole string.
            // maxBreak will be negative if current line too
            // long.
            counter = Math.max(0, maxBreak);
            maxBreak = endIndex - lastIndex;
            while (counter < maxBreak)
            {
              if (Character.isWhitespace(chars[counter +
                  lastIndex]))
              {
                breakPoint = counter;
                break;
              }
              counter++;
            }
            if (breakPoint == -1)
            {
              output(chars, lastIndex, endIndex - lastIndex);
              breakPoint = endIndex;
            }
            else
            {
              breakPoint += lastIndex;
              if (chars[breakPoint] == NEWLINE)
              {
                output(chars, lastIndex, breakPoint++ -
                    lastIndex);
                forceNewLine = true;
              }
              else
              {
                output(chars, lastIndex, ++breakPoint -
                    lastIndex);
                needsNewline = true;
              }
            }
            lastIndex = breakPoint;
          }
        }
        if (forceNewLine || needsNewline || lastIndex < endIndex)
        {
          writeLineSeparator();
          if (lastIndex < endIndex || !forceNewLine)
          {
            indent();
          }
        }
      }
    }
  }

  /**
   * Support method to locate an occurence of a particular character.
   */
  private int indexOf(char[] chars, char sChar, int startIndex,
      int endIndex)
  {
    while (startIndex < endIndex)
    {
      if (chars[startIndex] == sChar)
      {
        return startIndex;
      }
      startIndex++;
    }
    return -1;
  }

}

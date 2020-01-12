package javax.swing.text.html;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;

import net.atlanticbb.tantlinger.ui.text.dialogs.PropertiesPanel;

import com.optimasc.text.DocumentProperties;
import com.optimasc.text.html.HTMLHelper;
import com.optimasc.text.html.XHTMLParser;

/**
 * Extension of the HTMLDocument class that has been overriden to suppport
 * reading document properties.
 * 
 * @author Carl Eric Codere
 * 
 */
public class XHTMLDocument extends HTMLDocumentEx
{
  private static char[] NEWLINE = { '\n' };

  protected Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public XHTMLDocument(StyleSheet styles)
  {
    super(styles);
    setParser(new XHTMLParser());
  }

  public HTMLEditorKit.ParserCallback getReader(int pos)
  {
    Object desc = getProperty(Document.StreamDescriptionProperty);
    if (desc instanceof URL)
    {
      setBase((URL) desc);
    }
    return new XHTMLReader(pos);
  }

  @Override
  public HTMLEditorKit.ParserCallback getReader(int pos, int popDepth, int pushDepth, Tag insertTag)
  {
    return getReader(pos, popDepth, pushDepth, insertTag, true);
  }

  @Override
  HTMLEditorKit.ParserCallback getReader(int pos, int popDepth, int pushDepth, Tag insertTag,
      boolean insertInsertTag)
  {
    Object desc = getProperty(Document.StreamDescriptionProperty);
    if (desc instanceof URL)
    {
      setBase((URL) desc);
    }
    XHTMLReader reader = new XHTMLReader(pos, popDepth, pushDepth,
        insertTag);
    return reader;
  }

  @Override
  protected void insertHTML(Element parent, int offset, String html, boolean wantsTrailingNewline)
      throws BadLocationException, IOException
  {
    if (parent != null && html != null)
    {
      HTMLEditorKit.Parser parser = getParser();
      if (parser != null)
      {
        int lastOffset = Math.max(0, offset - 1);
        Element charElement = getCharacterElement(lastOffset);
        Element commonParent = parent;
        int pop = 0;
        int push = 0;

        if (parent.getStartOffset() > lastOffset)
        {
          while (commonParent != null &&
              commonParent.getStartOffset() > lastOffset)
          {
            commonParent = commonParent.getParentElement();
            push++;
          }
          if (commonParent == null)
          {
            throw new BadLocationException("No common parent",
                offset);
          }
        }
        while (charElement != null && charElement != commonParent)
        {
          pop++;
          charElement = charElement.getParentElement();
        }
        if (charElement != null)
        {
          // Found it, do the insert.
          XHTMLReader reader = new XHTMLReader(offset, pop - 1, push,
              null, false, true,
              wantsTrailingNewline);

          parser.parse(new StringReader(html), reader, true);
          reader.flush();
        }
      }
    }
  }

  /**
   * New version of the HTML Reader that is more modern and only manages XHTML
   * documents.
   * 
   * @author Carl Eric
   * 
   */
  public class XHTMLReader extends HTMLReader
  {
    //    protected Map<HTML.Tag, ElementAction> elementMap = new Hashtable<HTML.Tag,ElementAction>();

    public XHTMLReader(int offset, int popDepth, int pushDepth,
        HTML.Tag insertTag)
    {
      super(offset, popDepth, pushDepth, insertTag);
      registerTag(HTML.Tag.META, new XHTMLMetaAction());
      registerTag(HTML.Tag.HTML, new XHTMLBlockAction());
      registerTag(HTMLHelper.TBODY, new XHTMLBlockAction());
      registerTag(HTMLHelper.THEAD, new XHTMLBlockAction());
      registerTag(HTMLHelper.ADDRESS_X, new XHTMLBlockAction());
    }

    public XHTMLReader(int offset)
    {
      super(offset);
      registerTag(HTML.Tag.META, new XHTMLMetaAction());
      registerTag(HTML.Tag.HTML, new XHTMLBlockAction());
      registerTag(HTMLHelper.TBODY, new XHTMLBlockAction());
      registerTag(HTMLHelper.THEAD, new XHTMLBlockAction());
      registerTag(HTMLHelper.ADDRESS_X, new XHTMLBlockAction());
    }

    XHTMLReader(int offset, int popDepth, int pushDepth, Tag insertTag,
        boolean insertInsertTag, boolean insertAfterImplied, boolean wantsTrailingNewline)
    {
      super(offset, popDepth, pushDepth, insertTag, insertInsertTag, insertAfterImplied,
          wantsTrailingNewline);
      registerTag(HTML.Tag.META, new XHTMLMetaAction());
      registerTag(HTML.Tag.HTML, new XHTMLBlockAction());
      registerTag(HTMLHelper.TBODY, new XHTMLBlockAction());
      registerTag(HTMLHelper.THEAD, new XHTMLBlockAction());
      registerTag(HTMLHelper.ADDRESS_X, new XHTMLBlockAction());
    }

    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
    {
      super.handleStartTag(t, a, pos);
    }

    @Override
    public void handleText(char[] data, int pos)
    {
      super.handleText(data, pos);
    }

    @Override
    public void handleEndTag(Tag t, int pos)
    {
      super.handleEndTag(t, pos);
    }

    /***********************************************************************************************************/
    /***********************************************************************************************************/

    public class XHTMLMetaAction extends HiddenAction
    {
      @Override
      public void start(Tag t, MutableAttributeSet a)
      {
        if (a.isDefined(HTML.Attribute.NAME))
        {
          Object o = a.getAttribute(HTML.Attribute.NAME);
          Object value = a.getAttribute(HTML.Attribute.CONTENT);
          if (value != null)
          {
            if (DocumentProperties.isTitle(o.toString()))
            {
              putProperty(Document.TitleProperty, value);
            }
            if (DocumentProperties.isCreator(o.toString()))
            {
              putProperty(DocumentProperties.CreatorProperty, value);
            }
            if (DocumentProperties.isContributors(o.toString()))
            {
              putProperty(DocumentProperties.ContributorProperty, value);
            }
            if (DocumentProperties.isSubject(o.toString()))
            {
              putProperty(DocumentProperties.SubjectProperty, value);
            }
            if (DocumentProperties.isDescription(o.toString()))
            {
              putProperty(DocumentProperties.DescriptionProperty, value);
            }
          }
        }
        super.start(t, a);
      }

      @Override
      public void end(Tag t)
      {
        super.end(t);
      }
    }


    public class XHTMLBlockAction extends BlockAction
    {
      @Override
      public void start(Tag t, MutableAttributeSet a)
      {
        if (t.equals(HTML.Tag.HTML))
        {
          if (a.isDefined("xml:lang"))
          {
            Object value = a.getAttribute("xml:lang");
            if (value != null)
            {
              String s = value.toString();
              Locale lo = new Locale(s);
              if ((lo.getISO3Language() == null) || (lo.getISO3Language().length() == 0))
              {
                logger.warning("Invalid 'xml:lang' attribute value: " + s);
              } else
              {
                putProperty(DocumentProperties.LanguageProperty,
                    new PropertiesPanel.LanguageSelector(lo));
              }
            }
          }
        }
        super.start(t, a);
      }

      @Override
      public void end(Tag t)
      {
        super.end(t);
      }
    }

  }

}

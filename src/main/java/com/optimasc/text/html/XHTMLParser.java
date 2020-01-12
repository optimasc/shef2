package com.optimasc.text.html;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.Parser;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implementation of {@link javax.swing.text.html.parser.Parser} HTML parser
 * call but which parses XHTML documents instead of SGML documents.
 * 
 * It can be used to parse XML documents as well as XML Fragments, as defined in
 * the {@link javax.swing.text.html.parser.DocumentParser} class, in the case
 * where XHTML fragments does not contain the valid header tags, they will be
 * implicitly added. The following tags will implicitly be added
 * <code>DOCTYPE, html,head,body</code> and when the callback is called an
 * attribute For tags that are implied, the AttributeSet argument will have a
 * value of <code>Boolean.TRUE</code> for the key
 * {@link javax.swing.text.html.HTMLEditorKit.ParserCallback.IMPLIED}.
 * 
 * 
 * It uses the SAX Parser to parse the XML, with no schema validation performed
 * for the callback, because it is used in the editor by fragments which would
 * not successfully validate against the XHTML Schema.
 * 
 * In current {@link javax.swing.text.html.HTMLDocument} implementation offsets
 * are IGNORED when constructing the document, therefore the offset should be
 * considered invalid and is not used at all in the parser.
 * 
 * @author Carl Eric Codere
 * 
 */
public class XHTMLParser extends Parser
{
  Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public static final String SVG_TAG = "svg";

  static final String W3C_XML_SCHEMA =
      "http://www.w3.org/2001/XMLSchema";

  static final String JAXP_SCHEMA_SOURCE =
      "http://java.sun.com/xml/jaxp/properties/schemaSource";

  /** Parser added some tags implicitly to parse successfully */
  protected boolean impliedHeader;
  
  private final static Pattern DOUBLE_SPACE_NORMALIZE = Pattern.compile("\\s{2,}");
  private final static Pattern SINGLE_SPACE_NORMALIZE = Pattern.compile("\r\n");

  
  /** Normalize all double whitespace to a single space character,
   *  and normalize all cr/lf to no character.
   *  
   * @param s
   * @return
   */
  public static String normalize(String s) 
  {
      s  = DOUBLE_SPACE_NORMALIZE.matcher(s).replaceAll(" ");
      return SINGLE_SPACE_NORMALIZE.matcher(s).replaceAll("");

  }
  
  
  /** Remove leading white space from a string. */
  public String ltrim(String s)
  {
    int i = 0;
    while (i < s.length() && Character.isWhitespace(s.charAt(i)))
    {
      i++;
    }
    return s.substring(i);
  }
  
  

  public static class Input implements LSInput
  {

    private String publicId;

    private String systemId;

    public String getPublicId()
    {
      return publicId;
    }

    public void setPublicId(String publicId)
    {
      this.publicId = publicId;
    }

    public String getBaseURI()
    {
      return null;
    }

    public InputStream getByteStream()
    {
      return null;
    }

    public boolean getCertifiedText()
    {
      return false;
    }

    public Reader getCharacterStream()
    {
      return null;
    }

    public String getEncoding()
    {
      return null;
    }

    public String getStringData()
    {
      synchronized (inputStream)
      {
        try
        {
          byte[] input = new byte[inputStream.available()];
          inputStream.read(input);
          String contents = new String(input);
          return contents;
        } catch (IOException e)
        {
          e.printStackTrace();
          System.out.println("Exception " + e);
          return null;
        }
      }
    }

    public void setBaseURI(String baseURI)
    {
    }

    public void setByteStream(InputStream byteStream)
    {
    }

    public void setCertifiedText(boolean certifiedText)
    {
    }

    public void setCharacterStream(Reader characterStream)
    {
    }

    public void setEncoding(String encoding)
    {
    }

    public void setStringData(String stringData)
    {
    }

    public String getSystemId()
    {
      return systemId;
    }

    public void setSystemId(String systemId)
    {
      this.systemId = systemId;
    }

    public BufferedInputStream getInputStream()
    {
      return inputStream;
    }

    public void setInputStream(BufferedInputStream inputStream)
    {
      this.inputStream = inputStream;
    }

    private BufferedInputStream inputStream;

    public Input(String publicId, String sysId, InputStream input)
    {
      this.publicId = publicId;
      this.systemId = sysId;
      this.inputStream = new BufferedInputStream(input);
    }
  }

  /**
   * Entity resolver to parse the DTD. We return local copies of the DTD we
   * support, otherwise return an error
   */
  public static class LocalXMLEntityResolver implements XMLResolver
  {

    public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace)
    {
      /* Return the local versions of what we allow. */
      if (publicID.equals("-//W3C//DTD XHTML 1.0 Strict//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml1-strict.dtd");
        return is;
      }
      if (publicID.equals("-//W3C//ENTITIES Latin 1 for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-lat1.ent");
        return is;
      }
      if (publicID.equals("-//W3C//ENTITIES Symbols for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-symbol.ent");
        return is;
      }
      if (publicID.equals("-//W3C//ENTITIES Special for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-special.ent");
        return is;
      }
      throw new IllegalArgumentException("Illegal or unsupported Document Type (DTD): \""
          + publicID + "\"");
    }

  }

  /**
   * Entity resolver to parse the DTD. We return local copies of the DTD we
   * support, otherwise return an error
   */
  public static class LocalEntityResolver implements EntityResolver
  {

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
        IOException
    {
      /* Return the local versions of what we allow. */
      if (publicId.equals("-//W3C//DTD XHTML 1.0 Strict//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml1-strict.dtd");
        return new InputSource(is);
      }
      if (publicId.equals("-//W3C//ENTITIES Latin 1 for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-lat1.ent");
        return new InputSource(is);
      }
      if (publicId.equals("-//W3C//ENTITIES Symbols for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-symbol.ent");
        return new InputSource(is);
      }
      if (publicId.equals("-//W3C//ENTITIES Special for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-special.ent");
        return new InputSource(is);
      }
      throw new SAXNotSupportedException("Illegal or unsupported Document Type (DTD): \""
          + publicId + "\"");
    }

  }

  public static class ResourceResolver implements LSResourceResolver
  {

    public LSInput resolveResource(String type, String namespaceURI,
        String publicId, String systemId, String baseURI)
    {

      // note: in this sample, the XSD's are expected to be in the root of the classpath
      InputStream resourceAsStream = this.getClass().getClassLoader()
          .getResourceAsStream("res/" + systemId);
      return new Input(publicId, systemId, resourceAsStream);
    }

  }

  /**
   * The default handler used to retrieve the data and pass it to the parser
   * callback.
   * 
   * Only valid tags documented {@link in javax.swing.text.html.HTML.Tag} are
   * actually created and passed to the parser callback, otherwise they are
   * passed as {@link javax.swing.text.html.HTML.UnknownTag}.
   * 
   * <code>DTD</code>'s are also lost and ignored.
   * 
   */
  public class XHTMLCallbackHandler extends DefaultHandler implements LexicalHandler
  {
    /* Pass 2 usage */
    ParserCallback cb;
    StringWriter outputWriter;
    int offset = 0;
    int textOffset = 0;
    /* Indicate if we are in a preformatted block .*/
    boolean preformatted;
    /** Contains the last tag from startElement */
    HTML.Tag lastTag;

    /** Contains the tags that can contain PCDATA */
    public Vector<HTML.Tag> canContainText = new Vector<HTML.Tag>();

    public XHTMLCallbackHandler(ParserCallback callback)
    {
      super();
      cb = callback;
      /* For debugging purposes */
      outputWriter = new StringWriter();
      canContainText.add(HTML.Tag.TITLE);
      /*      canContainText.add(HTML.Tag.ABBR);
            canContainText.add(HTML.Tag.ACRONYM);*/
      canContainText.add(HTML.Tag.ADDRESS);
      canContainText.add(HTMLHelper.ADDRESS_X);
      canContainText.add(HTML.Tag.BLOCKQUOTE);
      canContainText.add(HTML.Tag.CITE);
      canContainText.add(HTML.Tag.CODE);
      canContainText.add(HTML.Tag.DFN);
      canContainText.add(HTML.Tag.DIV);
      canContainText.add(HTML.Tag.EM);
      canContainText.add(HTML.Tag.H1);
      canContainText.add(HTML.Tag.H2);
      canContainText.add(HTML.Tag.H3);
      canContainText.add(HTML.Tag.H4);
      canContainText.add(HTML.Tag.H5);
      canContainText.add(HTML.Tag.H6);
      canContainText.add(HTML.Tag.KBD);
      canContainText.add(HTML.Tag.P);
      canContainText.add(HTML.Tag.PRE);
      /*      canContainText.add(HTML.Tag.Q);*/
      canContainText.add(HTML.Tag.SAMP);
      canContainText.add(HTML.Tag.SPAN);
      canContainText.add(HTML.Tag.STRONG);
      canContainText.add(HTML.Tag.VAR);

      canContainText.add(HTML.Tag.A);

      canContainText.add(HTML.Tag.DT);
      canContainText.add(HTML.Tag.DD);
      canContainText.add(HTML.Tag.LI);

      canContainText.add(HTML.Tag.APPLET);

      canContainText.add(HTML.Tag.B);
      canContainText.add(HTML.Tag.BIG);
      canContainText.add(HTML.Tag.I);
      canContainText.add(HTML.Tag.SMALL);
      canContainText.add(HTML.Tag.SUB);
      canContainText.add(HTML.Tag.SUP);
      canContainText.add(HTML.Tag.TT);

      /*      canContainText.add(HTML.Tag.DEL);
            canContainText.add(HTML.Tag.INS);*/

      /*      canContainText.add(HTML.Tag.BDO); */

      /*      canContainText.add(HTML.Tag.LABEL); */
      canContainText.add(HTML.Tag.OPTION);
      canContainText.add(HTML.Tag.TEXTAREA);

      /*      
            canContainText.add(HTML.Tag.FIELDSET);
            canContainText.add(HTML.Tag.BUTTON);
            canContainText.add(HTML.Tag.LEGEND); */

      canContainText.add(HTML.Tag.CAPTION);
      canContainText.add(HTML.Tag.TD);
      canContainText.add(HTML.Tag.TH);

      canContainText.add(HTML.Tag.OBJECT);

      canContainText.add(HTML.Tag.SCRIPT);

      canContainText.add(HTML.Tag.STYLE);

      /** Legacy module */
      canContainText.add(HTML.Tag.CENTER);
      canContainText.add(HTML.Tag.FONT);
      canContainText.add(HTML.Tag.S);
      canContainText.add(HTML.Tag.STRIKE);
      canContainText.add(HTML.Tag.U);
    }

    public void setOffset(int offset)
    {
      this.offset = offset;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException,
        SAXException
    {
      /* Return the local versions of what we allow. */
      if (publicId.equals("-//W3C//DTD XHTML 1.0 Strict//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml1-strict.dtd");
        return new InputSource(is);
      }
      if (publicId.equals("-//W3C//ENTITIES Latin 1 for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-lat1.ent");
        return new InputSource(is);
      }
      if (publicId.equals("-//W3C//ENTITIES Symbols for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-symbol.ent");
        return new InputSource(is);
      }
      if (publicId.equals("-//W3C//ENTITIES Special for XHTML//EN"))
      {
        InputStream is = XHTMLParser.class.getClassLoader()
            .getResourceAsStream("res/xhtml-special.ent");
        return new InputSource(is);
      }
      throw new SAXNotSupportedException("Illegal or unsupported Document Type (DTD): \""
          + publicId + "\"");
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException
    {
      logger.warning("Validation warning: " + exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException
    {
      logger.severe("Validation error: " + exception.getMessage());
      throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException
    {
      logger.severe("Validation error: " + exception.getMessage());
      throw exception;
    }

    @Override
    public void startDocument() throws SAXException
    {
      super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException
    {
      super.endDocument();
    }

    public void setDocumentLocator(Locator locator)
    {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
    {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
    {
      int count;
      HTML.Attribute attr = null;

      HTML.Tag t = HTMLHelper.getTag(qName);
      if (t == null)
      {
        t = new HTML.UnknownTag(qName);
      }
      lastTag = t;
      if (t != null)
      {
        if (t.isPreformatted() == true)
        {
          preformatted = true;
        } else
        {
          preformatted = false;
        }
        MutableAttributeSet a = new SimpleAttributeSet();
        /* Get all specified attributes */
        count = attributes.getLength();
        for (int i = 0; i < count; i++)
        {
          String attName = attributes.getQName(i);
          String value = attributes.getValue(i);
          attr = HTML.getAttributeKey(attName);
          if (attr != null)
          {
            a.addAttribute(attr, value);
          } else
          {
            a.addAttribute(attName, value);
          }
        }

        /**
         * Were some required tags/elements added manually Then set attributes
         * accordingly.
         */
        if (impliedHeader)
        {
          if (t.equals(HTML.Tag.HTML) || t.equals(HTML.Tag.BODY)
              || t.equals(HTML.Tag.HEAD))
          {
            a.addAttribute(HTMLEditorKit.ParserCallback.IMPLIED, Boolean.TRUE);
          }
        }
        cb.handleStartTag(t, a, offset);
        outputWriter.append("<" + t.toString() + ">");
      }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
      HTML.Tag t = HTMLHelper.getTag(qName);
      if (t == null)
      {
        t = new HTML.UnknownTag(qName);
      }
      if (t != null)
      {
        if (t.isPreformatted() == false)
        {
          preformatted = false;
        }
        cb.handleEndTag(t, offset);
        outputWriter.append("</" + t.toString() + ">");
      }
    }

    

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
      /* When the header is implied always pass to the callback the character text, 
       * as it is what is expected in the current implementation.
       */
      if (((lastTag != null))
          || (lastTag instanceof HTML.UnknownTag)
          || (impliedHeader == true))
      {
        textOffset = offset;
        String str = new String(ch, start, length);
        if (preformatted == false)
        {
          /**
           * Follow the 'white-space' processing model with whitespace property
           * 'normal' of the Cascading Style Sheets Level 2 Revision 1 (CSS 2.1)
           * Specification.
           */
          str = normalize(str);

          /* If last element was a block element, trim the start of the string,
           * not the right side. 
           */
          if (lastTag.isBlock() == true)
          {
            str = ltrim(str);
          }
          /* Remove strings with whitespace only. */
          if (str.trim().length() > 0)
          {
            outputWriter.append(str);
            cb.handleText(str.toCharArray(), textOffset);
          }
        } else
        {
          outputWriter.append(str);
          cb.handleText(str.toCharArray(), textOffset);
        }
      }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException
    {
    }

    @Override
    public void endDTD() throws SAXException
    {
    }

    @Override
    public void startEntity(String name) throws SAXException
    {
    }

    @Override
    public void endEntity(String name) throws SAXException
    {
    }

    @Override
    public void startCDATA() throws SAXException
    {
    }

    @Override
    public void endCDATA() throws SAXException
    {
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException
    {
      String str = new String(ch, start, length);
      cb.handleComment(str.toCharArray(), 0);
    }
  } /* end class */

  /**
   * Converts a localName + prefix to standard qName format expected by SAX.
   * 
   * @param prefix
   *          The prefix, can be null.
   * @param localName
   *          The localname cannot, be null
   * @return
   */
  public static String getQName(String prefix, String localName)
  {
    if ((prefix == null) || (prefix.length() == 0))
    {
      return localName;
    }
    return prefix + ":" + localName;
  }

  public void parse(Reader r, ParserCallback cb, boolean ignoreCharSet) throws IOException
  {
    XMLStreamReader reader;
    int eventType;
    XHTMLCallbackHandler handler;
    int c;
    AttributesImpl attributes = new AttributesImpl();

    StringWriter w = new StringWriter();
    StringReader strReader;
    while ((c = r.read()) != -1)
    {
      w.append((char) c);
    }
    String str = w.toString();
    impliedHeader = false;
    /* No valid header, maybe an XHTML fragment, add a fake root element */
    if (str.indexOf("<html") == -1)
    {
      /* Need to add DOCTYPE html to resolve entities */
      str = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
          +
          "<html><head></head><body>" + str + "</body></html>";
      /** Indicate that the required elements were added manually */
      impliedHeader = true;
    }
    strReader = new StringReader(str);

    try
    {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      factory.setXMLResolver(new LocalXMLEntityResolver());
      /* We cannot split whitespace characters */
      factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
      factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
      factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);

      reader = factory.createXMLStreamReader(strReader);
      handler = new XHTMLCallbackHandler(cb);

      while (reader.hasNext())
      {
        int offset = reader.getLocation().getCharacterOffset();
        handler.setOffset(offset);
        eventType = reader.next();
        switch (eventType)
        {
          case XMLStreamConstants.START_ELEMENT:
            attributes.clear();
            for (int index = 0; index < reader.getAttributeCount(); index++)
            {
              attributes.addAttribute(reader.getAttributeNamespace(index),
                  reader.getAttributeLocalName(index),
                  getQName(reader.getAttributePrefix(index), reader.getAttributeLocalName(index)),
                  null, reader.getAttributeValue(index));
            }
            handler.startElement(reader.getNamespaceURI(), reader.getLocalName(),
                getQName(reader.getPrefix(), reader.getLocalName()), attributes);
            break;
          case XMLStreamConstants.END_ELEMENT:
            handler.endElement(reader.getNamespaceURI(), reader.getLocalName(),
                getQName(reader.getPrefix(), reader.getLocalName()));
            break;
          case XMLStreamConstants.PROCESSING_INSTRUCTION:
            handler.processingInstruction(reader.getPITarget(), reader.getPIData());
            break;
          case XMLStreamConstants.CHARACTERS:
            String characters = reader.getText();
            handler.characters(characters.toCharArray(), 0, characters.length());
            break;
          case XMLStreamConstants.COMMENT:
            String comment = reader.getText();
            handler.comment(comment.toCharArray(), 0, comment.length());
            break;
          case XMLStreamConstants.START_DOCUMENT:
            handler.startDocument();
            break;
          case XMLStreamConstants.END_DOCUMENT:
            handler.endDocument();
            break;
          case XMLStreamConstants.ENTITY_REFERENCE:
            //return "ENTITY_REFERENCE";
            break;
          case XMLStreamConstants.DTD:
            //                handler.startDTD(name, publicId, systemId)
            break;
          case XMLStreamConstants.CDATA:
            //          handler.startCDATA();
            //            String cdata = reader.getText();
            //            handler.characters(cdata.toCharArray(), 0, cdata.length());
            //            handler.endCDATA();
            break;
          case XMLStreamConstants.SPACE:
            break;
        }
      }
    } catch (XMLStreamException e)
    {
      throw new IllegalArgumentException(e);
    } catch (SAXException e)
    {
      throw new IllegalArgumentException(e);
    }

  }

  public void parse2(Reader r, ParserCallback cb, boolean ignoreCharSet) throws IOException
  {
    int c;
    StringWriter w = new StringWriter();
    StringReader strReader;
    while ((c = r.read()) != -1)
    {
      w.append((char) c);
    }
    String str = w.toString();
    impliedHeader = false;
    /* No valid header, maybe an XHTML fragment, add a fake root element */
    if (str.indexOf("<html") == -1)
    {
      str = "<html><head></head><body>" + str + "</body></html>";
      /** Indicate that the required elements were added manually */
      impliedHeader = true;
    }
    strReader = new StringReader(str);
    InputSource source = new InputSource(strReader);
    try
    {
      SAXParserFactory parserFactory = SAXParserFactory.newInstance();
      parserFactory.setNamespaceAware(true);
      parserFactory.setValidating(false);

      /* -------------------- Set validation --------------------* */
      /* Now validate against XMLSchema */
      /*      String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
            SchemaFactory factory = SchemaFactory.newInstance(language);
            factory.setResourceResolver(new ResourceResolver());

            // note that if your XML already declares the XSD to which it has to conform, then there's no need to create a validator from a Schema object
            InputStream is = XHTMLParser.class.getClassLoader()
                .getResourceAsStream("res/xhtml1-strict.xsd");
            Source schemaFile = new StreamSource(is);
            Schema schema = factory.newSchema(schemaFile);
            parserFactory.setSchema(schema);*/
      /*************************************************************/
      /* PASS 1 */
      SAXParser parser = parserFactory.newSAXParser();

      XHTMLCallbackHandler saxHandler = new XHTMLCallbackHandler(cb);

      /* Try to manage comments also */
      try
      {
        parser.setProperty("http://xml.org/sax/properties/lexical-handler", saxHandler);
      } catch (SAXNotRecognizedException e)
      {
        System.err.println(e.getMessage());
        return;
      } catch (SAXNotSupportedException e)
      {
        System.err.println(e.getMessage());
        return;
      }

      parser.parse(source, saxHandler);
      // end of line
      cb.handleEndOfLineString("\r\n");

      /*************************************************************/
      /* PASS 2 */
      //      parse2(new StringReader(w.toString()),cb,ignoreCharSet);

    } catch (SAXParseException e)
    {
      e.printStackTrace();
      throw new IllegalArgumentException(e.getMessage());
    } catch (SAXException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    } catch (ParserConfigurationException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Validates the XHTML Document against XHTML 1.0 Strict standard.
   * 
   * @param r
   *          The reader that contains the XHTML Document.
   * @throws ParserConfigurationException
   *           If some of the parser configuration is not supported when
   *           creating the document builder.
   * @throws SAXException
   *           All other exceptions are converted to this exception, indicating
   *           probably a parser or schema validation error.
   */
  public static void validateDocument(Reader r) throws SAXException
  {
    try
    {
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

      builderFactory.setNamespaceAware(true);
      builderFactory.setExpandEntityReferences(true);

      DocumentBuilder parser = builderFactory.newDocumentBuilder();
      parser.setEntityResolver(new LocalEntityResolver());

      // parse the XML into a document object
      InputSource fi = new InputSource(r);
      org.w3c.dom.Document document = parser.parse(fi);

      SchemaFactory factory = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      // associate the schema factory with the resource resolver, which is responsible for resolving the imported XSD's
      factory.setResourceResolver(new ResourceResolver());

      // note that if your XML already declares the XSD to which it has to conform, then there's no need to create a validator from a Schema object
      InputStream is = XHTMLParser.class.getClassLoader()
          .getResourceAsStream("res/xhtml1-strict.xsd");
      Source schemaFile = new StreamSource(is);
      Schema schema = factory.newSchema(schemaFile);

      Validator validator = schema.newValidator();
      validator.validate(new DOMSource(document));
    } catch (IOException e)
    {
      throw new SAXException(e);
    } catch (ParserConfigurationException e)
    {
      throw new SAXException(e);
    }
  }

}

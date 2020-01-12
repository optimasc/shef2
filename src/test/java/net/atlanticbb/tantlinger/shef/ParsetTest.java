package net.atlanticbb.tantlinger.shef;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.DocumentParser;
import javax.swing.text.html.parser.Element;
import javax.swing.text.html.parser.Parser;

import com.optimasc.text.html.XHTMLParser;


import junit.framework.TestCase;

public class ParsetTest extends TestCase
{
  
  public static final String HTML_FRAGMENT = "<p>Simple text</p>"; 
  public static final String HTML_TEXT = "<html><head></head><body><p>1<em>2</em></p></body></html>";
  
  public static final String HTML_FULL_TEXT = 
  "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"+
  "<html>  <head>      <title>XHTML Editor usage guide</title>\n"+
  "    </head>\n"+
  "       <body><!-- This we need to check further later !-->\n"+
  "       <h1>XHTML Editor usage guide</h1>\n"+
  "       <p>  paragraph    1</p>\n"+
  "       </body>\n"+
  "</html>\n";  
  
  /** JAVA SYSTEM PARSER DOES NOT SEEM TO LINE META TAG OR ATTRIBUTES */
/*   "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"+
  "<html>  <head>      <title>XHTML Editor usage guide</title>\n"+
  "<meta name=\"author\" content='The Black One'>   </meta>\n"+
  "    </head>\n"+
  "       <body><!-- This we need to check further later !-->\n"+
  "       <h1>XHTML Editor usage guide</h1>\n"+
  "       <p>  paragraph    1</p>\n"+
  "       </body>\n"+
  "</html>\n";  
*/
  
  public static final String NOHTML_FRAGMENT = "Simple text&nbsp;"; 
  
  
  /** Used to contain information on content */
  public static class TagRecord
  {
    public HTML.Tag tag;
    public int position;
    public String content;
    
    public TagRecord(HTML.Tag t, int pos)
    {
      this.tag = t;
      this.position = pos;
      this.content = null;
    }
    
    public TagRecord(HTML.Tag t, int pos, String s)
    {
      this.tag = t;
      this.position = pos;
      this.content = s;
    }
    
  }
  
  /** Base class for testing */
  public static abstract class ParserCallbackTest extends HTMLEditorKit.ParserCallback
  {
    Queue<TagRecord> tags = new LinkedList<TagRecord>();
    
    public ParserCallbackTest()
    {
      super();
    }
    
    
    @Override
    public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos)
    {
      TagRecord r = tags.remove();
      assertEquals(r.tag, t);
      r = tags.remove();
      assertEquals(r.tag, t);
      super.handleSimpleTag(t, a, pos);
    }

    @Override
    public void handleText(char[] data, int pos)
    {
      TagRecord r = tags.remove();
      assertEquals(r.tag, HTML.Tag.CONTENT);
      String str = new String(data);
      if (r.content!=null)
      {
        assertEquals(r.content,str);
      }
    }

    @Override
    public void handleComment(char[] data, int pos)
    {
      TagRecord r = tags.remove();
      assertEquals(r.tag, HTML.Tag.COMMENT);
    }

    @Override
    public void handleStartTag(Tag t, MutableAttributeSet a, int pos)
    {
      TagRecord r = tags.remove();
      assertEquals(r.tag, t);
    }

    @Override
    public void handleEndTag(Tag t, int pos)
    {
      TagRecord r = tags.remove();
      assertEquals(r.tag, t);
    }

    @Override
    public void handleError(String errorMsg, int pos)
    {
        /* The callback parser used ignores errors,
         * as it is used for parsing html fragments. 
         */
//      fail(errorMsg);
    }

    @Override
    public void handleEndOfLineString(String eol)
    {
      // TODO Auto-generated method stub
      super.handleEndOfLineString(eol);
    }
    
  }  
  
  public static class ParserCallbackTextTest extends ParserCallbackTest 
  {
    
    public ParserCallbackTextTest()
    {
      super();
      /** Pushes all values that will be received. */
      tags.add(new TagRecord(HTML.Tag.HTML,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"1"));
      tags.add(new TagRecord(HTML.Tag.EM,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"2"));
      tags.add(new TagRecord(HTML.Tag.EM,0));
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.HTML,0));
    }
  }
  
  
  public static class ParserCallbackFullTextTest extends ParserCallbackTest
  {
    
    public ParserCallbackFullTextTest()
    {
      super();
      /** Pushes all values that will be received. */
      tags.add(new TagRecord(HTML.Tag.HTML,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      tags.add(new TagRecord(HTML.Tag.TITLE,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"XHTML Editor usage guide"));
      tags.add(new TagRecord(HTML.Tag.TITLE,0));
/*      tags.add(new TagRecord(HTML.Tag.META,0));
      tags.add(new TagRecord(HTML.Tag.META,0));*/
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      /* No end tag for this one */
      tags.add(new TagRecord(HTML.Tag.COMMENT,0));
      tags.add(new TagRecord(HTML.Tag.H1,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"XHTML Editor usage guide"));
      tags.add(new TagRecord(HTML.Tag.H1,0));
      
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"paragraph 1"));
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.HTML,0));
    }
  }
  
  
  public static class ParserCallbackFragmentTest extends ParserCallbackTest
  {
    
    public ParserCallbackFragmentTest()
    {
      super();
      /** Pushes all values that will be received. */
      tags.add(new TagRecord(HTML.Tag.HTML,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0));
      tags.add(new TagRecord(HTML.Tag.P,0));
      
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.HTML,0));
    }
  }
  
  public static class ParserCallbackNoHTMLFragmentTest extends ParserCallbackTest
  {
    public ParserCallbackNoHTMLFragmentTest()
    {
      super();
      /** Pushes all values that will be received. */
      tags.add(new TagRecord(HTML.Tag.HTML,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0));
      
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.HTML,0));
    }
  }
  
  
  


  protected void setUp() throws Exception
  {
    super.setUp();
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /*******************************************************************************************/
  public void testSystemFragment()
  {
    StringReader str = new StringReader(HTML_FRAGMENT);
    
    
    InputStream in = null;
    DTD dtd= null;
    boolean debug = true;
    try {
        dtd = DTD.getDTD("html32");
        String name = "html32";
        String path = name + ".bdtd";
        in = ClassLoader.getSystemResourceAsStream("javax/swing/text/html/parser/"+path);
            if (in != null) {
                dtd.read(new DataInputStream(new BufferedInputStream(in)));
            }
        } catch (Exception e) {
            fail();
        }
    
    
    DocumentParser parser = new DocumentParser(dtd);
    ParserCallback callback = new ParserCallbackFragmentTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
  }
  
  public void testXHTMLFragment()
  {
    StringReader str = new StringReader(HTML_FRAGMENT);
    XHTMLParser parser = new XHTMLParser();
    ParserCallback callback = new ParserCallbackFragmentTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
    
  }
  
  
  /*******************************************************************************************/
  
  public void testSystemText()
  {
    StringReader str = new StringReader(HTML_TEXT);
    
    
    InputStream in = null;
    DTD dtd= null;
    boolean debug = true;
    try {
        dtd = DTD.getDTD("html32");
        String name = "html32";
        String path = name + ".bdtd";
        in = ClassLoader.getSystemResourceAsStream("javax/swing/text/html/parser/"+path);
            if (in != null) {
                dtd.read(new DataInputStream(new BufferedInputStream(in)));
            }
        } catch (Exception e) {
            fail();
        }
    
    
    DocumentParser parser = new DocumentParser(dtd);
    ParserCallback callback = new ParserCallbackTextTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
  }
  
  public void testXHTMLText()
  {
    StringReader str = new StringReader(HTML_TEXT);
    XHTMLParser parser = new XHTMLParser();
    ParserCallback callback = new ParserCallbackTextTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
    
  }
  
  /*******************************************************************************************/
  
  
  public void testSystemFullText()
  {
    StringReader str = new StringReader(HTML_FULL_TEXT);
    
    
    InputStream in = null;
    DTD dtd= null;
    boolean debug = true;
    try {
        dtd = DTD.getDTD("html32");
        String name = "html32";
        String path = name + ".bdtd";
        in = ClassLoader.getSystemResourceAsStream("javax/swing/text/html/parser/"+path);
            if (in != null) {
                dtd.read(new DataInputStream(new BufferedInputStream(in)));
            }
        } catch (Exception e) {
            fail();
        }
    
    
    DocumentParser parser = new DocumentParser(dtd);
    ParserCallback callback = new ParserCallbackFullTextTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
  }
  
  public void testXHTMLFullText()
  {
    StringReader str = new StringReader(HTML_FULL_TEXT);
    XHTMLParser parser = new XHTMLParser();
    ParserCallback callback = new ParserCallbackFullTextTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
    
  }
  /*******************************************************************************************/
  public void testSystemNoHTMLText()
  {
    StringReader str = new StringReader(NOHTML_FRAGMENT);
    
    
    InputStream in = null;
    DTD dtd= null;
    boolean debug = true;
    try {
        dtd = DTD.getDTD("html32");
        String name = "html32";
        String path = name + ".bdtd";
        in = ClassLoader.getSystemResourceAsStream("javax/swing/text/html/parser/"+path);
            if (in != null) {
                dtd.read(new DataInputStream(new BufferedInputStream(in)));
            }
        } catch (Exception e) {
            fail();
        }
    
    
    DocumentParser parser = new DocumentParser(dtd);
    ParserCallback callback = new ParserCallbackNoHTMLFragmentTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
  }
  
  public void testXHTMLNoHTMLText()
  {
    StringReader str = new StringReader(NOHTML_FRAGMENT);
    XHTMLParser parser = new XHTMLParser();
    ParserCallback callback = new ParserCallbackNoHTMLFragmentTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
    
  }
  
  
  /**********************************************************************************************/
  /*                               Whitespace parsing testing                                   */
  /**********************************************************************************************/
  public static final String HTML_NORMALIZED = "<p>This is <code>some code </code><br></br>example.</p><pre>This is  aaa</pre><p> Another   paragraph.  </p>";
  
  public static class ParserCallbackWhitespaceTest extends ParserCallbackTest 
  {
    
    public ParserCallbackWhitespaceTest()
    {
      super();
      /** Pushes all values that will be received. */
      tags.add(new TagRecord(HTML.Tag.HTML,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      tags.add(new TagRecord(HTML.Tag.HEAD,0));
      
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"This is "));
      tags.add(new TagRecord(HTML.Tag.CODE,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"some code "));
      tags.add(new TagRecord(HTML.Tag.CODE,0));
      tags.add(new TagRecord(HTML.Tag.BR,0));
      tags.add(new TagRecord(HTML.Tag.BR,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"example."));
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.PRE,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"This is  aaa"));
      tags.add(new TagRecord(HTML.Tag.PRE,0));
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.CONTENT,0,"Another paragraph. "));
      tags.add(new TagRecord(HTML.Tag.P,0));
      tags.add(new TagRecord(HTML.Tag.BODY,0));
      tags.add(new TagRecord(HTML.Tag.HTML,0));
    }
  }
  
  
  /* Tests that whitespace is properly normalized. */
  public void testXHTMLWhiteSpace()
  {
    StringReader str = new StringReader(HTML_NORMALIZED);
    XHTMLParser parser = new XHTMLParser();
    ParserCallback callback = new ParserCallbackWhitespaceTest();
    try{
      parser.parse(str, callback, true);
    } catch (Exception e)
    {
      fail(e.getLocalizedMessage());
    }
    
  }

}

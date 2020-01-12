package net.atlanticbb.tantlinger.shef;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.WysiwygHTMLEditorKit;
import net.atlanticbb.tantlinger.ui.text.actions.EnterKeyAction;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.optimasc.text.html.HTMLHelper;
import com.optimasc.text.html.HTMLHelper.Tag;

public class ElementTest extends TestCase
{

  private static org.w3c.dom.Document convertStringToXMLDocument(String xmlString)
  {
    //Parser that produces DOM object trees from XML content
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    //API to obtain DOM Document instance
    DocumentBuilder builder = null;
    try
    {
      //Create DocumentBuilder with default configuration
      builder = factory.newDocumentBuilder();

      //Parse the content to Document object
      org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
      return doc;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public static final String SINGLE_PARAGRAPH = "<html xmlns='http://www.w3.org/1999/xhtml'><head><title></title>"
      +
      "</head><body><p>Jim Rock<a href='mailto:jim@rock.com'>jim@rock.com</a><br></br><a href='tel:+13115552368'>(311) 555-2368</a>"
      +
      "</p><p></p></body></html>";

  public static final String DOUBLE_PARAGRAPH = "<html xmlns='http://www.w3.org/1999/xhtml'><head><title></title>"
      +
      "</head><body><p>Jim Rock<a href='mailto:jim@rock.com'>jim@rock.com</a><br></br><a href='tel:+13115552368'>(311) 555-2368</a>"
      +
      "</p><p>This is the 2nd paragraph.</p></body></html>";

  public static final String SINGLE_ADDRESS = "<html xmlns='http://www.w3.org/1999/xhtml'><head><title></title>"
      +
      "</head><body><address>Jim Rock<a href='mailto:jim@rock.com'>jim@rock.com</a><br></br><a href='tel:+13115552368'>(311) 555-2368</a>"
      +
      "</address><p></p></body></html>";
  
  
  public static final String BLOCK_SPECIAL = "<html xmlns='http://www.w3.org/1999/xhtml'><head><title></title>"
      +
      "</head><body><address>Jim Rock</address>"
      +
      "<p>Paragraph</p>"
      +
      "<pre>pre-formatted</pre>"
      +
      "<div><p>A divided paragraph</p><p>Another divided paragraph</p></div>"
      +
      "<blockquote><div>Block-quoted</div></blockquote>"
      +
      "</body></html>";
  

  protected static final HTML.Tag blockTags[] =
  {
      HTMLHelper.ADDRESS_X,
      HTML.Tag.DIV,
      HTML.Tag.H1,
      HTML.Tag.H2,
      HTML.Tag.H3,
      HTML.Tag.H4,
      HTML.Tag.H5,
      HTML.Tag.H6,
      HTML.Tag.P,
      HTML.Tag.PRE
  };

  protected static final HTML.Tag blockTagList[] =
  {
      HTML.Tag.OL,
      HTML.Tag.UL
  };

  protected void setUp() throws Exception
  {
    super.setUp();
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  public void testReplaceBlockOnce()
  {
    for (int i = 0; i < blockTags.length; i++)
    {
      HTML.Tag newTag = blockTags[i];
      Writer writer = new StringWriter();
      HTMLEditorKit editorKit = new WysiwygHTMLEditorKit();
      HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();
      try
      {
        editorKit.read(new StringReader(SINGLE_PARAGRAPH), doc, 0);

        HTMLUtils.changeBlockType(doc, editorKit, newTag, 3, doc.getLength());
        editorKit.write(writer, doc, 0, doc.getLength());

        /* Verify if the values are valid through DOM */
        org.w3c.dom.Document domDocument = convertStringToXMLDocument(writer.toString());

        /* If the current tag to replace with is a <P> element,
         * then we expect 2 <P> elements at end instead of
         * one because we started with a <P> element.
         */
        if (newTag.equals(HTML.Tag.P) == false)
        {
          /* Verify that the values are valid - summary testing */
          NodeList nodeList = domDocument.getElementsByTagName(newTag.toString());
          assertEquals(1, nodeList.getLength());
          assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
          nodeList = domDocument.getElementsByTagName("p");
          assertEquals(1, nodeList.getLength());
          assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
        } else
        {
          /* Verify that the values are valid - summary testing */
          NodeList nodeList = domDocument.getElementsByTagName(newTag.toString());
          assertEquals(2, nodeList.getLength());
          assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
          assertEquals("body", nodeList.item(1).getParentNode().getNodeName());
        }
        HTMLUtils.printHTML(doc);
      } catch (IOException e)
      {
        fail();
      } catch (BadLocationException e)
      {
        fail();
      }
    }
  }

  public void testReplaceBlockAddressTwice()
  {
    for (int i = 0; i < blockTags.length; i++)
    {
      HTML.Tag newTag = blockTags[i];
      Writer writer = new StringWriter();
      HTMLEditorKit editorKit = new WysiwygHTMLEditorKit();
      HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();
      try
      {
        editorKit.read(new StringReader(DOUBLE_PARAGRAPH), doc, 0);

        HTMLUtils.changeBlockType(doc, editorKit, newTag, 3, doc.getLength());
        editorKit.write(writer, doc, 0, doc.getLength());

        // Verify if the values are valid through DOM
        org.w3c.dom.Document domDocument = convertStringToXMLDocument(writer.toString());

        /* If the current tag to replace with is a <P> element,
         * then we expect 3 <P> elements at end instead of
         * one because we started with  <P> elements.
         */
        if (newTag.equals(HTML.Tag.P) == false)
        {
          // Verify that the values are valid - summary testing
          NodeList nodeList = domDocument.getElementsByTagName(newTag.toString());
          assertEquals(2, nodeList.getLength());
          assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
          assertEquals("body", nodeList.item(1).getParentNode().getNodeName());
          assertEquals("This is the 2nd paragraph.", nodeList.item(1).getChildNodes().item(0)
              .getNodeValue().trim());

          // This is automatically added by the editor
          nodeList = domDocument.getElementsByTagName("p");
          assertEquals(1, nodeList.getLength());
          assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
        } else
        {
          // Verify that the values are valid - summary testing
          NodeList nodeList = domDocument.getElementsByTagName(newTag.toString());
          assertEquals(3, nodeList.getLength());
          assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
          assertEquals("body", nodeList.item(1).getParentNode().getNodeName());
          assertEquals("This is the 2nd paragraph.", nodeList.item(1).getChildNodes().item(0)
              .getNodeValue().trim());
          assertEquals("body", nodeList.item(2).getParentNode().getNodeName());
        }
      } catch (IOException e)
      {
        fail();
      } catch (BadLocationException e)
      {
        fail();
      }
    }
  }

  public void testReplaceBlockAddressNoChange()
  {
    Writer writer = new StringWriter();
    HTMLEditorKit editorKit = new WysiwygHTMLEditorKit();
    HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();
    try
    {
      editorKit.read(new StringReader(SINGLE_ADDRESS), doc, 0);

      HTMLUtils.changeBlockType(doc, editorKit, HTMLHelper.ADDRESS_X, 3, doc.getLength());
      editorKit.write(writer, doc, 0, doc.getLength());

      // Verify if the values are valid through DOM
      org.w3c.dom.Document domDocument = convertStringToXMLDocument(writer.toString());

      // Verify that the values are valid - summary testing
      NodeList nodeList = domDocument.getElementsByTagName("address");
      assertEquals(1, nodeList.getLength());
      assertEquals("body", nodeList.item(0).getParentNode().getNodeName());

      // This is automatically added by the editor
      nodeList = domDocument.getElementsByTagName("p");
      assertEquals(1, nodeList.getLength());
      assertEquals("body", nodeList.item(0).getParentNode().getNodeName());

    } catch (IOException e)
    {
      fail();
    } catch (BadLocationException e)
    {
      fail();
    }
  }

  public void testReplaceBlockOnceByList()
  {
    for (int i = 0; i < blockTagList.length; i++)
    {
      HTML.Tag newTag = blockTagList[i];
      Writer writer = new StringWriter();
      HTMLEditorKit editorKit = new WysiwygHTMLEditorKit();
      HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();
      try
      {
        editorKit.read(new StringReader(SINGLE_PARAGRAPH), doc, 0);

        HTMLUtils.changeBlockType(doc, editorKit, newTag, 3, doc.getLength());
        editorKit.write(writer, doc, 0, doc.getLength());

        /* Verify if the values are valid through DOM */
        org.w3c.dom.Document domDocument = convertStringToXMLDocument(writer.toString());

        /* Verify that the values are valid - summary testing */
        NodeList nodeList = domDocument.getElementsByTagName("li");
        assertEquals(1, nodeList.getLength());
        assertEquals(newTag.toString(), nodeList.item(0).getParentNode().getNodeName());
        nodeList = domDocument.getElementsByTagName("p");
        assertEquals(1, nodeList.getLength());
        assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
        HTMLUtils.printHTML(doc);
      } catch (IOException e)
      {
        fail();
      } catch (BadLocationException e)
      {
        fail();
      }
    }
  }

  public void testReplaceBlockTwiceByList()
  {
    for (int i = 0; i < blockTagList.length; i++)
    {
      HTML.Tag newTag = blockTagList[i];
      Writer writer = new StringWriter();
      HTMLEditorKit editorKit = new WysiwygHTMLEditorKit();
      HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();
      try
      {
        editorKit.read(new StringReader(DOUBLE_PARAGRAPH), doc, 0);

        HTMLUtils.changeBlockType(doc, editorKit, newTag, 3, doc.getLength());
        editorKit.write(writer, doc, 0, doc.getLength());

        /* Verify if the values are valid through DOM */
        org.w3c.dom.Document domDocument = convertStringToXMLDocument(writer.toString());

        /* Verify that the values are valid - summary testing */
        NodeList nodeList = domDocument.getElementsByTagName("li");
        assertEquals(2, nodeList.getLength());
        assertEquals(newTag.toString(), nodeList.item(0).getParentNode().getNodeName());
        assertEquals(newTag.toString(), nodeList.item(1).getParentNode().getNodeName());
        nodeList = domDocument.getElementsByTagName("p");
        assertEquals(1, nodeList.getLength());
        assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
        HTMLUtils.printHTML(doc);
      } catch (IOException e)
      {
        fail();
      } catch (BadLocationException e)
      {
        fail();
      }
    }
  }
  
  
  public void testReplaceBlockSpecial()
  {
      HTML.Tag newTag = HTML.Tag.P;
      Writer writer = new StringWriter();
      HTMLEditorKit editorKit = new WysiwygHTMLEditorKit();
      HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();
      try
      {
        editorKit.read(new StringReader(BLOCK_SPECIAL), doc, 0);

        HTMLUtils.changeBlockType(doc, editorKit, newTag, 3, doc.getLength());
        editorKit.write(writer, doc, 0, doc.getLength());

        /* Verify if the values are valid through DOM */
        org.w3c.dom.Document domDocument = convertStringToXMLDocument(writer.toString());

        /* Verify that the values are valid - summary testing */
        NodeList nodeList = domDocument.getElementsByTagName("p");
        assertEquals(6, nodeList.getLength());
        assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(1).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(2).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(3).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(4).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(5).getParentNode().getNodeName());
        
        HTMLUtils.printHTML(doc);
      } catch (IOException e)
      {
        fail();
      } catch (BadLocationException e)
      {
        fail();
      }
  }
  
  
  public void testInsertBlockSpecial()
  {
      HTML.Tag newTag = HTML.Tag.P;
      Writer writer = new StringWriter();
      HTMLEditorKit editorKit = new WysiwygHTMLEditorKit();
      HTMLDocument doc = (HTMLDocument) editorKit.createDefaultDocument();
      try
      {
        editorKit.read(new StringReader(BLOCK_SPECIAL), doc, 0);

        Element elem = doc.getParagraphElement(3);
        EnterKeyAction.insertParagraphAfter(doc, editorKit, elem, 3);
        editorKit.write(writer, doc, 0, doc.getLength());

        /* Verify if the values are valid through DOM */
        org.w3c.dom.Document domDocument = convertStringToXMLDocument(writer.toString());

        /* Verify that the values are valid - summary testing */
     /*   NodeList nodeList = domDocument.getElementsByTagName("p");
        assertEquals(6, nodeList.getLength());
        assertEquals("body", nodeList.item(0).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(1).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(2).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(3).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(4).getParentNode().getNodeName());
        assertEquals("body", nodeList.item(5).getParentNode().getNodeName());*/
        
        HTMLUtils.printHTML(doc);
      } catch (IOException e)
      {
        fail();
      } catch (BadLocationException e)
      {
        fail();
      }
  }
  
  

}

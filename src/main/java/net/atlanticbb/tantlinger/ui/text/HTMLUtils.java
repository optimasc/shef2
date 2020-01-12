/*
 * Created on Jun 16, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AbstractDocument.LeafElement;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.optimasc.text.AttributeHelper;
import com.optimasc.text.html.HTMLHelper;
import com.optimasc.text.html.XHTMLWriter;


/**
 * A collection of static convenience methods for working with HTML,
 * HTMLDocuments, AttributeSets and Elements from HTML documents.
 * 
 * @author Bob Tantlinger
 *
 */
public class HTMLUtils
{
    
    private static final Tidy tidy = new Tidy();
    public static final AttributeHelper attributeHelper = new HTMLHelper();
    static
    {
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);
        tidy.setForceOutput(true);
        tidy.setFixComments(true);
        tidy.setFixUri(true);
        tidy.setDropEmptyParas(true);
        tidy.setLiteralAttribs(true);
        tidy.setTrimEmptyElements(true);
        tidy.setXHTML(true);
        //tidy.setInputEncoding("UTF-16");
        //tidy.setOutputEncoding("UTF-16");
    }
    
            
    /**
     * Tests if an element is an implied paragraph (p-implied)
     * 
     * @param el The element
     * @return true if the elements name equals "p-implied", false otherwise
     */
    public static boolean isImplied(Element el)
    {
        return el.getName().equals("p-implied");
    }
    
    
    /** Changes the selection to the specified block type. All block elements
     *  with selected positions will be replaced by <code>newBlockType</code>
     *  except if the old element was <code>blockquote</code> in that
     *  case the <code>blockquote</code> element is simply removed. 
     *  
     *  This procedure does not properly manage the <code>table</code> or 
     *  <code>dl</code> elements.
     * 
     * @param newBlockType
     *          The new block type
     * @param startPos
     *          The start position that requires replacement.
     * @param endPos
     *          The end position that requires replacement.
     * @return The position of the last element that was selected.         
     * @throws BadLocationException
     */
    public static int changeBlockType(HTMLDocument doc, HTMLEditorKit editorKit, HTML.Tag newBlockType,
        int startPos, int endPos) throws BadLocationException
    {
      Element curE = doc.getParagraphElement(startPos);
      Element endE = doc.getParagraphElement(endPos);

      Element curTD = HTMLUtils.getParent(curE, HTML.Tag.TD);
      HTML.Tag tag = newBlockType;
      String html = ""; //$NON-NLS-1$

      ////////////////////////////////
      // Special cases
      ////////////////////////////////
      if ( 
           newBlockType.equals(HTML.Tag.UL) ||
           newBlockType.equals(HTML.Tag.OL)
         )  
      {
         html = "<" + newBlockType.toString() + ">";
         tag = HTML.Tag.LI;
      }
      
      if (newBlockType.equals(HTML.Tag.BLOCKQUOTE))
      {
        html = "<" + newBlockType.toString() + ">";
        tag = HTML.Tag.P;
      }

      //a list to hold the elements we want to change
      List<Element> elToRemove = new ArrayList<Element>();
      elToRemove.add(curE);

//      doc.dump(System.out);
      while (true)
      {
        // In the case of blockquote element, we simply remove it
        // and do not replace it by anything else.
        
        /* Always remove the following attributes :
         *  
         * * CSS.Attribute.WHITE_SPACE which is used
         *   internally in p_implied paragraphs
         * * xml:space should be added to pre element only
         *   and removed from others.
         */
        MutableAttributeSet attrs = new SimpleAttributeSet(curE.getAttributes().copyAttributes());
        attrs.removeAttribute(CSS.Attribute.WHITE_SPACE);        
        if (tag.equals(HTML.Tag.PRE))
        {
          attrs.addAttribute("xml:space","preserve");        
        } else
        {
          attrs.removeAttribute("xml:space");        
        }
        
        html += HTMLUtils.createTag(tag,
             attrs, HTMLUtils.getElementHTML(curE, false));
        if (curE.getEndOffset() >= endE.getEndOffset()
            || curE.getEndOffset() >= doc.getLength())
          break;
        curE = doc.getParagraphElement(curE.getEndOffset() + 1);
        elToRemove.add(curE);
        
        // Embedded block types

        //did we enter a (different) table cell?
        Element ckTD = HTMLUtils.getParent(curE, HTML.Tag.TD);
        if (ckTD != null && !ckTD.equals(curTD))
          break;//stop here so we don't mess up the table

        
        Element ckQuote = HTMLUtils.getParent(curE, HTML.Tag.BLOCKQUOTE);
        if (ckQuote!=null)
        {
          elToRemove.add(ckQuote);
        }
      }
      
      ////////////////////////////////
      // Special cases
      ////////////////////////////////
      if ( 
          newBlockType.equals(HTML.Tag.UL) ||
          newBlockType.equals(HTML.Tag.OL)
        )  
     {
        html += "</" + newBlockType.toString() + ">";
     }
      
      if (newBlockType.equals(HTML.Tag.BLOCKQUOTE))
      {
        html += "</" + newBlockType.toString() + ">";
      }
      
      
      //set the caret to the start of the last selected block element
      int offset = curE.getStartOffset();
      /*    editor.setCaretPosition(curE.getStartOffset());*/


      HTMLUtils.insertHTML(doc, editorKit, html, newBlockType, startPos);
      
      //now, remove the elements that were changed.
      for (Iterator<Element> it = elToRemove.iterator(); it.hasNext();)
      {
        Element c = (Element) it.next();
        HTMLUtils.removeElement(c);
      }
      
      return offset;
    }
    
    
    /**
     * Incloses a chunk of HTML text in the specified tag
     * @param enclTag the tag to enclose the HTML in
     * @param innerHTML the HTML to be inclosed
     * @return
     */
    public static String createTag(HTML.Tag enclTag, String innerHTML)
    {
        return createTag(enclTag, new SimpleAttributeSet(), innerHTML);
    }
    
    
    /**
     * Returns the URL of the image data.
     * 
     * @return null if address does not point to supported or valid data,
     *         otherwise an image URL.
     * 
     */
    public static URL getImageURL(AttributeSet attr, HTMLDocument doc)
    {
      URL result;
      String src;
      Object o = attr.getAttribute(HTML.Attribute.SRC);
      if (o == null)
      {
        return null;
      }
      src = o.toString();

      /* Get reference relative to base if necessary for relative paths. */
      URL reference = doc.getBase();
      try
      {
        result = new URL(reference, src);
      } catch (MalformedURLException e)
      {
        return null;
      }
      return result;
    }
    
    /** Tries to actually verify if the image URL exists and
     *  returns the MIME type if it exists, otherwise returns 
     *  null;
     * 
     * @param url
     * @return
     */
    public static String getImageMIME(URL url)
    {
      String mimeType;
      /* Check if this stream actually exists. If not, return null */
      try
      {
        URLConnection connection = url.openConnection();
        if (connection instanceof HttpURLConnection)
        {
          ((HttpURLConnection)connection).setRequestMethod("HEAD");
          connection.connect();
          if (((HttpURLConnection)connection).getResponseCode() != HttpURLConnection.HTTP_OK)
          {
            return null;
          }
          mimeType = connection.getContentType();      
        } else
        {
          mimeType = connection.getContentType();      
          InputStream is = url.openStream();
          is.close();
        }
      } catch (IOException e)
      {
        return null;
      }
      return mimeType;      
    }
    
    
    /**
     * Computes the (inline or block) element at the focused editor's caret position
     * @return the element, or null of the element cannot be retrieved
     */
    public static Element elementAtCaretPosition(JEditorPane ed)
    {                        
        if(ed == null)
            return null;
        
        HTMLDocument doc = (HTMLDocument)ed.getDocument();
        int caret = ed.getCaretPosition();
        
        Element elem = doc.getParagraphElement(caret);
        HTMLDocument.BlockElement blockElem = (HTMLDocument.BlockElement)elem;
        
        /** SPECIAL CASES FOR OBJECTS / BR / IMG Inline elements, 
         *  if they are alone, they should be the one selected, not
         *  the outer block element!
         */
        if (elem.getElementCount()==2)
        {
          Element childElement = elem.getElement(0);
          Object o = childElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
          if (o.equals(HTML.Tag.BR) || o.equals(HTML.Tag.OBJECT) || o.equals(HTML.Tag.IMG))
          {
            /* Check if the other element is a content element, which contains only whitespace
             * this is automatically added by the document generator. */
            Element contentElement = elem.getElement(1);
            o = contentElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (contentElement.isLeaf())
            {
              int length = contentElement.getEndOffset() - contentElement.getStartOffset();
              String s;
              try
              {
                s = doc.getText(contentElement.getStartOffset(), length);
                if (s.trim().length() == 0)
                {
                  return childElement;
                }
              } catch (BadLocationException e)
              {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }
        }
        return blockElem.positionToElement(caret);            
    }
    
    
    /** Converts the value of a reader to a string. */
    public static String readerToString(Reader r)
    {
      int c;
      StringWriter w = new StringWriter();
      try
      {
        while ((c = r.read())!=-1)
        {
          w.write(c);
        }
      } catch (IOException e)
      {
        e.printStackTrace();
      }
      return w.toString();
    }
    
    /**
     * Incloses a chunk of HTML text in the specified tag
     * with the specified attribs
     * 
     * @param enclTag
     * @param set
     * @param innerHTML
     * @return
     */
    public static String createTag(HTML.Tag enclTag, AttributeSet set, String innerHTML)
    {
        String t = tagOpen(enclTag, set) + innerHTML + tagClose(enclTag);        
        return t;
    }
    
    private static String tagOpen(HTML.Tag enclTag, AttributeSet set)
    {
        String t = "<" + enclTag;
        for(Enumeration e = set.getAttributeNames(); e.hasMoreElements();)
        {
            Object name = e.nextElement();
            if(!name.toString().equals("name"))
            {               
                Object val = set.getAttribute(name);
                t += " " + name + "=\"" + val + "\"";
            }
        }
        
        return t + ">";
    }
    
    private static String tagClose(HTML.Tag t)
    {
        return "</" + t + ">";
    }
    
    public static List getParagraphElements(JEditorPane editor)
    {
        List elems = new LinkedList();
        try
        {
            HTMLDocument doc = (HTMLDocument)editor.getDocument();        
            Element curE = getParaElement(doc, editor.getSelectionStart());
            Element endE = getParaElement(doc, editor.getSelectionEnd());
            
            while(curE.getEndOffset() <= endE.getEndOffset())
            {               
                elems.add(curE);
                curE = getParaElement(doc, curE.getEndOffset() + 1);
                if(curE.getEndOffset() >= doc.getLength())
                    break;
            }
        }
        catch(ClassCastException cce){}
        
        return elems;
    }
    
    private static Element getParaElement(HTMLDocument doc, int pos)
    {
        Element curE = doc.getParagraphElement(pos);
        while(isImplied(curE))
        {
            curE = curE.getParentElement();
        }
        
        Element lp = getListParent(curE);
        if(lp != null)
            curE = lp;
        
        return curE;
    }
    
    /**
     * Searches upward for the specified parent for the element.
     * @param curElem
     * @param parentTag
     * @return The parent element, or null if the parent wasnt found
     */
    public static Element getParent(Element curElem, HTML.Tag parentTag)
    {
        Element parent = curElem;
        while(parent != null)
        {            
            if(parent.getName().equals(parentTag.toString()))
                return parent;
            parent = parent.getParentElement();
        }
        
        return null;
    }
    
    /**
     * Tests if the element is empty
     * @param el
     * @return
     */
    public static boolean isElementEmpty(Element el)
    {
        String s = getElementHTML(el, false).trim();        
        return s.length() == 0;
    }
    
    
    /** Dump the HTML to the console. */
    public static void printHTML(HTMLDocument doc)
    {
       StringWriter wr = new StringWriter();
       XHTMLWriter xhtml = new XHTMLWriter(wr,doc);
       try
      {
         xhtml.write();
         System.err.println(wr.toString());
      } catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (BadLocationException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
    }
    
    /**
     * Searches for a list {@link Element} that is the parent of the specified {@link Element}.
     * 
     * @param elem
     * @return A list element (UL, OL, DIR, MENU, or DL) if found, null otherwise
     */
    public static Element getListParent(Element elem)
    {
        Element parent = elem;
        while(parent != null)
        {
            if(parent.getName().toLowerCase().equals(HTML.Tag.UL.toString()) || 
                parent.getName().toLowerCase().equals(HTML.Tag.OL.toString()) ||
                parent.getName().equals("dl") || parent.getName().equals("menu") ||
                parent.getName().equals("dir"))
                return parent;
            parent = parent.getParentElement();
        }
        return null;
    }
    
    /** Returns the previous list element relative to this 
     *  list element.
     * 
     * @param elem The list element base that we need to start the search from.
     * @return The previous list element, or null if no previous
     *  list element exists.
     */
    public static Element getPreviousListElement(Element elem)
    {
      Element parent = getListParent(elem);
      Element currentElem = null;
      if (parent == null)
        return null;
      /* Enumerate all children, and find the one just before this one. */
      for (int i = 0; i < parent.getElementCount(); i++)
      {
        currentElem = parent.getElement(i);
        /* Found! */
        if (currentElem.getEndOffset()==elem.getStartOffset())
        {
          return currentElem;
        }
      }
      return null;
    }
    
    /**
     * Gets the element one position less than the start of the specified element
     * @param doc
     * @param el
     * @return
     */
    public static Element getPreviousElement(HTMLDocument doc, Element el)
    {
        if(el.getStartOffset() > 0)
            return doc.getParagraphElement(el.getStartOffset() - 1);
        return el;
    }
    
    /**
     * Gets the element one position greater than the end of the specified element
     * @param doc
     * @param el
     * @return
     */
    public static Element getNextElement(HTMLDocument doc, Element el)
    {
        if(el.getEndOffset() < doc.getLength())
            return doc.getParagraphElement(el.getEndOffset() + 1);
        return el;
    }
    
    /**
     * Removes the enclosing tags from a chunk of HTML text
     * @param elem
     * @param txt
     * @return
     */
    public static String removeEnclosingTags(Element elem, String txt)
    {
        HTML.Tag t = HTMLHelper.getTag(elem.getName());
        return removeEnclosingTags(t, txt);
    }

    /**
     * Removes the enclosing tags from a chunk of HTML text
     * @param t
     * @param txt
     * @return
     */
    public static String removeEnclosingTags(HTML.Tag t, String txt)
    {       
        String openStart = "<" + t;
        String closeTag = "</" + t + ">";
        
        txt = txt.trim();
        
        if(txt.startsWith(openStart))
        {
            int n = txt.indexOf(">");
            if(n != -1)
            {
                txt = txt.substring(n + 1, txt.length());                
            }
        }
        
        if(txt.endsWith(closeTag))
        {
            txt = txt.substring(0, txt.length() - closeTag.length());            
        }
        
        return txt;       
    }

    /**
     * Gets the html of the specified {@link Element}
     * 
     * @param el
     * @param includeEnclosingTags true, if the enclosing tags should be included
     * @return
     */
    public static String getElementHTML(Element el, boolean includeEnclosingTags)
    {
        String txt = "";

        try
        {
            StringWriter out = new StringWriter();
            ElementWriter w = new ElementWriter(out, el);
            w.write();
            txt = out.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if(includeEnclosingTags)
            return txt;
        return removeEnclosingTags(el, txt);
    }

    /**
     * Removes an element from the document that contains it
     * 
     * @param el
     * @throws BadLocationException
     */
    public static void removeElement(Element el) throws BadLocationException
    {
        HTMLDocument document = (HTMLDocument)el.getDocument();
        int start = el.getStartOffset();
        int len = el.getEndOffset() - start;
        
        Element tdEle = HTMLUtils.getParent(el, HTML.Tag.TD);        
        if(tdEle != null && el.getEndOffset() == tdEle.getEndOffset())
        {
            document.remove(start, len - 1);            
        }
        else
        {        
            if(el.getEndOffset() > document.getLength())            
                len = document.getLength() - start;            
            
            document.remove(start, len);            
        }           
    }
       
    
    public static HTML.Tag getStartTag(String text)
    {       
        String html = text.trim();
        int s = html.indexOf('<');
        if(s != 0)//doesn't start with a tag.
            return null;
        int e = html.indexOf('>');
        if(e == -1)
            return null; //not any kind of tag
        
        String tagName = html.substring(1, e).trim();
        if(tagName.indexOf(' ') != -1)
            tagName = tagName.split("\\s")[0];
        
        return HTMLHelper.getTag(tagName);
    }
    
    private static int depthFromRoot(Element curElem)
    {
        Element parent = curElem;
        int depth = 0;
        while(parent != null)
        {            
            if(parent.getName().equals("body") || /*parent.getName().equals("blockquote") ||*/ parent.getName().equals("td"))
                break;
            parent = parent.getParentElement();
            depth++;
        }
        
        return depth;
    }
    
    
   
    
    /**
     * Inserts an arbitrary chunk of HTML into the JEditorPane at the current
     * caret position.
     * 
     * @param rawHtml
     * @param editor
     */
    public static void insertArbitraryHTML(String rawHtml, JEditorPane editor)
    {
        tidy.setOutputEncoding("UTF-8");
        tidy.setInputEncoding("UTF-8");
        
        try
        {
            ByteArrayInputStream bin = new ByteArrayInputStream(rawHtml.getBytes("UTF-8"));       
            Document doc = tidy.parseDOM(bin, null);
            NodeList nodelist = doc.getElementsByTagName("body");
            
            if(nodelist != null)
            {
                Node body = nodelist.item(0);
                NodeList bodyChildren = body.getChildNodes();
                
                //for(int i = bodyChildren.getLength() - 1; i >= 0; i--)
                int len = bodyChildren.getLength();
                for(int i = 0; i < len; i++)
                {                
                    String ml = xmlToString(bodyChildren.item(i));
                    if(ml != null)
                    {
                        //System.out.println(ml);
                        HTML.Tag tag = getStartTag(ml);
                        if(tag == null)
                        {
                            tag = HTML.Tag.SPAN;
                            ml = "<span>" + ml + "</span>";
                        }
                        insertHTML((HTMLDocument)editor.getDocument(), 
                            (HTMLEditorKit)editor.getEditorKit(), ml, tag, editor.getCaretPosition());    
                    }
                }                               
            }
        }
        catch (UnsupportedEncodingException e)
        {            
            e.printStackTrace();
        }        
    }
            
    private static String xmlToString(Node node)
    {
        try
        {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");            
            transformer.transform(source, result);
            
            return stringWriter.getBuffer().toString();
        }
        catch(TransformerConfigurationException e)
        {
            e.printStackTrace();
        }
        catch(TransformerException e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    

    /**
     * Inserts a string of html into the {@link HTMLDocument}
     * at the specified position.
     * 
     * @param document The document to modify.
     * @param editorKit The editor kit associated with this document.
     * @param html The html to insert.
     * @param tag The first element tag of the html string. 
     * @param position Insert position (caret)
     */
    public static void insertHTML(HTMLDocument document, HTMLEditorKit editorKit, String html, HTML.Tag tag, int position)
    {
        int caret = position;        
        Element pElem = document.getParagraphElement(caret);

        boolean breakParagraph = tag.breaksFlow() || tag.isBlock();
        boolean beginParagraph = caret == pElem.getStartOffset();
        html = jEditorPaneizeHTML(html);

        //System.out.println(html);
        
        try
        {
            if(breakParagraph && beginParagraph)
            {
                //System.out.println("breakParagraph && beginParagraph");
                document.insertBeforeStart(pElem, "<p></p>");
                Element nextEl = document.getParagraphElement(caret + 1);
                editorKit.insertHTML(document, caret + 1, html, depthFromRoot(nextEl)/*1*/, 0, tag);
                document.remove(caret, 1);                
            }
            else if(breakParagraph && !beginParagraph)
            {
                //System.out.println("breakParagraph && !beginParagraph");
                editorKit.insertHTML(document, caret, html, depthFromRoot(pElem)/*1*/, 0, tag);
            }
            else if(!breakParagraph && beginParagraph)
            {
                //System.out.println("!breakParagraph && beginParagraph");
                
                 /* Trick: insert a non-breaking space after start, so that we're inserting into the middle of a line.
                 * Then, remove the space. This works around a bug when using insertHTML near the beginning of a
                 * paragraph.*/                 
                document.insertAfterStart(pElem, "&nbsp;");
                editorKit.insertHTML(document, caret + 1, html, 0, 0, tag);
                document.remove(caret, 1);
            }
            else if(!breakParagraph && !beginParagraph)
            {
                //System.out.println("!breakParagraph && !beginParagraph");
                editorKit.insertHTML(document, caret, html, 0, 0, tag);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }        
    }
    
    /**
     * Gets the character attributes at the {@link JEditorPane}'s caret position
     * <p>
     * If there is no selection, the character attributes at caretPos - 1 are retuned.
     * If there is a slection, the attributes at selectionEnd - 1 are returned
     * </p>
     * 
     * @param editor
     * @return An {@link AttributeSet} or null, if the editor doesn't have a {@link StyledDocument}
     */
    public static AttributeSet getCharacterAttributes(JEditorPane editor)
    {
        int p;
        if(editor.getSelectedText() != null)
        {
            p = editor.getSelectionEnd() - 1;
        }
        else
        {
            p = (editor.getCaretPosition() > 0) ? (editor.getCaretPosition() - 1) : 0;
        }
        
        try
        {
            StyledDocument doc = (StyledDocument)editor.getDocument();
            Element e = doc.getCharacterElement(p);
            return (e.getAttributes());
        }
        catch(ClassCastException cce){}
        
        return null;
    }
    
    
    
    /**
     * Removes a CSS character attribute that has the specified value
     * from the {@link JEditorPane}'s current caret position
     * or selection. 
     * <p>
     * The val parameter is a {@link String} even though the actual attribute value is not.
     * This is because the actual attribute values are not public. Thus, this method checks
     * the value via the toString() method</p>
     * 
     * @param editor
     * @param atr
     * @param val
     */
    public static void removeCharacterAttribute(JEditorPane editor, CSS.Attribute atr, String val)
    {
        HTMLDocument doc;
        MutableAttributeSet attr;
        
        doc = (HTMLDocument)editor.getDocument();           
        attr = ((HTMLEditorKit)editor.getEditorKit()).getInputAttributes();
        
        List tokens = tokenizeCharAttribs(doc, editor.getSelectionStart(), editor.getSelectionEnd());
        for(Iterator it = tokens.iterator(); it.hasNext();)
        {
            CharStyleToken t = (CharStyleToken)it.next();            
            if(t.attrs.isDefined(atr) && t.attrs.getAttribute(atr).toString().equals(val))
            {                                        
                SimpleAttributeSet sas = new SimpleAttributeSet();
                sas.addAttributes(t.attrs);
                sas.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                sas.removeAttribute(atr);
                doc.setCharacterAttributes(t.offs, t.len, sas, true);
            }
        }
        int pos = editor.getCaretPosition();
        attr.addAttributes(doc.getCharacterElement(pos).getAttributes());
        attr.removeAttribute(atr);
    }
    
    /**
     * Removes a single character attribute from the editor's current position/selection.
     * 
     * <p>Removes from the editor kit's input attribtues and/or document at the caret position.
     * If there is a selction the attribute is removed from the selected text</p>
     * 
     * @param editor
     * @param atr
     */    
    public static void removeCharacterAttribute(JEditorPane editor, Object atr)
    {
        HTMLDocument doc;
        MutableAttributeSet attr;
        try
        {
            doc = (HTMLDocument)editor.getDocument();           
            attr = ((HTMLEditorKit)editor.getEditorKit()).getInputAttributes();
        }
        catch(ClassCastException cce)
        {
            return;
        }
        
        List tokens = tokenizeCharAttribs(doc, editor.getSelectionStart(), editor.getSelectionEnd());
        for(Iterator it = tokens.iterator(); it.hasNext();)
        {
            CharStyleToken t = (CharStyleToken)it.next();            
            if(t.attrs.isDefined(atr))
            {                                        
                SimpleAttributeSet sas = new SimpleAttributeSet();
                sas.addAttributes(t.attrs);
                sas.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                sas.removeAttribute(atr);
                doc.setCharacterAttributes(t.offs, t.len, sas, true);
            }
        }
        int pos = editor.getCaretPosition();
        attr.addAttributes(doc.getCharacterElement(pos).getAttributes());
        attr.removeAttribute(atr);
    }    
    
    /**
     * Tokenizes character attrbutes.
     * @param doc
     * @param s
     * @param e
     * @return
     */
    private static List tokenizeCharAttribs(HTMLDocument doc, int s, int e)
    {        
        LinkedList tokens = new LinkedList();
        CharStyleToken tok = new CharStyleToken();            
        for(; s <= e; s++ )            
        {
            //if(s == doc.getLength())
            //    break;
            AttributeSet as = doc.getCharacterElement(s).getAttributes();
            if(tok.attrs == null || (s + 1 <= e && !as.isEqual(tok.attrs)))
            {              
                tok = new CharStyleToken();
                tok.offs = s;
                tokens.add(tok);                    
                tok.attrs = as;
            } 
            
            if(s+1 <= e)
               tok.len++;
        }
        
        return tokens;
    }
    
    /**
     * Sets the character attributes for selection of the specified editor
     * 
     * @param editor
     * @param attrs
     * @param replace if true, replaces the attrubutes
     */
    public static void setCharacterAttributes(JEditorPane editor, AttributeSet attr, boolean replace)
    {
        HTMLDocument doc;  
        StyledEditorKit k;
        try
        {
            doc = (HTMLDocument)editor.getDocument();
            k = (StyledEditorKit)editor.getEditorKit();
        }
        catch(ClassCastException ex)
        {
            return;
        }       
         
        //TODO figure out what the "CR" attribute is.
        //Somewhere along the line the attribute  CR (String key) with a value of Boolean.TRUE
        //gets inserted. If it is in the attributes, something gets screwed up
        //and the text gets all jumbled up and doesn't render correctly.
        //Is it yet another JEditorPane bug?
        MutableAttributeSet inputAttributes = k.getInputAttributes();        
        SimpleAttributeSet sas = new SimpleAttributeSet(attr);
        sas.removeAttribute("CR");
        attr = sas;
                
        int p0 = editor.getSelectionStart();
        int p1 = editor.getSelectionEnd();
        if(p0 != p1)
        {
            doc.setCharacterAttributes(p0, p1 - p0, attr, replace);
        }        
        else
        {
            //No selection, so we have to update the input attributes
            //otherwise they apparently get reread from the document...
            //not sure if this is a bug or what, but the following works
            //so just go with it.            
            if(replace)
            {
                attr = attr.copyAttributes();
                inputAttributes.removeAttributes(inputAttributes);                
                inputAttributes.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            }
            inputAttributes.addAttributes(attr);
            //System.err.println("inputAttr: " + inputAttributes);
        }
    }
    
    /**
     * Sets the character attributes for selection of the specified editor
     * 
     * @param editor
     * @param attrs
     */
    public static void setCharacterAttributes(JEditorPane editor, AttributeSet attrs)
    {
        setCharacterAttributes(editor, attrs, false);
    }
    
    /**
     * Converts an html tag attribute list to a {@link Map}. 
     * For example, the String 'href="http://blah.com" target="_self"' becomes 
     * name-value pairs:<br>
     * href > http://blah.com<br>
     * target > _self
     * @param atts
     * @return
     */
    public static Map tagAttribsToMap(String atts)
    {
        Map attribs = new HashMap();        
        
        StringTokenizer st = new StringTokenizer(atts.trim(), " ");        
        String lastAtt = null;
        while(st.hasMoreTokens())
        {
            String atVal = st.nextToken().trim();
            int equalPos = atVal.indexOf('=');
            if(equalPos == -1)
            {
                if(lastAtt == null)
                    break;//no equals char in this string
                String lastVal = attribs.get(lastAtt).toString();                
                attribs.put(lastAtt, lastVal + " " + atVal);
                continue;
            }
            
            String at = atVal.substring(0, equalPos);
            String val = atVal.substring(atVal.indexOf('=') + 1, atVal.length());
            if(val.startsWith("\""))
                val = val.substring(1, val.length());
            if(val.endsWith("\""))
                val = val.substring(0, val.length() - 1);
            
            attribs.put(at, val);
            lastAtt = at;            
        }
        
        return attribs;
    }
    
    

    /**
     * Converts a Color to a hex string
     * in the format "#RRGGBB"
     */
    public static String colorToHex(Color color) 
    {
        String colorstr = new String("#");

        // Red
        String str = Integer.toHexString(color.getRed());
        if (str.length() > 2)
            str = str.substring(0, 2);
        else if (str.length() < 2)
            colorstr += "0" + str;
        else
            colorstr += str;

        // Green
        str = Integer.toHexString(color.getGreen());
        if (str.length() > 2)
            str = str.substring(0, 2);
        else if (str.length() < 2)
            colorstr += "0" + str;
        else
            colorstr += str;

        // Blue
        str = Integer.toHexString(color.getBlue());
        if (str.length() > 2)
            str = str.substring(0, 2);
        else if (str.length() < 2)
            colorstr += "0" + str;
        else
            colorstr += str;

        return colorstr;
    }
    
    
    /**
     * Convert a "#FFFFFF" hex string to a Color.
     * If the color specification is bad, an attempt
     * will be made to fix it up.
     */
    public static Color hexToColor(String value)
    {
        String digits;
        //int n = value.length();
        if(value.startsWith("#"))        
            digits = value.substring(1, Math.min(value.length(), 7));        
        else         
            digits = value;
        
        String hstr = "0x" + digits;
        Color c;
        
        try 
        {
            c = Color.decode(hstr);
        } 
        catch(NumberFormatException nfe) 
        {
            c = Color.BLACK; // just return black
        }
        return c; 
    }
    
    /**
     * Convert a color string such as "RED" or "#NNNNNN" or "rgb(r, g, b)"
     * to a Color.
     */
    public static Color stringToColor(String str) 
    {
        Color color = null;
        
        if (str.length() == 0)
            color = Color.black;      
        else if (str.charAt(0) == '#')
            color = hexToColor(str);
        else if (str.equalsIgnoreCase("Black"))
            color = hexToColor("#000000");
        else if(str.equalsIgnoreCase("Silver"))
            color = hexToColor("#C0C0C0");
        else if(str.equalsIgnoreCase("Gray"))
            color = hexToColor("#808080");
        else if(str.equalsIgnoreCase("White"))
            color = hexToColor("#FFFFFF");
        else if(str.equalsIgnoreCase("Maroon"))
            color = hexToColor("#800000");
        else if(str.equalsIgnoreCase("Red"))
            color = hexToColor("#FF0000");
        else if(str.equalsIgnoreCase("Purple"))
            color = hexToColor("#800080");
        else if(str.equalsIgnoreCase("Fuchsia"))
            color = hexToColor("#FF00FF");
        else if(str.equalsIgnoreCase("Green"))
            color = hexToColor("#008000");
        else if(str.equalsIgnoreCase("Lime"))
            color = hexToColor("#00FF00");
        else if(str.equalsIgnoreCase("Olive"))
            color = hexToColor("#808000");
        else if(str.equalsIgnoreCase("Yellow"))
            color = hexToColor("#FFFF00");
        else if(str.equalsIgnoreCase("Navy"))
            color = hexToColor("#000080");
        else if(str.equalsIgnoreCase("Blue"))
            color = hexToColor("#0000FF");
        else if(str.equalsIgnoreCase("Teal"))
            color = hexToColor("#008080");
        else if(str.equalsIgnoreCase("Aqua"))
            color = hexToColor("#00FFFF");
        else
            color = hexToColor(str); // sometimes get specified without leading #
        return color;
    }
    
    
    /**
     * Removes self-closing tags from xhtml for the benifit of {@link JEditorPane}
     * 
     * <p>JEditorpane can't handle empty xhtml containers like &lt;br /&gt; or &lt;img /&gt;, so this method
     * replaces them without the "/" as in &lt;br&gt;</p>
     * 
     * @param html 
     * @return JEditorpane friendly html
     */
    public static String jEditorPaneizeHTML(String html)
    {        
        return html.replaceAll("(<\\s*\\w+\\b[^>]*)/(\\s*>)", "$1$2");
    }
    
    /** Helper method that prints out the contents of the text for this
     *  element.
     * @param elem
     */
    public static void printText(Element elem)
    {
      if (elem instanceof LeafElement)
      {
        AbstractDocument.LeafElement leaf = (AbstractDocument.LeafElement)elem; 
        Segment seg = new Segment();
        try
        {
          elem.getDocument().getText(leaf.getStartOffset(), leaf.getEndOffset() - leaf.getStartOffset(), seg);
          System.err.println("Text : "+seg.toString());
        } catch (BadLocationException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    
    /**
     * Helper method that prints out the contents of an {@link AttributeSet} to System.err
     * for debugging
     * @param attr
     */
    public static void printAttribs(AttributeSet attr)
    {
        System.err.println("----------------------------------------------------------------");
        System.err.println(attr);
        Enumeration ee = attr.getAttributeNames();
        while(ee.hasMoreElements())
        {
            Object name = ee.nextElement();
            Object atr = attr.getAttribute(name);
            System.err.println(name + " " + name.getClass().getName() + " | " + atr + " " + atr.getClass().getName());
        }
        System.err.println("----------------------------------------------------------------");
    }
    
    private static class CharStyleToken
    {
        int offs;
        int len;
        AttributeSet attrs;
    }
}

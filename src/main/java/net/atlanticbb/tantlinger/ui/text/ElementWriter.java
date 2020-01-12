package net.atlanticbb.tantlinger.ui.text;

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

import com.optimasc.text.html.XHTMLWriter;

/**
 * Writer for writing the html of a specified element only.
 * 
 * @author Bob Tantlinger
 *
 */
public class ElementWriter extends XHTMLWriter
{
    private Element root;
    
    private static int getStartPos(Element root, int start)
    {
        if(start >= root.getStartOffset() && start <= root.getEndOffset())
            return start;
        return root.getStartOffset();
    }
    
    private static int getEndPos(Element root, int end)
    {
        if(end >= root.getStartOffset() && end <= root.getEndOffset())
            return end;
        return root.getEndOffset();
    }
    
    public ElementWriter(Writer out, Element root)
    {
        this(out, root, root.getStartOffset(), root.getEndOffset());
    }

    public ElementWriter(Writer out, Element root, int startPos, int endPos)
    {
        super(out, (HTMLDocument)root.getDocument(), 
            getStartPos(root, startPos), 
            Math.min(root.getDocument().getLength(), 
                getEndPos(root, endPos) - getStartPos(root, startPos)));
        

        this.root = root;

        //setIndentSpace(0);        
        setLineLength(Integer.MAX_VALUE);
    }

    protected boolean synthesizedElement(Element e)
    {
        return e.getStartOffset() < getStartOffset() || 
            isAncestor(e, root) || matchNameAttribute(e.getAttributes(), HTML.Tag.IMPLIED);
    } 
    
    /**
     * Returns true if the StyleConstants.NameAttribute is
     * equal to the tag that is passed in as a parameter.
     */
    protected boolean matchNameAttribute(AttributeSet attr, HTML.Tag tag) {
    Object o = attr.getAttribute(StyleConstants.NameAttribute);
    if (o instanceof HTML.Tag) {
        HTML.Tag name = (HTML.Tag) o;
        if (name == tag) {
        return true;
        }
    }
    return false;
    }
    

    private static boolean isAncestor(Element a, Element d)
    {
        for(Element e = d.getParentElement(); e != null; e = e.getParentElement())
            if(e == a)
                return true;

        return false;
    }

    @Override
    public void write() throws IOException, BadLocationException
    {
      super.writeElement(root);
    }
    
    
}
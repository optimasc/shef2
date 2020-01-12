/*
 * Created on Feb 25, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.bushe.swing.action.ActionManager;

import com.optimasc.text.html.HTMLHelper;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;


/**
 * Action which aligns HTML elements
 * 
 * @author Bob Tantlinger
 *
 */
public class HTMLAlignAction extends HTMLTextEditAction
{        
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static final String ALIGNMENT_NAMES[] =
    {
        i18n.str("left"),  
        i18n.str("center"), 
        i18n.str("right"), 
        i18n.str("justify")
    };
    
    private static final int[] MNEMS =
    {
        i18n.mnem("left"),
        i18n.mnem("center"),
        i18n.mnem("right"),
        i18n.mnem("justify")
    };
    
    public static final String ALIGNMENTS[] =
    {
        "left", "center", "right", "justify"
    };
    
    private static final String IMGS[] =
    {
        "al_left.png",  "al_center.png", "al_right.png", "al_just.png"
    };
    
    private int align;
    
    
    /**
     * Creates a new HTMLAlignAction
     * @param al LEFT, RIGHT, CENTER, or JUSTIFY
     * @throws IllegalArgumentException
     */
    public HTMLAlignAction(int al) throws IllegalArgumentException
    {
        super("");
        
        
        putValue(NAME, (ALIGNMENT_NAMES[alignToIndex(al)]));
        putValue(MNEMONIC_KEY, new Integer(MNEMS[alignToIndex(al)]));        
        
        putValue(SMALL_ICON, UIUtils.getIcon(UIUtils.X16, IMGS[alignToIndex(al)]));
        putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);
        
        align = al;
    }
    
    protected void updateWysiwygContextState(JEditorPane ed)
    {        
        setSelected(shouldBeSelected(ed));
    }
    
    private boolean shouldBeSelected(JEditorPane ed)
    {
        HTMLDocument document = (HTMLDocument)ed.getDocument();        
        Element elem = document.getParagraphElement(ed.getCaretPosition());
        if(HTMLUtils.isImplied(elem))
            elem = elem.getParentElement();
        
        AttributeSet at = elem.getAttributes();
        return (HTMLUtils.attributeHelper.getAlignment(at)==align);
    }
    
    protected void updateSourceContextState(JEditorPane ed)
    {
        setSelected(false);
    }
    
    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor)
    {        
        HTMLDocument doc = (HTMLDocument)editor.getDocument();        
        Element curE = doc.getParagraphElement(editor.getSelectionStart());
        Element endE = doc.getParagraphElement(editor.getSelectionEnd());
        
        CompoundUndoManager.beginCompoundEdit(doc);        
        while(true)
        {            
            alignElement(curE);
            if(curE.getEndOffset() >= endE.getEndOffset()
                || curE.getEndOffset() >= doc.getLength())
                break;            
            curE = doc.getParagraphElement(curE.getEndOffset() + 1);            
        }        
        CompoundUndoManager.endCompoundEdit(doc);        
    }
    
    private void alignElement(Element elem)
    {
        HTMLDocument doc = (HTMLDocument)elem.getDocument();
        
        if(HTMLUtils.isImplied(elem))
        {           
            HTML.Tag tag = HTMLHelper.getTag(elem.getParentElement().getName());
            //System.out.println(tag);
            //pre tag doesn't support an align attribute
            //http://www.w3.org/TR/REC-html32#pre
            if(tag != null && (!tag.equals(HTML.Tag.BODY)) && 
                (!tag.isPreformatted() && !tag.equals(HTML.Tag.DD)))
            {
                SimpleAttributeSet as = new SimpleAttributeSet(elem.getAttributes());
                HTMLUtils.attributeHelper.setAlignment(as, align);                
                
                Element parent = elem.getParentElement();
                String html = HTMLUtils.getElementHTML(elem, false);
                html = HTMLUtils.createTag(tag, as, html);  
                String snipet = "";
                for(int i = 0; i < parent.getElementCount(); i++)
                {
                    Element el = parent.getElement(i);
                    if(el == elem)
                        snipet += html;
                    else
                        snipet += HTMLUtils.getElementHTML(el, true);                    
                }            
                
                try
                {
                    doc.setOuterHTML(parent, snipet);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }                
            }
        }
        else
        {
            //Set the HTML attribute on the paragraph...
            MutableAttributeSet set = new SimpleAttributeSet(elem.getAttributes());
            HTMLUtils.attributeHelper.setAlignment(set, align);
             //Set the paragraph attributes...
            int start = elem.getStartOffset();
            int length = elem.getEndOffset() - elem.getStartOffset();
            doc.setParagraphAttributes(start, length - 1, set, true);
        }
    }
    
    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor)
    {
        String prefix = "<p style=\"text-align: " + ALIGNMENTS[alignToIndex(align)] + "\">";
        String postfix = "</p>";
        String sel = editor.getSelectedText();
        if(sel == null)
        {
            editor.replaceSelection(prefix + postfix);
            
            int pos = editor.getCaretPosition() - postfix.length();
            if(pos >= 0)
            	editor.setCaretPosition(pos);                    		  
        }
        else
        {
            sel = prefix + sel + postfix;
            editor.replaceSelection(sel);                
        }
    }
    
    /** Converts a javax.swing.text.StyleConstants Alignment
     *  constant to an internal array index.
     *  
     * @param al StyleConstants alignment type
     * @return An internal 0-based index used for string/icon lookup.
     */

    protected static int alignToIndex(int al)
    {
      switch (al)
      {
        case StyleConstants.ALIGN_LEFT:
          return 0;
        case StyleConstants.ALIGN_CENTER:
          return 1;
        case StyleConstants.ALIGN_RIGHT:
          return 2;
        case StyleConstants.ALIGN_JUSTIFIED:
          return 3;
        default:
          throw new IllegalArgumentException("Illegal alignment value.");
      }
    }
}

/*
 * Created on Feb 27, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.dialogs.HTMLFontDialog;

import org.bushe.swing.action.ActionManager;

import com.optimasc.text.html.FontHelper;
import com.optimasc.text.html.HTMLHelper;


/**
 * Action which edits HTML font size
 * 
 * @author Bob Tantlinger
 *
 */
public class HTMLFontSizeAction extends HTMLTextEditAction
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    private int size;
    
    /**
     * Creates a new HTMLFontSizeAction
     * 
     * @param size one of the FONT_SIZES 
     * (SMALL, MEDIUM, LARGE)
     * 
     * @throws IllegalArgumentException
     */
    public HTMLFontSizeAction(int size) throws IllegalArgumentException
    {
        super("");
        if(size < 0 || size > 6)
            throw new IllegalArgumentException("Invalid size");
        this.size = size;
        putValue(NAME, FontHelper.FONT_SIZE_LABELS[size]);
        putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
    }
    
    protected void updateWysiwygContextState(JEditorPane ed)
    {
        /*HTMLDocument document = (HTMLDocument)ed.getDocument();        
        int caret = (ed.getCaretPosition() > 0) 
            ? (ed.getCaretPosition() - 1) : ed.getCaretPosition();
               
        AttributeSet at = document.getCharacterElement(caret).getAttributes();*/
        AttributeSet at = HTMLUtils.getCharacterAttributes(ed);
        if(at.isDefined(StyleConstants.FontSize))
        {
          assert false;
/*            setSelected(at.containsAttribute(
                StyleConstants.FontSize, new Integer(FONT_SIZES[size])));*/
        } else
        if (at.isDefined(HTML.Tag.SMALL))
        {
          setSelected(size == FontHelper.FONT_SIZE_SMALL);
        }
        else
        if (at.isDefined(HTML.Tag.BIG))
        {
          setSelected(size == FontHelper.FONT_SIZE_LARGE);
        }
        else
        {
           setSelected(size == FontHelper.FONT_SIZE_MEDIUM);
        }        
    }
    
    protected void updateSourceContextState(JEditorPane ed)
    {
        setSelected(false);
    }

    
    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor)
    {
        String prefix = "";
        String postfix = "";
        if (size == FontHelper.FONT_SIZE_SMALL)
        {
          prefix = "<small>";
          postfix = "</small>";
        } else
        if (size == FontHelper.FONT_SIZE_LARGE)
        {
          prefix = "<big>";
          postfix = "</big>";
        }
        
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

    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor)
    { 
      HTMLHelper.setFontSize(editor, FontHelper.FONT_SIZE_LABELS[size]);
    }
}

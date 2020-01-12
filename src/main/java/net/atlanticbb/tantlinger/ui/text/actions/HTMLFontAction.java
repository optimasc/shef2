/*
 * Created on Jan 18, 2006
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;

import com.optimasc.text.AttributeHelper;
import com.optimasc.text.html.HTMLHelper;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.dialogs.HTMLFontDialog;




/**
 * Action which edits an HTML font
 * 
 * @author Bob Tantlinger
 *
 */
public class HTMLFontAction extends HTMLTextEditAction
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public HTMLFontAction()
    {
        super(i18n.str("font_"));         //$NON-NLS-1$
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor)
    {
        HTMLFontDialog d = createDialog(editor);
        d.setLocationRelativeTo(d.getParent());
        d.setVisible(true);
        if(!d.hasUserCancelled())
        {
            editor.requestFocusInWindow();
            editor.replaceSelection(d.getHTML());     
        }
    }

    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor)
    {
        HTMLDocument doc = (HTMLDocument)editor.getDocument();
        Element chElem = doc.getCharacterElement(editor.getCaretPosition());
        AttributeSet sas = chElem.getAttributes();
                
        HTMLFontDialog d = createDialog(editor);
        d.setBold(HTMLUtils.attributeHelper.isBold(sas));
        d.setItalic(HTMLUtils.attributeHelper.isItalic(sas));
        d.setStrikethrough(HTMLUtils.attributeHelper.isStrikeThrough(sas));
        Object o = HTMLUtils.attributeHelper.getFontFamily(sas);
        if(o != null)
            d.setFontName(o.toString());
        o = sas.getAttribute(StyleConstants.FontSize);
        if(o != null)
        {
            try
            {
                d.setFontSize(o.toString());
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        
        d.setLocationRelativeTo(d.getParent());
        d.setVisible(true);
        if(!d.hasUserCancelled())
        {
            MutableAttributeSet tagAttrs = new SimpleAttributeSet();
            HTMLUtils.attributeHelper.setFontFamily(tagAttrs, d.getFontName());
            HTMLHelper.setFontSize(editor, d.getFontSize());
            HTMLUtils.attributeHelper.setBold(tagAttrs, d.isBold());
            HTMLUtils.attributeHelper.setItalic(tagAttrs, d.isItalic());
            HTMLUtils.attributeHelper.setStrikeThrough(tagAttrs, d.isStrikeThrough());
            
            CompoundUndoManager.beginCompoundEdit(editor.getDocument());
            HTMLUtils.setCharacterAttributes(editor, tagAttrs);
            CompoundUndoManager.endCompoundEdit(editor.getDocument());
        }
    }
    
    private HTMLFontDialog createDialog(JTextComponent ed)
    {
        Window w = SwingUtilities.getWindowAncestor(ed);
        String t = ""; //$NON-NLS-1$
        if(ed.getSelectedText() != null)
            t = ed.getSelectedText();
        HTMLFontDialog d = null;
        if(w != null && w instanceof Frame)
            d = new HTMLFontDialog((Frame)w, t);
        else if(w != null && w instanceof Dialog)
            d = new HTMLFontDialog((Dialog)w, t);        
                
        return d;
    }

}

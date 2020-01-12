/*
 * Created on Mar 3, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.optimasc.text.html.HTMLHelper;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;



/**
 * Action which inserts a horizontal rule
 * 
 * @author Bob Tantlinger
 *
 */
public class HTMLInsertHorizontalRuleAction extends HTMLTextEditAction
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /** List of tags that can be parent of HR */
    protected static final HTML.Tag tags[] =
      {
        HTML.Tag.BLOCKQUOTE,
        HTML.Tag.BODY,
        HTML.Tag.DD,
        HTML.Tag.LI,
        HTML.Tag.APPLET,
        HTML.Tag.TD,
        HTML.Tag.TH,
        HTML.Tag.OBJECT,
        HTML.Tag.DIV,
        HTML.Tag.IMPLIED
      };

    public HTMLInsertHorizontalRuleAction()
    {
        super(i18n.str("horizontal_rule"));
        putValue(MNEMONIC_KEY, new Integer(i18n.mnem("horizontal_rule")));
        putValue(SMALL_ICON, UIUtils.getIcon(UIUtils.X16, "hrule.png")); 
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor)
    {
        editor.replaceSelection("<hr></hr>");
    }
    
    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor)
    {
        boolean found;
        HTMLDocument document = (HTMLDocument)editor.getDocument();
        int caret = editor.getCaretPosition();
        Element elem = document.getParagraphElement(caret);

        HTML.Tag tag = HTMLHelper.getTag(elem.getName());
        if(HTMLUtils.isImplied(elem))
          tag = HTML.Tag.IMPLIED;
        
        
        found = false;
        /* Check if this is a valid parent tag */
        for (int i = 0; i < tags.length; i++)
        {
          if (tag.equals(tags[i]))
          {
            found = true;
            break;
          }
        }
        
        /* Not a valid parent, set to default */
        if (found == false)
        {
            /* Force to body parent */
            tag = HTML.Tag.BODY;
        }
        
        
        
        HTMLEditorKit.InsertHTMLTextAction a =
            new HTMLEditorKit.InsertHTMLTextAction("", "<hr></hr>", tag, HTML.Tag.HR);
        a.actionPerformed(e);
    }
}

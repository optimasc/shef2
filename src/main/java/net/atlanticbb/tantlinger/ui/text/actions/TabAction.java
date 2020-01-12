/*
 * Created on Dec 26, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.optimasc.text.html.HTMLHelper;

import net.atlanticbb.tantlinger.ui.text.HTMLUtils;

/**
 * Tab action for tabbing between table cells
 * 
 * @author Bob Tantlinger
 * 
 */
public class TabAction extends DecoratedTextAction
{
  /**
     * 
     */
  private static final long serialVersionUID = 1L;
  public static final int FORWARD = 0;
  public static final int BACKWARD = 1;

  //private Action delegate;
  private int type;

  public TabAction(int type, Action defaultTabAction)
  {
    super("tabAction", defaultTabAction);
    //delegate = defaultTabAction;
    this.type = type;
  }

  public void actionPerformed(ActionEvent e)
  {
    JEditorPane editor;
    HTMLDocument document;

    editor = (JEditorPane) getTextComponent(e);
    document = (HTMLDocument) editor.getDocument();
    Element elem = document.getParagraphElement(editor.getCaretPosition());
    Element tdElem = HTMLUtils.getParent(elem, HTML.Tag.TD);
    Element listElement = HTMLUtils.getParent(elem, HTML.Tag.LI);
    if (tdElem != null)
    {
      try
      {
        if (type == FORWARD)
          editor.setCaretPosition(tdElem.getEndOffset());
        else
          editor.setCaretPosition(tdElem.getStartOffset() - 1);
      } catch (IllegalArgumentException ex)
      {
        ex.printStackTrace();
      }
    }
    else if (listElement != null)
    {
      /* Find the previous list item if it exists. */
      /*          Element el1 = HTMLUtils.getPreviousListElement(listElement);
                if (el1 != null)
                {
                  try
                  {
                  String s = HTMLUtils.getElementHTML(el1,false);
                    document.setInnerHTML(el1, s+"<ul><li></li></ul>");
                    HTMLUtils.removeElement(listElement);
                    document.dump(System.out);
                  } catch (BadLocationException e1)
                  {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                  } catch (IOException e1)
                  {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                  }
                }*/
      if (type == FORWARD)
      {
        HTML.Tag tag = HTMLHelper.getTag(listElement.getName());
        HTMLEditorKit.InsertHTMLTextAction hta =
            new HTMLEditorKit.InsertHTMLTextAction(
                "insertLI",
                "<ul><li></li></ul>",
                tag,
                HTML.Tag.UL);
        hta.actionPerformed(e);
      }
    }
    else
      delegate.actionPerformed(e);
  }

}

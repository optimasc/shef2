package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;

import org.bushe.swing.action.ShouldBeEnabledDelegate;

/**
 * Action that permits to save an element. It currently supports saving
 * <code>IMG</code> elements.
 * 
 * @author Carl Eric Codere
 * 
 */
public class HTMLSaveElementAction extends HTMLTextEditAction
{

  private static final long serialVersionUID = -2824536497881710266L;

  public HTMLSaveElementAction()
  {
    super("Save As");
    addShouldBeEnabledDelegate(new ShouldBeEnabledDelegate()
    {
      public boolean shouldBeEnabled(Action a)
      {
        if (getEditMode() == SOURCE)
        {
          return false;
        }
        Element elem = HTMLUtils.elementAtCaretPosition(getCurrentEditor());
        AttributeSet att = elem.getAttributes();
        Object type = att.getAttribute(StyleConstants.NameAttribute);
        if ((type.equals(HTML.Tag.IMG)))
        {
          return true;
        }
        return false;
      }
    });
  }

  protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor)
  {
    Element elem = HTMLUtils.elementAtCaretPosition(editor);
    AttributeSet att = elem.getAttributes();
    Object type = att.getAttribute(StyleConstants.NameAttribute);
    if (type.equals(HTML.Tag.IMG))
    {
      URL url = HTMLUtils.getImageURL(elem.getAttributes(), (HTMLDocument) elem.getDocument());
      /* Get the content type. *
       * 
       */
      String fileExt = "";
      File currentFile;
      FileNameExtensionFilter fileFilter = null;
      try
      {
        String mimeType = HTMLUtils.getImageMIME(url);
        if (mimeType == null)
        {
          UIUtils.showError("Resource does not seem to exist " + url.toString());
          return;
        }
        if (mimeType.startsWith("image/png"))
        {
          fileExt = ".png";
          fileFilter = new FileNameExtensionFilter("PNG Files", "png", "PNG");
        } else if (mimeType.startsWith("image/svg+xml"))
        {
          fileExt = ".svg";
          fileFilter = new FileNameExtensionFilter("SVG Files", "svg", "SVG");
        } else
        {
          UIUtils.showError("Unsupported MIME type " + mimeType);
          return;
        }

        InputStream is = url.openStream();
        int c;
        File current = new File(".");
        JFileChooser chooser = new JFileChooser(current);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(fileFilter);
        int approval = chooser.showSaveDialog(null);
        if (approval == JFileChooser.APPROVE_OPTION)
        {
          File newFile = chooser.getSelectedFile();
          if (newFile.exists())
          {
            String message = newFile.getAbsolutePath()
                + " already exists. \n"
                + "Do you want to replace it?";
            if (JOptionPane.showConfirmDialog(null, message) == JOptionPane.YES_OPTION)
            {
              currentFile = newFile;

            } else
            {
              return;
            }
          } else
          {
            String fileName = newFile.getAbsolutePath();
            if (fileName.toLowerCase().endsWith(fileExt) == false)
            {
              fileName = fileName + fileExt;
            }
            currentFile = new File(fileName);
          }
          /* Save the file */
          OutputStream os = new FileOutputStream(currentFile);
          while ((c = is.read()) != -1)
          {
            os.write(c);
          }
          is.close();
          os.close();
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
              "Saved " + currentFile.getAbsolutePath());
        } /* end approve option */
      } catch (FileNotFoundException fnfe)
      {
        UIUtils.showError((Frame) null, "Error saving document.", fnfe);
      } catch (IOException ioe)
      {
        UIUtils.showError((Frame) null, "Error saving document.", ioe);
      }
    }
  }

  protected void sourceEditPerformed(ActionEvent e, JEditorPane editor)
  {

  }

}

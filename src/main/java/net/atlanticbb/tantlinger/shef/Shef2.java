package net.atlanticbb.tantlinger.shef;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;

import net.atlanticbb.tantlinger.ui.AboutDialog;
import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.dialogs.PropertiesDialog;

import org.xml.sax.SAXException;

import com.optimasc.text.DocumentProperties;
import com.optimasc.text.html.XHTMLParser;
import com.optimasc.text.html.XHTMLWriter;
import com.optimasc.utils.DataURLConnection;

/**
 * 
 * @author Bob Tantlinger
 */
public class Shef2 implements ActionListener
{
  protected static final String PRODUCT_VERSION = "0.9.0"; 
  protected static final String PRODUCT_NAME = "SHEF2"; 
  protected static final String PRODUCT_VENDOR = "Optima SC Inc."; 
  
  
  protected HTMLEditorPane editor;
  private File currentFile;
  protected JFrame frame;

  class XHTMLFileFilter extends javax.swing.filechooser.FileFilter
  {

    public boolean accept(File f)
    {
      return ((f.isDirectory()) || (f.getName().toLowerCase().indexOf(".html") > 0));
    }

    public String getDescription()
    {
      return "html";
    }
  }

  public Shef2()
  {

    /*    InputStream in = Demo.class.getResourceAsStream("/net/atlanticbb/tantlinger/shef/htmlsnip.txt");
        try
        {
          editor.setText(IOUtils.read(in));
        } catch (IOException ex)
        {
          ex.printStackTrace();
        } finally
        {
          IOUtils.close(in);
        }*/

    frame = new JFrame();
    editor = new HTMLEditorPane(frame);
    JMenuBar menuBar = new JMenuBar();

    JMenuItem newItem = new JMenuItem("New", new ImageIcon("whatsnew-bang.gif"));
    JMenuItem openItem = new JMenuItem("Open", new ImageIcon("open.gif"));
    JMenuItem saveItem = new JMenuItem("Save", new ImageIcon("save.gif"));
    JMenuItem saveAsItem = new JMenuItem("Save As");
    JMenuItem exitItem = new JMenuItem("Exit", new ImageIcon("exit.gif"));
    JMenuItem propertiesItem = new JMenuItem("Properties");
//    JMenuItem exportItem = new JMenuItem("Export");

    JMenu fileMenu = new JMenu("File");

    newItem.addActionListener(this);
    openItem.addActionListener(this);
    saveItem.addActionListener(this);
    saveAsItem.addActionListener(this);
    propertiesItem.addActionListener(this);
    exitItem.addActionListener(this);
//    exportItem.addActionListener(this);

    fileMenu.add(newItem);
    fileMenu.add(openItem);
    fileMenu.add(saveItem);
    fileMenu.add(saveAsItem);
    fileMenu.addSeparator();
    fileMenu.add(propertiesItem);
    fileMenu.addSeparator();
//    fileMenu.add(exportItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);
    
    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.addActionListener(this);
    
    JMenu aboutMenu = new JMenu("About");
    aboutMenu.add(aboutItem);


    menuBar.add(fileMenu);
    menuBar.add(editor.getEditMenu());
    menuBar.add(editor.getFormatMenu());
    menuBar.add(editor.getInsertMenu());
    menuBar.add(aboutMenu);
    frame.setJMenuBar(menuBar);

    frame.setTitle("SHEF2");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800, 600);
    frame.getContentPane().add(editor);
    frame.setVisible(true);

  }

  public void startNewDocument()
  {
    editor.startNewDocument();
  }

  public void openDocument()
  {
    try
    {
      File current = new File(".");
      JFileChooser chooser = new JFileChooser(current);
      chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      chooser.setFileFilter(new XHTMLFileFilter());
      int approval = chooser.showOpenDialog(frame);
      if (approval == JFileChooser.APPROVE_OPTION)
      {
        currentFile = chooser.getSelectedFile();
        frame.setTitle(currentFile.getName());

        /*        try 
                {
                  XHTMLValidator.validateDocument(currentFile);
                } catch (SAXParseException e)
                {
                  error("Invalid XHTML 1.0 Strict document.","Verify the syntax of the document.",e);
                  return;
                } catch (SAXNotSupportedException e)
                {
                  error("Invalid XHTML 1.0 Strict document.","Verify the Document Type.",e);
                  return;
                } catch (Exception e)
                {
                  error("Error opening document.",null,e);
                  e.printStackTrace();
                }*/
        FileReader fr = new FileReader(currentFile);
        XHTMLParser.validateDocument(fr);
        fr = new FileReader(currentFile);
        Document oldDoc = editor.getDocument();
        /*              if(oldDoc != null)
                              oldDoc.removeUndoableEditListener(undoHandler);*/
        EditorKit editorKit = editor.getEditorKit();
        HTMLDocument document = (HTMLDocument)editorKit.createDefaultDocument();
        document.setBase(currentFile.getParentFile().toURI().toURL());
        editorKit.read(fr, document, 0);
        //      document.addUndoableEditListener(undoHandler);
        //((AbstractDocument) document).dump(System.out);
        editor.setDocument(document);
        //              resetUndoManager();
      }
    } catch (BadLocationException ble)
    {
      UIUtils.showError(frame, "Error opening document.", ble);
    } catch (FileNotFoundException fnfe)
    {
      UIUtils.showError(frame, "Error opening document.", fnfe);
    } catch (IOException ioe)
    {
      UIUtils.showError(frame, "Error opening document.", ioe);
    } catch (SAXException e)
    {
      UIUtils.showError(frame, "Error opening document. Is it a valid document?", e);
    } catch (IllegalArgumentException e)
    {
      UIUtils.showError(frame, "Error opening document. Is it a valid document?", e);
    }

  }

  public void saveDocument()
  {
    if (currentFile != null)
    {
      try
      {
        FileWriter fw = new FileWriter(currentFile);
        /*                      fw.write(editor.getText());*/
        Document document = editor.getDocument();
        document.putProperty(DocumentProperties.GeneratorProperty, getVersionString());
        /* Write the document ourselves, but specifying that we need to write
         * the document header as well as full table headers. 
         */
        XHTMLWriter htmlWriter = new XHTMLWriter(fw, document);
        htmlWriter.setParameter(XHTMLWriter.CONFIG_WRITE_HEADER, Boolean.TRUE);
        htmlWriter.setParameter(XHTMLWriter.CONFIG_WRITE_FULL_TABLE, Boolean.TRUE);
        htmlWriter.write();

        fw.close();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Saved " + currentFile.getAbsolutePath());
      } catch (FileNotFoundException fnfe)
      {
        UIUtils.showError(frame, "Error saving document.", fnfe);
      } catch (IOException ioe)
      {
        UIUtils.showError(frame, "Error saving document.", ioe);
      } catch (BadLocationException e)
      {
        UIUtils.showError(frame, "Error saving document.", e);
      }
    } else
    {
      saveDocumentAs();
    }
  }

  public void saveDocumentAs()
  {
    try
    {
      File current = new File(".");
      JFileChooser chooser = new JFileChooser(current);
      chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      chooser.setFileFilter(new XHTMLFileFilter());
      int approval = chooser.showSaveDialog(frame);
      if (approval == JFileChooser.APPROVE_OPTION)
      {
        File newFile = chooser.getSelectedFile();
        if (newFile.exists())
        {
          String message = newFile.getAbsolutePath()
              + " already exists. \n"
              + "Do you want to replace it?";
          if (JOptionPane.showConfirmDialog(frame, message) == JOptionPane.YES_OPTION)
          {
            currentFile = newFile;
            frame.setTitle(currentFile.getName());

            FileWriter fw = new FileWriter(currentFile);
            /*                      fw.write(editor.getText());*/
            HTMLDocument document = (HTMLDocument)editor.getDocument();
            /* Set base for correct image information */
            document.setBase(currentFile.getParentFile().toURI().toURL());
            document.putProperty(DocumentProperties.GeneratorProperty, getVersionString());
            /* Write the document ourselve, but specifying that we need to write
             * the document header as well as full table headers. 
             */
            XHTMLWriter htmlWriter = new XHTMLWriter(fw, document);
            htmlWriter.setParameter(XHTMLWriter.CONFIG_WRITE_HEADER, Boolean.TRUE);
            htmlWriter.setParameter(XHTMLWriter.CONFIG_WRITE_FULL_TABLE, Boolean.TRUE);
            htmlWriter.write();

            fw.close();
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
                "Saved " + currentFile.getAbsolutePath());
          }
        } else
        {
          currentFile = new File(newFile.getAbsolutePath());
          frame.setTitle(currentFile.getName());
          FileWriter fw = new FileWriter(currentFile);

          Document document = editor.getDocument();
          /* Write the document ourselve, but specifying that we need to write
           * the document header. 
           */
          XHTMLWriter htmlWriter = new XHTMLWriter(fw, document);
          document.putProperty(DocumentProperties.GeneratorProperty, getVersionString());
          htmlWriter.setParameter(XHTMLWriter.CONFIG_WRITE_HEADER, Boolean.TRUE);
          htmlWriter.write();

          fw.close();
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
              .info("Saved " + currentFile.getAbsolutePath());
        }
      }
    } catch (BadLocationException ble)
    {
      UIUtils.showError(frame, "Error saving document.", ble);
    } catch (FileNotFoundException fnfe)
    {
      UIUtils.showError(frame, "Error saving document.", fnfe);
    } catch (IOException ioe)
    {
      UIUtils.showError(frame, "Error saving document.", ioe);
    }
  }

  public void exit()
  {
    String exitMessage = "Are you sure you want to exit?";
    if (JOptionPane.showConfirmDialog(frame, exitMessage) == JOptionPane.YES_OPTION)
    {
      System.exit(0);
    }
  }
  
  
  public static void forcefullyInstall(URLStreamHandlerFactory factory) {
     try {
         // Try doing it the normal way
         URL.setURLStreamHandlerFactory(factory);
     } catch (final Error e) {
         // Force it via reflection
         try {
             final Field factoryField = URL.class.getDeclaredField("factory");
             factoryField.setAccessible(true);
             factoryField.set(null, factory);
         } catch (Exception e1) {
             throw new Error("Could not access factory field on URL class: {}", e);
         }
     }
 }  

  public static void main(String args[])
  {
    try
    {
    //   URL.setURLStreamHandlerFactory(new DataURLConnection.DataURLStreamHandlerFactory());
      forcefullyInstall(new DataURLConnection.DataURLStreamHandlerFactory()); 
      UIManager.setLookAndFeel(
          UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex)
    {
    }

    SwingUtilities.invokeLater(new Runnable()
    {

      public void run()
      {
        new Shef2();
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    String actionCommand = e.getActionCommand();

    int modifier = e.getModifiers();
    long when = e.getWhen();
    String parameter = e.paramString();
    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    logger.info("actionCommand: " + actionCommand);
    logger.info("modifier: " + modifier);
    logger.info("modifier: " + modifier);
    logger.info("when: " + when);
    logger.info("parameter: " + parameter);
    if (actionCommand.compareTo("New") == 0)
    {
      startNewDocument();
    } else if (actionCommand.compareTo("Open") == 0)
    {
      openDocument();
    } else if (actionCommand.compareTo("Save") == 0)
    {
      saveDocument();
    } else if (actionCommand.compareTo("Save As") == 0)
    {
      saveDocumentAs();
    } else if (actionCommand.compareTo("Exit") == 0)
    {
      exit();
    } else if (actionCommand.compareTo("Properties") == 0)
    {
      PropertiesDialog d = new PropertiesDialog(frame);
      if (d != null)
      {
        AbstractDocument document = (AbstractDocument) editor.getDocument();
        d.setProperties(document.getDocumentProperties());
        d.setVisible(true);
        if (!d.hasUserCancelled())
        {
          d.getProperties(document.getDocumentProperties());
        }
      }
      //    exit();
    } else if (actionCommand.compareTo("About") == 0)
    {
      AboutDialog aboutDialog = new AboutDialog(frame);
      try
      {
        BufferedImage image = ImageIO.read( ClassLoader.getSystemResource( "res/images/misc/shef1.png" ));
        if (image != null)
        {
          aboutDialog.setIcon(new ImageIcon(image));
        }
      } catch (IOException e1)
      {
      }
      aboutDialog.setProductInfo(PRODUCT_NAME, PRODUCT_VERSION);
      aboutDialog.setProductVendor(PRODUCT_VENDOR);
      
      aboutDialog.setMessage("This application is under the LGPL 2.1 license.\nThis product is based on SHEF by Bob Tantlinger.");
      aboutDialog.setVisible(true);
    }
  }
  
  /** Returns the <code>AgentName</code> following
   *  the format defined in the XMP Specification.
   *  
   *  <code>AgentName</code> has the form:
   *  <p><code>Organization Software_name Version (token;token;...)</code></p>
   *  
   *  It tries to find the information in the MANIFEST file, using the following
   *  manifest attributes, and possible alternatives:
   *  <ul>
   *   <li><code>Implementation-Vendor</code> with spaces replaced by underscores.
   *   If not found, it uses a one space character</li>
   *   <li><code>Implementation-Title</code>, if not found it uses the current
   *   class name. </li>
   *   <li><code>Implementation-Version</code>, if not found, it uses the
   *   version number 0.0.0</code> 
   *  </ul>
   * 
   * @return The <code>AgentName</code> string.
   */
  protected String getVersionString()
  {
    Package packageInfo = Shef2.class.getPackage();
    String title = null;
    String vendor = null;
    String version = null;
    
    title = packageInfo.getImplementationTitle();
    if (title == null)
      title = Shef2.class.getSimpleName();
    vendor = packageInfo.getImplementationVendor();
    if (vendor == null)
      vendor = "";
    version = packageInfo.getImplementationVersion();
    if (version == null)
      version = "0.0.0";
    return PRODUCT_VENDOR + " " + PRODUCT_NAME + " " + PRODUCT_VERSION; 
  }

}

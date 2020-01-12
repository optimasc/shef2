package net.atlanticbb.tantlinger.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Simple About Dialog box for Java Standard Edition that displays information
 * on the application as well as some additional data as required. The dialog
 * box shall be created in a modal state, and will be centered on its parent.
 * 
 * By default, it will try to populate it will try to get information from the 
 * Manifest file if present, and if not use default values.
 * 
 * @author Carl Eric Codere
 * 
 */
public class AboutDialog extends JDialog implements ActionListener
{

  protected JLabel labelApplicationName;
  protected JLabel labelApplicationVendor;
  protected JLabel labelIcon;
  protected JEditorPane textArea;

  protected static final String TEXT_ABOUT = "About";
  protected static final String TEXT_OK = "Ok";
  
  protected static final SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");

  protected String textProductName = "Acme";
  protected String textProductVendor = "Acme Inc.";
  protected String textProductVersion = "0.0.0";

  /**
   * Creates an About Dialog
   * 
   * @param parent
   *          The parent frame, used for centering./
   * @param title
   *          The dialog box title
   * @param message
   *          The extra message to display, this value can be nil.
   * @param OkString
   *          The ok String label.
   */
  public AboutDialog(JFrame parent)
  {

    super(parent, TEXT_ABOUT, true);

    if (parent != null)
    {
      Dimension parentSize = parent.getSize();
      Point p = parent.getLocation();
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
      setIconImage(parent.getIconImage());
    }
    setMinimumSize(new Dimension(360, 240));
    setMaximumSize(new Dimension(360, 240));

    JPanel infoPanel = new JPanel();

    Box box = new Box(BoxLayout.PAGE_AXIS);
    labelApplicationName = new JLabel();
    labelApplicationName.setFont(new Font("Tahoma", Font.PLAIN, 13));
    labelApplicationName.setBorder(new EmptyBorder(2, 4, 2, 4));
    labelApplicationName.setText("<html><b>" + textProductName + "</b>, version "
        + textProductVersion + "</html>");

    labelApplicationVendor = new JLabel();
    labelApplicationVendor.setFont(new Font("Tahoma", Font.PLAIN, 13));
    labelApplicationVendor.setBorder(new EmptyBorder(2, 4, 2, 4));
    //labelApplicationVendor.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelApplicationVendor.setText("<html><i>" + textProductVendor + "</i></html>");

    box.add(Box.createVerticalStrut(40));
    box.add(labelApplicationName);
    box.add(labelApplicationVendor);


    getContentPane().add(infoPanel);

    GridBagLayout gbl_infoPanel = new GridBagLayout();
    gbl_infoPanel.columnWeights = new double[] { 0.4, 0.6 };
    gbl_infoPanel.rowWeights = new double[] { 1.0 };
    infoPanel.setLayout(gbl_infoPanel);

    labelIcon = new JLabel();
    labelIcon.setHorizontalAlignment(SwingConstants.CENTER);
    GridBagConstraints gbc_labelIcon = new GridBagConstraints();
    gbc_labelIcon.fill = GridBagConstraints.HORIZONTAL;
    gbc_labelIcon.gridx = 0;
    gbc_labelIcon.gridy = 0;
    infoPanel.add(labelIcon, gbc_labelIcon);

    GridBagConstraints gbc_box = new GridBagConstraints();
    gbc_box.anchor = GridBagConstraints.NORTH;
    gbc_box.fill = GridBagConstraints.BOTH;
    gbc_box.gridx = 1;
    gbc_box.gridy = 0;
    gbc_labelIcon.gridx = 1;
    gbc_labelIcon.gridy = 0;
    infoPanel.add(box, gbc_box);
    
    textArea = new JEditorPane();
    textArea.setEditable(false);
    textArea.setEditorKit(new HTMLEditorKit());
    textArea.setContentType("text/html");
    textArea.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
    Color c = getContentPane().getBackground();

    // Its the only way to change the background color - do not ask why
    // See http://stackoverflow.com/questions/12438974/jtextarea-background-issue
    Color color = new Color(UIManager.getColor("control").getRGB());
    getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    textArea.setBackground(color);
//    textArea.setLineWrap(true);

    textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
    JScrollPane scroll = new JScrollPane(textArea,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroll.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

    getContentPane().add(scroll);
    

    JPanel buttonPane = new JPanel();
    JButton button = new JButton(TEXT_OK);
    buttonPane.add(button);
    button.addActionListener(this);
    getContentPane().add(buttonPane);
    
    
    
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    

    getManifestInformation();
    pack();
  }

  public void actionPerformed(ActionEvent e)
  {
    setVisible(false);
    dispose();
  }

  public void setProductVendor(String vendor)
  {
    labelApplicationVendor.setText("<html><i>" + vendor + "</i></html>");
  }

  public void setProductInfo(String name, String version)
  {
    labelApplicationName.setText("<html><b>" + name + "</b>, version " + version + "</html>");
  }

  public void setIcon(Icon icon)
  {
    labelIcon.setIcon(icon);
  }

  public void setMessage(String message)
  {
    textArea.setText(message);
    textArea.setCaretPosition(0);
  }
  
  protected void getManifestInformation()
  {
    Class clazz = AboutDialog.class;
    String className = clazz.getSimpleName() + ".class";
    String classPath = clazz.getResource(className).toString();
    if (!classPath.startsWith("jar")) {
      // Class not from JAR
      return;
    }
    String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + 
      "/META-INF/MANIFEST.MF";
    Manifest manifest;
    try
    {
      manifest = new Manifest(new URL(manifestPath).openStream());
      Attributes attr = manifest.getMainAttributes();
      if (attr != null)
      {
        String value = attr.getValue("Implementation-Title");
        if (value != null)
        {
          textProductName = value;
        }
        value = attr.getValue("Implementation-Vendor");
        if (value != null)
        {
          textProductVendor = value;
        }
        value = attr.getValue("Implementation-Version");
        if (value != null)
        {
          textProductVersion = value;
        }
        
      }
    } catch (MalformedURLException e)
    {
    } catch (IOException e)
    {
    }
  }
}

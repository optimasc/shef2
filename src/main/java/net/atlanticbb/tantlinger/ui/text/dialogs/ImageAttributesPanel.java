/*
 * Created on Jan 14, 2006
 *
 */
package net.atlanticbb.tantlinger.ui.text.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.TextEditPopupManager;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.optimasc.text.html.HTMLHelper;

public class ImageAttributesPanel extends HTMLAttributeEditorPanel implements ActionListener
{

  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  private JLabel imgUrlLabel = null;
  private JLabel altTextLabel = null;
  private JTextField imgUrlField = null;
  private JTextField altTextField = null;

  private JPanel spacerPanel = null;
  private JCheckBox titleCB = null;
  private JTextField titleTextField = null;
  private SizeAttributesPanel sizeAttributesPanel = null;
  private JButton imgURLButton;

  /**
   * This is the default constructor
   */
  public ImageAttributesPanel()
  {
    super();
    initialize();
    updateComponentsFromAttribs();
  }

  public void updateComponentsFromAttribs()
  {
    sizeAttributesPanel.setAttributes(attribs);
    if (attribs.containsKey(HTMLHelper.Attributes.SRC)) //$NON-NLS-1$
      imgUrlField.setText(attribs.get(HTMLHelper.Attributes.SRC).toString()); //$NON-NLS-1$

    if (attribs.containsKey(HTMLHelper.Attributes.ALT)) //$NON-NLS-1$
    {
      altTextField.setText(attribs.get(HTMLHelper.Attributes.ALT).toString()); //$NON-NLS-1$
    }
    else
    {
      altTextField.setText("");
    }

    if (attribs.containsKey(HTMLHelper.Attributes.TITLE))
    {
      titleCB.setSelected(true);
      titleTextField.setEditable(true);
      titleTextField.setText(attribs.get(HTMLHelper.Attributes.TITLE).toString());
    }
    else
    {
      titleCB.setSelected(false);
      titleTextField.setEditable(false);
    }

  }

  public void updateAttribsFromComponents()
  {
    sizeAttributesPanel.updateAttribsFromComponents();
    attribs.put(HTMLHelper.Attributes.SRC, imgUrlField.getText()); //$NON-NLS-1$

    attribs.put(HTMLHelper.Attributes.ALT, altTextField.getText()); //$NON-NLS-1$

    if (titleCB.isSelected())
      attribs.put(HTMLHelper.Attributes.TITLE, titleTextField.getText());
    else
      attribs.remove(HTMLHelper.Attributes.TITLE);
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize()
  {
    GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
    gridBagConstraints21.insets = new Insets(0, 0, 5, 0);
    gridBagConstraints21.gridx = 0;
    gridBagConstraints21.gridwidth = 2;
    gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints21.weighty = 1.0;
    gridBagConstraints21.gridy = 3;
    GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
    gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints15.gridy = 1;
    gridBagConstraints15.weightx = 1.0;
    gridBagConstraints15.insets = new java.awt.Insets(0, 0, 10, 0);
    gridBagConstraints15.gridwidth = 2;
    gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints15.gridx = 1;
    GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
    gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints14.gridy = 0;
    gridBagConstraints14.weightx = 1.0;
    gridBagConstraints14.insets = new java.awt.Insets(0, 0, 5, 0);
    gridBagConstraints14.gridwidth = 1;
    gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints14.gridx = 1;
    GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints1.insets = new java.awt.Insets(0, 0, 10, 5);
    gridBagConstraints1.gridy = 1;
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
    gridBagConstraints.gridy = 0;
    imgUrlLabel = new JLabel();
    imgUrlLabel.setText(i18n.str("image_url")); //$NON-NLS-1$
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0 };
    this.setLayout(gridBagLayout);
    this.setSize(365, 188);
    //this.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Image Properties", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null), javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
    this.add(imgUrlLabel, gridBagConstraints);
    this.add(getAltTextLabel(), gridBagConstraints1);
    this.add(getImgUrlField(), gridBagConstraints14);
    this.add(getAltTextField(), gridBagConstraints15);
    GridBagConstraints gbc_titleCB = new GridBagConstraints();
    gbc_titleCB.anchor = GridBagConstraints.WEST;
    gbc_titleCB.insets = new Insets(0, 0, 5, 5);
    gbc_titleCB.gridx = 0;
    gbc_titleCB.gridy = 2;
    add(getTitleCB(), gbc_titleCB);
    GridBagConstraints gbc_titleTextField = new GridBagConstraints();
    gbc_titleTextField.gridwidth = 2;
    gbc_titleTextField.insets = new Insets(0, 0, 5, 0);
    gbc_titleTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_titleTextField.gridx = 1;
    gbc_titleTextField.gridy = 2;
    add(getTitleTextField(), gbc_titleTextField);
    GridBagConstraints gbc_sizeAttributesPanel = new GridBagConstraints();
    gbc_sizeAttributesPanel.insets = new Insets(0, 0, 5, 0);
    gbc_sizeAttributesPanel.gridwidth = 3;
    gbc_sizeAttributesPanel.gridy = 3;
    gbc_sizeAttributesPanel.gridx = 0;
    add(getSizeAttributesPanel(), gbc_sizeAttributesPanel);
    this.add(getSpacerPanel(), gridBagConstraints21);

    TextEditPopupManager popupMan = TextEditPopupManager.getInstance();
    popupMan.registerJTextComponent(imgUrlField);
    popupMan.registerJTextComponent(altTextField);
    GridBagConstraints gbc_imgURLButton = new GridBagConstraints();
    gbc_imgURLButton.insets = new Insets(0, 0, 0, 5);
    gbc_imgURLButton.gridx = 2;
    gbc_imgURLButton.gridy = 0;
    add(getImgURLButton(), gbc_imgURLButton);
  }

  /**
   * This method initializes altTextCB
   * 
   * @return javax.swing.JCheckBox
   */
  private JLabel getAltTextLabel()
  {
    if (altTextLabel == null)
    {
      altTextLabel = new JLabel();
      altTextLabel.setText(i18n.str("alt_text")); //$NON-NLS-1$
    }
    return altTextLabel;
  }

  private SizeAttributesPanel getSizeAttributesPanel()
  {
    if (sizeAttributesPanel == null)
    {
      sizeAttributesPanel = new SizeAttributesPanel();
    }
    return sizeAttributesPanel;
  }

  /**
   * This method initializes imgUrlField
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getImgUrlField()
  {
    if (imgUrlField == null)
    {
      imgUrlField = new JTextField();
    }
    return imgUrlField;
  }

  /**
   * This method initializes altTextField
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getAltTextField()
  {
    if (altTextField == null)
    {
      altTextField = new JTextField();
    }
    return altTextField;
  }

  /**
   * This method initializes spacerPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getSpacerPanel()
  {
    if (spacerPanel == null)
    {
      spacerPanel = new JPanel();
    }
    return spacerPanel;
  }

  private JCheckBox getTitleCB()
  {
    if (titleCB == null)
    {
      titleCB = new JCheckBox("Title");
      titleCB.addItemListener(new java.awt.event.ItemListener()
      {
        public void itemStateChanged(java.awt.event.ItemEvent e)
        {
          titleTextField.setEditable(titleCB.isSelected());
        }
      });

    }
    return titleCB;
  }

  private JTextField getTitleTextField()
  {
    if (titleTextField == null)
    {
      titleTextField = new JTextField();
      titleTextField.setColumns(255);
    }
    return titleTextField;
  }

  private JButton getImgURLButton()
  {
    if (imgURLButton == null)
    {
      imgURLButton = new JButton("Embed");
      imgURLButton.addActionListener(this);
    }
    return imgURLButton;
  }
  
  protected static List<Node> searchNodes(Node node, String[] values, int index)
  {
    Node n;
    List<Node> result = new Vector<Node>();
    if (index >= values.length)
    {
      return null;
    }
    NodeList list = node.getChildNodes();
    for (int i = 0; i < list.getLength(); i++)
    {
      n = list.item(i);
      if (n.getNodeName().equals(values[index]))
      {
        /* Last entry in path is being searched */
        if (index == values.length - 1)
        {
          result.add(n);
        } else
        {
          List<Node> foundNodes = searchNodes(n, values, index + 1);
          if (foundNodes != null)
          {
            result.addAll(foundNodes);
          }
        }
      }
    }
    return result;
  }

  /**
   * Searches for nodes matching {@code expression} where expression must be an
   * absolute {@code XPath} expression.
   * 
   * @param expression
   *          XPath expression
   * @param node
   *          The root node to search from.
   * @return A {@link java.util.List} containing the list of found nodes or
   *         {@code null} if not found.
   */
  public static List<Node> getTextNodes(String expression, Node node)
  {
    int i;
    List<Node> foundList = new Vector<Node>();
    String values[] = expression.split("/");
    if (node.getNodeName().equals(values[1]) == false)
      return null;
    return searchNodes(node, values, 2);
  }
  
  
  /* Try to extract the "Title" metadata from this image so it can 
   * be added to the title information. */
  public void readAndSetMetadata(File file)
  {
    try
    {
      ImageInputStream iis = ImageIO.createImageInputStream(file);
      Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

      while (readers.hasNext())
      {
        iis.seek(0);
        ImageReader reader = readers.next();

        // attach source to the reader
        reader.setInput(iis, true);

        // read metadata of first image
        IIOMetadata metadata = reader.getImageMetadata(0);

        Node root = metadata.getAsTree("javax_imageio_1.0");
        List<Node> result = getTextNodes("/javax_imageio_1.0/Text/TextEntry",root);
        if (result != null)
        {
            for (int j = 0; j < result.size(); j++)
            {
              NamedNodeMap ma = result.get(j).getAttributes();
              Node n = ma.getNamedItem("keyword");
              if (n != null)
              {
                if (n.getNodeValue().equals("Title"))
                {
                  Node n1 = ma.getNamedItem("value");
                  /* If the value exists, simply add it to the tile information */
                  if (n1 != null)
                  {
                    titleCB.setSelected(true);
                    titleTextField.setEditable(true);
                    titleTextField.setText(n1.getNodeValue());
                    altTextField.setText(n1.getNodeValue());
                  }
                }
              }
              }
            }
        }
      iis.close();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  
  
  /**
   * Called when the button is selected to embed an image into the document.
   */
  @Override
  public void actionPerformed(ActionEvent e)
  {
    final JFileChooser fc = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Images", "png","svg");
    fc.setFileFilter(filter);
    int returnVal = fc.showOpenDialog(SwingUtilities.getWindowAncestor(this));
    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      int c;
      InputStream is = null;
      ByteArrayOutputStream os = null;
      Base64OutputStream b64os = null;
      try
      {
        File file = fc.getSelectedFile();
        is = new FileInputStream(file);
        os = new ByteArrayOutputStream();
        // Line length = 76
        // Lineseparator = 0x0A
        b64os = new Base64OutputStream(os,true,76,new byte[]{0x0A});
        while ((c = is.read()) != -1)
        {
          b64os.write(c);
        }
        b64os.close();
        is.close();
        /* We support PNG and SVG only */
        String output = null;
        if (file.getName().toLowerCase().endsWith(".png"))
        {
          output = "data:image/png;base64," + os.toString("US-ASCII");
          readAndSetMetadata(file);
        } else
        if (file.getName().toLowerCase().endsWith(".svg"))
        {
          output = "data:image/svg+xml;charset=US-ASCII;base64," + os.toString("US-ASCII");
        }
        imgUrlField.setText(output);
      } catch (IOException err)
      {
        UIUtils.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Error loading image.",
            err);
      } finally
      {
        try
        {
        if (os != null)
        {
          os.close();
        }
        } catch (Exception exc)
        {
          exc.printStackTrace();
        }
      }
      //This is where a real application would open the file.
      //      log.append("Opening: " + file.getName() + "." + newline);
    } else
    {
      //      log.append("Open command cancelled by user." + newline);
    }
  }
} //  @jve:decl-index=0:visual-constraint="10,10"

package net.atlanticbb.tantlinger.ui.text.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class LengthAttributePanel extends JPanel implements ItemListener
{
  /** Index for selecting Percent combo box item. */
  public static final int PERCENT_INDEX = 0;
  /** Index for selecting Pixels combo box item. */
  public static final int PIXELS_INDEX = 1;

  /** Contains the information on the type of attribute it is, percent or pixels */
  private JComboBox typeCombo = null;
  private JCheckBox enabledCheckbox = null;
  private JSpinner  valueField = null;

  /**
   * 
   */
  private static final long serialVersionUID = 1485097690297817932L;

  /**
   * Create the panel.
   */
  public LengthAttributePanel(String label)
  {
    setPreferredSize(new Dimension(304, 30));
    setMinimumSize(new Dimension(200, 30));
    setMaximumSize(new Dimension(32767, 30));
    GridBagLayout gridBagLayout = new GridBagLayout();
//    gridBagLayout.columnWidths = new int[]{1, 1, 1};
//    gridBagLayout.rowHeights = new int[] {0};
//    gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0};
 //   gridBagLayout.rowWeights = new double[]{1.0};
    setLayout(gridBagLayout);
    
    typeCombo = new JComboBox();
    typeCombo.setModel(new DefaultComboBoxModel(new String[] {"percent", "pixels"}));
    typeCombo.setSelectedIndex(0);
    GridBagConstraints gbc_typeCombo = new GridBagConstraints();
    gbc_typeCombo.anchor = GridBagConstraints.WEST;
    gbc_typeCombo.insets = new Insets(0, 0, 5, 5);
    gbc_typeCombo.gridx = 2;
    gbc_typeCombo.gridy = 0;
    add(typeCombo, gbc_typeCombo);
    
    enabledCheckbox = new JCheckBox(label);
    GridBagConstraints gbc_enabledCheckbox = new GridBagConstraints();
    gbc_enabledCheckbox.weightx = 1.0;
    gbc_enabledCheckbox.anchor = GridBagConstraints.WEST;
    gbc_enabledCheckbox.insets = new Insets(0, 0, 0, 5);
    gbc_enabledCheckbox.gridx = 0;
    gbc_enabledCheckbox.gridy = 0;
    add(enabledCheckbox, gbc_enabledCheckbox);
    
    valueField = new JSpinner();
    valueField.setModel(new SpinnerNumberModel(1, 0, 100, 1));
    GridBagConstraints gbc_valueField = new GridBagConstraints();
    gbc_valueField.insets = new Insets(0, 0, 5, 5);
    gbc_valueField.gridx = 1;
    gbc_valueField.gridy = 0;
    add(valueField, gbc_valueField);
    this.setSize(225, 30);
    this.setPreferredSize(new java.awt.Dimension(225,30));
    this.setMinimumSize(getPreferredSize());
    this.setMaximumSize(getPreferredSize());
    
    /* Default state */
    valueField.setEnabled(false);
    typeCombo.setEnabled(false);
    enabledCheckbox.setSelected(false);

    /* Add item listener */
    enabledCheckbox.addItemListener(this);
    
    
  }

  @Override
  public void itemStateChanged(ItemEvent e)
  {
    Object source = e.getItemSelectable();
    
    if (source == enabledCheckbox)
    {
      if (e.getStateChange() == ItemEvent.DESELECTED)
      {
        valueField.setEnabled(false);
        typeCombo.setEnabled(false);
      } else
      if (e.getStateChange() == ItemEvent.SELECTED)
      {
        valueField.setEnabled(true);
        typeCombo.setEnabled(true);
      }
    }
  }
  
  public void setSelected(boolean b)
  {
    if (b)
    {
      enabledCheckbox.setSelected(true);
    } else
    {
      enabledCheckbox.setSelected(false);
    }
  }
  
  public boolean isSelected()
  {
    return enabledCheckbox.isSelected();
  }
  
  public void setSelectedIndex(int index)
  {
    typeCombo.setSelectedIndex(index);
  }
  
  public int getSelectedIndex()
  {
    return typeCombo.getSelectedIndex();
  }
  
  public void setValue(Integer value)
  {
    valueField.getModel().setValue(value);
  }
  
  public Integer getValue()
  {
    return (Integer)valueField.getModel().getValue();
  }
  
  
  /** Update the attributes from the specified length panel information. */
  
  /** Update the attributes from the specified length panel information.
   * 
   * @param attributes The attribute map that will be modified.
   * @param attribute The actual attribute name in the map.
   * @param panel The panel that contains the visual controls.
   */
  public static void updateAttribsFromLengthPanel(Map<String,String> attributes,String attribute, LengthAttributePanel panel)
  {
    if(panel.isSelected())
    {
        String w = panel.getValue().toString();
        if (panel.getSelectedIndex() == LengthAttributePanel.PERCENT_INDEX)
        {
            w += "%";
           attributes.put(attribute, w);
        }
        else
          attributes.put(attribute, w);
    } else
    {
      attributes.remove(attribute);
    }
  }
  

  /** Update the length panel information from the specified attribute. */
  public static void updateLengthPanelFromAttribs(Map<String, String> attributes, String attribute, LengthAttributePanel panel)
  {
    if(attributes.containsKey(attribute))
    {
        panel.setSelected(true);
        String w = attributes.get(attribute).toString();
        if(w.endsWith("%"))                             
            w = w.substring(0, w.length() - 1);            
        else
            panel.setSelectedIndex(LengthAttributePanel.PIXELS_INDEX);
        
        try
        {
              panel.setValue(new Integer(w));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }            
    }
    else
    {
          panel.setSelected(false);
    }
  }

}

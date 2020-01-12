/*
 * Created on Dec 22, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;

import com.optimasc.text.html.HTMLHelper;

public class TableAttributesPanel extends HTMLAttributeEditorPanel
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String ALIGNMENTS[] = {"left", "center", "right"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    
    private JCheckBox alignCB = null;
    private JCheckBox borderCB = null;
    private JSpinner borderField = null;
    private JComboBox alignCombo = null;
    private BGColorPanel bgPanel = null;
    private JPanel expansionPanel = null;
    private LengthAttributePanel widthPanel = null;
    private LengthAttributePanel cellPaddingPanel = null;
    private LengthAttributePanel cellSpacingPanel = null;
       
    
    /**
     * This is the default constructor
     */
    public TableAttributesPanel()
    {
        this(new Hashtable<String, String>());
        //super();
        //initialize();
        //Hashtable ht = new Hashtable();
        //ht.put("width", "100%");
        //ht.put("border", "1");        
        //setComponentStates(ht);
    }
    
    public TableAttributesPanel(Hashtable<String, String> attribs)
    {
        super(attribs);
        initialize();
        updateComponentsFromAttribs();        
    }
    
    public void updateComponentsFromAttribs()
    {
        if(attribs.containsKey(HTMLHelper.Attributes.ALIGN)) //$NON-NLS-1$
        {
            alignCB.setSelected(true);
            alignCombo.setEnabled(true);
            alignCombo.setSelectedItem(attribs.get(HTMLHelper.Attributes.ALIGN)); //$NON-NLS-1$
        }
        else
        {
            alignCB.setSelected(false);
            alignCombo.setEnabled(false);
        }
        
        if(attribs.containsKey(HTMLHelper.Attributes.BORDER)) //$NON-NLS-1$
        {
            borderCB.setSelected(true);
            borderField.setEnabled(true);
            try
            {
                borderField.getModel().setValue(
                    new Integer(attribs.get(HTMLHelper.Attributes.BORDER).toString())); //$NON-NLS-1$
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            borderCB.setSelected(false);
            borderField.setEnabled(false);
        }

        LengthAttributePanel.updateLengthPanelFromAttribs(attribs, HTMLHelper.Attributes.WIDTH, widthPanel);
        LengthAttributePanel.updateLengthPanelFromAttribs(attribs, HTMLHelper.Attributes.CELLPADDING, cellPaddingPanel);
        LengthAttributePanel.updateLengthPanelFromAttribs(attribs, HTMLHelper.Attributes.CELLSPACING, cellSpacingPanel);
        
        
        if(attribs.containsKey(HTMLHelper.Attributes.BGCOLOR)) //$NON-NLS-1$
        {
            bgPanel.setSelected(true);
            bgPanel.setColor(attribs.get(HTMLHelper.Attributes.BGCOLOR).toString()); //$NON-NLS-1$
        }
        else
        {
            bgPanel.setSelected(false);
        }
    }
    
    
    
    
    public void updateAttribsFromComponents()
    {
        LengthAttributePanel.updateAttribsFromLengthPanel(attribs, HTMLHelper.Attributes.WIDTH, widthPanel);
        LengthAttributePanel.updateAttribsFromLengthPanel(attribs, HTMLHelper.Attributes.CELLSPACING, cellSpacingPanel);
        LengthAttributePanel.updateAttribsFromLengthPanel(attribs, HTMLHelper.Attributes.CELLPADDING, cellPaddingPanel);
        
        
        if(alignCB.isSelected())
            attribs.put(HTMLHelper.Attributes.ALIGN, alignCombo.getSelectedItem().toString()); //$NON-NLS-1$
        else
            attribs.remove(HTMLHelper.Attributes.ALIGN); //$NON-NLS-1$
        
        if(borderCB.isSelected())
            attribs.put(HTMLHelper.Attributes.BORDER,  //$NON-NLS-1$
                borderField.getModel().getValue().toString());
        else
            attribs.remove(HTMLHelper.Attributes.BORDER); //$NON-NLS-1$
        
        if(bgPanel.isSelected())
            attribs.put(HTMLHelper.Attributes.BGCOLOR, bgPanel.getColor()); //$NON-NLS-1$
        else
            attribs.remove(HTMLHelper.Attributes.BGCOLOR); //$NON-NLS-1$
    }
    
    public void setComponentStates(Hashtable attribs)
    {
        if(attribs.containsKey(HTMLHelper.Attributes.WIDTH)) //$NON-NLS-1$
        {
            widthPanel.setSelected(true);
            String w = attribs.get(HTMLHelper.Attributes.WIDTH).toString(); //$NON-NLS-1$
            if(w.endsWith("%"))                             //$NON-NLS-1$
                w = w.substring(0, w.length() - 1);            
            else
                widthPanel.setSelectedIndex(LengthAttributePanel.PIXELS_INDEX);
            try
            {
                widthPanel.setValue(new Integer(w));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }            
        }
        else
        {
              widthPanel.setSelected(false);
        }

        
        if(attribs.containsKey(HTMLHelper.Attributes.ALIGN)) //$NON-NLS-1$
        {
            alignCB.setSelected(true);
            alignCombo.setSelectedItem(attribs.get(HTMLHelper.Attributes.ALIGN)); //$NON-NLS-1$
        }
        else
        {
            alignCB.setSelected(false);
            alignCombo.setEnabled(false);
        }
        
        if(attribs.containsKey(HTMLHelper.Attributes.BORDER)) //$NON-NLS-1$
        {
            borderCB.setSelected(true);
            try
            {
                borderField.getModel().setValue(
                    new Integer(attribs.get(HTMLHelper.Attributes.BORDER).toString())); //$NON-NLS-1$
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            borderCB.setSelected(false);
            borderField.setEnabled(false);
        }
        
        if(attribs.containsKey(HTMLHelper.Attributes.CELLPADDING)) //$NON-NLS-1$
        {
            cellPaddingPanel.setSelected(true);
            try
            {
                cellPaddingPanel.setValue(
                    new Integer(attribs.get(HTMLHelper.Attributes.CELLPADDING).toString())); //$NON-NLS-1$
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
           cellPaddingPanel.setSelected(false);
        }
        
        if(attribs.containsKey(HTMLHelper.Attributes.CELLSPACING))
        {
            cellSpacingPanel.setSelected(true);
            String w = attribs.get(HTMLHelper.Attributes.CELLSPACING).toString();
            if(w.endsWith("%"))                             
                w = w.substring(0, w.length() - 1);            
            else
                cellSpacingPanel.setSelectedIndex(LengthAttributePanel.PIXELS_INDEX);
            try
            {
                cellSpacingPanel.setValue(new Integer(w));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }            
        }
        else
        {
              cellSpacingPanel.setSelected(false);
        }
        
        if(attribs.containsKey(HTMLHelper.Attributes.BGCOLOR)) //$NON-NLS-1$
        {
            bgPanel.setSelected(true);
            bgPanel.setColor(attribs.get(HTMLHelper.Attributes.BGCOLOR).toString()); //$NON-NLS-1$
        }
        else
        {
            bgPanel.setSelected(false);
        }
        
    }
    
    
    protected LengthAttributePanel getCellSpacingPanel()
    {
      if (cellSpacingPanel == null)
      {
        cellSpacingPanel =  new LengthAttributePanel("Cell spacing");
      }
      return cellSpacingPanel;
    }
    
    protected LengthAttributePanel getCellPaddingPanel()
    {
      if (cellPaddingPanel == null)
      {
        cellPaddingPanel =  new LengthAttributePanel("Cell padding");
      }
      return cellPaddingPanel;
    }
    
        
        
    protected LengthAttributePanel getWidthPanel()
    {
      if(widthPanel == null)
      {
          widthPanel = new LengthAttributePanel("Width");
      }
      return widthPanel;
      
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
      GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
      gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints12.gridx = 0;
      gridBagConstraints12.anchor = GridBagConstraints.WEST;
      gridBagConstraints12.gridwidth = 4;
      gridBagConstraints12.weightx = 0.0;
      gridBagConstraints12.gridy = 6;
      
        GridBagConstraints gridBagConstraintsbgPanel = new GridBagConstraints();
        gridBagConstraintsbgPanel.insets = new Insets(0, 0, 5, 5);
        gridBagConstraintsbgPanel.gridx = 0;
        gridBagConstraintsbgPanel.gridwidth = 3;
        gridBagConstraintsbgPanel.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraintsbgPanel.weighty = 0.0;
        gridBagConstraintsbgPanel.gridy = 3;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 0.0;
        gridBagConstraints4.gridwidth = 2;
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints4.insets = new java.awt.Insets(0,0,5,15);
        gridBagConstraints4.gridx = 1;
        GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        gridBagConstraints10.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints10.gridy = 5;
        gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints10.insets = new Insets(0, 0, 10, 5);
        gridBagConstraints10.gridx = 1;
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.gridx = 0;
        gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints9.insets = new Insets(0, 0, 10, 5);
        gridBagConstraints9.gridy = 5;
        gridBagConstraints9.gridwidth = 3;
        
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints8.gridy = 2;
        gridBagConstraints8.weightx = 0.0;
        gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints8.insets = new java.awt.Insets(0,0,10,15);
        gridBagConstraints8.gridx = 1;
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.gridx = 0;
        gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints7.insets = new Insets(0, 0, 10, 5);
        gridBagConstraints7.gridy = 2;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints6.gridy = 4;
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints6.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints6.gridx = 1;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints5.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints5.gridy = 4;
        gridBagConstraints5.gridwidth = 3;
        
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints3.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints3.gridy = 1;
        GridBagConstraints gbc_widthCombo = new GridBagConstraints();
        gbc_widthCombo.weightx = 1.0;
        gbc_widthCombo.gridy = 0;
        gbc_widthCombo.anchor = java.awt.GridBagConstraints.WEST;
        gbc_widthCombo.gridwidth = 2;
        gbc_widthCombo.insets = new Insets(0, 0, 10, 0);
        gbc_widthCombo.gridx = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 0.0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new Insets(0, 0, 10, 5);
        gridBagConstraints1.gridx = 1;
        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        GridBagLayout gridBagLayout = new GridBagLayout();
//        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        this.setLayout(gridBagLayout);
        this.setSize(320, 200);
        this.setPreferredSize(new Dimension(300, 200));
        
        this.add(getWidthPanel(),gridBagConstraints);
        this.add(getAlignCB(), gridBagConstraints3);

        this.add(getCellSpacingPanel(),gridBagConstraints5);
        
        this.add(getBorderCB(), gridBagConstraints7);
        this.add(getBorderField(), gridBagConstraints8);
        GridBagConstraints gbc_cellSpacingCombo = new GridBagConstraints();
        gbc_cellSpacingCombo.gridwidth = 2;
        gbc_cellSpacingCombo.anchor = GridBagConstraints.WEST;
        gbc_cellSpacingCombo.insets = new Insets(0, 0, 5, 5);
        gbc_cellSpacingCombo.gridx = 2;
        gbc_cellSpacingCombo.gridy = 4;
        
        this.add(getCellPaddingPanel(), gridBagConstraints9);
        this.add(getAlignCombo(), gridBagConstraints4);
        this.add(getBGPanel(), gridBagConstraintsbgPanel);
        GridBagConstraints gbc_cellPaddingCombo = new GridBagConstraints();
        gbc_cellPaddingCombo.gridwidth = 2;
        gbc_cellPaddingCombo.anchor = GridBagConstraints.WEST;
        gbc_cellPaddingCombo.insets = new Insets(0, 0, 10, 5);
        gbc_cellPaddingCombo.gridx = 2;
        gbc_cellPaddingCombo.gridy = 5;
        add(getExpansionPanel(), gridBagConstraints12);
        
    }


    /**
     * This method initializes alignCB	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getAlignCB()
    {
        if(alignCB == null)
        {
            alignCB = new JCheckBox();
            alignCB.setText(i18n.str("align")); //$NON-NLS-1$
            alignCB.addItemListener(new java.awt.event.ItemListener()
            {
                public void itemStateChanged(java.awt.event.ItemEvent e)
                {
                    alignCombo.setEnabled(alignCB.isSelected());
                }
            });
        }
        return alignCB;
    }



    /**
     * This method initializes borderCB	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getBorderCB()
    {
        if(borderCB == null)
        {
            borderCB = new JCheckBox();
            borderCB.setText(i18n.str("border")); //$NON-NLS-1$
            borderCB.addItemListener(new java.awt.event.ItemListener()
            {
                public void itemStateChanged(java.awt.event.ItemEvent e)
                {
                    borderField.setEnabled(borderCB.isSelected());
                }
            });
        }
        return borderCB;
    }

    /**
     * This method initializes borderField	
     * 	
     * @return javax.swing.JSpinner	
     */
    private JSpinner getBorderField()
    {
        if(borderField == null)
        {
            borderField = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));
            
        }
        return borderField;
    }


    /**
     * This method initializes alignCombo	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getAlignCombo()
    {
        if(alignCombo == null)
        {
            alignCombo = new JComboBox(ALIGNMENTS);
        }
        return alignCombo;
    }

    /**
     * This method initializes tempPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getBGPanel()
    {
        if(bgPanel == null)
        {
            bgPanel = new BGColorPanel();
           
        }
        return bgPanel;
    }
    
  
  /**
   * This method initializes expansionPanel   
   *  
   * @return javax.swing.JPanel   
   */
  private JPanel getExpansionPanel()
  {
      if(expansionPanel == null)
      {
          expansionPanel = new JPanel();
      }
      return expansionPanel;
  }
  
}  //  @jve:decl-index=0:visual-constraint="16,10"

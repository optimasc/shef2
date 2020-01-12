/*
 * Created on Jan 17, 2006
 *
 */
package net.atlanticbb.tantlinger.ui.text.dialogs;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.text.html.HTML;

import com.optimasc.text.html.HTMLHelper;

import net.atlanticbb.tantlinger.ui.text.TextEditPopupManager;

import java.util.*;
import java.awt.Insets;
import java.awt.Dimension;


public class StyleAttributesPanel extends HTMLAttributeEditorPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JLabel classLabel = null;
    private JLabel idLabel = null;
    private JTextField classField = null;
    private JTextField idField = null;
    private JTextField titleField;
    
    /**
     * This method initializes 
     * 
     */
    public StyleAttributesPanel() 
    {
    	this(new Hashtable<String,String>());
    }
    
    public StyleAttributesPanel(Hashtable<String,String> attr) 
    {
        super();
        initialize();
        setAttributes(attr);
        
        this.updateComponentsFromAttribs();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.insets = new java.awt.Insets(0,0,5,0);
        gridBagConstraints4.gridx = 1;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.insets = new java.awt.Insets(0,0,5,0);
        gridBagConstraints3.weighty = 0.0;
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(0,0,5,5);
        gridBagConstraints1.gridy = 1;
        idLabel = new JLabel();
        idLabel.setText(i18n.str("id")); //$NON-NLS-1$
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0,0,5,5);
        gridBagConstraints.gridy = 0;
        classLabel = new JLabel();
        classLabel.setText(i18n.str("class")); //$NON-NLS-1$
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[]{0.0, 1.0};
        this.setLayout(gridBagLayout);
        this.setSize(new java.awt.Dimension(210,60));
        this.setPreferredSize(new Dimension(210, 82));
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
        this.add(classLabel, gridBagConstraints);
        this.add(idLabel, gridBagConstraints1);
        this.add(getClassField(), gridBagConstraints3);
        this.add(getIdField(), gridBagConstraints4);
        
        TextEditPopupManager popupMan = TextEditPopupManager.getInstance();
        popupMan.registerJTextComponent(classField);
        popupMan.registerJTextComponent(idField);

        JLabel titleLabel = new JLabel("Title");
        GridBagConstraints gbc_titleLabel = new GridBagConstraints();
        gbc_titleLabel.anchor = GridBagConstraints.WEST;
        gbc_titleLabel.insets = new Insets(0, 0, 0, 5);
        gbc_titleLabel.gridx = 0;
        gbc_titleLabel.gridy = 2;
        add(titleLabel, gbc_titleLabel);
        
        titleField = new JTextField();
        GridBagConstraints gbc_titleField = new GridBagConstraints();
        gbc_titleField.fill = GridBagConstraints.HORIZONTAL;
        gbc_titleField.gridx = 1;
        gbc_titleField.gridy = 2;
        add(titleField, gbc_titleField);
        titleField.setColumns(10);
        
        popupMan.registerJTextComponent(titleField);
    }

    public void updateComponentsFromAttribs()
    {
        if(attribs.containsKey(HTMLHelper.Attributes.CLASS)) //$NON-NLS-1$
            classField.setText(attribs.get(HTMLHelper.Attributes.CLASS).toString()); //$NON-NLS-1$
        else
            classField.setText(""); //$NON-NLS-1$
        
        if(attribs.containsKey(HTMLHelper.Attributes.ID)) //$NON-NLS-1$
            idField.setText(attribs.get(HTMLHelper.Attributes.ID).toString()); //$NON-NLS-1$
        else
            idField.setText("");         //$NON-NLS-1$
        
        if(attribs.containsKey(HTMLHelper.Attributes.TITLE)) //$NON-NLS-1$
          titleField.setText(attribs.get(HTMLHelper.Attributes.TITLE).toString()); //$NON-NLS-1$
      else
          titleField.setText("");         //$NON-NLS-1$
        
    }

    public void updateAttribsFromComponents()
    {
        if(!classField.getText().equals("")) //$NON-NLS-1$
            attribs.put(HTMLHelper.Attributes.CLASS, classField.getText()); //$NON-NLS-1$
        else
            attribs.remove(HTMLHelper.Attributes.CLASS); //$NON-NLS-1$
        
        if(!idField.getText().equals("")) //$NON-NLS-1$
            attribs.put(HTMLHelper.Attributes.ID, idField.getText()); //$NON-NLS-1$
        else
            attribs.remove(HTMLHelper.Attributes.ID); //$NON-NLS-1$
        
        if(!titleField.getText().equals("")) //$NON-NLS-1$
          attribs.put(HTMLHelper.Attributes.TITLE, titleField.getText()); //$NON-NLS-1$
      else
          attribs.remove(HTMLHelper.Attributes.TITLE); //$NON-NLS-1$
        

    }

    /**
     * This method initializes classField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getClassField()
    {
        if(classField == null)
        {
            classField = new JTextField();
        }
        return classField;
    }

    /**
     * This method initializes idField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getIdField()
    {
        if(idField == null)
        {
            idField = new JTextField();
        }
        return idField;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"

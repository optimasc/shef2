package net.atlanticbb.tantlinger.ui.text.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import java.awt.Insets;

public class TableLayoutPanel extends JPanel
{

    private static final long serialVersionUID = 1L;
    private JLabel rowsLabel = null;
    private JCheckBox tableHeaderCheckBox = null;
    private JLabel colsLabel = null;
    private int iRows, iCols;
    private JSpinner rowsField = null;
    private JSpinner colsField = null;
    
    /**
     * This is the default constructor
     */
    public TableLayoutPanel()
    {
        this(1, 1);
    }
    
    public TableLayoutPanel(int r, int c)
    {
        super();
        iRows = (r > 0) ? r : 1;
        iCols = (c > 0) ? c : 1;
        initialize();
    }
    
    public int getRows()
    {
        return Integer.parseInt(rowsField.getModel().getValue().toString());
    }
    
    public int getColumns()
    {
        return Integer.parseInt(colsField.getModel().getValue().toString());
    }
    
    public boolean getTableHeader()
    {
        return tableHeaderCheckBox.isSelected(); 
    }
    

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints7.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints7.gridy = 0;
        gridBagConstraints7.weightx = 1.0;
        gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints7.gridx = 3;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints6.gridy = 0;
        gridBagConstraints6.weightx = 0.0;
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints6.insets = new Insets(0, 0, 5, 15);
        gridBagConstraints6.gridx = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints1.gridy = 0;
        colsLabel = new JLabel();
        colsLabel.setText("columns");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridy = 0;
        rowsLabel = new JLabel();
        rowsLabel.setText("rows");
        this.setLayout(new GridBagLayout());
        this.setSize(330, 60);
        this.setPreferredSize(new java.awt.Dimension(330,60));
        //this.setMaximumSize(this.getPreferredSize());
        this.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "layout", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null), javax.swing.BorderFactory.createEmptyBorder(5,5,5,5))); //$NON-NLS-1$
        this.add(rowsLabel, gridBagConstraints);
        this.add(colsLabel, gridBagConstraints1);
        this.add(getRowsField(), gridBagConstraints6);
        this.add(getColsField(), gridBagConstraints7);
        
        tableHeaderCheckBox = new JCheckBox("Table header");
        tableHeaderCheckBox.setToolTipText("Indicate if the first row is a table header.");
        GridBagConstraints gbc_tableHeaderCheckBox = new GridBagConstraints();
        gbc_tableHeaderCheckBox.anchor = GridBagConstraints.WEST;
        gbc_tableHeaderCheckBox.insets = new Insets(0, 0, 0, 5);
        gbc_tableHeaderCheckBox.gridx = 4;
        gbc_tableHeaderCheckBox.gridy = 0;
        add(tableHeaderCheckBox, gbc_tableHeaderCheckBox);
    }

    /**
     * This method initializes rowsField    
     *  
     * @return javax.swing.JSpinner 
     */
    private JSpinner getRowsField()
    {
        if(rowsField == null)
        {
            rowsField = new JSpinner(new SpinnerNumberModel(iRows, 1, 999, 1));            
        }
        return rowsField;
    }

    /**
     * This method initializes colsField    
     *  
     * @return javax.swing.JSpinner
     */
    private JSpinner getColsField()
    {
        if(colsField == null)
        {
            colsField = new JSpinner(new SpinnerNumberModel(iCols, 1, 999, 1));     
        }
        return colsField;
    }

  }

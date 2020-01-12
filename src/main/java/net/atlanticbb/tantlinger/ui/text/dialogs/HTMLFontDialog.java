/*
 * Created on Jan 17, 2006
 *
 */
package net.atlanticbb.tantlinger.ui.text.dialogs;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.*;
import java.util.Vector;
import javax.swing.JPanel;

import com.optimasc.text.html.FontHelper;
import com.optimasc.text.html.HTMLHelper;

import net.atlanticbb.tantlinger.i18n.I18n;
import net.atlanticbb.tantlinger.ui.UIUtils;





public class HTMLFontDialog extends HTMLOptionDialog
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.getInstance("net.atlanticbb.tantlinger.ui.text.dialogs");
    
    private static Icon icon = UIUtils.getIcon(UIUtils.X48, "fontsize.png"); //$NON-NLS-1$
    private static String title = i18n.str("font"); //$NON-NLS-1$
    private static String desc = i18n.str("font_desc"); //$NON-NLS-1$
    
    /* Font sizes indexes */
    protected static final int SMALL = 0;
    protected static final int MEDIUM = 1;
    protected static final int LARGE = 2;    
    
    private static final String SIZES[] =
    {
        "small",
        "medium",
        "large"
    };    
    
    private JPanel jContentPane = null;
    private JLabel fontLabel = null;
    private JComboBox fontCombo = null;
    private JComboBox sizeCombo = null;
    private JPanel stylePanel = null;
    private JCheckBox boldCB = null;
    private JCheckBox italicCB = null;
    private JCheckBox sCB = null;
    private JPanel previewPanel = null;
    private JLabel previewLabel = null;
    private JPanel spacerPanel = null;
    
    private String text = "";   //$NON-NLS-1$
    
    public HTMLFontDialog(Frame parent, String text)
    {
        super(parent, title, desc, icon);
        initialize(text);
    }
    
    public HTMLFontDialog(Dialog parent, String text)
    {
        super(parent, title, desc, icon);
        initialize(text);
    }
    
    public boolean isBold()
    {
        return boldCB.isSelected();
    }
    
    public boolean isItalic()
    {
        return italicCB.isSelected();
    }
    
    public boolean isStrikeThrough()
    {
        return sCB.isSelected();
    }
    
    public void setBold(boolean b)
    {
        boldCB.setSelected(b);
        updatePreview();
    }
    
    public void setItalic(boolean b)
    {
        italicCB.setSelected(b);
        updatePreview();
    }
    
    public void setStrikethrough(boolean b)
    {
        sCB.setSelected(b);
        updatePreview();
    }
    
    public void setFontName(String fn)
    {
        fontCombo.setSelectedItem(fn);
        updatePreview();
    }
    
    /** Returns the selected font, or null if the "default" font is 
     *  selected.
     * @return
     */
    public String getFontName()
    {
        if (fontCombo.getSelectedItem().equals(FontHelper.FONT_FAMILY_LABEL_DEFAULT))
        {
          return null;
        }
        return fontCombo.getSelectedItem().toString();
    }
    
    public String getFontSize()
    {
        return sizeCombo.getSelectedItem().toString();
    }
    
    public void setFontSize(String size)
    {              
        sizeCombo.setSelectedItem(size);
        updatePreview();
    }
    
    
    public String getHTML()
    {
        String fontSize = "";
        
        switch (sizeCombo.getSelectedIndex())
        {
          case SMALL:
            fontSize = "small";
            break;
          case LARGE:
            fontSize = "big";
        }

        String html = "<html><span "; //$NON-NLS-1$
        if (fontCombo.getSelectedIndex()!=0)
        {
          html += "style=\"font-family:" + HTMLHelper.toCSSFontFamily(fontCombo.getSelectedItem().toString()) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        html += "\">"; //$NON-NLS-1$ //$NON-NLS-2$
        if(boldCB.isSelected())
            html += "<b>"; //$NON-NLS-1$
        if(italicCB.isSelected())
            html += "<i>"; //$NON-NLS-1$
        if(sCB.isSelected())
            html += "<s>"; //$NON-NLS-1$
        
        if (fontSize.length()>0)
        {
          html += "<" + fontSize + ">";
        }
        html += text;
        
        if (fontSize.length()>0)
        {
          html += "</" + fontSize + ">";
        }
        if(sCB.isSelected())
          html += "</s>"; //$NON-NLS-1$
        if(italicCB.isSelected())
          html += "</i>"; //$NON-NLS-1$
        if(boldCB.isSelected())
            html += "</b>"; //$NON-NLS-1$

        
        
        html += "</span></html>";         //$NON-NLS-1$
        return html;
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize(String text)
    {        
        setContentPane(getJContentPane());
        pack();
        setSize(285, getHeight());
        setResizable(false);
        sizeCombo.setSelectedIndex(MEDIUM);
        this.text = text;
    }
    
    private void updatePreview()
    {
        int style = Font.PLAIN;
        if(boldCB.isSelected())
            style += Font.BOLD;
        if(italicCB.isSelected())
            style += Font.ITALIC;
        
        previewLabel.setBorder(null);
        previewLabel.setText(getHTML());
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane()
    {
        if(jContentPane == null)
        {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.gridwidth = 3;
            gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.insets = new java.awt.Insets(5,0,0,0);
            gridBagConstraints21.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints2.gridx = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints.gridy = 0;
            fontLabel = new JLabel();
            fontLabel.setText(i18n.str("font")); //$NON-NLS-1$
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
            jContentPane.add(fontLabel, gridBagConstraints);
            jContentPane.add(getFontCombo(), gridBagConstraints1);
            jContentPane.add(getSizeCombo(), gridBagConstraints2);
            jContentPane.add(getStylePanel(), gridBagConstraints21);
            
            sizeCombo.setSelectedItem(new Integer(previewLabel.getFont().getSize()));
        }
        return jContentPane;
    }

    /**
     * This method initializes fontCombo	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getFontCombo()
    {        
        if(fontCombo == null)
        {            
            GraphicsEnvironment gEnv = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            String envfonts[] = gEnv.getAvailableFontFamilyNames();
            Vector fonts = new Vector();
            fonts.add(FontHelper.FONT_FAMILY_LABEL_DEFAULT);
            fonts.add(FontHelper.FONT_FAMILY_LABEL_SERIF);
            fonts.add(FontHelper.FONT_FAMILY_LABEL_SANS_SERIF);
            fonts.add(FontHelper.FONT_FAMILY_LABEL_MONOSPACE);
            
            fontCombo = new JComboBox(fonts);
            fontCombo.addItemListener(new java.awt.event.ItemListener()
            {
                public void itemStateChanged(java.awt.event.ItemEvent e)
                {
                    updatePreview();
                }
            });
        }
        return fontCombo;
    }

    /**
     * This method initializes sizeCombo	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getSizeCombo()
    {
        if(sizeCombo == null)
        {
            sizeCombo = new JComboBox(SIZES);
            sizeCombo.setSelectedItem(new Integer(12));
            sizeCombo.addItemListener(new java.awt.event.ItemListener()
            {
                public void itemStateChanged(java.awt.event.ItemEvent e)
                {
                    updatePreview();
                }
            });
        }
        return sizeCombo;
    }

    /**
     * This method initializes stylePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStylePanel()
    {
        if(stylePanel == null)
        {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints7.weighty = 1.0;
            gridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints7.gridy = 3;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridwidth = 1;
            gridBagConstraints6.gridheight = 4;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.weighty = 1.0;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints6.gridy = 0;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints5.weighty = 0.0;
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints4.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints4.gridy = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints3.insets = new java.awt.Insets(5,0,0,5);
            gridBagConstraints3.gridy = 0;
            stylePanel = new JPanel();
            stylePanel.setLayout(new GridBagLayout());
            stylePanel.add(getBoldCB(), gridBagConstraints3);
            stylePanel.add(getItalicCB(), gridBagConstraints4);
            stylePanel.add(getUlCB(), gridBagConstraints5);
            stylePanel.add(getPreviewPanel(), gridBagConstraints6);
            stylePanel.add(getSpacerPanel(), gridBagConstraints7);
        }
        return stylePanel;
    }

    /**
     * This method initializes boldCB	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getBoldCB()
    {
        if(boldCB == null)
        {
            boldCB = new JCheckBox();
            boldCB.setText(i18n.str("bold")); //$NON-NLS-1$
            boldCB.addItemListener(new java.awt.event.ItemListener()
            {
                public void itemStateChanged(java.awt.event.ItemEvent e)
                {
                    updatePreview();
                }
            });
        }
        return boldCB;
    }

    /**
     * This method initializes italicCB	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getItalicCB()
    {
        if(italicCB == null)
        {
            italicCB = new JCheckBox();
            italicCB.setText(i18n.str("italic")); //$NON-NLS-1$
            italicCB.addItemListener(new java.awt.event.ItemListener()
            {
                public void itemStateChanged(java.awt.event.ItemEvent e)
                {
                    updatePreview();
                }
            });
        }
        return italicCB;
    }

    /**
     * This method initializes ulCB	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getUlCB()
    {
        if(sCB == null)
        {
            sCB = new JCheckBox();
            sCB.setText(i18n.str("strikethrough")); //$NON-NLS-1$
            sCB.addItemListener(new java.awt.event.ItemListener()
            {
                public void itemStateChanged(java.awt.event.ItemEvent e)
                {
                    updatePreview();
                }
            });
        }
        return sCB;
    }

    /**
     * This method initializes previewPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getPreviewPanel()
    {
        if(previewPanel == null)
        {            
            previewLabel = new JLabel();
            previewLabel.setText("AaBbYyZz"); //$NON-NLS-1$
            JPanel spacer = new JPanel(new FlowLayout(FlowLayout.LEFT));
            spacer.setBackground(Color.WHITE);
            spacer.add(previewLabel);
            previewPanel = new JPanel();
            previewPanel.setLayout(new BorderLayout());
            previewPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, i18n.str("preview"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null), javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED))))); //$NON-NLS-1$
            previewPanel.setPreferredSize(new java.awt.Dimension(90,100));
            previewPanel.setMaximumSize(previewPanel.getPreferredSize());
            previewPanel.setMinimumSize(previewPanel.getPreferredSize());
            previewPanel.add(spacer, null);
        }
        return previewPanel;
    }

    /**
     * This method initializes spacerPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSpacerPanel()
    {
        if(spacerPanel == null)
        {
            spacerPanel = new JPanel();
        }
        return spacerPanel;
    }
    
    

}  //  @jve:decl-index=0:visual-constraint="48,14"

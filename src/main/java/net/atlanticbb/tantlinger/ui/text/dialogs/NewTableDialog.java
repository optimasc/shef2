/*
 * Created on Dec 24, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;

import com.optimasc.text.html.HTMLHelper;

import net.atlanticbb.tantlinger.i18n.I18n;
import net.atlanticbb.tantlinger.ui.OptionDialog;
import net.atlanticbb.tantlinger.ui.UIUtils;

public class NewTableDialog extends OptionDialog
{
  
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    

    private static final I18n i18n = I18n.getInstance("net.atlanticbb.tantlinger.ui.text.dialogs");
    
    private TableLayoutPanel layoutPanel = new TableLayoutPanel();
    private TableAttributesPanel propsPanel;
    private static Icon icon = UIUtils.getIcon(UIUtils.X48, "table.png"); //$NON-NLS-1$
    
    public NewTableDialog(Frame parent)
    {
        super(parent, i18n.str("new_table"), i18n.str("new_table_desc"), icon);         //$NON-NLS-1$ //$NON-NLS-2$
        init();
    }
    
    /**
     * @wbp.parser.constructor
     */
    public NewTableDialog(Dialog parent)
    {
        super(parent, i18n.str("new_table"), i18n.str("new_table_desc"), icon);         //$NON-NLS-1$ //$NON-NLS-2$
        init();
    }
    
    private void init()
    {
        //default attributes
        Hashtable<String,String> ht = new Hashtable<String, String>();
        ht.put(HTMLHelper.Attributes.WIDTH, "100%"); //$NON-NLS-1$ //$NON-NLS-2$
        propsPanel = new TableAttributesPanel();
        propsPanel.setAttributes(ht);
        
        propsPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(i18n.str("properties")),  //$NON-NLS-1$
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));        
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(layoutPanel, BorderLayout.NORTH);
        mainPanel.add(propsPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(mainPanel);
        setSize(new Dimension(360, 440));
        setResizable(false);        
    }
    
    public String getHTML()
    {
        String html = "<table"; //$NON-NLS-1$
        Map<String,String> attribs = propsPanel.getAttributes();
        String s = null;
        Object o = null;
        String styleValues = "";
        
        /* CSS Style attributes */
        o = attribs.get(HTMLHelper.Attributes.ALIGN);
        if (o != null)
        {
          s = o.toString();
          if (s.equals("center"))
          {
            styleValues = styleValues + "margin-left:auto; margin-right:auto;";
          } else
          if (s.equals("left"))
          {
            styleValues = styleValues + "float:left;";
          } else
          if (s.equals("right"))
          {
            styleValues = styleValues + "float:right;";
          }
        }
        
        o = attribs.get(HTMLHelper.Attributes.BGCOLOR);
        if (o != null)
        {
          s = o.toString();
          if (s.length() > 0)
          {
            styleValues = styleValues + "background-color:"+s+";";
          }
        }        
        
        /* Table attributes */
        for(Iterator e = attribs.keySet().iterator(); e.hasNext();)
        {
            String key = e.next().toString();
            /* These are CSS Styles instead. */
            if (key.equals(HTMLHelper.Attributes.ALIGN) || key.equals(HTMLHelper.Attributes.BGCOLOR))
            {
              continue;
            }
            String val = attribs.get(key).toString();
            html += ' ' + key + "=\"" + val + "\"";
        }
        
        if (styleValues.length()>0)
        {
          html = html + " " + HTML.Attribute.STYLE.toString()+"=\""+styleValues+"\"";
        }
        
        html += ">\n"; //$NON-NLS-1$
        
        int numRows = layoutPanel.getRows();
        int numCols = layoutPanel.getColumns();
        int startRow = 1;
        /* Is there a table header */
        if (layoutPanel.getTableHeader())
        {
          startRow = 2;
          html = html + "<thead>\n";
          html = html + "<tr>\n";
          for(int col = 1; col <= numCols; col++)
          {
              html += "\t<th></th>\n";
          }
          html += "</tr>\n";
          html += "</thead>\n";
        }
        
        html += "<tbody>\n";
        for(int row = startRow; row <= numRows; row++)
        {
            html += "<tr>\n"; //$NON-NLS-1$
            for(int col = 1; col <= numCols; col++)
            {
                html += "\t<td></td>\n"; //$NON-NLS-1$
            }
            html += "</tr>\n"; //$NON-NLS-1$
        }
        html += "</tbody>\n";
        
        return html + "</table><p></p>"; //$NON-NLS-1$
    }
    
 
}

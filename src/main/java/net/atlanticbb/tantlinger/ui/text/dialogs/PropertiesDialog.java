package net.atlanticbb.tantlinger.ui.text.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.Icon;

import net.atlanticbb.tantlinger.i18n.I18n;
import net.atlanticbb.tantlinger.ui.OptionDialog;
import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.dialogs.PropertiesPanel.DocumentClass;
import net.atlanticbb.tantlinger.ui.text.dialogs.PropertiesPanel.LanguageSelector;

import javax.swing.border.BevelBorder;
import javax.swing.text.Document;

import com.optimasc.text.DocumentProperties;

public class PropertiesDialog extends OptionDialog
{
  private static final long serialVersionUID = 1L;
  protected PropertiesPanel propertiesPanel;

  private static final I18n i18n = I18n.getInstance("net.atlanticbb.tantlinger.ui.text.dialogs");
  
  private static Icon icon = UIUtils.getIcon(UIUtils.X48, "pencil.png");
  private static String title = i18n.str("properties");
  private static String desc = i18n.str("properties_desc");
  

  public PropertiesDialog(Frame parent)
  {
    super(parent, title, desc, icon);   
    setResizable(false);
    init();
    pack();
    setLocationRelativeTo(parent);
  }
  
  private void init()
  {
    propertiesPanel = new PropertiesPanel();
    setContentPane(propertiesPanel);
    setSize(400, 440);
  }
  
  
  public void setProperties(Dictionary<Object, Object> properties)
  {
    Object o = properties.get(Document.TitleProperty);
    if (o!=null)
    {
      propertiesPanel.setTitle(o.toString());
    } else
    {
      propertiesPanel.setTitle("");
    }
    o = properties.get(DocumentProperties.CreatorProperty);
    if (o!=null)
    {
      propertiesPanel.setCreator(o.toString());
    } else
    {
      propertiesPanel.setCreator("");
    }
    o = properties.get(DocumentProperties.LanguageProperty);
    if (o!=null)
    {
      propertiesPanel.setLanguage((LanguageSelector)o);
    } else
    {
      propertiesPanel.setLanguage(null);
    }
    o = properties.get(DocumentProperties.SubjectProperty);
    if (o!=null)
    {
      propertiesPanel.setSubject(o.toString());
    } else
    {
      propertiesPanel.setSubject("");
    }
    o = properties.get(DocumentProperties.ContributorProperty);
    if (o!=null)
    {
      propertiesPanel.setContributors(o.toString());
    } else
    {
      propertiesPanel.setContributors("");
    }
    o = properties.get(DocumentProperties.DocumentClassProperty);
    if (o!=null)
    {
      propertiesPanel.setDocumentClass((DocumentClass)o);
    } else
    {
      /* Use default class. */
      propertiesPanel.setDocumentClass(DocumentClass.ARTICLE);
    }
  }
  
  public void getProperties(Dictionary<Object, Object> properties)
  {
    String s = propertiesPanel.getTitle();
    if (s.length()>0)
    {
      properties.put(Document.TitleProperty,s);
    } else
    {
      properties.remove(Document.TitleProperty);
    }
    s = propertiesPanel.getCreator();
    if (s.length()>0)
    {
      properties.put(DocumentProperties.CreatorProperty,s);
    } else
    {
      properties.remove(DocumentProperties.CreatorProperty);
    }
    s = propertiesPanel.getSubject();
    if (s.length()>0)
    {
      properties.put(DocumentProperties.SubjectProperty,s);
    } else
    {
      properties.remove(DocumentProperties.SubjectProperty);
    }
    s = propertiesPanel.getContributors();
    if (s.length()>0)
    {
      properties.put(DocumentProperties.ContributorProperty,s);
    } else
    {
      properties.remove(DocumentProperties.ContributorProperty);
    }
    
    LanguageSelector lang = propertiesPanel.getLanguage();
    if (lang!=null)
    {
      properties.put(DocumentProperties.LanguageProperty, lang);
    } else
    {
      properties.remove(DocumentProperties.LanguageProperty);
    }
    DocumentClass docClass  = propertiesPanel.getDocumentClass();
    if (docClass!=null)
    {
      properties.put(DocumentProperties.DocumentClassProperty, docClass);
    } else
    {
      properties.put(DocumentProperties.DocumentClassProperty, DocumentClass.ARTICLE);
    }
    
  }

  
}

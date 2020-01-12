package net.atlanticbb.tantlinger.ui.text.dialogs;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.util.Locale;
import java.awt.Font;

public class PropertiesPanel extends JPanel
{
  private JTextField titleTextField;
  private JTextField creatorTextField;
  private JTextField contributorsTextField;
  private JTextField subjectTextField;
  private JComboBox languageComboBox;
  private JTextArea descriptionTextArea;
  
  protected DefaultComboBoxModel languageModel = new DefaultComboBoxModel();
  protected DefaultComboBoxModel classModel = new DefaultComboBoxModel();
  
  
  public static class DocumentClass
  {
    public static final DocumentClass ARTICLE = new DocumentClass("article",false, false, false, false);
    public static final DocumentClass REPORT = new DocumentClass("report",true, false, true, true);
    public static final DocumentClass BOOK = new DocumentClass("book",true, true, false, true);
    public static final DocumentClass LETTER = new DocumentClass("letter",false, false, false, false);
    
    /** Document class name */
    String name;
    /** Generate table of contents. */
    boolean tableOfContents;
    /** Generate index */
    boolean index;
    /** Generated numbered headings */
    boolean numberedHeadings;
    /** Generate or insert title page */
    boolean titlePage;

    public DocumentClass(String name, boolean toc, boolean index, boolean numberedHeadings, boolean titlePage)
    {
      this.name = name;
      this.tableOfContents = toc;
      this.index = index;
      this.numberedHeadings = numberedHeadings;
      this.titlePage = titlePage;
    }
    
    @Override
    public String toString()
    {
      return name;
    }
    
    @Override
    public boolean equals(Object obj)
    {
      boolean b;
      b =  super.equals(obj);
      if (b==true)
      {
        return b;
      }
      if (obj instanceof DocumentClass)
      {
        DocumentClass clz = (DocumentClass)obj;
        if (
             (clz.name.equals(this.name))
           && (clz.index == this.index)
           && (clz.numberedHeadings == this.numberedHeadings)
           && (clz.tableOfContents == this.tableOfContents)
           && (clz.titlePage == this.titlePage)
             )
        {
          return true;
        }
      }
      return false;
    }    
  }
  
  
  
  /** Used to select a language. */
  public static class LanguageSelector
  {
    protected Locale locale;
    
    public static final LanguageSelector ENGLISH = new LanguageSelector(Locale.ENGLISH);
    public static final LanguageSelector FRENCH = new LanguageSelector(Locale.FRENCH);
    public static final LanguageSelector CHINESE = new LanguageSelector(Locale.CHINESE);
    public static final LanguageSelector GERMAN = new LanguageSelector(Locale.GERMAN);
    public static final LanguageSelector ITALIAN = new LanguageSelector(Locale.ITALIAN);
    public static final LanguageSelector JAPANESE = new LanguageSelector(Locale.JAPANESE);
    public static final LanguageSelector KOREAN = new LanguageSelector(Locale.KOREAN);
    public static final LanguageSelector SIMPLIFIED_CHINESE = new LanguageSelector(Locale.SIMPLIFIED_CHINESE);
    public static final LanguageSelector SPANISH = new LanguageSelector(new Locale("es"));
    public static final LanguageSelector RUSSIAN = new LanguageSelector(new Locale("ru"));
    
    @Override
    public String toString()
    {
      return locale.getDisplayName();
    }

    
    public LanguageSelector(Locale l)
    {
      locale = l;
    }
    
    public Locale getLocale()
    {
      return locale;
    }


    @Override
    public boolean equals(Object obj)
    {
      boolean b;
      b =  super.equals(obj);
      if (b==true)
      {
        return b;
      }
      if (obj instanceof LanguageSelector)
      {
        LanguageSelector lng = (LanguageSelector)obj;
        if (lng.locale.equals(this.locale))
        {
          return true;
        }
      }
      return false;
    }
    
    
  }
  
  
  
  /**
   * Create the panel.
   */
  public PropertiesPanel()
  {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    classModel.addElement(DocumentClass.ARTICLE);
    classModel.addElement(DocumentClass.REPORT);
    classModel.addElement(DocumentClass.BOOK);
    classModel.addElement(DocumentClass.LETTER);
    
    JLabel languageLabel = new JLabel("Language");
    languageLabel.setPreferredSize(new Dimension(47, 20));
    languageLabel.setToolTipText("Text document");
    add(languageLabel);
    
    languageComboBox = new JComboBox();
    languageComboBox.setToolTipText("Set language of most of the text in document");
    languageModel.addElement(LanguageSelector.ENGLISH);
    languageModel.addElement(LanguageSelector.FRENCH);
    languageModel.addElement(LanguageSelector.GERMAN);
    languageModel.addElement(LanguageSelector.SPANISH);
    languageModel.addElement(LanguageSelector.ITALIAN);
    languageModel.addElement(LanguageSelector.CHINESE);
    languageModel.addElement(LanguageSelector.KOREAN);
    languageModel.addElement(LanguageSelector.RUSSIAN);
    languageModel.addElement(LanguageSelector.SIMPLIFIED_CHINESE);
    languageModel.addElement(LanguageSelector.JAPANESE);
    languageComboBox.setModel(languageModel);
    languageComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(languageComboBox);
    
    JLabel titleLabel = new JLabel("Title");
    titleLabel.setPreferredSize(new Dimension(20, 20));
    add(titleLabel);
    
    titleTextField = new JTextField();
    titleTextField.setToolTipText("Title of document in specified language");
    titleTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(titleTextField);
    titleTextField.setColumns(10);
    
    JLabel creatorLabel = new JLabel("Creator(s)");
    creatorLabel.setPreferredSize(new Dimension(50, 20));
    add(creatorLabel);
    
    creatorTextField = new JTextField();
    creatorTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(creatorTextField);
    creatorTextField.setColumns(10);
    
    JLabel contributorsLabel = new JLabel("Contributor(s)");
    contributorsLabel.setPreferredSize(new Dimension(68, 20));
    add(contributorsLabel);
    
    contributorsTextField = new JTextField();
    contributorsTextField.setEnabled(false);
    contributorsTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(contributorsTextField);
    contributorsTextField.setColumns(10);
    
    JLabel subjectLabel = new JLabel("Subject");
    subjectLabel.setPreferredSize(new Dimension(36, 20));
    add(subjectLabel);
    
    subjectTextField = new JTextField();
    subjectTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(subjectTextField);
    subjectTextField.setColumns(10);
    
    JLabel lblNewLabel = new JLabel("Commas can be used to separate multiple values");
    lblNewLabel.setFont(new Font("Tahoma", Font.ITALIC, 11));
    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(lblNewLabel);
    
    JLabel descriptionLabel = new JLabel("Description");
    descriptionLabel.setPreferredSize(new Dimension(53, 20));
    add(descriptionLabel);
    
    descriptionTextArea = new JTextArea();
    descriptionTextArea.setBorder(new LineBorder(Color.LIGHT_GRAY));
    descriptionTextArea.setPreferredSize(new Dimension(4, 60));
    descriptionTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(descriptionTextArea);
  }
  
  
  /** Set the selected language, if the language does not
   *  exist, it is first added to the list before being
   *  selected.
   * @param lang
   */
  public void setLanguage(LanguageSelector lang)
  {
    if (languageModel.getIndexOf(lang)==-1)
    {
      languageModel.addElement(lang);
      languageModel.setSelectedItem(lang);
    } else
    {
      languageModel.setSelectedItem(lang);
    }
  }
  
  /** Returns the selected language or null
   *  if not is selected.
   *  
   * @return 
   */
  public LanguageSelector getLanguage()
  {
    return (LanguageSelector) languageModel.getSelectedItem();
  }
  
  
  /** Set the document class, if the document class does
   *  not exist, it is first added and then selected.
   */
  public void setDocumentClass(DocumentClass docClass)
  {
    if (classModel.getIndexOf(docClass)==-1)
    {
      classModel.addElement(docClass);
      classModel.setSelectedItem(docClass);
    } else
    {
      classModel.setSelectedItem(docClass);
    }
    
  }
  
  public DocumentClass getDocumentClass()
  {
    return (DocumentClass) classModel.getSelectedItem();
  }
  
  public String getTitle()
  {
    return titleTextField.getText();
  }
  
  public void setTitle(String title)
  {
    titleTextField.setText(title);
  }
  
  public String getCreator()
  {
    return creatorTextField.getText();
  }
  
  public void setCreator(String creator)
  {
    creatorTextField.setText(creator);
  }
  
  public String getSubject()
  {
    return subjectTextField.getText();
  }
  
  public void setSubject(String subject)
  {
    subjectTextField.setText(subject);
  }
  
  public String getContributors()
  {
    return contributorsTextField.getText();
  }
  
  public void setContributors(String contributors)
  {
    contributorsTextField.setText(contributors);
  }
  
  

}

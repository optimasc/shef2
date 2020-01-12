package net.atlanticbb.tantlinger.shef;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;

import net.atlanticbb.tantlinger.i18n.I18n;
import net.atlanticbb.tantlinger.ui.DefaultAction;
import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.Entities;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.IndentationFilter;
import net.atlanticbb.tantlinger.ui.text.SourceCodeEditor;
import net.atlanticbb.tantlinger.ui.text.WysiwygHTMLEditorKit;
import net.atlanticbb.tantlinger.ui.text.actions.ClearStylesAction;
import net.atlanticbb.tantlinger.ui.text.actions.FindReplaceAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLEditorActionFactory;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLElementPropertiesAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLFontAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLFontColorAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInsertHorizontalRuleAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInsertImageAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInlineAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInsertLineBreakAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInsertLinkAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInsertTableAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLSaveElementAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction;
import net.atlanticbb.tantlinger.ui.text.actions.IndentAction;
import net.atlanticbb.tantlinger.ui.text.actions.SpecialCharAction;

import novaworx.syntax.SyntaxFactory;
import novaworx.textpane.SyntaxDocument;
import novaworx.textpane.SyntaxGutter;
import novaworx.textpane.SyntaxGutterBase;

import org.bushe.swing.action.ActionList;
import org.bushe.swing.action.ActionManager;
import org.bushe.swing.action.ActionUIFactory;
import org.xml.sax.SAXException;

import com.optimasc.text.html.FontHelper;
import com.optimasc.text.html.HTMLHelper;
import com.optimasc.text.html.XHTMLHelper;
import com.optimasc.text.html.XHTMLParser;
import com.optimasc.text.html.XHTMLWriter;
import com.optimasc.utils.DataURLConnection;

/**
 * 
 * @author Bob Tantlinger
 * @author Carl Eric Codere
 */
public class HTMLEditorPane extends JPanel
{
  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  private static final I18n i18n = I18n.getInstance("net.atlanticbb.tantlinger.shef");

  private JEditorPane wysEditor;
  private SourceCodeEditor srcEditor;
  private JEditorPane focusedEditor;
  private JComboBox fontFamilyCombo;
  private JComboBox paragraphCombo;
  private JTabbedPane tabs;
  //private JMenuBar menuBar;
  private JToolBar formatToolBar;

  private JMenu editMenu;
  private JMenu formatMenu;
  private JMenu insertMenu;

  private JPopupMenu wysPopupMenu, srcPopupMenu;

  private ActionList actionList;

  private FocusListener focusHandler = new FocusHandler();
  private DocumentListener textChangedHandler = new TextChangedHandler();
  private ActionListener fontChangeHandler = new FontChangeHandler();
  private ActionListener paragraphComboHandler = new ParagraphComboHandler();
  private CaretListener caretHandler = new CaretHandler();
  private MouseListener popupHandler = new PopupHandler();
  
  private VetoableSingleSelectionModel tabbedPanemodel = new VetoableSingleSelectionModel();
  private VetoableChangeListener tabsValidator;
  

  protected Frame frame;

  private boolean isWysTextChanged;

  public HTMLEditorPane(Frame frame)
  {
    initUI();
    this.frame = frame;
  }

  public void setCaretPosition(int pos)
  {
    if (tabs.getSelectedIndex() == 0)
    {
      wysEditor.setCaretPosition(pos);
      wysEditor.requestFocusInWindow();
    }
    else if (tabs.getSelectedIndex() == 1)
    {
      srcEditor.setCaretPosition(pos);
      srcEditor.requestFocusInWindow();
    }
  }

  public void setSelectedTab(int i)
  {
    tabs.setSelectedIndex(i);
  }

  private void initUI()
  {
    createEditorTabs();
    createEditorActions();
    setLayout(new BorderLayout());
    add(formatToolBar, BorderLayout.NORTH);
    add(tabs, BorderLayout.CENTER);

  }

  public JMenu getEditMenu()
  {
    return editMenu;
  }

  public JMenu getFormatMenu()
  {
    return formatMenu;
  }

  public JMenu getInsertMenu()
  {
    return insertMenu;
  }

  private void createEditorActions()
  {
    actionList = new ActionList("editor-actions");

    ActionList paraActions = new ActionList("paraActions");
    ActionList fontSizeActions = new ActionList("fontSizeActions");
    ActionList editActions = HTMLEditorActionFactory.createEditActionList();
    Action objectPropertiesAction = new HTMLElementPropertiesAction();
    Action saveAction = new HTMLSaveElementAction();

    //create editor popupmenus
    wysPopupMenu = ActionUIFactory.getInstance().createPopupMenu(editActions);
    wysPopupMenu.addSeparator();
    wysPopupMenu.add(saveAction);
    wysPopupMenu.addSeparator();
    wysPopupMenu.add(objectPropertiesAction);
    srcPopupMenu = ActionUIFactory.getInstance().createPopupMenu(editActions);

    // create file menu
    JMenu fileMenu = new JMenu(i18n.str("file"));

    // create edit menu   
    ActionList lst = new ActionList("edits");
    Action act = new ChangeTabAction(0);
    lst.add(act);
    act = new ChangeTabAction(1);
    lst.add(act);
    lst.add(null);//separator        
    lst.addAll(editActions);
    lst.add(null);
    lst.add(new FindReplaceAction(false));
    actionList.addAll(lst);
    editMenu = ActionUIFactory.getInstance().createMenu(lst);
    editMenu.setText(i18n.str("edit"));

    //create format menu
    formatMenu = new JMenu(i18n.str("format"));
    lst = HTMLEditorActionFactory.createFontSizeActionList();//HTMLEditorActionFactory.createInlineActionList();
    actionList.addAll(lst);
    formatMenu.add(createMenu(lst, i18n.str("size")));
    fontSizeActions.addAll(lst);

    lst = HTMLEditorActionFactory.createLogicalInlineActionList();
    actionList.addAll(lst);
    formatMenu.add(createMenu(lst, i18n.str("style")));

    lst = HTMLEditorActionFactory.createVisualInlineActionList();
    actionList.addAll(lst);
    formatMenu.add(createMenu(lst, i18n.str("visual style")));

    act = new HTMLFontColorAction();
    actionList.add(act);
    formatMenu.add(act);

    act = new HTMLFontAction();
    actionList.add(act);
    formatMenu.add(act);

    act = new ClearStylesAction();
    actionList.add(act);
    formatMenu.add(act);
    formatMenu.addSeparator();

    lst = HTMLEditorActionFactory.createBlockElementActionList();
    actionList.addAll(lst);
    formatMenu.add(createMenu(lst, i18n.str("paragraph")));
    paraActions.addAll(lst);

    lst = HTMLEditorActionFactory.createListElementActionList();
    actionList.addAll(lst);
    formatMenu.add(createMenu(lst, i18n.str("list")));
    formatMenu.addSeparator();
    paraActions.addAll(lst);

    lst = HTMLEditorActionFactory.createAlignActionList();
    actionList.addAll(lst);
    formatMenu.add(createMenu(lst, i18n.str("align")));

    JMenu tableMenu = new JMenu(i18n.str("table"));
    lst = HTMLEditorActionFactory.createInsertTableElementActionList();
    actionList.addAll(lst);
    tableMenu.add(createMenu(lst, i18n.str("insert")));

    lst = HTMLEditorActionFactory.createDeleteTableElementActionList();
    actionList.addAll(lst);
    tableMenu.add(createMenu(lst, i18n.str("delete")));
    formatMenu.add(tableMenu);
    formatMenu.addSeparator();

    actionList.add(objectPropertiesAction);
    actionList.add(saveAction);
    formatMenu.add(objectPropertiesAction);
    formatMenu.add(saveAction);
    
    act = new IndentAction(IndentAction.INDENT);
    formatMenu.add(act);
    actionList.add(act);

    act = new IndentAction(IndentAction.OUTDENT);
    formatMenu.add(act);
    actionList.add(act);
    

    //create insert menu
    insertMenu = new JMenu(i18n.str("insert"));
    act = new HTMLInsertLinkAction();
    actionList.add(act);
    insertMenu.add(act);

    act = new HTMLInsertImageAction();
    actionList.add(act);
    insertMenu.add(act);

    act = new HTMLInsertTableAction();
    actionList.add(act);
    insertMenu.add(act);
    insertMenu.addSeparator();

    act = new HTMLInsertLineBreakAction();
    actionList.add(act);
    insertMenu.add(act);

    act = new HTMLInsertHorizontalRuleAction();
    actionList.add(act);
    insertMenu.add(act);

    act = new SpecialCharAction();
    actionList.add(act);
    insertMenu.add(act);

    createFormatToolBar(paraActions, fontSizeActions);
  }

  private void createFormatToolBar(ActionList blockActs, ActionList fontSizeActs)
  {
    formatToolBar = new JToolBar();
    formatToolBar.setFloatable(false);
    formatToolBar.setFocusable(false);

    Font comboFont = new Font("Dialog", Font.PLAIN, 12);
    PropertyChangeListener propLst = new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent evt)
      {
        if (evt.getPropertyName().equals("selected"))
        {
          if (evt.getNewValue().equals(Boolean.TRUE))
          {
            paragraphCombo.removeActionListener(paragraphComboHandler);
            paragraphCombo.setSelectedItem(evt.getSource());
            paragraphCombo.addActionListener(paragraphComboHandler);
          }
        }
      }
    };
    for (Iterator it = blockActs.iterator(); it.hasNext();)
    {
      Object o = it.next();
      if (o instanceof DefaultAction)
        ((DefaultAction) o).addPropertyChangeListener(propLst);
    }
    paragraphCombo = new JComboBox(toArray(blockActs));
    paragraphCombo.setPreferredSize(new Dimension(120, 22));
    paragraphCombo.setMinimumSize(new Dimension(120, 22));
    paragraphCombo.setMaximumSize(new Dimension(120, 22));
    paragraphCombo.setFont(comboFont);
    paragraphCombo.addActionListener(paragraphComboHandler);
    paragraphCombo.setRenderer(new ParagraphComboRenderer());
    formatToolBar.add(paragraphCombo);
    formatToolBar.addSeparator();

    Vector fonts = new Vector();
    fonts.add(FontHelper.FONT_FAMILY_LABEL_DEFAULT);
    fonts.add(FontHelper.FONT_FAMILY_LABEL_SERIF);
    fonts.add(FontHelper.FONT_FAMILY_LABEL_SANS_SERIF);
    fonts.add(FontHelper.FONT_FAMILY_LABEL_MONOSPACE);

    fontFamilyCombo = new JComboBox(fonts);
    fontFamilyCombo.setPreferredSize(new Dimension(150, 22));
    fontFamilyCombo.setMinimumSize(new Dimension(150, 22));
    fontFamilyCombo.setMaximumSize(new Dimension(150, 22));
    fontFamilyCombo.setFont(comboFont);
    fontFamilyCombo.addActionListener(fontChangeHandler);
    formatToolBar.add(fontFamilyCombo);

    final JButton fontSizeButton = new JButton(UIUtils.getIcon(UIUtils.X16, "fontsize.png"));
    final JPopupMenu sizePopup = ActionUIFactory.getInstance().createPopupMenu(fontSizeActs);
    ActionListener al = new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        sizePopup.show(fontSizeButton, 0, fontSizeButton.getHeight());
      }
    };
    fontSizeButton.addActionListener(al);
    configToolbarButton(fontSizeButton);
    formatToolBar.add(fontSizeButton);

    Action act = new HTMLFontColorAction();
    actionList.add(act);
    addToToolBar(formatToolBar, act);
    formatToolBar.addSeparator();

    act = new HTMLInlineAction(HTMLInlineAction.STRONG);
    act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
    actionList.add(act);
    addToToolBar(formatToolBar, act);

    act = new HTMLInlineAction(HTMLInlineAction.EM);
    act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
    actionList.add(act);
    addToToolBar(formatToolBar, act);

    formatToolBar.addSeparator();

    List alst = HTMLEditorActionFactory.createListElementActionList();
    for (Iterator it = alst.iterator(); it.hasNext();)
    {
      act = (Action) it.next();
      act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
      actionList.add(act);
      addToToolBar(formatToolBar, act);
    }
    formatToolBar.addSeparator();

    alst = HTMLEditorActionFactory.createAlignActionList();
    for (Iterator it = alst.iterator(); it.hasNext();)
    {
      act = (Action) it.next();
      act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
      actionList.add(act);
      addToToolBar(formatToolBar, act);
    }
    formatToolBar.addSeparator();

    act = new HTMLInsertLinkAction();
    actionList.add(act);
    addToToolBar(formatToolBar, act);

    act = new HTMLInsertImageAction();
    actionList.add(act);
    addToToolBar(formatToolBar, act);

    act = new HTMLInsertTableAction();
    actionList.add(act);
    addToToolBar(formatToolBar, act);

  }

  private void addToToolBar(JToolBar toolbar, Action act)
  {
    AbstractButton button = ActionUIFactory.getInstance().createButton(act);
    configToolbarButton(button);
    toolbar.add(button);
  }

  /**
   * Converts an action list to an array. Any of the null "separators" or sub
   * ActionLists are ommited from the array.
   * 
   * @param lst
   * @return
   */
  private Action[] toArray(ActionList lst)
  {
    List acts = new ArrayList();
    for (Iterator it = lst.iterator(); it.hasNext();)
    {
      Object v = it.next();
      if (v != null && v instanceof Action)
        acts.add(v);
    }

    return (Action[]) acts.toArray(new Action[acts.size()]);
  }

  private void configToolbarButton(AbstractButton button)
  {
    button.setText(null);
    button.setMnemonic(0);
    button.setMargin(new Insets(1, 1, 1, 1));
    button.setMaximumSize(new Dimension(22, 22));
    button.setMinimumSize(new Dimension(22, 22));
    button.setPreferredSize(new Dimension(22, 22));
    button.setFocusable(false);
    button.setFocusPainted(false);
    //button.setBorder(plainBorder);
    Action a = button.getAction();
    if (a != null)
      button.setToolTipText(a.getValue(Action.NAME).toString());
  }

  private JMenu createMenu(ActionList lst, String menuName)
  {
    JMenu m = ActionUIFactory.getInstance().createMenu(lst);
    m.setText(menuName);
    return m;
  }

  private void createEditorTabs()
  {
    tabs = new JTabbedPane(SwingConstants.BOTTOM);
    wysEditor = createWysiwygEditor();

    srcEditor = createSourceEditor();

    tabs.addTab("Edit", new JScrollPane(wysEditor));

    JScrollPane scrollPane = new JScrollPane(srcEditor);
    SyntaxGutter gutter = new SyntaxGutter(srcEditor);
    SyntaxGutterBase gutterBase = new SyntaxGutterBase(gutter);
    scrollPane.setRowHeaderView(gutter);
    scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, gutterBase);

    tabs.addTab("HTML", scrollPane);
    tabs.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        updateEditView();
      }
    });
    
    /** This implements a validator to validate the content
     *  of the source code editor when trying to switch
     *  tabs, it will reject changing tabs if the
     *  source code is not well-formed XML and
     *  not compliant with the active schema.
     */
    tabsValidator = new VetoableChangeListener() {

        @Override
        public void vetoableChange(PropertyChangeEvent evt)
                throws PropertyVetoException
                {
            int oldSelection = ((Integer) evt.getOldValue()).intValue();
            if ((oldSelection == -1) || isValidTab(oldSelection)) 
                return;

            throw new PropertyVetoException("change not valid", evt);

        }

        /* Verify validity of the current data. */
        private boolean isValidTab(int oldSelection) 
        {
          if (oldSelection == 1)
          {
            try
            {
              String topText = removeInvalidTags(srcEditor.getText());
              XHTMLParser.validateDocument(new StringReader(topText));
            } catch (SAXException e)
            {
              UIUtils.showError(frame, "XHTML Syntax error.", e);
              return false;
            } catch (IllegalArgumentException e)
            {
              UIUtils.showError(frame, "XHTML Syntax error.", e);
              return false;
            }
          }
          return true;
        }
    };
    
    
    tabbedPanemodel.addVetoableChangeListener(tabsValidator);
    tabs.setModel(tabbedPanemodel);    
  }

  private SourceCodeEditor createSourceEditor()
  {
    SourceCodeEditor ed = new SourceCodeEditor();
    SyntaxDocument doc = new SyntaxDocument();
    doc.setSyntax(SyntaxFactory.getSyntax("html"));
    CompoundUndoManager cuh = new CompoundUndoManager(doc, new UndoManager());

    doc.addUndoableEditListener(cuh);
    doc.setDocumentFilter(new IndentationFilter());
    doc.addDocumentListener(textChangedHandler);
    ed.setDocument(doc);
    ed.addFocusListener(focusHandler);
    ed.addCaretListener(caretHandler);
    ed.addMouseListener(popupHandler);

    return ed;
  }

  public EditorKit getEditorKit()
  {
    return wysEditor.getEditorKit();
  }

  private JEditorPane createWysiwygEditor()
  {
    JEditorPane ed = new JEditorPane();
    ed.setEditorKitForContentType("text/html", new WysiwygHTMLEditorKit());

    ed.setContentType("text/html");

    insertHTML(ed, "<html xmlns='" + XHTMLHelper.HTML_NS
        + "'><head><title></title></head><body><p></p></body></html>", 0);

    ed.addCaretListener(caretHandler);
    ed.addFocusListener(focusHandler);
    ed.addMouseListener(popupHandler);

    HTMLDocument document = (HTMLDocument) ed.getDocument();
    CompoundUndoManager cuh = new CompoundUndoManager(document, new UndoManager());
    document.addUndoableEditListener(cuh);
    document.addDocumentListener(textChangedHandler);

    return ed;
  }

  //  inserts html into the wysiwyg editor TODO remove JEditorPane parameter
  private void insertHTML(JEditorPane editor, String html, int location)
  {
    HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();
    Document doc = editor.getDocument();
    StringReader reader = new StringReader(HTMLUtils.jEditorPaneizeHTML(html));
    try
    {
      kit.read(reader, doc, location);
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (BadLocationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // called when changing tabs
  private void updateEditView()
  {
    if (tabs.getSelectedIndex() == 0)
    {
        String topText = removeInvalidTags(srcEditor.getText());
        wysEditor.setText("");
        insertHTML(wysEditor, topText, 0);
        CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
    }
    else
    {
      try
      {
        Writer sw = new StringWriter();
        HTMLEditorKit kit = (HTMLEditorKit) wysEditor.getEditorKit();
        kit.write(sw, wysEditor.getDocument(), 0, wysEditor.getDocument().getLength());
      } catch (BadLocationException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      String topText = removeInvalidTags(wysEditor.getText());
      if (isWysTextChanged || srcEditor.getText().equals(""))
      {
        //                String t = deIndent(removeInvalidTags(topText));
        String t = topText;
        t = Entities.HTML40.unescapeUnknownEntities(t);
        srcEditor.setText(t);
      }
      CompoundUndoManager.discardAllEdits(srcEditor.getDocument());
    }

    isWysTextChanged = false;
    paragraphCombo.setEnabled(tabs.getSelectedIndex() == 0);
    fontFamilyCombo.setEnabled(tabs.getSelectedIndex() == 0);
    updateState();
  }

  public Document getDocument()
  {
    return wysEditor.getDocument();
  }

  public void setDocument(Document doc)
  {
    wysEditor.setText("");
    wysEditor.setDocument(doc);
    CompoundUndoManager cuh = new CompoundUndoManager(doc, new UndoManager());
    doc.addUndoableEditListener(cuh);
    doc.addDocumentListener(textChangedHandler);
    CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
  }

  public void startNewDocument()
  {
    Document document = wysEditor.getEditorKitForContentType("text/html").createDefaultDocument();
    wysEditor.setText("");
    wysEditor.setDocument(document);
    insertHTML(wysEditor, "<html xmlns='" + XHTMLHelper.HTML_NS
        + "'><head><title></title></head><body><p></p></body></html>", 0);

    CompoundUndoManager cuh = new CompoundUndoManager(document, new UndoManager());
    document.addUndoableEditListener(cuh);
    document.addDocumentListener(textChangedHandler);
    CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
    /* For HTML view */
    tabs.setSelectedIndex(0);
  }

  /*    public void setText(String text)
      {
      	String topText = removeInvalidTags(text);  
          
          if(tabs.getSelectedIndex() == 0)
          {           
                        
              wysEditor.setText("");
              insertHTML(wysEditor, topText, 0);            
              CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
              
          }
          else 
          {
              {
                  String t = deIndent(removeInvalidTags(topText));
                  t = Entities.HTML40.unescapeUnknownEntities(t);                
                  srcEditor.setText(t);
              }            
              CompoundUndoManager.discardAllEdits(srcEditor.getDocument());            
          }
      }*/

  public String getText()
  {
    String topText;
    if (tabs.getSelectedIndex() == 0)
    {
      topText = removeInvalidTags(wysEditor.getText());

    }
    else
    {
      topText = removeInvalidTags(srcEditor.getText());
      topText = deIndent(removeInvalidTags(topText));
      topText = Entities.HTML40.unescapeUnknownEntities(topText);
    }

    return topText;
  }

  /* *******************************************************************
   *  Methods for dealing with HTML between wysiwyg and source editors 
   * ******************************************************************/
  private String deIndent(String html)
  {
    String ws = "\n    ";
    StringBuffer sb = new StringBuffer(html);

    while (sb.indexOf(ws) != -1)
    {
      int s = sb.indexOf(ws);
      int e = s + ws.length();
      sb.delete(s, e);
      sb.insert(s, "\n");
    }

    return sb.toString();
  }

  private String removeInvalidTags(String html)
  {
    /*    for(int i = 0; i < INVALID_TAGS.length; i++)
        {
            html = deleteOccurance(html, '<' + INVALID_TAGS[i] + '>');
            html = deleteOccurance(html, "</" + INVALID_TAGS[i] + '>');
        } */

    return html.trim();
  }

  private String deleteOccurance(String text, String word)
  {
    //if(text == null)return "";
    StringBuffer sb = new StringBuffer(text);
    int p;
    while ((p = sb.toString().toLowerCase().indexOf(word.toLowerCase())) != -1)
    {
      sb.delete(p, p + word.length());
    }
    return sb.toString();
  }

  /* ************************************* */

  private void updateState()
  {
    if (focusedEditor == wysEditor)
    {
      fontFamilyCombo.removeActionListener(fontChangeHandler);
      AttributeSet attr = HTMLUtils.getCharacterAttributes(wysEditor);
      String fontName = HTMLUtils.attributeHelper.getFontFamily(attr);
      if (fontName == null)
        fontFamilyCombo.setSelectedIndex(0);
      else
        fontFamilyCombo.setSelectedItem(fontName);
      fontFamilyCombo.addActionListener(fontChangeHandler);
    }

    actionList.putContextValueForAll(HTMLTextEditAction.EDITOR, focusedEditor);
    actionList.updateEnabledForAll();
  }

  private class CaretHandler implements CaretListener
  {
    /* (non-Javadoc)
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    public void caretUpdate(CaretEvent e)
    {
      updateState();
    }
  }

  private class PopupHandler extends MouseAdapter
  {
    public void mousePressed(MouseEvent e)
    {
      checkForPopupTrigger(e);
    }

    public void mouseReleased(MouseEvent e)
    {
      checkForPopupTrigger(e);
    }

    private void checkForPopupTrigger(MouseEvent e)
    {
      if (e.isPopupTrigger())
      {
        JPopupMenu p = null;
        if (e.getSource() == wysEditor)
          p = wysPopupMenu;
        else if (e.getSource() == srcEditor)
          p = srcPopupMenu;
        else
          return;
        p.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  private class FocusHandler implements FocusListener
  {
    public void focusGained(FocusEvent e)
    {
      if (e.getComponent() instanceof JEditorPane)
      {
        JEditorPane ed = (JEditorPane) e.getComponent();
        CompoundUndoManager.updateUndo(ed.getDocument());
        focusedEditor = ed;

        updateState();
        // updateEnabledStates();
      }
    }

    public void focusLost(FocusEvent e)
    {

      if (e.getComponent() instanceof JEditorPane)
      {
        //focusedEditor = null;
        //wysiwygUpdated();
      }
    }
  }

  private class TextChangedHandler implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      textChanged();
    }

    public void removeUpdate(DocumentEvent e)
    {
      textChanged();
    }

    public void changedUpdate(DocumentEvent e)
    {
      textChanged();
    }

    private void textChanged()
    {
      if (tabs.getSelectedIndex() == 0)
        isWysTextChanged = true;
    }
  }

  private class ChangeTabAction extends DefaultAction
  {
    /**
         * 
         */
    private static final long serialVersionUID = 1L;
    int tab;

    public ChangeTabAction(int tab)
    {
      super((tab == 0) ? i18n.str("rich_text") :
          i18n.str("source"));
      this.tab = tab;
      putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);
    }

    protected void execute(ActionEvent e)
    {
      tabs.setSelectedIndex(tab);
      setSelected(true);
    }

    protected void contextChanged()
    {
      setSelected(tabs.getSelectedIndex() == tab);
    }
  }

  private class ParagraphComboHandler implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() == paragraphCombo)
      {
        Action a = (Action) (paragraphCombo.getSelectedItem());
        a.actionPerformed(e);
      }
    }
  }

  private class ParagraphComboRenderer extends DefaultListCellRenderer
  {
    /**
         * 
         */
    private static final long serialVersionUID = 1L;

    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus)
    {
      if (value instanceof Action)
      {
        value = ((Action) value).getValue(Action.NAME);
      }

      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
  }

  private class FontChangeHandler implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() == fontFamilyCombo && focusedEditor == wysEditor)
      {
        //MutableAttributeSet tagAttrs = new SimpleAttributeSet();
        HTMLDocument document = (HTMLDocument) focusedEditor.getDocument();
        CompoundUndoManager.beginCompoundEdit(document);

        if (fontFamilyCombo.getSelectedIndex() != 0)
        {
          HTMLHelper.setFontFamily(wysEditor, fontFamilyCombo.getSelectedItem().toString());
        }
        else
        {
          HTMLHelper.setFontFamily(wysEditor, null);
        }
        CompoundUndoManager.endCompoundEdit(document);
      }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e)
    {

    }
  }

  
  /** Implements a vetoable selection model that can
   *  be used to validate an item before moving on 
   *  to another item.
   *  
   * @author Kleopatra user from Stackoverflow.com
   *
   */
  public static class VetoableSingleSelectionModel extends
      DefaultSingleSelectionModel
  {

    private VetoableChangeSupport vetoableChangeSupport;

    @Override
    public void setSelectedIndex(int index)
    {
      if (getSelectedIndex() == index)
        return;
      try
      {
        fireVetoableChange(getSelectedIndex(), index);
      } catch (PropertyVetoException e)
      {
        return;
      }
      super.setSelectedIndex(index);
    }

    private void fireVetoableChange(int oldSelectionIndex,
        int newSelectionIndex) throws PropertyVetoException
    {
      if (!isVetoable())
        return;
      vetoableChangeSupport.fireVetoableChange("selectedIndex",
          oldSelectionIndex, newSelectionIndex);

    }

    private boolean isVetoable()
    {
      if (vetoableChangeSupport == null)
        return false;
      return vetoableChangeSupport.hasListeners(null);
    }

    public void addVetoableChangeListener(VetoableChangeListener l)
    {
      if (vetoableChangeSupport == null)
      {
        vetoableChangeSupport = new VetoableChangeSupport(this);
      }
      vetoableChangeSupport.addVetoableChangeListener(l);
    }

    public void removeVetoableChangeListener(VetoableChangeListener l)
    {
      if (vetoableChangeSupport == null)
        return;
      vetoableChangeSupport.removeVetoableChangeListener(l);
    }

  }

}

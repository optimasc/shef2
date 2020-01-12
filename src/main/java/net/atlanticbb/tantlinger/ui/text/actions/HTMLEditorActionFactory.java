/*
 * Created on Nov 2, 2007
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import javax.swing.text.StyleConstants;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;

import org.bushe.swing.action.ActionList;

import com.optimasc.text.html.FontHelper;


/**
 * @author Bob Tantlinger
 *
 */
public class HTMLEditorActionFactory
{
    public static ActionList createEditActionList()
    {
        ActionList list = new ActionList("edit");
        list.add(CompoundUndoManager.UNDO);
        list.add(CompoundUndoManager.REDO);
        list.add(null);
        list.add(new CutAction());
        list.add(new CopyAction());
        list.add(new PasteAction());
        //list.add(new PasteFormattedAction());
        list.add(null);
        list.add(new SelectAllAction()); 
        //list.add(new IndentAction(IndentAction.INDENT));
        //list.add(new IndentAction(IndentAction.OUTDENT));
        return list;        
    }
    
    
    public static ActionList createVisualInlineActionList()
    {
        ActionList list = new ActionList("visual style");
        list.add(new HTMLInlineAction(HTMLInlineAction.BOLD));
        list.add(new HTMLInlineAction(HTMLInlineAction.ITALIC));
/*        list.add(new HTMLInlineAction(HTMLInlineAction.UNDERLINE)); */
        list.add(new HTMLInlineAction(HTMLInlineAction.STRIKE));
        return list;
    }
    
    
    public static ActionList createLogicalInlineActionList()
    {
        ActionList list = new ActionList("logical style");
        list.add(new HTMLInlineAction(HTMLInlineAction.CITE));
        list.add(new HTMLInlineAction(HTMLInlineAction.CODE));
        list.add(new HTMLInlineAction(HTMLInlineAction.EM));
        list.add(new HTMLInlineAction(HTMLInlineAction.STRONG));
        list.add(new HTMLInlineAction(HTMLInlineAction.SUB));
        list.add(new HTMLInlineAction(HTMLInlineAction.SUP));
        list.add(new HTMLInlineAction(HTMLInlineAction.DFN));
        list.add(new HTMLInlineAction(HTMLInlineAction.VAR));
        list.add(new HTMLInlineAction(HTMLInlineAction.TT));
        list.add(new HTMLInlineAction(HTMLInlineAction.KBD));
        list.add(new HTMLInlineAction(HTMLInlineAction.SAMP));
        return list;
    }
    
    public static ActionList createAlignActionList()
    {
        ActionList list = new ActionList("align");
        list.add(new HTMLAlignAction(StyleConstants.ALIGN_LEFT));
        list.add(new HTMLAlignAction(StyleConstants.ALIGN_CENTER));
        list.add(new HTMLAlignAction(StyleConstants.ALIGN_RIGHT));
        list.add(new HTMLAlignAction(StyleConstants.ALIGN_JUSTIFIED));
        return list;
    }
    
    public static ActionList createFontSizeActionList()
    {
        ActionList list = new ActionList("font-size");
        int[] t = FontHelper.FONT_SIZES;
        for(int i = 0; i < t.length; i++)
        {
            list.add(new HTMLFontSizeAction(i));
        }
        
        return list;
    }
    
    public static ActionList createBlockElementActionList()
    {
        ActionList list = new ActionList("paragraph");
        list.add(new HTMLBlockAction(HTMLBlockAction.DIV));
        list.add(new HTMLBlockAction(HTMLBlockAction.P));
        list.add(null);
        list.add(new HTMLBlockAction(HTMLBlockAction.BLOCKQUOTE));
        list.add(new HTMLBlockAction(HTMLBlockAction.PRE));
        list.add(new HTMLBlockAction(HTMLBlockAction.ADDRESS));
        list.add(null);
        list.add(new HTMLBlockAction(HTMLBlockAction.H1));
        list.add(new HTMLBlockAction(HTMLBlockAction.H2));
        list.add(new HTMLBlockAction(HTMLBlockAction.H3));
        list.add(new HTMLBlockAction(HTMLBlockAction.H4));
        list.add(new HTMLBlockAction(HTMLBlockAction.H5));
        list.add(new HTMLBlockAction(HTMLBlockAction.H6));
                
        return list;
    }
    
    public static ActionList createListElementActionList()
    {
        ActionList list = new ActionList("list");
        list.add(new HTMLBlockAction(HTMLBlockAction.UL));
        list.add(new HTMLBlockAction(HTMLBlockAction.OL));
        
        return list;
    }
    
    public static ActionList createInsertActionList()
    {
        ActionList list = new ActionList("insertActions");
        list.add(new HTMLInsertLinkAction());
        list.add(new HTMLInsertImageAction());
        list.add(new HTMLInsertTableAction());
        list.add(null);
        list.add(new HTMLInsertLineBreakAction());
        list.add(new HTMLInsertHorizontalRuleAction());
        list.add(new SpecialCharAction());
        return list;
    }
    
    public static ActionList createInsertTableElementActionList()
    {
        ActionList list = new ActionList("Insert into table");
        list.add(new TableEditAction(TableEditAction.INSERT_CELL));
        list.add(new TableEditAction(TableEditAction.INSERT_ROW));
        list.add(new TableEditAction(TableEditAction.INSERT_COL));
        return list;
    }
    
    public static ActionList createDeleteTableElementActionList()
    {
        ActionList list = new ActionList("Insert into table");
        list.add(new TableEditAction(TableEditAction.DELETE_CELL));
        list.add(new TableEditAction(TableEditAction.DELETE_ROW));
        list.add(new TableEditAction(TableEditAction.DELETE_COL));
        return list;
    }
    
}

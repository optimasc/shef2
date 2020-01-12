package net.atlanticbb.tantlinger.ui.text;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.InlineView;

/** An inline view that is aligned to the left and
 *  not the center as like the default implementation.
 * 
 * @author Carl Eric Codere
 *
 */
public class InlineViewEx extends InlineView
{

  public InlineViewEx(Element elem)
  {
    super(elem);
  }

  @Override
  public float getAlignment(int axis)
  {
    if (axis==View.X_AXIS)
      return 0.0f;
    return super.getAlignment(axis);
  }
  
  

}

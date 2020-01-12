package javax.swing.text.html;

import javax.swing.DefaultComboBoxModel;

public class OptionComboBoxModelEx extends DefaultComboBoxModel
{
  private Option selectedOption = null;

  public void setInitialSelection(Option option) {
      selectedOption = option;
  }

  public Option getInitialSelection() {
      return selectedOption;
  }

}

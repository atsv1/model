package mp.gui;

import mp.elements.*;

import javax.swing.*;
import java.awt.*;

/**
 * User: atsv
 * Date: 30.09.2006
 */
public class ModelGUITabSheet extends ModelGUIAbstrElement implements ModelGUIElement{
  private JPanel FPanel;
  private JPanel FMainPanel;
  private JScrollPane scrollPane = null;
  private double FPanelWidth = 0;
  private double FPanelHeigth = 0;
  private boolean IsPrefferedSizeInit = false;

  public ModelGUITabSheet(){
    FPanel = new JPanel( null );
    //FPanel = new JPanel( new FlowLayout() );
    scrollPane = new JScrollPane( FPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    FMainPanel = new JPanel();
    FMainPanel.add( scrollPane );
  }

  public void ReadDataFromNode() throws ModelException {
  }

  public Component GetComponent() {
    //return FMainPanel;
    return scrollPane;
  }

  public void AddGUIElement(ModelGUIElement aElement) {
    if ( !IsPrefferedSizeInit ){
      JTabbedPane parent = (JTabbedPane) scrollPane.getParent();
      scrollPane.setPreferredSize( new Dimension( (int)parent.getBoundsAt(0).getWidth(),
              (int)parent.getBoundsAt(0).getHeight() ) );
      IsPrefferedSizeInit = true;
    }
    FPanel.add( aElement.GetComponent() );
    AddElement( (ModelGUIAbstrElement) aElement );
    Rectangle bounds = aElement.GetComponent().getBounds();
    if ( ( bounds.getX() + bounds.getWidth() ) > FPanelWidth  ){
      FPanelWidth = bounds.getX() + bounds.getWidth();
    }
    if ( bounds.getY() + bounds.getHeight() > FPanelHeigth ){
      FPanelHeigth = bounds.getY() + bounds.getHeight();
    }
    FPanel.setPreferredSize( new Dimension((int)FPanelWidth, (int)FPanelHeigth) );
    FPanel.revalidate();
  }

}

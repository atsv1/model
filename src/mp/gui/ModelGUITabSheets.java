package mp.gui;

import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.*;

import mp.elements.*;

/**
 * User: atsv
 * Date: 30.09.2006
 */
public class ModelGUITabSheets extends ModelGUIAbstrElement implements ModelGUIElement {
  private JTabbedPane FTabbedPane = null;

  public ModelGUITabSheets(){
    super();
    FTabbedPane = new JTabbedPane();
  }

  public Component GetComponent() {
    return FTabbedPane;
  }

  public void AddGUIElement(ModelGUIElement aElement) {
    Node elementNode = aElement.GetNode();
    try {
      FAttrReader.SetNode( elementNode );
    } catch (ModelException e) {}
    String elementTitle = FAttrReader.GetTitle();
    FTabbedPane.addTab(elementTitle, aElement.GetComponent());
    AddElement( (ModelGUIAbstrElement) aElement );
  }

  public void SetConnector(ModelConnector connector) {
  }

  public void ReadDataFromNode() throws ModelException {
  }

}

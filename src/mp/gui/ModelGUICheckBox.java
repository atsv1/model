package mp.gui;

import mp.elements.ModelException;
import mp.parser.Operand;

import javax.swing.*;
import java.awt.*;

/**
 * Date: 09.05.2008
 */
public class ModelGUICheckBox extends ModelGUIAbstrElement implements ModelGUIElement{
  private JLabel FCaptionLabel = null;
  private JPanel FPanel;
  private JCheckBox FCheckBox = null;

  public ModelGUICheckBox(){
    FCheckBox = new JCheckBox("", false);
    FCaptionLabel = new JLabel();
    FPanel = new JPanel( new BorderLayout() );
    FPanel.add( FCaptionLabel, BorderLayout.WEST );
    FPanel.add( FCheckBox, BorderLayout.CENTER );
  }

  public void ReadDataFromNode() throws ModelException {
    if ( FNode == null ){
      return;
    }
    FAttrReader.SetNode( FNode );
    this.ReadCoordFromNode( FPanel );
    ReadCaption( FCaptionLabel );
    this.ReadConnectedParamInfo();
    if ( FConnector == null ){
      ModelException e = new ModelException("Отсутствует коннектор для объекта EditBox");
      throw e;
    }
    if ( !FConnector.IsManagingEnabled( FModelName, FBlockName, FBlockIndex, FParamName ) ){
      ModelException e = new ModelException("Невозможна передача значений в параметр \"" + FParamName +
         "\" блока \"" + FBlockName + "\"");
      throw e;
    }
    int valueType = FConnector.GetValueType( FModelName, FBlockName, FBlockIndex, FParamName );
    if ( valueType != Operand.OPERAND_TYPE_BOOLEAN ) {
      ModelException e = new ModelException("Пераменная в элементе \"" + FCaptionLabel.getText() + "\" должна быть типа boolean");
      throw e;
    }
    SetIdentityName( FCaptionLabel.getText() );
  }

  public Component GetComponent() {
    return FPanel;
  }

  public void AddGUIElement(ModelGUIElement aElement) {
    AddElement( (ModelGUIAbstrElement) aElement );
  }

  public void Send() throws ModelException{
    FConnector.SendValue(FCheckBox.isSelected(), FModelName, FBlockName, FBlockIndex, FParamName);
  }

}

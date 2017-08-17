package mp.gui;


import mp.elements.*;

import javax.swing.*;
import java.awt.*;

/**
 * User: atsv
 * Date: 30.09.2006
 */
public class ModelGUIEditBox extends ModelGUIAbstrElement implements ModelGUIElement{

  private JTextField FValueEdit = null;
  private JLabel FCaptionLabel = null;
  private JPanel FPanel;
  private boolean FIsInitValue = false;

  public ModelGUIEditBox(){
    FValueEdit = new JTextField("");
    //FValueEdit.setSize(80, 5);
    FCaptionLabel = new JLabel();
    FPanel = new JPanel( new BorderLayout() );
    FPanel.add( FCaptionLabel, BorderLayout.WEST );
    FPanel.add( FValueEdit, BorderLayout.CENTER );
  }

  public Component GetComponent() {
    return FPanel;
  }

  public void AddGUIElement(ModelGUIElement aElement) {
    AddElement( (ModelGUIAbstrElement) aElement );
  }

  public void Update() throws ModelException {
    if ( FIsInitValue ) {
      return;
    }
    String s = Double.toString( FConnector.GetValue( FAddress ) );
    FValueEdit.setText( s );
    FIsInitValue = true;
  }

  public void ReadDataFromNode() throws ModelException{    
    this.ReadCoordFromNode( FPanel );
    ReadCaption( FCaptionLabel );
    try{
      this.ReadConnectedParamInfo();
    } catch (ModelException e){
       ModelException e1 = new ModelException( "Ошибка в поле ввода \"" + FCaptionLabel.getText() + "\": " + e.getMessage() );
      throw e1;
    }
    if ( FConnector == null ){
      ModelException e = new ModelException("Отсутствует коннектор для объекта EditBox");
      throw e;
    }
    if ( !FConnector.IsManagingEnabled( FModelName, FBlockName, FBlockIndex, FParamName ) ){
      ModelException e = new ModelException("Невозможна передача значений в параметр \"" + FParamName +
         "\" блока \"" + FBlockName + "\"");
      throw e;
    }
    SetIdentityName( FCaptionLabel.getText() );
  }

  private boolean IsContinueInput( ){
    int answer = JOptionPane.showConfirmDialog( FPanel, "Неправильно введено значение \n Блок " + FBlockName + " параметр " + FParamName,
            "Хотите продолжить ввод?",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
    return answer == JOptionPane.YES_OPTION;
  }

  public void Send() throws ModelException{
    double v;
    boolean f = true;
    while ( f ){
      try{
        v = Double.parseDouble( FValueEdit.getText() );
        FConnector.SendValue( v, FModelName, FBlockName, FBlockIndex, FParamName );
        f = false;
      } catch (Exception e){
        if ( !IsContinueInput() ){
          f = false;
        }
      }
    }

  }

}

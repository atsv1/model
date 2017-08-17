package mp.gui;

import mp.elements.*;
import mp.parser.Operand;

import javax.swing.*;
import java.awt.*;

/**
 * User: atsv
 * Date: 30.09.2006
 */
public class ModelGUILabel extends ModelGUIAbstrElement implements ModelGUIElement{
  private JLabel FValueLabel = null;
  private JLabel FCaptionLabel = null;
  private JPanel FPanel;
  private double FCurrentValue = 0;
  

  public ModelGUILabel(){
    FValueLabel = new JLabel("0.0");
    FCaptionLabel = new JLabel();
    FPanel = new JPanel( new BorderLayout() );
    FPanel.add( FCaptionLabel, BorderLayout.WEST );
    FPanel.add( FValueLabel, BorderLayout.CENTER );
  }

  public Component GetComponent() {
    return FPanel;
  }

  public void AddGUIElement(ModelGUIElement aElement) {
    AddElement( (ModelGUIAbstrElement) aElement );
  }

  public void Update() throws ModelException {
    if ( !FIsConnected ){
      ReadConnectedParamInfo();
    }
    //value = FConnector.GetValue(FBlockName, FBlockIndex, FParamName);
    /*FCurrentValue = FAddress.GetValue( FConnector );
    FValueLabel.setText( Double.toString( FCurrentValue ) );*/
    String s = this.GetStringValue();
    FValueLabel.setText( s );
    try{
      FCurrentValue = Double.parseDouble( s );
    } catch (NumberFormatException e){}
  }

  public void ReadDataFromNode() throws ModelException{    
    ReadCoordFromNode( FPanel );
    ReadCaption( FCaptionLabel );
    if ( !FIsConnected ){
      ReadConnectedParamInfo();
    }
  }

  public double GetCurrentValue(){
    return FCurrentValue;
  }

}

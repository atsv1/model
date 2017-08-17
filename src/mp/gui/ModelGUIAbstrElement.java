package mp.gui;


import javax.swing.*;
import java.awt.*;
import java.util.Vector;

import mp.elements.*;
import mp.utils.ModelAttributeReader;
import mp.parser.Operand;

/**
 * User: atsv
 * Date: 30.09.2006
 */
public abstract class ModelGUIAbstrElement implements ModelForReadInterface, ModelGUIElement{
  protected ModelConnector FConnector = null;
    
  protected String FModelName = null;
  protected String FBlockName = null;
  protected String FParamName = null;
  protected String FBlockIndexValue = null;
  protected int FBlockIndex = -1;
  protected boolean FIsConnected = false;
  protected Vector FElementVector = null;
  protected ModelAddress FAddress = null;
  protected int FValueType = -1;
  private String FIdentityName = null;
  private ModelElementDataSource elementSource = null;

  public ModelGUIAbstrElement(){
      
  }

  

  public void SetConnector(ModelConnector connector) {
    FConnector = connector;
  }

  protected void ReadCoordFromNode( Component aComponent ) throws ModelException{
    Rectangle r = elementSource.GetRectangle();
    if ( r == null ){
      ModelException e = new ModelException("Неверно указаны координаты для элемента " + elementSource.GetAttrName());
      throw e;
    }
    aComponent.setBounds( r );
  }

  protected void ReadCaption(JLabel aLabel){
    String s = elementSource.GetCaption();
    aLabel.setText( s + "   ");
  }

  protected String GetCaption() throws ModelException {
    
    return elementSource.GetCaption();
  }

  protected String GetName() throws ModelException {
    return elementSource.GetAttrName();
  }

  protected ModelAddress GetAddress(){
    String blockname = elementSource.GetBlockName();
    String paramName = elementSource.GetParamName();
    String blockIndex = elementSource.GetBlockIndex();
    String modelName = elementSource.GetModelAttrValue();
    return new ModelAddress( modelName, blockname, FConnector.GetBlockIndex( blockIndex ), paramName  );
  }

  protected void ReadConnectedParamInfo() throws ModelException{
    if ( FConnector == null ){
      return;
    }
    FAddress = GetAddress();
    FBlockName = FAddress.GetBlockName();
    FParamName = FAddress.GetParamName();
    FBlockIndexValue = elementSource.GetBlockIndex();
    FBlockIndex = FAddress.GetBlockIndex();
    FModelName = FAddress.GetModelName();
    if (!FConnector.IsConnectionEnabled( FModelName, FBlockName, FBlockIndex, FParamName ) ){
      ModelException e = new ModelException("Запрещено подключаться  к параметру " + FParamName + " блока " + FBlockName);
      throw e;
    }
    FIsConnected = true;
    FValueType = FConnector.GetValueType( FModelName, FBlockName, FBlockIndex, FParamName );
  }

  public abstract void ReadDataFromNode() throws ModelException;

  public void AddElement( ModelGUIAbstrElement aElement ){
    if ( FElementVector == null ){
      FElementVector = new Vector();
    }
    FElementVector.add( aElement );
  }

  public ModelGUIAbstrElement GetElement(int index){
    return (ModelGUIAbstrElement)FElementVector.get( index );
  }

  public int size(){
    if ( FElementVector == null ){
      FElementVector = new Vector();
      return 0;
    }
    return FElementVector.size();
  }

  public void SetModelAddress( ModelAddress address ){
    FAddress = address;
    FIsConnected = true;
  }

  /**Метод вызывается для обновления визуального содержания компонента
   *
   * @throws ModelException
   */
  public void Update() throws ModelException{
    int i = 0;
    ModelGUIAbstrElement element;
    while ( i < size() ){
      element = GetElement( i );
      element.Update();
      i++;
    }
  }

  public void Send() throws ModelException{
    int i = 0;
    ModelGUIAbstrElement element;
    while ( i < size() ){
      element = GetElement( i );
      element.Send();
      i++;
    }

  }

  public Vector GetElementList(){
    return FElementVector;
  }
 
  public String GetStringValue() throws ModelException {
    String result = null;
    switch ( FValueType ){
      case Operand.OPERAND_TYPE_BOOLEAN:{
        boolean b = FConnector.GetBooleanValue( FAddress );
        if ( b ) {
          result = "true";
        } else
        {
          result = "false";
        }
        break;
      }
      case Operand.OPERAND_TYPE_INTEGER: {
        int i = FConnector.GetIntValue( FAddress );
        result = Integer.toString( i );
        break;
      }
      case Operand.OPERAND_TYPE_STRING:{
        result = FConnector.GetStringValue( FAddress );
        break;
      }
      default:{
        double d = FConnector.GetValue( FAddress );
        result = Double.toString( d );
        break;
      }
    }//switch
    return result;
  }

  public String GetIdentityName(){
    return FIdentityName;
  }

  public void SetIdentityName( String aName ){
    FIdentityName = aName;
  }

  protected static ModelGUIAbstrElement GetElementByIdentityName( Vector aElementList, String aIdentityName ){
    if ( aElementList == null || aElementList.size() == 0 || aIdentityName == null){
      return null;
    }
    ModelGUIAbstrElement result = null;
    String idName;
    int i = 0;
    while ( i < aElementList.size() ){
      result = (ModelGUIAbstrElement)aElementList.get( i );
      if ( result != null ) {
        idName = result.GetIdentityName();
        if ( idName != null && aIdentityName.equalsIgnoreCase( idName.trim() ) )
        return result;
      }
      i++;
    }
    return null;
  } 
 
  
	@Override
	public ModelElementDataSource GetDataSource() {		
		return elementSource;
	}

	@Override
	public void SetDataSource(ModelElementDataSource dataSource) {
		elementSource = dataSource;
	}



}

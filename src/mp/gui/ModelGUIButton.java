package mp.gui;

import mp.elements.ModelException;
import mp.elements.ModelAddress;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * User: Администратор
 * Date: 19.05.2008
 */
public class ModelGUIButton extends ModelGUIAbstrElement implements ModelGUIElement{
  private JButton FButton = null;
  private Vector FOwnerElements = null;
  private Vector FCaptionList = new Vector();
  private Vector FElementsToSend = new Vector();
  private boolean FIsListPrepared = false;
  private String FErrorString = null;
  private Vector FEventList;

  public ModelGUIButton(){
    FButton = new JButton("");
    FButton.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          PressButton();
        } catch (ModelException e1) {
          //e1.printStackTrace();
          FErrorString = e1.getMessage();
        }
      }
    } );
  }

  private void ReadEventNode( Node aEventNode ) throws ModelException{
    FAttrReader.SetNode( aEventNode );
    ModelAddress addr = GetAddress();
    String eventName = FAttrReader.GetEventName();
    if ( eventName == null || "".equalsIgnoreCase( eventName ) ){
      ModelException e = new ModelException("Пустое название обработчика соьытий в кнопке \"" + FButton.getText() + "\"");
      throw e;
    }
    EventRecord newRec = new EventRecord();
    newRec.FAddress = addr;
    newRec.FEventName = eventName;
    if ( FEventList == null ){
      FEventList = new Vector();
    }
    FEventList.add( newRec );
  }

  /**Читаем описание действий, совершаемых при нажатии на кнопку. При нажатии на кнопку совершаются
   * 2 типа действий - передача в модель некоторых значений и вызов у выбранных пользователем блоков
   * определенных обработчиков событий.
   * За передачу в модель данных отвечают элементы Send, за вызов событий в блоке отвечают
   * элементы Event
   * @throws ModelException
   */
  private void ReadSendedValues() throws ModelException{
    FAttrReader.SetNode( FNode );
    NodeList nodes = FNode.getChildNodes();
    if ( (nodes == null) || (nodes.getLength() == 0) ) {
      return;
    }
    Node childNode;
    int i = 0;
    String s;
    while ( i < nodes.getLength() ) {
      childNode = nodes.item( i );
      if ( childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("Send") ) {
        FAttrReader.SetNode( childNode );
        s = FAttrReader.GetCaption();
        if ( s == null || "".equalsIgnoreCase( s )){
          ModelException e = new ModelException("Пустое название передаваемого элемента в кнопке \"" +
                  FButton.getText() + "\"");
          throw e;
        }
        FCaptionList.add( s );
        //System.out.println( s );
      }
      if ( childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("Event") ) {
        ReadEventNode( childNode );  
      }
      i++;
    }
  }

  private void FillElementList() throws ModelException {
    int i = 0;
    if ( FCaptionList == null || FCaptionList.size() == 0 ){
      return;
    }
    if ( FOwnerElements == null ){
      return;
    }
    String caption;
    ModelGUIAbstrElement element;
    while ( i < FCaptionList.size() ) {
      caption = (String)FCaptionList.get( i );
      element = ModelGUIAbstrElement.GetElementByIdentityName( FOwnerElements, caption );
      if ( element == null ) {
        ModelException e = new ModelException("Отсутствует элемент \"" + caption + "\" для кнопки \"" +
                FButton.getText() + "\"");
        throw e;
      }
      if ( element instanceof ModelGUIEditBox || element instanceof ModelGUICheckBox ){
        FElementsToSend.add( element );
      } else {
        ModelException e = new ModelException("Ошибка в кнопке \"" + FButton.getText() + "\": элемент \""
                + caption + "\" не может передавать данные в модель");
        throw e;
      }
      i++;
    }//while
  }

  private void SendValuesToModel() throws ModelException {
    if ( !FIsListPrepared){
      FillElementList();
      FIsListPrepared = true;
    }
    if ( FElementsToSend.size() == 0 ) {
      return;
    }
    int i = 0;
    ModelGUIElement element;
    while ( i < FElementsToSend.size() ){
      element = (ModelGUIElement)FElementsToSend.get( i ); 
      element.Send();
      i++;
    }
  }

  private void FireEvents() throws ModelException{
    if ( FEventList == null ){
      return;
    }
    int i = 0;
    EventRecord rec;
    while ( i < FEventList.size() ){
      rec = (EventRecord) FEventList.get( i );
      FConnector.FireBlockEvent( rec.FAddress.GetBlockName(), rec.FAddress.GetBlockIndex(), rec.FEventName );
      i++;
    }
  }

  private void PressButton() throws ModelException {
    SendValuesToModel();
    FireEvents();
  }

  public void ReadDataFromNode() throws ModelException {
    if ( FNode == null ){
      return;
    }
    FAttrReader.SetNode( FNode );
    this.ReadCoordFromNode( FButton );
    FButton.setText( GetCaption() );
    ReadSendedValues();
  }

  public Component GetComponent() {
    return FButton;
  }

  public void AddGUIElement(ModelGUIElement aElement) {
    AddElement( (ModelGUIAbstrElement) aElement );
  }

  public void Update() throws ModelException{
    if ( FErrorString == null ) {
      super.Update();
    } else {
      ModelException e = new ModelException( FErrorString );
      throw e;
    }
  }

  public void SetOwnerElementList( Vector aOwnerElements ){
    FOwnerElements = aOwnerElements;
  }

  private class EventRecord {
    ModelAddress FAddress;
    String FEventName;
  }//class
  

}

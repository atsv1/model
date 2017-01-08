package mp.gui;

import mp.elements.ModelException;
import mp.parser.Variable;
import mp.parser.ScriptException;

import javax.swing.*;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *  Класс реализует поведение таблицы, в которой в колонках - разные переменные,
 * а в строках - значения этих переменных в разных блоках
 *
 * User: atsv
 * Date: 14.05.2007
 */
public class ModelGUITable extends ModelGUIAbstrTable implements ModelGUIElement{

  private Vector FNamesList = null; //названия колонок в таблице
  private Vector FParamNamesList = null; //названия параметров блоков, которые будут выводиться в таблицу
  private Vector FRows = null;
  private Object[] FBlockIndexes = null;
  private Object[] FBlockNames = null;

  public ModelGUITable(){
    FNamesList = new Vector();
    FParamNamesList = new Vector();
    FRows = new Vector();

    //FPanel = new JPanel( new BorderLayout() );
    FPanel = new JPanel( null );
  }

  protected void ReadParamNames( Vector aColumnsVector, Vector aParamsVector ) throws ModelException{
    Node columnNamesNode = GetChildNode( GetNode(), "ColumnsList" );
    if ( columnNamesNode == null ){
      ModelException e = new ModelException("Ошибка при чтении таблицы \"" + FCaption.getText() +
              "\" отсутствует описание колонок таблицы");
      throw e;
    }
    NodeList nodes = columnNamesNode.getChildNodes();
    int i = 0;
    Node currentNode = null;
    while ( i < nodes.getLength() ) {
      currentNode = nodes.item( i );
      if ( currentNode.getNodeType() == Node.ELEMENT_NODE &&
              "Column".equalsIgnoreCase( currentNode.getNodeName() )  ) {
        FAttrReader.SetNode( currentNode );
        aColumnsVector.add( FAttrReader.GetCaption() );
        aParamsVector.add( FAttrReader.GetParamName() );
      }
      i++;
    }
  }

  /**Создание массива, который обеспечивает таблицу данными.
   * Заполняется переданный в метод параметр
   *
   */
  protected void CreateRows( Vector aDataRows, Object aColumnsContainer, Object aRowsContainer ) throws ModelException{
    int i = 0;
    int j = 0;
    if ( aDataRows == null ){
      ModelException e = new ModelException( "Внутренняя ошибка в таблице \"" + FCaption.getText() +
              "\": пустой массив для заполнения данными");
      throw e;
    }
    aDataRows.clear();
    Vector currentRow = null;
    Object[] blockIndexes = (Object[]) aRowsContainer;
    Vector paramNamesList = (Vector) aColumnsContainer;
    String s = null;//строка для каждого конкретного данного
    //организуем цикл по всем блокам, указанным пользователем
    while ( i < blockIndexes.length ){
      currentRow = new Vector();
      j = 0;
      //цикл по всем
      while ( j < paramNamesList.size() ){
        s = Integer.toString((j+1) * (i+1) );
        currentRow.add( s );
        j++;
      }
      aDataRows.add( currentRow );
      i++;
    }
  }

  public void ReadDataFromNode() throws ModelException {
    FAttrReader.SetNode( GetNode() );
    FCaption = new JLabel("");
    FCaption.setText( FAttrReader.GetCaption() );

    // читаем названия колонок
    ReadParamNames( FNamesList, FParamNamesList );
    //читаем список блоков, из которых будут браться данные для таблицы
    Object[] container = new Object[2];
    ReadBlockList( container );
    FBlockIndexes = (Object[]) container[0];
    FBlockNames = (Object[]) container[1];
    // создаем массив с данными
    CreateRows( FRows, FParamNamesList, FBlockIndexes );

    FTable = new JTable(  FRows, FNamesList );

    ComponentPlacing();
    ReadFilterParam();
  }

  public void AddGUIElement(ModelGUIElement aElement) {
  }

  private void StaticUpdate() throws ModelException {
    int row = 0;
    int column = 0;
    if ( FBlockNames == null ){
      return;
    }
    String currentBlock = null;
    int currentBlockIndex = -1;
    String currentParamName = null;
    String strValue;
    while ( row < FBlockNames.length ){
      currentBlock = (String) FBlockNames[row];
      currentBlockIndex = (Integer) FBlockIndexes[row];
      column = 0;
      while ( column < FParamNamesList.size() ){
        currentParamName = (String) FParamNamesList.get( column );
        strValue = FConnector.GetStringValue( FAddress.GetModelName(), currentBlock,currentBlockIndex,currentParamName );
        StoreValue( strValue , column, row,  FRows );
        column++;
      }
      row++;
    }
  }

  private Vector GetRow( String aBlockName, int aBlockIndex ) throws ModelException {
    int paramsCount = FParamNamesList.size();
    Vector result = new Vector( paramsCount );
    String currentParamName;
    int column = 0;
    String strValue;
    while ( column < paramsCount ){
      currentParamName = (String) FParamNamesList.get( column );
      strValue = FConnector.GetStringValue( FAddress.GetModelName(), aBlockName,aBlockIndex,currentParamName );
      result.add( strValue );
      column++;
    }
    return result;
  }

  /**Обновление содержимого таблицы при наличии фильтра, т.е. когда не все заявленные блоки могут попасть
   * в таблицу
   *
   * @throws ModelException
   */
  private void DynamicUpdate() throws ModelException{
    int row = 0;
    if ( FBlockNames == null ){
      return;
    }
    String currentBlock = null;
    int currentBlockIndex = -1;
    Vector tempRows = new Vector( FBlockNames.length );
    int compareResult;
    while ( row < FBlockNames.length ){
      currentBlock = (String) FBlockNames[row];
      currentBlockIndex = (Integer) FBlockIndexes[row];
      compareResult = FConnector.Compare( FFilterValue, FAddress.GetModelName(), currentBlock, currentBlockIndex, FFilteredParamName );
      if ( compareResult == 0 ){
        tempRows.add( GetRow( currentBlock, currentBlockIndex ) );
      }
      row++;
    }//while

    FRows.clear();
    int i = 0;
    int newRowsCount = tempRows.size();
    while ( i < newRowsCount ){
      FRows.add( tempRows.get( i ) );
      i++;
    }
  }

  protected void UpdateCell() throws ModelException {
    if ( FIsFilterExist ) {
      DynamicUpdate();
      FTable.updateUI();
    } else {
      StaticUpdate();
    }

  }


}

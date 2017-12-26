package mp.gui;

import mp.elements.ModelElementDataSource;
import mp.elements.ModelException;
import mp.gui.ModelGUIAbstrTable.BlockData;
import mp.parser.Variable;
import mp.parser.ScriptException;

import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  
  Map<String, BlockData> blockMetaData = null;

  public ModelGUITable(){
    FNamesList = new Vector();
    FParamNamesList = new Vector();
    FRows = new Vector();

    //FPanel = new JPanel( new BorderLayout() );
    FPanel = new JPanel( null );
  }

  protected void ReadParamNames( Vector aColumnsVector, Vector aParamsVector ) throws ModelException{    
    ModelElementDataSource columnNamesNode = this.GetDataSource().GetChildElement("ColumnsList");
    if ( columnNamesNode == null ){;
      ModelException e = new ModelException("Ошибка при чтении таблицы \"" + FCaption.getText() +
              "\" отсутствует описание колонок таблицы");
      throw e;
    }
    java.util.List<ModelElementDataSource> columnElements = columnNamesNode.GetChildElements("Column");
    for (ModelElementDataSource column : columnElements) {
    	aColumnsVector.add( column.GetCaption() );
      aParamsVector.add( column.GetParamName() );
    }
  }

  /**Создание массива, который обеспечивает таблицу данными.
   * Заполняется переданный в метод параметр
   *
   */
  protected void CreateRows( Vector aDataRows, Object aColumnsContainer, List<BlockData> aRowsContainer ) throws ModelException{
    int i = 0;
    int j = 0;
    if ( aDataRows == null ){
      ModelException e = new ModelException( "Внутренняя ошибка в таблице \"" + FCaption.getText() +
              "\": пустой массив для заполнения данными");
      throw e;
    }
    aDataRows.clear();
    Vector currentRow = null;
    //Object[] blockIndexes = (Object[]) aRowsContainer;
    Vector paramNamesList = (Vector) aColumnsContainer;
    String s = null;//строка для каждого конкретного данного
    //организуем цикл по всем блокам, указанным пользователем
    for (BlockData bd : aRowsContainer){
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
    FCaption = new JLabel("");
    FCaption.setText( this.GetDataSource().GetCaption() );

    // читаем названия колонок
    ReadParamNames( FNamesList, FParamNamesList );
    //читаем список блоков, из которых будут браться данные для таблицы    
    ReadBlockList( );    
    // создаем массив с данными
    CreateRows( FRows, FParamNamesList, blocks );
    FTable = new JTable(  FRows, FNamesList );
    ComponentPlacing();
    ReadFilterParam();
  }

  public void AddGUIElement(ModelGUIElement aElement) {
  }
  
  
  
  protected boolean updateBlockList() throws ModelException{
  	if ( !isBlockCountChange() ) {
  		return false;
  	}
  	// проверяем, изменилось ли
  	try {  		
  		int curBlockCount = FRows.size();
			int newBlockCount = blocks.size();			
			while ( newBlockCount > curBlockCount ){
			  BlockData bd =blocks.get(curBlockCount);
				Vector newRow = GetRow(bd);
				FRows.addElement(newRow);
				curBlockCount++;			  		
			}					
		} catch (ModelException e) {
			e.printStackTrace();
		}
  	return true;
  }
  
  @Override
  protected boolean  StaticUpdate() throws ModelException {
    int row = 0;
    int column = 0;
    if ( blocks == null ){
      return false; 
    }
    boolean result = updateBlockList();
    
    String currentBlock = null;
    int currentBlockIndex = -1;
    String currentParamName = null;
    String strValue;
    for (BlockData bd : blocks){
      currentBlock = bd.blockName;
      currentBlockIndex = bd.blockIndex;
      column = 0;
      while ( column < FParamNamesList.size() ){
        currentParamName = (String) FParamNamesList.get( column );
        strValue = FConnector.GetStringValue( FAddress.GetModelName(), currentBlock,currentBlockIndex,currentParamName );
        StoreValue( strValue , column, row,  FRows );
        column++;
      }
      row++;
    }
    return result;
  }

  private Vector GetRow( BlockData blockData ) throws ModelException {
    int paramsCount = FParamNamesList.size();
    Vector result = new Vector( paramsCount );
    String currentParamName;
    int column = 0;
    String strValue;
    while ( column < paramsCount ){
      currentParamName = (String) FParamNamesList.get( column );
      strValue = FConnector.GetStringValue( FAddress.GetModelName(), blockData.blockName, blockData.blockIndex,currentParamName );
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
  @Override
  protected void DynamicUpdate() throws ModelException{
    int row = 0;
    if ( blocks == null ){
      return;
    }
    String currentBlock = null;
    int currentBlockIndex = -1;
    Vector tempRows = new Vector( blocks.size() );
    int compareResult;
    for (BlockData bd : blocks) {
      currentBlock = bd.blockName;
      currentBlockIndex = bd.blockIndex;
      compareResult = FConnector.Compare( FFilterValue, FAddress.GetModelName(), currentBlock, currentBlockIndex, FFilteredParamName );
      if ( compareResult == 0 ){
        tempRows.add( GetRow( bd ) );
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
  


}

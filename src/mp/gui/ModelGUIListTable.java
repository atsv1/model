package mp.gui;

import mp.elements.ModelElementDataSource;
import mp.elements.ModelException;

import javax.swing.*;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.util.Vector;


/**
 * User: atsv
 * Date: 26.05.2007
 *  ласс реализует поведение и представление таблицы, создаваемой на основании элемента "ListTable".
 *
 *
 */
public class ModelGUIListTable extends ModelGUIAbstrTable implements ModelGUIElement{
  private Vector FCaptionList = null;
  private Vector FParamNamesList = null;
  
  private Vector FRows = null;
  private Vector FColumnCaptionList = null;
  private String[] FFilteredBlockNames = null;
  private int[] FFilteredBlockIndexes = null;

  public ModelGUIListTable(){
    FPanel = new JPanel( null );
  }

  public void ReadDataFromNode() throws ModelException {    
    FCaption = new JLabel("");
    FCaption.setText( this.GetDataSource().GetCaption() );

    // читаем информацию о строках
    FCaptionList = new Vector();
    FParamNamesList = new Vector();
    ReadParamNames( FCaptionList, FParamNamesList );

    //читаем информацию о блоках таблицы    
    ReadBlockList();    
    FColumnCaptionList = new Vector();
    CreateColumnsCaptionList();

    FRows = new Vector();
    CreateRows( FRows, FColumnCaptionList, FCaptionList );

    FTable = new JTable(  FRows, FColumnCaptionList );

    ComponentPlacing();
    ReadFilterParam();
  }

  /**—оздание списка заголовков колонок дл€ таблицы. «аголовок колонки состоит из название блока и его номера (значени€
   * параметра selfIndex блока).
   * ѕри работе используютс€ переменные FBlockNames и FBlockIndexes.
   * –езультат работы метода хранитс€ в пемеременной FColumnCaptionList
   *
   */
  private void CreateColumnsCaptionList(){
    FColumnCaptionList.clear();
    int i = 0;
    String blockName = null;
    Integer blockIndex = null;
    FColumnCaptionList.add("");//добавл€ем одну колонку дл€ названий параметров    
    for (BlockData bd : blocks){      
      FColumnCaptionList.add( bd.blockName + "[" + (bd.blockIndex) + "]" );
      i++;
    }
  }

  public void AddGUIElement(ModelGUIElement aElement) {
  }

  protected void ReadParamNames(Vector aCaptionVector, Vector aParamsVector) throws ModelException {
    ModelElementDataSource columnNamesNode = this.GetDataSource().GetChildElement("RowsList");  
    if ( columnNamesNode == null ){
    	throw new ModelException("ќшибка  при чтении таблицы \"" + FCaption.getText() +  "\" отсутствует описание строк таблицы");      
    }
    java.util.List<ModelElementDataSource> rows = columnNamesNode.GetChildElements("Row");
    for (ModelElementDataSource row : rows) {
    	aCaptionVector.add( row.GetCaption() );
      aParamsVector.add( row.GetParamName() );
    }    
  }

  /**—оздание строк в таблице. —троками в таблице €вл€ютс€ названи€ параметров
   *
   * @param aDataRows ¬ектор, в котором будут создаватьс€ строки с данными. Ётот параметр будет измене  в ходе
   * выполнени€ процедуры
   * @param aColumnsContainer - —писок названий колонок
   * @param aRowsContainer - список строк (названий )
   * @throws ModelException
   */
  protected void CreateRows(Vector aDataRows, Object aColumnsContainer, Object aRowsContainer) throws ModelException {
    aDataRows.clear();
    int column = 0;
    int row = 0;
    Vector rowContainer = (Vector) aRowsContainer;
    Vector columnsContainer = (Vector) aColumnsContainer;
    Vector currentRow = null;
    while ( row < rowContainer.size() ){
      currentRow = new Vector();
      currentRow.add( rowContainer.get( row ) );
      column = 0;
      while ( column < columnsContainer.size() ){
        currentRow.add("");
        column++;
      }
      aDataRows.add( currentRow );
      row++;
    }
  }

  private void updateColumns(){
  	int rowNum = 0;
  	int currentBlokCount = blocks.size();
  	BlockData bd =  blocks.get(0);
  	boolean columnNotAdded = true;
  	while ( rowNum < FRows.size() ){
      Vector currentRow = (Vector) FRows.get( rowNum );
      int blockCount = currentRow.size();
      while ( (blockCount-1) < currentBlokCount ) {
      	currentRow.add("new column");
      	if (columnNotAdded) {      		
      	  TableColumn tc = new TableColumn();
      	  tc.setModelIndex(blockCount);
      	  tc.setHeaderValue(bd.blockName + "[" + (blockCount-1) + "]");
      	  FTable.getColumnModel().addColumn( tc );      	  
      	  columnNotAdded = false;
      	}
      	blockCount++;
      }
      rowNum++;
  	}
  	
  }
  
  @Override
  protected boolean StaticUpdate() throws ModelException {
    if ( FRows == null ){
      return false;
    }
    int row = 0;
    int column = 0;
    Vector currentRow = null;
    Integer blockIndex;
    String blockName;
    String paramName;
    double value;
    String strValue;
    boolean result = this.isBlockCountChange();
    if ( result ) {
    	updateColumns();    	
    }
    while ( row < FRows.size() ){
      currentRow = (Vector) FRows.get( row );
      String rowName = (String) currentRow.get(0);
      column = 1;
      for (BlockData bd : blocks){
        blockIndex = bd.blockIndex;
        blockName = bd.blockName;
        paramName = (String) FParamNamesList.get( row );
        //value = FConnector.GetValue( blockName, blockIndex.intValue(), paramName );
        strValue = FConnector.GetStringValue(FAddress.GetModelName(),  blockName, blockIndex, paramName );
        StoreValue( strValue, column, row,  FRows );
        column++;
      }      
      row++;
    }    
    return result;
  }

  private int FillFilteredBlockInfo() throws ModelException {
    int filteredBlockCount = 0;
    int i = 0;
    int compareRes = -1;
    String currentBlock;
    Integer currentBlockIndex;
    for (BlockData bd : blocks) {
      
      compareRes = FConnector.Compare( FFilterValue, FModelName, bd.blockName, bd.blockIndex, FFilteredParamName );
      if ( compareRes == 0 ){
        FFilteredBlockNames[ filteredBlockCount ] = bd.blockName;
        FFilteredBlockIndexes[ filteredBlockCount ] = bd.blockIndex;
        filteredBlockCount++;
      }
      i++;
    }
    return filteredBlockCount;
  }

  private Vector GetRow( String aParamName ) throws ModelException {
    int blockCount = FillFilteredBlockInfo();
    Vector result = new Vector( blockCount );
    String currentBlock;
    int currentBlockIndex;
    int i = 0;
    while ( i < blockCount ){
      currentBlock = FFilteredBlockNames[ i ];
      currentBlockIndex = FFilteredBlockIndexes[ i ];
      result.add( FConnector.GetStringValue(FAddress.GetModelName(),  currentBlock, currentBlockIndex, aParamName ) );
      i++;
    }

    return result;
  }

  @Override
  protected  void  DynamicUpdate() throws ModelException {
    Vector newRows = new Vector();
    int i = 0;
    int paramCount = FParamNamesList.size();
    String paramName;
    while ( i < paramCount ){
      paramName = (String) FParamNamesList.get( i );
      newRows.add( GetRow(paramName) );
      i++;
    }
    FRows.clear();
    i = 0;
    int newRowsCount = newRows.size();
    while ( i < newRowsCount ){
      FRows.add( newRows.get( i ) );
      i++;
    }
     
  }

  

}

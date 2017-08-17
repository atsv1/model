package mp.gui;

import mp.elements.ModelElementDataSource;
import mp.elements.ModelException;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;


/**
 * User: atsv
 * Date: 26.05.2007
 * ����� ��������� ��������� � ������������� �������, ����������� �� ��������� �������� "ListTable".
 *
 *
 */
public class ModelGUIListTable extends ModelGUIAbstrTable implements ModelGUIElement{
  private Vector FCaptionList = null;
  private Vector FParamNamesList = null;
  private Object[] FBlockIndexes = null;
  private Object[] FBlockNames = null;
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

    // ������ ���������� � �������
    FCaptionList = new Vector();
    FParamNamesList = new Vector();
    ReadParamNames( FCaptionList, FParamNamesList );

    //������ ���������� � ������ �������
    Object[] container = new Object[2];
    ReadBlockList( container );
    FBlockIndexes = (Object[]) container[0];
    FBlockNames = (Object[]) container[1];
    FColumnCaptionList = new Vector();
    CreateColumnsCaptionList();

    FRows = new Vector();
    CreateRows( FRows, FColumnCaptionList, FCaptionList );

    FTable = new JTable(  FRows, FColumnCaptionList );

    ComponentPlacing();
    ReadFilterParam();
  }

  /**�������� ������ ���������� ������� ��� �������. ��������� ������� ������� �� �������� ����� � ��� ������ (��������
   * ��������� selfIndex �����).
   * ��� ������ ������������ ���������� FBlockNames � FBlockIndexes.
   * ��������� ������ ������ �������� � ������������ FColumnCaptionList
   *
   */
  private void CreateColumnsCaptionList(){
    FColumnCaptionList.clear();
    int i = 0;
    String blockName = null;
    Integer blockIndex = null;
    FColumnCaptionList.add("");//��������� ���� ������� ��� �������� ����������
    while ( i < FBlockNames.length ){
      blockName = (String) FBlockNames[i];
      blockIndex = (Integer) FBlockIndexes[i];
      FColumnCaptionList.add( blockName + "[" + Integer.toString(blockIndex.intValue() ) + "]" );
      i++;
    }
  }

  public void AddGUIElement(ModelGUIElement aElement) {
  }

  protected void ReadParamNames(Vector aCaptionVector, Vector aParamsVector) throws ModelException {
    ModelElementDataSource columnNamesNode = this.GetDataSource().GetChildElement("RowsList");  
    if ( columnNamesNode == null ){
    	throw new ModelException("������  ��� ������ ������� \"" + FCaption.getText() +  "\" ����������� �������� ����� �������");      
    }
    java.util.List<ModelElementDataSource> rows = columnNamesNode.GetChildElements("Row");
    for (ModelElementDataSource row : rows) {
    	aCaptionVector.add( row.GetCaption() );
      aParamsVector.add( row.GetParamName() );
    }    
  }

  /**�������� ����� � �������. �������� � ������� �������� �������� ����������
   *
   * @param aDataRows ������, � ������� ����� ����������� ������ � �������. ���� �������� ����� ������  � ����
   * ���������� ���������
   * @param aColumnsContainer - ������ �������� �������
   * @param aRowsContainer - ������ ����� (�������� )
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

  private void StaticUpdate() throws ModelException {
    if ( FRows == null ){
      return;
    }
    int row = 0;
    int column = 0;
    Vector currentRow = null;
    Integer blockIndex;
    String blockName;
    String paramName;
    double value;
    String strValue;
    while ( row < FRows.size() ){
      currentRow = (Vector) FRows.get( row );
      column = 1;
      while ( column < currentRow.size() ){
        blockIndex = (Integer) FBlockIndexes[column-1];
        blockName = (String) FBlockNames[column-1];
        paramName = (String) FParamNamesList.get( row );
        //value = FConnector.GetValue( blockName, blockIndex.intValue(), paramName );
        strValue = FConnector.GetStringValue(FAddress.GetModelName(),  blockName, blockIndex, paramName );
        StoreValue( strValue, column, row,  FRows );
        column++;
      }
      row++;
    }
  }

  private int FillFilteredBlockInfo() throws ModelException {
    int filteredBlockCount = 0;
    int i = 0;
    int compareRes = -1;
    String currentBlock;
    Integer currentBlockIndex;
    while ( i < FBlockNames.length ){
      currentBlock = (String) FBlockNames[i];
      currentBlockIndex = (Integer) FBlockIndexes[i];
      compareRes = FConnector.Compare( FFilterValue, FModelName, currentBlock, currentBlockIndex, FFilteredParamName );
      if ( compareRes == 0 ){
        FFilteredBlockNames[ filteredBlockCount ] = currentBlock;
        FFilteredBlockIndexes[ filteredBlockCount ] = currentBlockIndex;
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

  private void DynamicUpdate() throws ModelException {
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

  protected void UpdateCell() throws ModelException {
    if ( FIsFilterExist ) {
      DynamicUpdate();
      FTable.updateUI();
    } else {
      StaticUpdate();
    }

  }

}

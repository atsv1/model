package mp.gui;

import mp.elements.ModelElementDataSource;
import mp.elements.ModelException;
import mp.gui.ModelGUIAbstrTable.BlockData;
import mp.parser.Variable;
import mp.parser.ScriptException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

/**
 * User: atsv
 * Date: 26.05.2007
 */
public abstract class ModelGUIAbstrTable extends ModelGUIAbstrElement {
  protected JLabel FCaption = null;
  protected JTable FTable = null;
  protected JPanel FPanel = null;
  protected boolean FIsFilterExist = false;
  protected String FFilteredParamName = null;
  protected Variable FFilterValue = null;
  List<BlockData> blocks = null;
  
  private boolean isAllBlocks = false;

  /** ����� ��������� ���������� ����������� ������� �� �����. ��� ����� ������ ����� ��������� �������������� ������
   * FTable.
   * ����������� ���������� ������� � �� ���������
   *
   * @throws ModelException
   */
  protected void ComponentPlacing() throws ModelException {

    JScrollPane scrollPanel = new JScrollPane(FTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    //������������� ����������
    
    ReadCoordFromNode( FPanel );
    Rectangle r = this.GetDataSource().GetRectangle();
    FCaption.setBounds( new Rectangle( 0,  5, r.width-6, 20) );
    scrollPanel.setBounds( new Rectangle( 5,  25, r.width-6, r.height-25) );
    FTable.setPreferredScrollableViewportSize( new Dimension( r.width-6, r.height-25 ) );
    //������ �������� �������
    FPanel.add( FCaption /*, BorderLayout.NORTH */);
    FPanel.add(scrollPanel , BorderLayout.SOUTH );
    FIsConnected = true;
  }
  
  protected void setNewTable(JTable newTable){  	
  	Rectangle r = this.GetDataSource().GetRectangle();
  	FTable.setPreferredScrollableViewportSize( new Dimension( r.width-6, r.height-25 ) );
  	FPanel.remove(FTable);
  	
  	//FPanel.add(newTable);  	
  	FPanel.revalidate();
  	FPanel.repaint();
  	FTable = newTable;
  	
  }

   /** ������ �� ����� �������� ����� ������ ����������, �������� ������� ���������� �������� � �������.
    * �������� ��������� ����������, ������� ����� �������� � ������� � �������� ����������, �� ������� �����
    * ���������� � ������.
    *
   * @param aCaptionVector - ������ ���������� ����������. ������ ������ ���� ������ ����� ��������� ��� �
    * ���� �����
   * @param aParamsVector - ������ �������� ���������� � ������ (� �����). ������ ������ ���� ������ ����� ���������
    * ��� � �����
   * @throws ModelException
   */
  protected abstract void ReadParamNames( Vector aCaptionVector, Vector aParamsVector ) throws ModelException;

  private void AddAllBlockTotable(String aBlockName) throws ModelException {
    if ( FConnector == null ){
      ModelException e = new ModelException("����������� ������ ����������� � ������ � ������� \"" + GetName() + "\"");
      throw e;
    }
    int blockCount = FConnector.GetBlockCount( FAddress.GetModelName(), aBlockName );
    int i = 0;
    while ( i < blockCount ){
    	BlockData newBlockInfo = new BlockData();
    	newBlockInfo.blockIndex =  i ;
    	newBlockInfo.blockName = aBlockName;      
    	blocks.add(newBlockInfo);    	
      i++;
    }
  }

  /**������ �� ����� �������� ����� ������ ������
   *
   * @param aContainer - ������ ������������ 2. � ������� ������� ����� �������� ������ �� ������ ���� Object[], �
   * ������� ����� ��������� �������� �������� ������. � ������ ������� ����� �������� ������ �� ������ ����
   * Object[], � ������� ����� ��������� �������� ������. �.�. ��� ������ ����� ��������� ��������� ���������� 2
   * ��������, � �� ����, ��� ��������� ��������� Java
   * @throws ModelException
   */
  protected  void ReadBlockList() throws ModelException{
  	blocks = new ArrayList<BlockData> ();    
    ModelElementDataSource blockNode = null;
    java.util.List<ModelElementDataSource> childList = this.GetDataSource().GetChildElements("BlockList");
    if ( childList == null || childList.isEmpty()) {
    	throw new ModelException("� ������� \"" + FCaption.getText() + "\" ����������� �������� ������" );
    }
    blockNode = childList.get(0);
    if ( blockNode == null ){
      ModelException e = new ModelException("� ������� \"" + FCaption.getText() + "\" ����������� �������� ������" );
      throw e;
    }
    childList = blockNode.GetChildElements("Block");
    if ( childList == null ) {
    	return;
    }
    int blockIndex = 0;
    String blockIndexStr = null;
    for (ModelElementDataSource currentNode : childList) {
    	FAddress = GetAddress();
    	blockIndexStr = currentNode.GetBlockLinkIndex();
    	if ( blockIndexStr == null ){
        blockIndex = -1;
      } else {
      	if ( "all".equalsIgnoreCase( blockIndexStr ) ){
          AddAllBlockTotable( currentNode.GetAttrName() );
          this.setAllBlocks(true);
        } else {
        	try {        		
            blockIndex = Integer.parseInt( blockIndexStr );
        	} catch (Exception e) {
        		 throw new ModelException("������ � ����� \"" + FCaption.getText() +   "\": �������� ������ �����- " + blockIndexStr);
        	}
        	this.setAllBlocks(false);
        	BlockData newBlockInfo = new BlockData();
        	newBlockInfo.blockIndex =  blockIndex ;
        	newBlockInfo.blockName = currentNode.GetAttrName();      
        	blocks.add(newBlockInfo);
        }
      	
      }
    }    
    if ( blocks.isEmpty() ){
      ModelException e = new ModelException("������ � ����� \"" + FCaption.getText() + "\": ������ ������ ������");
      throw e;
    }
    

  }

  protected abstract boolean  StaticUpdate() throws ModelException;

  protected void UpdateCell() throws ModelException {
    if ( FIsFilterExist ) {
      DynamicUpdate();
      FTable.updateUI();
    } else {
      if (StaticUpdate() ) {
      	FTable.updateUI();
      }
    }
  }
  
  
  

  protected void StoreValue( String aValue, int column, int row, Vector aDataRows ) throws ModelException{
    if ( aDataRows == null ){
      return;
    }   
    FTable.setValueAt(aValue, row, column);
  }

  public void Update() throws ModelException {
    if ( !FIsConnected ){
      ReadDataFromNode();
    }
    //��������� ��� ������ �������
    UpdateCell();
    FTable.repaint();
  }

  public Component GetComponent() {
    return FPanel;
  }

  /**������ �� ����� � ��������� ����� ���������� � ������������� ������������� ������������
   * �������. �.�. �� ����� ����� ���������, ����� �������� ����� ��������� ���, ����� ������ ����� ����������
   * � �������, � ����� �� �����.
   * ������: � ������ ������� 100 ������, � � ������� ������ ���������� ��� ���. �� ��� ��� � ������� �� �����, �
   * ����� ��������� ������� ���� ������. ������� � ������� ������������ ��������� �������� (������������), � �����
   * ����� �������� � ������� ������ ��� ���������� ���������� �������� � ��������� �����
   *
   */
  protected void ReadFilterParam() throws ModelException {
  	ModelElementDataSource blockListNode = this.GetDataSource().GetChildElement("BlockList");
  	if (blockListNode == null) {
  		throw new ModelException("����������� ������ ������ " + FCaption.getText());
  	}
  	ModelElementDataSource filterNode =  blockListNode.GetChildElement("Filter");
  	if ( filterNode == null ){
      FIsFilterExist = false;
      return;
    }
    
    FFilteredParamName = filterNode.GetParamName();
    if ( FFilteredParamName == null || "".equalsIgnoreCase( FFilteredParamName ) ){
      ModelException e = new ModelException("������ � ������� \"" + FCaption.getText() +
         "\": ������ �������� ��������� ��� ����������");
      throw e;
    }
    String filterType = filterNode.GetFilterValueType();
    if ( filterType == null || "".equalsIgnoreCase( filterType ) ){
      ModelException e = new ModelException("������ � ������� \"" + FCaption.getText() +
         "\": ����������� ��� �������� ��� ����������");
      throw e;
    }
    String filterValue = filterNode.GetFilterValue();
    if ( filterValue == null || "".equalsIgnoreCase( filterValue ) ){
      ModelException e = new ModelException("������ � ������� \"" + FCaption.getText() +
         "\": ����������� �������� ��� ����������");
      throw e;
    }
    try {
      FFilterValue = Variable.CreateNewInstance("filter var", filterType, filterValue);
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � ������� \"" + FCaption.getText() +
         "\":" + e.getMessage());
      throw e1;
    }
    FIsFilterExist = true;
  }

	public boolean isAllBlocks() {
		return isAllBlocks;
	}

	public void setAllBlocks(boolean isAllBlocks) {
		this.isAllBlocks = isAllBlocks;
	}
	
	protected boolean isBlockCountChange() throws ModelException{
		if ( !this.isAllBlocks() ) {
  		return false;
  	}
		BlockData bd = blocks.get(0);
		String blockName = bd.blockName;
		int curBlockCount = FConnector.GetBlockCount(FModelName, blockName);
		if ( blocks.size() == curBlockCount ) {
			return false;
		}
		int blockIndex = blocks.size();
		while ( blockIndex < curBlockCount ){
			BlockData newBd = new BlockData();
			newBd.blockName = blockName;
			newBd.blockIndex = blockIndex;
			blocks.add(newBd);
			blockIndex++;
		}
		return true;
	}
	
	protected  abstract void  DynamicUpdate() throws ModelException;
	
	protected static class BlockData{
  	String blockName;
  	int blockIndex;
  	public String toString(){
  		return blockName + "[" + blockIndex + "]";
  	}
  	
  }
  
  
}

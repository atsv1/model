package mp.gui;

import mp.elements.ModelException;
import mp.parser.Variable;
import mp.parser.ScriptException;

import java.awt.*;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    FAttrReader.SetNode( GetNode() );
    ReadCoordFromNode( FPanel );
    Rectangle r = FAttrReader.GetRectangle();
    FCaption.setBounds( new Rectangle( 0,  5, r.width-6, 20) );
    scrollPanel.setBounds( new Rectangle( 5,  25, r.width-6, r.height-25) );
    FTable.setPreferredScrollableViewportSize( new Dimension( r.width-6, r.height-25 ) );
    //������ �������� �������
    FPanel.add( FCaption /*, BorderLayout.NORTH */);
    FPanel.add(scrollPanel /*, BorderLayout.SOUTH*/ );
    FIsConnected = true;
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

  private void AddAllBlockTotable(String aBlockName, Vector aBlockNamesList, Vector aBlockIndexList) throws ModelException {
    if ( FConnector == null ){
      ModelException e = new ModelException("����������� ������ ����������� � ������ � ������� \"" + GetName() + "\"");
      throw e;
    }

    int blockCount = FConnector.GetBlockCount( FAddress.GetModelName(), aBlockName );
    int i = 0;
    while ( i < blockCount ){
      aBlockNamesList.add( aBlockName );
      aBlockIndexList.add( new Integer(i) );
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
  protected  void ReadBlockList(Object[] aContainer) throws ModelException{
    if ( aContainer == null || aContainer.length != 2 ){
      ModelException e = new ModelException( "������ ������� \"" + FCaption.getText() +
              "\" ������ ��������� ��� �������� ������ ������" );
      throw e;
    }
    Vector indexList = new Vector();
    Vector namesList = new Vector();
    Node blockNode = GetChildNode( GetNode(), "BlockList" );
    if ( blockNode == null ){
      ModelException e = new ModelException("� ������� \"" + FCaption.getText() + "\" ����������� �������� ������" );
      throw e;
    }
    NodeList nodes = blockNode.getChildNodes();
    int i = 0;
    Node currentNode;
    int blockIndex = 0;
    String blockIndexStr = null;
    while ( i < nodes.getLength() ){
      currentNode = nodes.item( i );
      if ( currentNode.getNodeType() == Node.ELEMENT_NODE  &&  "Block".equalsIgnoreCase( currentNode.getNodeName() ) ){
        FAttrReader.SetNode( currentNode );
        FAddress = GetAddress();
        try {
          blockIndexStr = FAttrReader.GetBlockLinkIndex();
          if ( blockIndexStr == null ){
            blockIndex = -1;
          } else {
            if ( "all".equalsIgnoreCase( blockIndexStr ) ){
              AddAllBlockTotable( FAttrReader.GetAttrName(), namesList, indexList );
            } else {
              blockIndex = Integer.parseInt( blockIndexStr );
              indexList.add( new Integer( blockIndex ) );
              namesList.add( FAttrReader.GetAttrName() );
            }
          }
        } catch (Exception e){
          ModelException e1 = new ModelException("������ � ����� \"" + FCaption.getText() +
                  "\": �������� ������ �����- " + blockIndexStr);
          throw e1;
        }
      }
      i++;
    }
    if ( indexList.size() != namesList.size() ){
      ModelException e = new ModelException("������ � ����� \"" + FCaption.getText() +
              "\": ������������ ���������. ���������� ������ ������");
      throw e;
    }
    if ( indexList.size() == 0 ){
      ModelException e = new ModelException("������ � ����� \"" + FCaption.getText() + "\": ������ ������ ������");
      throw e;
    }
    aContainer[0] = indexList.toArray();
    aContainer[1] = namesList.toArray();

  }

  /**�������� ������� � �������, ������� ����� ������������ � �������
   *
   * @param aDataRows
   * @param aColumnsContainer
   * @param aRowsContainer
   * @throws ModelException
   */
  protected abstract void CreateRows( Vector aDataRows, Object aColumnsContainer, Object aRowsContainer ) throws ModelException;

  protected abstract void UpdateCell() throws ModelException;

  protected void StoreValue( String aValue, int column, int row, Vector aDataRows ) throws ModelException{
    if ( aDataRows == null ){
      return;
    }
    if ( row >= aDataRows.size() ){
      ModelException e = new ModelException("������ � �������� \"" + FCaption.getText() + "\": �������� ����� ������ " +
        Integer.toString( row ) );
      throw e;
    }
    Vector columns = (Vector) aDataRows.get( row );
    if ( column >= columns.size() ){
      ModelException e = new ModelException("������ � �������� \"" + FCaption.getText() + "\": �������� ����� ������� " +
         Integer.toString( column ) );
      throw e;
    }
    columns.setElementAt( aValue,column );
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
    Node blockListNode = GetChildNode( GetNode(), "BlockList" );
    Node filterNode = GetChildNode(blockListNode, "Filter" );
    if ( filterNode == null ){
      FIsFilterExist = false;
      return;
    }
    FAttrReader.SetNode( filterNode );
    FFilteredParamName = FAttrReader.GetParamName();
    if ( FFilteredParamName == null || "".equalsIgnoreCase( FFilteredParamName ) ){
      ModelException e = new ModelException("������ � ������� \"" + FCaption.getText() +
         "\": ������ �������� ��������� ��� ����������");
      throw e;
    }
    String filterType = FAttrReader.GetFilterValueType();
    if ( filterType == null || "".equalsIgnoreCase( filterType ) ){
      ModelException e = new ModelException("������ � ������� \"" + FCaption.getText() +
         "\": ����������� ��� �������� ��� ����������");
      throw e;
    }
    String filterValue = FAttrReader.GetFilterValue();
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
}

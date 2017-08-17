package mp.gui;

import mp.elements.ModelElementDataSource;
import mp.elements.ModelException;
import mp.parser.Variable;
import mp.parser.ScriptException;

import java.awt.*;
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

  /** Метод выполняет размещение компонентов таблицы на форме. Для этого методу нужен полностью подготовленный объект
   * FTable.
   * Выполняется размещение таблицы и ее заголовка
   *
   * @throws ModelException
   */
  protected void ComponentPlacing() throws ModelException {

    JScrollPane scrollPanel = new JScrollPane(FTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    //устанавливаем координаты
    
    ReadCoordFromNode( FPanel );
    Rectangle r = this.GetDataSource().GetRectangle();
    FCaption.setBounds( new Rectangle( 0,  5, r.width-6, 20) );
    scrollPanel.setBounds( new Rectangle( 5,  25, r.width-6, r.height-25) );
    FTable.setPreferredScrollableViewportSize( new Dimension( r.width-6, r.height-25 ) );
    //читаем название таблицы
    FPanel.add( FCaption /*, BorderLayout.NORTH */);
    FPanel.add(scrollPanel /*, BorderLayout.SOUTH*/ );
    FIsConnected = true;
  }

   /** Чтение из файла описания формы списка параметров, значения которых необходимо выводить в таблицу.
    * Читаются заголовки параметров, которые будут выведены в таблицу и названия параметров, по которым можно
    * обращаться к модели.
    *
   * @param aCaptionVector - список заголовков параметров. Объект должен быть создан перед передачей его в
    * этот метод
   * @param aParamsVector - список названий параметров в модели (в блоке). Объект должен быть создан перед передачей
    * его в метод
   * @throws ModelException
   */
  protected abstract void ReadParamNames( Vector aCaptionVector, Vector aParamsVector ) throws ModelException;

  private void AddAllBlockTotable(String aBlockName, Vector aBlockNamesList, Vector aBlockIndexList) throws ModelException {
    if ( FConnector == null ){
      ModelException e = new ModelException("Отсутствует объект подключения в модели в таблице \"" + GetName() + "\"");
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

  /**Чтение из файла описания формы списка блоков
   *
   * @param aContainer - массив размерностью 2. В нулевой элемент будет записана ссылка на объект типа Object[], в
   * котором будут храниться значения индексов блоков. В первый элемент будет записана ссылка на объект типа
   * Object[], в котором будут храниться названия блоков. Т.е. при помощи этого параметра процедура возвращает 2
   * значения, а не одно, как позволяет синтаксис Java
   * @throws ModelException
   */
  protected  void ReadBlockList(Object[] aContainer) throws ModelException{
    if ( aContainer == null || aContainer.length != 2 ){
      ModelException e = new ModelException( "Ошибка таблице \"" + FCaption.getText() + "\" пустой контейнер для хранения списка блоков" );
      throw e;
    }
    Vector indexList = new Vector();
    Vector namesList = new Vector();
    ModelElementDataSource blockNode = null;
    java.util.List<ModelElementDataSource> childList = this.GetDataSource().GetChildElements("BlockList");
    if ( childList == null || childList.isEmpty()) {
    	throw new ModelException("В таблице \"" + FCaption.getText() + "\" отсутствует перечень блоков" );
    }
    blockNode = childList.get(0);
    if ( blockNode == null ){
      ModelException e = new ModelException("В таблице \"" + FCaption.getText() + "\" отсутствует перечень блоков" );
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
          AddAllBlockTotable( currentNode.GetAttrName(), namesList, indexList );
        } else {
        	try {
          blockIndex = Integer.parseInt( blockIndexStr );
        	} catch (Exception e) {
        		 throw new ModelException("Ошибка в блоке \"" + FCaption.getText() +   "\": неверный индекс блока- " + blockIndexStr);
        	}
          indexList.add( new Integer( blockIndex ) );
          namesList.add( currentNode.GetAttrName() );
        }
      	
      }
    }
    if ( indexList.size() != namesList.size() ){
      ModelException e = new ModelException("Ошибка в блоке \"" + FCaption.getText() +
              "\": несовпадение количеств. Внутренняя ошибка модели");
      throw e;
    }
    if ( indexList.size() == 0 ){
      ModelException e = new ModelException("Ошибка в блоке \"" + FCaption.getText() + "\": пустой список блоков");
      throw e;
    }
    aContainer[0] = indexList.toArray();
    aContainer[1] = namesList.toArray();

  }

  /**Создание массива с данными, который будет передаваться в таблицу
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
      ModelException e = new ModelException("Ошибка в элементе \"" + FCaption.getText() + "\": неверный номер строки " +
        Integer.toString( row ) );
      throw e;
    }
    Vector columns = (Vector) aDataRows.get( row );
    if ( column >= columns.size() ){
      ModelException e = new ModelException("Ошибка в элементе \"" + FCaption.getText() + "\": неверный номер столбца " +
         Integer.toString( column ) );
      throw e;
    }
    columns.setElementAt( aValue,column );
  }

  public void Update() throws ModelException {
    if ( !FIsConnected ){
      ReadDataFromNode();
    }
    //обновляем все ячейки таблицы
    UpdateCell();
    FTable.repaint();
  }

  public Component GetComponent() {
    return FPanel;
  }

  /**Чтение из файла с описанием формы информации о необходимости динамического формирования
   * таблицы. Т.е. из файла будет прочитано, какой параметр блока управляет тем, какие строки будут выводиться
   * в таблицу, а какие не будут.
   * Пример: в модели имеются 100 блоков, и в таблицу должны выводиться они все. Но все они в таблице не нужны, а
   * нужна некоторая выборка этих блоков. Поэтому в таблице определяется некоторое значение (произвольное), и блоки
   * будут попадать в таблицу только при совпадении указанного значения и параметра блока
   *
   */
  protected void ReadFilterParam() throws ModelException {
  	ModelElementDataSource blockListNode = this.GetDataSource().GetChildElement("BlockList");
  	if (blockListNode == null) {
  		throw new ModelException("Отсутствует список блоков " + FCaption.getText());
  	}
  	ModelElementDataSource filterNode =  blockListNode.GetChildElement("Filter");
  	if ( filterNode == null ){
      FIsFilterExist = false;
      return;
    }
    
    FFilteredParamName = filterNode.GetParamName();
    if ( FFilteredParamName == null || "".equalsIgnoreCase( FFilteredParamName ) ){
      ModelException e = new ModelException("Ошибка в таблице \"" + FCaption.getText() +
         "\": пустое название параметра для фильтрации");
      throw e;
    }
    String filterType = filterNode.GetFilterValueType();
    if ( filterType == null || "".equalsIgnoreCase( filterType ) ){
      ModelException e = new ModelException("Ошибка в таблице \"" + FCaption.getText() +
         "\": отсутствует тип значения для фильтрации");
      throw e;
    }
    String filterValue = filterNode.GetFilterValue();
    if ( filterValue == null || "".equalsIgnoreCase( filterValue ) ){
      ModelException e = new ModelException("Ошибка в таблице \"" + FCaption.getText() +
         "\": отсутствует значение для фильтрации");
      throw e;
    }
    try {
      FFilterValue = Variable.CreateNewInstance("filter var", filterType, filterValue);
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("Ошибка в таблице \"" + FCaption.getText() +
         "\":" + e.getMessage());
      throw e1;
    }
    FIsFilterExist = true;
  } 
  
  
}

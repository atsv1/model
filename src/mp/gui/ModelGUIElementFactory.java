package mp.gui;


import mp.elements.*;
import mp.utils.ModelAttributeReader;

/**
 * User: atsv
 * Date: 30.09.2006
 */
public class ModelGUIElementFactory extends ModelElementAbstractFactory {

  
  private ModelConnector FConnector = null;

  private static String[][] FormDef = {
    {"",           "Form",      "0"},
    {"Form",       "TabSheets", "1"},
    {"TabSheets",  "TabSheet",  "2"},
    {"TabSheet",   "Label",     "3"},
    {"TabSheet",   "EditBox",   "4"},
    {"TabSheet",   "Graphic",   "5" },
    {"TabSheet",   "Table",     "6" },
    {"TabSheet",   "ListTable", "7" },
    {"TabSheet",   "Animation", "8" },
    {"TabSheet",   "CheckBox",  "9"},
    {"TabSheet",   "Button",    "10"},
    {"TabSheet",   "VarHistory",    "11"}
  };

  public ModelGUIElementFactory() throws ModelException{
    
  }

  /**Создание базового класса для апплета. Название класса содержится в файле формы
   *
   * @param aNewNode
   * @return
   * @throws ModelException
   */
  private ModelForReadInterface GetNewBasicElement( ModelElementDataSource aNewNode ) throws ModelException {
    ModelForReadInterface result = null;
    
    String className = aNewNode.GetClassName();
    try {
      Class cl = Class.forName( className );
      Object o = cl.newInstance();
      result = (ModelForReadInterface) o;
    } catch (ClassNotFoundException e1) {
      ModelException e = new ModelException("Класс " + className + " не найден");
      throw e;
    } catch (IllegalAccessException e2) {
      ModelException e = new ModelException("При создании экземпляра класса  " + className + " произошла ошибка " + e2.getMessage());
      throw e;
    } catch (InstantiationException e3) {
      ModelException e = new ModelException("При создании экземпляра класса  " + className + " произошла ошибка " + e3.getMessage());
      throw e;
    }
    result.SetDataSource( aNewNode );
    return result;
  }

  private static ModelForReadInterface GetNewTabSheets(  ) throws ModelException{
    ModelForReadInterface result;
    result = new ModelGUITabSheets();
    return result;
  }

  private static ModelForReadInterface GetNewTabSheet(  ) throws ModelException{
    ModelForReadInterface result;
    result = new ModelGUITabSheet();
    return result;
  }

  private static ModelForReadInterface GetNewlabel( ModelElementDataSource aNode ) throws ModelException{
    ModelGUILabel label = new ModelGUILabel();
    label.SetDataSource( aNode );
    label.ReadDataFromNode();
    return label;
  }

  private static ModelForReadInterface GetNewEdit( ModelElementDataSource aNode, ModelConnector connector ) throws ModelException{
    ModelGUIEditBox editBox = new ModelGUIEditBox();
    editBox.SetDataSource( aNode );
    editBox.SetConnector( connector );
    editBox.ReadDataFromNode();
    return editBox;
  }

  private ModelForReadInterface GetNewGraphic( ModelElementDataSource aNode ) throws ModelException{
    //ModelGUIJCCKitGraph graphic = new ModelGUIJCCKitGraph();
    ModelGUIJFreeGraph graphic = new ModelGUIJFreeGraph();
    graphic.SetDataSource( aNode );
    graphic.SetConnector( FConnector );
    graphic.ReadDataFromNode();
    graphic.SetBoundsManager( new BoundsManager_LeftShift() );
    return graphic;
  }

  private ModelForReadInterface GetNewTable( ModelElementDataSource aNode ) throws ModelException{
    ModelGUITable table = new ModelGUITable();
    table.SetDataSource( aNode );
    table.SetConnector( FConnector );
    table.ReadDataFromNode();
    return table;
  }

  private ModelForReadInterface GetNewListTable( ModelElementDataSource aNode ) throws ModelException{
    ModelGUIListTable table = new ModelGUIListTable();
    table.SetDataSource( aNode );
    table.SetConnector( FConnector );
    table.ReadDataFromNode();
    return table;
  }

  private ModelForReadInterface GetNewAnimation( ModelElementDataSource aNode ) throws ModelException{
    ModelGUIAnimation animation = new ModelGUIAnimation();
    animation.SetDataSource( aNode );
    animation.SetConnector( FConnector );
    animation.ReadDataFromNode();
    return animation;
  }

  private ModelForReadInterface GetNewCheckBox( ModelElementDataSource aNode ) throws ModelException{
    ModelGUICheckBox cb = new ModelGUICheckBox();
    cb.SetDataSource( aNode );
    cb.SetConnector( FConnector );
    cb.ReadDataFromNode();
    return cb;
  }

  private ModelForReadInterface GetNewButton( ModelElementDataSource aNode ) throws ModelException{
    ModelGUIButton button = new ModelGUIButton();
    button.SetDataSource( aNode );
    button.SetConnector( FConnector );
    button.ReadDataFromNode();
    return button;
  }

  private ModelForReadInterface GetNewVarTable( ModelElementDataSource aNode ) throws ModelException{
  	ElementHistoryTable ht = new ElementHistoryTable();
  	ht.SetDataSource(aNode);
  	ht.SetConnector(FConnector);
  	ht.ReadDataFromNode();
  	return ht;
  }

  public ModelForReadInterface GetNewElement(ModelElementDataSource aCurrentNode, ModelForReadInterface aCurrentElement, ModelElementDataSource aNewNode,
                                             int aNewId) throws ModelException {
    String functionCode = GetFunctionCode( aCurrentNode, aNewNode );
    int i = Integer.parseInt( functionCode );
    ModelForReadInterface result = null;
    if ( FConnector == null ){
      ModelException e = new ModelException("Отсутствует коннектор. Создание элементов невозможно");
      throw e;
    }
    switch (i){
      case 0:{
        result =  GetNewBasicElement( aNewNode );
        break;
      }
      case -1 :{
        return aCurrentElement;
      }
        case 1:{
          result =  GetNewTabSheets();
          break;
        }
        case 2:{
          result =  GetNewTabSheet();
          break;
        }
        case 3:{
          result = GetNewlabel( aNewNode );
          break;
        }
        case 4:{
          result = GetNewEdit( aNewNode, FConnector );
          break;
        }
        case 5:{
          result = GetNewGraphic( aNewNode );
          break;
        }
        case 6:{
          result = GetNewTable( aNewNode );
          break;
        }
        case 7:{
          result = GetNewListTable( aNewNode );
          break;
        }
        case 8:{
          result = GetNewAnimation( aNewNode );
          break;
        }
        case 9:{
          result = GetNewCheckBox( aNewNode );
          break;
        }
        case 10:{
          result = GetNewButton( aNewNode );
          break;
        }
        case 11: {
        	result = GetNewVarTable(aNewNode);
        	break;
        }
        default :{
          ModelException e = new ModelException("Неизвестный код для создания элемента");
          throw e;
        }
    }
    if ( result == null ){
      ModelException e = new ModelException("Не удалось создать элемент формы");
      throw e;
    }
    result.SetDataSource( aNewNode );
    ((ModelGUIAbstrElement)result).SetConnector( FConnector );
    return result;
  }

  public  String[][] GetMatrix() {
    return FormDef;
  }

  public boolean IsLastNode(ModelElementDataSource aNode) {
    return false;
  }

  public boolean IsLastElement(ModelForReadInterface aElement) {
    String className = aElement.getClass().getName();
    if ( className.equalsIgnoreCase("mp.gui.StandartForm") ||
            className.equalsIgnoreCase("mp.gui.ModelGUITabSheets") ||
            className.equalsIgnoreCase( "mp.gui.ModelGUITabSheet" ) ||
            className.equalsIgnoreCase("mp.gui.ModelGUIPanel")
    ){
    return false;
    } else
      return true;
  }

  private static void AddElement( ModelForReadInterface aCurrentElement, ModelForReadInterface aNewElement ){
    ModelGUIElement owner = (ModelGUIElement) aCurrentElement;
    ModelGUIElement newElement = (ModelGUIElement) aNewElement;
    owner.AddGUIElement( newElement );
  }

  private static void AddButton( ModelForReadInterface aCurrentElement, ModelForReadInterface aNewElement ){
    ModelGUIElement owner = (ModelGUIElement) aCurrentElement;
    ModelGUIButton newElement = (ModelGUIButton) aNewElement;
    owner.AddGUIElement( newElement );
    newElement.SetOwnerElementList(  owner.GetElementList() );
  }

  public void ExecuteDoSomethingFunction(ModelElementDataSource aParentNode, ModelElementDataSource aCurrentNode, ModelForReadInterface aCurrentElement,
                                         ModelForReadInterface aNewElement) throws ModelException {
    String functionCode = GetFunctionCode( aParentNode, aCurrentNode );
    int i = Integer.parseInt( functionCode );
    switch (i){
      case -1:{
        return;
      }
        case 0:{
          break;
        }
        case 1:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 2:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 3:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 4:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 5:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 6:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 7:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 8:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 9:{
          AddElement(aCurrentElement, aNewElement);
          break;
        }
        case 10:{
          AddButton(aCurrentElement, aNewElement);
          break;
        }
        case 11: {
        	AddElement(aCurrentElement, aNewElement);
        }
    }//switch
  }

  public void SetConnector(ModelConnector aConnector){
    FConnector = aConnector;
  }


}

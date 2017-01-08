package mp.elements;

import org.w3c.dom.Node;
import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;

/**
 * User: atsv
 * Date: 29.09.2006
 *
 * Для добавления нового элемента нужно сделать следующее:
 * 1. Добавить запись в массиве ModelDef, соответствующую классу нового элемента
 * 2. Релизовать функцию создания экземляра создаваемого элемента
 * 3. Добавить вызов функции создания в методе GetNewElement()
 * 4. Изменить код метода IsLastElement(), если внутри добавлемого елемента не будет содержаться других элементов,
 * которые должны быть созданы классом ModelElementFactory
 * 5. Реализовать метод добавления мультиплексора в модель
 * 6. Изменить метод ExecuteDoSomethingFunction() - добавить в него вызов метода добавления созданного мультиплексора в
 * модель
 */
public class ModelElementFactory extends ModelElementAbstractFactory {
  private static String TRANSITION_TYPE_VALUE = "ByValue";
  private static String TRANSITION_TYPE_TIMEOUT = "Timeout";

  private ModelAttributeReader FAttrReader = null;

   /** Массив предназначен для хранения информации, которая будет использоваться при создании новых элементов
    * модели
   * Назначение столбцов массива:
   * 0 - Название текущей ноды (Model, BlockList, Block и т.п.)
   * 1 - название новой ноды
   * 2 - код функции, которая вернет новосозданный элемент нужного типа. Значение -1 указывает, что новый элемент
   *    создавать не нужно, но при этом это не ошибочная ситуация. См. функцию ExecuteCreateFunction(). Эта же
   * кодировка функций используется и для выполнения каких-либо действия с только что созданным элементом модели
   */
  private static String[][] ModelDef = {
    {"",               "Model",                         "0"},
    {"Model",          "BlockList",                     "1"},
    {"BlockList",      "Block",                         "2"},
    {"Block",          "InputParamList",                "-1"},
    {"Block",          "OutParamList",                  "-1"},
    {"Block",          "InnerParamList",                "-1"},
    {"Block",          "Statechart",                    "6"},
    {"InputParamList", "Param",                         "3"},
    {"InnerParamList", "Param",                         "4"},
    {"OutParamList",   "Param",                         "5"},
    {"Statechart",     "State",                         "7"},
    {"State",          "State",                         "7"},
    {"State",          "Code",                          "8"},
    {"State",          "Transition",                    "9"},
    {"BlockList",      "Multiplexor",                   "10"},
    {"Block",          "EventProcessors",               "11"},
    {"Multiplexor",    "OutParamList",                  "-1"},
    {"Model",          "ModelList",                     "-1"},
    {"ModelList",      "SubModel",                      "-1"},
    {"ModelList",      "ParallelModel",                 "-1"},
    {"Model",          "ConstantList",                  "-1"},
    {"ConstantList",   "Const",                         "12" },
    {"BlockList",      "Aggregator",                    "13"},
    {"Aggregator",     "Code",                          "-1" },
    {"Model",          "FunctionList",                  "-1"},
    {"FunctionList",   "Function",                      "14"},
    {"Function",       "Formula",                       "-1"},
    {"Function",       "Param",                         "-1"},
    {"Function",       "InnerParamList",                "-1"},
    {"InnerParamList", "Param",                         "4"},

  };

  public ModelElementFactory() throws ModelException{
    //FAttrReader = new ModelAttributeReader(null);
    FAttrReader = ServiceLocator.GetAttributeReader();
  }


  private ModelElement GetNewModel(ModelElement aNewElementOwner, Node aSourceNode , int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    Model result = new Model(aNewElementOwner, FAttrReader.GetAttrName() ,  aNewId );
    result.SetNode( aSourceNode );
    return result;
  }

  private ModelElement GetNewBlock(ModelElement aNewElementOwner, Node aSourceNode , int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    ModelSimpleBlock result = new ModelSimpleBlock(aNewElementOwner, FAttrReader.GetAttrName() , aNewId);
    result.SetNode( aSourceNode );
    return result;
  }

  private ModelElement GetNewInpParam(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    String s = FAttrReader.GetAttrParamType();
    ModelBlockParam result;
    if ( "material".equalsIgnoreCase( s ) ){
      result = new ModelMaterialParam( aNewElementOwner, FAttrReader.GetAttrName() , aNewId  );
    } else {
      result = new ModelInputBlockParam(aNewElementOwner, FAttrReader.GetAttrName() , aNewId );
    }
    result.SetNode( aSourceNode );
    result.ReadVariableInfo( FAttrReader );
    return result;
  }

  private ModelElement GetNewNotInpParam(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    ModelBlockParam result = null;
    String s = FAttrReader.GetAttrParamType();
    if ( "material".equalsIgnoreCase( s ) ){
      result = new ModelMaterialParam(aNewElementOwner, FAttrReader.GetAttrName() , aNewId );
    } else{
      if ( "array".equalsIgnoreCase( s ) ){
        result = new ModelArrayElement( aNewElementOwner, FAttrReader.GetAttrName() , aNewId );
      } else{
        result = new ModelCalculatedElement(aNewElementOwner, FAttrReader.GetAttrName() , aNewId );
      }
    }
    result.SetNode( aSourceNode );
    result.ReadVariableInfo( FAttrReader );
    return result;
  }

  private ModelElement GetNewInnerParam(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    return GetNewNotInpParam(aNewElementOwner, aSourceNode, aNewId);
  }

  private ModelElement GetNewOutParam(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    return GetNewNotInpParam(aNewElementOwner, aSourceNode, aNewId);
  }

  private ModelElement GetNewState(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    AutomatState result = new AutomatState(aNewElementOwner, FAttrReader.GetAttrName(),  aNewId );
    result.SetNode( aSourceNode );
    return result;
  }

  private ModelElement GetNewTransition( ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    AutomatTransition result = null;
    String typeName = FAttrReader.GetTransitionType();
    String ownerName;
    if ( aNewElementOwner == null ){
      ownerName = "";
    } else{
      ownerName = aNewElementOwner.GetName();
    }
    if ( typeName == null ||  "".equalsIgnoreCase(typeName) ){
      ModelException e = new ModelException("Отсутствует тип перехода в переходе \"" + ownerName + "." + FAttrReader.GetAttrName() + "\"");
      throw e;
    }
    if ( typeName.equalsIgnoreCase( TRANSITION_TYPE_VALUE ) ){
      result = new AutomatTransitionByValue(aNewElementOwner, FAttrReader.GetAttrName() ,aNewId);
      result.SetNode( aSourceNode );
      return result;
    }
    if ( typeName.equalsIgnoreCase( TRANSITION_TYPE_TIMEOUT ) ){
      result = new AutomatTransitionTimeout( aNewElementOwner, FAttrReader.GetAttrName() ,aNewId );
      result.SetNode( aSourceNode );
      return result;
    }
    ModelException e = new ModelException("Неизвестный тип перехода в элементе \""  + ownerName + "." +
            FAttrReader.GetAttrName() + "\": " + typeName );
    throw e;
  }

  private ModelElement GetNewMux(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    ModelMultiplexor result = null;
    String skipValue = FAttrReader.GetSkipFirstValue();
    if ( skipValue == null || "".equalsIgnoreCase( skipValue ) ) {
      result = new ModelMultiplexor(aNewElementOwner, FAttrReader.GetAttrName(),  aNewId);
    } else {
      result = new ModelMultiplexorWithSkipParam( aNewElementOwner, FAttrReader.GetAttrName(),  aNewId );
    }
    result.SetNode( aSourceNode );
    return result;
  }

  private static ModelEventProcessorContainer GetNewEventContainer( ModelElement aNewElementOwner, Node aSourceNode ){
    ModelEventProcessorContainer result = new ModelEventProcessorContainer(aNewElementOwner);
    result.SetNode( aSourceNode );
    return result;
  }

  private ModelElement GetNewConstant(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    String name = FAttrReader.GetAttrName();
    ModelConstant result;
    String s = FAttrReader.GetAttrParamType();
    if ( "array".equalsIgnoreCase( s ) ){
      result = new ModelConstant( aNewElementOwner, name, aNewId );;
      ModelArrayElement el = new ModelArrayElement( aNewElementOwner, FAttrReader.GetAttrName() , aNewId );
      el.SetNode(aSourceNode);
      el.ReadVariableInfo(FAttrReader);
      result.SetVariable( el.GetArray() );
      FAttrReader.AddConstant( name, FAttrReader.GetAttrInitValue() );
      return result;
    }
    result = new ModelConstant( aNewElementOwner, name, aNewId );
    ((ModelConstant)result).SetConstantDescr( name, FAttrReader.GetAttrParamType(), FAttrReader.GetAttrInitValue() );
    FAttrReader.AddConstant( name, FAttrReader.GetAttrInitValue() );
    return result;
  }

  private ModelAggregator GetNewAggregator(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException{
    FAttrReader.SetNode( aSourceNode );
    String name = FAttrReader.GetAttrName();
    ModelAggregator result = new ModelAggregator( aNewElementOwner, name, aNewId );
    result.SetNode( aSourceNode );
    return result;
  }

  private ModelForReadInterface GetNewFunction(ModelElement aNewElementOwner, Node aSourceNode, int aNewId) throws ModelException {
  	FAttrReader.SetNode( aSourceNode );
    String name = FAttrReader.GetAttrName();
    ModelFunction result = new ModelFunction(aNewElementOwner, name, aNewId);
    result.SetNode(aSourceNode);
    result.ReadFunctionInfo(FAttrReader);
    return result;
  }

  public ModelForReadInterface GetNewElement(Node aCurrentNode, ModelForReadInterface aCurrentElement,
                                             Node aNewNode, int aNewId) throws ModelException {
    String functionCode = GetFunctionCode( aCurrentNode, aNewNode );
    int i = Integer.parseInt( functionCode );
    switch (i){
      case -1:{
        return aCurrentElement;
      }
      case 0:{
        return GetNewModel((ModelElement)aCurrentElement, aNewNode, aNewId);
      }
        case 1:{
          return aCurrentElement;
        }
        //Создание нового блока
        case 2:{
          return GetNewBlock( (ModelElement)aCurrentElement, aNewNode, aNewId );
        }
        case 3:{
          return GetNewInpParam( (ModelElement)aCurrentElement, aNewNode, aNewId );
        }
        case 4:{
          return GetNewInnerParam( (ModelElement)aCurrentElement, aNewNode, aNewId );
        }
        case 5:{
          return GetNewOutParam( (ModelElement)aCurrentElement, aNewNode, aNewId );
        }
        case 6:{
          return GetNewState( (ModelElement)aCurrentElement, aNewNode, aNewId );
        }
        case 7:{
          return GetNewState( (ModelElement)aCurrentElement, aNewNode, aNewId );
        }
        case 8:{
          return null;
        }
        case 9:{
          return GetNewTransition( (ModelElement)aCurrentElement, aNewNode, aNewId );
        }
        case 10:{
          return GetNewMux( (ModelElement)aCurrentElement, aNewNode, aNewId );
        }
        case 11:{
          return GetNewEventContainer((ModelElement) aCurrentElement, aNewNode );
        }
        case 12:{
          return GetNewConstant( (ModelElement) aCurrentElement, aNewNode, aNewId );
        }
        case 13:{
          return GetNewAggregator( (ModelElement) aCurrentElement, aNewNode, aNewId );
        }
        case 14: {
           return GetNewFunction((ModelElement) aCurrentElement, aNewNode, aNewId);
        }
        default:{
          FAttrReader.SetNode( aNewNode );
          String s = FAttrReader.GetAttrName();
          ModelException e = new ModelException("Неверный код функции создания элемента модели. Нода \"" +
                  aNewNode.getNodeName() + "\" название: \"" + s + "\" код =" + Integer.toString(i));
          throw e;
        }
    }//case
  }

  public String[][] GetMatrix() {
    return ModelDef;
  }

  public boolean IsLastNode(Node aNode) {
    if ( aNode == null ){
      return false;
    }
    String nodeName = aNode.getNodeName();
    //проверяем ситуацию, когда читается нода Code, принадлежащая мультипексору
    if ( "Code".equalsIgnoreCase( nodeName ) ){
      Node muxNode = aNode.getParentNode();
      if ( muxNode == null ){
        return false;
      }
      if ( "Multiplexor".equalsIgnoreCase( muxNode.getNodeName() ) ){
        return true;
      }
    }
    if ( "EventList".equalsIgnoreCase( nodeName ) ){
      Node muxNode = aNode.getParentNode();
      if ( muxNode == null ){
        return false;
      }
      if ( "Multiplexor".equalsIgnoreCase( muxNode.getNodeName() ) ){
        return true;
      }
    }
    if ( "Value".equalsIgnoreCase( nodeName ) || "Function".equalsIgnoreCase( nodeName ) ){
      Node aggrNode = aNode.getParentNode();
      return aggrNode != null && "Aggregator".equalsIgnoreCase(aggrNode.getNodeName());
    }
    return false;
  }

  public boolean IsLastElement(ModelForReadInterface aElement) {
    if ( aElement == null ){
      return false;
    }
    Class cl = aElement.getClass();
    return cl.getName().equalsIgnoreCase("mp.elements.ModelInputBlockParam") ||
            cl.getName().equalsIgnoreCase("mp.elements.ModelCalculatedElement") ||
            cl.getName().equalsIgnoreCase("mp.elements.ModelMaterialParam") ||
            //cl.getName().equalsIgnoreCase("mp.elements.ModelMultiplexor") ||
            cl.getName().equalsIgnoreCase("mp.elements.ModelEventProcessorContainer") ||
            cl.getName().equalsIgnoreCase("mp.elements.ModelArrayElement")
       ;
  }

  private static void AddBlock(ModelForReadInterface aModel, ModelForReadInterface aBlock) throws ModelException{
    Model model = (Model)aModel;
    model.AddElement( (ModelElement)aBlock );
  }

  private static void AddInpParam(ModelForReadInterface aBlock, ModelForReadInterface aElement) throws ModelException{
    ModelBlock block = (ModelBlock)aBlock;
    block.AddInpParam( (ModelElement)aElement );
  }

  private static void AddInnerParam(ModelForReadInterface aParentElement, ModelForReadInterface aElement) throws ModelException{
  	if (aParentElement instanceof ModelBlock) {
      ModelBlock block = (ModelBlock)aParentElement;
      block.AddInnerParam( (ModelElement)aElement );
      return;
  	}
  	if (aParentElement instanceof ModelFunction) {
  		//AddInnerParam
  		ModelFunction fun = (ModelFunction) aParentElement;
  		fun.AddInnerParam((ModelBlockParam) aElement);
  	}
  }


  private static void AddOutParam(ModelForReadInterface aBlock, ModelForReadInterface aElement) throws ModelException{
    ModelBlock block = (ModelBlock)aBlock;
    block.AddOutParam( (ModelElement)aElement );
  }

  private static void AddRootStateParam(ModelForReadInterface aBlock, ModelForReadInterface aElement) throws ModelException{
    ModelBlock block = (ModelBlock) aBlock;
    block.AddState( (AutomatState) aElement );
  }

  private static void AddState(ModelForReadInterface aRootState, ModelForReadInterface aState) throws ModelException{
    AutomatState rootState = (AutomatState) aRootState;
    rootState.AddElement( (AutomatState) aState );
  }

  private static void AddTransition(ModelForReadInterface aRootState, ModelForReadInterface aState) throws ModelException{
    AutomatState rootState = (AutomatState) aRootState;
    rootState.AddElement( (AutomatTransition) aState );
  }

  private static void AddMux(ModelForReadInterface aModel, ModelForReadInterface aMux) throws ModelException{
    Model model = (Model)aModel;
    model.AddElement( (ModelElement)aMux );
  }

  private static void AddEventContainer(ModelBlock aBlock, ModelForReadInterface aEventContainer){
    aBlock.AddEventProcessorsContainer((ModelEventProcessorContainer) aEventContainer);
  }

  private static void AddConstant(ModelForReadInterface aModel, ModelForReadInterface aConstant) throws ModelException {
    ((Model)aModel).AddConstant( (ModelConstant)aConstant );
  }

  private static void AddFunction(ModelForReadInterface aModel, ModelForReadInterface aFunction){
  	((Model)aModel).AddFunction((ModelFunction) aFunction);

  }

  public void ExecuteDoSomethingFunction( Node aParentNode, Node aCurrentNode, ModelForReadInterface aCurrentElement,
                                           ModelForReadInterface aNewElement  ) throws ModelException{
    int i = Integer.parseInt( GetFunctionCode(GetNodeName( aParentNode ), GetNodeName(aCurrentNode)  ) );
    switch (i){
      case -1:{
        break;
      }
        case 0:{
          break;
        }
        case 1:{
          break;
        }
        case 2:{
          AddBlock( aCurrentElement, aNewElement );
          break;
        }
        case 3:{
          AddInpParam( aCurrentElement, aNewElement );
          break;
        }
        case 4:{
          AddInnerParam( aCurrentElement, aNewElement );
          break;
        }
        case 5:{
          AddOutParam( aCurrentElement, aNewElement );
          break;
        }
        case 6:{
          AddRootStateParam( aCurrentElement, aNewElement );
          break;
        }
        case 7:{
          AddState( aCurrentElement, aNewElement );
          break;
        }
        case 8 :{
          break;
        }
        case 9: {
          AddTransition( aCurrentElement, aNewElement );
          break;
        }
        case 10:{
          AddMux( aCurrentElement, aNewElement );
          break;
        }
        case 11:{
          AddEventContainer((ModelBlock) aCurrentElement, aNewElement );
          break;
        }
        case 12:{
          AddConstant( aCurrentElement, aNewElement );
          break;
        }
        case 13:{
          AddMux( aCurrentElement, aNewElement );
          break;
        }
        case 14:{
        	AddFunction(aCurrentElement, aNewElement );
        	break;
        }
        default:{
          FAttrReader.SetNode( aCurrentNode );
          String s = FAttrReader.GetAttrName();
          ModelException e = new ModelException("Неверный код функции для выполнения действия с элементом модели. Нода " +
                  aCurrentNode.getNodeName() + " название: " + s + " код = " + Integer.toString(i));
          throw e;
        }
    }
  }

}

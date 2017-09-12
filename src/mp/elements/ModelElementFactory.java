package mp.elements;



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
    {"Model",          "Block",                         "2"},
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
    {"Model",          "Multiplexor",                   "10"},
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
    
  }


  private ModelElement GetNewModel(ModelElement aNewElementOwner, ModelElementDataSource aDataSource , int aNewId) throws ModelException{    
    Model result = new Model(aNewElementOwner, aDataSource.GetAttrName() ,  aNewId );    
    return result;
  }

  private ModelElement GetNewBlock(ModelElement aNewElementOwner, ModelElementDataSource dataSource , int aNewId) throws ModelException{    
    ModelSimpleBlock result = new ModelSimpleBlock(aNewElementOwner, dataSource.GetAttrName() , aNewId);
    result.SetDataSource( dataSource );
    return result;
  }

  private ModelElement GetNewInpParam(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{    
    String s = aSourceNode.GetAttrParamType();
    ModelBlockParam result;
    if ( "material".equalsIgnoreCase( s ) ){
      result = new ModelMaterialParam( aNewElementOwner, aSourceNode.GetAttrName() , aNewId  );
    } else {
      result = new ModelInputBlockParam(aNewElementOwner, aSourceNode.GetAttrName() , aNewId );
    }
    result.SetDataSource(aSourceNode);    
    result.ReadVariableInfo( aSourceNode );
    return result;
  }

  private ModelElement GetNewNotInpParam(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{    
    ModelBlockParam result = null;
    String s = aSourceNode.GetAttrParamType();
    if ( "material".equalsIgnoreCase( s ) ){
      result = new ModelMaterialParam(aNewElementOwner, aSourceNode.GetAttrName() , aNewId );
    } else{
      if ( "array".equalsIgnoreCase( s ) ){
        result = new ModelArrayElement( aNewElementOwner, aSourceNode.GetAttrName() , aNewId );
      } else{
        result = new ModelCalculatedElement(aNewElementOwner, aSourceNode.GetAttrName() , aNewId );
      }
    }
    result.SetDataSource( aSourceNode );
    result.ReadVariableInfo( aSourceNode );
    return result;
  }

  private ModelElement GetNewInnerParam(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{
    return GetNewNotInpParam(aNewElementOwner, aSourceNode, aNewId);
  }

  private ModelElement GetNewOutParam(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{
    return GetNewNotInpParam(aNewElementOwner, aSourceNode, aNewId);
  }

  private ModelElement GetNewState(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{    
    AutomatState result = new AutomatState(aNewElementOwner, aSourceNode.GetAttrName(),  aNewId );
    result.SetDataSource( aSourceNode );
    return result;
  }

  private ModelElement GetNewTransition( ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{
    
    AutomatTransition result = null;
    String typeName = aSourceNode.GetTransitionType();
    String ownerName;
    if ( aNewElementOwner == null ){
      ownerName = "";
    } else{
      ownerName = aNewElementOwner.GetName();
    }
    if ( typeName == null ||  "".equalsIgnoreCase(typeName) ){
      ModelException e = new ModelException("Отсутствует тип перехода в переходе \"" + ownerName + "." + aSourceNode.GetAttrName() + "\"");
      throw e;
    }
    if ( typeName.equalsIgnoreCase( TRANSITION_TYPE_VALUE ) ){
      result = new AutomatTransitionByValue(aNewElementOwner, aSourceNode.GetAttrName() ,aNewId);
      result.SetDataSource( aSourceNode );
      return result;
    }
    if ( typeName.equalsIgnoreCase( TRANSITION_TYPE_TIMEOUT ) ){
      result = new AutomatTransitionTimeout( aNewElementOwner, aSourceNode.GetAttrName() ,aNewId );
      result.SetDataSource( aSourceNode );
      return result;
    }
    ModelException e = new ModelException("Неизвестный тип перехода в элементе \""  + ownerName + "." +
    		aSourceNode.GetAttrName() + "\": " + typeName );
    throw e;
  }

  private ModelElement GetNewMux(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{    
    ModelMultiplexor result = null;
    String skipValue = aSourceNode.GetSkipFirstValue();
    if ( skipValue == null || "".equalsIgnoreCase( skipValue ) ) {
      result = new ModelMultiplexor(aNewElementOwner, aSourceNode.GetAttrName(),  aNewId);
    } else {
      result = new ModelMultiplexorWithSkipParam( aNewElementOwner, aSourceNode.GetAttrName(),  aNewId );
    }
    result.SetDataSource( aSourceNode );
    return result;
  }

  private static ModelEventProcessorContainer GetNewEventContainer( ModelElement aNewElementOwner, ModelElementDataSource aSourceNode ){
    ModelEventProcessorContainer result = new ModelEventProcessorContainer(aNewElementOwner);
    result.SetDataSource( aSourceNode );
    return result;
  }

  private ModelElement GetNewConstant(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{    
    String name = aSourceNode.GetAttrName();
    ModelConstant result;
    String s = aSourceNode.GetAttrParamType();
    
      if ( "array".equalsIgnoreCase( s ) ){
      result = new ModelConstant( aNewElementOwner, name, aNewId );;
      ModelArrayElement el = new ModelArrayElement( aNewElementOwner, aSourceNode.GetAttrName() , aNewId );
      el.SetDataSource(aSourceNode);
      el.ReadVariableInfo(aSourceNode);
      result.SetVariable( el.GetArray() );      
      //aSourceNode.AddConstant( name, aSourceNode.GetAttrInitValue() );
      return result;
    }
    
    result = new ModelConstant( aNewElementOwner, name, aNewId );
    ((ModelConstant)result).SetConstantDescr( name, aSourceNode.GetAttrParamType(), aSourceNode.GetAttrInitValue() );    
    BuildContext.getBuildContext().addConstant(result);
    return result;
  }

  private ModelAggregator GetNewAggregator(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException{    
    String name = aSourceNode.GetAttrName();
    ModelAggregator result = new ModelAggregator( aNewElementOwner, name, aNewId );
    result.SetDataSource( aSourceNode );
    return result;
  }

  private ModelForReadInterface GetNewFunction(ModelElement aNewElementOwner, ModelElementDataSource aSourceNode, int aNewId) throws ModelException {  	
    String name = aSourceNode.GetAttrName();
    ModelFunction result = new ModelFunction(aNewElementOwner, name, aNewId);
    result.SetDataSource(aSourceNode);
    result.ReadFunctionInfo(aSourceNode);
    return result;
  }

  public ModelForReadInterface GetNewElement(ModelElementDataSource aCurrentNode, ModelForReadInterface aCurrentElement, ModelElementDataSource newElementSource, int aNewId) throws ModelException {
    String functionCode = GetFunctionCode( aCurrentNode, newElementSource );
    int i = Integer.parseInt( functionCode );
    switch (i){
      case -1:{
        return aCurrentElement;
      }
      case 0:{
        return GetNewModel((ModelElement)aCurrentElement, newElementSource, aNewId);
      }
        case 1:{
          return aCurrentElement;
        }
        //Создание нового блока
        case 2:{
          return GetNewBlock( (ModelElement)aCurrentElement, newElementSource, aNewId );
        }
        case 3:{
          return GetNewInpParam( (ModelElement)aCurrentElement, newElementSource, aNewId );
        }
        case 4:{
          return GetNewInnerParam( (ModelElement)aCurrentElement, newElementSource, aNewId );
        }
        case 5:{
          return GetNewOutParam( (ModelElement)aCurrentElement, newElementSource, aNewId );
        }
        case 6:{
          return GetNewState( (ModelElement)aCurrentElement, newElementSource, aNewId );
        }
        case 7:{
          return GetNewState( (ModelElement)aCurrentElement, newElementSource, aNewId );
        }
        case 8:{
          return null;
        }
        case 9:{
          return GetNewTransition( (ModelElement)aCurrentElement, newElementSource, aNewId );
        }
        case 10:{
          return GetNewMux( (ModelElement)aCurrentElement, newElementSource, aNewId );
        }
        case 11:{
          return GetNewEventContainer((ModelElement) aCurrentElement, newElementSource );
        }
        case 12:{
          return GetNewConstant( (ModelElement) aCurrentElement, newElementSource, aNewId );
        }
        case 13:{
          return GetNewAggregator( (ModelElement) aCurrentElement, newElementSource, aNewId );
        }
        case 14: {
           return GetNewFunction((ModelElement) aCurrentElement, newElementSource, aNewId);
        }
        default:{
          
          String s = aCurrentNode.GetAttrName();
          ModelException e = new ModelException("Неверный код функции создания элемента модели. Нода \"" +
          		newElementSource.GetElementName() + "\" название: \"" + s + "\" код =" + Integer.toString(i));
          throw e;
        }
    }//case
  }

  public String[][] GetMatrix() {
    return ModelDef;
  }
  
  public boolean IsLastNode(ModelElementDataSource aNode) {
    if ( aNode == null ){
      return false;
    }
    String nodeName = aNode.GetElementName();
    //проверяем ситуацию, когда читается нода Code, принадлежащая мультипексору
    if ( "Code".equalsIgnoreCase( nodeName ) ){
    	ModelElementDataSource muxNode = aNode.getParent();
      if ( muxNode == null ){
        return false;
      }
      if ( "Multiplexor".equalsIgnoreCase( muxNode.GetElementName() ) ){
        return true;
      }
    }
    if ( "EventList".equalsIgnoreCase( nodeName ) ){
    	ModelElementDataSource muxNode = aNode.getParent();
      if ( muxNode == null ){
        return false;
      }
      if ( "Multiplexor".equalsIgnoreCase( muxNode.GetElementName() ) ){
        return true;
      }
    }
    if ( "Value".equalsIgnoreCase( nodeName ) || "Function".equalsIgnoreCase( nodeName ) ){
    	ModelElementDataSource aggrNode = aNode.getParent();
      return aggrNode != null && "Aggregator".equalsIgnoreCase(aggrNode.GetElementName());
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

  public void ExecuteDoSomethingFunction( ModelElementDataSource aParentNode, ModelElementDataSource aCurrentNode, ModelForReadInterface aCurrentElement,
                                           ModelForReadInterface aNewElement  ) throws ModelException{
    int i = Integer.parseInt( GetFunctionCode(aParentNode,  aCurrentNode ) );
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
          
          String s = aCurrentNode.GetAttrName();
          ModelException e = new ModelException("Неверный код функции для выполнения действия с элементом модели. Нода " +
                  aCurrentNode.GetElementName() + " название: " + s + " код = " + Integer.toString(i));
          throw e;
        }
    }
  }




}

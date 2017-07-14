package mp.elements;

import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;
import mp.parser.*;


import java.util.Arrays;
import java.util.UUID;
import java.util.Vector;

/**
 * User: Администратор
 * Date: 05.07.2008
 */
public class ModelArrayElement extends ModelCalculatedElement{
  private ScriptArray FArray = null;
  private Variable FEnableVar = null;
  private Variable FArrayValueVar = null;
  private ScriptParser FForEachParser = null;
  private int[] FCoordinates = null;
  private Vector FCoordinateVariables = null;

  public ModelArrayElement(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }

  protected void ReadVariableInfo( ModelElementDataSource aAttrReader ) throws ModelException{
    ScriptArray array = new ScriptArray();
    array.SetName( this.GetName() );
    this.SetVariable( array );
    FArray = array;
    ReadArrayInfo( GetNode() );
  }

  private int GetValueType( ModelElementDataSource attrReader ) throws ModelException {
    String typeName = attrReader.GetValueType();
    int type =  Operand.GetTypCodeByName( typeName );
    if ( type == -1 ){
      ModelException e = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": неизвестный тип значения \"" + typeName + "\"" );
      throw e;
    }
    return type;
  }

  private int GetElementsCount( String aValue ) throws ModelException {
    int result = 0;
    String error = null;
    try{
      result = Integer.parseInt( aValue );
      return result;
    } catch (Exception e){
        /*ModelException e1 = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": неверное значение размерности массива: " + aValue);
        throw e1;*/
      error = e.getMessage();
    }
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    String constValue = attrReader.GetConstantValue( aValue );
    if ( constValue == null ){
      ModelException e1 = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": неверное значение размерности массива: " + aValue);
      throw e1;
    }
    try{
      result = Integer.parseInt( constValue );
      return result;
    } catch (Exception e){
        ModelException e1 = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": невозможно использовать константу \"" + aValue +
           "\" в качестве размерности массива");
        throw e1;
    }
  }

  /**Читаем информацию о размерности массива и типе его значений
   *
   * @param aNode
   */
  private void ReadArrayInfo( Node aNode) throws ModelException {
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    attrReader.SetNode( aNode );
    ArrayDefinition arrayDef = new ArrayDefinition();
    arrayDef.SetValueType( GetValueType( attrReader ) );
    arrayDef.SetInitValue( attrReader.GetAttrInitValue() );
    //читаем информацию о размерностях массива
    String startDimension = attrReader.GetArrayDimensionValue();
    if ( startDimension == null || "".equalsIgnoreCase( startDimension ) ){
      ModelException e = new ModelException( "Ошибка в массиве \"" + GetFullName() + "\": отсутствует информация о размерности" );
      throw e;
    }
    // определяем количество размерностей
    int dimCounter = 1;
    String dim = attrReader.GetArrayDimensionValue( dimCounter );
    while ( dim != null ){
      dimCounter++;
      dim = attrReader.GetArrayDimensionValue( dimCounter );
    }
    dimCounter--;
    int elementsCount = 0;
    while ( dimCounter > 0 ){
      dim = attrReader.GetArrayDimensionValue( dimCounter );
      elementsCount = GetElementsCount( dim );
      arrayDef.AddDimension( elementsCount );
      dimCounter--;
    }
    arrayDef.AddDimension( GetElementsCount(startDimension) );
    try {
    	InitArray(arrayDef);
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;
    }    
  }
  
  public void InitArray(ArrayDefinition arrayDef) throws ScriptException{
  	if ( arrayDef == null ) {
  		return;  		
  	}
  	if (FArray == null) {
  		ScriptArray array = new ScriptArray();
      array.SetName( this.GetName() );
      this.SetVariable( array );
      FArray = array;
  	}
  	FArray.InitArray( arrayDef );
  	FCoordinates = new int[ FArray.GetDimension() ];  	
  }

  private void ReadCoordinates( Node aForEachNode ) throws ModelException {
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    attrReader.SetNode( aForEachNode );
    String coordName = attrReader.GetArray_CoordinateParamName();
    if ( coordName == null || "".equalsIgnoreCase( coordName ) ){
      FCoordinateVariables = null;
      return;
    }
    int coordCounter = 1;
    ModelBlock owner = (ModelBlock) this.GetOwner();
    ModelBlockParam coordParam = null;
    FCoordinateVariables = new Vector(10);
    while ( coordName != null ){
      coordParam = (ModelBlockParam) owner.Get( coordName );
      if ( coordParam == null ){
        ModelException e = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + " отсутствует переменная \"" + coordName + "\"");
        throw e;
      }
      if ( coordParam.GetVariable().GetType() != Operand.OPERAND_TYPE_INTEGER ){
        ModelException e = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + "  переменная \"" + coordName + "\" должна быть типа integer");
        throw e;
      }
      FCoordinateVariables.add( coordParam.GetVariable() );
      coordName = attrReader.GetArray_CoordinateParamName( coordCounter );
      coordCounter++;
    }//while
    if ( FCoordinateVariables.size() != FArray.GetDimension() ){
      ModelException e = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + " количество объявленных " +
         "координат в секции ForEach не равно размерности массива");
      throw e;
    }
  }

  private void ReadForEachSection() throws ModelException{
    Node forEachNode = GetChildNodeByName( "ForEach" );
    if ( forEachNode == null ){
      return;
    }
    ModelBlock owner = (ModelBlock) this.GetOwner();
    if ( owner == null ){
      ModelException e = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + " отсутствует владелец элемента");
      throw e;
    }
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    attrReader.SetNode( forEachNode );
    ModelBlockParam param = null;

    //читаем переменную, в которую будут записываться значения массива
    String valueName = attrReader.GetArray_ForEachValue();
    if ( valueName == null || "".equalsIgnoreCase( valueName ) ){
      ModelException e = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + " отсутствует значение атрибута arrayvalue");
      throw e;
    }
    param = (ModelBlockParam) owner.Get( valueName );
    if ( param == null ){
      ModelException e = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + " отсутствует переменная \"" + valueName + "\"");
      throw e;
    }
    FArrayValueVar = param.GetVariable();

    //читаем разрешеющую переменную
    String enableName = attrReader.GetArray_EnableFlagName();
    if ( enableName != null && !"".equalsIgnoreCase( enableName ) ){
      param = (ModelBlockParam) owner.Get( enableName );
      if ( param == null ){
        ModelException e = new ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + " отсутствует переменная \"" + enableName + "\"");
        throw e;
      }
      FEnableVar = param.GetVariable();
      if ( FEnableVar.GetType() != Operand.OPERAND_TYPE_BOOLEAN ){
        ModelException e = new  ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + " переменная \"" +
                enableName + "\" не логического типа");
        throw e;
      }
    }

    // читаем исходный код
    String sourceCode = ModelAttributeReader.GetSourceCode( forEachNode );
    if ( sourceCode == null ){
      ModelException e = new  ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + "отсутствует исполняемый код");
      throw e;
    }
    try {
      FForEachParser = ParserFactory.GetParser( FLanguageExt, sourceCode );
    } catch (ScriptException e) {
      ModelException e1 = new  ModelException("Ошибка в массиве \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;
    }
    ReadCoordinates( forEachNode );
  }


  public void ApplyNodeInformation() throws ModelException{
    //ReadArrayInfo( GetNode() );
    super.ApplyNodeInformation();
    ReadForEachSection();
  }

  protected boolean IsForEachEnable(){
    return FEnableVar == null || FEnableVar.GetBooleanValue();
  }

  private void LoadCoordinates( int[] aCoord){
    if ( FCoordinateVariables == null || FCoordinateVariables.size() == 0 ){
      return;
    }
    int i = 0;
    int size = FCoordinateVariables.size();
    Variable var;
    while ( i < size ){
      var = (Variable) FCoordinateVariables.get( i );
      var.SetValue( aCoord[i] );
      i++;
    }
  }

  private void ExecuteForEach( int aCurrentDimension, int[] aCoord ) throws ScriptException {
    int elementsCount = FArray.GetDimensionLength( aCurrentDimension );
    int i = 0;
    if ( aCurrentDimension == FArray.GetDimension()-1 ){
      while ( i < elementsCount ){
        aCoord[aCurrentDimension] = i;
        FArray.StoreValueToVar( FArrayValueVar, aCoord );
        LoadCoordinates( aCoord );
        FForEachParser.ExecuteScript();
        FArray.StoreValueFromVar( FArrayValueVar, aCoord );
        i++;
      }
      return;
    }
    //не дошли еще до последней размерности массива
    while ( i < elementsCount ){
      aCoord[ aCurrentDimension ] = i;
      ExecuteForEach( aCurrentDimension +1, aCoord );
      i++;
    }

  }

  private void ExecuteForEach() throws ModelException {
    if ( FForEachParser == null ){
      // секции forEach нет, выполнять нечего
      return;
    }
    if ( !IsForEachEnable() ){
      //нет разрешения на выполнения кода forEach
      return;
    }
    Arrays.fill( FCoordinates,0);
    try {
      ExecuteForEach(0, FCoordinates);
    } catch (ScriptException e) {
      ModelException e1 = new ModelException();
      throw e1;
    }
  }

  public void UpdateParam() throws ScriptException, ModelException{
    super.UpdateParam();
    ExecuteForEach();
    if ( IsForEachEnable() ){
      InputParamChanged();
    }
  }

  public Variable GetEnableVar(){
    return FEnableVar;
  }

  public Variable GetArrayValue(){
    return FArrayValueVar;
  }

  public int GetCoordinateVariablesCount(){
    if ( FCoordinateVariables == null ){
      return 0;
    }
    return FCoordinateVariables.size();
  }

  public boolean IsNeedRuntimeUpdate() {
    return FForEachParser != null || super.IsNeedRuntimeUpdate();
  }

  public ScriptArray GetArray(){
  	return FArray;
  }
  
  public Variable GetVariable(){
  	return FArray;
  }
  
  
  public void fixState(UUID stateLabel) throws ModelException{
  	if (fixedStates.containsKey(stateLabel)) {
  		throw new ModelException("Дублирование фиксированного состояния");
  	}   	
  	fixedStates.put(stateLabel,  FArray.clone() );
  }
    
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	if (!fixedStates.containsKey(stateLabel)) {
  		throw new  ModelException("Отсутствует метка для отката");  		
  	}
  	Object o = fixedStates.get(stateLabel);
  	if ( !(o instanceof ScriptArray) ) {
  		throw new ModelException("Попытка записать в массив не массив");
  	}
  	try {
			FArray.StoreArray( (ScriptArray) o );
		} catch (ScriptException e) {
			throw new ModelException(e.getMessage()) ;
		}
  	 
  	  	
  }

}

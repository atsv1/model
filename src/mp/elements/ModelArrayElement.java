package mp.elements;

import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;
import mp.parser.*;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * User: �������������
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
    ReadArrayInfo( this.GetDataSource() );
  }

  private int GetValueType( ModelElementDataSource attrReader ) throws ModelException {
    String typeName = attrReader.GetValueType();
    int type =  Operand.GetTypCodeByName( typeName );
    if ( type == -1 ){
      ModelException e = new ModelException("������ � ������� \"" + GetFullName() + "\": ����������� ��� �������� \"" + typeName + "\"" );
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
        /*ModelException e1 = new ModelException("������ � ������� \"" + GetFullName() + "\": �������� �������� ����������� �������: " + aValue);
        throw e1;*/
      error = e.getMessage();
    }
    
    String constValue = BuildContext.getBuildContext().getConstantValue(aValue);;
    if ( constValue == null ){
      throw new ModelException("������ � ������� \"" + GetFullName() + "\": �������� �������� ����������� �������: " + aValue);
      
    }
    try{
      result = Integer.parseInt( constValue );
      return result;
    } catch (Exception e){
        throw  new ModelException("������ � ������� \"" + GetFullName() + "\": ���������� ������������ ��������� \"" + aValue + "\" � �������� ����������� �������");
        
    }
  }

  /**������ ���������� � ����������� ������� � ���� ��� ��������
   *
   * @param aNode
   */
  private void ReadArrayInfo( ModelElementDataSource aSourceElement) throws ModelException {
    
    ArrayDefinition arrayDef = new ArrayDefinition();
    arrayDef.SetValueType( GetValueType( aSourceElement ) );
    String initValue = BuildContext.getBuildContext().getConstantValue(aSourceElement.GetAttrInitValue());
    if ( initValue != null ) {
    	arrayDef.SetInitValue(initValue);
    } else  {
      arrayDef.SetInitValue( aSourceElement.GetAttrInitValue() );
    }
    //������ ���������� � ������������ �������
    String startDimension = aSourceElement.GetArrayDimensionValue();
    if ( startDimension == null || "".equalsIgnoreCase( startDimension ) ){
      ModelException e = new ModelException( "������ � ������� \"" + GetFullName() + "\": ����������� ���������� � �����������" );
      throw e;
    }
    // ���������� ���������� ������������
    int dimCounter = 1;
    String dim = aSourceElement.GetArrayDimensionValue( dimCounter );
    while ( dim != null ){
      dimCounter++;
      dim = aSourceElement.GetArrayDimensionValue( dimCounter );
    }
    dimCounter--;
    int elementsCount = 0;
    while ( dimCounter > 0 ){
      dim = aSourceElement.GetArrayDimensionValue( dimCounter );
      elementsCount = GetElementsCount( dim );
      arrayDef.AddDimension( elementsCount );
      dimCounter--;
    }
    arrayDef.AddDimension( GetElementsCount(startDimension) );
    try {
    	InitArray(arrayDef);
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � ������� \"" + GetFullName() + "\": " + e.getMessage());
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

  private void ReadCoordinates( ModelElementDataSource aForEachElement ) throws ModelException {    
    String coordName = aForEachElement.GetArray_CoordinateParamName();
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
        ModelException e = new ModelException("������ � ������� \"" + GetFullName() + "\": " + " ����������� ���������� \"" + coordName + "\"");
        throw e;
      }
      if ( coordParam.GetVariable().GetType() != Operand.OPERAND_TYPE_INTEGER ){
        ModelException e = new ModelException("������ � ������� \"" + GetFullName() + "\": " + "  ���������� \"" + coordName + "\" ������ ���� ���� integer");
        throw e;
      }
      FCoordinateVariables.add( coordParam.GetVariable() );
      coordName = aForEachElement.GetArray_CoordinateParamName( coordCounter );
      coordCounter++;
    }//while
    if ( FCoordinateVariables.size() != FArray.GetDimension() ){
      ModelException e = new ModelException("������ � ������� \"" + GetFullName() + "\": " + " ���������� ����������� " +
         "��������� � ������ ForEach �� ����� ����������� �������");
      throw e;
    }
  }

  private void ReadForEachSection() throws ModelException{
  	ModelElementDataSource ds = this.GetDataSource();
  	List<ModelElementDataSource> forEachList = ds.GetChildElements ( "ForEach" );
  	if ( forEachList == null || forEachList.isEmpty() ) {
  		return;
  	}
  	ModelElementDataSource aForEachElement = forEachList.get(0);
    if ( aForEachElement == null ){
      return;
    }
    ModelBlock owner = (ModelBlock) this.GetOwner();
    if ( owner == null ){
      ModelException e = new ModelException("������ � ������� \"" + GetFullName() + "\": " + " ����������� �������� ��������");
      throw e;
    }
   
    ModelBlockParam param = null;

    //������ ����������, � ������� ����� ������������ �������� �������
    String valueName = aForEachElement.GetArray_ForEachValue();
    if ( valueName == null || "".equalsIgnoreCase( valueName ) ){
      ModelException e = new ModelException("������ � ������� \"" + GetFullName() + "\": " + " ����������� �������� �������� arrayvalue");
      throw e;
    }
    param = (ModelBlockParam) owner.Get( valueName );
    if ( param == null ){
      ModelException e = new ModelException("������ � ������� \"" + GetFullName() + "\": " + " ����������� ���������� \"" + valueName + "\"");
      throw e;
    }
    FArrayValueVar = param.GetVariable();

    //������ ����������� ����������
    String enableName = aForEachElement.GetArray_EnableFlagName();
    if ( enableName != null && !"".equalsIgnoreCase( enableName ) ){
      param = (ModelBlockParam) owner.Get( enableName );
      if ( param == null ){
        ModelException e = new ModelException("������ � ������� \"" + GetFullName() + "\": " + " ����������� ���������� \"" + enableName + "\"");
        throw e;
      }
      FEnableVar = param.GetVariable();
      if ( FEnableVar.GetType() != Operand.OPERAND_TYPE_BOOLEAN ){
        ModelException e = new  ModelException("������ � ������� \"" + GetFullName() + "\": " + " ���������� \"" +
                enableName + "\" �� ����������� ����");
        throw e;
      }
    }

    // ������ �������� ���
    String sourceCode = aForEachElement.GetexecutionCode();
    if ( sourceCode == null ){
      ModelException e = new  ModelException("������ � ������� \"" + GetFullName() + "\": " + "����������� ����������� ���");
      throw e;
    }
    try {
      FForEachParser = ParserFactory.GetParser( FLanguageExt, sourceCode );
    } catch (ScriptException e) {
      ModelException e1 = new  ModelException("������ � ������� \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;
    }
    ReadCoordinates( aForEachElement );
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
    //�� ����� ��� �� ��������� ����������� �������
    while ( i < elementsCount ){
      aCoord[ aCurrentDimension ] = i;
      ExecuteForEach( aCurrentDimension +1, aCoord );
      i++;
    }

  }

  private void ExecuteForEach() throws ModelException {
    if ( FForEachParser == null ){
      // ������ forEach ���, ��������� ������
      return;
    }
    if ( !IsForEachEnable() ){
      //��� ���������� �� ���������� ���� forEach
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
  		throw new ModelException("������������ �������������� ���������");
  	}   	
  	fixedStates.put(stateLabel,  FArray.clone() );
  }
    
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	if (!fixedStates.containsKey(stateLabel)) {
  		throw new  ModelException("����������� ����� ��� ������");  		
  	}
  	Object o = fixedStates.get(stateLabel);
  	if ( !(o instanceof ScriptArray) ) {
  		throw new ModelException("������� �������� � ������ �� ������");
  	}
  	try {
			FArray.StoreArray( (ScriptArray) o );
		} catch (ScriptException e) {
			throw new ModelException(e.getMessage()) ;
		}
  	 
  	  	
  }

}

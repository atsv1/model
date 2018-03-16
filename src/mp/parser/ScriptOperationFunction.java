package mp.parser;

import java.util.Map;
import java.util.UUID;

/**
 * User: atsv
 * Date: 21.05.2006
 */
public class ScriptOperationFunction extends ScriptOperation {
  //в этой переменной хранится индекс функции в массиве ScriptLanguageDef.FunctionsList
  protected int OperationIndex = -1;
  private ExternalFunction function = null;

  public ScriptOperationFunction(int functionIndex){
  	OperationIndex = functionIndex;  	
  }
  
  public ScriptOperationFunction(ExternalFunction function){
  	this.function = function;
  }

  private int ExecSin(int aProgramPointer) throws ScriptException {
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (float)Math.sin( op1.GetFloatValue() ) );
    return 2;
  }

  private int ExecCos(int aProgramPointer) throws ScriptException {
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (float)Math.cos( op1.GetFloatValue() ) );
    return 2;
  }

  private int ExecTan(int aProgramPointer) throws ScriptException {
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (float)Math.tan( op1.GetFloatValue() ) );
    return 2;
  }

  private int ExecPi(int aProgramPointer) throws ScriptException {
    Operand result = InitOperand( aProgramPointer + 1 );
    result.SetValue( (float)Math.PI );
    return 1;
  }

  private int ExecExponent(int aProgramPointer) throws ScriptException  {
    Operand result = InitOperand( aProgramPointer + 1 );
    result.SetValue( (float)Math.E );
    return 1;
  }

  private int ExecLog(int aProgramPointer) throws ScriptException {
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (float)Math.log( op1.GetFloatValue() ) );
    return 2;
  }

  private int ExecExp(int aProgramPointer) throws ScriptException {
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (float)Math.exp( op1.GetFloatValue() ) );
    return 2;
  }

  private int ExecSqrt(int aProgramPointer) throws ScriptException {
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (float)Math.sqrt( op1.GetFloatValue() ) );
    return 2;
  }

  private int ExecAbs(int aProgramPointer) throws ScriptException {
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (float)Math.abs( op1.GetFloatValue() ) );
    return 2;
  }

  private int ExecRnd(int aProgramPointer) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    op1.SetValue((float) Math.random());
    return 1;
  }

  private int ExecTruncate( int aProgramPointer) throws ScriptException{
    Operand op = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (int)op.GetFloatValue() );
    return 2;
  }

  private int ExecASin( int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( (float)Math.asin( op1.GetFloatValue() ) );
    return 2;
  }

  private int ExecSqr(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    float res = (float)Math.pow( op1.GetFloatValue(), 2 );
    if ( res != res ) {
      ScriptException e = new ScriptException("Несуществующее число в операнде \"" + result.GetName() + "\" " +
              " значение операнда = " + Double.toString( op1.GetFloatValue() ));
      throw e;
    }
    result.SetValue( res );
    return 2;
  }

  private int ExecStopModel(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    if ( !"string".equalsIgnoreCase( op1.GetTypeName() ) ) {
      ScriptException e = new ScriptException("Ошибка при остановке модели: название модели не строкового типа");
      throw e;
    }
    ModelExecutionManager manager = ModelExecutionContext.GetManager( op1.GetStringValue() );
    if ( manager == null ){
      result.SetValue( false );
      ScriptException e = new ScriptException("Ошибка при остановке модели: отсутствует модель с именем \"" + op1.GetStringValue() + "\"");
      throw e;
    }
    manager.StopModelExecution();
    result.SetValue( true );
    return 2;
  }

  private int ExecStartModel(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    if ( !"string".equalsIgnoreCase( op1.GetTypeName() ) ) {
      ScriptException e = new ScriptException("Ошибка при старте модели: название модели не строкового типа");
      throw e;
    }
    ModelExecutionManager manager = ModelExecutionContext.GetManager( op1.GetStringValue() );
    if ( manager == null ){
      result.SetValue( false );
      ScriptException e = new ScriptException("Ошибка при старте модели: отсутствует модель с именем \"" + op1.GetStringValue() + "\"");
      throw e;
    }
    //manager.StartModelExecution();
    Thread t = new Thread((Runnable) manager);
    t.start();
    synchronized(manager) {
    	try {
				manager.wait();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
    }
    result.SetValue( true );
    return 2;
  }

  private int ExecSetToInit(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    if ( !"string".equalsIgnoreCase( op1.GetTypeName() ) ) {
      ScriptException e = new ScriptException("Ошибка при старте модели: название модели не строкового типа");
      throw e;
    }
    ModelExecutionManager manager = ModelExecutionContext.GetManager( op1.GetStringValue() );
    if ( manager == null ){
      result.SetValue( false );
      ScriptException e = new ScriptException("Ошибка при старте модели: отсутствует модель с именем \"" + op1.GetStringValue() + "\"");
      throw e;
    }
    //System.out.println("set to init \"" + op1.GetStringValue() + "\"");
    manager.SetToInitCondition();
    result.SetValue( true );
    return 2;
  }

  private int ExecPrint(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    System.out.println( op1.toString() );
    result.SetValue( true );
    return 2;
  }

  /**Выполняется пересылка значения параметра в какой-либо параметр иного блока и иной модели
   *
   * @param aProgramPointer
   * @return
   * @throws ScriptException
   */
  private int ExecSend( int aProgramPointer ) throws ScriptException{
    Operand modelName = InitOperand( aProgramPointer + 1 );
    ModelExecutionManager manager = ModelExecutionContext.GetManager( modelName.GetStringValue() );
    if ( manager == null ){
      ScriptException e = new ScriptException("В системе отсутствует модель \"" + modelName.GetStringValue() + "\"");
      throw e;
    }
    Operand blockName = InitOperand( aProgramPointer + 2 );
    Operand blockIndex = InitOperand( aProgramPointer + 3 );
    Operand varName = InitOperand( aProgramPointer + 4 );
    Operand value = InitOperand( aProgramPointer + 5 );
    Variable var = manager.GetVariable( blockName.GetStringValue(), blockIndex.GetIntValue(), varName.GetStringValue() );
    if ( var == null ){
      ScriptException e = new ScriptException("Отсутствует параметр \"" + varName.GetStringValue() + "\"");
      throw e;
    }
    var.StoreValueOf( value );
    return 6;
  }

  private int ExecGet( int aProgramPointer ) throws ScriptException{
    Operand modelName = InitOperand( aProgramPointer + 1 );
    ModelExecutionManager manager = ModelExecutionContext.GetManager( modelName.GetStringValue() );
    if ( manager == null ){
      ScriptException e = new ScriptException("В системе отсутствует модель \"" + modelName.GetStringValue() + "\"");
      throw e;
    }
    Operand blockName = InitOperand( aProgramPointer + 2 );
    Operand blockIndex = InitOperand( aProgramPointer + 3 );
    Operand varName = InitOperand( aProgramPointer + 4 );
    Operand result = InitOperand( aProgramPointer + 5 );
    if ( !( result instanceof Variable ) ) {
      ScriptException e = new ScriptException( "Результат операции Get не является переменной" );
      throw e;
    }
    Variable var = manager.GetVariable( blockName.GetStringValue(), blockIndex.GetIntValue(), varName.GetStringValue() );
    if ( var == null ){
      ScriptException e = new ScriptException("Отсутствует параметр \"" + varName.GetStringValue() + "\"");
      throw e;
    }
    int i = var.GetType();
    switch (i) {
      case Operand.OPERAND_TYPE_BOOLEAN:{
        result.InitBooleanOperand( var.GetBooleanValue() );
        break;
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        result.InitIntOperand( var.GetIntValue() );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        result.InitFloatOperand( var.GetFloatValue() );
        break;
      }
      case Operand.OPERAND_TYPE_STRING:{
        result.InitStringOperand( var.GetStringValue() );
        break;
      }
      case Operand.OPERAND_TYPE_ARRAY:{
        ScriptException e = new ScriptException("Операция Get для массивов пока не реализована");
        throw e;
      }
    }
    return 5;
  }

  private int ExecCastToString(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    if ( result.GetType() != Operand.OPERAND_TYPE_STRING ){
      ScriptException e = new ScriptException("Операнд \"" + result.GetName() + "\" не строкового типа. Невозможно записать строку");
      throw e;
    }
    result.SetValue( op1.GetStringValue() );
    return 2;
  }

  private int ExecCastToInt(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    if ( result.GetType() != Operand.OPERAND_TYPE_INTEGER ){
      ScriptException e = new ScriptException("Операнд \"" + result.GetName() + "\" не целочисленного типа. Невозможно записать строку");
      throw e;
    }
    int res;
    int valueType = op1.GetType();
    switch ( valueType ){
      case Operand.OPERAND_TYPE_BOOLEAN: {
        ScriptException e = new ScriptException("Невозможно преобразовать логическую переменную к типу integer");
        throw e;
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        result.SetValue( op1.GetIntValue() );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        result.SetValue( (int)op1.GetFloatValue() );
        break;
      }
      case Operand.OPERAND_TYPE_STRING:{
        String val = op1.GetStringValue();
        try{
          result.SetValue( Integer.parseInt( val ) );
        } catch (Exception e){
          ScriptException e1 = new ScriptException("Невозможно преобразовать значение \"" + val + "\" к типу integer");
          throw e1;
        }
        break;
      }
    }//switch
    return 2;
  }

  private int ExecCastToReal(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand result = InitOperand( aProgramPointer + 2 );
    if ( result.GetType() != Operand.OPERAND_TYPE_REAL ){
      ScriptException e = new ScriptException("Операнд \"" + result.GetName() + "\" не численного типа. Невозможно записать строку");
      throw e;
    }
    int res;
    int valueType = op1.GetType();
    switch ( valueType ){
      case Operand.OPERAND_TYPE_BOOLEAN: {
        ScriptException e = new ScriptException("Невозможно преобразовать логическую переменную к типу real");
        throw e;
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        result.SetValue( (float)op1.GetIntValue() );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        result.SetValue( op1.GetFloatValue() );
        break;
      }
      case Operand.OPERAND_TYPE_STRING:{
        String val = op1.GetStringValue();
        try{
          result.SetValue( (float)Double.parseDouble( val ) );
        } catch (Exception e){
          ScriptException e1 = new ScriptException("Невозможно преобразовать значение \"" + val + "\" к типу real");
          throw e1;
        }
        break;
      }
    }//switch
    return 2;
  }

  private int ExecMod(int aProgramPointer ) throws ScriptException{
    Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand op2 = InitOperand( aProgramPointer + 2 );
    Operand result = InitOperand( aProgramPointer + 3 );
    if ( op1.GetType() != Operand.OPERAND_TYPE_INTEGER ){
      ScriptException e = new ScriptException("Операнд \"" + op1.GetName() + "\" для операции mod не целочисленного типа. ");
      throw e;
    }
    if ( op2.GetType() != Operand.OPERAND_TYPE_INTEGER ){
      ScriptException e = new ScriptException("Операнд \"" + op2.GetName() + "\" для операции mod не целочисленного типа. ");
      throw e;
    }
    int i = op1.GetIntValue() / op2.GetIntValue() ;
    result.SetValue( op1.GetIntValue() - i * op2.GetIntValue() );
    return 3;
  }
  
  private int ExecMin(int aProgramPointer ) throws ScriptException{
  	Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand op2 = InitOperand( aProgramPointer + 2 );
    Operand result = InitOperand( aProgramPointer + 3 );
    float op1Value = op1.GetFloatValue();
    float op2Value = op2.GetFloatValue();
    float minVal;
    if ( op1Value < op2Value ) {
    	minVal = op1Value; 
    } else minVal = op2Value;
    if ( result.GetType() ==  Operand.OPERAND_TYPE_INTEGER) {
    	result.SetValue( (int)minVal );
    } else {
    	result.SetValue( minVal );
    }
    return 3;
  }
  
  private int ExecMin3(int aProgramPointer ) throws ScriptException{
  	Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand op2 = InitOperand( aProgramPointer + 2 );
    Operand op3 = InitOperand( aProgramPointer + 3 );
    Operand result = InitOperand( aProgramPointer + 4 );
    float op1Value = op1.GetFloatValue();
    float op2Value = op2.GetFloatValue();
    float op3Value = op3.GetFloatValue();
    float minVal;
    if ( op1Value < op2Value ) {
    	minVal = op1Value; 
    } else minVal = op2Value;
    
    if ( op3Value < minVal ) {
    	minVal = op3Value;
    }
    
    if ( result.GetType() ==  Operand.OPERAND_TYPE_INTEGER) {
    	result.SetValue( (int)minVal );
    } else {
    	result.SetValue( minVal );
    }
    return 4;  	
  }
  
  private int ExecMax(int aProgramPointer ) throws ScriptException{
  	Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand op2 = InitOperand( aProgramPointer + 2 );
    Operand result = InitOperand( aProgramPointer + 3 );
    float op1Value = op1.GetFloatValue();
    float op2Value = op2.GetFloatValue();
    float maxVal;
    if ( op1Value > op2Value ) {
    	maxVal = op1Value; 
    } else maxVal = op2Value;
    if ( result.GetType() ==  Operand.OPERAND_TYPE_INTEGER) {
    	result.SetValue( (int)maxVal );
    } else {
    	result.SetValue( maxVal );
    }
    return 3;
  }
  
  private int ExecMax3(int aProgramPointer ) throws ScriptException{
  	Operand op1 = InitOperand( aProgramPointer + 1 );
    Operand op2 = InitOperand( aProgramPointer + 2 );
    Operand op3 = InitOperand( aProgramPointer + 3 );
    Operand result = InitOperand( aProgramPointer + 4 );
    float op1Value = op1.GetFloatValue();
    float op2Value = op2.GetFloatValue();
    float op3Value = op3.GetFloatValue();
    float maxVal;
    if ( op1Value > op2Value ) {
    	maxVal = op1Value; 
    } else maxVal = op2Value;
    if (maxVal < op3Value) {
    	maxVal = op3Value;
    }
    
    if ( result.GetType() ==  Operand.OPERAND_TYPE_INTEGER) {
    	result.SetValue( (int)maxVal );
    } else {
    	result.SetValue( maxVal );
    }
    return 4;
  }

  private int ExecReLink(int aProgramPointer ) throws ScriptException{
    Operand modelName = InitOperand( aProgramPointer + 1 );
    ModelExecutionManager manager = ModelExecutionContext.GetManager( modelName.GetStringValue() );
    if ( manager == null ){
      ScriptException e = new ScriptException("В системе отсутствует модель \"" + modelName.GetStringValue() + "\"");
      throw e;
    }
    Operand blockName = InitOperand( aProgramPointer + 2 );
    Operand blockIndex = InitOperand( aProgramPointer + 3 );
    Operand inputParamName = InitOperand( aProgramPointer + 4 );
    Operand modelTo = InitOperand( aProgramPointer + 5 );
    Operand blockToConnect = InitOperand( aProgramPointer + 6 );
    Operand blockToConnectIndex = InitOperand( aProgramPointer + 7 );
    Operand paramToConnectName = InitOperand( aProgramPointer + 8 );
    manager.ReConnectParam( blockName.GetStringValue(), blockIndex.GetIntValue(), inputParamName.GetStringValue(),
            modelTo.GetStringValue(), blockToConnect.GetStringValue(), blockToConnectIndex.GetIntValue(), paramToConnectName.GetStringValue());

    return 9;
  }

  private int ExecExit(int aProgramPointer ) throws ScriptException{
    return this.Program.size();
  }


  public int ExecOperation(int aProgramPointer) throws ScriptException {    
  	if ( function != null ) {
  		return ExecExternalFunction(function, aProgramPointer);
  	}
    if ( OperationIndex == -1 )  {
      ScriptException e = new ScriptException("Неизвестная команда для выполнения");
      throw e;
    }    
    String s = ScriptLanguageDef.FunctionsList[OperationIndex][1];
    int i = Integer.parseInt( s );
    switch ( i )
    {
      case 0: {
          return ExecSin(aProgramPointer);
        }
      case 1: {
          return ExecCos(aProgramPointer);
        }
      case 2:{
        return ExecTan( aProgramPointer );
      }
      case 3:{
        return ExecPi( aProgramPointer );
      }
      case 4:{
        return ExecExponent( aProgramPointer );
      }
      case 5:{
        return ExecLog(aProgramPointer);
      }
      case 6:{
        return ExecExp( aProgramPointer );
      }
      case 7:{
        return ExecSqrt( aProgramPointer );
      }
      case 8:{
        return ExecAbs( aProgramPointer );
      }
      case 9: {
        return ExecRnd( aProgramPointer );
      }
      case 10:{
        return ExecTruncate( aProgramPointer );
      }
      case 11:{
        return ExecASin( aProgramPointer );
      }
      case 12:{
        return ExecSqr( aProgramPointer );
      }
      case 13:{
        return ExecStopModel( aProgramPointer );
      }
      case 14:{
        return ExecStartModel( aProgramPointer );
      }
      case 15:{
        return ExecSetToInit( aProgramPointer );
      }
      case 16:{
        return ExecSend( aProgramPointer );
      }
      case 17:{
        return ExecGet( aProgramPointer );
      }
      case 18:{
        return ExecCastToString( aProgramPointer );
      }
      case 19:{
        return ExecCastToInt( aProgramPointer );
      }
      case 20:{
        return ExecCastToReal( aProgramPointer );
      }
      case 21:{
        return ExecMod( aProgramPointer );
      }
      case 22:{
        return ExecReLink( aProgramPointer );
      }
      case 23:{
        return ExecMin( aProgramPointer );
      }
      case 24:{
        return ExecMin3( aProgramPointer );
      }
      case 25:{
        return ExecMax( aProgramPointer );
      }
      case 26:{
        return ExecMax3( aProgramPointer );
      }
      case 100:{
        return ExecPrint( aProgramPointer );
      }
      case 101:{
        return ExecCreateNewBlock( aProgramPointer );
      }
      
      case 200:{
        return ExecArray_GetValue( aProgramPointer );
      }
      case 201:{
        return ExecArray_SetValue( aProgramPointer );
      }
      case 202:{
        return ExecArray_GetArraySumm( aProgramPointer );
      }
      case 203:{
        return ExecArray_GetMinValue( aProgramPointer );
      }
      case 204:{
        return ExecArray_GetMaxValue( aProgramPointer );
      }
      case 205:{
        return ExecArray_GetDimension( aProgramPointer );
      }
      case 206:{
        return ExecArray_GetDimensionLength( aProgramPointer );
      }
      case 207:{
        return ExecArray_Transpose( aProgramPointer );
      }
      case 208:{
        return ExecArray_Mul( aProgramPointer );
      }
      case 209:{
        return ExecArray_Inverse( aProgramPointer );
      }
      case 210:{
        return ExecArray_ShiftR( aProgramPointer );
      }
      case 211: {
        return ExecArray_ShiftL( aProgramPointer );
      }
      case 300:{
        return Exec_GetNormalDistribution( aProgramPointer );
      }
      case 301:{
        return Exec_GetNormalDistribution2( aProgramPointer );
      }
      case 400 : {
      	return ExecExit( aProgramPointer );
      }
      case 501 : {
      	return ExecFork(aProgramPointer);
      } 
      case 502 : {
      	return ExecRollback(aProgramPointer);
      }
      default: {
         ScriptException e = new ScriptException("Неизвестная команда для выполнения");
         throw e;
      }
    }//switch
  }
  
 
  public String GetResultType() throws ScriptException {
    if ( OperationIndex == -1 && function == null ) {
      ScriptException e = new ScriptException("Неизвестная команда для выполнения");
      throw e;
    }
    if ( OperationIndex != -1 ) {
      String s;
      int i = ScriptLanguageDef.FunctionsList[OperationIndex].length;
      s = ScriptLanguageDef.FunctionsList[OperationIndex][i-1];
      return s;
    }
    if ( function != null ){
    	return function.getResultTypeName();
    }
    throw new ScriptException("Неизвестная команда для выполнения");
  }

  public Variable GetResultVariable(int aProgramPointer) throws ScriptException {
    String s = GetResultType();
    Variable result = null;
    if ( s.equalsIgnoreCase("integer") ) {
      int i = 0;
      result =  new Variable(i);
    }
    if ( s.equalsIgnoreCase("boolean") ) {
      boolean f = false;
      result =  new Variable(f);
    }
    if ( s.equalsIgnoreCase("real") ) {
      float r = (float) 7.1;
      result =  new Variable(r);
    }
    if ( s.equalsIgnoreCase( "string" ) ){
      result = new Variable("");
    }

    return result;
  }

  private boolean FIsArrayOperationsPrepared = false;
  private int[] FCoordinates = null;
  private int FArrayDimension = 0;
  private int FArrayValueType = 0;
  private ScriptArray FArray = null;
  private Operand FIntOperand = null;

  private void PrepareArrayOperations( int aPointerToCommand ) throws ScriptException {
    Operand operandArray = InitOperand( aPointerToCommand + 1 );
    if ( operandArray.GetType() != Operand.OPERAND_TYPE_ARRAY ){
      ScriptException e = new ScriptException("Первым параметром функции должен идти массив");
      throw e;
    }
    ScriptArray array = (ScriptArray) operandArray;
    FArray = array;
    FArrayDimension =  array.GetDimension();
    FCoordinates = new int[FArrayDimension];
    //проверяем, чтобы первая команда после операции с массивом была именно командой, а не чем-либо иным. Это нужно
    // для организации контроля за правильным количеством параметров в вызове процедуры работы с массивом.
    // Необходимость такой проверки вызвана тем, что все процедуры, которые обращаются к матрицам, имеют неопределенное
    // количество параметров (поскольку матрица может иметь неграниченное количество размерностей)
    try{
      Operand op = InitOperand( aPointerToCommand + 2 + FArrayDimension );
      ScriptException e1 = new ScriptException("Неверное количество параметров в процедуре");
      throw e1;
    } catch (ScriptException e){
       //здесь ничего не делаем, поскольку ошибка - это то, что здесь и должно быть.
    }
    FArrayValueType = array.GetArrayValueType();
  }

  private void FillCoordinatesArray( int aPointerToFirstCoordOperand ) throws ScriptException {
    int i = 0;
    Operand arrayIndex;
    while ( i < FArrayDimension ){
      arrayIndex = InitOperand( aPointerToFirstCoordOperand + i );
      FCoordinates[i] = arrayIndex.GetIntValue();
      i++;
    }

  }

  /** Откомпилированный код имеет такую структуру:
   * mp.parser.ScriptOperationFunction@1df38fd
     array 0 - массив. Всегда идет после команды
     counter 0 индексы в массиве
     TmpVar_1 0 - значение, в которое нужно записывать результат

   * @param aProgramPointer
   * @return
   * @throws ScriptException
   */
  private int ExecArray_GetValue( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      PrepareArrayOperations( aProgramPointer );
      FIsArrayOperationsPrepared = true;
    }
    Operand result = InitOperand( aProgramPointer + 2 + FArrayDimension );
    FillCoordinatesArray( aProgramPointer + 2 );
    switch ( FArrayValueType ) {
      case Operand.OPERAND_TYPE_INTEGER:{
        result.SetValue( FArray.GetIntValue(FCoordinates) );
        break;
      }
      case Operand.OPERAND_TYPE_BOOLEAN:{
        result.SetValue( FArray.GetBooleanValue( FCoordinates ) );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        result.SetValue((float) FArray.GetFloatValue( FCoordinates ));
        break;
      }
      case Operand.OPERAND_TYPE_STRING:{
        result.SetValue( FArray.GetStringValue() );
        break;
      }
      default:{
        ScriptException e = new ScriptException("Неизвестный тип значений в массиве при выполнении функции GetArrayValue");
        throw e;
      }
    }
    return 2 + FArrayDimension;
  }

  private int ExecArray_SetValue( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      PrepareArrayOperations( aProgramPointer );
      FIsArrayOperationsPrepared = true;
    }
    Operand valueToStore = InitOperand( aProgramPointer + 2 + FArrayDimension );
    int storeType = valueToStore.GetType();
    FillCoordinatesArray( aProgramPointer + 2 );
    switch ( storeType ) {
      case Operand.OPERAND_TYPE_BOOLEAN:{
        FArray.SetValue( valueToStore.GetBooleanValue(), FCoordinates );
        break;
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        FArray.SetValue( valueToStore.GetIntValue(), FCoordinates );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        FArray.SetValue( valueToStore.GetFloatValue(), FCoordinates );
        break;
      }
      default:{
        ScriptException e = new ScriptException("Неизвестный тип значений в массиве при выполнении функции SetArrayValue");
        throw e;
      }
    }//case
    int addValue = 0;
    try {
      ScriptProgramObject po = (ScriptProgramObject) Program.get(aProgramPointer + 3 + FArrayDimension + 1);
      if ( po instanceof ScriptConstant || po instanceof Variable ) {
      	addValue = -1;
      }
    } catch (Exception e){
      addValue = 0;
    }
    return 3 + FArrayDimension + addValue;
  }

  private int ExecArray_GetArraySumm( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      PrepareArrayOperations( aProgramPointer );
      FIsArrayOperationsPrepared = true;
    }
    /*Operand operandArray = InitOperand( aProgramPointer + 1 );
    ScriptArray array = (ScriptArray) operandArray;*/
    Operand result = InitOperand( aProgramPointer + 2 );
    switch ( FArrayValueType ){
      case Operand.OPERAND_TYPE_INTEGER:{
        int res = FArray.GetIntSumm();
        result.SetValue( res );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        result.SetValue((float) FArray.GetFloatSumm());
        break;
      }
      default:{
        ScriptException e = new ScriptException("Невозможно получение суммы элементов для массива \"" + FArray.GetName() + "\"");
        throw e;
      }
    }//switch
    return 2;
  }

  private int ExecArray_GetMinValue( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      PrepareArrayOperations( aProgramPointer );
      FIsArrayOperationsPrepared = true;
    }
    Operand result = InitOperand( aProgramPointer + 2 );
    switch ( FArrayValueType ){
      case Operand.OPERAND_TYPE_INTEGER:{
        int res = FArray.GetIntMinValue();
        result.SetValue( res );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        result.SetValue((float) FArray.GetFloatMinValue());
        break;
      }
      default:{
        ScriptException e = new ScriptException("Невозможно получение суммы элементов для массива \"" + FArray.GetName() + "\"");
        throw e;
      }
    }//switch
    return  2;
  }

  private int ExecArray_GetMaxValue( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      PrepareArrayOperations( aProgramPointer );
      FIsArrayOperationsPrepared = true;
    }
    Operand result = InitOperand( aProgramPointer + 2 );
    switch ( FArrayValueType ){
      case Operand.OPERAND_TYPE_INTEGER:{
        int res = FArray.GetIntMaxValue();
        result.SetValue( res );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        result.SetValue((float) FArray.GetFloatMaxValue());
        break;
      }
      default:{
        ScriptException e = new ScriptException("Невозможно получение суммы элементов для массива \"" + FArray.GetName() + "\"");
        throw e;
      }
    }//switch
    return  2;
  }

  private int ExecArray_GetDimension( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      PrepareArrayOperations( aProgramPointer );
      FIsArrayOperationsPrepared = true;
    }
    Operand result = InitOperand( aProgramPointer + 2 );
    result.SetValue( FArray.GetDimension() );
    return  2;
  }

  private int ExecArray_GetDimensionLength( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      PrepareArrayOperations( aProgramPointer );
      FIsArrayOperationsPrepared = true;
    }
    Operand dimNum = InitOperand( aProgramPointer + 2 );
    if ( dimNum.GetType() != Operand.OPERAND_TYPE_INTEGER ){
      ScriptException e = new ScriptException("Ошибка при выполнении операции GetArrayDimensionLength: операнд " +
              "не целочисленного типа");
      throw e;
    }
    if ( dimNum.GetIntValue() >= FArrayDimension ){
      ScriptException e = new ScriptException("Ошибка при выполнении операции GetArrayDimensionLength: значение операнда " +
              "больше размерности массива");
      throw e;
    }
    Operand result = InitOperand( aProgramPointer + 3 );
    result.SetValue( FArray.GetDimensionLength( dimNum.GetIntValue() ) );
    return 3;
  }

  private int ExecArray_Transpose( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      PrepareArrayOperations( aProgramPointer );
      FIsArrayOperationsPrepared = true;
    }
    if ( FArray.GetDimension() != 2 ){
      ScriptException e = new ScriptException("Ошибка при транспонировании: матрица должна быть двумерной");
      throw e;
    }
    int length = FArray.GetDimensionLength(0);
    if ( length !=  FArray.GetDimensionLength(1) ){
      ScriptException e = new ScriptException("Ошибка при транспонировании: матрица должна быть квадратной");
      throw e;
    }
    int i = 0;
    int j = 0;
    int[] coord = new int[ FArrayDimension ];
    while ( i < length ){
      FCoordinates[0] = i;
      coord[1] = i;
      j = i+1;
      while ( j < length ){
        //System.out.println( "i = " + Integer.toString(i) + " j = " + Integer.toString( j ) );
        FCoordinates[1] = j;
        coord[0] = j;
        FArray.SwitchValues( FCoordinates, coord );
        j++;
      }
      i++;
    }

    return 2;
  }

  private ScriptArray FArray2 = null;
  private ScriptArray FArray3 = null;

  private int ExecArray_Mul( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      //PrepareArrayOperations( aProgramPointer );
      Operand operandArray = InitOperand( aProgramPointer + 1 );
      if ( operandArray.GetType() != Operand.OPERAND_TYPE_ARRAY ) {
        ScriptException e = new ScriptException("Первый операнд в команде умножения должен быть массивом");
        throw e;
      }
      FArray = (ScriptArray) operandArray;
      operandArray = InitOperand( aProgramPointer + 2 );
      if ( operandArray.GetType() != Operand.OPERAND_TYPE_ARRAY ) {
        ScriptException e = new ScriptException("Второй операнд в команде умножения должен быть массивом");
        throw e;
      }
      FArray2 = (ScriptArray) operandArray;
      operandArray = InitOperand( aProgramPointer + 3 );
      if ( operandArray.GetType() != Operand.OPERAND_TYPE_ARRAY ) {
        ScriptException e = new ScriptException("Третий операнд в команде умножения должен быть массивом");
        throw e;
      }
      FArray3 = (ScriptArray) operandArray;
      FIsArrayOperationsPrepared = true;
    }
    ScriptArray.Mul( FArray, FArray2, FArray3 );
    return 4;
  }

  private int ExecArray_Inverse( int aProgramPointer ) throws ScriptException{
    if ( !FIsArrayOperationsPrepared ){
      //PrepareArrayOperations( aProgramPointer );
      Operand operandArray = InitOperand( aProgramPointer + 1 );
      if ( operandArray.GetType() != Operand.OPERAND_TYPE_ARRAY ) {
        ScriptException e = new ScriptException("Первый операнд в команде инверсии должен быть массивом");
        throw e;
      }
      FArray = (ScriptArray) operandArray;
      operandArray = InitOperand( aProgramPointer + 2 );
      if ( operandArray.GetType() != Operand.OPERAND_TYPE_ARRAY ) {
        ScriptException e = new ScriptException("Второй операнд в команде инверсии должен быть массивом");
        throw e;
      }
      FArray2 = (ScriptArray) operandArray;
      FIsArrayOperationsPrepared = true;
    }
    FArray.CreateInverseMatrix( FArray2 );
    return 3;
  }

  private int ExecArray_ShiftR( int aProgramPointer ) throws ScriptException {
    if ( !FIsArrayOperationsPrepared ){
      Operand operandArray = InitOperand( aProgramPointer + 1 );
      if ( operandArray.GetType() != Operand.OPERAND_TYPE_ARRAY ) {
        ScriptException e = new ScriptException("Первый операнд в команде сдвига должен быть массивом");
        throw e;
      }
      FArray = (ScriptArray) operandArray;
      Operand step = InitOperand( aProgramPointer + 2 );
      if (step.GetType() != Operand.OPERAND_TYPE_INTEGER ) {
        ScriptException e = new ScriptException("Второй операнд в команде сдвига должен быть целым числом");
        throw e;
      }
      FIntOperand = step;
    }
    FArray.ShiftR( FIntOperand.GetIntValue() );
    return 3;

  }

  private int ExecArray_ShiftL( int aProgramPointer ) throws ScriptException {
    if ( !FIsArrayOperationsPrepared ){
      Operand operandArray = InitOperand( aProgramPointer + 1 );
      if ( operandArray.GetType() != Operand.OPERAND_TYPE_ARRAY ) {
        ScriptException e = new ScriptException("Первый операнд в команде сдвига должен быть массивом");
        throw e;
      }
      FArray = (ScriptArray) operandArray;
      Operand step = InitOperand( aProgramPointer + 2 );
      if (step.GetType() != Operand.OPERAND_TYPE_INTEGER ) {
        ScriptException e = new ScriptException("Второй операнд в команде сдвига должен быть целым числом");
        throw e;
      }
      FIntOperand = step;
    }
    FArray.ShiftL( FIntOperand.GetIntValue() );
    return 3;

  }


  ///////////////////////////////////////////////////////////////////////////
  ////////////// Распределения //////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////

  private int Exec_GetNormalDistribution ( int aProgramPointer ) throws ScriptException{
    Operand mu = InitOperand( aProgramPointer + 1 );
    Operand sigma = InitOperand( aProgramPointer + 2 );
    Operand result = InitOperand( aProgramPointer + 3 );
    result.SetValue( (float)Distributions.GetNormalDistribution( mu.GetFloatValue(), sigma.GetFloatValue() ) );
    return 3;
  }

  private int Exec_GetNormalDistribution2 ( int aProgramPointer ) throws ScriptException{
    Operand mu = InitOperand( aProgramPointer + 1 );
    Operand sigma = InitOperand( aProgramPointer + 2 );
    Operand rndValue = InitOperand( aProgramPointer + 3 );
    Operand result = InitOperand( aProgramPointer + 4 );
    result.SetValue( (float)Distributions.GetNormalDistribution( mu.GetFloatValue(),
            sigma.GetFloatValue(), rndValue.GetFloatValue() ) );
    return 4;
  }

  public String toString(){
  	return ScriptLanguageDef.FunctionsList[OperationIndex][0];
  }
  

  ///////////////////////////////////////////////////////////////////////////
  ////////////// Fork //////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////

  
  private int ExecFork(int aProgramPointer) throws ScriptException {
  	Operand op1 = InitOperand( aProgramPointer + 1 );
  	ModelExecutionManager manager = ModelExecutionContext.GetManager( op1.GetStringValue() );
  	if ( manager == null ){      
      throw new ScriptException("fork err: отсутствует модель с именем \"" + op1.GetStringValue() + "\"");      
    }
  	Operand op2 = InitOperand( aProgramPointer + 2 );
    int tactCount = op2.GetIntValue();
    Operand op3 = InitOperand( aProgramPointer + 3 );
    Operand forkLabel = InitOperand( aProgramPointer + 4 );
    boolean nestedFork = op3.GetBooleanValue();    
    UUID forkUid = manager.fork(tactCount, nestedFork);
    forkLabel.SetValue( forkUid.toString() );  	
  	return 4;  	
  }  
  
  private int ExecRollback(int aProgramPointer) throws ScriptException {
  	Operand op1 = InitOperand( aProgramPointer + 1 );
  	ModelExecutionManager manager = ModelExecutionContext.GetManager( op1.GetStringValue() );
  	if ( manager == null ){      
      throw new ScriptException("fork err: отсутствует модель с именем \"" + op1.GetStringValue() + "\"");      
    }
  	Operand forkLabel = InitOperand( aProgramPointer + 2 );
  	String s = forkLabel.GetStringValue();
  	UUID uid = UUID.fromString(s);
  	manager.rollback(uid);
  	return 3;  	
  }  
  
  
  //////////////////////////////////////
  //////////// Создание нового блока во время выполнения модели
  
  private int ExecCreateNewBlock(int aProgramPointer) throws ScriptException {  	
  	int opCount = this.getOperandCount();
  	if ( !( opCount == 2 || opCount == 3 ) ) {
  		throw new ScriptException("Неверное количество операндов функции CreateNewBlock");
  	}
  	Operand op1 = InitOperand( aProgramPointer + 1 );
  	ModelExecutionManager manager = ModelExecutionContext.GetManager( op1.GetStringValue() );
  	if ( manager == null ){      
      throw new ScriptException("Отсутствует модель с именем \"" + op1.GetStringValue() + "\"");      
    }
  	Operand op2 = InitOperand( aProgramPointer + 2 );
  	String parentParamName = null;
  	String blockName = op2.GetStringValue();
  	if ( opCount == 3 ) {
  		Operand op3 = InitOperand( aProgramPointer + 3 );
  		parentParamName = op3.GetStringValue();
  	}
  	manager.createNewBlock(blockName, parentParamName);
  	
  	return opCount+1;
  	
  }
  
  private int ExecExternalFunction(ExternalFunction function, int aProgramPointer) throws ScriptException{
  	int paramNum = 1;
  	if ( function.getParamCount() == 0 ) {
  		function.execute();
  		Operand returnValue = InitOperand( aProgramPointer + 1 );
  		if (returnValue.GetType() == Operand.OPERAND_TYPE_BOOLEAN) {
  			returnValue.SetValue(true);
  		}
  		return 1;
  	}
  	Object[] params = new Object[function.getParamCount()]; 
  	while ( paramNum <= function.getParamCount() ) {
  		Operand op = InitOperand( aProgramPointer + paramNum );
  		params[paramNum-1] = op;
  		paramNum++;
  	}
  	function.execute(params);
  	return function.getParamCount();  	
  }
  
  
}
 
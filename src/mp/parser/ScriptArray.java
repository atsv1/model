package mp.parser;

import mp.utils.ServiceLocator;

import java.util.Arrays;


/**
 * User: Администратор
 * Date: 29.06.2008
 * Time: 11:47:07
 */
public  class ScriptArray extends Variable{
  /**Переменная хранит тип значений, которые хранятся в массиве
   * т.е. переменная принимает одно из значений, описанных в константах OPERAND_TYPE_
   */
  private int ArrayValueType = -1;

  private boolean[] FBooleanArray = null;
  private int[] FIntArray = null;
  private double[] FFloatArray = null;
  private String[] FStringArray;

  private Object[] FArrayOfArray = null;

  private int FDimension = 0; //размерность массива
  private ArrayDefinition FDefinition;

  //переменные, хранящие минимальные значения
  private double FMinFloatValue = Double.MAX_VALUE;
  private int FMinIntValue = Integer.MAX_VALUE;
  //private boolean FIsMinCalculated = false;
  //переменные, хранящие максимальные значения
  private double FMaxFloatValue = Double.MIN_VALUE;
  private int FMaxIntValue = Integer.MIN_VALUE;
  //private boolean FIsMaxCalculated = false;
  //переменные, хранящие суммы
  private double FFloatSumm;
  private int FIntSumm;
  private boolean FIsSummCalculated = false;

  private static final int FUNCTION_SUMM = 1;
  private static final int FUNCTION_MIN = 2;
  private static final int FUNCTION_MAX = 3;
  private static final int FUNCTION_EXECUTOR = 4;


  private Object GetNewConcreteArray( int aArrayLength ) throws ScriptException{
    String initValue = FDefinition.GetInitValue();
    switch ( ArrayValueType ){
      case Operand.OPERAND_TYPE_BOOLEAN:{
        if ( initValue == null || "".equalsIgnoreCase( initValue ) ){
           return new boolean[aArrayLength];
        } else {
          boolean[] temp = new boolean[aArrayLength];
          if ( "true".equalsIgnoreCase( initValue ) ){
            Arrays.fill( temp, true );
          }
          return temp; 
        }
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        if ( initValue == null || "".equalsIgnoreCase( initValue ) ){
          return new int[aArrayLength];
        }
        int[] temp = new int[aArrayLength];
        try{
          int val = Integer.parseInt( initValue );
          Arrays.fill( temp, val );
        } catch (NumberFormatException e){
          ScriptException e1 = new ScriptException("Ошибка при обработке начального значения массива: " + initValue);
          throw e1;
        }
        return temp;
      }
      case Operand.OPERAND_TYPE_REAL:{
        if ( initValue == null || "".equalsIgnoreCase( initValue ) ){
          return new double[aArrayLength];
        }
        double[] temp = new double[aArrayLength];
        try{
          double val = Double.parseDouble( initValue );
          Arrays.fill( temp, (float)val );
        } catch (NumberFormatException e){
          ScriptException e1 = new ScriptException("Ошибка при обработке начального значения массива: " + initValue);
          throw e1;
        }
        return temp;
      }
      case Operand.OPERAND_TYPE_STRING:{
         return new String[aArrayLength];
      }
      default:{
        ScriptException e = new ScriptException("Неизвестный тип при создании массива");
        throw e;
      }
    }

  }

  private void SaveConcreteArray( Object aConcretaArray ){
    switch ( ArrayValueType ){
      case Operand.OPERAND_TYPE_BOOLEAN:{
        FBooleanArray = (boolean[]) aConcretaArray;
        break;
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        FIntArray = (int[]) aConcretaArray;
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        FFloatArray = (double[]) aConcretaArray;
        break;
      }
      case Operand.OPERAND_TYPE_STRING:{
        FStringArray = (String[]) aConcretaArray;
        break;
      }
    }//case

  }

  /**Рекурсивная процедура.
   *
   * @param aCurrentDimension - уровень текущей создаваемой размерности. 0 - означает, что создается массив из
   * конкретных значений
   * @return
   */
  private Object GetArray( int aCurrentDimension ) throws ScriptException {
    int length = FDefinition.GetDimensionLength( aCurrentDimension );
    if ( aCurrentDimension == 0 ){
      return GetNewConcreteArray( length );
    }
    //это не последний уровень матрицы. поэтому здесь будет цикл и рекурсия
    int i = 0;
    Object[] result = new Object[ length ];
    while ( i < length ){
      result[i] = GetArray( aCurrentDimension - 1 );
      i++;
    }
    return result;
  }

  protected void SaveArrayDef( ArrayDefinition aArrayDef) throws ScriptException {
    if ( aArrayDef == null ){
      ScriptException e = new ScriptException("Попытка создать массив на основании пустого описания");
      throw e;
    }
    FDimension = aArrayDef.GetDimensionCount();
    if ( FDimension == 0 ){
      ScriptException e = new ScriptException("Попытка создать массив с размерностью 0");
      throw e;
    }
    ArrayValueType = aArrayDef.GetValueType();
    FDefinition = aArrayDef;
    InitArray();
  }

  /**  Инициализация массива - заполнение служебных поле и создание объектов, в которые будут записываться значения
   *
   * @param aArrayDef - описание массива.
   *  Особенное внимание следует  уделить порядку следования размерностей в этом объекте.
   * Например:
   * 1. если список размерностей содержат два значения 10 и 10, то создастся матрица 10Х10, это самый
   *    простой случай
   * 2. Если список размерностей содержит значения 2 и 5 (то есть в объект aArrayDef сначала было добавлено число 2,
   *   а затем 5), то создатся матрица 5Х2, к элементам которой можно обращаться при помощих двух индексов: Х[i,j],
   *   причем i может изменяться от 0 до 4, а j может изменяться от 0 до 1.
   * 3. Аналогично п.2. создаются и все матрицы бОльших размерностей, чем 2. 
   * @throws ScriptException
   */
  public void InitArray( ArrayDefinition aArrayDef) throws ScriptException {
    this.InitArray();
    SaveArrayDef( aArrayDef );
    if ( FDimension == 1 ) {
      SaveConcreteArray( GetArray( FDefinition.GetDimensionCount() - 1 ) );
    } else {
      FArrayOfArray = (Object[]) GetArray( FDefinition.GetDimensionCount() - 1 );
    }
    FIsSummCalculated = false;
  }

  public int GetDimension(){
    return FDimension; 
  }

  /** Функция возвращает количество элементов выбранной размерности.
   * Так, если у нас есть двумерная матрица 3 х 4, то количество элементов в 0-й размерности будет 3, а количество
   * элементов 1-й размерности будет 4
   *
   * @return
   */
  public int GetDimensionLength( int aDimensionNum ){
    //такое уловие для GetDimensionLength нужно потому что измерения матрицы создаются в обратном порядке 
    return FDefinition.GetDimensionLength( FDefinition.GetDimensionCount() - 1 -  aDimensionNum);
  }

  protected ArrayDefinition GetArrayDef(){
    return FDefinition;
  }

  public int GetIntValue() throws ScriptException {
    ScriptException e = new ScriptException("Недопустимая операция для массива: GetInt");
    throw e;
  }

  public float GetFloatValue() throws ScriptException {
    ScriptException e = new ScriptException("Недопустимая операция для массива: GetInt");
    throw e;
  }

  public boolean GetBooleanValue(){
    return false;
  }

  public String GetStringValue() throws ScriptException{
    ScriptException e = new ScriptException("Недопустимая операция для массива: GetInt");
    throw e;
  }

  public int GetArrayValueType(){
    return ArrayValueType;
  }

  private void CheckFoBooleanOperations(int[] aCoordinates)throws ScriptException{
    if ( ArrayValueType != Operand.OPERAND_TYPE_BOOLEAN ){
      ScriptException e = new ScriptException( "Ошибка в массиве \"" + GetName() +
              "\": невозможно получить значения типа boolean" );
      throw e;
    }
    if ( aCoordinates == null || aCoordinates.length != FDimension ){
      ScriptException e = new ScriptException("Ошибка в массиве: не совпадают размерности");
      throw e;
    }
  }

  private void CheckFoIntOperations(int[] aCoordinates)throws ScriptException{
    if ( ArrayValueType != Operand.OPERAND_TYPE_INTEGER && ArrayValueType != Operand.OPERAND_TYPE_REAL){
      ScriptException e = new ScriptException( "Ошибка в массиве \"" + GetName() +
              "\": невозможно получить значения типа integer" );
      throw e;
    }
    if ( aCoordinates == null || aCoordinates.length != FDimension ){
      ScriptException e = new ScriptException("Ошибка в массиве: не совпадают размерности");
      throw e;
    }
  }

   private void CheckFoFloatOperations(int[] aCoordinates)throws ScriptException{
    if ( ArrayValueType != Operand.OPERAND_TYPE_REAL && ArrayValueType != Operand.OPERAND_TYPE_INTEGER ){
      ScriptException e = new ScriptException( "Ошибка в массиве \"" + GetName() +
              "\": невозможно получить значения типа real" );
      throw e;
    }
    if ( aCoordinates == null || aCoordinates.length != FDimension ){
      ScriptException e = new ScriptException("Ошибка в массиве: не совпадают размерности");
      throw e;
    }
  }

  private Object GetConcreteArray( int[] aCoordinates ) throws ScriptException {
    int currentDim = 0;
    Object[] tempArr = FArrayOfArray;
    int i;
    while ( currentDim < FDimension - 2 ){
      i = aCoordinates[currentDim];
      if ( i >= tempArr.length ){
        ScriptException e = new ScriptException("Выход за границы массива: " + Integer.toString( i ) +
           " больше, чем размер массива " + Integer.toString(tempArr.length));
        throw e;
      }
      tempArr = (Object[]) tempArr[ i ];
      currentDim++;
    }
    return tempArr[ aCoordinates[currentDim] ];
  }

  public boolean GetBooleanValue( int[] aCoordinates ) throws ScriptException{
    CheckFoBooleanOperations( aCoordinates );
    if ( FDimension == 1 ){
      return FBooleanArray[ aCoordinates[0] ];
    }
    boolean[] concreteArr = (boolean[]) GetConcreteArray( aCoordinates );
    return concreteArr[ aCoordinates[FDimension - 1] ];
  }

  public void SetValue( boolean aValue, int[] aCoordinates ) throws ScriptException {
    CheckFoBooleanOperations( aCoordinates );
    boolean oldValue;
    boolean[] arr = null;
    int concreteCoord = 0;
    if ( FDimension == 1 ){
      arr = FBooleanArray;
      concreteCoord = aCoordinates[0];
    } else {
      arr = (boolean[]) GetConcreteArray( aCoordinates );
      concreteCoord = aCoordinates[ aCoordinates.length-1 ];
    }
    oldValue = arr[ concreteCoord ];
    arr[ concreteCoord ] = aValue;
    if ( oldValue != aValue ){
      FireChangeEvent();
    }
  }

  public int GetIntValue( int[] aCoordinates ) throws ScriptException {
    CheckFoIntOperations( aCoordinates );
    if ( FDimension == 1 ){
      return FIntArray[ aCoordinates[0] ];
    }
    int[] concreteArr = (int[]) GetConcreteArray( aCoordinates );
    if ( concreteArr.length <= aCoordinates[FDimension - 1] ){
      ScriptException e = new ScriptException("Вход за пределы массива. Индекс " + Integer.toString( aCoordinates[FDimension - 1] ));
      throw e;
    }
    return concreteArr[ aCoordinates[FDimension - 1] ];
  }

  public void SetValue( int aValue, int[] aCoordinates) throws ScriptException {
    CheckFoIntOperations( aCoordinates );
    if ( ArrayValueType == Operand.OPERAND_TYPE_REAL ){
      SetValue( (double)aValue, aCoordinates );
      return;
    }
    int oldValue;
    int[] arr = null;
    int concreteCoord = 0;
    if ( FDimension == 1 ){
      arr = FIntArray;
      concreteCoord = aCoordinates[0];
    } else {
      arr = (int[]) GetConcreteArray( aCoordinates );
      concreteCoord = aCoordinates[ aCoordinates.length-1 ];
    }
    oldValue = arr[ concreteCoord ];
    arr[ concreteCoord ] = aValue;

    if ( oldValue != aValue ){
      FireChangeEvent();
      
      if ( FIsSummCalculated ){
        //сумма всех элементов матрицы уже рассчитана. Значит, надо изменить значение суммы в соответствии с новым
        // значением
        FIntSumm = FIntSumm + aValue - oldValue;
      }
      if ( aValue < FMinIntValue  ){
        FMinIntValue = aValue;
      }
      if ( aValue > FMaxIntValue ){
        FMaxIntValue = aValue;
      }
    }// значение изменилось
  }

  public double GetFloatValue( int[] aCoordinates ) throws ScriptException {
    CheckFoFloatOperations( aCoordinates );
    if ( FDimension == 1 ){
      switch ( ArrayValueType ){
        case Operand.OPERAND_TYPE_REAL: {
          return FFloatArray[ aCoordinates[0] ];
        }
        case Operand.OPERAND_TYPE_INTEGER: {
          return FIntArray[ aCoordinates[0] ];
        }
        default: {
          ScriptException e = new ScriptException("нельзя получить вещественоне число из массива \"" + GetName() + "\"");
          throw e;
        }
      }//switch
    }
    double[] concreteArr = (double[]) GetConcreteArray( aCoordinates );
    return concreteArr[ aCoordinates[FDimension - 1] ];
  }

  public void SetValue( double aValue, int[] aCoordinates ) throws ScriptException {
    CheckFoFloatOperations( aCoordinates );
    if ( ArrayValueType == Operand.OPERAND_TYPE_INTEGER ){
      SetValue( (int)aValue, aCoordinates );
      return;
    }
    double oldValue;
    double[] arr = null;
    int concreteCoord = 0;
    if ( FDimension == 1 ){
      arr = FFloatArray;
      concreteCoord = aCoordinates[0];
    } else {
      arr = (double[]) GetConcreteArray( aCoordinates );
      concreteCoord = aCoordinates[ aCoordinates.length-1 ];
    }
    oldValue = arr[ concreteCoord ];
    arr[ concreteCoord ] = aValue;
    int compareRes = ServiceLocator.CompareDouble( oldValue, aValue );
    if ( compareRes != 0 ){
      FireChangeEvent();
      if ( FIsSummCalculated ){
        FFloatSumm = FFloatSumm - oldValue + aValue;
      }
      if ( aValue < FMinIntValue  ){
        FMinFloatValue = aValue;
      }
      if ( aValue > FMaxFloatValue ){
        FMaxFloatValue = aValue;
      }
    }
  }

  private int GetIntSumm( int[] array ){
    int result = 0;
    int i = 0;
    int length = array.length;
    while ( i < length ){
      result += array[i];
      i++;
    }
    return result;
  }

  private double GetDoubleSumm( double[] array ){
    double result = 0;
    int i = 0;
    int length = array.length;
    while ( i < length ){
      result += array[i];
      i++;
    }
    return result;
  }

  private void CalculateSumm( Object array ){
    switch (ArrayValueType){
      case Operand.OPERAND_TYPE_INTEGER:{
        FIntSumm += GetIntSumm((int[]) array);
        FIsSummCalculated = true;
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        FFloatSumm += GetDoubleSumm((double[]) array);
        FIsSummCalculated = true;
        break;
      }
    }//switch
  }

  private void CalculateSumm(){
    switch (ArrayValueType){
      case Operand.OPERAND_TYPE_INTEGER:{
        CalculateSumm( FIntArray );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        CalculateSumm( FFloatArray );
        break;
      }
    }//switch

  }

  private int GetMinValue( int[] array ){
    int result = Integer.MAX_VALUE;
    int i = 0;
    int length = array.length;
    while ( i < length ){
      if ( result > array[i] ){
        result = array[i];
      }
      i++;
    }
    return result;
  }

  private double GetMinValue( double[] array ){
    double result = Double.MAX_VALUE;
    int i = 0;
    int length = array.length;
    while ( i < length ){
      if ( result > array[i] ){
        result = array[i];
      }
      i++;
    }
    return result;
  }

  private void CalculateMinValue( Object array ){
    switch (ArrayValueType){
      case Operand.OPERAND_TYPE_INTEGER:{
        int currentRes = GetMinValue((int[]) array);
        if ( currentRes < FMinIntValue  ){
          FMinIntValue = currentRes; 
        }
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        double currentMin = GetMinValue((double[]) array);
        if ( currentMin < FMinFloatValue  ){
          FMinFloatValue = currentMin; 
        }
        break;
      }
    }//switch
  }

  private void CalculateMinValue(){
    switch (ArrayValueType){
      case Operand.OPERAND_TYPE_INTEGER:{
        CalculateMinValue( FIntArray );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        CalculateMinValue( FFloatArray );
        break;
      }
    }//switch
  }

  private int GetMaxValue(int[] array){
    int result = Integer.MIN_VALUE;
    int i = 0;
    int length = array.length;
    while ( i < length ){
      if ( result < array[i] ){
        result = array[i];
      }
      i++;
    }
    return result;
  }

  private double GetMaxValue( double[] array ){
    double result = Double.MIN_VALUE;
    int i = 0;
    int length = array.length;
    while ( i < length ){
      if ( result < array[i] ){
        result = array[i];
      }
      i++;
    }
    return result;

  }

  private void CalculateMaxValue( Object array ){
    switch (ArrayValueType){
      case Operand.OPERAND_TYPE_INTEGER:{
        int currentRes = GetMaxValue((int[]) array);
        if ( currentRes > FMaxIntValue  ){
          FMaxIntValue = currentRes;
        }
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        double currentMax = GetMaxValue((double[]) array);
        if ( currentMax > FMaxFloatValue  ){
          FMaxFloatValue = currentMax; 
        }
        break;
      }
    }//switch
  }

  private void CalculateMaxValue(){
    switch (ArrayValueType){
      case Operand.OPERAND_TYPE_INTEGER:{
        CalculateMaxValue( FIntArray );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        CalculateMaxValue( FFloatArray );
        break;
      }
    }//switch
  }

  private void RunExecutor( Object aConcreteArray, ArrayExecutor aExecutor ){
    switch (ArrayValueType){
      case Operand.OPERAND_TYPE_INTEGER:{
        aExecutor.ExecIntOperation((int[]) aConcreteArray);
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        aExecutor.ExecFloatOperation((double[]) aConcreteArray);
        break;
      }
      case Operand.OPERAND_TYPE_BOOLEAN:{
        aExecutor.ExecBooleanOperation((boolean[]) aConcreteArray);
        break;
      }
      case Operand.OPERAND_TYPE_STRING:{
        aExecutor.ExecStringOperation((String[]) aConcreteArray);
        break;
      }
    }//switch

  }


  private void ExecuteArrayFunction( int aFunctionCode, Object aConcreteArray, ArrayExecutor aExecutor ){
    switch ( aFunctionCode ){
      case FUNCTION_SUMM:{
        CalculateSumm( aConcreteArray );
        break;
      }
      case FUNCTION_MIN:{
        CalculateMinValue( aConcreteArray );
        break;
      }
      case FUNCTION_MAX:{
        CalculateMaxValue( aConcreteArray );
        break;
      }
      case FUNCTION_EXECUTOR:{
        RunExecutor( aConcreteArray, aExecutor );
        break;
      }
    }//case
  }


  /**Метод осуществляет обход всего массива. И для каждого одномерного массива конкретных значений вызывает
   * функцию, код которой указан в параметре
   *
   * @param functionCode - код функции, которую должен вызвать обходчик
   * @param array - 
   */
  private void Runner( int functionCode, Object[] array, int dimensionNum, ArrayExecutor aExecutor ){
    if ( dimensionNum == 1 ){
      int i = 0;
      int length = array.length;
      Object concreteArray;
      while ( i < length ){
        concreteArray = array[i];
        ExecuteArrayFunction( functionCode, concreteArray, aExecutor );
        i++;
      }
    } else {
      Object[] currentArray;
      int length = array.length;
      int i = 0;
      while ( i < length ){
        currentArray = (Object[]) array[i];
        Runner( functionCode, currentArray, dimensionNum - 1, aExecutor );
        i++;
      }
    }

  }

  private void ExecToAllArray( ArrayExecutor aExecutor ){
    if ( FDimension == 1 ){
      switch ( ArrayValueType ){
        case Operand.OPERAND_TYPE_BOOLEAN:{
          aExecutor.ExecBooleanOperation( FBooleanArray );
          break;
        }
        case Operand.OPERAND_TYPE_INTEGER:{
          aExecutor.ExecIntOperation( FIntArray );
          break;
        }
        case Operand.OPERAND_TYPE_REAL:{
          aExecutor.ExecFloatOperation( FFloatArray );
          break;
        }
        case Operand.OPERAND_TYPE_STRING:{
          aExecutor.ExecStringOperation( FStringArray );
          break;
        }
      }//switch
      return;          
    }
    Runner( FUNCTION_EXECUTOR, FArrayOfArray, FDimension - 1, aExecutor );
  }

  public int GetIntSumm(){
    if ( ArrayValueType == Operand.OPERAND_TYPE_REAL ){
      return (int) GetFloatSumm();
    }
    if ( !FIsSummCalculated ){
      if ( FDimension == 1 ){
        CalculateSumm();
      } else {
        Runner( FUNCTION_SUMM, FArrayOfArray, FDimension - 1, null );
      }
    }
    return FIntSumm; 
  }

  public double GetFloatSumm(){
    if ( ArrayValueType == Operand.OPERAND_TYPE_INTEGER ){
      return GetIntSumm();
    }
    if ( !FIsSummCalculated ){
      if ( FDimension == 1 ){
        CalculateSumm();
      } else {
        Runner( FUNCTION_SUMM, FArrayOfArray, FDimension - 1, null );
      }
    }
    return FFloatSumm;
  }

  public int GetIntMinValue(){
    FMinIntValue = Integer.MAX_VALUE;
    if ( ArrayValueType == Operand.OPERAND_TYPE_REAL ){
      return (int) GetFloatMinValue(); 
    }
    if ( FDimension == 1 ){
      CalculateMinValue();
    } else {
      Runner( FUNCTION_MIN, FArrayOfArray, FDimension - 1, null );
    }
    return FMinIntValue;
  }

  public double GetFloatMinValue(){
    FMinFloatValue = Double.MAX_VALUE;
    if ( ArrayValueType == Operand.OPERAND_TYPE_INTEGER ){
      return GetIntMinValue();
    }

    if ( FDimension == 1 ){
      CalculateMinValue();
    } else {
      Runner( FUNCTION_MIN, FArrayOfArray, FDimension - 1, null );
    }
    return FMinFloatValue;
  }

  public int GetIntMaxValue(){
    FMaxIntValue = Integer.MIN_VALUE;
    if ( ArrayValueType == Operand.OPERAND_TYPE_REAL ){
      return (int) GetFloatMaxValue();
    }
    if ( FDimension == 1 ){
      CalculateMaxValue();
    } else {
      Runner( FUNCTION_MAX, FArrayOfArray, FDimension - 1, null );
    }
    return FMaxIntValue;
  }

  public double GetFloatMaxValue(){
    FMaxFloatValue = Double.MIN_VALUE;
    if ( ArrayValueType == Operand.OPERAND_TYPE_INTEGER ){
      return GetIntMaxValue();
    }
    if ( FDimension == 1 ){
      CalculateMaxValue();
    } else {
      Runner( FUNCTION_MAX, FArrayOfArray, FDimension - 1, null );
    }
    return FMaxFloatValue;
  }

  public Object clone(){
    int type = this.GetType();
    if ( type != Operand.OPERAND_TYPE_ARRAY ) {
      System.out.println("собственный тип матрицы не является матрицей");
    }
    ScriptArray newArray = new ScriptArray();
    newArray.InitArray();
    try {
      newArray.StoreValueOf( this );
    } catch (ScriptException e) {
      System.out.println( e.getMessage() );
      return null;
    }
    newArray.SetName( this.GetName() );
    return newArray;
  }

  public void StoreValueOf(Operand aNewValue) throws ScriptException{

    int type = aNewValue.GetType();
    if ( type != Operand.OPERAND_TYPE_ARRAY ){
      ScriptException e = new ScriptException("Попытка присвоить переменной-массиву значение переменной не массива");
      throw e;
    }
    ScriptArray array = (ScriptArray) aNewValue;
    FBooleanArray = array.FBooleanArray;
    FIntArray = array.FIntArray;
    FFloatArray = array.FFloatArray;
    FStringArray = array.FStringArray;
    FArrayOfArray = array.FArrayOfArray;

    FMinFloatValue = array.FMinFloatValue;
    FMinIntValue = array.FMinIntValue;
    FMaxFloatValue = array.FMaxFloatValue;
    FMaxIntValue = array.FMaxIntValue;
    FFloatSumm = array.FFloatSumm;
    FIntSumm = array.FIntSumm;
    FIsSummCalculated = array.FIsSummCalculated;
    SetChangeListeners( array );
    SaveArrayDef( array.GetArrayDef() );
  }

  public void StoreValueToVar( Operand aOperand, int[]  aCoordinates) throws ScriptException{
    if ( aOperand == null ){
      return;
    }
    if ( aCoordinates == null || aCoordinates.length != FDimension ){
      ScriptException e = new ScriptException("Невалидные координаты");
      throw e;
    }
    switch (ArrayValueType){
       case Operand.OPERAND_TYPE_BOOLEAN:{
         aOperand.SetValue( this.GetBooleanValue( aCoordinates ) );
         return;
       }
       case Operand.OPERAND_TYPE_INTEGER:{
         aOperand.SetValue( this.GetIntValue( aCoordinates ) );
         return;
       }
       case Operand.OPERAND_TYPE_REAL:{
         aOperand.SetValue( (float)this.GetFloatValue( aCoordinates ) );
         return;
       }
    }//switch
  }

  public void StoreValueFromVar( Operand aOperand, int[]  aCoordinates) throws ScriptException{
    if ( aOperand == null ){
      return;
    }
    if ( aCoordinates == null || aCoordinates.length != FDimension ){
      ScriptException e = new ScriptException("Невалидные координаты");
      throw e;
    }
    switch (ArrayValueType){
       case Operand.OPERAND_TYPE_BOOLEAN:{
         SetValue( aOperand.GetBooleanValue(), aCoordinates  );
         return;
       }
       case Operand.OPERAND_TYPE_INTEGER:{
         SetValue( aOperand.GetIntValue(), aCoordinates  );
         return;
       }
       case Operand.OPERAND_TYPE_REAL:{
         SetValue( aOperand.GetFloatValue(), aCoordinates );
         return;
       }
    }//switch

  }

  private void StoreConcreteArrays( ScriptArray aArray ) throws ScriptException {
    switch ( ArrayValueType ){
      case Operand.OPERAND_TYPE_BOOLEAN:{
        aArray.FBooleanArray = FBooleanArray.clone();
        return;
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        aArray.FIntArray = FIntArray.clone();
        return;
      }
      case Operand.OPERAND_TYPE_REAL:{
        aArray.FFloatArray = FFloatArray.clone();
        return;
      }
      case Operand.OPERAND_TYPE_STRING:{
        aArray.FStringArray = FStringArray.clone();
        return;
      }
      default:{
        ScriptException e = new ScriptException("Неизвестный тип значения в массиве при операции копирования");
        throw e;
      }
    }//switch
  }

  private Object GetConcreteArrayClone( Object aArray ) throws ScriptException {
    switch ( ArrayValueType ){
      case Operand.OPERAND_TYPE_BOOLEAN:{
        boolean[] arr = (boolean[]) aArray;
        return arr.clone();
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        int[] arr = (int[]) aArray;
        return arr.clone();
      }
      case Operand.OPERAND_TYPE_REAL:{
        double[] arr = (double[]) aArray;
        return arr.clone();
      }
      case Operand.OPERAND_TYPE_STRING:{
        String[] arr = (String[]) aArray;
        return arr.clone();
      }
      default:{
        ScriptException e = new ScriptException("Неизвестный тип значения в массиве при операции копирования");
        throw e;
      }
    }//switch

  }

  private void StoreValuesToArray( int aCurrentDimension, Object aArrayFrom, Object aArrayTo ) throws ScriptException {
    int i = 0;
    int elementsCount = this.GetDimensionLength( aCurrentDimension );
    Object[] arrayFrom = (Object[]) aArrayFrom;
    Object[] arrayTo = (Object[]) aArrayTo;
    while ( i < elementsCount ){
      if ( aCurrentDimension == FDimension - 2 ){
        arrayTo[i] = GetConcreteArrayClone( arrayFrom[i] );  
      } else {
        StoreValuesToArray( aCurrentDimension+1, arrayFrom[i], arrayTo[i] );
      }
      i++;
    }

  }

  public void CopyValuesToArray( ScriptArray aArray ) throws ScriptException{
    if ( aArray == null ){
      ScriptException e = new ScriptException("Пустая матрица для копирования данных");
      throw e;
    }
    aArray.InitArray( FDefinition );
    if ( FDimension == 1 ){
      StoreConcreteArrays( aArray );
      return;
    }
    StoreValuesToArray( 0, FArrayOfArray, aArray.FArrayOfArray );
  }

  public void SwitchValues( int[] aCoord1, int[] aCoord2 ) throws ScriptException {
    if ( aCoord1 == null || aCoord2 == null ){
      ScriptException e = new ScriptException("Пустые координаты для команды обмена значениями");
      throw e;
    }
   if ( aCoord1.length != aCoord2.length ){
     ScriptException e = new ScriptException(" Неодинаковые размерности координат для процедуры обмена значениями");
     throw e;
   }
    if ( aCoord1.length != FDimension ){
      ScriptException e = new ScriptException("Размерность переданных координат не совпадает с размерностью матрицы");
      throw e;
    }
    switch (ArrayValueType){
      case Operand.OPERAND_TYPE_BOOLEAN:{
        boolean val1 = GetBooleanValue( aCoord1 );
        boolean val2 = GetBooleanValue( aCoord2 );
        SetValue( val2, aCoord1 );
        SetValue( val1, aCoord2 );
        return;
      }
      case Operand.OPERAND_TYPE_INTEGER:{
        int val1 = GetIntValue( aCoord1 );
        int val2 = GetIntValue( aCoord2 );
        SetValue( val2, aCoord1 );
        SetValue( val1, aCoord2 );
        return;
      }
      case Operand.OPERAND_TYPE_REAL:{
        double val1 = GetFloatValue( aCoord1 );
        double val2 = GetFloatValue( aCoord2 );
        SetValue( val1, aCoord2 );
        SetValue( val2, aCoord1 );
        return;
      }
      default:{
        ScriptException e = new ScriptException("Неизвестный тип данных для операции обмена");
        throw e;
      }
    }//switch
  }

  private static void CheckBeforeMul( ScriptArray rowArray, ScriptArray columnArray, ScriptArray result )
          throws ScriptException{
    if ( rowArray == null || columnArray == null || result == null){
      ScriptException e = new ScriptException("пустой операнд при операции умножения");
      throw e;
    }
    int type = rowArray.ArrayValueType;
    if ( type == Operand.OPERAND_TYPE_BOOLEAN || type == Operand.OPERAND_TYPE_STRING ){
      ScriptException e = new ScriptException("Недопустимый тип значений в первом операнде при умножении матриц");
      throw e;
    }
    type = columnArray.ArrayValueType;
    if ( type == Operand.OPERAND_TYPE_BOOLEAN || type == Operand.OPERAND_TYPE_STRING ){
      ScriptException e = new ScriptException("Недопустимый тип значений во втором операнде при умножении матриц");
      throw e;
    }
    if ( rowArray.GetDimension() != 2 || columnArray.GetDimension() != 2 ){
      ScriptException e = new ScriptException("Перемножать можно только двумерные матрицы");
      throw e;
    }
    int columnCount = rowArray.GetDimensionLength(1);
    int rowCount = columnArray.GetDimensionLength(0);
    if ( columnCount != rowCount ){
      ScriptException e = new ScriptException("количество строк не равно количеству столбцов. Умножение невозможно");
      throw e;
    }
  }

  private static void PrepareResultMatrix( ScriptArray rowArray, ScriptArray columnArray, ScriptArray result ) throws ScriptException{
    int rowCount = rowArray.GetDimensionLength(0); //количество столбцов в результирующей матрице
    int columnCount = columnArray.GetDimensionLength(1); // количество строк в результирующей матрице
    int rowType = rowArray.ArrayValueType;
    int columnType = columnArray.ArrayValueType;
    boolean isNeedReCreate = false;
    int resultType;
    if ( rowType == Operand.OPERAND_TYPE_INTEGER && columnType == Operand.OPERAND_TYPE_INTEGER ){
      resultType = Operand.OPERAND_TYPE_INTEGER;
    } else {
      resultType = Operand.OPERAND_TYPE_REAL;
    }
    if ( resultType != result.ArrayValueType ){
      isNeedReCreate = true;
    }
    if ( result.GetDimension() != 2 ){
      isNeedReCreate = true;
    }
    /*if ( result.GetDimensionLength(0) != result.GetDimensionLength(1) ){
      isNeedReCreate = true;
    }*/
    if ( result.GetDimensionLength(0) != rowCount ){
      isNeedReCreate = true;
    }
    if ( result.GetDimensionLength(1) != columnCount ){
      isNeedReCreate = true;
    }
    if ( !isNeedReCreate ){
      return;
    }
    ArrayDefinition resultDef = new ArrayDefinition();
    resultDef.AddDimension( columnCount );
    resultDef.AddDimension( rowCount );
    resultDef.SetValueType( resultType );
    result.InitArray( resultDef );
  }

  private static int MulIntVectors(int row, int column, int[] rowCoord, int[] columnCoord, int length,
                                    ScriptArray rowArray, ScriptArray columnArray) throws ScriptException {
    int res = 0;
    int i = 0;
    rowCoord[0] = row;
    columnCoord[1] = column;
    while ( i < length ){
      rowCoord[1] = i;
      columnCoord[0] = i;
      res += rowArray.GetIntValue( rowCoord ) * columnArray.GetIntValue( columnCoord );
      i++;
    }
    return res;
  }

  private static double MulFloatVectors(int row, int column, int[] rowCoord, int[] columnCoord, int length,
                                    ScriptArray rowArray, ScriptArray columnArray) throws ScriptException {
    double res = 0;
    int i = 0;
    rowCoord[0] = row;
    columnCoord[1] = column;
    while ( i < length ){
      rowCoord[1] = i;
      columnCoord[0] = i;
      res += rowArray.GetFloatValue( rowCoord ) * columnArray.GetFloatValue( columnCoord );
      i++;
    }
    return res;
  }

  private static void MulInt( ScriptArray rowArray, ScriptArray columnArray, ScriptArray result ) throws ScriptException{
    int[] rowCoord = new int[2];
    int[] columnCoord = new int[2];
    //int length = result.GetDimensionLength(0);
    int rowCount = result.GetDimensionLength(0);
    int columnCount = result.GetDimensionLength(1);
    int vectorLength = rowArray.GetDimensionLength( 1 );
    int i = 0;
    int j = 0;
    int res = 0;
    while ( i < rowCount ){
      j = 0;
      res = 0;
      while ( j < columnCount ){
        res = MulIntVectors( i, j, rowCoord, columnCoord, vectorLength, rowArray, columnArray );
        rowCoord[0] = i;
        rowCoord[1] = j;
        result.SetValue( res,rowCoord );
        j++;
      }
      i++;
    }// while i

  }

  private static void MulFloat( ScriptArray rowArray, ScriptArray columnArray, ScriptArray result ) throws ScriptException{
      int[] rowCoord = new int[2];
      int[] columnCoord = new int[2];
      int rowCount = result.GetDimensionLength(0);
      int columnCount = result.GetDimensionLength(1);
      int vectorLength = rowArray.GetDimensionLength( 1 );
      int i = 0;
      int j = 0;
      double res = 0;
      while ( i < rowCount ){
        j = 0;
        res = 0;
        while ( j < columnCount ){
          res = MulFloatVectors( i, j, rowCoord, columnCoord, vectorLength, rowArray, columnArray );
          rowCoord[0] = i;
          rowCoord[1] = j;
          result.SetValue( res,rowCoord );
          j++;
        }
        i++;
      }// while i

    }


  public static void Mul( ScriptArray rowArray, ScriptArray columnArray, ScriptArray result ) throws ScriptException{
    CheckBeforeMul( rowArray, columnArray, result );
    PrepareResultMatrix( rowArray, columnArray, result );
    switch ( result.ArrayValueType ){
      case Operand.OPERAND_TYPE_INTEGER:{
        MulInt( rowArray, columnArray, result );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        MulFloat( rowArray, columnArray, result );
        break;
      }
    }
  }

  public String toString(){
    String result = GetName() + "\n";
    ArrayExecutor_ToString executor = new ArrayExecutor_ToString();
    executor.SetResultString( result );
    ExecToAllArray( executor );
    return executor.GetResult();
  }

  /** Функция сравнивает значение, содержащееся в данном объекте со значением переменной, переданной в параметре
   *
   * @param aVariable - переменная, с которой производится сравнение
   * @return 0 - если значения равны, 1 - если значения не равны
   * @throws - возникает, если производится попытка сравнить несравниваемые типы, либо если в метод передано пустое
   * значение
   */
  public int Compare( Operand aVariable ) throws ScriptException{
    if ( !( aVariable instanceof ScriptArray ) ){
      ScriptException e = new ScriptException("Попытка сравнивать матрицу с нематрицей");
      throw e;
    }
    return 1;
  }

  private void CheckForLU() throws ScriptException{
    if ( FDimension != 2 ) {
      ScriptException e = new ScriptException("Ошибка при попытке LU-разложения. Матрица должна быть двумерной");
      throw e;
    }
    if (!( ArrayValueType == Operand.OPERAND_TYPE_REAL || ArrayValueType == Operand.OPERAND_TYPE_INTEGER  )) {
      ScriptException e = new ScriptException(" Ошибка при попытке LU-разложения. Исходная матрица должна быть типа integer или real");
      throw e;
    }
  }

  private void PrepareResultMatrixForLU( ScriptArray aResultLUMatrix, ScriptArray aPivotVector ) throws ScriptException {
    ArrayDefinition resultDef = new ArrayDefinition();
    resultDef.AddDimension( GetDimensionLength(0) );
    resultDef.SetValueType( Operand.OPERAND_TYPE_REAL );
    aPivotVector.InitArray( resultDef );
    resultDef.AddDimension( GetDimensionLength(1) );
    aResultLUMatrix.InitArray( resultDef );
  }

  public void CreateLU( ScriptArray aResultLUMatrix, ScriptArray aPivotVector ) throws ScriptException{
    CheckForLU();
    PrepareResultMatrixForLU( aResultLUMatrix, aPivotVector );
    CopyValuesToArray( aResultLUMatrix );
    //System.out.println( aResultLUMatrix.toString() );
    int rowsCount = GetDimensionLength( 0 );
    int columnsCount = GetDimensionLength( 1 );
    int squareSize = rowsCount;
    if ( rowsCount > columnsCount ) squareSize = columnsCount;
    int p = 0;
    int k = 0;
    int i = 0;
    int j = 0;
    double maxVal;
    double d;
    double d2, d3;
    int[] coord = new int[2];
    int[] pivotCoord = new int[1];
    //заполняем pivot
    while ( i < rowsCount ){
      pivotCoord[0] = i;
      aPivotVector.SetValue( (float)i , pivotCoord );
      i++;
    }
    while ( k < squareSize ){
      p = k;
      coord[0] = k;
      coord[1] = p;
      maxVal = Math.abs( aResultLUMatrix.GetFloatValue( coord ) );
      i = p + 1;
      while ( i < rowsCount ){
        coord[0] = i;
        d = Math.abs( aResultLUMatrix.GetFloatValue( coord ) );
        if ( maxVal < d ) {
          maxVal = d;
          p = i;
        }
        i++;
      }
      if ( p != k ){
        i = 0;
        while ( i < columnsCount ){
          coord[0] = p;
          coord[1] = i;
          d = aResultLUMatrix.GetFloatValue( coord );
          coord[0] = k;
          coord[1] = i;
          d2 = aResultLUMatrix.GetFloatValue( coord );
          aResultLUMatrix.SetValue( d, coord );
          coord[0] = p;
          coord[1] = i;
          aResultLUMatrix.SetValue( d2, coord );
          i++;
        }
        pivotCoord[0] = k;
        d = aPivotVector.GetFloatValue( pivotCoord );
        pivotCoord[0] = p;
        d2 = aPivotVector.GetFloatValue( pivotCoord );
        aPivotVector.SetValue( d, pivotCoord );
        pivotCoord[0] = k;
        aPivotVector.SetValue( d2, pivotCoord );
      }
      coord[0] = k;
      coord[1] = k;
      d = aResultLUMatrix.GetFloatValue( coord );
      if ( d != 0 ) {
        i = k + 1;
        while ( i < rowsCount ){
          coord[0] = i;
          coord[1] = k;
          d2 = aResultLUMatrix.GetFloatValue( coord );
          aResultLUMatrix.SetValue( d2 / d, coord );
          i++;
        }
        j = k + 1;
        while ( j < columnsCount ){
          i = k + 1;
          while ( i < rowsCount ){
            coord[0] = i;
            coord[1] = j;
            d = aResultLUMatrix.GetFloatValue( coord );
            coord[0] = i;
            coord[1] = k;
            d2 = aResultLUMatrix.GetFloatValue( coord );
            coord[0] = k;
            coord[1] = j;
            d3 = aResultLUMatrix.GetFloatValue( coord );
            coord[0] = i;
            coord[1] = j;
            aResultLUMatrix.SetValue( d - d2 * d3, coord );

            i++;
          }
          j++;
        }
      }
      k++;
    }//while по squareSize

  }

  private void CheckForInverse() throws ScriptException{
    if ( FDimension != 2 ) {
      ScriptException e = new ScriptException("Для операции обращения матрица должна быть двумерной");
      throw e;
    }
    int rowCount = this.GetDimensionLength(0);
    int columnsCount = this.GetDimensionLength(1);
    if ( rowCount != columnsCount ) {
      ScriptException e = new ScriptException("матрица для обращения должна быть квадратной");
      throw e;
    }
  }

  private void AllocateResultInversionMatrix( ScriptArray aResultInvMatrix ) throws ScriptException {
    int n = this.GetDimensionLength(0);
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "0.0" );
    def.AddDimension( n );
    def.AddDimension( n );
    aResultInvMatrix.InitArray( def );
  }

  public void CreateInverseMatrix( ScriptArray aResultInvMatrix ) throws ScriptException{
    CheckForInverse();
    ScriptArray lu = new ScriptArray();
    ScriptArray pivot = new ScriptArray();
    CreateLU( lu, pivot );
    AllocateResultInversionMatrix( aResultInvMatrix );
    //System.out.println( lu.toString() );
    //System.out.println( pivot.toString() );
    int rowCount = this.GetDimensionLength(0);
    //проверяем главную диагональ. она не должна содержать нулей и неопределенных значений
    int i = 0;
    int[] coord = new int[2];
    int[] pivotCoord = new int[1];
    
    double val;
    while ( i < rowCount ) {
      coord[0] = i;
      coord[1] = i;
      val = lu.GetFloatValue( coord );
      if ( val == 0 || val != val ){
        ScriptException e = new ScriptException("Невозможно инвертировать матрицу \"" + aResultInvMatrix.GetName() + "\" \n" + aResultInvMatrix.toString());
        throw e;
      }
      i++;
    }
    int k = 0;
    int j = 0;
    int c;
    double d, d1, d2, d3;
    while ( k < rowCount  ){
      pivotCoord[0] = k;
      c = (int)pivot.GetFloatValue( pivotCoord );
      coord[0] = k;
      coord[1] = c;
      aResultInvMatrix.SetValue( 1.0, coord );
      j = k;
      while ( j < rowCount ){
        coord[0] = j;
        coord[1] = c;
        d = aResultInvMatrix.GetFloatValue( coord );
        if ( d != 0 ){
          i  = j + 1;
          while ( i < rowCount ){
            coord[0] = i;
            coord[1] = c;
            d = aResultInvMatrix.GetFloatValue( coord );
            coord[0] = j;
            coord[1] = c;
            d1 = aResultInvMatrix.GetFloatValue( coord );
            coord[0] = i;
            coord[1] = j;
            d2 = lu.GetFloatValue( coord );
            coord[0] = i;
            coord[1] = c;
            aResultInvMatrix.SetValue(d - d1*d2, coord);
            i++;
          }
        }
        j++;
      }
      j = rowCount - 1;
      while ( j >= 0 ) {
        coord[0] = j;
        coord[1] = c;
        d = aResultInvMatrix.GetFloatValue( coord );
        coord[0] = j;
        coord[1] = j;
        d1 = lu.GetFloatValue( coord );
        coord[0] = j;
        coord[1] = c;
        aResultInvMatrix.SetValue( d / d1, coord );
        if ( d != 0 ){
          i = 0;
          while ( i <= j -1 ){
            coord[0] = i;
            coord[1] = c;
            d = aResultInvMatrix.GetFloatValue( coord );
            coord[0] = j;
            coord[1] = c;
            d1 = aResultInvMatrix.GetFloatValue( coord );
            coord[0] = i;
            coord[1] = j;
            d2 = lu.GetFloatValue( coord );
            coord[0] = i;
            coord[1] = c;
            aResultInvMatrix.SetValue( d - d1*d2, coord );
            i++;
          }
        }
        j--;
      }
      k++;
    }
  }

  private void ShiftRBoolean( int aStep ){
    boolean[] tempArr = new boolean[ aStep ];
    int i = 0;
    int length = FBooleanArray.length;
    int stepIndex = 0;
    boolean temp;
    while (i < aStep && i < length){
      tempArr[i] = FBooleanArray[i];
      FBooleanArray[i] = false;
      i++;
    }
    while ( i < length ){
      temp = FBooleanArray[i];
      FBooleanArray[i] = tempArr[ stepIndex ];
      tempArr[ stepIndex ] = temp;
      stepIndex++;
      if ( stepIndex >= aStep ){
        stepIndex = 0;
      }
      i++;
    }
  }

  private void ShiftRInt( int aStep ){
    int[] tempArr = new int[ aStep ];
    int i = 0;
    int length = FIntArray.length;
    int stepIndex = 0;
    int temp;
    while (i < aStep && i < length){
      tempArr[i] = FIntArray[i];
      FIntArray[i] = 0;
      i++;
    }
    while ( i < length ){
      temp = FIntArray[i];
      FIntArray[i] = tempArr[ stepIndex ];
      tempArr[ stepIndex ] = temp;
      stepIndex++;
      if ( stepIndex >= aStep ){
        stepIndex = 0;
      }
      i++;
    }
  }

  private void ShiftRReal( int aStep ){
    double[] tempArr = new double[ aStep ];
    int i = 0;
    int length = FFloatArray.length;
    int stepIndex = 0;
    double temp;
    while (i < aStep && i < length){
      tempArr[i] = FFloatArray[i];
      FFloatArray[i] = 0;
      i++;
    }
    while ( i < length ){
      temp = FFloatArray[i];
      FFloatArray[i] = tempArr[ stepIndex ];
      tempArr[ stepIndex ] = temp;
      stepIndex++;
      if ( stepIndex >= aStep ){
        stepIndex = 0;
      }
      i++;
    }
  }

  public void ShiftR( int aStep ) throws ScriptException{
    if ( FDimension != 1 ){
      ScriptException e = new ScriptException("Выполнять операцию сдвига можно только для одномерного массива!. " +
         "Неверная попытка сдвига для массива \"" + GetName() + "\"");
      throw e;
    }
    if ( aStep < 1 ){
      ScriptException e = new ScriptException( "Неверный аргумент для операции сдвига" );
      throw e;
    }
    switch ( ArrayValueType ){
      case Operand.OPERAND_TYPE_BOOLEAN:{
        ShiftRBoolean( aStep );
        return;
      }
      case Operand.OPERAND_TYPE_INTEGER: {
        ShiftRInt( aStep );
        FIsSummCalculated = false;
        FIntSumm = 0;
        return;
      }
      case Operand.OPERAND_TYPE_REAL:{
        ShiftRReal( aStep );
        FIsSummCalculated = false;
        FFloatSumm = 0;
        return;
      }
      case Operand.OPERAND_TYPE_STRING:{
        return;
      }
    }
  }

  private void ShiftLBoolean( int aStep ){
    boolean[] tempArr = new boolean[ aStep ];
    int i = FBooleanArray.length - 1;
    int length = i;
    int stepIndex = 0;
    boolean temp;
    stepIndex = aStep - 1;
    while ( i >= 0 && ( ( length - i ) < aStep ) ){
      tempArr[ stepIndex ] = FBooleanArray[i];
      FBooleanArray[i] = false;
      stepIndex--;
      i--;
    }
    stepIndex = aStep - 1;
    while ( i >= 0 ){
      temp = FBooleanArray[i];
      FBooleanArray[i] = tempArr[ stepIndex ];
      tempArr[ stepIndex ] = temp;
      stepIndex--;
      if ( stepIndex < 0 ) {
        stepIndex = aStep - 1; 
      }
      i--;
    }
  }

  private void ShiftLInt( int aStep ){
    int[] tempArr = new int[ aStep ];
    int i = FIntArray.length - 1;
    int length = i;
    int stepIndex = 0;
    int temp;
    stepIndex = aStep - 1;
    while ( i >= 0 && ( ( length - i ) < aStep ) ){
      tempArr[ stepIndex ] = FIntArray[i];
      FIntArray[i] = 0;
      stepIndex--;
      i--;
    }
    stepIndex = aStep - 1;
    while ( i >= 0 ){
      temp = FIntArray[i];
      FIntArray[i] = tempArr[ stepIndex ];
      tempArr[ stepIndex ] = temp;
      stepIndex--;
      if ( stepIndex < 0 ) {
        stepIndex = aStep - 1;
      }
      i--;
    }
  }

  private void ShiftLReal( int aStep ){
    double[] tempArr = new double[ aStep ];
    int i = FFloatArray.length - 1;
    int length = i;
    int stepIndex = 0;
    double temp;
    stepIndex = aStep - 1;
    while ( i >= 0 && ( ( length - i ) < aStep ) ){
      tempArr[ stepIndex ] = FFloatArray[i];
      FFloatArray[i] = 0;
      stepIndex--;
      i--;
    }
    stepIndex = aStep - 1;
    while ( i >= 0 ){
      temp = FFloatArray[i];
      FFloatArray[i] = tempArr[ stepIndex ];
      tempArr[ stepIndex ] = temp;
      stepIndex--;
      if ( stepIndex < 0 ) {
        stepIndex = aStep - 1;
      }
      i--;
    }

  }

    public void ShiftL( int aStep ) throws ScriptException{
    if ( FDimension != 1 ){
      ScriptException e = new ScriptException("Выполнять операцию сдвига можно только для одномерного массива!. " +
         "Неверная попытка сдвига для массива \"" + GetName() + "\"");
      throw e;
    }
    if ( aStep < 1 ){
      ScriptException e = new ScriptException( "Неверный аргумент для операции сдвига" );
      throw e;
    }  
    switch ( ArrayValueType ){
      case Operand.OPERAND_TYPE_BOOLEAN:{
        ShiftLBoolean( aStep );
        return;
      }
      case Operand.OPERAND_TYPE_INTEGER: {
        ShiftLInt( aStep );
        FIsSummCalculated = false;
        FIntSumm = 0;
        return;
      }
      case Operand.OPERAND_TYPE_REAL:{
        ShiftLReal( aStep );
        FIsSummCalculated = false;
        FFloatSumm = 0;
        return;
      }
      case Operand.OPERAND_TYPE_STRING:{
        return;
      }
    }
  }
  
}

package mp.parser;

import junit.framework.TestCase;

/**
 * User: саша
 * Date: 30.06.2008
 */
public class ArrayTest extends TestCase {

  public ArrayTest(String aName){
    super( aName );
  }

  private int GetTrueCount( ScriptArray array, int[] coord, int dimension ) throws ScriptException {
    int i = 0;
    int result = 0;
    while ( i < dimension ){
      coord[ coord.length-1 ] = i;
      if ( array.GetBooleanValue( coord ) ){
        result++;
      }
      i++;
    }
    return result;
  }

  public void testOneDimBooleanArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_BOOLEAN );
    def.AddDimension( 10 );
    ScriptArray boolArray = new ScriptArray();
    boolArray.SetName("boolArray");
    boolean f = false;
    try {
      boolArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    int[] coord = new int[1];
    int trueCount = 0;
    try {
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 0 );

    f = true;
    try {
      boolArray.SetValue( true, coord );
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 1 );
  }

  public void testTwoDimBooleanArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_BOOLEAN );
    def.AddDimension( 10 );
    def.AddDimension( 10 );
    ScriptArray boolArray = new ScriptArray();
    boolArray.SetName("boolArray");
    boolean f = false;
    try {
      boolArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    int i = 0;
    int[] coord = new int[2];
    int resCounter = 0;
    try{
      while ( i < 10 ){
        coord[0] = i;
        resCounter = resCounter + GetTrueCount( boolArray, coord, 10);
        i++;
      }

    } catch (ScriptException e){
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( resCounter, 0 );

    f = false;
    coord[0] = 0;
    coord[1] = 5;
    try {
      boolArray.SetValue( true, coord );
      coord[0] = 3;
      coord[1] = 0;
      boolArray.SetValue( true, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  public void testBooleanInitValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_BOOLEAN );
    def.AddDimension( 10 );
    def.SetInitValue( "true" );
    ScriptArray boolArray = new ScriptArray();
    boolArray.SetName("boolArray");
    boolean f = false;
    try {
      boolArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    int res = 0;
    f = false;
    try {
      res = GetTrueCount( boolArray, coord, 10 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 10 );
  }

  private int GetIntSumm( ScriptArray array, int[] coord, int dimension ) throws ScriptException {
    int i = 0;
    int result = 0;
    while ( i < dimension ){
      coord[ coord.length-1 ] = i;
      result += array.GetIntValue( coord );
      i++;
    }
    return result;
  }

  public void testOneDimIntArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    int summ = 0;
    f = false;
    try {
      summ = GetIntSumm( arr, coord, 10 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( summ, 0 );
    coord[0] = 4;
    f = false;
    try {
      arr.SetValue( 8, coord );
      summ = GetIntSumm( arr, coord, 10 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( summ, 8 );
  }

  public void testIntInitValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    int summ = 0;
    f = false;
    try {
      summ = GetIntSumm( arr, coord, 10 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( summ, 50 );
  }

  public void testIntInitErrorValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "_5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );

  }

   private float GetRealSumm( ScriptArray array, int[] coord, int dimension ) throws ScriptException {
    int i = 0;
    float result = 0;
    while ( i < dimension ){
      coord[ coord.length-1 ] = i;
      result += array.GetFloatValue( coord );
      i++;
    }
    return result;
  }

  public void testRealValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "1.5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    int res = 0;
    f = false;
    try {
      res = (int) GetRealSumm( arr, coord, 10 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 15 );
    coord[0] = 0;


    f = false;
    try {
      arr.SetValue( 2.5, coord );
      res = (int) GetRealSumm( arr, coord, 10 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 16 );
  }

  public void testIntArrayInScript(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );


    Variable counter = new Variable(0);
    counter.SetName("counter");
    Variable result = new Variable( 0 );
    result.SetName( "result" );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( counter );
      ext.AddVariable( result );
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f =  false;
    int res = 0;
    try {
      parser.ParseScript(" while (counter < 5) do \n" +
         " begin \n" +
         "  result := result + GetArrayValue( array, counter); \n" +
         "  counter := counter + 1; \n" +
         "end");
      parser.ExecuteScript();
      res = result.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 25 );
  }

  public void testRealArrayInScript(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "2.5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );


    Variable counter = new Variable(0);
    counter.SetName("counter");
    Variable result = new Variable( 0.0 );
    result.SetName( "result" );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( counter );
      ext.AddVariable( result );
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f =  false;
    int res = 0;
    try {
      parser.ParseScript(" while (counter < 10) do \n" +
         " begin \n" +
         "  result := result + GetArrayValue( array, counter); \n" +
         "  counter := counter + 1; \n " +
         "end");
      parser.ExecuteScript();
      res = result.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 25 );

  }

  public void testBooleanArrayInScript(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_BOOLEAN );
    def.SetInitValue( "true" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    Variable counter = new Variable(0);
    counter.SetName("counter");
    Variable result = new Variable( 0 );
    result.SetName( "result" );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( counter );
      ext.AddVariable( result );
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f =  false;
    int res = 0;
    try {
      parser.ParseScript(" while (counter < 10) do \n" +
         " begin \n" +
         "  if ( GetArrayValue( array, counter)) then \n" +
              "begin result := result  +1; \n" +
              "end;\n" +
         "  counter := counter + 1; \n " +
         "end");
      parser.ExecuteScript();
      res = result.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 10 );

    int[] coord = new int[1];
    coord[0] = 1;
    f = false;
    try {
      result.SetValue(0);
      counter.SetValue(0);
      arr.SetValue(  false, coord);
      parser.ExecuteScript();
      res = result.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 9 );
  }

  public void test2DimIntArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "3" );
    def.AddDimension( 10 );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );


    Variable counter = new Variable(0);
    counter.SetName("counter");
    Variable counter2 = new Variable( 0 );
    counter2.SetName("counter2");
    Variable result = new Variable( 0 );
    result.SetName( "result" );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( counter );
      ext.AddVariable( counter2 );
      ext.AddVariable( result );
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f =  false;
    int res = 0;
    try {
      parser.ParseScript(" while (counter < 10) do \n" +
              " begin \n" +
              "   counter2 := 0;\n " +
              "   while ( counter2 < 10 ) do " +
              "    begin \n " +
              "      result := result + GetArrayValue(array, counter, counter2); \n " +
              "      counter2 := counter2 + 1; \n" +
              "    end;\n" +
              "   counter := counter +1;\n" +
              " end;");
      parser.ExecuteScript();
      res = result.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 300 );
  }

  /**Проверяем правильность выполнеyия функции SetArrayValue внутри скрипта для операндов типа integer
   *
   */
  public void testSetValueScriptOperation_Int(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );



    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f =  false;
    int res = 0;
    try {
      parser.ParseScript(  " SetArrayValue( array, 1, 100 );");
      parser.ExecuteScript();
      res = GetIntSumm( arr, new int[1], 10 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals(res, 145);
  }

  public void testSetValueScriptOperation_Real(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "3.2" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f =  false;
    int res = 0;
    try {
      parser.ParseScript(  " SetArrayValue( array, 1, 4.2 );");
      parser.ExecuteScript();
      res = (int)GetRealSumm( arr, new int[1], 10 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals(res, 33);
  }

  public void testIntSumm_OneDimension(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int res = 0;
    res = arr.GetIntSumm();
    assertEquals(res, 50);

    int[] coord = new int[1];
    coord[0] = 1;
    f = false;
    try {
      arr.SetValue(10,coord);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntSumm();
    assertEquals(res, 55);
  }

  public void testIntSumm_2Dimension(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "5" );
    def.AddDimension( 10 );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int res = 0;
    res = arr.GetIntSumm();
    assertEquals(res, 500);

    int[] coord = new int[2];
    coord[0] = 1;
    coord[1] = 1;
    f = false;
    try {
      arr.SetValue(10,coord);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntSumm();
    assertEquals(res, 505);

  }

  public void testIntSumm_3Dimension(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "5" );
    def.AddDimension( 10 );
    def.AddDimension( 10 );
    def.AddDimension( 2 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int res = 0;
    res = arr.GetIntSumm();
    assertEquals(res, 1000);

    int[] coord = new int[3];
    coord[0] = 1;
    coord[1] = 1;
    coord[2] = 1;
    f = false;
    try {
      arr.SetValue(10,coord);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntSumm();
    assertEquals(res, 1005);

  }

  public void testRealSumm(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "2.1" );
    def.AddDimension( 100 );
    ScriptArray arr = new ScriptArray();
    arr.SetName( "array" );
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    double res = 0;
    res = arr.GetFloatSumm();
    assertTrue((int)res == 210 || (int)res == 209);

    int[] coord = new int[1];
    coord[0] = 1;
    f = false;
    try {
      arr.SetValue(10.1,coord);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetFloatSumm();
    assertTrue((int)res == 218 || (int)res == 217);
    //System.out.println( arr );

  }

  public void testIntSumm_FromScript(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "7" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );


    Variable summ = new Variable( 0 );
    summ.SetName( "summ" );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( summ );
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    int result = 0;
    try {
      parser.ParseScript(" summ := GetArraySumm(array); ");
      parser.ExecuteScript();
      result = summ.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( result, 70 );
  }

  public void testRealSumm_FromScript(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "5.2" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );


    Variable summ = new Variable( 0.0 );
    summ.SetName( "summ" );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( summ );
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    int result = 0;
    try {
      parser.ParseScript(" summ := GetArraySumm(array); ");
      parser.ExecuteScript();
      result = summ.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( result, 52 );
  }

  public void testIntMinValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "-10" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int res = 0;
    res = arr.GetIntMinValue();
    assertEquals(res, -10);

    int[] coord = new int[1];
    coord[0] = 1;
    f = false;
    try {
      arr.SetValue( 1, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntMinValue();
    assertEquals(res, -10);

    coord[0] = 2;
    f = false;
    try {
      arr.SetValue( -11, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntMinValue();
    assertEquals(res, -11);


  }

  public void testIntMinValue_2Dim(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "-10" );
    def.AddDimension( 10 );
    def.AddDimension( 100 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int res = 0;
    res = arr.GetIntMinValue();
    assertEquals(res, -10);

    int[] coord = new int[2];
    coord[0] = 1;
    coord[1] = 1;
    f = false;
    try {
      arr.SetValue( 1, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntMinValue();
    assertEquals(res, -10);

    coord[0] = 2;
    coord[1] = 2;
    f = false;
    try {
      arr.SetValue( -11, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntMinValue();
    assertEquals(res, -11);
  }

  public void testFloatMinValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "2.1" );
    def.AddDimension( 100 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    double res = 0;
    res = arr.GetFloatMinValue();
    assertTrue( res > 2.0 && res < 2.11);

    int[] coord = new int[1];
    coord[0] = 1;
    try {
      arr.SetValue( -2.1, coord );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    res = arr.GetFloatMinValue();
    assertTrue( res > -2.101 && res < -2.09);
  }

  public void testIntMaxValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.SetInitValue( "10" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int res = 0;
    res = arr.GetIntMaxValue();
    assertEquals(res, 10);

    int[] coord = new int[1];
    coord[0] = 1;
    f = false;
    try {
      arr.SetValue( 1, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntMaxValue();
    assertEquals(res, 10);

    coord[0] = 2;
    f = false;
    try {
      arr.SetValue( 11, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetIntMaxValue();
    assertEquals(res, 11);
  }

  public void testFloatMaxValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "5.5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    double res = 0;
    res = arr.GetFloatMaxValue();
    assertEquals(res, 5.5);

    int[] coord = new int[1];
    coord[0] = 1;
    f = false;
    try {
      arr.SetValue( 1.1, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetFloatMaxValue();
    assertEquals(res, 5.5);

    coord[0] = 2;
    f = false;
    try {
      arr.SetValue( 6.5, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    res = arr.GetFloatMaxValue();
    assertEquals(res, 6.5);
  }

  public void testGetDimensionElementsFromScript(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "5.5" );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    Variable count1 = new Variable(0);
    count1.SetName("count1");
    Variable dimension = new Variable( 0 );
    dimension.SetName("dim");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    int dimLength = 0;
    int dimCount = 0;
    try {
      ext.AddVariable( count1 );
      ext.AddVariable( dimension );
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      parser.ParseScript(" count1 := GetArrayDimension( array ); dim := GetArrayDimensionLength(array, 0);");
      parser.ExecuteScript();
      dimCount = count1.GetIntValue();
      dimLength = dimension.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( dimCount, 1 );
    assertEquals( dimLength, 10 );
  }

  public void testGetDimensionElementsFromScript_2Dim(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "5.5" );
    def.AddDimension( 5 );
    def.AddDimension( 10 );
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    boolean f = false;
    try {
      arr.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    Variable count1 = new Variable(0);
    count1.SetName("count1");
    Variable count2 = new Variable(0);
    count2.SetName("count2");
    Variable dimension = new Variable( 0 );
    dimension.SetName("dim");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    int dim1Length = 0;
    int dim2Length = 0;
    int dimCount = 0;
    try {
      ext.AddVariable( count1 );
      ext.AddVariable( count2 );
      ext.AddVariable( dimension );
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      parser.ParseScript(" dim := GetArrayDimension( array ); " +
              " count1 := GetArrayDimensionLength(array, 0); " +
              "count2 := GetArrayDimensionLength(array, 1);");
      parser.ExecuteScript();
      dimCount = dimension.GetIntValue();
      dim1Length = count1.GetIntValue();
      dim2Length = count2.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( dimCount, 2 );
    assertEquals( dim1Length, 10 );
    assertEquals( dim2Length, 5 );
  }

  /**Проверяется правильность выполнения операции копирования массивов.
   * В этом тесте копируется одномерный массив.
   *
   * Под операцией копирования понимается следующее: из старого массива удаляется вся информация,  он структурно
   * становится идентичным массиву, который копируется. Затем в этот массив копируются все значения из
   * массива-источника
   *
   */
  public void testCopyArrays_1Dim(){
    ScriptArray sourceArray = new ScriptArray();
    ScriptArray destArray = new ScriptArray();

    ArrayDefinition sourceDef = new ArrayDefinition();
    sourceDef.AddDimension(10);
    sourceDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    sourceDef.SetInitValue( "5" );

    ArrayDefinition destDef = new ArrayDefinition();
    destDef.AddDimension(10);
    destDef.AddDimension(10);
    destDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    destDef.SetInitValue( "8" );

    boolean f = false;
    try {
      sourceArray.InitArray( sourceDef );
      destArray.InitArray( destDef );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( destArray.GetDimension(), 2 );
    assertEquals( sourceArray.GetDimension(), 1 );

    f = false;
    try {
      sourceArray.CopyValuesToArray( destArray );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( destArray.GetDimension(), 1 );
    int i = 0;
    int[] coord = new int[1];
    f = true;
    while ( i < 10 ){
      coord[0] = i;
      try {
        assertEquals( sourceArray.GetIntValue(coord), destArray.GetIntValue( coord ) );
      } catch (ScriptException e) {
        f = false;
      }
      i++;
    }
    assertTrue( f );

    //теперь изменим произвольное значение в одном из массивов и скопирем его в другой массив. массивы
    // опять должны быть идентичны
    coord[0] = 3;
    f = false;
    try {
      sourceArray.SetValue( 19, coord );
      sourceArray.CopyValuesToArray( destArray );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    i = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        assertEquals( sourceArray.GetIntValue(coord), destArray.GetIntValue( coord ) );
      } catch (ScriptException e) {
        f = false;
      }
      i++;
    }
    assertTrue( f );
  }

  public void testCopyArrays_2Dim(){
    ScriptArray sourceArray = new ScriptArray();
    ScriptArray destArray = new ScriptArray();

    ArrayDefinition sourceDef = new ArrayDefinition();
    sourceDef.AddDimension(10);
    sourceDef.AddDimension(20);
    sourceDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    sourceDef.SetInitValue( "5" );

    ArrayDefinition destDef = new ArrayDefinition();
    destDef.AddDimension(10);
    destDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    destDef.SetInitValue( "8" );

    boolean f = false;
    try {
      sourceArray.InitArray( sourceDef );
      destArray.InitArray( destDef );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( destArray.GetDimension(), 1 );
    assertEquals( sourceArray.GetDimension(), 2 );

    f = false;
    try {
      sourceArray.CopyValuesToArray( destArray );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( destArray.GetDimension(), 2 );
    assertEquals( sourceArray.GetDimension(), 2 );

    int[] coord = new int[2];
    int i = 0;
    int j = 0;
    f = true;
    while ( i < 20 ){
      j = 0;
      coord[0] = i;
      while ( j < 10 ){
        coord[1] = j;
        try {
          assertEquals( sourceArray.GetIntValue(coord), destArray.GetIntValue( coord ) );
        } catch (ScriptException e) {
          f = false;
          e.printStackTrace();
        }
        j++;
      }
      i++;
    }//while i
    assertTrue( f );
  }

  public void testCopyArrays_3Dim(){
    ScriptArray sourceArray = new ScriptArray();
    ScriptArray destArray = new ScriptArray();

    ArrayDefinition sourceDef = new ArrayDefinition();
    sourceDef.AddDimension(10);
    sourceDef.AddDimension(20);
    sourceDef.AddDimension(30);
    sourceDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    sourceDef.SetInitValue( "5" );

    ArrayDefinition destDef = new ArrayDefinition();
    destDef.AddDimension(10);
    destDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    destDef.SetInitValue( "8" );

    boolean f = false;
    try {
      sourceArray.InitArray( sourceDef );
      destArray.InitArray( destDef );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( destArray.GetDimension(), 1 );
    assertEquals( sourceArray.GetDimension(), 3 );

    f = false;
    try {
      sourceArray.CopyValuesToArray( destArray );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  public void testCopyArraysFromScript(){
    ScriptArray sourceArray = new ScriptArray();
    sourceArray.SetName("source");
    ScriptArray destArray = new ScriptArray();
    destArray.SetName( "dest" );

    ArrayDefinition sourceDef = new ArrayDefinition();
    sourceDef.AddDimension(10);
    sourceDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    sourceDef.SetInitValue( "5" );

    ArrayDefinition destDef = new ArrayDefinition();
    destDef.AddDimension(10);
    destDef.AddDimension(10);
    destDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    destDef.SetInitValue( "8" );

    boolean f = false;
    try {
      sourceArray.InitArray( sourceDef );
      destArray.InitArray( destDef );
      sourceArray.InitArray();
      destArray.InitArray();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( destArray.GetDimension(), 2 );
    assertEquals( sourceArray.GetDimension(), 1 );

    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    f = false;
    try {
      ext.AddVariable( sourceArray );
      ext.AddVariable( destArray );
      parser.SetLanguageExt( ext );
      parser.ParseScript(" dest := source ");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int i = 0;
    int[] coord = new int[1];
    f = true;
    while ( i < 10 ){
      coord[0] = i;
      try {
        assertEquals( sourceArray.GetIntValue(coord), destArray.GetIntValue( coord ) );
      } catch (ScriptException e) {
        f = false;
      }
      i++;
    }
    assertTrue( f );
    assertEquals( destArray.GetIntSumm(), 50 );
  }

  public void testTranspose(){
    ScriptArray arr = new ScriptArray();
    arr.SetName("array");
    ArrayDefinition sourceDef = new ArrayDefinition();
    sourceDef.AddDimension(3);
    sourceDef.AddDimension(3);
    sourceDef.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    sourceDef.SetInitValue( "0" );

    boolean f = false;
    try {
      arr.InitArray( sourceDef );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    int i = 0;
    int j = 0;
    int[] coord = new int[2];
    int counter = 0;
    f = true;
    while ( i < 3 ){
      j = 0;
      coord[0] = i;
      while ( j < 3 ){
        coord[1] = j;
        try {
          arr.SetValue( counter , coord);
          counter++;
        } catch (ScriptException e) {
          f = false;
          e.printStackTrace();
        }
        j++;
      }
      i++;
    }
    assertTrue( f );


    f = false;
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    try {
      ext.AddVariable( arr );
      parser.SetLanguageExt( ext );
      parser.ParseScript( " ArrayTranspose(array); " );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    try {
      coord[0] = 0;
      coord[1] = 0;
      assertEquals( arr.GetIntValue(coord),0 );
      coord[1] = 1;
      assertEquals( arr.GetIntValue(coord),3 );
      coord[1] = 2;
      assertEquals( arr.GetIntValue(coord),6 );
      coord[0] = 1;
      coord[1] = 0;
      assertEquals( arr.GetIntValue(coord),1 );
      coord[1] = 1;
      assertEquals( arr.GetIntValue(coord),4 );
      coord[1] = 2;
      assertEquals( arr.GetIntValue(coord),7 );
      coord[0] = 2;
      coord[1] = 0;
      assertEquals( arr.GetIntValue(coord),2 );
      coord[1] = 1;
      assertEquals( arr.GetIntValue(coord),5 );
      coord[1] = 2;
      assertEquals( arr.GetIntValue(coord),8 );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }

  /**проверяется правильность перемножения единичных матриц
   *
   */
  public void testMul_1(){
    ArrayDefinition def = new ArrayDefinition();
    def.AddDimension( 2 );
    def.AddDimension( 2 );
    def.SetInitValue("1");
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    ScriptArray arr1 = new ScriptArray();
    ScriptArray arr2 = new ScriptArray();
    ScriptArray result = new ScriptArray();
    boolean f = false;
    try {
      arr1.InitArray( def );
      arr2.InitArray( def );
      result.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }

    f = false;
    try {
      ScriptArray.Mul( arr1, arr2, result );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( result.GetIntMinValue(), 2 );
    assertEquals( result.GetIntMaxValue(), 2 );
    assertEquals( result.GetIntSumm(),8 );

    f = false;
    int[] coord = new int[2];
    coord[0] = 1;
    coord[0] = 1;
    try {
      arr2.SetValue(0, coord);
      ScriptArray.Mul( arr1, arr2, result );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( result.GetIntMinValue(), 1 );
    assertEquals( result.GetIntMaxValue(), 2 );
    assertEquals( result.GetIntSumm(),6 );
  }

  /**на этот раз проверяется более сложная матрица, с неединичными значениями
   * Сначала такая матрица умножается на матрицу с единичной диагональю. В результате матрица должна остаться
   * сама собой.
   */
  public void testMul_2(){
    int length = 10;
    ArrayDefinition def = new ArrayDefinition();
    def.AddDimension( length );
    def.AddDimension( length );
    def.SetInitValue("0");
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    ScriptArray arr1 = new ScriptArray();
    ScriptArray arr2 = new ScriptArray();
    ScriptArray result = new ScriptArray();
    result.SetName("result");
    arr1.SetName("arr1");
    arr2.SetName("arr2");
    boolean f = false;
    try {
      arr1.InitArray( def );
      arr2.InitArray( def );
      result.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    //подготавливаем матрицы
    f = true;
    int[] coord = new int[2];
    int i = 0;
    int j = 0;
    int counter = 0;
    while ( i < length ){
      j = 0;
      coord[0] = i;
      while ( j < length ){
        coord[1] = j;
        try {
          arr1.SetValue( counter, coord );
        } catch (ScriptException e) {
          f = false;
          e.printStackTrace();
        }
        counter++;
        j++;
      }
      coord[1] = i;
      try {
        arr2.SetValue( 1, coord );
      } catch (ScriptException e) {
        e.printStackTrace();
        f = false;
      }
      i++;
    }//while i
    assertTrue( f );

    f = false;
    try {
      ScriptArray.Mul( arr1, arr2, result );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = true;
    counter = 0;
    i = 0;
    while ( i < length ){
      j = 0;
      coord[0] = i;
      while ( j < length ){
        coord[1] = j;
        try {
          assertEquals( counter, result.GetIntValue( coord ) );
        } catch (ScriptException e) {
          e.printStackTrace();
          f = false;
        }
        counter++;
        j++;
      }
      coord[1] = i;
      i++;
    }//while i
    assertTrue( f );
    //System.out.println( result.toString() );
    //System.out.println( arr2.toString() );
  }

  /**проверка правильности работы умножения матриц для случая, когда:
   * - перемножаются неквадратные матрицы
   * - количество строк в левой матрице меньше, чем количество столбцов (и, соответственно, количество столбцов в
   * правой матрице меньше количества строк)
   */
  public void testMul_3(){
    ArrayDefinition def = new ArrayDefinition();
    def.AddDimension( 10 );
    def.AddDimension( 2 );
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue("1");
    ArrayDefinition def2 = new ArrayDefinition();
    def2.AddDimension( 2 );
    def2.AddDimension( 10 );
    def2.SetValueType( Operand.OPERAND_TYPE_REAL );
    def2.SetInitValue("1");
    ScriptArray arr1 = new ScriptArray();
    ScriptArray arr2 = new ScriptArray();
    ScriptArray result = new ScriptArray();

    result.SetName("result");
    arr1.SetName("arr1");
    arr2.SetName("arr2");
    boolean f = false;
    try {
      arr1.InitArray( def );
      arr2.InitArray( def2 );
      result.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //System.out.println( arr1.toString() );
    //System.out.println( arr2.toString() );
    f = false;
    try {
      ScriptArray.Mul( arr1, arr2, result );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //System.out.println( result.toString() );
    int len1 = result.GetDimensionLength( 0 );
    assertEquals( len1, 2 );
    len1 = result.GetDimensionLength( 1 );
    assertEquals( len1, 2 );
    int maxVal = 0;
    maxVal = (int) result.GetFloatMaxValue();
    assertEquals( maxVal, 10 );
  }

  /**проверка правильности работы умножения матриц для случая, когда:
   * - перемножаются неквадратные матрицы
   * - количество строк в левой матрице больше, чем количество столбцов (и, соответственно, количество столбцов в
   * правой матрице больше количества строк)
   */
  public void testMul_4(){
    ArrayDefinition def = new ArrayDefinition();
    def.AddDimension( 2 );
    def.AddDimension( 10 );
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue("1");
    ArrayDefinition def2 = new ArrayDefinition();
    def2.AddDimension( 10 );
    def2.AddDimension( 2 );
    def2.SetValueType( Operand.OPERAND_TYPE_REAL );
    def2.SetInitValue("1");
    ScriptArray arr1 = new ScriptArray();
    ScriptArray arr2 = new ScriptArray();
    ScriptArray result = new ScriptArray();

    result.SetName("result");
    arr1.SetName("arr1");
    arr2.SetName("arr2");
    boolean f = false;
    try {
      arr1.InitArray( def );
      arr2.InitArray( def2 );
      result.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //System.out.println( arr1.toString() );
    //System.out.println( arr2.toString() );
    f = false;
    try {
      ScriptArray.Mul( arr1, arr2, result );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //System.out.println( result.toString() );
    int len1 = result.GetDimensionLength( 0 );
    assertEquals( len1, 10 );
    len1 = result.GetDimensionLength( 1 );
    assertEquals( len1, 10 );
    int maxVal = 0;
    maxVal = (int) result.GetFloatMaxValue();
    assertEquals( maxVal, 2 );
  }

  /**проверка умножения двумерной матрицы на вектор - столбец
   * Проверяется:
   * - то, что такое умножение возможно
   * - размер получившейся матрицы
   *
   */
  public void testMul_5(){
    ArrayDefinition def = new ArrayDefinition();
    def.AddDimension( 10 );
    def.AddDimension( 2 );
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue("1");

    ArrayDefinition def2 = new ArrayDefinition();
    def2.AddDimension( 1 );
    def2.AddDimension( 10 );
    def2.SetValueType( Operand.OPERAND_TYPE_REAL );
    def2.SetInitValue("1");

    boolean f = false;
    ScriptArray arr1 = new ScriptArray();
    ScriptArray arr2 = new ScriptArray();
    ScriptArray res = new ScriptArray();
    try {
      arr1.InitArray( def );
      arr2.InitArray( def2 );
      res.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      ScriptArray.Mul( arr1, arr2, res );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res.GetDimensionLength(0), 2 );
    assertEquals( res.GetDimensionLength(1), 1 );
    assertEquals( (int)res.GetFloatMaxValue(), 10 );
    //System.out.println( res.toString() );
  }

  /**Проверяется случай умножения матриц, когда одномерная матрица (вектор-строка)
   * умножается на двумерную матрицу (стоблец)
   *
   */
  public void testMul_6(){
    ArrayDefinition def = new ArrayDefinition();
    def.AddDimension( 5 );
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue("1");
    ArrayDefinition def2 = new ArrayDefinition();
    def2.AddDimension( 5 );
    def2.AddDimension( 1 );
    def2.SetValueType( Operand.OPERAND_TYPE_REAL );
    def2.SetInitValue("1");


  }



  /**Проверяется правильность вызова умножения матриц из скрипта
   *
   */
  public void testMulInScript(){
    ScriptArray arr1 = new ScriptArray();
    arr1.SetName( "arr1" );
    ScriptArray arr2 = new ScriptArray();
    arr2.SetName( "arr2" );
    ScriptArray resArray = new ScriptArray();
    resArray.SetName( "res" );
    Variable var1 = new Variable(0);
    var1.SetName( "matrixSumm" );
    ArrayDefinition def = new ArrayDefinition();
    def.SetInitValue( "1" );
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 2 );
    def.AddDimension( 2 );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      arr1.InitArray( def );
      arr2.InitArray( def );
      resArray.InitArray( def );
      ext.AddVariable( arr1 );
      ext.AddVariable( arr2 );
      ext.AddVariable( resArray );
      ext.AddVariable( var1 );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String sourceCode = " SetArrayValue( arr1, 0, 0, 1 );" +
            " SetArrayValue( arr1, 0, 1, 2 );" +
            "SetArrayValue( arr1, 1, 0, 3 );" +
            "SetArrayValue( arr1, 1, 1, 4 );" +
            "SetArrayValue( arr2, 0, 0, 1 );" +
            "SetArrayValue( arr2, 0, 1, 0 );" +
            "SetArrayValue( arr2, 1,0, 0 );" +
            "SetArrayValue( arr2, 1, 1, 1 );" +
            " ArrayMul( arr1, arr2, res ); " +
            "matrixSumm := GetArraySumm( res );" +
            "{print( arr1 );" +
            "print( arr2 );" +
            "print( res );}";
    f = false;
    int i = 0;
    try {
      parser.ParseScript( sourceCode );
      parser.ExecuteScript();
      i = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i, 10 );

  }

  public void testLU_1(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "3" );
    def.AddDimension( 3 );
    def.AddDimension( 3 );
    ScriptArray sourceArray = new ScriptArray();
    ScriptArray luArray = new ScriptArray();
    ScriptArray pivot = new ScriptArray();
    boolean f = false;
    int summ = 0;
    try {
      sourceArray.InitArray( def );
      sourceArray.CreateLU( luArray, pivot );
      summ = luArray.GetIntSumm();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( summ, 11 );
    //System.out.println( luArray.toString() );
  }

  public void testLU_2(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "1.0" );
    def.AddDimension( 3 );
    def.AddDimension( 3 );
    ScriptArray sourceArray = new ScriptArray();
    boolean f = false;
    int[] coord = new int[2];
    coord[0] = 0;
    coord[1] = 0;
    try {
      sourceArray.InitArray( def );
      sourceArray.SetValue( 1.0, coord );
      coord[0] = 0;
      coord[1] = 1;
      sourceArray.SetValue( 2.0, coord );
      coord[0] = 0;
      coord[1] = 2;
      sourceArray.SetValue( 3.0, coord );

      coord[0] = 1;
      coord[1] = 0;
      sourceArray.SetValue( 4.0, coord );
      coord[0] = 1;
      coord[1] = 1;
      sourceArray.SetValue( 5.0, coord );
      coord[0] = 1;
      coord[1] = 2;
      sourceArray.SetValue( 6.0, coord );

      coord[0] = 2;
      coord[1] = 0;
      sourceArray.SetValue( 7.0, coord );
      coord[0] = 2;
      coord[1] = 1;
      sourceArray.SetValue( 8.0, coord );
      coord[0] = 2;
      coord[1] = 2;
      sourceArray.SetValue( 9.0, coord );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ScriptArray luArray = new ScriptArray();
    ScriptArray pivot = new ScriptArray();

    int summ = 0;
    try {
      sourceArray.CreateLU( luArray, pivot );
      summ = luArray.GetIntSumm();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
   // System.out.println("11");
    //System.out.println( luArray.toString() );
    assertEquals( summ, 27 );
  }

  public void testInverse_1(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.SetInitValue( "3" );
    def.AddDimension( 4 );
    def.AddDimension( 4 );
    ScriptArray sourceArray = new ScriptArray();
    ScriptArray invArray = new ScriptArray();
    boolean f = false;
    int[] coord = new int[2];
    coord[0] = 0;
    coord[1] = 0;
    try {
      sourceArray.InitArray( def );
      sourceArray.SetValue( 1.0, coord );
      coord[0] = 0;
      coord[1] = 1;
      sourceArray.SetValue( 2.0, coord );
      coord[0] = 0;
      coord[1] = 2;
      sourceArray.SetValue( 0.0, coord );
      coord[0] = 0;
      coord[1] = 3;
      sourceArray.SetValue( 0.0, coord );

      coord[0] = 1;
      coord[1] = 0;
      sourceArray.SetValue( 2.0, coord );
      coord[0] = 1;
      coord[1] = 1;
      sourceArray.SetValue( 1.0, coord );
      coord[0] = 1;
      coord[1] = 2;
      sourceArray.SetValue( 2.0, coord );
      coord[0] = 1;
      coord[1] = 3;
      sourceArray.SetValue( 0.0, coord );

      coord[0] = 2;
      coord[1] = 0;
      sourceArray.SetValue( 0.0, coord );
      coord[0] = 2;
      coord[1] = 1;
      sourceArray.SetValue( 2.0, coord );
      coord[0] = 2;
      coord[1] = 2;
      sourceArray.SetValue( 1.0, coord );
      coord[0] = 2;
      coord[1] = 3;
      sourceArray.SetValue( 2.0, coord );

      coord[0] = 3;
      coord[1] = 0;
      sourceArray.SetValue( 0.0, coord );
      coord[0] = 3;
      coord[1] = 1;
      sourceArray.SetValue( 0.0, coord );
      coord[0] = 3;
      coord[1] = 2;
      sourceArray.SetValue( 2.0, coord );
      coord[0] = 3;
      coord[1] = 3;
      sourceArray.SetValue( 1.0, coord );

      sourceArray.CreateInverseMatrix( invArray );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }

    f = false;
    ScriptArray res = new ScriptArray();
    int summ = 0;
    try {
      res.InitArray( def );
      ScriptArray.Mul( sourceArray, invArray, res );
      summ = res.GetIntSumm();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( summ, 4 );
    /*sourceArray.SetName( "source" );
    System.out.println( sourceArray.toString() );
    invArray.SetName("inverse");
    System.out.println( invArray.toString(  ) );
    res.SetName(" mul result ");
    System.out.println( res.toString() );*/
  }

  public void testInverse_InScript(){
    ScriptArray arr1 = new ScriptArray();
    arr1.SetName( "arr1" );
    ScriptArray arr2 = new ScriptArray();
    arr2.SetName( "arr2" );
    ScriptArray resArray = new ScriptArray();
    resArray.SetName( "res" );
    Variable var1 = new Variable(0);
    var1.SetName( "matrixSumm" );
    ArrayDefinition def = new ArrayDefinition();
    def.SetInitValue( "1" );
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.AddDimension( 4 );
    def.AddDimension( 4 );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      arr1.InitArray( def );
      arr2.InitArray( def );
      resArray.InitArray( def );
      ext.AddVariable( arr1 );
      ext.AddVariable( arr2 );
      ext.AddVariable( resArray );
      ext.AddVariable( var1 );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String sourceCode = " SetArrayValue( arr1, 0, 0, 1 );" +
            " SetArrayValue( arr1, 0, 1, 2 );" +
            " SetArrayValue( arr1, 0, 2, 0 );" +
            " SetArrayValue( arr1, 0, 3, 0 );" +

            " SetArrayValue( arr1, 1, 0, 2 );" +
            " SetArrayValue( arr1, 1, 1, 1 );" +
            " SetArrayValue( arr1, 1, 2, 2 );" +
            " SetArrayValue( arr1, 1, 3, 0 );" +

            " SetArrayValue( arr1, 2, 0, 0 );" +
            " SetArrayValue( arr1, 2, 1, 2 );" +
            " SetArrayValue( arr1, 2, 2, 1 );" +
            " SetArrayValue( arr1, 2, 3, 2 );" +

            " SetArrayValue( arr1, 3, 0, 0 );" +
            " SetArrayValue( arr1, 3, 1, 0 );" +
            " SetArrayValue( arr1, 3, 2, 2 );" +
            " SetArrayValue( arr1, 3, 3, 1 );" +

            " ArrayInverse( arr1, arr2 );" +
            " ArrayMul( arr1, arr2, res );" +
            "{print( arr2 );} " +
            "matrixSumm := GetArraySumm( res );";
    f = false;
    int i = 0;
    try {
      parser.ParseScript( sourceCode );
      parser.ExecuteScript();
      i = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i, 4 );
  }

  public void testToStr(){
    Variable var1 = new Variable(0.01);
    var1.SetName( "name" );
    System.out.println( var1.toString() );
  }

  public void testShiftRBooleanArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_BOOLEAN );
    def.AddDimension( 10 );
    ScriptArray boolArray = new ScriptArray();
    boolArray.SetName("boolArray");
    boolean f = false;
    try {
      boolArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    int[] coord = new int[1];
    int trueCount = 0;
    boolean arrayValue = false;
    int i  = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        boolArray.SetValue( arrayValue, coord );
      } catch (ScriptException e) { }
      arrayValue = !arrayValue;
      i++;
    }

    f = true;
    try {
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 5 );

    f = true;
    try {
      boolArray.ShiftR(1);
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 4 );

    f = true;
    try {
      coord[0] = 0;
      boolArray.SetValue( true, coord );
      //System.out.println( boolArray.toString() );
      boolArray.ShiftR(4);
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 3 );

    f = true;
    try {
      boolArray.ShiftR(15);
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 0 );
  }

  public void testShiftRIntArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("boolArray");
    boolean f = false;
    try {
      intArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    int summ = 0;
    int i = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        intArray.SetValue( i, coord );
      } catch (ScriptException e) {}
      i++;
    }//while
    summ = intArray.GetIntSumm();
    assertEquals( summ, 45 );
    f = true;
    try {
      intArray.ShiftR(1);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = intArray.GetIntSumm();
    //System.out.println( intArray.toString() );
    assertEquals( summ, 36 );

    try {
      intArray.ShiftR(177);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = intArray.GetIntSumm();
    //System.out.println( intArray.toString() );
    assertEquals( summ, 0 );
  }

  public void testShiftRRealArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.AddDimension( 10 );
    ScriptArray realArray = new ScriptArray();
    realArray.SetName("boolArray");
    boolean f = false;
    try {
      realArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    double summ = 0;
    int i = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        realArray.SetValue( i, coord );
      } catch (ScriptException e) {}
      i++;
    }//while
    summ = realArray.GetFloatSumm();
    assertTrue( summ > 44.99 && summ< 45.001 );

    f = true;
    try {
      realArray.ShiftR( 2 );
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = realArray.GetFloatSumm();
    assertTrue( summ > 27.99 && summ< 28.001 );

    try {
      realArray.ShiftR( 20 );
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = realArray.GetFloatSumm();
    assertTrue( summ < 0.001 );
  }

  public void testShiftRRealArrayInScript(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.AddDimension( 10 );
    ScriptArray realArray = new ScriptArray();
    realArray.SetName("realArray");
    boolean f = false;
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    try {
      realArray.InitArray( def );
      ext.AddVariable( realArray );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    double summ = 0;
    int i = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        realArray.SetValue( i, coord );
      } catch (ScriptException e) {}
      i++;
    }//while

    String sourceCode = " ShiftR( realArray, 2 ); ";
    try {
      parser.ParseScript( sourceCode );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = realArray.GetFloatSumm();
    assertTrue( summ > 27.99 && summ< 28.001 );
  }

  public void testShiftLBooleanArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_BOOLEAN );
    def.AddDimension( 10 );
    ScriptArray boolArray = new ScriptArray();
    boolArray.SetName("boolArray");
    boolean f = false;
    try {
      boolArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    int[] coord = new int[1];
    int trueCount = 0;
    boolean arrayValue = false;
    int i  = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        boolArray.SetValue( arrayValue, coord );
      } catch (ScriptException e) { }
      arrayValue = !arrayValue;
      i++;
    }

    f = true;
    try {
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 5 );

    f = true;
    try {
      boolArray.ShiftL(1);
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 5 );

    f = true;
    try {
      coord[0] = 0;
      boolArray.SetValue( true, coord );
      //System.out.println( boolArray.toString() );
      boolArray.ShiftL(4);
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 3 );

    f = true;
    try {
      boolArray.ShiftL(15);
      trueCount = GetTrueCount( boolArray, coord, 10);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    assertEquals( trueCount, 0 );
  }

  public void testShiftLIntArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("boolArray");
    boolean f = false;
    try {
      intArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    int summ = 0;
    int i = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        intArray.SetValue( i, coord );
      } catch (ScriptException e) {}
      i++;
    }//while
    summ = intArray.GetIntSumm();
    assertEquals( summ, 45 );
    f = true;
    try {
      intArray.ShiftL(1);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = intArray.GetIntSumm();
    assertEquals( summ, 45 );

    try {
      intArray.ShiftL(177);
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = intArray.GetIntSumm();
    assertEquals( summ, 0 );

  }

  public void testShiftLRealArray(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.AddDimension( 10 );
    ScriptArray realArray = new ScriptArray();
    realArray.SetName("boolArray");
    boolean f = false;
    try {
      realArray.InitArray( def );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    double summ = 0;
    int i = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        realArray.SetValue( i, coord );
      } catch (ScriptException e) {}
      i++;
    }//while
    summ = realArray.GetFloatSumm();
    assertTrue( summ > 44.99 && summ< 45.001 );

    f = true;
    try {
      realArray.ShiftL( 2 );
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = realArray.GetFloatSumm();
    assertTrue( summ > 43.99 && summ< 44.001 );

    try {
      realArray.ShiftR( 20 );
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = realArray.GetFloatSumm();
    assertTrue( summ < 0.001 );
  }

  public void testShiftLRealArrayInScript(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_REAL );
    def.AddDimension( 10 );
    ScriptArray realArray = new ScriptArray();
    realArray.SetName("realArray");
    boolean f = false;
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    try {
      realArray.InitArray( def );
      ext.AddVariable( realArray );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    double summ = 0;
    int i = 0;
    while ( i < 10 ){
      coord[0] = i;
      try {
        realArray.SetValue( i, coord );
      } catch (ScriptException e) {}
      i++;
    }//while

    String sourceCode = " ShiftL( realArray, 2 ); ";
    try {
      parser.ParseScript( sourceCode );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
      f = false;
    }
    assertTrue( f );
    summ = realArray.GetFloatSumm();
    assertTrue( summ > 43.99 && summ< 44.001 );
  }


  /*
   * Проверка присваивания элементу массива значения не при помощи функции, а
  при помсщи конструкции a[i] := 1;
  * */

  public void testSimpleArraySet(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("a");
    boolean f = false;
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();

    try {
      intArray.InitArray( def );
      ext.AddVariable( intArray );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String source = " a[1] := 10 ";
    f = false;
    int arrayVal = 0;
    try {
      parser.ParseScript( source );
      parser.ExecuteScript();

      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    coord[0] = 1;
    try {
	    arrayVal = intArray.GetIntValue(coord);
    } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(10, arrayVal );
  }

  public void testSimpleArraySet_2Dim(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    def.AddDimension( 10 );
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("a");
    boolean f = false;
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();

    try {
      intArray.InitArray( def );
      ext.AddVariable( intArray );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String source = " a[1,1] := 10 ";
    f = false;
    int arrayVal = 0;
    try {
      parser.ParseScript( source );
      parser.ExecuteScript();

      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[2];
    coord[0] = 1;
    coord[1] = 1;
    try {
	    arrayVal = intArray.GetIntValue(coord);
    } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(10, arrayVal );
  }

  public void testSimpleArrayGet(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    def.SetInitValue("5");
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("a");
    boolean f = false;
    Variable val = new Variable( 11 );
    val.SetName( "value" );

    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();

    try {
      intArray.InitArray( def );
      ext.AddVariable( intArray );
      ext.AddVariable(val);
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String source = " value := a[0]; ";
    f = false;
    int arrayVal = 999;
    try {
      parser.ParseScript( source );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    try {
	    arrayVal = val.GetIntValue();
    } catch (ScriptException e) {
	    e.printStackTrace();
	    f = false;
    }
    assertTrue(f);
    assertEquals(arrayVal, 5);
  }

  public void testArraySet_VariableIndex(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    def.SetInitValue("5");
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("a");
    boolean f = false;
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();

    Variable arrIndex = new Variable( 2 );
    arrIndex.SetName( "arrIndex" );

    try {
      intArray.InitArray( def );
      ext.AddVariable( intArray );
      ext.AddVariable( arrIndex );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String source = " a[arrIndex] := 10 ";
    f = false;
    int arrayVal = 0;
    try {
      parser.ParseScript( source );
      parser.ExecuteScript();

      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    int[] coord = new int[1];
    coord[0] = 2;
    try {
	    arrayVal = intArray.GetIntValue(coord);
    } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(10, arrayVal );

    coord[0] = 1;
    try {
	    arrayVal = intArray.GetIntValue(coord);
    } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(5, arrayVal );
  }

  public void testArraySet_VariableValue(){
    ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    def.SetInitValue("5");
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("a");
    boolean f = false;
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();

    Variable arrIndex = new Variable( 2 );
    arrIndex.SetName( "arrIndex" );

    Variable arrValue = new Variable( 0 );
    arrValue.SetName( "arrValue" );

    try {
      intArray.InitArray( def );
      ext.AddVariable( intArray );
      ext.AddVariable( arrIndex );
      ext.AddVariable( arrValue );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String source = "arrValue:= 15;  a[arrIndex] := arrValue; ";
    f = false;
    int arrayVal = 0;
    try {
      parser.ParseScript( source );
      parser.ExecuteScript();

      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] coord = new int[1];
    coord[0] = 2;
    try {
	    arrayVal = intArray.GetIntValue(coord);
    } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
    }
    coord[0] = 1;
    try {
	    arrayVal = intArray.GetIntValue(coord);
    } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(5, arrayVal );
  }

  public void testSetByArrayCoord(){
  	ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    def.SetInitValue("5");
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("a");

    ArrayDefinition def2 = new ArrayDefinition();
    def2.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def2.AddDimension( 2 );
    def2.SetInitValue("0");
    ScriptArray coordArray = new ScriptArray();
    coordArray.SetName("b");

    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      intArray.InitArray( def );
      coordArray.InitArray( def2 );
      ext.AddVariable( intArray );
      ext.AddVariable( coordArray );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    String source = "b[0] := 1;  a[ b[0] ] := 99; ";
    f = false;
    try {
      parser.ParseScript( source );
      parser.ExecuteScript();

      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int arrayVal = 0;
    int[] coord = new int[1];
    coord[0] = 1;
    try {
	    arrayVal = intArray.GetIntValue(coord);
    } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(arrayVal, 99);

  }

  public void testGetByArrayCoord(){
  	ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    def.SetInitValue("5");
    ScriptArray intArray = new ScriptArray();
    intArray.SetName("a");

    ArrayDefinition def2 = new ArrayDefinition();
    def2.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def2.AddDimension( 2 );
    def2.SetInitValue("0");
    ScriptArray coordArray = new ScriptArray();
    coordArray.SetName("b");

    ArrayDefinition def3 = new ArrayDefinition();
    def3.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def3.AddDimension( 10 );
    def3.SetInitValue("0");
    ScriptArray array2 = new ScriptArray();
    array2.SetName("c");

    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      intArray.InitArray( def );
      coordArray.InitArray( def2 );
      array2.InitArray( def3 );
      ext.AddVariable( intArray );
      ext.AddVariable( coordArray );
      ext.AddVariable(array2);
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    String source = "b[0] := 1; c[0] := 77;   a[ b[0] ] := c[0]; ";
    f = false;
    try {
      parser.ParseScript( source );
      parser.ExecuteScript();

      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    int arrayVal = 0;
    int[] coord = new int[1];
    coord[0] = 1;
    try {
	    arrayVal = intArray.GetIntValue(coord);
    } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(arrayVal, 77);
  }

 public void testAdd(){
	 ArrayDefinition def = new ArrayDefinition();
   def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
   def.AddDimension( 10 );
   def.SetInitValue("0");
   ScriptArray intArray = new ScriptArray();
   intArray.SetName("a");

   ArrayDefinition def2 = new ArrayDefinition();
   def2.SetValueType( Operand.OPERAND_TYPE_INTEGER );
   def2.AddDimension( 2 );
   def2.SetInitValue("0");
   ScriptArray coordArray = new ScriptArray();
   coordArray.SetName("b");

   ScriptLanguageExt ext = new ScriptLanguageExt();
   PascalParser parser = new PascalParser();

   boolean f = false;
   try {
     intArray.InitArray( def );
     coordArray.InitArray( def2 );
     ext.AddVariable( intArray );
     ext.AddVariable( coordArray );
     parser.SetLanguageExt( ext );
     f = true;
   } catch (ScriptException e) {
     e.printStackTrace();
   }
   assertTrue(f);

   String source = "b[0] := 1; a[0] := 77;   a[ 1 ] := a[0] + b[0]; ";
   f = false;
   try {
     parser.ParseScript( source );
     parser.ExecuteScript();

     f = true;
   } catch (ScriptException e) {
     e.printStackTrace();
   }
   assertTrue( f );


   int arrayVal = 0;
   int[] coord = new int[1];
   coord[0] = 1;
   try {
	    arrayVal = intArray.GetIntValue(coord);
   } catch (ScriptException e) {
	    f = false;
	    e.printStackTrace();
   }
   assertTrue(f);
   assertEquals(arrayVal, 78);
 }

 /**
  *  Проверяем правильность возникновения ошибки, когда при установке значения массива в него передается неверное количество координат
  */
 public void testWrongCoord(){
	 ArrayDefinition def = new ArrayDefinition();
   def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
   def.AddDimension( 10 );
   def.AddDimension( 10 );
   ScriptArray intArray = new ScriptArray();
   intArray.SetName("a");
   boolean f = false;
   ScriptLanguageExt ext = new ScriptLanguageExt();
   PascalParser parser = new PascalParser();

   try {
     intArray.InitArray( def );
     ext.AddVariable( intArray );
     parser.SetLanguageExt( ext );
     f = true;
   } catch (ScriptException e) {
     e.printStackTrace();
   }
   assertTrue( f );
   String source = " a[1] := 10 ";
   f = false;
   int arrayVal = 0;
   try {
     parser.ParseScript( source );
     parser.ExecuteScript();
     f = true;
   } catch (ScriptException e) {
     e.printStackTrace();
   }
   assertTrue(!f);

 }

}

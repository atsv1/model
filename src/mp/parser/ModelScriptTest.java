package mp.parser;

import junit.framework.TestCase;
import java.util.Vector;

/**
 * User: atsv
 * Date: 04.04.2006
 */
public class ModelScriptTest extends TestCase {

  public ModelScriptTest(String testName) {
    super(testName);
  }

  /** Проверяется правильность работы класса ScriptLexemTokenizer.
   * В этом тесте классу передается просто название переменной. И он должен вернуть именно ее, но без пробелов в
   * начале и в конце последовательности
   */
  public void testTokenizer_VarName(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer(" мфк1  ");
    String s = t.GetNextLexem();
    assertTrue( "мфк1".equalsIgnoreCase( s ) );
  }

  public void testTokenizer_2Var(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer(" мфк1  var2");
    String s = t.GetNextLexem();
    assertTrue( "мфк1".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( "var2".equalsIgnoreCase( s ) );
  }

  /**Проверяется правильность разбиения на лексемы. Две переменных разделены единичной лексемой - символом ";"
   */
  public void testTokenizer_VarWithLexemSeparator(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer(" мфк1;var2");
    String s = t.GetNextLexem();
    assertTrue( "мфк1".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( ";".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( "var2".equalsIgnoreCase( s ) );
  }

  public void testTokenizer_VarWithLexemSeparator2(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer(" мфк1 ; var2");
    String s = t.GetNextLexem();
    assertTrue( "мфк1".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( ";".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( "var2".equalsIgnoreCase( s ) );
  }

  /**Проверяется правильность разбиения на лексемы, если  разделитель - лексема, состоящая из нескольких
   * символов.
   */
  public void testTokenizer_NoSingleSeparator(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer(" var1:=var2");
    String s = t.GetNextLexem();
    assertTrue( "var1".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( ":=".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( "var2".equalsIgnoreCase( s ) );
  }

  public void testTokenizer_NoSingleSeparator_2(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer(" var1 := var2");
    String s = t.GetNextLexem();
    assertTrue( "var1".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( ":=".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( "var2".equalsIgnoreCase( s ) );
  }

  /**Проверяется правильность разбиения на лексемы в следующем случае: в тексте встретилась лексема-блок,
   * которую нужно вернуть вызывающей процедуре. При этом  не нужно возвращать начальный и конечный маркер блока.
   */
  public void testTokenizer_BlockMarker(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer("[123 567]");
    String s = t.GetNextLexem();
    assertTrue( "123 567".equalsIgnoreCase( s ) );
  }

  public void testTokenizer_BlockMarker2(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer("[var 1] := [var2]");
    String s = t.GetNextLexem();
    assertTrue( "var 1".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( ":=".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( "var2".equalsIgnoreCase( s ) );
  }

  public void testTokenizer_StringMarker(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer("[var 1] :=  \"var2\" ");
    String s = t.GetNextLexem();
    assertTrue( "var 1".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( ":=".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( "\"var2\"".equalsIgnoreCase( s ) );
  }

  public void testTokenizer_Comment(){
    ScriptLexemTokenizer t = new ScriptLexemTokenizer("var1 :=  {комментарий} var2");
    String s = t.GetNextLexem();
    assertTrue( "var1".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( ":=".equalsIgnoreCase( s ) );
    s = t.GetNextLexem();
    assertTrue( "var2".equalsIgnoreCase( s ) );
  }

  public void testOperand()
  {
    Operand simpleOperand;
    simpleOperand = new ScriptConstant(7);
    try {
      assertEquals( simpleOperand.GetIntValue(),7 );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }

  public void testOperand_DoubleToString(){
    String s = Operand.DoubleToString( null, 0.5 );
    assertTrue( "0.5000".equalsIgnoreCase( s ) );

    s = Operand.DoubleToString( null, -10.5 );
    assertTrue( "-10.5000".equalsIgnoreCase( s ) );

    s = Operand.DoubleToString( null, 1.05 );
    assertTrue( "1.0500".equalsIgnoreCase( s ) );

    /*s = Operand.DoubleToString( null, 1.0005 );
    assertTrue( "1.0005".equalsIgnoreCase( s ) );*/
  }

  /**Проверяется правильность сравнения двух различных объектов Variable
   *
   */
  public void testVariableCompare(){
    Variable var1 = new Variable(true);
    Variable var2 = new Variable(0);
    boolean f = false;
    try {
      var1.Compare( var2 );
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
    Variable var3 = new Variable(1);
    int compareRes = 0;
    try {
      compareRes = var3.Compare( var2 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertEquals( compareRes, 1 );
    f = false;
    try {
      compareRes = var2.Compare( var3 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( compareRes, -1 );

    Variable var4 = new Variable(0.0);
    f = false;
    try {
      compareRes = var4.Compare( var2 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( compareRes, 0 );

    f = false;
    var4.SetValue( 0.5 );
    try {
      compareRes = var4.Compare( var2 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( compareRes, 1 );

    f = false;
    Variable var5 = new Variable(1.5);
    var4.SetValue( 1.5 );
    try {
      compareRes = var5.Compare( var4 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( compareRes, 0 );
  }

  /**Проверяется правильность работы метода Variable.SetValueWithTypeCheck() - метода загрузки в переменную значения,
   * представленного в текстовом вида.
   *
   */
  public void testVariable_LoadWithCheck_Int(){
    Variable var1 = new Variable(1);
    int i = 0;
    try {
      i = var1.GetIntValue();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertEquals( i, 1 );
    boolean f = false;
    try {
      var1.SetValueWithTypeCheck("5");
      i = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i, 5 );
    f = false;
    try {
      var1.SetValueWithTypeCheck( "5.4" );
      i = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i, 5 );
  }

  public void testVariable_LoadWithCheck_Real(){
    Variable var = new Variable(0.0);
    double d = 0;
    boolean f = false;
    Variable testVar = new Variable(5.5);
    try {
      var.SetValueWithTypeCheck( "5.5" );
      d = var.GetFloatValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    try {
      assertTrue( testVar.Compare( var ) == 0 );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }

  public void testDigitLexem()
  {
    ScriptDigitLexem digitLexem;
    digitLexem = new ScriptDigitLexem();
    assertEquals( digitLexem.IsMyToken("5"), true );
    assertEquals( digitLexem.IsMyToken("576"), true );
    assertEquals( digitLexem.IsMyToken("ук"), false );

  }

  public void testActionLexem()
  {
    ScriptActionLexem3Operand actionLexem;
    actionLexem = new ScriptActionLexem3Operand();
    assertEquals( actionLexem.IsMyToken("xor"), true );
    assertEquals( actionLexem.IsMyToken("98"), false );
    ScriptSimpleLexem lexem;
    lexem = new ScriptSimpleLexem("(");
    assertEquals( actionLexem.IsLexemEquals( lexem ), false );
    ScriptActionLexem3Operand actionLexem1;
    actionLexem1 = new ScriptActionLexem3Operand();
    assertEquals( actionLexem.IsLexemEquals( actionLexem1 ), true );

  }

  public void testParserOneLexem()
  {
    PascalParser parser;
    parser = new PascalParser("+");
    ScriptActionLexem3Operand actionLexem;
    ScriptActionLexem3Operand testedLexem;
    actionLexem = new ScriptActionLexem3Operand();
    try
    {
      parser.ParseScript("5 +");
    }
    catch (ScriptException e)
    {

    }
    testedLexem = (ScriptActionLexem3Operand) parser.GetParsedLexem(2);
    assertEquals( actionLexem.IsLexemEquals(testedLexem), true );


  }

  public void testParserOneLexemIntValue()
    {
      PascalParser parser;
      parser = new PascalParser("+");
      ScriptActionLexem3Operand actionLexem;
      ScriptActionLexem3Operand testedLexem;
      actionLexem = new ScriptActionLexem3Operand();
      ScriptDigitLexem digitLexem = new ScriptDigitLexem();
      try
      {
        parser.ParseScript("556 +");
      }
      catch (ScriptException e)
      {

      }
      testedLexem = (ScriptActionLexem3Operand) parser.GetParsedLexem(2);
      assertEquals( actionLexem.IsLexemEquals(testedLexem), true );
      ScriptDigitLexem testedLexem2;
      testedLexem2 = (ScriptDigitLexem)parser.GetParsedLexem(1);
      assertTrue(digitLexem.IsLexemEquals(testedLexem2));
    }


  public void testParserOneLexemFloatValue()
    {
      PascalParser parser;
      parser = new PascalParser("+");
      ScriptActionLexem3Operand actionLexem;
      ScriptActionLexem3Operand testedLexem;
      actionLexem = new ScriptActionLexem3Operand();
      ScriptDigitLexem digitLexem = new ScriptDigitLexem();
      try
      {
        parser.ParseScript("55.6 +");
      }
      catch (ScriptException e)
      {

      }
      testedLexem = (ScriptActionLexem3Operand) parser.GetParsedLexem(2);
      assertEquals( actionLexem.IsLexemEquals(testedLexem), true );
      ScriptDigitLexem testedLexem2;
      testedLexem2 = (ScriptDigitLexem)parser.GetParsedLexem(1);
      assertTrue(digitLexem.IsLexemEquals(testedLexem2));
    }

  public void testParser2Lexem(){
    PascalParser parser;
    parser = new PascalParser("+");
    ScriptActionLexem3Operand actionLexem;
    ScriptLexem testedLexem;
    actionLexem = new ScriptActionLexem3Operand();
    ScriptLexem testedLexem2;
    ScriptDigitLexem digitLexem;
    digitLexem = new ScriptDigitLexem();

    try {
      parser.ParseScript("3+");
    }
    catch (ScriptException e)
    {}
    try {
      testedLexem = parser.GetParsedLexem(2);
      assertEquals( actionLexem.IsLexemEquals(testedLexem), true );
      testedLexem2 = parser.GetParsedLexem(1);
      assertEquals( digitLexem.IsLexemEquals(testedLexem2), true );
      ScriptLexem testedLexem3;
      testedLexem3 = new ScriptSimpleLexem("(");
      testedLexem3.SetLanguageName("(");
      assertEquals( digitLexem.IsLexemEquals(testedLexem3), false );
    } catch (ScriptException e)
    {  }
  }

   public void testErrorScript() {
     PascalParser parser;
     parser = new PascalParser();
     boolean f = true;
     try {
       parser.ParseScript("6 7");
       f = false;
     } catch (ScriptException e) {
       f = true;
     }
     assertTrue(f);
     f = true;
     try {
       parser.ParseScript("6 ++");
       f = false;
     } catch (ScriptException e) {
       f = true;
     }
     assertTrue(f);
     try {
       parser.ParseScript("+ 7");
       f = false;
     } catch (ScriptException e) {
       f = true;
     }
     assertTrue(f);
   }


   public void testGetLexem() {
    PascalParser parser;
    parser = new PascalParser();
    ScriptDigitLexem digitLexem1;
    digitLexem1 = (ScriptDigitLexem) parser.GetLexem("Число");
    assertTrue(digitLexem1 != null);

    ScriptDigitLexem digitLexem2;
    digitLexem2 = (ScriptDigitLexem) parser.GetLexem("Число плюс что-то там");
    assertTrue(digitLexem2 == null);
  }



  public void testSvertkaEnabled() {
    StringBuffer code = new StringBuffer();
    ScriptDigitLexem digitLexem1 = new ScriptDigitLexem();
    ScriptDigitLexem digitLexem2 = new ScriptDigitLexem();
    ScriptEndLexem endLexem = new ScriptEndLexem();
    ScriptActionLexem3Operand actionLexem = new ScriptActionLexem3Operand();
    PascalParser parser = new PascalParser();
    boolean hasException = false;
    try {
      parser.Add2ParsedLexemList( digitLexem1, code );
      parser.Add2ParsedLexemList( actionLexem, code );
      parser.Add2ParsedLexemList( digitLexem2, code );
      parser.Add2ParsedLexemList( endLexem, code );
      hasException = false;
    } catch ( ScriptException e ) {
      hasException = true;
      System.out.println(e.getMessage());
    }
    assertTrue( !hasException );
    assertTrue( parser.GetSvertkaIndex(1) != 0 );
  }

  public void testAddOperation() {
    PascalParser parser;
    parser = new PascalParser("");
    boolean f = false;
    try {
      parser.ParseScript("6 + 7");
      f = true;
    } catch (ScriptException e) {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    Operand operand1 = parser.GetVariables().GetVariable("TmpVar_0");
    assertTrue( operand1 != null);
  }

  /** Проверются "внутренности" скомпилированного кода.
   *  Смысл этой проверки достаточно сомнителен
   */
  public void testProgram() {
    PascalParser parser;
    parser = new PascalParser("");
    boolean f = false;
    try
    {
      parser.ParseScript("6 + 7 + 8 ");
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    ScriptOperationAdd operation;
    ScriptConstant constant1;
    ScriptConstant constant2;
    Variable var1;
    f = false;
    try {
      operation = (ScriptOperationAdd)parser.FProgram.get(1);
      constant1 = (ScriptConstant)parser.FProgram.get(2);
      constant2 = (ScriptConstant)parser.FProgram.get(3);
      var1 = (Variable) parser.FProgram.get(4);
      operation = (ScriptOperationAdd)parser.FProgram.get(5);
      var1 = (Variable) parser.FProgram.get(6);
      constant2 = (ScriptConstant)parser.FProgram.get(7);
      var1 = (Variable) parser.FProgram.get(8);
      f = true;
    } catch (Exception r){}
    assertTrue(f);
  }

  public void testCreateOperation()
  {
    ScriptActionLexem3Operand action = new ScriptActionLexem3Operand();
    action.FCodePart = "+";
    ScriptOperationAdd operation;
    boolean f = false;
    try
    {
      operation = (ScriptOperationAdd)action.GetExecutableObject();
      f = true;
    }
    catch (Exception e)
    {   }
    assertTrue( f );
    ScriptOperationMul operation2;
    f = false;
    try
    {
      operation2 = (ScriptOperationMul)action.GetExecutableObject();
      f = true;
    } catch (Exception e) {}
    assertTrue( !f );
  }

  public void testVariablesList()
  {
    VariableList list = new VariableList();
    Variable testVariable = new Variable(6);
    testVariable.SetName("testVariable");
    list.AddVariable( testVariable );
    Variable testVariable2 = (Variable) list.GetVariable("testVariable");
    try {
      assertEquals( testVariable2.GetIntValue(),6 );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    Variable testVariable3 = (Variable) list.GetVariable("testVariable2");
    assertNull(testVariable3);
  }

  public void testTempVariables()
  {
    VariableList list = new VariableList();
    Variable tempVariable = list.AddTempVariable();
    assertTrue( tempVariable.GetName().equalsIgnoreCase("TmpVar_0") );
    Variable testOperand1 = list.AddTempVariable();
    assertTrue( testOperand1.GetName().equalsIgnoreCase("TmpVar_1") );
  }

  public void testAddOperator()
  {
    ScriptOperationAdd operation = new ScriptOperationAdd();
    operation.Program = new Vector();
    Variable var1 = new Variable(1);
    var1.SetName("name1");
    Variable var2 = new Variable(2);
    var2.SetName("name2");
    Variable var3 = new Variable(1);
    var3.SetName("name3");
    operation.Program.add(this);
    operation.Program.add(var1);
    operation.Program.add(var2);
    operation.Program.add(var3);
    boolean f = false;
    try
    {
      operation.ExecOperation(0);
      f = true;
    } catch (ScriptException e) {}
    assertTrue(f);
    try {
      assertEquals( var3.GetIntValue(),3 );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }


  public void testExecutionAddInt() {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("4+10");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
    	e.printStackTrace();
    }
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
    try {
      assertEquals( var1.GetIntValue(),14 );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }

   public void testExecutionAddFloat()
  {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("4.1+10");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
     try {
       f = ( var1.GetFloatValue() == (float)14.1 );
     } catch (ScriptException e) {
       e.printStackTrace();
     }
     assertTrue( f );
  }

  public void testExecutionMulInt() {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("4*10");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
    try {
      assertEquals( var1.GetIntValue(),40 );
    } catch (ScriptException e) {
    }
  }

   public void testExecutionMulFloat() {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("4.1*10.5");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
     boolean f1 = false;
     try {
       f1 = ( var1.GetFloatValue() == (float)43.05 );
     } catch (ScriptException e) {
     }
     assertTrue( f1 );
  }

   public void testExecutionDiv() {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("10 / 4");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
     boolean f1 = false;
     try {
       f1 = ( var1.GetFloatValue() == (float)2.5 );
     } catch (ScriptException e) {
     }
     assertTrue( f1 );
  }

  public void testExecutionPow()
  {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("2 ^ 2");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
    boolean f1 = false;
    try {
      f1 = ( var1.GetFloatValue() == (float)4 );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
  }

  public void testExecutionSubInt()
  {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("10 - 4");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
    boolean f1 = false;
    try {
      f1 = ( var1.GetFloatValue() == (float)6 );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
  }

  public void testExecutionSubFloat()
  {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("10.1 - 4");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");

    boolean f1 = false;
    try {
      f1 = ( (var1.GetFloatValue() >= (float)6.1) & (var1.GetFloatValue() <= (float)6.11) );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
  }

  public void testExecution3Operation()
  {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("10 - 4 * 5");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
    boolean f1 = false;
    try {
      f1 = ( var1.GetIntValue() == 20 );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_1");
    try {
      f1 = ( var1.GetIntValue() == -10 );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
  }

  public void testExecution3Operation_1()
  {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("(10 - (4 * 5))+10");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var1;
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_0");
    boolean f1 = false;
    try {
      f1 = ( var1.GetIntValue() == 20 );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f1 );
    var1 = (Variable) parser.GetVariables().GetVariable("TmpVar_2");
    try {
      f1 = ( var1.GetIntValue() == 0 );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
    var1 = parser.GetVariables().GetLastTempVariable();
    try {
      f1 = ( var1.GetIntValue() == 0 );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
  }

  public void testExecution3Operation_2_Error()
  {
    PascalParser parser = new PascalParser();
    boolean f = false;
    try
    {
      parser.ParseScript("(10 - (4 * 5+10)");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(!f);
  }

  public void testExecutionSubOrder(){
    Variable var1 = new Variable( 50);
    var1.SetName( "var1" );
    Variable var2 = new Variable( 3 );
    var2.SetName( "var2" );
    Variable var3 = new Variable( 5 );
    var3.SetName( "var3" );
    Variable var4 = new Variable( 18 );
    var4.SetName( "var4" );
    Variable result = new Variable( 0 );
    result.SetName( "result" );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    int res = 0;
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      ext.AddVariable( var3 );
      ext.AddVariable( var4 );
      ext.AddVariable( result );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "result := var1 - var2 - var3 - var4" );
      parser.ExecuteScript();
      res = result.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 24 );
  }


   public void testParseOperand()
  {
    VariableList variables = new VariableList();
    Variable var1 = new Variable(8);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    try
    {
      parser.ParseScript("var1 + 6 + 10");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {}
    assertTrue(f);
    Variable var2;
    var2 =  parser.GetVariables().GetLastTempVariable();
     boolean f1 = false;
     try {
       f1 = ( var2.GetIntValue() == 24 );
     } catch (ScriptException e) {
     }
     assertTrue( f1 );
  }

   public void testParseMov()
  {
    VariableList variables = new VariableList();
    Variable var1 = new Variable(8);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    try
    {
      parser.ParseScript("var1 := 6 + 10");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    //var1 = (Variable) parser.Variables.GetLastTempVariable();
     boolean f1 = false;
     try {
       f1 = ( var1.GetIntValue() == 16 );
     } catch (ScriptException e) {
     }
     assertTrue( f1 );
  }

  public void testParseMov_2()
   {
     VariableList variables = new VariableList();
     Variable var1 = new Variable(8);
     var1.SetName("var1");
     variables.AddVariable( var1 );
     PascalParser parser = new PascalParser( variables );
     boolean f = false;
     try
     {
       parser.ParseScript("var1 := (6 + 4) * 5");
       parser.ExecuteScript();
       f = true;
     } catch (ScriptException e)
     {
       System.out.println( e.getMessage() );
     }
     assertTrue(f);
     //var1 = (Variable) parser.Variables.GetLastTempVariable();
    boolean f1 = false;
    try {
      f1 = ( var1.GetIntValue() == 50 );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
   }

  public void testParseMov_3()
  {
    VariableList variables = new VariableList();
    Variable var1 = new Variable(8);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    try
    {
      parser.ParseScript("var1 := 4 + 6 ^ 2 + 4");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    //var1 = (Variable) parser.Variables.GetLastTempVariable();
    boolean f1 = false;
    try {
      f1 = ( var1.GetIntValue() == 44 );
    } catch (ScriptException e) {
    }
    assertTrue( f1 );
  }

  public void testFunctionSin() {
    VariableList variables = new VariableList();
    Variable var1 = new Variable((float)0);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    boolean f1 = false;
    try {
      parser.ParseScript("var1 := sin(0) + 5");
      parser.ExecuteScript();
      int i = (int)var1.GetFloatValue();
      f1 = (i == 5);
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    assertTrue(f1);
  }

  public void testFunctionASin()
  {
    VariableList variables = new VariableList();
    Variable var1 = new Variable((float)0);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    boolean f1 = false;
    try {
      parser.ParseScript("var1 := asin(0) + 5");
      parser.ExecuteScript();
      int i = (int)var1.GetFloatValue();
      f1 = (i == 5);
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    assertTrue(f1);
  }

  public void testFunctionCos()
  {
    VariableList variables = new VariableList();
    Variable var1 = new Variable((float)0);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    boolean f1 = false;
    try {
      parser.ParseScript("var1 := cos(0) + 5");
      parser.ExecuteScript();
      int i = (int)var1.GetFloatValue();
      f1 = (i == 6);
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    assertTrue(f1);
  }

  public void test2Function()
  {
    VariableList variables = new VariableList();
    Variable var1 = new Variable((float)0);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    Variable var2 = new Variable((float)0);
    var2.SetName("var2");
    variables.AddVariable( var2 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    boolean f1 = false;
    try {
      parser.ParseScript("var1 := cos(0)*sin(var2) + 5");
      parser.ExecuteScript();
      int i = (int)var1.GetFloatValue();
      f1 = (i == 5);
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    assertTrue(f1);
  }

  public void testTruncateFunction(){
    VariableList variables = new VariableList();
    Variable var1 = new Variable((float)1.5);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    Variable var2 = new Variable( 15 );
    var2.SetName("var2");
    variables.AddVariable( var2 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    try {
      parser.ParseScript( "var2 := truncate( var1 );" );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int i = 0;
    try {
      i = var2.GetIntValue();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertEquals( i, 1 );
    f = true;
    try {
      parser.ParseScript( "var2 := truncate(23.9)" );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    try {
      i = var2.GetIntValue();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertEquals( i, 23 );
  }

  public void testStringSeparator_1()
  {
    VariableList variables = new VariableList();
    Variable var1 = new Variable((float)0);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    Variable var2 = new Variable((float)0);
    var2.SetName("var2");
    variables.AddVariable( var2 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    boolean f1 = false;
    boolean f2 = false;
    try {
      parser.ParseScript("var1 := cos(0)*sin(var2) + 5; var2 := var1 + 8.2;");
      parser.ExecuteScript();
      int i = (int)var1.GetFloatValue();
      f1 = (i == 5);
      i = (int)var2.GetFloatValue();
      f2 = ( i == 13 );
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    assertTrue(f1);
    assertTrue(f2);
  }

  public void testPi()
  {
    VariableList variables = new VariableList();
    Variable var1 = new Variable((float)0);
    var1.SetName("var1");
    variables.AddVariable( var1 );
    Variable var2 = new Variable((float)0);
    var2.SetName("var2");
    variables.AddVariable( var2 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    boolean f1 = false;
    boolean f2 = false;
    try {
      parser.ParseScript("var1 := cos( pi )*sin( pi / 2 ) + 5; var2 := var1 + 8.2;");
      parser.ExecuteScript();
      int i = (int)var1.GetFloatValue();
      f1 = (i == 4);
      i = (int)var2.GetFloatValue();
      f2 = ( i == 12 );
      f = true;
    } catch (ScriptException e)
    {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    assertTrue(f1);
    assertTrue(f2);
  }


  public void testOperandResultType()
  {
    boolean f = true;
    int i = 1;
    float r = (float) 7.6;
    Operand booleanOperand = new Variable( f );
    Operand intOperand = new Variable(i);
    Operand floatOperand = new Variable(r);
    ScriptOperationAdd operation = new ScriptOperationAdd();

    operation.InitFirstOperand( booleanOperand );
    operation.InitSecondOperand( booleanOperand );
    String s = null;
    boolean f1 = false;
    try
    {
      s = operation.GetResultType();
      f1 = true;
    } catch(ScriptException e){}
    assertTrue(f1);
    assertEquals( s,"boolean" );

    f1 = false;
    operation.InitSecondOperand( intOperand );
    s = null;
    try
    {
      s = operation.GetResultType();
      f1 = true;
    } catch (ScriptException e){}
    assertTrue(!f1);

    f1 = false;
    operation.InitFirstOperand( intOperand );
    operation.InitSecondOperand( floatOperand );
    s = null;
    try
    {
      s = operation.GetResultType();
      f1 = true;
    } catch(ScriptException e){}
    assertEquals(s,"real");

    f1 = false;
    operation.InitSecondOperand( booleanOperand );
    s = null;
    try
    {
      s = operation.GetResultType();
      f1 = true;
    } catch(ScriptException e){}
    assertTrue(!f1);
  }


  public void testBooleanMov()
  {
    Variable var1;
    var1 = new Variable(true);
    var1.SetName("var1");
    VariableList variables = new VariableList();
    variables.AddVariable( var1 );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := false;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(!var1.GetBooleanValue());
  }


   public void testBooleanAnd()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(true);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(true);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(false);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := var2 and var3;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(!var1.GetBooleanValue());
  }

  public void testIntAnd()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(1);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(2);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(3);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    int i = 0;
    try
    {
      parser.ParseScript("var1 := var2 and var3;");
      parser.ExecuteScript();
      i = var1.GetIntValue();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertEquals(i,2);
  }

   public void testBooleanOr()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(true);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(true);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(false);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := var2 OR var3;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(var1.GetBooleanValue());
  }

  public void testIntOr()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(1);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(2);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(3);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    int i = 0;
    try
    {
      parser.ParseScript("var1 := var2 or var3;");
      parser.ExecuteScript();
      i = var1.GetIntValue();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertEquals(i,3);
  }


  public void testBooleanMultipleBoolean()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(false);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(true);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := var2 OR var3 and var1;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(!var1.GetBooleanValue());
  }

  public void testBooleanXOR()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(true);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(true);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(false);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := var2 XOR var3;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(var1.GetBooleanValue());
  }


  public void testBooleanEquals()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(true);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(true);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := var2 = var3; var3 := true = var1; var2 := false;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(var1.GetBooleanValue());
    assertTrue(var3.GetBooleanValue());
    assertTrue(!var2.GetBooleanValue());
  }

  public void testNumericEquals()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(true);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(1);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(1);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    Variable var4;
    var4 = new Variable( false );
    var4.SetName("var4");
    variables.AddVariable( var4 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    int i1 = 0;
    int i2 = 0;
    try
    {
      parser.ParseScript("var1 := var2 = var3; var2 := 7; var3 := 8; var4 := var2 = var3;");
      parser.ExecuteScript();
      i1 = var2.GetIntValue();
      i2 = var3.GetIntValue();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(var1.GetBooleanValue());
    assertEquals( i1,7 );
    assertEquals( i2,8 );
    assertTrue(!var4.GetBooleanValue());
  }

  public void testBooleanNot()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(true);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(true);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := not var2;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(!var1.GetBooleanValue());
  }

  /**Проверяется правильность выполнения операции >=
   */
  public void testBooleanMoreOrEquals(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := 7 >= 7;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(var1.GetBooleanValue());
  }

  /**Производится проверка операции >= при сравнении типов integer и real.
   *
   */
  public void testGreaterOrEquals(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    double d = 15.0;
    Variable var1 = new Variable(d);
    var1.SetName("var1");
    Variable var2 = new Variable(15);
    var2.SetName("var2");
    Variable result = new Variable(false);
    result.SetName("result");
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      ext.AddVariable( result );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String s = " result := (var1 >= var1) ";
    f = false;
    try {
      parser.ParseScript( s );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    boolean r = false;
    r = result.GetBooleanValue();
    assertTrue( r );

    s = " result := (var2 >= var1) ";
    f = false;
    try {
      parser.ParseScript( s );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    r = false;
    r = result.GetBooleanValue();
    assertTrue( r );

    s = " result := (var1 >= var2) ";
    f = false;
    try {
      parser.ParseScript( s );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    r = false;
    r = result.GetBooleanValue();
    assertTrue( r );

    var1.SetValue(15.1);
    result.SetValue( false );
    s = " result := (var2 >= var1) ";
    f = false;
    try {
      parser.ParseScript( s );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    r = false;
    r = result.GetBooleanValue();
    assertTrue( !r );

  }

  public void testBooleanLowerOrEquals_Int(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := 6 <= 7;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(var1.GetBooleanValue());
  }

  public void testBooleanLowerOrEquals_Real(){
    VariableList variables = new VariableList();
    Variable result = new Variable(false);
    result.SetName("result");
    Variable var1 = new Variable(1.5);
    var1.SetName("var1");
    Variable var2 = new Variable(2.5);
    var2.SetName("var2");
    variables.AddVariable( var1 );
    variables.AddVariable( var2 );
    variables.AddVariable( result );
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    try {
      parser.ParseScript( "result := ( var1 <= var2 );" );
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertTrue(result.GetBooleanValue());
    var2.SetValue( 1.5 );
    f = false;
    try {
      parser.ExecuteScript( );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue(result.GetBooleanValue());
  }

  public void testNotEquals(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;

    try
    {
      parser.ParseScript("var1 := 6 <> 7;");
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    assertTrue(var1.GetBooleanValue());
  }

  /**проверка правильности обработки команды, в которой переменной присваивается отрицательное значение.
   * var1 := -1;
   */
  public void testParseSubZeroValues(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(0);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    int i = 0;
    try
    {
      parser.ParseScript("var1 := -1;");
      parser.ExecuteScript();
      i = var1.GetIntValue();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    //@todo
//    assertTrue(f);
//    assertEquals( i, -1 );

  }

  public void testVarCast_String(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(1);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable("");
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable(1.1);
    var3.SetName("var3");
    variables.AddVariable( var3 );

    Variable var4;
    var4 = new Variable("");
    var4.SetName("var4");
    variables.AddVariable( var4 );

    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    String res = null;
    String res2 = null;
    try
    {
      parser.ParseScript("var2 := string( var1 ); var4 := string(var3)");
      parser.ExecuteScript();
      res = var2.GetStringValue();
      res2 = var4.GetStringValue();
      f = true;
    } catch (Exception e)  {
      e.printStackTrace();
      parser.PrintLexemList();
    }
    assertTrue( f );
    assertTrue( "1".equalsIgnoreCase( res ) );
    assertTrue( res2 != null );
    assertTrue( res2.startsWith("1.") );
    //System.out.println( res2 );
  }

  public void testVarCast_Integer(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(1);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable("12");
    var2.SetName("var2");
    variables.AddVariable( var2 );

    PascalParser parser = new PascalParser( variables );
    int res = -1;
    boolean f = false;
    try {
      parser.ParseScript("var1 := integer( var2 );");
      parser.ExecuteScript();
      res = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 12 );

    //проверяем ошибочную ситуацию, когда делается попытка преобразовать в целое число некую непреобразовываемую строку
    var2.SetValue( "fsgklh" );
    f = false;
    try {
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  public void testVarCast_Real(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(1.1);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable("12.5");
    var2.SetName("var2");
    variables.AddVariable( var2 );

    PascalParser parser = new PascalParser( variables );
    double res = -1;
    boolean f = false;
    try {
      parser.ParseScript("var1 := real( var2 );");
      parser.ExecuteScript();
      res = var1.GetFloatValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( 12.5, res );

    //проверяем ошибочную ситуацию, когда делается попытка преобразовать в  число некую непреобразовываемую строку
    var2.SetValue( "fsgklh" );
    f = false;
    try {
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  /**проверяем правильность выполнения операции Mod
   *
   */
  public void testMod(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable( 5 );
    var1.SetName("var1");
    variables.AddVariable( var1 );

    int res = 0;
    PascalParser parser = new PascalParser( variables );
    boolean f = false;
    try {
      parser.ParseScript("var1 := mod( var1, 3 );");
      parser.ExecuteScript();
      res = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 2 );

    var1.SetValue( 1 );
    f = false;
    try {
      parser.ParseScript("var1 := mod( var1, 3 );");
      parser.ExecuteScript();
      res = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 1 );

    var1.SetValue( 3 );
    f = false;
    try {
      parser.ParseScript("var1 := mod( var1, 3 );");
      parser.ExecuteScript();
      res = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 0 );

    var1.SetValue( 0 );
    f = false;
    try {
      parser.ParseScript("var1 := mod( var1, 3 );");
      parser.ExecuteScript();
      res = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 0 );
  }


  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  //Цикл тестов по проверке правильности функционирования оператора IF
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  public void testParseIf1()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(false);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    PascalParser parser = new PascalParser(  variables );
    boolean f = false;
    String s = "var1 := var2 and true;" +
               "var1 := true;" +
                "if (var1) then " +
                "begin var2 := false; end " +
                "else var2 := true;";

    try
    {
      parser.ParseScript( s );
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
       System.out.println(e.getMessage());
    }
    assertTrue( f );
    assertTrue( !var2.GetBooleanValue() );
  }

  /**Проверяется наиболее полная ситуация с ветвлением алгоритма: когда есть секции IF и ELSE, ограниченные
   * метками BEGIN END
   */
  public void testParseIf2()
  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable(true);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    Variable var3;
    var3 = new Variable( 10 );
    var3.SetName("var3");
    variables.AddVariable( var3 );

    PascalParser parser = new PascalParser(  variables );
    boolean f = false;
    String s = "var1 := var2 and true;" +
                "if (var1) then " +
                "begin var2 := false; end " +
                "else begin var2 := true; end; var3 := 18;";

    try
    {
      parser.ParseScript( s );
      parser.ExecuteScript();
      f = true;
    } catch (Exception e)
    {
       System.out.println(e.getMessage());
    }
    assertTrue( f );
    assertTrue( !var2.GetBooleanValue() );
    int i = 0;
    f = false;
    try {
      i = var3.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i, 18 );
  }

  public void testParseIf3()
   {
     VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable(false);
     var1.SetName("var1");
     variables.AddVariable( var1 );

     Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

     Variable var3 = new Variable( 5 );
     var3.SetName("var3");
     variables.AddVariable( var3 );

     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s = "var1 := var2 and true;" +
                 "if (var1) then " +
                 " var2 := false " +
                 "else begin var2 := true; end; var3 := 18";

     try
     {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f = true;
     } catch (Exception e)
     {
        System.out.println(e.getMessage());
     }
     assertTrue( f );
     assertTrue( !var2.GetBooleanValue() );

     int i = 0;
     f = false;
     try {
       i = var3.GetIntValue();
       f = true;
     } catch (ScriptException e) {
       e.printStackTrace();
     }
     assertTrue( f );
     assertEquals( i, 18 );
   }


  public void testParseIf4()
   {
     VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable(false);
     var1.SetName("var1");
     variables.AddVariable( var1 );

     Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s = "var1 := var2 and true;" +
                 "if (var1) then " +
                 "var2 := false " +
                 "else var2 := true;";

     try
     {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f = true;
     } catch (Exception e)
     {
        System.out.println(e.getMessage());
     }
     assertTrue( f );
    assertTrue( !var2.GetBooleanValue() );
   }

  /**Проверяется простейшая ситуация, когда есть только снкция IF, внутри которой всего один оператор
   *
   */
   public void testParseIf5()
   {
     VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable(false);
     var1.SetName("var1");
     variables.AddVariable( var1 );

     Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s = "var1 := var2 and true;" +
                 "if (var1) then " +
                 " var2 := false; "
             ;
     try
     {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f = true;
     } catch (Exception e)
     {
        System.out.println(e.getMessage());
     }
     assertTrue( f );
     assertTrue( !var2.GetBooleanValue() );
   }

  public void testParserIf5_1(){
    VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable( true );
     var1.SetName("var1");
     variables.AddVariable( var1 );

    Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

    Variable var3;
     var3 = new Variable( 2 );
     var3.SetName("var3");
     variables.AddVariable( var3 );


     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s = " if ( var2 and (not var1) and (var3 >= 0 ) ) then begin " +
                " var2 := ( var3 < 0 ); " +
                 "if (var1) then " +
                 " print( \"teststring\" ); " +
                 " end "
             ;
     try
     {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f = true;
     } catch (Exception e)
     {
        System.out.println(e.getMessage());
     }
     assertTrue( f );

  }

  /**Проверяется ошибочная ситуация, когда перед else ставится символ ";"
   *
   */
   public void testParseIf6()
   {
     VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable(false);
     var1.SetName("var1");
     variables.AddVariable( var1 );

     Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s = "var1 := var2 and true;" +
                 "if (var1) then " +
                 " var2 := false; " +
                 "else begin var2 := true; end; ";

     try
     {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f = true;
     } catch (Exception e)
     {
        System.out.println(e.getMessage());
     }
     assertTrue( !f );

   }

  /**Проверяется ошибочная ситуация секция IF содержит всего один оператор (без )
   *
   */
   public void testParseIf7() {
     VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable(false);
     var1.SetName("var1");
     variables.AddVariable( var1 );

     Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s = "var1 := var2 and true;" +
                 "if (var1) then " +
                 " var2 := false; " +
                 "var1 := true " +
                 "else begin var2 := true; end; ";

     try  {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f = true;
     } catch (Exception e) {
        System.out.println(e.getMessage());
     }
     assertTrue( !f );
   }

   public void testParseIf8()
   {
     VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable(false);
     var1.SetName("var1");
     variables.AddVariable( var1 );

     Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

     Variable var3;
     var3 = new Variable(12);
     var3.SetName("var3");
     variables.AddVariable( var3 );
     boolean f1 = false;

     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s =  "var3 := 2;" +
                 "var1 := var2 and true;" +
                 "if ( var3 = 3 ) then " +
                 " begin var2 := false; var3 := 7; end " +
                 "else begin var2 := true; var3 := 8; end; ";

     try
     {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f1 = var3.GetIntValue() == 8;
       f = true;
     } catch (Exception e)
     {
        System.out.println(e.getMessage());
     }
     assertTrue( f );
     assertTrue( f1 );
   }

   public void testParseIf9()
   {
     VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable(false);
     var1.SetName("var1");
     variables.AddVariable( var1 );

     Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

     Variable var3;
     var3 = new Variable(1);
     var3.SetName("var3");
     variables.AddVariable( var3 );

     int i = -1;
     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s = "var1 := true;" +
                 "if (var1) then " +
                 " begin var2 := false; " +
                 " var1 := true; " +
                 " if (var1) then var3 := 5 else var3 := 7;  " +
                 " end " +
                 " else begin var2 := true; end; ";

     try
     {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f = true;
       //f1 = var3.GetBooleanValue();
       i = var3.GetIntValue();
     } catch (Exception e)
     {
        System.out.println(e.getMessage());
     }
     assertTrue( f );
     assertEquals( i,5 );
   }

  public void testParseIf10()
   {
     VariableList variables = new VariableList();

     Variable var1;
     var1 = new Variable(false);
     var1.SetName("var1");
     variables.AddVariable( var1 );

     Variable var2;
     var2 = new Variable(true);
     var2.SetName("var2");
     variables.AddVariable( var2 );

     Variable var3;
     var3 = new Variable(1);
     var3.SetName("var3");
     variables.AddVariable( var3 );

     int i = -1;
     PascalParser parser = new PascalParser(  variables );
     boolean f = false;
     String s = "var1 := false;" +
                 "if (var1) then " +
                 " begin var2 := false; " +
                 " var1 := true; " +
                 " if (var1) then var3 := 5 else var3 := 7;  " +
                 " end " +
                 " else begin var2 := true;if (var2) then begin var3 := 8; end else var3 := 9;  end; ";

     try
     {
       parser.ParseScript( s );
       parser.ExecuteScript();
       f = true;
       //f1 = var3.GetBooleanValue();
       i = var3.GetIntValue();
     } catch (Exception e)
     {
        System.out.println(e.getMessage());
     }
     assertTrue( f );
     assertEquals( i,8 );
   }

  public void testParseIf11(){
    boolean f = false;
    ScriptLanguageExt ext = new ScriptLanguageExt();

    Variable passenger = new Variable(0.0);
    passenger.SetName("КоличествоПассажиров");

    Variable currentTime = new Variable(0.5);
    currentTime.SetName("currentTime");

    Variable firstPassengerTime = new Variable(0.0);
    firstPassengerTime.SetName("ВремяПоявленияПервогоПассажира");

    PascalParser parser = new PascalParser( );
    String s = " КоличествоПассажиров := КоличествоПассажиров + 1;" +
               " if ( КоличествоПассажиров <= 1 ) then" +
               " begin" +
               "   ВремяПоявленияПервогоПассажира := CurrentTime; " +
                "end;" +
                "КоличествоПассажиров := КоличествоПассажиров + 1;";
    try {
      ext.AddVariable( passenger );
      ext.AddVariable( currentTime );
      ext.AddVariable( firstPassengerTime );
      parser.SetLanguageExt( ext );
      parser.ParseScript( s );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    double d = 0;
    int i = 0;
    try {
      parser.ExecuteScript();
      d = firstPassengerTime.GetFloatValue();
      i = passenger.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    System.out.println( Double.toString(d) );
    assertTrue( d == 0.5 );
    assertEquals( i, 2 );
    f = false;
    try {
      parser.ExecuteScript();
      i = passenger.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals(i, 4);
  }

  public void testParseIf12(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable flag = new Variable(false);
    flag.SetName("startFlag");
    Variable position = new Variable(0);
    position.SetName("position");
    PascalParser parser = new PascalParser();
    boolean f = false;
    String s = "if ( startFlag ) then\n" +
            "               begin\n" +
            "                 position := position + 1;\n" +
            "                 if ( position >= 3000 ) then\n" +
            "                  begin\n" +
            "                    position := 0;\n" +
            "                    startFlag := false;\n" +
            "                  end;\n" +
            "               end;";
    try {
      ext.AddVariable( flag );
      ext.AddVariable( position );
      parser.SetLanguageExt( ext );
      parser.ParseScript( s );
      //parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }


  public void testParseIf13(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable flag = new Variable(false);
    flag.SetName("startFlag");
    Variable position = new Variable(0);
    position.SetName("position");
    PascalParser parser = new PascalParser();
    boolean f = false;
    String s = "if ( startFlag ) then\n" +
            "               begin\n" +
            "                 position := position + 1;\n" +
            "               end else \n" +
            "begin position := position + 2; end;";
    try {
      ext.AddVariable( flag );
      ext.AddVariable( position );
      parser.SetLanguageExt( ext );
      parser.ParseScript( s );
      //parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

  }


  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  //Цикл тестов по проверке правильности функционирования оператора WHILE
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  public void testWhile1()  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable( 1 );
    var2.SetName("i");
    variables.AddVariable( var2 );

    PascalParser parser = new PascalParser(  variables );
    boolean f = false;
    int i = -1;
    String s = "var1 := true;" +
            " while ( var1 ) do " +
            " begin "+
            " i := i + 1; " +
            " if i > 5 then var1 := false; " +
            " end";
    try{
      parser.ParseScript( s );
      parser.ExecuteScript();
      i = var2.GetIntValue();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue( f );
    assertEquals( i,6 );
  }

  public void testWhile2() {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable( 1 );
    var2.SetName("i");
    variables.AddVariable( var2 );

    PascalParser parser = new PascalParser(  variables );
    boolean f = false;
    int i = -1;
    String s = "var1 := true;" +
            " while ( i < 5 ) do " +
            " begin "+
            " i := i + 3; " +
            " end";
    try{
      parser.ParseScript( s );
      parser.ExecuteScript();
      i = var2.GetIntValue();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue( f );
    assertEquals( i,7 );
  }

  public void testWhile3()  {
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(false);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2;
    var2 = new Variable( 1 );
    var2.SetName("i");
    variables.AddVariable( var2 );

    PascalParser parser = new PascalParser(  variables );
    boolean f = false;
    int i = -1;
    String s = "var1 := true;" +
            " while ( i < 5 ) do " +
            "  "+
            " i := i + 3; " +
            " ";
    try{
      parser.ParseScript( s );
      parser.ExecuteScript();
      i = var2.GetIntValue();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue( f );
    assertEquals( i,7 );
  }

  /** Проверяется правильность работы цикла while, когда перед ним нет ни одного оператора.
   * Есть такое подозрения, что в таких случаях скрипт компилируется неверно.
   */
  public void testWhile4(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    Variable counter = new Variable( 0 );
    counter.SetName( "counter" );
    Variable scriptValue = new Variable( 0 );
    scriptValue.SetName( "scriptvalue" );
    boolean f = false;
    int res = 0;
    try {
      ext.AddVariable( counter );
      ext.AddVariable( scriptValue );
      parser.SetLanguageExt( ext );
      parser.ParseScript(" counter := 0; \n while ( counter < 5 ) do \n begin  " +
                         " scriptValue := scriptValue + counter; \n counter := counter + 1; \n" +
                        " end; \n " +
                        "counter := 0;");
      parser.ExecuteScript();
      res = scriptValue.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 10 );
  }

  /** Проверяется правильность работы while, когда он находится внутри оператора if
   *
   */
  public void testWhile5(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    Variable condition1 = new Variable(2.0);
    condition1.SetName("condition1");
    Variable condition2 = new Variable(3.0);
    condition2.SetName("condition2");
    Variable counter = new Variable(0);
    counter.SetName("counter");
    Variable result = new Variable(0);
    result.SetName( "result" );
    boolean f = false;
    try {
      ext.AddVariable( condition1 );
      ext.AddVariable( condition2 );
      ext.AddVariable( counter );
      ext.AddVariable( result );
      parser.SetLanguageExt( ext );
      parser.ParseScript( " if ( condition1 <> condition2 ) then \n" +
                          "  begin  \n" +
                          "    counter := 0; \n" +
                          "      while ( counter < 5 ) do \n" +
                          "       begin \n" +
                          "         result := result + counter * ( condition1 + condition2 ); \n" +
                          "         counter := counter +1; \n" +
                          "       end; \n" +
                          "  end; \n" );
      parser.ExecuteScript();
      assertEquals( result.GetIntValue(), 50 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  public void testIf11(){
    VariableList variables = new VariableList();

    Variable var1;
    var1 = new Variable(0);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable input1;
    input1 = new Variable( 0 );
    input1.SetName("input1");
    variables.AddVariable( input1 );
    int i1 = -1;
    PascalParser parser = new PascalParser(  variables );
    boolean f = false;
    int i = -1;
    String s = "var1 := var1 + 1 + input1; \n" +
            "            if (input1 > 0) then begin input1 := 0; end; ";
    try{
      parser.ParseScript( s );
      parser.ExecuteScript();
      i = input1.GetIntValue();
      i1 = var1.GetIntValue();
      f = true;
    } catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    assertTrue( f );
    assertEquals(i, 0);
    assertEquals( i1,1 );
  }

  /**проверяется правильность разбора выражений, в которых встречаются функции.
   * При этом функция встречается с сложном выражении. Сложном - это состоящем не из одной
   * операции приравнивания, а из более сложного выражения. Проверяется главным образом правильность определения
   * порядка выполнения операций.
   * При этом, если в выражении расставить скобки, оно точно будет выполняться правильно. Но это явно не наш метод
   *
   */
  public void testSofisticallyFunc1(){
    VariableList variables = new VariableList();

    Variable f;
    f = new Variable(false);
    f.SetName("f");
    variables.AddVariable(f);

    variables.AddVariable( f );
    PascalParser parser = new PascalParser( variables );
    String s = "f := 6 <= 2 * pi";

    boolean flag = false;
    try {
      parser.ParseScript( s );
      flag = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( flag );
  }

  /**Проверка правильности разбора выражения, в котором встречаются вложенные друг в друга функции.
   *
   */
  public void testSofisticallyFunc2(){
    VariableList variables = new VariableList();

    Variable f;
    f = new Variable(false);
    f.SetName("f");
    variables.AddVariable(f);

    variables.AddVariable( f );
    PascalParser parser = new PascalParser( variables );
    String s = "f := 6 <= sin( pi * 2 + 3) + cos( sin(4) + 5 )";

    boolean flag = false;
    try {
      parser.ParseScript( s );
      parser.ExecuteScript();
      flag = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( flag );

  }

  private static PascalParser GetParser1() throws ScriptException{
    PascalParser parser = null;
    VariableList variables = new VariableList();
    Variable var1 = new Variable(0);
    var1.SetName("var1");
    variables.AddVariable( var1 );

    Variable var2 = new Variable(0);
    var2.SetName("var2");
    variables.AddVariable( var2 );

    parser = new PascalParser( variables );
    parser.ParseScript("var1 := var2 +  5 + 10;");

    return parser;
  }

  public void testParserFirstOperation(){
    boolean f = false;
    PascalParser parser = null;
    try {
      parser = GetParser1();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    Variable testVar1 = (Variable) parser.First("mp.parser.Variable");
    assertTrue(f);
    assertTrue( testVar1!= null );
    ScriptProgramObject op1 = parser.First("mp.parser.ScriptOperationAdd");
    assertTrue( op1 != null );
    op1 = parser.First("mp.parser.ScriptOperationMul");
    assertTrue( op1 == null );
  }

  public void testFindNotServiceVar(){
    boolean f = false;
    PascalParser parser = null;
    try {
      parser = GetParser1();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    Variable var1 = null;
    Variable var2 = null;
    Variable var = null;
    var = (Variable)parser.First("mp.parser.Variable");
    boolean movResultVar1 = false;
    boolean movResultVar2 = false;
    while ( var != null ){
      if ( var1 == null ){
        if ( "var1".equalsIgnoreCase(var.GetName()) ){
          var1 = var;
          movResultVar1 = parser.IsMovResult();
        }
      }
      if (var2 == null) {
        if ( "var2".equalsIgnoreCase(var.GetName()) ){
          var2 = var;
          movResultVar2 = parser.IsMovResult();
        }
      }
      var = (Variable)parser.Next("mp.parser.Variable");
    }
    assertTrue( var1 != null );
    assertTrue( var2 != null );
    //проверка, является ли var1 результатом операции Mov
    assertTrue( movResultVar1 );
    assertTrue( !movResultVar2 );
  }

  private boolean changeFlag = false;
  private boolean changeFlag2 = false;

  public void testVariableChanging_Int(){
    Variable var = new Variable(1);

    ChangeListener listener = new ChangeListener() {
      public void VariableChanged(VariableChangeEvent changeEvent) {
        changeFlag = true;
      }
    };

    changeFlag = false;
    var.AddChangeListener( listener );
    var.SetValue( 3 );
    assertTrue( changeFlag );

    changeFlag = false;
    var.SetValue(3);
    assertTrue( !changeFlag );

    var.SetValue(true);
    assertTrue( changeFlag );

  }

  public void testVariableChanging_Double(){
    double d1 = 0;
    Variable var = new Variable(d1);

    ChangeListener listener = new ChangeListener() {
      public void VariableChanged(VariableChangeEvent changeEvent) {
        changeFlag = true;
      }
    };

    changeFlag = false;
    var.AddChangeListener( listener );

    d1 = 3;
    var.SetValue(d1 );
    assertTrue( changeFlag );

    changeFlag = false;
    var.SetValue( d1 );
    assertTrue( !changeFlag );

    var.SetValue(true);
    assertTrue( changeFlag );

  }

  public void testVariableChanging_Boolean(){
    Variable var = new Variable( true );

    ChangeListener listener = new ChangeListener() {
      public void VariableChanged(VariableChangeEvent changeEvent) {
        changeFlag = true;
      }
    };

    changeFlag = false;
    var.AddChangeListener( listener );
    var.SetValue( false );
    assertTrue( changeFlag );

    changeFlag = false;
    var.SetValue( false );
    assertTrue( !changeFlag );

    var.SetValue( 12 );
    assertTrue( changeFlag );

  }

  /**Метод производит проверку правильности функционирования метода удаления "слушателя изменений" из класса Variable.
   * Для этого создается объект Variable и два "слушателя изменений", каждый из которых изменяет свою переменную
   * (переменные различаются). Для проверки производится изменение значения в созданной Variable. Обе переменные должны
   * изменить свои значения.
   * Затем один из "слушателей изменений" удаляется и снова производится изменение в Variable. Изменить свое значение
   * должна только одна переменная
   *
   */
  public void testRemoveChangeListener(){
    Variable var1 = new Variable(0);
    changeFlag = false;
    changeFlag2 = false;
    ChangeListener flag1Listener = new ChangeListener( var1 ) {
      public void VariableChanged(VariableChangeEvent changeEvent) {
        changeFlag = true;
      }
    };
    ChangeListener flag2Listener = new ChangeListener( this ) {
      public void VariableChanged(VariableChangeEvent changeEvent) {
        changeFlag2 = true;
      }
    };
    var1.AddChangeListener( flag1Listener );
    var1.AddChangeListener( flag2Listener );
    var1.SetValue( 2 );
    assertTrue( changeFlag );
    assertTrue( changeFlag2 );
    changeFlag = false;
    changeFlag2 = false;

    var1.RemoveChangeListener( this );
    var1.SetValue( 3 );
    assertTrue( changeFlag );
    assertTrue( !changeFlag2 );
    changeFlag = false;
    changeFlag2 = false;

    var1.RemoveChangeListener( var1 );
    var1.SetValue( 4 );
    assertTrue( !changeFlag );
    assertTrue( !changeFlag2 );
  }

  /**Проверяется работа функции rnd (генерации случайного числа)
   *
   */
  public void testRnd(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable position = new Variable(0);
    position.SetName("position");
    PascalParser parser = new PascalParser();
    boolean f = false;
    String s = " position := rnd; ";
    try {
      ext.AddVariable( position );
      parser.SetLanguageExt( ext );
      parser.ParseScript( s );
      //parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  public void testSqr(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable var1 = new Variable(1);
    var1.SetName("herbCount");
    boolean f = false;
    PascalParser parser = new PascalParser();
    int v1 = 0;
    int v2 = 0;
    try {
      ext.AddVariable( var1 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "herbCount := sqr( 3 );" );
      v1 = var1.GetIntValue();
      parser.ExecuteScript();
      v2 = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( v1, 1 );
    assertEquals( v2, 9 );
  }

  /**Проверяется правильность определения порядка свертки лексем в случае, когда за менее приоритетной операцией
   * идет более приоритетная операция, но она присутствует в скрипте не в явном виде
   *
   */
  public void testExecOrder_WithBrackets(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable var1 = new Variable(1.0);
    var1.SetName("herbCount");
    boolean f = false;
    PascalParser parser = new PascalParser();
    double v1 = 0;
    double v2 = 0;
    try {
      ext.AddVariable( var1 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "herbCount := herbCount +   1 /  (herbCount * 2)  ;" );
      parser.ExecuteScript();
      v1 = var1.GetFloatValue();
      parser.ExecuteScript();
      v2 = var1.GetFloatValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( v2 > v1 );
  }

  public void testExecOrder_WithBrackets2(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable var1 = new Variable(1);
    var1.SetName("herbCount");
    boolean f = false;
    PascalParser parser = new PascalParser();
    int v1 = 0;
    int v2 = 0;
    try {
      ext.AddVariable( var1 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "herbCount := herbCount + 2 + (herbCount * 2) ;" );
      v1 = var1.GetIntValue();
      parser.ExecuteScript();
      v2 = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( v1, 1 );
    assertEquals( v2, 5 );
  }

  public void testInnerElse(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable var1 = new Variable(1.0);
    var1.SetName("herbCount");
    Variable colour = new Variable(0);
    colour.SetName("colour");
    boolean f = false;
    PascalParser parser = new PascalParser();
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( colour );
      parser.SetLanguageExt( ext );
      parser.ParseScript("if ( herbCount < 2 ) then\n" +
              "                colour := 9000 else\n" +
              "                begin\n" +
              "                 if ( herbCount < 5 ) then\n" +
              "                   colour := (0-15711936) else\n" +
              "                   begin\n" +
              "                     colour := (0-16711936);\n" +
              "                   end;\n" +
              "                 end;");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  public void testManyAND(){
    Variable width = new Variable(5.0);
    width.SetName("width");
    Variable cowY = new Variable( 47.0 );
    cowY.SetName("cowY");
    Variable height = new Variable(5.0);
    height.SetName("height");
    Variable cowX = new Variable(1.1);
    cowX.SetName("cowX");
    Variable y = new Variable(19.0);
    y.SetName("y");
    Variable x = new Variable(19.0);
    x.SetName("x");
    Variable enable = new Variable( true );
    enable.SetName("enable");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    boolean result = false;
    try {
      ext.AddVariable( width );
      ext.AddVariable( cowY );
      ext.AddVariable( height );
      ext.AddVariable( cowX );
      ext.AddVariable( y );
      ext.AddVariable( x );
      ext.AddVariable( enable );
      parser.SetLanguageExt( ext );
      parser.ParseScript("if ( (cowX > x) and ( cowX < (x + width) )  and (cowY > y) and ( cowY < (cowY + height) ) ) then\n" +
              "            enable := true else\n" +
              "            enable := false;");
      parser.ExecuteScript();
      result = enable.GetBooleanValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( !result );
  }

  //проверка операций с типом string

  public void testCreateStringVar(){
    Variable stringVar = new Variable("test value");
    boolean f = false;
    String s = "";
    try {
      s = stringVar.GetStringValue();
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( "test value".equalsIgnoreCase( s ) );
  }

  /**тестируется операция приравнивания с типом string
   *
   */
  public void testMoveString(){
    Variable stringVar = new Variable("empty");
    stringVar.SetName("var1");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      ext.AddVariable( stringVar );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      parser.ParseScript(" var1 := \"parsed string\" ");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String s = "";
    try {
      s = stringVar.GetStringValue();
      System.out.println( s );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( "parsed string".equalsIgnoreCase( s ) );
  }

  public void testAddString(){
    Variable stringVar = new Variable("var1value");
    stringVar.SetName("var1");
    Variable stringVar2 = new Variable("var2value");
    stringVar2.SetName("var2");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      ext.AddVariable( stringVar );
      ext.AddVariable( stringVar2 );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      parser.ParseScript(" var1 := var1 + var2 ");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String s = "";
    try {
      s = stringVar.GetStringValue();
      System.out.println( s );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( "var1valuevar2value".equalsIgnoreCase( s ) );

  }

  public void testAddConstantString(){
    Variable stringVar = new Variable("var1value");
    stringVar.SetName("var1");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      ext.AddVariable( stringVar );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      parser.ParseScript(" var1 := var1 + \"constant value\" ");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    String s = "";
    try {
      s = stringVar.GetStringValue();
      System.out.println( s );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( "var1valueconstant value".equalsIgnoreCase( s ) );
  }

  public void testWrongStringOperations(){
    Variable stringVar = new Variable("var1value");
    stringVar.SetName("var1");
    Variable stringVar2 = new Variable("var2value");
    stringVar2.SetName("var2");
    Variable intVar = new Variable(1);
    intVar.SetName( "var3" );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      ext.AddVariable( stringVar );
      ext.AddVariable( stringVar2 );
      ext.AddVariable( intVar );
      parser.SetLanguageExt( ext );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      parser.ParseScript(" var1 := var1 - var2 ");
      parser.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );

    parser = new PascalParser();
    try {
      parser.SetLanguageExt( ext );
      parser.ParseScript("var1 := var1 + var3");
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
   assertTrue( !f );
  }

  /**Проверяется правильность обработки процедуры, в которую передается несколько параметров
   * Например: Send("Модель2", "block1", 0, "var2", 500);
   * Проверяется только разбор данной конструкции, поскольку из данного контекста вызвать эту процедуру невозможно
   */
  public void testMultyOperandProcedureCall(){
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
      parser.ParseScript(" Send(\"Модель2\", \"block1\", 0, \"var2\", 500); ");
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  public void testVar(){
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable var1 = new Variable(1);
    var1.SetName("var1");

    Variable var2 = new Variable(2);
    var2.SetName("var2");

    boolean f = false;
    PascalParser parser = new PascalParser();
    int v1 = 0;
    int v2 = 0;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "[var1] := [var2];" );
      v1 = var1.GetIntValue();
      parser.ExecuteScript();
      v2 = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //assertEquals( v1, 1 );
    //assertEquals( v2, 9 );
  }

  public void testExitCommand(){
  	ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable var1 = new Variable(1);
    var1.SetName("var1");

    Variable var2 = new Variable(2);
    var2.SetName("var2");

    boolean f = false;
    PascalParser parser = new PascalParser();
    int v1 = 0;
    int v2 = 0;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "var1 := 100; exit; var2 := 200;" );
      parser.ExecuteScript();
      v1 = var1.GetIntValue();
      parser.ExecuteScript();
      v2 = var2.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals(v1, 100);
    assertEquals(v2, 2);
  }
  
  public void testVarSection(){
  	boolean f = false;
    PascalParser parser = new PascalParser();
    ScriptLanguageExt ext = new ScriptLanguageExt();
    try {
    	parser.SetLanguageExt( ext );
			parser.ParseScript( "var var1 : integer; var1 := 100; " );
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    assertTrue( ext.GetVariables().GetVariable("var1") != null);
    f = false;
    try {
			parser.ExecuteScript();
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    f = false;
    try {
			assertEquals( ext.GetVariables().GetVariable("var1").GetIntValue() , 100);
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		} 
    assertTrue(f);  	
  }
  
  public void testVarSection_2Vars(){
  	boolean f = false;
    PascalParser parser = new PascalParser();
    ScriptLanguageExt ext = new ScriptLanguageExt();
    try {
    	parser.SetLanguageExt( ext );
			parser.ParseScript( "var var1, var2 : integer; var1 := 101; var2 := var1 + 3;" );
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    assertTrue( ext.GetVariables().GetVariable("var1") != null);
    assertTrue( ext.GetVariables().GetVariable("var2") != null);
    f = false;
    try {
			parser.ExecuteScript();
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    f = false;
    try {
			assertEquals( ext.GetVariables().GetVariable("var1").GetIntValue() , 101);
			assertEquals( ext.GetVariables().GetVariable("var2").GetIntValue() , 104);
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		} 
    assertTrue(f);  	
  }
  
  public void testVarSection_2Sect(){
  	boolean f = false;
    PascalParser parser = new PascalParser();
    ScriptLanguageExt ext = new ScriptLanguageExt();
    try {
    	parser.SetLanguageExt( ext );
    	parser.ParseScript( "var var1 : integer; var var3: real;  var1 := 100; var3 := 9;" );
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    f = false;
    try {
			parser.ExecuteScript();
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    f = false;
    try {			
			assertEquals( ext.GetVariables().GetVariable("var3").GetIntValue() , 9);
			assertEquals( ext.GetVariables().GetVariable("var1").GetIntValue() , 100);
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		} 
    assertTrue(f);
  	
  }
  
  public void testVarSection_2Variables(){
  	boolean f = false;
    PascalParser parser = new PascalParser();
    ScriptLanguageExt ext = new ScriptLanguageExt();
    try {
    	parser.SetLanguageExt( ext );
    	parser.ParseScript( "var var1, var3 : integer; var1 := 100; var3 := 9;" );
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    f = false;
    try {
			parser.ExecuteScript();
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    f = false;
    try {			
			assertEquals( ext.GetVariables().GetVariable("var3").GetIntValue() , 9);
			assertEquals( ext.GetVariables().GetVariable("var1").GetIntValue() , 100);
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		} 
    assertTrue(f);
  }
  
  public void testVarSection_Error1(){
  	boolean f = false;
    PascalParser parser = new PascalParser();
    ScriptLanguageExt ext = new ScriptLanguageExt();
    try {
    	parser.SetLanguageExt( ext );
    	parser.ParseScript( "var var1, var3  integer; var1 := 100; var3 := 9;" );
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(!f);
   }
  
  public void testVarSection_Error2(){
  	boolean f = false;
    PascalParser parser = new PascalParser();
    ScriptLanguageExt ext = new ScriptLanguageExt();
    try {
    	parser.SetLanguageExt( ext );
    	parser.ParseScript( "var var1, var3 : integer var1 := 100; var3 := 9;" );
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();			
		}
    assertTrue(!f);
   }




}

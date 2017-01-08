package mp.parser;

import junit.framework.TestCase;

/**
 * User: atsv
 * Date: 06.09.2007
 */
public class TestNotExecParser extends TestCase {

  public TestNotExecParser(String testName) {
    super(testName);
  }

  public void testCreateNotExecParser(){
    Variable var1 = new Variable(1);
    var1.SetName("var1");
    Variable var2 = new Variable(2);
    var2.SetName("var2");

    Variable var3 = new Variable(1);
    var3.SetName("var1");
    Variable var4 = new Variable(2);
    var4.SetName("var2");

    ScriptLanguageExt ext = new ScriptLanguageExt();
    ScriptLanguageExt ext2 = new ScriptLanguageExt();
    PascalParser parser = new PascalParser( );
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      ext2.AddVariable( var3 );
      ext2.AddVariable( var4 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "var1 := var2 + 1" );
      new NotExecutiveParser(parser, ext2);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();  
    }
    assertTrue( f );
  }

  /** Проверяется правильность создания неисполняемого парсера. Этот парсер создается с ошибкой - в него передается
   * список переменных, тип одной из которых не совпадает с типом переменной, имеющейся в парсере
   *
   */
  public void testCreateNotExecParser_WithError_Types(){
    Variable var1 = new Variable(1);
    var1.SetName("var1");
    Variable var2 = new Variable(2);
    var2.SetName("var2");
    VariableList list = new VariableList();
    list.AddVariable( var1 );
    list.AddVariable( var2 );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser( );
    ScriptLanguageExt ext2 = new ScriptLanguageExt();
    Variable var3 = new Variable(true);
    var3.SetName("var1");
    Variable var4 = new Variable(1);
    var4.SetName("var1");
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      ext2.AddVariable( var3 );
      ext2.AddVariable( var4 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "var1 := var2 + 1" );
      new NotExecutiveParser(parser, ext2);
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  public void testCreateNotExecParser_ErrorNotEqualsCount(){
    Variable var1 = new Variable(1);
    var1.SetName("var1");
    Variable var2 = new Variable(2);
    var2.SetName("var2");
    VariableList list = new VariableList();
    list.AddVariable( var1 );
    list.AddVariable( var2 );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser( );
    ScriptLanguageExt ext2 = new ScriptLanguageExt();
    Variable var3 = new Variable(1);
    var3.SetName("var1");
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      ext2.AddVariable( var3 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "var1 := var2 + 1" );
      new NotExecutiveParser(parser, ext2);
      f = true;
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );

  }

  public void testCreateNotExecParser_NotEqualsCount(){
    Variable var1 = new Variable(1);
    var1.SetName("var1");
    Variable var2 = new Variable(2);
    var2.SetName("var2");
    VariableList list = new VariableList();
    list.AddVariable( var1 );
    list.AddVariable( var2 );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser( );
    ScriptLanguageExt ext2 = new ScriptLanguageExt();
    Variable var3 = new Variable(1);
    var3.SetName("var1");
    Variable var4 = new Variable(2);
    var4.SetName("var2");
    Variable var5 = new Variable(true);
    var5.SetName("var5");
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      ext2.AddVariable( var3 );
      ext2.AddVariable( var4 );
      ext2.AddVariable( var5 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "var1 := var2 + 1" );
      new NotExecutiveParser(parser, ext2);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  /**Проверяется правильность создания списка используемых переменных. Этот список  создается неисполняемым парсером
   *
   */
  public void testCreateUsedVarsList(){
    Variable var1 = new Variable(1);
    var1.SetName("var1");
    Variable var2 = new Variable(2);
    var2.SetName("var2");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser( );
    ScriptLanguageExt ext2 = new ScriptLanguageExt();
    Variable var3 = new Variable(1);
    var3.SetName("var1");
    Variable var4 = new Variable(2);
    var4.SetName("var2");
    Variable var5 = new Variable(true);
    var5.SetName("var5");
    boolean f = false;
    NotExecutiveParser parser2 = null;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      ext2.AddVariable( var3 );
      ext2.AddVariable( var4 );
      ext2.AddVariable( var5 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "var1 := var2 + 1" );
      parser2 = new NotExecutiveParser(parser, ext2);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int[] list = parser2.GetUsedVars();
    assertEquals( list.length, 2 );
    VariableList varList = ext2.GetVariables();
    int index = varList.IndexOf( var5 );
    int i = 0;
    //проверяем, что ссылка на var5 отсутствует в списке используемых переменных
    while ( i < list.length ){
      assertTrue( index != list[i] );
      i++;
    }
    // проверяем, что в списке используемых переменных имеются ссылки на var3 и var4
    assertTrue( list[0] != list[1] );
  }

  /**Проверяется правильность выполнения скрипта неисполняющим парсером. Т.е. вся его работа в совокупности: загрузка
   * в парсер своих переменных, выполнение парсера и выгрузка из парсера расчитанных значений  
   * 
   */
  public void testExecute(){
    Variable var1 = new Variable(1);
    var1.SetName("var1");
    Variable var2 = new Variable(2);
    var2.SetName("var2");
    VariableList list = new VariableList();
    list.AddVariable( var1 );
    list.AddVariable( var2 );
    ScriptLanguageExt ext = new ScriptLanguageExt();
    PascalParser parser = new PascalParser( );
    ScriptLanguageExt ext2 = new ScriptLanguageExt();
    Variable var3 = new Variable(111);
    var3.SetName("var1");
    Variable var4 = new Variable(5);
    var4.SetName("var2");
    boolean f = false;
    NotExecutiveParser parser2;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      ext2.AddVariable( var3 );
      ext2.AddVariable( var4 );
      parser.SetLanguageExt( ext );
      parser.ParseScript( "var1 := var2 + 1" );
      parser2 = new NotExecutiveParser(parser, ext2);
      parser2.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int i1 = 0;
    f = false;
    try {
      i1 = var3.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals(i1, 6);
  }

  private static ScriptLanguageExt GetLanguageExt() throws ScriptException {
    ScriptLanguageExt result = new ScriptLanguageExt();
    Variable var = new Variable(1);
    var.SetName("var1");
    result.AddVariable( var );
    var = new Variable(2);
    var.SetName("var2");
    result.AddVariable( var );
    var = new Variable(3);
    var.SetName("var3");
    result.AddVariable( var );
    return result;
  }

  /**Тестируется правильность создания исполняемого парсера классом ParserFactory
   *
   */
  public void testCreateExecParserByFactory(){
    boolean f = false;
    ScriptLanguageExt ext = null;
    ScriptParser parser = null;
    try {
      ext = GetLanguageExt();
      parser = ParserFactory.GetParser( ext, "var1 := var2 + var3" );
      f = true;
    } catch (ScriptException e) {
      ParserFactory.ClearParserList();
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( parser instanceof PascalParser );
    f = false;
    ScriptParser parser2 = null;
    try {
      ext = GetLanguageExt();
      parser2 = ParserFactory.GetParser( ext, "var2 := var1 + var3" );
      f = true;
    } catch (ScriptException e) {
      ParserFactory.ClearParserList();
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( parser2 instanceof PascalParser );
    assertTrue( parser != parser2 );
    ParserFactory.ClearParserList();
  }

  public void testCreateNotExecParserByFactory(){
    boolean f = false;
    ScriptParser execParser = null;
    ScriptParser notExecParser = null;
    ScriptLanguageExt parserExt = null;
    ScriptLanguageExt notExecExt = null;
    try {
      parserExt = GetLanguageExt();
      execParser = ParserFactory.GetParser( parserExt, "var2 := var1 + var3" );
      f = true;
    } catch (ScriptException e) {
      ParserFactory.ClearParserList();
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( execParser instanceof PascalParser );
    f = false;
    try {
      notExecExt = GetLanguageExt();
      notExecParser = ParserFactory.GetParser( notExecExt, "var2 := var1 + var3" );
      f = true;
    } catch (ScriptException e) {
      ParserFactory.ClearParserList();
      e.printStackTrace();  
    }
    assertTrue( f );
    assertTrue( notExecParser instanceof NotExecutiveParser );

    f = false;
    int parserVar2 = 0;
    int noParserVar2 = 0;
    try {
      notExecParser.ExecuteScript();
      parserVar2 = parserExt.Get("var2").GetIntValue();
      noParserVar2 = notExecExt.Get("var2").GetIntValue(); 
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();  
    }
    assertTrue(f);
    assertEquals( noParserVar2, 4 );
    assertEquals( parserVar2, 2 );
    f = false;
    try {
      execParser.ExecuteScript();
      parserVar2 = parserExt.Get("var2").GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(  f);
    assertEquals( parserVar2, 4 );
    
    ParserFactory.ClearParserList();
  }

}

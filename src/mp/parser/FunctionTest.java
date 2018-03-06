package mp.parser;

import mp.elements.ModelFunction;
import junit.framework.TestCase;

public class FunctionTest extends TestCase {


	public FunctionTest( String name ){
    super(name);
  }


	/**
	 * Простейший тест функции:
	 * - создается функция, возвращающая константное значение
	 * - вручную создается блок с одной переменной
	 * - в переменной определяется код - переменной присваивается значение, возвращаемой функцией
	 */
	public void test(){
    Variable var1 = new Variable(0);
    var1.SetName("var1");
    ScriptOperationUserFunction fun = new ScriptOperationUserFunction();
    fun.SetName("fun1");
    ScriptLanguageExt varExt = new ScriptLanguageExt();
    ScriptLanguageExt funExt = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;
    try {
	    varExt.AddVariable(var1);
	    fun.SetResultInfo("integer");
	    varExt.AddFuction(fun );


	    fun.SetExternalLanguageExt(funExt);
	    parser.SetLanguageExt( varExt );
	    f = true;
    } catch (Exception e1) {
	    e1.printStackTrace();
    }
    assertTrue(f);
    f = false;
    try {
	    fun.SetSourceCode("fun1 := 16");
	    f = true;
    } catch (Exception e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    f = false;
    try {
	    parser.ParseScript(" var1 := fun1; ");
	    parser.ExecuteScript();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    int val = 0;
    try {
	    val = var1.GetIntValue();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertEquals(val, 16);
	}

	/**
	 * Проверяем функцию со входными параметрами
	 *
	 */
	public void testFuncInpParams(){
		Variable var1 = new Variable(0);
    var1.SetName("var1");
    Variable var2 = new Variable(1);
    var2.SetName("var2");
    ScriptOperationUserFunction fun = new ScriptOperationUserFunction();
    fun.SetName("Inc");
    ScriptLanguageExt varExt = new ScriptLanguageExt();
    ScriptLanguageExt funExt = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;

    try {
	    varExt.AddVariable(var1);
	    varExt.AddVariable(var2);
	    varExt.AddFuction(fun);

	    funExt.AddVariable(var1);
	    funExt.AddVariable(var2);

	    fun.SetResultInfo("integer");

	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    f = false;
    try {
    	fun.SetExternalLanguageExt(funExt);
    	fun.AddInpVar(0,  "inpVar", "integer");
	    fun.SetSourceCode("inc := inpvar + 1;");

	    parser.SetLanguageExt(varExt);
	    f = true;
    } catch (Exception e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    f = false;
    try {
	    parser.ParseScript(" var1 := inc(var2); ");
	    parser.ExecuteScript();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);

    int val = 0;
    try {
	    val = var1.GetIntValue();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertEquals(val, 2);

	}

	/**
	 * Проверка ситуации, когда в функции используются и входные параметры, и переменные блока, т.е. глобальные по тношению к функции
	 */
	public void testFunWithGlobalVar(){
		Variable var1 = new Variable(0);
    var1.SetName("var1");
    Variable var2 = new Variable(99);
    var2.SetName("var2");
    Variable var3 = new Variable(7);
    var3.SetName("var3");

    ScriptOperationUserFunction fun = new ScriptOperationUserFunction();
    fun.SetName("TestSumm");

    ScriptLanguageExt varExt = new ScriptLanguageExt();
    ScriptLanguageExt funExt = new ScriptLanguageExt();
    PascalParser parser = new PascalParser();
    boolean f = false;

    try {
	    varExt.AddVariable(var1);
	    varExt.AddVariable(var2);
	    varExt.AddVariable(var3);
	    varExt.AddFuction(fun);

	    funExt.AddVariable(var1);
	    funExt.AddVariable(var2);
	    funExt.AddVariable(var3);

	    fun.SetResultInfo("integer");

	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    f = false;

    try {
    	fun.SetExternalLanguageExt(funExt);
    	fun.AddInpVar(0,  "inpVar", "integer");
	    fun.SetSourceCode("testSumm := inpvar + 1 + var3;");

	    parser.SetLanguageExt(varExt);
	    f = true;
    } catch (Exception e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    f = false;

    try {
	    parser.ParseScript(" var1 := testSumm(var2); ");
	    parser.ExecuteScript();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);

    int val = 0;
    try {
	    val = var1.GetIntValue();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertEquals(val, 107);
	}
	
	public void testExternalFunc_Dump(){
		boolean f = false;
		try {
			Class.forName("mp.elements.functions.FunctionsLoader");
			f = true;
		} catch (ClassNotFoundException e1) {
			
			e1.printStackTrace();
		}
		assertTrue(f);
		f = false;
		Variable var1 = new Variable(false);
    var1.SetName("dumpFlag");
    Variable intVar = new Variable(0);
    intVar.SetName("intVar");
    ScriptLanguageExt varExt = new ScriptLanguageExt();
		try {
			varExt.AddVariable(var1);
			varExt.AddVariable(intVar);			
			ScriptParser parser = ParserFactory.GetParser(varExt, " dumpFlag := dump; intVar := 15;");	    
	    parser.ExecuteScript();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    try {
    	f = false;
			assertEquals( intVar.GetIntValue(), 15 );
			assertTrue( var1.GetBooleanValue() );
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
    assertTrue(f);
    
		
	}


}

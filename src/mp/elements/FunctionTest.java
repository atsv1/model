package mp.elements;

import java.io.IOException;


import mp.parser.ParserFactory;
import mp.parser.ScriptException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class FunctionTest extends TestCase {

	public FunctionTest( String name ){
    super(name);
  }

	private static int GetIntValue(Model model, int blockIndex, String varName) throws ModelException, ScriptException{
		ModelBlock block = null;
    ModelBlockParam param = null;
    block = (ModelBlock) model.GetByIndex(blockIndex);
    param = (ModelBlockParam) block.Get(varName);
    int val = param.GetVariable().GetIntValue();
		return val;
	}


	public void testConstantFunction() {
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func1.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (Exception e) {
    	e.printStackTrace();
    }
    assertTrue(f);

    int val = -1;
    f = false;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 13);
	}

	public void testFunctionWithParams(){
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func2.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (Exception e) {
    	e.printStackTrace();
    }
    assertTrue(f);
    int val = -1;
    f = false;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 5);
	}

	/**
	 * Проверяем цепочку функций, когда одна функция вызывается из другой
	 */
	public void testFunctionChain(){
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func3.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    int val = -1;
    f = false;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 14);
	}

	/**
	 * Проверяем использование констант в функции.
	 */
	public void testConstInFunction(){
		ParserFactory.ClearParserList();
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func4.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    int val = -1;
    f = false;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 104);
	}

	/**
	 * проверяем функцию, которая использует свои внутренние переменные
	 */
	public void testFuntWithLocalVar(){
		ParserFactory.ClearParserList();
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func5.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    f = false;
    int val = -1;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ModelException e) {
	    e.printStackTrace();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 18);
	}

	/**
	 * Проверяем функцию, которая в качестве параметра принимает массив.
	 */
	public void testFuncWithArray(){
		ParserFactory.ClearParserList();
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func6.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);

    f = false;
    int val = -1;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ModelException e) {
	    e.printStackTrace();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 10);
	}

	/**
	 * Проверяем, как функция умеет изменять значения внутри массива. Массив должен измениться
	 */
	public void testChangeArray(){
		ParserFactory.ClearParserList();
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func7.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);

    f = false;
    int val = -1;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ModelException e) {
	    e.printStackTrace();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 35);
	}

	/**
	 * Проверяем вызов функции, когда при вызове используются значения массива
	 */
	public void testCallFunWith2ArrayParams(){
		ParserFactory.ClearParserList();
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func8.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);

    f = false;
    int val = -1;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ModelException e) {
	    e.printStackTrace();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 6);
	}

	public void testCompareArrayValues(){
		ParserFactory.ClearParserList();
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func9.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);

    f = false;
    int val = -1;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ModelException e) {
	    e.printStackTrace();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 3);

	}

	public void testCompareArrayValues_WithOR(){
		ParserFactory.ClearParserList();
		boolean f = false;
		Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "func10.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    assertTrue( f );
    f = false;
    try {
	    model.Execute();
	    f = true;
    } catch (ScriptException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
    assertTrue(f);

    f = false;
    int val = -1;
    try {
	    val = GetIntValue(model, 0, "var");
	    f = true;
    } catch (ModelException e) {
	    e.printStackTrace();
    } catch (ScriptException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 2);

	}


}

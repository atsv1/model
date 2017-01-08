package mp.elements;

import java.io.IOException;

import org.xml.sax.SAXException;

import mp.parser.ExecutionContext;
import mp.parser.ExecutionContextLocator;
import mp.parser.ScriptException;
import mp.parser.Variable;
import junit.framework.TestCase;

public class TestVariableChangeHistory extends TestCase {

	private static int GetIntValue(Model model, int blockIndex, String varName) throws ModelException, ScriptException{
		ModelBlock block = null;
    ModelBlockParam param = null;
    block = (ModelBlock) model.GetByIndex(blockIndex);
    param = (ModelBlockParam) block.Get(varName);
    int val = param.GetVariable().GetIntValue();
		return val;
	}

	private static ModelBlockParam GetParam(Model model, int blockIndex, String varName) throws ModelException, ScriptException{
		ModelBlock block = null;
    block = (ModelBlock) model.GetByIndex(blockIndex);
    return  (ModelBlockParam) block.Get(varName);
	}

	private static int GetIntValue(ModelBlockParam param  ){
		try {
	    return param.GetVariable().GetIntValue();
    } catch (ScriptException e) {
	    return -99;
    }
	}


	 public TestVariableChangeHistory( String name ){
	    super( name );
	  }

	 /**
	  * Простой тест создания переменной, для которой создается слушатель, сохраняющий историю
	  */
	 public void testCreateVarWithHistory(){
		 Variable var = null;
		 boolean f = false;
		 ValueChangeListener changeListener = new ValueChangeListener(  );
		 ExecutionContext cont = new ExecutionContext("test");
		 ExecutionContextLocator.setContect(cont);
			try {
	      var = Variable.CreateNewInstance("var", "integer", "0");
	      var.AddChangeListener(changeListener);
	      f = true;
      } catch (ScriptException e) {
	      e.printStackTrace();
      }
			assertTrue(f);
			assertEquals( changeListener.GetHistoryRecordCount(), 0 );
			var.SetValue(1);
			assertEquals( changeListener.GetHistoryRecordCount(), 1 );
			var.SetValue(2);
			assertEquals( changeListener.GetHistoryRecordCount(), 2 );
	 }

	 public void testCreateParamWithHistory(){
		 mp.parser.ModelExecutionContext.ClearExecutionContext();
		 boolean f = false;
			Model model = null;
	    try {
	      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "varHistory.xml");
	      f = true;
	    } catch (ModelException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    } catch (SAXException e) {
	      e.printStackTrace();
	    }
	    assertTrue( f );
	    ModelBlockParam param = null;
	    f = false;
	    try {
	      param = GetParam(model, 0, "param1");
	      f = true;
      } catch (Exception e) {
	      e.printStackTrace();
      }
	    assertTrue(f);
	    assertTrue( param != null );
	    assertTrue(param.IsHistoryExists());
	 }

	 /**
	  * Проверка того, что история изменений сохраняется, при изменении значения переменной в материальном параметре
	  */
	 public void testSaveHistInMaterialParam(){
		 mp.parser.ModelExecutionContext.ClearExecutionContext();
		 boolean f = false;
			Model model = null;
	    try {
	      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "varHistory2.xml");
	      f = true;
	    } catch (ModelException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    } catch (SAXException e) {
	      e.printStackTrace();
	    }
	    assertTrue( f );
	    ModelBlockParam param = null;
	    f = false;
	    try {
	      param = GetParam(model, 1, "inpMaterialParam");
	      f = true;
      } catch (Exception e) {
	      e.printStackTrace();
      }
	    assertTrue(f);
	    assertTrue( param != null );
	    assertTrue(param.IsHistoryExists());
      assertEquals(0, GetIntValue(param));
      ValueChangeListener.HistoryBean bean = null;
      bean = param.GetHistoryBean(0);
      assertTrue(bean == null);
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
      assertEquals(2, GetIntValue(param));
      bean = param.GetHistoryBean(0);
      assertTrue(bean != null);
	 }

}

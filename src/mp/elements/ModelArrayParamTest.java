package mp.elements;

import junit.framework.TestCase;

import java.io.IOException;

import org.xml.sax.SAXException;
import mp.parser.Variable;
import mp.parser.ScriptArray;
import mp.parser.ScriptException;
import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;

/**
 * User: јдминистратор
 * Date: 05.07.2008
 */
public class ModelArrayParamTest extends TestCase {

  public ModelArrayParamTest( String name ){
    super(name);
  }

  /**ѕроверка чтени€ параметра-массива из модели. ћассив одномерный
   *
   */
  public void testReadArrayParam(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array1.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("block");
    assertTrue( block != null );
    ModelBlockParam param = null;
    try {
      param = (ModelBlockParam) block.Get("arrayParam");
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( param != null );
    assertTrue( param instanceof ModelArrayElement );
    Variable var = param.GetVariable();
    assertTrue( var instanceof ScriptArray);
    ScriptArray array = (ScriptArray) var;
    assertEquals( array.GetDimension(),1 );
    assertEquals( array.GetIntSumm(), 0 );
    assertEquals( array.GetDimensionLength(0), 5 );
  }

  public void testRead2DimArray(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array2.xml" );
      ModelBlock block = (ModelBlock) model.Get("block");
      ModelBlockParam param = (ModelBlockParam) block.Get("arrayParam");
      array = (ScriptArray) param.GetVariable();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( array != null );
    assertEquals( array.GetDimension(), 2 );
    assertEquals( array.GetIntSumm(), 0 );
    assertEquals( array.GetDimensionLength(0), 5 );
    assertEquals( array.GetDimensionLength(1), 10 );
  }

  public void testExecScriptWithArray(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array3.xml" );
      model.Execute();
      block = (ModelBlock) model.Get("block");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( block != null );
    assertEquals( block.GetIntValue("singleArrayValue"), 2 );

  }

  public void testReadForEachSection(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array4.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( array.GetIntSumm(), 10 );
    assertTrue( arrayParam.IsForEachEnable() );
    assertTrue( arrayParam.GetEnableVar() != null );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( array.GetIntSumm(), 16*5 );
  }

  public void testExecForEachSection_2Dim(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array5.xml" );
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( array.GetIntSumm(), 50 );
    assertTrue( arrayParam.IsForEachEnable() );
    assertTrue( arrayParam.GetEnableVar() != null );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( array.GetIntSumm(), 16*5*5 );

  }

  public void testExecForEachSection_3Dim(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array6.xml" );
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( array.GetIntSumm(), 2 * 5*5*5 );
    assertTrue( arrayParam.IsForEachEnable() );
    assertTrue( arrayParam.GetEnableVar() != null );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( array.GetIntSumm(), 16*5*5*5 );

  }


  /**провер€етс€ как работает forEach секци€, когда к уже существующему в массиве значению прибавл€етс€ еще одно
   * значение. ≈стественно, значение должно накапливатьс€. Ётот код запускаетс€ несколько раз, и каждый раз сумма
   * элементов массива должна увеличиватьс€.
   * «атем дл€ проверки значение разрешающего флага сбрасываетс€ в false и снова запускаетс€ выполнение модели.
   * значени€ в массиве изменитьс€ не должны
   */
  public void testForEachExec_ArrayAccumulator(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array7.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( array.GetIntSumm(), 10 );
    assertTrue( arrayParam.IsForEachEnable() );
    assertTrue( arrayParam.GetEnableVar() != null );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( array.GetIntSumm(), 12 * 5 );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( array.GetIntSumm(), 12 * 5  + 50);

    arrayParam.GetEnableVar().SetValue( false );
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( array.GetIntSumm(), 12 * 5  + 50);
  }

  public void testForEachExec_WithCoordinates(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array8.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( arrayParam.GetCoordinateVariablesCount(), 1 );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( array.GetIntSumm(), 10 );
    assertEquals( array.GetIntMinValue(), 0 );
    assertEquals( array.GetIntMaxValue(), 4 );
  }

  public void testForEachExec_WithCoordinates_2Dim(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array9.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( arrayParam.GetCoordinateVariablesCount(), 2 );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( array.GetIntSumm(), 100 );
    assertEquals( array.GetIntMinValue(), 0 );
    assertEquals( array.GetIntMaxValue(), 8 );
  }

  public void testForEachExec_WithCoordinates_3Dim(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array10.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( arrayParam.GetCoordinateVariablesCount(), 3 );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( array.GetIntMinValue(), 0 );
    assertEquals( array.GetIntMaxValue(), 12 );
  }

  public void testReadDimensionFromConstant(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array11.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( array.GetDimensionLength(0), 25 );

  }

  public void testReadDimensionFromConstant_2Dim(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array12.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( array.GetDimensionLength(0), 25 );
    assertEquals( array.GetDimensionLength(1), 4 );
  }

  public void testReadInitValueFromConstant(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array13.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    //System.out.println( array.toString() );
    assertEquals( array.GetIntSumm(), 120 );
  }

  public void testReadInitValueFromConstant_Real(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array14.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    //System.out.println( array.toString() );
    assertEquals( array.GetIntSumm(), 22 );
  }

  public void testReadInitValuefromConstant_NotEqualTypes(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array15.xml" );
      //model.Execute();
      block = (ModelBlock) model.Get("block");
      arrayParam = (ModelArrayElement) block.Get("arrayParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( arrayParam != null );
    array = (ScriptArray) arrayParam.GetVariable();
    assertEquals( array.GetIntSumm(), 30 );
  }

  /**провер€етс€ правильность работы блока с массивом в качестве параметра, когда таких блоков создаетс€
   * много (больше трех).
   *
   */
  public void testForEachNoSingleBlocks(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    ScriptArray array = null;
    ModelArrayElement arrayParam = null;
    Model model = null;
    boolean f = false;
    ModelBlock block = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array16.xml" );
      model.Execute();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int i = 0;
    while ( i < 5 ){
      block = model.Get( "block", i );
      assertTrue( block != null );
      try {
        arrayParam = (ModelArrayElement) block.Get("arrayParam");
        assertTrue( arrayParam != null );
        array = (ScriptArray) arrayParam.GetVariable();
        assertEquals( array.GetIntSumm(), i * 5 );
      } catch (ModelException e) {
        e.printStackTrace();
      }
      i++;
    }

  }

  /**ѕровер€етс€ правильность создани€ переменной-массива и передачи ее в скрипты. ѕровер€етс€ в услови€х, когда
   * блоков с данным скриптом много, т.е. когда в дело вступает NotExecutiveParser.
   * ќдним скопом провер€етс€:
   * - правильность работу "слушателей изменений"
   * - правильность работы аггрегатной функции (сумма) в массиве
   *
   */
  public void testArryaInParamScript(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array17.xml" );
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();

      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block1 = model.Get("block", 0);
    ModelBlock block2 = model.Get("block", 1);
    assertTrue( block1 != null );
    assertTrue( block2 != null );
    try {
      ModelBlockParam arrayParam = (ModelBlockParam) block1.Get("arrayParam");
      ModelBlockParam valueParam = (ModelBlockParam) block1.Get("arrayAvgValue");
      assertTrue( arrayParam != null );
      assertTrue( valueParam != null );
      valueParam.IsInputParam( arrayParam );
      arrayParam.IsDependElement( valueParam );
    } catch (ModelException e) {
      e.printStackTrace();
    }

    assertEquals( block1.GetIntValue("arrayAvgValue"), 2 );
    assertEquals( block2.GetIntValue("arrayAvgValue"), 3 );
  }

  private static int GetIntValue(Model model, String blockName, int blockIndex, String varName) throws ModelException, ScriptException{
		ModelBlock block = null;
    ModelBlockParam param = null;
    block = (ModelBlock)model.Get(blockName, blockIndex);
    param = (ModelBlockParam) block.Get(varName);
    int val = param.GetVariable().GetIntValue();
		return val;
	}

  public void testArrayAsConst() {

  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "array18.xml" );
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
	    model.Execute();
	    model.Execute();
	    f = true;
    } catch (Exception e) {
	    e.printStackTrace();
    }
    assertTrue(f);

    f = false;
    int val = -1;
    try {
	    val = GetIntValue(model, "block1", 0, "var");
	    f = true;
    } catch (Exception e) {
	    e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(val, 3);
  }


}

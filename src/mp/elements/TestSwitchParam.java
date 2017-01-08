package mp.elements;

import junit.framework.TestCase;
import mp.parser.ScriptException;


/**
 * User: саша
 * Date: 25.02.2009
 */

public class TestSwitchParam extends TestCase {

  public TestSwitchParam( String aTestName ){
    super( aTestName );
  }

  public void testReadSwitchedParam(){
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch1.xml" );
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  public void testReadNotExistsSwitchParam(){
    boolean f = false;
    try {
      ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch2.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( !f );
  }

  public void testReadRealSwitchParam(){
    boolean f = false;
    try {
      ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch3.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( !f );
  }

  public void testReadArraySwitchParam(){
    boolean f = false;
    try {
      ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch4.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( !f );
  }

  public void testCreateFormulas(){
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch5.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( f );
    ModelBlock block  = (ModelBlock) model.Get( "block1" );
    assertTrue( block != null );
    ModelCalculatedElement param = null;
    f = false;
    try {
      param = (ModelCalculatedElement) block.Get( "param1" );
      f = true;
    } catch (ModelException e) {
    }
    assertTrue( f );
    assertTrue( param != null );
    assertEquals( param.GetFormulaCount(), 2 );
  }

  public void testCreateFormulas_WithOneDefaultFormula(){
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch6.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( f );
    ModelBlock block  = (ModelBlock) model.Get( "block1" );
    assertTrue( block != null );
    ModelCalculatedElement param = null;
    f = false;
    try {
      param = (ModelCalculatedElement) block.Get( "param1" );
      f = true;
    } catch (ModelException e) {
    }
    assertTrue( f );
    assertTrue( param != null );
    assertEquals( param.GetFormulaCount(), 2 );
    assertTrue( param.IsDefaultFormulaExists() );
  }

  public void test2DefaultFormula(){
    boolean f = false;
    try {
      ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch7.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( !f );
  }

  public void testFormulaWithEqualsIntSwitchValues(){
    boolean f = false;
    try {
      ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch8.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( !f );
  }

  public void testFormulaWithEqualsStringSwitchValues(){
    boolean f = false;
    try {
      ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch9.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( !f );
  }

  /**Проверяется правильность переключения при наличии параметра по умолчанию и нескольких переключаемых
   * формул
   *
   */
  public void testSwith(){
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch10.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock block = null;
    block = (ModelBlock) model.Get("block1");
    assertTrue( block != null );
    ModelBlockParam keyParam = null;
    ModelBlockParam switchParam = null;
    f = false;
    try {
      keyParam = (ModelBlockParam) block.Get("switchParameter");
      switchParam = (ModelBlockParam) block.Get("param1");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    //проверяем правильность установки начального значения. Начльное значение рассчитывается по формуле,
    // которая соответствует значению по умолчанию для ключевого параметра

    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int switchedValue = -1;
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( switchedValue, 1 );

    //переключаем ключевое значение. Значение ф переключаемомо параметре должно измениться
    keyParam.GetVariable().SetValue( 2 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( switchedValue, 2 );

    //новое переключение ключевого значения
    keyParam.GetVariable().SetValue( 3 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( switchedValue, 3 );

    //новое переключение ключевого значения. Параметр должен переключиться на формулу по умолчанию
    keyParam.GetVariable().SetValue( 30 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( switchedValue, 1 );
  }

  public void testSwitch2(){
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch11.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock block = null;
    block = (ModelBlock) model.Get("block1");
    assertTrue( block != null );
    ModelBlockParam keyParam = null;
    ModelBlockParam switchParam = null;
    f = false;
    try {
      keyParam = (ModelBlockParam) block.Get("switchParameter");
      switchParam = (ModelBlockParam) block.Get("param1");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int switchedValue = -1;

    //новое переключение ключевого значения. Параметр должен переключиться на формулу по умолчанию
    keyParam.GetVariable().SetValue( 30 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( switchedValue, 31 );

    keyParam.GetVariable().SetValue( 2 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( switchedValue, 4 );

    keyParam.GetVariable().SetValue( 0 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( switchedValue, 1 );
  }

  public void testSwitch3(){
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch12.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock block = null;
    block = (ModelBlock) model.Get("block1");
    assertTrue( block != null );
    ModelBlockParam keyParam = null;
    ModelBlockParam switchParam = null;
    ModelBlockParam x = null;
    f = false;
    try {
      keyParam = (ModelBlockParam) block.Get("switchParameter");
      switchParam = (ModelBlockParam) block.Get("param1");
      x = (ModelBlockParam) block.Get("x");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int switchedValue = -1;
    int xValue = -1;

    keyParam.GetVariable().SetValue( 2 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      xValue = x.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //такая сложная проверка нужна потому что неизвестно точно, в каком порядке будут выполняться элементы,
    // поскольку модель не может правильно выстроить порядок выполнения элементов для переключаемых параметров.
    // Из-за этой неопределенности неизвестно, какое значение Х будет использовано - текущее, или предыдущее
    assertTrue( switchedValue == xValue+2 || switchedValue == xValue - 1 +2);

    keyParam.GetVariable().SetValue( 3 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      xValue = x.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( switchedValue == xValue+3 || switchedValue == xValue - 1 + 3);

    keyParam.GetVariable().SetValue( 50 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      xValue = x.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( switchedValue == xValue || switchedValue == xValue - 1);

  }

  public void testSwitch4(){
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch13.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock block = null;
    block = (ModelBlock) model.Get("block1");
    assertTrue( block != null );
    ModelBlockParam keyParam = null;
    ModelBlockParam switchParam = null;
    ModelBlockParam x = null;
    f = false;
    try {
      keyParam = (ModelBlockParam) block.Get("switchParameter");
      switchParam = (ModelBlockParam) block.Get("param1");
      x = (ModelBlockParam) block.Get("x");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int switchedValue = -1;
    int xValue = -1;

    keyParam.GetVariable().SetValue( true );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      xValue = x.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( switchedValue == xValue+2 || switchedValue == xValue - 1 + 2);

    keyParam.GetVariable().SetValue( false );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      xValue = x.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( switchedValue == xValue + 3 || switchedValue == xValue - 1 + 3);

    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      xValue = x.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( switchedValue == xValue + 3 || switchedValue == xValue - 1 + 3);
  }

  /**Проверяется правильность выбора формулы, когда значение у ключевого параметра установлено при
   * инициализации модели и какое-то время не изменяется. И в то же время в переключающемся параметре
   * есть формула, соответствующая текущему значению ключевого параметра.
   * Ожидается, что переключающийся параметр будет работать по правильной формуле - по формуле, соответствующей
   * текущему значению в ключевом параметре 
   *
   */
  public void testInitSwitchFormula(){
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles +  "switch14.xml" );
      f = true;
    } catch (Exception e) {
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock block = null;
    block = (ModelBlock) model.Get("block1");
    assertTrue( block != null );
    ModelBlockParam keyParam = null;
    ModelBlockParam switchParam = null;
    ModelBlockParam x = null;
    f = false;
    try {
      keyParam = (ModelBlockParam) block.Get("switchParameter");
      switchParam = (ModelBlockParam) block.Get("param1");
      x = (ModelBlockParam) block.Get("x");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int switchedValue = -1;
    int xValue = -1;

    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      xValue = x.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //System.out.println( Integer.toString( switchedValue ) );
    assertTrue( switchedValue == xValue+2 || switchedValue == xValue - 1 + 2);


    keyParam.GetVariable().SetValue( 3 );
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      switchedValue = switchParam.GetVariable().GetIntValue();
      xValue = x.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //System.out.println( Integer.toString( switchedValue ) );
    assertTrue( switchedValue == xValue+3 || switchedValue == xValue - 1 + 3);
  }


}

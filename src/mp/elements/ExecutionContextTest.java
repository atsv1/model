package mp.elements;

import junit.framework.TestCase;

import java.io.IOException;

import org.xml.sax.SAXException;
import mp.parser.ModelExecutionManager;
import mp.parser.ModelExecutionContext;
import mp.parser.ScriptException;
import mp.utils.ServiceLocator;

/**
 * Date: 07.04.2008
 */
public class ExecutionContextTest extends TestCase {

  public ExecutionContextTest( String testName ){
    super( testName );
  }

  /**Проверяется остановка работы модели с вызовом функции StopExecution изнутри блока в останавливаемой модели
   * Результат работы процедуры остановки модели записывается в отдельную переменную
   */
  public void testInnerStop_WithResultVariable(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles + "execution1.xml" );
      model.run();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block;
    block = (ModelBlock) model.Get( "block" );
    assertTrue( block != null );
    assertEquals( block.GetIntValue("var1"), 500 );
    mp.parser.ModelExecutionContext.ClearExecutionContext();
  }

  public void testInnerStop_NoResult(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles + "execution2.xml" );
      model.run();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block;
    block = (ModelBlock) model.Get( "block" );
    assertTrue( block != null );
    assertEquals( block.GetIntValue("var1"), 500 );
    ModelExecutionManager manager = null;
    manager = mp.parser.ModelExecutionContext.GetManager("Модель1");
    assertTrue( manager != null );
    mp.parser.ModelExecutionContext.ClearExecutionContext();
  }

  public void testInnerStop_WrongModelName(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles + "execution3.xml" );
      model.run();
      f = !( model.GetErrorString() == null || "".equalsIgnoreCase( model.GetErrorString() ) ) ;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      //e.printStackTrace();
    } catch (SAXException e) {
      //e.printStackTrace();
    }
    assertTrue( f );
    mp.parser.ModelExecutionContext.ClearExecutionContext();
  }

  public void testInnerStop_ManageAnotherModel(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      //model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles + "execution4.xml" );
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution4.xml" );
      model = builder.GetRootModel();
      assertTrue( model != null );
      model.run();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    //проверяем, присутствуют ли в списке моделей обе модели
    ModelExecutionManager manager = null;
    manager = mp.parser.ModelExecutionContext.GetManager("Модель1");
    assertTrue( manager != null );
    manager = mp.parser.ModelExecutionContext.GetManager("Модель3");
    assertTrue( manager == null );
    manager = mp.parser.ModelExecutionContext.GetManager("Модель2");
    assertTrue( manager != null );
    Model model2 = (Model) manager;
    model.run();

    ModelBlock block1 = (ModelBlock) model.Get("block");
    assertTrue( block1 != null );
    ModelBlock block2 = (ModelBlock) model2.Get("block");
    assertTrue( block2 != null );

    assertEquals( block1.GetIntValue("var1"), 5 );
    assertEquals( block2.GetIntValue("var1"), 500 );
    mp.parser.ModelExecutionContext.ClearExecutionContext();
  }

  public void testManage2Models(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution5.xml" );
      model = builder.GetRootModel();
      assertTrue( model != null );
      model.run();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  public void testCycleManage(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution6.xml" );
      model = builder.GetRootModel();
      assertTrue( model != null );
      model.run();
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
  }

  /**Проверка ошибочной ситуации: создается корневая модель и две других модели, которые имеют одинаковый номер.
   * Система должна выдать ошибку при разборе модели
   */
  public void testEqualModelName(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution7.xml" );
      model = builder.GetRootModel();
      model.RegisterModelInContext();
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( !f );
  }

  /**проверка тройного уровня вложенности моделей
   *
   */
  public void test3Level(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution8.xml" );
      model = builder.GetRootModel();
      model.RegisterModelInContext();
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
    ModelExecutionManager manager = null;
    manager = ModelExecutionContext.GetManager("Модель1");
    assertTrue( manager != null );
    manager = ModelExecutionContext.GetManager("Модель2");
    assertTrue( manager != null );
    manager = ModelExecutionContext.GetManager("Модель3");
    assertTrue( manager != null );

    model.run();

    Model model3 = (Model) manager;
    ModelBlock block = (ModelBlock) model3.Get( "block3" );
    assertEquals( block.GetIntValue("var1"), 499 );

    manager = ModelExecutionContext.GetManager("Модель2");
    Model model2 = (Model) manager;
    block = (ModelBlock) model2.Get("block2");
    assertEquals( block.GetIntValue("var1"), 498 );
  }

  /**Проверяется правильность работы функции скрипта SetToInitConditions() внутри одной модели и без
   * стэйтчарта
   *
   */
  public void testSetToInit(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution9.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    model.run();

    ModelBlock block = (ModelBlock) model.Get("block1");
    assertEquals( block.GetIntValue("var1"), 1 );
    assertEquals( block.GetIntValue("var2"), 2 );
    f = (block.GetIntValue("var3") == 3) ||  ( block.GetIntValue("var3") == 8 );
    assertTrue( f );
  }

  public void testSetToInit_SubModel(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution10.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    model.run();
    String errorStr = model.GetErrorString();
    f = ( errorStr == null || "".equalsIgnoreCase( errorStr ) );
    if ( !f ) {
      System.out.println( errorStr );
    }
    assertTrue( f );
    ModelExecutionManager manager = ModelExecutionContext.GetManager("Модель2");
    assertTrue( manager != null );
    //ModelBlock
  }

  /**Проверяется правильность получения данных из другой модели
   *
   */
  public void testConnectToAnotherModel(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution11.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    model.run();
    String errorStr = model.GetErrorString();
    f = ( errorStr == null || "".equalsIgnoreCase( errorStr ) );
    if ( !f ) {
      System.out.println( errorStr );
    }
    assertTrue( f );

    ModelBlock block = (ModelBlock) model.Get("block");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("param1"), 5 );
  }

  public void testConnectToAnotherModel_ErrorBlockName(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution12.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      //e.printStackTrace();
    } catch (SAXException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );

  }

  public void testSendValueToAnotherModel(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution13.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    model.run();
    ModelExecutionManager manager= ModelExecutionContext.GetManager( "Модель2" );
    Model model2 = (Model) manager;
    ModelBlock block = (ModelBlock) model2.Get("block2");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("var2"), 500 );
  }

  public void testSendValueToBlockWithIndex(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution14.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    model.run();

    ModelExecutionManager manager= ModelExecutionContext.GetManager( "Модель2" );
    Model model2 = (Model) manager;
    ModelBlock block = model2.Get("block2", 5);
    assertTrue( block != null );
    assertEquals( block.GetIntValue("var2"), 500 );
  }

  public void testGetValueFromAnotherModel(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution15.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    model.run();

    ModelBlock block = (ModelBlock) model.Get("block");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("var"), 499 );
  }

  /**проверяется правильность работы функции Get в условиях, когда индекс блока дан в виде переменной,
   * а не константы
   *
   */
  public void testGetValueWithVarParam(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution16.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    model.run();

    ModelBlock block = (ModelBlock) model.Get("block");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("var1"), 3 );
  }

  public void testParallelModels(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution17.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    Model subModel1 = (Model) ModelExecutionContext.GetManager( "SubModel1" );;
    ModelBlock subModel1Block = (ModelBlock) subModel1.Get("block1");
    assertTrue(subModel1Block != null);
    int curValue = 0;
    int prevValue = 0;
    prevValue = subModel1Block.GetIntValue("var1");
    try {
	    model.Execute();
	    model.Execute();
    } catch (Exception e) {
	     e.printStackTrace();
    }
    curValue = subModel1Block.GetIntValue("var1");;
    assertTrue(curValue > prevValue);
  }

  /////////////////////////////////////////////////////////////////////////
  ///////// тестируем константы в модели //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////

  /**проверяется простое создание константы из файла модели, ее получение
   * из модели
   *
   */
  public void testCreateConstant(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "file48.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelConstant constant;
    constant = model.GetConstant( "const1" );
    assertTrue( constant != null );
  }

  /**проверяется правильность отработки константы в атрибуте initvalue параметра блока
   *
   */
  public void testConstantInInitValueAttr(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "file49.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    ModelBlock block = (ModelBlock)model.Get("block");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("var2"), 15 );
  }

  public void testConstantInInitValueAttr_WrongConstName(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "file50.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( !f );
  }

  public void testConstantInBlockCountAttr(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( TestUtils.GetPath() + "file51.xml" );
      model = builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( model.GetBlockCount("block"), 5 );
  }

  /**проверяется правильность обработки констант внутри скриптов
   *
   */
  public void testConstantInScript(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ServiceLocator.GetAttributeReader().ClearConstantList();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( TestUtils.GetPath() + "file52.xml" );
      model = builder.GetRootModel();
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
    ModelBlock block = (ModelBlock)model.Get( "block" );
    assertTrue( block != null );
    assertEquals( block.GetIntValue("var1"), 100 );
    assertEquals( block.GetIntValue("var2"), 95 );
  }


  /////////////////////////////////////////////////////////////////////////
  ///////// тестируем возможность выполнения одной и той же модели одновременно, но в разных потоках //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////

  /**
   * Проверка возможности двойного запуска одной и той же модели
   */
  public void test2SameModelsRun(){
  	Model model1 = null;
  	Model model2 = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( TestUtils.GetPath() + "file65.xml" );
      model1 = builder.GetRootModel();
      //model1.Execute();
      builder.ReadModelTree( TestUtils.GetPath() + "file65.xml" );
      model2 = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertTrue( model1 != null && model2 != null );
    f = false;
    Thread FModelThread = new Thread( model1 );
    FModelThread.start();

    Thread FModelThread2 = new Thread( model2 );
    FModelThread2.start();

    try {
	    Thread.sleep(100);
	    f = true;
    } catch (InterruptedException e) {
	    e.printStackTrace();
    }
    assertTrue(f);
  }

}

package mp.elements;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.UUID;

import org.xml.sax.SAXException;
import mp.parser.ModelExecutionManager;
import mp.parser.Operand;
import mp.parser.ScriptArray;
import mp.parser.ArrayDefinition;
import mp.parser.ModelExecutionContext;
import mp.parser.ScriptException;
import mp.parser.ScriptLanguageExt;
import mp.parser.Variable;
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
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution4.xml" );
      model = builder.GetRootModel();
      assertTrue( model != null );
      //model.run();
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
    ModelBlock block2 = (ModelBlock) model2.Get("sub_block");
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
      //model.RegisterModelInContext();
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
    Model subModel1 = (Model) ModelExecutionContext.GetManager( "SubModel1" );
    Model subModel2 = (Model) ModelExecutionContext.GetManager( "SubModel2" );
    ModelBlock subModel1Block = (ModelBlock) subModel1.Get("block1");
    assertTrue(subModel1Block != null);
    ModelBlock subModel2Block = (ModelBlock) subModel2.Get("block2");
    assertTrue(subModel2Block != null);
    int curValue = 0;
    int prevValue = 0;
    prevValue = subModel1Block.GetIntValue("var1");
    try {
    	model.run();	    
    } catch (Exception e) {
	     e.printStackTrace();
    }
    assertTrue(model.GetErrorString() == null);
    curValue = subModel1Block.GetIntValue("var1");
    assertTrue(curValue > prevValue);
    curValue = subModel1Block.GetIntValue("var2");
    assertTrue( curValue == 100 );
    curValue = subModel2Block.GetIntValue("var3");
    assertEquals( curValue, 50 );
  }
  
  public void testParallelModelsStatechart(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution19.xml" );
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
    f = false;
    try {
    	model.run();
    	f = true;
    } catch (Exception e) {
	     e.printStackTrace();
    }
    assertTrue(f);
    Model subModel1 = (Model) ModelExecutionContext.GetManager( "SubModel19_1" );
    assertTrue( subModel1 != null );
    ModelBlock subModel1Block = (ModelBlock) subModel1.Get("block1");
    assertTrue(subModel1Block != null);
    assertEquals( subModel1Block.GetIntValue("var1"), 100 );
  	
  }
  
  /**
   * проверяем возможность подключения к блокам основной модели из блоков модели, которая подключена из секции <ModelList><ParallelModel
   */
  public void testConnectBlocks_FromParallelModel(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "execution18.xml" );
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
    Model mainModel = (Model) ModelExecutionContext.GetManager( "Модель18" );
    Model subModel = (Model) ModelExecutionContext.GetManager( "Модель18_1" );
    assertTrue(mainModel != null);
    assertTrue(subModel != null);
    f = false;
    try {
			mainModel.Execute();
			mainModel.Execute();
			f = true;
		} catch (ScriptException e) {
			
			e.printStackTrace();
		} catch (ModelException e) {
			
			e.printStackTrace();
		}
    assertTrue(f);
    ModelBlock subBlock = (ModelBlock) subModel.Get("sub_block");
    assertTrue( subBlock != null);
    assertEquals( subBlock.GetIntValue("inp1"), 8 );
  	
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
 public static String syncFlag = "123";
 public static Model globalModel = null;
  
  /**
   * Проверка возможности двойного запуска одной и той же модели
   */
  public void test2SameModelsRun(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
  	Model model1 = null;
  	Model model2 = null;
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      Thread builderThread1 = new Thread( new Runnable() {
				public void run() {
					synchronized (syncFlag) {
						ModelTreeBuilder builder = new ModelTreeBuilder();
						try {
							builder.SetElementFactory(new ModelElementFactory());
							builder.ReadModelTree(TestUtils.GetPath() + "file65.xml");
							globalModel = builder.GetRootModel();
						} catch (Exception e) {
							e.printStackTrace();
						}
						syncFlag.notifyAll();
					}
				}
      } );
      builderThread1.start();
      synchronized(syncFlag){
      	syncFlag.wait();
      }
      
      model1 = globalModel;
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
  
  
  ////////////////////////////////////////////////////////////////////////////////
  /////////////// новая функция fork ////////////////////////////////////////////
  
  public void testFixState_CalculatedElement(){
  	ModelBlockParam param = new ModelCalculatedElement((ModelElement)null, "name", 1);
  	boolean f = false;
  	try {
			param.SetVarInfo("integer", "50");
			f = true;
		} catch (ModelException e) {
			
			e.printStackTrace();
		}
  	assertTrue(f);
  	UUID uid = java.util.UUID.randomUUID();
  	try {
			param.fixState(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	param.GetVariable().SetValue(100);
  	assertEquals( getIntValue(param), 100 );
  	try {
			param.rollbackTo(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	assertEquals( getIntValue(param), 50 );  	
  }
  
  public void testFixState_ArrayElement(){
  	ModelArrayElement param = new ModelArrayElement((ModelElement)null, "name", 1);
  	boolean f = false;
  	ArrayDefinition def = new ArrayDefinition();
    def.SetValueType( Operand.OPERAND_TYPE_INTEGER );
    def.AddDimension( 10 );
    def.SetInitValue("5");
  	try {
			param.InitArray(def);
			f = true;
		} catch (ScriptException e) {			
			e.printStackTrace();
		}
  	assertTrue(f);
  	UUID uid = java.util.UUID.randomUUID();
  	try {
			param.fixState(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	ScriptArray arr = (ScriptArray) param.GetVariable();
  	int[] coord = new int[1];
  	coord[0] = 0;
  	try {
			arr.SetValue(33, coord);
		} catch (ScriptException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	try {
			int val = arr.GetIntValue(coord);
			assertEquals(33, val);
		} catch (ScriptException e) {
			f = false;
			e.printStackTrace();
		}
  	try {
			param.rollbackTo(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	try {
			int val = arr.GetIntValue(coord);
			assertEquals(5, val);
		} catch (ScriptException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  }
  
  public void  testFixState_Statechart(){
  	AutomatState state = new AutomatState(null, "state", 1);
  	AutomatState innerState1 = new AutomatState(null, "begin", 2);
  	AutomatState innerState2 = new AutomatState(null, "state2", 3);
  	
  	
  	AutomatTransitionByValue trans1 = new AutomatTransitionByValue(innerState1, "tr1", 4);
  	trans1.SetNextStateName("state2");
  	Variable var1 = new Variable(false);
    var1.SetName("var1");
    ScriptLanguageExt ext = new ScriptLanguageExt();
  	  	
  	boolean f = false;
  	try {
  		ext.AddVariable( var1 );
  		trans1.SetlanguageExt( ext );
  		innerState1.SetlanguageExt( ext );
  		trans1.SetTransitionVariable("var1");
			state.AddElement(innerState1);
			state.AddElement(innerState2);		
			
			innerState1.AddElement(trans1);
			f = true;
		} catch (Exception e) {			
			e.printStackTrace();
		}
  	assertTrue(f);
  	try {
			state.SetActive(null);
		} catch (Exception e) {
			f = false;
			e.printStackTrace();
		} 
  	assertTrue(f);
  	AutomatState activeState = state.GetActiveState();
  	assertTrue( activeState != null && "begin".equalsIgnoreCase(activeState.GetName()) );
  	UUID uid = java.util.UUID.randomUUID();
  	try {
			state.fixState(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	var1.SetValue(true);
  	try {
			state.SetActive(null);
		} catch (Exception e) {
			f = false;
			e.printStackTrace();
		} 
  	assertTrue(f);
  	activeState = state.GetActiveState();
  	assertTrue( activeState != null && "state2".equalsIgnoreCase(activeState.GetName()) );
  	try {
			state.rollbackTo(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	activeState = state.GetActiveState();
  	assertTrue( activeState != null && "begin".equalsIgnoreCase(activeState.GetName()) );
  }
  
  public void testFixState_BlockParams(){
  	ModelBlock block = new ModelSimpleBlock(null, "block", 1);
  	ModelBlockParam param1 = new ModelCalculatedElement(block, "param1", 2);
  	ModelBlockParam param2 = new ModelCalculatedElement(block, "param2", 3);
  	ModelBlockParam param3 = new ModelCalculatedElement(block, "param3", 4);
  	boolean f = true;
  	try {
  		param1.SetVarInfo("integer", "1");
  		param2.SetVarInfo("integer", "2");
  		param3.SetVarInfo("integer", "3");
  		
			block.AddInnerParam(param1);
			block.AddOutParam(param2);
			block.AddOutParam(param3);
			
			ModelLanguageBuilder builder = new ModelLanguageBuilder( null );
	    builder.UpdateBlock( block );
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		} catch (ScriptException e) {			
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	UUID uid = java.util.UUID.randomUUID();
  	try {
  		block.fixState(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	param1.GetVariable().SetValue(10);
  	param2.GetVariable().SetValue(20);
  	param3.GetVariable().SetValue(30);
  	
  	try {
			block.rollbackTo(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	assertEquals(1, getIntValue(param1));
  	assertEquals(2, getIntValue(param2));
  	assertEquals(3, getIntValue(param3));
  }
  
  public void testFixState_BlockStatechart(){
  	ModelBlock block = new ModelSimpleBlock(null, "block", 1);
  	AutomatState state = new AutomatState(null, "state", 1);
  	AutomatState innerState1 = new AutomatState(null, "begin", 2);
  	AutomatState innerState2 = new AutomatState(null, "state2", 3);
  	
  	
  	AutomatTransitionByValue trans1 = new AutomatTransitionByValue(innerState1, "tr1", 4);
  	trans1.SetNextStateName("state2");
  	Variable var1 = new Variable(false);
    var1.SetName("var1");
    ScriptLanguageExt ext = new ScriptLanguageExt();
  	  	
  	boolean f = false;
  	try {
  		ext.AddVariable( var1 );
  		trans1.SetlanguageExt( ext );
  		innerState1.SetlanguageExt( ext );
  		trans1.SetTransitionVariable("var1");
			state.AddElement(innerState1);
			state.AddElement(innerState2);		
			
			innerState1.AddElement(trans1);
			
			block.AddState(state);
			
			ModelLanguageBuilder builder = new ModelLanguageBuilder( null );
	    builder.UpdateBlock( block );
			
			
			f = true;
		} catch (Exception e) {			
			e.printStackTrace();
		}
  	assertTrue(f);
  	ModelTime t = new ModelTime();
  	try {
  		block.InitStatechart();
			block.Execute(t);
		} catch (Exception e) {
			f = false;
			e.printStackTrace();
		} 
  	assertTrue(f);
  	AutomatState activeState = state.GetActiveState();
  	assertTrue( activeState != null && "begin".equalsIgnoreCase(activeState.GetName()) );
  	UUID uid = java.util.UUID.randomUUID();
  	try {
			block.fixState(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	var1.SetValue(true);
  	try {
			block.Execute(t);
		} catch (Exception e) {
			f = false;
			e.printStackTrace();
		} 
  	assertTrue(f);
  	activeState = null;
  	activeState = state.GetActiveState();
  	assertTrue( activeState != null && "state2".equalsIgnoreCase(activeState.GetName()) );
  	
  	try {
			block.rollbackTo(uid);
		} catch (ModelException e) {
			f = false;
			e.printStackTrace();
		}
  	assertTrue(f);
  	activeState = null;
  	activeState = state.GetActiveState();
  	assertTrue( activeState != null && "begin".equalsIgnoreCase(activeState.GetName()) );
  	
  }
  
  
  public void testFork1(){
  	
  	mp.parser.ModelExecutionContext.ClearExecutionContext();    
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "fork1.xml" );
      builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    Model mainModel = (Model) ModelExecutionContext.GetManager( "fork1_main" );
    Model subModel = (Model) ModelExecutionContext.GetManager( "fork1_sub" );
    assertTrue(mainModel != null);
    assertTrue(subModel != null);
    mainModel.run();
    f = false;  	
    Integer i = getIntValue(mainModel, "block", 0, "forkResult");
    assertTrue(i != null);
    assertTrue(i >= new Integer(20));
    i = getIntValue(subModel, "sub_block", 0, "innerCounter");
    /*проверяем 11, а не 10 потому что перед завершением все элементы модели получают возможность выполниться в основном цикле выполнения,
     * а в следующий цикл модель не заходит
     * */
    assertEquals(i, new Integer(11));
  }
  
  public void testFork_Cycle(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();    
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "fork2.xml" );
      builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    Model mainModel = (Model) ModelExecutionContext.GetManager( "fork2_main" );
    Model subModel = (Model) ModelExecutionContext.GetManager( "fork2_sub" );
    assertTrue(mainModel != null);
    assertTrue(subModel != null);
    mainModel.run();
    Integer i = getIntValue(mainModel, "block", 0, "forkResult");
    assertEquals(i, new Integer(8));
  	i = getIntValue(mainModel, "block", 0, "i");
  	assertEquals(i, new Integer(10));
  	i = getIntValue(subModel, "sub_block", 0, "inp1");
  	assertEquals(i, new Integer(0));
  }
  
  public void testFork_ModelWithConst(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();    
    boolean f = false;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "fork3.xml" );
      builder.GetRootModel();
      f = true;
    } catch (Exception e) {    	
      e.printStackTrace();
    } 
    assertTrue( f );
    Model mainModel = (Model) ModelExecutionContext.GetManager( "fork3_main" );
    Model subModel = (Model) ModelExecutionContext.GetManager( "fork3_sub" );
    assertTrue(mainModel != null);
    assertTrue(subModel != null);
    mainModel.run();
    String err = mainModel.GetErrorString();
    assertTrue( err == null || "".equals(err) );
  	
  }
  
  public void testFork_StatechartTimeoutTransitionAfterFork(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();  	
    boolean f = false;    
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "fork4.xml" );
      builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    Model mainModel = (Model) ModelExecutionContext.GetManager( "fork4_main" );
    Model subModel = (Model) ModelExecutionContext.GetManager( "fork4_sub" );
    assertTrue(mainModel != null);
    assertTrue(subModel != null);
    mainModel.run();
    Integer i = getIntValue(mainModel, "block", 0, "forkResult");
    assertEquals(i, new Integer(103));
  	i = getIntValue(mainModel, "block", 0, "counter");
  	assertEquals(i, new Integer(10));
  	i = getIntValue(subModel, "sub_block", 0, "subCounter");
  	assertEquals(i, new Integer(10));  	
  }
  
  public void testFork_isForkVariable(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();  	
    boolean f = false;    
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "fork5.xml" );
      builder.GetRootModel();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    Model mainModel = (Model) ModelExecutionContext.GetManager( "fork5_main" );
    mainModel.run();
    assertTrue(mainModel != null);
    Integer i = getIntValue(mainModel, "block", 0, "forkResult");
    assertEquals(i, new Integer(100));  	
  }
   
  
  
  private int getIntValue(ModelBlockParam param){
  	if ( param == null ) {
  		return 0;
  	}
  	try {
			return param.GetVariable().GetIntValue();
		} catch (ScriptException e) {
			return 0;
		}
  	
  }
  
  private Integer getIntValue(Model model, String blockName, int blockIndex, String paramName){
  	if (model == null) {
  		return null;
  	}
  	ModelBlock block = model.Get(blockName, blockIndex);
  	if (block == null) {
  		return null;
  	}
  	return block.GetIntValue(paramName);  	
  }
  

}

package mp.elements;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Vector;
import java.lang.reflect.Field;
import java.io.IOException;

import mp.parser.ScriptException;
import mp.utils.ServiceLocator;
import org.xml.sax.SAXException;

/**
 * User: atsv
 * Date: 10.10.2007
 */
public class TimeManagerTest extends TestCase {

  public TimeManagerTest(String s){
    super(s);
  }

  public void testCreateEmptyTimeManager(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager();
    assertTrue( manager.GetNearestModelTime() == null );
    assertEquals( manager.GetExecuteGroupCount(), 0 );
  }


  public void testAddSingleElement(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager =ModelTimeManager.getTimeManager();
    ModelBlock block = new ModelSimpleBlock(null, "block", ServiceLocator.GetNextId());
    ModelElementContainer container = new ModelElementContainer();
    try {
      container.AddElement( block );
      container.AddElement( new ModelElement( null, "block2", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block3", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block4", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block5", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block6", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block7", ServiceLocator.GetNextId() ) );
      manager.SetFullElementsList( container );
    } catch (ModelException e) {
      e.printStackTrace();
    }

    ModelTime time = new ModelTime(1);
    boolean f = false;
    try {
      manager.AddElement( block, time );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    ModelTime time2 = new ModelTime( 1 );
    ModelBlock block2 = new ModelSimpleBlock(null, "block2", ServiceLocator.GetNextId());
    f = false;
    try {
      manager.AddElement( block2, time2 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    //
    ModelTime time3 = new ModelTime( 2 );
    ModelBlock block3 = new ModelSimpleBlock(null, "block2", ServiceLocator.GetNextId());
    f = false;
    try {
      manager.AddElement( block3, time3 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 3 );
  }

  /**Проверяется добавление в менеджер времени элемента, который уже имеется в нем. Ожидается, что повторно
   * добавляемый элмент добавлен в менеджер памяти не будет
   *
   */
  public void testAddExistElement(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager();
    int id = ServiceLocator.GetNextId();
    ModelBlock block1 = new ModelSimpleBlock(null, "block1", id);
    ModelElementContainer container = new ModelElementContainer();
    try {
      container.AddElement( block1 );
      container.AddElement( new ModelElement( null, "block2", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block3", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block4", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block5", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block6", ServiceLocator.GetNextId() ) );
      container.AddElement( new ModelElement( null, "block7", ServiceLocator.GetNextId() ) );
      manager.SetFullElementsList( container );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    boolean f = false;
    try {
      manager.AddElement( block1, new ModelTime(0) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    ModelBlock block2 = new ModelSimpleBlock( null, "block2", id );
    f = false;
    try {
      manager.AddElement( block2, new ModelTime(100) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 2 );
  }

  private static ArrayList GetGroupsList( ModelTimeManager manager ){
    Class cl = manager.getClass();
    Field f = null;
    try {
      f = cl.getDeclaredField("FGroupList");
      f.setAccessible( true );
      return (ArrayList) f.get( manager );
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**Проверяется правильность переключения менеджера времени на режим "полный список" и
   * правильность действий, предпринимаемых после такого переключения:
   * 1. Должны быть удалены из списка исполняемых групп все группы, у которых время выполнения больше, чем время
   * выполнения "полного списка"
   * 2. При добавлении в менеджер времени элемента, с временем выполнения большим, чем время выполнения "полного списка",
   * такой элемент добавляться не должен (т.е. не должна создаваться новая группа для этого элемента)
   *
   */
  public void testShiftToFullList(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager();
    int id = 0;
    ModelElementContainer container = new ModelElementContainer();
    int i = 0;
    boolean f = true;
    while ( i < (ModelTimeManager.ELEMENT_COUNT_LIMIT + 10)){
      id = ServiceLocator.GetNextId();
      try {
        container.AddElement( new ModelSimpleBlock( null, Integer.toString( id ), id ) );
      } catch (ModelException e) {
        f = false;
        e.printStackTrace();
        break;
      }
      i++;
    }
    assertTrue( f );
    f = false;
    try {
      manager.SetFullElementsList( container );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    ModelTime timeToFullExecution = new ModelTime(5);
    ModelTime lowTime = new ModelTime(1);
    ModelTime greaterTime = new ModelTime(10);
    f = false;
    try {
      manager.AddElement( (ModelBlock)container.get( 0 ), timeToFullExecution );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( 2, manager.GetExecuteGroupCount());
    f = false;
    try {
      manager.AddElement( (ModelBlock)container.get( 1 ), lowTime );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( 3, manager.GetExecuteGroupCount());
    f = false;
    try {
      manager.AddElement( (ModelBlock)container.get( 1 ), greaterTime );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( 4, manager.GetExecuteGroupCount());
    ArrayList groups = GetGroupsList( manager );
    assertTrue( groups != null );
    assertEquals( groups.size(), 4 );

    ModelTimeManager.ExecuteGroup lowGroup = (ModelTimeManager.ExecuteGroup) groups.get(1);
    ModelTimeManager.ExecuteGroup fullGroup = (ModelTimeManager.ExecuteGroup) groups.get(2);
    ModelTimeManager.ExecuteGroup greaterGroup = (ModelTimeManager.ExecuteGroup) groups.get(3);

    assertEquals( lowGroup.GetExecuteTime().Compare( lowTime ), ModelTime.TIME_COMPARE_EQUALS );
    assertEquals( fullGroup.GetExecuteTime().Compare( timeToFullExecution ), ModelTime.TIME_COMPARE_EQUALS );
    assertEquals( greaterGroup.GetExecuteTime().Compare( greaterTime ), ModelTime.TIME_COMPARE_EQUALS );

    i = 4;
    f = true;
    while ( fullGroup.FillFlag != ModelTimeManager.EXECUTE_GROUP_IS_FULL ){
      try {
        manager.AddElement( (ModelBlock)container.get( i ), timeToFullExecution );
      } catch (ModelException e) {
        e.printStackTrace();
        f = false;
        break;
      }
      i++;
    }
    assertTrue( f );
    /**
     * Было 4 группы (одна создана при добавлении списка, три других - при добавлении блоков с определенным временем)
     * Стало 3, т.к. группа с номером 3 стала полной
     */
    assertEquals( manager.GetExecuteGroupCount(), 3 );

    lowGroup = (ModelTimeManager.ExecuteGroup) groups.get(1);
    fullGroup = (ModelTimeManager.ExecuteGroup) groups.get(2);
    assertEquals( lowGroup.GetExecuteTime().Compare( lowTime ), ModelTime.TIME_COMPARE_EQUALS );
    assertEquals( fullGroup.GetExecuteTime().Compare( timeToFullExecution ), ModelTime.TIME_COMPARE_EQUALS );

    f = false;
    try {
      manager.AddElement( new ModelSimpleBlock(null, "test", ServiceLocator.GetNextId()), greaterTime );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 3 ); // Добавление элемента после "полной" группы не создает новую группу
    assertEquals( lowGroup.GetExecuteTime().Compare( lowTime ), ModelTime.TIME_COMPARE_EQUALS );
    assertEquals( fullGroup.GetExecuteTime().Compare( timeToFullExecution ), ModelTime.TIME_COMPARE_EQUALS );

  }

  /**Проверяется правильность работы менеджера времени с внешним значением шага времени.
   * Для проверки используется менеджер с большим шагом времени.
   *
   */
  public void testSelfTimeStep(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager =  ModelTimeManager.getTimeManager( new ModelTime(5) );
    ModelElementContainer container = new ModelElementContainer();
    int i = 0;
    boolean f = true;
    int id = 0;
    while ( i < (ModelTimeManager.ELEMENT_COUNT_LIMIT + 10)){
      id = ServiceLocator.GetNextId();
      try {
        container.AddElement( new ModelSimpleBlock( null, Integer.toString( id ), id ) );
      } catch (ModelException e) {
        f = false;
        e.printStackTrace();
        break;
      }
      i++;
    }
    assertTrue( f );
    f = false;
    try {
      manager.SetFullElementsList( container );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    assertTrue( f );
    f = false;
    try {
      manager.AddElement((ModelBlock) container.get( 0 ), new ModelTime(2) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );

    f = false;
    try {
      manager.AddElement((ModelBlock) container.get( 1 ), new ModelTime(3) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );


     f = false;
    try {
      manager.AddElement((ModelBlock) container.get( 2 ), new ModelTime(10) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 2 );


  }

  /**Проверяется правильность добвления блока, который не может поределить время собственного выполнения.
   * В таких случаях блок добавляется в ближайшую группу, у которой время выполнения.
   * Проверяется правильность добавления такого блока в пустой менеджер времени
   */
  public void testAddBlockWithNullTime_EmptyManager(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager();
    ModelBlock block = new ModelSimpleBlock( null, "", 1 );
    assertTrue( manager.GetTimeIndependentBlockGroupIndex() < 0 );
    boolean f = false;
    try {
      manager.AddElement( block );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    assertEquals( manager.GetTimeIndependentBlockGroupIndex(), 0 );

    manager = ModelTimeManager.getTimeManager();
    f = false;
    try {
      manager.AddElement( block, null );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    assertEquals( manager.GetTimeIndependentBlockGroupIndex(), 0 );
  }

  /**Проверяется правильность изменения индекса группы, в которую в настоящий момент добавляются не зависящие от времени
   * блоки.
   * В начале добавления индекс должен быть равным -1. После добавления первого же блока он должен быть равным 0.
   * После добавления последующих блоков он не должен изменяться.
   * После выполнения всех блоков он должен снова стать равным -1
   */
  public void testChangeIndepandentIndex(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager();
    ModelBlock block = new ModelSimpleBlock( null, "", 1 );
    assertTrue( manager.GetTimeIndependentBlockGroupIndex() < 0 );
    boolean f = false;
    try {
      manager.AddElement( block );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    assertEquals( manager.GetTimeIndependentBlockGroupIndex(), 0 );
    f = false;
    try {
      manager.ExecuteElements();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 0 );
    assertTrue( manager.GetTimeIndependentBlockGroupIndex() < 0 );
  }

  /** Проверяется правильность добавления независимого от времени блока в случае, если менеджер времени не пуст - в нем
   * уже есть группы, у которых время выполнения РАНЬШЕ  возможного времени выполнения независимого блока
   *
   */
  public void testAddTimeIndependentBlock_1(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager( new ModelTime(0.05) );
    ModelBlock timeDependBlock = new ModelSimpleBlock( null, "1", 1 );
    ModelBlock timeIndependBlock = new ModelSimpleBlock( null, "2", 2 );
    boolean f = false;
    try {
      manager.AddElement( timeDependBlock, new ModelTime( 0.5 ) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    assertTrue( manager.GetTimeIndependentBlockGroupIndex() < 0 );
    f = false;
    try {
      manager.AddElement( timeIndependBlock );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    assertEquals( manager.GetTimeIndependentBlockGroupIndex(), 1 );

    f = false;
    try {
      manager.ExecuteElements();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    assertEquals( manager.GetTimeIndependentBlockGroupIndex(), 0 );
  }

  /**проверяется правильность добавления независимого от времени блока в случае, если менеджер времени не пуст, и в нем
   * уже есть группы, у которых время выполнения ПОЗЖЕ возможного времени выполнения независимого блока
   *
   */
  public void testAddTimeIndependentBlock_2(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager( new ModelTime(0.05) );
    ModelBlock timeDependBlock = new ModelSimpleBlock( null, "1", 1 );
    ModelBlock timeIndependBlock = new ModelSimpleBlock( null, "2", 2 );
    boolean f = false;
    try {
      manager.AddElement( timeDependBlock, new ModelTime( 100 ) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    assertTrue( manager.GetTimeIndependentBlockGroupIndex() < 0 );
    f = false;
    try {
      manager.AddElement( timeIndependBlock );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    assertEquals( manager.GetTimeIndependentBlockGroupIndex(), 0 );
  }

  /**Проверяется правильность обработки ситуации, когда блок добавляется в менеджер времени, когда в нем уже
   * присутствует группа с "полным списком" и время выполнения блока наступит позже времени выполнения группы с "полным
   * списком".
   * Ожидается, что такой блок добавлен не будет
   *
   */
  public void testAddIndependentBlock_AfterFullList(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager();
    ModelElementContainer container = new ModelElementContainer();
    int id = 0;
    int i = 0;
    boolean f = true;
    while ( i < (ModelTimeManager.ELEMENT_COUNT_LIMIT + 10)){
      id = ServiceLocator.GetNextId();
      try {
        container.AddElement( new ModelSimpleBlock( null, Integer.toString( id ), id ) );
      } catch (ModelException e) {
        f = false;
        e.printStackTrace();
        break;
      }
      i++;
    }
    assertTrue( f );
    f = false;
    try {
      manager.SetFullElementsList( container );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ArrayList groups = GetGroupsList( manager );
    assertTrue( groups != null );
    ModelTime fullListTime  = new ModelTime(0.1);
    f = false;
    try {
      manager.AddElement((ModelBlock) container.get( 0 ), fullListTime );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelTimeManager.ExecuteGroup group = (ModelTimeManager.ExecuteGroup) groups.get( 0 );
    assertTrue( group != null );
    i = 1;
    f = false;
    while ( group.FillFlag != ModelTimeManager.EXECUTE_GROUP_IS_FULL ){
      try {
        manager.AddElement((ModelBlock) container.get( i ), fullListTime );
        i++;
        f = true;
      } catch (ModelException e) {
        f = false;
        e.printStackTrace();
        assertTrue( f );
      }
    }
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    ModelBlock block = new ModelSimpleBlock( null, "1", 1 );
    f = false;
    try {
      manager.AddElement( block );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    assertTrue( manager.GetTimeIndependentBlockGroupIndex() >= 0 );
  }

  public void testAddIndependentBlock_BeforeFullList(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager();
    ModelElementContainer container = new ModelElementContainer();
    int id = 0;
    int i = 0;
    boolean f = true;
    while ( i < (ModelTimeManager.ELEMENT_COUNT_LIMIT + 10)){
      id = ServiceLocator.GetNextId();
      try {
        container.AddElement( new ModelSimpleBlock( null, Integer.toString( id ), id ) );
      } catch (ModelException e) {
        f = false;
        e.printStackTrace();
        break;
      }
      i++;
    }
    assertTrue( f );
    f = false;
    try {
      manager.SetFullElementsList( container );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ArrayList groups = GetGroupsList( manager );
    assertTrue( groups != null );
    ModelTime fullListTime  = new ModelTime(100);
    f = false;
    try {
      manager.AddElement((ModelBlock) container.get( 0 ), fullListTime );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelTimeManager.ExecuteGroup group = (ModelTimeManager.ExecuteGroup) groups.get( 0 );
    assertTrue( group != null );
    i = 1;
    f = false;
    while ( group.FillFlag != ModelTimeManager.EXECUTE_GROUP_IS_FULL ){
      try {
        manager.AddElement((ModelBlock) container.get( i ), fullListTime );
        i++;
        f = true;
      } catch (ModelException e) {
        f = false;
        e.printStackTrace();
        assertTrue( f );
      }
    }
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    ModelBlock block = new ModelSimpleBlock( null, "1", 1 );
    f = false;
    try {
      manager.AddElement( block );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( manager.GetExecuteGroupCount(), 2 );

  }

  /**Проверяется правильность добавления блока, когда в менеджере времени выполняется процедура выполнения. (выполнения
   * элементов некоторой группы)
   *
   */
  public void testAddIndependentBlock_InRuntime(){

  }

  /**Проверяется работа функции, возвращающей время выполнения для блока, который сам не может определить свое время
   *
   */
  public void testGetNearestGreaterTime(  ){
    ModelTime testTime = ModelTimeManager.GetExecTimeForTimeIndependentElement( null );
    ModelTime time = new ModelTime(1);
    assertTrue( time.Compare( testTime ) == ModelTime.TIME_COMPARE_EQUALS );

    time.Add( 0.1 );
    testTime = ModelTimeManager.GetExecTimeForTimeIndependentElement( time );
    time = new ModelTime( 2 );
    assertTrue( time.Compare( testTime ) == ModelTime.TIME_COMPARE_EQUALS );

    testTime = ModelTimeManager.GetExecTimeForTimeIndependentElement( time );
    time = new ModelTime( 3 );
    assertTrue( time.Compare( testTime ) == ModelTime.TIME_COMPARE_EQUALS );

  }

  public void testTimeCompareWithStep(){
  	mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelTimeManager manager = ModelTimeManager.getTimeManager( new ModelTime(1) );
    ModelTime time1 = new ModelTime(10);
    ModelTime time2 = new ModelTime(2);
    assertEquals( manager.CompareWithStep( time1, time2 ), ModelTime.TIME_COMPARE_GREATER );
    assertEquals( manager.CompareWithStep( time2, time1 ), ModelTime.TIME_COMPARE_LESS );
    assertEquals( manager.CompareWithStep( time2, time2 ), ModelTime.TIME_COMPARE_EQUALS );

    time2 = new ModelTime(9.5);
    assertEquals( manager.CompareWithStep( time2, time1 ), ModelTime.TIME_COMPARE_EQUALS );
    assertEquals( manager.CompareWithStep( time1, time2 ), ModelTime.TIME_COMPARE_EQUALS );
  }

  /** Проверяется правильность изменения модельного времени в ходе работы модели.
   * Для этого создаются два класса блоков с различным интервалом выполнения.
   *
   */
  public void testExecTimeChanging(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles + "file40.xml" );
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
    //assertTrue( model != null );
    ModelTimeManager manager = model.GetTimeManager();
    ArrayList groups = GetGroupsList( manager );
    assertTrue( groups != null );
    // должно быть две группы, в первой по времени выполнения - block2, во второй - block1
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    ModelTimeManager.ExecuteGroup group = (ModelTimeManager.ExecuteGroup) groups.get( 1 );
    assertEquals( group.GetExecuteTime().Compare( new ModelTime(3) ),  ModelTime.TIME_COMPARE_EQUALS );
    assertTrue( group.FillFlag == ModelTimeManager.EXECUTE_GROUP_IS_FULL );
    assertEquals( group.GetElementsCount(), 100 );

    group = (ModelTimeManager.ExecuteGroup) groups.get( 0 );
    assertEquals( group.GetExecuteTime().Compare( new ModelTime(2) ),  ModelTime.TIME_COMPARE_EQUALS );
    assertEquals( group.GetElementsCount(), 2 );
    ModelBlock block1 = model.Get("block1", 0);
    ModelBlock block2 = model.Get("block2", 0);
    assertEquals( block1.GetIntValue("var1"), 0 );
    assertEquals( block2.GetIntValue("var2"), 1 );

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
    //теперь должна быть только одна группа, со всеми элементами сразу
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    group = (ModelTimeManager.ExecuteGroup) groups.get( 0 );
    assertEquals( group.GetElementsCount(), 102 );
    assertTrue( group.FillFlag == ModelTimeManager.EXECUTE_GROUP_IS_FULL );
    assertEquals( group.GetExecuteTime().Compare( new ModelTime(3) ),  ModelTime.TIME_COMPARE_EQUALS );
    assertEquals( block1.GetIntValue("var1"), 0 );
    assertEquals( block2.GetIntValue("var2"), 2 );

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
    assertEquals( block1.GetIntValue("var1"), 1 );
    assertEquals( block2.GetIntValue("var2"), 3 );
    // теперь снова должно быть две группы
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    group = (ModelTimeManager.ExecuteGroup) groups.get( 0 );
    assertEquals( group.GetElementsCount(), 2 );
    group = (ModelTimeManager.ExecuteGroup) groups.get( 1 );
    assertEquals( group.FillFlag, ModelTimeManager.EXECUTE_GROUP_IS_FULL );
    assertEquals( group.GetElementsCount(), 100 );
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
    assertEquals( manager.GetExecuteGroupCount(), 2 );
    group = (ModelTimeManager.ExecuteGroup) groups.get( 0 );
    assertEquals( group.GetElementsCount(), 2 );
    group = (ModelTimeManager.ExecuteGroup) groups.get( 1 );
    assertEquals( group.FillFlag, ModelTimeManager.EXECUTE_GROUP_IS_FULL );
    assertEquals( group.GetElementsCount(), 100 );

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
    assertEquals( manager.GetExecuteGroupCount(), 1 );
    assertEquals( group.FillFlag, ModelTimeManager.EXECUTE_GROUP_IS_FULL );
    assertEquals( group.GetElementsCount(), 102 );

  }


}

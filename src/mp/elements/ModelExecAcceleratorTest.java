package mp.elements;

import junit.framework.TestCase;

import java.io.IOException;
import java.awt.*;

import org.xml.sax.SAXException;
import mp.parser.ScriptException;
import mp.parser.Variable;

/** “естируютс€ классы и методы, предназначенные дл€ ускорени€ работы блоков. ј именно:
 * - провер€етс€ работа класса ModelExecuteList - класса, который управл€ет списком исполн€емыйх переменных
 * - работа класса SimpleBlock совместно с ModelExecuteList
 * - работа мультиплексора с этим же классом
 * Date: 19.09.2007
 */
public class ModelExecAcceleratorTest extends TestCase {

  public ModelExecAcceleratorTest( String name ){
    super(name);
  }

  /**ѕровер€етс€ правильность добавлени€ элементов в исполн€емый список  
   * ѕровер€етс€ правиьлность добавлени€ одного элемента
   */
  public void testAddToExecuteList(){
    ModelElementContainer container = new ModelElementContainer();
    boolean f = false;
    ModelElement e1 = new ModelElement(null, "name1", 1);
    try {
      container.AddElement( e1 );
      //делаем список большим, чтобы проверить добавление в оба списка
      container.AddElement( new ModelElement(null, "name2", 2) );
      container.AddElement( new ModelElement(null, "name3", 3) );
      container.AddElement( new ModelElement(null, "name4", 4) );
      container.AddElement( new ModelElement(null, "name5", 5) );
      container.AddElement( new ModelElement(null, "name6", 6) );
      container.AddElement( new ModelElement(null, "name7", 7) );
      container.AddElement( new ModelElement(null, "name8", 8) );
      container.AddElement( new ModelElement(null, "name9", 9) );
      container.AddElement( new ModelElement(null, "name10", 10) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    ModelExecuteList list = null;
    f = false;
    try {
      list = new ModelExecuteList( container );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    // провер€ем начальные установки класса
    assertEquals( list.CurrentCycleList.length, container.size() );
    assertEquals( list.CurrentCyclePointer, -1 );
    assertEquals( list.NextCycleList.length, container.size() );
    assertEquals( list.NextCyclePointer, -1 );
    try {
      list.AddToExecuteList( e1 );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    //избавл€емс€ от начальной установки, котоа€ указывает, что в первый запуск все элементы из списка должны быть
    // выполнены
    list.FinishCurrentCycle();
    //провер€ем правильность добавлени€ элемента
    assertEquals( list.CurrentCyclePointer, 0 );
    assertEquals( list.CurrentCycleList[0].GetElementId(), e1.GetElementId() );
    assertEquals( list.NextCyclePointer, -1 );
    try {
      list.AddToExecuteList( e1 );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    //провер€ем правильность повторного добавлени€ элемента
    //ожидаетс€, что список CurrentCyclePointer не изменитс€, а элемент добавитс€ в список NextCycleList 
    assertEquals( list.CurrentCyclePointer, 0 );
    assertEquals( list.CurrentCycleList[0].GetElementId(), e1.GetElementId() );
    assertEquals( list.NextCyclePointer, 0 );
    assertEquals( list.NextCycleList[0].GetElementId(), e1.GetElementId() );
    list.FinishCurrentCycle();
    assertEquals( list.CurrentCycleList.length, container.size() );
    assertEquals( list.CurrentCyclePointer, 0 );
    assertEquals( list.NextCycleList.length, container.size() );
    assertEquals( list.NextCyclePointer, -1 );
  }



  public void test(){
    //System.out.println( Integer.toString( Color.GREEN.getRGB() ) );
  }


}

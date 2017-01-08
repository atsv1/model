package mp.elements;

import junit.framework.TestCase;

import java.io.IOException;
import java.awt.*;

import org.xml.sax.SAXException;
import mp.parser.ScriptException;
import mp.parser.Variable;

/** ����������� ������ � ������, ��������������� ��� ��������� ������ ������. � ������:
 * - ����������� ������ ������ ModelExecuteList - ������, ������� ��������� ������� ������������ ����������
 * - ������ ������ SimpleBlock ��������� � ModelExecuteList
 * - ������ �������������� � ���� �� �������
 * Date: 19.09.2007
 */
public class ModelExecAcceleratorTest extends TestCase {

  public ModelExecAcceleratorTest( String name ){
    super(name);
  }

  /**����������� ������������ ���������� ��������� � ����������� ������  
   * ����������� ������������ ���������� ������ ��������
   */
  public void testAddToExecuteList(){
    ModelElementContainer container = new ModelElementContainer();
    boolean f = false;
    ModelElement e1 = new ModelElement(null, "name1", 1);
    try {
      container.AddElement( e1 );
      //������ ������ �������, ����� ��������� ���������� � ��� ������
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
    // ��������� ��������� ��������� ������
    assertEquals( list.CurrentCycleList.length, container.size() );
    assertEquals( list.CurrentCyclePointer, -1 );
    assertEquals( list.NextCycleList.length, container.size() );
    assertEquals( list.NextCyclePointer, -1 );
    try {
      list.AddToExecuteList( e1 );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    //����������� �� ��������� ���������, ������ ���������, ��� � ������ ������ ��� �������� �� ������ ������ ����
    // ���������
    list.FinishCurrentCycle();
    //��������� ������������ ���������� ��������
    assertEquals( list.CurrentCyclePointer, 0 );
    assertEquals( list.CurrentCycleList[0].GetElementId(), e1.GetElementId() );
    assertEquals( list.NextCyclePointer, -1 );
    try {
      list.AddToExecuteList( e1 );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    //��������� ������������ ���������� ���������� ��������
    //���������, ��� ������ CurrentCyclePointer �� ���������, � ������� ��������� � ������ NextCycleList 
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

package mp.elements;

import junit.framework.TestCase;

/**
 * User: �������������
 * Date: 31.05.2008
 */
public class TestExistsArray extends TestCase {

  public TestExistsArray( String  name){
    super( name );
  }

  public void testSimpleExists(){
    ExistsService es = new ExistsService(  0, 10);
    boolean f = false;
    try {
      es.ElementExists( 5 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      assertTrue( es.IsExistsInList( 5 ) );
      assertTrue( !es.IsExistsInList( 6 ) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  /**������� ������ �� 10 ��������� � �������� ��������� ���� ����������� � ��������,
   * �������� �������� ��� � ������.
   * ������ ���������� ������
   *
   */
  public void testUserTooBigElement(){
    ExistsService es = new ExistsService(  0, 10);
    boolean f = false;
    try {
      es.ElementExists( 150 );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  /**����������� ������������ ��������� ���� ��������� �������
   *
   */
  public void testAllElements(){
    ExistsService es = new ExistsService(  0, 10);
    int i = 0;
    int existsCount = 0;
    while( i < 10 ){
      try {
        if ( es.IsExistsInList( i ) ) {
          existsCount++;
        }
        i++;
      } catch (ModelException e) {
        e.printStackTrace();
      }
    }
    assertEquals( existsCount, 0 );

    boolean f = false;
    try {
      es.ElementExists( 5 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    i = 0;
    while( i < 10 ){
      try {
        if ( es.IsExistsInList( i ) ) {
          existsCount++;
        }
        i++;
      } catch (ModelException e) {
        e.printStackTrace();
      }
    }
    assertEquals( existsCount, 1 );
  }

  public void testSetNotExists(){
    ExistsService es = new ExistsService(  0, 10);
    boolean f = false;
    try {
      es.ElementExists( 5 );
      assertTrue( es.IsExistsInList( 5 ) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    int i = 0;
    int existsCount = 0;
    //���������, ����� ������ ���� ������� ���� ������������� ����
    while( i < 10 ){
      try {
        if ( es.IsExistsInList( i ) ) {
          existsCount++;
        }
        i++;
      } catch (ModelException e) {
        e.printStackTrace();
      }
    }
    assertEquals( existsCount, 1 );
    f = false;
    //���������� ���� ��� ������������� ��������
    try {
      es.ElementNotExists( 5 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    i = 0;
    existsCount = 0;
    while( i < 10 ){
      try {
        if ( es.IsExistsInList( i ) ) {
          existsCount++;
        }
        i++;
      } catch (ModelException e) {
        e.printStackTrace();
      }
    }
    assertEquals( existsCount , 0 );
  }

}

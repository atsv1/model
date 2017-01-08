package mp.utils;

import junit.framework.TestCase;

/**
 * User: atsv
 * Date: 07.11.2007
 */
public class TestNameService extends TestCase {

  public TestNameService( String name ){
    super( name );
  }

  public void testAddName(){
    String s1 = "1";
    String s2 = "2";
    NameService ns = ServiceLocator.GetNamesList();
    assertTrue( ns != null );
    int i1 = ns.GetNameIndex( s1 );
    int i2 = ns.GetNameIndex( s2 );
    assertTrue( i1 != i2 );
    String s3 = "1";
    int i3 = ns.GetNameIndex( s3 );
    assertTrue( i1 == i3 );
  }

  public void testAddUpperName(){
    String s1 = "test";
    String s2 = "tEsT";
    NameService ns = ServiceLocator.GetNamesList();
    assertTrue( ns != null );
    int i1 = ns.GetNameIndex( s1 );
    int i2 = ns.GetNameIndex( s2 );
    assertTrue( i1 == i2 );
  }

  public void testGetName(){
    String s1 = "1";
    String s2 = "2";
    NameService ns = ServiceLocator.GetNamesList();
    assertTrue( ns != null );
    int i1 = ns.GetNameIndex( s1 );
    ns.GetNameIndex( s2 );
    String s3 = ns.GetName( i1 );
    assertTrue( s3 != null ) ;
    assertTrue( s1.equalsIgnoreCase( s3 ) );

  }

}

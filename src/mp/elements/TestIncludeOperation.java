package mp.elements;

import java.io.IOException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;
import mp.parser.ModelExecutionContext;

public class TestIncludeOperation extends TestCase{
	
	 public static String FPathToXMLFiles = TestUtils.GetPath();
	
	protected static Model ReadModel(String aFileName) throws ModelException, IOException, SAXException{
		ModelTreeBuilder builder = new ModelTreeBuilder();
    builder.SetElementFactory( new ModelElementFactory() );
    builder.ReadModelTree( aFileName );
    return builder.GetRootModel();
    
  }
	
	/**
	 * проверяем правильность добавления одной переменной из include-модели
	 */
	public void testIncludeVariable() {		
		boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "incl_1_conf.xml" );     
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    Model mainModel = (Model) ModelExecutionContext.GetManager( "include_MainModel1" );
    assertTrue(mainModel != null);
    assertTrue( f );
    ModelBlock block = mainModel.Get("b1", 0);
    assertTrue(block != null);
    assertTrue( block.GetOutParam("incl_param") != null);
    assertEquals( block.GetIntValue("incl_param"), 15 );		
	}
	
	 

}

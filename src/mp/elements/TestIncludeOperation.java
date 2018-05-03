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
	
	/**
	 * проверяем добавлене блока в модель
	 */
	public void testIncludeBlock() {
		boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "incl_2_conf.xml" );     
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    Model mainModel = (Model) ModelExecutionContext.GetManager( "include_MainModel2" );
    assertTrue(mainModel != null);
    assertTrue( f );
    ModelBlock block = mainModel.Get("b3", 0);
    assertTrue(block != null);
    assertTrue( block.GetOutParam("incl_param") != null);    
    assertEquals( block.GetIntValue("incl_param"), 30 );
    // проверяем, что блок в include- модели имеет в  полном названии имя основной модели
    assertTrue( block.GetFullName().contains("include_MainModel2") );		
	}
	
	public void testAddStatechart() {
		boolean f = false;   
		Model model = null;
    try {
    	model = ReadModel( FPathToXMLFiles + "incl_3_conf.xml" );     
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    Model mainModel = (Model) ModelExecutionContext.GetManager( "include_MainModel3" );
    assertTrue(mainModel != null);
    assertTrue( f );
    mainModel.run();
    String error = model.GetErrorString();
    assertTrue( error == null );
    
    assertTrue( f );
    ModelBlock block = mainModel.Get("b1", 0);
    assertTrue(block != null);        
    assertEquals( block.GetIntValue("param1"), 11 );
    
		
	}
	
	 

}

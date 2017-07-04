package mp.elements;

import java.io.IOException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class TestCreateNewBlock extends TestCase {
	
	public TestCreateNewBlock( String testName ){
    super( testName );
  }
	
	public void testCreateNewBlock(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate1.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
	}

}

package mp.elements;

import junit.framework.TestCase;

public class TestDeleteBlock extends TestCase{
	
	public TestDeleteBlock( String testName ){
    super( testName );
  }
	
	public void testFirstTest() {
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockDelete1.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    int blocksBefore = mainModel.GetBlockCount("block");
    assertEquals(blocksBefore, 3 );
    mainModel.run();
    assertTrue(mainModel.GetErrorString() == null);
    int blockAfter = mainModel.GetBlockCount("block");
    assertEquals(blockAfter, 2 );
	}

	
}

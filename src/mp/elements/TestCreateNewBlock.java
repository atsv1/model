package mp.elements;

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
    int blocksBefore = mainModel.GetBlockCount("block");
    assertEquals(blocksBefore, 1 );
    
    mainModel.run();
    int blocksAfter = mainModel.GetBlockCount("block");
    assertEquals(blocksAfter, 2 );
	}

}

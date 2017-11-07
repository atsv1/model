package mp.elements;

import junit.framework.TestCase;

public class TestCreateNewBlock extends TestCase {
	
	public TestCreateNewBlock( String testName ){
    super( testName );
  }
	
	public void testCreateNewVerySimpleBlock(){
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
    assertTrue( mainModel.GetErrorString() == null );
    int blocksAfter = mainModel.GetBlockCount("block");
    assertEquals(blocksAfter, 2 );
    
    ModelBlock createdBlock = mainModel.Get("block", 1);
    assertTrue( createdBlock != null );
    
    ModelElement param = createdBlock.GetInnerParam("selfIndex");
    assertTrue(param != null);
    param = createdBlock.GetInnerParam("elementId");
    assertTrue(param != null);
    param = createdBlock.GetInnerParam("isForkMode");
    assertTrue(param != null);
	}
	
	public void testCreateNewBlock(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate2.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    
    ModelBlock createdBlock = mainModel.Get("block", 1);
    assertTrue( createdBlock != null );
    
    ModelElement param = createdBlock.GetInnerParam("inner1");
    assertTrue(param != null);
    AutomatState state1 = createdBlock.GetAutomatState(0);
    assertTrue(state1 != null);
    assertTrue( state1.GetName().equals("st1") );       
	}
	
	public void testCreateOtherBlock(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate3.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    
    ModelBlock createdBlock = mainModel.Get("blockToCreate", 1);
    assertTrue( createdBlock != null );		
	}
	
	public void testCreateAndExec(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate4.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    ModelBlock createdBlock = mainModel.Get("blockToCreate", 1);
    assertTrue( createdBlock != null ); 
    assertTrue( createdBlock.GetIntValue("inner1") >= 80 );
	}
	
	/**
	 * проверяется соединение созданного блока с другим, уже имеющимся в модели блоком
	 */
	public void testLinkNewBlock(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate5.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    ModelBlock createdBlock = mainModel.Get("blockToCreate", 1);
    assertTrue( createdBlock != null );
    ModelInputBlockParam inp1 = (ModelInputBlockParam) createdBlock.GetInpParam("inp1");
    assertTrue(inp1 != null);
    assertTrue( inp1.isLinked() );
    assertEquals( createdBlock.GetIntValue("inp1"), 987 );
	}
	
	public void testLinkNewBlockToSelfIndexBlock(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate6.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    ModelBlock createdBlock = mainModel.Get("blockToCreate", 1);
    assertTrue( createdBlock != null );
    assertEquals( createdBlock.GetIntValue("inp1"), 1 );
    
    createdBlock = mainModel.Get("block2", 1);
    assertTrue( createdBlock != null );
    assertEquals( createdBlock.GetIntValue("out1"), 1 );
    
    ModelBlock existBlock = mainModel.Get("blockToCreate", 0);
    assertTrue( existBlock != null );
    assertEquals( existBlock.GetIntValue("inp1"), 0 );		
	}
	
	public void testBlockChain(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate7.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    ModelBlock block = mainModel.Get("block3", 0);
    assertTrue( block != null );
    block = mainModel.Get("block3", 1);
    assertTrue( block == null );    
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    block = mainModel.Get("block3", 1);
    assertTrue( block != null );    
    block = mainModel.Get("block2", 1);
    assertTrue( block != null );
    block = mainModel.Get("blockToCreate", 1);
    assertTrue( block != null );
    assertTrue( block.GetIntValue("inner1") >= 80 );		
	}
	
	public void testCreateBlockWithMultiplexor(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate8.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    ModelBlock block = mainModel.Get("blockToCreate", 0);
    assertTrue( block != null );
    assertEquals( block.GetIntValue("inp1"), 9 );
    
    ModelBlock createdMultiplexor = mainModel.Get("mux1", 1);
    assertTrue(createdMultiplexor != null);
    
    
    ModelBlock createdBlock = mainModel.Get("blockToCreate", 1);
    assertTrue( createdBlock != null );
    assertEquals( createdBlock.GetIntValue("inp1"), 9 );		
	}
	
	/**
	 * проверяется работа созданного мультиплексора, когда создается блок-владелец мультиплексора, в котором есть выходные параметры, которые влияют на выбор в мультиплексоре
	 */
	public void testCreateMultiplexorWithOutOwnerParams(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate9.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    ModelBlock block = mainModel.Get("blockToCreate", 0);
    assertTrue( block != null );
    assertEquals( block.GetIntValue("inp1"), 0 );
    
    block = mainModel.Get("blockToCreate", 1);
    assertTrue( block != null );
    assertEquals( block.GetIntValue("inp1"), 1 );		
	}
	
	/**
	 * проверяем создание блока и его регистрацию в мультиплексоре в виде эталонного блока
	 */
	public void testCreateBlockAsMultiplexorEtalon(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate10.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    ModelBlock block = mainModel.Get("block2", 0);
    assertTrue( block != null );
    assertEquals( block.GetIntValue("curInpValue"), 1 );
    assertEquals( block.GetIntValue("changeCounter"), 2 );		
	}
	
	/**
	 * проверяется создание блока, в цепочке связей которого небходимо создать мультиплексор, а потом создается блок эталон для мультиплексора
	 */
	public void testFullBlockChainWithMux(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate11.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    ModelBlock block = mainModel.Get("blockToCreate", 1);
    assertTrue( block != null );
    assertEquals( block.GetIntValue("inp1"), 100 );
    
    block = mainModel.Get("etalonBlock", 10);
    assertTrue( block != null );
    assertEquals( block.GetIntValue("etalonOutParam"), 100 );
	}
	
	/**
	 * проверка создания блока, в котором есть скрипт, содержащий var-секцию
	 */
	public void testVarInScript(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate12.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    ModelBlock block = mainModel.Get("blockToCreate", 0);
    assertTrue( block != null );
    int v0 = block.GetIntValue("result");
    block = mainModel.Get("blockToCreate", 1);
    assertTrue( block != null );
    int v1 = block.GetIntValue("result");
    assertEquals(v0-5, v1);
	}
	
	/**
	 * проверяем цепочку создания блоков, в которых один из блоков находится в другой модели, но к нему есть обращение из созданного блока по selfIndex
	 */
	public void testCreateBlocksInDifferentModels(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate13_main.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
		
		ModelBlock block = mainModel.Get("block", 0); // блок, из которого вызывалась процедура создания блока
		assertTrue(block != null);
		block = mainModel.Get("block", 1);
		assertTrue(block != null);
	}
	
	/**
	 * проверяем создание нового блока и правильность заполнения в нем параметра, в который должен записаться индекс блока-родителя
	 */
	public void testCreateBlock_ParentBlockParam(){
		mp.parser.ModelExecutionContext.ClearExecutionContext();
		boolean f = false;    
		Model mainModel = null;
    try {
      ModelTreeBuilder builder = new ModelTreeBuilder();
      builder.SetElementFactory( new ModelElementFactory() );
      builder.ReadModelTree( ModelMuxTest.FPathToXMLFiles + "blockCreate14.xml" );
      mainModel = builder.GetRootModel();
      f = true;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    assertTrue( f );
    assertTrue(mainModel != null);
    
    ModelBlock block = mainModel.Get("block", 9);
    assertTrue(block != null);
    block = mainModel.Get("block", 10);
    assertTrue(block == null);
        
    mainModel.run();
    assertTrue( mainModel.GetErrorString() == null );
    
    block = mainModel.Get("block", 10);
    assertTrue(block != null);
    assertEquals(block.GetIntValue("inp1"), 10);
    
		
	}
	
	

}

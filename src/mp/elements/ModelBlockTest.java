package mp.elements;


/**
 */

import junit.framework.TestCase;
import org.xml.sax.SAXException;

import java.io.IOException;

import mp.parser.*;
import mp.gui.*;
import mp.utils.ServiceLocator;

public class ModelBlockTest extends TestCase {

   public ModelBlockTest(String testName) {
    super(testName);
  }

  public void testVariables(){
    Variable var1 = new Variable(0);
    Variable var2 = new Variable(2);
    Variable var3;
    assertTrue( !var1.equals(var2) );
    try {
      var1.StoreValueOf( var2 );
      assertTrue( var1.equals( var2 ) );
      assertTrue( var1.equals( var1 ) );
      assertTrue( var2.equals( var2 ) );
      var3 = (Variable) var2.clone();
      assertTrue( var2.equals( var3 ) );

    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }

  public void testElementsContainer(){
    ModelElementContainer container = new ModelElementContainer();
    ModelElement element1 = ModelElement.CreateModelElement(null,"�������1",1);
    ModelElement element2 = ModelElement.CreateModelElement(null,"�������2",2);
    boolean f = false;
    try {
      container.AddElement( element1 );
      container.AddElement( element2 );
      f = true;
    } catch (ModelException e) {
      System.out.println(e.getMessage());
    }
    assertTrue(f);
    ModelElement element3 = container.Get("�������1");
    assertEquals( element1,element3 );
    element3 = new ModelElement( null, "�������1",3 );
    f = false;
    try{
       container.AddElement( element3 );
      f = true;
    } catch (ModelException e1) {
      //System.out.println(e1.getMessage());
    }
    assertTrue(!f);
  }

  public void testVariableCreate(){
    ModelCalculatedElement element = new ModelCalculatedElement(null, "var1",1);
    Variable var1 = null;
    boolean f = false;
    int i = 4;
    try{
      element.SetVarInfo("integer","0");
      var1 = element.GetVariable();
      i = var1.GetIntValue();
      f = true;
    } catch (ModelException e){
      System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
    }
    assertTrue(f);
    assertEquals("var1", var1.GetName());
    assertEquals( i,0 );
  }

  private static void setLanguageExtToBlock( ModelBlock block ) throws ScriptException, ModelException {
    ModelLanguageBuilder builder = new ModelLanguageBuilder( null );
    builder.UpdateBlock( block );
  }

  public void testCalculatedParam(){
    //�������� ��������-�����, �������� ����������� ������ ��������
    ModelSimpleBlock block = new ModelSimpleBlock(null, "����1", ServiceLocator.GetNextId());
    //�������� ��������������� ��������
    ModelCalculatedElement element = new ModelCalculatedElement(block, "var1", ServiceLocator.GetNextId());

    boolean f = false;
    int i = 90;
    try{
      //���������� ���������� �������� � ����
      block.AddInnerParam( element );
      //�������� ���������� � �����
      element.SetVarInfo("integer","0");
      //var1 = element.GetVariable();
      setLanguageExtToBlock( block );
      //�������� ��������� ������� (��� ����������)
      element.SetSourceCode("var1 := 3+4;");
    //����� ������� �������� �������� ���������
      element.Update();
    //��������� ������������� ��������
      i = element.GetVariable().GetIntValue();
    //�������� � ���������
      f = true;
    } catch (ModelException e){
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
      e1.printStackTrace();
    }
    assertTrue(f);
    assertEquals( i, 7 );
  }

  /** ���� ��������� ����������������� ���� ��������� ����������. ��������� ���� ����, � ��� ���������
   * ��� ���������� ���������. ������ �������� �� ������� �� �� ����, ������ � ����� ����������� ���������� ������.
   * ����� ������� ��������� ������ ��������, ����� ������. ��������� ������� ���������.
   */
  public void testCalculationLinkedParam(){
    ModelSimpleBlock block = new ModelSimpleBlock(null,"����1",1);
    ModelCalculatedElement element1 = new ModelCalculatedElement(block,"var1",2);
    ModelCalculatedElement element2 = new ModelCalculatedElement(block, "var2",3);
    boolean f = false;
    int var1 = 0;
    int var2 = 0;
    try{
      block.AddInnerParam( element1 );
      element1.SetVarInfo("integer","0");
      element2.SetVarInfo("integer","0");
      block.AddInnerParam( element2 );
      setLanguageExtToBlock( block );
      element1.SetSourceCode("var1 := 5 + 10;");
      element2.SetSourceCode("var2 := var1 - 7;");
      element1.Update();
      element2.Update();
      var1 = element1.GetVariable().GetIntValue();
      var2 = element2.GetVariable().GetIntValue();
      f = true;
    } catch(ModelException e){
      System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
    }
    assertTrue(f);
    assertEquals(var1,15);
    assertEquals(var2,8);
  }

  public void testCyclesLink(){
    ModelSimpleBlock block = new ModelSimpleBlock(null,"����1",1);
    ModelCalculatedElement element1 = new ModelCalculatedElement(block,"var1",2);
    ModelCalculatedElement element2 = new ModelCalculatedElement(block, "var2",3);
    boolean f = false;
    int var1 = 0;
    int var2 = 0;
    try{
      block.AddInnerParam( element1 );
      element1.SetVarInfo("integer","0");
      element2.SetVarInfo("integer","0");
      block.AddInnerParam( element2 );
      setLanguageExtToBlock( block );
      element1.SetSourceCode("var1 := var2 +  5 + 10;");
      element2.SetSourceCode("var2 := var1 - 7;");
      element1.Update();
      element2.Update();
      //������ ����
      element1.Update();
      element2.Update();
      var1 = element1.GetVariable().GetIntValue();
      var2 = element2.GetVariable().GetIntValue();
      f = true;
    } catch(ModelException e){
      System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
    }
    assertTrue(f);
    //�������� ������������ ���������������� ����������� ������������ ����� �����������.
    // element2 ������� �� element1
    assertTrue( element2.IsInputParam( element1 ) );
    assertTrue( element1.IsInputParam( element2 ) );
    //���������, ����� �������� �� �������� ���� �� ����
    assertTrue( !element1.IsInputParam( element1 ) );
    assertTrue( !element2.IsInputParam( element2 ) );
    assertEquals(var1,23);
    assertEquals(var2,16);
    assertTrue( !element2.IsNeedToUpdate() );
    assertTrue( element1.IsNeedToUpdate() );

  }

  /** ���� ��������� ������������ �������� � ������� ������ ����������. ��������� ��� �����, � ������ �� ���������
   * ����������. � ����� ����� ���������� ������ ������������ ���������� �� ������� �����. ������ ������ ������ ������
   *
   */
  public void testErrorConnected(){
    ModelSimpleBlock block1 = new ModelSimpleBlock(null,"����1",1);
    ModelCalculatedElement element1 = new ModelCalculatedElement(block1,"var1",2);
    ModelSimpleBlock block2 = new ModelSimpleBlock(null,"����2",1);
    ModelCalculatedElement element2 = new ModelCalculatedElement(block2, "var2",3);
    boolean f = false;
    try{
      block1.AddInnerParam( element1 );
      element1.SetVarInfo("integer","0");
      element2.SetVarInfo("integer","0");
      block2.AddInnerParam( element2 );
      element1.SetSourceCode("var1 := 5 + 10;");
      element2.SetSourceCode("var2 := var1 - 7;");
      f = true;
    } catch(ModelException e){
      //System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      //System.out.println(e1.getMessage());
    }
    assertTrue(!f);
  }

  /** ���� ��������� ������������ ��������  ���������� ������� ���������� ��� ���������� ������ ������ �����.
   * ��� ����� ������� ���� ����. ������� ���� ����������� �� �� ���� ��������.
   */
  public void testOrderCreate1(){
    ModelSimpleBlock block = new ModelSimpleBlock(null,"����1",1);
    ModelCalculatedElement element = new ModelCalculatedElement(block,"var1",2);
    boolean f = false;
    try{
      block.AddInnerParam( element );
      element.SetVarInfo("integer","0");
      setLanguageExtToBlock( block );
      element.SetSourceCode("var1 := 5 + 10;");
      block.Execute();
      f = true;
    } catch(ModelException e){
      System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
    }
    ModelElement element1 = block.FOrderParamsList.get(0);
    assertTrue(f);
    assertEquals( element,element1 );
  }

  /**��������� ������������ ���������� ���������� ���������� � ���������� ������ ���������� ����������. ������
   * ���� ����������� ������� � ��� ��������� �� ����.
   *
   */
  public void testOrderCreate2(){
    ModelSimpleBlock block = new ModelSimpleBlock(null,"����1", ServiceLocator.GetNextId());
    ModelCalculatedElement element1 = new ModelCalculatedElement(block,"var1", ServiceLocator.GetNextId());
    ModelCalculatedElement element2 = new ModelCalculatedElement(block,"var2", ServiceLocator.GetNextId());
    ModelCalculatedElement element3 = new ModelCalculatedElement(block,"var3", ServiceLocator.GetNextId());
    ModelCalculatedElement element4 = new ModelCalculatedElement(block,"var4", ServiceLocator.GetNextId());
    boolean f = false;
    try{
      block.AddInnerParam( element1 );
      block.AddInnerParam( element2 );
      block.AddInnerParam( element3 );
      block.AddInnerParam( element4 );

      element1.SetVarInfo("integer","0");
      element2.SetVarInfo("integer","0");
      element3.SetVarInfo("integer","0");
      element4.SetVarInfo("integer","0");
      setLanguageExtToBlock( block );

      element1.SetSourceCode("var1 := 5 + 10;");
      element2.SetSourceCode("var2 := var1 + 3;");
      element3.SetSourceCode("var3 := var1 - 7;");
      element4.SetSourceCode("var4 := var3 + 9;");
      block.Execute();
      f = true;
    } catch(ModelException e){
      System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
    }
    ModelElement testedElement1 = block.FOrderParamsList.get(0);
    assertTrue(f);
    assertEquals( element1,testedElement1 );
    //��������� ������������ ���������� ���� ������ ����������
    assertEquals( 4, block.FOrderParamsList.size() );
    ModelElement testedElement4 = block.FOrderParamsList.get(3);
    assertEquals(element4, testedElement4);
  }

  /**����������� ����������� ����������� ����� �����������.
   * ��� ����� ����������� ��� ���������� ���������.
   */
  public void testOrderCreateCycle(){
    ModelSimpleBlock block = new ModelSimpleBlock(null,"����1", ServiceLocator.GetNextId());
    ModelCalculatedElement element1 = new ModelCalculatedElement(block,"var1", ServiceLocator.GetNextId());
    ModelCalculatedElement element2 = new ModelCalculatedElement(block,"var2", ServiceLocator.GetNextId());
    ModelCalculatedElement element3 = new ModelCalculatedElement(block,"var3", ServiceLocator.GetNextId());
    ModelCalculatedElement element4 = new ModelCalculatedElement(block,"var4", ServiceLocator.GetNextId());
    boolean f = false;
    try{
      block.AddInnerParam( element1 );
      block.AddInnerParam( element2 );
      block.AddInnerParam( element3 );
      block.AddInnerParam( element4 );
      element1.SetVarInfo( "integer","3" );
      element2.SetVarInfo("integer", "1");
      element3.SetVarInfo("integer", "1");
      element4.SetVarInfo("integer", "1");
      setLanguageExtToBlock( block );

      element1.SetSourceCode("var1 := 8 + 9;");
      element2.SetSourceCode("var2 := var2 + 8;");
      element3.SetSourceCode("var3 := var2 * 3;");
      element4.SetSourceCode("var4 := var2 * 5;");

      block.Execute();
      f = true;
    } catch (ModelException e){
      System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
    }
    assertTrue( f );
    ModelCalculatedElement testedElement4 = (ModelCalculatedElement) block.FOrderParamsList.get(3);
    assertEquals( element4, testedElement4 );
    ModelCalculatedElement testedElement1 = (ModelCalculatedElement) block.FOrderParamsList.get(0);
    assertEquals( element1, testedElement1 );
  }

  /**����������� ������������ ����� ����� ����� �������.
   * ��� ����� ��������� ��� �����. � ������ ������ ���� ����������, ������� ����� ����������� ���� ��������.
   * �� ������ ����� ����� ������� �������� � ����������, ������� ����� �������� �� ������� ����������
   *
   */
  public void testBlockLink(){
    ModelSimpleBlock block1 = new ModelSimpleBlock(null,"����1",1);
    ModelSimpleBlock block2 = new ModelSimpleBlock(null,"����2",2);
    ModelCalculatedElement block1Element = new ModelCalculatedElement(block1,"var1",3);
    ModelCalculatedElement block2Element = new ModelCalculatedElement(block2,"var2",4);
    ModelInputBlockParam block2inp = new ModelInputBlockParam(block2, "inp1",5);
    boolean f = false;
    int i = 0;
    int i2 = 0;
    try{
      block1.AddInnerParam( block1Element );
      block1Element.SetVarInfo("integer","4");
      setLanguageExtToBlock( block1 );
      block1Element.SetSourceCode("var1 := var1 + 1;");

      block2.AddInpParam( block2inp );
      block2inp.SetVarInfo("integer","1");
      block2inp.Link( block1, block1Element );

      block2.AddInnerParam( block2Element );
      block2Element.SetVarInfo("integer","1");
      setLanguageExtToBlock( block2 );
      block2Element.SetSourceCode("var2 := inp1 + 1");
      block1.Execute();
      block2.Execute();
      i = block2Element.GetVariable().GetIntValue();
      i2 = block1Element.GetVariable().GetIntValue();
      f = true;

    } catch(ModelException e){
      System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
    }
    assertTrue(f);
    ModelElement testedElement = block2.FOrderParamsList.get(0);
    assertEquals( testedElement, block2inp );
    assertEquals(i,6);
    assertEquals(i2,5);

    int inp = 0;
    try{
      block1.Execute();
      block2.Execute();
      i = block2Element.GetVariable().GetIntValue();
      i2 = block1Element.GetVariable().GetIntValue();
      inp = block2inp.GetVariable().GetIntValue();
    } catch (ScriptException e){
      System.out.println(e.getMessage());
    }
    catch (ModelException e1){
      System.out.println(e1.getMessage());
    }
    assertEquals(i2,6);
    assertEquals( inp, 6 );
    assertEquals(i,7);
  }

  public void testClassContainer(){
    ModelElementClassesContainer container = new ModelElementClassesContainer();
    ModelElement element = new ModelElement(null,"1",1);
    try {
      container.AddElement( element );
    } catch (ModelException e) {
      e.printStackTrace();
    }
  }

  /**����������� ������������ �������� � ����� ����������� ���������� elementId, ������� ������� ��
   * ���������� ������������� �������� ������
   *
   */
  public void testCreateElementId(){
    ModelSimpleBlock block = new ModelSimpleBlock(null, "block", 1);
    ModelBlockParam param = null;
    boolean f = false;
    try {
      ModelLanguageBuilder.AddElementIdVariable( block );
      param = (ModelBlockParam) block.Get("elementId");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( param != null );
    int i = -1;
    f = false;
    try {
      i = param.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i, 1 );
  }

  //***********************************************************************
  //************������������ ������ ������ �� XML**************************
  //***********************************************************************

  public static String FPathToXMLFiles = TestUtils.GetPath();

  public void testOpenEmptyXML(){
    boolean f = false;
    ModelElement element = null;
    ModelElementFactory elementFactory;
    try {
      elementFactory = new ModelElementFactory();
      ModelXMLReader reader = new ModelXMLReader( elementFactory );
      reader.ReadModel( FPathToXMLFiles + "file1.xml" );
      element = (Model)reader.GetRootElement();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    catch (SAXException e){
      e.printStackTrace();
    }
    catch (IOException e){
      e.printStackTrace();
    }
    assertTrue(f);
    assertTrue( element != null );
    Class cl = element.getClass();
    assertTrue("mp.elements.Model".equalsIgnoreCase( cl.getName() ));
    assertTrue(  element.GetName().equalsIgnoreCase("������1") );
  }

  /**������ xml � �������� ������.
   */
  public void testReadBlockList(){
    boolean f = false;
    Model rootModel = null;
    int blockCount = 0;
    try {
      ModelElementFactory elementFactory = new ModelElementFactory();
      ModelXMLReader reader = new ModelXMLReader(elementFactory);
      reader.ReadModel( FPathToXMLFiles + "file2.xml" );
      rootModel = (Model) reader.GetRootElement();
      blockCount = rootModel.size();
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
      System.out.println( e.getMessage() );
    }
    catch (SAXException e){
      e.printStackTrace();
    }
    catch (IOException e){
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(blockCount,5);
    assertTrue( rootModel.Get("����1") != null );
    assertTrue( rootModel.Get("����2") != null );
    assertTrue( rootModel.Get("����3") == null );
    assertTrue( rootModel.Get("����3",0) != null );
    assertTrue( rootModel.Get("����3",1) != null );
    assertTrue( rootModel.Get("����3",2) != null );
    assertTrue( rootModel.Get("����3",3) == null );
  }

  /**�������� ������������ �������� ���������� ������ � ��������������� ����������� ������ ���
   */
  public void testReadParamsList(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
     boolean f = false;
    Model rootModel = null;
    int inp1Init = 0;
    int inp2Init = 0;
    int var1init = 0;
    int var1exec = 0;
    ModelSimpleBlock block = null;
    try {
      ModelElementFactory elementFactory = new ModelElementFactory();
      ModelXMLReader reader = new ModelXMLReader( elementFactory );
      reader.ReadModel( FPathToXMLFiles + "file3.xml" );
      rootModel = (Model) reader.GetRootElement();
      rootModel.ApplyNodeInformation();
      block = (ModelSimpleBlock) rootModel.Get("����1");
      inp1Init = ((ModelInputBlockParam)block.Get("inp1")).GetVariable().GetIntValue();
      inp2Init = ((ModelInputBlockParam)block.Get("inp2")).GetVariable().GetIntValue();
      var1init = ((ModelCalculatedElement)block.Get("var1")).GetVariable().GetIntValue();
      block.Execute();
      var1exec = ((ModelCalculatedElement)block.Get("var1")).GetVariable().GetIntValue();
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
      System.out.println( e.getMessage() );
      e.printStackTrace();
    }
    catch (ScriptException e){
      System.out.println( e.getMessage() );
      e.printStackTrace();
    }
    catch (SAXException e){
      e.printStackTrace();
    }
    catch (IOException e){
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(inp1Init, 1);
    assertEquals( inp2Init,2 );
    assertEquals( var1init, 1);
    assertEquals( var1exec, 8);
  }

  public void testBlockLinks(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model rootModel = null;
    try {
      ModelElementFactory elementFactory = new ModelElementFactory();
      ModelXMLReader reader = new ModelXMLReader( elementFactory );
      reader.ReadModel( FPathToXMLFiles + "file4.xml" );
      rootModel = (Model) reader.GetRootElement();
      rootModel.ApplyNodeInformation();
      rootModel.Execute();
      rootModel.Execute();
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
      System.out.println( e.getMessage() );
    }
    catch (ScriptException e){
      System.out.println( e.getMessage() );
    }
    catch (SAXException e){
      e.printStackTrace();
    }
    catch (IOException e){
      e.printStackTrace();
    }
   assertTrue( f );
    try {
      ModelElement var1 = rootModel.Get("����2").Get("var1");
      ModelElement var2 = rootModel.Get("����2").Get("var2");
      ModelElement var1Owner = var1.GetOwner();
      ModelElement var2Owner = var2.GetOwner();
      assertEquals(  var1Owner, var2Owner );
      int i = ((ModelCalculatedElement)rootModel.Get("����1").Get("var11")).GetVariable().GetIntValue();
      int var2_1 = ((ModelCalculatedElement)rootModel.Get("����2").Get("var1")).GetVariable().GetIntValue();
      int var2_2 = ((ModelCalculatedElement)rootModel.Get("����2").Get("var2")).GetVariable().GetIntValue();

      assertEquals( var2_1,11 );
      assertTrue( (var2_2 == 22 || var2_2 == 12) );


      //((ModelBlockParam)rootModel.Get("����1").Get("inp1")).GetVariable().GetIntValue();
      //assertEquals( inp1,22 );
      assertEquals( i, 14);
    } catch (ScriptException e) {

    } catch (ModelException e) {

    }
  }

  public void testCreateForm_1(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    String rootClassName = null;
    ModelXMLReader reader = null;
    Model model = null;
    ModelDirectConnector connector = null;
    try{
      ModelElementFactory modelElementFactory = new ModelElementFactory();
      reader = new ModelXMLReader( modelElementFactory );
      reader.ReadModel( FPathToXMLFiles + "file4.xml" );
      model = (Model) reader.GetRootElement();
      model.ApplyNodeInformation();

      connector = new ModelDirectConnector( model );
      ModelGUIElementFactory elementFactory = new ModelGUIElementFactory();
      elementFactory.SetConnector( connector );
      reader = new ModelXMLReader( elementFactory );
      reader.ReadModel( FPathToXMLFiles + "formExample1.xml" );

      Object rootElement = reader.GetRootElement();
      rootClassName = rootElement.getClass().getName();
    } catch (Exception e) {
      System.out.println( e.getMessage() );
    }
    assertTrue( reader != null);
    Object rootElement = reader.GetRootElement();
    rootClassName = rootElement.getClass().getName();
    assertEquals( "mp.gui.StandartForm", rootClassName );
  }

  protected static Model ReadModel(String aFileName) throws ModelException, IOException, SAXException{
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model result = null;
    ModelElementFactory modelElementFactory = new ModelElementFactory();
    ModelXMLReader reader = new ModelXMLReader( modelElementFactory );
    reader.ReadModel( aFileName );
    result = (Model) reader.GetRootElement();
    result.ApplyNodeInformation();
    result.InitAllBlockStatecharts();
    return result;
  }

  private static StandartForm ReadForm(String aFileName, Model aModel) throws ModelException, IOException, SAXException {
    ModelXMLReader formReader = null;
    StandartForm result = null;
    ModelDirectConnector connector = new ModelDirectConnector( aModel );
    ModelGUIElementFactory elementGUIFactory = new ModelGUIElementFactory();
    elementGUIFactory.SetConnector( connector );
    formReader = new ModelXMLReader( elementGUIFactory );
    formReader.ReadModel( aFileName );
    result = (StandartForm) formReader.GetRootElement();
    return result;
  }

  public void testLabelUpdate(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    int i = 0;
    try {
      Model model = ReadModel(FPathToXMLFiles + "file5.xml");
      ModelDirectConnector connector = new ModelDirectConnector(model);
      ModelGUILabel label = new ModelGUILabel();
      label.SetConnector( connector );
      ModelAddress address = new ModelAddress("����1", -1, "var1");
      label.SetModelAddress( address );
      model.Execute();
      label.Update();
      i = (int) label.GetCurrentValue();
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertEquals( i,2 );
  }

  public void testGraph1(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model;
    StandartForm form;
    ModelGUIAbstrElement element = null;
    try {
      model = ReadModel(FPathToXMLFiles + "form1Model.xml");
      form = ReadForm( FPathToXMLFiles + "form1.xml", model );
      ModelGUIAbstrElement tabSheets = form.GetElement(0);
      ModelGUIAbstrElement tabSheet = tabSheets.GetElement(0);
      element = tabSheet.GetElement(0);
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( element != null );
    assertTrue( "mp.gui.ModelGUIJFreeGraph".equalsIgnoreCase( element.getClass().getName() ) );
    /*ModelGUIGraph graph = (ModelGUIGraph) element;
    assertTrue( Double.compare( graph.GetMaxX(),20 ) == 0 );
    double maxY = graph.GetMaxY();
    assertTrue( Double.compare( maxY,20 ) == 0 );
    double minY = graph.GetMinY();
    assertTrue( Double.compare( minY,-20 ) == 0 );
    double minX = graph.GetMinX();
    assertTrue( Double.compare( minX,-1 ) == 0 );*/
  }

   public void testGraph2(){
     mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model;
    StandartForm form;
    ModelGUIAbstrElement element = null;
    try {
      model = ReadModel(FPathToXMLFiles + "form2Model.xml");
      form = ReadForm( FPathToXMLFiles + "form2.xml", model );
      ModelGUIAbstrElement tabSheets = form.GetElement(0);
      ModelGUIAbstrElement tabSheet = tabSheets.GetElement(0);
      element = tabSheet.GetElement(0);
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( element != null );
    assertTrue( "mp.gui.ModelGUIJFreeGraph".equalsIgnoreCase( element.getClass().getName() ) );
    /*ModelGUIGraph graph = (ModelGUIGraph) element;
    assertTrue( Double.compare( graph.GetMaxX(),21 ) == 0 );
    double maxY = graph.GetMaxY();
    assertTrue( Double.compare( maxY,12 ) == 0 );
    double minY = graph.GetMinY();
    assertTrue( Double.compare( minY,-14 ) == 0 );
    double minX = graph.GetMinX();
    assertTrue( Double.compare( minX,-1 ) == 0 );*/
  }

  /**��������� ������������ ����������� ������ editBox. ������� ����������� � ����������� ��������
   */
  public void testEditBoxConnectionError(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model;
    boolean f = false;
    try {
      model = ReadModel(FPathToXMLFiles + "form3Model.xml");
      ReadForm( FPathToXMLFiles + "form3.xml", model );
      f = true;
    } catch (ModelException e) {
    } catch (IOException e) {
    } catch (SAXException e) {
    }
   assertTrue(!f);
  }

  /**��������� ���������� ����������� ������ editBox.
    */
  public void testEditBoxConnection(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model;
    boolean f = false;
    try {
      model = ReadModel(FPathToXMLFiles + "form4Model.xml");
      ReadForm( FPathToXMLFiles + "form4.xml", model );
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    }
   assertTrue(f);
  }



  public void testTimeCompare(){
    ModelTime time1 = new ModelTime(1);
    ModelTime time2 = new ModelTime(2);
    int i = time1.Compare( time2 );
    assertEquals( i, ModelTime.TIME_COMPARE_LESS );
    i = time2.Compare( time1 );
    assertEquals( i, ModelTime.TIME_COMPARE_GREATER);
    i = time1.Compare( new ModelTime(1) );
    assertEquals( i, ModelTime.TIME_COMPARE_EQUALS );
    i = time1.Compare( null );
    assertEquals( i, ModelTime.TIME_COMPARE_UNKNOWN );

  }


  /** ����������� ����������������� ������ ModelSimpleBlock.Execute(ModelTime) ��� ������, ����� � ����� ��� �� ������
   * ����������.
   * ��������� ���� � �������� �����������, � ��������� ��� ������ ��� ���� ���������� ����������� �����. ������
   * ���������� ������� - ������ �������. �������� ���������� ������ ���������� ������ ����� ���������� ����� �����
   * �������
   */
  public void testExecutingWithoutStatechart(){
    ModelSimpleBlock block = new ModelSimpleBlock(null,"����1",1);
    ModelCalculatedElement element = new ModelCalculatedElement(block,"var1",2);
    ModelTime time = new ModelTime(0);
    boolean f = false;
    int i = 9;
    try{
      block.AddInnerParam( element );
      element.SetVarInfo("integer","0");
      setLanguageExtToBlock( block );
      element.SetSourceCode("var1 := var1 + 1;");
      block.Execute( time );
      i = element.GetVariable().GetIntValue();
      f = true;
    } catch(ModelException e){
      System.out.println(e.getMessage());
    }
    catch (ScriptException e1){
      System.out.println(e1.getMessage());
    }
    assertTrue(f);
    assertEquals(i,1);
    f = false;
    //���������� ��������, �������� ������� 1, �������� ����������� ���������� �� ������ ����������
    time.Add( 0.5 );
    try {
      block.Execute( time );
      i = element.GetVariable().GetIntValue();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(i,1);
    //������ ����� �������� ���������� ������� ����� ������, ��� ��� ������� ��������
    time.Add( 0.5 );
    try {
      block.Execute( time );
      i = element.GetVariable().GetIntValue();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(i,2);
  }

  /** ����������� ������������ �������� ��������� ���������� � �����.
   *  ����������� ������������ �������� ���������� selfIndex � ������������ ���������� �� ��������.
   * � ������ ����� ������������ �������� ������ ������, ��������� � ����������� ����������
   */
  public void testCreatingServiceVariable(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ReadModel(FPathToXMLFiles + "file6.xml");
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    ModelBlock block = (ModelBlock) model.Get( "����1" );
    f = false;
    int i = -1;
    try {
      ModelBlockParam selfIndex = (ModelBlockParam) block.Get("selfIndex");
      assertTrue( selfIndex != null );
      i = selfIndex.GetVariable().GetIntValue();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(i,0);
  }

  public void testCreatingServiceVariables(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ReadModel(FPathToXMLFiles + "file7.xml");
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    ModelBlock block;
    ModelBlockParam param = null;
    f = false;
    int sum = 0;
    int i = 0;
    while ( i < 4 ){
      block = model.Get("����1",i);
      try {
        param = (ModelBlockParam) block.Get("selfindex");
        assertTrue(param != null);
      } catch (ModelException e) {
        e.printStackTrace();
      }
      try {
        assertTrue( param != null );
        sum = sum + param.GetVariable().GetIntValue();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      i++;
    }//while
    assertEquals(sum, 6);
  }

  /** ����������� ������������ ���������� ����� �������. ����������� �������� � ���������:
   * 1. ����������� ��� ������ ������ (��� ������ ������, ���������� ����������� ������� ������ 1).
   * 2. �� �������� ����������, � ���������, ������� ��������� ����-��������, ������� �������� ����� selfindex, �.�.
   * ������ ������������� ������������� � �����, �������� �� �� �������� ��������� selfindex, ��� � ����, �������
   * ���������� �����������
   * ��� �������� ��������� ��������� ��������:
   * 1. ��������� ���� ������ ���������� � ���� ������ ����������
   * 2. ������ �� ������ ���������� ������������ � ���������������� ��������� (������������ ������������ �� ����������
   * ��������)
   * 3. ������������� ������������ � ����������, � ������� �������� �������� ���������� selfindex
   * 4. ����������� ���������� �������� ���������� �� ����� � ����������� ���������� selfindex ��� ���� ����
   * ������-����������
   *
   */
  public void testSelfIndexBlockLink(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ReadModel(FPathToXMLFiles + "file18.xml");
      assertTrue( model != null );
      model.Execute();
      model.Execute();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    int currentIndexValue = 0;
    ModelBlockParam currentIndexValueParam = null;
    int i = 0;
    ModelBlock block = null;
    ModelBlockParam currentInputParam = null;
    int currentInputValue = 0;
    while ( i <= 4 ){
      f = false;
      block = model.Get("��������", i);
      try {
        currentIndexValueParam = (ModelBlockParam) block.Get("selfIndex");
        currentIndexValue = currentIndexValueParam.GetVariable().GetIntValue();
        currentInputParam = (ModelBlockParam) block.Get("testInput");
        currentInputValue = currentInputParam.GetVariable().GetIntValue();
        //System.out.println("currentIndexValue = " + Integer.toString(currentIndexValue) + " currentInputValue = " + Integer.toString(currentInputValue));
        f = true;
        assertEquals( currentIndexValue, currentInputValue );
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      i++;
    }

  }

  /**�������� ���������� ����� ������. ��������� ��������� ��������, ��� ������� ������-���������� ��������� ������, ���
   * ������-����������
   *
   */
  public void testSelfIndexErrorLink(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    //Model model = null;
    boolean f = false;
    try {
      ReadModel(FPathToXMLFiles + "file19.xml");
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      //e.printStackTrace();
    } catch (SAXException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  /**
   * �������� ����������� ������������� ����������  ����������.
   * � ������ ����� �������� � ����� block2 �� ����� ���������� ���������� ������������ �� ���� ������ block1
   */
  public void testReLinkCommand(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ReadModel(FPathToXMLFiles + "file63.xml");
      model.RegisterModelInContext();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("block2");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("linkIndex"), 0 );
    assertEquals( block.GetIntValue("inpParam"), -1 );

    f = false;
    try {
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 0 );
      assertEquals( block.GetIntValue("linkIndex"), 1 );
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 1 );
      assertEquals( block.GetIntValue("linkIndex"), 2 );
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 2 );
      assertEquals( block.GetIntValue("linkIndex"), 3 );
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 3 );
      assertEquals( block.GetIntValue("linkIndex"), 4 );
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 4 );
      assertEquals( block.GetIntValue("linkIndex"), 5 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 2 );
      assertEquals( block.GetIntValue("linkIndex"), 3 );
      f = true;
    } catch (ScriptException e) {
    } catch (ModelException e) {
    }
    assertTrue( !f );

  }

  /**
   * �������� ����������� ������������� ����������  ������������ ����������.
   *
   */
  public void testReLinkMaterialParams(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ReadModel(FPathToXMLFiles + "file64.xml");
      model.RegisterModelInContext();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("block2");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("linkIndex"), 0 );
    assertEquals( block.GetIntValue("inpParam"), 0 );

    f = false;
    try {
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 10 );
      assertEquals( block.GetIntValue("linkIndex"), 1 );
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 20 );
      assertEquals( block.GetIntValue("linkIndex"), 2 );
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 30 );
      assertEquals( block.GetIntValue("linkIndex"), 3 );
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 40 );
      assertEquals( block.GetIntValue("linkIndex"), 4 );
      model.Execute();
      model.Execute();
      assertEquals( block.GetIntValue("inpParam"), 50);
      assertEquals( block.GetIntValue("linkIndex"), 5 );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }

  }



  ///////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////// �������� ������������� ���������  ////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////



  /** ������������ �������� ������������ �������� ������������� ���������.
   *  ��� ����� ����������� ���� ������, � ������� ���������� � ������������ � �������������� ��������.
   */
  public void testMaterialParamCreating(){
    Model model = null;
    ModelBlock block = null;
    ModelBlockParam matParam = null;
    ModelBlockParam infParam = null;
    boolean f = false;
    try {
      model = ReadModel( FPathToXMLFiles + "file10.xml" );
      block = (ModelBlock) model.Get("����1");
      matParam = (ModelBlockParam) block.Get("material1");
      infParam = (ModelBlockParam) block.Get("var2");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( matParam != null );
    assertTrue( infParam != null );
    assertTrue( matParam.GetParamType() ==  ModelBlockParam.PARAM_TYPE_MATERIAL );
    assertTrue( infParam.GetParamType() ==  ModelBlockParam.PARAM_TYPE_INFORM );
  }

  /**����������� ������������ ��������� ��������� ��������: ����� ������������ �������� � �������� ��������
   * �������� ������������ � ��������������� ���������-���������.
   * ��������� ������ �������� ������.
   */
  public void testErrorMaterialParamLink(){
    boolean f = false;
    try {
      ReadModel( FPathToXMLFiles + "material2.xml" );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      //e.printStackTrace();
    } catch (SAXException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  /** ����������� ������������ ���������������� ���������� ��������:
   * 1. ��������� ��� �����, � � ����� ����� ������������ �������� ���������� � �������� ��������, � ������ - �� �������
   * 2. � ��������� ��������������� ����� �������� ������������� ���������. � ��������� �������� �������������
   * ��������� ��������������� � 0
   * 3. �������� ������������ � ��������� � �������� �� ���� ���.
   * 4. ����� ������ �������� ��������� � ��������� ������ ���� ����� 0, � ��������� �� ��� ������ ���� ����� ��������
   * �������� ���������
   *
   */
  public void testFullTransfer(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "material1.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock source = (ModelBlock) model.Get("��������");
    ModelBlock reciever = (ModelBlock) model.Get("��������");
    assertTrue( source != null );
    assertTrue( reciever != null );
    assertEquals( source.GetIntValue("material1"), 10 );
    assertEquals( reciever.GetIntValue("material2"), 0 );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( source.GetIntValue("material1"), 0 );
    assertEquals( reciever.GetIntValue("material2"), 10 );
  }

  /** ����������� ������������ ������ ���������� �� ����� � ���������.
   * ��� �������� ��������� ��� ����� - �������� � ��������. � ������ ����� �� ������ ������������� ���������.
   * � �����-��������� ������������ �������� �������� �����-�� ��������. � �����-��������� ��� �������������
   * ��������� (�������) ���������� ���������� ������ �� ���������.
   * ������ �������� - �������� � �������� ���������. � ���������-��������� ������ ���� ��������, � ���������-���������
   * �������� ���� �� ������.
   * ����� ������������ ���� ���������� ������ � ������ ����� ����������� �� ����������.
   */
  public void testEnableTransfer(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "material3.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock source = (ModelBlock) model.Get("��������");
    ModelBlock reciever = (ModelBlock) model.Get("��������");
    assertTrue( source != null );
    assertTrue( reciever != null );
    assertEquals( source.GetIntValue("material1"), 10 );
    assertEquals( reciever.GetIntValue("material2"), 0 );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( source.GetIntValue("material1"), 10 );
    assertEquals( reciever.GetIntValue("material2"), 0 );
    ModelBlockParam enableParam = null;
    f = false;
    try {
      enableParam = (ModelBlockParam) reciever.Get("recieveEnable");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( enableParam != null );
    enableParam.GetVariable().SetValue( true );
    f = false;
    try {
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( source.GetIntValue("material1"), 0 );
    assertEquals( reciever.GetIntValue("material2"), 10 );
  }

  /**����������� ������������ ��������� ������������� ���������� ������ �� ������������� ���������.
   * ��� ����� ��������� ������������ ��������-�������� � ������� RecieveQuantity, � ������� ���������
   * �������� ����������.
   * ����������� ������������ ��������� �� ������������� ���������-��������� ��������� ��������
   */
  public void testRecieveQuantity(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
     boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "material4.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock source = (ModelBlock) model.Get("��������");
    ModelBlock reciever = (ModelBlock) model.Get("��������");
    assertTrue( source != null );
    assertTrue( reciever != null );
    assertEquals( source.GetIntValue("material1"), 90 );
    assertEquals( reciever.GetIntValue("material2"), 10 );
    ModelBlockParam param = null;
    try {
      param = (ModelBlockParam) reciever.Get("recieveQuantityParam");
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( param != null );
    param.GetVariable().SetValue(20);
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( source.GetIntValue("material1"), 70 );
    assertEquals( reciever.GetIntValue("material2"), 30 );
  }

  /**����������� ������������ ������ ������ RecieveQuantity, ����� � ��� ��������� ��������, � �� �������� ����������.
   * ������������ ����������� ������������ ��������� ��������, ����� � ������������ ��������� ��� ������ ����������,
   * ������� ��������� ���������.  � ����� ������ ��������� ���������� ���, ��� ���� � ���������. � ���������
   * �������� 0
   */
  public void testRecieveConstant(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "material5.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock source = (ModelBlock) model.Get("��������");
    ModelBlock reciever = (ModelBlock) model.Get("��������");
    assertTrue( source != null );
    assertTrue( reciever != null );
    assertEquals( source.GetIntValue("material1"), 10 );
    assertEquals( reciever.GetIntValue("material2"), 20 );
    // ������ ���������, ��� �������� ������ ��������� ���, ��� � ��� ����
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( source.GetIntValue("material1"), 0 );
    assertEquals( reciever.GetIntValue("material2"), 30 );
  }

  /** ����������� ��������������� �������, ������� ����������� � ���������. ���� ������ ������������ ��� �������
   * ����������, ������� ����� ���������� � ��������. �����������:
   * - ��������� ��������������� ���������� ���_orderQuantity, � ������� ������ ������������ ����������, �������
   *   ���������� ��������
   * - �������� ����������, � ������� ����� ��������� ��������� �������
   * - ������������ �������� ������ ��������� � ��������
   */
  public void testCalculateOutgoigQuantity(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "material6.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock source = (ModelBlock) model.Get("��������");
    ModelBlock reciever = (ModelBlock) model.Get("��������");
    assertTrue( source != null );
    assertTrue( reciever != null );
    assertEquals( source.GetIntValue("material1"), 23 );
    assertEquals( reciever.GetIntValue("material2"), 7 );
    f = false;
    ModelMaterialParam param = null;
    try {
      param = (ModelMaterialParam) source.Get("material1");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( param != null );
    Variable var = param.GetOrderQuantityVar();
    assertTrue( var != null );
    assertTrue( var.GetName().equalsIgnoreCase("material1_orderQuantity") );
    var = null;
    var = param.GetOutgoingVar();
    assertTrue( var != null );
    assertTrue( var.GetName().equalsIgnoreCase("outgoingQuantity") );
  }

  /**����������� ���������� �������� ������� � ������������ ���������.
   *
   */
  public void testExecuteInnerCode(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "material7.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock block = (ModelBlock) model.Get("��������");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("material1"), 0 );
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( block.GetIntValue("material1"), 1 );
  }

  /**����������� ������������ ������ �������, ������� ������������ ��������� ���������� � ������������ ���������
   * ����������� ������ ����� ������� ������� � ���, ��� �� ������ ���������� ������ ��� �������� ����������
   * ����������. � �� ������ ���������� ������-���������� ����� ���������.
   *
   */
  public void testExecutionOutgoingScript(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "material8.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    int i = 20;
    try {
      while ( i > 0 ) {
        model.Execute();
        i--;
      }
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }

    ModelBlock block = (ModelBlock) model.Get("��������");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("material1"), 0 );
    assertEquals( block.GetIntValue("testValue"), 30 );
  }

  /**����������� ������������ ���������� incoming-������� ������ ������������� ���������
   * incoming-������ - ��� ������, ������� ����������� ������ �������������-���������-��������� � ���
   * ������, ����� �� �������� ������ �� ������������� ���������-���������
   * � ������ ����� ����������� ������������ ������ ����������� �������� � ���������� �������� ������������
   * incoming-�������
   */
  public void testExecuteIncomingScript(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "material9.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock block = (ModelBlock) model.Get("��������");
    assertTrue( block != null );

    //�������� ��������. ������� ������������� ��������� ������ �  incoming-��������
    ModelBlockParam incomingQuantityParam = null;
    f = false;
    try {
      incomingQuantityParam = (ModelBlockParam) block.Get("incomingValue_��������");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( incomingQuantityParam != null );
    assertTrue( f );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( block.GetIntValue("incomingCounter"), 1 );
    assertEquals( block.GetIntValue("lastIncomingValue"), 1 );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( block.GetIntValue("incomingCounter"), 2 );
    assertEquals( block.GetIntValue("lastIncomingValue"), 2 );
  }

  public void testIncomingScriptError(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    try {
      ReadModel( FPathToXMLFiles + "material10.xml" );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      //e.printStackTrace();
    } catch (SAXException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );

  }


  /////////////////////////////////////////////////////////////////////
  ///////////// �������� ���������� ������� ///////////////////////////
  /////////////////////////////////////////////////////////////////////

  /**����������� ������������ �������� ���������� �������, �������� � ���� ���������� � �������
   */
  public void testEventContainerCreate(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    ModelEventProcessorContainer events = new ModelEventProcessorContainer(null);
    boolean f = false;
    try {
      events.AddEventProcessor("event", "var1 := 1");
      f = true;
    } catch (ModelException e) {
    }
    assertTrue( !f );
    Variable var1 = new Variable(1);
    var1.SetName("var1");
    ScriptLanguageExt ext;
    ext = new ScriptLanguageExt();
    try {
      ext.AddVariable( var1 );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    events.SetLanguageExt( ext );
    try {
      events.AddEventProcessor("event", "var1 := 1");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //��������� ���������� ������� � ������, ����������� � ������ ��� ���������� �������. ������ ��������������� ������
    f = false;
    try {
      events.AddEventProcessor("event", "var1 := 5");
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  /**����������� ������������ ���������� ��������� ������������ ������� ��� ������ ������
   * ModelEventProcessorContainer.Execute()
   * ��� ������������ ��������� ��� �������. ������� � ��������� ���������� �������� ��� ���������� ������ �������
   * � ���������� ����� Execute(). ������ ���������� �������� ������ � ����� ����������.
   */
  public void testExecEveent(){
    ModelEventProcessorContainer events = new ModelEventProcessorContainer(null);
    Variable var1 = new Variable(1);
    var1.SetName("var1");
    Variable var2 = new Variable(10);
    var2.SetName("var2");
    ScriptLanguageExt ext;
    ext = new ScriptLanguageExt();
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      events.SetLanguageExt( ext );
      events.AddEventProcessor("event1", "var1 := var1 + 1");
      events.AddEventProcessor("event2", "var2 := var2 + 1");
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    int i1 = 0;
    int i2 = 0;
    try {
      events.EventFired( "event1" );
      events.Execute();
      i1 = var1.GetIntValue();
      i2 = var2.GetIntValue();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i1, 2 );
    assertEquals( i2, 10 );
    f = false;
    try {
      events.EventFired( "event2" );
      events.Execute();
      i1 = var1.GetIntValue();
      i2 = var2.GetIntValue();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i1, 2 );
    assertEquals( i2, 11 );

    f = false;
    try {
      events.EventFired( "event3" );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  public void testReadEventContainer(){
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file23.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( model != null );
    ModelBlock block = (ModelBlock) model.Get("block");
    ModelBlockParam param = null;
    assertTrue( block != null );
    int i = -1;
    f = false;
    try {
      param = (ModelBlockParam) block.Get("var1");
      block.FireEvent("event1");
      model.Execute();
      assertTrue( param != null );
      i = param.GetVariable().GetIntValue();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i, 1 );

    f = true;
    try {
      model.Execute();
      i = param.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( i, 1 );
  }

  public void testCreatingOrderList(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file33.xml" );
      model.Execute();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("BusStation");
    assertTrue( block != null );
    int passengerIncrement = block.GetIntValue( "passengerIncrement" );
    assertEquals( passengerIncrement, 20 );
  }

  /** ����������� ������������ ������ ������� CaclulateIndependencyFlag() - �������, ������� ����������, �������� ��
   * ���� ��������� �� �������, ��� �� ��������. ���� ����� �������� ��������� �� ������� �����, ����� � ��� ����
   * ��������� � ��������� � ����� timeout. �� ���� ��������� ������� ���� �������� ����������� �� �������.
   * � ������ ����� ����������� ����, � ������� ������ ��� ����������.
   *
   */
  public void testTimeIndependency_WithoutStatechart(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file37.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("TimeIndependentBlock");
    assertTrue( block != null );
    block.CaclulateIndependencyFlag();
    assertTrue( block.IsTimeIndependentBlock() );
  }

  public void testTimdeIndependency_WithStatechart(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file38.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("TimeIndependentBlock");
    assertTrue( block != null );
    block.CaclulateIndependencyFlag();
    assertTrue( block.IsTimeIndependentBlock() );
  }

  public void testTimdeDependency_WithStatechart(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file39.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("TimeDependentBlock");
    assertTrue( block != null );
    block.CaclulateIndependencyFlag();
    assertTrue( !block.IsTimeIndependentBlock() );
  }

  public void testCreateStringParams(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file44.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }
  
  public void testDynamicBlocCreate_FromBlock(){
  	
  }

}

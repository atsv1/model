package mp.elements;

import junit.framework.TestCase;
import mp.parser.ScriptLanguageExt;
import mp.parser.ScriptException;
import mp.parser.Variable;
import mp.utils.ServiceLocator;

import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * User: atsv
 * Date: 06.02.2007
 */
public class ModelMuxTest extends TestCase {

  public ModelMuxTest(String testName) {
    super(testName);
  }

  protected static String FPathToXMLFiles = TestUtils.GetPath();

  protected static Model ReadModel(String aFileName) throws ModelException, IOException, SAXException{
    Model result = null;
    ModelElementFactory modelElementFactory = new ModelElementFactory();
    ModelXMLReader reader = new ModelXMLReader( modelElementFactory );
    reader.ReadModel( aFileName );
    result = (Model) reader.GetRootElement();
    result.ApplyNodeInformation();
    return result;
  }

  private static Model ReadModelWithoutApply( String aFileName ) throws ModelException{
    Model result = null;
    ModelElementFactory modelElementFactory = new ModelElementFactory();
    ModelXMLReader reader = new ModelXMLReader( modelElementFactory );
    try {
      reader.ReadModel( aFileName );
    } catch (SAXException e) {
      ModelException e1 = new ModelException("������ ������ " + e.getMessage());
      throw e1;
    } catch (IOException e) {
      ModelException e1 = new ModelException("������ ������ " + e.getMessage());
      throw e1;
    }
    result = (Model) reader.GetRootElement();
    //result.ApplyNodeInformation();
    return result;
  }


  /**����������� ������������ ��������� ��������� ��������: � ������������� ���������� ���� ��� �������� ����������.
     * ������������� ������ ������������� ������.
     */
    public void testEmptyOutParamList(){
      ModelSimpleBlock etalon = new ModelSimpleBlock(null,"block1",1);
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux1",2);
      ModelCalculatedElement param1 = new ModelCalculatedElement(etalon,"1",2);
      boolean f = false;
      try {
        param1.SetVarInfo("integer","0");
        etalon.AddInnerParam( param1 );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      try {
        mux.SetEtalon( etalon );
        f = false;
      } catch (ModelException e) {
        //e.printStackTrace();
      }
      assertTrue( f );
    }

    /**����������� ������������ �������� ������ ������� ���������� � ��������������.
     * � ������������� ���������� ��������� ���� � ���������� ������� �������� ����������� ������� ����������.
     * � �������������� ������ ���� ������� ���������-������� ���� �������� ���������� ��������� �����.
     */
    public void testCreateInputParams(){
      ModelSimpleBlock etalon = new ModelSimpleBlock(null,"block1",0);
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux",0);
      ModelCalculatedElement param1 = new ModelCalculatedElement(etalon,"1",2);
      ModelCalculatedElement param2 = new ModelCalculatedElement(etalon,"2",3);
      ModelCalculatedElement param3 = new ModelCalculatedElement(etalon,"3",4);
      boolean f = false;
      try {
        param1.SetVarInfo("integer","0");
        etalon.AddOutParam( param1 );
        param2.SetVarInfo( "real","0" );
        etalon.AddOutParam( param2 );
        param3.SetVarInfo("boolean","false");
        etalon.AddOutParam( param3 );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.SetEtalon( etalon );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      ModelBlockParam inp = mux.GetInpParam(0);
      assertTrue( inp != null );
      inp = mux.GetInpParam(1);
      assertTrue( inp != null );
      inp = mux.GetInpParam(2);
      assertTrue( inp != null );
      inp = mux.GetInpParam(3);
      assertTrue( inp == null );

    }

    /**�������� ������������ ���������� ���������� � �������������.
     * ������� ����������� ���������� - ���������� ���� ��������� � ��������.
     * ������������ ���������� ���������, ��������� ������������ � ��������
     */
    public void testAddSource(){
      ModelSimpleBlock etalon = new ModelSimpleBlock(null,"block1",0);
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux",0);
      ModelCalculatedElement param1 = new ModelCalculatedElement(etalon,"1",2);
      ModelCalculatedElement param2 = new ModelCalculatedElement(etalon,"2",3);
      ModelCalculatedElement param3 = new ModelCalculatedElement(etalon,"3",4);
      boolean f = false;
      try {
        param1.SetVarInfo("integer","0");
        etalon.AddOutParam( param1 );
        param2.SetVarInfo( "real","0" );
        etalon.AddOutParam( param2 );
        param3.SetVarInfo("boolean","false");
        etalon.AddOutParam( param3 );
        mux.SetEtalon( etalon );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      ModelSimpleBlock block = new ModelSimpleBlock(null, "1", 1);
      try {
        block.AddOutParam( param1 );
        block.AddOutParam( param2 );
        block.AddOutParam( param3 );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.AddSource( block );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
    }

    /**����������� ���������� � ������������� ������ ���������, ������� �� ��������� � ��������.
     * ������������ �����������  � ���������� � ����� ������ �� �������� ���������� �������
     */
    public void testAddSource2(){
      ModelSimpleBlock etalon = new ModelSimpleBlock(null,"block1",0);
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux",0);
      ModelCalculatedElement param1 = new ModelCalculatedElement(etalon,"1",2);
      ModelCalculatedElement param2 = new ModelCalculatedElement(etalon,"2",3);
      ModelCalculatedElement param3 = new ModelCalculatedElement(etalon,"3",4);
      boolean f = false;
      try {
        param1.SetVarInfo("integer","0");
        etalon.AddOutParam( param1 );
        param2.SetVarInfo( "real","0" );
        etalon.AddOutParam( param2 );
        param3.SetVarInfo("boolean","false");
        etalon.AddOutParam( param3 );
        mux.SetEtalon( etalon );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      ModelSimpleBlock block = new ModelSimpleBlock(null, "block1",10);
      try {
        block.AddOutParam( param1 );
      } catch (ModelException e) {
        e.printStackTrace();
      }
      f = false;
      try {
        mux.AddSource( block );
        f = true;
      } catch (ModelException e) {
        //e.printStackTrace();
      }
      assertTrue( !f );
    }

    /** �������� ������������ ���������� � ������������� ������ ���������, ������� �� �������� � ��������.
     * ������������ ������ ����������� � ������������ ����� ������ �� ����������
     */
    public void testAddSource3(){
      ModelSimpleBlock etalon = new ModelSimpleBlock(null,"block1",0);
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux",0);
      ModelCalculatedElement param1 = new ModelCalculatedElement(etalon,"1",2);
      ModelSimpleBlock block = new ModelSimpleBlock(null, "block2",4);
      ModelCalculatedElement param2 = new ModelCalculatedElement(block,"1",4);
      boolean f = false;
      try {
        param1.SetVarInfo("integer","0");
        etalon.AddOutParam( param1 );
        mux.SetEtalon( etalon );
        param2.SetVarInfo( "boolean","0" );
        block.AddOutParam( param2 );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.AddSource( block );
        f = true;
      } catch (ModelException e) {
        //e.printStackTrace();
      }
      assertTrue( !f );
    }

    /**����������� ������������ �������� ��������������.
     * ����������� ��������� �������:
     * 1. �������� ���������������� ����������� ��������� ��� �������� ���������� ������ ������������ �������
     * 2. �������� ���������������� ����������� ��������� ��� �������� ������� ��������
     *
     */
    public void testCreateMultiplexor(){
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux1",0);
      ModelBlockParam criteriaParam = null;
      ModelBlockParam enableParam = null;
      boolean f = false;
      try {
        criteriaParam = (ModelBlockParam) mux.Get( ModelConstants.GetMultiplexorCriteriaVarName() );
        enableParam = (ModelBlockParam) mux.Get( ModelConstants.GetMultiplexorEnableVarName() );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( criteriaParam != null );
      assertTrue( enableParam != null );
      assertTrue(enableParam.GetVariable().GetTypeName().equalsIgnoreCase("boolean"));
      assertTrue(criteriaParam.GetVariable().GetTypeName().equalsIgnoreCase("real"));

    }

    private static ScriptLanguageExt GetMuxLanguageExt( ModelMultiplexor mux ) throws ScriptException, ModelException {
      ScriptLanguageExt result = new ScriptLanguageExt();
      ModelLanguageBuilder.SetVariables(mux, null, result);
      return result;
    }


    /**�������� ������������ �������� � ������������� �������, �������������� ���������� �� ������ ��������.
     * ����������� ������������ �������� ������� � ������.
     */
    public void testAddEnableScript(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"1",1);
      boolean f = false;
      try {
        mux.SetLanguageExt( GetMuxLanguageExt(mux) );
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.AddEnableScript("enable := criteria >= 5");
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
    }

    public void testAddCriteriaScript(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"1",1);
      boolean f = false;
      try {
        mux.SetLanguageExt( GetMuxLanguageExt(mux) );
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.AddCriteriaScript("criteria := 5; ");
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
    }

    /**����������� ������������ �������� ������� ���������� � ������������� �� �����-���������.
     * ��� �������� ����������� ��������� ��������:
     * 1. ��������� ���� � ����� ��������� �����������
     * 2. ��������� �������������, � ������� � �������� ������� ���������� ��������� ����
     * 3. ����������� ����� �������� ��������.
     * �������� ���� ������� ���������� �������������� ������ ��������� �� ����������  ��������������� ���������
     */
    public void testLoadInpParams(){
      ModelSimpleBlock block = new ModelSimpleBlock(null,"block1",1);
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux",0);
      ModelCalculatedElement param1 = new ModelCalculatedElement(block,"var1",2);
      ModelCalculatedElement param2 = new ModelCalculatedElement(block,"var2",3);
      boolean f = false;
      try{
        param1.SetVarInfo("integer","2");
        block.AddOutParam( param1 );
        param2.SetVarInfo( "integer","3" );
        block.AddOutParam( param2 );
        mux.SetEtalon( block );
        f = true;
      } catch( ModelException e ){
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      int inp1 = 0;
      int inp2 = 0;
      Variable inp1Var = mux.GetInpParam("var1").GetVariable();
      Variable inp2Var = mux.GetInpParam("var2").GetVariable();
      assertTrue( inp1Var != null );
      assertTrue( inp2Var != null );
      try {
        inp1 = inp1Var.GetIntValue();
        inp2 = inp2Var.GetIntValue();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertEquals( inp1, 2 );
      assertEquals( inp1, 2 );
      param1.GetVariable().SetValue(5);
      param2.GetVariable().SetValue(6);
      try {
        mux.LoadInputVariables( block );
        inp1 = inp1Var.GetIntValue();
        inp2 = inp2Var.GetIntValue();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals( inp1, 5 );
      assertEquals( inp2, 6 );
      f = false;
      ModelSimpleBlock errorBlock = new ModelSimpleBlock(null,"block2",1);
      try {
        errorBlock.AddOutParam( param1 );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.LoadInputVariables( errorBlock );
        f = true;
      } catch (ModelException e) {
        //e.printStackTrace();
      }
      assertTrue( !f );
    }

    private static ModelSimpleBlock GetBlock(String aBlockName, String aParam1Name, String aParam1Value,
                                      String aParam2Name, String aParam2Value) throws ModelException {
      ModelSimpleBlock result = new ModelSimpleBlock(null,aBlockName, ServiceLocator.GetNextId() );
      ModelCalculatedElement param1 = new ModelCalculatedElement(result, aParam1Name, ServiceLocator.GetNextId() );
      ModelCalculatedElement param2 = new ModelCalculatedElement(result, aParam2Name, ServiceLocator.GetNextId());
      param1.SetVarInfo("integer", aParam1Value);
      param2.SetVarInfo("integer", aParam2Value);
      result.AddOutParam( param1 );
      result.AddOutParam( param2 );
      return result;
    }

    private static int GetIntValue( ModelBlockParam param ){
      Variable var = param.GetVariable();
      if ( var == null ){
        return 0;
      }
      int i;
      try {
        i = var.GetIntValue();
        return i;
      } catch (ScriptException e) {
        return 0;
      }
    }

    /**����������� ������������ �������� �������� ������� ���������� ��������������. �������� ������� �� ����������,
     * ������� ��������� � �������������.
     * ��� ������������ ���������� ��������� ��������:
     * 1. ��������� �������������
     * 2. � ������������� ����������� ��������� ������
     * 3. ���������� ����� �������� ������ �� ������� ���������
     * 4. ������������ �������� � �������������� � � �����
     */
    public void testLoadFromSourceList(){
      ModelSimpleBlock etalon;
      ModelSimpleBlock block1 = null;
      ModelSimpleBlock block2 = null;
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux1",1);
      boolean f = false;
      try {
        etalon = GetBlock("etalonblock","param1","0","param2","0");
        block1 = GetBlock("block1","param1","11","param2","12");
        block2 = GetBlock("block2","param1","21","param2","22");
        mux.SetEtalon( etalon );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.AddSource( block1 );
        mux.AddSource( block2 );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      boolean loadResult = false;
      try {
        loadResult = mux.LoadFirstForEnableScript();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( loadResult );
      assertTrue(f);
      int param1 = GetIntValue( mux.GetInpParam("param1") );
      assertTrue( param1 != 0 );
      int param2 = GetIntValue( mux.GetInpParam("param2") );
      boolean firstTested;
      if ( param1 == 11 ){
        assertEquals(param2, 12);
        firstTested = true;
      } else {
        assertEquals(param2, 22);
        firstTested = false;
      }
      f = false;
      try {
        loadResult = mux.LoadNextForEnableScript();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( loadResult );
      param1 = GetIntValue( mux.GetInpParam("param1") );
      assertTrue( param1 != 0 );
      param2 = GetIntValue( mux.GetInpParam("param2") );
      if ( firstTested ){
        assertEquals( param1, 21 );
        assertEquals( param2,22 );
      } else {
        assertEquals( param1, 11 );
        assertEquals( param2,12 );
      }
      f = false;
      try {
        loadResult = mux.LoadNextForEnableScript();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( !loadResult );

    }

    /** ����������� ������������ ������ �������������� ��� ������������ �������. ���������� ������������ �������
     * ��������, ��� ��� ��������� ����� ����������� � ������� ��������� ���������.
     * ��� ������������ ������������ ��������� ��������:
     * 1. ��������� ������������� ��� ������������ �������
     * 2. � ������������ ����������� ��������� ����������
     * 3. ����������� ����� �������������� �� ���������� �������� ������������ �����
     * 4. ����� ���������� ���������� � �������������� ������ ��������� � ����������� ����������, ��� ������� ��������
     * ������ ��������� ���������
     */
    public void testEmptyEnableScript(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"mux1", ServiceLocator.GetNextId());
      ModelSimpleBlock etalon;
      ModelSimpleBlock block1 = null;
      ModelSimpleBlock block2 = null;
      boolean f = false;
      try {
        etalon = GetBlock("etalonblock","param1","0","param2","0");
        block1 = GetBlock("block1","param1","11","param2","12");
        block2 = GetBlock("block2","param1","21","param2","22");
        mux.SetEtalon( etalon );
        mux.AddSource( block1 );
        mux.AddSource( block2 );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.UpdateEnableFlag();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      int i = mux.GetSourceCount();
      assertEquals(i, 2);
      i = mux.GetAvailableSourceCount();
      assertEquals( i, 2 );
    }

    private static void LoadSelfIndexValue(ModelBlock block, int selfIndexValue){
      if ( block == null ){
        return;
      }
      ModelBlockParam param = null;
      try {
          ModelLanguageBuilder.AddSelfIndexVariable( block, selfIndexValue );
        } catch (ModelException e) {
          e.printStackTrace();
        }
      try {
        param = (ModelBlockParam) block.Get( "selfIndex" );
      } catch (ModelException e) {
        e.printStackTrace();
      }
      if ( param == null ){
        return;
      }
      param.GetVariable().SetValue( selfIndexValue );
    }

    /**����������� ������������ ���������� ������������ �������.
     *
     */
    public void testExecEnableScript(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"mux1", ServiceLocator.GetNextId());
      ModelSimpleBlock etalon;
      ModelSimpleBlock block1 = null;
      ModelSimpleBlock block2 = null;
      ScriptLanguageExt ext;
      boolean f = false;
      try {
        etalon = GetBlock("etalonblock","param1","0","param2","0");
        block1 = GetBlock("block1","param1","11","param2","12");
        LoadSelfIndexValue( block1, 1 );
        block2 = GetBlock("block2","param1","21","param2","22");
        LoadSelfIndexValue( block2, 2 );
        mux.SetEtalon( etalon );
        mux.AddSource( block1 );
        mux.AddSource( block2 );
        f = true;
        ext = GetMuxLanguageExt(mux);
        mux.SetLanguageExt( ext );
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      try {
        mux.AddEnableScript("enable := (param1 = 11);");
        mux.UpdateEnableFlag();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      int i = mux.GetAvailableSourceCount();
      assertEquals( i, 1 );
      //�������� ������� ��������� param1 � ����� block2, ��� ����� � ��� ���� ����������� ������� �������
      ModelBlockParam param;
      param = (ModelBlockParam) block2.GetOutParam("param1");
      param.GetVariable().SetValue(11);
      f = false;
      try {
        mux.UpdateEnableFlag();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      i = mux.GetAvailableSourceCount();
      assertEquals( i, 2 );
    }

    /**����������� ������������ ������� ��������� ��������� � ��������������. ����������� ������������ ���������
     * ��������� �������� - ����� � �������������� ��� ����������.
     *
     */
    public void testExecCriteriaScript_NoSources(){
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux",1);
      boolean f = false;
      try {
        mux.SetLanguageExt( GetMuxLanguageExt( mux ) );
        mux.AddCriteriaScript( "criteria := 8;" );
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      ModelBlock maxBlock = null;
      try {
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        maxBlock = mux.GetMaxCriteriaBlock();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( maxBlock == null );
    }

    /**�������� ������������ ����������� ��������� � ������������ ��������� ��������.
     *
     */
    public void testExecCriteriaScript_EqualsResult(){
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux",1);
      ModelBlock block1;
      ModelBlock block2;
      boolean f = false;
      try {
        block1 = GetBlock("block1","param1","11","param2","12");
        block2 = GetBlock("block2","param1","11","param2","12");
        mux.SetEtalon( block1 );
        mux.AddSource( block1 );
        mux.AddSource( block2 );
        mux.SetLanguageExt( GetMuxLanguageExt( mux ) );
        mux.AddCriteriaScript( "criteria := param1 + param2;" );
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      ModelBlock maxBlock = null;
      f = false;
      try {
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        maxBlock = mux.GetMaxCriteriaBlock();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( maxBlock == null );

    }

    /**����������� ������������ ������ ��������� � ��������������. ���������� ��������, ��� ������� �� ���� �� ����������
     * �� ����� ���������� �� ����� ���� ����������. � �������� �������� ����������� ������� ������ ���������� null
     *
     */
    public void testExecCriteriaScript_NoAvailSource(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"mux1",0);
      ModelBlock block1;
      ModelBlock block2;
      boolean f = false;
      try {
        block1 = GetBlock("block1","param1","11","param2","12");
        block2 = GetBlock("block2","param1","11","param2","22");
        mux.SetEtalon( block1 );
        mux.AddSource( block1 );
        mux.AddSource( block2 );
        mux.SetLanguageExt( GetMuxLanguageExt( mux ) );
        mux.AddEnableScript("enable := (param1 = 999);");
        mux.AddCriteriaScript( "criteria := param2;" );
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      ModelBlock maxBlock = null;
      f = false;
      try {
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        maxBlock = mux.GetMaxCriteriaBlock();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( maxBlock == null );
    }

    /**����������� ������������ ������ ��������� ������ � ��������������. � �������������� ������������ ���������, ������
     * �� ������� ����� ���� ������ � �������� ���������.
     *
     */
    public void testCriteriaScript_1(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"mux1",0);
      ModelBlock block1;
      ModelBlock block2 = null;
      boolean f = false;
      try {
        block1 = GetBlock("block1","param1","11","param2","12");
        block2 = GetBlock("block2","param1","11","param2","22");
        mux.SetEtalon( block1 );
        mux.AddSource( block1 );
        mux.AddSource( block2 );
        mux.SetLanguageExt( GetMuxLanguageExt( mux ) );
        mux.AddCriteriaScript( "criteria := param2;" );
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      ModelBlock maxBlock = null;
      f = false;
      try {
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        maxBlock = mux.GetMaxCriteriaBlock();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( maxBlock == block2 );
    }

    /**����������� ������������ ������ ��������� � ��������������. ����������� ��������� ������, ����� � ������
     * ���������� ������������ ��������� � �����������, ��� � ��� ����������. ��� ���� ��������, � �������� ��� ����������,
     * �� ����� ���� ����� ������������ �������� ��������.
     */
    public void testCriteriaScript_2(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"mux1",0);
      ModelBlock block1;
      ModelBlock block2 = null;
      ModelBlock block3 = null;
      boolean f = false;
      try {
        block1 = GetBlock("block1","param1","11","param2","12");
        block2 = GetBlock("block2","param1","21","param2","22");
        block3 = GetBlock("block3","param1","31","param2","32");
        mux.SetEtalon( block1 );
        mux.AddSource( block1 );
        mux.AddSource( block2 );
        mux.AddSource( block3 );
        mux.SetLanguageExt( GetMuxLanguageExt( mux ) );
        mux.AddEnableScript( "enable := (param1 <> 31);" );
        mux.AddCriteriaScript( "criteria := param2;" );
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      ModelBlock maxBlock = null;
      f = false;
      try {
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        maxBlock = mux.GetMaxCriteriaBlock();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( maxBlock != block3 );
      assertTrue( maxBlock == block2 );
    }

    /**����������� ����������������� ������ ModelMultiplexorLinker - ������������ ���������� ������ ����������,
     * � ������� ����� ������������� ������������� ��������� ������, ������������� � ��������������.
     * ��� ����� ������������ �������� ��������:
     * 1. ��������� �������������
     * 2. ��������� ��������� ���� � ���������� � �������������
     * 3. ��������� ����, � ������� ���� ������� ���������, ������� �������������� � ��������������
     * 4. ��������� ��������� ������ ModelMultiplexorLinker, � � ���� ���������� ����� ���������� ������ ����������.
     * 5. ������������ ���������� ������� � ����������� ������
     */
    public void testMuxLinker_ParamList(){
      ModelMultiplexor mux = new ModelMultiplexor(null, "mux1", 1);
      ModelBlock etalon = null;
      boolean f = false;
      try {
        etalon = GetBlock("block1","param1","11","param2","12");
        mux.SetEtalon( etalon );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      ModelSimpleBlock destBlock = new ModelSimpleBlock(null,"block2", ServiceLocator.GetNextId());
      ModelInputBlockParam inp1 = new ModelInputBlockParam(destBlock,"inp1", ServiceLocator.GetNextId());
      ModelInputBlockParam inp2 = new ModelInputBlockParam(destBlock,"inp2", ServiceLocator.GetNextId());
      ModelCalculatedElement el1 = new ModelCalculatedElement(destBlock, "var1", ServiceLocator.GetNextId());
      try {
        inp1.SetVarInfo("integer","0");
        inp2.SetVarInfo("integer","0");
        el1.SetVarInfo("real","0");
        destBlock.AddInpParam( inp1 );
        destBlock.AddInpParam( inp2 );
        destBlock.AddOutParam( el1 );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      try {
        mux.SetEtalon( etalon );
        mux.SetMuxOwner( destBlock );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      //��������� ����������������� ������
      f = false;
      try {
        inp1.Link( mux, mux.GetInpParam("param1") );
        //inp1.Link( mux, mux.GetInpParam("param2") );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      ModelMuxBlockLinker linker = new ModelMuxBlockLinker( mux, destBlock );
      linker.BuildParamsList();
      assertEquals( linker.GetDestParamsCount(),1 );
      f = false;
      try {
        inp2.Link( mux, mux.GetInpParam("param2") );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      linker.BuildParamsList();
      assertEquals( linker.GetDestParamsCount(),2 );
    }

    /**����������� ����������������� ����� ModelMuxBlockLinker. ����� ������ ��������� ����-�������� �
     * ����������, ������� ��� ��������� � ��������������.
     * ��� ���� �������� �������� ��������� ����:
     * 1. ��������� �������������
     * 2. � ���� ����������� ��������� ���������� ������ (� �������������� ���������� �������� ����������)
     * 3. ��������� ����, ������� ������ ���� ���������� � ����������� � ���������������
     * 4. � �������������� ����������� ��������� ����������� ���������
     * 5. ����������� ����� Link()
     * 6. ����������� �������� �� ������ �����-���������
     */
    public void testMux_BlockLinking(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"mux1", 0);
      ModelBlock source1 = null;
      ModelBlock source2 = null;
      ModelBlock reciever = new ModelSimpleBlock(null,"dest1", ServiceLocator.GetNextId());
      ModelInputBlockParam inp1 = null;
      ModelInputBlockParam inp2 = null;
      ModelCalculatedElement fake = null;
      ModelMuxBlockLinker linker = new ModelMuxBlockLinker(mux, reciever);
      boolean f = false;
      try {
        source1 = GetBlock("source1","param1","11","param2","12");
        source2 = GetBlock("source2","param1","21","param2","22");
        mux.SetEtalon( source1 );
        mux.AddSource( source1 );
        inp1 = new ModelInputBlockParam(reciever,"inp1", ServiceLocator.GetNextId());
        inp2 = new ModelInputBlockParam(reciever,"inp2", ServiceLocator.GetNextId());
        reciever.AddInpParam( inp1 );
        reciever.AddInpParam( inp2 );
        fake = new ModelCalculatedElement(reciever, "fake1", ServiceLocator.GetNextId());
        reciever.AddOutParam( fake );
        inp1.SetVarInfo("integer","0");
        inp2.SetVarInfo( "integer","0" );
        fake.SetVarInfo("integer","0");

        mux.AddSource( source2 );
        mux.SetMuxOwner( reciever );
        mux.SetLanguageExt( GetMuxLanguageExt( mux ) );
        mux.AddCriteriaScript( "criteria := param1 + param2;" );

        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      try {
        inp1.Link( mux, (ModelBlockParam) mux.Get("param1"));
        inp2.Link( mux, (ModelBlockParam) mux.Get("param2"));
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      try {
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        linker.Link();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      int i1 = 0;
      int i2 = 0;
      try {
        i1 = inp1.GetVariable().GetIntValue();
        i2 = inp2.GetVariable().GetIntValue();
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals(i1, 21);
      assertEquals( i2, 22 );

      f = false;
      try {
        mux.AddEnableScript("enable := (param1 = 11);");
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        assertEquals( mux.GetMaxCriteriaBlock(), source1 );
        linker.Link();
        i1 = inp1.GetVariable().GetIntValue();
        i2 = inp2.GetVariable().GetIntValue();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals( i1, 11 );
      assertEquals( i2, 12 );
    }

    /**����������� ������������ ������ ������ ��������� ��������� ��������������. ��� ������������ ����������� ���������
     * ��������:
     * 1. ��������� �������������
     * 2. ��������� ��������� ���� � ���������� � �������������
     * 3. ��������� ����-�������� �������������� � ���������� � �������������. ������ � �������� �� ����� ����������
     * � ����������� �������
     * 4. �������������� ��, ����� � �������������� ���� ������� ���������, � �������, ������������ � �������
     * ���������� � ������� � ���������
     * 5. ��������� ������ ������� ��������� - ������ �� ����� �������������� ����� � ��������. ��� ���������� ������
     * ��������� � �������������, ����� ������������� ��������� ��������
     */
    public void testSetMuxOwner(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"mux", ServiceLocator.GetNextId());
      ModelBlock owner = null;
      ModelBlock owner2 = null;
      ModelBlock etalon = null;
      boolean f = false;
      try {
        owner = GetBlock("muxOwner","param1","11","param2","12");
        etalon = GetBlock("etalon","et1","11","et2","12");
        owner2 = GetBlock("etalon","_et1","11","et2","12");
        mux.SetEtalon( etalon );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      try {
        mux.SetMuxOwner( owner );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( mux.GetInpParam("param1") != null );
      assertTrue( mux.GetInpParam("param2") != null );
      assertTrue( mux.GetInpParam("et1") != null );
      assertTrue( mux.GetInpParam("et2") != null );

      f = false;
      try {
        mux.SetMuxOwner( owner2 );
        f = true;
      } catch (ModelException e) {
        //System.out.println( e.getMessage() );
      }
      assertTrue( !f );
    }

    /**�����������  �������� ����������������� �������������� � �������� ����� ���� �������� ������ ���� ��������
     * � ����������� �����������.
     * ��� ����� ������������ ������������ ��������� ��������:
     * 1. ��������� �������� ������ � �������������� � �������������� ��� ��� ��������
     * 2. ��������� ��������� ���������� ������
     * 3. ��������� ������ �������������� � ��������������
     * 4. ��������� ������ ��� ���������� ��������� � ��������� ����������
     * ����������� ����� ���������� ��������� - ��� ������ ���� ����� ������� ���������
     */
    public void testLinkOneSource_ManyRecievers(){
      ModelMultiplexor mux = new ModelMultiplexor(null,"mux", ServiceLocator.GetNextId() );
      ModelSimpleBlock source = null;
      ModelSimpleBlock reciever1 = null;
      ModelSimpleBlock reciever2 = null;
      ModelInputBlockParam reciever1Param1 = null;
      ModelInputBlockParam reciever1Param2 = null;
      ModelInputBlockParam reciever2Param1 = null;
      ModelInputBlockParam reciever2Param2 = null;
      boolean f = false;
      try {
        source = GetBlock("source","param1","7","param2","8");
        reciever1 = GetBlock("reciever1","out1","11","out2","12");
        reciever2 = GetBlock("reciever2","out1","21","out2","22");
        reciever1Param1 = new ModelInputBlockParam(reciever1,"inp1", ServiceLocator.GetNextId() );
        reciever1Param2 = new ModelInputBlockParam(reciever1,"inp2", ServiceLocator.GetNextId() );
        reciever1Param1.SetVarInfo("integer","0");
        reciever1Param2.SetVarInfo("integer","0");
        reciever1.AddInpParam( reciever1Param1 );
        reciever1.AddInpParam( reciever1Param2 );
        reciever2Param1 = new ModelInputBlockParam(reciever1,"inp1", ServiceLocator.GetNextId() );
        reciever2Param2 = new ModelInputBlockParam(reciever1,"inp2", ServiceLocator.GetNextId() );
        reciever2Param1.SetVarInfo("integer","0");
        reciever2Param2.SetVarInfo("integer","0");
        reciever2.AddInpParam( reciever2Param1 );
        reciever2.AddInpParam( reciever2Param2 );
        mux.SetEtalon( reciever1 );
        mux.AddSource( reciever1 );
        mux.AddSource( reciever2 );
        mux.SetMuxOwner( source );
        mux.SetLanguageExt( GetMuxLanguageExt( mux ) );
        mux.AddCriteriaScript( "criteria := out1 + out2;" );
        reciever1Param1.Link( mux, (ModelBlockParam) mux.Get( "param1" ));
        reciever1Param2.Link( mux, (ModelBlockParam) mux.Get( "param2" ));
        reciever2Param1.Link( mux, (ModelBlockParam) mux.Get( "param1" ));
        reciever2Param2.Link( mux, (ModelBlockParam) mux.Get( "param2" ));
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      //�������� ������� ��������� ��������������, ������� ������ ���� ��������� � ��������� ����������� ���������
      // ��������������
      f = false;
      try {
        ModelInputBlockParam inp = (ModelInputBlockParam) mux.Get("param1");
        assertTrue( inp != null );
        inp.Link( source, (ModelBlockParam) source.Get( "param1" ));
        inp = (ModelInputBlockParam) mux.Get("param2");
        assertTrue( inp != null );
        inp.Link( source, (ModelBlockParam) source.Get( "param2" ));
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      ModelOneSourceManyReciever linker = new ModelOneSourceManyReciever(mux, source);
      try {
        mux.UpdateCriteria();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      try {
        linker.Link();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      //��������� � �������� ���������� ����� ��������
      f = false;
      try {
        ModelBlockParam out = (ModelBlockParam) source.Get("param1");
        out.GetVariable().SetValue(71);
        out = (ModelBlockParam) source.Get("param2");
        out.GetVariable().SetValue(81);
        //reciever2.Execute();
        reciever2Param1.Update();
        reciever2Param2.Update();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      //��������� �������� ������ ���������� ���������. ��������� ���������� � ������ �������� ����� ���� reciever2
      f = false;
      int inp1 = 0;
      int inp2 = 0;
      try {
        inp1 = reciever2Param1.GetVariable().GetIntValue();
        inp2 = reciever2Param2.GetVariable().GetIntValue();
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals( inp1, 71 );
      assertEquals( inp2, 81 );

      //�� ������ ������ ���������, ����� � ����������� ����� �������� ������ ������� ��������� ��������
      f = false;
      try {
        inp1 = reciever1Param1.GetVariable().GetIntValue();
        inp2 = reciever1Param2.GetVariable().GetIntValue();
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals( inp1, 7 );
      assertEquals( inp2, 8 );

    }

    /**������� ������ �������������� �� ����� ������
     * ����������� ��, ��� ��������� ��������� ������ ��������������.
     */
    public void testReadMuxFromFile(){
      Model model = null;
      boolean f = false;
      try {
        model = ReadModelWithoutApply( FPathToXMLFiles + "file11.xml" );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }

      assertTrue( f );
      ModelElement element = model.Get("mux1");
      assertTrue( element != null );
      ModelMultiplexor mux = null;
      f = false;
      try{
        mux = (ModelMultiplexor) element;
        f = true;
      } catch (Exception e){
        mux = null;
      }
      assertTrue( mux != null );
      assertTrue( f );
    }

    private static ModelMultiplexor GetMux(Model aModel) throws ModelException{
      ModelMultiplexor mux = (ModelMultiplexor) aModel.Get("mux1");
      assertTrue( mux != null );
      try {
        ModelLanguageBuilder.AddSelfIndexVariable( mux, aModel );
        mux.BuildParams();
        /*ScriptLanguageExt ext = GetMuxLanguageExt(mux);
        mux.SetLanguageExt( ext );
        mux.ApplyNodeInformation();*/
      } catch (Exception e) {
        ModelException e1 = new ModelException(e.getMessage());
        throw e1;
      }
      return mux;
    }

    /**����������� ������������ ������  �������������� �� ����� ������. ����� ����������� ������������ ������ �� ������
     * ���������� � ��������� �������������� � ��������� �����. ������ ����������� ������ - ������������ ����������
     * �������������� � ���������� � ����������� �������.
     * ����������� �������� - �������� �������������� - ��������, ���������� ����� - ���������. �� ���� �����������
     * �������� "����������� �� �����������".
     * ��� ����� �������� ��������� ���� ������, ���������� ��������� ��������:
     * 1. ��������� ����. � ����� ������ ���� �������� ���������, ������� ����� �������������� ��� ������� ��������,
     * � ����� �������� ���������, ������� ������������� ������ ����� ��������� �� ������� ��������� ��������������
     * 2. ���� - �������� ��������������. � ��� ������ ���� ������� ���������, ������� ����������� � ��������
     * ��������������, � ���������, ������� �� ����������� � �������� ��������������.
     * 3. �������������.
     *
     */
    public void testMuxLink_1(){
      mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      boolean f = false;
      try {
        model = ReadModelWithoutApply( FPathToXMLFiles + "file12.xml" );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( model != null );
      ModelMultiplexor mux = null;
      f = false;
      try {
        mux = GetMux(model);
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( mux != null );
      //������ �� ������ ���������� ��� ���� ��������� ������
      f = false;
      try {
        model.ApplyNodeInformation();
        model.Execute();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      //������� ������������ ���������� ����� � ��������������
      int i = 0;
      ModelBlock block = model.Get("etalon",i);
      while ( block != null ){
        try {
          mux.AddSource( block );
        } catch (ModelException e) {
          e.printStackTrace();
          break;
        }
        i++;
        block = model.Get("etalon",i);
      }//while
      assertEquals( i , 6 );

      //��������� �������������
      f = false;
      try {
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      ModelBlock owner = (ModelBlock) model.Get("owner");
      ModelMuxBlockLinker linker = new ModelMuxBlockLinker(mux, owner);
      try {
        linker.Link();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      i = 0;
      f = false;
      try {
        ModelBlockParam param = (ModelBlockParam) owner.Get("testLink");
        i = param.GetVariable().GetIntValue();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals( i, 5 );
    }

    /**����������� ������������ ������ ������������� � �������� "����������� �� ��������" - �.� ����� ����� ����������
     * � ���� ��������.
     * ��� �������� ��������� ���� ������, ������� ��������:
     * 1. �������������
     * 2. ���� �������� �������������� - ����-��������
     * 3. ��������� ���������� ������ - ����������
     * ������������� ����� �������� �� ����� ������ �� ����������, ��� ������ ���� ����� ��������� ������ ���������
     *
     */
    public void testMuxLink_2(){
      mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      boolean f = false;
      try {
        model = ReadModelWithoutApply( FPathToXMLFiles + "file13.xml" );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( model != null );
      ModelMultiplexor mux = null;
      f = false;
      try {
        mux = GetMux(model);
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( mux != null );

      //������ �� ������ ���������� ��� ���� ��������� ������
      f = false;
      try {
        model.ApplyNodeInformation();
        model.Execute();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      //������� ������������ ���������� ����� � ��������������
      int i = 0;
      ModelBlock block = model.Get("reciever",i);
      while ( block != null ){
        try {
          mux.AddSource( block );
        } catch (ModelException e) {
          e.printStackTrace();
          break;
        }
        i++;
        block = model.Get("reciever",i);
      }//while
      assertEquals( i , 5 );

      //��������� �������������
      f = false;
      try {
        mux.UpdateEnableFlag();
        mux.UpdateCriteria();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      ModelOneSourceManyReciever linker = new ModelOneSourceManyReciever(mux, (ModelBlock) model.Get("owner"));
      try {
        linker.Link();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      ModelBlock reciever = model.Get("reciever", 3);
      assertTrue( reciever  != null );
      ModelBlockParam inp1 = reciever.GetInpParam("input1");
      ModelBlockParam inp2 = reciever.GetInpParam("input2");
      int i1 = 0;
      int i2 = 0;
      try {
        i1 = inp1.GetVariable().GetIntValue();
        i2 = inp2.GetVariable().GetIntValue();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertEquals(i1, 12);
      assertEquals(i2, 14);

      ModelBlock sender = (ModelBlock) model.Get("owner");
      assertTrue( sender != null );
      ModelBlockParam out1 = (ModelBlockParam) sender.GetOutParam("ownerOut1");
      ModelBlockParam out2 = (ModelBlockParam) sender.GetOutParam("ownerOut2");
      out1.GetVariable().SetValue(999);
      out2.GetVariable().SetValue(888);
      f = false;
      try {
        reciever.Execute();
        i1 = inp1.GetVariable().GetIntValue();
        i2 = inp2.GetVariable().GetIntValue();
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals(i1, 999);
      assertEquals(i2, 888);
    }

    /** �������� ������������ �������� �����������, �.�. ���������� �� ModelMultiplexorLinker. ����������� ���������
     * ������� DynamicBlockLinkerFactory.
     * � ���� ����� ��������� �����������, ��������������� �������� "����������� �� �����������", �.�. ������ ���������
     * ����� ModelMuxBlockLinker.
     */
    public void testCreateLinker_1(){
      mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      boolean f = false;
      try {
        model = ReadModelWithoutApply( FPathToXMLFiles + "file14.xml" );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( model != null );
      ModelMultiplexor mux = null;
      f = false;
      try {
        mux = GetMux(model);
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( mux != null );

      f = false;
      try {
        model.ApplyNodeInformation();
        model.Execute();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      ModelMultiplexorLinker linker = null;
      try {
        linker = DynamicBlockLinkerFactory.GetLinker( mux );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( linker != null );
      String s = linker.getClass().getName();
      assertTrue( "mp.elements.ModelMuxBlockLinker".equalsIgnoreCase( s ) );
    }

    /** �������� ������������ �������� �����������, �.�. ���������� �� ModelMultiplexorLinker. ����������� ���������
     * ������� DynamicBlockLinkerFactory.
     * � ���� ����� ��������� �����������, ��������������� �������� "����������� �� ��������", �.�. ������ ���������
     * ����� ModelOneSourceManyReciever.
     */
    public void testCreateLinker_2(){
      mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      boolean f = false;
      try {
        model = ReadModelWithoutApply( FPathToXMLFiles + "file15.xml" );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( model != null );
      ModelMultiplexor mux = null;
      f = false;
      try {
        mux = GetMux(model);
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( mux != null );

      f = false;
      try {
        model.ApplyNodeInformation();
        model.Execute();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      f = false;
      ModelMultiplexorLinker linker = null;
      try {
        linker = DynamicBlockLinkerFactory.GetLinker( mux );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( linker != null );
      String s = linker.getClass().getName();
      assertTrue( "mp.elements.ModelOneSourceManyReciever".equalsIgnoreCase( s ) );
    }

    /** ����������� ������������ ������ ������ ��� ������ ������ � ��������������� ������. � ������� �� ���������� ������
     * ������ ������ ������ ���� ����������� ��������� � ������ ApplyNodeInformation, ��� ������� ������ �������������
     * �������.
     * �������� ������ � ��������� "����������� �� ��������".
     */
    public void testFullReadModelWithMix_1(){
      mp.parser.ModelExecutionContext.ClearExecutionContext();
      boolean f = false;
      Model model = null;
      try {
        model = ReadModel( FPathToXMLFiles + "file16.xml" );
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
        model.Execute();
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      ModelMultiplexor mux = (ModelMultiplexor) model.Get("mux1");
      assertTrue( mux != null );
      assertEquals(mux.GetSourceCount(), 5 );

      ModelBlock reciever = model.Get("reciever", 3);
      assertTrue( reciever  != null );
      ModelBlockParam inp1 = reciever.GetInpParam("input1");
      ModelBlockParam inp2 = reciever.GetInpParam("input2");
      int i1 = 0;
      int i2 = 0;
      try {
        i1 = inp1.GetVariable().GetIntValue();
        i2 = inp2.GetVariable().GetIntValue();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertEquals(i1, 12);
      assertEquals(i2, 14);

      ModelBlock sender = (ModelBlock) model.Get("owner");
      assertTrue( sender != null );
      ModelBlockParam out1 = (ModelBlockParam) sender.GetOutParam("ownerOut1");
      ModelBlockParam out2 = (ModelBlockParam) sender.GetOutParam("ownerOut2");
      out1.GetVariable().SetValue(999);
      out2.GetVariable().SetValue(888);
      f = false;
      try {
        //reciever.Execute();
        model.Execute();
        model.Execute();
        i1 = inp1.GetVariable().GetIntValue();
        i2 = inp2.GetVariable().GetIntValue();
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals(i1, 999);
      assertEquals(i2, 888);
    }

    /**����������� ������������ ������ ����� ������, ������� �������� �������� ��������������. �������� ������������
     * ��� �������� "����������� �� �����������"
     * ��� ����� ��������  ��������� ���� ������, ������� ����� ��������� ��������� ����������, ���� �������� � ����
     * �������������, ������� �������� ���� �� ���������� ��� ���������.
     *
     */
    public void testFullReadModelWithMix_2(){
      mp.parser.ModelExecutionContext.ClearExecutionContext();
      boolean f = false;
      Model model = null;
      try {
        model = ReadModel( FPathToXMLFiles + "file17.xml" );
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
      ModelBlock source = model.Get("��������",3);
      assertTrue( source != null );
      f = false;
      try {
        ModelBlockParam out1 = (ModelBlockParam) source.Get("��������1");
        ModelBlockParam out2 = (ModelBlockParam) source.Get("��������2");
        assertTrue( out1 != null );
        assertTrue( out2 != null );
        out1.GetVariable().SetValue( 12 );
        out2.GetVariable().SetValue( 14 );
        model.Execute();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );

      ModelBlock reciever = (ModelBlock) model.Get("��������");
      assertTrue( reciever != null );
      ModelBlockParam inpParam1 = null;
      ModelBlockParam inpParam2 = null;
      int inp1 = 0;
      int inp2 = 0;
      f = false;
      try {
        inpParam1 = (ModelBlockParam) reciever.Get("inp1");
        inpParam2 = (ModelBlockParam) reciever.Get("inp2");
        assertTrue( inpParam1 != null );
        assertTrue( inpParam2 != null );
        inp1 = inpParam1.GetVariable().GetIntValue();
        inp2 = inpParam2.GetVariable().GetIntValue();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertEquals( inp1, 12 );
      assertEquals( inp2, 14 );

    }

  /** ����������� ������������ ����������� �������������� ������� �������.
   * �������� ������� ��������� ���������� ���������� ������ � ��������������, ��� ������� ����� ��������������
   * ��������.
   * ��� �������� ��������� ����� ��������:
   * 1. ��������� ������, � ������� ���� ���������� �����, ����-�������� �������������� � ��� �������������
   * 2. � �������������� ����������� ������������� ����������� ������, ������� � ������ ������ ������ ������ ���
   * ���� ���������� ������ true.
   * 3. �� �������������� ���������� ������ �������. �� ������ ���� ������ ���������� ���������� ������
   */
  public void testQueueSize(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file20.xml" );
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
    ModelMultiplexor mux = null;
    mux = (ModelMultiplexor) model.Get("mux");
    int queueSize = mux.GetQueueSize();
    assertEquals( queueSize, 5 );
    ModelBlock block = null;
    block = model.Get("��������", 0);
    assertTrue( block != null );
    ModelBlockParam param = null;
    f = false;
    try {
      param = (ModelBlockParam) block.Get("criteriaParam");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    param.GetVariable().SetValue( false );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    queueSize = mux.GetQueueSize();
    assertEquals( queueSize, 4 );
    //��������� �������� ���������������� ��������� � ��������������
    ModelBlockParam sizeParam = null;
    int i = 0;
    f = false;
    try {
      sizeParam = (ModelBlockParam) mux.Get( ModelConstants.GetMupltiplexorQueueSizeVarName() );
      assertTrue( sizeParam != null );
      i = sizeParam.GetVariable().GetIntValue();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( i, 4 );
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
    ModelBlock block1 = (ModelBlock) model.Get("��������");
    assertTrue( block1 != null );
    assertEquals( block1.GetIntValue("inp3"), 4 );

  }

  /**�������� �������� � ������������� ����������� ���������� �� �����-���������.
   * ������������ �������� "����������� �� ��������". �������� ���������� ����������������, �� ��� ���� � �������������
   * ���������� ����� ����������� �������� �� �����-��������� (��������� ��������������).
   */
  public void testManagedMux_1(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file21.xml" );
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
    ModelBlockParam muxCriteria = null;
    f = false;
    ModelMultiplexor mux = (ModelMultiplexor) model.Get("mux");
    ModelBlock reciever = (ModelBlock) model.Get("��������");
    ModelBlockParam recieverCriteria = null;
    try {
      muxCriteria = (ModelBlockParam) mux.Get("recieverCriteria");
      recieverCriteria = (ModelBlockParam) reciever.Get("recieverCriteria");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( muxCriteria != null );
    assertTrue( recieverCriteria != null );
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
    ModelBlockParam inp1 = null;
    ModelBlockParam inp2 = null;
    int i1 = 0;
    int i2 = 0;
    f = false;
    try {
      inp1 = (ModelBlockParam) reciever.Get("inp1");
      inp2 = (ModelBlockParam) reciever.Get("inp2");
      i1 = inp1.GetVariable().GetIntValue();
      i2 = inp2.GetVariable().GetIntValue();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals(i1, 10);
    assertEquals(i2, 15);
    recieverCriteria.GetVariable().SetValue(2);
    f = false;
    try {
      model.Execute();
      model.Execute();
      i1 = inp1.GetVariable().GetIntValue();
      i2 = inp2.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals(i1, 20);
    assertEquals(i2, 30);
  }

  /**����������� ������������ �������� ���������� ��������������� �� ������ ��������.
   *
   */
  public void testCreateManyMuxInstances(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file22.xml" );
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
  }

  /** ����������� ������������ ��������� ������� "Enabled" - �������, ������� ��������� ��� ������, � ������� ��������
   * ������������ ������� ����� true
   */
  public void testGenerateEnableEvent(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file24.xml" );
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
    ModelBlock block = model.Get("��������",1);
    assertEquals( block.GetIntValue("enableFlag"),1 );
    block = model.Get("��������",0);
    assertEquals( block.GetIntValue("enableFlag"),0 );
    assertEquals( block.GetIntValue("notEnableFlag"),1 );
  }

  /** ����������� ������������ ��������� ������� "MaxCriteria" � "NotMaxCriteria"
   *
   */
  public void testGenerateCriteriaEvent(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file26.xml" );
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
    ModelBlock block = model.Get("��������",1);
    assertEquals( block.GetIntValue("maxCriteriaFlag"), 1 );
    assertEquals( block.GetIntValue("notmaxCriteriaFlag"), 0 );

    block = model.Get("��������",2);
    assertEquals( block.GetIntValue("maxCriteriaFlag"), 0 );
    assertEquals( block.GetIntValue("notmaxCriteriaFlag"), 1 );
  }

  /**����������� ������������ ���������� ������������ ���������� ����������� ���������������.
   * ����������� ����� ���������� � �������� "����������� �� ��������": ��������� ����-�������� � ������������
   * ���������� �� ������, ������������� � ��������� ���������� � ������������ ����������
   */
  public void testMaterialParamReciever(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
     boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file28.xml" );
      //model.Execute();
      //model.Execute();
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
    ModelBlock reciever = model.Get("��������", 4);
    assertTrue( reciever != null );
    ModelBlock sender = (ModelBlock) model.Get("��������");
    assertTrue( sender != null );
    assertEquals( reciever.GetIntValue("inp1"), 0 );
    assertEquals( sender.GetIntValue("material1"), 10 );
    ModelBlock reciever1 = model.Get( "��������", 1 );
    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelInputBlockParam senderParam = (ModelInputBlockParam) sender.GetOutParam("material1");
    ModelInputBlockParam recieverParam = (ModelInputBlockParam) reciever.GetInpParam("inp1");
    ModelInputBlockParam reciever1Param = (ModelInputBlockParam) reciever1.GetInpParam("inp1");
    assertTrue( senderParam != null );
    assertTrue( recieverParam != null );
    assertTrue( senderParam.IsDependElement( recieverParam ) );
    assertTrue( !senderParam.IsDependElement( reciever1Param ) );
    assertEquals( reciever1.GetIntValue("inp1"), 0 );
    assertEquals( reciever.GetIntValue("inp1"),10 );
    assertEquals( sender.GetIntValue("material1"),0 );
  }

  /**����������� ������ �������������� � ������������� ���������� � ������ "����������� �� ��������".
   * ��������� ����-�������� � ������������ ����������, ������������� � ��������� ����������
   *
   */
  public void testMaterialParamSender(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file29.xml" );
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
    ModelBlock reciever = (ModelBlock) model.Get("��������");
    assertTrue( reciever != null );
    ModelBlock sender = model.Get("��������", 4);
    assertTrue( sender != null );
    assertEquals( sender.GetIntValue("materialOut"), 100 );
    assertEquals( reciever.GetIntValue("materialInput"), 0 );

    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }

    ModelInputBlockParam senderParam = null;
    ModelInputBlockParam recieverParam = null;
    try {
      senderParam = (ModelInputBlockParam) sender.Get("materialout");
      recieverParam = (ModelInputBlockParam) reciever.GetInpParam( "materialInput" );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( senderParam != null );
    assertTrue( recieverParam != null );
    assertTrue( senderParam.IsDependElement( recieverParam ) );
    int transferCount = ( 100 - sender.GetIntValue("materialOut") ) / 5;
    assertTrue( transferCount > 0 );
    assertEquals( reciever.GetIntValue("materialInput"), 5 * transferCount  );
  }

  /** ��� ������ ��������������� ������ ���� ����� ���� ���� ����������� � ���������� ��������������. ��� ���������
   * ����� ������ ��������������, �� ����� ������� ������ ����. ������� ����, ������� ��� ������ �������������,
   * ������ ���� ���������� �� ��������� ��������������. ��� ������������ � ������� ��������� ����������� �������
   * � ��������� ��������. � ������������ ��������� � ��������� �������� ������������ �� �������, ��������� � ���
   * ��� ����� ���� ��������� �����-���� ��������.
   * ����������� �������� "����������� �� ��������":
   * - ��������� ���� ��������, ������������� � ��������� ����������
   * - �������� ���, ��� �������� �������� ������ �� ������ �� ����������
   * - ����������� ��������� ������
   * - �������� ���, ����� ������������� ������ ������ ��������
   * - �������� ���, ����� ��������� �����-���� ����� ����� ���������� � ����� ����������
   * - �����������, ���
   */
  public void testChangeMaterialParam_Source(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file30.xml" );
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

    ModelBlock reciever = (ModelBlock) model.Get("��������");
    assertTrue( reciever != null );
    ModelBlock sender = model.Get("��������", 4);
    assertTrue( sender != null );
    assertEquals( sender.GetIntValue("materialOut"), 100 );
    assertEquals( reciever.GetIntValue("materialInput"), 0 );
    // ��� ����� �������������� ��������, � �������� �������� ����� �������������� �����
    ModelBlock anotherSender = model.Get("��������", 3);
    assertTrue( anotherSender != null );
    assertEquals( anotherSender.GetIntValue("materialOut"), 100 );
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
    int oldRecieverValue = reciever.GetIntValue( "materialInput" );
    assertTrue( oldRecieverValue > 0 );
    int senderValue = sender.GetIntValue("materialOut");
    assertEquals( 100 - senderValue, oldRecieverValue );
    assertEquals( anotherSender.GetIntValue("materialOut"), 100 );
    //�������� �������� �������������� �������� ���, ����� ������������� ������ ���
    ModelBlockParam criteriaParam = null;
    f = false;
    try {
      criteriaParam = (ModelBlockParam) anotherSender.Get("criteriaValue");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( criteriaParam != null );
    criteriaParam.GetVariable().SetValue(100);
    //�������� ���������� ������, ����� �������� ������������� � ������ ���������
    try {
      model.Execute();
      //model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int newRecieverValue = reciever.GetIntValue( "materialInput" );
    int newSenderValue = sender.GetIntValue("materialOut");
    int newSender1Value = anotherSender.GetIntValue("materialOut");
    assertTrue( newSender1Value < 100 );
    assertEquals( newRecieverValue, (100 - newSenderValue) + (100 - newSender1Value ));
    /*System.out.println("newRecieverValue = " + Integer.toString(newRecieverValue) +
            " newSenderValue = " + Integer.toString( newSenderValue )  +
            " newSender1Value = " + Integer.toString( newSender1Value )
     );*/

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
    assertEquals( newSenderValue, sender.GetIntValue("materialOut") );
  }

  /**����������� �� �� �����, ��� � � ����� testChangeMaterialParam_Source, �� ������ � ��������
   * "����������� �� ��������"
   *
   */
  public void testChangeMaterialParam_Recieve(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
     boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file31.xml" );
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
    ModelBlock sender = (ModelBlock) model.Get("��������");
    assertTrue( sender != null );
    ModelBlock reciever = model.Get("��������", 4);
    assertTrue( reciever != null );

    assertEquals( sender.GetIntValue("material1"), 200 );
    assertEquals( reciever.GetIntValue("inp1"), 0 );
    // ��� ����� �������������� ��������, � �������� �������� ����� �������������� �����
    ModelBlock anotherReciever = model.Get("��������", 3);
    assertTrue( anotherReciever != null );
    assertEquals( anotherReciever.GetIntValue("inp1"), 0 );
    ModelInputBlockParam senderParam = null;
    ModelInputBlockParam recieverParam = null;
    ModelInputBlockParam anotherRecieverParam = null;
    f = false;
    try {
      senderParam = (ModelInputBlockParam) sender.Get("material1");
      recieverParam = (ModelInputBlockParam) reciever.Get("inp1");
      anotherRecieverParam = (ModelInputBlockParam) anotherReciever.Get("inp1");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );

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
    int senderValue = sender.GetIntValue("material1");
    assertTrue( senderValue < 200 );
    int recieverValue = reciever.GetIntValue("inp1");
    assertEquals( 200 - senderValue, recieverValue );
    assertEquals( anotherReciever.GetIntValue("inp1"), 0  );
    assertTrue( senderParam.IsDependElement( recieverParam ) );
    assertTrue( !senderParam.IsDependElement( anotherRecieverParam ) );

    ModelBlockParam anotherRecieverCriteriaParam = null;
    f = false;
    try {
      anotherRecieverCriteriaParam = (ModelBlockParam) anotherReciever.Get("criteriaValue");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    anotherRecieverCriteriaParam.GetVariable().SetValue(100);

    f = false;
    try {
      model.Execute();
      //model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int anotherRecieverValue = anotherReciever.GetIntValue("inp1");
    assertTrue( anotherRecieverValue > 0 );
    senderValue = sender.GetIntValue("material1");
    recieverValue = reciever.GetIntValue("inp1");
    assertEquals( 200 - senderValue, anotherRecieverValue + recieverValue );
    /*System.out.println( "senderValue = " + Integer.toString( senderValue ) +
            " recieverValue = " + Integer.toString( recieverValue ) +
            " anotherRecieverValue = " + Integer.toString( anotherRecieverValue )
    );*/
    assertTrue( !senderParam.IsDependElement( recieverParam ) );
    assertTrue( senderParam.IsDependElement( anotherRecieverParam ) );
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
    assertTrue( anotherReciever.GetIntValue("inp1") > anotherRecieverValue );
    assertEquals( reciever.GetIntValue("inp1"), recieverValue );
  }

  /**����������� ������������ �������� � �������������� ����������� ����������, ������� ����� �������������� �
   * �������� ��������������
   * ��� �������� ����������� ��������, ����� ��� ������ �������������� ����� ���������� ����� ������ �� �����������
   * ���������� ������. ��� ����� ��������� ������, �������:
   * - �������� ��������� ���������� ������;
   * - �������� �������������, ������� ���������� ����������� �� ��������
   * - � �������������� � ���� Enable ����������� �������� ������ �� �������� ���������� ���������� ������
   * - �������� ��������������, ������� �������� �� �������������� ���������������� ��������
   */
  public void testUseSelfParams(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file32.xml" );
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

    ModelBlock muxOwner = (ModelBlock) model.Get("��������");
    assertEquals( muxOwner.GetIntValue("summReciever"), 0 );
    ModelBlock mux = (ModelBlock) model.Get("mux");
    assertEquals( mux.GetIntValue("�������� �����"), 0 );
    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( mux.GetIntValue("�������� �����"), 100 );
    assertEquals( muxOwner.GetIntValue("summReciever"), 100 );
  }

  /**����������� ������������ ���������� ����, ������� ���������� ������ ����������� ���������� ��������������
   *
   */
  public void testUseSelfParams_WithExecCode(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file32_1.xml" );
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

    ModelBlock muxOwner = (ModelBlock) model.Get("��������");
    assertEquals( muxOwner.GetIntValue("summReciever"), 0 );
    ModelBlock mux = (ModelBlock) model.Get("mux");
    assertEquals( mux.GetIntValue("�������� �����"), 0 );
    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( mux.GetIntValue("�������� �����"), 200 );

  }

  /**����������� ����������������� ������ BeforeStart � ��������������. ������ ������ ����������� ����� �������
   * ������� ���������� ����� ������ ��������������.
   * ��� �������� � �������������� ������������ ����������� ������������ ������-���� �� ���������� ���������� ������,
   * � � ������ BeforeStart �������� ���������� ����� ����������. ��������� ����� �� �������� ����� ������-����
   * ��������� ���� ���������� ������, � ��� ���� ����� �� ������������� �� ����� � ����� (����� ������
   * ��������������)
   *
   */
  public void testBeforeStartSection(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file34.xml" );
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
    ModelBlock muxOwner = (ModelBlock) model.Get("��������");
    assertEquals( muxOwner.GetIntValue("summReciever"), 0 );
    ModelBlock mux = (ModelBlock) model.Get("mux");
    assertEquals( mux.GetIntValue("�������� �����"), 0 );
    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( mux.GetIntValue("�������� �����"), 100 );
    assertEquals( muxOwner.GetIntValue("summReciever"), 100 );
  }

  /**�������� ������������ ��������� ��������������� ��������� ��������, ����� � ������� BeforeStart ������������
   * ����������, ��������� �� ������ ���������� �����. �� ����� ���� ������ ���� �� ������, ��������� � ����� ������
   * ������ ������� ����� ��������������� (� ���� ����� ������ ��� ����������� ��������� �������� ���������)
   * � ������� BeforeStart ������ �������������� ������ ���������, ��������� ������������� � ����� ��������������,
   * ���� ���������, ��������� �� ������ �����-��������� ��������������
   *
   *
   */
  public void testBeforeStartError(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    try {
      ReadModel( FPathToXMLFiles + "file35.xml" );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( !f );

  }

  //////////////////////////////////////////////////////////////////////////////////////////////
  // ����������� ����������� ���������� ������, ������� �������� ����� ���������� ��������������
  //////////////////////////////////////////////////////////////////////////////////////////////

  /**����������� ������������ ������ ������������� ���������� ������, ������� ����� �������� ��� ������� ��������
   *
   */
  public void testReadMaxEnableBlockCount(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file41.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelMultiplexor mux = (ModelMultiplexor) model.Get("mux");
    assertTrue( mux != null );
    assertEquals( mux.GetMaxEnableBlockCount(), 5 );

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
    assertEquals( mux.GetEnabledElementsCount(), 3 );
  }

  /**����������� ������������ ���������� �������� ��� ������, ������������������ � ��������������. ��� ����, �
   * ������������ ��������� �������� maxCount.
   *
   */
  public void testCalculateCriteria_WithMaxBlockCount(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file42.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelMultiplexor mux = (ModelMultiplexor) model.Get("mux");
    assertTrue( mux != null );
    ModelBlock block = (ModelBlock) model.Get("��������");
    assertTrue( block != null );
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
    assertEquals( mux.GetEnabledElementsCount(), 3 );
    assertEquals( block.GetIntValue("summReciever"), 20 );
  }

  public void testCalculateCriteria_WithSingleEnableBlock(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file43.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelMultiplexor mux = (ModelMultiplexor) model.Get("mux");
    assertTrue( mux != null );
    ModelBlock block = (ModelBlock) model.Get("��������");
    assertTrue( block != null );
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
    assertEquals( mux.GetEnabledElementsCount(), 1 );
    assertTrue( block.GetIntValue("summReciever") > 10 );
  }

  /**����������� ������������ ��������� ��������, ����� ���� � ��� �� ���� (����������� ����) ��������� � ����������
   * ��������� �����������.
   * ���� - ����������� �� ��������, ������� ���������.
   * ������ �������� ����������� ������� �����������. �� ����� ���� ��� ���� "����������� �� ��������", �� �������������,
   * ������������ ��� ����������� ������ ��� ������������� "����������� �� ��������"
   *
   */
  public void testMultipleCompetition(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    try {
      ReadModel( FPathToXMLFiles + "file45.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    /*f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( !f );*/
  }

  /**����������� �������� ����������� �� ��������, ����� � ��������� ���� ��������� ������� ����������,
   * �� �� ��� ��� ������������, �.�. ����� ������� ���������� ������������� �������������� � ��������������, � �����
   * � �������������� �� ��������������.
   * ������� ��������� ��������, ����� ������������� �������� ��������� ������ ���������� ���������� �����, �������
   * ����� ��������� � ����������� �����-���������.
   *  ��� �������� ���������� � ���, ��� ��� ���������� ���� �������� ������������� �������� �������� ����, � ��������
   * �������� ������������ ������� �������� ��������������. � ������ ����� ����, ������� �������� null pointer exception,
   * ���� ������ ������
   *
   */
  public void testNotUsedInputParams(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file46.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
  }

  /**����������� ��������������� ���������� ������� criteria. ���� � ���, ��� ���� ������ ������ ����������� ������
   * ��� ��� ������, ��� ������� ��������� ������� ��������� enable ����� true
   * ��� ���� �������� � ������ criteria ���������� �������, �������� �������� ����� ������������� ��� ������
   * ���������� ������� �������
   *
   */
  public void testExecCriteriaScript_OnlyForEnableBlock(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file47.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      model.Execute();
      //model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock mux = (ModelBlock)model.Get("mux");
    assertTrue( mux != null );
    assertEquals( mux.GetIntValue("queueSize"), 5 );
    assertEquals( mux.GetIntValue("muxCounter"), 5 );
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////
  ////////�������� ��������� �������� skipfirst -  ���������, ������� ����������, ����� ��
  // ����� ���� � ������������ ���������  ����� ������ ���������������
  ////////////////////////////////////////////////////////////////////////////////////////////////


  /**�������� ������ �������� �������� skipfirst - ���������,��� ������� �������� ����� �����
   * ��������������
   * ����������� ������ ������, � �� ���������
   */
  public void testReadSkipFirst_FromAttributeValue(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file53.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    ModelMultiplexorWithSkipParam mux = (ModelMultiplexorWithSkipParam) model.Get( "mux" );
    assertTrue( mux != null );
    assertEquals( mux.GetSkipFirstValue(), 1 );
  }

  /**�������� ������  �������� skipfirst �� �������� ������
   * ����������� ������ ������, � �� ���������
   */
  public void testReadSkipFirst_FromConstant(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file54.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    ModelMultiplexorWithSkipParam mux = (ModelMultiplexorWithSkipParam) model.Get( "mux" );
    assertTrue( mux != null );
    assertEquals( mux.GetSkipFirstValue(), 2 );
  }

  public void testReadSkipFirst_FromOwnerParam(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file55.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    ModelMultiplexorWithSkipParam mux = (ModelMultiplexorWithSkipParam) model.Get( "mux" );
    assertTrue( mux != null );
    assertEquals( mux.GetSkipFirstValue(), 155 );
  }

  public void testReadSkipFirst_FromSelfMuxParam(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file56.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    ModelMultiplexorWithSkipParam mux = (ModelMultiplexorWithSkipParam) model.Get( "mux" );
    assertTrue( mux != null );
    assertEquals( mux.GetSkipFirstValue(), 1 );
  }

  public void testErrorReadSkipFirst_WrongParamName(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    try {
      ReadModel( FPathToXMLFiles + "file57.xml" );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( !f );

  }

  public void testErrorReadSkipFirst_WrongParamType(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    try {
      ReadModel( FPathToXMLFiles + "file58.xml" );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( !f );
  }

  /**����������� ������������ ��������� ��������� skipfirst � ������ ����������� �� ��������
   *
   */
  public void testSkipFirst_Exec1(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file59.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    ModelMultiplexorWithSkipParam mux = (ModelMultiplexorWithSkipParam) model.Get( "mux" );
    assertTrue( mux != null );
    assertEquals( mux.GetSkipFirstValue(), 3 );

    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = model.Get("reciever", 6);
    assertTrue( block != null );
    assertEquals( block.GetIntValue("input2"),5 );
  }

  /**����������� ������������ ��������� ��������� skipfirst � �������� �����������
   * �� ��������
   *
   */
  public void testSkipFirst_Exec2(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file60.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("reciever");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("input2"),12 );
  }

  /**����������� ������������ ���������, ����� skipfirst-����� �������� �����������
   * ���������� ��������������, � ��� ������ �� ���� ���������� ������
   *
   */
  public void testSkipFirst_WithChangeMuxSkipParam(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file61.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("reciever");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("input2"),12 );

    ModelMultiplexor mux = (ModelMultiplexor) model.Get("mux");
    assertTrue( mux != null );
    ModelBlockParam param = null;
    f = false;
    int skipValue = 0;
    try {
      param = (ModelBlockParam) mux.Get("skipFirstParam");
      assertTrue( param != null );
      skipValue = param.GetVariable().GetIntValue();
      assertEquals( skipValue, 2 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    param.GetVariable().SetValue( 3 );
    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( block.GetIntValue("input2"),11 );
  }

  /**�������� ������, ����� skipfirst �������� �������� ���������� ��������� ��������������,
   * � �� �������� �� ���� ���������� ��������������
   *
   */
  public void testSkipFirst_WithChangeSkipFirstParam2(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ReadModel( FPathToXMLFiles + "file62.xml" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("reciever");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("input2"),12 );
    assertEquals( block.GetIntValue("skipFirstParam"), 2 );

    ModelBlockParam param;
    f = false;
    try {
      param = (ModelBlockParam) block.Get("skipFirstParam");
      assertTrue( param != null );
      param.GetVariable().SetValue( 3 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      model.Execute();
      model.Execute();
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( block.GetIntValue("input2"),11 );
  }

}

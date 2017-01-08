package mp.elements;

import junit.framework.TestCase;
import mp.parser.ScriptLanguageExt;
import mp.parser.ScriptException;
import mp.parser.Variable;
import mp.utils.ServiceLocator;

import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * Date: 27.05.2008
 */

public class ModelAggregatorTest extends TestCase {

  public ModelAggregatorTest( String name ){
    super(name);
  }

  private static ModelDynamicBlock GetOwner(){
    ModelDynamicBlock result = new ModelDynamicBlock(null, "0", 1) {
      ScriptLanguageExt ext = null;

      public void SetDynamicLinker() throws ModelException {
      }

      public ModelBlock GetDynamicBlockOwner() {
        return null;
      }

      public String GetDynamicBlockEtalonName() {
        return null;  
      }

      public void AddSource(ModelBlock aSourceBlock) throws ModelException {
      }

      protected void ReCreateAllInputParams() throws ModelException {
      }

      public void Execute() throws ModelException, ScriptException {
      }

      public void Execute(ModelTime aCurrentTime) throws ModelException, ScriptException {
      }

      public void PrintExecutionTime() {
      }

      public ScriptLanguageExt GetLanguageExt() {
        if ( ext == null ){
          ext = new ScriptLanguageExt();
          ModelBlockParam p;
          p = new ModelCalculatedElement(this, "var1", 2);
          try {
            p.SetVarInfo("integer", "0");
            ext.AddVariable( p.GetVariable() );
            this.AddOutParam( p );

            p = new ModelCalculatedElement(this, "var2", 3);
            p.SetVarInfo("integer", "0");
            this.AddOutParam( p );
            ext.AddVariable( p.GetVariable() );

            p = new ModelCalculatedElement(this, "enable",4);
            p.SetVarInfo("boolean","false");
            this.AddOutParam( p );
            ext.AddVariable( p.GetVariable() );

            p = new ModelCalculatedElement( this, "summ", 5 );
            p.SetVarInfo("integer", "0");
            this.AddOutParam( p );
            ext.AddVariable( p.GetVariable() );
           } catch (ScriptException e) {
             e.printStackTrace();
           } catch (ModelException e) {
            e.printStackTrace();
           }
        }
        return ext;
      }      

    };
    result.GetLanguageExt();
    return result;
  }

  private static ModelSimpleBlock GetResourceBlock( String aBlockName, String aVar1Value, String aVar2Value ){
    ModelSimpleBlock result;
    result = new ModelSimpleBlock(null, aBlockName, ServiceLocator.GetNextId());
    ModelCalculatedElement param1 = new ModelCalculatedElement( result, "var1", ServiceLocator.GetNextId() );
    try {
      param1.SetVarInfo("integer",aVar1Value );
      result.AddOutParam( param1 );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    ModelCalculatedElement param2  = new ModelCalculatedElement( result, "var2", ServiceLocator.GetNextId() );
    try {
      param2.SetVarInfo( "integer", aVar2Value );
      result.AddOutParam( param2 );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void testCreateParserAndUsedParamsList(){
    ModelDynamicBlock owner = GetOwner();
    MultiBlockExecutor ex = null;
    ModelBlockParam resultParam = null;
    boolean f = false;
    String s = " enable := var1 > 0 ";
    try {
      resultParam = (ModelBlockParam) owner.Get( "enable" );
      ex = new EnableExecutor( owner, resultParam, s );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( resultParam != null );
    f = false;
    try {
      ex.UpdateServiceInformation();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( ex.GetUsedParamCount(), 1 );
  }

  /**����������� ������������ ���������� ���������� �����
   * ����������� ���������� ����������� ����� (��������� �������� ������������� ����������� � ����������� �����
   * ������� )
   * ����������� ������ ���������� � ���������� �����
   */
  public void testAddResourceBlock(){
    ModelDynamicBlock owner = GetOwner();
    MultiBlockExecutor ex = null;
    ModelBlockParam resultParam = null;
    boolean f = false;
    String s = " enable := var1 > 0 ";
    try {
      resultParam = (ModelBlockParam) owner.Get( "enable" );
      ex = new EnableExecutor( owner, resultParam, s );
      ex.UpdateServiceInformation();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace(); 
    }
    assertTrue( f );
    ModelBlock block = GetResourceBlock( "block", "0", "0" );
    assertTrue( block != null );
    f = false;
    try {
      ex.AddResourceBlock( block );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( ex.GetResourceCount(), 1 );
    //������� ����, ������� ����� �� ������ ���������� ��� ������, ������ ��� � ��� ��� ������ ����������
    ModelSimpleBlock errorBlock = new ModelSimpleBlock(null, "block2", ServiceLocator.GetNextId());
    f = false;
    try {
      ex.AddResourceBlock( errorBlock );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
    assertEquals( ex.GetResourceCount(), 1 );

    ModelCalculatedElement p = new ModelCalculatedElement(errorBlock, "var1", ServiceLocator.GetNextId());
    ModelCalculatedElement p2 = new ModelCalculatedElement(errorBlock, "var2", ServiceLocator.GetNextId());
    try {
      p.SetVarInfo("string", "123");
      p2.SetVarInfo("integer", "0");
      errorBlock.AddOutParam( p );
      errorBlock.AddOutParam( p2 );
    } catch (ModelException e) {
      e.printStackTrace();
    }

    f = false;
    try {
      ex.AddResourceBlock( errorBlock );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
    assertEquals( ex.GetResourceCount(), 1 );
  }

  /**����������� ������������ ���������� �������, ������� ��������� ��������� ��������. ������ �����������
   * ��� ���������� ������
   *
   */
  public void testExecAndGetBooleanValue(){
    ModelDynamicBlock owner = GetOwner();
    MultiBlockExecutor ex = null;
    ModelBlockParam resultParam = null;
    boolean f = false;
    String s = " enable := var1 > var2 ";
    try {
      resultParam = (ModelBlockParam) owner.Get( "enable" );
      ex = new EnableExecutor( owner, resultParam, s );
      ex.UpdateServiceInformation();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    ModelBlock block = GetResourceBlock("block", "1", "1");
    ModelBlock block2 = GetResourceBlock("block", "1", "2");
    ModelBlock block3 = GetResourceBlock("block", "2", "3");
    try {
      ex.AddResourceBlock( block );
      ex.AddResourceBlock( block2 );
      ex.AddResourceBlock( block3 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      ex.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();  
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    //������� ���������
    int falseCount = 0;
    int trueCount = 0;
    Variable result = ex.GetFirstResult();
    boolean res;
    while ( result != null ){
      res = result.GetBooleanValue();
      if ( res ) {
        trueCount++;
      } else {
        falseCount++;
      }
      result = ex.GetNextResult();
    }
    assertEquals( trueCount, 0 );
    assertEquals( falseCount, 3 );
  }

  public void testExecAndGetIntValue(){
    ModelDynamicBlock owner = GetOwner();
    MultiBlockExecutor ex = null;
    ModelBlockParam resultParam = null;
    boolean f = false;
    String s = " summ := var1 + var2 ";
    try {
      resultParam = (ModelBlockParam) owner.Get( "summ" );
      ex = new EnableExecutor( owner, resultParam, s );
      ex.UpdateServiceInformation();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertTrue( resultParam != null );

    f = false;
    ModelBlock block = GetResourceBlock("block", "10", "1");
    ModelBlock block2 = GetResourceBlock("block", "100", "20");
    ModelBlock block3 = GetResourceBlock("block", "2000", "300");
    try {
      ex.AddResourceBlock( block );
      ex.AddResourceBlock( block2 );
      ex.AddResourceBlock( block3 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );

    f = false;
    try {
      ex.ExecuteScript();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    int summ = 0;
    Variable res = ex.GetFirstResult();
    while ( res != null ){
      try {
        summ = summ + res.GetIntValue();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      res = ex.GetNextResult();
    }
    assertEquals( summ, 2431 );

    //��������� ���������� ���������� ����
    assertEquals( 3, ex.GetExecCount() );
    assertEquals( ex.GetEnterCount(), 1 );
    try {
      ex.ExecuteScript();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertEquals( ex.GetExecCount(), 3 ); //���������� �������� ���������� ������� �� ������ �����������
    assertEquals( ex.GetEnterCount(), 2 );

    //������ �������� ���� �� ����������, ����� ����� �������� ���������� ������� ������� �� 1,
    // ��� ��� ����� ����� ������������� ������ ������ ��� ������ �����
    try {
      ModelBlockParam param = (ModelBlockParam) block.Get("var2");
      assertTrue( param != null );
      param.GetVariable().SetValue( 2 );
    } catch (ModelException e) {
      e.printStackTrace();
    }
    try {
      ex.ExecuteScript();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();  
    }
    assertEquals( ex.GetExecCount(), 4 );
    assertEquals( ex.GetEnterCount(), 3 );

    res = ex.GetFirstResult();
    summ = 0;
    while ( res != null ){
      try {
        summ = summ + res.GetIntValue();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      res = ex.GetNextResult();
    }
    assertEquals( summ, 2432 );

  }

  /**����������� ������������ ������ �����������
   * ����������� ������ ������ 
   */
  public void testReadAggregator(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator1.xml");
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
    ModelAggregator aggr = (ModelAggregator) model.Get("aggr1");
    assertTrue( aggr != null );
  }

  /**����������� ������������ ������ ������ � ������� - � ���� ����������� ������������ ����������,
   * ������� ����������� � �� ���������, � � �������, � � ����� �����������
   *
   */
  public void testReadAggregator_WithError(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator2.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( model != null );
    assertTrue( f );

    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  /** ����������� ������������ ��������  �������� � �����������.
   * �����������:
   * - ������� ������ ������������ �������
   * - ���������� ������, ������� �� ������ ������������
   *
   * - ���������� ��������, ������� ������������ �������� �������
   */
  public void testCreateEnableExecutor(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator3.xml");
      assertTrue( model != null );
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
    ModelAggregator aggr = (ModelAggregator) model.Get("aggr1");
    EnableExecutor executor = (EnableExecutor) aggr.GetEnableExecutor();
    assertTrue( executor != null );
    assertEquals( executor.GetResourceCount(), 10 );
    assertEquals( aggr.GetValueExecutorsCount(), 2 );
    //���������, ������� ��� ���������� ����������� ������. ��������� ���������� ������, �� ����������
    // ���������� ������ ���� ����� ���������� ������
    assertEquals( executor.GetExecCount(), 10 );
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    //��������� �� ���� �� ������������ � ����������� ������� ���������� �� ����������, ����������
    // ���������� ������������ ������� �������� �������
    assertEquals( executor.GetExecCount(), 10 );
    //���������� �������� �������� ����� �� ������������ ����������, ����� ���������� ���������� ������������
    // ������� ����������� �� 1
    ModelBlock block = model.Get("etalon", 0);
    assertTrue( block != null );
    ModelBlockParam param = null;
    f = false;
    try {
      param = (ModelBlockParam) block.Get("param1");
      assertTrue( param != null );
      param.GetVariable().SetValue( 10 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
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
    assertEquals( executor.GetExecCount(), 11 );

  }

  /**����������� ������������ ����������� ������ � �������.
   * ������ ���� ������ ����� �������������� ��� ���������, ����� �� ��� ���� ���
   * ����� ����� ������������ �������� � ����� �� ��� �������� ����������� �  
   */
  public void testEsistsArrayInEnableScript(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator5.xml");
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
    ModelAggregator aggr = (ModelAggregator) model.Get("aggr1");
    EnableExecutor executor = (EnableExecutor) aggr.GetEnableExecutor();
    assertTrue( executor != null );
    int i = 0;
    int existsCount = 0;
    ExistsService es = executor.GetEnableArray();
    boolean b = false;
    while ( i < executor.GetResourceCount()  ) {
      try {
        b = es.IsExistsInList( i );
      } catch (ModelException e) {
        e.printStackTrace();
      }
      if ( b ) {
        existsCount++;
      }
      i++;
    }
    assertEquals( existsCount, 4 );
  }

  /**
   * ����������� ������������ �������� � ������ ��������, ������� ������������ �������� ��� �������
   * �����������:
   * - �������� ����������� ���������� ����� ��������
   * - ������������ �������� � ������ ���������� �� ������� ��������
   * - ������������ �������� ��������
   * - ��������������� ��������� - �� ���� ������ ������ ����������� ������ ��� ��� ������, � �������� ����������
   *   �������� ���������� � � ������� ���� ���������� �� �������
   *
   * ��������!!!
   * ���� ���� ������ ������� �� ������� ���������� ������ � ������. ����� ��������������, ��� ������� �����������
   * ����������, � ������ ����� ����, � ������� ������������ ������������ � ����������� ���������
   */
  public void testCreateValueScript_WithEnable(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator4.xml");
      assertTrue( model != null );
      model.Execute();
      //model.Execute();
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
    ModelAggregator aggr = (ModelAggregator) model.Get("aggr1");
    assertTrue( aggr != null );
    assertEquals( aggr.GetValueExecutorsCount(), 1 );
    ValueExecutor ve = aggr.GetValueExecutor( 0 );
    assertTrue( ve != null );
    int enterCount = ve.GetEnterCount();
    assertEquals( enterCount, 1 );
    int execCount = ve.GetExecCount();
    assertEquals( execCount, 10 );// ������ ������ ����������� ��� ���� ����������, �.�. 10 ���

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
    enterCount = ve.GetEnterCount();
    assertEquals( enterCount, 2 );
    execCount = ve.GetExecCount();
    // ������ ������ ����������� ������ ��� ��� ����������, ��� ������� ���������� ����������
    //� ���������� ���� ������ ��� ���� ����������. ��� ���� ���������� ��������� ���� ����������
    assertEquals( execCount, 13 );

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
    enterCount = ve.GetEnterCount();
    assertEquals( enterCount, 3 );
    execCount = ve.GetExecCount();
    assertEquals( execCount, 13 );//������ ������ �� ����������, ������� ���������� �������� ���������� �������
                                  // �������� �������

    //�������� ����, ��� �������� �������� ������ ��������, � ������ � ��� �������� ���������, �������������
    // ��� �������� ��������. ���������, ��� ���������� �������� ���������� ������� ���������� �� 1
    f = false;
    ModelBlock block = model.Get("etalon", 0);
    assertTrue( block != null );
    ModelBlockParam param2 = null;
    try {
      param2 = (ModelBlockParam) block.Get("param2");
      assertTrue( param2 != null );
      param2.GetVariable().SetValue(100);
      model.Execute();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    enterCount = ve.GetEnterCount();
    assertEquals( enterCount, 4 );
    execCount = ve.GetExecCount();
    assertEquals( execCount, 14 );

    //�������� ����, ��� �������� �������� ������ ��������, � ������ � ��� �������� ���������, �������������
    // ��� �������� ��������. ���������, ��� ���������� �������� ���������� ������� �� ����������
    f = false;
    ModelBlock block9 = model.Get("etalon", 9);
    assertTrue( block9 != null );
    ModelBlockParam param2_9 = null;
    try {
      param2_9 = (ModelBlockParam) block.Get("param2");
      assertTrue( param2_9 != null );
      param2_9.GetVariable().SetValue(100);
      model.Execute();
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    enterCount = ve.GetEnterCount();
    assertEquals( enterCount, 5 );
    execCount = ve.GetExecCount();
    assertEquals( execCount, 14 );


  }

  /**����������� ������������ �������� � ���������������� ������� �����������, ����������q ��������.
   *
   */
  public void testFunctionSumm_WithoutValueParam(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator6.xml");
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
    ModelAggregator aggr = (ModelAggregator) model.Get("aggr1");
    assertTrue( aggr != null );
    assertEquals( aggr.GetFunctionsCount(), 1 );
    MultiBlockExecutor function = aggr.GetFunctionExecutor( 0 );
    assertTrue( function instanceof FunctionExecutorSumm );
    ModelBlockParam result = function.GetResultParam();
    assertTrue( result.GetName().equalsIgnoreCase("valueSumm") );
    int res = 0;
    try {
      res = result.GetVariable().GetIntValue();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertEquals( res, 6 );
    //���������, ����� ����� �� ���������� ��� ��������� ���������� ������
    f = false;
    try {
      model.Execute();
      res = result.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 6 );
  }

  /**��������� ������������ ������ ���������, ����� �� ������ �������������������, ������������ � ��� ��
   * �����������
   *
   */
  public void testFunctionSumm_ValueParam(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator7.xml");
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
    ModelAggregator aggr = (ModelAggregator) model.Get("aggr1");
    assertTrue( aggr != null );

    MultiBlockExecutor function = aggr.GetFunctionExecutor( 0 );
    assertTrue( function instanceof FunctionExecutorSumm );
    ModelBlockParam result = function.GetResultParam();
    assertTrue( result.GetName().equalsIgnoreCase("valueSumm") );
    int res = 0;
    try {
      res = result.GetVariable().GetIntValue();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertEquals( res, 66 );
    f = false;
    try {
      model.Execute();
      res = result.GetVariable().GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    assertEquals( res, 66 );
  }

  public void testFunctionSumm_WithoutEnable(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator8.xml");
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
    ModelAggregator aggr = (ModelAggregator) model.Get("aggr1");
    assertTrue( aggr != null );
    assertEquals( aggr.GetFunctionsCount(), 1 );
    MultiBlockExecutor function = aggr.GetFunctionExecutor( 0 );
    assertTrue( function instanceof FunctionExecutorSumm );
    ModelBlockParam result = function.GetResultParam();
    assertTrue( result.GetName().equalsIgnoreCase("valueSumm") );
    int res = 0;
    try {
      res = result.GetVariable().GetIntValue();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertEquals( res, 110 );

  }

  public void testFunctionSumm_ErrorValueName(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator9.xml");
      assertTrue( model != null );
      model.Execute();
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      //e.printStackTrace();
    } catch (SAXException e) {
      //e.printStackTrace();
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );

  }

  public void testLinkToAggregator(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator10.xml");
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
    ModelBlock block = (ModelBlock) model.Get("owner");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("summ"), 110 );
  }

  public void testGetAggregatorQueueSize_WithoutEnable(){
        mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator11.xml");
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
    ModelBlock block = (ModelBlock) model.Get("owner");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("queueSize"), 5 );
  }

  public void testGetAggregatorQueueSize_WithEnable(){
        mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelMuxTest.ReadModel( ModelMuxTest.FPathToXMLFiles  + "aggregator12.xml");
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
    ModelBlock block = (ModelBlock) model.Get("owner");
    assertTrue( block != null );
    assertEquals( block.GetIntValue("queueSize"), 2 );
  }



}

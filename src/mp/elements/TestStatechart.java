package mp.elements;

import junit.framework.TestCase;
import mp.parser.Variable;
import mp.parser.ScriptLanguageExt;
import mp.parser.ScriptException;
import mp.parser.ParserFactory;

import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * User: atsv
 * Date: 09.11.2007
 */
public class TestStatechart extends TestCase {

  public TestStatechart( String name ){
    super( name );
  }

  public void testStateChartAddInnerState(){
    AutomatState rootState = new AutomatState(null, "root",1);
    AutomatState childState1 = new AutomatState(rootState,"2",2);
    AutomatState childState2 = new AutomatState(rootState,"2",3);
    boolean f = false;
    try {
      rootState.AddElement( childState1 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      rootState.AddElement( childState2 );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  public void testStateChartAddElement(){
    AutomatState rootState = new AutomatState(null, "root",1);
    AutomatState childState1 = new AutomatState(rootState,"2",2);
    ModelSimpleBlock block = new ModelSimpleBlock(null, "33",3);
    boolean f = false;
    try {
      rootState.AddElement( childState1 );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    f = false;
    try {
      rootState.AddElement( block );
      f = true;
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  /** ����������� ������������ ��������� ����, ������� ������ ����������� ������ ���� ��� - ��� �������������.
   *  ������� ������� ��������� ������ ��� ��� ����. � ����� ������� ��������� ������ ���� ���� � ��� ��,
   * �������� �� ��, ��� ����������� ��� ������������� ��������� ���������� ��� ����������������
   */
  public void testStateExecInitCode(){
    AutomatState state = new AutomatState(null,"1",1);
    Variable var1 = new Variable(0);
    var1.SetName("var1");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    boolean f = false;
    int i = 0;
    try {
      ext.AddVariable( var1 );
      state.SetlanguageExt( ext );
      state.SetInitCode("var1 := var1 + 5");
      state.ExecuteInitCode();
      i = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals( i, 5 );
    f = false;
    try {
      state.ExecuteInitCode();
      i = var1.GetIntValue();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals( i, 5 );
  }

  public void testExecuteInitWithChild(){
      AutomatState state = new AutomatState(null,"1",1);
      AutomatState childState1 = new AutomatState(state, "2",2);
      AutomatState childState2 = new AutomatState(state, "3",3);
      Variable var1 = new Variable(0);
      var1.SetName("var1");
      ScriptLanguageExt ext = new ScriptLanguageExt();
      boolean f = false;
      int i = 0;
      try {
        ext.AddVariable( var1 );
        state.AddElement( childState1 );
        state.AddElement( childState2 );
        state.SetlanguageExt( ext );
        String s = "var1 := var1 + 1";
        state.SetInitCode( s );
        childState1.SetInitCode( s );
        childState2.SetInitCode( s );
        state.ExecuteInitCode();
        i = var1.GetIntValue();
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertEquals(i, 3);
      try {
        state.ExecuteInitCode();
        i = var1.GetIntValue();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertEquals(i,3);
    }

  /**����������� ������������ ��������  �� ������� - ����������.
   * ���� ��� ����������  ������������ �������� ���� ����������.
   * ������ �������� �������� ������������ ��������� �������� �� ���������� ����������� ����.
   */
  public void testValueTransition(){
    AutomatTransitionByValue transition = new AutomatTransitionByValue(null, "1",1);
    Variable var1 = new Variable(0);
    var1.SetName("var1");
    Variable var2 = new Variable(true);
    var2.SetName("var2");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      ext.AddVariable( var2 );
      transition.SetlanguageExt( ext );
      transition.SetTransitionVariable( "var1" );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      //System.out.println( e.getMessage() );
    }
    assertTrue( !f );
    try {
      transition.SetTransitionVariable( "var2" );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    f = false;
    try {
      assertTrue( transition.IsTransitionEnabled( null ) );
      var2.SetValue( false );
      assertTrue( !transition.IsTransitionEnabled( null ) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
  }

  /**����������� ������������ ��������� ��������� ��������� ��������: �� ������ "���������" ���� ��������� ���������.
   * ��� ��������� ����� ���������, ��� ������ ���������, ���� �� ������������� ����������� �������� � ������
   * ���������. ������ ���������, ���� ����� ������������� ����� ��������� ���������
   */
  public void testMultipleTransition(){
    AutomatState state = new AutomatState(null, "state1", 1);
    AutomatTransitionByValue trans1 = new AutomatTransitionByValue(state, "2", 2);
    AutomatTransitionByValue trans2 = new AutomatTransitionByValue(state, "3", 3);
    ScriptLanguageExt ext = new ScriptLanguageExt();
    Variable var1 = new Variable(true);
    var1.SetName("var1");
    Variable var2 = new Variable(true);
    var2.SetName("var2");
    boolean f = false;
    try {
      ext.AddVariable(var1);
      ext.AddVariable(var2);
      state.AddElement( trans1 );
      state.AddElement( trans2 );
      state.SetlanguageExt( ext );
      trans1.SetTransitionVariable( "var1" );
      trans2.SetTransitionVariable( "var2" );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    f = false;
    try {
      state.SetActive(null);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      //System.out.println( e.getMessage() );
    }
    assertTrue( !f );
    var2.SetValue( false );
    try {
      state.SetActive(null);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
  }

  /**�������� ������������ ���������� ������ SetActive() ��� ���������, � �������� ��� �� ��������� ���������,
   * �� ���������. ���������, ��� ���������� ������� ���������� ��� ������, � ������� ������ �� ������
   */
  public void testSetActive_StateWithNoTransition(){
    AutomatState state = new AutomatState(null, "1",1);
    boolean f = false;
    String s = null;
    try {
      s = state.SetActive(null);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals( s, null );
  }

  /**����������� �������� ������������ ������������ ������ SetActive() ��� ������������� �������.
   * ����� ������� ���� ���������, � ������� ����� ���������� ������� � ��������� ���������.
   * ����� ����� ����������, ������������ ������� ���������, � ������� SetActive ������ ������� �������� ����������
   * ���������
   */
  public void testSetActiveState_1(){
    AutomatState state1 = new AutomatState(null, "1", 1);
    AutomatTransitionByValue trans1 = new AutomatTransitionByValue(state1, "2",2);
    trans1.SetNextStateName("state2");
    Variable var1 = new Variable(false);
    var1.SetName("var1");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      state1.AddElement( trans1 );
      state1.SetlanguageExt( ext );
      trans1.SetTransitionVariable("var1");
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f); //���������� ������ �������
    f = false;
    String s = null;
    try {
      s = state1.SetActive(null);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals(s, null);
    var1.SetValue( true );
    f = false;
    try {
      s = state1.SetActive(null);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertTrue( "state2".equalsIgnoreCase( s ) );
  }

  /**�������� ������������ ���������������� ������ SetActive ��� ���������, � ������� ���� ��������� ���������.
   * ��� ����� ��������� �������� ���������, ��� ���� �������� ��������� ��������� � ������, �������� �� begin.
   * ����� ������ ������ ������.
   * ����� ������ ������ ������������ ���������� ��������� �������� � ������ begin, � ��������� ����� ������
   * SetActive �� ������ �������� � ������
   */
  public void testSetActive_WithChildWithoutTransition(){
    AutomatState rootState = new AutomatState(null, "1",1);
    AutomatState childState = new AutomatState( rootState,"2",2 );
    boolean f = false;
    try {
      rootState.AddElement( childState );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    f = false;
    try {
      rootState.SetActive(null);
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      //System.out.println( e.getMessage() );
    }
    assertTrue( !f );
    //��������� ��������� "begin"
    AutomatState beginState = new AutomatState(rootState,"begin",3);
    f = false;
    try {
      rootState.AddElement( beginState );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    f = false;
    String s = null;
    try {
      s = rootState.SetActive( null );
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertEquals( beginState, rootState.GetActiveState() );
    assertEquals( s, null );
  }

  /**����������� ������������ ����������� ��������� ��������� � �������� ����������: ��������� ���� ������������
     * ��������� � ��� ��������. ���� �� �������� - � ������ begin. � ���� �������� ��������� ����� ������� � �������������
     * �������� ��������, ������ ������� �������� ������ ��������� �� ������ ���������. �� ���� ����� ���������� ������
     * SetActive ��� ��������� ���������, � ��� ������ ����� �������� ������ ���������.
     * ����� ����������� ���� ���������� ����, ������� ������ ����������� ��� ������������� �������� (� ������� Transition)
     */
    public void testSetActive_ChildWithTransition(){
      String s = "state2";
      AutomatState rootState = new AutomatState(null,"1",1);
      AutomatState beginState = new AutomatState(rootState,"begin",2);
      AutomatState childState = new AutomatState(rootState,s,3);
      AutomatTransitionByValue trans1 = new AutomatTransitionByValue(null,"1",4);
      Variable var1 = new Variable(true);
      var1.SetName("var1");
      Variable var2 = new Variable(0);
      var2.SetName("var2");
      ScriptLanguageExt ext = new ScriptLanguageExt();
      boolean f = false;
      try {
        ext.AddVariable( var1 );
        ext.AddVariable( var2 );
        rootState.AddElement( beginState );
        rootState.AddElement( childState );
        beginState.AddElement( trans1 );
        rootState.SetlanguageExt( ext );
        trans1.SetTransitionVariable("var1");
        trans1.SetNextStateName(s);
        trans1.SetCode("var2 := 20");
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      f = false;
      int i = 0;
      try {
        rootState.SetActive(null);
        i = var2.GetIntValue();
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertEquals( rootState.GetActiveState(),childState );
      assertEquals(i,20);
    }

  public void testTimeoutTransition_Error(){
    AutomatTransitionTimeout trans1 = new AutomatTransitionTimeout(null,"1",1);
    Variable var1 = new Variable(true);
    var1.SetName("var1");
    ScriptLanguageExt ext = new ScriptLanguageExt();
    boolean f = false;
    try {
      ext.AddVariable( var1 );
      trans1.SetlanguageExt( ext );
      trans1.SetTimeoutVariable("var1");
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      //e.printStackTrace();
    }
    assertTrue( !f );
  }

  public void testTimeoutTransition(){
      AutomatTransitionTimeout trans1 = new AutomatTransitionTimeout(null,"1",1);
      Variable var1 = new Variable(1);
      var1.SetName("var1");
      ScriptLanguageExt ext = new ScriptLanguageExt();
      boolean f = false;
      try {
        ext.AddVariable( var1 );
        trans1.SetlanguageExt( ext );
        trans1.SetTimeoutVariable("var1");
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      f = false;
      ModelTime time = new ModelTime(0.5);
      double d = 0;
      try {
        //������� �������� ���������� var1 - 1, ������� ���� ����������
        assertTrue( !trans1.IsTransitionEnabled( time ) );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      try {
        time = trans1.GetTransitionTime();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( time != null );
      d = time.GetValue();
      assertEquals( Double.compare(d, 1),0 );
      f = false;
      time = new ModelTime(10);
      try {
        assertTrue( trans1.IsTransitionEnabled( time ) );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
    }


  /** ����������� ������������ ������ ������� GetNearestEventTime.
     *  � ���� ����� ��������� ��������� ������� ��� �������� ��������. � ���� ������ ����������� ������ �����������
     * ������� ����� Null.
     * ����� � ��������� ����������� ������� ��������, ������������� ��� ����� �������. � ���� ������ ��������� ����
     * ������ ���� null.
     */
    public void testNearestEventTime_Null(){
      AutomatState state = new AutomatState(null,"1",1);
      ModelTime time = null;
      boolean f = false;
      try {
        time = state.GetNearestEventTime( null );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertEquals( time, null );
      AutomatTransitionByValue trans = new AutomatTransitionByValue(state,"2",2);
      f = false;
      try {
        state.AddElement( trans );
        time = state.GetNearestEventTime( null );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertEquals( time, null );
    }

  /** ����������� ������������ ������ ������� GetNearestEventTime.
     *  �������� ������������ � ���������� ��������, ������������� �������� ������� ���������� �������
     */
    public void testNearestEventTime(){
      AutomatState state = new AutomatState(null,"1",1);
      AutomatTransitionTimeout trans1 = new AutomatTransitionTimeout(state,"2",2);
      AutomatTransitionTimeout trans2 = new AutomatTransitionTimeout(state,"3",3);
      Variable var1 = new Variable(0.5);
      var1.SetName("var1");
      Variable var2 = new Variable(1);
      var2.SetName("var2");
      ScriptLanguageExt ext = new ScriptLanguageExt();
      boolean f = false;
      try {
        ext.AddVariable(var1);
        ext.AddVariable(var2);
        state.AddElement( trans1 );
        state.AddElement( trans2 );
        state.SetlanguageExt( ext );
        trans1.SetTimeoutVariable("var1");
        trans2.SetTimeoutVariable("var2");
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      ModelTime time = new ModelTime(10);
      ModelTime nearestEventTime = null;
      f = false;
      try {
        nearestEventTime = state.GetNearestEventTime( time );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertTrue( nearestEventTime != null );
      f = false;
      try {
        assertEquals( Double.compare( nearestEventTime.GetValue(),var1.GetFloatValue() ), 0 );
        f = true;
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue(f);
    }

  public void testReadStateChart(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel(ModelBlockTest.FPathToXMLFiles + "stateChart1.xml");
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    }
    assertTrue(f); // ������ ����� � ��� ������ ������ �������
    ModelSimpleBlock block = (ModelSimpleBlock) model.Get("����1");
    assertTrue(block != null);
    AutomatState state1 = block.GetAutomatState(0);
    assertTrue( state1 != null );
    AutomatState childState1 = state1.GetState(0);
    assertTrue( childState1 != null );
    AutomatTransition trans = state1.GetTransition(0);
    assertTrue( trans == null );
    trans = childState1.GetTransition(0);
    assertTrue( trans != null );
  }

  /**����������� ������������ ������� ����� � �������� ����������. ������� ��� �������� ����, ����� �������� ���
  * ��� ���������. ���������� ���� ��������� ����������, ������ ���� �� ������
  *
  */
  public void testReadStateChart_2(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel(ModelBlockTest.FPathToXMLFiles + "stateChart2.xml");
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    ModelSimpleBlock block = (ModelSimpleBlock) model.Get("����1");
    assertTrue(block != null);
    AutomatState state1 = block.GetAutomatState(0);
    assertTrue( state1 != null );
   f = false;
   try {
     state1.ApplyNodeInformation();
     f = true;
   } catch (ModelException e) {
     e.printStackTrace();
   } catch (ScriptException e) {
     e.printStackTrace();
   }
   f = false;
   try {
     state1.SetActive(null);
     f = true;
   } catch (ScriptException e) {
     e.printStackTrace();
   } catch (ModelException e) {
     e.printStackTrace();
   }
   assertTrue(f);
 }

   /**������� ���������� ������ ��������� ����� � ������ "����1"
   *
   * @return
   * @param fileName
   */
  private static AutomatState GetRootState( String fileName ) throws IOException, ModelException, SAXException {
    Model model = ModelBlockTest.ReadModel( fileName );
    ModelSimpleBlock block = (ModelSimpleBlock) model.Get("����1");
    block.InitStatechart();
    AutomatState state1 = block.GetAutomatState(0);
//    model.ApplyNodeInformation();
    return state1;
  }

  /**����������� ���������� init-���� ��� ��������� ����������, ���������� �� �����
    *
    */
   public void testStateInitCode(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
     AutomatState state = null;
     boolean f = false;
     try {
       state = GetRootState( ModelBlockTest.FPathToXMLFiles + "stateChart2.xml" );
       //state.SetActive( null );
       f = true;
     } catch (IOException e) {
       e.printStackTrace();
     } catch (ModelException e) {
       e.printStackTrace();
     } catch (SAXException e) {
       e.printStackTrace();
     }
     assertTrue( f );
     ScriptLanguageExt ext = state.FLanguageExt;
     assertTrue( ext != null );
     Variable var1 = ext.Get( "var1" );
     assertTrue( var1 != null );
     int i = 0;
     try {
       i = var1.GetIntValue();
     } catch (ScriptException e) {
       e.printStackTrace();
     }
     assertEquals(i , 11);
   }

  private static void ClearSystem(){
    ParserFactory.ClearParserList();
  }

  /** ����������� ������������ ���������� �������� �������� � ������������ �� ����������. ������� ������� �� ����������
   * var4. ��� ����������� �� �������: var4 := var3 > 3;
   */
  public void testReadTransitions(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    AutomatState state = null;
    boolean f = false;
    try {
      state = GetRootState( ModelBlockTest.FPathToXMLFiles + "stateChart3.xml" );
      f = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ScriptLanguageExt ext = state.FLanguageExt;
    Variable var3 = ext.Get("var3");
    assertTrue( var3 != null );
    var3.SetValue(0);
    f = false;
    try {
      //�������� ������ ��� ����������� ������ GlobalParams.ExecTimeOutputEnabled � GlobalParams.StateNameOutputEnabled
      state.SetTime(null);
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    AutomatState activeState = state.GetActiveState();
    assertTrue( activeState != null );
    assertTrue( "begin".equalsIgnoreCase( activeState.GetName() ) );
    //�������� ��������� ����������, �� ������� ������� �������
    Variable var4 =ext.Get("var4");
    var4.SetValue(true);
    f = false;
    try {
      state.SetTime(null);
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    activeState = state.GetActiveState();
    assertTrue( activeState != null );
    assertTrue( "state2".equalsIgnoreCase( activeState.GetName() ) );
    ClearSystem();
  }

  /**�������� ����������������� ���������� ������ ���������.
   *
   */
  public void testReadTransitions_2(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    AutomatState state = null;
    boolean f = false;
    try {
      state = GetRootState( ModelBlockTest.FPathToXMLFiles + "stateChart4.xml" );
      state.SetTime(null);
      f = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    AutomatState state1 = state.GetActiveState();
    assertTrue( state1 != null );
    assertTrue( "state3".equalsIgnoreCase( state1.GetName() ) );
    ClearSystem();
  }

  /** ����������� ������������ ������ ������ ModelSimpleBlock.Execute. ��� ���� � ����� ������������ ����������.
     */
    public void testExecutingWithStatechrt(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      boolean f = false;
      try {
        model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "stateChart5.xml");
        f = true;
      } catch (ModelException e) {
        System.out.println( e.getMessage() );
      } catch (IOException e) {
        System.out.println( e.getMessage() );
      } catch (SAXException e) {
        System.out.println( e.getMessage() );
      }
      assertTrue(f);
      ModelSimpleBlock block = (ModelSimpleBlock) model.Get("����1");
      ModelTime time = new ModelTime(0);
      f = false;
      Variable var = ((ModelBlockParam)block.GetElements().Get("var1")).GetVariable();
      int var1 = 0;
      try {
        block.Execute(time);
        var1 = var.GetIntValue();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertEquals(var1,11);
      f = false;
      time.Add( 0.4 );
      try {
        block.Execute(time);
        var1 = var.GetIntValue();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertEquals(var1,11);
      f = false;
      time.Add( 0.11 );
      try {
        block.Execute(time);
        var1 = var.GetIntValue();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertEquals(var1,12);
    }

  /**����������� ������������ ����������� ������� ���������� ������� ��� ������, ��������� �� ������ �����.
     *
     */
    public void testGetModelNearestExecTime(){
      mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      boolean f = false;
      try {
        model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "stateChart6.xml");
        f = true;
      } catch (ModelException e) {
        System.out.println( e.getMessage() );
      } catch (IOException e) {
        System.out.println( e.getMessage() );
      } catch (SAXException e) {
        System.out.println( e.getMessage() );
      }
      assertTrue(f);
      f = false;
      ModelTime time = null;
      try {
        time = model.GetNearestEventTime( new ModelTime(0) );
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      }
      assertTrue(f);
      assertTrue( time != null );
      assertEquals( Double.compare( 0.5, time.GetValue() ),0 );
    }

  /** ����������� ������������ ���������� ������ ���������� � �������� ���������. ��� ����� �������� ���������, �
     * ������� �� ������� ������������� �������� ���������� ���������. � � ������� ��������� �������� �������, �������
     * ����������� �������� ���������, ����������� � ����������. �������� ����� ���������� ������ ���������� ����������.
     * ������������ ������������ ������ �� �������������, ��� ��������� ����������� ����� ����������� �������� �
     * ����������
     */
    public void testWorkingStatechartWithParam(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      Variable par2 = null;
      Variable par1 = null;
      boolean f = false;
      try {
        model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "stateChart11.xml" );
        ModelBlock block = (ModelBlock) model.Get("����1");
        par2 = ((ModelCalculatedElement)block.Get("par2")).GetVariable();
        par1 = ((ModelCalculatedElement)block.Get("par1")).GetVariable();
        f = true;
      } catch (ModelException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (SAXException e) {
        e.printStackTrace();
      }
      assertTrue( f );
      assertTrue( par2 != null );
      assertTrue( par1 != null );
      int iPar2 = 0;
      int iPar1 = 0;
      int i = 0;
      while ( i < 5){
        try {
          model.Execute();
          iPar2 = par2.GetIntValue();
          iPar1 = par1.GetIntValue();
        } catch (ScriptException e) {
          e.printStackTrace();
        } catch (ModelException e) {
          e.printStackTrace();
        }
        i++;
      }
      assertEquals( iPar2+2, iPar1 );
    }

  /**�������� ������������ ����������� ������� ���������� ������� ��� ������ � ����������� �������
   */
  public void testNearestTimeMultipleBlock(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "stateChart7.xml");
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    f = false;
    ModelTime time = null;
    try {
      time = model.GetNearestEventTime( new ModelTime(0) );
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    assertTrue( time != null );
    assertEquals( time.Compare( new ModelTime(0.2) ), ModelTime.TIME_COMPARE_EQUALS );

  }

  /** �������� ������������ ���������� ���������� ���� ������ �� XML-�����. ����� ����������� ���������� ���� ����� ��
     * ������ ������ (���������� ���� ������ �� �������� ����)
     */
    public void testReadModelStep(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      boolean f = false;
      try {
        model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "stateChart8.xml");
        f = true;
      } catch (ModelException e) {
        System.out.println( e.getMessage() );
      } catch (IOException e) {
        System.out.println( e.getMessage() );
      } catch (SAXException e) {
        System.out.println( e.getMessage() );
      }
      assertTrue(f);
      ModelTime modelStep = model.GetModelStep();
      assertTrue( modelStep != null );
      assertEquals( modelStep.Compare(new ModelTime(0.5)), ModelTime.TIME_COMPARE_EQUALS );
      ModelTime modelTime = model.GetCurrentTime();
      assertTrue( modelTime != null );
      assertEquals( modelTime.Compare(new ModelTime(0)), ModelTime.TIME_COMPARE_EQUALS );
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
      assertEquals( modelTime.Compare(new ModelTime(1.0)), ModelTime.TIME_COMPARE_EQUALS );
    }

  /** ����������� ������� ������ �������� ���������� �������. ������� � ����� ��������� � ����������,
   * ������� ��� ��� ������.
   */
  public void testModelStep(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "stateChart9.xml");
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f);
    ModelTime time;
    time = model.GetCurrentTime();
    assertEquals( time.Compare( new ModelTime(0.6) ), ModelTime.TIME_COMPARE_EQUALS );
    f = false;
    try {
      model.Execute();
      f = true;
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    assertTrue( f);
    time = model.GetCurrentTime();
    assertEquals( time.Compare( new ModelTime(0.9) ), ModelTime.TIME_COMPARE_EQUALS );
  }

  /**����������� ������������ �������� ���������� �������.
   * ��� ������ ������, ��� �������� ������������� ������� � �����. � ���� ������ ��������� ���� ������ ������������.
   */
  public void testModelStep_2(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "stateChart10.xml");
      model.Execute();
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    assertTrue(f);
    ModelTime time = model.GetCurrentTime();
    assertEquals( time.Compare( new ModelTime(1.4) ), ModelTime.TIME_COMPARE_EQUALS );
    try {
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    time = model.GetCurrentTime();
    assertEquals( time.Compare( new ModelTime(2.1) ), ModelTime.TIME_COMPARE_EQUALS );
    try {
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    time = model.GetCurrentTime();
    assertEquals( time.Compare( new ModelTime(2.8) ), ModelTime.TIME_COMPARE_EQUALS );
  }

  /**����������� ������������ ������������ ���������� ���������, ����������� � ����� �����.
     *
     */
    public void testTransitionOrder(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
      Model model = null;
      boolean f = false;
      try {
        model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "stateChart12.xml");
        f = true;
      } catch (ModelException e) {
        System.out.println( e.getMessage() );
      } catch (IOException e) {
        System.out.println( e.getMessage() );
      } catch (SAXException e) {
        System.out.println( e.getMessage() );
      }
      assertTrue(f);
      ModelBlock block = (ModelBlock) model.Get( "block" );
      assertTrue( block != null);

      f = false;
      try {
        model.Execute();
        assertEquals( block.GetIntValue( "x" ), 1 );
        assertEquals( block.GetIntValue( "y" ), 0 );
        model.Execute();
        assertEquals( block.GetIntValue( "x" ), 2 );
        assertEquals( block.GetIntValue( "y" ), 0 );
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
      assertEquals( block.GetIntValue( "x" ), 5 );
      assertEquals( block.GetIntValue( "y" ), 1 );

      f = false;
      try {
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
      assertEquals( block.GetIntValue( "x" ), 10 );
      assertEquals( block.GetIntValue( "y" ), 0 );
    }

  /** ����������� ������������ ������������ ����, ������� ������ ����������� ����� ������� �� ��������� (BeforeOut)
   * � ����, ������� ������ ����������� ����� ����� � ��������� (AfterIn)
   */
  public void testBeforeOutCode(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    Model model = null;
    boolean f = false;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "file27.xml");
      f = true;
    } catch (ModelException e) {
      System.out.println( e.getMessage() );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
    } catch (SAXException e) {
      System.out.println( e.getMessage() );
    }
    assertTrue(f);
    ModelBlock block = (ModelBlock) model.Get( "block" );
    assertEquals( block.GetIntValue("beforeOutCounter"), 0 );
    assertEquals( block.GetIntValue("afterInCounter"), 0 );
    try {
      model.Execute();
      model.Execute();
      model.Execute();
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (ModelException e) {
      e.printStackTrace();
    }
    //System.out.println( "counter = " + Integer.toString( block.GetIntValue("counter") ) );
    //System.out.println( "counter2 = " + Integer.toString( block.GetIntValue("counter2") ) );
    //System.out.println( "counter3 = " + Integer.toString( block.GetIntValue("counter3") ) );
    assertEquals( block.GetIntValue("transitionCounter"), 5 );
    assertEquals( block.GetIntValue("afterInCounter"), 17 );
    assertEquals( block.GetIntValue("beforeOutCounter"), 12 );
  }

  public void testMultipleTransitions(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
      boolean f = false;
      Model model = null;
      try {
        model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "file36.xml");
        model.RegisterModelInContext();
        f = true;
      } catch (ModelException e) {
        //e.printStackTrace();
      } catch (IOException e) {
        //e.printStackTrace();
      } catch (SAXException e) {
        //e.printStackTrace();
      }
      assertTrue( f );
      ModelBlock block1 = (ModelBlock) model.Get("block1");
      ModelBlock block2 = (ModelBlock) model.Get("block2");
      assertTrue( block1 != null );
      assertTrue( block2 != null );
      f = true;
      while ( block1.GetIntValue( "var1" )  < 10){
        try {
          model.Execute();
          //System.out.println( "var1 = " + Integer.toString( block1.GetIntValue( "var1" ) ) + " var2 = " + block2.GetIntValue( "var2" ) );
        } catch (ScriptException e) {
          f = false;
          break;
        } catch (ModelException e) {
          f = false;
          break;
        }
      }
      assertTrue( f );
      assertEquals( block1.GetIntValue( "var1" ) , 10 );
      assertEquals( block2.GetIntValue( "var2" ) , 2 );
    }

  /**����������� ������������ �������������� ������������ ���������� ���������. ����������� ����������
   * ��������. ����� � ��������� ������ ���������.  ����������� �������� �� ��������� (� �� �� ��������)
   *
   */
  public void testOneMomentMultipleTransition(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "statechart13.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("����1");
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
    assertEquals( block.GetIntValue("par1"), 20 );
    assertEquals( block.GetIntValue("par2"), 25 );
  }

  /**����������� ������������� ������������ ���������� ���������. ������ ���� �� ��� - ������� �� �������,
   * ������ - �� ��������.
   *
   */
  public void testOneMomentMultipleTransition_2(){
    mp.parser.ModelExecutionContext.ClearExecutionContext();
    boolean f = false;
    Model model = null;
    try {
      model = ModelBlockTest.ReadModel( ModelBlockTest.FPathToXMLFiles + "statechart14.xml");
      f = true;
    } catch (ModelException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    assertTrue( f );
    ModelBlock block = (ModelBlock) model.Get("����1");
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
    assertEquals( block.GetIntValue("par1"), 20 );
    assertEquals( block.GetIntValue("par2"), 25 );

  }


}

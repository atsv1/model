package mp.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mp.parser.*;

/**
 * User: atsv
 * Date: 18.09.2006
 */
public class ModelSimpleBlock extends ModelBlock {
  private boolean FPreparedFlag = false;
  private int FLastExecutionTime = -1;
  private ScriptLanguageExt FLanguageExt = null;

  private long FExecDuration = 0;
  private long FExecCount = 0;

  public ModelSimpleBlock(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }


  public void SetLanguageExt(ScriptLanguageExt aLanguageExt) {
    SetLanguageExtToInnerParam( aLanguageExt );
    SetLanguageExtToOutParam( aLanguageExt );
    SetLanguageExtToStatecharts( aLanguageExt );
    SetLanguageExtToEventContainer( aLanguageExt );
    FLanguageExt = aLanguageExt;
  }

  public ScriptLanguageExt GetLanguageExt(){
    return FLanguageExt;
  }

  private void PrepareBlock() throws ModelException{
    PrepareParamsOrder();
    FPreparedFlag = true;
  }

  public void Execute() throws ModelException, ScriptException {
    if ( !FPreparedFlag ){
      PrepareBlock();
    }
    ModelBlockParam param = null;
    int i = 0;
    while ( i < FOrderParamsList.size() ){
      param = (ModelBlockParam) FOrderParamsList.get( i );
      param.Update();
      i++;
    }
  }

  private void ExecuteParamsUpdate(ModelTime aCurrentTime) throws ModelException, ScriptException{
    if ( !FPreparedFlag ){
      PrepareBlock();
    }
    UpdateAllParams( aCurrentTime );
  }

  /**���������� ���������� �������� ���� ����������. ���������� ������������ ������ � ��� ������, ���� ���������
   * ����� ��������� ��������� ����� ��������.
   * ����� ���������� ������ �����, ����� � ����� ��� �� ������ ����������
   * @param aCurrentTime
   * @throws ModelException
   * @throws ScriptException
   */
  private void ExecuteInTime(ModelTime aCurrentTime) throws ModelException, ScriptException{
    if ( aCurrentTime == null ){
      ModelException e = new ModelException("� ������� \"" + GetFullName() + "\" �������� ������ ������ �� ��������� �����");
      throw e;
    }
    if ( GlobalParams.ExecTimeOutputEnabled() ){
      System.out.println( "������ ���������� ���������� � ����� " + this.GetFullName() + ". ����� ������ = " +
              Double.toString(aCurrentTime.GetValue()) + " ��������� ����� ���������� ����� = " + Integer.toString(FLastExecutionTime) );
    }
    int currentTimeValue = aCurrentTime.GetIntValue();    
    if ( currentTimeValue > FLastExecutionTime){      
    	// ������� ����������, ����� ��������� ����������� ������ ���� ��������� ����� ������������ ��������� ����� ��������
    	ExecuteParamsUpdate( aCurrentTime );  
      FLastExecutionTime = currentTimeValue;
    } else {
      if ( GlobalParams.ExecTimeOutputEnabled() ){
        System.out.println( "������ �� ����������� " + this.GetFullName() + " ��������� ����� ���������� ����� = " + Integer.toString(FLastExecutionTime)  );
      }
    }
  }


  /** ����� ������������ �������� ���������� ������� � ���������� ������� �����. ���� ���������� ���, �� ����������
   * ����� Execute() - ������� ��� ����������.
   * ����� ��������� ��������� �������:
   * 1. ���������� ��������� ����� � ����������
   * 2. ��������������� �������� ��� ��� ���������� �����, ������� ��������� � ���������.
   * ������� ������ ������ Execute(), ���� � ����� ����������� ���������:
   * ����� ���������� ������ �����, ����� �������� ���������� ������� ������������ ����� ��������� ������ ��������.
   * @param aCurrentTime - �������� �������� ���������� �������
   * @throws ModelException
   * @throws ScriptException
   */
  public void Execute(ModelTime aCurrentTime) throws ModelException, ScriptException {
    long startTime = System.nanoTime();
    ExecuteEvents();
    int size = FRootStates.size();
    if ( size == 0 ){
      //�� ������ ���������� ���.
      ExecuteInTime( aCurrentTime );
      AddSelfToTimeManager( aCurrentTime );
      return;
    }
    //���������� ����
    int i = 0;
    AutomatState state = null;
    while ( i < size ){
      state = (AutomatState) FRootStates.get( i );
      state.SetTime( aCurrentTime );
      i++;
    }
    //UpdateParamsWithChangedInput();
    //Execute();
    ExecuteInTime( aCurrentTime );
    AddSelfToTimeManager( aCurrentTime );
    if ( GlobalParams.ExecTimeOutputEnabled() ){
      System.out.println("�������� ���� " + GetFullName() + " � �������� �������. ������� ����� " + aCurrentTime.toString()); 
    }
    long duration = System.nanoTime() - startTime;
    if ( duration < 0 ) duration = 0;
    FExecDuration = FExecDuration + duration;
    FExecCount++;
  }

  public void PrintExecutionTime() {
    int selfIndex = GetIntValue("selfIndex");
    double ms = FExecDuration / 1000000;
    System.out.println( "mux " + this.GetFullName() +"[" + Integer.toString(selfIndex) + "]" +  " count = " +
       Long.toString( FExecCount ) + " duration = " + Double.toString( ms ) + ".ms");
  }

  public boolean IsDynamicParamCreate() {
    return false;  
  }

  public void ApplyNodeInformation() throws ModelException, ScriptException{
    //�������� ����� ApplyNodeInformation() ��� ���� ���������� ����� (����������, ������� � ��������).
    //����� ������ ���������� �������� � ���������� ������ ModelElement
    ApplyAllElementsNodeInformation();
    PrepareBlock();
    // �������� ����� ApplyNodeInformation() ��� ����������� �����
    ApplyStatechartNodeInfo();
    //������ ���������� �� ������������ �������
    ApplyEventNodeInfo();
  }
  
  private  Map<UUID, Integer> fixedStates = new HashMap<UUID, Integer> ();
  public void fixState(UUID stateLabel) throws ModelException{
  	super.fixState(stateLabel);  	  	
  	ModelBlockParam forkModeParam = super.GetInnerParam("isForkMode");
  	forkModeParam.GetVariable().SetValue(true);
  	fixedStates.put(stateLabel, new Integer(FLastExecutionTime));
  }
  
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	super.rollbackTo(stateLabel);
  	FLastExecutionTime = fixedStates.get(stateLabel);
  	fixedStates.remove(stateLabel);
  	if ( fixedStates.isEmpty() ) {
  		ModelBlockParam forkModeParam = super.GetInnerParam("isForkMode");
    	forkModeParam.GetVariable().SetValue(false);  		
  	}
  	
  }

}

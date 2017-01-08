package mp.elements;

import mp.parser.ScriptException;
import mp.parser.Variable;
import mp.parser.Operand;

/**
 * User: �������������
 * Date: 26.06.2008
 */


public class FunctionExecutorSumm extends MultiBlockEnableExecutor {
  private ModelBlockParam FValueParam = null;
  private String FValueParamName = null;
  private boolean FIsNotPrepared = true;
  //���� ���������� ��� ������������� ��������. ���� ����� true, �� ��� ��������, ��� �������� �������������� �
  // ����� �����������. ���� false - �� ������ - ������� ��������������� �� ������� �����
  private boolean FIsCalculatedValue = false;
  private MultiBlockExecutor FCalculatedValueContainer = null;
  private int FResultType;
  private Variable FResultVariable;
  
  public FunctionExecutorSumm(ModelDynamicBlock aOwner, ModelBlockParam aResultParam, String aScriptSource) {
    super(aOwner, aResultParam, aScriptSource);
  }

  /** ������������� �������������� �������� � ��������� ���������
   *
   */
  private void SetResultParamToInitState() throws ModelException {
    FResultParam.LoadInitValue();
  }

  private void Prepare() throws ModelException {
    SetEnableArray();
    if ( FValueParamName == null || "".equalsIgnoreCase( FValueParamName ) ){
      ModelException e = new ModelException("����������� �������� ���������� � ����������� �������");
      throw e;
    }
    ModelBlock owner = this.GetOwner();
    //�������� ��������, � ������� ������ ����������� �������� ��� ������������
    FValueParam = (ModelBlockParam) owner.Get( FValueParamName );
    if ( FValueParam == null ){
      ModelException e = new ModelException("����������� �������� ����������� ������� � ������ \"" + FValueParamName + "\"");
      throw e;
    }
    int placementType = FValueParam.GetParamPlacementType();
    // ����������, ������ ����� ����� �������� �������� ��� ������������
    if ( placementType ==  ModelBlockParam.PLACEMENT_TYPE_INNER){
      // ����� �������� �������� �� ����������� ��������
      FIsCalculatedValue = true;
      if ( owner instanceof ModelAggregator ){
        FCalculatedValueContainer = ((ModelAggregator)owner).GetValueExecutorByResultName( FValueParamName );
      } else {
        ModelException e = new ModelException("������ � ����������: �������� ��������� �� ����������, ���������� ��� " +
           "�������� ������������ ��������" );
        throw e;
      }
      if (FCalculatedValueContainer == null){
        ModelException e = new ModelException("������ � ���������: ����������� ���������� �������� \"" + FValueParamName + "\"");
        throw e;
      }
    } else {
      // ����� �������� �������� ��������������� �� �����
      FIsCalculatedValue = false;
    }
    FResultType = FResultParam.GetVariable().GetType();
    FResultVariable = FResultParam.GetVariable();
  }

  private Variable GetVariable( int aVarPosition ) throws ModelException {
    if ( FIsCalculatedValue ){
      return FCalculatedValueContainer.GetResult( aVarPosition ); 
    } else {
      ResourceRecord rec = GetResource( aVarPosition );
      if ( rec == null ){
        return null;
      }
      ModelBlock block = rec.GetBlock();
      ModelBlockParam param = (ModelBlockParam) block.Get( FValueParamName );
      return param.GetVariable();
    }
  }

  private void AddVar( Variable aVarToAdd ) throws ModelException, ScriptException {
    if ( aVarToAdd == null ){
      ModelException e = new ModelException("������ �������� ��� �������� ��������");
      throw e;
    }
    switch ( FResultType ){
      case Operand.OPERAND_TYPE_INTEGER:{
        FResultVariable.SetValue( FResultVariable.GetIntValue() + aVarToAdd.GetIntValue() );
        return;
      }
      case Operand.OPERAND_TYPE_REAL:{
        FResultVariable.SetValue( FResultVariable.GetFloatValue() + aVarToAdd.GetFloatValue() );
        return;
      }
      default:{
        ModelException e = new ModelException("������������ ��� ���������� ��� ������� ������������");
        throw e;
      }
    }
  }

  public void ExecuteScript() throws ScriptException, ModelException {
    long start = System.nanoTime();
    FEnterCount++;
    SetResultParamToInitState();
    if ( FIsNotPrepared ){
      Prepare();
      FIsNotPrepared = false;
    }
    int resourceBlockCount = GetResourceCount();
    int i = 0;
    Variable varToAdd = null;
    while ( i < resourceBlockCount ){
      if ( GetEnableFlag( i ) ){
        varToAdd = GetVariable( i );
        AddVar( varToAdd );
      }
      i++;
    }//while

    FRealDuration = FRealDuration + System.nanoTime() - start;
  }

  public void SetParameterName( String aName ){
    FValueParamName = aName;
  }

}

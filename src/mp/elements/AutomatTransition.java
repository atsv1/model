package mp.elements;

import mp.parser.*;
import mp.utils.ModelAttributeReader;

import java.util.UUID;


/** ����� �������� �� ���������� ��������� ����� ����������� ����������: ��������� ����������� ��������, ����������
 * ���� ��������.
 */
public abstract class AutomatTransition extends ModelEventGenerator{
  private ScriptParser FParser = null;
  protected ScriptLanguageExt FLanguageExt = null;
  private String FNextStateName = null;  
  protected Variable FTransitionVar = null;
  private int FTransitionCount = 0; //���������� ������������ � �������� �����. ��� ������ ������ ��� �� �����
  private int FPriority = 0;
  protected ExecutionContext FExecutionContext = null;

  public AutomatTransition(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);      
    FExecutionContext = new ExecutionContext(  this.GetFullName() );
  }

  protected void SetCode(String aCode) throws ModelException, ScriptException{
    if ( FLanguageExt == null ){
      ModelException e = new ModelException("������ ������ ���������� � �����������");
      throw e;
    }
    if ( FParser == null ){
      //FParser = new PascalParser();
      FParser = ParserFactory.GetParser( FLanguageExt, aCode );
    }
    //FParser.SetLanguageExt( FLanguageExt );
    //FParser.ParseScript( aCode );
  }

  public void SetlanguageExt( ScriptLanguageExt aLanguageExt ){
    FLanguageExt = aLanguageExt;
  }

  /** ������� �������� ����������� ������������� ��������.
   *
   * @param aCurrentTime - �������� �������� ���������� �������
   * @return ���������� true, ���� ������� ��������, false - ���� ����������
   */
  public abstract boolean IsTransitionEnabled( ModelTime aCurrentTime ) throws ModelException;

  public abstract ModelTime GetTransitionTime() throws ModelException;

  public String GetNextStateName(){
    return FNextStateName;
  }

  public void ExecuteTransitionCode( ModelTime aCurrentTime ) throws ModelException {
    if ( GlobalParams.ExecTimeOutputEnabled() && aCurrentTime != null ){
      System.out.println("���������� ���� � �������� " + this.GetFullName() + ". ����� ������ = " +
              Double.toString(aCurrentTime.GetValue()));
    }
    if ( FParser != null ){
      try{
      	//FExecutionContext
      	if (FExecutionContext == null) {
      		FExecutionContext = new ExecutionContext( this.GetFullName() );
      	}
      	FParser.AddExecutionContext(FExecutionContext);
        FParser.ExecuteScript();
      } catch (ScriptException e){
      	e.printStackTrace();
        ModelException e1 = new ModelException("������ � �������� \"" + this.GetFullName() + "\": " + e.getMessage());
        throw e1;
      }
      FTransitionCount++;
      //System.out.println( this.GetFullName() + " exec transitioncode. Count=" + Integer.toString( FTransitionCount ) );
    }
  }

  public void SetNextStateName( String aStateName ){
    FNextStateName = aStateName;
  }

  protected Variable GetVarByName( String aVarName ) throws ModelException{
    if ( aVarName == null || "".equalsIgnoreCase( aVarName ) ){
      ModelException e = new ModelException("������ ��� ����������-������� ��������");
      throw e;
    }
    Variable result = null;
    result = FLanguageExt.Get( aVarName );
    if ( result == null ){
      ModelException e = new ModelException("����������� ��� ���������� " + aVarName);
      throw e;
    }
    return result;
  }

  public ModelTime GetNearestEventTime(ModelTime aCurrentTime) throws ModelException {
    return GetTransitionTime();
  }

  protected void ReadNextState() throws ModelException{    
    this.SetNextStateName( this.GetDataSource().GetNextStateName() );
  }

  protected abstract boolean IsValue(String aTransValue);

  protected void ReadValueInfo() throws ModelException {
  	ModelElementDataSource ds = this.GetDataSource();    
    String transValue = ds.GetTransitionValue();
    String ownerName;    
    FPriority = ds.GetTransitionPriority();
    ModelElement owner = this.GetOwner();
      if ( owner != null ){
        ownerName = owner.GetName();
      } else ownerName = "";
    if ( transValue == null || "".equalsIgnoreCase( transValue ) ){
      ModelException e = new ModelException("������� \"" + ownerName + "." + this.GetName() + "\". ����������� �������� ���������� �������� ");
      throw e;
    }
    if ( IsValue(transValue) ){
      return;
    }
    //�������� � �������� "value" �� ������� ������������� � ��������. �������� ����� ���������� � ����� ��������
    if ( FLanguageExt == null ){
      ModelException e = new ModelException("������� \"" + ownerName + "." + this.GetName() + "\". ����������� ������ ���������� ");
      throw e;
    }
    FTransitionVar = FLanguageExt.Get( transValue );
    if ( FTransitionVar == null ){
      ModelException e = new ModelException("������� \"" + ownerName + "." + this.GetName() + "\". ����������� ���������� \"" + transValue + "\"");
      throw e;
    }
  }

  protected void ReadTransitionCode() throws ScriptException{
    
    String code = this.GetDataSource().GetexecutionCode();
    if ( code == null ){
      return;
    }
    if ( FParser == null ){
      FParser = ParserFactory.GetParser( FLanguageExt, code );
    }
  }

  public int GetPriority(){
    return FPriority;
  }
  
  public abstract void fixState(UUID stateLabel) throws ModelException;
  
  public abstract void rollbackTo(UUID stateLabel) throws ModelException;
  

}

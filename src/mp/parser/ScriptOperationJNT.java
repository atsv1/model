package mp.parser;


/**
 * User: atsv
 * Date: 04.09.2006
 * ����� JNT - JumpNotTrue - ������������ �������, ���� ������� (������ �������)
 * ������� ����� false
 */
public class ScriptOperationJNT extends ScriptOperationAbstrJMP {
  private Operand FOperand = null;


  public int ExecOperation(int aProgramPointer) throws ScriptException {
    FOperand = InitOperand( aProgramPointer + 1 );
    FAddress = InitOperand( aProgramPointer + 2 );
    IsAddressValid();
    if ( !FOperand.GetTypeName().equalsIgnoreCase("boolean") )
    {
      ScriptException e = new ScriptException("������� �������� �� ����������� ����");
      throw e;
    }
    if ( FOperand.GetBooleanValue() )
    {
      return 2;
    } else
    return GetExecResult( aProgramPointer );
  }
  
  @Override
  public String toString(){
  	return "JumpNotTrue to " + (FAddress != null ? FAddress.toString() : " no addr" ) + " condition " + FOperand;
  }
  
}

package mp.parser;


/**
 * User: atsv
 * Date: 04.09.2006
 */
public abstract class ScriptOperationAbstrJMP extends ScriptOperation {
  protected Operand FAddress = null;

  /**������� ���������� �������� �� ��������� ������������ ������ �������,
   * ��������� ���������, ������� ��������� ����������� ������ � ���������,
   * �������� �� ��������� ExecOperation() �������� ������������ ��������
   * ��������� (ProgramPointer)
   * @param aProgramPointer - ��������� �� ������� �������.
   * @return - ��������.
   * @throws ScriptException - ���������� ������������ �����, ����� ����������
   * ��������� ������� �� ������� ���������
   */
  protected int GetExecResult(int aProgramPointer) throws ScriptException {
    return FAddress.GetIntValue() - aProgramPointer;
  }

  protected void IsAddressValid() throws ScriptException  {
    ScriptException e;
    if ( FAddress == null ) {
      e = new ScriptException("�� ��������������� ����� ��������");
      throw e;
    }
    if ( !FAddress.GetTypeName().equalsIgnoreCase("integer") ) {
      e = new ScriptException("����� �������� �� �������� ������");
      throw e;
    }
    if ( ( FAddress.GetIntValue() < 0 ) || (FAddress.GetIntValue() > Program.size()) )  {
      e = new ScriptException("����� ����� ������� �� ������� ���������");
      throw e;
    }
  }

 /**
  public int ExecOperation(int aProgramPointer) throws ScriptException {
    return 0;
  }
  */

  public String GetResultType() throws ScriptException {
   ScriptException e = new ScriptException("����������� ����� ��� ������ ���������");
   throw e;
  }

  public Variable GetResultVariable(int aProgramPointer) throws ScriptException {
    ScriptException e = new ScriptException("����������� ����� ��� ������ ���������");
    throw e;
  }
}

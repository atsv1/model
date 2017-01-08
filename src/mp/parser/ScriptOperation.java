package mp.parser;


/**
 * User: atsv
 * Date: 15.04.1995
 */


public abstract class ScriptOperation extends ScriptProgramObject{

  //public Vector Program;
  private int FLineNumber = 0;

  public ScriptOperation() {
    Program = null;
  }

  public int GetLineNumber() {
    return FLineNumber;
  }

  public void SetLineNumber(int aLineNumber)  {
    FLineNumber = aLineNumber;
  }

  /**
   *
   * @param aProgramPointer ���������� ����� ���������� ���������� ����� ����
   * ������� � ���������. ��������! - ������ ���� �������, � �� ��������� �� ���
   * @return
   * @throws ScriptException
   */
  public abstract int ExecOperation( int aProgramPointer ) throws ScriptException;

  /** ������� ��������� ������� �� ���������. ��������� ��������� � ������� Program
   *
   * @param aProgramPointer
   * @return
   * @throws ScriptException
   */
  protected Operand InitOperand(int aProgramPointer) throws ScriptException {
    ScriptException e;
    Operand aOperand = null;
    try {
    	if (aProgramPointer >= Program.size()) {
    		throw new ScriptException("����� �� ������� ���������");
    	}
    	Object obj = Program.get( aProgramPointer );
    	if ( !(obj instanceof Operand) ) {
    		throw new ScriptException("�� �������� ������� ��� �������");
    	}
      aOperand = (Operand) Program.get( aProgramPointer );
      if (aOperand == null )   {
        e = new ScriptException("�� �������� ������� ��� ������� ");
        throw e;
      }
    } catch (Exception e1) {
      e = new ScriptException("��� ��������� �������� ��� ������� �������� ������ " + e1.getMessage());
      throw e;
    }
    return aOperand;
  }

  public abstract String GetResultType() throws ScriptException;

  public abstract Variable GetResultVariable(int aProgramPointer) throws ScriptException;

}

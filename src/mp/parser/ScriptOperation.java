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
   * @param aProgramPointer Параметром нужно передавать порядковый номер этой
   * команды в программе. Внимание! - именно этой команды, а не следующей за ней
   * @return
   * @throws ScriptException
   */
  public abstract int ExecOperation( int aProgramPointer ) throws ScriptException;

  /** Функция извлекает операнд из программы. Программа находится в объекте Program
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
    		throw new ScriptException("Выход за пределы программы");
    	}
    	Object obj = Program.get( aProgramPointer );
    	if ( !(obj instanceof Operand) ) {
    		throw new ScriptException("Не получить операнд для команды");
    	}
      aOperand = (Operand) Program.get( aProgramPointer );
      if (aOperand == null )   {
        e = new ScriptException("Не получить операнд для команды ");
        throw e;
      }
    } catch (Exception e1) {
      e = new ScriptException("При получении операнда для команды возникла ошибка " + e1.getMessage());
      throw e;
    }
    return aOperand;
  }

  public abstract String GetResultType() throws ScriptException;

  public abstract Variable GetResultVariable(int aProgramPointer) throws ScriptException;

}

package mp.parser;


/**
 * User: atsv
 * Date: 04.09.2006
 */
public abstract class ScriptOperationAbstrJMP extends ScriptOperation {
  protected Operand FAddress = null;

  /**Функция возвращает смещение на основании фактического адреса команды,
   * поскольку процедура, которая управляет выполнением команд в программе,
   * получает от процедуры ExecOperation() смещение относительно текущего
   * указателя (ProgramPointer)
   * @param aProgramPointer - указатель на текущую команду.
   * @return - смещение.
   * @throws ScriptException - исключение генерируется тогда, когда полученный
   * указатель выходит за пределы программы
   */
  protected int GetExecResult(int aProgramPointer) throws ScriptException {
    return FAddress.GetIntValue() - aProgramPointer;
  }

  protected void IsAddressValid() throws ScriptException  {
    ScriptException e;
    if ( FAddress == null ) {
      e = new ScriptException("Не инициализирован адрес перехода");
      throw e;
    }
    if ( !FAddress.GetTypeName().equalsIgnoreCase("integer") ) {
      e = new ScriptException("Адрес перехода не является числом");
      throw e;
    }
    if ( ( FAddress.GetIntValue() < 0 ) || (FAddress.GetIntValue() > Program.size()) )  {
      e = new ScriptException("Новый адрес выходит за пределы программы");
      throw e;
    }
  }

 /**
  public int ExecOperation(int aProgramPointer) throws ScriptException {
    return 0;
  }
  */

  public String GetResultType() throws ScriptException {
   ScriptException e = new ScriptException("Невозможный вызов для класса переходов");
   throw e;
  }

  public Variable GetResultVariable(int aProgramPointer) throws ScriptException {
    ScriptException e = new ScriptException("Невозможный вызов для класса переходов");
    throw e;
  }
}

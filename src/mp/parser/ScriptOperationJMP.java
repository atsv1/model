
package mp.parser;
/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 04.09.2006
 */
public class ScriptOperationJMP extends ScriptOperationAbstrJMP {

  public int ExecOperation(int aProgramPointer) throws ScriptException {
    FAddress = InitOperand( aProgramPointer + 1 );
    IsAddressValid();
    return GetExecResult( aProgramPointer );
  }
}

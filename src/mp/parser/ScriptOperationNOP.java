package mp.parser;


/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 10.09.2006
 * Time: 21:34:56
 * To change this template use File | Settings | File Templates.
 */
public class ScriptOperationNOP extends ScriptOperation {
  public int ExecOperation(int aProgramPointer) throws ScriptException {
    return 0;
  }

  public String GetResultType() throws ScriptException {
    return null;
  }

  public Variable GetResultVariable(int aProgramPointer) throws ScriptException {
    return null;  
  }
}

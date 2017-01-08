
package mp.parser;

/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 21.08.2006
 * Time: 8:40:54
 * To change this template use File | Settings | File Templates.
 */
public class ScriptOperationEquals extends ScriptOperationBooleanResult {

  protected void CompareInt() throws ScriptException
  {
    boolean result;
    result = (FFirstOperand.GetIntValue() == FSecondOperand.GetIntValue());
    FResult.SetValue( result );
  }

  protected void CompareReal() throws ScriptException
  {
    boolean result;
    result = (FFirstOperand.GetFloatValue() == FSecondOperand.GetFloatValue());
    FResult.SetValue( result );
  }


  protected void CompareBoolean() throws ScriptException
  {
    boolean result;
    result = (FFirstOperand.GetBooleanValue() == FSecondOperand.GetBooleanValue());
    FResult.SetValue( result );
  }

}

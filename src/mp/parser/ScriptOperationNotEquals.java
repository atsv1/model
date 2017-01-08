package mp.parser;

/**
 * User: atsv
 * Date: 06.01.2007
 */
public class ScriptOperationNotEquals extends ScriptOperationBooleanResult {

  protected void CompareInt() throws ScriptException {
    boolean result;
    result = (FFirstOperand.GetIntValue() != FSecondOperand.GetIntValue());
    FResult.SetValue( result );
  }

  protected void CompareReal() throws ScriptException {
    boolean result;
    //result = (FFirstOperand.GetFloatValue() != FSecondOperand.GetFloatValue());
    result = (  FFirstOperand.Compare( FSecondOperand ) != 0 );
    FResult.SetValue( result );
  }

  protected void CompareBoolean() throws ScriptException {
  }
}

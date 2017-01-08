package mp.parser;


/**
 * User: atsv
 * Date: 12.09.2006
 */
public class ScriptOperationMore extends ScriptOperationBooleanResult {

  protected void CompareInt() throws ScriptException
  {
    boolean result;
    result = (FFirstOperand.GetIntValue() > FSecondOperand.GetIntValue());
    FResult.SetValue( result );
  }

  protected void CompareReal() throws ScriptException
  {
    boolean result;
    result = (FFirstOperand.GetFloatValue() > FSecondOperand.GetFloatValue());
    FResult.SetValue( result );
  }

  protected void CompareBoolean() throws ScriptException
  {
    boolean result = false;
    //result = (FFirstOperand.GetBooleanValue() > FSecondOperand.GetBooleanValue());
    if ( FFirstOperand.GetBooleanValue() == true && FSecondOperand.GetBooleanValue() == false )
    result = true;
    FResult.SetValue( result );
  }

}

package mp.parser;

/**
 * User: atsv
 * Date: 07.11.2006
 */
public class ScriptOperationGreaterOrEquals extends ScriptOperationBooleanResult{

  protected void CompareInt() throws ScriptException {
    boolean result;
    result = (FFirstOperand.GetIntValue() >= FSecondOperand.GetIntValue());
    FResult.SetValue( result );
  }

  protected void CompareReal() throws ScriptException {
    boolean result;
    int i = FFirstOperand.Compare( FSecondOperand );
    result = ( i == 1  || i == 0);
    FResult.SetValue( result );
  }

  protected void CompareBoolean() throws ScriptException {
    boolean result = false;
    if ( FFirstOperand.GetBooleanValue() ){
        result = true;
    }
    if ( !FFirstOperand.GetBooleanValue() && !FSecondOperand.GetBooleanValue()){
      result = true;
    }
    FResult.SetValue( result );
  }
}

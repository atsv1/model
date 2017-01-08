package mp.parser;


/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 13.05.2006
 * Time: 11:59:30
 * To change this template use File | Settings | File Templates.
 */
public class ScriptOperationDiv extends ScriptOperation3Operand
 {

  private float Div() throws ScriptException
  {
    return FFirstOperand.GetFloatValue() / FSecondOperand.GetFloatValue();
  }

  protected void IsOperationEnabled() throws ScriptException
  {
    if ( ( IsOperandNumeric(FFirstOperand) ) &&
         ( IsOperandNumeric(FSecondOperand) ) &&
         (  FResult.GetTypeName().equalsIgnoreCase("real"))
       )
    {

    } else
    {
      ScriptException e = new ScriptException("");
      throw e;
    }
  }

  public int ExecOperation(int aProgramPointer) throws ScriptException
  {
    FFirstOperand = InitOperand( aProgramPointer + 1 );
    FSecondOperand = InitOperand( aProgramPointer + 2 );
    FResult = (Variable)InitOperand( aProgramPointer + 3 );
    if ( FIsPreviousOperationEnabled )
    {
     IsOperationEnabled();
    }

    FResult.SetValue( Div() );
    return 3;
  }
}

package mp.parser;


/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 20.05.2006
 * Time: 14:52:21
 * To change this template use File | Settings | File Templates.
 */
public class ScriptOperationPow extends ScriptOperation3Operand {

  protected void IsOperationEnabled() throws ScriptException
  {
    if ( FResult.GetTypeName().equalsIgnoreCase("integer") )
    {
      if ( FFirstOperand.GetTypeName().equalsIgnoreCase("real") ||
           FSecondOperand.GetTypeName().equalsIgnoreCase("real")
         )
      {
        ScriptException e = new ScriptException("Ќевозможно выполнить возведение в степень");
        throw e;
      }
     return;
    }
    if ( FResult.GetTypeName().equalsIgnoreCase("real") )
    {

    }
    ScriptException e = new ScriptException("Ќевозможно выполнить возведение в степень");
    throw e;
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
    String s = FResult.GetTypeName();
    if ( s.equalsIgnoreCase("integer") )
    {
      FResult.SetValue( PowInt() );
    } else
    {
      FResult.SetValue( PowFloat() );
    }
    return 3;
  }

  private float PowFloat() throws ScriptException
  {
    return (int)Math.pow( (double)FFirstOperand.GetFloatValue(), (double)FSecondOperand.GetFloatValue());
  }

  private int PowInt() throws ScriptException
  {
    return (int)Math.pow( (double)FFirstOperand.GetFloatValue(), (double)FSecondOperand.GetFloatValue());
  }
}

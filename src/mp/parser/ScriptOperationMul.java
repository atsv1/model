package mp.parser;


/**
 * User: atsv
 * Date: 29.04.2006
 */
/**
 * Умножение
 */

public class ScriptOperationMul extends ScriptOperation3Operand {

  private int MulInt() throws ScriptException
  {
    int result = 0;
    result = FFirstOperand.GetIntValue() * FSecondOperand.GetIntValue();
    return result;
  }

  private float MulFloat() throws ScriptException 
  {
    float result = 0;
    result = FFirstOperand.GetFloatValue() * FSecondOperand.GetFloatValue();
    return result;
  }

  public int ExecOperation( int aProgramPointer ) throws ScriptException
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
      FResult.SetValue(  MulInt() );
      return 3;
    }
    if ( s.equalsIgnoreCase("real") )
    {
      FResult.SetValue(  MulFloat() );
      return 3;
    }
    return 0;
  }
}

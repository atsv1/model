
package mp.parser;

/**
 * User: atsv
 * Date: 13.05.2006
 * Time: 12:47:59
 */
public class ScriptOperationSub extends ScriptOperation3Operand {

  public ScriptOperationSub(){
    super();
    //FIsPreviousOperationEnabled = false;
  }

  protected void IsOperationEnabled() throws ScriptException  {
    ScriptException e;
    if ( FResult.GetTypeName().equalsIgnoreCase("integer") ) {
      if ( (FFirstOperand.GetTypeName().equalsIgnoreCase("integer") || FFirstOperand.GetTypeName().equalsIgnoreCase("real")) &&
           (FSecondOperand.GetTypeName().equalsIgnoreCase("integer") || FSecondOperand.GetTypeName().equalsIgnoreCase("real"))
         ) {
        FIsPreviousOperationEnabled = true;

        return;
      } else  {
        e = new ScriptException("Несовместимые типы с integer при операции вычитания");
        throw e;
      }
    }
    if ( FResult.GetTypeName().equalsIgnoreCase("real") ) {
      if ( (IsOperandNumeric(FFirstOperand))  &&
           (IsOperandNumeric(FSecondOperand))
          ) {
        FIsPreviousOperationEnabled = true;
        return;
      }
    } else {
       e = new ScriptException("Несовместимые типы с real");
       throw e;
    }
    e = new ScriptException(FResult.GetTypeName() + "несовместимо с операцией вычитания");
    throw e;
  }

  private float SubFloat() throws ScriptException
  {
    return FFirstOperand.GetFloatValue() - FSecondOperand.GetFloatValue();
  }

  private int SubInt() throws ScriptException
  {
    return FFirstOperand.GetIntValue() - FSecondOperand.GetIntValue();
  }

  public int ExecOperation(int aProgramPointer) throws ScriptException {
    FFirstOperand = InitOperand( aProgramPointer + 1 );
    FSecondOperand = InitOperand( aProgramPointer + 2 );
    FResult = (Variable)InitOperand( aProgramPointer + 3 );
    if ( !FIsPreviousOperationEnabled ) {
      IsOperationEnabled();
      FIsPreviousOperationEnabled = false;
    }
    if ( FResult.GetTypeName().equalsIgnoreCase("integer") )
    {
      FResult.SetValue( SubInt() );
    }
    if ( FResult.GetTypeName().equalsIgnoreCase("real") )
    {
      FResult.SetValue( SubFloat() );
    }
    return 3;
  }

}

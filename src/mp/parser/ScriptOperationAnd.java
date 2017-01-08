package mp.parser;

/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 19.08.2006
 * Time: 18:25:05
 * To change this template use File | Settings | File Templates.
 */
public class ScriptOperationAnd extends ScriptOperation3Operand {

  protected int FOperationType = 0;

  protected void IsOperationEnabled() throws ScriptException
  {
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("integer") &&
         FSecondOperand.GetTypeName().equalsIgnoreCase("integer") &&
         FResult.GetTypeName().equalsIgnoreCase("integer")
       )
    {
      FOperationType = 1;
      return;
    }
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("boolean") &&
         FSecondOperand.GetTypeName().equalsIgnoreCase("boolean") &&
         FResult.GetTypeName().equalsIgnoreCase("boolean")
       )
    {
      FOperationType = 2;
      return;
    }
    ScriptException e = new ScriptException("Несовместимые типы для выполнения операции and" );
    throw e;
  }

  protected void ExecInt() throws ScriptException
  {
    int i;
    i = FFirstOperand.GetIntValue() & FSecondOperand.GetIntValue();
    FResult.SetValue(i);
  }

  protected void ExecBoolean() throws ScriptException
  {
    boolean i;
    i = FFirstOperand.GetBooleanValue() & FSecondOperand.GetBooleanValue();
    FResult.SetValue(i);
  }

  public int ExecOperation(int aProgramPointer) throws ScriptException
  {
    FFirstOperand = InitOperand( aProgramPointer + 1 );
    FSecondOperand = InitOperand( aProgramPointer + 2 );
    FResult = (Variable)InitOperand( aProgramPointer + 3 );
    if ( !FIsPreviousOperationEnabled )
    {
     IsOperationEnabled();
     FIsPreviousOperationEnabled = true;
    }
    if ( FOperationType == 1 )
    {
      ExecInt();
      return 3;
    }
    if ( FOperationType == 2 )
    {
      ExecBoolean();
      return 3;
    }
    return 0;
  }

}

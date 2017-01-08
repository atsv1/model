package mp.parser;


/**
 * User: atsv
 * Date: 21.08.2006
 */
public abstract class ScriptOperationBooleanResult extends ScriptOperation3Operand {

  int FOperationType = 0;

  protected void IsOperationEnabled() throws ScriptException
  {
    ScriptException e;
    if ( !FResult.GetTypeName().equalsIgnoreCase("boolean") )
    {
      e = new ScriptException("–езультат операции не логического типа");
      throw e;
    }
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase( FSecondOperand.GetTypeName() ) )
    {
      e = new ScriptException("Ќельз€ сравнивать значени€ разных типов: " + FFirstOperand.GetTypeName() +
                              " и " + FSecondOperand.GetTypeName());
    }
    //если операци€ возможна, то определ€ем тип операции
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("integer") &&
            FSecondOperand.GetTypeName().equalsIgnoreCase("integer"))
    {
      FOperationType = 1;
      return;
    }
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("real") || FSecondOperand.GetTypeName().equalsIgnoreCase("real"))
    {
      FOperationType = 2;
      return;
    }
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("boolean") )
    {
      FOperationType = 3;
    }
  }

  public String GetResultType() throws ScriptException
  {
    return "boolean";
  }

  protected abstract void CompareInt() throws ScriptException;

  protected abstract void CompareReal() throws ScriptException;

  protected abstract void CompareBoolean() throws ScriptException;

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
    switch (FOperationType)
    {
      case 1:{
        CompareInt();
        break;
      }
      case 2:{
        CompareReal();
        break;
      }
      case 3:{
        CompareBoolean();
        break;
      }
    }
    return 3;
  }
}

package mp.parser;


/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 21.08.2006
 * Time: 20:16:08
 * To change this template use File | Settings | File Templates.
 */
public class ScriptOperationNot extends ScriptOperation2Operand {

  protected void IsOperationEnabled() throws ScriptException
  {
    if ( !FResult.GetTypeName().equalsIgnoreCase("boolean") ||
         !FOperand.GetTypeName().equalsIgnoreCase("boolean")
       )
    {
      ScriptException e;
      e = new ScriptException("Ќедопустимый тип данных дл€ данной операции");
      throw e;
    }
  }

   public String GetResultType() throws ScriptException
  {
    return "boolean";
  }

  public int ExecOperation(int aProgramPointer) throws ScriptException
  {
    FOperand = InitOperand( aProgramPointer + 1 );
    FResult = (Variable)InitOperand( aProgramPointer + 2 );
    if ( !FIsPreviousOperationEnabled )
    {
     IsOperationEnabled();
     FIsPreviousOperationEnabled = true;
    }
    boolean f = !FOperand.GetBooleanValue();
    FResult.SetValue(f);
    return 2;
  }
}

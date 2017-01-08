
package mp.parser;

/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 17.05.2006
 * Time: 19:03:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class ScriptOperation2Operand extends ScriptOperation {
  protected Operand FOperand = null;
  protected Variable FResult = null;
  protected boolean FIsPreviousOperationEnabled = false;

  protected void IsOperationEnabled() throws ScriptException
  {
    if (! FOperand.GetTypeName().equalsIgnoreCase( FResult.GetTypeName() )  )
    {
      if ( !FOperand.IsAutoCastEnabled( FResult.GetTypeName() ) ){
        ScriptException e;
        e = new ScriptException("Несовместимые типы " + FOperand.GetTypeName() + " и " + FResult.GetTypeName());
        throw e;
      }
    }
    FIsPreviousOperationEnabled = true;
  }

  public String GetResultType() throws ScriptException
  {
    return FOperand.GetTypeName();
  }

  public Variable GetResultVariable(int aProgramPointer) throws ScriptException
  {
    String s = GetResultType();
    Variable result = null;
    if ( s.equalsIgnoreCase("integer") )
    {
      int i = 0;
      result =  new Variable(i);
    }
    if ( s.equalsIgnoreCase("boolean") )
    {
      boolean f = false;
      result =  new Variable(f);
    }
    if ( s.equalsIgnoreCase("real") )
    {
      float r = (float) 7.1;
      result =  new Variable(r);
    }
    return result;
  }

}

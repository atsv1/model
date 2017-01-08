package mp.parser;

/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 05.04.2006
 * Time: 21:28:54
 * To change this template use File | Settings | File Templates.
 */
public class ScriptExecutor
{
  public Operand resultValue;
  private PascalParser parser;

  public ScriptExecutor()
  {
    resultValue = new ScriptConstant(7);
    resultValue.SetName("result");
    parser = new PascalParser();
  }

  public boolean ExecuteScript( String aScript )
  {
    boolean f = false;
    if ( aScript != "" )
    {
       f = true;
    }
    return f;
  }
}

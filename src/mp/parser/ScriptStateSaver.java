package mp.parser;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 29.08.2006
 * Time: 15:21:28
 * Класс предназначен для хранения текущего состояния автомата, который
 * разбирает операторы перехода
 */
public class ScriptStateSaver {
  private Stack FStack = null;
  private int FLinkCounter = 0;

  public ScriptStateSaver()
  {
    FStack = new Stack();
    FLinkCounter = 0;
  }

  public void NewOperator(ScriptLexem aInitLexem)
  {
    Object[] newOperator;
    newOperator = new Object[2];
    newOperator[0] = aInitLexem;
    Integer linkCounter = new Integer( FLinkCounter );
    FLinkCounter++;
    newOperator[1] = linkCounter;
    FStack.push( newOperator );
  }

  public int GetCurrentLinkId()
  {
    if ( FStack.empty() )
    {
      return -1;
    }
    Object[] operator;
    operator = (Object[])FStack.peek();
    Integer i = (Integer) operator[1];
    return i.intValue();
  }

  public void DelCurrentOperator()
  {
    if ( !FStack.empty() )
    {
      FStack.pop();
    }
  }

}

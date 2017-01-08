package mp.parser;

/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 15.04.2006
 * Time: 16:14:00
 * To change this template use File | Settings | File Templates.
 */
public class ScriptBeginLexem extends ScriptLexem {
  public ScriptBeginLexem()
  {
    super();
    FLanguageName = "Начало";
  }

  public boolean IsLexemEquals(ScriptLexem aLexem)
  {
     boolean f = false;
    if ( aLexem == null )
    {
      f = false;
    } else
    {
      f = aLexem.GetLanguageName().equalsIgnoreCase( FLanguageName );
    }
    return f;
  }

  public boolean IsMyToken(String aTokenName)
  {
    return false;
  }

  public ScriptLexem GetProducedLexem(int aLexemIndex)
  {
    return null;
  }

  public Object clone()
  {
    return new ScriptBeginLexem();
  }

  public Object GetExecutableObject() {
    return null;  
  }

  public String toString(){
    return "begin";
  }

}

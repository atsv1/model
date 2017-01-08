package mp.parser;

/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 15.04.1995
 * Time: 23:51:24
 * To change this template use File | Settings | File Templates.
 */
public class ScriptEndLexem extends ScriptLexem {

  public ScriptEndLexem()
  {
    super();
    FLanguageName = "Конец";
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

  public boolean IsMyToken(String aTokenName) {
    return false;
  }

  public ScriptLexem GetProducedLexem(int aLexemIndex) {
    return null;
  }

  public Object clone()
  {
    return new ScriptEndLexem();
  }

  public Object GetExecutableObject() {
    return null;  
  }

  public String toString(){
    return "end";
  }

}

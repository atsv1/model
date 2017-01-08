package mp.parser;


/**
 * User: atsv
 * Date: 22.05.2006
 */
public class ScriptLexemEndString extends ScriptLexem {

  public ScriptLexemEndString()
  {
    super();
    FLanguageName = ";";
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
    return ";".equalsIgnoreCase( aTokenName );
  }

  public Object GetExecutableObject() throws ScriptException
  {
    return null;
  }

  public Object clone()
  {
    ScriptLexemEndString result;
    result = new ScriptLexemEndString();
    result.FCodePart = ";";
    return result;
  }

  public String toString(){
    return ";";
  }

}

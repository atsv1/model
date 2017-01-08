package mp.parser;

/**
 * User: atsv
 * Date: 21.08.2006
 */
public class ScriptActionLexem2Operand extends ScriptLexem {

  public ScriptActionLexem2Operand()
  {

    super();
    try {
      this.SetLanguageName("Действие2");
    } catch (ScriptException e) {

    }
  }

  public boolean IsLexemEquals(ScriptLexem aLexem)
  {
    if ( aLexem != null )
    {
      return GetLanguageName().equalsIgnoreCase( aLexem.GetLanguageName() );
    }  return false;
  }

  public boolean IsMyToken(String aTokenName)
  {
     boolean f = false;
    int i = 0;
    String token;
    while ( i < ScriptLanguageDef.SimpleOperation2OperandList.length )
    {
      token = ScriptLanguageDef.SimpleOperation2OperandList[i];
      if ( aTokenName.toUpperCase().equalsIgnoreCase( token ) )
      {
        f = true;
        break;
      }

      i++;
    }//while
    return f;
  }

  public Object GetExecutableObject() throws ScriptException
  {
    ScriptOperation operation = null;
    if ( FCodePart.equalsIgnoreCase("not") )
    {
      operation = new ScriptOperationNot();
      return operation;
    }
    return operation;
  }

   public Object clone()
  {
    ScriptActionLexem2Operand  lexem = new ScriptActionLexem2Operand();
    lexem.FCodePart = this.FCodePart;
    return lexem;
  }

  public boolean IsNewOperandNeed()
  {
    return true;
  }

  public String toString(){
    return "Действие2 " + FCodePart;
  }

}

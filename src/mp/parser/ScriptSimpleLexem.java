 package mp.parser;


/**
 * User: atsv
 * Date: 04.04.2006
 */
 public class ScriptSimpleLexem extends ScriptLexem {
  protected String FToken = "";

  //Процедуры

  public ScriptSimpleLexem(String aTokenName)
  {
    FToken = aTokenName;
    FLanguageName = FToken;
  }

  public boolean IsLexemEquals(ScriptLexem aLexem) {
    try {
      ScriptSimpleLexem lexem = (ScriptSimpleLexem)aLexem;
      return lexem.FToken.equalsIgnoreCase( FToken );
    }
    catch (Exception e) {
      return false;
    }
  }

  public  boolean IsMyToken(String aTokenName)  {
   if ( FToken.equalsIgnoreCase( aTokenName ) ) {
      return true;
    } else
     return false;
  } //IsMyToken

  public ScriptLexem GetProducedLexem(int aLexemIndex) {
    if ( aLexemIndex >= FProducedLexem.size() )
      return null;
    else    {
      return (ScriptLexem) FProducedLexem.get(aLexemIndex);
    }
  } //GetProducedLexem




  public void finalize()
  {
    FProducedLexem = null;

  }
  /*
  Проверяется, может ли грамматическая конструкция, передаваемая
  в параметре,
  следовать в коде скрипта за конструкцией, за которую отвечает
  данный объект
  */
  public  boolean IsTokenCanProduced(String aTokenName) {
    return false;
  }

  public Object clone()  {
    return new ScriptSimpleLexem( FToken );
  }

  public Object GetExecutableObject() {
    if ( FToken.equalsIgnoreCase("jnt") )
    {
      ScriptOperationJNT operation = new ScriptOperationJNT();
      return operation;
    }
    if ( FToken.equalsIgnoreCase("jmp") )
    {
      ScriptOperationJMP operation = new ScriptOperationJMP();
      return operation;
    }
    return null;
  }

  public String toString(){
    return "Simple " + FCodePart;
  }

}

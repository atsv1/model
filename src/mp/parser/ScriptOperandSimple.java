package mp.parser;

import java.util.Vector;

/**
 * User: atsv
 * Date: 04.04.2006
 */

/*
 Класс, отвечающий за операнды
*/
public class ScriptOperandSimple extends ScriptLexem {
	
  private String FOperandName;
  
  public ScriptOperandSimple(String aOperandName) {
    super();
    FProducedLexem = new Vector();
    Variables = null;
    FOperandName = aOperandName;
  }//ScriptSimpleLexem

  public boolean IsLexemEquals(ScriptLexem aLexem) {
    if ( aLexem == null ) return false;
    return GetLanguageName().equalsIgnoreCase(aLexem.GetLanguageName());
  }

  public boolean IsMyToken(String aTokenName) {
    Object variable = null;
    if ( Variables != null ) {
      variable = Variables.GetVariable(aTokenName.toUpperCase());
    }
    return variable != null;
  }

  public static ScriptLexem GetProducedLexem(int aLexemIndex) {
    return null;
    //@todo
  }

  public Object clone() {
    ScriptOperandSimple result = new ScriptOperandSimple(FOperandName);
    result.Variables = this.Variables;
    result.FCodePart = this.FCodePart;
    result.FOperandName = this.FCodePart;
    return result;
  }

  public Object GetExecutableObject()  throws ScriptException {
    Object result = null;
    if ( Variables != null ) {
      result = Variables.GetVariable( FOperandName );
      if ( result != null ) {
         return result;
      } else {
        ScriptException e = new ScriptException("Неизвестная переменная " + FOperandName);
        throw e;
      }
    } else {
      //return null;
      ScriptException e = new ScriptException("Нет ни одной переменной ");
      throw e;
    }
  }


  public String GetLanguageName() {
    return "Операнд";
  }

  public String GetOperandName() {
    return FOperandName;
  }

  public void SaveCode(String aCode) {
    FCodePart =  aCode ;
    FOperandName = aCode;
  }

  public String toString(){
    return "Операнд назв=" + FOperandName + " код =  " + FCodePart;
  }

}

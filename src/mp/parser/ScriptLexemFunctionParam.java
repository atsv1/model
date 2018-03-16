package mp.parser;

import java.util.List;
import java.util.Vector;

/**
 * User: Администратор
 * Date: 19.04.2008
 * Time: 14:23:59
 */

public class ScriptLexemFunctionParam extends ScriptLexem {
  private final static String FTokenName = "ПараметрФункции";
  /**В этом списке хранятся ссылки на 
   *
   */
  private Vector FPreviousLexemList = new Vector();

  public ScriptLexemFunctionParam(){
    super();
    FLanguageName = FTokenName;
    FCodePart = FTokenName; 
  }

  public boolean IsLexemEquals(ScriptLexem aLexem) {

    return FTokenName.equalsIgnoreCase( aLexem.GetLanguageName() );
  }

  public boolean IsMyToken(String aTokenName) {
    return FTokenName.equalsIgnoreCase( aTokenName );
  }

  public Object GetExecutableObject() throws ScriptException {
    Vector result = new Vector();
    AddProgramObject( result );
    return result;
  }

  public Object clone(){
    return new ScriptLexemFunctionParam(); 
  }

  /**Добавляем в лексему информацию о том, какие именно лексемы она заменит
   *
   * @param aParsedLexemList
   * @param aStartPos
   * @param aLexemCount
   */
  public void AddProduction( List<ScriptLexem> aParsedLexemList, int aStartPos, int aLexemCount){
    int i = 0;
    ScriptLexem lexem;
    while ( i < aLexemCount ){
      lexem = (ScriptLexem) aParsedLexemList.get( aStartPos + i );
      if ( (lexem instanceof ScriptLexemFunctionParam) || ( lexem instanceof  ScriptDigitLexem) ||
         (lexem instanceof ScriptOperandSimple)){
        FPreviousLexemList.add( lexem );  
      }
      i++;
    }
  }

  private void AddProgramObject( Vector aProgramObjects ) throws ScriptException {
    int i = 0;
    ScriptLexem lexem;
    while ( i < FPreviousLexemList.size() ){
      lexem = (ScriptLexem) FPreviousLexemList.get( i );
      if ( lexem instanceof ScriptLexemFunctionParam ){
        ((ScriptLexemFunctionParam)lexem).AddProgramObject( aProgramObjects );
      } else {
        aProgramObjects.add( lexem.GetExecutableObject() );
      }
      i++;
    }//while
  }

  public Vector GetProgramObjectList() throws ScriptException {
    Vector result = new Vector();
    AddProgramObject( result );
    return result;
  }

  

}

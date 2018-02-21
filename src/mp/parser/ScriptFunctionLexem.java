package mp.parser;

import java.util.Hashtable;
import java.util.Map;




/**
 *
 * Date: 20.05.2006
 */
public class ScriptFunctionLexem extends ScriptLexem {


  private String FSearchedToken = null;
  private int FSearchedIndex = -1;
  private Hashtable FFunctionList = null;
  private String FUserFunctionName = null;
  private Map<String, ExternalFunction> externalFunctions;

  public ScriptFunctionLexem(){
    super();
    FLanguageName = "Функция";
  }

  public void SetFunctionList(Hashtable aFunctionList){
  	FFunctionList = aFunctionList;
  }

  public boolean IsLexemEquals(ScriptLexem aLexem) {
    boolean f = false;
    if ( aLexem == null ){
      f = false;
    } else {
      try {
        ScriptFunctionLexem l = (ScriptFunctionLexem)aLexem;
        f = true;
      } catch (Exception e) {
        return false;
      }
    }
    return f;
  }
  
  public boolean IsMyToken(String aTokenName) {
    int i = 0;    
    while ( i < ScriptLanguageDef.FunctionsList.length) {
      if ( ScriptLanguageDef.FunctionsList[i][0].equalsIgnoreCase( aTokenName ) ) {
        FSearchedToken = aTokenName;
        FSearchedIndex = i;
        FUserFunctionName = null;
        return true;
      }
     i++;
    }
    /*Функции, определяемые непосредственно внутри модели*/
    if ( FFunctionList != null ) {
      if ( FFunctionList.get(aTokenName.toUpperCase()) != null) {
      	FUserFunctionName = aTokenName;
      	FSearchedIndex = -1;
      	return true;
      }
    }
    
    if ( externalFunctions != null && !externalFunctions.isEmpty() ) {
    	ExternalFunction ef = externalFunctions.get(aTokenName);
    	if ( ef != null ) {
    		return true;
    	}
    }
    return false;
  }

  public Object GetExecutableObject() throws ScriptException {
  	if (FUserFunctionName != null && FFunctionList != null ) {
  		return FFunctionList.get(FUserFunctionName.toUpperCase());
  	}
    IsMyToken( FCodePart );
    ScriptOperationFunction result = new ScriptOperationFunction(FSearchedIndex);    
    return result;
  }

  public Object clone() {
    ScriptFunctionLexem result = null;
    result = new ScriptFunctionLexem();
    result.FCodePart = this.FCodePart;
    result.FFunctionList = this.FFunctionList;
    result.FUserFunctionName = this.FUserFunctionName;
    return result;
  }
  
  public void setExternalFunctions(Map<String, ExternalFunction> functions){
  	this.externalFunctions = functions;
  	
  }

   public boolean IsNewOperandNeed() {
    return true;
  }

  public String toString(){
    return "Функция  " + FCodePart;
  }

}

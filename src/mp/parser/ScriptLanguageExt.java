package mp.parser;

import java.util.Collection;
import java.util.Hashtable;


/**
 * User: atsv
 * Date: 11.10.2006
 */
public class ScriptLanguageExt {
  private VariableList FVariables;
  private Hashtable<String, ScriptOperationUserFunction> FFunctions = null;


  public ScriptLanguageExt(){
    FVariables = new VariableList();
  }

  public void AddVariable( Variable aVariable ) throws ScriptException{
    FVariables.AddVariable( aVariable );
  }

  protected VariableList GetVariables(){
    return FVariables;
  }

  public Variable Get( String name ){
    return (Variable) FVariables.GetVariable( name );
  }



  public ScriptLanguageExt clone() throws CloneNotSupportedException {
    ScriptLanguageExt result = new ScriptLanguageExt();
    Variable currentVar = FVariables.First();
    while ( currentVar != null ){
      try {
        result.AddVariable((Variable) currentVar.clone());
      } catch (ScriptException e) {
        CloneNotSupportedException e1 = new CloneNotSupportedException( e.getMessage() );
        throw e1;
      }
      currentVar = FVariables.Next();
    }
    return result;
  }

  public void AddFuction(ScriptOperationUserFunction aFunction) throws ScriptException{
  	if (aFunction == null) {
  		throw new ScriptException("Попытка добавить пустую функцию");
  	}
  	if ( FFunctions == null ) {
  		FFunctions = new Hashtable();
  	}
  	FFunctions.put(aFunction.GetName().toUpperCase(), aFunction);
  }

  public ScriptOperationUserFunction GetFunction(String aFunctionName) {
  	if ( FFunctions == null || aFunctionName == null) {
  		return null;
  	}
  	return FFunctions.get( aFunctionName.toUpperCase() );
  }

  public Hashtable<String, ScriptOperationUserFunction> GetFunctions(){
  	return  FFunctions;
  }

  public void RemoveVariable( String aVarName ){
  	FVariables.RemoveVariable(aVarName);

  }



}

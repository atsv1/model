package mp.parser;


/**
 * User: atsv
 * Date: 29.04.2006
 */

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 */
public class VariableList {
  private Hashtable<String, Operand> FVariables;
  private int FTempVariablesCount = 0;
  private String FTempVariablePrefix = ScriptLanguageDef.GetTempVarPrefix();
  private Enumeration FElements = null;
  private Vector FVariablesVector = null;

  public VariableList() {
    FVariables = new Hashtable();
    FVariablesVector = new Vector();
  }

  public void AddVariable( Operand aVariable ) {
    if ( aVariable != null ) {
      FVariables.put( aVariable.GetName().toUpperCase(), aVariable );
      FVariablesVector.add( aVariable );
    }
  }

  public Operand GetVariable(String aVariableName) {
    return (Operand)FVariables.get(aVariableName.toUpperCase());
  }

  public Variable GetVariable( int index ){
    if ( index > FVariablesVector.size() ) {
      return null;
    }
    return (Variable) FVariablesVector.get( index );
  }

  public int GetSize(){
    return FVariablesVector.size();
  }

  public int GetNoServiceVariablesCount(){
    return FVariablesVector.size() - FTempVariablesCount;
  }

  public Variable AddTempVariable() {
    Variable tempVariable = new Variable(0);
    tempVariable.SetName(GetNewTempVariableName());
    this.AddVariable( tempVariable );
    return tempVariable;
  }

  public String GetNewTempVariableName() {
    FTempVariablesCount++;
    return FTempVariablePrefix + String.valueOf( FTempVariablesCount-1 );
  }

  public Variable GetLastTempVariable() {
    Variable result = null;
    if ( FTempVariablesCount > 0 )
    {
      result = (Variable) GetVariable( FTempVariablePrefix + String.valueOf( FTempVariablesCount-1 ));
    }
    return result;
  }

  public Variable First(  ){
    FElements = FVariables.elements();
    if ( FElements.hasMoreElements() ){
      return (Variable) FElements.nextElement();
    } else {
      FElements = null;
      return null;
    }
  }

  public Variable Next(){
    if ( FElements == null ){
      FElements = FVariables.elements();
    }
    if ( FElements.hasMoreElements() ){
      return (Variable) FElements.nextElement();
    }
    FElements = null;
    return null;
  }

  private Variable GetNotServiceVar(){
    Variable var;
    while ( FElements.hasMoreElements() ){
      var = (Variable) FElements.nextElement();
      if ( !ScriptLanguageDef.IsServiceName(var.GetName()) ){
        return var;
      }
    }
    return null;
  }

  public Variable GetFirstNoServiceVar(){
    FElements = FVariables.elements();
    return GetNotServiceVar();
  }

  public Variable GetNextNoServiceVar(){
    FElements = FVariables.elements();
    return GetNotServiceVar();
  }

  public int IndexOf( Variable aVariable ){
    return FVariablesVector.indexOf( aVariable );
  }

  public void RemoveVariable(String aVarName){
  	if ( aVarName == null ) {
  		return;
  	}
  	Variable varToDel = (Variable) FVariables.get(aVarName.toUpperCase());
  	if (varToDel == null) {
  		return;
  	}
  	FVariables.remove(aVarName.toUpperCase());
  	FVariablesVector.remove(varToDel);

  }

}

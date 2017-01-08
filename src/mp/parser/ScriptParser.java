package mp.parser;

/**
 * User: atsv
 * Date: 04.09.2007
 */
public interface ScriptParser {

  void ExecuteScript() throws ScriptException;

  ScriptProgramObject First(String ... aClassName);

  String GetSource();

  VariableList GetVariables();

  boolean IsMovResult();

  ScriptProgramObject Next(String ... aClassName);

  void ParseScript(String aScript) throws ScriptException;

  void SetLanguageExt( ScriptLanguageExt aLanguageExt ) throws ScriptException;

  public void AddExecutionContext(ExecutionContext context);

  public ExecutionContext GetExecutionContext();

}

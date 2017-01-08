package mp.elements;

import mp.parser.ScriptException;

/**
 * User: саша
 * Date: 30.05.2008
 */
public class ValueExecutor extends MultiBlockEnableExecutor {
  private boolean FIsEnableArrayNotUpdated = true;

  public ValueExecutor(ModelDynamicBlock aOwner, ModelBlockParam aResultParam, String aScriptSource) {
    super(aOwner, aResultParam, aScriptSource);
  }

  public void ExecuteScript() throws ScriptException {
    long start = System.nanoTime();
    FEnterCount++;
    if ( FIsEnableArrayNotUpdated ){
      SetEnableArray();
      FIsEnableArrayNotUpdated = false;
    }

    ResourceRecord rec = GetFirst();
    while ( rec != null ){
      if ( rec.IsNeedToExec() ){
        LoadParams( rec.GetBlock() );
        FParser.ExecuteScript();
        rec.StoreExecResult( FResultParam.GetVariable() );
        rec.ScriptExecuted();
        FRealExecCount++;
      }
      rec = GetNext();
    }

    FRealDuration = FRealDuration + System.nanoTime() - start;
  }

}

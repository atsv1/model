package mp.elements;

import mp.parser.ScriptException;

/**
 * User: саша
 * Date: 30.05.2008
 */
public class EnableExecutor extends MultiBlockExecutor {
  /** Массив предназначен для хранения результатов разрешающего скрипта.
   * Нужен для ускорения доступа к этой информации извне.
   */
  ExistsService FEnableArray = null;

  public EnableExecutor(ModelDynamicBlock aOwner, ModelBlockParam aResultParam, String aScriptSource) {
    super(aOwner, aResultParam, aScriptSource);
  }

  private void CreateEnableArray(){
    FEnableArray = new ExistsService(0, GetResourceCount());
  }

  public void ExecuteScript() throws ScriptException, ModelException {
    if ( FEnableArray == null ){
      CreateEnableArray();
    }
    long start = System.nanoTime();
    FEnterCount++;
    ResourceRecord rec = GetFirst();
    while ( rec != null ){
      if ( rec.IsNeedToExec() ){
        LoadParams( rec.GetBlock() );
        FParser.ExecuteScript();
        rec.StoreExecResult( FResultParam.GetVariable() );
        int i = GetCurrentRecordPos();
        if ( FResultParam.GetVariable().GetBooleanValue() ) {
          FEnableArray.ElementExists( i );
        } else {
          FEnableArray.ElementNotExists( i );
        }
        rec.ScriptExecuted();
        FRealExecCount++;
      }
      rec = GetNext();
    }
    FRealDuration = FRealDuration + System.nanoTime() - start;
  }

  protected ExistsService GetEnableArray(){
    return FEnableArray; 
  }



}

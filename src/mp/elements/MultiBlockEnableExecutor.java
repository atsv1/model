package mp.elements;

/**
 * User: Администратор
 * Date: 26.06.2008
 */
public abstract class MultiBlockEnableExecutor extends MultiBlockExecutor{
  private ExistsService FEnableArray = null;
  protected EnableExecutor FEnableExecutor;
  private boolean FIsEnableExists = false;

  public MultiBlockEnableExecutor(ModelDynamicBlock aOwner, ModelBlockParam aResultParam, String aScriptSource) {
    super(aOwner, aResultParam, aScriptSource);
  }


  protected void SetEnableArray(){
    if ( FEnableExecutor == null ){
      return;
    }
    FEnableArray = FEnableExecutor.GetEnableArray();
  }

  protected boolean GetEnableFlag( int aPosition ) throws ModelException {
    if ( FEnableArray == null ){
      return true;
    }
    return FEnableArray.IsExistsInList( aPosition );
  }

  protected ResourceRecord GetNext(){
    ResourceRecord result = super.GetNext();
    if ( FEnableArray == null ){
      return result;
    }
    int pos = this.GetCurrentRecordPos();
    boolean flag = true;
    boolean enableFlag = false;
    while ( flag ){
      try {
        enableFlag = GetEnableFlag( pos );
      } catch (ModelException e) {
        enableFlag = true;
      }
      flag = !( (result == null) || enableFlag);
      if ( flag ) {
        result = super.GetNext();
        pos = this.GetCurrentRecordPos();
      }
    }
    return result;
  }

  public void SetEnableExecutor( EnableExecutor aEnableExec ){
    FIsEnableExists = false;
    if ( aEnableExec != null ){
      FIsEnableExists = true;
    }
    FEnableExecutor = aEnableExec;
  }
}

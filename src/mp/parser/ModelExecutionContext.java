package mp.parser;

import java.util.Hashtable;

import mp.elements.Model;

/**
 * Date: 07.04.2008
 */
public class ModelExecutionContext {
  private static final ThreadLocal<Hashtable<String, ModelExecutionManager>> FExecutionManagerList = new ThreadLocal <Hashtable<String, ModelExecutionManager>>();

  public static void AddModelExecutionManager( ModelExecutionManager aManager ) throws ScriptException{
    if ( aManager == null ){
      return;
    }
    if ( FExecutionManagerList.get() == null ) {
    	Hashtable<String, ModelExecutionManager> managerList = new Hashtable();
    	FExecutionManagerList.set(managerList);
    }
    Hashtable<String, ModelExecutionManager> tab = FExecutionManagerList.get();
    ModelExecutionManager manager = tab.get( aManager.GetManagerName().toUpperCase() );
    if ( manager == null ){
      FExecutionManagerList.get().put( aManager.GetManagerName().toUpperCase(), aManager );

    } else {
      if ( aManager != manager ){
        ScriptException e = new ScriptException( "¬ системе уже присутствует модель с именем \"" + aManager.GetManagerName() + "\"" );
        throw e;
      }
    }
  }

  public static ModelExecutionManager GetManager( String aManagerName ){
    if ( aManagerName == null ){
      return null;
    }
    Hashtable<String, ModelExecutionManager> tab = FExecutionManagerList.get();
    return (ModelExecutionManager) tab.get( aManagerName.toUpperCase() );
  }

  public static void ClearExecutionContext(){
    FExecutionManagerList.set(null);
  }



}

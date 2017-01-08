package mp.parser;

/**
 */
public abstract class ChangeListener implements VariableChange{

  private Object FListener = null;

  public ChangeListener( Object aListener ){
    FListener = aListener;    
  }

  public ChangeListener( ){
    FListener = null;
  }

  public abstract void VariableChanged( VariableChangeEvent changeEvent );

  public boolean IsListenerEquals( Object aAnotherListener ){
    if ( FListener == null ){
      return false;
    }
    return aAnotherListener == FListener;
  }

}

package mp.elements;

/**
 * User: atsv
 * Date: 16.09.2006
 */
public abstract class ModelExecutor {

  private ModelIterator FModelIterator = null;

  public ModelExecutor(ModelIterator aModelIterator){
    FModelIterator = aModelIterator;
  }

  public abstract void  DoSomething(ModelElement aObjectToAction) throws ModelException;

  public void Execute() throws ModelException{
    ModelElement element = FModelIterator.First();
    while ( element != null ) {
      DoSomething( element );
      element = FModelIterator.Next(); 
    }
  }

}

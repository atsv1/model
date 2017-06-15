package mp.elements;

/**
 * User: atsv
 * Date: 16.09.2006
 * Класс выполняет добавление элемента в переданный ему список.
 *
 */
public class ModelAddExecutor extends ModelExecutor {

  private boolean FIsUniqueElements = false;
  public ModelElementContainer container = null;

  public ModelAddExecutor(ModelIterator aModelIterator) {
    super(aModelIterator);
  }

  public void SetUniqueFlag( boolean aFlagValue ){
    FIsUniqueElements = true;  
  }

  public void DoSomething(ModelElement aObjectToAction) throws ModelException{
    if ( container == null ) {
      ModelException e = new ModelException("Попытка добавить элемент в пустой список");
      throw e;
    }
    if ( aObjectToAction == null ) {
      ModelException e = new ModelException("Попытка добавить пустой элемент");
      throw e;
    }
    if ( FIsUniqueElements && container.Get( aObjectToAction.GetElementId() ) != null){
      return;
    }
    container.AddElement( aObjectToAction );
  }

}

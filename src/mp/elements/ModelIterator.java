package mp.elements;
/**
 * User: atsv
 * Date: 16.09.2006
 */
public abstract class ModelIterator {

  public ModelIterator(){
    super();
  }

  public abstract ModelElement First() throws ModelException;
  public abstract ModelElement Next() throws ModelException;
}

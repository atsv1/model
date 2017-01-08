package mp.elements;
import java.util.Hashtable;
import java.util.Vector;

/**
 * User: atsv
 * Date: 22.09.2006
 *
 */
public class ModelElementClassesContainer extends ModelElementContainer{

  private Hashtable FElementClasses;

  public ModelElementClassesContainer(){
    super();
    SetUniqueNameFlag(false);
    FElementClasses = new Hashtable();
  }

  protected void AddByName(ModelElement aElement){
    Vector elementVector = null;
    elementVector = (Vector) FElementClasses.get( aElement.GetName().toUpperCase() );
    if ( elementVector == null ){
      elementVector = new Vector();
      FElementClasses.put( aElement.GetName().toUpperCase(), elementVector );
    }
    elementVector.add( aElement );
  }

  public void AddElement( ModelElement aElement ) throws ModelException{
    CheckBeforeAdd( aElement );
    AddById( aElement );
    AddToVector( aElement );
    AddByName( aElement );
  }

  public ModelElement Get(String aName) {
    Vector elementVector = (Vector) FElementClasses.get( aName.toUpperCase() );
    if ( elementVector != null && elementVector.size() == 1){
      return (ModelElement) elementVector.get(0);
    }
    return null;
  }

  public ModelElement Get(String aName, int aIndex){
    if ( aName == null ){
      return null;
    }
    Vector elementVector = (Vector) FElementClasses.get( aName.toUpperCase() );
    if ( elementVector != null && elementVector.size() > aIndex){
      return (ModelElement) elementVector.get( aIndex );
    }
    return null;
  }

  public int GetElementsCount(String aElementsName){
    Vector elementVector = (Vector) FElementClasses.get( aElementsName.toUpperCase() );
    if ( elementVector == null ){
      return -1;
    } else{
      return elementVector.size();
    }
  }

}

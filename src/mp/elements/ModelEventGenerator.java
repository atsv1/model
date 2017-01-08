package mp.elements;

/**
 * User: atsv
 * Date: 21.10.2006
 */
public abstract class ModelEventGenerator extends ModelElement{

  public ModelEventGenerator(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }

  static ModelTime GetNearestEventTime(ModelTime aCurrentTime, ModelElementContainer aContainer) throws ModelException{
    ModelEventGenerator eventGenerator;
    ModelTime currentTime = null;
    ModelTime result = null;
    int i = 0;
    int compareResult;
    while ( i < aContainer.size() ){
      eventGenerator = (ModelEventGenerator) aContainer.get(i);
      currentTime = eventGenerator.GetNearestEventTime( aCurrentTime );
      if ( currentTime != null ){
        compareResult = currentTime.Compare( result );
        if ( compareResult != ModelTime.TIME_COMPARE_GREATER){
          result = currentTime;
        }
      }
      i++;
    }
    return result;
  }

  public abstract ModelTime GetNearestEventTime(ModelTime aCurrentTime) throws ModelException;
  
}

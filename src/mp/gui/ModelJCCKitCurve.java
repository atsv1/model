package mp.gui;


import jcckit.data.DataCurve;
import jcckit.data.DataPoint;
import mp.elements.*;

/**
 * User: atsv
 * Date: 02.10.2006
 */
public class ModelJCCKitCurve extends ModelGUICurve{
  private DataCurve FCurve;
  private String FCurveName = null;

  public ModelJCCKitCurve(ModelAddress aAddressX, ModelAddress aAddressY, String aCurveName, ModelConnector aConnector){
    super(aAddressY, aAddressX, aConnector);
    FCurveName = aCurveName;
    FCurve = new DataCurve(FCurveName);
  }

  public DataCurve GetCurve(){
    return FCurve;
  }

  protected void AddNewPoint(double x, double y){
    FCurve.addElement( new DataPoint(x,y) );
  }

  public void ClearNotVisiblePoints(){
    int i = 0;
    DataPoint element;
    while (i < FCurve.getNumberOfElements() ){
      element = (DataPoint) FCurve.getElement( i );
      if ( IsNeedToDel(element.getX(), element.getY()) ){
        FCurve.removeElementAt( i );
      } else
      i++;
    }

  }

}

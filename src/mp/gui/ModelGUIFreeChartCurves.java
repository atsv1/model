package mp.gui;

import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.DomainOrder;
import org.jfree.chart.axis.ValueAxis;
import mp.elements.ModelException;
import mp.elements.ModelConnector;
import mp.elements.ModelAddress;
import mp.utils.ServiceLocator;

import java.util.Vector;
import java.awt.*;

/**
 * User: �������������
 * Date: 30.11.2008
 */
public class ModelGUIFreeChartCurves extends AbstractSeriesDataset  implements XYDataset {
  private Vector FCurves = null;
  private GraphBoundsManager FBoundsManager = null;
  private boolean FIsBoundsApplyToBoundsManager = false;
  private ModelGUICurveDescr FLastDescr = null;
  private boolean FIsNeedToResize = false;
  private ValueAxis FXAxis = null;
  private ValueAxis FYAxis = null;
  private String FFullName = null;

  public int getSeriesCount() {
    if ( FCurves == null ){
      return 0;
    }
    return FCurves.size();
  }

  public Comparable getSeriesKey(int series) {
    if ( series < FCurves.size() ) {
      JFreeCurve curve = (JFreeCurve) FCurves.get( series );
      return curve.GetCurveName();
    }
    IllegalArgumentException ex = new IllegalArgumentException();
    throw ex;
  }

  public DomainOrder getDomainOrder() {
    return DomainOrder.NONE;
  }

  public int getItemCount(int series) {
    if ( series >= FCurves.size() ) {
      IllegalArgumentException ex = new IllegalArgumentException();
      throw ex;
    }
    JFreeCurve curve = (JFreeCurve) FCurves.get( series );
    return curve.GetPointCount();
  }

  public Number getX(int series, int item) {
    if ( series < FCurves.size() ) {
      JFreeCurve curve = (JFreeCurve) FCurves.get( series );
      return curve.GetNumberX( item );
    }
    IllegalArgumentException ex = new IllegalArgumentException();
    throw ex;
  }

  public double getXValue(int series, int item) {
    if ( series < FCurves.size() ) {
      JFreeCurve curve = (JFreeCurve) FCurves.get( series );
      return curve.GetX( item );
    }
    IllegalArgumentException ex = new IllegalArgumentException();
    throw ex;
  }

  public Number getY(int series, int item) {
    if ( series < FCurves.size() ) {
      JFreeCurve curve = (JFreeCurve) FCurves.get( series );
      return curve.GetNumberY( item );
    }
    IllegalArgumentException ex = new IllegalArgumentException();
    throw ex;
  }

  public double getYValue(int series, int item) {
    if ( series < FCurves.size() ) {
      JFreeCurve curve = (JFreeCurve) FCurves.get( series );
      return curve.GetY( item );
    }
    IllegalArgumentException ex = new IllegalArgumentException();
    throw ex;
  }

  public void SetBoundsManager( GraphBoundsManager aBoundsManager ){
    FBoundsManager = aBoundsManager;
    FIsBoundsApplyToBoundsManager = (aBoundsManager != null);
  }

  public void SetAxis( ValueAxis xAxis, ValueAxis yAxis ){
    FXAxis = xAxis;
    FYAxis = yAxis;
  }

  public void CreateNewCurve( ModelGUICurveDescr aCurveDescr ) throws ModelException {
    if ( FCurves == null ){
      FCurves = new Vector();
    }
    JFreeCurve newCurve;
    if ( aCurveDescr.ISArray ) {
      newCurve = new JFreeArrayCurve( aCurveDescr );
    } else {
      newCurve = new JFreeCurve( aCurveDescr );
    }
    FCurves.add( newCurve );
    FLastDescr = aCurveDescr;
    if ( FFullName == null) {
      FFullName = aCurveDescr.Caption;
    } else {
       FFullName = FFullName + " " + aCurveDescr.Caption;
    }
    if ( FBoundsManager != null ){
      FBoundsManager.SetIncrements( FLastDescr.XIncrement, FLastDescr.YIncrement );
      FBoundsManager.SetInitBounds( new Rectangle( (int)FLastDescr.XAxisMinValue, (int)FLastDescr.YAxisMinValue ,
         (int)( FLastDescr.XAxisMaxValue - FLastDescr.XAxisMinValue ), (int)( FLastDescr.YAxisMaxValue - FLastDescr.YAxisMinValue ) ) );
    }
  }

  private void UpdateBounds(){
    Rectangle newBounds = FBoundsManager.GetGraphBounds();
    FXAxis.setRange( newBounds.x,  newBounds.x + newBounds.width );
    FYAxis.setRange( newBounds.y, newBounds.y + newBounds.height );
  }

  private void DeleteRedundandPoints(){
    int i = 0;
    JFreeCurve curve;
    while ( i < FCurves.size() ){
      curve = (JFreeCurve) FCurves.get( i );
      curve.DeleteRedundandPoints();
      i++;
    }

  }

  public void UpdateAll() throws ModelException {
    int i = 0;
    if ( FCurves == null ){
      return;
    }
    if (FIsBoundsApplyToBoundsManager){
      FIsBoundsApplyToBoundsManager = false;
      FBoundsManager.SetIncrements( FLastDescr.XIncrement, FLastDescr.YIncrement );
      FBoundsManager.SetInitBounds( new Rectangle( (int)FLastDescr.XAxisMinValue, (int)FLastDescr.YAxisMinValue ,
         (int)( FLastDescr.XAxisMaxValue - FLastDescr.XAxisMinValue ), (int)( FLastDescr.YAxisMaxValue - FLastDescr.YAxisMinValue ) ) );
      UpdateBounds();
    }
    FIsNeedToResize = false;
    JFreeCurve curve;
    boolean flag = false;
    while ( i < FCurves.size() ){
      curve = (JFreeCurve) FCurves.get( i );
      flag = flag | curve.UpdateCurve();
      i++;
    }
    if ( flag ) {
      DatasetChangeEvent event = new DatasetChangeEvent(this, this);
      notifyListeners( event );
    }
    if ( FIsNeedToResize ){
      UpdateBounds();
      DeleteRedundandPoints();
    }
  }

  public String toString(){
    if ( FFullName == null ){
      return super.toString();
    }
    return FFullName;
  }

  private class JFreeCurve{
    protected Vector FPointList = null;
    private String FCurveName = null;
    private ModelConnector FConnector = null;
    ModelAddress FXAddress = null;
    ModelAddress FYAddress = null;
    private double FCurrentX = 0;
    private double FCurrentY = 0;
    ModelGUICurveDescr FCurveDescr = null;


    public JFreeCurve( ModelGUICurveDescr aCurveDescr ){
      FCurveName = aCurveDescr.Caption;
      FPointList = new Vector();
      FXAddress = aCurveDescr.XAddress;
      FYAddress = aCurveDescr.YAddress;
      FCurveDescr = aCurveDescr;
      FConnector = aCurveDescr.Connector; 
    }

    public String GetCurveName(){
      return FCurveName;
    }

    public boolean UpdateCurve() throws ModelException {
      double x = FConnector.GetValue( FXAddress );
      double y = FConnector.GetValue( FYAddress );
      if ( ServiceLocator.CompareDouble( x, FCurrentX ) != 0 || ServiceLocator.CompareDouble( y, FCurrentY ) != 0 ){
        AddPoint( x, y );
        FCurrentX = x;
        FCurrentY = y;
        if ( FBoundsManager != null ){
          FIsNeedToResize = FIsNeedToResize | FBoundsManager.NewPoint( x, y );
        }
        return true;
      }
      return false;
    }

    public void AddPoint( double x, double y ) throws ModelException {
      CurvePoint point = new CurvePoint( x, y );
      FPointList.add( point );
    }

    public Number GetNumberX( int index ){
      if ( index >=  FPointList.size()){
        IllegalArgumentException ex = new IllegalArgumentException();
        throw ex;
      }
      CurvePoint point = (CurvePoint) FPointList.get( index );
      return point.GetNumberX();
    }

    public Number GetNumberY( int index ){
      if ( index >=  FPointList.size()){
        IllegalArgumentException ex = new IllegalArgumentException();
        throw ex;
      }
      CurvePoint point = (CurvePoint) FPointList.get( index );
      return point.GetNumberY();
    }

    public double GetX( int index ){
      if ( index >=  FPointList.size() && index != 0 ){
        IllegalArgumentException ex = new IllegalArgumentException();
        throw ex;
      }
      if ( index ==  FPointList.size() && index == 0 ){
        return 0;
      }
      CurvePoint point = (CurvePoint) FPointList.get( index );
      return point.GetX();
    }

    public double GetY( int index ){
      if ( index >=  FPointList.size()){
        IllegalArgumentException ex = new IllegalArgumentException();
        throw ex;
      }
      CurvePoint point = (CurvePoint) FPointList.get( index );
      return point.GetY();
    }

    public int GetPointCount(){
      return FPointList.size();
    }

    public void DeleteRedundandPoints(){
      int i = 0;
      CurvePoint point;
      while ( i <  FPointList.size()){
        point = (CurvePoint) FPointList.get( i );
        if (FBoundsManager.IsNeedToDeletePoint( point.GetX(), point.GetY() )) {
          FPointList.remove( i );
        } else{
          i++;
        }
      }
    }

  }//class JFreeCurve

  private class JFreeArrayCurve extends JFreeCurve{
    private ModelAddress FArrayAddress = null;

    public JFreeArrayCurve(ModelGUICurveDescr aCurveDescr) {
      super(aCurveDescr);
      FArrayAddress = aCurveDescr.ArrayAddress;
    }

    public boolean UpdateCurve() throws ModelException{
      FPointList.clear();
      int index = 0;
      double value;
      int[] coord = new int[1];
      int length = FCurveDescr.Connector.GetArrayDimensionLength( FArrayAddress, 0 );
      while ( index < length ){
        coord[0] = index;
        value = FCurveDescr.Connector.GetArrayValue( FArrayAddress, coord );
        AddPoint( index, value );
        index++;
      }
      return true;
    }

  } // JFreeArrayCurve

  private class CurvePoint{
    private double x;
    private double y;
    private CurveCoordinateValue xNumber;
    private CurveCoordinateValue yNumber;

    public CurvePoint( double aX, double aY ){
      xNumber = new CurveCoordinateValue( aX );
      yNumber = new CurveCoordinateValue( aY );
      x = aX;
      y = aY;
    }

    protected double GetX(){
      return x;
    }

    protected double GetY(){
      return y;
    }

    protected Number GetNumberX(){
      return xNumber;
    }

    protected Number GetNumberY(){
      return yNumber;
    }

  }



  private class CurveCoordinateValue extends Number{
    private double FValue;

    public CurveCoordinateValue( double value ){
      FValue = value;
    }

    public int intValue() {
      return (int) FValue;
    }

    public long longValue() {
      return (long) FValue;
    }

    public float floatValue() {
      return (float) FValue;
    }

    public double doubleValue() {
      return FValue;
    }
  }

}

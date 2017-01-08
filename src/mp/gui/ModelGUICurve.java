package mp.gui;

import mp.elements.ModelException;
import mp.elements.ModelAddress;
import mp.elements.ModelConnector;
import jcckit.data.DataPoint;

/**
 * User: Администратор
 * Date: 30.11.2008
 */
public abstract class ModelGUICurve {
  protected ModelAddress FAddressX = null;
  protected ModelAddress FAddressY = null;
  protected ModelConnector FConnector = null;
  private double FCurrentX = 0;
  private double FCurrentY = 0;
  private double FMinX = 0;
  private double FMaxX = 100;
  private double FMinY = 0;
  private double FMaxY = 100;
  private double FXIncrement = 10;
  private double FYIncrement = 10;

  public ModelGUICurve(ModelAddress aAddressY, ModelAddress aAddressX, ModelConnector aConnector) {
    FAddressY = aAddressY;
    FAddressX = aAddressX;
    FConnector = aConnector;
  }

  private double GetX() throws ModelException {
    return FConnector.GetValue( FAddressX );
  }

  private double GetY() throws ModelException {
    return FConnector.GetValue( FAddressY );
  }

  protected abstract void AddNewPoint(double x, double y);

  private void SetNewBoard(){
    //Проверяем, вышел ли Y за границы
    if ( FCurrentY < FMinY ) {
      FMinY = FMinY - FYIncrement;
     // FMaxY = FMaxY - FYIncrement;
    }
    if ( FCurrentY > FMaxY ){
      FMaxY = FMaxY + FYIncrement;
      //FMinY = FMinY + FYIncrement;
    }
    //то же самое делаем с X
    if ( FCurrentX < FMinX ){
      FMinX = FMinX - FXIncrement;
      FMaxX = FMaxX - FXIncrement;
    }
    if ( FCurrentX > FMaxX ){
      FMaxX = FMaxX + FXIncrement;
      FMinX = FMinX + FXIncrement;
    }
  }

  public void Update() throws ModelException {
    double newX = GetX();
    double newY = GetY();
    if ( Double.compare(newX,FCurrentX) !=0 || Double.compare( newY, FCurrentY ) != 0 ){
      FCurrentX = newX;
      FCurrentY = newY;
      AddNewPoint( FCurrentX, FCurrentY );
      SetNewBoard();
    }
  }

  public double GetMinX() {
    return FMinX;
  }

  public void SetMinX(double aMinX) {
    FMinX = aMinX;
  }

  public double GetMaxX() {
    return FMaxX;
  }

  public void SetMaxX(double aMaxX) {
    this.FMaxX = aMaxX;
  }

  public double GetMinY() {
    return FMinY;
  }

  public void SetMinY(double aMinY) {
    this.FMinY = aMinY;
  }

  public double GetMaxY() {
    return FMaxY;
  }

  public void SetMaxY(double aMaxY) {
    this.FMaxY = aMaxY;
  }

  public double GetXIncrement() {
    return FXIncrement;
  }

  public void SetXIncrement(double aXIncrement) {
    this.FXIncrement = aXIncrement;
  }

  public double GetYIncrement() {
    return FYIncrement;
  }

  public void SetYIncrement(double aYIncrement) {
    this.FYIncrement = aYIncrement;
  }

  protected boolean IsNeedToDel( double x, double y ){
    if ( x > FMaxX ){
      return true;
    }
    if ( x < FMinX ){
      return true;
    }
    if ( y > FMaxY ){
      return true;
    }
    if ( y < FMinY ){
      return true;
    }
    return false;
  }
}

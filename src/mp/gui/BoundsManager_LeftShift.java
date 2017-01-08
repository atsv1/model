package mp.gui;

import java.awt.*;

/**
 * User: Администратор
 * Date: 14.12.2008
 */
public class BoundsManager_LeftShift implements GraphBoundsManager {
  private double FMinX = 0;
  private double FMinY = 0;
  private double FMaxX = 100;
  private double FMaxY = 100;
  private double FXIncrement = 10;
  private double FYIncrement = 10;
  private int FXDistance = 100;

  public void SetInitBounds(Rectangle aRectangle) {
    if ( aRectangle == null ){
      return;
    }
    FMinX = aRectangle.x;
    FMaxX = aRectangle.x + aRectangle.width;
    FMinY = aRectangle.y;
    FMaxY = FMinY + aRectangle.height;
    FXDistance = (int)(FMaxX - FMinX);
  }

  public void SetIncrements(double xIncrement, double yIncrement) {
    FXIncrement = xIncrement;
    FYIncrement = yIncrement;
  }

  public boolean NewPoint(double x, double y) {
    boolean isNeedToResize = false;
    if ( x >= FMaxX - ( FXDistance*0.05) ){
      isNeedToResize = true;
      FMaxX = Math.max( x +(FXDistance* 0.05), FMaxX + FXIncrement );
      FMinX = FMaxX - FXDistance;
    }
    if ( x <= FMinX ){
      FMinX = FMinX - FXIncrement;
      FXDistance = FXDistance + (int)FXIncrement;
    }
    if ( y >= FMaxY*0.95 ) {
      isNeedToResize = true;
      FMaxY = Math.max( y*1.05, FMaxY +  FYIncrement);
    }
    if ( y < FMinY * 0.95 ){
      isNeedToResize = true;
      FMinY = Math.min( y, FMinY - FYIncrement );
    }
    return isNeedToResize;
  }

  public Rectangle GetGraphBounds() {
    Rectangle res = new Rectangle(  (int)FMinX, (int)FMinY, (int)(FMaxX - FMinX), (int)(FMaxY - FMinY) );
    return res;
  }

  public boolean IsNeedToDeletePoint(double x, double y) {
    if ( x < FMinX ){
      return true;
    }
    return false;
  }


}

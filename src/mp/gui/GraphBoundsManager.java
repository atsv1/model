package mp.gui;

import java.awt.*;

/**
 * User: Администратор
 * Date: 08.12.2008
 *
 * Интерфейс предназначен для управления границами, в которых прорисовывается график.  
 *
 *
 */


public interface GraphBoundsManager {

  public void SetInitBounds( Rectangle aRectangle );

  public void SetIncrements( double xIncrement, double yIncrement );

  public boolean NewPoint( double x, double y );

  public Rectangle GetGraphBounds();

  public boolean IsNeedToDeletePoint( double x, double y );

}

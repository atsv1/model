package mp.gui;

import java.awt.*;

/**
 * User: �������������
 * Date: 08.12.2008
 *
 * ��������� ������������ ��� ���������� ���������, � ������� ��������������� ������.  
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

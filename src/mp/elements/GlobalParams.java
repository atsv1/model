package mp.elements;

import java.awt.*;

/**
 * User: atsv
 * Date: 22.12.2006
 *
 * ����� ������������ ��� �������� ����������� ���������� ��� ��������� ������.
 * @noinspection ALL
 */
public  class GlobalParams {

  /**����� ���������� �������� �����, ������������ �������� � Java-������� ����� ���������� ������� ��������. ���
   * "��������" ���������� ���������� �������� ���������� ������ ModelTime 
   * @return - ���������� true, ���� �������� ������ ������ �������� ����� ������ ���������� �������� ���������� �������.
   */
  public static boolean ExecTimeOutputEnabled(){
    /*Color c = Color.RED;
    System.out.println( Integer.toString( c.getRGB()  ));*/
    return false;
  }

  /** ����� ���������� �������� �����, ������������ �������� � Java-������� �������� ������� ��������� ��� �����������
   * @return ���������� true, ���� ����� �������� �������� ���������
   */
  public static boolean StateNameOutputEnabled(){
    return false;
  }

  public static boolean MuxOutputEnabled(){
    return false;
  }

}

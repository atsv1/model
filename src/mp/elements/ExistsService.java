package mp.elements;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * User: �������������
 * Date: 31.05.2008
 */

class ExistsService {
  private int[] FEsistsArray = null;
  private int FMinElementId;
  private int[] FCoordArray = new int[3];

  public ExistsService( int minElementId, int maxElementId ){
    FEsistsArray = new int[ ((maxElementId - minElementId + 1) >>> 5) + 1 ];
    FMinElementId = minElementId;
  }

  /** ���������� ��������� �������� � ������� ������
   *
   * @param aElementId - ������������� ��������
   * 0-� ���� ������� ����� ��������� ����� �����
   * 1-� ������� ������� ����� ��������� ��������  ������ �����
   * 2-� ������� ������� ����� ��������� �����
   */
  private void GetCoord(int aElementId) throws ModelException{
    int byteNum = (aElementId - FMinElementId) >>> 5;
    int shift = aElementId - ( byteNum << 5 ) - FMinElementId;
    FCoordArray[0] = byteNum;
    FCoordArray[1] = shift;
    FCoordArray[2] = 1 << shift;
  }

  /** ����� ������������ ��� ���������� ���������� � "�����������" �������� � ������
   *
   * @param aElementId - ������������� ��������
   * @throws ModelException
   */
  public void ElementExists( int aElementId ) throws ModelException {
    GetCoord( aElementId );
    int byteNum = FCoordArray[0];
    if ( byteNum >= FEsistsArray.length ) {
      ModelException e = new ModelException("������ ��� ���������� ����� � ������ ����������� � ������ ����������");
      throw e;
    }
    int mask = FCoordArray[ 2 ];
    FEsistsArray[ byteNum ] = FEsistsArray[ byteNum ] | mask;
  }

  /**����� ������������ ��� ���������� ���������� � ���, ��� ������� �� ������������ � ������
   *
   * @param aElementId - ������������� ��������
   * @throws ModelException
   */
  public void ElementNotExists( int aElementId ) throws ModelException {
    GetCoord( aElementId );
    int byteNum = FCoordArray[0];
    if ( byteNum >= FEsistsArray.length ) {
      ModelException e = new ModelException("������ ��� ���������� ����� � ������ ����������� � ������ ����������");
      throw e;
    }
    //System.out.println( Integer.toBinaryString( FCoordArray[ 2 ] ) );
    int mask = ~FCoordArray[ 2 ]; //����������� �����
    //System.out.println( Integer.toBinaryString( mask ) );
    //System.out.println( Integer.toBinaryString( FEsistsArray[ byteNum ] ) );
    FEsistsArray[ byteNum ] = FEsistsArray[ byteNum ] & mask;
    //System.out.println( Integer.toBinaryString( FEsistsArray[ byteNum ] ) );
  }

  /**  ������� ������������� ��� ���������, "������������" �� ��������� ������� � ������
   *
   * @param aElementId - ������������� ��������
   * @return  true - ���� ��������� ������� "������������" � �������.
   * @throws ModelException
   */
  public boolean IsExistsInList( int aElementId) throws ModelException{
    GetCoord( aElementId );
    int byteNum = FCoordArray[0];
    if ( byteNum >= FEsistsArray.length ) {
      ModelException e = new ModelException("������ ��� ���������� ����� � ������ ����������� � ������ ����������");
      throw e;
    }
    int mask = FCoordArray[ 2 ];
    int flag = FEsistsArray[byteNum] & mask;
    return flag != 0;
  }

  public void SetToInitState(){
    Arrays.fill( FEsistsArray, 0 );
  }

}

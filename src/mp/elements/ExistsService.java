package mp.elements;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * User: Администратор
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

  /** Вычисление координат элемента в массиве флагов
   *
   * @param aElementId - идентификатор элемента
   * 0-й байт массива будет содержать номер байта
   * 1-й элемент массива будет содержать смещение  внутри байта
   * 2-й элемент массива будет содержать маску
   */
  private void GetCoord(int aElementId) throws ModelException{
    int byteNum = (aElementId - FMinElementId) >>> 5;
    int shift = aElementId - ( byteNum << 5 ) - FMinElementId;
    FCoordArray[0] = byteNum;
    FCoordArray[1] = shift;
    FCoordArray[2] = 1 << shift;
  }

  /** Метод предназначен для сохранении информации о "присутствии" элемента в списке
   *
   * @param aElementId - идентификатор элемента
   * @throws ModelException
   */
  public void ElementExists( int aElementId ) throws ModelException {
    GetCoord( aElementId );
    int byteNum = FCoordArray[0];
    if ( byteNum >= FEsistsArray.length ) {
      ModelException e = new ModelException("Ошибка при вычислении байта с флагом присутствия в списке выполнения");
      throw e;
    }
    int mask = FCoordArray[ 2 ];
    FEsistsArray[ byteNum ] = FEsistsArray[ byteNum ] | mask;
  }

  /**Метод предназначен для сохранения информации о том, что элемент не присутствует в списке
   *
   * @param aElementId - идентификатор элемента
   * @throws ModelException
   */
  public void ElementNotExists( int aElementId ) throws ModelException {
    GetCoord( aElementId );
    int byteNum = FCoordArray[0];
    if ( byteNum >= FEsistsArray.length ) {
      ModelException e = new ModelException("Ошибка при вычислении байта с флагом присутствия в списке выполнения");
      throw e;
    }
    //System.out.println( Integer.toBinaryString( FCoordArray[ 2 ] ) );
    int mask = ~FCoordArray[ 2 ]; //инвертируем маску
    //System.out.println( Integer.toBinaryString( mask ) );
    //System.out.println( Integer.toBinaryString( FEsistsArray[ byteNum ] ) );
    FEsistsArray[ byteNum ] = FEsistsArray[ byteNum ] & mask;
    //System.out.println( Integer.toBinaryString( FEsistsArray[ byteNum ] ) );
  }

  /**  Функция предназначена для выяснения, "присутствует" ли указанный элемент в списке
   *
   * @param aElementId - идентификатор элемента
   * @return  true - если указанный элемент "присутствует" в массиве.
   * @throws ModelException
   */
  public boolean IsExistsInList( int aElementId) throws ModelException{
    GetCoord( aElementId );
    int byteNum = FCoordArray[0];
    if ( byteNum >= FEsistsArray.length ) {
      ModelException e = new ModelException("Ошибка при вычислении байта с флагом присутствия в списке выполнения");
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

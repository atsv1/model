package mp.elements;

/**
 * ����� ������������ ��� �������� ������ ���������, ������� ����� ��������� -
 * �.�. ������ ���������, ��� ������� ����� ��������� ����� Update() (��� Execute())
 * 
 * User: atsv
 * Date: 16.09.2007
 */
public class ModelExecuteList {
  public static int LIST_TYPE_FULL = 1; //���� ��������� �� ��, ��� ����� ������������ ������ ������
  public static int LIST_TYPE_PART = 2; //���� ��������� �� ��, ��� ����� ������������ ������ � ������� CurrentCycleList
  public int CurrentListType = -1;

  private ModelElementContainer FList = null;
  /**������ ���������, ������� ����� ��������� � ������� �����. �� �������� ��������� ��� ����������� ������� ��������
   * ������� � ����
   */
  protected ModelElement[] CurrentCycleList = null;
  /**��������� �� ��������� ����������� ������ � ������� CurrentCycleList. ���� �������� ����� -1, ��  ��� ��������,
   * ��� ������ ���������� ����
   */
  protected int CurrentCyclePointer = -1;
  /** ������ ��������, ������� ����� ����������� � ��������� �����
   */
  protected ModelElement[] NextCycleList = null;
  protected int NextCyclePointer = -1;
  private int[] FCurrentListFlags = null;
  private int[] FNextListFlags = null;
  private int FMinElementId = 0;
  private int FMaxElementId = 0;
  private int[] FCoordArray = new int[3];

  private static int GetMinElementId( ModelElementContainer aList ){
    int i = 0;
    int result = Integer.MAX_VALUE;
    ModelElement currentElement = null;
    while ( i < aList.size() ){
      currentElement = aList.get( i );
      if ( result > currentElement.GetElementId() ){
        result = currentElement.GetElementId(); 
      }
      i++;
    }
    return result;
  }

  private static int GetMaxElementId( ModelElementContainer aList ){
    int i = 0;
    int result = Integer.MIN_VALUE;
    ModelElement currentElement = null;
    while ( i < aList.size() ){
      currentElement = aList.get( i );
      if ( result < currentElement.GetElementId() ){
        result = currentElement.GetElementId();
      }
      i++;
    }
    return result;
  }

  public ModelExecuteList( ModelElementContainer aList ) throws ModelException{
    if ( aList == null ){
      ModelException e = new ModelException("������� ������� ������ �������������������������� � ������ ������ ���������");
      throw e;
    }
    FList = aList;
    CurrentCycleList = new ModelElement[ FList.size() ];
    NextCycleList = new ModelElement[ FList.size() ];
    CurrentListType = LIST_TYPE_FULL;
    FMinElementId = GetMinElementId( FList );
    FMaxElementId = GetMaxElementId( FList );
    FCurrentListFlags = new int[((FMaxElementId - FMinElementId + 1) >>> 5) + 1 ];
    FNextListFlags = new int[ FCurrentListFlags.length ];

  }

  /** ���������� ��������� �������� � ������� ������
   *
   * @param aElementId - ������������� ��������
   * @param aCoordArray - ������ � ������������. ����������� ������ ������.
   * 0-� ���� ������� ����� ��������� ����� �����
   * 1-� ������� ������� ����� ��������� ��������  ������ �����
   * 2-� ������� ������� ����� ��������� �����
   */
  private void GetCoord(int aElementId, int[] aCoordArray) throws ModelException{
    int byteNum = (aElementId - FMinElementId) >>> 5;
    int shift = aElementId - ( byteNum << 5 ) - FMinElementId;
    aCoordArray[0] = byteNum;
    aCoordArray[1] = shift;
    aCoordArray[2] = 1 << shift;
  }

  private boolean IsExistsInList( ModelElement aElement, int[] aFlagList) throws ModelException{
    int elementId = aElement.GetElementId();
    GetCoord( elementId, FCoordArray );
    int byteNum = FCoordArray[0];
    if ( byteNum >= aFlagList.length ) {
      ModelException e = new ModelException("������ ��� ���������� ����� � ������ ����������� � ������ ����������");
      throw e;
    }
    int mask = FCoordArray[ 2 ];
    int flag = aFlagList[byteNum] & mask;
    return flag != 0;
  }

  private void SetExistFlag( ModelElement aElement, int[] aFlagList ) throws ModelException{
    int elementId = aElement.GetElementId();
    GetCoord( elementId, FCoordArray );
    int byteNum = FCoordArray[0];
    if ( byteNum >= aFlagList.length ) {
      ModelException e = new ModelException("������ ��� ���������� ����� � ������ ����������� � ������ ����������");
      throw e;
    }
    int mask = FCoordArray[ 2 ];
    aFlagList[ byteNum ] = aFlagList[ byteNum ] | mask; 
  }

  /**
   * 
   * @param aList
   * @param aListPointer
   * @return true, ���� � ������ �� ����� ������ ��������� ����� ��������
   */
  private static boolean IsListFull( ModelElement[] aList, int aListPointer ){
    return (aList.length < aListPointer - 1);
  }

  private void PrintErrorLog( ModelElement[] aList, int[] aFlagList, int aPointer) throws ModelException {
    int i = 0;
    ModelElement element = null;
    while ( i < aPointer ){
      element = aList[ i ];
      if ( !IsExistsInList(element, aFlagList) ) {
        System.out.println( "error " + element.toString() );
        SetExistFlag( element, aFlagList );
        if ( !IsExistsInList(element, aFlagList) ) {
          GetCoord( element.GetElementId(), FCoordArray );
          System.out.println( "  error not deleted" + element.toString() +
             " byteNum = " + Integer.toString( FCoordArray[0] ) +
             " binMask = " + Integer.toBinaryString( FCoordArray[2] ) + 
             " shift = "+ Integer.toString( FCoordArray[1]) +
             " byteValue = " + Integer.toBinaryString( aFlagList[ FCoordArray[0] ] )
          );
        }
      }
      i++;
    }

  }

  public void AddToExecuteList( ModelElement aElement ) throws ModelException {
    if ( aElement == null ){
      return;
    }

    if ( CurrentListType == LIST_TYPE_PART && !IsExistsInList(aElement, FCurrentListFlags) ){
      if ( (CurrentCyclePointer+1) >= CurrentCycleList.length ){
        PrintErrorLog( CurrentCycleList, FCurrentListFlags, CurrentCyclePointer );
        System.out.println( aElement.toString() );
        if ( !IsExistsInList(aElement, FCurrentListFlags) ){
          System.out.println( aElement.toString() );
        }
      }
      // ������ �������� ��� ��� � ����������� ������
      CurrentCyclePointer++;
      CurrentCycleList[CurrentCyclePointer] = aElement;
      SetExistFlag( aElement, FCurrentListFlags );
      return;
    }
    if ( !IsListFull(NextCycleList, NextCyclePointer) ){
      if ( !IsExistsInList(aElement, FNextListFlags) ){
        //������ �������� ��� � ������ ���������� �� ��������� ����
        NextCyclePointer++;
        NextCycleList[NextCyclePointer] = aElement;
        SetExistFlag( aElement, FNextListFlags );
      }
    } 
  }

  private static void SetZeroToFlagList( int[] aFlagList ){
    int i = 0;
    while ( i < aFlagList.length ){
      aFlagList[i] = 0;
      i++;
    }

  }

  public void FinishCurrentCycle(){
    ModelElement[] tempList = CurrentCycleList;
    CurrentCycleList = NextCycleList;
    CurrentCyclePointer = NextCyclePointer;
    NextCycleList = tempList;
    NextCyclePointer = -1;
    int[] tempFlagList = FCurrentListFlags;
    FCurrentListFlags = FNextListFlags;
    FNextListFlags = tempFlagList;
    SetZeroToFlagList( FNextListFlags );
    if ( IsListFull(CurrentCycleList, CurrentCyclePointer) ){
      CurrentListType = LIST_TYPE_FULL;
    } else {
      CurrentListType = LIST_TYPE_PART;
    }
    //System.out.println("Finish " + Integer.toString( CurrentListType ) );
  }
  

}

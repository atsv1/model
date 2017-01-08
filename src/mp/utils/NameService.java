package mp.utils;

import java.util.Hashtable;
import java.util.Vector;

/**
 * ����� ������������ ��� �������� ��������. ����� ����� �������������� ��
 * ���� ��������� ������, ������� ������ �������� ����������� ������. 
 * �������� ������� ��������� ����� ������ - � ������ ��������� ����� ���������, ��������� ����� � ��� �� ������.
 * ������ ������� - ��, ��� ����� ������� �������� �� ��������� �������� ���������.
 * User: atsv
 * Date: 07.11.2007
 */
public class NameService {
  private Hashtable FNamesTable = null;
  private Vector FNamesList = null;
  private int FCurrentIndex = 0;

  protected NameService(){
    FNamesTable = new Hashtable(500);
    FNamesList = new Vector( 500 );
  }

  private int AddNewName( String aName ){
    NameRecord rec = new NameRecord();
    rec.FName = aName;
    FNamesList.add( rec );
    rec.FNameIndex =  FCurrentIndex ;
    FCurrentIndex++;
    rec.FUpperName = aName.toUpperCase();
    FNamesTable.put( rec.FUpperName, rec );
    return rec.FNameIndex;
  }

  /** ������� ���������� ������ ����������� �����. �� ����� ������� ����� ������� ������ ����� �������� ���� ���,
   * ��� � ������� �������� � ��.
   *
   * @param aName ���, ��� �������� ����� ������� ������. ���� ������ ����� ��� �� ����������������, �� ���
   * �������������� � ��������� ���� ������, ������� � ������������.
   * @return ������������ ���� ���������� ���� �����, ���� -1, ���� ������ ����� ������� null
   */
  public int GetNameIndex( String aName ){
    if ( aName == null ){
      return -1;
    }
    NameRecord rec = (NameRecord) FNamesTable.get( aName.toUpperCase() );
    if ( rec == null ) {
      return AddNewName( aName );
    }
    return rec.FNameIndex;
  }
  
  public String GetName( int aNameIndex ){
    if ( aNameIndex > FCurrentIndex || aNameIndex == -1){
      return null;
    }
    NameRecord rec = (NameRecord) FNamesList.get( aNameIndex );
    if ( rec == null ){
      return null;
    }
    return rec.FName;
  }


  private static class NameRecord{
    String FName = null;
    int FNameIndex = 0;
    String FUpperName = null;
  }

}

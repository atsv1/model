package mp.utils;

import java.util.Hashtable;
import java.util.Vector;

/**
 *  ласс предназначен дл€ хранени€ названий.  ласс будет использоватьс€ во
 * всех элементах модели, которые должны обладать собственным именем. 
 * ќсновна€ причина по€влени€ этого класса - в модели создаетс€ много элементов, обладющих одним и тем же именем.
 * ƒруга€ причина - то, что много времени тратитс€ на сравнение названий элементов.
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

  /** ‘ункци€ возвращает индекс переданного имени. ѕо этому индексу любой элемент модели может получить свое им€,
   * им€ в верхнем регистре и пр.
   *
   * @param aName им€, дл€ которого нужно вернуть индекс. ≈сли такого имени еще не зарегистрировано, то им€
   * регистрируетс€ и создаетс€ ключ записи, который и возвращаетс€.
   * @return возвращаетс€ либо уникальный ключ имени, либо -1, если вместо имени передан null
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

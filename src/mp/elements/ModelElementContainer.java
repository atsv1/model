package mp.elements;

import java.util.Hashtable;
import java.util.Vector;

/**
 * User: atsv
 * Date: 15.09.2006
 */
public class ModelElementContainer {

  private Hashtable<String, ModelElement> FHashByName = null;
  private Hashtable FHashById = null;
  private Hashtable FHashByNameIndex = null;
  private boolean FNameUnique = true;
  protected Vector<ModelElement> ElementList = null;


  public ModelElementContainer() {
    FHashByName = new  Hashtable();
    FHashById = new Hashtable();
    ElementList = new Vector<ModelElement> ();
    FHashByNameIndex = new Hashtable();
  }

  /**Параметр указывает, нужно ли производить проверку  на уникальность имени хранимого элемента. "Да" означает,
   * что имена всех элементов уникальны, и это значение по умолчанию. "Нет" - означает, что к контейнере теперь
   * могут храниться элементы с одинаковыми именами.
   * @param aFlagValue
   */
  public void SetUniqueNameFlag(boolean aFlagValue){
    FNameUnique = aFlagValue;
  }

  protected void CheckBeforeAdd(ModelElement aElement) throws ModelException{
    if ( aElement == null ) {
      ModelException e = new ModelException("Попытка добавить пустой элемент в список элементов модели");
      throw e;
    }
    ModelElement element;
    if ( FNameUnique ){
      element = (ModelElement) FHashByNameIndex.get( aElement.GetNameIndexObj() );
      if ( element != null && element != aElement){
        ModelException e;
        e = new ModelException("Элемент с таким названием уже присутсвует в списке: " + aElement.GetName());
        throw e;
      }
    }
    Integer i = new Integer( aElement.GetElementId() );
    element = (ModelElement) FHashById.get( i );
    if ( element != null && element != aElement){
      ModelException e;
      e = new ModelException("Элемент с таким идентификатором уже присутсвует в списке");
      throw e;
    }

  }

  private boolean CheckEqualsCount() {
    int nameCount = FHashByName.size();
    int idCount = FHashById.size();
    int count = ElementList.size();
    int nameIndexCount = FHashByNameIndex.size();
    if ( (count * 4) != (nameCount + idCount + count + nameIndexCount)  ){
      return false;
    }
    return true;
  }

  protected void AddById( ModelElement aElement){
    Integer i = new Integer( aElement.GetElementId() );
    FHashById.put( i, aElement );
  }

  protected void AddByName(ModelElement aElement){
    FHashByName.put( aElement.GetName().toUpperCase(), aElement );
  }

  protected void AddToVector(ModelElement aElement){
    ElementList.add( aElement );
  }

  protected void AddByNameIndex( ModelElement aElement ){
    FHashByNameIndex.put( aElement.GetNameIndexObj(), aElement );
  }

  public void AddElement( ModelElement aElement ) throws ModelException {
    CheckBeforeAdd( aElement );
    if ( !CheckEqualsCount() ){
    	//throw new ModelException( "Ошибка: несовпадение количеств в контейнере при добавлении элемента \"" +  aElement.GetFullName() + "\"");
      
    }
    if (aElement.GetName().indexOf("skipFirst") >= 0) {
    	throw new ModelException("skipFirst");
    }
    AddByName( aElement );
    AddById( aElement );
    AddToVector( aElement );
    AddByNameIndex( aElement );
  }

  /**Возвращает элемент по его названию
   *
   * @param aName название элемента
   * @return найденный элемент, либо null, если элемент не найден
   */
  public ModelElement Get( String aName ){
    ModelElement result = null;
    if ( aName == null ){
      return null;
    }
    result = (ModelElement)FHashByName.get( aName.toUpperCase() );
    return result;
  }

  public ModelElement GetByNameIndex( Integer aNameIndex ){
    if ( aNameIndex == null ){
      return null;
    }
    return (ModelElement) FHashByNameIndex.get( aNameIndex );
  }

  /**Возвращает элемент по его уникальному идентификатору.
   *
   * @param aElementId уникальный идентификатор элемента
   * @return найденный элемент, либо null, если элемент не найден
   */
  public ModelElement Get(int aElementId){
    Integer i = new Integer( aElementId );
    return (ModelElement)FHashById.get( i );
  }

  public int size()  {
    return ElementList.size();
  }

  /**Возвращает элемент по его порядковому номеру. Данная функция может использоваться для полного перебора
   * всех элементов
   * @param aIndex  - порядковый номер элемента
   * @return найденный элемент, либо null, если элемент не найден
   */
  public ModelElement get(int aIndex){
    return (ModelElement) ElementList.get(aIndex);
  }

  public Object clone(){
    ModelElementContainer container = new ModelElementContainer();
    container.FHashById = (Hashtable) this.FHashById.clone();
    container.FHashByName = (Hashtable) this.FHashByName.clone();
    container.ElementList = (Vector) this.ElementList.clone();
    container.FHashByNameIndex = (Hashtable) this.FHashByNameIndex.clone();
    return container;
  }

  public void Clear(){
    FHashByName.clear();
    FHashById.clear();
    ElementList.removeAllElements();
    FHashByNameIndex.clear();
  }

  public void RemoveElement( ModelElement element ) throws ModelException {
    if ( element == null ){
      return;
    }
    if ( !CheckEqualsCount() ){
      ModelException e = new ModelException( "Ошибка: несовпадение количеств в контейнере при удалении элемента \"" + element.GetFullName() + "\"");
      throw e;
    }
    FHashById.remove( new Integer(element.GetElementId()) );
    FHashByName.remove( element.GetName().toUpperCase() );
    ElementList.removeElement( element );
    FHashByNameIndex.remove( element.GetNameIndexObj() );
  }

}

package mp.elements;


/**
 * User: atsv
 * Date: 18.09.2006
 *  ласс предназначен дл€ создани€ в блоке списка параметров дл€ вычислени€. ѕараметры в списке должны идти по пор€дку
 * вызова их метода Update(). “.е. первыми должны идти параметры, которые не завис€т ни от каких параметров внутри
 * данного блока.
 * ѕри этом класс выполн€ет только определение параметров, которые наименьшим образом завис€т от тех параметров,
 * которые еще остались в списке необработанных параметров.
 */
public class ModelBlockParamsIterator extends ModelIterator {

  public ModelElementContainer FullParamsList = null;
  public ModelElementContainer ResultList = null;
  public  String BlockName = null;

  private ModelElementContainer TempList = null;
  //Ёта переменна€ определ€ет тип текущего поиска. ¬озможны следующие варианты:
  // 1 - перебор только ¬’ќƒЌџ’ параметров (не вычисл€емых, то есть)
  // 2 - поиск параметра, который не зависит от тех, которые остались в списке
  // 3 - поиск параметра, который наименьшим образом зависит от тех, которые остались в списке
  private int FCurrentSearchMode = -1;
  //”казывает на позицию, из которой произошел возврат значени€
  private int FCurrentPos = -1;

  private void PrepareProcess(){
    TempList = (ModelElementContainer) FullParamsList.clone();
    FCurrentSearchMode = 1;//начинаем с входных параметров блока
    FCurrentPos = 0;
  }

  private void Check() throws ModelException{
    ModelException e;
    if ( BlockName == null) {
      BlockName = "Ѕлок без имени";
    }
    if ( FullParamsList == null ) {
      e = new ModelException("ќшибка при создании пор€дкового списка вычислени€ параметров в блоке " + BlockName +
                             ": пустой список параметров");
      throw e;
    }
  }

  private ModelElement GetNextInpParam( ){
    ModelBlockParam element = null;
    Class c;
    while ( FCurrentPos < TempList.size() ){
      element = (ModelBlockParam) TempList.get( FCurrentPos );
      c = element.getClass();
      if ( ModelBlockParam.PLACEMENT_TYPE_INPUT == element.GetParamPlacementType() ){
        return element;
      }
      FCurrentPos++;
    }
    return null;
  }

  //удаление найденного элемента из временного списка
  private void DelCurrentElement() throws ModelException {
    //TempList.ElementList.removeElementAt( FCurrentPos );
    ModelElement element = (ModelElement) TempList.ElementList.get( FCurrentPos );
    TempList.RemoveElement( element );
  }

  private void DelCurrentElement( ModelElement element ) throws ModelException {
    //TempList.ElementList.removeElementAt( FCurrentPos );
    TempList.RemoveElement( element );
  }

  private int GetDependCount( ModelElement aElement, int maxCount ){
    int count = 0;
    int i = 0;
    ModelBlockParam param;
    try{
      param = (ModelBlockParam)aElement;
    } catch (Exception e) {
      return 0;
    }
    ModelElement currentElement;
    while ( i < TempList.size() ){
      currentElement = TempList.get( i );
      if ( param.IsInputParam( currentElement ) ) {
        count++;
      }
      if ( count > maxCount ){
        return count;
      }
      i++;
    }
    return count;
  }

  /**
   * Ќахождение элемента, который бы не зависел ни от одного из элементов из списка
   */
  private ModelElement GetElementWithNoDependencies(){
    ModelElement element = null;
    while ( FCurrentPos < TempList.size() ){
      element = TempList.get( FCurrentPos );
      if ( GetDependCount( element,0 ) == 0){
        return element;
      }
      FCurrentPos++;
    }
    return null;
  }

  private ModelElement GetElementWithMinDependencies(){
    FCurrentPos = 0;
    ModelElement result = null;
    int resultCount = TempList.size()+1;
    ModelElement element;
    int currentCount = 0;
    while ( FCurrentPos < TempList.size() ){
      element = TempList.get( FCurrentPos );
      currentCount = GetDependCount( element, TempList.size() );
      if ( currentCount < resultCount ){
        result = element;
        resultCount = currentCount;
      }
      FCurrentPos++;
    }
    return result;
  }

  private ModelElement GetResult() throws ModelException {
    ModelElement result = null;
    switch ( FCurrentSearchMode ){
      case 1:{
        result = GetNextInpParam();
        if ( result == null ){
          FCurrentSearchMode = 2;
          FCurrentPos = 0;
          result = GetResult();
        } else {
          DelCurrentElement();
        }
        break;
      }
        case 2:{
          result = GetElementWithNoDependencies();
          if ( result == null ){
            FCurrentSearchMode = 3;
            FCurrentPos = 0;
            result = GetResult();
          } else{
            DelCurrentElement();
          }
          break;
        }
        case 3:{
          result = GetElementWithMinDependencies();
          if ( result != null ){
            FCurrentSearchMode = 2;
            DelCurrentElement( result );
            FCurrentPos = 0;
          }
          break;
        }
        default:{
          //ошибка
        }
    }//case
    return result;
  }

  public ModelElement First() throws ModelException {
    Check();
    PrepareProcess();
    return GetResult();
  }

  public ModelElement Next() throws ModelException {
    return GetResult();
  }
}

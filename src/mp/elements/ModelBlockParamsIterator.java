package mp.elements;


/**
 * User: atsv
 * Date: 18.09.2006
 * ����� ������������ ��� �������� � ����� ������ ���������� ��� ����������. ��������� � ������ ������ ���� �� �������
 * ������ �� ������ Update(). �.�. ������� ������ ���� ���������, ������� �� ������� �� �� ����� ���������� ������
 * ������� �����.
 * ��� ���� ����� ��������� ������ ����������� ����������, ������� ���������� ������� ������� �� ��� ����������,
 * ������� ��� �������� � ������ �������������� ����������.
 */
public class ModelBlockParamsIterator extends ModelIterator {

  public ModelElementContainer FullParamsList = null;
  public ModelElementContainer ResultList = null;
  public  String BlockName = null;

  private ModelElementContainer TempList = null;
  //��� ���������� ���������� ��� �������� ������. �������� ��������� ��������:
  // 1 - ������� ������ ������� ���������� (�� �����������, �� ����)
  // 2 - ����� ���������, ������� �� ������� �� ���, ������� �������� � ������
  // 3 - ����� ���������, ������� ���������� ������� ������� �� ���, ������� �������� � ������
  private int FCurrentSearchMode = -1;
  //��������� �� �������, �� ������� ��������� ������� ��������
  private int FCurrentPos = -1;

  private void PrepareProcess(){
    TempList = (ModelElementContainer) FullParamsList.clone();
    FCurrentSearchMode = 1;//�������� � ������� ���������� �����
    FCurrentPos = 0;
  }

  private void Check() throws ModelException{
    ModelException e;
    if ( BlockName == null) {
      BlockName = "���� ��� �����";
    }
    if ( FullParamsList == null ) {
      e = new ModelException("������ ��� �������� ����������� ������ ���������� ���������� � ����� " + BlockName +
                             ": ������ ������ ����������");
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

  //�������� ���������� �������� �� ���������� ������
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
   * ���������� ��������, ������� �� �� ������� �� �� ������ �� ��������� �� ������
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
          //������
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

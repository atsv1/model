package mp.elements;


import mp.parser.ScriptException;
import java.util.Vector;

/**
 * ����� ������������ ��� ���������� �������� ������� � ������.
 *  ��������� ����� ������ ���������� �� ��� �������� ������, ����������� ������� ���������� ���������.
 * ������ ������� ������ � ������������ ������ ��������� ���� � �������� �������.
 * �������� ������� (��� ���� ��� �����) �� ������� ������������ ������� �������� �������� ���������� �������, �
 * ������� ���� ������ ������ ���� ��������. � ������������ � ���� �������� ������ ���������� � ���� ������ - ������
 * ���������, ������� ������ ���� ��������� � ������ ������ �������
 * �������� ��������� ������� ����������� � ���� ��������� �����, ������� ������ ����� ������� �������� ������ �
 * ����������� ������ ���������, ������� ������ ���� ��������� � ������ ��������� �����.
 * ����� �������, ��� ������ ������� ������ ����������� ��������� ��������� ���������� ��������: ������ ���� �����������
 * ������ �����, ����� ����� �������� ��� �����. �.�. ���� �������� ����� ���������� �� ���������� ���� ������.
 *
 * User: atsv
 * Date: 08.10.2007
 */
public class ModelTimeManager {
  private Vector FGroupList = new Vector();
  private ModelTime FTimeStep = null;
  private ModelTime FTempTime1 = new ModelTime(0);
  private ModelTime FTempTime2 = new ModelTime(0);

  private ModelTime FTimeToExecFullList = null;
  private ExecuteGroup FFullGroup = null;

  private int FMaxElementsCount = 10;

  public static final int EXECUTE_GROUP_IS_FULL = 1;
  public static final int EXECUTE_GROUP_IS_NOT_FULL = 2;
  protected static final int ELEMENT_COUNT_LIMIT = 100;
  private ModelTime FLastExecTime = null;
  private ModelElementContainer FFullList = null;

  /**���� ����������, ��������� �� �������� ������� � ������ ���������� ���������, ��� ���.
   * �.�. true - ���������, false - �� ���������
   */
  private boolean FListExecuted = false;
  /**������ ������ � ������ ����� (FGroupList). ���������� ������, � ������� � ��������� ������ �����������
   * ��������, ������� �� ������� �� ������� (������� �� ����� ���������� ����� ������ ����������). ����� ��� ���������
   * ���������� ����� ���������.
   * ���� ������ ������ 1, �� ��� ��������, ��� ����� ������ ��� ���, ���� ��� ������ ��� ��� �����������, � ���������
   * �������� � ��� ��� ������
   */
  private int FTimeIndependentBlockGroupIndex = -1;

  public ModelTimeManager(){
    FTimeStep = new ModelTime(0.05);
  }

  public ModelTimeManager( ModelTime aStep ){
    FTimeStep = aStep;
    if ( FTimeStep == null ){
      FTimeStep = new ModelTime(0.05);
    }
  }

  private int GetExecuteGroup( ModelTime aModelTime, int aStartIndex ){
    ExecuteGroup result = null;
    ModelTime groupTime = null;
    int i = aStartIndex;
    while ( i < FGroupList.size() ){
      result = (ExecuteGroup) FGroupList.get( i );
      groupTime = result.FExecTime;
      if ( groupTime.Compare( aModelTime ) == ModelTime.TIME_COMPARE_EQUALS ){
        return i;
      }
      FTempTime1.StoreValue( groupTime );
      FTempTime1.Sub( FTimeStep );
      FTempTime2.StoreValue( groupTime );
      FTempTime2.Add( FTimeStep );
      if ( FTempTime1.Compare( aModelTime ) == ModelTime.TIME_COMPARE_LESS ) {
        // ���������� ����� ������, ��� ����� �������� ������
        if (FTempTime2.Compare( aModelTime ) == ModelTime.TIME_COMPARE_GREATER) {
          return i;
        }
      } else {
        // ���������� ����� ������, ��� ����� �������� ������. ��� ��������, ��� ��� ������ ������, � ��������
        // ����� ���������� ��������� �� � ���������� � ��������� ��������
        return -1;
      }

      i++;
    }
    return -1;
  }

  private int InsertExecGroupInList( ExecuteGroup aGroup, ModelTime aTime ){
    if ( FGroupList.size() == 0 ){
      FGroupList.add( aGroup );
      return 0;
    }
    int i = 0;
    ExecuteGroup group = null;
    while ( i < FGroupList.size() ){
      group = (ExecuteGroup) FGroupList.get( i );
      FTempTime1.StoreValue( group.FExecTime );
      if ( FTempTime1.Compare( aTime ) == ModelTime.TIME_COMPARE_GREATER) {
        // ���������� � ��������� ����� ������, ��� ����� ������� ������. ����� ��������� ����� ������
        // ����� ������� �������
        FGroupList.insertElementAt( aGroup, i );
        return i;
      }
      i++;
    }
    // ������ �� ��������� � ������. ��� ��������, ��� ����� ���������� ���� ������ - ����� ������� �� ������.
    // ������ ��������� ����� ������ � ����� ����� ������
    FGroupList.add( aGroup );
    return FGroupList.size() - 1;
  }

  private boolean IsTimeEqualsWithStep(ModelTime aBaseTime, ModelTime aTimeToCheck){
    FTempTime1.StoreValue( aBaseTime );
    FTempTime1.Sub( FTimeStep );
    if ( FTempTime1.Compare( aTimeToCheck ) == ModelTime.TIME_COMPARE_LESS){
      FTempTime1.StoreValue( aBaseTime );
      FTempTime1.Add( FTimeStep );
      if ( FTempTime1.Compare( aTimeToCheck ) == ModelTime.TIME_COMPARE_GREATER){
        return true;
      }
    }
    return false;
  }

  /** ������� ��������� ��������� ���� ������ � ������ ���� �������
   *
   * @param aTime1 1-� ������������ �����
   * @param aTime2 2-� ������������ �����
   * @return ���������� ModelTime.TIME_COMPARE_EQUALS, ���� ��� ��������� �����, ModelTime.TIME_COMPARE_LOW, ����
   * ������ �������� ������ ������� ���������, ModelTime.TIME_COMPARE_GREATER, ���� ������ �������� ������ �������
   */
  protected int CompareWithStep( ModelTime aTime1, ModelTime aTime2 ){
    FTempTime1.StoreValue( aTime1 );
    FTempTime1.Sub( FTimeStep );
    if ( FTempTime1.Compare( aTime2 ) == ModelTime.TIME_COMPARE_GREATER ) {
      return ModelTime.TIME_COMPARE_GREATER;
    }
    FTempTime1.StoreValue( aTime1 );
    FTempTime1.Add( FTimeStep );
    if ( FTempTime1.Compare( aTime2 ) == ModelTime.TIME_COMPARE_GREATER ) {
      return ModelTime.TIME_COMPARE_EQUALS;
    } else
    return ModelTime.TIME_COMPARE_LESS;
  }

  private void DeleteGroups( int aStartIndex ){
    while ( aStartIndex< FGroupList.size() ){
      FGroupList.removeElementAt( aStartIndex );
    }

  }

  /** ���������� �������� � �������� �������.
   *
   * @param aElement - �������, ������� ������ ���� ���������� � ������������ �����.
   */
  public int AddElement( ModelBlock aElement, ModelTime aExecTime ) throws ModelException{
    if ( aElement == null  ){
      ModelException e = new ModelException(" ������ ������� ��� ���������� � �������� �������");
      throw e;
    }
    if ( aExecTime == null ){
      AddElement( aElement );
      return -1;
    }
    if ( FTimeToExecFullList != null && IsTimeEqualsWithStep( FTimeToExecFullList, aExecTime ) ){
      //����� ���������� ������� ��������� � ������� ���������� ���� ���������. ������� ������������ ����������
      // ��� ����� ���������� - ������� �� ����� ���� ���������� ���������
      FFullGroup.AddElement( aElement );
      return FGroupList.indexOf( FFullGroup );
    } else {
      if ( FTimeToExecFullList != null && FTimeToExecFullList.Compare( aExecTime ) == ModelTime.TIME_COMPARE_LESS
              &&  FGroupList.indexOf( FFullGroup ) > 0
             ){
        // ����� ���������� ����� �������� ������, ��� ����� ���������� "������� ������", � ��� ���� ���� "������
        // ������" ����������� ��  ������.
        //  ������� ����� ������� ������ �� �����������
        return -1;
      }
    }
    int startSearchIndex = 0;
    if ( FListExecuted ) {
      startSearchIndex = 1;
    }
    int groupIndex = GetExecuteGroup( aExecTime, startSearchIndex );
    ExecuteGroup group;
    if ( groupIndex == -1 ){
      group = new ExecuteGroup( aExecTime );
      //FGroupList.add( group );
      groupIndex = InsertExecGroupInList( group, aExecTime );
    } else {
      group = (ExecuteGroup) FGroupList.get( groupIndex );
    }
    AddElementToGroup(aElement, group, groupIndex, aExecTime);
    return groupIndex;
  }

  private void AddElementToGroup(ModelBlock aElement, ExecuteGroup aGroup, int aGroupIndex, ModelTime aExecTime ){
    aGroup.AddElement( aElement );
    if ( aGroup.FillFlag == EXECUTE_GROUP_IS_FULL ){
      FTimeToExecFullList = new ModelTime( 0 );
      FTimeToExecFullList.Add( aExecTime );

      FFullGroup = aGroup;
      /**������� ����������� � ������, ������� �������� "������ �������".
       * ������� ����� ������� ��� ������, ������� ����� ����������� ����� ���. ��� ����, ���� ������, � �������
       * ����������� �������, �������� �������, �������� ����������� � ������� ������, �� �������� ��������� �����
       * �� ������������
       */
      if ( aGroupIndex == 0 ) {
        //DeleteGroups( aGroupIndex + 2 );
      } else {
        DeleteGroups( aGroupIndex + 1 );
      }
      if ( FTimeIndependentBlockGroupIndex > aGroupIndex ) {
        FTimeIndependentBlockGroupIndex = 0;
      }
    }

  }

  /**������� ���������� �����, ������� ������������� ���������� �������: ����� ����� ����� ������� ������ ���� ������,
   * ��� � �������, ����������� � ���������
   *
   * @param aTime - �����, ������ ��������
   * @return
   */
  protected static ModelTime GetExecTimeForTimeIndependentElement( ModelTime aTime ){
    if ( aTime == null ){
      return new ModelTime(1);
    }
    ModelTime newTime = new ModelTime(  );
    newTime.Add( aTime );
    newTime.Add( 1 );
    newTime.TruncateTime();
    return newTime;
  }

  private boolean IsAddEnabled( ModelTime aCurrentExecTime ){
    //��������� ������� "������� ������". ���� ����� ������ �������, �� �������� ��������� ��������:
      // 1. ����������� ������� ����������� �� ���������� "������� ������". � ����� ������ ���������� ������������
      //    ���� ������ �������.
      // 2. ����������� ������� ����������� ����� ������� ���������� "������� ������", � ��� ���� ������ ������ ���
      //    ��� �����������. � ���� ������ ���������� ������������ ��� ������ �����������.
      // 3. ����������� ������� ����������� ����� ������� ���������� "������� ������", � ��� ��� "������ ������" ���
      //    ����� �����������. � ����� ������ ���������� �� ������������
      if ( FTimeToExecFullList != null  // "������ ������" ������������
         ){
        if ( FGroupList.indexOf( FFullGroup ) == 0 &&
                ( CompareWithStep( FTimeToExecFullList, aCurrentExecTime ) == ModelTime.TIME_COMPARE_EQUALS ||
                 CompareWithStep( FTimeToExecFullList, aCurrentExecTime ) == ModelTime.TIME_COMPARE_GREATER )
           ){
          // "������ ������ ����", �� ����������� � ��������� ������, � �������� ������� �������� ������� ���� � ���
          // "������ ������", ���� � ������. ������� ����������� ������ ����� ������
          return false;
        }
        if ( FGroupList.indexOf( FFullGroup ) > 0 &&
                ( CompareWithStep( FTimeToExecFullList, aCurrentExecTime ) == ModelTime.TIME_COMPARE_EQUALS ||
                 CompareWithStep( FTimeToExecFullList, aCurrentExecTime ) == ModelTime.TIME_COMPARE_GREATER )
            ) {
          // "������ ������ ����", �� ��� ����� �����������. �������� ������� �������� ������� ���� � ����, ���� �
          // ������, ������� ����� ����������� ����� "������� ������"
          return false;
        }
      }
    return true;
  }

  private ModelTime GetMinimumTimeWithStep( ModelTime aTime1, ModelTime aTime2 ){
    if ( CompareWithStep( aTime1, aTime2 ) == ModelTime.TIME_COMPARE_LESS) {
      return aTime1;
    }
    return aTime2;
  }

  /**���������� �������� � �������� �������. ����� ������������ � ������, ���� ������� �� ����� ��������������
   * ���������� ����� ������ ����������.
   *
   * @param aElement - ����������� �������
   * @throws ModelException
   */
  public void AddElement( ModelBlock aElement ) throws ModelException{
    if ( FTimeIndependentBlockGroupIndex >= 0 ) {
      ExecuteGroup execGroup = (ExecuteGroup) FGroupList.get( FTimeIndependentBlockGroupIndex );
      AddElementToGroup( aElement, execGroup, FTimeIndependentBlockGroupIndex, execGroup.FExecTime );
      return;
    }
    ExecuteGroup currentExecGroup;
    ModelTime currentExecTime;
    //���������� ����� ���������� ������������ ��������
    if ( FGroupList.size() == 0 ){
      currentExecTime = new ModelTime( 0 );
      currentExecGroup = new ExecuteGroup( currentExecTime );
    } else {
      currentExecGroup = (ExecuteGroup) FGroupList.get( 0 );
      currentExecTime = currentExecGroup.FExecTime;
      if ( FLastExecTime != null ){
        if ( FGroupList.size() == 1 ) {
          currentExecTime = GetExecTimeForTimeIndependentElement( currentExecTime );
        } else {
           ModelTime t1 = GetExecTimeForTimeIndependentElement( currentExecTime );
           ModelTime t2 = GetExecTimeForTimeIndependentElement( FLastExecTime );
           currentExecTime = GetMinimumTimeWithStep( t1, t2 );
        }
        /*ModelTime t1 = GetExecTimeForTimeIndependentElement( currentExecTime );
        ModelTime t2 = GetExecTimeForTimeIndependentElement( FLastExecTime );
        currentExecTime = GetMinimumTimeWithStep( t1, t2 );*/

      } else {
        ModelTime t1 = GetExecTimeForTimeIndependentElement( currentExecTime );
        ModelTime t2 = GetExecTimeForTimeIndependentElement( new ModelTime( 0 ) );
        currentExecTime = GetMinimumTimeWithStep( t1, t2 );

      }
      if ( !IsAddEnabled( currentExecTime ) ){
        return;
      }

    }
    FTimeIndependentBlockGroupIndex = AddElement( aElement, currentExecTime );
  }

  public ModelTime GetNearestModelTime(){
    if ( FGroupList.size() == 0 ){
      return null;
    }
    ExecuteGroup group = (ExecuteGroup) FGroupList.get( 0 );
    if ( group == null ){
      return null;
    }
    return group.FExecTime;
  }

  protected int GetTimeIndependentBlockGroupIndex(){
    return FTimeIndependentBlockGroupIndex;
  }

  protected int GetExecuteGroupCount(){
    return FGroupList.size();
  }

  private void UpdateTimeGroups(ModelElementContainer elementList, boolean addFlag) throws ModelException{
  	int i = 0;
  	ModelElement element;
  	ModelBlock block;
  	ModelTime execTime;
    ModelTime currentTime = FLastExecTime != null ? FLastExecTime : new ModelTime(0);

  	while ( i < elementList.size() ){
  		element = elementList.get(i);
      if ( !(element instanceof ModelBlock) ) {
      	i++;
      	continue;
      }
      if ( addFlag ) {
      	FFullList.AddElement(element);
      }
      block = (ModelBlock) element;
      block.SetTimeManager( this );
      execTime = block.GetNearestEventTime( currentTime );
      if ( execTime == null ) {
        this.AddElement( block );
      } else {
        this.AddElement( block, execTime );
      }
      i++;
  	}
  }

  public void SetFullElementsList( ModelElementContainer aFullList ) throws ModelException{
    if ( aFullList == null || aFullList.size() == 0 ){
      ModelException e = new ModelException("������� ������ ������ ��������� � �������� �������");
      throw e;
    }
    if ( aFullList.size() <= ELEMENT_COUNT_LIMIT ) {
      FMaxElementsCount = aFullList.size();
    } else {
      FMaxElementsCount = aFullList.size() / 10;
    }
    FFullList = aFullList;
    UpdateTimeGroups(FFullList, false);
  }

  public void AddElementList( ModelElementContainer elementList) throws ModelException{
  	UpdateTimeGroups(elementList, true);
  }

  public void ExecuteElements() throws ModelException, ScriptException {
    if ( FGroupList.size() == 0 ){
      String s = "";
      if ( FLastExecTime != null ){
        s = "��������� ����� ����������: " + FLastExecTime.toString();
      }
      ModelException e = new ModelException("������ ������ ����� ��� ���������� " + s);
      throw e;
    }
    FListExecuted = true;
    ExecuteGroup currentGroup = (ExecuteGroup) FGroupList.get( 0 );
    ModelTime execTime = currentGroup.FExecTime;

    int i = 0;
    ModelBlock element = null;
    // ��������� �������� ������� ������
    if ( FTimeIndependentBlockGroupIndex == 0 ) {
      FTimeIndependentBlockGroupIndex--;
    }
    if ( currentGroup.FillFlag == EXECUTE_GROUP_IS_NOT_FULL  ) {
      while ( i <= currentGroup.FPointerToLastElement ){
        element = currentGroup.FElementList[ i ];
        element.Execute( execTime );
        i++;
      }
    } else {
       while ( i <  FFullList.size() ){
        element = (ModelBlock) FFullList.get( i );
        element.Execute( execTime );
        i++;
      }
      FFullGroup = null;
      FTimeToExecFullList = null;
    }
    FLastExecTime = execTime;
    FGroupList.removeElementAt( 0 );
    FListExecuted = false;
    FTimeIndependentBlockGroupIndex--;
  }

  protected class ExecuteGroup{
    private ModelBlock[] FElementList = null;
    private int FPointerToLastElement = -1;
    private ModelTime FExecTime = null;
    public int FillFlag = EXECUTE_GROUP_IS_NOT_FULL;
    private int FElementsCount = 0;

    public ExecuteGroup( ModelTime aExecTime ){
      FExecTime = new ModelTime();
      FExecTime.Add( aExecTime );
      FElementList = new ModelBlock[ FMaxElementsCount + 1 ];
    }

    public void AddElement( ModelBlock aElement ){
      if ( FillFlag == EXECUTE_GROUP_IS_FULL  ){
        FElementsCount++;
        return;
      }
      // ���������� �������������� ��� ��������
      if ( FPointerToLastElement >= FElementList.length - 1 ){
        FillFlag = EXECUTE_GROUP_IS_FULL;
        //System.out.println(" full  " + FExecTime.toString());
        FElementsCount++;
        return;
      }
      FPointerToLastElement++;
      FElementList[ FPointerToLastElement ] = aElement;
      FElementsCount++;
      //System.out.println("�������� ������� " + aElement.toString() + " modelTime " + FExecTime.toString());
    }

    /**���������� ���������� ���������, �������
     *
     * @return
     */
    public int GetElementsCount(){
      return FElementsCount;
    }

    protected ModelTime GetExecuteTime(){
      return FExecTime;
    }

  }//ExecuteGroup

}

package mp.elements;


import mp.parser.ScriptException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

/**
 * Класс предназначен для управления течением времени в модели.
 *  Экземпляр этого класса передается во все элементы модели, выполнением которых необходимо управлять.
 * Каждый элемент модели в определенный момент добавляет себя в менеджер времени.
 * Менеджер времени (вот этот вот класс) от каждого добавляемого объекта получает значение модельного времени, в
 * которое этот элмент должен быть выполнен. В соответствии с этим временем объект помещается в свой список - список
 * элементов, которые должны быть выполнены в данный момент времени
 * Владелец менеджера времени запрашивает у него модельное время, которое должно стать текущим временем модели и
 * запрашивает список элементов, которые должны быть выполнены в данное модельное время.
 * Таким образом, при помощи данного класса реализуется особенная стратегия управления временем: каждый блок выполняется
 * только тогда, когда когда подходит его время. Т.е. одни элементы могут вызываться на выполнение чаще других.
 *
 * User: atsv
 * Date: 08.10.2007
 */
public class ModelTimeManager {
  private ArrayList<ExecuteGroup> FGroupList = new ArrayList<ExecuteGroup> ();
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

  /**Флаг показывает, находится ли менеджер времени в режиме выполнения элементов, или нет.
   * Т.е. true - находится, false - не находится
   */
  private boolean FListExecuted = false;
  /**Индекс группы в списке групп (FGroupList). Обозначает группу, в которую в настоящий момент добавляются
   * элементы, которые не зависят от времени (которые не могут определить время своего выполнения). Нужен для ускорения
   * добавления таких элементов.
   * Если индекс меньше 1, то это означает, что такой группы уже нет, либо она сейчас как раз выполняется, и добавлять
   * элементы в нее уже нельзя
   */
  private int FTimeIndependentBlockGroupIndex = -1;

  private static ThreadLocal<ModelTimeManager> tmList = new  ThreadLocal<ModelTimeManager>();
  
  private ModelTimeManager(){  	
    FTimeStep = new ModelTime(0.05);
  }

  private ModelTimeManager( ModelTime aStep ){
    FTimeStep = aStep;
    if ( FTimeStep == null ){
      FTimeStep = new ModelTime(0.05);
    }
  }
  
  public static ModelTimeManager getTimeManager(){
  	ModelTimeManager result = tmList.get();
  	if (result == null) {
  		result = new ModelTimeManager();
  		tmList.set(result);
  	}
  	return result;
  	//return new ModelTimeManager();
  }
  
  public static ModelTimeManager getTimeManager(ModelTime aStep){
  	ModelTimeManager result = tmList.get();
  	if (result == null) {
  		result = new ModelTimeManager(aStep);
  		tmList.set(result);
  	}
  	return result;
  	//return new ModelTimeManager(aStep);
  }
  
  public static void clearContext(){
  	tmList.set(null);
  }
  
  public ModelElementContainer getFullElementsList(){
  	return FFullList;
  	
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
        // переданное время больше, чем время текущего списка
        if (FTempTime2.Compare( aModelTime ) == ModelTime.TIME_COMPARE_GREATER) {
          return i;
        }
      } else {
        // переданное время больше, чем время текущего списка. Это означает, что нет такого списка, у которого
        // время выполнения совпадало бы с переданным в параметре временем
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
        // переданное в параметре время меньше, чем время текущей группы. Нужно вставлять новую группу
        // перед текущей группой
        //FGroupList.insertElementAt( aGroup, i );
        FGroupList.add(i, aGroup);
        return i;
      }
      i++;
    }
    // группа не добавлена в список. Это означает, что время выполнения этой группы - самое позднее из списка.
    // Просто добавляем новую группу в самый конец списка
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

  /** Функция выполняет сравнение двух времен с учетом шага времени
   *
   * @param aTime1 1-е сравниваемое время
   * @param aTime2 2-е сравниваемое время
   * @return Возвращает ModelTime.TIME_COMPARE_EQUALS, если оба параметра равны, ModelTime.TIME_COMPARE_LOW, если
   * первый аргумент меньше второго аргумента, ModelTime.TIME_COMPARE_GREATER, если первый аргумент больше второго
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
      //FGroupList.removeElementAt( aStartIndex );
      FGroupList.remove(aStartIndex);
    }

  }

  /** Добавление элемента в менеджер времени.
   *
   * @param aElement - элемент, который желает быть выполненым в определенное время.
   */
  public int AddElement( ModelBlock aElement, ModelTime aExecTime ) throws ModelException{
    if ( aElement == null  ){
      ModelException e = new ModelException(" Пустой элемент при добавлении в менеджер времени");
      throw e;
    }
    if ( aExecTime == null ){
      AddElement( aElement );
      return -1;
    }
    if ( FTimeToExecFullList != null && IsTimeEqualsWithStep( FTimeToExecFullList, aExecTime ) ){
      //время выполнения элемнта совпадает с врменем выполнения всех элементов. Поэтому производится добавления
      // для сбора статистики - сколько на самом деле выполнится элементов
      FFullGroup.AddElement( aElement );
      return FGroupList.indexOf( FFullGroup );
    } else {
      if ( FTimeToExecFullList != null && FTimeToExecFullList.Compare( aExecTime ) == ModelTime.TIME_COMPARE_LESS
              &&  FGroupList.indexOf( FFullGroup ) > 0
             ){
        // время выполнения этого элемента больше, чем время выполнения "полного списка", и при этом этот "полный
        // список" выполняется не  сейчас.
        //  поэтому такой элемент никуда не добавляется
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
      /**Элемент добавляется в группу, которая является "полной группой".
       * Поэтому нужно удалить все группы, которые будут выполняться после нее. При этом, если группа, в которую
       * добавляется элемент, является группой, котороая выполняется в текущий момент, то удалелия остальных групп
       * не производится
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

  /**Функция возвращает время, которое удовлетворяет следующему условию: целая часть этого времени должна быть больше,
   * чем у времени, переданного в параметре
   *
   * @param aTime - время, больше которого
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
    //проверяем наличие "полного списка". Если такой список имеется, то возможны следующие варианты:
      // 1. Добавляемый элемент добавляется до выполнения "полного списка". В таком случае добавление производится
      //    безо всяких проблем.
      // 2. Добавляемый элемент добавляется после времени выполнения "полного списка", и при этом полный список как
      //    раз выполняется. В этом случае добавление производится без всяких ограничений.
      // 3. Добавляемый элемент добавляется после времени выполнения "полного списка", и при это "полный список" еще
      //    будет выполняться. В таком случае добавление не производится
      if ( FTimeToExecFullList != null  // "полный список" присутствует
         ){
        if ( FGroupList.indexOf( FFullGroup ) == 0 &&
                ( CompareWithStep( FTimeToExecFullList, aCurrentExecTime ) == ModelTime.TIME_COMPARE_EQUALS ||
                 CompareWithStep( FTimeToExecFullList, aCurrentExecTime ) == ModelTime.TIME_COMPARE_GREATER )
           ){
          // "полный список есть", он выполняется в настоящее момент, и делается попытка добавить элемент либо в сам
          // "полный список", либо в группу. которая выполнялась раньше этого списка
          return false;
        }
        if ( FGroupList.indexOf( FFullGroup ) > 0 &&
                ( CompareWithStep( FTimeToExecFullList, aCurrentExecTime ) == ModelTime.TIME_COMPARE_EQUALS ||
                 CompareWithStep( FTimeToExecFullList, aCurrentExecTime ) == ModelTime.TIME_COMPARE_GREATER )
            ) {
          // "полный список есть", он еще будет выполняться. Делается попытка добавить элемент либо в него, либо в
          // группу, которая будет выполняться после "полного списка"
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

  /**
   * Добавление элемента в менеджер времени. Метод используется в случае, если элемент не может самостоятельно
   * определить время своего выполнения.
   *
   * @param aElement - добавляемый элемент
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
    //определяем время выполнения добавляемого элемента
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
  
  public void AddNewElement(ModelBlock aElement) throws ModelException{
  	FFullList.AddElement(aElement);
  	AddElement(aElement);
  	
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
    if ( FFullList == null ){
    	FFullList = new ModelElementContainer();
    	FFullList.SetUniqueNameFlag(false);
    }

    
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
      ModelException e = new ModelException("Передан пустой список элементов в менеджер времени");
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
        s = "Последнее время выполнения: " + FLastExecTime.toString();
      }
      throw new ModelException("Пустой список групп для выполнения " + s);      
    }
    FListExecuted = true;
    ExecuteGroup currentGroup = (ExecuteGroup) FGroupList.get( 0 );
    ModelTime execTime = currentGroup.FExecTime;

    int i = 0;
    ModelBlock element = null;
    // уменьшаем значение индекса группы
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
    //FGroupList.removeElementAt( 0 );
    FGroupList.remove(0);
    FListExecuted = false;
    FTimeIndependentBlockGroupIndex--;
  }
  
  
  private  Map<UUID, RollbackData> fixedStates = new HashMap<UUID, RollbackData> ();
  
  public void fixState(UUID uid) throws ModelException {
  	RollbackData rd = new RollbackData();
  	rd.FTimeStep.StoreValue(FTimeStep);
  	rd.FTempTime1.StoreValue(FTempTime1);
  	rd.FTempTime2.StoreValue(FTempTime2);
  	rd.FTimeToExecFullList.StoreValue(FTimeToExecFullList);
  	rd.FLastExecTime.StoreValue(FLastExecTime);
  	rd.FGroupList = (ArrayList<ExecuteGroup>) FGroupList.clone();
  	fixedStates.put(uid, rd);
  }
  
  public void rollback(UUID label) throws ScriptException{
  	RollbackData rd = fixedStates.get(label);
  	if ( rd == null ) {
  		throw new ScriptException("Нет данных для отката состояния"); 
  	}
  	
  	FTimeStep.StoreValue(rd.FTimeStep);
  	FTempTime1.StoreValue(rd.FTempTime1);
  	FTempTime2.StoreValue(rd.FTempTime2);
  	if ( FTimeToExecFullList != null ) {
  	  FTimeToExecFullList.StoreValue(rd.FTimeToExecFullList);
  	}
  	FLastExecTime.StoreValue(rd.FLastExecTime);
  	FGroupList = rd.FGroupList;
  	fixedStates.remove(label);
  	
  }
  
  private static class RollbackData{
  	 ModelTime FTimeStep = new ModelTime(0);
     ModelTime FTempTime1 = new ModelTime(0);
     ModelTime FTempTime2 = new ModelTime(0);
     ModelTime FTimeToExecFullList = new ModelTime(0);         
     ModelTime FLastExecTime = new ModelTime(0);
     ArrayList<ExecuteGroup> FGroupList = null;
  	
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
      // добавление осуществляется без проверки
      if ( FPointerToLastElement >= FElementList.length - 1 ){
        FillFlag = EXECUTE_GROUP_IS_FULL;
        //System.out.println(" full  " + FExecTime.toString());
        FElementsCount++;
        return;
      }
      FPointerToLastElement++;
      FElementList[ FPointerToLastElement ] = aElement;
      FElementsCount++;
      //System.out.println("добавлен элемент " + aElement.toString() + " modelTime " + FExecTime.toString());
    }

    /**Возвращает количество элементов, которые
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

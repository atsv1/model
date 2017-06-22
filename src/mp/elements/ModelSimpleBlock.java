package mp.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mp.parser.*;

/**
 * User: atsv
 * Date: 18.09.2006
 */
public class ModelSimpleBlock extends ModelBlock {
  private boolean FPreparedFlag = false;
  private int FLastExecutionTime = -1;
  private ScriptLanguageExt FLanguageExt = null;

  private long FExecDuration = 0;
  private long FExecCount = 0;

  public ModelSimpleBlock(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }


  public void SetLanguageExt(ScriptLanguageExt aLanguageExt) {
    SetLanguageExtToInnerParam( aLanguageExt );
    SetLanguageExtToOutParam( aLanguageExt );
    SetLanguageExtToStatecharts( aLanguageExt );
    SetLanguageExtToEventContainer( aLanguageExt );
    FLanguageExt = aLanguageExt;
  }

  public ScriptLanguageExt GetLanguageExt(){
    return FLanguageExt;
  }

  private void PrepareBlock() throws ModelException{
    PrepareParamsOrder();
    FPreparedFlag = true;
  }

  public void Execute() throws ModelException, ScriptException {
    if ( !FPreparedFlag ){
      PrepareBlock();
    }
    ModelBlockParam param = null;
    int i = 0;
    while ( i < FOrderParamsList.size() ){
      param = (ModelBlockParam) FOrderParamsList.get( i );
      param.Update();
      i++;
    }
  }

  private void ExecuteParamsUpdate(ModelTime aCurrentTime) throws ModelException, ScriptException{
    if ( !FPreparedFlag ){
      PrepareBlock();
    }
    UpdateAllParams( aCurrentTime );
  }

  /**Выполнение обновления значений всех параметров. Выполнение производится только в том случае, если модельное
   * время превысило следующее целое значение.
   * Метод вызывается только тогда, когда в блоке нет ни одного стэйтчарта
   * @param aCurrentTime
   * @throws ModelException
   * @throws ScriptException
   */
  private void ExecuteInTime(ModelTime aCurrentTime) throws ModelException, ScriptException{
    if ( aCurrentTime == null ){
      ModelException e = new ModelException("В элемент \"" + GetFullName() + "\" передана пустая ссылка на модельное время");
      throw e;
    }
    if ( GlobalParams.ExecTimeOutputEnabled() ){
      System.out.println( "Начало выполнения параметров в блоке " + this.GetFullName() + ". Время модели = " +
              Double.toString(aCurrentTime.GetValue()) + " последнее время выполнения блока = " + Integer.toString(FLastExecutionTime) );
    }
    int currentTimeValue = aCurrentTime.GetIntValue();    
    if ( currentTimeValue > FLastExecutionTime){      
    	// сделано специально, чтобы параметры обновлялись только если модельное время переваливает очередное целое значение
    	ExecuteParamsUpdate( aCurrentTime );  
      FLastExecutionTime = currentTimeValue;
    } else {
      if ( GlobalParams.ExecTimeOutputEnabled() ){
        System.out.println( "Ничего не выполнялось " + this.GetFullName() + " последнее время выполнения блока = " + Integer.toString(FLastExecutionTime)  );
      }
    }
  }


  /** Метод обеспечивает передачу модельного времени в стэйтчарты данного блока. Если стэйтчарта нет, то вызывается
   * метод Execute() - который без параметров.
   * Метод действует следующим образом:
   * 1. Передается модельное время в стэйтчарты
   * 2. Пересчитываются значения для тех переменных блока, которые нуждаются в пересчете.
   * Правила вызова метода Execute(), если в блоке отсутствует стэйтчарт:
   * метод вызывается только тогда, когда значение модельного времени переваливает через очередное целове значение.
   * @param aCurrentTime - значение текущего модельного времени
   * @throws ModelException
   * @throws ScriptException
   */
  public void Execute(ModelTime aCurrentTime) throws ModelException, ScriptException {
    long startTime = System.nanoTime();
    ExecuteEvents();
    int size = FRootStates.size();
    if ( size == 0 ){
      //ни одного стэйтчарта нет.
      ExecuteInTime( aCurrentTime );
      AddSelfToTimeManager( aCurrentTime );
      return;
    }
    //стэйтчарты есть
    int i = 0;
    AutomatState state = null;
    while ( i < size ){
      state = (AutomatState) FRootStates.get( i );
      state.SetTime( aCurrentTime );
      i++;
    }
    //UpdateParamsWithChangedInput();
    //Execute();
    ExecuteInTime( aCurrentTime );
    AddSelfToTimeManager( aCurrentTime );
    if ( GlobalParams.ExecTimeOutputEnabled() ){
      System.out.println("Добавили блок " + GetFullName() + " в менеджер времени. Текущее время " + aCurrentTime.toString()); 
    }
    long duration = System.nanoTime() - startTime;
    if ( duration < 0 ) duration = 0;
    FExecDuration = FExecDuration + duration;
    FExecCount++;
  }

  public void PrintExecutionTime() {
    int selfIndex = GetIntValue("selfIndex");
    double ms = FExecDuration / 1000000;
    System.out.println( "mux " + this.GetFullName() +"[" + Integer.toString(selfIndex) + "]" +  " count = " +
       Long.toString( FExecCount ) + " duration = " + Double.toString( ms ) + ".ms");
  }

  public boolean IsDynamicParamCreate() {
    return false;  
  }

  public void ApplyNodeInformation() throws ModelException, ScriptException{
    //вызываем метод ApplyNodeInformation() для всех параметров блока (внутренних, внешних и выходных).
    //Поный список параметров хранится в контейнере класса ModelElement
    ApplyAllElementsNodeInformation();
    PrepareBlock();
    // вызываем метод ApplyNodeInformation() для стэйтчартов блока
    ApplyStatechartNodeInfo();
    //чтение информации об обработчиках событий
    ApplyEventNodeInfo();
  }
  
  private  Map<UUID, Integer> fixedStates = new HashMap<UUID, Integer> ();
  public void fixState(UUID stateLabel) throws ModelException{
  	super.fixState(stateLabel);  	  	
  	ModelBlockParam forkModeParam = super.GetInnerParam("isForkMode");
  	forkModeParam.GetVariable().SetValue(true);
  	fixedStates.put(stateLabel, new Integer(FLastExecutionTime));
  }
  
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	super.rollbackTo(stateLabel);
  	FLastExecutionTime = fixedStates.get(stateLabel);
  	fixedStates.remove(stateLabel);
  	if ( fixedStates.isEmpty() ) {
  		ModelBlockParam forkModeParam = super.GetInnerParam("isForkMode");
    	forkModeParam.GetVariable().SetValue(false);  		
  	}
  	
  }

}

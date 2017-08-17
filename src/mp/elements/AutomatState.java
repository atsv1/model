package mp.elements;

import mp.parser.*;
import mp.elements.AutomatTransitionTimeout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;


/**
 * Порядок вызова скриптов при смене состояния:
 * 1. Код перехода ( в Transition)
 * 2. Код BeforeOut старого активного элемента
 * 3. Код AfterIn нового активного элементе
 * @noinspection BreakStatement
 */
public class AutomatState extends ModelEventGenerator{
  private static String CODE_TYPE_BEFORE_OUT = "BeforeOut";
  private static String CODE_TYPE_INIT = "Init";
  private static String CODE_TYPE_AFTER_IN = "AfterIn";

  private ScriptParser FCodeInit = null;
  private ScriptParser FCodeAfterIn = null;
  private ScriptParser FCodeBeforeOut = null;

  private ModelElementContainer FInnerStates = null;
  private ModelElementContainer FTransitions = null;

  protected ScriptLanguageExt FLanguageExt = null;
  private boolean FInitCodeExecuted = false;

  private AutomatState FActiveState = null;
  private ModelElementDataSource FAttrReader = null;

  private ModelTime FActivateTime = new ModelTime(0);
  Vector transitions = new Vector();
  protected ExecutionContext FExecutionContext = null;

  public AutomatState(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FInnerStates = new ModelElementContainer();
    FTransitions = new ModelElementContainer();
    FExecutionContext = new ExecutionContext(this.GetFullName());
  }

  private void ReadCode(ModelElementDataSource aCodeElement) throws ModelException, ScriptException {
    String s = aCodeElement.GetAutomatCodeType();
    if ( s == null || "".equalsIgnoreCase( s ) ){
      ModelException e = new ModelException("Незаполнено значение типа исполняемого кода в элементе \"" + this.GetFullName() + "\"");
      throw e;
    }
    String code = aCodeElement.GetexecutionCode();    
    if ( code == null ){
      return;
    }
    if ( CODE_TYPE_BEFORE_OUT.equalsIgnoreCase( s ) ){
      SetBeforeOutCode( code );      
      return;
    }
    if ( CODE_TYPE_INIT.equalsIgnoreCase( s ) ){
      SetInitCode( code );
      return;
    }
    if ( CODE_TYPE_AFTER_IN.equalsIgnoreCase( s ) ){
      SetAfterInCode( code );      
      return;
    }
    ModelException e = new ModelException("Неизвестный тип исполняемого кода в элементе \"" + this.GetName() + "\"");
    throw e;
  }

  private void ApplyTransitionNodeInformation() throws ModelException, ScriptException{
    int i = 0;
    AutomatTransition transition;
    while ( i < FTransitions.size() ){
      transition = (AutomatTransition) FTransitions.get(i);
      transition.ApplyNodeInformation();
      i++;
    }
  }

  private void ApplyChildNodeInformation() throws ModelException, ScriptException{
    int i = 0;
    AutomatState state;
    while ( i < FInnerStates.size() ){
      state = (AutomatState) FInnerStates.get(i);
      state.ApplyNodeInformation();
      i++;
    }
  }

  public void ApplyNodeInformation() throws ModelException, ScriptException{
    ModelElementDataSource ds = this.GetDataSource();
    if ( ds == null ){
      ModelException e = new ModelException("Пустая нода в элементе \"" + this.GetFullName() + "\"");
      throw e;
    }
    
    // читаем исполняемый код
    List<ModelElementDataSource> codeList = ds.GetCodeElements();
    if ( codeList != null ) {
    	for ( ModelElementDataSource codeElement : codeList ) {
    		ReadCode( codeElement );
    	}
    }    
    ApplyTransitionNodeInformation();
    ApplyChildNodeInformation();
  }

  public void SetlanguageExt( ScriptLanguageExt aLanguageExt ){
    FLanguageExt = aLanguageExt;
    int i = 0;
    AutomatState state = null;
    while ( i < FInnerStates.size() ){
      state = (AutomatState) FInnerStates.get( i );
      state.SetlanguageExt( FLanguageExt );
      i++;
    }
    i = 0;
    AutomatTransition transition;
    while ( i < FTransitions.size() ){
      transition = (AutomatTransition) FTransitions.get( i );
      transition.SetlanguageExt( FLanguageExt );
      i++;
    }
  }

  protected void SetActiveChildState( String aStateName ) throws ModelException{
    if ( aStateName == null || "".equalsIgnoreCase( aStateName ) ){
      ModelException e = new ModelException("В состоянии \"" + this.GetName() +
              "\" сделана попытка установить активное состояние с пустым именем  ");
      throw e;
    }
    AutomatState state = (AutomatState) FInnerStates.Get( aStateName );
    if ( state == null ){
      ModelException e = new ModelException("В состоянии \"" + this.GetName() + "\" отсутствует состояние  \"" +
              aStateName + "\"" );
      throw e;
    }
    FActiveState = state;
  }

  protected AutomatState GetActiveState(){
    return FActiveState;
  }

  /**Функция возвращает объект, отвечающий за возможный переход. Т.е. тот объект AutomatTransition, который
   * вернет true при  вызове функции IsTransitionEnabled()
   *
   * @return индекс объекта AutomatTransition
   * @throws ModelException возникает, если несколько объектов перехода могут осуществить переход
   */
  private AutomatTransition GetAvailableTransition( ModelTime aCurrentTime ) throws ModelException{
    int i = 0;
    AutomatTransition transition;
    // переход с максимальным приоритетом, т.е. тот самый переход
    int maxPriority = 0;
    //Vector transitions = new Vector();
    transitions.removeAllElements();
    int transitionsCount = FTransitions.size();
    while ( i < transitionsCount ){
      transition = (AutomatTransition) FTransitions.get( i );
      if ( transition.IsTransitionEnabled( aCurrentTime ) ){
        if ( transition.GetPriority() >= maxPriority ) {
          if ( transition.GetPriority() > maxPriority   ) {
            transitions.clear();
            transitions.add( transition );
            maxPriority = transition.GetPriority();
          } else {
            if ( maxPriority == transition.GetPriority() ) {
              transitions.add( transition );
            }
          }
        }
      }
      i++;
    }
    if ( transitions.size() > 1 ){
      //формируем сообщение об ошибке
      String s = "";
      i = 0;
      while ( i < transitions.size() ){
        transition = (AutomatTransition) transitions.get( i );
        s = s + " " + "\"" + transition.GetName() + "\"" + "(" +  Integer.toString( transition.GetPriority() ) +  ")";
        i++;
      }
      ModelException e = new ModelException("В состоянии \"" + this.GetFullName() +
              "\" имеется несколько одновременно срабатывающих переходов :" + s);
      throw e;
    }
    if (transitions.size() == 0){
      return null;
    }
    return (AutomatTransition) transitions.get(0);
  }

  /**
   * Процедура вызывается для активации состояния. Метод должен вызываться для АКТИВИРУЕМОГО состояния.
   * При активации состояния происходит:
   * 1. Вызов кода CodeAfterIn
   * 2. Проверяется, не выполняется ли какое-либо из условий перехода. Если выполняется, то вызывается код перехода и
   *   возвращается название следующего состояния
   * 3. Проверяется, есть ли вложенные элементы. Если такие элементы есть, то находится элемент с именем begin и
   *   вызывается его метод SetActive(). Если вложенные элементы есть, но элемента с именем begin нет, то вызывается
   *   исключение.
   * @return - null, если не требуется немедленного перехода в следующее состояние, либо название следующего блока
   */
  public String SetActive( ModelTime aCurrentTime ) throws ScriptException, ModelException {
    FActivateTime.StoreValue( aCurrentTime );
    try{
      this.ExecuteAfterInCode();
    } catch (ScriptException e){
      ModelException e1 = new ModelException("При выполнении кода AfterIn элемента \"" +
         this.GetFullName() + "\" произошла ошибка: " + e.getMessage() );
      throw e1;
    }
    AutomatTransition transition = GetAvailableTransition( aCurrentTime );
    if ( transition != null ){
      transition.ExecuteTransitionCode( aCurrentTime );
      return  transition.GetNextStateName();
    }
    if ( FInnerStates.size() > 0 ){
      //есть вложенные состояния
      SetActiveChildState("begin");
      String s = FActiveState.SetActive( aCurrentTime );
      while ( s != null ){
        ChangeActiveState( s,  aCurrentTime);
        s = FActiveState.SetActive( aCurrentTime );
      }
    }
    return null;
  }

  /**Смена текущего активного состояния на новое активное состояние. Метод должен вызываться для АКТИВИРУЮЩЕГО
   * состояния.
   * Выполняется:
   * 1. Поиск состояния по переданному имени.
   * 2. Вызов у старого активного состояния функции BeforeOut
   * 3. Вызов у нового активного состояния функции SetActive()
   *
   * @param aNewStateName - название состояния, которое должно стать активным
   * @throws ModelException
   * @return - возвращает результат выполнения функции SetActive() нового состояния
   */
  protected String ChangeActiveState( String aNewStateName, ModelTime aCurrentTime ) throws ModelException, ScriptException{
    AutomatState newActiveState = (AutomatState) FInnerStates.Get( aNewStateName );
    if ( newActiveState == null ){
      ModelException e = new ModelException("В состоянии \"" + this.GetName() + "\" отсутствует состояние  \"" +
              aNewStateName + "\"" );
      throw e;
    }
    if ( FActiveState != null ){
      FActiveState.ExecuteBeforeOutCode();
    }
    FActiveState = newActiveState;
    return newActiveState.SetActive( aCurrentTime );
  }

  /**Процедура передачи в стэйтчарт текущего модельного времени. Является аналогом метода Execute() для блоков
   * При получении нового значения модельного времени, процедура производит:
   * 1. Проверку, возможен ли какой-либо переход  из данного состояния: опрос собственных transitions. Если переход
   *   возможен, то производится выход из процедуры, с возвращением названия блока, в который производится переход.
   *   Перед выходом из процедуры выполняется код перехода.
   * 2. Вызов этой же функции для текущего активного состояния (вложенного состояния). Если текущее состояние вернет
   *    название нового блока, то производится смена текущего блока - вызов ChangeActiveState().
   *
   * @param aCurrentTime - значение текущего состояния
   * @return - название состояния, в которое должен быть осуществлен переход, либо null, если никакого перехода не
   *          требуется
   * @throws ModelException
   */
  public String SetTime(ModelTime aCurrentTime) throws ModelException, ScriptException{
    if ( GlobalParams.StateNameOutputEnabled() ){
      if ( aCurrentTime != null ){
        System.out.println( "Вошли в SetTime \"" + this.GetFullName() + "\" Время = " + aCurrentTime.GetStringValue());
      } else {
        System.out.println( "Вошли в SetTime \"" + this.GetFullName() + "\" Время = null");
      }
    }
    AutomatTransition transition = GetAvailableTransition( aCurrentTime );
    if ( transition != null ){
      transition.ExecuteTransitionCode( aCurrentTime );
      String s = transition.GetNextStateName();
      if ( GlobalParams.StateNameOutputEnabled() ){
        System.out.println("Есть срабатывающий переход \"" + transition.GetFullName() + "\". Переход в состояние \"" + s + "\"");
      }
      if ( s == null || "".equalsIgnoreCase( s ) ){
        ModelException e = new ModelException("Ошибка в элементе \"" + GetFullName() +
                "\": отсутствует информация о следующем состоянии" );
        throw e;
      }
      return  s;
    }
    if ( FActiveState == null ){
      return null;
    }
    String nextStateName = FActiveState.SetTime(aCurrentTime);
    while ( nextStateName != null ){ //@todo Предусмотреть возможность выхода из бесконечного зацикливания
      if ( GlobalParams.StateNameOutputEnabled() ){
        System.out.println(" Получили название следующего активного состояния \"" + nextStateName + "\"");
      }
      nextStateName = ChangeActiveState( nextStateName, aCurrentTime );
    }
    if ( GlobalParams.StateNameOutputEnabled() ){
      if ( aCurrentTime != null ){
        System.out.println("Выход из SetTime \""+ this.GetFullName() + "\" Время = " + aCurrentTime.GetStringValue());
      } else {
        System.out.println("Выход из SetTime \""+ this.GetFullName() + "\" Время = null");
      }
      System.out.println("");
    }
    return null;
  }

  /**Функция возвращает время ближайшего события
   * @param  aCurrentTime - значение текущего времени
   * @return null, если никаких событий запланировать невозможно. Значение определяется
   * в абсолютных единицах, то есть результат нужно интерпретировать так: "следующее событие произойдет на 5-м такте
   * модельного времени". Ошибочная интерпретация: "следущющее событие произойдет через 5 тактов модельного времени"
   */
  public ModelTime GetNearestEventTime(ModelTime aCurrentTime) throws ModelException {
    //получаем ближайшее время перехода для собственных переходов
    ModelTime selfTransitionsTime = GetNearestEventTime(aCurrentTime, FTransitions);
    if ( FActiveState == null ){
      return selfTransitionsTime;
    }
    ModelTime childNearestTime = FActiveState.GetNearestEventTime( aCurrentTime );
    if ( selfTransitionsTime == null && childNearestTime == null ){
      return null;
    }
    if ( selfTransitionsTime == null ){
      return childNearestTime;
    }
    if ( childNearestTime == null ){
      return selfTransitionsTime;
    }
    int i = selfTransitionsTime.Compare( childNearestTime );
    if ( i == ModelTime.TIME_COMPARE_LESS ){
      return selfTransitionsTime;
    }
    return childNearestTime;
  }

  /**Добавление нового внутреннего состояния
   *
   * @param aInnerState - добавляемое состояние
   * @throws ModelException - генерируется в двух случаях - если передана пустая ссылка, либо если внутри данного
   * состояния уже существует внутреннее состояние с таким же именем
   */
  private void AddInnerState( AutomatState aInnerState ) throws ModelException{
    if ( aInnerState == null ){
      ModelException e = new ModelException("Попытка добавить пустое состояние в состояние " + this.GetName());
      throw e;
    }
    FInnerStates.AddElement( aInnerState );
  }

  private void AddTransition(AutomatTransition aTransition) throws ModelException{
    if (aTransition == null){
      ModelException e = new ModelException("Попытка добавить пустой соединитель в состояние " + this.GetName());
      throw e;
    }
    FTransitions.AddElement( aTransition );
  }

  public void AddElement(ModelElement aElement) throws ModelException{
    if ( aElement == null ){
      ModelException e = new ModelException("Попытка добавить пустой элемент в состояние " + this.GetName());
      throw e;
    }
    
    if ( aElement instanceof AutomatState ){
      AddInnerState((AutomatState)aElement);
      return;
    }
    
    if ( aElement instanceof  AutomatTransition ){
      AddTransition( (AutomatTransition) aElement );
      return;
    }
    ModelException e = new ModelException("Попытка добавить неизвестный элемент \"" + aElement.GetName() +
            "\" в состояние " + this.GetName());
    throw e;
  }

  private ScriptParser SetCode(String aCode, String aSectionName) throws ModelException{
    if ( aCode == null || "".equalsIgnoreCase( aCode ) ){
      return null;
    }
    if ( FLanguageExt == null ){
      ModelException e = new ModelException( "Отсутствует список переменных. Инициализация парсера невозможна" );
      throw e;
    }
    ScriptParser result = null;
    try{
      result = ParserFactory.GetParser( FLanguageExt, aCode );
    } catch (ScriptException e){
      ModelElement owner = this.GetOwner();
      String s = "";
      if ( owner != null ){
        s = owner.GetName();
      }
      ModelException e1 = new ModelException("Ошибка в скрипте. Элемент \"" + s + "." + this.GetName() +
              "\" Секция \"" + aSectionName + "\" " +
               e.getMessage());
      throw e1;
    }
    return result;
  }

  protected void SetInitCode(String aCode) throws ModelException, ScriptException{
    if ( FCodeInit == null ){
      //FCodeInit = new PascalParser( );
    }
    FCodeInit = SetCode(aCode, "Init");
  }

  protected void SetAfterInCode(String aCode) throws ModelException, ScriptException{
    if ( FCodeAfterIn == null ){
      //FCodeAfterIn = new PascalParser( );
    }
    FCodeAfterIn = SetCode(aCode, "AfterIn");
  }

  protected void SetBeforeOutCode(String aCode) throws ModelException, ScriptException{
    if ( FCodeBeforeOut == null ){
      //FCodeBeforeOut = new PascalParser( );
    }
    FCodeBeforeOut = SetCode(aCode, "BeforeOut");
  }

  protected void ExecuteInitCode() throws ScriptException{
    if ( !(FCodeInit == null || FInitCodeExecuted)  ){
    	FCodeInit.AddExecutionContext(FExecutionContext);
      FCodeInit.ExecuteScript();
      FInitCodeExecuted = true;
    }
    AutomatState childState;
    int i = 0;
    while ( i < FInnerStates.size() ){
      childState = (AutomatState) FInnerStates.get( i );
      childState.ExecuteInitCode();
      i++;
    }
  }

  protected void ExecuteBeforeOutCode() throws ScriptException{
    if ( FCodeBeforeOut == null ){
      //System.out.println( GetFullName() +  " null ExecuteBeforeOutCode");
      return;
    }
    FCodeBeforeOut.AddExecutionContext(FExecutionContext);
    FCodeBeforeOut.ExecuteScript();
    //System.out.println(GetFullName() + " ExecuteBeforeOutCode");
  }

  protected void ExecuteAfterInCode()throws ScriptException {
    if ( FCodeAfterIn == null ){
      //System.out.println(GetFullName() + " null ExecuteAfterInCode");
      return;
    }
    FCodeAfterIn.AddExecutionContext(FExecutionContext);
    FCodeAfterIn.ExecuteScript();
    //System.out.println(GetFullName() + " ExecuteAfterInCode");
  }

  public AutomatState GetState(int index){
    if ( index >= FInnerStates.size() ){
      return null;
    }
    return (AutomatState) FInnerStates.get( index );
  }

  public AutomatState GetState(String aStateName){
    return (AutomatState) FInnerStates.Get( aStateName );
  }

  public AutomatTransition GetTransition(int index){
    if ( index >= FTransitions.size() ){
      return null;
    }
    return (AutomatTransition) FTransitions.get( index );
  }

  public AutomatTransition GetTransition(String aTransitionName){
    return (AutomatTransition) FTransitions.Get( aTransitionName );
  }

  /** Функция предназначена для выдачи наружу информации о том, содержится в этом элементе элементы, срабатывание
   * которых зависит от модельного времени.
   *
   * @return возвращается true, если в элементе содержатся элементы, засисимые от времени. Иначе возвращается false
   */
  protected boolean IsExistsTimeDependentElements(){
    boolean result = false;
    int i = 0;
    AutomatTransition transition;
    while ( (FTransitions != null) && (i < FTransitions.size()) ){
      transition = (AutomatTransition) FTransitions.get( i );
      if ( transition instanceof AutomatTransitionTimeout ) {
        return true;
      }
      i++;
    }
    AutomatState state;
    i = 0;
    while ( FInnerStates != null && i < FInnerStates.size() ){
      state = (AutomatState) FInnerStates.get( i );
      if ( state.IsExistsTimeDependentElements() ) {
        return true;
      }
      i++;
    }
    return result;
  }

  public String toString(){
    return GetFullName();
  }

  protected ModelTime GetActivateTime(){
    return FActivateTime;
  }
  
  protected Map<UUID, RollbackData> fixedStates = new HashMap<UUID, RollbackData> ();
  
  
  private void doFix(ModelElementContainer elements, UUID stateLabel, int operation) throws ModelException {
  	if ( elements == null || elements.size() == 0 ) {
  		return;
  	}
  	int i = 0;
  	ModelElement element;  	
  	while ( i < elements.size() ) {
  		element = elements.get(i);  		
  		if (operation == 1 ) {
  		  element.fixState(stateLabel);
  		} else if (operation == 2) {
  			element.rollbackTo(stateLabel);
  		} else throw new ModelException("недопустимая операция");
  		i++;
  	}
  	
  }
  
  public void fixState(UUID stateLabel) throws ModelException{
  	if (fixedStates.containsKey(stateLabel)) {
  		throw new ModelException("Дублирование фиксированного состояния");
  	}
  	RollbackData rd = new RollbackData();
  	rd.actualState = FActiveState;
  	rd.mt = new ModelTime();
  	rd.mt.StoreValue(FActivateTime);
  	fixedStates.put(stateLabel, rd);
  	doFix(FInnerStates, stateLabel, 1);
  	doFix(FTransitions, stateLabel, 1);
  	  	
  }
  
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	RollbackData rd = fixedStates.get(stateLabel); 
  	fixedStates.remove(stateLabel);
  	FActiveState = rd.actualState;
  	FActivateTime.StoreValue(rd.mt);  	
  	doFix(FInnerStates, stateLabel, 2);
  	doFix(FTransitions, stateLabel, 2);
  	  	
  }
  
  private static class RollbackData{
  	public ModelTime mt = null;
  	public AutomatState actualState = null;
  }

}

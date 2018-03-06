package mp.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mp.parser.*;
import mp.utils.ServiceLocator;


/**
 * User: atsv
 * Date: 18.05.2006
 */
public abstract class ModelBlock extends ModelEventGenerator {

  protected ModelElementContainer<ModelElement> FInpParams = null;
  protected ModelElementContainer<ModelElement> FInnerParams = null;
  protected ModelElementContainer<ModelElement> FOutParams = null;
  protected ModelElementContainer<ModelElement> FOrderParamsList = null;//в этом списке хранятся параметры блока в порядке их выполнения
  protected ModelElementContainer<ModelElement> FRootStates = null;
  protected ModelEventProcessorContainer FEventContainer = null;
  /** Список содержит параметры, которые не надо обновлять в обычном режиме. Это могут быть вспомогательные
   * параметры, которые пользователь не описывает явно.
   */
  private ModelElementContainer FNotUpdateParams = null;
  /** Флаг показывает, что в блоке появились параметры, которые необходимо удалить из списка параметров, для которых
   * вызывается метод обновления.
   */
  private boolean FIsNeedToClear  = false;
  private ModelExecuteList FExecList = null;
  private ModelTimeManager FTimeManager = null;
  private boolean IsTimeIndependentBlock = false; //по умолчанию - блок зависит от времени выполнения
  private boolean IsIndependencyCalculated = false; //по умолчанию, флаг IsTimeIndependentBlock не рассчитан 

  private static void SetLanguageExt( ModelElementContainer<ModelElement> aContainer, ScriptLanguageExt aLanguageExt ){
    ModelBlockParam param;
    int i = 0;
    while ( i < aContainer.size() ){
      param = (ModelBlockParam) aContainer.get( i );
      param.SetLanguageExt( aLanguageExt );
      i++;
    }
  }

  protected void SetLanguageExtToInnerParam(ScriptLanguageExt aLanguageExt){
    SetLanguageExt(FInnerParams,aLanguageExt);
  }

  protected void SetLanguageExtToOutParam(ScriptLanguageExt aLanguageExt){
    SetLanguageExt(FOutParams,aLanguageExt);
  }

  protected void SetLanguageExtToStatecharts(ScriptLanguageExt aLanguageExt){
    int i = 0;
    AutomatState currentState;
    while ( i < FRootStates.size() ){
      currentState = (AutomatState) FRootStates.get( i );
      currentState.SetlanguageExt( aLanguageExt );
      i++;
    }
  }

  protected void SetLanguageExtToEventContainer( ScriptLanguageExt aLanguageExt ){
    if ( FEventContainer == null ){
      return;
    }
    FEventContainer.SetLanguageExt( aLanguageExt );
  }

  public ModelBlock(ModelElement aOwner, String aElementName, int aElementId){
    super(aOwner, aElementName, aElementId);
    FInpParams = new ModelElementContainer<ModelElement>();
    FInnerParams = new ModelElementContainer<ModelElement>();
    FOutParams = new ModelElementContainer<ModelElement>();
    FOrderParamsList = new ModelElementContainer<ModelElement>();
    FRootStates = new ModelElementContainer<ModelElement>();
    FNotUpdateParams = new ModelElementContainer();
  }

  public void AddInpParam( ModelElement aInpParam ) throws ModelException {
    AddBlockParam(aInpParam, FInpParams);
    AddElement( aInpParam );
    ( (ModelBlockParam)aInpParam).SetParamPlacementType( ModelBlockParam.PLACEMENT_TYPE_INPUT );
  }

  private void AddBlockParam(ModelElement aParam, ModelElementContainer<ModelElement> aContainer) throws ModelException{
    ModelException e;
    if ( aParam == null ){
      e = new ModelException("Попытка добавить в список  параметров пустой элемент. Блок " + this.GetName());
      throw e;
    }
    ModelBlockParam param;
    try{
      param = (ModelBlockParam)aParam;
    } catch(Exception e1){
      e = new ModelException("При добавлении  парамтра в блок " + this.GetName() +
              " произошла ошибка: переданный параметр не является параметром " + aParam.GetName() );
      throw e;
    }
   aContainer.AddElement( param );
  }

  public void AddInnerParam(ModelElement aInnerParam) throws ModelException{
    AddBlockParam(aInnerParam, FInnerParams);
    AddElement( aInnerParam );
    ( (ModelBlockParam)aInnerParam).SetParamPlacementType( ModelBlockParam.PLACEMENT_TYPE_INNER );

  }

  public void AddOutParam(ModelElement aOutParam) throws ModelException{
    AddBlockParam(aOutParam, FOutParams);
    AddElement( aOutParam );
    ( (ModelBlockParam)aOutParam).SetParamPlacementType( ModelBlockParam.PLACEMENT_TYPE_OUT );
  }

  public void AddEventProcessorsContainer(ModelEventProcessorContainer aContainer){
    FEventContainer = aContainer;
  }

  public void ClearInpParamList() throws ModelException {
    int i = 0;
    ModelElement param = GetInpParam(0);
    while ( param != null ){
      this.RemoveElement( param );
      i++;
      param = GetInpParam( i );
    }
    FInpParams.Clear();
  }

  public void ExecuteEvents() throws ModelException {
    if ( FEventContainer == null ){
      return;
    }
    FEventContainer.Execute();
  }

  public void FireEvent( String aEventName ) throws ModelException {
    if ( FEventContainer == null ){
      return;
    }
    FEventContainer.EventFired( aEventName );
  }

  public abstract void SetLanguageExt( ScriptLanguageExt aLanguageExt );

  public abstract ScriptLanguageExt GetLanguageExt();

  public abstract void Execute() throws ModelException, ScriptException;

  public abstract void Execute( ModelTime aCurrentTime ) throws ModelException, ScriptException;

  public abstract void PrintExecutionTime();

  public void AddState( AutomatState aNewState ) throws ModelException {
    FRootStates.AddElement( aNewState );
  }

  private static ModelBlockParam GetParam(int aParamIndex, ModelElementContainer<ModelElement> container ){
    if (aParamIndex >= container.size()){
      return null;
    }
    return (ModelBlockParam) container.get( aParamIndex );
  }

  private static ModelBlockParam GetParam(String aParamName, ModelElementContainer<ModelElement> container ){
    if (aParamName == null  ){
      return null;
    }
    return (ModelBlockParam) container.Get( aParamName );
  }

  private static ModelBlockParam GetParam( Integer aParamNameIndex, ModelElementContainer<ModelElement> container ){
    if ( aParamNameIndex == null ){
      return null;
    }
    return (ModelBlockParam) container.GetByNameIndex( aParamNameIndex );
  }

  public ModelBlockParam GetOutParam(int aParamIndex){
    return GetParam( aParamIndex, FOutParams );
  }

  public ModelElement GetOutParam( String aParamName ){
    return GetParam( aParamName, FOutParams );
  }

  public ModelBlockParam GetInnerParam(int aParamIndex){
    return GetParam( aParamIndex, FInnerParams );
  }
  
  public ModelBlockParam GetInnerParam(String aParamName){
    return GetParam( aParamName, FInnerParams );
  }

  public ModelBlockParam GetInpParam(int aParamIndex){
    return GetParam( aParamIndex, FInpParams );
  }

  public ModelBlockParam GetInpParam(String aParamName){
    return GetParam( aParamName, FInpParams );
  }

  public ModelBlockParam GetOutParam( Integer aParamNameIndex ){
    return GetParam( aParamNameIndex, FOutParams );
  }

  public AutomatState GetAutomatState(int aStateIndex){
    if ( aStateIndex >= FRootStates.size() ){
      return null;
    }
    return (AutomatState) FRootStates.get( aStateIndex );
  }

  public ModelTime GetNearestEventTime(ModelTime aCurrentTime) throws ModelException{
    if ( !IsIndependencyCalculated ) {
      CaclulateIndependencyFlag();
    }
    if ( IsTimeIndependentBlock) {
      return null;
    }
    return GetNearestEventTime( aCurrentTime, FRootStates );
  }

  protected void CaclulateIndependencyFlag() {
    IsIndependencyCalculated = true;
    if ( FRootStates == null || FRootStates.size() == 0 ) {
       IsTimeIndependentBlock = true;
      return;
    }
    int i = 0;
    AutomatState state;
    while ( i < FRootStates.size() ){
      state = (AutomatState) FRootStates.get( i );
      if ( state.IsExistsTimeDependentElements() ) {
        IsTimeIndependentBlock = false;
        return;
      }
      i++;
    }
    IsTimeIndependentBlock = true;
  }

  protected void ApplyStatechartNodeInfo() throws ModelException{
    int i = 0;
    AutomatState currentState;
    while ( i < FRootStates.size() ){
      currentState = (AutomatState) FRootStates.get( i );
      try {
        currentState.ApplyNodeInformation();
      } catch (ScriptException e) {
        //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        ModelException e1 = new ModelException( e.getMessage() );
        throw e1;
      }
      i++;
    }
  }

  protected void ApplyEventNodeInfo() throws ModelException{
    if ( FEventContainer == null ){
      return;
    }
    FEventContainer.ApplyNodeInfo();
  }

  /**Активация всех стэйтчартов.
   * @throws ModelException
   */
  protected void InitStatechart() throws ModelException{
    int i = 0;
    AutomatState currentState;
    while ( i < FRootStates.size() ){
      currentState = (AutomatState) FRootStates.get( i );
      try {
        currentState.ExecuteInitCode();
      } catch (ScriptException e) {
        ModelException e1 = new ModelException( e.getMessage() );
        throw e1;
      }
      i++;
    }
    i = 0;
    while ( i < FRootStates.size() ){
      currentState = (AutomatState) FRootStates.get( i );
      try {
        currentState.SetActive( null );
      } catch (ScriptException e) {
        ModelException e1 = new ModelException( e.getMessage() );
        throw e1;
      }
      i++;
    }
  }

  /** Данный метод определяет, может ли содержать данный класс динамически создаваемые параметры.
   * Под "динамически создаваемыми параметрами" здесь понимается следующее - в классе могут создаваться параметры,
   * которые явно не описаны в файле. Например, в блоке-мультиплексоре будут создаваться параметры, которых нет в
   * описании модели
   *
   * @return - возвращается true, если класс может содержать динамически создаваемые параметры, иначе - false
   */
  public abstract boolean IsDynamicParamCreate();

  /**Удаление из списка выполняемых параметров тех параметров, которые не нужно обновлять (не нужно пресчитывать
   * имеющийся внутри них скрипт). В отсутствии динамически добавляемых блоков, этот метод выполняется один раз
   *
   * @param aContainer
   */
  private void ClearParams( ModelElementContainer<ModelElement> aContainer ){
    if ( !FIsNeedToClear ){
      return;
    }
    int i = 0;
    ModelBlockParam param = null;
    ModelBlockParam elementToDel = null;
    while ( i < FNotUpdateParams.size() ){
      param = (ModelBlockParam) FNotUpdateParams.get( i );
      elementToDel = (ModelBlockParam) aContainer.Get( param.GetElementId() );
      if ( elementToDel != null ){
        try {
          aContainer.RemoveElement( elementToDel );
        } catch (ModelException e) { }
      }
      i++;
    }//while
    FIsNeedToClear = false;
  }

  private void InjectExecListToParams( ModelElementContainer<ModelElement> aParamContainer ) throws ModelException {
    ModelBlockParam param;
    int i = 0;
    FExecList = new ModelExecuteList( aParamContainer );
    while ( i < aParamContainer.size() ){
      param = (ModelBlockParam) aParamContainer.get( i );
      param.SetExecuteList( FExecList );
      i++;
    }
  }

  protected void InjectExecListToParam( ModelBlockParam aParam ){
    if ( aParam == null ){
      return;
    }
    aParam.SetExecuteList( FExecList );
  }

  protected void UpdateAllParams( ModelTime aCurrentTime ) throws ScriptException, ModelException {
    ClearParams( FOrderParamsList );
    ModelBlockParam param = null;
    int i = 0;
    if ( FExecList == null || FExecList.CurrentListType == ModelExecuteList.LIST_TYPE_FULL  ) {
      while ( i < FOrderParamsList.size() ){
        param = (ModelBlockParam) FOrderParamsList.get( i );
        param.Update( aCurrentTime );
        i++;
      }
      if ( FExecList != null ){
        FExecList.FinishCurrentCycle();
      }
      return;
    }
    if ( FExecList.CurrentListType == ModelExecuteList.LIST_TYPE_PART  ){
       while ( i <= FExecList.CurrentCyclePointer ){
         param = (ModelBlockParam) FExecList.CurrentCycleList[i];
         param.Update( aCurrentTime );
         i++;
       }
      FExecList.FinishCurrentCycle();
    }
  }

  /** Производится удаления из списка параметров таких параметров, у которых  отсутствует исполняемый скрипт, т.е.
   * для них не нужно вызывать метод Update()
   *
   * @param aParamContainer
   */
  private static void ClearParamsWithEmptyScript( ModelElementContainer<ModelElement> aParamContainer ) throws ModelException {
    ModelBlockParam param;
    int i = 0;
    while ( i < aParamContainer.size() ){
      param = (ModelBlockParam) aParamContainer.get( i );
      if ( !param.IsNeedRuntimeUpdate() ){
        //i++;
        aParamContainer.RemoveElement( param );
      } else
      i++;
    }

  }

  /**Подготавливается список параметров блока. Список подготавливается таким образом, чтобы переменные шли в порядке их
   * выполнения
   *
   * @throws ModelException
   */
  protected void PrepareParamsOrder() throws ModelException{
    ModelBlockParamsIterator iterator = new ModelBlockParamsIterator();
    iterator.FullParamsList = this.GetElements();
    ModelAddExecutor executor = new ModelAddExecutor( iterator );
    executor.container = FOrderParamsList;
    executor.SetUniqueFlag( true );
    executor.Execute();
    ClearParamsWithEmptyScript( FOrderParamsList );
    InjectExecListToParams( FOrderParamsList );
  }

  /** Возвращает целочисленное значение параметра.
   *
   * @param aParamName - название параметра.
   * @return - значение параметра, или -1, если нельзя привести данный параметр к целому значению, или такого
   * паарметра не существует
   */
  public int GetIntValue(String aParamName){
    try {
      ModelBlockParam param = (ModelBlockParam) Get( aParamName );
      if ( param == null ){
        return -1;
      }
      return param.GetVariable().GetIntValue();
    } catch (ModelException e) {
      return -1;
    } catch (ScriptException e) {
      return -1;
    }
  }

  protected void AddToNotUpdatedList( ModelBlockParam aElement ){
    if ( aElement == null ){
      return;
    }
    try {
      FNotUpdateParams.AddElement( aElement );
    } catch (ModelException e) {}
    FIsNeedToClear = true;
  }

  public void SetTimeManager( ModelTimeManager  aTimeManager){
    FTimeManager = aTimeManager;
  }

  protected void AddSelfToTimeManager( ModelTime aLastExecModelTime ) throws ModelException {
    if ( FTimeManager == null){
      return;
    }
    if ( IsTimeIndependentBlock ) {
    	ModelTimeManager.getTimeManager().AddElement( this, null );
      //FTimeManager.AddElement( this, null );
    } else {
    	ModelTimeManager.getTimeManager().AddElement( this, this.GetNearestEventTime( aLastExecModelTime ) );
      //FTimeManager.AddElement( this, this.GetNearestEventTime( aLastExecModelTime ) );
    }
  }

  protected boolean IsTimeIndependentBlock(){
    return IsTimeIndependentBlock; 
  }

  public String toString(){
    return GetFullName();
  }
  
  public String GetFullName(){
  	int i = GetIntValue("selfIndex");
    if ( i == -1 ) {
      return super.GetFullName();
    } else {
      return super.GetFullName() + "i" + Integer.toString( i );
    }
  	
  }

  protected void SetToInitCondition() throws ModelException {
    //переводим вначальное состояние все параметры
    int i = 0;
    ModelBlockParam param;
    while ( i < size() ){
      param = (ModelBlockParam) GetByIndex( i );
      param.LoadInitValue();
      i++;
    }
  }

  /**Производится создание входных параметров мультиплексора. Параметры создаются как аналоги всех выходных
   * параметров блока, переданного в параметре. Аналог - это совпадение имени параметра и его типа (материальный или
   * нематериальный) и типа значения в параметре - real, integer  ипр.
   *
   * @param aBlock - создаются аналоги выходных параметров именно этого блока
   * @param aLinkFlag - флаг, который указывает - производить ли немедленное соединение только что созданного входного
   * параметра с параметром, на основании которого этот входной параметр создан
   * @throws ModelException
   */
  protected void ReCreateInputParams( ModelBlock aBlock, boolean aLinkFlag ) throws ModelException{
    int i = 1;
    ModelBlockParam outParam = aBlock.GetOutParam(0);
    ModelInputBlockParam inpParam = null;
    while ( outParam != null ){
      if ( outParam.GetParamType() != ModelBlockParam.PARAM_TYPE_MATERIAL) {
        inpParam = new ModelInputBlockParam( this, outParam.GetName() , ServiceLocator.GetNextId() );
      } else {
        inpParam = new ModelMaterialParam( this, outParam.GetName() , ServiceLocator.GetNextId() );
      }
      inpParam.SetVariable( (Variable)outParam.GetVariable().clone());
      AddInpParam( inpParam );
      if ( aLinkFlag ){
        inpParam.Link(aBlock, outParam);
      }
      outParam = aBlock.GetOutParam(i);
      i++;
    }
  }
  
  private void doFix(ModelElementContainer<ModelElement> elements, UUID stateLabel, int operation) throws ModelException {
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
  	doFix(FInpParams, stateLabel, 1);
  	doFix(FInnerParams, stateLabel, 1);
  	doFix(FOutParams, stateLabel, 1);
  	doFix(FRootStates, stateLabel, 1);  	  	
  }
  
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	doFix(FInpParams, stateLabel, 2);
  	doFix(FInnerParams, stateLabel, 2);
  	doFix(FOutParams, stateLabel, 2);
  	doFix(FRootStates, stateLabel, 2);  	  	
  }
  
}

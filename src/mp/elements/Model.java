package mp.elements;

import mp.parser.*;
import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;

import java.util.Stack;
import java.util.UUID;
import java.util.Vector;

/**
 * User: atsv
 * Date: 21.09.2006
 */
public class Model extends ModelEventGenerator implements Runnable, ModelExecutionManager{
  private ModelElementClassesContainer FBlockList = null;
  private boolean FStopFlag = true;
  private String FErrorString = null;
  private ModelTime FCurrentModelTime = null;
  private ModelTime FTimeIncrement = null;
  private ModelAttributeReader FAttrReader = null;
  private Vector<ModelBlock> FDynamicBlockList = null;
  private ModelTimeManager FTimeManager = null;
  private boolean FIsTimeManagerInit = false;
  private int FStepDelay = 0;
  private int noForkStepDelay = 0;
  /* Флаг, определяющий возможность выполнения модели. используется для приостановки работы модели*/
  private boolean FEnableExec = true;
  private Vector<Model> parallelModelList = new Vector<Model>();
  private Vector<ModelFunction> FFunctionList = null;
  private boolean FInitFlag = false;

  private int FPrintDurationInterval = 0;

  private ModelElementContainer FConstantList = null;
  
  private Stack<ModelTime> stopTimesStack = new Stack<ModelTime> ();

  public Model(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FBlockList = new ModelElementClassesContainer();
    FConstantList = new ModelElementContainer();
    FCurrentModelTime = new ModelTime(0);
  }

  public ModelTime GetNearestEventTime(ModelTime aCurrentTime) throws ModelException {
    return GetNearestEventTime( FCurrentModelTime, FBlockList );
  }

  public ModelElement Get(String aBlockName){
    return  FBlockList.Get( aBlockName );
  }

  public ModelBlock Get(String aBlockName, int aBlockIndex){
    return (ModelBlock) FBlockList.Get( aBlockName, aBlockIndex );
  }

  public void AddElement(ModelElement aElement) throws ModelException {
    FBlockList.AddElement( aElement );
  }

  /** Возвращается количество блоков, имеющихся в модели
   *
   * @return количество блоков
   */
  public int size(){
    return FBlockList.size();
  }

  /**Чтение шага моделирования из файла модели. Шаг считывается и записывается в переменную FTimeIncrement.
   * Если шаг моделирования в модели отсутствует, то указанная переменная  создается со значением 1.
   */
  private void ReadTimeIncrement() throws ModelException {
    FAttrReader.SetNode( this.GetNode() );
    String s = FAttrReader.GetModelStep();
    double d;
    if ( s == null || "".equalsIgnoreCase(s) ){
      d = 1;
    } else
    try{
      d = Double.parseDouble( s );
    } catch (Exception e1){
      ModelException e = new ModelException("Неверный формат шага моделирования в модели \"" + this.GetName() + "\"");
      throw e;
    }
    FTimeIncrement = new ModelTime(d);
  }

  private void BuldDynamicParams( ModelBlock aBlock ) throws ModelException {
    if ( aBlock == null ){
      return;
    }
    ModelDynamicBlock block;
    try{
      block = (ModelDynamicBlock) aBlock;
    } catch (Exception e){
      ModelException e1 = new ModelException("Невозможно преобразовать блок \"" + aBlock.GetFullName() +
              "\" в блок с динамически формируемыми параметрами");
      throw e1;
    }
    ModelLanguageBuilder.AddSelfIndexVariable( aBlock, this );
    block.BuildParams();
  }

  /**Формируем список блоков, для которых нужно производить динамическое формирование параметров
   * Список будет храниться в поле FDynamicBlockList. Если в модели нет таких блоков, то FDynamicBlockList останется равным null
   */
  private void CreateDynamicBlockList(){
    int i = 0;
    ModelBlock currentBlock;
    while ( i < FBlockList.size() ){
      currentBlock = (ModelBlock) FBlockList.get(i);
      if ( currentBlock.IsDynamicParamCreate() ) {
        if ( FDynamicBlockList == null ){
          FDynamicBlockList = new  Vector<ModelBlock>();
        }
        FDynamicBlockList.add( currentBlock );
      }
      i++;
    }
  }

  /** Метод производит построение динамических параметров для всех блоков, которые попали в объект FDynamicBlockList
   */
  private void BuildAllDynamicParams() throws ModelException {
    //Пока производится простой перебор блоков. Когда начнутся соединяться мультиплексор с мультиплексором, это придется
    // переделать
    //@todo переделать, когда будут соединяться мультиплексор с мультиплексором
    if ( FDynamicBlockList == null ){
      return;
    }
    int i = 0;
    ModelBlock currentBlock;
    while ( i < FDynamicBlockList.size() ){
      currentBlock = FDynamicBlockList.get( i );
      BuldDynamicParams( currentBlock );
      i++;
    }
  }

  private void AddLinkerToDynamicBlock() throws ModelException {
    if ( FDynamicBlockList == null ){
      return;
    }
    int i = 0;
    ModelDynamicBlock currentBlock;
    while ( i < FDynamicBlockList.size() ){
      currentBlock = (ModelDynamicBlock) FDynamicBlockList.get( i );
      currentBlock.SetDynamicLinker( );
      i++;
    }
  }

  private void PrepareDynamicBlock() throws ModelException {
    CreateDynamicBlockList();
    BuildAllDynamicParams();
    //AddLinkerToDynamicBlock();
  }

  public void RegisterModelInContext() throws ModelException {
    try {
      ModelExecutionContext.AddModelExecutionManager( this );
      if ( parallelModelList != null && !parallelModelList.isEmpty() ) {
      	for (Model subModel : parallelModelList) {
      		ModelExecutionContext.AddModelExecutionManager( subModel );
      	}
      }
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }
  }

  public void InitAllBlockStatecharts() throws ModelException {
    int i = FBlockList.size()-1;
    ModelElement element;
    while ( i >= 0 ){
      element = FBlockList.get(i);
        if ( element instanceof ModelSimpleBlock ){
          ((ModelBlock)element).InitStatechart();
        }
      i--;
    }
    // модели из секции ParallelModel
    if ( parallelModelList != null && !parallelModelList.isEmpty() ) {
    	for (Model subModel : parallelModelList) {
    		subModel.InitAllBlockStatecharts();
    	}
    }

  }

  public void ApplyNodeInformation() throws ModelException{
    PrepareDynamicBlock();
    FAttrReader = ServiceLocator.GetAttributeReader();
    //передаем во все объекты расширитель языка
    ModelLanguageBuilder builder = new ModelLanguageBuilder( this );
    try{
      builder.UpdateModelElements();
    } catch (ScriptException e1){
      ModelException e = new ModelException(e1.getMessage());
      throw e;
    }
    int i = FBlockList.size()-1;
    //вызывваем у всех объектов метод ApplyNodeInformation() - чтение информации из ноды
    ModelElement element;
    while ( i >= 0 ){
      element = FBlockList.get(i);
      try {
        element.ApplyNodeInformation();
      } catch (ScriptException e1) {
        ModelException e = new ModelException(e1.getMessage());
        throw e;
      }
      i--;
    }
    if ( this.FFunctionList != null ) {
    	ModelFunction fun;
    	i = 0;
    	while ( i < FFunctionList.size()) {
    		fun = FFunctionList.get(i);
    		fun.ApplyNodeInformation();
    		i++;
    	}
    }
    AddLinkerToDynamicBlock();
    ReadTimeIncrement();
    FStepDelay = FAttrReader.GetStepDelay();
    if ( FStepDelay == -1 ){
      FStepDelay = 100;
    }
    FPrintDurationInterval = FAttrReader.GetDurationPrintInterval();
    // RegisterModelInContext();
    //InitAllBlockStatecharts();
  }

  private void ExecuteWithoutInit() throws ScriptException, ModelException {
  	if ( !FIsTimeManagerInit ) {
  		FTimeManager = CreateTimeManager();
      FIsTimeManagerInit = true;
    }
    FTimeManager.ExecuteElements();
    FCurrentModelTime.StoreValue( FTimeManager.GetNearestModelTime() );
    for (Model subModel : parallelModelList) {
    	subModel.FCurrentModelTime.StoreValue(FCurrentModelTime);
    }    
  }

 /**Выполнение тактов модели. Такты выполняются на основании потребности блоков в выполнении такта
  * и на основании шага такта, значение которого было заложено при создании модели
  * @throws ScriptException
  * @throws ModelException
  */
  public void Execute() throws ScriptException, ModelException {
  	if ( !FInitFlag ) {
  		RegisterModelInContext();
	    InitAllBlockStatecharts();
  		FInitFlag = true;
  	}
  	ExecuteWithoutInit();
  }

  public ModelElement GetByIndex(int aIndex) throws ModelException{
    return FBlockList.get( aIndex );
  }

  public int GetBlockCount(String aBlockName){
    return FBlockList.GetElementsCount( aBlockName );
  }

  public void StopExec(){
    FStopFlag = false;
  }

  public String GetErrorString(){
    return FErrorString;
  }

  /**Установка флага, разрешающего выполнение модели
   * Используется для приостановки работы модели
   *
   * @param aEnableExec true - выполнение модели разрешено, false - модель не выполняется
   */
  public void SetEnableExecution( boolean aEnableExec ){
    FEnableExec = aEnableExec;
  }
  
  private boolean runEnable(){
  	if ( !FStopFlag  ) {
  		return false;
  	}
  	if ( FStopFlag && stopTimesStack.isEmpty()) {
  		return true;
  	}
  	if ( stopTimesStack.isEmpty() ) {
  		return false;
  	}
  	ModelTime curT = stopTimesStack.peek();
  	int i = FCurrentModelTime.Compare(curT);
  	return (i == ModelTime.TIME_COMPARE_LESS  || i == ModelTime.TIME_COMPARE_EQUALS) ;
  }
  
  private void mainCycle(){
  	int tactWithoutGC = 0;
    int toPrint = 0;
    while ( runEnable() ){
      try {
        if ( FEnableExec ) {
          ExecuteWithoutInit();
        }
        Thread.sleep(FStepDelay);
        if ( tactWithoutGC > 1000 ){
          Runtime r = Runtime.getRuntime();
          r.gc();
          tactWithoutGC = 0;
        }
        tactWithoutGC++;
        if ( FPrintDurationInterval > 0 &&  toPrint >= FPrintDurationInterval ){
          PrintDurationOfAllBlocks();
          toPrint = 0;
        }
        toPrint++ ;

      } catch (Exception e) {
        FErrorString = e.getMessage();
        if ( FErrorString == null ) {
          FErrorString = e.toString();
        }
        e.printStackTrace();
        break;
      }
    }
  	
  }

  public void run() {
    //int tactCount = 0;    
    try {
	    RegisterModelInContext();
	    InitAllBlockStatecharts();
	    FInitFlag = true;
    } catch (ModelException e1) {
    	FErrorString = e1.getMessage();
    	return;
    }
    mainCycle();

  }

 /** Функция возвращает значение текущего модельного времени
  *  Внимание! Возвращается именно тот экземпляр класса, который действительно используются в модели. Поэтому
  *  нежелательно изменять значения внутри полученного объекта.
  * @return Значение текущего модельного времени
  */
  public ModelTime GetCurrentTime(){
    return FCurrentModelTime;
  }

  public ModelTime GetModelStep(){
    return FTimeIncrement;
  }

  /** Функция возвращает индекс переданного в параметре блока. Здесь индекс - это не порядковый номер блока
   * в общем списке блоков, а порядковый номер блока в списке блоков с названием, таким же, как и у переданного
   * в параметре блока.
   *
   * @param aBlock - блок, индекс которого необходимо определить
   * @return - индекс блока в массиве блоков с одинаковым именем. Возвращается 0, если блок присутсвует в
   * единственном числе. -1 - если такой блок отсутствует
   */
  public int GetBlockIndex( ModelBlock aBlock ){
    int result = -1;
    if ( aBlock == null ){
      return -1;
    }
    int i = 0;
    String blockName = aBlock.GetName();
    ModelBlock currentBlock = this.Get(blockName,i);
    while ( currentBlock != null ){
      if ( currentBlock.GetElementId() == aBlock.GetElementId() ){
        return i;
      }
      i++;
      currentBlock = this.Get(blockName,i);
    }
    return result;
  }

  private ModelTimeManager CreateTimeManager() throws ModelException {
  	ModelTimeManager result = new ModelTimeManager( FTimeIncrement );
  	result.SetFullElementsList( FBlockList );
    int i = 0;
    Model subModel;
    while ( i < parallelModelList.size()) {
    	subModel = parallelModelList.get(i);
    	result.AddElementList( subModel.FBlockList );
      i++;
    }
    return result;
  }

  protected ModelTimeManager GetTimeManager(){
    return FTimeManager;
  }

  private void PrintDurationOfAllBlocks(){
    int blockCount = FBlockList.size();
    ModelBlock block;
    int i = 0;
    while ( i < blockCount ){
      block = (ModelBlock) FBlockList.get( i );
      block.PrintExecutionTime();
      i++;
    }

  }


  public String GetManagerName() {
    return this.GetName();
  }

  public void StopModelExecution() throws ScriptException {
    StopExec();
  }

  public void StartModelExecution() throws ScriptException {
    FStopFlag = true;
    FEnableExec = true;
    run();
  }

  public void SetToInitCondition() throws ScriptException {
    int i = 0;
    ModelBlock currentBlock;
    while ( i < FBlockList.size() ){
      currentBlock = (ModelBlock) FBlockList.get(i);
      try {
        currentBlock.SetToInitCondition();
      } catch (ModelException e) {
        ScriptException e1 = new ScriptException( "Ошибка при установке начальных значений в блоке \"" + currentBlock.GetFullName() + "\" " + e.getMessage() );
        throw e1;
      }
      i++;
    }//while
  }

  public Variable GetVariable(String aBlockName, int aBlockIndex, String aParamValue) throws ScriptException {
    ModelBlock block;
    if ( aBlockIndex == -1 ){
      block = (ModelBlock) Get( aBlockName );
    } else {
      block = Get( aBlockName, aBlockIndex );
    }
    if ( block == null ){
      ScriptException e = new ScriptException("Отсутствует блок \"" + aBlockName + "\" с индексом \"" + Integer.toString( aBlockIndex ) + "\"");
      throw e;
    }
    ModelBlockParam param;
    try {
      param = (ModelBlockParam) block.Get( aParamValue );
    } catch (ModelException e) {
      //e.printStackTrace();
      ScriptException e1 = new ScriptException( e.getMessage() );
      throw e1;
    }
    return param.GetVariable();
  }

  public void ReConnectParam(String aBlockName, int aBlockIndex, String aParamName,
                             String aModelToConnect, String aBlockToConnect, int aBlockIndexToConnect, String aParamToConnect)
          throws ScriptException {
    ModelBlock block = this.Get( aBlockName, aBlockIndex );
    if ( block == null ){
      ScriptException e = new ScriptException("Отсутствует блок \"" + aBlockName + "\"");
      throw e;
    }
    ModelBlockParam param;
    try {
      param = (ModelBlockParam) block.Get( aParamName );
    } catch (ModelException e) {
      //e.printStackTrace();
      ScriptException e1 = new ScriptException( e.getMessage() );
      throw e1;
    }
    if ( param == null ){
      ScriptException e = new ScriptException("Отсутствует параметр \"" + aParamName + "\" в блоке \"" + aBlockName + "\"");
      throw e;
    }
    ModelInputBlockParam inputParam = null;
    if (!( param instanceof  ModelInputBlockParam)) {
      ScriptException e = new ScriptException("Параметр \"" + aParamName + "\" должен быть входным параметром");
      throw e;
    }
    inputParam = (ModelInputBlockParam) param;
    ModelExecutionManager manager = ModelExecutionContext.GetManager( aModelToConnect );
    if ( manager == null ){
      ScriptException e = new ScriptException("В системе отсутствует модель \"" + aModelToConnect +
              "\". Выполнение операции ReConnect невозможно");
      throw e;
    }
    if ( !( manager instanceof Model ) ){
      ScriptException e = new ScriptException("Выполнение операции ReConnect невозможно. Модель для коннекта не является моделью");
      throw e;
    }
    Model modelToConnect = (Model) manager;
    ModelBlock blockToConnect = modelToConnect.Get(aBlockToConnect, aBlockIndexToConnect);
    if ( blockToConnect == null ){
      ScriptException e = new ScriptException("Отсутствует блок \"" + aBlockToConnect + "\"");
      throw e;
    }
    ModelBlockParam paramToConnect = null;
    try {
      paramToConnect = (ModelBlockParam) blockToConnect.Get( aParamToConnect );
    } catch (ModelException e) {
      ScriptException e1 = new ScriptException( e.getMessage() );
      throw e1;
    }
    if ( paramToConnect == null ){
      ScriptException e = new ScriptException("Отсутствует параметр \"" + aParamToConnect + "\" в блоке \"" + aBlockToConnect + "\"");
      throw e;
    }
    try {
      inputParam.Link( blockToConnect, paramToConnect );
    } catch (ModelException e) {
      ScriptException e1 = new ScriptException( e.getMessage() );
      throw e1;
    }
  }

  public void AddConstant( ModelConstant aConstant ) throws ModelException{
    FConstantList.AddElement( aConstant );
  }

  public ModelConstant GetConstant( String aConstantName ){
    return (ModelConstant)FConstantList.Get( aConstantName );
  }

  public ModelConstant GetConstant( int index ){
    return (ModelConstant)FConstantList.get( index );
  }

  public int GetConstantCount(){
    return FConstantList.size();
  }

  public void AddParallelModel(Model subModel) {
  	parallelModelList.add(subModel);
  }

  public void AddFunction( ModelFunction aFunction ){
  	if (FFunctionList == null) {
  		FFunctionList = new Vector<ModelFunction>();
  	}
  	if (aFunction == null) {
  		return;
  	}
  	FFunctionList.add(aFunction);
  }

  public Vector GetFunctionList(){
  	return FFunctionList;
  }

  public Model GetParallelModel( String modelName ){
  	if ( parallelModelList == null || parallelModelList.size() == 0 ) {
  		return null;
  	}
  	int i = 0;
  	while ( i < parallelModelList.size() ) {
  		Model model = parallelModelList.get(i);
  		if ( model.GetName().equalsIgnoreCase(modelName) ) {
  			return model;
  		}
  		i++;
  	}
    return null;
  }
  
  private void fixStates(UUID uid) throws ModelException {
  	int i = 0;
    while ( i < FBlockList.size() ) {
    	ModelBlock block = (ModelBlock) FBlockList.get(i);
    	block.fixState(uid);
    	i++;
    }
    FCurrentModelTime.fixState(uid);
  }

	@Override
	public UUID fork(int modelTimePeriod, boolean nestedFork) throws ScriptException {
		UUID uid = java.util.UUID.randomUUID();
		try {
			fixStates(uid);
		} catch (ModelException e) {
			throw new ScriptException(e.getMessage());
		}	
		
		ModelTime stopTime = new ModelTime();
		stopTime.StoreValue(FCurrentModelTime);
		stopTime.Add(modelTimePeriod);
		stopTimesStack.push(stopTime);
		noForkStepDelay = FStepDelay; 
		mainCycle();
		return uid;
	}

	@Override
	public void rollback(UUID label) throws ScriptException{
		try {
	  	int i = 0;
	    while ( i < FBlockList.size() ) {
	    	ModelBlock block = (ModelBlock) FBlockList.get(i);
	    	block.rollbackTo(label);
	    	i++;
	    }
	    FCurrentModelTime.rollbackTo(label);
	    stopTimesStack.pop();
	    if ( stopTimesStack.isEmpty() ) {
	    	// задержку в выполнении основного потока модели восстанавливаем когда стек форков пуст
	    	FStepDelay = noForkStepDelay;
	    }
		} catch (ModelException e) {			
			 throw new ScriptException( e.getMessage() );
		}
		
	}

}

package mp.elements;

import mp.parser.*;
import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private List<ModelBlock> FDynamicBlockList = null;
  private ModelTimeManager FTimeManager = null;
  
  private int FStepDelay = 0;
  private int noForkStepDelay = 0;
  /* Флаг, определяющий возможность выполнения модели. используется для приостановки работы модели*/
  private boolean FEnableExec = true;
  private ArrayList<Model> parallelModelList = new ArrayList<Model>();
  private ArrayList<Model> subModelList = new ArrayList<Model>();
  private Vector<ModelFunction> FFunctionList = null;

  
  private boolean statechartInitFlag = false;
  private boolean contextRegFlag = false;
  private boolean timeManagerInit = false;

  private int FPrintDurationInterval = 0;

  private ModelElementContainer FConstantList = null;
  
  private Stack<ModelTime> stopTimesStack = new Stack<ModelTime> ();
  
  private int forkCounter = 0;

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
  	ModelBlock result = (ModelBlock) FBlockList.Get( aBlockName, aBlockIndex );
  	if ( result == null ) {
  		for (Model parallelModel : parallelModelList) {
  			result = parallelModel.Get(aBlockName, aBlockIndex);
  			if (result != null) {
  				break;
  			}  			
  		}
  	}
    return result;
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
    String s = elementSource.GetModelStep();
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
          FDynamicBlockList = new  ArrayList<ModelBlock>();
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
      if ( subModelList != null && !subModelList.isEmpty() ) {
      	for (Model subModel : subModelList) {
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
    FStepDelay = elementSource.GetStepDelay();
    if ( FStepDelay == -1 ){
      FStepDelay = 100;
    }
    FPrintDurationInterval = elementSource.GetDurationPrintInterval();
    // RegisterModelInContext();
    //InitAllBlockStatecharts();
  }

  private void ExecuteWithoutInit() throws ScriptException, ModelException {
  	if ( !timeManagerInit ) {
  		initTimeManager();
  		timeManagerInit = true;
    }
    FTimeManager.ExecuteElements();
    FCurrentModelTime.StoreValue( FTimeManager.GetNearestModelTime() );
    for (Model subModel : parallelModelList) {
    	subModel.FCurrentModelTime.StoreValue(FCurrentModelTime);
    }    
  }
  
  private void init() throws ScriptException, ModelException {
  	if ( !statechartInitFlag ) {
  		InitAllBlockStatecharts();
  		statechartInitFlag = true;
  	}
		if (!contextRegFlag) {
			RegisterModelInContext();				
			contextRegFlag = true;
		}
  	if ( !timeManagerInit ) {
  		initTimeManager();
  		timeManagerInit = true;
    }
  	
  }

 /**Выполнение тактов модели. Такты выполняются на основании потребности блоков в выполнении такта
  * и на основании шага такта, значение которого было заложено при создании модели
  * @throws ScriptException
  * @throws ModelException
  */
  public void Execute() throws ScriptException, ModelException {
  	init();
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
        if (FStepDelay != 0) {
          Thread.sleep(FStepDelay);
        }
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
    	init();
    } catch (Exception e1) {
    	System.out.println(e1.getMessage());
    	e1.printStackTrace();
    	FErrorString = e1.getMessage();
    	return;
    }
    FStopFlag = true;
    FEnableExec = true;
    synchronized(this) {
      mainCycle();
      this.notifyAll();
    }

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

  private void initTimeManager() throws ModelException {
  	ModelTimeManager result = ModelTimeManager.getTimeManager( FTimeIncrement );
  	//result.SetFullElementsList( FBlockList );
  	result.AddElementList(FBlockList);
    int i = 0;
    Model subModel;
    while ( i < parallelModelList.size()) {
    	subModel = parallelModelList.get(i);
    	subModel.initTimeManager();
    	//result.AddElementList( subModel.FBlockList );
      i++;
    }
    FTimeManager = result;
    //return result;
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
  	//FStopFlag = true;
    //FEnableExec = true;
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
  
  public void addSubModel(Model subModel){
  	subModelList.add(subModel);
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
  
  private Map<UUID, ModelTimeManager> timeManagerFixedStates = new HashMap<UUID, ModelTimeManager> ();
  
  private void fixStates(UUID uid) throws ModelException {
  	int i = 0;
    while ( i < FBlockList.size() ) {
    	ModelBlock block = (ModelBlock) FBlockList.get(i);
    	block.fixState(uid);
    	i++;
    }
    FCurrentModelTime.fixState(uid);
    if ( FTimeManager == null ) {
    	FTimeManager = ModelTimeManager.getTimeManager();
    }
    FTimeManager.fixState(uid);
    timeManagerFixedStates.put(uid, FTimeManager);
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
		FStepDelay = 0;
		contextRegFlag = false;
		timeManagerInit = false;
		forkCounter++;
		Thread tr = new Thread(this);
		tr.start();
		synchronized(this) {
			try {
				this.wait(2000);
			} catch (InterruptedException e) {				
				e.printStackTrace();
				tr.dumpStack();
			}
		}
		contextRegFlag = true;
		timeManagerInit = true;		
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
	    FTimeManager =  timeManagerFixedStates.get(label);
	    timeManagerFixedStates.remove(label);
	    FTimeManager.rollback(label);	  
	    forkCounter--;
	   
		} catch (ModelException e) {			
			 throw new ScriptException( e.getMessage() );
		}		
	}
	
	public int forkStatus(){
		return forkCounter;		
	}
	
	public boolean isForkMode(){
		return (forkCounter > 0);
	}
	
	
	/**
	 * Возвращает список блоков, соединенных с блоком block. 
	 * Тип соединения - в блоке block есть входной параметр, который соединен с другим блоком, при этом индекс блока - selfIndex  
	 * 
	 * @param block	 
	 * @return
	 * @throws ModelException 
	 */
	private List<ModelBlock> getLinkedBlockBySelfIndex(ModelBlock block, Map<String, ModelBlock> alreadyDefinedBlocks ) throws ModelException{
		if ( block == null ) {
			return null;
		}
		int i = 0;
		ModelInputBlockParam param = (ModelInputBlockParam) block.GetInpParam(i);
		List<ModelBlock> result = new ArrayList<ModelBlock> ();
		
		while ( param != null ) {
			if ( param.isConnectedBySelfIndex() ) {
				String linkedBlockName = param.getLinkedBlockName();
				ModelElement linkedBlock = param.GetOwner().GetOwner().Get(linkedBlockName);
				if ( linkedBlock instanceof ModelBlock  ) {
					String s = linkedBlock.GetFullName();
					if ( !alreadyDefinedBlocks.containsKey(s) ) {
						result.add((ModelBlock) linkedBlock);
						alreadyDefinedBlocks.put(s, (ModelBlock) linkedBlock);
						List<ModelBlock> list = getLinkedBlockBySelfIndex((ModelBlock) linkedBlock, alreadyDefinedBlocks);
						result.addAll(list);
					}
				}
			}
			i++;
			param = (ModelInputBlockParam) block.GetInpParam(i);
		}
		return result;		
	}
	
	private List<ModelBlock> createBlocks(List<ModelBlock> blocks, String blockName) throws ScriptException, ModelException{
		List<ModelBlock> createdBlocks = new ArrayList<ModelBlock>();
		for (ModelBlock block : blocks) {
			ModelElementDataSource ds = block.GetDataSource();
			if ( ds == null ) {
				throw new ScriptException("Отсутствует исходный код для \"" + blockName + "\"");
			}
			ModelElement owner = block.GetOwner();
			if (!( owner instanceof Model )) {
				throw new ScriptException("Не создать блок \"" + blockName + "\"");
			}
			Model blockOwner = (Model) owner;
			ModelBuilder mb = block.getElementBuilder();
			if ( mb == null ) {
				throw new ScriptException("unknown element builder");
			}						
			ModelBlock newBlock = (ModelBlock) mb.CreateElementInstance(blockOwner.GetDataSource(), block.GetDataSource(), blockOwner);
			newBlock.SetDataSource(ds);			
			blockOwner.AddElement(newBlock);
			
			mb.buildElement(ds, newBlock, blockOwner.GetDataSource());
			createdBlocks.add(newBlock);
		}
		
		for (ModelBlock newBlock : createdBlocks) {
			if ( newBlock.IsDynamicParamCreate() ) {
				this.FDynamicBlockList.add(newBlock);
				BuldDynamicParams( newBlock );
			}
			ModelLanguageBuilder builder = new ModelLanguageBuilder( this );
			builder.UpdateBlock(newBlock);
		}
		
		for (ModelBlock newBlock : createdBlocks) {
			newBlock.ApplyNodeInformation();
			newBlock.InitStatechart();
			
			ModelTimeManager.getTimeManager().AddNewElement( newBlock  );
			newBlock.SetTimeManager(ModelTimeManager.getTimeManager());
		} 
		for (ModelBlock newBlock : createdBlocks) {
			if ( newBlock.IsDynamicParamCreate() ) {
				((ModelDynamicBlock) newBlock).SetDynamicLinker();				
			}
		}
		return createdBlocks;
	}
	
	private ModelBlock getBlock( List<ModelBlock> blockList, ModelBlock blockToCompare ){
		for (ModelBlock block : blockList) {
			if ( block.GetName().equals(blockToCompare.GetName()) && block.GetOwner() == blockToCompare.GetOwner()) {
				return block;				
			}
		}
		return null;
	}
	
	private void addBlocksAsEtalon(List<ModelBlock> newBlocskList) throws ScriptException, ModelException{
		if ( newBlocskList == null || newBlocskList.isEmpty() ) {
			return;
		}
		ModelElementContainer allElements = ModelTimeManager.getTimeManager().getFullElementsList();
		int i = 0;
		ModelElement element = allElements.get(i);
		while ( i <  allElements.size()) {
			element = allElements.get(i);
			if ( element instanceof ModelDynamicBlock) {
				ModelDynamicBlock block = (ModelDynamicBlock) element;
				ModelBlock etalonBlock = block.GetEtalon();
				if (etalonBlock == null) {
					throw new ScriptException("Нет эталона для блока " + block.GetFullName());
				}
				ModelBlock createdBlock = getBlock(newBlocskList, etalonBlock);
				if (createdBlock != null) {
					block.AddSource(createdBlock);
				}
			}
			i++;			
		}
	}

	@Override
	public int createNewBlock(String blockName) throws ScriptException{
		ModelBlock block = Get(blockName, 0);
		if ( block == null ) {
			throw new ScriptException("Отсутствует блок \"" + blockName + "\"");
		}
		List<ModelBlock> blocksToCreate = new ArrayList<ModelBlock> ();
		Map<String, ModelBlock> blockMap = new HashMap<String, ModelBlock> ();
		blocksToCreate .add(block);
		blockMap.put(block.GetFullName(), block);		
		try {
			List<ModelBlock> otherBlocks = getLinkedBlockBySelfIndex(block, blockMap); 
			blocksToCreate.addAll( otherBlocks );
			 List<ModelBlock> newBlocks = createBlocks(blocksToCreate, blockName);
			addBlocksAsEtalon(newBlocks);
		} catch (ModelException e) {
			throw new ScriptException(e.getMessage());			
		}
		
		return 0;
	}

}

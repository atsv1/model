package mp.elements;

import mp.parser.*;
import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;

import java.util.ArrayList;
import java.util.HashMap;
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
  private ModelAttributeReader FAttrReader = null;
  private Vector<ModelBlock> FDynamicBlockList = null;
  private ModelTimeManager FTimeManager = null;
  
  private int FStepDelay = 0;
  private int noForkStepDelay = 0;

  /* ����, ������������ ����������� ���������� ������. ������������ ��� ������������ ������ ������*/

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
    return (ModelBlock) FBlockList.Get( aBlockName, aBlockIndex );
  }

  public void AddElement(ModelElement aElement) throws ModelException {
    FBlockList.AddElement( aElement );
  }

  /** Âîçâðàùàåòñÿ êîëè÷åñòâî áëîêîâ, èìåþùèõñÿ â ìîäåëè
   *
   * @return êîëè÷åñòâî áëîêîâ
   */
  public int size(){
    return FBlockList.size();
  }

  /**×òåíèå øàãà ìîäåëèðîâàíèÿ èç ôàéëà ìîäåëè. Øàã ñ÷èòûâàåòñÿ è çàïèñûâàåòñÿ â ïåðåìåííóþ FTimeIncrement.
   * Åñëè øàã ìîäåëèðîâàíèÿ â ìîäåëè îòñóòñòâóåò, òî óêàçàííàÿ ïåðåìåííàÿ  ñîçäàåòñÿ ñî çíà÷åíèåì 1.
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
      ModelException e = new ModelException("Íåâåðíûé ôîðìàò øàãà ìîäåëèðîâàíèÿ â ìîäåëè \"" + this.GetName() + "\"");
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
      ModelException e1 = new ModelException("Íåâîçìîæíî ïðåîáðàçîâàòü áëîê \"" + aBlock.GetFullName() +
              "\" â áëîê ñ äèíàìè÷åñêè ôîðìèðóåìûìè ïàðàìåòðàìè");
      throw e1;
    }
    ModelLanguageBuilder.AddSelfIndexVariable( aBlock, this );
    block.BuildParams();
  }

  /**Ôîðìèðóåì ñïèñîê áëîêîâ, äëÿ êîòîðûõ íóæíî ïðîèçâîäèòü äèíàìè÷åñêîå ôîðìèðîâàíèå ïàðàìåòðîâ
   * Ñïèñîê áóäåò õðàíèòüñÿ â ïîëå FDynamicBlockList. Åñëè â ìîäåëè íåò òàêèõ áëîêîâ, òî FDynamicBlockList îñòàíåòñÿ ðàâíûì null
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

  /** Ìåòîä ïðîèçâîäèò ïîñòðîåíèå äèíàìè÷åñêèõ ïàðàìåòðîâ äëÿ âñåõ áëîêîâ, êîòîðûå ïîïàëè â îáúåêò FDynamicBlockList
   */
  private void BuildAllDynamicParams() throws ModelException {
    //Ïîêà ïðîèçâîäèòñÿ ïðîñòîé ïåðåáîð áëîêîâ. Êîãäà íà÷íóòñÿ ñîåäèíÿòüñÿ ìóëüòèïëåêñîð ñ ìóëüòèïëåêñîðîì, ýòî ïðèäåòñÿ
    // ïåðåäåëàòü
    //@todo ïåðåäåëàòü, êîãäà áóäóò ñîåäèíÿòüñÿ ìóëüòèïëåêñîð ñ ìóëüòèïëåêñîðîì
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
    // ìîäåëè èç ñåêöèè ParallelModel
    if ( parallelModelList != null && !parallelModelList.isEmpty() ) {
    	for (Model subModel : parallelModelList) {
    		subModel.InitAllBlockStatecharts();
    	}
    }

  }

  public void ApplyNodeInformation() throws ModelException{
    PrepareDynamicBlock();
    FAttrReader = ServiceLocator.GetAttributeReader();
    //ïåðåäàåì âî âñå îáúåêòû ðàñøèðèòåëü ÿçûêà
    ModelLanguageBuilder builder = new ModelLanguageBuilder( this );
    try{
      builder.UpdateModelElements();
    } catch (ScriptException e1){
      ModelException e = new ModelException(e1.getMessage());
      throw e;
    }
    int i = FBlockList.size()-1;
    //âûçûââàåì ó âñåõ îáúåêòîâ ìåòîä ApplyNodeInformation() - ÷òåíèå èíôîðìàöèè èç íîäû
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

 /**Âûïîëíåíèå òàêòîâ ìîäåëè. Òàêòû âûïîëíÿþòñÿ íà îñíîâàíèè ïîòðåáíîñòè áëîêîâ â âûïîëíåíèè òàêòà
  * è íà îñíîâàíèè øàãà òàêòà, çíà÷åíèå êîòîðîãî áûëî çàëîæåíî ïðè ñîçäàíèè ìîäåëè
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

  /**Óñòàíîâêà ôëàãà, ðàçðåøàþùåãî âûïîëíåíèå ìîäåëè
   * Èñïîëüçóåòñÿ äëÿ ïðèîñòàíîâêè ðàáîòû ìîäåëè
   *
   * @param aEnableExec true - âûïîëíåíèå ìîäåëè ðàçðåøåíî, false - ìîäåëü íå âûïîëíÿåòñÿ
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

 /** Ôóíêöèÿ âîçâðàùàåò çíà÷åíèå òåêóùåãî ìîäåëüíîãî âðåìåíè
  *  Âíèìàíèå! Âîçâðàùàåòñÿ èìåííî òîò ýêçåìïëÿð êëàññà, êîòîðûé äåéñòâèòåëüíî èñïîëüçóþòñÿ â ìîäåëè. Ïîýòîìó
  *  íåæåëàòåëüíî èçìåíÿòü çíà÷åíèÿ âíóòðè ïîëó÷åííîãî îáúåêòà.
  * @return Çíà÷åíèå òåêóùåãî ìîäåëüíîãî âðåìåíè
  */
  public ModelTime GetCurrentTime(){
    return FCurrentModelTime;
  }

  public ModelTime GetModelStep(){
    return FTimeIncrement;
  }

  /** Ôóíêöèÿ âîçâðàùàåò èíäåêñ ïåðåäàííîãî â ïàðàìåòðå áëîêà. Çäåñü èíäåêñ - ýòî íå ïîðÿäêîâûé íîìåð áëîêà
   * â îáùåì ñïèñêå áëîêîâ, à ïîðÿäêîâûé íîìåð áëîêà â ñïèñêå áëîêîâ ñ íàçâàíèåì, òàêèì æå, êàê è ó ïåðåäàííîãî
   * â ïàðàìåòðå áëîêà.
   *
   * @param aBlock - áëîê, èíäåêñ êîòîðîãî íåîáõîäèìî îïðåäåëèòü
   * @return - èíäåêñ áëîêà â ìàññèâå áëîêîâ ñ îäèíàêîâûì èìåíåì. Âîçâðàùàåòñÿ 0, åñëè áëîê ïðèñóòñâóåò â
   * åäèíñòâåííîì ÷èñëå. -1 - åñëè òàêîé áëîê îòñóòñòâóåò
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
        ScriptException e1 = new ScriptException( "Îøèáêà ïðè óñòàíîâêå íà÷àëüíûõ çíà÷åíèé â áëîêå \"" + currentBlock.GetFullName() + "\" " + e.getMessage() );
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
      ScriptException e = new ScriptException("Îòñóòñòâóåò áëîê \"" + aBlockName + "\" ñ èíäåêñîì \"" + Integer.toString( aBlockIndex ) + "\"");
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
      ScriptException e = new ScriptException("Îòñóòñòâóåò áëîê \"" + aBlockName + "\"");
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
      ScriptException e = new ScriptException("Îòñóòñòâóåò ïàðàìåòð \"" + aParamName + "\" â áëîêå \"" + aBlockName + "\"");
      throw e;
    }
    ModelInputBlockParam inputParam = null;
    if (!( param instanceof  ModelInputBlockParam)) {
      ScriptException e = new ScriptException("Ïàðàìåòð \"" + aParamName + "\" äîëæåí áûòü âõîäíûì ïàðàìåòðîì");
      throw e;
    }
    inputParam = (ModelInputBlockParam) param;
    ModelExecutionManager manager = ModelExecutionContext.GetManager( aModelToConnect );
    if ( manager == null ){
      ScriptException e = new ScriptException("Â ñèñòåìå îòñóòñòâóåò ìîäåëü \"" + aModelToConnect +
              "\". Âûïîëíåíèå îïåðàöèè ReConnect íåâîçìîæíî");
      throw e;
    }
    if ( !( manager instanceof Model ) ){
      ScriptException e = new ScriptException("Âûïîëíåíèå îïåðàöèè ReConnect íåâîçìîæíî. Ìîäåëü äëÿ êîííåêòà íå ÿâëÿåòñÿ ìîäåëüþ");
      throw e;
    }
    Model modelToConnect = (Model) manager;
    ModelBlock blockToConnect = modelToConnect.Get(aBlockToConnect, aBlockIndexToConnect);
    if ( blockToConnect == null ){
      ScriptException e = new ScriptException("Îòñóòñòâóåò áëîê \"" + aBlockToConnect + "\"");
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
      ScriptException e = new ScriptException("Îòñóòñòâóåò ïàðàìåòð \"" + aParamToConnect + "\" â áëîêå \"" + aBlockToConnect + "\"");
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

	    	// �������� � ���������� ��������� ������ ������ ��������������� ����� ���� ������ ����

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

}

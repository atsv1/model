package mp.elements;

import mp.parser.ScriptException;
import mp.parser.ScriptLanguageExt;
import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;

import java.util.Vector;

/**
 * User: саша
 * Date: 30.05.2008
 */
public class ModelAggregator extends ModelDynamicBlock {
  private boolean FIsNodesApplied = false;

  private Vector FValueNodes;
  private Vector FValueExecutors = null;

  private Vector FFunctionNodes;
  private Vector FFunctionExecutors = null;

  private Node FEnableNode = null;
  private EnableExecutor FEnableExecutor = null;

  private boolean FIsBlockAdded = false;

  private String FEtalonName = null;
  private String FOwnerName = null;

  private boolean FIsExecutorsReady = false;
  private ModelBlockParam FQueueSizeParam = null;

  public ModelAggregator(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FQueueSizeParam = new ModelBlockParam(this, ModelConstants.GetMupltiplexorQueueSizeVarName(), ServiceLocator.GetNextId()) {
      protected void UpdateParam() throws ScriptException, ModelException {
        GetVariable().SetValue( GetQueueSize() );
      }

      public boolean IsNeedRuntimeUpdate() {
        return false;
      }
    };
    try {
      this.AddInnerParam( FQueueSizeParam );
      FQueueSizeParam.SetVarInfo("integer","0");
    } catch (ModelException e) { }
  }

  public void SetDynamicLinker() throws ModelException {

  }

  public ModelBlock GetDynamicBlockOwner() {
    return null;
  }

  public String GetDynamicBlockEtalonName() {
    return null;
  }

  private void ExecuteValueScripts() throws ScriptException {
    int i = 0;
    if ( FValueExecutors == null ){
      return;
    }
    int size = FValueExecutors.size();
    ValueExecutor executor;
    while ( i < size ){
      executor = (ValueExecutor) FValueExecutors.get( i );
      executor.ExecuteScript();
      i++;
    }
  }

  private void ExecuteFunctions() throws ScriptException, ModelException {
    if ( FFunctionExecutors == null || FFunctionExecutors.size() == 0 ){
      return;
    }
    int i = 0;
    MultiBlockExecutor func = null;
    int size = FFunctionExecutors.size();
    while ( i < size ){
      func = (MultiBlockExecutor) FFunctionExecutors.get( i );
      func.ExecuteScript();
      i++;
    }
  }

  public void Execute() throws ModelException, ScriptException {
    if ( !FIsBlockAdded ) {
      AddAllBlock( FEtalonName );
      FIsBlockAdded = true;
    }
    if ( !FIsExecutorsReady ){
      PrepareAllExecutors();
      FIsExecutorsReady = true;
    }
    if ( FEnableExecutor != null ){
      FEnableExecutor.ExecuteScript();
    }
    ExecuteValueScripts();
    ExecuteFunctions();
    FQueueSizeParam.UpdateParam();
  }

  public void Execute(ModelTime aCurrentTime) throws ModelException, ScriptException {
    Execute();
    AddSelfToTimeManager( aCurrentTime );
  }

  public void PrintExecutionTime() {

  }

  private void PrepareExecutorsList( Vector aExecutors ) throws ModelException {
    if ( aExecutors == null || aExecutors.size() == 0 ){
      return;
    }
    int i = 0;
    MultiBlockExecutor executor;
    int size = aExecutors.size();
    while ( i < size ){
      executor = (MultiBlockExecutor) aExecutors.get( i );
      if ( executor instanceof ValueExecutor ){
        ((ValueExecutor)executor).SetEnableExecutor( FEnableExecutor );
      }
      try {
        executor.UpdateServiceInformation();
      } catch ( Exception e ){
         ModelException e1 = new ModelException("Ошибка в элементе \"" + executor.GetResultFullName() + "\": " + e.getMessage());
        throw e1;
      }
      i++;
    }
  }

  private void PrepareAllExecutors() throws ModelException {
    if ( FEnableExecutor != null ){
      try {
        FEnableExecutor.UpdateServiceInformation();
      } catch ( Exception e ){
        ModelException e1 = new ModelException("Ошибка при обработка разрешающего скрипта в блоке \"" + GetFullName() + "\": " + e.getMessage());
        throw e1;
      }
    }
    PrepareExecutorsList( FValueExecutors );
    PrepareExecutorsList( FFunctionExecutors );
  }

  private void AddSourceBlock( Vector aExecutorList, ModelBlock aBlock ) throws ModelException {
    if ( aExecutorList == null || aExecutorList.size() == 0 ){
      return;
    }
    int i = 0;
    MultiBlockExecutor executor;
    int size = aExecutorList.size();
    while ( i < size ){
      executor = (MultiBlockExecutor) aExecutorList.get( i );
      executor.AddResourceBlock( aBlock );
      i++;
    }
  }

  /** Педаваемый блок добавляется в:
   * - EnableExecutor
   * - во все ValueExecutor-ы
   * - во все FunctionExecutor-ы
   * @param aSourceBlock
   * @throws ModelException
   */
  public void AddSource( ModelBlock aSourceBlock ) throws ModelException{
    if ( FEnableExecutor != null ){
      FEnableExecutor.AddResourceBlock( aSourceBlock );
    }
    AddSourceBlock( FValueExecutors, aSourceBlock );
    AddSourceBlock(FFunctionExecutors , aSourceBlock );
  }

  protected void ReCreateAllInputParams() throws ModelException {
    ClearInpParamList();
    if ( FEtalon != null ) {
      /**Параметры из выбираемых блоков должны присоединяться к мультиплексору самостоятельно, именно поэтому
       * второй параметр вызываемого метода равен false
       */
      ReCreateInputParams( FEtalon, false );
    }
    if ( FDynamicOwner != null ){
      ReCreateInputParams(FDynamicOwner, true );
    }
  }

  /** метод читает ноды, относящиеся к разрешающему значению, значению функция и самих агрегатных функций.
   * Ноды не обрабатываются, а просто читаются и заносятся в разные списки
   *
   */
  private void ReadNodes() throws ModelException{
    Node node = GetNode();
    NodeList childNodes = node.getChildNodes();
    if ( childNodes == null || childNodes.getLength() == 0 ){
      ModelException e = new ModelException("Ошибка в аггрегаторе \"" + GetFullName() + "\": отсутствуют все внутренности");
      throw e;
    }
    int i = 0;
    Node childNode;
    String nodeName;
    while ( i < childNodes.getLength() ){
      childNode = childNodes.item( i );
      if ( childNode.getNodeType() == Node.ELEMENT_NODE ){
        nodeName = childNode.getNodeName();
        if ( "Code".equalsIgnoreCase( nodeName ) ){
          FEnableNode = childNode;
        }
        if ( "Value".equalsIgnoreCase( nodeName ) ){
          FValueNodes.add( childNode );
        }
        if ( "Function".equalsIgnoreCase( nodeName ) ){
          FFunctionNodes.add( childNode );  
        }
      }
      i++;
    }
  }

  /**Обработка нод Value
   *
   */
  private void ProcessValueNodes() throws ModelException {
    int i = 0;
    Node valueNode;
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    String valueName;
    String valueType;
    String initValue;
    ModelBlockParam valueParam;
    ScriptLanguageExt ext = this.GetLanguageExt();
    if ( ext == null ){
      ModelException e = new ModelException("Ошибка в блоке \"" + GetFullName() + "\": отсутствует расширитель языка");
      throw e;
    }
    int nodesCount = FValueNodes.size();
    if ( nodesCount > 0 ){
      FValueExecutors = new Vector();
    }
    ValueExecutor ve;
    String source = null;
    while ( i < nodesCount ){
      valueNode = (Node) FValueNodes.get( i );
      attrReader.SetNode( valueNode );
      valueName = attrReader.GetAttrName();
      valueType = attrReader.GetAttrParamType();
      initValue = attrReader.GetAttrInitValue();

      valueParam = new ModelBlockParam( this, valueName, ServiceLocator.GetNextId() ) {
        protected void UpdateParam() throws ScriptException, ModelException {
        }

        public boolean IsNeedRuntimeUpdate() {
          return false;
        }
      };
      valueParam.SetVarInfo( valueType, initValue );
      this.AddInnerParam( valueParam );
      try {
        ext.AddVariable( valueParam.GetVariable() );
      } catch (ScriptException e) {
         ModelException e1 = new ModelException("Ошибка в блоке \"" + GetFullName() + "\": " + e.getMessage());
         throw e1;
      }

      source = ModelAttributeReader.GetSourceCode( valueNode );
      ve = new ValueExecutor( this, valueParam, source );
      FValueExecutors.add( ve );
      i++;
    }
  }

  /**Обработка разрешающего скрипта
   *
   */
  private void ProcessEnableNode() throws ModelException {
    if ( FEnableNode == null ){
      return;
    }
    //разрешающий скрипт есть. создаем параметр, в котором будет храниться текущее разрешение
    ModelBlockParam enableParam = new ModelServiceParam( this, "enable", ServiceLocator.GetNextId() );
    enableParam.SetVarInfo("boolean", "false");
    try {
      this.GetLanguageExt().AddVariable( enableParam.GetVariable() );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( "Ошибка в блоке \"" + GetFullName() + "\": " + e.getMessage() );
      throw e1;
    }
    this.AddInnerParam( enableParam );
    FEnableExecutor = new EnableExecutor( this, enableParam, ModelAttributeReader.GetSourceCode( FEnableNode ) );
  }

  private void CreateInnerFunctionParams() throws ModelException {
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    String functionValueName;
    String functionValueType;
    String initValue;
    int i = 0;
    Node funcNode;
    while ( i < FFunctionNodes.size() ){
      funcNode = (Node) FFunctionNodes.get( i );
      attrReader.SetNode( funcNode );
      functionValueName = attrReader.GetAttrName();
      functionValueType = attrReader.GetAttrParamType();
      initValue = attrReader.GetAttrInitValue();
      ModelBlockParam functionParam;
      functionParam = new ModelBlockParam( this, functionValueName, ServiceLocator.GetNextId() ) {
        protected void UpdateParam() throws ScriptException, ModelException {   }

        public boolean IsNeedRuntimeUpdate() {
          return false;
        }
      };
      functionParam.SetVarInfo( functionValueType, initValue );
      this.AddInnerParam( functionParam );
      i++;
    }

  }

  private MultiBlockExecutor GetNewFunctionExecutor( Node aFunctionNode ) throws ModelException {
    if ( aFunctionNode == null ){
      return null;
    }
    String funcType = null;
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    attrReader.SetNode( aFunctionNode );
    funcType = attrReader.GetAggregatorFunctionType();
    String functionValueName;
    functionValueName = attrReader.GetAttrName();
    ModelBlockParam functionParam = (ModelBlockParam) this.Get( functionValueName );
    if ( funcType == null ){
      return null;
    }
    if ( funcType.equalsIgnoreCase("summ") ){

      FunctionExecutorSumm result = new FunctionExecutorSumm( this, functionParam, "" );
      result.SetParameterName( attrReader.GetValueAttr() );
      result.SetEnableExecutor( FEnableExecutor );
      return result;
    }
    return null;
  }

  private void ProcessFunctionNodes() throws ModelException {
    int i = 0;
    Node functionNode = null;
    if ( FFunctionNodes == null || FFunctionNodes.size() == 0 ){
      return;
    }
    FFunctionExecutors = new Vector();
    MultiBlockExecutor functionExecutor = null;
    while ( i < FFunctionNodes.size() ){
      functionNode = (Node) FFunctionNodes.get( i );
      functionExecutor = GetNewFunctionExecutor( functionNode );
      if ( functionExecutor != null){
        FFunctionExecutors.add( functionExecutor );
      }
      i++;
    }
  }

  public void BuildParams() throws ModelException{
    super.BuildParams();
    FValueNodes = new Vector(5);
    FFunctionNodes = new Vector(5);
    ReadNodes();
    CreateInnerFunctionParams();
  }

  public void ApplyNodeInformation() throws ModelException {
    if ( FIsNodesApplied ){
      return;
    }
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    ProcessValueNodes();
    ProcessEnableNode();
    ProcessFunctionNodes();
    attrReader.SetNode( this.GetNode() );
    FEtalonName = attrReader.GetDynamicEtalonName();
    FOwnerName = attrReader.GetDynamicOwnerName();
    FIsNodesApplied = true;
  }

  protected MultiBlockExecutor GetEnableExecutor(){
    return FEnableExecutor; 
  }

  protected int GetValueExecutorsCount(){
    if ( FValueExecutors == null ){
      return 0;
    }
    return FValueExecutors.size();
  }

  protected ValueExecutor GetValueExecutor(int aExecutorIndex){
    if ( aExecutorIndex >= FValueExecutors.size() ){
      return null;
    }
    return (ValueExecutor) FValueExecutors.get( aExecutorIndex );
  }

  protected ValueExecutor GetValueExecutorByResultName( String aResultName ){
    if ( aResultName == null || aResultName.equalsIgnoreCase("") ){
      return null;
    }
    if ( FValueExecutors == null || FValueExecutors.size() == 0 ){
      return null;
    }
    int i = 0;
    ValueExecutor executor;
    while ( i < FValueExecutors.size() ){
      executor = (ValueExecutor) FValueExecutors.get( i );
      if ( executor.GetResultName().equalsIgnoreCase( aResultName ) ){
        return executor;
      }
      i++;
    }
    return null;
  }

  protected int GetFunctionsCount(){
    if ( FFunctionExecutors == null || FFunctionExecutors.size() == 0 ){
      return 0;
    }
    return FFunctionExecutors.size();
  }

  protected MultiBlockExecutor GetFunctionExecutor( int aFunctionIndex ){
    if ( FFunctionExecutors == null || FFunctionExecutors.size() <=  aFunctionIndex ){
      return null;
    }
    return (MultiBlockExecutor) FFunctionExecutors.get( aFunctionIndex );

  }

  private int GetQueueSize() throws ModelException {
    int result = 0;
    if ( FEnableExecutor != null ){
      int i = 0;
      ExistsService serv = FEnableExecutor.GetEnableArray();
      int size = FEnableExecutor.GetResourceCount();
      while ( i < size ){
        if ( serv.IsExistsInList( i ) ){
          result++;  
        }
        i++;
      }
      return result;
    }
    if ( FFunctionExecutors == null || FFunctionExecutors.size() == 0 ){
      return 0;
    }
    MultiBlockExecutor executor = (MultiBlockExecutor) FFunctionExecutors.get( 0 );
    return executor.GetResourceCount();
  }
  
}

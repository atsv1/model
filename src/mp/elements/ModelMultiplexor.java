package mp.elements;

import mp.parser.*;
import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;

import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * User: atsv
 * Date: 23.12.2006
 *
 * ������ ����� ��������� ��������� �����-��������������.
 * ��� ��������, ���:
 * 1. ������ ���� ��� ������� � ���� ������� � �������� ���������, �������������� �����������, ���������� ��
 * ���������� ����� (�.�. ������ �����, ������� ����� �������� ���������� ��� ���� ������, ������� ����� ��������
 * ������� ��������������)
 * 2. ������ ���� �������� ���� ��� ��������� �� ����� ������ � ����������� �������� �� �� ���� �����(�.�. )
 */
public class ModelMultiplexor extends ModelDynamicBlock{

  private ScriptParser FEnableParser = null;
  private ModelElementContainer FEnableParams = null;
  private ModelBlockParam FEnableParam = null;

  private ModelBlockParam FCriteriaParam = null;
  private ScriptParser FCriteriaParser = null;
  private ModelElementContainer FCriteriaParams = null;

  private ScriptParser FBeforeStartParser = null;
  private ModelElementContainer FBeforeStartParams = null;
  private ModelBlockParam FBeforeStartParam = null;

  private LinkedBlockRecord[] FEnabledElementsArray = null;


  private static String[] FEventTypes = {"Enabled", "NotEnabled", "MaxCriteria", "NotMaxCriteria"};
  private String[] FExistsEvents = null;

  /**�������� ������ ������ ������� ��������������
   */
  private ModelBlockParam FQueueSizeParam = null;

  protected Vector FSourceList = null;
  private int FCurrentPointer  = 0;

  private static final String SCRIPT_TYPE_ENABLE = "Enable";
  private static final String SCRIPT_TYPE_CRITERIA = "Criteria";
  private static final String SCRIPT_TYPE_BEFORE_START = "BeforeStart";

  private boolean FIsNodeRead = false;
  private boolean FIsParamsBuild = false;
  private boolean FIsSelfParamsPrepared = false;

  private ModelMultiplexorLinker FMuxLinker = null;
  private ModelMultiplexor FMux = null;

  private long FExecDuration = 0;
  private long FExecCount = 0;
  private long FEnableExecCount = 0;
  private long FEnableExecDuration = 0;
  private long FCriteriaExecCount = 0;
  private long FCriteriaExecDuration = 0;


  public ModelMultiplexor(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FEnableParam = new ModelBlockParam(this, ModelConstants.GetMultiplexorEnableVarName(), ServiceLocator.GetNextId()) {
      protected void UpdateParam() throws ScriptException, ModelException {
        //����� ������ �� ������
      }

      public boolean IsNeedRuntimeUpdate() {
        return false;
      }
    };
    try {
      this.AddInnerParam( FEnableParam );
      FEnableParam.SetVarInfo("boolean","true");
    } catch (ModelException e) {}

    FCriteriaParam = new ModelBlockParam( this, ModelConstants.GetMultiplexorCriteriaVarName(), ServiceLocator.GetNextId() ) {
      protected void UpdateParam() throws ScriptException, ModelException { }

      public boolean IsNeedRuntimeUpdate() {
        return false;
      }
    };
    try {
      this.AddInnerParam( FCriteriaParam );
      FCriteriaParam.SetVarInfo("real","0");
    } catch (ModelException e) {}

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
    FSourceList = new Vector();
    FMux = this;
    FExistsEvents = new String[ FEventTypes.length ];
    int i = 0;
    while ( i < FEventTypes.length ){
      FExistsEvents[i] = null;
      i++;
    }
  }

  /**����� ��������� ���������� ���� ������� ���������� ��������������. � ��� ������������ �������� ��
   * ��������������� ���������� �����, ������� ������� � ���������.
   * ����� ����������� ����� �������� �������� ��� ����������.
   * @param aSourceBlock - �������� ��� ������� ����������. ��� ������ ������ ���� ���� �� ���������� ������, ���������
   * ������ ��� ��� ����� ����������� ������� ��������������.
   * @throws ModelException
   */
  protected void LoadInputVariables( ModelBlock aSourceBlock ) throws ModelException{
    if ( aSourceBlock == null ){
      ModelException e = new ModelException("������ � �������������� \"" + GetName() + "\": ������� ������ ���� ��� �������� ������� ����������");
      throw e;
    }
    ModelInputBlockParam param;
    ModelBlockParam sourceParam;
    int i = 0;
    param = (ModelInputBlockParam) this.GetInpParam( i );
    int muxOwnerId = -1;
    if ( FDynamicOwner != null ){
      muxOwnerId = FDynamicOwner.GetElementId();
    }
    while ( param != null ){
      sourceParam = aSourceBlock.GetOutParam( param.GetNameIndexObj() );
      if ( sourceParam == null ){
        /** � ����� ��� ������ ��������� ���������, ����� ������������ � ������ ������� ���������� ��������������
         * ����� ����� ���� ��������� ������:
         * 1. � �����, ������������������ � �������������� ��� "���������� ����", ����������� ���������, �������
         * ������������ � ��������
         * 2. ������� �������� �������� ���������� ������� ��������� ��������������. �������� ����������� � ���, ���
         *  ��� �������� ���������� ��������� �������������� ��������� �� ������� � ��������������. ���� ��������������
         * ����������� ����� ������� ��������� �� ������� ���������� ����� � ��������, ����� ������������ ���� ��������
         * (�� �� ��������) � ����� ���������� ����������.
         * ������ ������� �������� ���������, ������ ����������. ������������� ��� ��� ��� � ����������, � �����
         * ������� ��������� ������ ���������� ������
        */
        if ( FDynamicOwner != null ){
          sourceParam = FDynamicOwner.GetOutParam( param.GetNameIndexObj() );
          if ( sourceParam == null ){
            ModelException e = new ModelException("������ � �������������� \"" + GetName() + "\": ����������� �������� �������� \"" +
            param.GetName() + "\" � ����� \"" + aSourceBlock.GetName() + "\"");
            throw e;
          }
        }
      }
      if ( sourceParam == null ){
        ModelException e = new ModelException("������ � �������������� \"" + GetName() + "\": ����������� �������� �������� \"" +
             param.GetName() + "\" � ����� \"" + aSourceBlock.GetName() + "\"");
        throw e;
      }
      /**�������� ����� ��� ����, ����� �� �������� �������� �������  ����������, ������� ������� �� ��������� ��������
       * ���������� ��������� ��������������. ������� �������, �������� ����� ������������ ������ � �� �����
       * ��������������, ������� ����������� � �������� ���������� ������
       */
      if ( !IsOwnerInpParam( param, muxOwnerId ) ){
        try {
          param.GetVariable().StoreValueOf( sourceParam.GetVariable() );
        } catch (ScriptException e) {
          ModelException e1 = new ModelException( "������ � �������������� \"" + GetName() + "\":" + e.getMessage() );
          throw e1;
        }

      }
      try {
        param.Update();
      } catch (ScriptException e) {
        //e.printStackTrace();
        ModelException e1 = new ModelException("������ � �������������� \"" + GetName() + "\" ��� ���������� �������� ���������: " + e.getMessage());
        throw e1;
      }

      i++;
      param = (ModelInputBlockParam) this.GetInpParam( i );
    }//while
  }

  private static boolean IsOwnerInpParam(ModelInputBlockParam aParam, int ownerId){
    if ( aParam == null ){
      return false;
    }
    ModelElement owner = aParam.GetLinkedElementOwner();
    if ( owner == null ){
      return false;
    }
    if ( ownerId == owner.GetElementId() ){
      return true;
    } else return false;
  }

  /**����� ���������� ���������� ��� ������� ����������, ������� ������� � �������������� �� ���������
   * �������� ���������� ��������� ��������������
   */
  private void UpdateOwnersInpParam() throws ModelException {
    int i = 0;
    int ownerId = FDynamicOwner.GetElementId();
    ModelInputBlockParam param = (ModelInputBlockParam) GetInpParam(i);
    while ( param != null ){
      if ( IsOwnerInpParam(param, ownerId )  ){
        try {
          param.Update();
          //System.out.println("�������� " + param.GetFullName());
        } catch (ScriptException e) {
          ModelException e1 = new ModelException("������ � �������������� \"" + GetFullName() + "\" " + e.getMessage());
          throw e1;
        }
      }
      i++;
      param = (ModelInputBlockParam) GetInpParam(i);
    }
  }

  /**������������ ��������� ������� Enabled � NotEnabled ��� ���� ������
   */
  private void FireEnableEvents() throws ModelException {
    int i;
    boolean isEnableExist = false;
    boolean isNotEnableExist = false;
    String enableEventName = null;
    String notEnableEventName = null;
    i = GetEventTypeIndex("Enabled");
    enableEventName = FExistsEvents[i];
    isEnableExist = ( enableEventName != null );
    i = GetEventTypeIndex("NotEnabled");
    notEnableEventName = FExistsEvents[i];
    isNotEnableExist = ( notEnableEventName != null );
    if ( !(isEnableExist || isNotEnableExist) ){
      return;
    }
    boolean enableValue;
    i = 0;
    LinkedBlockRecord rec = null;
    ModelBlock block;
    while ( i < FSourceList.size()  ){
      rec = (LinkedBlockRecord) FSourceList.get( i );
      enableValue = rec.GetEnableResult();
      block = rec.GetBlock();
      if ( enableValue ){
        if ( isEnableExist ) {
          block.FireEvent( enableEventName );
        }
      } else {
        if ( isNotEnableExist ){
          block.FireEvent( notEnableEventName );
        }
      }
      i++;
    }
  }

  /**����� �������� ������� "MaxCriteria" � "NotMaxCriteria" ��� ���� ������������������ � �������������� ������.
   *
   * @param aMaxCriteriaBlock - ����, ��� �������� ����� ������� ������� "MaxCriteria", ��� ���� ��������� ������
   * ����� ������� ������� "NotMaxCriteria" 
   */
  protected void FireCriteriaEvents( ModelBlock aMaxCriteriaBlock ) throws ModelException {
    boolean isMaxCriteriaExists = false;
    boolean isNotMaxCriteriaExist = false;
    String maxCriteriaName = null;
    String notMaxCriteriaName = null;
    int i = GetEventTypeIndex("MaxCriteria");
    maxCriteriaName = FExistsEvents[i];
    isMaxCriteriaExists = (maxCriteriaName != null);
    i = GetEventTypeIndex("NotMaxCriteria");
    notMaxCriteriaName = FExistsEvents[i];
    isNotMaxCriteriaExist = ( notMaxCriteriaName != null );
    if ( !(isMaxCriteriaExists || isNotMaxCriteriaExist) ){
      return;
    }
    i = 0;
    LinkedBlockRecord rec = null;
    ModelBlock block;
    while ( i <  FSourceList.size() ){
      rec = (LinkedBlockRecord) FSourceList.get( i );
      block = rec.GetBlock();
      if ( block.GetElementId() == aMaxCriteriaBlock.GetElementId() ){
        if ( isMaxCriteriaExists ){
          aMaxCriteriaBlock.FireEvent( maxCriteriaName );
          /*System.out.println("������ MaxCriteria ��� ����� " + aMaxCriteriaBlock.GetFullName() + " ������ " +
                  Integer.toString( aMaxCriteriaBlock.GetIntValue("selfIndex") )  );*/
        }
      } else {
        if ( isNotMaxCriteriaExist ){
          block.FireEvent( notMaxCriteriaName );
        }
      }
      i++;
    }
  }

  private void UpdateSelfParams() throws ScriptException, ModelException {
    ModelBlockParam param = this.GetOutParam( 0 );
    int i = 1;
    while ( param != null ){
      param.Update();
      param = this.GetOutParam( i );
      i++;
    }
  }


  public void Execute() throws ModelException, ScriptException {
    if ( FSourceList.size() == 0 && FMuxLinker != null ){
      FMuxLinker.BuildBlockList();
    }
    UpdateSelfParams();
    ExecuteBeforeStartScript();
    UpdateOwnersInpParam();
    if ( FEnabledElementsArray == null || FEnabledElementsArray.length == 0 ) {
      UpdateEnableFlag();
    } else {
      UpdateEnableFlag_WithCheckPreviousBlock();
    }
    FireEnableEvents();
    UpdateCriteria();
    if ( FMuxLinker != null ){
      FMuxLinker.Link();
    }
  }

  public void Execute(ModelTime aCurrentTime) throws ModelException, ScriptException {
    long startTime = System.nanoTime();
    if ( GlobalParams.ExecTimeOutputEnabled() ){
      System.out.println( "���������� �������������� " + GetFullName() + " ����� " +  aCurrentTime.toString());
    }
    Execute();
    //System.out.println( "mux = " + this.GetFullName() );
    FQueueSizeParam.GetVariable().SetValue( GetQueueSize() );
    AddSelfToTimeManager( aCurrentTime );
    FExecDuration = FExecDuration + System.nanoTime() - startTime;
    FExecCount++;
  }

  public void PrintExecutionTime() {
    int selfIndex = GetIntValue("selfIndex");
    double ms = FExecDuration / 1000000;
    System.out.println( "mux " + this.GetFullName() +"[" + Integer.toString(selfIndex) + "]" +  " count = " +
       Long.toString( FExecCount ) + " duration = " + Double.toString( ms ) + ".ms");
    ms = FEnableExecDuration / 1000000;
    System.out.println("        enable:   count = " + Long.toString( FEnableExecCount ) + " duration = " + Double.toString( ms ));
    ms = FCriteriaExecDuration / 1000000;
    System.out.println("        criteria: count = " + Long.toString( FCriteriaExecCount ) + " duration = " + Double.toString( ms ));
  }

  /**�������� ���� ������������ � �������������� ������� ����������
   */
  private void RemoveAllInputParams() throws ModelException {
    ClearInpParamList();
  }

  /**����� �������� �� ��� ��������� ��������������� ��������� ( LinkedBlockRecord ) ������ ����������, �������
   * ��������� � ������� �������� ������������ ������� � � ������� ��������.
   * @param aEnableParams
   * @param aCriteriaParams
   */
  private void ResetAllUsedParams(ModelElementContainer aEnableParams, ModelElementContainer aCriteriaParams) throws ModelException {
    int i = 0;
    LinkedBlockRecord record = null;
    int size = FSourceList.size();
    while ( i < size ){
      record = (LinkedBlockRecord) FSourceList.get( i );
      record.SetUsedParamsList( aEnableParams,  aCriteriaParams);
      i++;
    }
  }

  protected void ReCreateAllInputParams() throws ModelException {
    RemoveAllInputParams();
    if ( FEtalon != null ) {
      /**��������� �� ���������� ������ ������ �������������� � �������������� ��������������, ������ �������
       * ������ �������� ����������� ������ ����� false 
       */
      ReCreateInputParams( FEtalon, false );
    }
    if ( FDynamicOwner != null ){
      ReCreateInputParams(FDynamicOwner, true );
    }
  }

  public ModelBlock GetEtalon(){
    return FEtalon;
  }

  public ModelBlock GetMuxOwner(){
    return FDynamicOwner;
  }

  public ModelBlock GetDynamicBlockOwner(){
    return GetMuxOwner();
  }

  public  String GetDynamicBlockEtalonName(){
    ModelBlock etalon = GetEtalon();
    if ( etalon != null ) {
      return etalon.GetName();
    } else return null;
  }

  /** ����� ������������ �������� ����������� � ��������� ����� �� ������������ ���������� �����.
   * ������������ ������������ �� ���������� ��������: � ����������� ��������� ������ �������������� ��� ��������
   * ���������, ������� ������������ � ��������� �����.  ������������ ����������� �� ���������� �������� � ����
   * ���������.
   * @param aBlock - ����������� ����.
   * @throws ModelException ���������� ��������� � ��� ������, ���� ��������� ������������ ����������, ���� �� �����
   */
  private void IsAddEnabled( ModelBlock aBlock ) throws ModelException{
    int i = 1;
    ModelBlockParam etalonParam = FEtalon.GetOutParam(0);
    ModelBlockParam param = null;
    String varTypeEtalon = null;
    String varType = null;
    while ( etalonParam != null ){
      param = (ModelBlockParam) aBlock.GetOutParam( etalonParam.GetName() );
      if ( param == null ){
        ModelException e = new ModelException("�������������� ������������ ����� \"" + aBlock.GetFullName() + "\" � ���������� � �������������� \"" +
                GetFullName() + "\"");
        throw e;
      }
      varTypeEtalon = etalonParam.GetVariable().GetTypeName();
      varType = param.GetVariable().GetTypeName();
      if ( !varTypeEtalon.equalsIgnoreCase( varType ) ){
        ModelException e = new ModelException("������ � �������������� \"" + GetFullName() +
                "\". ������������ ����� � ��������� \"" + etalonParam.GetName() + "\".");
        throw e;
      }
      i++;
      etalonParam = FEtalon.GetOutParam( i );
    }
  }


  /**���������� ������ ��������� � ������ ��������� ����������.
   * ���������� ������������ � ��������� ������������ ������ ��������� ����������.
   * @param aSourceBlock - ����������� ��������. ������
   * @throws ModelException
   */
  public void AddSource( ModelBlock aSourceBlock ) throws ModelException{
    if ( FEtalon == null ){
      ModelException e = new ModelException("� �������������� \"" + GetFullName() +
              "\" ����������� ��������� ����. ���������� ��������� ��������� ��� ���������� �����");
      throw e;
    }
    if ( aSourceBlock == null ){
      ModelException e = new ModelException("������� �������� ������ �������� � ������������� \"" + GetFullName() + "\"");
      throw e;
    }
    IsAddEnabled( aSourceBlock );
    AddSourceToList( aSourceBlock );
  }

  /**����� ������ ��� ������� ����:
   * 1. �������� � ������ �������� ��� ������� � �������� ���������� ����� ��������� ����
   * 2. �������� ������ ����������, ������� ������������ � ������� ��� ��������� ���������� 
   *
   * @param aScriptSource - �������� ��� �������
   * @param aUsedParams - � ���� �������� ����� �������� ��� ���������, �� ������� ����� �������� ��������� ������ �������
   * @param aResultElement - ��������, � ������� ����� ������������ ��������� ������ �������. ����� ��� ����,
   * ����� � aUsedParams �� ��������� ��������, ���������� ����������� ������  
   * @throws ModelException
   * @throws ScriptException
   */
  private ScriptParser AddScriptToParser(String aScriptSource,
          ModelElementContainer aUsedParams, ModelBlockParam aResultElement ) throws ModelException, ScriptException{
    ScriptParser result = null;
    aUsedParams.Clear();
    result = ParserFactory.GetParser( FLanguageExt, aScriptSource );
    //aParser.SetLanguageExt( FLanguageExt );
    //aParser.ParseScript( aScriptSource );
    ModelInpParamsIterator iterator = new ModelInpParamsIterator();
    iterator.parser = result;
    iterator.sourceList = this.GetElements();
    iterator.ownerElement = aResultElement;
    ModelAddExecutor executor = new ModelAddExecutor( iterator );
    executor.container = aUsedParams;
    executor.SetUniqueFlag( true );
    executor.Execute();
    return result;
  }

  /** ����� �������� � ����� �������� ��� ������������ �������. �.�. �������, ������� ����� ����������� ��� ����, �����
   * ���������� - ����� �� ������ �������� ����������� � ������� �������� � ��� ����� ������������ �� ��, �����
   * ���� �� ������������� ����������, � ����������� ���������� ��� ������-���� �����������.
   *
   * @param aScriptSource - �������� ��� �������
   * @throws ModelException
   */
  public void AddEnableScript( String aScriptSource ) throws ModelException {
    if ( aScriptSource == null || "".equalsIgnoreCase(aScriptSource)  ){
      ModelException e = new ModelException("������ � �������������� \"" + GetFullName() +
              "\". ������� �������� ������ ����� ������������ �������");
      throw e;
    }
    //FEnableParser = new PascalParser();
    FEnableParams = new ModelElementContainer();
    try {
      FEnableParser = AddScriptToParser( aScriptSource, FEnableParams, FEnableParam );
    } catch (ScriptException e) {
      //e.printStackTrace();
      ModelException e1 = new ModelException("������ � �������������� \"" + GetFullName() +
              "\". ��������� ������������ ������� \"" + e.getMessage() + "\"");
      throw e1;
    }
    ResetAllUsedParams(FEnableParams, FCriteriaParams);
  }

  private void ExecuteBeforeStartScript() throws ModelException{
    if ( FBeforeStartParam == null ){
      return;
    }
    try {
      //FBeforeStartParam.UpdateParam();
      FBeforeStartParser.ExecuteScript();
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������������� \"" + GetFullName() +
              "\" ��� ���������� ����  BeforeStart: " + e.getMessage());
      throw e1;
    }
  }

  /** ����� ��������� ������������ ������������� ���������� � ������� BeforeStart.
   * ������������ ������������� ��������, ��� � ������� BeforeStart ������������ ���� ����������� ���������
   * ��������������, ���� ���������, ��������� �� ������ �����-���������. ��������� ��������� �������� �������������
   * ����������, ��������� �� ��������� ����������� �����, ������ ��� � ����� ������ ����� ������ ������� ����������
   * �������������� 
   *
   * @param aParamsList - ������ ����������, ��� ������� ����� ����������� ��������
   * @throws ModelException
   */
  private void CheckParamsList( ModelElementContainer aParamsList ) throws ModelException{
    if ( aParamsList == null || aParamsList.size() == 0 ){
      return;
    }
    int i = 0;
    ModelBlockParam param = null;
    ModelBlock muxOwner = GetMuxOwner();
    while ( i < aParamsList.size() ){
      param = (ModelBlockParam) aParamsList.get( i );
      if (  param.GetParamPlacementType() == ModelBlockParam.PLACEMENT_TYPE_INPUT ){
        // ������� ��������. ��� ��������� ��������� ����������
        if ( muxOwner.Get( param.GetNameIndexObj() ) == null ){
          // ��������� � ����� ������ ��� � �����-��������� ��������������. ������, �� ��������� � �����-�������,
          // ��� ���� ������
          ModelException e = new ModelException("������ � �������������� \"" + GetFullName() +
                  "\": � ������ BeforeStart ������������ �������� �� ����������� �����. ��� ���������");
          throw e;
        }
      }
      i++;
    }
  }

  public void AddBeforeStartScript( String aScriptSource ) throws ModelException {
    if ( aScriptSource == null || "".equalsIgnoreCase(aScriptSource)  ){
      ModelException e = new ModelException("������ � �������������� \"" + GetFullName() +
              "\". ������� �������� ������ ����� ������� � ������ BeforeStart");
      throw e;
    }
    //FBeforeStartParser = new PascalParser();
    FBeforeStartParams = new ModelElementContainer();
    String beforeStartParamName =  "[" + this.GetName() + "_beforeStart]";
    FBeforeStartParam = new ModelBlockParam( this, beforeStartParamName, ServiceLocator.GetNextId() ) {
      protected void UpdateParam() throws ScriptException, ModelException {
        //����� �������� �� �����������
      }

      public boolean IsNeedRuntimeUpdate() {
        return false;  
      }
    };
    try {
      FBeforeStartParser = AddScriptToParser( aScriptSource, FBeforeStartParams, FBeforeStartParam);
      CheckParamsList( FBeforeStartParams );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������������� \"" + GetFullName() +
              "\". ��������� ������� BeforeStart\"" + e.getMessage() + "\"");
      throw e1;
    }

  }

  public void AddCriteriaScript( String aScriptSource ) throws ModelException{
    if ( aScriptSource == null || "".equalsIgnoreCase(aScriptSource)  ){
      ModelException e = new ModelException("������ � �������������� \"" + GetFullName() +
              "\". ������� �������� ������ ����� ��������� �������");
      throw e;
    }
    //FCriteriaParser = new PascalParser();
    FCriteriaParams = new ModelElementContainer();
    try {
      FCriteriaParser = AddScriptToParser( aScriptSource, FCriteriaParams, FCriteriaParam);
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������������� \"" + GetFullName() +
              "\". ��������� ��������� ������� \"" + e.getMessage() + "\"");
      throw e1;
    }
    ResetAllUsedParams(FEnableParams, FCriteriaParams);
  }

  private LinkedBlockRecord GetBlockRecordById(int id){
    int i = 0;
    LinkedBlockRecord currentRecord = null;
    ModelBlock block;
    while ( i < FSourceList.size() ){
      currentRecord = (LinkedBlockRecord) FSourceList.get(i);
      block = currentRecord.GetBlock();
      if ( id == block.GetElementId() ){
        return currentRecord;
      }
      i++;
    }
    return null;
  }

  private void AddSourceToList(ModelBlock aSourceBlock) throws ModelException {
    int sourceId = aSourceBlock.GetElementId();
    LinkedBlockRecord anotherBlock = GetBlockRecordById( sourceId );
    if ( anotherBlock != null ){
      //����� �������� � ������ ��� ����, ������ ������� �� ���������
      return;
    }
    LinkedBlockRecord newRec = new LinkedBlockRecord( aSourceBlock );
    FSourceList.add( newRec );
    newRec.SetUsedParamsList( FEnableParams, FCriteriaParams );
  }

  private int LoadNearestSourceForEnableScript( int aCurrentPointer ) throws ModelException {
    LinkedBlockRecord record = null;
    int i = aCurrentPointer;
    while ( i < FSourceList.size() ) {
      record = (LinkedBlockRecord) FSourceList.get( i );
      if ( record.IsNeedToExecEnableScript() ){
        LoadInputVariables( record.GetBlock() );
        return i;
      }
      i++;
    }
    return -1;
  }

  private LinkedBlockRecord LoadNextSourceForCriteriaScript( ) throws ModelException{
    LinkedBlockRecord record = null;
    //������� ������������ �������� - ���������� �� ������ FEnabledElementsArray. �.�. - ������� �� � ��������������
    // �������� maxCount, ������� ������������ ���������� ������, ��� ������� ����� ������� �������� ��������.
    if ( FEnabledElementsArray != null && FEnabledElementsArray.length != 0) {
      //���������, �������� �� aCurrentPointer � ������ �������
      if ( FCurrentPointer >= FEnabledElementsArray.length ) {
        return null;
      }
      record = FEnabledElementsArray[ FCurrentPointer ];
      if ( record == null ){
        return null;
      }
      LoadInputVariables( record.GetBlock() );
      FCurrentPointer++;
      return record;
    }

    //������� FEnabledElementsArray �� ����������, ������, ���������� ��� ��������

    int i = FCurrentPointer;
    int size =  FSourceList.size();
    while ( i < size ) {
      record = (LinkedBlockRecord) FSourceList.get( i );
      if (record.GetEnableResult() && record.IsNeedToExecCriteriaScript() ){

        LoadInputVariables( record.GetBlock() );
        FCurrentPointer = i + 1;
        return record;
      }
      i++;
    }
    return null;
  }

  private LinkedBlockRecord LoadFirstForCriteriaScript() throws ModelException {
    FCurrentPointer = 0;
    return LoadNextSourceForCriteriaScript();
  }

  /**������������ �������� ������� ���������� �������������� ������� �� ������� ������������������� � ��������������
   * ���������. � �������� ��������� ���������� ������ �������� �� ������, �� ��� ���� ����������, ����� ����
   * ������������� ������������� ����������� ������. 
   *
   * @return - ������������ true, ���� ������� ���� �� ���� ��������, ��������������� ����������������� ��������, �.�.
   * �� ������� ��������� �������������� ���� ��������� ���� �����-�� ��������. 
   */
  protected boolean LoadFirstForEnableScript() throws ModelException {
    FCurrentPointer = LoadNearestSourceForEnableScript(0);
    return (FCurrentPointer != -1);
  }

  protected boolean LoadNextForEnableScript() throws ModelException {
    FCurrentPointer++;
    FCurrentPointer = LoadNearestSourceForEnableScript( FCurrentPointer );
    return (FCurrentPointer != -1);
  }

  private void SetEnableFlag( boolean aFlagValue ){
    LinkedBlockRecord record;
    int i = 0;
    while ( i < FSourceList.size() ){
      record = (LinkedBlockRecord) FSourceList.get( i );
      record.SetEnableResult( aFlagValue );
      i++;
    }
  }

  private boolean ExecuteEnableScript() throws ModelException{
    if ( FEnableParser == null ){
      return true;
    }
    try {
      FEnableParser.ExecuteScript();
      return FEnableParam.GetVariable().GetBooleanValue();
    } catch (ScriptException e) {
      e.printStackTrace();
      ModelException e1 = new ModelException("������ ��� ���������� ������������ ������� � �������������� \"" +
              GetFullName() + "\": " + e.getMessage());
      throw e1;
    }
  }

  /** ����� ���������� ���������� ������������ ������� ��� ������� �� ������������������ ����������.
   * 
   * @throws ModelException
   */
  protected void UpdateEnableFlag() throws ModelException {
    if ( FEnableParser == null ){
      //����������� ������ �����������. ������, ��� ��������� ����� ����������� � ������� ��������� ��������
      //����������� �� ���� ���������� ����������� ���� � true
      SetEnableFlag(true);
      return;
    }
    //����������� ������ ����. �������� ��������� ��� ��� ������� �� ���������� (��� ������� ���� ������������� ���
    // ���������)
    long start = System.nanoTime();
    boolean loadResult = LoadFirstForEnableScript();
    boolean execResult;
    LinkedBlockRecord record;
    while ( loadResult ){
      execResult = ExecuteEnableScript();
      record = (LinkedBlockRecord) FSourceList.get( FCurrentPointer );
      record.SetEnableResult( execResult );
      record.EnableScriptExecuted();
      loadResult = LoadNextForEnableScript();
      FEnableExecCount++;
    }//while
    FEnableExecDuration = FEnableExecDuration + System.nanoTime() - start;
  }

  /**����� �������� �������� ������� FEnabledElementsArray ���, ����� ������� ��� ��������, �� ���������� null, �
   * �������� � null ��� � ����� �������.
   *
   * @return ���������� ���������� ��������� ��������� �������
   */
  private int ShiftArray(){
    int i = 0;
    LinkedBlockRecord rec = null;
    int shiftValue = 0;
    while ( i < FEnabledElementsArray.length ){
      rec = FEnabledElementsArray[ i ];
      if ( rec == null ){
        shiftValue++;
      } else {
        FEnabledElementsArray[ i - shiftValue ] = rec;  
      }
      i++;  
    }
    return FEnabledElementsArray.length - shiftValue ;
  }

  private boolean IsElementExistsInArray( LinkedBlockRecord aRecord ){
    int i = 0;

    while ( i < FEnabledElementsArray.length ){
      if ( FEnabledElementsArray[i] == null){
        return false;
      }
      if ( FEnabledElementsArray[i] == aRecord ){
        return true;
      }
      i++;
    }
    return false;
  }

  protected void UpdateEnableFlag_WithCheckPreviousBlock() throws ModelException {
    int i = 0;
    LinkedBlockRecord rec = null;
    boolean execResult;
    //���������� ��������, ��� ��������� � �������
    while ( i < FEnabledElementsArray.length ) {
      rec = FEnabledElementsArray[i];
      if ( rec == null ){
        break;
      }
      // ���������, ����� �� ������������� ���� ����
      if ( rec.IsNeedToExecEnableScript() ) {
        LoadInputVariables( rec.GetBlock() );
        execResult = ExecuteEnableScript();
        if ( !execResult ){
          rec.SetEnableResult( false );
          FEnabledElementsArray[i] = null;
        } else {
          rec.SetEnableResult( true );
        }
      }
      i++;
    }//while
    int elementsCount = ShiftArray();
    //����������� ������
    i = 0;
    while (  elementsCount < FEnabledElementsArray.length && i < FSourceList.size()){
      rec = (LinkedBlockRecord) FSourceList.get( i );
      if ( !IsElementExistsInArray( rec ) && rec.IsNeedToExecEnableScript() ){
        LoadInputVariables( rec.GetBlock() );
        execResult = ExecuteEnableScript();
        if ( execResult ){
          rec.SetEnableResult( true );
          FEnabledElementsArray[ elementsCount ] = rec;
          elementsCount++;
        } else {
          rec.SetEnableResult( false );
        }
      }
      i++;
    }
  }

  /** ���������� ���������� ������, ������� ��������� ����������� � �������� ��������. ����� ����� ����� ������ � ���
   * ������, ���� � ������������� ������������ �������� "maxCount" � ������ Enable.
   *
   * @return ���������� ���� ���������� ������, � ������� �������� enable-������� ����� true. ���� �������� maxCount
   * �����������, �� ������������ -1 
   */
  protected int GetEnabledElementsCount(){
    if ( FEnabledElementsArray != null  ) {
      return ShiftArray();
    }
    return -1;
  }

  /**����� ��������� ������ ��������� ��������� (��������) ��� ���� ������������������ � ��������������
   * ���������� (�������� �� ���������, � ������� �������� ������������ ����� ����� false)
   *
   * @throws ModelException
   */
  protected void UpdateCriteria() throws ModelException{
    if ( FCriteriaParser == null ){
      return;
    }
    //int pointer = LoadNextSourceForCriteriaScript(0);
    long start = System.nanoTime();
    LinkedBlockRecord record = LoadFirstForCriteriaScript();
    int enabledCount = GetQueueSize();
    int execCounter = 0;
    while ( record != null ){
      try {
        FCriteriaParser.ExecuteScript();
        record.GetCriteriaValue().StoreValueOf( FCriteriaParam.GetVariable() );
        record.CriteriaScriptExecuted();
        //System.out.println("criteria := " + Integer.toString( FCriteriaParam.GetVariable().GetIntValue() ) );
      } catch (ScriptException e) {
        ModelException e1 = new ModelException("������ � �������������� \"" + GetFullName() +
                "\" ��� ���������� ��������� �������: "  + e.getMessage() );
        throw e1;
      }
      record = LoadNextSourceForCriteriaScript(  );
      FCriteriaExecCount++;
      execCounter++;

      if ( execCounter > enabledCount ) {
        System.out.println( " error " + this.GetFullName() );
      }
    }//while
    FCriteriaExecDuration = FCriteriaExecDuration + System.nanoTime() - start;
  }


  protected ModelBlock GetMaxCriteriaBlock_WithMaxCount() throws ModelException{
    Variable currentMax = new Variable( Double.NEGATIVE_INFINITY );
    int maximumCount = 0;
    int i = 0;
    Variable currentVar;
    LinkedBlockRecord record = null;
    LinkedBlockRecord maxRecord = null;
    int compareRes;
    while ( i < FEnabledElementsArray.length ) {
      record = FEnabledElementsArray[ i ];
      if ( record == null ){
        break;
      }
      currentVar = record.GetCriteriaValue();
      try {
          compareRes = currentVar.Compare( currentMax );
      } catch (ScriptException e) {
        ModelException e1 = new ModelException("������ � �������������� \"" + GetFullName() + "\": " + e.getMessage());
        throw e1;
      }
      if ( compareRes == 1){
        maxRecord = record;
        currentMax = currentVar;
        maximumCount = 0;
      }
      if ( compareRes == 0 ){
        maximumCount++;
      }
      i++;
    }
    try {
      FCriteriaParam.GetVariable().StoreValueOf( currentMax );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }
    if ( maxRecord == null || maximumCount > 1  ) {
      return null;
    }
    FireCriteriaEvents( maxRecord.GetBlock() );
    return maxRecord.GetBlock();
  }


  /** ������� ���������� ����, ��� �������� ��������� ������������ �������� ��������
   *
   * @return - ������������ ���� ������ �� ���� � ������������ ���������, ���� null � ��������� �������:
   * 1. � �������������� ��� �� ������ ���������
   * 2. �� ���� �� ������������������ ���������� �� ����� ������������ �������� (�.�. �� ���� �� ���
   * ������� � ������� ��������)
   * 3. ������������ �������� �������� ������� � ���������� ��������� 
   */
  protected ModelBlock GetMaxCriteriaBlock() throws ModelException{
    if ( FEnabledElementsArray == null || FEnabledElementsArray.length == 0 ){
      return GetMaxCriteriaBlock_WithoutMaxCount(); 
    } else {
      return GetMaxCriteriaBlock_WithMaxCount(); 
    }
  }


  protected  ModelBlock GetMaxCriteriaBlock_WithoutMaxCount() throws ModelException{
    if ( GlobalParams.MuxOutputEnabled()  ){
      System.out.println(" GetMaxCriteriaBlock started. mux: " + GetFullName());
    }
    if ( FSourceList.size() == 0 ){
      return null;
    }
    Variable currentMax = new Variable( Double.NEGATIVE_INFINITY );
    int maximumCount = 0;
    int i = 0;
    Variable currentVar;
    LinkedBlockRecord record;
    LinkedBlockRecord lastRecord = null;
    int compareRes = 0;
    int currentMaxIndex = -1;
    int queueSize = 0;
    while ( i < FSourceList.size() ) {
      record = (LinkedBlockRecord) FSourceList.get( i );
      if ( record.GetEnableResult() ){
        queueSize++;
        currentVar = record.GetCriteriaValue();
        try {
          compareRes = currentVar.Compare( currentMax );
        } catch (ScriptException e) {
          //e.printStackTrace();
          ModelException e1 = new ModelException("������ � �������������� \"" + GetFullName() + "\": " + e.getMessage());
          throw e1;
        }
        lastRecord = record;
        if ( compareRes == 1){
          currentMaxIndex = i;
          currentMax = currentVar;
          maximumCount = 0;

        }
        if ( compareRes == 0 ){
          maximumCount++;
        }
      }
      i++;
    }//while
    if ( maximumCount > 0 || ( currentMaxIndex == -1 ) ){
      if ( GlobalParams.MuxOutputEnabled() ) {
        System.out.println(" mux " + GetFullName() + " no block ");
      }
      try {
        FCriteriaParam.GetVariable().StoreValueOf( currentMax );
      } catch (ScriptException e) { }
      return null;
    }
    record = (LinkedBlockRecord) FSourceList.get( currentMaxIndex  );
    if ( GlobalParams.MuxOutputEnabled() ) {
      System.out.println(" mux " + GetFullName() + " block " + record.GetBlock().toString() );
    }

    try {

      FCriteriaParam.GetVariable().StoreValueOf( currentMax );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }
    FireCriteriaEvents( record.GetBlock() );
    return record.GetBlock();
  }



  /**������� ���������� ���������� ������������������ � �������������� ���������� ������.
   *
   * @return - ���������� ������������������ ����������
   */
  public int GetSourceCount(){
    return FSourceList.size();
  }

  public int GetAvailableSourceCount(){
    int result = 0;
    int i = 0;
    LinkedBlockRecord record;
    while ( i < FSourceList.size() ){
      record = (LinkedBlockRecord) FSourceList.get( i );
      if ( record.GetEnableResult() ){
        result++;
      }
      i++;
    }
    return result;
  }

  private void ReadCode( Node aCodeNode ) throws ModelException {
    NodeList nodes = aCodeNode.getChildNodes();
    if ( nodes == null ){
      return;
    }
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    attrReader.SetNode( aCodeNode );
    String s = attrReader.GetAutomatCodeType();
    if ( s == null || "".equalsIgnoreCase( s ) ){
      ModelException e = new ModelException("����������� �������� ���� ������� � �������� \"" + this.GetFullName() + "\"");
      throw e;
    }
    Node currentNode;
    int i = 0;
    String code = null;
    while ( i < nodes.getLength() ){
      currentNode = nodes.item(i);
      if ( currentNode.getNodeType() == Node.CDATA_SECTION_NODE ){
        code = currentNode.getNodeValue();
        break;
      }
      i++;
    }
    if ( SCRIPT_TYPE_ENABLE.equalsIgnoreCase( s ) ){
      this.AddEnableScript( code );
      int maxEnableCount = attrReader.GetMaxEnableBlockCount();
      if ( maxEnableCount != -1 ) {
        FEnabledElementsArray = new LinkedBlockRecord[ maxEnableCount ];
      }
      return;
    }
    if ( SCRIPT_TYPE_CRITERIA.equalsIgnoreCase( s ) ){
      this.AddCriteriaScript( code );
      return;
    }
    if ( SCRIPT_TYPE_BEFORE_START.equalsIgnoreCase( s ) ){
      AddBeforeStartScript( code );
      return;
    }
    ModelException e = new ModelException("����������� ��� ������� \""+  s +  "\"  � �������� \"" + GetFullName() + "\"");
    throw e;
  }

  private void ReadScripts() throws ModelException{
    Node node = this.GetNode();
    NodeList nodes = node.getChildNodes();
    int i = 0;
    Node currentNode;
    while ( i < nodes.getLength() ){
      currentNode = nodes.item(i);
      if ( currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getNodeName().equalsIgnoreCase("Code") ){
        ReadCode( currentNode );
      }
      i++;
    }
  }

  public void SetDynamicLinker( ) throws ModelException {
    FMuxLinker =  DynamicBlockLinkerFactory.GetLinker( this );
  }

  private static int GetEventTypeIndex(String eventTypeName){
    if ( eventTypeName == null || "".equalsIgnoreCase( eventTypeName ) ){
      return -1;
    }
    int i = 0;
    while ( i < FEventTypes.length ) {
      if ( FEventTypes[i].equalsIgnoreCase( eventTypeName ) ){
        return i;
      }
      i++;
    }
    return -1;
  }

  private void ReadEventList(Node listNode) throws ModelException{
    NodeList nodes = listNode.getChildNodes();
    int i = 0;
    Node node;
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    String eventType = null;
    String procName = null;
    int eventTypeIndex = -1;
    while ( i < nodes.getLength() ){
      node = nodes.item( i );
      if ( node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("Event") ){
        attrReader.SetNode( node );
        eventType = attrReader.GetAttrParamType();
        eventTypeIndex = GetEventTypeIndex( eventType );
        if ( eventTypeIndex == -1 ){
          ModelException e = new ModelException( "������ � �������������� \"" + GetFullName() +
                  "\": ����������� ��� ������� " + eventType );
          throw e;
        }
        procName = attrReader.GetAttrName();
        FExistsEvents[ eventTypeIndex ] = procName;
      }
      i++;
    }
  }

  /** �������� ���������� � ���, ����� ������� ����� ������������
   *
   * @throws ModelException
   */
  private void ReadEventInfo() throws ModelException{
    Node rootNode = GetNode();
    if ( rootNode == null ){
      return;
    }
    NodeList nodes = rootNode.getChildNodes();
    if ( nodes == null ){
      return;
    }
    int i = 0;
    Node node;
    while ( i < nodes.getLength() ){
      node = nodes.item( i );
      if ( node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("EventList") ){
        ReadEventList( node );
        return;
      }
      i++;
    }
  }

  private void SetTransferFlagToMaterialParams( boolean aFlagValue ){
    int i = 0;
    ModelBlockParam param = GetInpParam(i);
    ModelMaterialParam mp = null;
    while ( param != null ){
      if ( param.GetParamType() == ModelBlockParam.PARAM_TYPE_MATERIAL ){
        mp = (ModelMaterialParam) param;
        mp.SetTransferFlag( aFlagValue );
        //System.out.println("SetTransferFlag to " + mp.GetFullName());
      }
      i++;
      param = GetInpParam(i);
    }
  }

  private void ApplySelfParams() throws ScriptException, ModelException {
    ModelBlockParam param = this.GetOutParam( 0 );
    ScriptLanguageExt ext = GetLanguageExt();
    int i = 1;
    while ( param != null ){
      param.SetLanguageExt( ext );
      param.ApplyNodeInformation();
      param = this.GetOutParam( i );
      i++;
    }

  }

  public void ApplyNodeInformation() throws ModelException {
    if ( FIsNodeRead ) {
      return;
    }
    ReadScripts();
    ReadEventInfo();
    FIsNodeRead = true;
    //������ �� ����� ������� ��� ������������ ����������
    SetTransferFlagToMaterialParams(false);
    if ( !FIsSelfParamsPrepared ){
      try {
        ApplySelfParams();
      } catch (ScriptException e) {
        ModelException e1 = new ModelException( e.getMessage() );
        throw e1;
      }
      FIsSelfParamsPrepared = true;
    }
  }

  protected int GetQueueSize(){
    int i = 0;
    LinkedBlockRecord rec = null;
    int result = 0;
    while (i < FSourceList.size()){
      rec = (LinkedBlockRecord) FSourceList.get( i );
      if ( rec.GetEnableResult() ){
        result++;        
      }
      i++;
    }
    return result;
  }

  /**������� ���������� ������������ ���������� ������, ������� ����� ���� �������� � ������� ��������. �������
   * ������� - ������������ ���������� ������, ������� ����� ����� true ����� ������� ������������ ����
   *
   * @return ������������ -1, ���� ���������� ����� ������ �����������, ���� ��������, ������� 0, ����� ����������
   * ������ ���������� 
   */
  protected int GetMaxEnableBlockCount(){
    if ( FEnabledElementsArray == null ) {
      return -1;
    } else {
      return FEnabledElementsArray.length;
    }
  }


  /**����� ������������ ��� �������� ��������������� ������ ��� ������� �� ������������������ � ��������������
   * ���������� ������. 
   *
   */
  protected class LinkedBlockRecord{
    private ModelBlock FBlock = null;// �������� ������, ������������������ � ��������������

    /** ����, ������� ���������, ��� ���������� ����������� ������, ������� ��������� ������ ��������� ��������� ���
     * ������� ���������. ����� ������������� ��������� � ��� ������, ���� ���������� �������� � �����-���� ��
     * ����������, ������������ ��� ������� ����������. 
     */
    private boolean FEnableScriptFlag = true;

    /** ����, ������� ��������� �� ��, ��� ���������� ����������� �������� �������� ��� ������� ���������.
     *  ������������� � ��������� ��������� � ������ ��������� �������� � ����������, ������� ������������ ���
     * ������� ��������� ���������
     */
    private boolean FCriteriaScriptFlag = true;
    private Variable FResultVar = null;
    private boolean FEnableResult = true;
    private ModelElementContainer FEnableParamsList = null;
    private ModelElementContainer FCriteriaParamsList = null;
    

    public LinkedBlockRecord(ModelBlock aBlock){
      FBlock = aBlock;
      FResultVar = new  Variable( Double.MIN_VALUE + 1 );
      FResultVar.SetName(ModelConstants.GetMultiplexorCriteriaVarName());
    }

    public ModelBlock GetBlock(){
      return FBlock;
    }

    private void RemoveChangeListeners( ModelElementContainer aParams ) throws ModelException {
      if ( aParams == null ){
        return;
      }
      int i = 0;
      ModelBlockParam innerParam = null;
      ModelBlockParam param = null;
      while ( i < aParams.size() ){
        innerParam = (ModelBlockParam) aParams.get( i );
        param = (ModelBlockParam) FBlock.Get( innerParam.GetNameIndexObj() );
        param.GetVariable().RemoveChangeListener( this );
        i++;
      }
    }

    private void AddEnableChangeListeners() throws ModelException {
      if ( FEnableParamsList == null ){
        return;
      }
      int i = 0;
      ChangeListener listener = new ChangeListener( this ) {
        public void VariableChanged(VariableChangeEvent changeEvent) {
          FEnableScriptFlag = true;
        }
      };
      ModelBlockParam innerParam = null;
      ModelBlockParam param = null;
      //String innerParamName;
      while ( i < FEnableParamsList.size() ){
        innerParam = (ModelBlockParam) FEnableParamsList.get( i );
        //innerParamName = innerParam.GetName();
        param = (ModelBlockParam) FBlock.Get( innerParam.GetNameIndexObj() );
         if ( param == null ) {
          //� ���������� ����� ������� ��������� ����� �� ����. ����� �������� ������ � ����� ��������������
          param = (ModelBlockParam) FMux.Get( innerParam.GetNameIndexObj() );
        }
        if ( param == null ){
          ModelException e = new ModelException("������ � ���������� EnableChangeListener ��� ��������� \"" +
                  innerParam.GetName() + "\" � �������� \"" + FMux.GetFullName() + "\""  );
          throw e;
        }
        if ( !"enable".equalsIgnoreCase( param.GetName() ) ) {
          param.GetVariable().AddChangeListener( listener );
        }
        i++;
      }
    }

    private void AddCriteriaChangeListeners() throws ModelException {
      if ( FCriteriaParamsList == null ){
        return;
      }
      int i = 0;
      ChangeListener listener = new ChangeListener( this ) {
        public void VariableChanged(VariableChangeEvent changeEvent) {
          FCriteriaScriptFlag = true;
        }
      };
      ModelBlockParam innerParam = null;
      ModelBlockParam param = null;
      //String innerParamName;
      while ( i < FCriteriaParamsList.size() ){
        innerParam = (ModelBlockParam) FCriteriaParamsList.get( i );
        //innerParamName = innerParam.GetName();
        param = (ModelBlockParam) FBlock.Get( innerParam.GetNameIndexObj() );
        if ( param == null ) {
          //� ���������� ����� ������� ��������� ����� �� ����. ����� �������� ������ � ����� ��������������
          param = (ModelBlockParam) FMux.Get( innerParam.GetNameIndexObj() );
        }
        if ( param == null ){
          ModelException e = new ModelException("������ � ���������� CriteriaChangeListener ��� ��������� \"" +
                  innerParam.GetName() + "\" � �������� \"" + FMux.GetFullName() + "\""  );
          throw e;
        }
        if ( !"criteria".equalsIgnoreCase( param.GetName() ) ) {
          param.GetVariable().AddChangeListener( listener );
        }
        i++;
      }
    }

    /**����� ������������ ��� �������� � ����� ������� ����������, ������� ����� �������������� ��� �������
     * ���������� �� ������ �������� � � ������� ������ ��������.
     * � ������ �� ������������ ���������� ������ ���� �������� ���� ��������� �������, ��� ����, ����� ���������
     * ��������������� ������ ������ � ������ �������������. �.�. ��� ��������� �������� � �����-���� ���������.
     * ���� ������, ��� ���� ����� ����� ����������� �����������, �� � ��� ������ ���������� ������� �� "��������� ���������",
     * ������� ���� ��������� � ��������� �� ����������� ������.
     *
     * @param aEnableParams
     * @param aCriteriaParams
     */
    public void SetUsedParamsList( ModelElementContainer aEnableParams, ModelElementContainer aCriteriaParams ) throws ModelException {
      if ( FEnableParamsList != aEnableParams ){
        RemoveChangeListeners( FEnableParamsList );
        FEnableParamsList = aEnableParams;
        AddEnableChangeListeners();
      }
      if ( FCriteriaParamsList != aCriteriaParams ){
        RemoveChangeListeners(FCriteriaParamsList);
        FCriteriaParamsList = aCriteriaParams;
        AddCriteriaChangeListeners();
      }
    }

    /**���� ����� ���������� ���������� ������� ������ ����� ����, ��� �������� ����������� ������, ��� �����,
     * ���������� � ������� ������ ������ ��������� ������ 
     */
    public void EnableScriptExecuted(){
      FEnableScriptFlag = false;
    }

    /** ����� ���������� �������� �����, ������������ �� ������������� ��������� ������������ �������. 
     *
     * @return - ���������� true, ���� ����� ��������� ��� ������� ����� ����������� ������, false - ���� �� �����
     * ��� ���������
     */
    public boolean IsNeedToExecEnableScript(){
      return FEnableScriptFlag;
    }

    /**����� ���������� �������� ����� ����� ���������� �������, �������������� �������� ��������.
     *
     */
    public void CriteriaScriptExecuted(){
      FCriteriaScriptFlag = false;
    }

    /** ������� ���������� �������� ��������, ������� ��������� �� ������������� ��������� �������, ������������
     * �������� ��������
     *
     * @return - ���������� true, ���� ���������� ��������� ������
     */
    public boolean IsNeedToExecCriteriaScript(){
      return FCriteriaScriptFlag;
    }

    public Variable GetCriteriaValue(){
      return FResultVar;
    }

    /**������ ����� ��������� � ������� ��������� ���������� ������������ �������.
     * @param newEnableValue
     */
    public void SetEnableResult( boolean newEnableValue ){
      FEnableResult = newEnableValue;
    }

    /** ������� ���������� ��������� ���������� ������������ ������� (�.�. �������� ��������� Enabled)
     *
     * @return - ���������� true, ���� ������ �������� ����� ���� ����������, �.�. ��� ������� �������� �����
     * ����������� �������� ��������
     */
    public boolean GetEnableResult(){
      return FEnableResult;
    }

  }//class LinkedBlockRecord
  
}

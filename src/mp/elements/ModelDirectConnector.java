package mp.elements;

import mp.parser.*;
import mp.utils.ModelAttributeReader;

/**
 * User: atsv
 * Date: 30.09.2006
 */
public class ModelDirectConnector implements ModelConnector {
  private Model FModel = null;
  private boolean FModelStarted = false;
  private Thread FModelThread;

  public ModelDirectConnector(Model model) throws ModelException {
    if (model == null){
      ModelException e = new ModelException("Пустая ссылка на исполняемую модель");
      throw e;
    }
    FModel = model;
  }

  private ModelBlockParam GetParam(Model aModel, String aBlockName, int aBlockIndex, String aParamName) throws ModelException{
  	ModelBlock block;
    if ( aBlockIndex == -1 ){
      block = (ModelBlock) aModel.Get( aBlockName );
    } else {
      block = aModel.Get( aBlockName, aBlockIndex );
    }
    if (block == null) {
      ModelException e = new ModelException("Отсутствует блок " + aBlockName + "  с номером " + Integer.toString(aBlockIndex) );
      throw e;
    }
    ModelBlockParam param = (ModelBlockParam) block.Get( aParamName );
    if ( param == null ){
      ModelException e = new ModelException("В блоке \"" + aBlockName + "\" отсутствует параметр \"" + aParamName + "\"");
      throw e;
    }
    return param;

  }

  private ModelBlockParam GetParam(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException{
    Model model = FModel.GetParallelModel(aModelName);
    if (model == null) {
    	return GetParam(FModel, aBlockName, aBlockIndex, aParamName);
    } else {
    	return GetParam(model, aBlockName, aBlockIndex, aParamName);
    }
  }


  private ModelBlockParam GetParam(String aBlockName, int aBlockIndex, String aParamName) throws ModelException{
    return GetParam(FModel, aBlockName, aBlockIndex, aParamName);
  }

  public double GetValue(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException{
    ModelBlockParam param = GetParam( aModelName, aBlockName, aBlockIndex, aParamName);
    try {
      return param.GetVariable().GetFloatValue();
    } catch (ScriptException e) {
      return 0;
    }
  }

  public double GetValue(ModelAddress address) throws ModelException {
    return GetValue( address.GetModelName(), address.GetBlockName(), address.GetBlockIndex(), address.GetParamName() );
  }

  public boolean GetBooleanValue(ModelAddress address) throws ModelException {
    ModelBlockParam param = GetParam( address.GetModelName(), address.GetBlockName(), address.GetBlockIndex(), address.GetParamName() );
    return param.GetVariable().GetBooleanValue();
  }

  public int GetIntValue(ModelAddress address) throws ModelException {
    ModelBlockParam param = GetParam( address.GetModelName(), address.GetBlockName(), address.GetBlockIndex(), address.GetParamName() );
    try {
      return param.GetVariable().GetIntValue();
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }
  }

  public String GetStringValue(ModelAddress address) throws ModelException {
    ModelBlockParam param = GetParam( address.GetModelName(), address.GetBlockName(), address.GetBlockIndex(), address.GetParamName() );
    try {
      return param.GetVariable().GetStringValue();
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }
  }

  public String GetStringValue(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException{
    ModelBlockParam param = GetParam( aModelName, aBlockName, aBlockIndex, aParamName );
    try {
      return param.GetVariable().GetStringValue();
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }

  }

  public void StartModel() throws ModelException {
    if ( FModelStarted ){
      return;
    }
    FModelThread = new Thread( FModel );
    FModelThread.start();
  }

  public void StopModel(){
    FModelThread = null;
    FModel.StopExec();
  }

  public void PauseModel(){
    if ( FModelThread != null && FModel != null){
       FModel.SetEnableExecution( false );
    }
  }

  public void ResumeModel(){
    if ( FModelThread != null && FModel != null){
       FModel.SetEnableExecution( true );
    }
  }

  public String GetErrorString(){
    String s = FModel.GetErrorString();
    /*
    if ( s == null || "".equalsIgnoreCase( s ) ){
      s = "Модель выполняется...";
    }
    */
    return s;
  }

  public int GetBlockIndex(String aBlockIndexValue) {
    if ( ModelAttributeReader.BLOCK_INDEX_SELF.equalsIgnoreCase( aBlockIndexValue ) ){
      //@todo Сделать процедуру получения индекса управляемого блока
      return 0;
    }
    if ( ModelAttributeReader.BLOCK_INDEX_ALL.equalsIgnoreCase( aBlockIndexValue )  ){
      return -1;
    }
    int i;
    try{
      i = Integer.parseInt( aBlockIndexValue );
      return i;
    } catch (Exception e){
      return -1;
    }
  }

  public int GetBlockCount( String aModelName, String aBlockName ) throws ModelException {
  	Model model = null;
  	if (!( aModelName == null || "".equals(aModelName) )) {
      model = FModel.GetParallelModel(aModelName);
  	}
  	if ( model == null ) {
      return FModel.GetBlockCount( aBlockName );
  	} else {
  		return model.GetBlockCount( aBlockName );
  	}
  }

  public void SendValue(double aValue, String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException{
    ModelBlockParam param = GetParam(aModelName, aBlockName, aBlockIndex, aParamName);
    param.GetVariable().SetValue( (float)aValue );
  }

  public void SendValue(boolean aValue, String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException {
    ModelBlockParam param = GetParam(aModelName, aBlockName, aBlockIndex, aParamName);
    param.GetVariable().SetValue( aValue );
  }

  public boolean IsConnectionEnabled(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException {
  	ModelBlockParam param = null;

    param = GetParam( aModelName, aBlockName, aBlockIndex, aParamName );
    return  ( (param.GetParamPlacementType() == ModelBlockParam.PLACEMENT_TYPE_INNER)  ||
              (param.GetParamPlacementType() == ModelBlockParam.PLACEMENT_TYPE_OUT) ||
              (param.GetParamPlacementType() == ModelBlockParam.PLACEMENT_TYPE_INPUT)
            );
  }

  public boolean IsManagingEnabled(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException{
    ModelBlockParam param = GetParam( aModelName, aBlockName, aBlockIndex, aParamName );
    return  ( param.GetParamPlacementType() == ModelBlockParam.PLACEMENT_TYPE_INPUT );
  }

  public int GetValueType(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException {
    ModelBlockParam param = GetParam( aModelName, aBlockName, aBlockIndex, aParamName );
    int type = param.GetVariable().GetType();
    return type;
  }

  private ModelBlock GetBlock(String aBlockName, int aBlockIndex) throws ModelException {
    ModelBlock block;
    if ( aBlockIndex == -1 ){
      block = (ModelBlock) FModel.Get( aBlockName );
    } else
    {
      block = FModel.Get( aBlockName, aBlockIndex );
    }
    if (block == null) {
      ModelException e = new ModelException("Отсутствует блок " + aBlockName + "  с номером " + Integer.toString(aBlockIndex) );
      throw e;
    }
    return block;
  }

  public void FireBlockEvent(String aBlockName, int aBlockIndex, String aEventName) throws ModelException {
    ModelBlock block = GetBlock( aBlockName, aBlockIndex );
    block.FireEvent( aEventName );
  }

  public int Compare(Variable aVarToCompare, String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException {
    if ( aVarToCompare == null ){
      ModelException e = new ModelException("Пустая переменная для сравнения");
      throw e;
    }
    ModelBlockParam param = GetParam( aModelName, aBlockName, aBlockIndex, aParamName );
    try {
      return  aVarToCompare.Compare( param.GetVariable() ) ;
    } catch (ScriptException e) {
      //e.printStackTrace();
      ModelException e1 = new ModelException(e.getMessage());
      throw e1;
    }
  }

  public boolean IsArray(ModelAddress address) throws ModelException {
    ModelBlockParam param = GetParam( address.GetBlockName(), address.GetBlockIndex(), address.GetParamName() );
    Variable var = param.GetVariable();
    if ( var == null ){
      ModelException e = new ModelException("Отсутствует информация о переменной в параметре");
      throw e;
    }
    return var instanceof ScriptArray;
  }

  public int GetArrayDimensionCount(ModelAddress address) throws ModelException {
    return 0;
  }

  public int GetArrayDimensionLength(ModelAddress address, int dimension) throws ModelException {
    ModelBlockParam param = GetParam( address.GetBlockName(), address.GetBlockIndex(), address.GetParamName() );
    ScriptArray arr = (ScriptArray) param.GetVariable();
    return arr.GetDimensionLength( dimension );
  }

  public double GetArrayValue(ModelAddress address, int[] coordinates) throws ModelException {
    ModelBlockParam param = GetParam( address.GetBlockName(), address.GetBlockIndex(), address.GetParamName() );
    ScriptArray arr = (ScriptArray) param.GetVariable();
    try {
      return arr.GetFloatValue( coordinates );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }
  }

	@Override
  public boolean IsHistoryExists(ModelAddress address) throws ModelException {
	  ModelBlockParam param = this.GetParam(address.GetModelName(), address.GetBlockName(), address.GetBlockIndex(), address.GetParamName());
	  if (param == null)  throw new ModelException("В блоке \"" + address.GetBlockName() + "\" отсутствует параметр \"" + address.GetParamName() + "\"");
	  return param.IsHistoryExists();
  }

	@Override
  public String GetHistoryStringValue(ModelAddress address, int index) throws ModelException {
		ModelBlockParam param = this.GetParam(address.GetModelName(), address.GetBlockName(), address.GetBlockIndex(), address.GetParamName());
		if (param == null)  throw new ModelException("В блоке \"" + address.GetBlockName() + "\" отсутствует параметр \"" + address.GetParamName() + "\"");
		ValueChangeListener.HistoryBean bean = param.GetHistoryBean(index);
		if (bean == null) return null;
		return bean.getOperationName() + " " + bean.getStringValue();

  }



}

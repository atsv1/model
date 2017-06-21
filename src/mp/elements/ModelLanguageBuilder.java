package mp.elements;

import java.util.UUID;
import java.util.Vector;

import mp.parser.*;
import mp.utils.ServiceLocator;

/**  ласс предназначен дл€ передачи в элементы модели (блоки) информации о расширении встроенного €зыка.
 * ѕод "расширением встроенного €зыка" понимаетс€ список переменных, наход€щихс€ в блоке. ¬ дальнейшем планируетс€
 * в этом же расширении хранить списки блоков и их выходные параметры дл€ того, чтобы к ним можно было обращатьс€
 * из встроенного €зыка.
 */
public class ModelLanguageBuilder {
  private Model FModel;
  private Vector FFunctionList = null;

  public ModelLanguageBuilder( Model aModel ){
    FModel = aModel;
  }

  public void SetModel( Model aModel ){
    FModel = aModel;
  }

  /** ѕроизводитс€ формирование списка переменных (объектов Variable).
   *
   * @param block - блок, в котором хран€тс€ переменные. ѕеременные берутс€ из входных, выходных и внутренних
   * параметров блока.
   * @param languageExt - расширитель €зыка. ¬ него записываютс€ все переменные из блока/
   * @throws ScriptException
   */
  protected static void  SetVariables( ModelBlock block, Vector functionList, ScriptLanguageExt languageExt ) throws ScriptException {
    //добавл€ем входные переменные
    ModelBlockParam param;
    int i = 0;
    param = block.GetInpParam(i);
    while ( param != null ){
      languageExt.AddVariable( param.GetVariable() );
      i++;
      param = block.GetInpParam(i);
    }
    //добавл€ем выходные переменные
    i = 0;
    param = block.GetOutParam(i);
    while ( param != null ){
      languageExt.AddVariable( param.GetVariable() );
      i++;
      param = block.GetOutParam(i);
    }
    //добавл€ем внутренние переменные
    i = 0;
    param = block.GetInnerParam(i);
    while ( param != null ){
      languageExt.AddVariable( param.GetVariable() );
      i++;
      param = block.GetInnerParam(i);
    }
    if (functionList == null || functionList.size() == 0) {
    	return;
    }
    i = 0;
    ModelFunction fun;
    while (i < functionList.size()) {
    	fun = (ModelFunction) functionList.get(i);
    	languageExt.AddFuction( fun.GetFunction() );
    	i++;
    }
  }

  protected static void AddSelfIndexVariable( ModelBlock aBlock, int indexValue ) throws ModelException{
    ModelBlockParam selfIndex = (ModelBlockParam) aBlock.Get("selfindex");
    if ( selfIndex != null ){
      return;
    }
    selfIndex = new ModelBlockParam(aBlock,"selfIndex", ServiceLocator.GetNextId()) {
      protected void UpdateParam() throws ScriptException, ModelException {
      }

      public boolean IsNeedRuntimeUpdate() {
        return false;
      }
      
      public void fixState(UUID stateLabel) throws ModelException{
      	// ничего не делаем, т.к значение не мен€етс€  	
      }
      
      public void rollbackTo(UUID stateLabel) throws ModelException{
        //  ничего не делаем, т.к значение не мен€етс€      	  	
      }

      
    };
    selfIndex.SetVarInfo("integer",String.valueOf( indexValue ));
    aBlock.AddInnerParam( selfIndex );
  }

  protected static void AddSelfIndexVariable( ModelBlock aBlock, Model aModel ) throws ModelException{
    if ( aModel == null ){
      return;
    }
    int i = aModel.GetBlockIndex( aBlock );
    AddSelfIndexVariable( aBlock, i );
  }

  protected static void AddElementIdVariable( ModelBlock aBlock ) throws ModelException {
    int i = aBlock.GetElementId();
    ModelBlockParam param = new ModelBlockParam(aBlock, "elementId", ServiceLocator.GetNextId()) {
      protected void UpdateParam() throws ScriptException, ModelException {
      }

      public boolean IsNeedRuntimeUpdate() {
        return false;
      }
      
      public void fixState(UUID stateLabel) throws ModelException{
      	// ничего не делаем, т.к значение не мен€етс€  	
      }
      
      public void rollbackTo(UUID stateLabel) throws ModelException{
        //  ничего не делаем, т.к значение не мен€етс€      	  	
      }
    };
    param.SetVarInfo("integer",String.valueOf( i ));
    aBlock.AddInnerParam( param );
  }

  /** ƒобавл€ем в блок все константы модели. Ќа основании констант создаютс€
   *
   * @param aBlock
   */
  private void AddConstants( ModelBlock aBlock ) throws ModelException {
    if ( FModel == null ){
      return;
    }
    ModelConstant cnt = null;
    int i = 0;
    int constCount = FModel.GetConstantCount();
    while (i < constCount) {
      cnt = FModel.GetConstant( i );
      aBlock.AddInnerParam( cnt );
      i++;
    }
  }
  
  private void addForkVar(ModelBlock aBlock) throws ModelException{
  	ModelBlockParam param = new ModelBlockParam(aBlock, "isForkMode", ServiceLocator.GetNextId()) {
      protected void UpdateParam() throws ScriptException, ModelException {
      }

      public boolean IsNeedRuntimeUpdate() {
        return false;
      }
      
      public void fixState(UUID stateLabel) throws ModelException{
      	// ничего не делаем, т.к значение не мен€етс€  	
      }
      
      public void rollbackTo(UUID stateLabel) throws ModelException{
        //  ничего не делаем, т.к значение не мен€етс€      	  	
      }
    };
    param.SetVarInfo("boolean",String.valueOf( false ));
    aBlock.AddInnerParam( param );
  	
  }

  private void AddServiceVariables( ModelBlock aBlock ) throws ModelException{
    AddSelfIndexVariable(aBlock, FModel);
    AddElementIdVariable( aBlock );
    AddConstants( aBlock );
    addForkVar(aBlock);
  }

  private  static ScriptLanguageExt GetLanguageExt( ModelBlock block, Vector functionList ) throws ScriptException {
    ScriptLanguageExt result = new ScriptLanguageExt();
    SetVariables( block, functionList, result );
    return result;
  }

  private ScriptLanguageExt GetFunctionLanguageExt( ) throws ScriptException{
  	if ( FFunctionList == null ) {
  		return null;
  	}
  	ScriptLanguageExt result = new ScriptLanguageExt();
  	int i = 0;
    int constCount = FModel.GetConstantCount();
    ModelConstant cnst = null;
    while (i < constCount) {
      cnst = FModel.GetConstant( i );
      result.AddVariable(cnst.GetVariable());
      i++;
    }
    ModelFunction fun;
    i = 0;
    while (i < FFunctionList.size()) {
    	fun = (ModelFunction) FFunctionList.get(i);
    	result.AddFuction( fun.GetFunction() );
    	i++;
    }
  	return result;
  }

  protected void UpdateBlock( ModelBlock aBlock ) throws ScriptException, ModelException {
    AddServiceVariables(aBlock);
    aBlock.SetLanguageExt( GetLanguageExt( aBlock, FFunctionList ) );
  }

  private void UpdateBlockList() throws ModelException, ScriptException{
    ModelBlock block;
    int i = 0;
    boolean funExists = false;
    if ( FModel.GetFunctionList() != null && FModel.GetFunctionList().size() > 0) {
    	FFunctionList = FModel.GetFunctionList();
    	funExists = true;
    }
    while ( i < FModel.size() ){
      block = (ModelBlock) FModel.GetByIndex(i);
      UpdateBlock( block );
      i++;
    }

  }

  private void UpdateFunctions() throws ScriptException{
  	int i = 0;
    if ( FModel.GetFunctionList() != null && FModel.GetFunctionList().size() > 0) {
    	FFunctionList = FModel.GetFunctionList();
    } else {
    	FFunctionList = null;
    	return;
    }
		i = 0;
		while (i < FFunctionList.size()) {
			ModelFunction fun = (ModelFunction) FFunctionList.get(i);
			fun.SetLanguageExt( GetFunctionLanguageExt( ) );
			i++;
		}

  }

  /**ƒобавл€ем во все элементы, принадлежащие данной модели, свои расширени€ €зыка, т.е. экземпл€ры класса
   * ScriptLanguageExt
   *
   * @throws ModelException
   * @throws ScriptException
   */
  public void UpdateModelElements() throws ModelException, ScriptException{
    UpdateBlockList();
    UpdateFunctions();
  }

}

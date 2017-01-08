package mp.elements;

import java.util.Vector;

import mp.parser.*;
import mp.utils.ServiceLocator;

/** Класс предназначен для передачи в элементы модели (блоки) информации о расширении встроенного языка.
 * Под "расширением встроенного языка" понимается список переменных, находящихся в блоке. В дальнейшем планируется
 * в этом же расширении хранить списки блоков и их выходные параметры для того, чтобы к ним можно было обращаться
 * из встроенного языка.
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

  /** Производится формирование списка переменных (объектов Variable).
   *
   * @param block - блок, в котором хранятся переменные. Переменные берутся из входных, выходных и внутренних
   * параметров блока.
   * @param languageExt - расширитель языка. В него записываются все переменные из блока/
   * @throws ScriptException
   */
  protected static void  SetVariables( ModelBlock block, Vector functionList, ScriptLanguageExt languageExt ) throws ScriptException {
    //добавляем входные переменные
    ModelBlockParam param;
    int i = 0;
    param = block.GetInpParam(i);
    while ( param != null ){
      languageExt.AddVariable( param.GetVariable() );
      i++;
      param = block.GetInpParam(i);
    }
    //добавляем выходные переменные
    i = 0;
    param = block.GetOutParam(i);
    while ( param != null ){
      languageExt.AddVariable( param.GetVariable() );
      i++;
      param = block.GetOutParam(i);
    }
    //добавляем внутренние переменные
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
    };
    param.SetVarInfo("integer",String.valueOf( i ));
    aBlock.AddInnerParam( param );
  }

  /** Добавляем в блок все константы модели. На основании констант создаются
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

  private void AddServiceVariables( ModelBlock aBlock ) throws ModelException{
    AddSelfIndexVariable(aBlock, FModel);
    AddElementIdVariable( aBlock );
    AddConstants( aBlock );
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

  /**Добавляем во все элементы, принадлежащие данной модели, свои расширения языка, т.е. экземпляры класса
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

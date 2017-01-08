package mp.elements;

import mp.parser.*;

import java.util.Vector;

/**
 *   Класс предназначен для выполнения какого-либо кода (и получения его результатов). При этом
 * код должен выполняться для неограниченного количества блоков, зарегистрированных в экземпляре
 * этого класса.
 *   Алгоритм работы данного класса таков:
 * - берется первый зарегистрированный блок.
 * - проверяется, нужно ли выполнять скрипт (т.е. изменилось ли значение  хотя бы одного из параметров, используемых
 * в скрипте). Если не нужно, то берем следующий элемент. Если нужно, то идем дальше по циклу
 * - из него берутся значения параметров, используемых в скрипте
 * - скрипт выполняется
 * - результат запоминается
 * - переходим к следующему блоку и начинаем все с начала
 *
 * Для организации озвученного алгоритма в класс передается:
 * - полный список параметров, которые могут участвовать в рассчете (а уж из них класс формирует список
 * параметров, которые действительно участвуют в рассчетах)
 * - результирующая переменная (переменная, значеине которой нужно запоминать после выполнения скрипта)
 * - исходный текст скрипта
 * - список блоков 
 *
 * Класс используется в мультиплексоре и аггрегаторе
 * 
 * Алгоритм взаимодействия с классом-владельцем:
 * -  
 *
 * User: саша
 * Date: 27.05.2008
 */
public abstract class MultiBlockExecutor {
  private ModelElementContainer FFullList = null;
  protected ModelBlockParam FResultParam = null;
  private String FScriptSource;
  private ModelElementContainer FUsedParamList = null;
  private ModelDynamicBlock FOwner = null;
  protected ScriptParser FParser = null;
  private boolean FIsUsedParamsListCreated = false;
  private Vector FResourceList = new Vector();
  private int FPointer = 0;
  protected int FRealExecCount = 0;
  protected int FEnterCount = 0;
  protected long FRealDuration = 0;


  public MultiBlockExecutor( ModelDynamicBlock aOwner, ModelBlockParam aResultParam, String aScriptSource ){
    FResultParam = aResultParam;
    FScriptSource = aScriptSource;
    FOwner = aOwner;
  }

  private ScriptParser CreateParser() throws ScriptException, ModelException {
    ScriptParser result = null;
    if ( FUsedParamList == null ){
      FUsedParamList = new ModelElementContainer();
    } else {
      FUsedParamList.Clear();
    }
    if ( FOwner == null ){
      ModelException e = new ModelException("Отстутствует владелец. Внутренняя ошибка");
      throw e;
    }
    result = ParserFactory.GetParser( FOwner.GetLanguageExt(), FScriptSource );

    ModelInpParamsIterator iterator = new ModelInpParamsIterator();
    iterator.parser = result;
    iterator.sourceList = FOwner.GetElements();
    iterator.ownerElement = FResultParam;

    ModelAddExecutor executor = new ModelAddExecutor( iterator );
    executor.container = FUsedParamList;
    executor.SetUniqueFlag( true );
    executor.Execute();

    return result;
  }

  protected void UpdateServiceInformation() throws ScriptException, ModelException {
    FParser = CreateParser();
    FIsUsedParamsListCreated = true;
    RemoveAllChangeListeners();
    AddAllChangeListeners();
  }

  protected int GetUsedParamCount(){
    return FUsedParamList.size();
  }

  private void RemoveAllChangeListeners() throws ModelException {
    int i = 0;
    ResourceRecord rec;
    while ( i < FResourceList.size() ){
      rec = (ResourceRecord) FResourceList.get( i );
      rec.RemoveChangeListeners();
      i++;
    }
  }

  private void AddAllChangeListeners() throws ModelException {
    int i = 0;
    ResourceRecord rec;
    while ( i < FResourceList.size() ){
      rec = (ResourceRecord) FResourceList.get( i );
      rec.AddChangeListeners();
      i++;
    }
  }

  /**Проверяем блок перед добавлением в список ресурсов.
   * Проверяется следующее: у блока должны быть все параметры, которые используются в скрипте, за исключением собственных
   * параметров владельца, параметров владельца владельца (владелец владельца - это блок, прописанный в поле owner
   * блока-владельца данного класса) и параметра-результата (FResultParam).
   *
   * @param aBlock - проверяемый блок
   * @throws ModelException
   */
  private void CheckBlockBeforeAdd( ModelBlock aBlock ) throws ModelException{
    int i = 0;
    ModelBlockParam param;
    ModelBlockParam resourceParam;
    ModelBlock ownerOfOwner = FOwner.GetDynamicBlockOwner();
    while ( i < FUsedParamList.size() ){
      param = (ModelBlockParam) FUsedParamList.get( i );
      resourceParam = (ModelBlockParam)aBlock.Get( param.GetNameIndexObj() );
      if ( resourceParam == null ){
        //такого параметра в проверяемом блоке нет. Начинаем проверять, почему его нет.
        // проверяем, не является ли выбранный параметр параметром-результатом. Проверка сейчас кажется лишней,
        // поскольку параметр-результат не добавляется в FUsedParamList. Но проверяем на всякий случай
        if ( param != FResultParam ){
          //теперь проверяем, не является ли используемый параметр таким параметром, который создан на основании
          // параметра блока-владельцы
          if ( ownerOfOwner == null ){
            ModelException e = new ModelException("В блоке \"" + aBlock.GetFullName() + "\" отсутствует параметр \"" +
                 param.GetName() + "\". Невозможно использовать этот блок в качестве ресурса");
            throw e;
          }
          //владелец владельца есть. пытаемся получить из него параметр с таким же именем
          resourceParam = (ModelBlockParam)ownerOfOwner.Get( param.GetNameIndexObj() );
          if ( resourceParam == null ){
            //такого параметре во владельце владельца нет. Значит, он должен быть в проверяемом блоке.
            // Но и в проверяемом блоке его нет. Значит, это ошибка
            ModelException e = new ModelException("В блоке \"" + aBlock.GetFullName() + "\" отсутствует параметр \"" +
                 param.GetName() + "\". Невозможно использовать этот блок в качестве ресурса");
            throw e;
          }
        }
      }
      if ( resourceParam != null && param.GetVariable().GetType() != resourceParam.GetVariable().GetType() ){
        ModelException e = new ModelException("У параметра \"" + param.GetName() + "\" несовпадающие типы");
        throw e;
      }
      i++;
    }

  }

  protected void AddResourceBlock( ModelBlock aBlock ) throws ModelException{
    if ( aBlock == null ){
      ModelException e = new ModelException("Добавляется пустой блок. Внутренняя ошибка");
      throw e;
    }
    if ( FIsUsedParamsListCreated ){
      CheckBlockBeforeAdd( aBlock );
    }
    ResourceRecord rec = new ResourceRecord( aBlock, FUsedParamList );
    FResourceList.add( rec );
    if ( FIsUsedParamsListCreated ){
      rec.AddChangeListeners();
    }
  }

  protected int GetResourceCount(){
    return FResourceList.size(); 
  }

  protected ResourceRecord GetNext(){
    FPointer++;
    ResourceRecord rec;
    while ( FPointer < FResourceList.size() ){
      rec = (ResourceRecord) FResourceList.get( FPointer );
      if ( rec.IsNeedToExec() ) {
      return rec; 
    }
      FPointer++;
    }
    return null;
  }

  protected ResourceRecord GetFirst(){
    FPointer = -1;
    return GetNext();
  }

  protected ResourceRecord GetResource( int aResourcePos ){
    if ( aResourcePos >= FResourceList.size() ||  aResourcePos < 0 ){
      return null;
    }
    return (ResourceRecord) FResourceList.get( aResourcePos );
  }

  /**Возвращает  позицию блока, только что выбранного методами GetFirst или GetNext
   *
   * @return
   */
  protected int GetCurrentRecordPos(){
    return FPointer;
  }

  
  protected void LoadParams( ModelBlock aBlock ) throws ScriptException {
    ModelBlockParam param;
    ModelBlockParam sourceParam;
    int i = 0;
    int paramsCount = FUsedParamList.size();
    while ( i < paramsCount ){
      param = (ModelBlockParam) FUsedParamList.get( i );
      sourceParam = aBlock.GetOutParam( param.GetNameIndexObj() );
      if ( sourceParam != null ){
        param.GetVariable().StoreValueOf( sourceParam.GetVariable() );
      }
      i++;
    }//while
  }

  public abstract void ExecuteScript() throws ScriptException, ModelException;

  public int GetExecCount(){
    return FRealExecCount;
  }

  public int GetEnterCount(){
    return FEnterCount;
  }

  public long GetDuration(){
    return FRealDuration;
  }

  public Variable GetNextResult(){
    FPointer++;
    if ( FPointer < FResourceList.size() ){
      ResourceRecord rec = (ResourceRecord) FResourceList.get( FPointer );
      return rec.GetExecResult();
    }
    return null;
  }

  public Variable GetFirstResult(){
    FPointer = -1;
    return GetNextResult();
  }

  public Variable GetResult( int aResultPos ){
    if ( aResultPos >= FResourceList.size() ||  aResultPos < 0 ){
      return null;
    }
    ResourceRecord rec = (ResourceRecord) FResourceList.get( aResultPos );
    return rec.GetExecResult();
  }

  public String GetResultFullName(){
    if ( FResultParam == null ){
      return "empty result param";
    } else {
      return FResultParam.GetFullName();
    }
  }

  public String GetResultName(){
    if ( FResultParam == null ){
      return "empty result param";
    }
    return FResultParam.GetName();
  }

  public ModelBlockParam GetResultParam(){
    return FResultParam;
  }

  protected ModelDynamicBlock GetOwner(){
    return FOwner;
  }

  public String toString(){
    if ( FResultParam != null ){
      return FResultParam.GetName();
    }
    return super.toString();
  }


  protected class ResourceRecord{
    private ModelBlock FBlock = null;
    private ModelElementContainer FRecUsingParams;
    private boolean FIsParamChanged = true;
    private Variable FExecResult = null;


    public ResourceRecord( ModelBlock aResourceBlock, ModelElementContainer aUsingParams ){
      FBlock = aResourceBlock;
      FRecUsingParams = aUsingParams;
    }

    public void AddChangeListeners() throws ModelException {
      FRecUsingParams = FUsedParamList; 
      if ( FRecUsingParams == null || FRecUsingParams.size() == 0 ){
        return;
      }
      ChangeListener listener = new ChangeListener() {
        public void VariableChanged(VariableChangeEvent changeEvent) {
          FIsParamChanged = true;  
        }
      };
      int i = 0;
      ModelBlockParam param;
      ModelBlockParam sourceParam;
      while ( i < FRecUsingParams.size() ){
        param = (ModelBlockParam) FRecUsingParams.get( i );
        sourceParam = (ModelBlockParam) FBlock.Get( param.GetNameIndexObj() );
        if ( sourceParam != null ){
          sourceParam.GetVariable().AddChangeListener( listener );
        } else {
          param.GetVariable().RemoveChangeListener( this );
        }
        i++;
      }
    }

    public void RemoveChangeListeners() throws ModelException {
      if ( FRecUsingParams == null || FRecUsingParams.size() == 0 ){
        return;
      }
      int i = 0;
      ModelBlockParam param;
      ModelBlockParam sourceParam;
      while ( i < FRecUsingParams.size() ){
        param = (ModelBlockParam) FRecUsingParams.get( i );
        sourceParam = (ModelBlockParam) FBlock.Get( param.GetNameIndexObj() );
        if ( sourceParam != null ){
          sourceParam.GetVariable().RemoveChangeListener( this );
        } else {
          param.GetVariable().RemoveChangeListener( this );
        }
        i++;
      }
    }

    public ModelBlock GetBlock(){
      return FBlock;
    }

    public boolean IsNeedToExec(){
      return FIsParamChanged;
    }

    public void StoreExecResult( Variable aExecResult) throws ScriptException {
      if ( FExecResult == null ){
        FExecResult = (Variable) aExecResult.clone();
      } else {
        FExecResult.StoreValueOf( aExecResult );
      }
    }

    public Variable GetExecResult(){
      return FExecResult;
    }

    public void ScriptExecuted(){
      FIsParamChanged = false;
    }

  }//class

}

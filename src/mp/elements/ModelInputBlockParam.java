package mp.elements;

import mp.parser.*;
import mp.utils.ModelAttributeReader;

/**
 * User: atsv
 * Date: 18.09.2006
 * Данный класс предназначен для получения информации из другого блока.
 */
public class ModelInputBlockParam extends ModelBlockParam{
  private ModelBlock FLinkedBlock = null;
  /**Элемент из другого блока, к которому было выполнено подключение данным элементом
   */
  private ModelBlockParam FLinkedElement = null;
  private ModelElement FRealOwner = null;
  /**Переменная из элемента FLinkedElement. Хранится в виде отдельной переменной только для экономии времени
   */
  private Variable FSourceVar = null;

  public ModelInputBlockParam(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FRealOwner = aOwner;
    this.SetParamType( ModelBlockParam.PARAM_TYPE_INFORM );
  }

  protected void UpdateParam() throws ScriptException, ModelException {
    if ( FLinkedElement == null ){
      return;
    }
    this.GetVariable().StoreValueOf( FSourceVar );
  }

  public boolean IsNeedRuntimeUpdate() {
    return true;
  }

  public ModelElement GetOwner(){
    return FLinkedBlock;
  }

  protected void UpdateLink(ModelBlock aNewBlock,  ModelBlockParam aNewElement) throws ModelException{
    if ( aNewElement == null){
      ModelException e = new ModelException("Попытка создать связь с пустым элементом в элементе " + this.GetFullName());
      throw e;
    }
    FLinkedElement = aNewElement;
    FLinkedBlock = aNewBlock;
    FSourceVar = aNewElement.GetVariable();
  }

  protected void SetLinkedElementToNull(){
    FLinkedBlock = null;
    FLinkedElement = null;

  }

  protected void UnLink() throws ModelException {
    if ( FLinkedElement == null ){
      LoadInitValue();
      return;
    }
    FLinkedElement.GetVariable().RemoveChangeListener( this );
    FLinkedElement.RemoveFromDependList( this );
    SetLinkedElementToNull();
    LoadInitValue();
  }

  /**Метод выполняет подключение к эказанному в параметрах элементу. Под "подключением" здесь понимается следующее:
   * данные из параметра, к которому выполняется подключение,
   *
   * @param aLinkOwner - экземпляр владельца того параметра, к которому выполняется подключение
   * @param aElement - элемент, к которому будет выполняться подключение.
   * @throws ModelException
   */
  public void Link(ModelBlock aLinkOwner, ModelBlockParam aElement) throws ModelException{
    FLinkedBlock = aLinkOwner;
    FLinkedElement = aElement;
    ModelException e;
    if ( FLinkedElement == null ){
      e = new ModelException("Попытка создать связь с пустым элементом в элементе " + this.GetFullName());
      throw e;
    }
    FSourceVar = FLinkedElement.GetVariable();
    FLinkedElement.AddInDependParams( this );
    ChangeListener listener = new ChangeListener(this) {
      public void VariableChanged(VariableChangeEvent changeEvent)  {
        try {
          InputParamChanged();
        } catch (ModelException e1) {
          e1.printStackTrace();
        }
      }
    };
    aElement.AddChangeListener( listener );
    try {
      UpdateParam();
    } catch (ScriptException e1) {
      //e1.printStackTrace();
      e = new ModelException("Ошибка в элементе \"" + GetFullName() + "\": " + e1.getMessage());
    }
  }

  protected ModelElement GetLinkedBlock( ModelElementDataSource elementSource ) throws ModelException {
    String blockName = elementSource.GetLinkedBlockName();
    if ( blockName == null){
      return null;
    }
    ModelElement result = null;
    Model model = null;
    String modelName = elementSource.GetLinkedModelName();
    if ( modelName == null || "".equalsIgnoreCase( modelName ) ){
      model = (Model) FRealOwner.GetOwner();
    } else {
      // имеется название модели, с которой происходит соединение. пытаемся получить ссылку на эту модель
      ModelExecutionManager manager = ModelExecutionContext.GetManager( modelName );
      if ( manager == null ){
        ModelException e = new ModelException( "Ошибка в элементе \"" + GetFullName() + "\": " +
                "отсутствует модель \"" + modelName + "\"" );
        throw e;
      }
      model = (Model) manager;
    }
    String blockIndex = elementSource.GetBlockLinkIndex();
    if ( blockIndex == null || "".equalsIgnoreCase( blockIndex ) ){
      return model.Get( blockName) ;
    }
    int intBlockIndex = 0;
    if ( "selfIndex".equalsIgnoreCase( blockIndex ) ){
      ModelBlockParam selfIndexParam = (ModelBlockParam) FRealOwner.Get("selfindex");
      try {
        intBlockIndex = selfIndexParam.GetVariable().GetIntValue();
      } catch (ScriptException e) {
        //e.printStackTrace();
        ModelException e1 = new ModelException("Ошибка в элементе \"" + GetFullName() + "\": " + e.getMessage());
        throw e1;
      }
      return model.Get( blockName, intBlockIndex );
    }
    String error;
    String s;
    try{
      intBlockIndex = Integer.parseInt( blockIndex );
    } catch (Exception e) {
      error = e.getMessage();
      /*ModelException e1 = new ModelException("Ошибка в элементе \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;*/
      //s = elementSource.GetConstantValue( blockIndex );
      s= BuildContext.getBuildContext().getConstantValue(blockIndex);
      if ( s == null ){
        throw new ModelException("Ошибка в элементе \"" + GetFullName() + "\": " + e.getMessage());        
      }
      intBlockIndex = Integer.parseInt( s );
    }

    result = model.Get( blockName, intBlockIndex );
    return result;
  }

  private void ReadLinkInfo(ModelElementDataSource elementSource  ) throws ModelException{
    String blockName = elementSource.GetLinkedBlockName();
    String paramName = elementSource.GetLinkedParamName();
    if ( blockName == null && paramName == null ){
      return;
    }
    if ( blockName != null && paramName == null ){
      ModelException e = new ModelException("Ошибка во входной переменной. В параметре " + GetName() +
              " присутствует название блока, но отсуствует название параметра");
      throw e;
    }
    if ( blockName == null  ){
      ModelException e = new ModelException("Ошибка во входной переменной.  В параметре " + GetName() +
              "присутствует название переменной, но отсутствует название блока");
      throw e;
    }
    ModelElement block = GetLinkedBlock( elementSource );
    if ( block == null ){
      ModelException e = new ModelException("Ошибка в элементе \"" + GetFullName() + "\": отсутстствует блок \"" +
              blockName + "\"");
      throw e;
    }
    ModelElement param = block.Get( paramName );
    Link((ModelBlock)block,(ModelBlockParam)param);
  }

  /**Метод чтения информации из файла модели.
   * В этом методе (или в методах, которые вызываются из этого метода) обязательно должен выполняться
   * метод Link(). Это касается и наследников данного класса. Это нужно для того, чтобы правильно работал мультиплексор,
   * точнее - правильно определялся тип конкуренции в мультиплексоре.
   * @throws ModelException
   */
  public void ApplyNodeInformation() throws ModelException{
  	super.ApplyNodeInformation();     
     ReadLinkInfo( elementSource );
  }

  public ModelElement GetRealOwner(){
    return FRealOwner;
  }

  public ModelElement GetLinkedElement(){
    return FLinkedElement;
  }

  public ModelElement GetLinkedElementOwner(){
    return FLinkedBlock;
  }

}

package mp.elements;

import mp.parser.*;
import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * User: atsv
 * Date: 15.09.2006
 */
public abstract class ModelBlockParam extends ModelElement{
  protected ScriptLanguageExt FLanguageExt = null;
  //Константы, описывающие расположение параметра внутри блока
  public static int PLACEMENT_TYPE_INNER = 1;// внутренний параметр
  public static int PLACEMENT_TYPE_OUT = 2; // выходной параметр
  public static int PLACEMENT_TYPE_INPUT = 3;// входной параметр

  //Константы, описывающие тип параметра - материальный или нематериальный
  public static final int PARAM_TYPE_INFORM = 1; //нематериальный параметр
  public static final int PARAM_TYPE_MATERIAL = 2; //материальный параметр

  private int FParamPlacementType = 0;
  protected int FParamType = 0;

  private Vector FDependElements = null; //Хранит список элементов, которые используют в своих вычислениях
                                                        //данный параметр
  private boolean FIsInpParamChanged = true; //показывает, изменился ли хотя бы один из параметров, которые использует
                                             // данный параметр
  protected ModelElementContainer FInpElements = null;//здесь хранится список элементов, которые используются
                                                    // данным параметром
  protected Variable FVariable = null;
  private String FInitValue = null;
  protected ExecutionContext FExecutionContext = null;

  /**Это поле должен будет заполнить объект, который желает получать информацию о том, нужно ли вы каким-то образом
   * выполнять обновление этого элемента.
   * Это может быть блок, который будет вызывать метод Update() только для тех параметров, которые нужно
   * пересчитать (а пересчитывать параметр нужно тогда, когда изменилось хотя бы одно значение в списке параметров,
   * от которых зависит данный параметр)
   *
   */
  private ModelExecuteList FExecList = null;
  /*
   * В этом листенере должна будет храниться история изменений переменной
   * */
  private ValueChangeListener FVarChangeListener = null;



  public ModelBlockParam(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FDependElements = new Vector();
    FInpElements = new ModelElementContainer();
    FExecutionContext = new ExecutionContext( this.GetFullName() );
  }

  public Variable GetVariable(){
    return FVariable;
  }

  public void SetVariable(Variable aVariable){
    FVariable = aVariable;
  }

  protected abstract void UpdateParam() throws ScriptException, ModelException;

  public  void  Update() throws ScriptException, ModelException{
    if ( !FIsInpParamChanged ) {
      /*if ( GetName().equalsIgnoreCase("passengerIncrement") ){
        System.out.println("not changed");
      }*/
      return;
    }
    FIsInpParamChanged = false;
    UpdateParam();
  }

  public void Update( ModelTime aCurrentTime ) throws ScriptException, ModelException{
    Update();
    if ( GlobalParams.ExecTimeOutputEnabled() && (aCurrentTime != null) ){
      System.out.println("Выполнение параметра " + this.GetFullName() + ". Время модели = " +
              Double.toString(aCurrentTime.GetValue()) + " Значение параметра = " +  FVariable.toString());
    }
  }

  public void AddInDependParams( ModelElement aElement ) throws ModelException{
    FDependElements.add( aElement );
  }

  /**Функция возвращает ссылку на параметр, который зависит от данного параметра
   *
   * @param index - индекс элемента
   * @return - ссылка на зависимый параметр, либо null, если индекс выходит за пределы списка зависимых параметров
   */
  public ModelElement GetDependElement(int index){
    if ( index < 0 || FDependElements.size() <= index ){
      return null;
    }
    return (ModelElement) FDependElements.get( index );
  }

  public void RemoveFromDependList( ModelElement element ){
    FDependElements.remove( element );
  }

  public void RemoveChangeListener( ModelElement element ){
    FVariable.RemoveChangeListener( element );

  }

  /** Проверяется, является ли переданный в параметре элемент зависимым элементом. Зависимым элементом
   * считается такой элемент, который получает данные из этого элемента
   *
   * @param aElement
   * @return
   */
  public boolean IsDependElement( ModelElement aElement ){
    int i = FDependElements.indexOf( aElement );
    return ( i != -1 );
  }

  public void LoadInitValue() throws ModelException{
    try {
      FVariable.SetValueWithTypeCheck( FInitValue );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("Ошибка в элементе \"" + GetFullName() + "\": " + e.getMessage() );
      throw e1;
    }
  }

  /**Создание переменной (объекта Variable).
   *
   * @param aVarType - текстовое название типа переменной: integer, real, boolean
   * @param aInitValue - текстовое значение переменной
   */
  public void SetVarInfo(String aVarType, String aInitValue) throws ModelException{
    ModelElement owner = this.GetOwner();
    String ownerName = "";
    if ( owner != null ){
      ownerName = owner.GetName();
    }

    FInitValue = aInitValue;
    String error;
    try {
      FVariable = Variable.CreateNewInstance( this.GetName(), aVarType, aInitValue );
      return;
    } catch (ScriptException e) {
      //e.printStackTrace();
      //ModelException e1 = new ModelException( e.getMessage() );
      //throw e1;
      error = e.getMessage();
    }
    if ( owner == null ){
      ModelException e1 = new ModelException( "Ошибка в элементе \"" + GetFullName() + "\": " + error );
      throw e1;
    }
    ModelBlock block = (ModelBlock) owner;
    Model model = (Model)block.GetOwner();

    ModelConstant cnst = model.GetConstant( aInitValue );
    if ( cnst == null ) {
      ModelException e1 = new ModelException("Ошибка в элементе \"" + GetFullName() + "\": " + error );
      throw e1;
    }

    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    try {
      FVariable = Variable.CreateNewInstance( this.GetName(), aVarType,
              attrReader.GetConstantValue( aInitValue ) );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }

  }

  /**Данную функцию вызывает у данного параметра другой параметр (параметр-источник), который используется в
   * вычислениях значения  для этого параметра. Вызов происходит, если значение параметра-источника изменилось.
   * @param aChangedParam - ссылка на параметр-источник
   */
  public void InputParamChanged(ModelElement aChangedParam){
    FIsInpParamChanged = true;
  }

  public void InputParamChanged() throws ModelException {
    FIsInpParamChanged = true;
    if ( FExecList != null){
      FExecList.AddToExecuteList( this );
    }
  }

  /**Функция возвращает "да", если данный параметр нуждается в пересчете, т.е. при вызове процедуры Update() значение
   * этого параметра изменится.
   * @return
   */
  public boolean IsNeedToUpdate(){
    return FIsInpParamChanged;
  }

  /** Функция проверяет, является ли переданный в параметре элемент тем параметром, который используется в
   * вычислениях внутри данного параметра.
   * @param aElement - проверяемый параметр
   * @return
   */
  public boolean IsInputParam(ModelElement aElement){
    if ( aElement == null ){
      return false;
    }
    ModelElement e = FInpElements.Get( aElement.GetElementId() );
    return (e != null);
  }

  protected void ReadVariableInfo( ModelAttributeReader aAttrReader ) throws ModelException{
    String typeName = aAttrReader.GetAttrParamType();
    String initValue = aAttrReader.GetAttrInitValue();
    SetVarInfo( typeName, initValue );
  }

  public int GetParamPlacementType() {
    return FParamPlacementType;
  }

  public void SetParamPlacementType(int aParamType) {
    this.FParamPlacementType = aParamType;
  }

  public  void SetLanguageExt(ScriptLanguageExt aLanguageExt){
    FLanguageExt = aLanguageExt;
  }

  public void AddChangeListener(ChangeListener aListener){
    if ( FVariable == null ){
      System.out.println("null");
    }
    FVariable.AddChangeListener( aListener );

  }

  public int GetParamType(){
    return FParamType;
  }

  public void SetParamType( int aValueType ){
    FParamType = aValueType;
  }

  public void SetExecuteList( ModelExecuteList aExecList ){
    FExecList = aExecList;
  }

  protected boolean IsExecuteListInjected(){
    return (FExecList != null);
  }

  protected void AddHistoryChangeListener() {
  	FVarChangeListener = new ValueChangeListener();

  	this.FVariable.AddChangeListener(FVarChangeListener);
  }

  /**
   *  Признак сохраняющейся истории изменений
   *
   * @return true, если для данного параметра сохраняется история изменений переменной
   */
  public boolean IsHistoryExists(){
  	return (FVarChangeListener != null);
  }

  public ValueChangeListener.HistoryBean GetHistoryBean(int index){
  	if (FVarChangeListener == null) {
  		return null;
  	}
  	return FVarChangeListener.GetBean(index);
  }

  /**
   *
   * @return возвращает true, если для данного параметра нужно вызывать метод Update() во время выполнения модели.
   * Т.е. это означает, что при вызове метода Update() есть надежда на то, что значение параметра может измениться
   */
  public abstract boolean IsNeedRuntimeUpdate();

  public void ApplyNodeInformation() throws ModelException{
    Node paramNode = GetNode();
		if (paramNode != null) {
			ModelAttributeReader reader = ServiceLocator.GetAttributeReader();
			reader.SetNode(paramNode);
			// проверяем, не должен ли параметр сохранять свою историю
			if (reader.GetSaveHistoryFlag()) {
				AddHistoryChangeListener();
			}
		}
  }

}

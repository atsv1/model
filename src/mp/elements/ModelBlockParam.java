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
  //���������, ����������� ������������ ��������� ������ �����
  public static int PLACEMENT_TYPE_INNER = 1;// ���������� ��������
  public static int PLACEMENT_TYPE_OUT = 2; // �������� ��������
  public static int PLACEMENT_TYPE_INPUT = 3;// ������� ��������

  //���������, ����������� ��� ��������� - ������������ ��� ��������������
  public static final int PARAM_TYPE_INFORM = 1; //�������������� ��������
  public static final int PARAM_TYPE_MATERIAL = 2; //������������ ��������

  private int FParamPlacementType = 0;
  protected int FParamType = 0;

  private Vector FDependElements = null; //������ ������ ���������, ������� ���������� � ����� �����������
                                                        //������ ��������
  private boolean FIsInpParamChanged = true; //����������, ��������� �� ���� �� ���� �� ����������, ������� ����������
                                             // ������ ��������
  protected ModelElementContainer FInpElements = null;//����� �������� ������ ���������, ������� ������������
                                                    // ������ ����������
  protected Variable FVariable = null;
  private String FInitValue = null;
  protected ExecutionContext FExecutionContext = null;

  /**��� ���� ������ ����� ��������� ������, ������� ������ �������� ���������� � ���, ����� �� �� �����-�� �������
   * ��������� ���������� ����� ��������.
   * ��� ����� ���� ����, ������� ����� �������� ����� Update() ������ ��� ��� ����������, ������� �����
   * ����������� (� ������������� �������� ����� �����, ����� ���������� ���� �� ���� �������� � ������ ����������,
   * �� ������� ������� ������ ��������)
   *
   */
  private ModelExecuteList FExecList = null;
  /*
   * � ���� ��������� ������ ����� ��������� ������� ��������� ����������
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
      System.out.println("���������� ��������� " + this.GetFullName() + ". ����� ������ = " +
              Double.toString(aCurrentTime.GetValue()) + " �������� ��������� = " +  FVariable.toString());
    }
  }

  public void AddInDependParams( ModelElement aElement ) throws ModelException{
    FDependElements.add( aElement );
  }

  /**������� ���������� ������ �� ��������, ������� ������� �� ������� ���������
   *
   * @param index - ������ ��������
   * @return - ������ �� ��������� ��������, ���� null, ���� ������ ������� �� ������� ������ ��������� ����������
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

  /** �����������, �������� �� ���������� � ��������� ������� ��������� ���������. ��������� ���������
   * ��������� ����� �������, ������� �������� ������ �� ����� ��������
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
      ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage() );
      throw e1;
    }
  }

  /**�������� ���������� (������� Variable).
   *
   * @param aVarType - ��������� �������� ���� ����������: integer, real, boolean
   * @param aInitValue - ��������� �������� ����������
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
      ModelException e1 = new ModelException( "������ � �������� \"" + GetFullName() + "\": " + error );
      throw e1;
    }
    ModelBlock block = (ModelBlock) owner;
    Model model = (Model)block.GetOwner();

    ModelConstant cnst = model.GetConstant( aInitValue );
    if ( cnst == null ) {
      ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + error );
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

  /**������ ������� �������� � ������� ��������� ������ �������� (��������-��������), ������� ������������ �
   * ����������� ��������  ��� ����� ���������. ����� ����������, ���� �������� ���������-��������� ����������.
   * @param aChangedParam - ������ �� ��������-��������
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

  /**������� ���������� "��", ���� ������ �������� ��������� � ���������, �.�. ��� ������ ��������� Update() ��������
   * ����� ��������� ���������.
   * @return
   */
  public boolean IsNeedToUpdate(){
    return FIsInpParamChanged;
  }

  /** ������� ���������, �������� �� ���������� � ��������� ������� ��� ����������, ������� ������������ �
   * ����������� ������ ������� ���������.
   * @param aElement - ����������� ��������
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
   *  ������� ������������� ������� ���������
   *
   * @return true, ���� ��� ������� ��������� ����������� ������� ��������� ����������
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
   * @return ���������� true, ���� ��� ������� ��������� ����� �������� ����� Update() �� ����� ���������� ������.
   * �.�. ��� ��������, ��� ��� ������ ������ Update() ���� ������� �� ��, ��� �������� ��������� ����� ����������
   */
  public abstract boolean IsNeedRuntimeUpdate();

  public void ApplyNodeInformation() throws ModelException{
    Node paramNode = GetNode();
		if (paramNode != null) {
			ModelAttributeReader reader = ServiceLocator.GetAttributeReader();
			reader.SetNode(paramNode);
			// ���������, �� ������ �� �������� ��������� ���� �������
			if (reader.GetSaveHistoryFlag()) {
				AddHistoryChangeListener();
			}
		}
  }

}

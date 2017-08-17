package mp.elements;

import mp.parser.*;
import mp.elements.AutomatTransitionTimeout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;


/**
 * ������� ������ �������� ��� ����� ���������:
 * 1. ��� �������� ( � Transition)
 * 2. ��� BeforeOut ������� ��������� ��������
 * 3. ��� AfterIn ������ ��������� ��������
 * @noinspection BreakStatement
 */
public class AutomatState extends ModelEventGenerator{
  private static String CODE_TYPE_BEFORE_OUT = "BeforeOut";
  private static String CODE_TYPE_INIT = "Init";
  private static String CODE_TYPE_AFTER_IN = "AfterIn";

  private ScriptParser FCodeInit = null;
  private ScriptParser FCodeAfterIn = null;
  private ScriptParser FCodeBeforeOut = null;

  private ModelElementContainer FInnerStates = null;
  private ModelElementContainer FTransitions = null;

  protected ScriptLanguageExt FLanguageExt = null;
  private boolean FInitCodeExecuted = false;

  private AutomatState FActiveState = null;
  private ModelElementDataSource FAttrReader = null;

  private ModelTime FActivateTime = new ModelTime(0);
  Vector transitions = new Vector();
  protected ExecutionContext FExecutionContext = null;

  public AutomatState(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FInnerStates = new ModelElementContainer();
    FTransitions = new ModelElementContainer();
    FExecutionContext = new ExecutionContext(this.GetFullName());
  }

  private void ReadCode(ModelElementDataSource aCodeElement) throws ModelException, ScriptException {
    String s = aCodeElement.GetAutomatCodeType();
    if ( s == null || "".equalsIgnoreCase( s ) ){
      ModelException e = new ModelException("����������� �������� ���� ������������ ���� � �������� \"" + this.GetFullName() + "\"");
      throw e;
    }
    String code = aCodeElement.GetexecutionCode();    
    if ( code == null ){
      return;
    }
    if ( CODE_TYPE_BEFORE_OUT.equalsIgnoreCase( s ) ){
      SetBeforeOutCode( code );      
      return;
    }
    if ( CODE_TYPE_INIT.equalsIgnoreCase( s ) ){
      SetInitCode( code );
      return;
    }
    if ( CODE_TYPE_AFTER_IN.equalsIgnoreCase( s ) ){
      SetAfterInCode( code );      
      return;
    }
    ModelException e = new ModelException("����������� ��� ������������ ���� � �������� \"" + this.GetName() + "\"");
    throw e;
  }

  private void ApplyTransitionNodeInformation() throws ModelException, ScriptException{
    int i = 0;
    AutomatTransition transition;
    while ( i < FTransitions.size() ){
      transition = (AutomatTransition) FTransitions.get(i);
      transition.ApplyNodeInformation();
      i++;
    }
  }

  private void ApplyChildNodeInformation() throws ModelException, ScriptException{
    int i = 0;
    AutomatState state;
    while ( i < FInnerStates.size() ){
      state = (AutomatState) FInnerStates.get(i);
      state.ApplyNodeInformation();
      i++;
    }
  }

  public void ApplyNodeInformation() throws ModelException, ScriptException{
    ModelElementDataSource ds = this.GetDataSource();
    if ( ds == null ){
      ModelException e = new ModelException("������ ���� � �������� \"" + this.GetFullName() + "\"");
      throw e;
    }
    
    // ������ ����������� ���
    List<ModelElementDataSource> codeList = ds.GetCodeElements();
    if ( codeList != null ) {
    	for ( ModelElementDataSource codeElement : codeList ) {
    		ReadCode( codeElement );
    	}
    }    
    ApplyTransitionNodeInformation();
    ApplyChildNodeInformation();
  }

  public void SetlanguageExt( ScriptLanguageExt aLanguageExt ){
    FLanguageExt = aLanguageExt;
    int i = 0;
    AutomatState state = null;
    while ( i < FInnerStates.size() ){
      state = (AutomatState) FInnerStates.get( i );
      state.SetlanguageExt( FLanguageExt );
      i++;
    }
    i = 0;
    AutomatTransition transition;
    while ( i < FTransitions.size() ){
      transition = (AutomatTransition) FTransitions.get( i );
      transition.SetlanguageExt( FLanguageExt );
      i++;
    }
  }

  protected void SetActiveChildState( String aStateName ) throws ModelException{
    if ( aStateName == null || "".equalsIgnoreCase( aStateName ) ){
      ModelException e = new ModelException("� ��������� \"" + this.GetName() +
              "\" ������� ������� ���������� �������� ��������� � ������ ������  ");
      throw e;
    }
    AutomatState state = (AutomatState) FInnerStates.Get( aStateName );
    if ( state == null ){
      ModelException e = new ModelException("� ��������� \"" + this.GetName() + "\" ����������� ���������  \"" +
              aStateName + "\"" );
      throw e;
    }
    FActiveState = state;
  }

  protected AutomatState GetActiveState(){
    return FActiveState;
  }

  /**������� ���������� ������, ���������� �� ��������� �������. �.�. ��� ������ AutomatTransition, �������
   * ������ true ���  ������ ������� IsTransitionEnabled()
   *
   * @return ������ ������� AutomatTransition
   * @throws ModelException ���������, ���� ��������� �������� �������� ����� ����������� �������
   */
  private AutomatTransition GetAvailableTransition( ModelTime aCurrentTime ) throws ModelException{
    int i = 0;
    AutomatTransition transition;
    // ������� � ������������ �����������, �.�. ��� ����� �������
    int maxPriority = 0;
    //Vector transitions = new Vector();
    transitions.removeAllElements();
    int transitionsCount = FTransitions.size();
    while ( i < transitionsCount ){
      transition = (AutomatTransition) FTransitions.get( i );
      if ( transition.IsTransitionEnabled( aCurrentTime ) ){
        if ( transition.GetPriority() >= maxPriority ) {
          if ( transition.GetPriority() > maxPriority   ) {
            transitions.clear();
            transitions.add( transition );
            maxPriority = transition.GetPriority();
          } else {
            if ( maxPriority == transition.GetPriority() ) {
              transitions.add( transition );
            }
          }
        }
      }
      i++;
    }
    if ( transitions.size() > 1 ){
      //��������� ��������� �� ������
      String s = "";
      i = 0;
      while ( i < transitions.size() ){
        transition = (AutomatTransition) transitions.get( i );
        s = s + " " + "\"" + transition.GetName() + "\"" + "(" +  Integer.toString( transition.GetPriority() ) +  ")";
        i++;
      }
      ModelException e = new ModelException("� ��������� \"" + this.GetFullName() +
              "\" ������� ��������� ������������ ������������� ��������� :" + s);
      throw e;
    }
    if (transitions.size() == 0){
      return null;
    }
    return (AutomatTransition) transitions.get(0);
  }

  /**
   * ��������� ���������� ��� ��������� ���������. ����� ������ ���������� ��� ������������� ���������.
   * ��� ��������� ��������� ����������:
   * 1. ����� ���� CodeAfterIn
   * 2. �����������, �� ����������� �� �����-���� �� ������� ��������. ���� �����������, �� ���������� ��� �������� �
   *   ������������ �������� ���������� ���������
   * 3. �����������, ���� �� ��������� ��������. ���� ����� �������� ����, �� ��������� ������� � ������ begin �
   *   ���������� ��� ����� SetActive(). ���� ��������� �������� ����, �� �������� � ������ begin ���, �� ����������
   *   ����������.
   * @return - null, ���� �� ��������� ������������ �������� � ��������� ���������, ���� �������� ���������� �����
   */
  public String SetActive( ModelTime aCurrentTime ) throws ScriptException, ModelException {
    FActivateTime.StoreValue( aCurrentTime );
    try{
      this.ExecuteAfterInCode();
    } catch (ScriptException e){
      ModelException e1 = new ModelException("��� ���������� ���� AfterIn �������� \"" +
         this.GetFullName() + "\" ��������� ������: " + e.getMessage() );
      throw e1;
    }
    AutomatTransition transition = GetAvailableTransition( aCurrentTime );
    if ( transition != null ){
      transition.ExecuteTransitionCode( aCurrentTime );
      return  transition.GetNextStateName();
    }
    if ( FInnerStates.size() > 0 ){
      //���� ��������� ���������
      SetActiveChildState("begin");
      String s = FActiveState.SetActive( aCurrentTime );
      while ( s != null ){
        ChangeActiveState( s,  aCurrentTime);
        s = FActiveState.SetActive( aCurrentTime );
      }
    }
    return null;
  }

  /**����� �������� ��������� ��������� �� ����� �������� ���������. ����� ������ ���������� ��� �������������
   * ���������.
   * �����������:
   * 1. ����� ��������� �� ����������� �����.
   * 2. ����� � ������� ��������� ��������� ������� BeforeOut
   * 3. ����� � ������ ��������� ��������� ������� SetActive()
   *
   * @param aNewStateName - �������� ���������, ������� ������ ����� ��������
   * @throws ModelException
   * @return - ���������� ��������� ���������� ������� SetActive() ������ ���������
   */
  protected String ChangeActiveState( String aNewStateName, ModelTime aCurrentTime ) throws ModelException, ScriptException{
    AutomatState newActiveState = (AutomatState) FInnerStates.Get( aNewStateName );
    if ( newActiveState == null ){
      ModelException e = new ModelException("� ��������� \"" + this.GetName() + "\" ����������� ���������  \"" +
              aNewStateName + "\"" );
      throw e;
    }
    if ( FActiveState != null ){
      FActiveState.ExecuteBeforeOutCode();
    }
    FActiveState = newActiveState;
    return newActiveState.SetActive( aCurrentTime );
  }

  /**��������� �������� � ��������� �������� ���������� �������. �������� �������� ������ Execute() ��� ������
   * ��� ��������� ������ �������� ���������� �������, ��������� ����������:
   * 1. ��������, �������� �� �����-���� �������  �� ������� ���������: ����� ����������� transitions. ���� �������
   *   ��������, �� ������������ ����� �� ���������, � ������������ �������� �����, � ������� ������������ �������.
   *   ����� ������� �� ��������� ����������� ��� ��������.
   * 2. ����� ���� �� ������� ��� �������� ��������� ��������� (���������� ���������). ���� ������� ��������� ������
   *    �������� ������ �����, �� ������������ ����� �������� ����� - ����� ChangeActiveState().
   *
   * @param aCurrentTime - �������� �������� ���������
   * @return - �������� ���������, � ������� ������ ���� ����������� �������, ���� null, ���� �������� �������� ��
   *          ���������
   * @throws ModelException
   */
  public String SetTime(ModelTime aCurrentTime) throws ModelException, ScriptException{
    if ( GlobalParams.StateNameOutputEnabled() ){
      if ( aCurrentTime != null ){
        System.out.println( "����� � SetTime \"" + this.GetFullName() + "\" ����� = " + aCurrentTime.GetStringValue());
      } else {
        System.out.println( "����� � SetTime \"" + this.GetFullName() + "\" ����� = null");
      }
    }
    AutomatTransition transition = GetAvailableTransition( aCurrentTime );
    if ( transition != null ){
      transition.ExecuteTransitionCode( aCurrentTime );
      String s = transition.GetNextStateName();
      if ( GlobalParams.StateNameOutputEnabled() ){
        System.out.println("���� ������������� ������� \"" + transition.GetFullName() + "\". ������� � ��������� \"" + s + "\"");
      }
      if ( s == null || "".equalsIgnoreCase( s ) ){
        ModelException e = new ModelException("������ � �������� \"" + GetFullName() +
                "\": ����������� ���������� � ��������� ���������" );
        throw e;
      }
      return  s;
    }
    if ( FActiveState == null ){
      return null;
    }
    String nextStateName = FActiveState.SetTime(aCurrentTime);
    while ( nextStateName != null ){ //@todo ������������� ����������� ������ �� ������������ ������������
      if ( GlobalParams.StateNameOutputEnabled() ){
        System.out.println(" �������� �������� ���������� ��������� ��������� \"" + nextStateName + "\"");
      }
      nextStateName = ChangeActiveState( nextStateName, aCurrentTime );
    }
    if ( GlobalParams.StateNameOutputEnabled() ){
      if ( aCurrentTime != null ){
        System.out.println("����� �� SetTime \""+ this.GetFullName() + "\" ����� = " + aCurrentTime.GetStringValue());
      } else {
        System.out.println("����� �� SetTime \""+ this.GetFullName() + "\" ����� = null");
      }
      System.out.println("");
    }
    return null;
  }

  /**������� ���������� ����� ���������� �������
   * @param  aCurrentTime - �������� �������� �������
   * @return null, ���� ������� ������� ������������� ����������. �������� ������������
   * � ���������� ��������, �� ���� ��������� ����� ���������������� ���: "��������� ������� ���������� �� 5-� �����
   * ���������� �������". ��������� �������������: "���������� ������� ���������� ����� 5 ������ ���������� �������"
   */
  public ModelTime GetNearestEventTime(ModelTime aCurrentTime) throws ModelException {
    //�������� ��������� ����� �������� ��� ����������� ���������
    ModelTime selfTransitionsTime = GetNearestEventTime(aCurrentTime, FTransitions);
    if ( FActiveState == null ){
      return selfTransitionsTime;
    }
    ModelTime childNearestTime = FActiveState.GetNearestEventTime( aCurrentTime );
    if ( selfTransitionsTime == null && childNearestTime == null ){
      return null;
    }
    if ( selfTransitionsTime == null ){
      return childNearestTime;
    }
    if ( childNearestTime == null ){
      return selfTransitionsTime;
    }
    int i = selfTransitionsTime.Compare( childNearestTime );
    if ( i == ModelTime.TIME_COMPARE_LESS ){
      return selfTransitionsTime;
    }
    return childNearestTime;
  }

  /**���������� ������ ����������� ���������
   *
   * @param aInnerState - ����������� ���������
   * @throws ModelException - ������������ � ���� ������� - ���� �������� ������ ������, ���� ���� ������ �������
   * ��������� ��� ���������� ���������� ��������� � ����� �� ������
   */
  private void AddInnerState( AutomatState aInnerState ) throws ModelException{
    if ( aInnerState == null ){
      ModelException e = new ModelException("������� �������� ������ ��������� � ��������� " + this.GetName());
      throw e;
    }
    FInnerStates.AddElement( aInnerState );
  }

  private void AddTransition(AutomatTransition aTransition) throws ModelException{
    if (aTransition == null){
      ModelException e = new ModelException("������� �������� ������ ����������� � ��������� " + this.GetName());
      throw e;
    }
    FTransitions.AddElement( aTransition );
  }

  public void AddElement(ModelElement aElement) throws ModelException{
    if ( aElement == null ){
      ModelException e = new ModelException("������� �������� ������ ������� � ��������� " + this.GetName());
      throw e;
    }
    
    if ( aElement instanceof AutomatState ){
      AddInnerState((AutomatState)aElement);
      return;
    }
    
    if ( aElement instanceof  AutomatTransition ){
      AddTransition( (AutomatTransition) aElement );
      return;
    }
    ModelException e = new ModelException("������� �������� ����������� ������� \"" + aElement.GetName() +
            "\" � ��������� " + this.GetName());
    throw e;
  }

  private ScriptParser SetCode(String aCode, String aSectionName) throws ModelException{
    if ( aCode == null || "".equalsIgnoreCase( aCode ) ){
      return null;
    }
    if ( FLanguageExt == null ){
      ModelException e = new ModelException( "����������� ������ ����������. ������������� ������� ����������" );
      throw e;
    }
    ScriptParser result = null;
    try{
      result = ParserFactory.GetParser( FLanguageExt, aCode );
    } catch (ScriptException e){
      ModelElement owner = this.GetOwner();
      String s = "";
      if ( owner != null ){
        s = owner.GetName();
      }
      ModelException e1 = new ModelException("������ � �������. ������� \"" + s + "." + this.GetName() +
              "\" ������ \"" + aSectionName + "\" " +
               e.getMessage());
      throw e1;
    }
    return result;
  }

  protected void SetInitCode(String aCode) throws ModelException, ScriptException{
    if ( FCodeInit == null ){
      //FCodeInit = new PascalParser( );
    }
    FCodeInit = SetCode(aCode, "Init");
  }

  protected void SetAfterInCode(String aCode) throws ModelException, ScriptException{
    if ( FCodeAfterIn == null ){
      //FCodeAfterIn = new PascalParser( );
    }
    FCodeAfterIn = SetCode(aCode, "AfterIn");
  }

  protected void SetBeforeOutCode(String aCode) throws ModelException, ScriptException{
    if ( FCodeBeforeOut == null ){
      //FCodeBeforeOut = new PascalParser( );
    }
    FCodeBeforeOut = SetCode(aCode, "BeforeOut");
  }

  protected void ExecuteInitCode() throws ScriptException{
    if ( !(FCodeInit == null || FInitCodeExecuted)  ){
    	FCodeInit.AddExecutionContext(FExecutionContext);
      FCodeInit.ExecuteScript();
      FInitCodeExecuted = true;
    }
    AutomatState childState;
    int i = 0;
    while ( i < FInnerStates.size() ){
      childState = (AutomatState) FInnerStates.get( i );
      childState.ExecuteInitCode();
      i++;
    }
  }

  protected void ExecuteBeforeOutCode() throws ScriptException{
    if ( FCodeBeforeOut == null ){
      //System.out.println( GetFullName() +  " null ExecuteBeforeOutCode");
      return;
    }
    FCodeBeforeOut.AddExecutionContext(FExecutionContext);
    FCodeBeforeOut.ExecuteScript();
    //System.out.println(GetFullName() + " ExecuteBeforeOutCode");
  }

  protected void ExecuteAfterInCode()throws ScriptException {
    if ( FCodeAfterIn == null ){
      //System.out.println(GetFullName() + " null ExecuteAfterInCode");
      return;
    }
    FCodeAfterIn.AddExecutionContext(FExecutionContext);
    FCodeAfterIn.ExecuteScript();
    //System.out.println(GetFullName() + " ExecuteAfterInCode");
  }

  public AutomatState GetState(int index){
    if ( index >= FInnerStates.size() ){
      return null;
    }
    return (AutomatState) FInnerStates.get( index );
  }

  public AutomatState GetState(String aStateName){
    return (AutomatState) FInnerStates.Get( aStateName );
  }

  public AutomatTransition GetTransition(int index){
    if ( index >= FTransitions.size() ){
      return null;
    }
    return (AutomatTransition) FTransitions.get( index );
  }

  public AutomatTransition GetTransition(String aTransitionName){
    return (AutomatTransition) FTransitions.Get( aTransitionName );
  }

  /** ������� ������������� ��� ������ ������ ���������� � ���, ���������� � ���� �������� ��������, ������������
   * ������� ������� �� ���������� �������.
   *
   * @return ������������ true, ���� � �������� ���������� ��������, ��������� �� �������. ����� ������������ false
   */
  protected boolean IsExistsTimeDependentElements(){
    boolean result = false;
    int i = 0;
    AutomatTransition transition;
    while ( (FTransitions != null) && (i < FTransitions.size()) ){
      transition = (AutomatTransition) FTransitions.get( i );
      if ( transition instanceof AutomatTransitionTimeout ) {
        return true;
      }
      i++;
    }
    AutomatState state;
    i = 0;
    while ( FInnerStates != null && i < FInnerStates.size() ){
      state = (AutomatState) FInnerStates.get( i );
      if ( state.IsExistsTimeDependentElements() ) {
        return true;
      }
      i++;
    }
    return result;
  }

  public String toString(){
    return GetFullName();
  }

  protected ModelTime GetActivateTime(){
    return FActivateTime;
  }
  
  protected Map<UUID, RollbackData> fixedStates = new HashMap<UUID, RollbackData> ();
  
  
  private void doFix(ModelElementContainer elements, UUID stateLabel, int operation) throws ModelException {
  	if ( elements == null || elements.size() == 0 ) {
  		return;
  	}
  	int i = 0;
  	ModelElement element;  	
  	while ( i < elements.size() ) {
  		element = elements.get(i);  		
  		if (operation == 1 ) {
  		  element.fixState(stateLabel);
  		} else if (operation == 2) {
  			element.rollbackTo(stateLabel);
  		} else throw new ModelException("������������ ��������");
  		i++;
  	}
  	
  }
  
  public void fixState(UUID stateLabel) throws ModelException{
  	if (fixedStates.containsKey(stateLabel)) {
  		throw new ModelException("������������ �������������� ���������");
  	}
  	RollbackData rd = new RollbackData();
  	rd.actualState = FActiveState;
  	rd.mt = new ModelTime();
  	rd.mt.StoreValue(FActivateTime);
  	fixedStates.put(stateLabel, rd);
  	doFix(FInnerStates, stateLabel, 1);
  	doFix(FTransitions, stateLabel, 1);
  	  	
  }
  
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	RollbackData rd = fixedStates.get(stateLabel); 
  	fixedStates.remove(stateLabel);
  	FActiveState = rd.actualState;
  	FActivateTime.StoreValue(rd.mt);  	
  	doFix(FInnerStates, stateLabel, 2);
  	doFix(FTransitions, stateLabel, 2);
  	  	
  }
  
  private static class RollbackData{
  	public ModelTime mt = null;
  	public AutomatState actualState = null;
  }

}

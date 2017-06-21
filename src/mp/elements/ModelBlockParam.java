package mp.elements;

import mp.parser.*;
import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.w3c.dom.Node;

/**
 * User: atsv
 * Date: 15.09.2006
 */
public abstract class ModelBlockParam extends ModelElement{
  protected ScriptLanguageExt FLanguageExt = null;
  //Êîíñòàíòû, îïèñûâàþùèå ðàñïîëîæåíèå ïàðàìåòðà âíóòðè áëîêà
  public static int PLACEMENT_TYPE_INNER = 1;// âíóòðåííèé ïàðàìåòð
  public static int PLACEMENT_TYPE_OUT = 2; // âûõîäíîé ïàðàìåòð
  public static int PLACEMENT_TYPE_INPUT = 3;// âõîäíîé ïàðàìåòð

  //Êîíñòàíòû, îïèñûâàþùèå òèï ïàðàìåòðà - ìàòåðèàëüíûé èëè íåìàòåðèàëüíûé
  public static final int PARAM_TYPE_INFORM = 1; //íåìàòåðèàëüíûé ïàðàìåòð
  public static final int PARAM_TYPE_MATERIAL = 2; //ìàòåðèàëüíûé ïàðàìåòð

  private int FParamPlacementType = 0;
  protected int FParamType = 0;

  private Vector FDependElements = null; //Õðàíèò ñïèñîê ýëåìåíòîâ, êîòîðûå èñïîëüçóþò â ñâîèõ âû÷èñëåíèÿõ
                                                        //äàííûé ïàðàìåòð
  private boolean FIsInpParamChanged = true; //ïîêàçûâàåò, èçìåíèëñÿ ëè õîòÿ áû îäèí èç ïàðàìåòðîâ, êîòîðûå èñïîëüçóåò
                                             // äàííûé ïàðàìåòð
  protected ModelElementContainer FInpElements = null;//çäåñü õðàíèòñÿ ñïèñîê ýëåìåíòîâ, êîòîðûå èñïîëüçóþòñÿ
                                                    // äàííûì ïàðàìåòðîì
  protected Variable FVariable = null;
  private String FInitValue = null;
  protected ExecutionContext FExecutionContext = null;

  /**Ýòî ïîëå äîëæåí áóäåò çàïîëíèòü îáúåêò, êîòîðûé æåëàåò ïîëó÷àòü èíôîðìàöèþ î òîì, íóæíî ëè âû êàêèì-òî îáðàçîì
   * âûïîëíÿòü îáíîâëåíèå ýòîãî ýëåìåíòà.
   * Ýòî ìîæåò áûòü áëîê, êîòîðûé áóäåò âûçûâàòü ìåòîä Update() òîëüêî äëÿ òåõ ïàðàìåòðîâ, êîòîðûå íóæíî
   * ïåðåñ÷èòàòü (à ïåðåñ÷èòûâàòü ïàðàìåòð íóæíî òîãäà, êîãäà èçìåíèëîñü õîòÿ áû îäíî çíà÷åíèå â ñïèñêå ïàðàìåòðîâ,
   * îò êîòîðûõ çàâèñèò äàííûé ïàðàìåòð)
   *
   */
  private ModelExecuteList FExecList = null;
  /*
   * Â ýòîì ëèñòåíåðå äîëæíà áóäåò õðàíèòüñÿ èñòîðèÿ èçìåíåíèé ïåðåìåííîé
   * */
  private ValueChangeListener FVarChangeListener = null;

  /**
   *  õðàíèëèùå ñîñòîÿíèé äëÿ îòêàòà íà ïðåæíþþ òî÷êó
   */
  protected Map<UUID, Object> fixedStates = new HashMap<UUID, Object> ();


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
      System.out.println("Âûïîëíåíèå ïàðàìåòðà " + this.GetFullName() + ". Âðåìÿ ìîäåëè = " +
              Double.toString(aCurrentTime.GetValue()) + " Çíà÷åíèå ïàðàìåòðà = " +  FVariable.toString());
    }
  }

  public void AddInDependParams( ModelElement aElement ) throws ModelException{
    FDependElements.add( aElement );
  }

  /**Ôóíêöèÿ âîçâðàùàåò ññûëêó íà ïàðàìåòð, êîòîðûé çàâèñèò îò äàííîãî ïàðàìåòðà
   *
   * @param index - èíäåêñ ýëåìåíòà
   * @return - ññûëêà íà çàâèñèìûé ïàðàìåòð, ëèáî null, åñëè èíäåêñ âûõîäèò çà ïðåäåëû ñïèñêà çàâèñèìûõ ïàðàìåòðîâ
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

  /** Ïðîâåðÿåòñÿ, ÿâëÿåòñÿ ëè ïåðåäàííûé â ïàðàìåòðå ýëåìåíò çàâèñèìûì ýëåìåíòîì. Çàâèñèìûì ýëåìåíòîì
   * ñ÷èòàåòñÿ òàêîé ýëåìåíò, êîòîðûé ïîëó÷àåò äàííûå èç ýòîãî ýëåìåíòà
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
      ModelException e1 = new ModelException("Îøèáêà â ýëåìåíòå \"" + GetFullName() + "\": " + e.getMessage() );
      throw e1;
    }
  }

  /**Ñîçäàíèå ïåðåìåííîé (îáúåêòà Variable).
   *
   * @param aVarType - òåêñòîâîå íàçâàíèå òèïà ïåðåìåííîé: integer, real, boolean
   * @param aInitValue - òåêñòîâîå çíà÷åíèå ïåðåìåííîé
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
      ModelException e1 = new ModelException( "Îøèáêà â ýëåìåíòå \"" + GetFullName() + "\": " + error );
      throw e1;
    }
    ModelBlock block = (ModelBlock) owner;
    Model model = (Model)block.GetOwner();

    ModelConstant cnst = model.GetConstant( aInitValue );
    if ( cnst == null ) {
      ModelException e1 = new ModelException("Îøèáêà â ýëåìåíòå \"" + GetFullName() + "\": " + error );
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

  /**Äàííóþ ôóíêöèþ âûçûâàåò ó äàííîãî ïàðàìåòðà äðóãîé ïàðàìåòð (ïàðàìåòð-èñòî÷íèê), êîòîðûé èñïîëüçóåòñÿ â
   * âû÷èñëåíèÿõ çíà÷åíèÿ  äëÿ ýòîãî ïàðàìåòðà. Âûçîâ ïðîèñõîäèò, åñëè çíà÷åíèå ïàðàìåòðà-èñòî÷íèêà èçìåíèëîñü.
   * @param aChangedParam - ññûëêà íà ïàðàìåòð-èñòî÷íèê
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

  /**Ôóíêöèÿ âîçâðàùàåò "äà", åñëè äàííûé ïàðàìåòð íóæäàåòñÿ â ïåðåñ÷åòå, ò.å. ïðè âûçîâå ïðîöåäóðû Update() çíà÷åíèå
   * ýòîãî ïàðàìåòðà èçìåíèòñÿ.
   * @return
   */
  public boolean IsNeedToUpdate(){
    return FIsInpParamChanged;
  }

  /** Ôóíêöèÿ ïðîâåðÿåò, ÿâëÿåòñÿ ëè ïåðåäàííûé â ïàðàìåòðå ýëåìåíò òåì ïàðàìåòðîì, êîòîðûé èñïîëüçóåòñÿ â
   * âû÷èñëåíèÿõ âíóòðè äàííîãî ïàðàìåòðà.
   * @param aElement - ïðîâåðÿåìûé ïàðàìåòð
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
   *  Ïðèçíàê ñîõðàíÿþùåéñÿ èñòîðèè èçìåíåíèé
   *
   * @return true, åñëè äëÿ äàííîãî ïàðàìåòðà ñîõðàíÿåòñÿ èñòîðèÿ èçìåíåíèé ïåðåìåííîé
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
   * @return âîçâðàùàåò true, åñëè äëÿ äàííîãî ïàðàìåòðà íóæíî âûçûâàòü ìåòîä Update() âî âðåìÿ âûïîëíåíèÿ ìîäåëè.
   * Ò.å. ýòî îçíà÷àåò, ÷òî ïðè âûçîâå ìåòîäà Update() åñòü íàäåæäà íà òî, ÷òî çíà÷åíèå ïàðàìåòðà ìîæåò èçìåíèòüñÿ
   */
  public abstract boolean IsNeedRuntimeUpdate();

  public void ApplyNodeInformation() throws ModelException{
    Node paramNode = GetNode();
		if (paramNode != null) {
			ModelAttributeReader reader = ServiceLocator.GetAttributeReader();
			reader.SetNode(paramNode);
			// ïðîâåðÿåì, íå äîëæåí ëè ïàðàìåòð ñîõðàíÿòü ñâîþ èñòîðèþ
			if (reader.GetSaveHistoryFlag()) {
				AddHistoryChangeListener();
			}
		}
  }
  
  
  public void fixState(UUID stateLabel) throws ModelException{
  	if (fixedStates.containsKey(stateLabel)) {
  		throw new ModelException("Äóáëèðîâàíèå ôèêñèðîâàííîãî ñîñòîÿíèÿ "+ this.GetFullName());
  	}   	
  	fixedStates.put(stateLabel, this.GetVariable().GetObject());
  }
    
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	if (!fixedStates.containsKey(stateLabel)) {
  		throw new  ModelException("Îòñóòñòâóåò ìåòêà äëÿ îòêàòà " + this.GetFullName());  		
  	}
  	this.GetVariable().SetValue( fixedStates.get(stateLabel) ); 

  	fixedStates.remove(stateLabel);

  	  	
  }

}

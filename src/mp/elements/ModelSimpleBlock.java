package mp.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mp.parser.*;

/**
 * User: atsv
 * Date: 18.09.2006
 */
public class ModelSimpleBlock extends ModelBlock {
  private boolean FPreparedFlag = false;
  private int FLastExecutionTime = -1;
  private ScriptLanguageExt FLanguageExt = null;

  private long FExecDuration = 0;
  private long FExecCount = 0;

  public ModelSimpleBlock(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }


  public void SetLanguageExt(ScriptLanguageExt aLanguageExt) {
    SetLanguageExtToInnerParam( aLanguageExt );
    SetLanguageExtToOutParam( aLanguageExt );
    SetLanguageExtToStatecharts( aLanguageExt );
    SetLanguageExtToEventContainer( aLanguageExt );
    FLanguageExt = aLanguageExt;
  }

  public ScriptLanguageExt GetLanguageExt(){
    return FLanguageExt;
  }

  private void PrepareBlock() throws ModelException{
    PrepareParamsOrder();
    FPreparedFlag = true;
  }

  public void Execute() throws ModelException, ScriptException {
    if ( !FPreparedFlag ){
      PrepareBlock();
    }
    ModelBlockParam param = null;
    int i = 0;
    while ( i < FOrderParamsList.size() ){
      param = (ModelBlockParam) FOrderParamsList.get( i );
      param.Update();
      i++;
    }
  }

  private void ExecuteParamsUpdate(ModelTime aCurrentTime) throws ModelException, ScriptException{
    if ( !FPreparedFlag ){
      PrepareBlock();
    }
    UpdateAllParams( aCurrentTime );
  }

  /**Âûïîëíåíèå îáíîâëåíèÿ çíà÷åíèé âñåõ ïàðàìåòðîâ. Âûïîëíåíèå ïðîèçâîäèòñÿ òîëüêî â òîì ñëó÷àå, åñëè ìîäåëüíîå
   * âðåìÿ ïðåâûñèëî ñëåäóþùåå öåëîå çíà÷åíèå.
   * Ìåòîä âûçûâàåòñÿ òîëüêî òîãäà, êîãäà â áëîêå íåò íè îäíîãî ñòýéò÷àðòà
   * @param aCurrentTime
   * @throws ModelException
   * @throws ScriptException
   */
  private void ExecuteInTime(ModelTime aCurrentTime) throws ModelException, ScriptException{
    if ( aCurrentTime == null ){
      ModelException e = new ModelException("Â ýëåìåíò \"" + GetFullName() + "\" ïåðåäàíà ïóñòàÿ ññûëêà íà ìîäåëüíîå âðåìÿ");
      throw e;
    }
    if ( GlobalParams.ExecTimeOutputEnabled() ){
      System.out.println( "Íà÷àëî âûïîëíåíèÿ ïàðàìåòðîâ â áëîêå " + this.GetFullName() + ". Âðåìÿ ìîäåëè = " +
              Double.toString(aCurrentTime.GetValue()) + " ïîñëåäíåå âðåìÿ âûïîëíåíèÿ áëîêà = " + Integer.toString(FLastExecutionTime) );
    }
    int currentTimeValue = aCurrentTime.GetIntValue();    
    if ( currentTimeValue > FLastExecutionTime){      
    	// ñäåëàíî ñïåöèàëüíî, ÷òîáû ïàðàìåòðû îáíîâëÿëèñü òîëüêî åñëè ìîäåëüíîå âðåìÿ ïåðåâàëèâàåò î÷åðåäíîå öåëîå çíà÷åíèå
    	ExecuteParamsUpdate( aCurrentTime );  
      FLastExecutionTime = currentTimeValue;
    } else {
      if ( GlobalParams.ExecTimeOutputEnabled() ){
        System.out.println( "Íè÷åãî íå âûïîëíÿëîñü " + this.GetFullName() + " ïîñëåäíåå âðåìÿ âûïîëíåíèÿ áëîêà = " + Integer.toString(FLastExecutionTime)  );
      }
    }
  }


  /** Ìåòîä îáåñïå÷èâàåò ïåðåäà÷ó ìîäåëüíîãî âðåìåíè â ñòýéò÷àðòû äàííîãî áëîêà. Åñëè ñòýéò÷àðòà íåò, òî âûçûâàåòñÿ
   * ìåòîä Execute() - êîòîðûé áåç ïàðàìåòðîâ.
   * Ìåòîä äåéñòâóåò ñëåäóþùèì îáðàçîì:
   * 1. Ïåðåäàåòñÿ ìîäåëüíîå âðåìÿ â ñòýéò÷àðòû
   * 2. Ïåðåñ÷èòûâàþòñÿ çíà÷åíèÿ äëÿ òåõ ïåðåìåííûõ áëîêà, êîòîðûå íóæäàþòñÿ â ïåðåñ÷åòå.
   * Ïðàâèëà âûçîâà ìåòîäà Execute(), åñëè â áëîêå îòñóòñòâóåò ñòýéò÷àðò:
   * ìåòîä âûçûâàåòñÿ òîëüêî òîãäà, êîãäà çíà÷åíèå ìîäåëüíîãî âðåìåíè ïåðåâàëèâàåò ÷åðåç î÷åðåäíîå öåëîâå çíà÷åíèå.
   * @param aCurrentTime - çíà÷åíèå òåêóùåãî ìîäåëüíîãî âðåìåíè
   * @throws ModelException
   * @throws ScriptException
   */
  public void Execute(ModelTime aCurrentTime) throws ModelException, ScriptException {
    long startTime = System.nanoTime();
    ExecuteEvents();
    int size = FRootStates.size();
    if ( size == 0 ){
      //íè îäíîãî ñòýéò÷àðòà íåò.
      ExecuteInTime( aCurrentTime );
      AddSelfToTimeManager( aCurrentTime );
      return;
    }
    //ñòýéò÷àðòû åñòü
    int i = 0;
    AutomatState state = null;
    while ( i < size ){
      state = (AutomatState) FRootStates.get( i );
      state.SetTime( aCurrentTime );
      i++;
    }
    //UpdateParamsWithChangedInput();
    //Execute();
    ExecuteInTime( aCurrentTime );
    AddSelfToTimeManager( aCurrentTime );
    if ( GlobalParams.ExecTimeOutputEnabled() ){
      System.out.println("Äîáàâèëè áëîê " + GetFullName() + " â ìåíåäæåð âðåìåíè. Òåêóùåå âðåìÿ " + aCurrentTime.toString()); 
    }
    long duration = System.nanoTime() - startTime;
    if ( duration < 0 ) duration = 0;
    FExecDuration = FExecDuration + duration;
    FExecCount++;
  }

  public void PrintExecutionTime() {
    int selfIndex = GetIntValue("selfIndex");
    double ms = FExecDuration / 1000000;
    System.out.println( "mux " + this.GetFullName() +"[" + Integer.toString(selfIndex) + "]" +  " count = " +
       Long.toString( FExecCount ) + " duration = " + Double.toString( ms ) + ".ms");
  }

  public boolean IsDynamicParamCreate() {
    return false;  
  }

  public void ApplyNodeInformation() throws ModelException, ScriptException{
    //âûçûâàåì ìåòîä ApplyNodeInformation() äëÿ âñåõ ïàðàìåòðîâ áëîêà (âíóòðåííèõ, âíåøíèõ è âûõîäíûõ).
    //Ïîíûé ñïèñîê ïàðàìåòðîâ õðàíèòñÿ â êîíòåéíåðå êëàññà ModelElement
    ApplyAllElementsNodeInformation();
    PrepareBlock();
    // âûçûâàåì ìåòîä ApplyNodeInformation() äëÿ ñòýéò÷àðòîâ áëîêà
    ApplyStatechartNodeInfo();
    //÷òåíèå èíôîðìàöèè îá îáðàáîò÷èêàõ ñîáûòèé
    ApplyEventNodeInfo();
  }
  
  private  Map<UUID, Integer> fixedStates = new HashMap<UUID, Integer> ();
  public void fixState(UUID stateLabel) throws ModelException{
  	super.fixState(stateLabel);  	  	

  	ModelBlockParam forkModeParam = super.GetInnerParam("isForkMode");
  	forkModeParam.GetVariable().SetValue(true);

  	fixedStates.put(stateLabel, new Integer(FLastExecutionTime));
  }
  
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	super.rollbackTo(stateLabel);
  	FLastExecutionTime = fixedStates.get(stateLabel);
  	fixedStates.remove(stateLabel);

  	if ( fixedStates.isEmpty() ) {
  		ModelBlockParam forkModeParam = super.GetInnerParam("isForkMode");
    	forkModeParam.GetVariable().SetValue(false);  		
  	}

  	
  }

}

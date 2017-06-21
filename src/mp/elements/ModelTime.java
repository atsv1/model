package mp.elements;

import mp.parser.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mp.parser.ScriptException;

/**
 */
public class ModelTime {
  public static int TIME_COMPARE_EQUALS =0;
  public static int TIME_COMPARE_LESS = 1;
  public static int TIME_COMPARE_GREATER = 2;
  public static int TIME_COMPARE_UNKNOWN = 3;
  public static int TIME_COMPARE_GREATER_EQUAL = 4;
  private static int TIME_FIDELITY = 1000;
  private double FValue = 0;

  public ModelTime(){

  }

  public ModelTime(double aTimeValue){
    FValue = aTimeValue;
  }

  public void SetTimeIncrement(double aTimeIncrement){

  }

  public double GetValue(){
    return FValue;
  }

  public int GetIntValue(){
    return (int)this.GetValue(); 
  }

  private static int Compare(int value1, int value2){
    if ( value1 == value2 ){
      return TIME_COMPARE_EQUALS;
    }
    if ( value1 > value2 ){
      return TIME_COMPARE_GREATER;
    }
    return TIME_COMPARE_LESS;
  }

  /**Ôóíêöèÿ ñðàâíåíèÿ âðåìåíè. Ñðàâíèâàåòñÿ âðåìÿ, êîòîðîå õðàíèòñÿ â äàííîì îáúåêòå ñ âðåìåíåì, ïåðåäàííûì â
   * ïàðàìåòðå
   *
   * @param aTimeMoment - âðåìÿ äëÿ ñðàâíåíèÿ
   * @return Âîçâðàùàåòñÿ TIME_COMPARE_EQUALS, åñëè äâà çíà÷åíèÿ ðàâíû,
   *         TIME_COMPARE_LOW, åñëè âðåìÿ âíóòðè îáúåêòà ìåíüøå, ÷åì ïåðåäàííîå âðåìÿ â ïàðàìåòðå,
   *         TIME_COMPARE_GREATER, åñëè âðåìÿ âíóòðè îáúåêòà áîëüøå, ÷åì âðåìÿ â ïàðàìåòðå
   */
  public int Compare( ModelTime aTimeMoment ){
    if ( aTimeMoment == null ){
      return TIME_COMPARE_UNKNOWN;
    }
    /** Àëãîðèòì ñðàâíåíèÿ íàïðàâëåí íà èñêëþ÷åíèå "øóìà", êîòîðûé ïðèñóòñòâóåò â òèïå double.
     * Ñðàâíåíèå ñîñòîÿèò èç ñëåäóþùèõ äåéñòâèé:
     * 1. áåðóòñÿ öåëûå ÷àñòè îáîèõ ÷èñåë è ñðàâíèâàþòñÿ. Ïðè èõ ðàâåíñòâå ðàáîòà àëãîðèòìà ïðîäîëæàåòñÿ. Èíà÷å - ñðàçó
     * ÿñåí ðåçóëüòàò ñòðàâíåíèÿ
     * 2. Äðîáíûå ÷àñòè îáîèõ ÷èñåë óìíîæåþòñÿ íà êîýôôèöèåò, îò íèõ áåðóòñÿ öåëûå ÷èñëà. Ñðàâíåíèå öåëûõ ÷èñåë äàñò
     * êîíå÷íûé ðåçóëüòàò
     */
    int thisValue = (int)FValue;
    int anotherValue = (int)aTimeMoment.GetValue();
    int res = Compare(thisValue, anotherValue);
    if ( res !=  TIME_COMPARE_EQUALS){
      return res;
    }
    thisValue = (int)((FValue - thisValue)*TIME_FIDELITY);
    anotherValue = (int)((aTimeMoment.GetValue() - anotherValue)*TIME_FIDELITY);
    if ( Math.abs( thisValue - anotherValue ) <= 1 ){
      return TIME_COMPARE_EQUALS;
    }
    return Compare(thisValue, anotherValue);
  }

  public void Add( double value ){
    FValue = FValue + value;
  }

  public void Add(Variable value){
    if ( value == null ){
      return;
    }
    try {
      FValue = FValue + value.GetFloatValue();
    } catch (ScriptException e) {
      //e.printStackTrace();
    }
  }

  public void Add(ModelTime time){
    if ( time == null ){
      return;
    }
    this.Add( time.GetValue() );
  }

  public void Sub( ModelTime aValue ){
    if ( aValue == null ){
      return;
    }
    FValue = FValue - aValue.FValue; 

  }

  public String GetStringValue(){
    return Double.toString( FValue );
  }

  public void StoreValue(ModelTime aValue){
    if ( aValue == null ){
      return;
    }
    FValue = aValue.FValue;
  }

  public String toString(){
    return "ModelTime = " + Double.toString( FValue );
  }

  public void TruncateTime(){
    int newVal = (int) FValue;
    FValue = newVal;
  }
  
  protected Map<UUID, Double> fixedStates = new HashMap<UUID, Double> ();
  
  public void  fixState(UUID uid) throws ModelException{
  	if (fixedStates.containsKey(uid)) {

  		throw new ModelException("����� ����� ��� �������������");

  	}   	
  	fixedStates.put(uid, new Double(FValue));
  	
  }
  
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	if (!fixedStates.containsKey(stateLabel)) {

  		throw new  ModelException("����������� ����� ��� ������");  		
  	}
  	FValue =  fixedStates.get(stateLabel);
  	fixedStates.remove(stateLabel);

  }

}

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

  /**������� ��������� �������. ������������ �����, ������� �������� � ������ ������� � ��������, ���������� �
   * ���������
   *
   * @param aTimeMoment - ����� ��� ���������
   * @return ������������ TIME_COMPARE_EQUALS, ���� ��� �������� �����,
   *         TIME_COMPARE_LOW, ���� ����� ������ ������� ������, ��� ���������� ����� � ���������,
   *         TIME_COMPARE_GREATER, ���� ����� ������ ������� ������, ��� ����� � ���������
   */
  public int Compare( ModelTime aTimeMoment ){
    if ( aTimeMoment == null ){
      return TIME_COMPARE_UNKNOWN;
    }
    /** �������� ��������� ��������� �� ���������� "����", ������� ������������ � ���� double.
     * ��������� �������� �� ��������� ��������:
     * 1. ������� ����� ����� ����� ����� � ������������. ��� �� ��������� ������ ��������� ������������. ����� - �����
     * ���� ��������� ����������
     * 2. ������� ����� ����� ����� ���������� �� ����������, �� ��� ������� ����� �����. ��������� ����� ����� ����
     * �������� ���������
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

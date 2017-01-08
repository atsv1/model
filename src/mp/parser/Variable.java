package mp.parser;

import mp.elements.ModelException;

import java.util.Vector;

/**
 * User: atsv
 * Date: 25.04.2006
 */
public class Variable extends Operand {
  private Vector FChangeListeners = null;
  VariableChangeEvent event = new VariableChangeEvent(this);

  public Variable(int aOperandValue) {
    super(aOperandValue);
  }

  public Variable(float aOperandValue) {
    super(aOperandValue);
  }

  public Variable(boolean aOperandValue)  {
    super( aOperandValue );
  }

  public Variable(double aOperandvalue){
    super((float)aOperandvalue);
  }

  public Variable(String aOperandValue) {
     super( aOperandValue );
  }

  public Variable() {
    super( 0 );
  }

  public static Variable CreateNewInstance( String aName, String aVarType, String aInitValue ) throws ScriptException{

    if ( aName == null || "".equalsIgnoreCase( aName ) ){
      ScriptException e = new ScriptException("Отсутствует имя");
      throw e;
    }
    if ( aVarType == null || "".equalsIgnoreCase( aVarType ) ){
      ScriptException e = new ScriptException("Отсутствует тип у объекта \"" + aName + "\"");
      throw e;
    }

    Variable newVariable;
    if ("integer".equalsIgnoreCase(aVarType) ){
      if ( aInitValue == null || aInitValue.equalsIgnoreCase("") )
      {
        aInitValue = "0";
      }
      try{
        newVariable = new Variable( Integer.parseInt( aInitValue ) );
      } catch (NumberFormatException e){
        ScriptException e1 = new ScriptException("Ошибка в элементе \"" + aName +
                "\" невозможно преобразовать строку " + aInitValue + " в начальное значение для элемента");
        throw e1;
      }
      newVariable.SetName( aName );
      return newVariable;
    }
    if ( "real".equalsIgnoreCase( aVarType ) ){
      if ( aInitValue == null || aInitValue.equalsIgnoreCase("") )
      {
        aInitValue = "0.0";
      }
      newVariable = new Variable( Float.parseFloat( aInitValue ) );
      newVariable.SetName( aName );
      return newVariable;
    }
    boolean boolValue;
    if ( "boolean".equalsIgnoreCase( aVarType ) ){
      if ( aInitValue == null || aInitValue.equalsIgnoreCase("") )
      {
        aInitValue = "false";
      }
      boolValue = "true".equalsIgnoreCase(aInitValue);
      newVariable = new Variable( boolValue );
      newVariable.SetName( aName );
      return newVariable;
    }
    if ( "string".equalsIgnoreCase( aVarType ) ){
      newVariable = new Variable( aInitValue );
      newVariable.SetName( aName );
      return newVariable;
    }
    ScriptException e = new ScriptException("Неизвестный тип \"" + aVarType + "\"" + " у объекта \"" + aName + "\"");
    throw e;
  }

  public Object clone(){
    Variable result = null;
    int i = GetType();
    try{
      switch ( i ){
        case OPERAND_TYPE_INTEGER: {
         result = new Variable( this.GetIntValue() );
          break;
        }
        case OPERAND_TYPE_REAL:{
          result = new Variable( this.GetFloatValue() );
          break;
        }
        case OPERAND_TYPE_BOOLEAN: {
          result = new Variable( this.GetBooleanValue() );
          break;
        }
        case OPERAND_TYPE_STRING:{
          result = new Variable( this.GetStringValue() );
          break;
        }
      }//swith
    } catch (Exception e){}
    result.SetName(this.GetName());
    return result;
  }

  public void StoreValueOf(Operand aNewValue) throws ScriptException{
    int i = GetType();
    switch ( i ){
      case OPERAND_TYPE_INTEGER: {
        this.SetValue( aNewValue.GetIntValue() );
        break;
      }
      case OPERAND_TYPE_REAL:{
        this.SetValue( aNewValue.GetFloatValue() );
        break;
      }
      case OPERAND_TYPE_BOOLEAN: {
        this.SetValue( aNewValue.GetBooleanValue() );
        break;
      }
      case OPERAND_TYPE_STRING:{
        this.SetValue( aNewValue.GetStringValue() );
        break;
      }
    }//swith
  }

  public void AddChangeListener( VariableChange listener ){
    if ( FChangeListeners == null ){
      FChangeListeners = new Vector();
    }
    if ( listener == null ){
      return;
    }
    FChangeListeners.add( listener );
  }

  protected void FireChangeEvent() {
    if ( FChangeListeners == null ){
      return;
    }
    int i = 0;
    VariableChange listener;
    //VariableChangeEvent event = new VariableChangeEvent(this);
    int size =  FChangeListeners.size();
    while ( i < size ){
      listener = (VariableChange)FChangeListeners.get(i);
      listener.VariableChanged( event );
      i++;
    }
  }

  public void RemoveChangeListener( Object aListenerOwner ){
    if ( FChangeListeners == null ){
      return;
    }
    int i = 0;
    VariableChange listener;
    while ( i < FChangeListeners.size() ){
      listener = (VariableChange)FChangeListeners.get(i);
      if ( listener.IsListenerEquals( aListenerOwner ) ){
        FChangeListeners.removeElementAt( i );
      } else
      i++;
    }
  }

  /**Метод выполняет присваивание переменной значения, переданного в парметре. При этом значение
   * приводится к типу хранящейся в классе переменной
   *
   * @param aNewValue - значение, которое будет приведено к определенному типу
   */
  public void SetValueWithTypeCheck( String aNewValue ) throws ScriptException{
    if ( aNewValue == null ){
      return;
    }
    if (  GetType() == OPERAND_TYPE_INTEGER ){
      try {
        int i = Integer.parseInt( aNewValue );
        SetValue(i);
        return;
      } catch (Exception e){
        //попытка преобразовать строку в Double
        try{
          double d = Double.parseDouble( aNewValue );
          SetValue( (int)d );
          return;
        } catch (Exception e2){
          ScriptException e1 = new ScriptException("Не преобразовать в integer значение " + aNewValue);
          throw e1;
        }
      }

    }
    if ( GetType() == OPERAND_TYPE_REAL ){
      try {
        double d = Double.parseDouble( aNewValue );
        SetValue( d );
        return;
      } catch (Exception e){
        ScriptException e1 = new ScriptException("Не преобразовать в real значение " + aNewValue);
        throw e1;
      }
    }
    if ( GetType()  == OPERAND_TYPE_BOOLEAN ){
      if ( "true".equalsIgnoreCase( aNewValue ) ){
        SetValue(true);
        return;
      }
      if ( "false".equalsIgnoreCase( aNewValue ) ){
        SetValue(false);
        return;
      }
      ScriptException e = new ScriptException("Не преобразовать в boolean значение " + aNewValue);
      throw e;
    }
    if ( GetType() == OPERAND_TYPE_STRING ){
      SetValue( aNewValue );
    }
  }

   public void SetValue(int aOperandValue) {
     int oldValue = 0;
     try {
       oldValue = this.GetIntValue();
     } catch (ScriptException e) {
       FireChangeEvent();
     }
     InitIntOperand(aOperandValue);
     if ( oldValue !=  aOperandValue){
       FireChangeEvent();
     }
  }

  public void SetValue(boolean aOperandValue) {
    boolean oldValue = false;
    oldValue = this.GetBooleanValue();
    InitBooleanOperand( aOperandValue );
    if ( oldValue != aOperandValue ){
      FireChangeEvent();
    }
  }

  public void SetValue(float aOperandValue)  {
    this.SetValue( (double)aOperandValue );
  }

  public void SetValue( double aOperandValue ){
    double oldValue = 0;
    try {
      oldValue = this.GetFloatValue();
    } catch (ScriptException e) {
      FireChangeEvent();
    }
    InitFloatOperand( (float)aOperandValue );
    if ( Double.compare( oldValue, aOperandValue) != 0 ){
      FireChangeEvent();
    }
  }

  protected void SetChangeListeners( Variable aVariable ){
    FChangeListeners = aVariable.FChangeListeners;
  }


}

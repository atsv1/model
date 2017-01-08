package mp.parser;


/**
 * User: atsv
 * Date: 19.04.2006
 * Time: 20:41:31
 */
public class ScriptOperationAdd extends ScriptOperation3Operand {

  private int AddInt() throws ScriptException {
    int result = 0;
    result = FFirstOperand.GetIntValue() + FSecondOperand.GetIntValue();
    return result;
  }

  private float AddFloat() throws ScriptException {
    float result = 0;
    float r1 = FFirstOperand.GetFloatValue();
    float r2 = FSecondOperand.GetFloatValue();
    result = r1 + r2;
    return result;
  }

  private String AddString() throws ScriptException {
    return FFirstOperand.GetStringValue() + FSecondOperand.GetStringValue();
  }

  private boolean AddBoolean() {
    boolean result = false;
    return result;
  }

  public int ExecOperation(int aProgramPointer) throws ScriptException {
    FFirstOperand = InitOperand( aProgramPointer + 1 );
    FSecondOperand = InitOperand( aProgramPointer + 2 );
    FResult = (Variable)InitOperand( aProgramPointer + 3 );
    if ( FIsPreviousOperationEnabled ) {
     IsOperationEnabled();
    }

    int valueType = FResult.GetType();
    switch ( valueType ) {
      case Operand.OPERAND_TYPE_INTEGER:{
        FResult.SetValue(  AddInt() );
        return 3;
      }
      case Operand.OPERAND_TYPE_REAL:{
        FResult.SetValue(  AddFloat() );
        return 3;
      }
      case Operand.OPERAND_TYPE_STRING:{
        FResult.SetValue( AddString() );
        return 3;
      }
      case Operand.OPERAND_TYPE_BOOLEAN:{
         FResult.SetValue(  AddBoolean() );
        return 3;
      }
    }//switch

    return 0;
  }
}

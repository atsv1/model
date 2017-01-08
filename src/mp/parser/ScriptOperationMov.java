package mp.parser;


/**
 * User: atsv
 * Date: 17.05.2006
 */
public class ScriptOperationMov extends ScriptOperation2Operand {



  public int ExecOperation(int aProgramPointer) throws ScriptException
  {
    FOperand = InitOperand( aProgramPointer + 2 );
    FResult = (Variable) InitOperand( aProgramPointer + 1 );
    if ( !FIsPreviousOperationEnabled )
    {
      IsOperationEnabled();
    }
    String s = FResult.GetTypeName();
    int type = FResult.GetType();
    switch (type){
      case Operand.OPERAND_TYPE_INTEGER:{
        int res = FOperand.GetIntValue();
        if ( res != res ){
          ScriptException e = new ScriptException( "Попытка присвоить операнду \"" + FResult.GetName() + "\" несуществующее значение" );
          throw e;
        }
        FResult.SetValue( res );
        break;
      }
      case Operand.OPERAND_TYPE_BOOLEAN:{
        FResult.SetValue( FOperand.GetBooleanValue() );
        break;
      }
      case Operand.OPERAND_TYPE_REAL:{
        double res = FOperand.GetFloatValue();
        if ( res != res ){
          ScriptException e = new ScriptException( "Попытка присвоить операнду \"" + FResult.GetName() + "\" несуществующее значение" );
          throw e;
        }
        FResult.SetValue( res );
        break;
      }
      case Operand.OPERAND_TYPE_STRING:{
        FResult.SetValue( FOperand.GetStringValue() );
        break;
      }
      case Operand.OPERAND_TYPE_ARRAY:{
        ScriptArray result = (ScriptArray) FResult;
        if ( FOperand.GetType() != Operand.OPERAND_TYPE_ARRAY ){
          ScriptException e = new ScriptException("Приравнивать одной матрице можно только другую матрицу. " +
                  "Ошибка в операции с матрицей");
          throw e;
        }
        ScriptArray sourceArray = (ScriptArray) FOperand;
        sourceArray.CopyValuesToArray( result );
        break;
      }
      default:{
        ScriptException e = new ScriptException("Неизвестный тип результата для операции приравнивания");
        throw e;
      }
    }
    return 2;
  }

}

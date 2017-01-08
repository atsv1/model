package mp.parser;


/**
 * Created by IntelliJ IDEA.
 * User: atsv
 * Date: 07.05.2006
 * Time: 20:57:40
 * To change this template use File | Settings | File Templates.
 */
public abstract  class ScriptOperation3Operand extends ScriptOperation {
  protected Operand FFirstOperand = null;
  protected Operand FSecondOperand = null;
  protected Variable FResult = null;
  protected boolean FIsPreviousOperationEnabled = false;

  protected boolean IsOperandNumeric(Operand aOperand)
  {
    boolean f = false;
    if ( ( aOperand.GetTypeName().equalsIgnoreCase("integer") ) ||
         ( aOperand.GetTypeName().equalsIgnoreCase("real") )
        )
    {
      f = true;
    }
    return f;
  }

  public void InitFirstOperand( Operand aOperand )
  {
    FFirstOperand = aOperand;
  }

  public void InitSecondOperand( Operand aOperand )
  {
    FSecondOperand = aOperand;
  }

  /** Функция возвращает ссылку на операнд (либо на переменную-результат),
   * тип которой не совпадает с названием типа, переданным в параметре
   * @param aTypeName
   * @return
   */
  private Operand GetOperandNotThisType(String aTypeName)
  {
    if (  !FFirstOperand.GetTypeName().equalsIgnoreCase(aTypeName) )
    {
      return FFirstOperand;
    }
    if (  !FSecondOperand.GetTypeName().equalsIgnoreCase(aTypeName) )
    {
      return FSecondOperand;
    }
   if (  !FResult.GetTypeName().equalsIgnoreCase(aTypeName) )
    {
      return FResult;
    }
   return null;
  }

  /** Процедура сгенерирует исключение, если операнды несовместимы
   * Если операнды совместимы, то значение FIsPreviousOperationEnabled будет равно true;
   * Возможно, данная процедура нуждается в оптимизации, слишком много здесь сравнений
   * строк
   * @throws ScriptException
   */
  protected void IsOperationEnabled() throws ScriptException
  {
    ScriptException e;
    Operand tempOperand;
    //проверяем, чтобы все три операнда были цифровыми
    if ( ( IsOperandNumeric(FFirstOperand) ) &&
         ( IsOperandNumeric(FSecondOperand) ) &&
         ( IsOperandNumeric(FResult) )
       )
    {
      if ( FResult.GetTypeName().equalsIgnoreCase("integer") )
      { //оба операнда должны быть тоже integer
        tempOperand = GetOperandNotThisType("integer");
        if ( tempOperand != null )
        {
          e = new ScriptException( tempOperand.GetTypeName() +  " несовместимo с integer");
          throw e;
        }
      }
    } else
    { //есть нецифровые операнды
      //проверяем, чтобы все три операнды были булевскими
      tempOperand = GetOperandNotThisType("boolean");
      if ( tempOperand != null )
      {
        e = new ScriptException( tempOperand.GetTypeName() +  " несовместимo с boolean");
        throw e;
      }
      //проверяем на строки
      tempOperand = GetOperandNotThisType("string");
      if ( tempOperand != null )
      {
        e = new ScriptException( tempOperand.GetTypeName() +  " несовместимo со string");
        throw e;
      }
    }//else
   //если до сих пор не сгенерирована ошибка, значит типы операндов совместимы
   FIsPreviousOperationEnabled = true;

  }

  public String GetResultType() throws ScriptException
  {
    ScriptException e;
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("integer") )
    {
      if ( FSecondOperand.GetTypeName().equalsIgnoreCase("integer") )
      {
        return "integer";
      }
      if ( FSecondOperand.GetTypeName().equalsIgnoreCase("real") )
      {
        return "real";
      } else
      {
        e = new ScriptException("Несовместимые типы integer и " + FSecondOperand.GetTypeName());
        throw e;
      }
    }
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("real") )
    {
      if ( FSecondOperand.GetTypeName().equalsIgnoreCase("integer") )
      {
        return "real";
      }
      if ( FSecondOperand.GetTypeName().equalsIgnoreCase("real") )
      {
        return "real";
      } else
      {
        e = new ScriptException("Несовместимые типы real и " + FSecondOperand.GetTypeName());
        throw e;
      }
    }
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("boolean") )
    {
      if ( FSecondOperand.GetTypeName().equalsIgnoreCase("boolean") )
      {
        return "boolean";
      } else
      {
        e = new ScriptException("Несовместимые типы boolean и " + FSecondOperand.GetTypeName());
        throw e;
      }
    }
    if ( FFirstOperand.GetTypeName().equalsIgnoreCase("string") )
    {
      if ( FSecondOperand.GetTypeName().equalsIgnoreCase("string") )
      {
        return "string";
      } else
      {
        e = new ScriptException("Несовместимые типы string и " + FSecondOperand.GetTypeName());
        throw e;
      }
    };
    return null;
  }

  public Variable GetResultVariable(int aProgramPointer) throws ScriptException
  {
    FFirstOperand = InitOperand( aProgramPointer + 1 );
    FSecondOperand = InitOperand( aProgramPointer + 2 );
    String s = GetResultType();
    Variable var1 = null;
    if ( s.equalsIgnoreCase("integer") )
    {
      int i = 0;
      var1 =  new Variable(i);
    }
    if ( s.equalsIgnoreCase("boolean") )
    {
      boolean f = false;
      var1 =  new Variable(f);
    }
    if ( s.equalsIgnoreCase("real") )
    {
      float r = (float) 7.1;
      var1 =  new Variable(r);
    }
    if ( s.equalsIgnoreCase("string") ) {
      var1 = new Variable("");
    }
    return var1;
  }
 
}

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

  /** ������� ���������� ������ �� ������� (���� �� ����������-���������),
   * ��� ������� �� ��������� � ��������� ����, ���������� � ���������
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

  /** ��������� ����������� ����������, ���� �������� ������������
   * ���� �������� ����������, �� �������� FIsPreviousOperationEnabled ����� ����� true;
   * ��������, ������ ��������� ��������� � �����������, ������� ����� ����� ���������
   * �����
   * @throws ScriptException
   */
  protected void IsOperationEnabled() throws ScriptException
  {
    ScriptException e;
    Operand tempOperand;
    //���������, ����� ��� ��� �������� ���� ���������
    if ( ( IsOperandNumeric(FFirstOperand) ) &&
         ( IsOperandNumeric(FSecondOperand) ) &&
         ( IsOperandNumeric(FResult) )
       )
    {
      if ( FResult.GetTypeName().equalsIgnoreCase("integer") )
      { //��� �������� ������ ���� ���� integer
        tempOperand = GetOperandNotThisType("integer");
        if ( tempOperand != null )
        {
          e = new ScriptException( tempOperand.GetTypeName() +  " �����������o � integer");
          throw e;
        }
      }
    } else
    { //���� ���������� ��������
      //���������, ����� ��� ��� �������� ���� ����������
      tempOperand = GetOperandNotThisType("boolean");
      if ( tempOperand != null )
      {
        e = new ScriptException( tempOperand.GetTypeName() +  " �����������o � boolean");
        throw e;
      }
      //��������� �� ������
      tempOperand = GetOperandNotThisType("string");
      if ( tempOperand != null )
      {
        e = new ScriptException( tempOperand.GetTypeName() +  " �����������o �� string");
        throw e;
      }
    }//else
   //���� �� ��� ��� �� ������������� ������, ������ ���� ��������� ����������
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
        e = new ScriptException("������������� ���� integer � " + FSecondOperand.GetTypeName());
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
        e = new ScriptException("������������� ���� real � " + FSecondOperand.GetTypeName());
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
        e = new ScriptException("������������� ���� boolean � " + FSecondOperand.GetTypeName());
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
        e = new ScriptException("������������� ���� string � " + FSecondOperand.GetTypeName());
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

package mp.parser;

/**
  ����� �������� �������������� ������, ��������� � �����������
  �������������� ��������.
  ������ �������� ����������� � ������� ScriptLanguageDef.SimpleOperation3OperandList
*/
public class ScriptActionLexem3Operand extends ScriptLexem {

  public ScriptActionLexem3Operand()
  {
   
    super();
    try {
      this.SetLanguageName("��������3");
    } catch (ScriptException e) {
      
    }
  }

  /**
   * �������� ������������ ������.
   *������������ ����� �������� ������� ScriptLanguageDef.SimpleOperation3OperandList
   * ���� �� ������ �� ������������ � ������� �������� ���������� � ���������
   * ������ ������� IsMyToken == truw
   */
  public boolean IsLexemEquals(ScriptLexem aLexem)
  {
    if ( aLexem != null )
    {
      return GetLanguageName().equalsIgnoreCase( aLexem.GetLanguageName() );
    }  return false;
  }

  public boolean IsMyToken(String aTokenName)
          //IsMyToken
  {
    boolean f = false;
    int i = 0;
    String token;
    while ( i < ScriptLanguageDef.SimpleOperation3OperandList.length )
    {
      token = ScriptLanguageDef.SimpleOperation3OperandList[i];
      if ( aTokenName.toUpperCase().equalsIgnoreCase( token ) )
      {
        f = true;
        break;
      }

      i++;
    }//while
    return f;
  }


  public Object clone()
  {
    ScriptActionLexem3Operand  lexem = new ScriptActionLexem3Operand();
    lexem.FCodePart = this.FCodePart;
    return lexem;
  }

  public boolean IsNewOperandNeed()
  {
    if ( FCodePart.equalsIgnoreCase(":=") )
    {
     return false;
    }
    return true;
  }

  public Object GetExecutableObject() {
    ScriptOperation operation = null;
    if ( FCodePart.equalsIgnoreCase("+") )
    {
      operation = new ScriptOperationAdd();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("*") )
    {
      operation = new ScriptOperationMul();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("/") )
    {
      operation = new ScriptOperationDiv();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("-") )
    {
      operation = new ScriptOperationSub();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase(":=") )
    {
      operation = new ScriptOperationMov();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("^") )
    {
      operation = new ScriptOperationPow();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("and") )
    {
      operation = new ScriptOperationAnd();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("or") )
    {
      operation = new ScriptOperationOr();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("xor") )
    {
      operation = new ScriptOperationXOR();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("=") )
    {
      operation = new ScriptOperationEquals();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase(">") )
    {
      operation = new ScriptOperationMore();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("<") )
    {
      operation = new ScriptOperationLower();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("<=") ){
      operation = new ScriptOperationLowerOrEquals();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase(">=") ){
      operation = new ScriptOperationGreaterOrEquals();
      return operation;
    }
    if ( FCodePart.equalsIgnoreCase("<>") ){
      operation = new ScriptOperationNotEquals();
      return operation;
    }
    return operation;
    //@todo ���������� �������� ���� ��������, ������������� � ������� SimpleOperation3OperandList
  }

  public String toString(){
    return "��������3 " + FCodePart;
  }

}

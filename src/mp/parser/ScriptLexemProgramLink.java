package mp.parser;

/**
 * User: atsv
 * Date: 25.08.2006
 */
public class ScriptLexemProgramLink extends ScriptLexem
 {

  private int FLinkType = -1; /*��� ������. 1 - ��� ����� �������� (����� �������������� � �������� �������� �
                              �������� ��������). 2 - ��� �����;
                              3 - ��� ������ (������������ ��� �������� ������)
                              */
  private String FLinkId;

  public ScriptLexemProgramLink(String aLinkId, int aLinkType)
  {
    FLinkType = aLinkType;
    FLinkId = aLinkId;
    switch (FLinkType){
      case 1:{
        FLanguageName = "�����";
        break;
      }
        case 2:{
          FLanguageName = "�����";
          break;
        }
        case 3:{
          FLanguageName = "������";
          break;
        }
    }
  }

  public String GetLinkId()
  {
    return FLinkId;
  }

  public boolean IsLabel()
  {
    return (FLinkType == 2);
  }

  public boolean IsAddress()
  {
    return (FLinkType == 1);
  }

  public boolean IsLexemEquals(ScriptLexem aLexem)
  {
    try
    {
      ScriptLexemProgramLink lexem = (ScriptLexemProgramLink)aLexem;
      if ( (FLinkType == 1) && lexem.IsAddress() )
      {
        return true;
      } else
      {
        if ( (FLinkType == 2) && lexem.IsLabel() )
        {
          return true;
        } else
          return false;
      }
    } catch (Exception e)
    {
      return false;
    }
  }

  public boolean IsMyToken(String aTokenName)
  {
    return false;
  }

  public Object GetExecutableObject() throws ScriptException
  {
    int linkId = 0;
    linkId = Integer.parseInt( FLinkId );
    Variable operand = new Variable(linkId);
    switch (FLinkType){
      case 1:{
        operand.SetName("�����");
        break;
      }
        case 2:{
          operand.SetName("�����");
          break;
        }
        case 3:{
          ScriptException e = new ScriptException("������� ������� ����������� ������ ��� ���������� ������� - �����");
          throw e;
        }
    }
    //operand.Name =
    return operand;
  }

   public boolean IsServiceLexem()
  {
    return true;
  }

  public String toString(){
    String result = null;
    switch (FLinkType){
      case 1:{
        result = "�����";
        break;
      }
        case 2:{
          result = "�����";
          break;
        }
        case 3:{
          result = "������";
          break;
        }
    }
    result = result +  FLinkId ;
    return result;

  }

}

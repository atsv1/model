package mp.parser;


import java.util.Vector;

/**
 * User: atsv
 * Date: 06.04.2006
 * Time: 21:38:35
 */

/** ��� ���������� ����� ������� ����� ������� ���������:
 *  1. ������� �����-��������� �� ScriptLexem. �������������� ��� ���������
 *   ������:
 *    - Clone
 *    - IsNewOperandNeed
 *    - GetExecutableObject
 *    - IsMyToken
 *    - IsLexemEquals
 *    - ����������� ������
 *  2. �������� �������� ������ ������ � ������ PascalParser.InitLexemVector
 *  3. �������� ������ ScriptLanguageDef.LanguageDef
 *  4. �������� ������ ScriptLanguageDef.productionList
 */

public abstract class ScriptLexem {

  /**
   ���������� �������� �������� �� ������ ScriptLanguageDef,
   ������ LexemList - "�����", "�������", "��������" � �.�.
   */
  protected String FLanguageName = "";
  protected String FUpperLanguageName = "";
  protected Vector<ScriptLexem> FProducedLexem;
  protected String FCodePart = null;
  public VariableList Variables;

  public ScriptLexem() {
    FProducedLexem = new Vector<ScriptLexem> ();
  }

  public String GetLanguageName()  {
    return FLanguageName;
  }

  public void SetLanguageName( String aName ) throws ScriptException  {
    if ( FLanguageName != "" ) {
      ScriptException e;
      e = new ScriptException("���������� �������� ��� ����������� ��� � ������� " + aName);
      throw e;
    } else {
      FLanguageName = aName;
      FUpperLanguageName = aName.toUpperCase();
    }
  }

  public abstract boolean IsLexemEquals( ScriptLexem aLexem );

  public abstract  boolean IsMyToken(String aTokenName);


  public  boolean IsProducedLexemExist(String aToken) {
    boolean f = false;
    int i = 0;
    ScriptLexem lexem;
    while ( i < FProducedLexem.size() ) {
      lexem = FProducedLexem.get( i );
      if ( lexem.IsMyToken( aToken ) ) {
        f = true;
        break;
      }
      i++;
    }//while
    return f;
  }

  public  boolean IsProducedLexemExist(ScriptLexem aLexem)  {
    boolean f = false;
    int i = 0;
    ScriptLexem lexem;
    if ( aLexem == null ) {
      return f;
    }
    while ( i < FProducedLexem.size() ) {
      lexem = FProducedLexem.get( i );
      if ( lexem.IsLexemEquals( aLexem ) ) {
        f = true;
        break;
      }
      i++;
    }//while
    return f;
  }

  public  void AddProducedLexem( ScriptLexem aLexem ) throws ScriptException {

    if ( IsProducedLexemExist( aLexem ) ) {
      throw new ScriptException("����� ������� ��� ������������ � ����������");
    }
    if ( aLexem != null ) {
      FProducedLexem.add( aLexem );
    }
  }

  /**
  �����������, ����� �� �������������� �����������, ������������
  � ���������,
  ��������� � ���� ������� �� ������������, �� ������� ��������
  ������ ������
  */
  public  boolean IsTokenCanProduced(String aTokenName) {
    boolean f = false;
    int i = 0;
    ScriptLexem lexem;
    while ( i <  FProducedLexem.size()) {
      lexem =  FProducedLexem.get( i );
      if ( lexem.IsMyToken((aTokenName)) ) {
        f = true;
        break;
      }
      i++;
    }//while
    return f;
  }

  public void SaveCode(String aCode) {
    FCodePart = aCode;
  }

  public String GetCode() {
    return FCodePart;
  }

  public Object clone() {
    return null;
  }

  public abstract Object GetExecutableObject() throws ScriptException;

  public Object GetExecutableObject_BeforeMov() throws ScriptException{
  	return GetExecutableObject();
  }


  public boolean IsNewOperandNeed() {
    return false;
  }

  /**������� ������ ���������� ��, ���� ������ ������� �������� ���������,
   *� �� ����� ���������, ����� �� ��������� �� � ������������ ����� ������ �����
   * @return
   */
  public boolean IsServiceLexem() {
    return false;
  }

}

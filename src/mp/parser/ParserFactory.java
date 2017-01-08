package mp.parser;

import java.util.Hashtable;

/** ����� ������������ ��� �������� ������� � �������� ��� � ������, ������� ���� ������ ����� ������������.
 * ����� ��� ����������, ����� ������ ������ ��� ����� ��������� - �����������, ��� �������������. 
 *  
 * User: atsv
 * Date: 04.09.2007
 */
public class ParserFactory {
  /**������ �� ��� ��������� ��������. ������ ��� ������ ������� �������� �������� ���.
   * � ���� ������ �������� ������� ������ ParserRecord  
   */
  private static Hashtable FParserList = new Hashtable();


  /**�������� ������ �������
   *
   * @param aLanguageExt  - ���������� �����, � ������� ����� �������� ������. 
   * @param aSourceCode - �������� ���, ������� ����� ����������� ��������.
   * @return
   */
  public static ScriptParser GetParser( ScriptLanguageExt aLanguageExt, String aSourceCode ) throws ScriptException{
    /** �������� �������� ������ ������� �����:
     * 1. �������� ������� �������� ������ (� ������ - ������ ������ ParserRecord) �� ������ ��� ��������� ��������
     * (�� ������������ ���� � ���� �����).
     * 2. ���� ������� � ����� ������ ���, �� ��������� ����������� ������
     * 3. ���� ������ � ����� ������ ����, �� ����������� �������� ���� ParserRecord.ParserCount
     * 4. ���� ��� ����� 0, �� ��� ��������, ��� ��� ������ ����������� ������, � �� ������� �������. � ����� ������,
     *   ��� ������ ���������, ��������� ����� ����������� ������, ��������� ����� ���������� �����, ����������� ������
     *   �������������, ����������� � ������ ( �� ��������� � ParserRecord.ParserCount, ������ 1 ), ���������
     *   ������������� ������, � ������� ���������� ������ ��� ��������� ����������� ������. � ��������� �������������
     *   ������ ������������ �������
     * 5. ���� ������ ����, �� ���������   
     */
    Object o = FParserList.get( aSourceCode );
    if ( o == null ){
      PascalParser parser = new PascalParser();
      parser.SetLanguageExt( aLanguageExt );
      parser.ParseScript( aSourceCode );
      ParserRecord rec = new ParserRecord();
      rec.Parser = parser;
      rec.ParserCount = 0;
      FParserList.put( aSourceCode, rec );
      return parser;
    }
    // ���� ����� ������ � ������
    ParserRecord record = (ParserRecord) o;
    if ( record.ParserCount == 0 ){
      ScriptLanguageExt newExt;
      try {
        newExt = aLanguageExt.clone();
      } catch (CloneNotSupportedException e) {
        ScriptException e1 = new ScriptException( e.getMessage() );
        throw e1;
      }
      FParserList.remove( aSourceCode );
      PascalParser parser = new PascalParser();
      parser.SetLanguageExt( newExt );
      parser.ParseScript( aSourceCode );
      record = new ParserRecord();
      record.Parser = parser;
      record.ParserCount = 1;
      FParserList.put( aSourceCode, record );
      ScriptParser newParser = new NotExecutiveParser( parser, aLanguageExt );
      return newParser;
    } else{
      ScriptParser newParser = new NotExecutiveParser( record.Parser, aLanguageExt );
      record.ParserCount++;
      return newParser;
    }
  }

  public static ScriptParser _GetParser( ScriptLanguageExt aLanguageExt, String aSourceCode ) throws ScriptException{
    PascalParser result = new PascalParser();
    result.SetLanguageExt( aLanguageExt );
    result.ParseScript( aSourceCode );
    return result;
  }


  public static void ClearParserList(){
    FParserList.clear();  
  }

  private static class ParserRecord{
    int ParserCount = 0;
    ScriptParser Parser = null;
  }

}

package mp.parser;

import java.util.Hashtable;

/** Класс предназначен для создания парсера и передачи его в классы, которые этот парсер будут использовать.
 * Класс сам определяет, какой именно парсер ему нужно создавать - исполняемый, или неисполняемый. 
 *  
 * User: atsv
 * Date: 04.09.2007
 */
public class ParserFactory {
  /**Список из уже созданных парсеров. Ключом для поиска парсера является исходный код.
   * В этом списке хранятся объекты класса ParserRecord  
   */
  private static Hashtable FParserList = new Hashtable();


  /**Создание нового парсера
   *
   * @param aLanguageExt  - расширение языка, с которым будет работать парсер. 
   * @param aSourceCode - исходный код, который будет выполняться парсером.
   * @return
   */
  public static ScriptParser GetParser( ScriptLanguageExt aLanguageExt, String aSourceCode ) throws ScriptException{
    /** Алгоритм создания нового парсера такой:
     * 1. Делается попытка получить парсер (а точнее - объект класса ParserRecord) из списка уже имеющихся парсеров
     * (по исполняемому коду в виде ключа).
     * 2. Если объекта с таким ключем нет, то создается исполняемый парсер
     * 3. Если объект с таким ключем есть, то проверяется значение поля ParserRecord.ParserCount
     * 4. Если оно равно 0, то это означает, что был создан исполняемый парсер, и он передан клиенту. В таком случае,
     *   эта запись удаляется, создается новый исполняемый парсер, создается копия расширения языка, исполняемый парсер
     *   компилируется, добавляется в список ( со значением в ParserRecord.ParserCount, равным 1 ), создается
     *   неисполняемый парсер, в который передается только что созданный исполняемый парсер. И созданный неисполняемый
     *   парсер возвращается клиенту
     * 5. Если больше нуля, то создается   
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
    // есть такой парсер в списке
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

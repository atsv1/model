package mp.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * User: atsv
 * Date: 02.11.2006
 * Задача данного класса - разбивать переданный ему текст на лексемы и поочередно возвращать эти лексемы вызывающему
 * классу.
 */
public class ScriptLexemTokenizer {
  /**Константы, которые определяют тип строки, ее соответствие разделителям, принятым в языке.
     * Внимание! При изменении значений констант необходимо также переделать массив AutomatMatrix, поскольку
     * в нем значения констант явно использованы
     */
  public static final int UNKNOWN = 0;//невозможно определить, к какому классу относится переданная последовательность
  //переданная последовательность - это отдельная лексема, у которой не может быть модификаций, т.е. стоящие после
  //нее символы не могут являться продолжением данной лексемы
  public static final int LEXEM_SEPARATOR = 1;
  //переданная последовательность является началом новой лексемы
  public static final int LEXEM_BEGINING = 2;
  //переданный символ - это разделитель, который при этом не является началом новой лексемы. Таким симполом
  // может быть либо перевод строки, либо пробел
  public static final int EMPTY_SEPARATOR = 3;
  //переданный символ - метка начала блока. Причем начальный и конечный маркер не являются частью лексемы
  public static final int BLOK_BEGIN_NO_MARKER = 4;
  //переданный символ - метка начала блока. Начальный и конечный маркер являются частью лексемы
  public static final int BLOCK_BEGIN_MARKER = 5;
  // переданный символ - начало комментария
  public static final int COMMENT_BEGIN = 6;

  private ScriptLexem previousLexem; // предыдущая лексема
  /**
   *  количество незакрытых пар [ - ], не являющихся ограничителями неразбираемых последовательностей
   */
  private int bracketStack = 0;


    public  final String[] LexemSeparatorList =
   {
     "*",
     "+",
     "-",
     "/",
     "(",
     ")",
     ":=",
     "^",
     ";",
     "<",
     "<",
     ">=",
     "<=",
     "=",
     "<>",
     "\"",
     ","

   };

  private String FScriptCode = null;
  private int FCurrentPointer = 0;
  private TokenAccumulator FCurrentAccumuator = null;
  private ScriptLexemTokenizer tokenizer = null;

  public ScriptLexemTokenizer(String aScriptCode){
    FScriptCode = aScriptCode;
    FCurrentAccumuator = GetBeginAccumulator();
  }

  private abstract class TokenAccumulator{

    private String FAccumulatedToken = null;

    /**
     *  Добавление символа к формируемой последовательности и проверка того, что последовательность закончена, т.е
     * добавляемый символ уже НЕ принадлежит последовательности
     *
     * @param aSimbol добавляемый символ
     *
     * @return true
     */
    public abstract boolean AddSimbol(char aSimbol);
    public abstract TokenAccumulator GetNextAccumulator();

    public TokenAccumulator(){
      FAccumulatedToken = null;
    }

    public String GetToken(){
      return FAccumulatedToken;
    }

    public void SetToken(String aNewValue){
      FAccumulatedToken = aNewValue;
    }

    /**
     *  Непосредственное добавление символа к последовательности
     *
     * @param simbol добавляемый символ
     */
    public void Add( char simbol ){
      if ( FAccumulatedToken == null ){
        FAccumulatedToken = String.valueOf( simbol );
      } else{
        FAccumulatedToken = FAccumulatedToken + String.valueOf( simbol );
      }
    }

    private int GetIndex( String s ){
      int i = 0;
      while ( i < LexemSeparatorList.length ){
        if ( LexemSeparatorList[i].equalsIgnoreCase( s ) ){
          return i;
        }
        i++;
      }
      return -1;
    }

    private int GetStartIndex(String s){
      int i = 0;
      while ( i < LexemSeparatorList.length ){
        if ( LexemSeparatorList[i].startsWith( s ) && !LexemSeparatorList[i].equalsIgnoreCase( s )){
          return i;
        }
        i++;
      }
      return -1;
    }



    /**Функция возвращает идентификатор типа переданной последовательности
     * @param s  - последовательность, тип которой нужно определить
     * @return - определенный тип
     */
    public int GetType( String  s){
      if ( s == null ){
        return UNKNOWN;
      }
      char c = s.charAt(0);
      if ( c == ' ' || c == '\n'){
        return EMPTY_SEPARATOR;
      }
      if ( "[".equalsIgnoreCase(s) ){
        // Символ [ может быть как началом неразбираемой последовательности, так и началом обращения к массиву
        if (previousLexem == null) {
          // предыдущей лексемы нет, значит точно неразбираемая последовательность
          return BLOK_BEGIN_NO_MARKER;
        }  else {
          if (previousLexem instanceof ScriptOperandSimple) {
            bracketStack++;
            return LEXEM_SEPARATOR;
          } else
          return BLOK_BEGIN_NO_MARKER;
        }
      }
      if ( "]".equalsIgnoreCase(s) ) {
        if ( bracketStack > 0 ) {
          bracketStack--;
        } else {
          //ошибка
        }
        return LEXEM_SEPARATOR;
      }
      if ( "\"".equalsIgnoreCase( s ) ){
        return BLOCK_BEGIN_MARKER;
      }
      if ( "{".equalsIgnoreCase(s) ){
        return COMMENT_BEGIN;

      }
      int j = GetStartIndex(s);
      if ( j != -1 ){
        return LEXEM_BEGINING;
      }
      int i = GetIndex(s);
      if ( i == -1 ){
        return UNKNOWN;
      } else{
        return LEXEM_SEPARATOR;
      }
    }

    protected String GetNextStateName( String aCurrentStateName, int aSignal ){
      String result = null;
      switch ( aSignal ){
        case UNKNOWN:{
          result = "mp.parser.ScriptLexemTokenizer$UnknownAccumulator";
          break;
        }
          case LEXEM_SEPARATOR:{
            result = "mp.parser.ScriptLexemTokenizer$SingleLexemSeparator";
            break;
          }
          case LEXEM_BEGINING:{
            result = "mp.parser.ScriptLexemTokenizer$LexemSeparatorAccumulator";
            break;
          }
          case EMPTY_SEPARATOR:{
            result = "mp.parser.ScriptLexemTokenizer$BeginAccumulator";
            break;
          }
          case BLOK_BEGIN_NO_MARKER:{
            result = "mp.parser.ScriptLexemTokenizer$BlockWithoutMarker";
            break;
          }
          case BLOCK_BEGIN_MARKER: {
            result = "mp.parser.ScriptLexemTokenizer$BlockWithMarker";
            break;
          }
          case COMMENT_BEGIN:{
            result = "mp.parser.ScriptLexemTokenizer$Comment";
            break;
          }
      }
      return result;
    }

    protected TokenAccumulator GetNewAccumulator(String aClassName){
      if ( aClassName == null || "".equalsIgnoreCase( aClassName ) ){
        return null;
      }
      TokenAccumulator result = null;
      try {
        Class cl = Class.forName( aClassName );
        Constructor[] constr = cl.getConstructors();
        if ( constr.length == 0 ){
          //System.out.println("0");
          return null;
        }
        Object[] params = new Object[1];
        params[0] = tokenizer;
        Constructor con = constr[0];
        Object o = con.newInstance( params );
        result = (TokenAccumulator) o;
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      return result;
    }

    protected void ClearAccumulator(){
      FAccumulatedToken = null;
    }

  }//class TokenAccumulator

  private class BeginAccumulator extends TokenAccumulator{
    private TokenAccumulator FNextState = null;

    public BeginAccumulator(){
      super();
    }

    public boolean AddSimbol(char aSimbol) {
      boolean result = false;
      int i = GetType( String.valueOf(aSimbol) );
      switch (i) {
        case EMPTY_SEPARATOR: {
          break;
        }
        default:{
            FNextState = GetNewAccumulator( GetNextStateName( this.getClass().getName(), i ) );
            result = true;
            break;
          }
      }//switch i
      return result;
    }

    public TokenAccumulator GetNextAccumulator() {
      return FNextState;
    }

  }//class BeginAccumulator

  /**Класс реализует простое накапливание символов. Например, накливание переменной, числа или чего-то в этом роде.
   */
  private class UnknownAccumulator extends TokenAccumulator{
    private TokenAccumulator FNextAccum = null;

    public UnknownAccumulator(){
      super();
    }

    public boolean AddSimbol(char aSimbol) {
      int i = GetType( String.valueOf(aSimbol) );
      boolean result = false;
      switch (i) {
        case UNKNOWN:{ // неизвестный символ, прибавляем его к последовательности
          this.Add( aSimbol );
          result = false;
          break;
        }
          default:{
            result = true;
            FNextAccum = GetNewAccumulator( GetNextStateName( this.getClass().getName(), i ) );
            break;
          }
      }//case
      return result;
    }

    public TokenAccumulator GetNextAccumulator() {
      return FNextAccum;
    }

  }// class UnknownAccumulator

  /**Класс предназначен для хранения лекем, которые сами являются разделителями. При этом лексема-разделитель
   * состоит из нескольких символов
   */
  private class LexemSeparatorAccumulator extends TokenAccumulator{
    private TokenAccumulator FNextState = null;

    public LexemSeparatorAccumulator(){
      super();
    }

    public boolean AddSimbol(char aSimbol) {
      int i;
      String s = this.GetToken();
      if ( s == null || "".equalsIgnoreCase(s) ){
        i = GetType( String.valueOf( aSimbol ));
      } else{
        i = GetType(  s + String.valueOf( aSimbol ));
      }
      boolean result = false;
      switch (i) {
        case LEXEM_BEGINING:{
          this.Add( aSimbol );
          break;
        }
          default:{
            FNextState = GetNewAccumulator( GetNextStateName( this.getClass().getName(), i ) );
            result = true;
            break;
          }
          case LEXEM_SEPARATOR:{
            this.Add( aSimbol );
            //FNextState = GetNewAccumulator( GetNextStateName( this.getClass().getName(), i ) );
            //result = true;
            break;
          }
      }//switch
      return result;
    }

    public TokenAccumulator GetNextAccumulator() {
      return FNextState;
    }
  }

  private class SingleLexemSeparator extends TokenAccumulator{
    private TokenAccumulator FNextState = null;

    public SingleLexemSeparator(){
      super();
    }

    public boolean AddSimbol(char aSimbol) {
      String s = this.GetToken();
      if ( s == null || "".equalsIgnoreCase(s) ){
        this.Add( aSimbol );
        return false;
      }
      /*if ("-".equalsIgnoreCase( s )){
        String s2 = s + String.valueOf( aSimbol );
        try {
          Double.parseDouble( s2 );
          this.SetToken( null );
          FNextState = GetNewAccumulator( GetNextStateName( this.getClass().getName(), GetType( String.valueOf( aSimbol ) ) ) );
          FNextState.SetToken( s );
          return true;
        } catch (Exception e){}
      }*/
      FNextState = GetNewAccumulator( GetNextStateName( this.getClass().getName(), GetType( String.valueOf( aSimbol ) ) ) );
      return true;
    }

    public TokenAccumulator GetNextAccumulator() {
      return FNextState;
    }
  }

  private class BlockWithoutMarker extends TokenAccumulator{
    private TokenAccumulator FNextState = null;
    private boolean FFlag = false;

    public BlockWithoutMarker(){
      super();
    }

    public boolean AddSimbol(char aSimbol) {
      String s = this.GetToken();
      if ( (s == null || "".equalsIgnoreCase(s) ) && ( !FFlag ) ){ //пока что это просто пустая строка.
        // Проверяем тип предыдущей лексемы и ее наличие
        if ( previousLexem != null && (previousLexem instanceof ScriptOperandSimple)) {
          // есть предыдущая лексема, и она - переменная. Это означает, что
          //ло
        }

        FFlag = true;
        return false;
      }
      if ( ']' == aSimbol ){//появился закрывающий маркер блока
        FFlag = false;
        return false;
      }
      if ( FFlag ){
        this.Add( aSimbol );
        return false;
      }
      int i = GetType( String.valueOf(aSimbol) );
      FNextState = GetNewAccumulator( GetNextStateName( this.getClass().getName(), i ) );
      return true;
    }

    public TokenAccumulator GetNextAccumulator() {
      return FNextState;
    }
  }

  private class BlockWithMarker extends TokenAccumulator{
    private TokenAccumulator FNextState = null;
    private boolean FFlag = false;

    public BlockWithMarker(){
      super();
    }

    public boolean AddSimbol(char aSimbol) {
      String s = this.GetToken();
      if ( (s == null || "".equalsIgnoreCase(s)) && (!FFlag)){
        FFlag = true;
        this.Add( aSimbol );
        return false;
      }
      if ( '\"' == aSimbol ){
        FFlag = false;
        this.Add( aSimbol );
        return false;
      }
      if ( FFlag ){
        this.Add( aSimbol );
        return false;
      }
      int i = GetType( String.valueOf( aSimbol ) );
      FNextState = GetNewAccumulator( GetNextStateName( this.getClass().getName(), i ) );
      return true;
    }

    public TokenAccumulator GetNextAccumulator() {
      return FNextState;
    }
  }

  private class Comment extends TokenAccumulator{
    private TokenAccumulator FNextState = null;
    private boolean FFlag = false;

    public Comment(){
      super();
    }

    public boolean AddSimbol(char aSimbol) {
      if ( '{' == aSimbol){
        FFlag = true;
        return false;
      }
      if ( '}' == aSimbol ){
        FFlag = false;
        return false;
      }
      if ( FFlag ){
        return false;
      }
      int i = GetType( String.valueOf( aSimbol ) );
      FNextState = GetNewAccumulator( GetNextStateName( this.getClass().getName(), i ) );
      return true;
    }

    public TokenAccumulator GetNextAccumulator() {
      return FNextState;
    }
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////// Закончились внутренние классы /////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////

  private TokenAccumulator GetBeginAccumulator(){
    return new BeginAccumulator() ;
  }

  public String GetNextLexem(){
    tokenizer = this;
    int len = FScriptCode.length();
    boolean f = FCurrentPointer < len;
    if ( !f ){
      return null;
    }
    char c;
    String result = null;
    while ( f ){
      c = FScriptCode.charAt( FCurrentPointer );
      if ( FCurrentAccumuator.AddSimbol( c ) ){
        result = FCurrentAccumuator.GetToken();
        FCurrentAccumuator = FCurrentAccumuator.GetNextAccumulator();
        if ( !( result == null || "".equalsIgnoreCase( result ) ) ){
           return result;
        }
      } else{
        FCurrentPointer++;
      }
      f = FCurrentPointer < len;
    }
    if ( FCurrentAccumuator != null ){
      return FCurrentAccumuator.GetToken();
    }
    return null;
  }

  /**
   *
   * @param value
   * @return
   */
  private boolean isSeparаtor(char value){
  	if (value == ' ' || value == '\n') {
  		return true;
  	} else
  		return false;

  }

  public String GetNextLexem3(){
  	int len = FScriptCode.length();
		boolean f = FCurrentPointer < len;
		if (!f) {
			return null;
		}
		char c;
		String result = "";
		int separatorIndex = -1;
		int separatorStartIndex = -1;
		int currentValueSeparatorIndex = -1;
		int currentValueSeparatorStartIndex = -1;
		int currentMode = UNKNOWN;
		String s;
		boolean separatorFlag = false;
		while (FCurrentPointer < len) {
			c = FScriptCode.charAt( FCurrentPointer );
			if ( isSeparаtor(c) ) {
				// безусловный разделитель (пробел или перевод строки)
				if ( !"".equals(result)  ) {
					FCurrentPointer++;
					return result;
				} else {
					FCurrentPointer++;
					continue;
				}
			}
			// Обработка символов [ и ]
			if ( c =='[' ) {
				if (  !"".equals(result)){
					return result;
				}
				if ( previousLexem != null && previousLexem instanceof ScriptOperandSimple) {
					// есть предыдущая лексема, и она операнд. Считаем, что это - массив, и к нему идет обращение вида a[1] := 1;
					result = "[";
					FCurrentPointer++;
					return result;
				} else {
					// После символа [ идет неразбираемый текст до  символа ]
					int pos = FScriptCode.indexOf("]", FCurrentPointer);
					if (pos > FCurrentPointer) {
            result = FScriptCode.substring(FCurrentPointer+1, pos);
            FCurrentPointer = pos+1;
            return result;
					} else {
						result = FScriptCode.substring(FCurrentPointer);
						FCurrentPointer = FScriptCode.length()+1;
						return result;
					}
				}
			}
			if ( c == ']' ) {
				if (!"".equals(result)) {
					return result;
				} else {
					FCurrentPointer++;
					return "]";
				}
			}
			// закончилась Обработка символов [ и ]
			// Обработка строк символов
			if ( c == '"' ) {
				if (  !"".equals(result)){
					return result;
				}
				int pos = FScriptCode.indexOf("\"", FCurrentPointer+1);
				result = FScriptCode.substring(FCurrentPointer, pos+1);
        FCurrentPointer = pos+1;
        return result;
			}
			// Обработка комментариев
			if ( c == '{' ) {
			  if (  !"".equals(result)){
					return result;
			  }
			  int pos = FScriptCode.indexOf("}", FCurrentPointer+1);
			  FCurrentPointer = pos+1;
        continue;
			}
			currentValueSeparatorIndex = GetSeparatorIndex(result);
			currentValueSeparatorStartIndex = GetStartSeparatorIndex(result);
			separatorIndex = GetSeparatorIndex(result+c);
			separatorStartIndex = GetStartSeparatorIndex(result+c);
			s = String.valueOf(c);
			switch (currentMode){
			case UNKNOWN:{
				// накопление непонятно пока чего (числа, переменной, названия функции и пр)
        if ( GetSeparatorIndex(s) >= 0 && GetStartSeparatorIndex(s) < 0) {
        	// встретился символ -  лексема-разделител.
        	if ("".equals(result)) {
        	  result = s;
        	  FCurrentPointer++;
        	  return result;
        	} else {
        		currentMode = LEXEM_SEPARATOR;
        		return result;
        	}
        }
        if (GetStartSeparatorIndex(s) >= 0 ){
          if ( "".equals(result) ) {
          	currentMode = LEXEM_BEGINING;
          } else {
          	return result;
          }
        }
        result = result + c;
      	FCurrentPointer++;
				break;
			} // UNKNOWN
			case LEXEM_SEPARATOR:{
				FCurrentPointer++;
				currentMode = UNKNOWN;
        return s;
			}
			case LEXEM_BEGINING: {
        if ( "".equals(result) ){
        	result = s;
        	break;
        }
        if ( GetSeparatorIndex(result+c) < 0 && GetStartSeparatorIndex(result+c) >= 0){
        	FCurrentPointer++;
        	result = result + c;
        	break;
        }
        if ( GetSeparatorIndex(result+c) >= 0 && GetStartSeparatorIndex(result+c) < 0){
        	currentMode = UNKNOWN;
        	FCurrentPointer++;
        	return result + c;
        }
			} //LEXEM_BEGINING

			}//switch
		}//while
		return result;

  }

  public String GetNextLexem2(){
		int len = FScriptCode.length();
		boolean f = FCurrentPointer < len;
		if (!f) {
			return null;
		}
		char c;
		String result = "";
		int separatorIndex = -1;
		int separatorStartIndex = -1;
		int currentValueSeparatorIndex = -1;
		int currentValueSeparatorStartIndex = -1;
		String s;
		boolean separatorFlag = false;
		while (FCurrentPointer < len) {
			c = FScriptCode.charAt( FCurrentPointer );
			if ( isSeparаtor(c) ) {
				// безусловный разделитель (пробел или перевод строки)
				if ( !"".equals(result)  ) {
					FCurrentPointer++;
					return result;
				} else {
					FCurrentPointer++;
				}
			}
			// Обработка символов [ и ]
			if ( c =='[' ) {
				if (  !"".equals(result)){
					return result;
				}
				if ( previousLexem != null && previousLexem instanceof ScriptOperandSimple) {
					// есть предыдущая лексема, и она операнд. Считаем, что это - массив, и к нему идет обращение вида a[1] := 1;
					result = "[";
					FCurrentPointer++;
					return result;
				} else {
					// После символа [ идет неразбираемый текст до  символа ]
					int pos = FScriptCode.indexOf("]", FCurrentPointer);
					if (pos > FCurrentPointer) {
            result = FScriptCode.substring(FCurrentPointer, pos);
            FCurrentPointer = pos+1;
            return result;
					} else {
						result = FScriptCode.substring(FCurrentPointer);
						FCurrentPointer = FScriptCode.length()+1;
						return result;
					}
				}
			}// закончилась Обработка символов [ и ]

			// обработка лексем-разделителей
			currentValueSeparatorIndex = GetSeparatorIndex(result);
			currentValueSeparatorStartIndex = GetStartSeparatorIndex(result);
			separatorIndex = GetSeparatorIndex(result+c);
			separatorStartIndex = GetStartSeparatorIndex(result+c);
			// проверяем ситуацию: после лексемы-разделителя идет что-от другое
			if ( !"".equalsIgnoreCase(result) ){
				if ( currentValueSeparatorIndex >= 0 && separatorIndex < 0) {
					// добавление нового символа превращает лексему-разделитель непонятно во что. значит, нужно возвращать лексему-разделитель
					return result;
				}
			}
			 // проверяем, что новый символ - разделитель
      s = String.valueOf(c);
      separatorIndex = GetSeparatorIndex(s);
			separatorStartIndex = GetStartSeparatorIndex(s);
			if ( (separatorIndex >=0 || separatorStartIndex >= 0) && !(currentValueSeparatorIndex >= 0 || currentValueSeparatorStartIndex >=0) ) {
				if ( !"".equalsIgnoreCase(result) ) {
				  return result;
				}

			}
			if ( !"".equals(result) && (separatorIndex >=0 || separatorStartIndex >= 0) ) {
				//Уже накоплена последовательность символов, и встречается либо лексема-разделитель, либо начало лексемы-разделителя
				// Увеличения значения счетчика не производим, чтобы следующий вызов процедуры начал работу с начала лексемы-разделителя
				return result;
			}
      if ( separatorIndex >=0 && separatorStartIndex < 0 ) {
      	//встретилась лексема разделитель
      	result = result + c;
				FCurrentPointer++;
				continue;
      }
      result = result + c;
      FCurrentPointer++;
			f = FCurrentPointer < len;
		}//while

		return result;
  }

  private int GetSeparatorIndex( String s ){
    int i = 0;
    while ( i < LexemSeparatorList.length ){
      if ( LexemSeparatorList[i].equalsIgnoreCase( s ) ){
        return i;
      }
      i++;
    }
    return -1;
  }

  private int GetStartSeparatorIndex(String s){
    int i = 0;
    while ( i < LexemSeparatorList.length ){
      if ( LexemSeparatorList[i].startsWith( s ) && !LexemSeparatorList[i].equalsIgnoreCase( s )){
        return i;
      }
      i++;
    }
    return -1;
  }


  public void SetPreviousLexem( ScriptLexem prevLexem ){
    previousLexem = prevLexem;
  }

}

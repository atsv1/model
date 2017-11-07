package mp.parser;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.Stack;

/**
 * User: atsv
 * Date: 04.04.2006
 */
public class PascalParser implements ScriptParser {
  /**
  в переменной FLexemVector хранится спосок возможных объектов-лексем, соответствующих
  значениям массива ScriptLanguageDef.LexemList
  */
  private Vector<ScriptLexem> FLexemVector;
  /**
    В переменной FParsedLexemList хранится лексем (объектов ScriptLexem),
    порядок следования которых  соответствует порядку следования лексем
    в обрабатываемом скрипте
  */
  private Vector FParsedLexemList;
  private String FScriptSource = "";

  protected Vector FProgram;
  protected VariableList Variables = null;
  private int FCurrentLexemPos = 0;

  private ScriptStateSaver FAutomatState = null;
  //Стек для разметки блоков. Разметка блоков используется в свертке
  private ScriptStateSaver FMarkerState = null;
  private int FCurrentState = 1;//начальное состояние автомата
  private int FPreviousState = -1;//предыдущее состояние автомата
  private Stack FStateStack = null;
  private ScriptLexemTokenizer FLexemTokenizer = null;
  private int FInnerPointer = -1;

  private int FMovMode = 0;
  private static final int MOV_MODE_AFTER = 1;
  private static final int MOV_MODE_BEFORE = 2;

  private ExecutionContext FExecutionContext = null;

  private void PrepareLanguage() {
    FParsedLexemList = new Vector();
    FLexemVector = new Vector();
    InitLexemVector();
    ParseLanguageDef();
    FProgram = new Vector();
    FAutomatState = new ScriptStateSaver();
    FMarkerState = new ScriptStateSaver();
    FStateStack = new Stack();
  }

  public PascalParser() {
    this.PrepareLanguage();
  }

  protected PascalParser(String aScript) {
    this.PrepareLanguage();
    FScriptSource = aScript;
  }

  protected PascalParser( VariableList aVariables ) {
    SetVariables(aVariables);
    this.PrepareLanguage();
    FScriptSource = "";
  }

  public String GetSource(){
    return FScriptSource;
  }

 /**
  Заполнение массива FLexemVector объектами-лексемами
 */
  private void InitLexemVector() {
    ScriptLexem lexem;
   lexem = new ScriptBeginLexem();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("(");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem(")");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("begin");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("end");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("if");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("then");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("else");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("jnt");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("jmp");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("while");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem("do");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptActionLexem3Operand();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptDigitLexem();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptEndLexem();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemEndString();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );


   lexem = new ScriptOperandSimple("test lexem variable &^%^^#");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );
   /*Порядок важен. Функции должны обязательно идти после переменных*/
   lexem = new ScriptFunctionLexem();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptActionLexem2Operand();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemProgramLink("Эталонный адрес", 1);
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemProgramLink("Эталонная метка", 2);
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemProgramLink("Эталонный маркер", 3);
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptSimpleLexem(",");
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemFunctionParam();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemArrayStartBracket();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemArrayFinishBracket();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );
   
   lexem = new VarLexem();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

 }

  private boolean IsMovResult(int aPointer){
    try{
      Object programObject = FProgram.get( aPointer-1 );
      Class cl = programObject.getClass();
      if ( "mp.parser.ScriptOperationMov".equalsIgnoreCase( cl.getName() ) ){
        return true;
      }
    } catch (Exception e){
    }
    return false;
  }

  /**Проверяется, является ли программный объект, на который указывает внутренняя ссылка (которая изменяется
   * в результате вызовов First и Next) результатом операци Mov.
   *
   * @return - возвращается "да", если программный объект, на который указывает внутренний указатель, действительно
   * является результатом операции Mov
   */
  public boolean IsMovResult(){
    return IsMovResult( FInnerPointer );
  }

  public ScriptProgramObject First(String ... aClassName){
    FInnerPointer = -1;
    return Next( aClassName );
  }

  public ScriptProgramObject Next(String ... aClassName){
    FInnerPointer++;
    if ( FInnerPointer >= FProgram.size() )
    {
      return null;
    }
    int i = 0;
    int len = aClassName.length;
    String className;
    while ( true ){
      ScriptProgramObject o = (ScriptProgramObject) FProgram.get( FInnerPointer );
      Class c = o.getClass();
      className = c.getName();
      i = 0;
      while ( i < len ){
        if ( className.equalsIgnoreCase( aClassName[i] ) ){
          return o;
        }
        i++;
      }
      /*if (c.getName().equalsIgnoreCase( aClassName[0] )  )
      {
        return o;
      } else*/ FInnerPointer++;
      if ( FInnerPointer >= FProgram.size() ) {
        return null;
      }
    }
  }

  /**
   Процедура производит разбор продукций для одной (переданной в параметре)
   лексемы.
   Второй параметр - список лексем, которые могут следовать за переданной в параметре
   лексемой
   */
  private void ParseProduction( ScriptLexem aLexem, String aLexemString ) throws ScriptException  {
    if ( aLexem == null ) {
      ScriptException e;
      e = new ScriptException("В процедуру разбора продукций передано пустое значение");
      throw e;
    }//if
    if ( ( aLexemString == null ) || (aLexemString.equalsIgnoreCase(""))   ) {
      ScriptException e;
      e = new ScriptException("В процедуру разбора продукций передана пустая строка продукций");
      throw e;
    }//if
     
   ScriptLexem producedLexem = null;
   String[] lexemArr = aLexemString.split("#");
   for (String l : lexemArr) {
  	 producedLexem = GetLexem( l.trim() );
  	 aLexem.AddProducedLexem( producedLexem );
   }   
  }

  /**
   * Процедура разбирает определения языка, содержащиеся в классе ScriptLanguageDef.
   * Сначала вызывается процедура разбора продукций.
   *
  */
  protected void ParseLanguageDef() {
    int i = 0;
    ScriptLexem lexem = null;

    String lexemString;
    int lexemPos;

    while ( i < ScriptLanguageDef.LanguageDef.length )
    {
      lexemPos =  ScriptLanguageDef.LanguageDef[i].indexOf("->");
      if ( lexemPos != -1 )
      {
        lexemString = ScriptLanguageDef.LanguageDef[i].substring(0,lexemPos-1).trim();
        lexem = GetLexem( lexemString );
        if ( lexem != null )
        {
          try
          {
            ParseProduction(lexem, ScriptLanguageDef.LanguageDef[i].substring(lexemPos+2).trim());
          } catch (ScriptException e){}
        } //if
      }//if
      i++;
    }//while
  }


  protected void ClearParsedObjects()
  {
    FParsedLexemList.clear();
    FProgram.clear();
  }

  protected ScriptLexem GetParsedLexem(int aLexemIndex)
  {
    ScriptLexem lexem = null;
    if ( ( aLexemIndex >= 0 ) && ( aLexemIndex < FParsedLexemList.size() ) ){
      lexem = (ScriptLexem) FParsedLexemList.get(aLexemIndex);
    }
    return lexem;
  }

  protected ScriptLexem GetLexemByString(String aToken) {
    ScriptLexem lexem = null;
    int i = 0;
    while ( i < FLexemVector.size() ) {
       lexem = (ScriptLexem)FLexemVector.get(i);
       if ( lexem.IsMyToken(aToken) ) {
         break;
       } else {
         i++;
       }
    }//while
    return lexem;
  }

  protected ScriptLexem GetLexem(String aLexemName) {
    ScriptLexem lexem = null;
    int i = 0;
    while ( i < FLexemVector.size() ) {
       lexem =  FLexemVector.get(i);
       if ( aLexemName.equalsIgnoreCase(lexem.GetLanguageName()) ) {
      	 return lexem;
       } else {
         if ( lexem.IsMyToken( aLexemName ) ) {
           //break;
        	 return lexem;
         } 
       }
     i++;
    }//while
    return null;
  }

  protected ScriptLexem GetNextLexem(StringBuffer aCodePart) throws ScriptException {
    String lexemCode = FLexemTokenizer.GetNextLexem3();
    if ( lexemCode == null || "".equals(lexemCode) ){
      return null;
    }
    aCodePart.append( lexemCode );
    ScriptLexem lexem;
    lexem = GetLexem( lexemCode.trim() );
    FLexemTokenizer.SetPreviousLexem( lexem );
    if ( (lexem == null) && (!lexemCode.equals("")) ) {      
      throw  new ScriptException( "Неизвестная строка: " +  lexemCode);      
    }
    return lexem;
  }


  private ScriptLexem GetEtalonLexem( ScriptLexem aLexem ) {
    ScriptLexem etalonLexem = null;
    etalonLexem = GetLexem( aLexem.GetLanguageName() );
    return etalonLexem;
  }

  protected void Add2ParsedLexemList(ScriptLexem aLexem, StringBuffer aCodePart) throws ScriptException {
     int i = FParsedLexemList.size();
     if ( i > 0 ) {
       i--;
       ScriptLexem previousLexem = (ScriptLexem) FParsedLexemList.get(i);
       ScriptLexem previousEtalonLexem = GetEtalonLexem( previousLexem );
       if (previousEtalonLexem.IsProducedLexemExist( aLexem )) {
          ScriptLexem newLexem = (ScriptLexem)aLexem.clone();
          newLexem.SaveCode( aCodePart.toString() );
          FParsedLexemList.add( newLexem );
       } else {
         throw new ScriptException("_После "  + previousLexem.GetCode() + " не может следовать " + aCodePart );         
       }
     } else {
       ScriptBeginLexem beginLexem;
       beginLexem = (ScriptBeginLexem) GetLexem("Начало");
       ScriptLexem newLexem = (ScriptLexem)beginLexem.clone();
       FParsedLexemList.add( newLexem );
       Add2ParsedLexemList( aLexem, aCodePart );
     }
  }

  private void IsLexemCenInsertedAfter(int position, ScriptLexem aLexem) throws ScriptException {
    ScriptException e;
    if ( position <= 0 && !aLexem.GetLanguageName().equalsIgnoreCase("Начало")) {
    	throw new ScriptException("Нельзя вставлять в самое начало лексему " + aLexem.GetLanguageName());      
    }
    if ( aLexem.IsServiceLexem() ) {
      return ;
    }
    if ( position == 0 ) {
      //@todo Здесь какой-то баг, это заглушка. Попробовать убрать и увидеть, что получится
      return;
    }
    ScriptLexem prevLexem = (ScriptLexem) FParsedLexemList.get( position-1 );
    if ( prevLexem.IsServiceLexem() ) {
      return;
    }
    ScriptLexem prevEtalonLexem = GetEtalonLexem( prevLexem );
    if (!prevEtalonLexem.IsProducedLexemExist( aLexem )) {
      throw new ScriptException("После  " + prevEtalonLexem.GetLanguageName() + " не может следовать  " + aLexem.GetLanguageName());      
    }
  }

  private void IsLexemCenInsertedBefore(int position, ScriptLexem aLexem) throws ScriptException {
    ScriptException e;
    int i = FParsedLexemList.size();
    if ( position >= i && !aLexem.GetLanguageName().equalsIgnoreCase("Конец") ) {
       e = new ScriptException("Нельзя вставлять в конец скрипта лексему " + aLexem.GetLanguageName());
      throw e;
    }
    if ( aLexem.IsServiceLexem() ) {
      return ;
    }
    if ( (position) >= FParsedLexemList.size() ) {
      e = new ScriptException("Попытка добавить лексему в самый конец последовательности");
      throw e;
    }
    ScriptLexem nextLexem = (ScriptLexem) FParsedLexemList.get( position);
    if (nextLexem.IsServiceLexem()) {
      return;
    }
    ScriptLexem etalonLexem = GetEtalonLexem( aLexem );
     if (!etalonLexem.IsProducedLexemExist( nextLexem ))  {
      e = new ScriptException("После  " + etalonLexem.GetLanguageName() + " не может следовать  " +
                 nextLexem.GetLanguageName());
      throw e;
    }
  }

  /**Процедура замены одной лексемы на другую в списка обработанных лексем
   * @param position номер позиции замены в списке обработанных лексем
   * @param aLexem Лексема, которая будет вставлена в список взамен старой
   */
  private void ChangeLexemInParsedList(int position, ScriptLexem aLexem) throws ScriptException {
    IsLexemCenInsertedAfter(position, aLexem);
    IsLexemCenInsertedBefore(position+1, aLexem);
    FParsedLexemList.setElementAt(aLexem, position);
  }

  protected int AddObjectToProgram(Object aProgramObject, int aOperationPos)  {
    if ( aProgramObject == null ){
      return 0;
    }
    while ( aOperationPos >= FProgram.size() ) {
      FProgram.add(null);
    }//while
    if ( aProgramObject instanceof Vector ){
      int i = 0;
      Vector v = (Vector) aProgramObject;
      Object o;
      while ( i < v.size() ){
        o = v.get( i );
        if ( FProgram.size() <= aOperationPos ) {
          FProgram.add(null);
        }
        FProgram.setElementAt(o, aOperationPos);
        aOperationPos++;
        i++;
      }//while
      return v.size();
    }
    FProgram.setElementAt(aProgramObject, aOperationPos);
    return 1;
  }

  protected int GetSvertkaIndex(int aLexemPos) {
    int productionIndex = -1;
    int i = 0;
    int j;
    ScriptLexem lexem;
    int currentLexemPos = aLexemPos;
    boolean f;
    String s = "";
    boolean result = false;
    if ( FParsedLexemList.size() <= aLexemPos ) {
      return -1;
    }
    while ( i < ScriptLanguageDef.productionList.length ) {
      j = 2;
      f = true;
      currentLexemPos = aLexemPos;
      while ( f ) {
        lexem = GetParsedLexem( currentLexemPos );
        if ( lexem == null ) {
          return i;
        }
        try {
          s = ScriptLanguageDef.productionList[i][j];
        } catch (ArrayIndexOutOfBoundsException e) {
          f = false;
          result = true;
        }
        f = f & lexem.GetLanguageName().equalsIgnoreCase(s);
        if ( "Операнд".equalsIgnoreCase(s) && f && (currentLexemPos < this.FParsedLexemList.size()-1)
        		 &&
        		 ( (j < (ScriptLanguageDef.productionList[i].length-1)
        		    && !"[".equalsIgnoreCase( ScriptLanguageDef.productionList[i][j+1] )
        		   ) || (j >=  (ScriptLanguageDef.productionList[i].length-1) )
        		) ) {
        	/*Случай, когда совпали операнды
        	 * Необходимо проверить, что если операнд- массив, то следующая лексема не должна быть скобкой открывания массива - [.
        	 * Тогда текущий операнд - не операнд а начало вызова функции
        	 * */
        	ScriptLexem nextLexem = GetParsedLexem(currentLexemPos+1);
        	if ( nextLexem instanceof ScriptLexemArrayStartBracket ) {
        		f = false;
        	}
        }
        currentLexemPos++;
        j++;
      }//while
      if ( result ) {
        return i;
      }
      i++;
    }//while

    return productionIndex;
  }

  /**
   *
   * @param aSvertkaIndex номер строки в матрице свертки
   * @param aLexemPos номер первой лексемы в данной свертке
   * @return
   */
  protected ScriptLexem GetMainLexem( int aSvertkaIndex, int aLexemPos ) {
    ScriptLexem result = null;
    int i = 2;
    while ( i < ScriptLanguageDef.productionList[ aSvertkaIndex ].length) {
      if ( ScriptLanguageDef.productionList[ aSvertkaIndex ][i].equalsIgnoreCase(
               ScriptLanguageDef.productionList[ aSvertkaIndex ][1] )
         )   {
        result = (ScriptLexem) FParsedLexemList.get(aLexemPos + i-2);
        break;
      }
      i++;
    }
    return result;
  }

  protected static int GetLexemPriority(ScriptLexem aLexem) {
    String code = aLexem.GetCode();
    if ( code == null ) {
      return ScriptLanguageDef.operationPriority.length + 1;
    }
    int result = ScriptLanguageDef.operationPriority.length;
    int i = 0;
    int j = 0;
    String s;
    while ( i < ScriptLanguageDef.operationPriority.length )  {
      j = 0;
      while ( j < ScriptLanguageDef.operationPriority[i].length )    {
        //@todo Для ускорения работы нужно убрать лишнее приравнивание
        s = ScriptLanguageDef.operationPriority[i][j];
        if ( aLexem.GetCode().equalsIgnoreCase( s ) )    {
          return i;
        }
        j++;
      }
      i++;
    }//while
    return result;
  }

  /** Возвращает 1, если операция в лексеме1 имеет бОльший приоритет по сравнению с
   * операцией в лексеме2.
   * Возвращает 2, если операция в лексем1 имеет мЕнтший приоритет по сравнению с операцией
   * в лексеме2.
   * Возвращает 3, если приоритеты равны
   *
   * @param lexem1
   * @param lexem2
   * @return
   */
  protected static int PriorityCompare(ScriptLexem lexem1, ScriptLexem lexem2) {
    int priority1;
    int priority2;
    if ( (lexem1 != null) && (lexem2 == null) )  {
      return 1;
    }
    if ( (lexem1 == null) && (lexem2 != null) ) {
      return 2;
    }
    if ( (lexem1 == null) && (lexem2 == null) )  {
      //return 3;
      return 1;
    }
    priority1 = GetLexemPriority( lexem1 );
    priority2 = GetLexemPriority( lexem2 );
    //Чем меньше приоритет, тем он считается приоритетнее
    if ( priority1 < priority2 )
    {
      return 1;
    }
    if ( priority1 > priority2 )
    {
      return 2;
    } else return 3;
  }

  /**
   * Функция определяет новый индекс свертки. Ноывй индекс нужен для того, чтобы определить, какая из рядом стоящих сверток
   * является более приоритетной.
   * Приоритет свертки определяется по операции, которая участвует в свертке. Список приоритетов находится в массиве
   * ScriptLanguageDef.operationPriority
   *
   * @param aLexemPos - начальная позиция текущей свертки, для которой определяется более приоритетная свертка
   * @param aCurrentIndex - индекс свертки в массиве ScriptLanguageDef.productionList. Это индекс той свертки,
   * которой можно воспользоваться уже прямо сейчас, но
   * @return - возвращается индекс новой возможной свертки (индекс из массива сверток ScriptLanguageDef.productionList).
   * Также изменяется значение переменной FCurrentLexemPos - оно становится равным первой позиции новой свертки (если конечно
   * нашлась более приоритетная свертка).
   */
  protected int GetSvertkaIndexWithPriority( int aLexemPos, int aCurrentIndex )  {
    int currentIndex = aCurrentIndex;
    int currentPos = aLexemPos;
    int newIndex = 0;
    int newPos = 0;
    int i = 0;
    boolean f = true;
    i = ScriptLanguageDef.productionList[currentIndex].length;
    newPos =  aLexemPos +  i - 3; //устанавливаем указатель на последнюю лексему текущей свертки
    newIndex = GetSvertkaIndex( newPos );
    f = ( newIndex > -1 );
    ScriptLexem currentMainLexem = null;
    ScriptLexem nextMainLexem = null;
    int priorityCompareRes = 0;
    while ( f ) {
      currentMainLexem = GetMainLexem( currentIndex, currentPos );
      nextMainLexem = GetMainLexem( newIndex, newPos );
      priorityCompareRes = PriorityCompare( currentMainLexem, nextMainLexem );
      if ( ( priorityCompareRes== 1) || (priorityCompareRes == 3) ||
           (currentPos ==  newPos )
         )  {
        FCurrentLexemPos = currentPos;
        return currentIndex;
      }
      currentPos = newPos;
      currentIndex = newIndex;
      i = ScriptLanguageDef.productionList[currentIndex].length;
      newPos =  currentPos +  i - 3; //устанавливаем указатель на последнюю лексему текущей свертки
      newIndex = GetSvertkaIndex( newPos );
      f = ( newIndex > -1 );
      currentMainLexem = null;
      nextMainLexem = null;
    }//while
    //aLexemPos = newPos;
    FCurrentLexemPos = currentPos;
    return currentIndex;
  }

  /**Функция возвращает номер последнего ненулевого элемента.
   * Если функция возвращает значение < 0, то значит, что в программе нет ненулевых
   * элементов
   * @return
   */
  private int LastNotNullProgramIndex() {
    int counter = FProgram.size()-1;
    if ( counter <= 0  )
    {
      return -1;
    }
    Object programObject = null;
    boolean f = true;
    while ( f )
    {
      programObject = FProgram.get(counter);
      counter--;
      if ( ( programObject != null ) || ( counter < 0 ) )
      {
        f = false;
        counter++;
      }

    }//while
    return counter;
  }


  /**Вставка лексемы в определенное место списка лексем.
   *
   * @param aLexem Добавляемая лексема
   * @param aLexemPos Позиция, в которую будет добавлена лексема
   * @param aCheckType Тип добавления. Возможные варианты:
   * 0 - без контроля
   * 1 - контроль слева
   * 2 - контроль справа
   * 3 - полный контроль
   * @throws ScriptException
   */
  protected void InsertLexemWithCheck( ScriptLexem aLexem, int aLexemPos, int aCheckType ) throws ScriptException {
    switch ( aCheckType ){
    case 3: {
      IsLexemCenInsertedAfter(aLexemPos, aLexem);
      IsLexemCenInsertedBefore(aLexemPos, aLexem);
      FParsedLexemList.insertElementAt(aLexem, aLexemPos);
      //InsertLexemAt(aLexem, aLexemPos);
      return;
    }
        case 0:{
          FParsedLexemList.insertElementAt(aLexem, aLexemPos);
          break;
        }
        case 1:{
          IsLexemCenInsertedAfter(aLexemPos, aLexem);
          FParsedLexemList.insertElementAt(aLexem, aLexemPos);
          break;
        }
        case 2:{
          IsLexemCenInsertedBefore(aLexemPos, aLexem);
          FParsedLexemList.insertElementAt(aLexem, aLexemPos);
          break;
        }
    }//case
  }


  private  Operand GetTempOperand( ScriptOperation operation, int aProgramPointer ) throws ScriptException {
    Variable result = null;
    result = operation.GetResultVariable( aProgramPointer );
    if ( result == null ){
      return null;
    }
    result.SetName(GetVariables().GetNewTempVariableName());
    return result;
  }

  /** Функция возвращает лексему, которая останется в списке лексем, после того, как продукция будет свернута.
   * Функция действует двояко:
   * 1. она может вернуть лексему, которая уже присутствует в списке лексем. Тогда из списка лексем  будет удалены
   *    все лексемы, кроме возвращенной лексемы
   * 2. она может вернуть самостоятельно созданную лексему. Лексема создается тогда, когда строка в нулевом столбце
   *    массива ScriptLanguageDef.productionList начинается со служебного префикса New_
   * @param aLexemPos
   * @param aMatrixIndex
   * @return
   */
  private ScriptLexem GetSvertkaLexem(int aLexemPos, int aMatrixIndex) throws ScriptException  {
    ScriptLexem result = null;
    int i = aLexemPos;
    int matrixIndex = 2;
    int counter = ScriptLanguageDef.productionList[aMatrixIndex].length-2;
    String resultLexemName = ScriptLanguageDef.productionList[aMatrixIndex][0];
    boolean newLexemFlag = resultLexemName.startsWith( ScriptLanguageDef.CREATE_LEXEM_PREFIX );
    if ( newLexemFlag ) {
      // функция должна возвратить новую лексему
      resultLexemName = resultLexemName.substring( ScriptLanguageDef.CREATE_LEXEM_PREFIX.length() );
      ScriptLexem etalon = GetLexemByString( resultLexemName );
      if ( etalon == null ){
        ScriptException e = new ScriptException("Не найти эталонную лексему для строки \"" + resultLexemName + "\"");
        throw e;
      }
      result = (ScriptLexem) etalon.clone();
      if ( result instanceof ScriptLexemFunctionParam) {
        //пока не знаю, для какого количества случаев нужно передавать в результарующую лексему информацию о
        // лексемах, которые будут свернуты в нее. Пока что это нужно только для одной лексемы,
        // поэтому сейчас поступаю самым простым способом
        ScriptLexemFunctionParam fp = (ScriptLexemFunctionParam) result;
        fp.AddProduction( FParsedLexemList, aLexemPos, counter );

      }
    }
    while ( !newLexemFlag && counter > 0) {
      if ( ScriptLanguageDef.productionList[aMatrixIndex][ matrixIndex ].equalsIgnoreCase(
                   ScriptLanguageDef.productionList[aMatrixIndex][0] )
         )  {
        result = (ScriptLexem) FParsedLexemList.get( i );
        return result;
      } else   {
        i++;
        counter--;
        matrixIndex++;
      }
    }
    return result;
  }

  /** Производится свертка одной продукции.
   * При свертке лексемы удаляются из списка FParsedLexemList и добавляются в программу FProgram.
   * В список лексем может быть добавлена новая лексема, замещающая собой удаленные
   * @param aMatrixIndex - номер продукции в массиве возможных сверток из массива ScriptLanguageDef.productionList
   * @param aLexemPos - номер позиции первой лексемы в свертке
   * @return - номер лексемы, с которой нужно продолжать парсинг
   * @throws ScriptException
   */
  protected int ProcessOneSvertka( int aMatrixIndex, int aLexemPos ) throws ScriptException  {
    boolean f = ( !ScriptLanguageDef.productionList[aMatrixIndex][1].equalsIgnoreCase("") );
    ScriptLexem lexem = null;
    ScriptLexem svertkaLexem = null;
    int i = aLexemPos;
    int matrixIndex = 2;
    Operand tempOperand = null;
    Operand operand;
    int programOperationIndex = LastNotNullProgramIndex() + 1;
    int programOperandIndex = programOperationIndex + 1;
    ScriptOperandSimple tempOperandLexem = null;
    ScriptProgramObject operation = null;

    boolean isNewOperandNeed = false;
    int operandCount = 0;
    while ( f ) {
      lexem = (ScriptLexem) FParsedLexemList.get( i );
      if ( ScriptLanguageDef.productionList[aMatrixIndex][matrixIndex].equalsIgnoreCase(
                   ScriptLanguageDef.productionList[aMatrixIndex][1] )
         )  {
      	operandCount = 0;
        //здесь будет создан объект-операция
      	if ( FMovMode == MOV_MODE_AFTER ) {
          operation = (ScriptProgramObject) lexem.GetExecutableObject();
      	} else {
      		if ( FMovMode == MOV_MODE_BEFORE ) {
      			operation = (ScriptProgramObject) lexem.GetExecutableObject_BeforeMov();
      		}
      	}
        if ( operation == null ){
          ScriptException e = new ScriptException("Для лексемы " + lexem.GetLanguageName() + " не создается исполняемого объекта");
          throw e;
        }
        operation.Program = FProgram;
        if ( lexem.IsNewOperandNeed() )   {
          isNewOperandNeed = true;
          svertkaLexem = lexem;
        }
        AddObjectToProgram( operation, programOperationIndex );
      } else  {
      	
        //operand = (Operand) lexem.GetExecutableObject();
        Object o =  lexem.GetExecutableObject();
        if ( o != null )    {
        	operandCount = AddObjectToProgram( o, programOperandIndex );
          programOperandIndex = programOperandIndex + operandCount;          
          if ( operation != null && operation instanceof ScriptOperation ) {
          	((ScriptOperation)operation).setOperandCount(operandCount);
          }
          //programOperandIndex++;
        }
      }
      matrixIndex++;
      i++;
      if ( matrixIndex >= ScriptLanguageDef.productionList[aMatrixIndex].length  )  {
        f = false;
      }
    }//while
    if ( (svertkaLexem == null) && !ScriptLanguageDef.productionList[aMatrixIndex][0].equalsIgnoreCase("")  ) {
      svertkaLexem = GetSvertkaLexem( aLexemPos, aMatrixIndex );
    }
    //удаление обработанных лексем
    int delIndex = aLexemPos;
    while ( delIndex < aLexemPos + ScriptLanguageDef.productionList[aMatrixIndex].length - 2 ) {
      FParsedLexemList.removeElementAt( aLexemPos );
      delIndex++;
    }
    if ( isNewOperandNeed )  {
      tempOperand = GetTempOperand( (ScriptOperation)operation, programOperationIndex);
      tempOperandLexem = new ScriptOperandSimple(tempOperand.GetName());
      GetVariables().AddVariable( tempOperand );
      AddObjectToProgram( tempOperand, programOperandIndex );
      tempOperandLexem.Variables = GetVariables();
      InsertLexemWithCheck( tempOperandLexem, aLexemPos,3);
    } else  {
      if ( svertkaLexem != null ) {
        InsertLexemWithCheck( svertkaLexem, aLexemPos,3);
      }
    }
    return aLexemPos + i;
  }


  private int GetFirstLexemPos(int aStartPos, String aLexem)  {
    int result = -1;
    if ( (aLexem == null) || ( aStartPos >= FParsedLexemList.size() ) )   {
      return result;
    }
    int i = aStartPos;
    boolean f =  (aStartPos >= 0) ;
    ScriptLexem currentLexem;
    while ( f ) {
      currentLexem = (ScriptLexem) FParsedLexemList.get( i );
      if ( aLexem.equalsIgnoreCase(currentLexem.FCodePart) )  {
        return i;
      }
      i++;
      f = ( i < FParsedLexemList.size() );
    }//while
    return result;
  }

  private int GetFirstLexemPos(int aStartPos, String aLexem1, String aLexem2)  {
    int result = -1;
    if (  aLexem1 == null) {
      aLexem1 = "";
    }
    if (  aLexem2 == null) {
      aLexem1 = "";
    }
    if (  aStartPos >= FParsedLexemList.size()  ) {
      return result;
    }
    int i = aStartPos;
    boolean f =  (aStartPos >= 0) ;
    ScriptLexem currentLexem;
    while ( f )  {
      currentLexem = (ScriptLexem) FParsedLexemList.get( i );
      if ( aLexem1.equalsIgnoreCase(currentLexem.FCodePart) || aLexem2.equalsIgnoreCase(currentLexem.FCodePart))  {
        return i;
      }
      i++;
      f = ( i < FParsedLexemList.size() );
    }//while
    return result;
  }

  private static int GetSvertkaLexemCount(int aMatrixIndex) {
    int result = -1;
    result = ScriptLanguageDef.productionList[ aMatrixIndex ].length - 2;
    return result;
  }

  private static boolean IsOpenBracketLexem( ScriptLexem aLexem ){
    if ( aLexem == null ){
      return false;
    }
    if ( !( aLexem instanceof ScriptSimpleLexem ) ){
      return false;
    }
    return "(".equalsIgnoreCase( aLexem.GetCode() );
  }

  private static boolean IsCloseBracketLexem( ScriptLexem aLexem ){
    if ( aLexem == null ){
      return false;
    }
    if ( !( aLexem instanceof ScriptSimpleLexem ) ){
      return false;
    }
    return ")".equalsIgnoreCase( aLexem.GetCode() );
  }

  /**Функция возвращает позицию закрывающей скобки.
   * Поиск ведется в списке FParsedLexemList
   *
   * @param aOpenBracketPosition - позиция открывающей скобки, для которой нужно найти закрывающую скобку
   * @return - позиция закрывающей скобки
   */
  private int GetCloseBracketPos( int aOpenBracketPosition ) throws ScriptException{
    int result = 0;
    if ( aOpenBracketPosition >= FParsedLexemList.size()  ) {
      ScriptException e = new ScriptException("Внутренняя ошибка при определении закрывающей скобки");
      throw e;
    }
    ScriptLexem bracketLexem = (ScriptLexem) FParsedLexemList.get( aOpenBracketPosition );
    if ( !IsOpenBracketLexem(bracketLexem) ){
      ScriptException e = new ScriptException( "Внутренняя ошибка: переданная лексема не является открывающей скобкой" );
      throw e;
    }
    // проверки закончены
    //переменная, в которой хранится количество незакрытых скобок после открывающей скобки, переданной в параметре
    int middleBracketsCount = 0;
    int currentPos = aOpenBracketPosition + 1;
    boolean f = true;
    ScriptLexem lexem = null;
    while ( f ){
      lexem = (ScriptLexem) FParsedLexemList.get( currentPos );
      if ( IsOpenBracketLexem(lexem) ){
        middleBracketsCount++;
      } else {
        if ( IsCloseBracketLexem( lexem ) ){
          middleBracketsCount--;
          if ( middleBracketsCount <= 0 ){
            return currentPos;
          }
        }
      }
      currentPos++;
    }
    return result;
  }

  /**
   * Метод выполняет свертку всех функций, находящихся между начальной и конечной позициями включительно,
   * переданными в параметрах
   * Функци  ищутся в списке FParsedLexemList.
   * После работы этого метода в списке лексем внутри заданного интервала не должно быть ни одной лексемы-функции
   *
   * @param aStartPos - начальная позиция
   * @param aLastPos - конечная позиция
   */
  private int ParseLexemFunctions( int aStartPos, int aLastPos ) throws ScriptException {
    /**То, что текущая лексема является функцией, проверяется по принадлежности класса лексемы классу
     * mp.parser.ScriptFunctionLexem
     *
     */
    int currentPos = aStartPos;
    boolean f = currentPos < aLastPos;
    ScriptLexem currentLexem;
    int matrixIndex = -1;
    int currentLastPos = aLastPos;
    //ScriptLexem lastLexem = (ScriptLexem) FParsedLexemList.get( currentLastPos );
    int oldLexemListSize = FParsedLexemList.size();
    while ( f ){
      currentLexem = (ScriptLexem) FParsedLexemList.get( currentPos );
      if ( currentLexem instanceof ScriptFunctionLexem ){
        //текущая лексема - функция. Проверяем, можно ли сразу же произвести свертку лексем, принадлежащих этой
        // функции, или внутри функции находится составной аргумент, который нужно сворачивать отдельно
        matrixIndex = GetSvertkaIndex( currentPos );
        if ( matrixIndex == -1 ){
          //похоже, что внутри функции находится сложный аргумент.
          //вызываем функцию обычной свертки, с учетом того, что после текущей лексемы обязательно следует лексема (
          //т.е. вызывается сверка с таким расчетом, чтобы в процессе сверики не исчезла открывающая скобка, иначе
          // получится так, что после лексемы "Функция" сразу же добавляется лексема "Операнд". а это недопустимо
          // правилами
          //завершающая позиция получается после нахождения закрывающей скобки функции
          ProcessSvertkaBetweenPos( currentPos + 2, GetCloseBracketPos( currentPos + 1 ) );
        } else {
          ProcessOneSvertka(matrixIndex, currentPos);
        }
      } else
      {
        currentPos++;
      }
      currentLastPos = currentLastPos - ( oldLexemListSize - FParsedLexemList.size() );
      oldLexemListSize = FParsedLexemList.size();
      if ( currentPos >= currentLastPos ) {
        f = false;
      }
    }//while
    return currentLastPos;
  }

  private int HasOpenBracket( int aStartPos, int aDistance ){
    ScriptLexem lexem;
    int i = aStartPos;
    while ( i < aDistance || i < FParsedLexemList.size() ){
      lexem = (ScriptLexem) FParsedLexemList.get( i );
      if ( IsOpenBracketLexem( lexem ) ){
        return i;
      }
      i++;
    }
    return -1;
  }

  /**  Процедура сворачивает одну строку - лексемы, которые находятся
   * между начальной позицией строки (первый параметр процедуры) и последней позицией
   * (последняя позиция свертки).
   * Строка обрабатывается начиная с начальной позиции включительно
   * по конечную позицию включительно
   * @throws ScriptException
   */
  private void ProcessSvertkaBetweenPos(int aFirstPos, int aLastPos) throws ScriptException {
    if ( aFirstPos > aLastPos ) {
      ScriptException e = new ScriptException("Начальная позиция свертки больше, чем конечная");
      throw e;
    }
    boolean f = true;
    int currentPos = aFirstPos;
    int currentLastPos = ParseLexemFunctions( aFirstPos, aLastPos );
    int matrixPos = -1;
    int emptyCycles = 0;
    //int lexemDistance = aLastPos - aFirstPos;
    int firstParsedLexemCount = FParsedLexemList.size();
    int oldParsedLexemCount  = FParsedLexemList.size();
    int lexemCount;
    int newMatrixPos;
    int lexemDecr;
    while ( f ) {
      matrixPos = GetSvertkaIndex( currentPos );
      newMatrixPos = matrixPos;
      if ( matrixPos >= 0 )  {
        //нужно проверить, возможна ли данная свертка с точки зрения попадания ВСЕХ
        //лексем в нужный диапазон
        lexemCount = GetSvertkaLexemCount( matrixPos );
        if (  (currentPos + lexemCount) <= ( currentLastPos+1 ) ) {
          newMatrixPos = GetSvertkaIndexWithPriority( currentPos, matrixPos );
          lexemCount = GetSvertkaLexemCount( newMatrixPos );
          if ( (FCurrentLexemPos + lexemCount) <= ( currentLastPos + 1) ) {
            currentPos = FCurrentLexemPos;
            matrixPos = newMatrixPos;
          }
          int bracketPos = HasOpenBracket( currentPos + lexemCount, 2 );
          if ( bracketPos >= currentLastPos ){
            bracketPos = -1;
          }
          if ( bracketPos == -1 ){
            ProcessOneSvertka(matrixPos, currentPos);
            //вычисляем текущую позицию диапазона лексем, в котором можно
            // производить свертку
            currentLastPos = currentLastPos - (oldParsedLexemCount - FParsedLexemList.size());
            oldParsedLexemCount =  FParsedLexemList.size();
            currentPos = aFirstPos;
          } else {
            currentPos = bracketPos;
          }
        } else {
          emptyCycles++;
        }
      } else {
        currentPos++;
      }
      /**Здесь нужно проверить, не вышел ли алгоритм за пределы указанного ему
         * интервала лексем.
         */
      lexemDecr = firstParsedLexemCount - FParsedLexemList.size();
      if ( currentPos > (aLastPos - lexemDecr) )  {
        emptyCycles++;
      }
      if (  emptyCycles >= 2 )   {
        f = false;
      }
    }//while
  }

  private boolean isSetFunction( int startPos, int endPos ){
  	int i = startPos;
  	ScriptLexem lexem;
  	while ( i <= endPos ){
  		lexem = (ScriptLexem) FParsedLexemList.get(i);
  		if ( lexem instanceof ScriptLexemArrayStartBracket ) {
  			return true;
  		}
  		i++;
  	}
  	return false;
  }

  private void ProcessSvertkaSetFunction(int aFirstPos, int aLastPos, int aMovPos) throws ScriptException{
  	ScriptLexem startLexem = (ScriptLexem) FParsedLexemList.get(aFirstPos);
    if ( startLexem instanceof ScriptBeginLexem ) {
    	aFirstPos = aFirstPos + 1;
    }
    ScriptOperation lastOperand = null;
    if ( FProgram.size() > 0 ) {
    	Object lastProgramObject  = FProgram.get( FProgram.size()-1 );
    	if ( lastProgramObject instanceof  ScriptOperation) {
        lastOperand = (ScriptOperation)lastProgramObject ;
    	}
    }
    int programSize = FProgram.size();
  	// получаем индекс массива свертки
  	int matrixPos = GetSvertkaIndex( aFirstPos );
  	if ( matrixPos < 0 ) {
      // не найдено индекса в массиве для свертки. Это означает, что нужно выполнять свертку имеющихся вложенных конструкций
  		FMovMode = MOV_MODE_AFTER;
  		ProcessSvertkaBetweenPos(aFirstPos, aMovPos-2);
  	}
  	FMovMode = MOV_MODE_BEFORE;
  	matrixPos = GetSvertkaIndex( aFirstPos );
  	if ( matrixPos < 0 ) {
  		throw new ScriptException("Невозможно обработать операцию установки значения");
  	}
  	ProcessSvertkaBetweenPos(aFirstPos, GetFirstLexemPos( aFirstPos, ":=" )-1);
  	//проверяем наличие операнда после операции приравнивания
  	int movPos = GetFirstLexemPos( aFirstPos, ":=" );
  	ScriptLexem op = (ScriptLexem) FParsedLexemList.get(movPos+1);
  	FParsedLexemList.remove(movPos);
    if ( ";".equals(op.GetCode()) ) {
    	if ( lastOperand != null ) {
    		FProgram.add(lastOperand);
    	} else {
    		throw new ScriptException("1");
    	}
    } else {
    	Object o = op.GetExecutableObject();
    	if ( o != null ) {
    		FProgram.add(o);
    		FParsedLexemList.remove(op);
    	} else {
    		throw new ScriptException("2");
    	}
    }
  }

  /**
   * Организует свертку строки, в которой есть операция приравнивания. Весь смысл этой процедуры - в организации
   * последовательной обработки выражения, а только затем - операции приравнивания.
   *
   * @param aFirstPos
   * @param aLastPos
   * @throws ScriptException
   */
  private void ProcessSvertkaOneString(int aFirstPos, int aLastPos) throws ScriptException {
    int firstMovPos = GetFirstLexemPos( aFirstPos, ":=" );
    boolean isMovExist = false;
    int currentPos = aFirstPos;
    if ( (firstMovPos < aLastPos) && ( firstMovPos != -1 ) ) {
      currentPos = firstMovPos + 1;
      isMovExist = true;
    }
    int beginLexemCount = FParsedLexemList.size();
    FMovMode = MOV_MODE_AFTER;
    ProcessSvertkaBetweenPos(currentPos, aLastPos);
    if ( isMovExist ) {
      //сворачиваем приравнивание
    	int i = aLastPos - ( beginLexemCount - FParsedLexemList.size() );
      FMovMode = MOV_MODE_BEFORE;
      if ( !isSetFunction(aFirstPos, i) ) {
         ProcessSvertkaBetweenPos( aFirstPos, i );
      } else {
      	ProcessSvertkaSetFunction(aFirstPos, i, firstMovPos);
      }
    }
    //проверяем наличие лексемы ";" на первой позиции, и если она есть, то просто
    // удаляем эту лексему
    if ( FParsedLexemList.size() > 0 ) {
      ScriptLexem lexem = (ScriptLexem) FParsedLexemList.get( aFirstPos );
      if (  ";".equalsIgnoreCase( lexem.GetCode() ) ) {
        FParsedLexemList.removeElementAt( aFirstPos );
      }
    }
  }


  protected static int GetProgramLinkId(ScriptLexem aLexem) throws ScriptException {
    ScriptException e;
    if ( aLexem == null ) {
      //e = new ScriptException("Пустая ссылка на лексему-ссылку");
      //throw e;
      return -1;
    }
    try {
      ScriptLexemProgramLink lexem = (ScriptLexemProgramLink)aLexem;
     // return  lexem.GetLinkId();
     return Integer.parseInt( lexem.GetLinkId() );
    } catch (Exception e1){
      e = new ScriptException("Не преобразовать в ссылочный объект");
      throw e;
    }
  }

  private static boolean IslinkLexemEquals(ScriptLexem aLexem1, ScriptLexem aLexem2) {
    if ( (aLexem1 == null) || (aLexem2 == null) ) {
      return false;
    }
    ScriptLexemProgramLink lexem1= (ScriptLexemProgramLink)aLexem1;
    ScriptLexemProgramLink lexem2= (ScriptLexemProgramLink)aLexem2;
    return lexem1.GetLinkId().equalsIgnoreCase( lexem2.GetLinkId() );
  }

  private void ClearMarkerLexem(int aStartPos, int aStopPos) {
    int counter = aStopPos - aStartPos;
    ScriptLexem lexem = null;
    int currentPos = aStartPos;
    while ( counter >= 0 ) {
      lexem = (ScriptLexem)FParsedLexemList.get( currentPos );
      if ( lexem.GetLanguageName().equalsIgnoreCase("Маркер") ) {
        FParsedLexemList.removeElementAt( currentPos );
      } else {
        currentPos++;
      }
      counter--;
    }
  }

  /** Избавление от ненужных лексем, которые появляются в списке лексем вследствие разбора  исходного кода.
   *
   * В настоящий момент избавляемся только от кода, который порождается при разборе вызова функций внутри скрипта, и
   * при этом результат работы функции не приравнивается ни одной пользовательской переменной. В результате  в списке
   * лексем остается такая последовательность лексем: временная переменная и лексема окончания строки (";"). Эти две
   * лексемы нужно просто выбросить из списка лексем
   *
   * @param aStartPos
   * @param aStopPos
   */
  private boolean ClearRedundandLexem( int aStartPos, int aStopPos ){
    int currentStart = aStartPos;
    ScriptLexem lexem;
    while ( (currentStart+1) <= aStopPos ){
      lexem = (ScriptLexem) FParsedLexemList.get( currentStart );
      if ( lexem instanceof  ScriptOperandSimple){
        //да, лексема является операндом.
        //теперь проверяем, не временный ли это операнд
        String operandName = ((ScriptOperandSimple)lexem).GetOperandName();
        lexem = (ScriptLexem) FParsedLexemList.get( currentStart + 1);
        if ( ScriptLanguageDef.IsServiceName( operandName ) && ( lexem instanceof  ScriptLexemEndString ) ){
          FParsedLexemList.remove( currentStart );
          FParsedLexemList.remove( currentStart );
          return true;
        }
      }
      currentStart++;
    }//while
    return false;
  }

  /**Процедура выполняет свертку между двух позиций, переданных в параметре
   * Основная цель метода - вызывать свертку и при этом проверять - уменьшается ли количество лексем. Если количество
   * лексем уменьшается - значит, все идет хорошо. Если же не уменьшается, значит - что-то не работает
   * @param aStartPos
   * @param aStopPos
   * @throws ScriptException Исключение генерируется, когда количество лексем не уменьшилось две свертки подряд
   */
  private void SvertkaOneStringWithCheck(int aStartPos, int aStopPos) throws ScriptException {
    ScriptLexem endLexem = (ScriptLexem) FParsedLexemList.get( aStopPos );
    int currentStopPos = aStopPos;
    int newStopPos;
    int emptyCycles = 0;
    boolean f = true;

    while ( f ) {
      ProcessSvertkaOneString(aStartPos, currentStopPos);
      newStopPos = FParsedLexemList.indexOf( endLexem );
      f =  !( newStopPos == -1 );
      //провереям, изменилась ли позиция последней лексемы в списке лексем. Если позиция ищменилась - значит
      // свертка произошла и все идет нормально
      if ( newStopPos != currentStopPos ){
        emptyCycles = 0;
        currentStopPos = newStopPos;
      } else {
        emptyCycles++;
      }
      if ( emptyCycles >= 2 && !ClearRedundandLexem(aStartPos, currentStopPos) ) {
        /*System.out.println( "startPos=" + Integer.toString( aStartPos ) + " stopPos=" + Integer.toString( aStopPos ) +
          " currentStop=" + Integer.toString( currentStopPos ));
        PrintLexemList();*/
        ScriptException e = new ScriptException("Попозже напишу текст ошибки");
        throw e;
      }
    }//while
  }

  /**Выполняем свертку блока - т.е. одной или нескольких команд, разделенных символами ";" "end"
   * Блок ограничивается начальным и конечным символами, переданными в параметре
   * @param aStartPos начальная лексема блока
   * @param aStopPos Конечная лексема блока. Указывает либо на последнюю ";", либо на "end", либо на лексему окончания
   * программы
   * @throws ScriptException
   */
  private void SvertkaOneBlock(int aStartPos, int aStopPos) throws ScriptException
  {
    int startPos = aStartPos;
    ScriptLexem stopLexem = (ScriptLexem) FParsedLexemList.get( aStopPos );
    boolean f = true;
    int currentStopLexemPos = aStopPos;
    int stopPos = aStopPos;
    while ( f ){
      currentStopLexemPos = GetFirstLexemPos(startPos,";","end");
      if ( currentStopLexemPos == -1 )  {
        currentStopLexemPos = FParsedLexemList.size()-1;
      }
      if (  currentStopLexemPos > stopPos ) {
        currentStopLexemPos = stopPos;
      }
      SvertkaOneStringWithCheck(aStartPos, currentStopLexemPos);
      stopPos = FParsedLexemList.indexOf( stopLexem );
      f = !(stopPos == -1);
    }
  }

  protected void Svertka(ScriptLexem aInitLexem, int aStartPos) throws ScriptException {
    int stopPos;
    int startPos = aStartPos;
    ScriptLexem stopLexem = null;
    ScriptLexem initLexem = aInitLexem;
    ScriptException e;
    while ( true ) {
      stopPos = GetFirstLexemPos(startPos+1,"Маркер");
      if ( stopPos == -1) {
        if ( initLexem == null )
        {//код до самого своего конца не содержит больше меток
          SvertkaOneBlock(startPos, FParsedLexemList.size()-1);
          return;
        } else {
          e = new ScriptException("Незакрытый маркер ");
          throw e;
        }
      } else {
        stopLexem = (ScriptLexem)FParsedLexemList.get(stopPos);
        if ( IslinkLexemEquals(initLexem, stopLexem) ) {
        	//метки одинаковы, выполняем свертку и выходим из текущего варианта подпрограммы
          SvertkaOneBlock(startPos+1, stopPos-1);
          stopPos = FParsedLexemList.indexOf( stopLexem );
          ClearMarkerLexem(startPos, stopPos);
          return;
        } else {
          //выполнаяем свертку для всех лексем, идущих до метки
          /*try
          {
            SvertkaOneBlock(startPos+1, stopPos-1);
          } catch (ScriptException e1) {
            System.out.println( "start = " + Integer.toString( startPos+1 ) + " stop = " + Integer.toString( stopPos - 1 ) );
          }*/
          if ( !( (startPos+1) >= ( stopPos - 1 ) ) ) {
            SvertkaOneBlock(startPos+1, stopPos-1);
          }
          stopPos = FParsedLexemList.indexOf( stopLexem );
          Svertka( stopLexem, stopPos );
        }
      }
    }//while
  }

  /**Построчная свертка.
   * Определяется первая и последняя лексема строки и вызывается
   * процедура ProcessSvertkaOneString
   * Цикли повторяется до тех пор, пока массив лексем не станет пустым
   * @throws ScriptException
   */
  private void ProcessSvertka() throws ScriptException {
    if ( FParsedLexemList.size() == 0 ) {
      return;
    }
    ParseCaseOperators();
    ScriptLexem beginLexem = GetParsedLexem( 0 );
    ScriptLexem endLexem = GetParsedLexem( FParsedLexemList.size()-1 );
    if ( !beginLexem.GetLanguageName().equalsIgnoreCase("Начало") ) {
      ScriptException e;
      e = new ScriptException("Разобранная строка не начинается с начальной лексемы");
      throw e;
    }
    if ( !endLexem.GetLanguageName().equalsIgnoreCase("Конец") ) {
      ScriptException e;
      e = new ScriptException("Разобранная строка не заканчивается конечной лексемой");
      throw e;
    }
    //PrintLexemList();
    Svertka(null,0);
    LinkProgram();
    //PrintProgramList();
  }

  /**Процедура проверяет, присутствуют ли в списке лексем лексемы-функции,
   * которые соответствуют функциям (процедурам) без параметров.
   * Если такие функции есть, то после такой функции в список добавляется
   * еще две лексемы - "(" и ")"
   *
   */
  private void CheckForEmptyParamsFunction() {
    int i = 0;
    ScriptFunctionLexem lexem;
    ScriptSimpleLexem simpleLexem;
    ScriptLexem  nextLexem;

    while ( i < FParsedLexemList.size() ) {
      try {
        lexem = (ScriptFunctionLexem)FParsedLexemList.get(i);
        nextLexem = (ScriptLexem)FParsedLexemList.get(i+1);
        if ( !"(".equalsIgnoreCase( nextLexem.GetCode() ) ) {
          simpleLexem = new ScriptSimpleLexem("(");
          simpleLexem.FCodePart = "(";
          FParsedLexemList.insertElementAt( simpleLexem, i+1 );
          simpleLexem = new ScriptSimpleLexem(")");
          simpleLexem.FCodePart = ")";
          FParsedLexemList.insertElementAt( simpleLexem, i+2 );
          i = i + 3;
        } else {
          i = i + 2;
        }
      } catch (Exception e)
      {
        i++;
      }
    }//while
  }
  
  private int VAR_LEXEM_TYPE_VARIABLE = 1;
  private int VAR_LEXEM_TYPE_VARIABLE_DIVIDER = 2;
  private int VAR_LEXEM_TYPE_TYPE_SEPARATOR = 3;
  private int VAR_LEXEM_TYPE_TYPE = 4;
  private int VAR_LEXEM_TYPE_STOP_VAR_SECTION = 5;
  
  private int getLexemType(String lexemCode) throws ScriptException{
  	if ( lexemCode == null || "".equals(lexemCode) ) {
  		throw new ScriptException("неизвестная ошибка"); 
  	}
  	if ( ";".equals(lexemCode) ) {
  		return VAR_LEXEM_TYPE_STOP_VAR_SECTION;
  	}
  	if (",".equals(lexemCode)) {
  		return VAR_LEXEM_TYPE_VARIABLE_DIVIDER;
  	}
  	if (":".equalsIgnoreCase(lexemCode)) {
  		return VAR_LEXEM_TYPE_TYPE_SEPARATOR;
  	}
  	if ("integer".equalsIgnoreCase(lexemCode) 
  			|| "real".equalsIgnoreCase(lexemCode)
  			|| "boolean".equalsIgnoreCase(lexemCode)
  			|| "string".equalsIgnoreCase(lexemCode)
  			) {
  		return VAR_LEXEM_TYPE_TYPE;
  	}
  	return VAR_LEXEM_TYPE_VARIABLE;
  } 
  
  private void parseVarSection() throws ScriptException{
  	int prevLexemType = 0;  	
  	String lexemCode = FLexemTokenizer.GetNextLexem3();
  	int curLexemType = getLexemType(lexemCode);
  	boolean f = true;
  	List<String> varList = new ArrayList<String> ();
  	String typeName = null;
  	
  	while ( f ) {  		
  		if ( prevLexemType == 0 && curLexemType == VAR_LEXEM_TYPE_VARIABLE ) {
  			varList.add(lexemCode);
  			prevLexemType = VAR_LEXEM_TYPE_VARIABLE; 
  		} else if ( prevLexemType == 0 && curLexemType != VAR_LEXEM_TYPE_VARIABLE ) {
  			throw new ScriptException("Некорректный синтаксис около " + lexemCode);
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_VARIABLE && curLexemType == VAR_LEXEM_TYPE_VARIABLE_DIVIDER ) {
  			// запятая после названия переменной
  			prevLexemType = 0;
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_VARIABLE && curLexemType == VAR_LEXEM_TYPE_TYPE_SEPARATOR ) {
  			prevLexemType = VAR_LEXEM_TYPE_TYPE_SEPARATOR;
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_VARIABLE ) {
  			throw new ScriptException("Некорректный синтаксис около " + lexemCode);
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_TYPE_SEPARATOR && curLexemType == VAR_LEXEM_TYPE_TYPE) {
  			// пришел тип переменной, выходим из цикла и создаем все что накопилось в varList;
  			typeName = lexemCode;
  			prevLexemType = VAR_LEXEM_TYPE_TYPE;
  			//break;
  		}  else if ( prevLexemType == VAR_LEXEM_TYPE_TYPE_SEPARATOR ) {
  			throw new ScriptException("Некорректный синтаксис около " + lexemCode);  			
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_TYPE && curLexemType == VAR_LEXEM_TYPE_STOP_VAR_SECTION) {
  			break;
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_TYPE ) {
  			throw new ScriptException("Некорректный синтаксис около " + lexemCode);  			
  		}
  		lexemCode = FLexemTokenizer.GetNextLexem3();
  		curLexemType = getLexemType(lexemCode);
  		
  	}// while
  	for (String varName : varList) {
  		Variable newVar = new Variable();
  		newVar.SetName(varName);
  		if ( "integer".equalsIgnoreCase(typeName)  ) {
  			newVar.InitIntOperand(0);
  		} else if ( "real".equalsIgnoreCase(typeName)  ) {
  			newVar.InitFloatOperand(0);
  		} else if ( "boolean".equalsIgnoreCase(typeName) ) {
  			newVar.InitBooleanOperand(false);
  		} else if ( "string".equalsIgnoreCase(typeName) ) {
  			newVar.InitStringOperand("");
  		} else {
  			throw new ScriptException("Неизвестный тип переменной " +  typeName);
  		}
  		Variables.AddVariable(newVar);  		
  	}
  	
  }
  
  private void parseVarSection3() throws ScriptException{
  	boolean f = true;
  	String lexemCode = null;
  	String prevLexemCode = FLexemTokenizer.GetNextLexem3();
  	if ( prevLexemCode ==null  ){
  		throw new ScriptException("Пустая секция var");
  	}
  	List<String> varList = new ArrayList<String> ();
  	String typeName = null;
  	while (f) {
  		lexemCode = FLexemTokenizer.GetNextLexem3();
  		if ( ",".equals(lexemCode) || ":".equals(lexemCode) ) {
  			varList.add(prevLexemCode);
  			prevLexemCode = lexemCode;
  		} else  if ( ";".equals(lexemCode) ) {
  			typeName = prevLexemCode;
  			break;
  		} else if (lexemCode != null) { 
  			prevLexemCode = lexemCode;
  		} else {
  			throw new ScriptException("ошибка секции var");
  		}  		
  	}
  	if ( typeName == null ) {
  		throw new ScriptException("Отсутствует тип переменной ");
  	}
  	if ( varList.isEmpty() ) {
  		throw new ScriptException("Отсутствуют переменные ");
  	}
  	for (String varName : varList) {
  		Variable newVar = new Variable();
  		newVar.SetName(varName);
  		if ( "integer".equalsIgnoreCase(typeName)  ) {
  			newVar.InitIntOperand(0);
  		} else if ( "real".equalsIgnoreCase(typeName)  ) {
  			newVar.InitFloatOperand(0);
  		} else if ( "boolean".equalsIgnoreCase(typeName) ) {
  			newVar.InitBooleanOperand(false);
  		} else if ( "string".equalsIgnoreCase(typeName) ) {
  			newVar.InitStringOperand("");
  		} else {
  			throw new ScriptException("Неизвестный тип переменной " +  typeName);
  		}
  		Variables.AddVariable(newVar);
  	}
  	
  }

  /**
    Алгоритм парсера следующий:
    1. вначале скрипт разбивается на лексемы (с попутной проверкой на ошибки)
    2. потом производится свертка последовательностей лексем (опять же
      с проверкой на ошибки)
    3. в процессе свертки формируется последовательность команд, которые
      будет выполнять исполняющее устройство
  */
  public void ParseScript(String aScript) throws ScriptException {
    boolean f = true;
    ScriptLexem lexem = null;
    StringBuffer codePart = new StringBuffer();

    if ( !FScriptSource.equalsIgnoreCase("") ) {
      ClearParsedObjects();
    }//if
    FScriptSource = aScript;
    if ( GetVariables() == null ) {
       SetVariables(new VariableList());
    }
    FLexemTokenizer = new ScriptLexemTokenizer( FScriptSource );

    while ( f ) {
      codePart.delete( 0, codePart.length() );
      lexem = GetNextLexem( codePart );
      if ( lexem != null && lexem instanceof VarLexem ) {
      	parseVarSection();
      } else  if ( lexem == null ) {
        ScriptLexem endLexem = GetLexem("Конец");
        ScriptLexem newLexem = (ScriptLexem)endLexem.clone();
        codePart.append("");
        Add2ParsedLexemList( newLexem, codePart );
        f = false;
      } else {        
        Add2ParsedLexemList( lexem, codePart );
      }
    }//while
    CheckForEmptyParamsFunction();
    ProcessSvertka();
    //System.out.println( FProgram.size() );
  }//ParseScript

  public void ExecuteScript() throws ScriptException {
  	// Устанавливаем контекст выполнения, чтобы он был доступен всем в исполняющемся потоке.
  	// Сделано для правильной установки истории изменения переменных
  	ExecutionContextLocator.setContect(FExecutionContext);
    ScriptException e;
    if ( FProgram.size() == 0 ) {
      //e = new ScriptException("Пустая программа.");
      //throw e;
      return;
    }
    int programPointer = 0;
    boolean f = true;
    ScriptOperation currentOperation = null;
    int operationStep = 0;
    while ( f ) {
      try {
        currentOperation = (ScriptOperation) FProgram.get( programPointer );
      } catch (ClassCastException e1) {
        Object o = FProgram.get( programPointer );
        PrintProgramList();
        e = new ScriptException("ошибка преобразования классов: ожидается ScriptOperation, получен  " +
           o.getClass().getName() + " pointer = " + Integer.toString( programPointer ));
        throw e;
      }
      currentOperation.Program = FProgram;
      try {
        operationStep = currentOperation.ExecOperation( programPointer );
      } catch ( ScriptException e1 ){
        //PrintProgramList();
        throw e1;
      }
      programPointer = programPointer + operationStep + 1;
      if ( ( programPointer >= FProgram.size() ) || (programPointer < 0)  ) {
        f = false;
      }
    }//while
  }



  /*********************************************************************
   * *******************************************************************
   * Блок процедур, связанный с обработкой условий переходов
   * *******************************************************************
   * *******************************************************************
   */

  private void AddOpenMarker(int aMarkerPos) throws ScriptException
  {
    FMarkerState.NewOperator(null);
    int i = FMarkerState.GetCurrentLinkId();
    ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 3 );
    lexem.SaveCode("Маркер");
    FParsedLexemList.insertElementAt(lexem,aMarkerPos);
  }

  private void AddCloseMarker(int aMarkerPos) throws ScriptException {
    int i = FMarkerState.GetCurrentLinkId();
    /*if ( i == -1 ){
      return;
    }*/
    ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 3 );
    lexem.SaveCode("Маркер");
    FParsedLexemList.insertElementAt(lexem,aMarkerPos);
    FMarkerState.DelCurrentOperator();
  }

  private void AddStringSeparator(int aPosition) throws ScriptException {
    ScriptLexemEndString endLexem = (ScriptLexemEndString)GetLexem(";");
    ScriptLexemEndString lexem = (ScriptLexemEndString)endLexem.clone();
    InsertLexemWithCheck(lexem, aPosition,3);
  }

  /**Процедура предназначена для сохранения предыдущего состояния автомата разбора
   *операторов условного перехода. Состояние сохраняется в том случае, если оно не равно
   * нулевому
   */
  private void SavePreviousState() {
   if ( FPreviousState != 1 )  {
  	 //выполняется только в том случае, если предыдущее состояние - не начальное
     Integer i = FPreviousState;
     FStateStack.push( i );
   }
  }

  private void AddJumpCommand(int aPos, int aLinkId) throws ScriptException {
    ScriptLexem etalonLexem = GetLexem("jmp");
    ScriptLexem jumpLexem = (ScriptLexem) etalonLexem.clone();
    jumpLexem.SaveCode("jmp");
    ScriptLexemProgramLink linkLexem = new ScriptLexemProgramLink( Integer.toString(aLinkId), 1 );
    InsertLexemWithCheck(jumpLexem, aPos,1);
    InsertLexemWithCheck( linkLexem, aPos+1,1 );
    AddStringSeparator(aPos+2);
  }

  private int AutomatDoSomethingAfterIn( ScriptLexem aCurrentLexem, int aLexemIndex ) throws ScriptException {
    ScriptException e;
    int result = 0;
    switch (FCurrentState) {
      //
      case 1:{  //начальное состояние
        //проверяем необходимость изменения состояния автомата
        if ( !FStateStack.empty() ){
          Integer i = (Integer) FStateStack.pop();
          FCurrentState = i;
          result = 0;
        }
        break;
      }
        //состояние после получения лексемы IF
        case 2:{
          FAutomatState.NewOperator( aCurrentLexem );
          //создаем лексему условного перехода JT
          ScriptLexem etalonLexem = GetLexem("jnt");
          ScriptLexem newLexem = (ScriptLexem) etalonLexem.clone();
          newLexem.SaveCode("jnt");
          //меняем лексему if на лексему условного перехода jt
          ChangeLexemInParsedList(aLexemIndex, newLexem);
          //добавляем маркер перед IF
          AddOpenMarker(aLexemIndex);
          //добавляем маркер после IF на случай, если в условии - выражение, нуждающееся в свертке
          AddOpenMarker(aLexemIndex+2);
          result = 2;
          SavePreviousState();
          break;
        }
        //состояние после получения лексемы THEN
        //ждем либо BEGIN, либо начало выражения
        case 3:{
          //создаем лексему-ссылку
          int i = FAutomatState.GetCurrentLinkId();
          if ( i == -1 )
          {
            e = new ScriptException("Невозможно получить адрес перехода");
            throw e;
          }
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 1 );
          //меняем THEN на лексему-ссылку
          ChangeLexemInParsedList(aLexemIndex, lexem);
          //Добавляем закрывающий маркер перед THEN, то есть сразу же после выражения в условии
          AddCloseMarker(aLexemIndex);
          //добавляем закрывающий маркер после THEN
          AddCloseMarker(aLexemIndex+2);
          result = 2;
          break;
        }
        // получили BEGIN состояние после "if XXX then "
        case 4:{
          //добавляем перед BEGIN открывающий маркер
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        // получили либо операнд, либо функцию после "if XXX then"
        case 5:{
          //Добавляем открывающий маркер перед лексемой
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //получили ";" после "if XXX then XXX"
        case 6:{
          //добавляем после ; закрывающий маркер
          AddCloseMarker( aLexemIndex + 1 );
          //ставим метку после ";"
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex + 2, 3 );
          FAutomatState.DelCurrentOperator();
          result = 2;
          break;
        }
        //получили "end" после "if XXX then begin XXXXX"
        //ждем либо ";", либо "else"
        case 7:{
          //добавляем закрывающий маркер после end
          AddCloseMarker(aLexemIndex+1);
          result = 1;
          break;
        }
        //получили "else" после "if XXX begin XXXXX end"
        //Внимание!! Маркеры здесь не расставляются.
        case 8:{
          //сохраняем текущую ссылку из стека переходов
          int oldLink = FAutomatState.GetCurrentLinkId();
          FAutomatState.DelCurrentOperator();
          //создаем новую запись в стеке переходов
          // и меняем "else" на старую метку
          FAutomatState.NewOperator( aCurrentLexem );
          int newLink = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink newLinkLexem = new ScriptLexemProgramLink( Integer.toString(oldLink), 2 );
          ChangeLexemInParsedList(aLexemIndex, newLinkLexem);
          //добавляем после метки ";"
          AddStringSeparator(aLexemIndex+1);
          //вставляем команду перехода на еще несуществующую ссылку после окончания блока else
          //вставка осуществляется ПЕРЕД else
          AddStringSeparator(aLexemIndex-1);
          AddJumpCommand(aLexemIndex, newLink);
          result = 5;
          break;
        }
        // получили операнд либо функцию после "if  begin XXXXX end else"
        //ждем ";"
        case 9:{
          // добавляем открывающий маркер перед лексемой
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //получили BEGIN после "ХХХ else "
        case 10:{
          //добавляем открывающий маркер перед BEGIN
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //получили ";" после "if  begin XXXXX end else XXX"
        case 11:{
          //вставляем после ";" лексему-метку
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex+1,3);
          //вставляем ";" после метки
          AddStringSeparator( aLexemIndex+2 );
          FAutomatState.DelCurrentOperator();
          //добавляем после ; закрывающий маркер
          AddCloseMarker(aLexemIndex+3);
          result = 3;
          break;
        }
        //получили "end" после "else begin XXX"
        case 12:{
          //добавляем после end закрывающий маркер
          AddCloseMarker(aLexemIndex+1);
          //добавляем перед "end" метку
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex,3);
          FAutomatState.DelCurrentOperator();
          AddStringSeparator( aLexemIndex+1 );
          result = 1;
          break;
        }
        // получили ";" после if XXX begin XXXXX end
        case 13:{
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex+1,3);
          //удаляем текущий оператор из вершины стека
          FAutomatState.DelCurrentOperator();
          AddStringSeparator(aLexemIndex+2);
          result = 0;
          break;
        }
        //получили ";" после "else begin XXX end"
        case 14:{

          result = 0;
          break;
        }
        //получили "else" после "if XXX then XXXX "
        case 15:{
          //меняем "else" на текущую метку
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          ChangeLexemInParsedList(aLexemIndex, lexem);
          //добавляем ";" после метки
          AddStringSeparator(aLexemIndex+1);
          FAutomatState.DelCurrentOperator();
          //вставляем перед "else" команду безусловного перехода
          FAutomatState.NewOperator( lexem );
          i = FAutomatState.GetCurrentLinkId();
          AddStringSeparator(aLexemIndex);//вставляем перед else ";"
          AddJumpCommand(aLexemIndex+1, i);
          //добавление закрывающего маркера после команды JMP
          AddCloseMarker( aLexemIndex+4 );
          result = 6;
          break;
        }
        // получили WHILE
        case 16:{
          SavePreviousState();
          //добавляем метку перед while
          FAutomatState.NewOperator( aCurrentLexem );
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex,1);
          AddStringSeparator( aLexemIndex+1);
          //меняем while на JNT
          ScriptLexem etalonLexem = GetLexem("jnt");
          ScriptLexem newLexem = (ScriptLexem) etalonLexem.clone();
          newLexem.SaveCode("jnt");
          ChangeLexemInParsedList(aLexemIndex+2, newLexem);
          //добавляем маркер перед JNT
          AddOpenMarker( aLexemIndex + 2 );
          //добавляем маркер после JNT
          AddOpenMarker( aLexemIndex + 4 );
          result = 4;
          break;
        }
        // получили DO после "while XXX "
        case 17:{
          //добавляем закрывающий маркер перед DO
          AddCloseMarker( aLexemIndex );
          //меняем DO на адрес перехода
          FAutomatState.NewOperator( aCurrentLexem );
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 1 );
          ChangeLexemInParsedList(aLexemIndex+1, lexem);
          AddStringSeparator(aLexemIndex+2);//добавляем ";" после адреса
          AddCloseMarker( aLexemIndex+3);
          result = 3;
          break;
        }
        //получили begin после "while XXX do "
        case 18:{
          //добавляем открывающей маркер перед BEGIN
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //получили операнд либо функцию после  "while XXX do "
        case 19:{
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //получили "end" "while XXX do begin XXX "
        case 20:{
          //AddCloseMarker( aLexemIndex +1 );
          //добавляем после END метку, на которую будет переходить программа в случае выхода из цикла
          int i = FAutomatState.GetCurrentLinkId();
          FAutomatState.DelCurrentOperator();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex+1,1);
          AddStringSeparator( aLexemIndex+2 );
          //добавляем команду JUMP перед END
          i = FAutomatState.GetCurrentLinkId();
          AddJumpCommand( aLexemIndex, i);
          FAutomatState.DelCurrentOperator();
          //добавляем закрывающий маркер между командой JUMP и END
          AddCloseMarker( aLexemIndex +3 );
          result = 6;
          break;
        }
        //получили ";" после "while XXX do XXX"
        case 21:{
          //добавляем после END метку, на которую будет переходить программа в случае выхода из цикла
          int i = FAutomatState.GetCurrentLinkId();
          FAutomatState.DelCurrentOperator();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex+1,1);
          AddStringSeparator( aLexemIndex+2 );
          //добавляем команду JUMP перед END
          i = FAutomatState.GetCurrentLinkId();
          AddStringSeparator( aLexemIndex );// ставим ";" перед JUMP
          AddJumpCommand( aLexemIndex+1, i);
          FAutomatState.DelCurrentOperator();
          //добавляем закрывающий маркер между командой JUMP и END
          AddCloseMarker( aLexemIndex +4 );
          result = 7;
          break;
        }
        case 22:{ //после if XXX begin XXX end не поставлен else и не поставлена ;. При это сам скрипт закончился
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex - 1,1);
          result = 1;
          break;
        }
        case 23:{
          break;
        }
        default:{
          e = new ScriptException("Неизвестное состояние автомата разбора лексем - " + Integer.toString( FCurrentState ));
          throw e;
        }
    }//switch
    return result;
  }

  private static boolean IsLexemExistsInList(String aList, ScriptLexem aLexem) {
    String s = aLexem.GetLanguageName().toLowerCase();
    int i = aList.toLowerCase().indexOf( s );
    return i >= 0;
  }


  private static int GetAutomatMatrixIndex(int aCurrentState, ScriptLexem aLexem) {
    int i = 0;
    boolean f = true;
    String s;
    while ( f ) {
      s = ScriptLanguageDef.Automat[i][0];
      if ( s.equalsIgnoreCase( Integer.toString(aCurrentState) )  ) {
      	//нашли нужное состояние автомата
        s = ScriptLanguageDef.Automat[i][1];
        if ( IsLexemExistsInList(s,aLexem) ) {
          return i;
        }
      }
      i++;
      if ( i >=  ScriptLanguageDef.Automat.length) {
        f = false;
      }
    }
    return -1;
  }

  private static int GetImmediatelyNextMatrixIndex(int aAutomatState) {
    int i = 0;
    boolean f = true;
    String s;
    while ( f ) {
      s = ScriptLanguageDef.Automat[i][0];
      if ( s.equalsIgnoreCase( Integer.toString(aAutomatState) )  )
      { //нашли нужное состояние автомата
        s = ScriptLanguageDef.Automat[i][3];
        if ( s.equalsIgnoreCase("0") ) {
          return i;
        }
      }
      i++;
      if ( i >=  ScriptLanguageDef.Automat.length) {
        f = false;
      }
    }
    return -1;
  }

  private void ClearBlockLexem() {
    int i = 0;
    ScriptLexem lexem;
    while ( i < FParsedLexemList.size() ) {
      lexem = (ScriptLexem) FParsedLexemList.get(i);
      if ( lexem.GetLanguageName().equalsIgnoreCase("begin") || lexem.GetLanguageName().equalsIgnoreCase("end") ) {
        FParsedLexemList.removeElementAt( i );
      } else i++;
    }
  }

  private void ParseCaseOperators() throws ScriptException  {
    int i = 0;
    boolean f = FParsedLexemList.size() > 0;
    ScriptLexem lexem = null;
    int automatIndex = -1;
    String s;
    while ( f )
    {
      lexem = (ScriptLexem) FParsedLexemList.get(i);
      automatIndex =  GetAutomatMatrixIndex(FCurrentState, lexem);
      if ( automatIndex != -1 )
      {
        //получаем код нового состояния
        s = ScriptLanguageDef.Automat[automatIndex][2];
        FPreviousState = FCurrentState;
        FCurrentState = Integer.parseInt( s );
        try{
        i = i + AutomatDoSomethingAfterIn( lexem,i );
        } catch (ScriptException e)
        {
           ScriptException e1 = new ScriptException("Ошибка: " + e.getMessage() + " " +
                   "лексема " + Integer.toString(i));
          throw e1;
        }
        //проверяем, нужно ли сразу переходить в другое состояние
        automatIndex = GetImmediatelyNextMatrixIndex( FCurrentState );
        while ( automatIndex != -1 )
        {
          s = ScriptLanguageDef.Automat[automatIndex][2];
          FCurrentState = Integer.parseInt( s );
          AutomatDoSomethingAfterIn( lexem, i );
          automatIndex = GetImmediatelyNextMatrixIndex( FCurrentState );
        }
      }
      i++;
      f = FParsedLexemList.size() > i;
    }//while
    ClearBlockLexem();
  }

  private void LinkProgram() throws ScriptException  {
    ScriptLinker linker = new ScriptLinker();
    linker.LinkProgram( FProgram );
  }

  public VariableList GetVariables() {
    return Variables;
  }

  protected void SetVariables(VariableList variables) {
    Variables = variables;
    ScriptLexem lexem = null;
    int i = 0;
    if ( FLexemVector == null ) return;
    while ( i < FLexemVector.size() )  {
      lexem = (ScriptLexem) FLexemVector.get(i);
      lexem.Variables = Variables;
      i++;
    }//while
  }

  private void SetFunctions( ScriptLanguageExt aLanguageExt ){
  	Object funcList = aLanguageExt.GetFunctions();
    if ( funcList == null ) {
    	return;
    }
    ScriptLexem lexem = null;
    int i = 0;
    if ( FLexemVector == null ) return;
    while ( i < FLexemVector.size() )  {
      lexem = (ScriptLexem) FLexemVector.get(i);
      if ( lexem instanceof ScriptFunctionLexem ) {
        ((ScriptFunctionLexem) lexem).SetFunctionList(aLanguageExt.GetFunctions());
      }
      i++;
    }//while

  }

  public void SetLanguageExt( ScriptLanguageExt aLanguageExt ) throws ScriptException{
    if ( aLanguageExt == null ){
      ScriptException e = new ScriptException("Попытка передать в парсер пустое расширение языка");
      throw e;
    }
    SetVariables( aLanguageExt.GetVariables() );
    SetFunctions(aLanguageExt);
  }

  protected void PrintLexemList(){
    int i = 0;
    ScriptLexem lexem = null;
    System.out.println("print lexem list");
    System.out.println("Source: \n" + FScriptSource);
    while ( i < FParsedLexemList.size() ){
      lexem = (ScriptLexem) FParsedLexemList.get( i );
      System.out.println(Integer.toString( i ) + " " + lexem.toString() );
      i++;
    }
  }

  private void PrintProgramList(){
    int i = 0;
    Object programObject = null;
    System.out.println("print program ");
    System.out.println("Source: \n" + FScriptSource);
    while ( i < FProgram.size() ){
      programObject =  FProgram.get( i );
      System.out.println(Integer.toString( i ) + " " + programObject.toString() );
      i++;
    }
  }

	@Override
  public void AddExecutionContext(ExecutionContext context) {
		FExecutionContext = context;

  }

	@Override
  public ExecutionContext GetExecutionContext() {
	  return FExecutionContext;
  }

}

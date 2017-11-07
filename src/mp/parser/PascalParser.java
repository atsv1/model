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
  � ���������� FLexemVector �������� ������ ��������� ��������-������, ���������������
  ��������� ������� ScriptLanguageDef.LexemList
  */
  private Vector<ScriptLexem> FLexemVector;
  /**
    � ���������� FParsedLexemList �������� ������ (�������� ScriptLexem),
    ������� ���������� �������  ������������� ������� ���������� ������
    � �������������� �������
  */
  private Vector FParsedLexemList;
  private String FScriptSource = "";

  protected Vector FProgram;
  protected VariableList Variables = null;
  private int FCurrentLexemPos = 0;

  private ScriptStateSaver FAutomatState = null;
  //���� ��� �������� ������. �������� ������ ������������ � �������
  private ScriptStateSaver FMarkerState = null;
  private int FCurrentState = 1;//��������� ��������� ��������
  private int FPreviousState = -1;//���������� ��������� ��������
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
  ���������� ������� FLexemVector ���������-���������
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
   /*������� �����. ������� ������ ����������� ���� ����� ����������*/
   lexem = new ScriptFunctionLexem();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptActionLexem2Operand();
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemProgramLink("��������� �����", 1);
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemProgramLink("��������� �����", 2);
   lexem.Variables = GetVariables();
   FLexemVector.add( lexem );

   lexem = new ScriptLexemProgramLink("��������� ������", 3);
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

  /**�����������, �������� �� ����������� ������, �� ������� ��������� ���������� ������ (������� ����������
   * � ���������� ������� First � Next) ����������� ������� Mov.
   *
   * @return - ������������ "��", ���� ����������� ������, �� ������� ��������� ���������� ���������, �������������
   * �������� ����������� �������� Mov
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
   ��������� ���������� ������ ��������� ��� ����� (���������� � ���������)
   �������.
   ������ �������� - ������ ������, ������� ����� ��������� �� ���������� � ���������
   ��������
   */
  private void ParseProduction( ScriptLexem aLexem, String aLexemString ) throws ScriptException  {
    if ( aLexem == null ) {
      ScriptException e;
      e = new ScriptException("� ��������� ������� ��������� �������� ������ ��������");
      throw e;
    }//if
    if ( ( aLexemString == null ) || (aLexemString.equalsIgnoreCase(""))   ) {
      ScriptException e;
      e = new ScriptException("� ��������� ������� ��������� �������� ������ ������ ���������");
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
   * ��������� ��������� ����������� �����, ������������ � ������ ScriptLanguageDef.
   * ������� ���������� ��������� ������� ���������.
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
      throw  new ScriptException( "����������� ������: " +  lexemCode);      
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
         throw new ScriptException("_����� "  + previousLexem.GetCode() + " �� ����� ��������� " + aCodePart );         
       }
     } else {
       ScriptBeginLexem beginLexem;
       beginLexem = (ScriptBeginLexem) GetLexem("������");
       ScriptLexem newLexem = (ScriptLexem)beginLexem.clone();
       FParsedLexemList.add( newLexem );
       Add2ParsedLexemList( aLexem, aCodePart );
     }
  }

  private void IsLexemCenInsertedAfter(int position, ScriptLexem aLexem) throws ScriptException {
    ScriptException e;
    if ( position <= 0 && !aLexem.GetLanguageName().equalsIgnoreCase("������")) {
    	throw new ScriptException("������ ��������� � ����� ������ ������� " + aLexem.GetLanguageName());      
    }
    if ( aLexem.IsServiceLexem() ) {
      return ;
    }
    if ( position == 0 ) {
      //@todo ����� �����-�� ���, ��� ��������. ����������� ������ � �������, ��� ���������
      return;
    }
    ScriptLexem prevLexem = (ScriptLexem) FParsedLexemList.get( position-1 );
    if ( prevLexem.IsServiceLexem() ) {
      return;
    }
    ScriptLexem prevEtalonLexem = GetEtalonLexem( prevLexem );
    if (!prevEtalonLexem.IsProducedLexemExist( aLexem )) {
      throw new ScriptException("�����  " + prevEtalonLexem.GetLanguageName() + " �� ����� ���������  " + aLexem.GetLanguageName());      
    }
  }

  private void IsLexemCenInsertedBefore(int position, ScriptLexem aLexem) throws ScriptException {
    ScriptException e;
    int i = FParsedLexemList.size();
    if ( position >= i && !aLexem.GetLanguageName().equalsIgnoreCase("�����") ) {
       e = new ScriptException("������ ��������� � ����� ������� ������� " + aLexem.GetLanguageName());
      throw e;
    }
    if ( aLexem.IsServiceLexem() ) {
      return ;
    }
    if ( (position) >= FParsedLexemList.size() ) {
      e = new ScriptException("������� �������� ������� � ����� ����� ������������������");
      throw e;
    }
    ScriptLexem nextLexem = (ScriptLexem) FParsedLexemList.get( position);
    if (nextLexem.IsServiceLexem()) {
      return;
    }
    ScriptLexem etalonLexem = GetEtalonLexem( aLexem );
     if (!etalonLexem.IsProducedLexemExist( nextLexem ))  {
      e = new ScriptException("�����  " + etalonLexem.GetLanguageName() + " �� ����� ���������  " +
                 nextLexem.GetLanguageName());
      throw e;
    }
  }

  /**��������� ������ ����� ������� �� ������ � ������ ������������ ������
   * @param position ����� ������� ������ � ������ ������������ ������
   * @param aLexem �������, ������� ����� ��������� � ������ ������ ������
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
        if ( "�������".equalsIgnoreCase(s) && f && (currentLexemPos < this.FParsedLexemList.size()-1)
        		 &&
        		 ( (j < (ScriptLanguageDef.productionList[i].length-1)
        		    && !"[".equalsIgnoreCase( ScriptLanguageDef.productionList[i][j+1] )
        		   ) || (j >=  (ScriptLanguageDef.productionList[i].length-1) )
        		) ) {
        	/*������, ����� ������� ��������
        	 * ���������� ���������, ��� ���� �������- ������, �� ��������� ������� �� ������ ���� ������� ���������� ������� - [.
        	 * ����� ������� ������� - �� ������� � ������ ������ �������
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
   * @param aSvertkaIndex ����� ������ � ������� �������
   * @param aLexemPos ����� ������ ������� � ������ �������
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
        //@todo ��� ��������� ������ ����� ������ ������ �������������
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

  /** ���������� 1, ���� �������� � �������1 ����� ������� ��������� �� ��������� �
   * ��������� � �������2.
   * ���������� 2, ���� �������� � ������1 ����� ������� ��������� �� ��������� � ���������
   * � �������2.
   * ���������� 3, ���� ���������� �����
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
    //��� ������ ���������, ��� �� ��������� ������������
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
   * ������� ���������� ����� ������ �������. ����� ������ ����� ��� ����, ����� ����������, ����� �� ����� ������� �������
   * �������� ����� ������������.
   * ��������� ������� ������������ �� ��������, ������� ��������� � �������. ������ ����������� ��������� � �������
   * ScriptLanguageDef.operationPriority
   *
   * @param aLexemPos - ��������� ������� ������� �������, ��� ������� ������������ ����� ������������ �������
   * @param aCurrentIndex - ������ ������� � ������� ScriptLanguageDef.productionList. ��� ������ ��� �������,
   * ������� ����� ��������������� ��� ����� ������, ��
   * @return - ������������ ������ ����� ��������� ������� (������ �� ������� ������� ScriptLanguageDef.productionList).
   * ����� ���������� �������� ���������� FCurrentLexemPos - ��� ���������� ������ ������ ������� ����� ������� (���� �������
   * ������� ����� ������������ �������).
   */
  protected int GetSvertkaIndexWithPriority( int aLexemPos, int aCurrentIndex )  {
    int currentIndex = aCurrentIndex;
    int currentPos = aLexemPos;
    int newIndex = 0;
    int newPos = 0;
    int i = 0;
    boolean f = true;
    i = ScriptLanguageDef.productionList[currentIndex].length;
    newPos =  aLexemPos +  i - 3; //������������� ��������� �� ��������� ������� ������� �������
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
      newPos =  currentPos +  i - 3; //������������� ��������� �� ��������� ������� ������� �������
      newIndex = GetSvertkaIndex( newPos );
      f = ( newIndex > -1 );
      currentMainLexem = null;
      nextMainLexem = null;
    }//while
    //aLexemPos = newPos;
    FCurrentLexemPos = currentPos;
    return currentIndex;
  }

  /**������� ���������� ����� ���������� ���������� ��������.
   * ���� ������� ���������� �������� < 0, �� ������, ��� � ��������� ��� ���������
   * ���������
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


  /**������� ������� � ������������ ����� ������ ������.
   *
   * @param aLexem ����������� �������
   * @param aLexemPos �������, � ������� ����� ��������� �������
   * @param aCheckType ��� ����������. ��������� ��������:
   * 0 - ��� ��������
   * 1 - �������� �����
   * 2 - �������� ������
   * 3 - ������ ��������
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

  /** ������� ���������� �������, ������� ��������� � ������ ������, ����� ����, ��� ��������� ����� ��������.
   * ������� ��������� ������:
   * 1. ��� ����� ������� �������, ������� ��� ������������ � ������ ������. ����� �� ������ ������  ����� �������
   *    ��� �������, ����� ������������ �������
   * 2. ��� ����� ������� �������������� ��������� �������. ������� ��������� �����, ����� ������ � ������� �������
   *    ������� ScriptLanguageDef.productionList ���������� �� ���������� �������� New_
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
      // ������� ������ ���������� ����� �������
      resultLexemName = resultLexemName.substring( ScriptLanguageDef.CREATE_LEXEM_PREFIX.length() );
      ScriptLexem etalon = GetLexemByString( resultLexemName );
      if ( etalon == null ){
        ScriptException e = new ScriptException("�� ����� ��������� ������� ��� ������ \"" + resultLexemName + "\"");
        throw e;
      }
      result = (ScriptLexem) etalon.clone();
      if ( result instanceof ScriptLexemFunctionParam) {
        //���� �� ����, ��� ������ ���������� ������� ����� ���������� � �������������� ������� ���������� �
        // ��������, ������� ����� �������� � ���. ���� ��� ��� ����� ������ ��� ����� �������,
        // ������� ������ �������� ����� ������� ��������
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

  /** ������������ ������� ����� ���������.
   * ��� ������� ������� ��������� �� ������ FParsedLexemList � ����������� � ��������� FProgram.
   * � ������ ������ ����� ���� ��������� ����� �������, ���������� ����� ���������
   * @param aMatrixIndex - ����� ��������� � ������� ��������� ������� �� ������� ScriptLanguageDef.productionList
   * @param aLexemPos - ����� ������� ������ ������� � �������
   * @return - ����� �������, � ������� ����� ���������� �������
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
        //����� ����� ������ ������-��������
      	if ( FMovMode == MOV_MODE_AFTER ) {
          operation = (ScriptProgramObject) lexem.GetExecutableObject();
      	} else {
      		if ( FMovMode == MOV_MODE_BEFORE ) {
      			operation = (ScriptProgramObject) lexem.GetExecutableObject_BeforeMov();
      		}
      	}
        if ( operation == null ){
          ScriptException e = new ScriptException("��� ������� " + lexem.GetLanguageName() + " �� ��������� ������������ �������");
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
    //�������� ������������ ������
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

  /**������� ���������� ������� ����������� ������.
   * ����� ������� � ������ FParsedLexemList
   *
   * @param aOpenBracketPosition - ������� ����������� ������, ��� ������� ����� ����� ����������� ������
   * @return - ������� ����������� ������
   */
  private int GetCloseBracketPos( int aOpenBracketPosition ) throws ScriptException{
    int result = 0;
    if ( aOpenBracketPosition >= FParsedLexemList.size()  ) {
      ScriptException e = new ScriptException("���������� ������ ��� ����������� ����������� ������");
      throw e;
    }
    ScriptLexem bracketLexem = (ScriptLexem) FParsedLexemList.get( aOpenBracketPosition );
    if ( !IsOpenBracketLexem(bracketLexem) ){
      ScriptException e = new ScriptException( "���������� ������: ���������� ������� �� �������� ����������� �������" );
      throw e;
    }
    // �������� ���������
    //����������, � ������� �������� ���������� ���������� ������ ����� ����������� ������, ���������� � ���������
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
   * ����� ��������� ������� ���� �������, ����������� ����� ��������� � �������� ��������� ������������,
   * ����������� � ����������
   * ������  ������ � ������ FParsedLexemList.
   * ����� ������ ����� ������ � ������ ������ ������ ��������� ��������� �� ������ ���� �� ����� �������-�������
   *
   * @param aStartPos - ��������� �������
   * @param aLastPos - �������� �������
   */
  private int ParseLexemFunctions( int aStartPos, int aLastPos ) throws ScriptException {
    /**��, ��� ������� ������� �������� ��������, ����������� �� �������������� ������ ������� ������
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
        //������� ������� - �������. ���������, ����� �� ����� �� ���������� ������� ������, ������������� ����
        // �������, ��� ������ ������� ��������� ��������� ��������, ������� ����� ����������� ��������
        matrixIndex = GetSvertkaIndex( currentPos );
        if ( matrixIndex == -1 ){
          //������, ��� ������ ������� ��������� ������� ��������.
          //�������� ������� ������� �������, � ������ ����, ��� ����� ������� ������� ����������� ������� ������� (
          //�.�. ���������� ������ � ����� ��������, ����� � �������� ������� �� ������� ����������� ������, �����
          // ��������� ���, ��� ����� ������� "�������" ����� �� ����������� ������� "�������". � ��� �����������
          // ���������
          //����������� ������� ���������� ����� ���������� ����������� ������ �������
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

  /**  ��������� ����������� ���� ������ - �������, ������� ���������
   * ����� ��������� �������� ������ (������ �������� ���������) � ��������� ��������
   * (��������� ������� �������).
   * ������ �������������� ������� � ��������� ������� ������������
   * �� �������� ������� ������������
   * @throws ScriptException
   */
  private void ProcessSvertkaBetweenPos(int aFirstPos, int aLastPos) throws ScriptException {
    if ( aFirstPos > aLastPos ) {
      ScriptException e = new ScriptException("��������� ������� ������� ������, ��� ��������");
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
        //����� ���������, �������� �� ������ ������� � ����� ������ ��������� ����
        //������ � ������ ��������
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
            //��������� ������� ������� ��������� ������, � ������� �����
            // ����������� �������
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
      /**����� ����� ���������, �� ����� �� �������� �� ������� ���������� ���
         * ��������� ������.
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
  	// �������� ������ ������� �������
  	int matrixPos = GetSvertkaIndex( aFirstPos );
  	if ( matrixPos < 0 ) {
      // �� ������� ������� � ������� ��� �������. ��� ��������, ��� ����� ��������� ������� ��������� ��������� �����������
  		FMovMode = MOV_MODE_AFTER;
  		ProcessSvertkaBetweenPos(aFirstPos, aMovPos-2);
  	}
  	FMovMode = MOV_MODE_BEFORE;
  	matrixPos = GetSvertkaIndex( aFirstPos );
  	if ( matrixPos < 0 ) {
  		throw new ScriptException("���������� ���������� �������� ��������� ��������");
  	}
  	ProcessSvertkaBetweenPos(aFirstPos, GetFirstLexemPos( aFirstPos, ":=" )-1);
  	//��������� ������� �������� ����� �������� �������������
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
   * ���������� ������� ������, � ������� ���� �������� �������������. ���� ����� ���� ��������� - � �����������
   * ���������������� ��������� ���������, � ������ ����� - �������� �������������.
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
      //����������� �������������
    	int i = aLastPos - ( beginLexemCount - FParsedLexemList.size() );
      FMovMode = MOV_MODE_BEFORE;
      if ( !isSetFunction(aFirstPos, i) ) {
         ProcessSvertkaBetweenPos( aFirstPos, i );
      } else {
      	ProcessSvertkaSetFunction(aFirstPos, i, firstMovPos);
      }
    }
    //��������� ������� ������� ";" �� ������ �������, � ���� ��� ����, �� ������
    // ������� ��� �������
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
      //e = new ScriptException("������ ������ �� �������-������");
      //throw e;
      return -1;
    }
    try {
      ScriptLexemProgramLink lexem = (ScriptLexemProgramLink)aLexem;
     // return  lexem.GetLinkId();
     return Integer.parseInt( lexem.GetLinkId() );
    } catch (Exception e1){
      e = new ScriptException("�� ������������� � ��������� ������");
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
      if ( lexem.GetLanguageName().equalsIgnoreCase("������") ) {
        FParsedLexemList.removeElementAt( currentPos );
      } else {
        currentPos++;
      }
      counter--;
    }
  }

  /** ���������� �� �������� ������, ������� ���������� � ������ ������ ���������� �������  ��������� ����.
   *
   * � ��������� ������ ����������� ������ �� ����, ������� ����������� ��� ������� ������ ������� ������ �������, �
   * ��� ���� ��������� ������ ������� �� �������������� �� ����� ���������������� ����������. � ����������  � ������
   * ������ �������� ����� ������������������ ������: ��������� ���������� � ������� ��������� ������ (";"). ��� ���
   * ������� ����� ������ ��������� �� ������ ������
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
        //��, ������� �������� ���������.
        //������ ���������, �� ��������� �� ��� �������
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

  /**��������� ��������� ������� ����� ���� �������, ���������� � ���������
   * �������� ���� ������ - �������� ������� � ��� ���� ��������� - ����������� �� ���������� ������. ���� ����������
   * ������ ����������� - ������, ��� ���� ������. ���� �� �� �����������, ������ - ���-�� �� ��������
   * @param aStartPos
   * @param aStopPos
   * @throws ScriptException ���������� ������������, ����� ���������� ������ �� ����������� ��� ������� ������
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
      //���������, ���������� �� ������� ��������� ������� � ������ ������. ���� ������� ���������� - ������
      // ������� ��������� � ��� ���� ���������
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
        ScriptException e = new ScriptException("������� ������ ����� ������");
        throw e;
      }
    }//while
  }

  /**��������� ������� ����� - �.�. ����� ��� ���������� ������, ����������� ��������� ";" "end"
   * ���� �������������� ��������� � �������� ���������, ����������� � ���������
   * @param aStartPos ��������� ������� �����
   * @param aStopPos �������� ������� �����. ��������� ���� �� ��������� ";", ���� �� "end", ���� �� ������� ���������
   * ���������
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
      stopPos = GetFirstLexemPos(startPos+1,"������");
      if ( stopPos == -1) {
        if ( initLexem == null )
        {//��� �� ������ ������ ����� �� �������� ������ �����
          SvertkaOneBlock(startPos, FParsedLexemList.size()-1);
          return;
        } else {
          e = new ScriptException("���������� ������ ");
          throw e;
        }
      } else {
        stopLexem = (ScriptLexem)FParsedLexemList.get(stopPos);
        if ( IslinkLexemEquals(initLexem, stopLexem) ) {
        	//����� ���������, ��������� ������� � ������� �� �������� �������� ������������
          SvertkaOneBlock(startPos+1, stopPos-1);
          stopPos = FParsedLexemList.indexOf( stopLexem );
          ClearMarkerLexem(startPos, stopPos);
          return;
        } else {
          //���������� ������� ��� ���� ������, ������ �� �����
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

  /**���������� �������.
   * ������������ ������ � ��������� ������� ������ � ����������
   * ��������� ProcessSvertkaOneString
   * ����� ����������� �� ��� ���, ���� ������ ������ �� ������ ������
   * @throws ScriptException
   */
  private void ProcessSvertka() throws ScriptException {
    if ( FParsedLexemList.size() == 0 ) {
      return;
    }
    ParseCaseOperators();
    ScriptLexem beginLexem = GetParsedLexem( 0 );
    ScriptLexem endLexem = GetParsedLexem( FParsedLexemList.size()-1 );
    if ( !beginLexem.GetLanguageName().equalsIgnoreCase("������") ) {
      ScriptException e;
      e = new ScriptException("����������� ������ �� ���������� � ��������� �������");
      throw e;
    }
    if ( !endLexem.GetLanguageName().equalsIgnoreCase("�����") ) {
      ScriptException e;
      e = new ScriptException("����������� ������ �� ������������� �������� ��������");
      throw e;
    }
    //PrintLexemList();
    Svertka(null,0);
    LinkProgram();
    //PrintProgramList();
  }

  /**��������� ���������, ������������ �� � ������ ������ �������-�������,
   * ������� ������������� �������� (����������) ��� ����������.
   * ���� ����� ������� ����, �� ����� ����� ������� � ������ �����������
   * ��� ��� ������� - "(" � ")"
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
  		throw new ScriptException("����������� ������"); 
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
  			throw new ScriptException("������������ ��������� ����� " + lexemCode);
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_VARIABLE && curLexemType == VAR_LEXEM_TYPE_VARIABLE_DIVIDER ) {
  			// ������� ����� �������� ����������
  			prevLexemType = 0;
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_VARIABLE && curLexemType == VAR_LEXEM_TYPE_TYPE_SEPARATOR ) {
  			prevLexemType = VAR_LEXEM_TYPE_TYPE_SEPARATOR;
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_VARIABLE ) {
  			throw new ScriptException("������������ ��������� ����� " + lexemCode);
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_TYPE_SEPARATOR && curLexemType == VAR_LEXEM_TYPE_TYPE) {
  			// ������ ��� ����������, ������� �� ����� � ������� ��� ��� ���������� � varList;
  			typeName = lexemCode;
  			prevLexemType = VAR_LEXEM_TYPE_TYPE;
  			//break;
  		}  else if ( prevLexemType == VAR_LEXEM_TYPE_TYPE_SEPARATOR ) {
  			throw new ScriptException("������������ ��������� ����� " + lexemCode);  			
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_TYPE && curLexemType == VAR_LEXEM_TYPE_STOP_VAR_SECTION) {
  			break;
  		} else if ( prevLexemType == VAR_LEXEM_TYPE_TYPE ) {
  			throw new ScriptException("������������ ��������� ����� " + lexemCode);  			
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
  			throw new ScriptException("����������� ��� ���������� " +  typeName);
  		}
  		Variables.AddVariable(newVar);  		
  	}
  	
  }
  
  private void parseVarSection3() throws ScriptException{
  	boolean f = true;
  	String lexemCode = null;
  	String prevLexemCode = FLexemTokenizer.GetNextLexem3();
  	if ( prevLexemCode ==null  ){
  		throw new ScriptException("������ ������ var");
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
  			throw new ScriptException("������ ������ var");
  		}  		
  	}
  	if ( typeName == null ) {
  		throw new ScriptException("����������� ��� ���������� ");
  	}
  	if ( varList.isEmpty() ) {
  		throw new ScriptException("����������� ���������� ");
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
  			throw new ScriptException("����������� ��� ���������� " +  typeName);
  		}
  		Variables.AddVariable(newVar);
  	}
  	
  }

  /**
    �������� ������� ���������:
    1. ������� ������ ����������� �� ������� (� �������� ��������� �� ������)
    2. ����� ������������ ������� ������������������� ������ (����� ��
      � ��������� �� ������)
    3. � �������� ������� ����������� ������������������ ������, �������
      ����� ��������� ����������� ����������
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
        ScriptLexem endLexem = GetLexem("�����");
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
  	// ������������� �������� ����������, ����� �� ��� �������� ���� � ������������� ������.
  	// ������� ��� ���������� ��������� ������� ��������� ����������
  	ExecutionContextLocator.setContect(FExecutionContext);
    ScriptException e;
    if ( FProgram.size() == 0 ) {
      //e = new ScriptException("������ ���������.");
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
        e = new ScriptException("������ �������������� �������: ��������� ScriptOperation, �������  " +
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
   * ���� ��������, ��������� � ���������� ������� ���������
   * *******************************************************************
   * *******************************************************************
   */

  private void AddOpenMarker(int aMarkerPos) throws ScriptException
  {
    FMarkerState.NewOperator(null);
    int i = FMarkerState.GetCurrentLinkId();
    ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 3 );
    lexem.SaveCode("������");
    FParsedLexemList.insertElementAt(lexem,aMarkerPos);
  }

  private void AddCloseMarker(int aMarkerPos) throws ScriptException {
    int i = FMarkerState.GetCurrentLinkId();
    /*if ( i == -1 ){
      return;
    }*/
    ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 3 );
    lexem.SaveCode("������");
    FParsedLexemList.insertElementAt(lexem,aMarkerPos);
    FMarkerState.DelCurrentOperator();
  }

  private void AddStringSeparator(int aPosition) throws ScriptException {
    ScriptLexemEndString endLexem = (ScriptLexemEndString)GetLexem(";");
    ScriptLexemEndString lexem = (ScriptLexemEndString)endLexem.clone();
    InsertLexemWithCheck(lexem, aPosition,3);
  }

  /**��������� ������������� ��� ���������� ����������� ��������� �������� �������
   *���������� ��������� ��������. ��������� ����������� � ��� ������, ���� ��� �� �����
   * ��������
   */
  private void SavePreviousState() {
   if ( FPreviousState != 1 )  {
  	 //����������� ������ � ��� ������, ���� ���������� ��������� - �� ���������
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
      case 1:{  //��������� ���������
        //��������� ������������� ��������� ��������� ��������
        if ( !FStateStack.empty() ){
          Integer i = (Integer) FStateStack.pop();
          FCurrentState = i;
          result = 0;
        }
        break;
      }
        //��������� ����� ��������� ������� IF
        case 2:{
          FAutomatState.NewOperator( aCurrentLexem );
          //������� ������� ��������� �������� JT
          ScriptLexem etalonLexem = GetLexem("jnt");
          ScriptLexem newLexem = (ScriptLexem) etalonLexem.clone();
          newLexem.SaveCode("jnt");
          //������ ������� if �� ������� ��������� �������� jt
          ChangeLexemInParsedList(aLexemIndex, newLexem);
          //��������� ������ ����� IF
          AddOpenMarker(aLexemIndex);
          //��������� ������ ����� IF �� ������, ���� � ������� - ���������, ����������� � �������
          AddOpenMarker(aLexemIndex+2);
          result = 2;
          SavePreviousState();
          break;
        }
        //��������� ����� ��������� ������� THEN
        //���� ���� BEGIN, ���� ������ ���������
        case 3:{
          //������� �������-������
          int i = FAutomatState.GetCurrentLinkId();
          if ( i == -1 )
          {
            e = new ScriptException("���������� �������� ����� ��������");
            throw e;
          }
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 1 );
          //������ THEN �� �������-������
          ChangeLexemInParsedList(aLexemIndex, lexem);
          //��������� ����������� ������ ����� THEN, �� ���� ����� �� ����� ��������� � �������
          AddCloseMarker(aLexemIndex);
          //��������� ����������� ������ ����� THEN
          AddCloseMarker(aLexemIndex+2);
          result = 2;
          break;
        }
        // �������� BEGIN ��������� ����� "if XXX then "
        case 4:{
          //��������� ����� BEGIN ����������� ������
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        // �������� ���� �������, ���� ������� ����� "if XXX then"
        case 5:{
          //��������� ����������� ������ ����� ��������
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //�������� ";" ����� "if XXX then XXX"
        case 6:{
          //��������� ����� ; ����������� ������
          AddCloseMarker( aLexemIndex + 1 );
          //������ ����� ����� ";"
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex + 2, 3 );
          FAutomatState.DelCurrentOperator();
          result = 2;
          break;
        }
        //�������� "end" ����� "if XXX then begin XXXXX"
        //���� ���� ";", ���� "else"
        case 7:{
          //��������� ����������� ������ ����� end
          AddCloseMarker(aLexemIndex+1);
          result = 1;
          break;
        }
        //�������� "else" ����� "if XXX begin XXXXX end"
        //��������!! ������� ����� �� �������������.
        case 8:{
          //��������� ������� ������ �� ����� ���������
          int oldLink = FAutomatState.GetCurrentLinkId();
          FAutomatState.DelCurrentOperator();
          //������� ����� ������ � ����� ���������
          // � ������ "else" �� ������ �����
          FAutomatState.NewOperator( aCurrentLexem );
          int newLink = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink newLinkLexem = new ScriptLexemProgramLink( Integer.toString(oldLink), 2 );
          ChangeLexemInParsedList(aLexemIndex, newLinkLexem);
          //��������� ����� ����� ";"
          AddStringSeparator(aLexemIndex+1);
          //��������� ������� �������� �� ��� �������������� ������ ����� ��������� ����� else
          //������� �������������� ����� else
          AddStringSeparator(aLexemIndex-1);
          AddJumpCommand(aLexemIndex, newLink);
          result = 5;
          break;
        }
        // �������� ������� ���� ������� ����� "if  begin XXXXX end else"
        //���� ";"
        case 9:{
          // ��������� ����������� ������ ����� ��������
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //�������� BEGIN ����� "��� else "
        case 10:{
          //��������� ����������� ������ ����� BEGIN
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //�������� ";" ����� "if  begin XXXXX end else XXX"
        case 11:{
          //��������� ����� ";" �������-�����
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex+1,3);
          //��������� ";" ����� �����
          AddStringSeparator( aLexemIndex+2 );
          FAutomatState.DelCurrentOperator();
          //��������� ����� ; ����������� ������
          AddCloseMarker(aLexemIndex+3);
          result = 3;
          break;
        }
        //�������� "end" ����� "else begin XXX"
        case 12:{
          //��������� ����� end ����������� ������
          AddCloseMarker(aLexemIndex+1);
          //��������� ����� "end" �����
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex,3);
          FAutomatState.DelCurrentOperator();
          AddStringSeparator( aLexemIndex+1 );
          result = 1;
          break;
        }
        // �������� ";" ����� if XXX begin XXXXX end
        case 13:{
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex+1,3);
          //������� ������� �������� �� ������� �����
          FAutomatState.DelCurrentOperator();
          AddStringSeparator(aLexemIndex+2);
          result = 0;
          break;
        }
        //�������� ";" ����� "else begin XXX end"
        case 14:{

          result = 0;
          break;
        }
        //�������� "else" ����� "if XXX then XXXX "
        case 15:{
          //������ "else" �� ������� �����
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          ChangeLexemInParsedList(aLexemIndex, lexem);
          //��������� ";" ����� �����
          AddStringSeparator(aLexemIndex+1);
          FAutomatState.DelCurrentOperator();
          //��������� ����� "else" ������� ������������ ��������
          FAutomatState.NewOperator( lexem );
          i = FAutomatState.GetCurrentLinkId();
          AddStringSeparator(aLexemIndex);//��������� ����� else ";"
          AddJumpCommand(aLexemIndex+1, i);
          //���������� ������������ ������� ����� ������� JMP
          AddCloseMarker( aLexemIndex+4 );
          result = 6;
          break;
        }
        // �������� WHILE
        case 16:{
          SavePreviousState();
          //��������� ����� ����� while
          FAutomatState.NewOperator( aCurrentLexem );
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex,1);
          AddStringSeparator( aLexemIndex+1);
          //������ while �� JNT
          ScriptLexem etalonLexem = GetLexem("jnt");
          ScriptLexem newLexem = (ScriptLexem) etalonLexem.clone();
          newLexem.SaveCode("jnt");
          ChangeLexemInParsedList(aLexemIndex+2, newLexem);
          //��������� ������ ����� JNT
          AddOpenMarker( aLexemIndex + 2 );
          //��������� ������ ����� JNT
          AddOpenMarker( aLexemIndex + 4 );
          result = 4;
          break;
        }
        // �������� DO ����� "while XXX "
        case 17:{
          //��������� ����������� ������ ����� DO
          AddCloseMarker( aLexemIndex );
          //������ DO �� ����� ��������
          FAutomatState.NewOperator( aCurrentLexem );
          int i = FAutomatState.GetCurrentLinkId();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 1 );
          ChangeLexemInParsedList(aLexemIndex+1, lexem);
          AddStringSeparator(aLexemIndex+2);//��������� ";" ����� ������
          AddCloseMarker( aLexemIndex+3);
          result = 3;
          break;
        }
        //�������� begin ����� "while XXX do "
        case 18:{
          //��������� ����������� ������ ����� BEGIN
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //�������� ������� ���� ������� �����  "while XXX do "
        case 19:{
          AddOpenMarker( aLexemIndex );
          result = 1;
          break;
        }
        //�������� "end" "while XXX do begin XXX "
        case 20:{
          //AddCloseMarker( aLexemIndex +1 );
          //��������� ����� END �����, �� ������� ����� ���������� ��������� � ������ ������ �� �����
          int i = FAutomatState.GetCurrentLinkId();
          FAutomatState.DelCurrentOperator();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex+1,1);
          AddStringSeparator( aLexemIndex+2 );
          //��������� ������� JUMP ����� END
          i = FAutomatState.GetCurrentLinkId();
          AddJumpCommand( aLexemIndex, i);
          FAutomatState.DelCurrentOperator();
          //��������� ����������� ������ ����� �������� JUMP � END
          AddCloseMarker( aLexemIndex +3 );
          result = 6;
          break;
        }
        //�������� ";" ����� "while XXX do XXX"
        case 21:{
          //��������� ����� END �����, �� ������� ����� ���������� ��������� � ������ ������ �� �����
          int i = FAutomatState.GetCurrentLinkId();
          FAutomatState.DelCurrentOperator();
          ScriptLexemProgramLink lexem = new ScriptLexemProgramLink( Integer.toString(i), 2 );
          InsertLexemWithCheck( lexem, aLexemIndex+1,1);
          AddStringSeparator( aLexemIndex+2 );
          //��������� ������� JUMP ����� END
          i = FAutomatState.GetCurrentLinkId();
          AddStringSeparator( aLexemIndex );// ������ ";" ����� JUMP
          AddJumpCommand( aLexemIndex+1, i);
          FAutomatState.DelCurrentOperator();
          //��������� ����������� ������ ����� �������� JUMP � END
          AddCloseMarker( aLexemIndex +4 );
          result = 7;
          break;
        }
        case 22:{ //����� if XXX begin XXX end �� ��������� else � �� ���������� ;. ��� ��� ��� ������ ����������
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
          e = new ScriptException("����������� ��������� �������� ������� ������ - " + Integer.toString( FCurrentState ));
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
      	//����� ������ ��������� ��������
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
      { //����� ������ ��������� ��������
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
        //�������� ��� ������ ���������
        s = ScriptLanguageDef.Automat[automatIndex][2];
        FPreviousState = FCurrentState;
        FCurrentState = Integer.parseInt( s );
        try{
        i = i + AutomatDoSomethingAfterIn( lexem,i );
        } catch (ScriptException e)
        {
           ScriptException e1 = new ScriptException("������: " + e.getMessage() + " " +
                   "������� " + Integer.toString(i));
          throw e1;
        }
        //���������, ����� �� ����� ���������� � ������ ���������
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
      ScriptException e = new ScriptException("������� �������� � ������ ������ ���������� �����");
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

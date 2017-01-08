package mp.parser;

import mp.utils.ServiceLocator;
import mp.utils.NameService;

/**
 * User: atsv
 * Date: 05.04.2006
 */
public  class Operand  extends ScriptProgramObject {

  public static final int OPERAND_TYPE_INTEGER = 1;
  public static final int OPERAND_TYPE_REAL = 2;
  public static final int OPERAND_TYPE_BOOLEAN = 3;
  public static final int OPERAND_TYPE_STRING = 4;
  public static final int OPERAND_TYPE_ARRAY = 5;

  private int FIntOperandValue = 0;
  private float FFloatOperandValue = 0;
  private boolean FBooleanOperandValue = false;
  private String FStringOperandValue = null;

  private boolean isIntAssigned = false;
  private boolean isFloatAssigned = false;
  private boolean isBooleanAssigned = false;
  private boolean isStringAssigned = false;
  private int FNameIndex = -1;
  private NameService FNames = ServiceLocator.GetNamesList();
  private String FTypeName = null;
  private int FType = 0;
  private ScriptParser FParser = null;

  protected boolean IsValueAssigned()  {
    return (isFloatAssigned || isIntAssigned || isBooleanAssigned || isStringAssigned);
  }

  protected final void InitIntOperand(int aOperandValue){
    if ( isIntAssigned ) {
      FIntOperandValue = aOperandValue;
      return;
    }
    FIntOperandValue = aOperandValue;
    FFloatOperandValue = 0;
    FStringOperandValue = null;
    FBooleanOperandValue = false;
    isIntAssigned = true;
    isFloatAssigned = false;
    isBooleanAssigned = false;
    isStringAssigned = false;
    FTypeName = "integer";
    FType = OPERAND_TYPE_INTEGER;
  }

  protected final void InitFloatOperand(float aOperandValue )  {
    if ( isFloatAssigned ){
      FFloatOperandValue = aOperandValue;
      return;
    }
    FIntOperandValue = 0;
    FFloatOperandValue = aOperandValue;
    FBooleanOperandValue = false;
    FStringOperandValue = null;
    isIntAssigned = false;
    isFloatAssigned = true;
    isBooleanAssigned = false;
    isStringAssigned = false;
    FTypeName = "real";
    FType = OPERAND_TYPE_REAL;
  }

  protected final void InitBooleanOperand(boolean aOperandValue) {
    if ( isBooleanAssigned ){
      FBooleanOperandValue = aOperandValue;
      return;
    }
    FIntOperandValue = 0;
    FFloatOperandValue = 0;
    FStringOperandValue = null;
    FBooleanOperandValue = aOperandValue;
    isIntAssigned = false;
    isFloatAssigned = false;
    isBooleanAssigned = true;
    isStringAssigned = false;
    FTypeName = "boolean";
    FType = OPERAND_TYPE_BOOLEAN;
  }

  protected final void InitStringOperand( String aOperandValue ){
    FIntOperandValue = 0;
    FFloatOperandValue = 0;
    FBooleanOperandValue = false;
    FStringOperandValue = aOperandValue;
    isIntAssigned = false;
    isFloatAssigned = false;
    isBooleanAssigned = false;
    isStringAssigned = true;
    FTypeName = "string";
    FType = OPERAND_TYPE_STRING;
    if ( "".equalsIgnoreCase( aOperandValue ) || aOperandValue == null){
      FStringOperandValue = "";
      return;
    }
    String s = aOperandValue.substring(0, 1);
    if ( "\"".equalsIgnoreCase( s ) ) {
      FStringOperandValue = aOperandValue.substring(1, aOperandValue.length());
    }
    s = aOperandValue.substring(aOperandValue.length()-1, aOperandValue.length());
    if ( "\"".equalsIgnoreCase( s ) ) {
      FStringOperandValue = FStringOperandValue.substring(0, FStringOperandValue.length() - 1);
    }

  }

  protected final void InitArray(){
    FType = OPERAND_TYPE_ARRAY;
  }

  public Operand(){
    InitIntOperand( 0 );
  }

  public Operand(int aOperandValue)  {
    InitIntOperand(aOperandValue);
  }

  public Operand( float aOperandValue ) {
    InitFloatOperand( aOperandValue );
  }

  public Operand(boolean aOperandValue) {
    InitBooleanOperand( aOperandValue );
  }

  public Operand( String aOperandValue ){
    InitStringOperand( aOperandValue );
  }

  public void SetValue(int aOperandValue) {
    InitIntOperand(aOperandValue);
  }

  public void SetValue(boolean aOperandValue) {
    InitBooleanOperand( aOperandValue );
  }

  public void SetValue(float aOperandValue) {
    InitFloatOperand( aOperandValue );
  }

  public void SetValue( String aOperandValue ){
    InitStringOperand( aOperandValue );
  }


  public void SetStringValueWithConvertToOperandType( String aValue ) throws ScriptException{
    if ( aValue == null ){
      return;
    }
    switch (FType) {
      case OPERAND_TYPE_INTEGER:{
        int i = 0;
        try {
          i = Integer.parseInt( aValue );
          InitIntOperand( i );
        } catch ( Exception e ) {
          ScriptException e1 = new ScriptException( "Ќевозможно преобразовать значение \"" + aValue + "\" к целочисленному типу" );
          throw e1;
        }
        return;
      }
      case OPERAND_TYPE_REAL:{
        double d;
        try {
          d = Double.parseDouble( aValue );
          InitFloatOperand((float) d);
        } catch ( Exception e ) {
          ScriptException e1 = new ScriptException( "Ќевозможно преобразовать значение \"" + aValue + "\" к вещественному типу" );
          throw e1;
        }
        return;
      }
      case OPERAND_TYPE_BOOLEAN:{
        boolean b;
        try {
          b = Boolean.parseBoolean( aValue );
          InitBooleanOperand( b );
        } catch ( Exception e ) {
          ScriptException e1 = new ScriptException( "Ќевозможно преобразовать значение \"" + aValue + "\" к логическому типу" );
          throw e1;
        }
        return;
      }
      case OPERAND_TYPE_STRING:{
        InitStringOperand( aValue );
        return;
      }
      case OPERAND_TYPE_ARRAY:{
        ScriptException e = new ScriptException( "Ќевозможно преобразовать значение \"" + aValue + "\" в массив" );
        throw e;
      }
    }

  }

  public int GetIntValue() throws ScriptException {
    if ( isIntAssigned )
    {
      return FIntOperandValue;
    }
    if ( isFloatAssigned )
    {
      return (int)FFloatOperandValue;
    }
    ScriptException e = new ScriptException("Ќе преобразовать " +
                     FTypeName + " в integer");
    throw e;
  }

  public float GetFloatValue() throws ScriptException {
    if ( isFloatAssigned )
    {
      return FFloatOperandValue;
    }
    if ( isIntAssigned )
    {
      return (float)FIntOperandValue;
    }
    ScriptException e = new ScriptException("Ќе преобразовать " +
                      FTypeName + " в real");
    throw e;
  }

  public boolean GetBooleanValue()  {
    if ( isBooleanAssigned )
    {
      return FBooleanOperandValue;
    } else
      return false;
  }

  protected static String DoubleToString( String aName, double aValue ){
    int intPart = (int) aValue;
    String res;
    if ( intPart == 0 && aValue < 0 ) {
      res = "-0.";
    } else {
      res = Integer.toString( intPart ) + ".";
    }
    intPart = Math.abs( (int)((intPart - aValue) * 10000) );
    if ( intPart > 1000 ){
      res = res + Integer.toString( intPart );
    } else {
      if ( intPart > 100 ){
       res = res + "0" + Integer.toString( intPart );
      } else {
        if ( intPart > 10 ) {
          res = res + "00" + Integer.toString( intPart );
        } else {
            res = res + "000" + Integer.toString( intPart );
        }
      }
    }
    if ( ( aName != null ) && !ScriptLanguageDef.IsServiceName( aName ) ) {
        return aName + " " + res;
    } else {
       return  res;
    }
  }

  public String GetStringValue() throws ScriptException{
    if ( isStringAssigned ){
      return FStringOperandValue;
    }
    if ( isIntAssigned ){
      return Integer.toString( FIntOperandValue );
    }
    if ( isFloatAssigned ){
      int intPart = (int) FFloatOperandValue;
      String res;
      int floatPart = (int)(Math.abs( FFloatOperandValue - intPart ) * 1000);
      if ( intPart == 0 && FFloatOperandValue < 0 ) {
        res = "-0.";
      } else {
        res = Integer.toString( intPart ) + ".";
      }
      if ( floatPart < 10 ) {
        res = res + "00" + Integer.toString( floatPart );
        return res;
      }
      if ( floatPart < 100 ){
        res = res +  "0" + Integer.toString( floatPart );
        return res;
      }
      if ( floatPart < 100 ){
        res = res +  Integer.toString( floatPart );
        return res;
      }
      return res + Integer.toString( floatPart );
      //return Double.toString( FFloatOperandValue );
    }
    if ( isBooleanAssigned ){
      if ( FBooleanOperandValue ) return "true"; else return "false";
    }
    ScriptException e = new ScriptException("Ќе получить строковое значение дл€ операнда ");
    throw e;
  }

  public Object GetObject()
  {
    if ( isIntAssigned )
    {
      return new Integer( FIntOperandValue );
    }
    if ( isFloatAssigned )
    {
      return new Float( FFloatOperandValue );
    }
    if ( isBooleanAssigned )
    {
      return new Boolean( FBooleanOperandValue );
    }
    if ( isStringAssigned ) {
      return FStringOperandValue;
    }
    return null;
  }

  public String GetTypeName() {
    return FTypeName;
  }

  public int GetType(){
    return FType;
  }

  public static int GetTypCodeByName( String aTypeName ){
    if ( aTypeName == null || "".equalsIgnoreCase( aTypeName ) ){
      return -1;
    }
    if ( "integer".equalsIgnoreCase( aTypeName ) ){
      return OPERAND_TYPE_INTEGER;
    }
    if ("real".equalsIgnoreCase( aTypeName )){
      return OPERAND_TYPE_REAL;
    }
    if ( "boolean".equalsIgnoreCase( aTypeName ) ){
      return OPERAND_TYPE_BOOLEAN;
    }
    if ( "string".equalsIgnoreCase( aTypeName ) ){
      return OPERAND_TYPE_STRING;
    }
    if ( "array".equalsIgnoreCase( aTypeName ) ){
      return OPERAND_TYPE_ARRAY;
    }
    return -1;
  }

  public boolean IsAutoCastEnabled( String  aAnotherType){
    if ( isIntAssigned && aAnotherType.equalsIgnoreCase("real") ){
      return true;
    }
    if ( isFloatAssigned  && aAnotherType.equalsIgnoreCase("integer")){
      return true;
    }
    return false;
  }

  private boolean equalsInt(Operand aVariable){
    try{
      return ( this.GetIntValue() == aVariable.GetIntValue() );
    } catch (ScriptException e){
      return false;
    }
  }

  private boolean equalsReal(Operand aVariable){
    try{
      return ( this.Compare(aVariable) == 0 );
    } catch (ScriptException e){
      return false;
    }
  }

  private boolean equalsBoolean(Operand aVariable){
    return ( this.GetBooleanValue() == aVariable.GetBooleanValue() );
  }

  private boolean equalsString( Operand aVariable ){
    if ( aVariable == null ) {
      return false;
    }
    if ( FType == OPERAND_TYPE_STRING && aVariable.GetType() ==  OPERAND_TYPE_STRING ){
      try {
        return FStringOperandValue.equalsIgnoreCase( aVariable.GetStringValue() ) ;
      } catch (ScriptException e) {
        return false;
      }
    }
    return false;
  }

  public boolean equals( Operand aVariable ){
    if ( aVariable == null ) return false;
    //сравниваютс€ только однотипные значени€
    if ( !aVariable.GetTypeName().equalsIgnoreCase( this.GetTypeName() ) ) return false;
    //сравниваем значени€
    switch ( FType ){
      case OPERAND_TYPE_INTEGER: {
       return equalsInt( aVariable );
    }
      case OPERAND_TYPE_REAL:{
        return equalsReal( aVariable );
      }
      case OPERAND_TYPE_BOOLEAN: {
        return equalsBoolean( aVariable );
      }
      case OPERAND_TYPE_STRING:{
        return equalsString( aVariable );
      }
    }
    return false;
  }

  private boolean IsComparableTypes(String anotherType){
    if ( anotherType == null ){
      return false;
    }
    if ( "string".equalsIgnoreCase( GetTypeName() ) && !"string".equalsIgnoreCase( anotherType ) ){
      return false;
    }
    if ( "boolean".equalsIgnoreCase( GetTypeName() ) && !"boolean".equalsIgnoreCase( anotherType ) ){
      return false;
    }
    return true;
  }

  /** ‘ункци€ сравнивает значение, содержащеес€ в данном объекте со значением переменной, переданной в параметре
   *
   * @param aVariable - переменна€, с которой производитс€ сравнение
   * @return 0 - если значени€ равны, 1 - если значение в объекте больше, чем значение в параметре, -1 - если
   * значение в параметре больше, чем в объекте
   * @throws - возникает, если производитс€ попытка сравнить несравниваемые типы, либо если в метод передано пустое
   * значение
   */
  public int Compare( Operand aVariable ) throws ScriptException{
    if ( aVariable == null ){
      ScriptException e = new ScriptException("ѕопытка передать пустое значение в процедуру сравнени€ переменных");
      throw e;
    }
    if ( !IsComparableTypes( aVariable.GetTypeName() ) ){
      ScriptException e = new ScriptException("ѕопытка сравнивать несравниваемые значени€ " + GetTypeName() + " и " +
              aVariable.GetTypeName());
      throw e;
    }
    if ( "integer".equalsIgnoreCase( GetTypeName() ) && "integer".equalsIgnoreCase( aVariable.GetTypeName() ) ){
      if (GetIntValue() == aVariable.GetIntValue()) {
        return 0;
      }
      if ( GetIntValue() > aVariable.GetIntValue() ){
        return 1;
      } else {
        return -1;
      }
    }
    if ( "real".equalsIgnoreCase( GetTypeName() ) || "real".equalsIgnoreCase( aVariable.GetTypeName() ) ){
      return ServiceLocator.CompareDouble( GetFloatValue(),  aVariable.GetFloatValue() );
    }
    if ( "boolean".equalsIgnoreCase( GetTypeName() ) && "boolean".equalsIgnoreCase( aVariable.GetTypeName() ) ){
      if ( GetBooleanValue() == aVariable.GetBooleanValue() ) {
        return 0;
      } else {
        return 1;
      }
    }
    return 0;
  }

  public String toString(){
    String name = GetName();
    if ( name == null ){
      name = "";
    }
    if ( isIntAssigned )
    {
      if ( !ScriptLanguageDef.IsServiceName( name ) ) {
        return name + " " + Integer.toString( FIntOperandValue );
      } else {
        return Integer.toString( FIntOperandValue );
      }
    }
    if ( isFloatAssigned )
    {
      return DoubleToString( name, FFloatOperandValue );
    }
    if ( isBooleanAssigned )
    {
      if (FBooleanOperandValue) return name + " " + "true"; else return name + " " + "false";
    }
    if ( isStringAssigned ){
      if ( !ScriptLanguageDef.IsServiceName( name ) ) {
        return name + " " + FStringOperandValue;
      } else {
        return FStringOperandValue;
      }
    }
    return super.toString();
  }

  public String GetName() {
    return FNames.GetName( FNameIndex );
  }

  public void SetName(String name) {
    FNameIndex = FNames.GetNameIndex( name );
    //FNameIndexObj = new Integer( FNameIndex );
  }

  public int GetNameIndex(){
    return FNameIndex;
  }

  private void SetExecutor(ScriptParser parser) {
  	FParser = parser;
  }

  private ScriptParser GetExecutor(){
  	return FParser;
  }


}//class

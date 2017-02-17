package mp.parser;

public class ScriptDigitLexem extends ScriptLexem {


  /**если 1 - то целое, если 2 - то float
   * если 3 - то boolean, если 4 - то string
   */
  int FDigitType = 0;

  public ScriptDigitLexem()
  {
   super();
   try
   {
     this.SetLanguageName("Число");
   } catch(ScriptException e){}

  }

  public boolean IsLexemEquals(ScriptLexem aLexem)
  {
    if ( FLanguageName.equalsIgnoreCase(aLexem.GetLanguageName())  )
    {
      return true;
    }

    return false;
  }

  /**
    Функция проверяет, может ли переданная строка буть лексемой,
    за которую отвечает данный объект - т.е. быть цифровым значением.
    Проверка осуществляется путем попытки преобразовать переданную строку
    сначала в целое число, а затем в дробное
  */
  public boolean IsMyToken(String aTokenName)  {

    float r = 0;
    boolean f = false;
    Boolean f1;
    String s;
    try  {
       r = Integer.parseInt( aTokenName );
       f = true;
       FDigitType = 1;
      return f;
    }
    catch(Exception e) {
      try  {
        r = (float)Double.parseDouble( aTokenName );
        f = true;
        FDigitType = 2;
        return f;
      } catch(Exception e1)  {
        s = aTokenName.trim();
        if (  s.equalsIgnoreCase("true")  || s.equalsIgnoreCase("false")) {
          FDigitType = 3;
          return true;
        } else{
          //проверяем, не string ли это        	
          s = aTokenName.substring(0, 1);
          if ( "\"".equalsIgnoreCase( s ) ) {
            s = aTokenName.substring( aTokenName.length() - 1, aTokenName.length() );
            if ( "\"".equalsIgnoreCase( s ) ) {
              FDigitType = 4;
              return true;
            } else return false;
          } else
          return false;
        }
          //return false;
      }
    }
    //return f;
  }

  public ScriptLexem GetProducedLexem(int aLexemIndex) {
    return null;
  }

  public Object clone() {
    ScriptDigitLexem newLexem = new ScriptDigitLexem();
    newLexem.FDigitType = FDigitType;
    return newLexem;
  }


  public Object GetExecutableObject() {
    IsMyToken( FCodePart );
    switch (FDigitType) {
      case 1:{
        int i = Integer.parseInt( FCodePart );
        return new ScriptConstant( i );
      }
      case 2:{
        float f = (float)Double.parseDouble( FCodePart );
        return new ScriptConstant( f );
      }
      case 3:{
        boolean f1 = true;
        if ( FCodePart.equalsIgnoreCase( "false" ) )
        {
          f1 = false;
        }
        return new ScriptConstant( f1 );
      }
      case 4:{
        return new ScriptConstant( FCodePart );
      }
    }//switch
    return new ScriptConstant(7);
  }

  public String toString(){
    return "digit " + FCodePart;
  }

}//class

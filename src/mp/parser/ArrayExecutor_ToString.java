package mp.parser;

/**
 * User: Администратор
 * Date: 10.07.2008
 */
public class ArrayExecutor_ToString extends ArrayExecutor {
  private String FResult;
  private int FRecordLength = 5;

  public void ExecIntOperation(int[] array) {
    int value;
    int i = 0;
    String s = "";
    while ( i < array.length ){
      value = array[i];
      s = Integer.toString( value );
      //дополняем слева пробелами до одинаковой длины
      while ( s.length() < FRecordLength ){
        s = " " + s;
      }
      i++;
      FResult = FResult + s;
    }
    FResult = FResult + "\n";

  }

  public void ExecFloatOperation(double[] array) {
    double value;
    int intPart;
    int floatPart;
    String s;
    int i = 0;
    int length = array.length;
    while ( i < length ){
      value = array[i];
      s = Operand.DoubleToString( null, value );
      /*intPart = (int) value;
      floatPart = (int)(Math.abs( value - intPart ) * 100);
      if ( intPart == 0 && value < 0 ) {
        s =  "-0." + Integer.toString( floatPart );
      } else {
        s = Integer.toString( intPart ) + "." + Integer.toString( floatPart );
      }
      */
      while ( s.length() < (FRecordLength+3) ){
        s = " " + s;
      }
      i++;
      FResult = FResult + s;
    }
    FResult = FResult + "\n";
  }

  public void ExecBooleanOperation(boolean[] array) {
  }

  public void ExecStringOperation(String[] array) {
  }

  public void  SetResultString( String aResultString ){
    FResult = aResultString;
  }

  public void SetMaxIntValue( int aValue ){
    String s = Integer.toString( aValue );
    if ( s.length() <= 5 ){
      FRecordLength = 5;
    } else {
      FRecordLength = s.length() + 2;
    }
  }

  public String GetResult(){
    return FResult;
  }

}

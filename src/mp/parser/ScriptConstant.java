package mp.parser;


/**
 * User: atsv
 * Date: 24.04.2006
 * Time: 20:55:46
 */
public class ScriptConstant extends Operand {

  public ScriptConstant(int aOperandValue) {
    super(aOperandValue);
  }

  public ScriptConstant(float aOperandValue) {
    super(aOperandValue);
  }

  public ScriptConstant(boolean aOperandValue) {
    super(aOperandValue);
  }

  public ScriptConstant( String aOperandValue){
    super( aOperandValue );
  }

  public void SetValue(int aOperandValue)
  {
    if ( !IsValueAssigned() )
    {
      super.SetValue(aOperandValue);
    }
  }

  public void SetValue(boolean aOperandValue){
    if ( !IsValueAssigned() )
    {
      super.SetValue(aOperandValue);
    }
  }

  public void SetValue(float aOperandValue)
  {
    if ( !IsValueAssigned() )
    {
      super.SetValue(aOperandValue);
    }
  }

}

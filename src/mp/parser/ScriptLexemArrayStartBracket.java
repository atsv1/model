package mp.parser;

public class ScriptLexemArrayStartBracket extends ScriptLexem {
	private boolean getModeFlag = false;

	public ScriptLexemArrayStartBracket(){
		super();
		FLanguageName = "[";
	}

	@Override
	public boolean IsLexemEquals(ScriptLexem aLexem) {
		if ( aLexem == null ) {
			return false;
		}
		if ( aLexem instanceof ScriptLexemArrayStartBracket ){
			return true;
		}
		return false;
	}

	@Override
	public boolean IsMyToken(String aTokenName) {
		if ("[".equals(aTokenName) ) {
			return true;
		}
		return false;
	}

	@Override
	public Object GetExecutableObject() throws ScriptException {
		ScriptOperationFunction result = new ScriptOperationFunction( ScriptLanguageDef.GetArrayGetFunction() );		
		getModeFlag = true;
  	return result;
	}

	public Object GetExecutableObject_BeforeMov() throws ScriptException{
		ScriptOperationFunction result = new ScriptOperationFunction( ScriptLanguageDef.GetArraySetFunction() );		
		getModeFlag = false;
  	return result;
  }

	public Object clone(){
		ScriptLexemArrayStartBracket result = new ScriptLexemArrayStartBracket();
		result.FCodePart = "[";
		return result;
	}

	public boolean IsNewOperandNeed() {
    return getModeFlag;
  }


}

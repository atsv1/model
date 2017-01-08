package mp.parser;

public class ScriptLexemArrayFinishBracket extends ScriptLexem {

	public ScriptLexemArrayFinishBracket(){
		super();
		FLanguageName = "]";
	}

	@Override
	public boolean IsLexemEquals(ScriptLexem aLexem) {
		if ( aLexem == null ) {
			return false;
		}
		if ( aLexem instanceof ScriptLexemArrayFinishBracket ){
			return true;
		}
		return false;

	}

	@Override
	public boolean IsMyToken(String aTokenName) {
		if ("]".equals(aTokenName) ) {
			return true;
		}
		return false;
	}

	@Override
	public Object GetExecutableObject() throws ScriptException {
		return null;
	}

	public Object clone(){
		ScriptLexemArrayFinishBracket result = new ScriptLexemArrayFinishBracket();
		result.FCodePart = "]";
		return result;
	}



}

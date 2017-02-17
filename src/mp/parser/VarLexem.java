package mp.parser;

public class VarLexem extends ScriptLexem {

	@Override
	public boolean IsLexemEquals(ScriptLexem aLexem) {		
		return (aLexem != null && (aLexem instanceof VarLexem));
	}

	@Override
	public boolean IsMyToken(String aTokenName) {		
		return "var".equalsIgnoreCase(aTokenName);
	}

	@Override
	public Object GetExecutableObject() throws ScriptException {		
		return null;
	}
	
	public Object clone(){
		return new VarLexem();
		
	}

}

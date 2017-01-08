package mp.parser;

public class ExecutionContext {

	private String FContextName = null;

	public ExecutionContext(String aContextName){
		FContextName = aContextName;
	}

	public String getContextName(){
		return FContextName;
	}

}

package mp.parser;

public interface ExternalFunction {
	
	public String getName();
	
	public int getParamCount();
	
	public void execute(Object...functionParams );

}

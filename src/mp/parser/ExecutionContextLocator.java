package mp.parser;


public class ExecutionContextLocator {
	private static ThreadLocal<ExecutionContext> contexts = new ThreadLocal<ExecutionContext>();
	private static ExecutionContext lastContext;

	public static void setContect(ExecutionContext context){
		if ( context == null ) {
			return;
		}
		contexts.set(context);
		lastContext = context;
	}

	public static ExecutionContext getContext(){
		ExecutionContext ec = contexts.get();
		if ( ec == null ) {
			ec = lastContext;
		}
		return ec;
	}

}

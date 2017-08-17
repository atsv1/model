package mp.elements;

import java.util.HashMap;
import java.util.Map;

public class BuildContext {
	
	private static ThreadLocal<BuildContext> contexts = new ThreadLocal<BuildContext> ();
	private Map<String, ModelConstant> constants = new HashMap<String, ModelConstant> (); 
	
	private BuildContext(){		
	}
	
	public static BuildContext getBuildContext(){
		synchronized(contexts) {
			BuildContext instance = contexts.get();
			if (instance == null) {
				instance = new BuildContext();
				contexts.set(instance);				
			}
			return instance;
		}		
	}
	
	public void addConstant(ModelConstant constant)  throws ModelException{
		if (constant == null) {
			return;
		}
		String name = constant.GetName();
		if ( name == null || "".equals(name) ) {
			throw new ModelException("Константа без имени");
		}
		if ( constants.containsKey(name.toUpperCase()  ) ) {
			//throw new ModelException("такая константа уже существует: " + name);
		}
		constants.put(name.toUpperCase(), constant);		
	}
	
	public ModelConstant getConstant(String name){
		return constants.get(name.toUpperCase());		
	}
	
	public String getConstantValue(String name){
		ModelConstant c = constants.get(name.toUpperCase());
		if (c == null) {
			return null;
		}
		return c.GetConstantStringValue();
	}

}

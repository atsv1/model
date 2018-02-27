package mp.elements.functions;

import java.io.PrintStream;

import mp.elements.ModelBlock;
import mp.elements.ModelElement;
import mp.elements.ModelTimeManager;
import mp.parser.ExternalFunction;
import mp.parser.ParserFactory;

public class DumpModel implements ExternalFunction{
	
	static {
		ParserFactory.addExternalFunction(new DumpModel());		
	}

	@Override
	public String getName() {		
		return "dump";
	}

	@Override
	public int getParamCount() {		
		return 0;
	}

	@Override
	public void execute(Object[] functionParams) {
		execute();
		
	}

	@Override
	public void execute() {	  	
	  ModelTimeManager tm = ModelTimeManager.getTimeManager();
	  ModelElement element = tm.getCurrentExecElement();
	  if ( element == null ) {
	  	/*если вызов функции производится из юнит теста, то здесь может ничего не быть, если создается голый парсер, без собственно элемента, в котором парсер вызывается*/
	  	return;
	  }
	  ModelElement curExecElement = element;	  
	  while ( element.GetOwner() != null ) {
	  	element = element.GetOwner();
	  }
	  ModelElement rootElement = element;
	  PrintStream ps = System.out;
	  ps.println( "PrintStream " + ps.getClass().getName() );
	  
	  
		
	}

	@Override
	public String getResultTypeName() {		
		return "boolean";
	}

}

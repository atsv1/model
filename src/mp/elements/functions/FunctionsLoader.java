package mp.elements.functions;

import mp.parser.ParserFactory;

public class FunctionsLoader {
	static {
		ParserFactory.addExternalFunction(new DumpModel());
	}

}

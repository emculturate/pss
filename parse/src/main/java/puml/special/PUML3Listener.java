package puml.special;

import puml3.PUML3BaseListener;
import puml3.PUML3Parser;

public class PUML3Listener extends PUML3BaseListener {

	PUML3Parser parser;
	
	public PUML3Listener(PUML3Parser theparser) {
		this.parser = theparser;
	}

}

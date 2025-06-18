package pss.special;

import puml3.PUML3BaseListener;
import puml3.PUML3Parser;

public class MyListener extends PUML3BaseListener {

	PUML3Parser parser;
	
	public MyListener(PUML3Parser theparser) {
		this.parser = theparser;
	}

}

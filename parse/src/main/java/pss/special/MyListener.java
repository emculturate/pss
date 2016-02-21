package pss.special;

import pss.parse.PUML3BaseListener;
import pss.parse.PUML3Parser;

public class MyListener extends PUML3BaseListener {

	PUML3Parser parser;
	
	public MyListener(PUML3Parser theparser) {
		this.parser = theparser;
	}

}

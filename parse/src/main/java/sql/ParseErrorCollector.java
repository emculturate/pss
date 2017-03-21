package sql;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;



public class ParseErrorCollector implements ANTLRErrorStrategy {

	private List<String> errorList = new ArrayList<String> ();
	
	public List<String> getErrorList() {
		return errorList;
	}

	@Override
	public void reset(Parser recognizer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Token recoverInline(Parser recognizer) throws RecognitionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recover(Parser recognizer, RecognitionException e) throws RecognitionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sync(Parser recognizer) throws RecognitionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean inErrorRecoveryMode(Parser recognizer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reportMatch(Parser recognizer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportError(Parser recognizer, RecognitionException e) {
		// TODO Auto-generated method stub
		errorList.add(e.getMessage());
	}

}

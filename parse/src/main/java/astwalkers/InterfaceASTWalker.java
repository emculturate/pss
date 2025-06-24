package astwalkers;

import java.util.Map;

public interface InterfaceASTWalker {

	public Map<String, Object> handle(String key, Map<String, Object> map, Map<String, Object> resTree);

}

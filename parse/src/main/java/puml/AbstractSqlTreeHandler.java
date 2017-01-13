package puml;

import java.util.Map;

public abstract class AbstractSqlTreeHandler implements SqlTreeHandlerInterface {

	@Override
	public Map<String, Object> handle(String key, Map<String, Object> map, Map<String, Object> resTree) {
		System.out.println("Key: " + key);
		System.out.println("Handling: " + map);
		System.out.println("Partial inputs: " + resTree);
		return map;
	}

}

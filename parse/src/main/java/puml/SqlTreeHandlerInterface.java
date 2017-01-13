package puml;

import java.util.Map;

public interface SqlTreeHandlerInterface {

	public Map<String, Object> handle(String key, Map<String, Object> map, Map<String, Object> resTree);

}

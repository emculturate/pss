package puml;

import java.util.Map;

public class ConstructSqlTreehandler extends AbstractSqlTreeHandler {


	@Override
	public Map<String, Object> handle(String key, Map<String, Object> map, Map<String, Object> resTree) {
		System.out.println("Key: " + key);
		System.out.println("Handling: " + map);
		System.out.println("Inprogress: " + resTree);
		
		switch (key) {
		case "column" :
			String entry = null;
			if (resTree.containsKey("table_ref")) {
				entry = (String) resTree.remove("table_ref") + ".";
			}
			if (resTree.containsKey("name")) {
				entry = entry + (String) resTree.remove("name");
			}
			resTree.put("qry", entry);
			break;
		default : 
			System.out.println("Unhandled key:"+ key);
		}
		
		return resTree;
	}

}

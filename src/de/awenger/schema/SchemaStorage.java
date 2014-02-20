package de.awenger.schema;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import de.awenger.schema.json.JSON;
import de.awenger.schema.json.JSONPath;

public class SchemaStorage {

	private Map<JSONPath, HyperSchema> schemata = new HashMap<JSONPath, HyperSchema>();
	private Map<JSONPath, JSONPath> equivalents = new HashMap<JSONPath, JSONPath>();
	
	public void addSchemaDoc(JSON schemaDoc){
		
		JSONPath basePath = new JSONPath(URI.create("#"));
		if(schemaDoc.asObject().has("id") && schemaDoc.asObject().get("id") instanceof String){
			String id = schemaDoc.asObject().getString("id");
			try {
				basePath = new JSONPath(new URI(id));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map<JSONPath, JSONObject> incObjs = schemaDoc.includedObjects(basePath);
		for(JSONPath path : incObjs.keySet()){
			extractEquivalents(path);
//			System.out.println(path);
			schemata.put(path, new HyperSchema(path, incObjs.get(path)));
		}
	}
	
	private void extractEquivalents(JSONPath path){
		for(JSONPath equivalent : path.getEquivalents()){
			equivalents.put(equivalent, path);
			extractEquivalents(equivalent);
		}
	}
	
	public Validator getValidator(){
		return new Validator(this);
	}
	
	public HyperSchema getSchema(JSONPath path){
		path = findBasePath(path);
		HyperSchema schema = schemata.get(path);
		return schema;
	}
	
	public HyperSchema[] getSchemataWithRelLink(String rel){
		List<HyperSchema> result = new ArrayList<HyperSchema>();
		for(JSONPath path : schemata.keySet()){
			HyperSchema candidate = schemata.get(path);
			boolean hasThisRel = false;
			for(Link link : candidate.getLinks()){
				if(link.getRel().equals("self"))
					hasThisRel = true;
			}
			result.add(candidate);
		}
		return result.toArray(new HyperSchema[result.size()]);
	}
	
	private JSONPath findBasePath(JSONPath path){
		if(equivalents.containsKey(path))
			return findBasePath(equivalents.get(path));
		else
			return path;
	}
}

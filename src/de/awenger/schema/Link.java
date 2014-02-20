package de.awenger.schema;

import org.json.JSONObject;

import de.awenger.schema.json.JSONPath;

public class Link {

	private String rel;
	private String href;
	private String title;
	private JSONPath targetSchema;
	private String method;
	private String mediaType;
	private String encType;
	private JSONPath schema;
	
	public Link(JSONPath path, JSONObject linkDesc){
		href = (linkDesc.opt("href") instanceof String) ? linkDesc.getString("href") : null;
		rel = (linkDesc.opt("rel") instanceof String) ? linkDesc.getString("rel") : null;
		title = (linkDesc.opt("title") instanceof String) ? linkDesc.getString("title") : null;
		if(linkDesc.opt("targetSchema") instanceof JSONObject)
			targetSchema = path.resolvePath("targetSchema");
		method = (linkDesc.opt("method") instanceof String) ? linkDesc.getString("method") : "get";
		method = method.toLowerCase();
		mediaType = (linkDesc.opt("mediaType") instanceof String) ? linkDesc.getString("mediaType") : null;
		encType = (linkDesc.opt("encType") instanceof String) ? linkDesc.getString("encType") : null;
		if(linkDesc.opt("targetSchema") instanceof JSONObject)
			targetSchema = path.resolvePath("schema");
	}

	
	
	public String getRel() {
		return rel;
	}



	public void setRel(String rel) {
		this.rel = rel;
	}



	@Override
	public String toString() {
		return method + "\t "+href + "\t "+rel;
	}
	
	

}

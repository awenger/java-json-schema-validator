package de.awenger.schema;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.awenger.schema.json.JSONPath;

public class HyperSchema extends Schema {

	private List<Link> links = new ArrayList<Link>();

	public HyperSchema(JSONPath path, JSONObject obj) {
		super(path, obj);
		extractLinks(path, obj);
	}

	private void extractLinks(JSONPath path, JSONObject obj) {
		if (!obj.has("links") || !(obj.get("links") instanceof JSONArray))
			return;
		JSONPath linksPath = path.resolvePath("links");
		JSONArray linksArr = obj.getJSONArray("links");
		//System.out.println(path);
		for (int i = 0; i < linksArr.length(); i++) {
			Object linkDescObj = linksArr.get(i);
			if (linkDescObj instanceof JSONObject) {
				JSONPath linkPath = linksPath.resolvePath("" + i);
				Link link = new Link(linkPath, (JSONObject) linkDescObj);
				links.add(link);
				//System.out.println(link);
			}
		}

	}

	public List<Link> getLinks() {
		return links;
	}

}

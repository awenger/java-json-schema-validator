package de.awenger.schema.json;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class JSONPath {

	private URI uri;
	private List<JSONPath> equivalents = new ArrayList<JSONPath>();
	
	public JSONPath(URI uri){
		this.uri = uri;
	}
	
	private JSONPath(URI uri, List<JSONPath> equivalents){
		this.uri = uri;
		this.equivalents = equivalents;
	}
	
	public JSONPath resolvePath(String subPath){
		List<JSONPath> newEquivalents = new ArrayList<JSONPath>();
		for(JSONPath newEquivalent : equivalents){
			newEquivalents.add(newEquivalent.resolvePath(subPath));
		}
		String escapedSubPath = escapePathFragment(subPath);
		if(uri.getRawFragment() == null){
			return new JSONPath(uri.resolve("#/"+ escapedSubPath), newEquivalents);
		}else{
			return new JSONPath(uri.resolve("#"+uri.getRawFragment()+"/"+escapedSubPath), newEquivalents);
		}
	}
	
	public JSONPath resolve(JSONPath other){
		URI newUri = uri.resolve(other.getUri());
		List<JSONPath> newEquivalents = new ArrayList<JSONPath>();
		for(JSONPath newEquivalent : equivalents){
			newEquivalents.add(newEquivalent.resolve(other));
		}
		return new JSONPath(newUri, newEquivalents);
	}

	private String escapePathFragment(String pathFragment){
		return pathFragment.replace("~", "~0").replace("/", "~1").replace("%", "%25").replace("^", "%5E").replace("{","%7B").replace("}", "%7D");
	}
	
	
	public URI getUri() {
		return uri;
	}

	public void addEquivalent(JSONPath equivalent){
		if(!equivalent.equals(this))
			equivalents.add(equivalent);
	}
	
	public List<JSONPath> getEquivalents() {
		return equivalents;
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof JSONPath){
			JSONPath other = (JSONPath) obj;
			return other.getUri().equals(uri);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return uri.toString();
	}
	
	
}

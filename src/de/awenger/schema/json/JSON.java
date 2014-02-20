package de.awenger.schema.json;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


public class JSON {
	Object val;

	public JSON(Object val) {
		this.val = val;
	}
	
	public JSON(String val){
		if(val == null)
			this.val = JSONObject.NULL;
		else
			this.val = new JSONTokener(val).nextValue();
		
	}
	
	public Map<JSONPath, JSONObject> includedObjects(JSONPath base){
		Map<JSONPath, JSONObject> objects = new HashMap<JSONPath, JSONObject>();
		findEmbededObjects(base, val, objects);
		return objects;
	}
	
	private void findEmbededObjects(JSONPath path, Object val, Map<JSONPath, JSONObject> results){
		if(val instanceof JSONObject){
			JSONObject obj = (JSONObject) val;
			if(obj.has("id") && obj.get("id") instanceof String){
//				System.out.println("has id: "+obj.get("id"));
				JSONPath equivalent = path.resolve(new JSONPath(URI.create(obj.getString("id"))));
				path.addEquivalent(equivalent);
			}
			results.put(path, obj);
			
			Iterator<String> keyItr = obj.keys();
			while(keyItr.hasNext()){
				String key = keyItr.next();
				JSONPath newPath = path.resolvePath(key);
				findEmbededObjects(newPath, obj.get(key), results);
			}
		} else if(val instanceof JSONArray) {
			JSONArray array = (JSONArray) val;
			for(int i=0; i< array.length(); i++){
				JSONPath newPath = path.resolvePath(i+"");
				findEmbededObjects(newPath, array.get(i), results);
			}
		}
	}
	
	public boolean isInteger(){
		return val instanceof Integer;
	}
	
	public boolean isNumber(){
		return val instanceof Double || val instanceof Float || val instanceof Integer;
	}
	
	public boolean isString(){
		return val instanceof String;
	}
	
	public boolean isObject(){
		return val instanceof JSONObject;
	}
	
	public boolean isArray(){
		return val instanceof JSONArray;
	}
	public boolean isBoolean(){
		return val instanceof Boolean;
	}
	public boolean isNull(){
		return JSONObject.NULL.equals(val);
	}
	
	public JSONObject asObject(){
		return isObject() ? (JSONObject)val : null;
	}
	
	public Integer asInteger(){
		return isInteger() ? (Integer)val : null;
	}
	public Double asNumber(){
		if(isInteger()){
			return ((Integer)val).doubleValue();
		}else if(isNumber()){
			return (Double)val;
		}
		return null;
	}
	
	public Boolean asBoolean(){
		return isBoolean() ? (Boolean)val : null;
	}
	public String asString(){
		return isString() ? (String)val : null;
	}
	
	public JSONArray asArray(){
		return isArray() ? (JSONArray) val : null;
	}
	
	public Object getVal(){
		return val;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof JSON)){
			return super.equals(obj);
		}
		JSON other = (JSON)obj;
		if(isNull() && other.isNull()){
			return true;
		} else if(isBoolean() && other.isBoolean() && asBoolean().equals(other.asBoolean())){
			return true;
		} else if(isString() && other.isString() && asString().equals(other.asString())){
			return true;
		} else if(isNumber() && other.isNumber() && asNumber().equals(other.asNumber())){
			return true;
		} else if(isArray() && other.isArray()){
			JSONArray arr = asArray();
			JSONArray otherArr = other.asArray();
			if(arr.length() != otherArr.length())
				return false;
			for(int i=0; i < arr.length(); i++){
				JSON ele = new JSON(arr.get(i));
				JSON otherEle = new JSON(otherArr.get(i));
				if(!ele.equals(otherEle))
					return false;
			}
			return true;
			
		}else if(isObject() && other.isObject()){
			JSONObject meObj = asObject();
			JSONObject otherObj = other.asObject();
			Set<String> meFields = meObj.keySet();
			Set<String> otherFields = otherObj.keySet();
		
			if(meFields.containsAll(otherFields) && otherFields.containsAll(meFields)){
				for(String field : meFields){
					JSON myVal = new JSON(meObj.opt(field));
					JSON otherVal = new JSON(otherObj.opt(field));
					if(!myVal.equals(otherVal))
						return false;
				}
				return true;
			}else{
				return false;
			}
			
		}else{
			return false;
		}
	}

	@Override
	public String toString() {
		if(JSONObject.NULL.equals(val))
			return "null";
		return val.toString();
	}
	
	
	
}

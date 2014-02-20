package de.awenger.schema;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import de.awenger.schema.json.JSON;
import de.awenger.schema.json.JSONPath;

public class Schema {

	private JSONPath path;
	
	// number validation
	private Double multipleOf = null;
	private Boolean exclusiveMaximum = false;
	private Double maximum = null;
	private Boolean exclusiveMinimum = false;
	private Double minimum = null;
	// string validation
	private Integer maxLength = null;
	private Integer minLength = null;
	private String pattern;
	// array validation
	private JSON items = new JSON(null);
	private JSON additionalItems = new JSON(null);
	private Integer maxItems = null;
	private Integer minItems = null;
	private Boolean uniqueItems = false;
	// object validation
	private Integer maxProperties = null;
	private Integer minProperties = 0;
	private String[] requiredFields;
	private Set<String> properties = new HashSet<String>();
	private JSON additionalProperties = new JSON(null);
	private Set<String> patternProperties = new HashSet<String>();
	private Map<String,JSON> dependencies = new HashMap<String, JSON>();
	// geberal validation
	private Set<JSON> enumOptions = new HashSet<>();
	private String[] permittedTypes;
	private Integer allOfCount = new Integer(0);
	private Integer anyOfCount = new Integer(0);
	private Integer oneOfCount = new Integer(0);
	private boolean hasNot = false;
	private JSONPath ref = null;
	
	public Schema(JSONPath path, JSONObject obj){
		this.path = path;
		
		extractRequired(obj);
		extractMultipleOf(obj);
		extractMaximum(obj);
		extractMinimum(obj);
		extractMaxLength(obj);
		extractMinLength(obj);
		extractPattern(obj);
		extractItems(obj);
		extractAdditionalItems(obj);
		extractMaxItems(obj);
		extractMinItems(obj);
		extractUniqueItems(obj);
		extractMaxProperties(obj);
		extractMinProperties(obj);
		extractProperties(obj);
		extractAdditionalProperties(obj);
		extractPatternProperties(obj);
		extractDependencies(obj);
		
		extractEnumOptions(obj);
		extractType(obj);
		extractAllOf(obj);
		extractAnyOf(obj);
		extractOneOf(obj);
		extractNot(obj);
		extractRef(obj);
	}

	// Rule extraction
	
	// number and integer rule extraction
	
	private void extractMultipleOf(JSONObject obj){
		JSON multOf = new JSON(obj.opt("multipleOf"));
		if(multOf.isNull())
			return;
		if(multOf.isNumber())
			multipleOf = multOf.asNumber();
	}
	
	private void extractMaximum(JSONObject obj) {
		JSON max = new JSON(obj.opt("maximum"));
		if(max.isNull())
			return;
		if(max.isNumber())
			maximum = max.asNumber();
		
		JSON exMax = new JSON(obj.opt("exclusiveMaximum"));
		if(exMax.isNull())
			return;
		if(exMax.isBoolean())
			exclusiveMaximum = exMax.asBoolean();
	}
	
	private void extractMinimum(JSONObject obj) {
		JSON min = new JSON(obj.opt("minimum"));
		if(min.isNull())
			return;
		if(min.isNumber())
			minimum = min.asNumber();
		
		JSON exMin = new JSON(obj.opt("exclusiveMinimum"));
		if(exMin.isNull())
			return;
		if(exMin.isBoolean())
			exclusiveMinimum = exMin.asBoolean();
	}
	
	// string rule extraction
	
	private void extractMaxLength(JSONObject obj) {
		JSON maxL = new JSON(obj.opt("maxLength"));
		if(!maxL.isInteger())
			return;
		maxLength = maxL.asInteger();		
	}
	
	private void extractMinLength(JSONObject obj) {
		JSON minL = new JSON(obj.opt("minLength"));
		if(!minL.isInteger())
			return;
		minLength = minL.asInteger();
	}
	
	private void extractPattern(JSONObject obj) {
		JSON pat = new JSON(obj.opt("pattern"));
		if(pat.isString())
			pattern = pat.asString();
			
	}
	
	// array rule extraction
	
	private void extractItems(JSONObject obj) {
		JSON items = new JSON(obj.opt("items"));
		if(items.isObject())
			this.items = items;
		else if(items.isArray()){
			JSONArray itemsArr = items.asArray();
			for(int i=0; i< itemsArr.length(); i++){
				if(itemsArr.optJSONObject(i) == null){
					System.out.println("warning items Array contains non Object");
					return;
				}
			}
			this.items = items;
		}
	}
	
	private void extractAdditionalItems(JSONObject obj) {
		JSON additionalItems = new JSON(obj.opt("additionalItems"));
		if(additionalItems.isObject() || additionalItems.isBoolean())
			this.additionalItems = additionalItems;
	}
	
	private void extractMaxItems(JSONObject obj) {
		JSON maxItems = new JSON(obj.opt("maxItems"));
		if(maxItems.isInteger() && maxItems.asInteger() >= 0)
			this.maxItems = maxItems.asInteger();
		
	}
	
	private void extractMinItems(JSONObject obj) {
		JSON minItems = new JSON(obj.opt("minItems"));
		if(minItems.isInteger() && minItems.asInteger() >= 0)
			this.minItems = minItems.asInteger();
		
	}
	
	private void extractUniqueItems(JSONObject obj) {
		JSON uniqueItems = new JSON(obj.opt("uniqueItems"));
		if(!uniqueItems.isBoolean())
			return;
		this.uniqueItems = uniqueItems.asBoolean();
	}

	// object rule extraction
	
	private void extractMaxProperties(JSONObject obj) {
		JSON maxProp = new JSON(obj.opt("maxProperties"));
		if(maxProp.isInteger())
			maxProperties = maxProp.asInteger();		
	}
	
	private void extractMinProperties(JSONObject obj) {
		JSON minProp = new JSON(obj.opt("minProperties"));
		if(minProp.isInteger())
			minProperties = minProp.asInteger();		
	}
	
	private void extractRequired(JSONObject obj) {
		JSONArray requiredArr = obj.optJSONArray("required");
		if(requiredArr == null){
			requiredFields = new String[0];
			return;
		}
		requiredFields = new String[requiredArr.length()];
		for(int i=0; i< requiredArr.length(); i++){
			requiredFields[i] = requiredArr.optString(i);
		}
	}
	
	private void extractProperties(JSONObject obj) {
		JSON props = new JSON(obj.opt("properties"));
		if(props.isObject()){
			this.properties = props.asObject().keySet();
		}
	}
	
	private void extractAdditionalProperties(JSONObject obj) {
		JSON props = new JSON(obj.opt("additionalProperties"));
		if(props.isObject() || props.isBoolean())
			this.additionalProperties = props;
	}
	
	private void extractPatternProperties(JSONObject obj) {
		JSON props = new JSON(obj.opt("patternProperties"));
		if(props.isObject())
			this.patternProperties = props.asObject().keySet();
	}
	
	private void extractDependencies(JSONObject obj) {
		JSON dep = new JSON(obj.opt("dependencies"));
		if(dep.isObject()){
			Set<String> dependents = dep.asObject().keySet();
			for(String dependent : dependents){
				JSON depDesc = new JSON(dep.asObject().opt(dependent));
				if(depDesc.isArray() || depDesc.isObject())
					dependencies.put(dependent, depDesc);
			}
		}
		
	}
	
	// general rule extraction
	
	private void extractEnumOptions(JSONObject obj) {
		JSONArray enumOptionsArr = obj.optJSONArray("enum");
		if(enumOptionsArr == null)
			return;
		for(int i=0; i < enumOptionsArr.length(); i++){
			enumOptions.add(new JSON(enumOptionsArr.get(i)));
		}
	}
	
	private void extractType(JSONObject obj){
		if(!obj.has("type")){
			permittedTypes = new String[0];
			return;
		}
		Object typeVal = obj.get("type");
		if(typeVal instanceof String)
			permittedTypes = new String[]{(String)typeVal};
		else if(typeVal instanceof JSONArray){
			permittedTypes = new String[((JSONArray) typeVal).length()];
			for(int i=0; i< permittedTypes.length; i++)
				permittedTypes[i] = ((JSONArray) typeVal).getString(i);
		}
	}
	
	private void extractAllOf(JSONObject obj) {
		JSONArray allOfOptionsArr = obj.optJSONArray("allOf");
		if(allOfOptionsArr == null)
			return;
		this.allOfCount = allOfOptionsArr.length();
	}
	
	private void extractAnyOf(JSONObject obj) {
		JSONArray anyOfOptionsArr = obj.optJSONArray("anyOf");
		if(anyOfOptionsArr == null)
			return;
		this.anyOfCount = anyOfOptionsArr.length();
	}
	
	private void extractOneOf(JSONObject obj) {
		JSONArray oneOfOptionsArr = obj.optJSONArray("oneOf");
		if(oneOfOptionsArr == null)
			return;
		this.oneOfCount = oneOfOptionsArr.length();
	}
	
	private void extractNot(JSONObject obj) {
		JSONObject notOptionsArr = obj.optJSONObject("not");
		if(notOptionsArr == null)
			return;
		this.hasNot = true;
	}
	private void extractRef(JSONObject obj) {
		if(obj.has("$ref") && obj.get("$ref") instanceof String){
			String refStr = obj.getString("$ref");
			try {
				URI refUri = new URI(refStr);
				JSONPath refPath = new JSONPath(refUri);
				ref = path.resolve(refPath);
//				URI pathUri = new URI(path);
//				ref = pathUri.resolve(refUri);
//				System.out.println("RESOLVING REF: ");
//				System.out.println(refUri);
//				System.out.println(pathUri);
//				System.out.println(ref);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// Validation
	
	public boolean validate(JSON obj, Validator validator){
		boolean valid = true;
		valid &= isRequiredValid(obj);
		valid &= isMultipleOfValid(obj);
		valid &= isMaximumValid(obj);
		valid &= isMinimumValid(obj);
		valid &= isMaxLengthValid(obj);
		valid &= isMinLengthValid(obj);
		valid &= isPatternValid(obj);
		
		valid &= areItemsValid(obj, validator);
		valid &= isMaxItemsValid(obj);
		valid &= isMinItemsValid(obj);
		valid &= isUniqueItemsValid(obj);
		valid &= isMaxPropertiesValid(obj);
		valid &= isMinPropertiesValid(obj);
		valid &= arePropertiesValid(obj, validator);
		valid &= areDependenciesValid(obj, validator);
		
		valid &= doesEnumOptionsMatch(obj);
		valid &= isTypeValid(obj);
		valid &= isAllOfValid(obj, validator);
		valid &= isAnyOfValid(obj, validator);
		valid &= isOneOfValid(obj, validator);
		valid &= isNotValid(obj, validator);
		valid &= isRefValid(obj, validator);
		return valid;
	}

	// number and integer validation

	private boolean isMultipleOfValid(JSON obj) {
		if(multipleOf == null)
			return true;
		if(!obj.isNumber())
			return true;
				
		Double val = obj.asNumber();
		Double devided = val/multipleOf;
		if(Math.round(devided) == devided)
			return true;
		return false;
	}
	
	private boolean isMaximumValid(JSON obj) {
		if(!obj.isNumber())
			return true;
		if(maximum == null)
			return true;
		
		if(exclusiveMaximum)
			return obj.asNumber() < maximum;
		else
			return obj.asNumber() <= maximum;
	}
	
	private boolean isMinimumValid(JSON obj) {
		if(!obj.isNumber())
			return true;
		if(minimum == null)
			return true;
		
		if(exclusiveMinimum)
			return obj.asNumber() > minimum;
		else
			return obj.asNumber() >= minimum;
	}
	
	// string validation
	
	private boolean isMaxLengthValid(JSON obj) {
		if(!obj.isString() || maxLength == null)
			return true;
		return obj.asString().length() <= maxLength;
	}
	
	private boolean isMinLengthValid(JSON obj) {
		if(!obj.isString() || minLength == null)
			return true;
		return obj.asString().length() >= minLength;
	}
	
	private boolean isPatternValid(JSON obj) {
		if(pattern == null || !obj.isString())
			return true;
		return regexMatch(obj.asString(), pattern);
	}
	
	// array validation
	
	private boolean areItemsValid(JSON obj, Validator validator) {
		if(!obj.isArray())
			return true;
		JSONArray asArr = obj.asArray();
		if(items.isObject()){
			// validate all elements in array agains this prototype
			for(int i = 0; i < asArr.length(); i++) {
				JSONPath subPath = path.resolvePath("items");
				JSON subVal = new JSON(asArr.opt(i));
				if(!validator.validate(subPath, subVal))
					return false;
			}
		}else if(items.isArray()){
			for(int i = 0; i< asArr.length(); i++) {
				JSON subVal = new JSON(asArr.opt(i));
				if(items.asArray().length()>i){
					// validate agains items schema
					JSONPath subPath = path.resolvePath("items").resolvePath(i+"");
					if(!validator.validate(subPath, subVal))
						return false;
				} else {
					// validate agains additionalItems schema?
//					System.out.println(path+" ~additional items is: "+additionalItems);
//					System.out.println("val: "+subVal);
					if(additionalItems.isObject()){
						// match agains schema in additional items
						JSONPath subPath = path.resolvePath("additionalItems");
						if(!validator.validate(subPath, subVal))
							return false;
					}else if(additionalItems.isBoolean() && additionalItems.asBoolean() == false){
						return false; // too much items
					}else {
						return true;
					}
				}
			}
		}
//		System.out.println(asArr);
//		System.out.println(items);
//		System.out.println(additionalItems);asdsad
		return true;
		
	}
	
	private boolean isMaxItemsValid(JSON obj) {
		if(!obj.isArray() || maxItems == null)
			return true;
		return obj.asArray().length() <= maxItems;
	}
	
	private boolean isMinItemsValid(JSON obj) {
		if(!obj.isArray() || minItems == null)
			return true;
		return obj.asArray().length() >= minItems;
	}
	
	private boolean isUniqueItemsValid(JSON obj) {
		if(!obj.isArray() || uniqueItems == false)
			return true;
		JSONArray objArr = obj.asArray();
		for(int i = 0; i < objArr.length(); i++){
			JSON objA = new JSON(objArr.get(i));
			for(int j = i + 1; j < objArr.length(); j++){
				JSON objB = new JSON(objArr.get(j));
				if(objA.equals(objB)){
					return false;
				}
			}
		}
		return true;
	}

	// object validation
	private boolean isMaxPropertiesValid(JSON obj) {
		if(!obj.isObject() || maxProperties == null)
			return true;
		return obj.asObject().keySet().size() <= maxProperties;
	}
	
	private boolean isMinPropertiesValid(JSON obj) {
		if(!obj.isObject() || minProperties == null)
			return true;
		return obj.asObject().keySet().size() >= minProperties;
	}
	
	
	private boolean isRequiredValid(JSON obj) {
		JSONObject jsonObj = obj.asObject();
		if(jsonObj == null)
			return true; // required field only applicatble for object
		for(String reqField : requiredFields){
			if(!jsonObj.has(reqField))
				return false;
		}
		return true;
	}
	
	private boolean arePropertiesValid(JSON obj, Validator validator) {
		if(!obj.isObject())
			return true;
			
		JSONObject asObj = obj.asObject();
//		System.out.println("validating : "+asObj);
		Set<String> appearingProps = asObj.keySet();
		
		for(String appearingProp : appearingProps){
			boolean found = false;
			JSON propValue = new JSON(asObj.opt(appearingProp));
//			System.out.println("prop: "+appearingProp);
			if(properties.contains(appearingProp)){
				JSONPath subPath = path.resolvePath("properties").resolvePath(appearingProp);
				if(!validator.validate(subPath, propValue))
					return false;
				found = true;
			}
			for(String pattern : patternProperties){
				if(regexMatch(appearingProp, pattern)){
//					String subPath = path + "/patternProperties/" + pattern;
					JSONPath subPath = path.resolvePath("patternProperties").resolvePath(pattern);
					if(!validator.validate(subPath, propValue))
						return false;
					found = true;
				}
			}
//			System.out.println("surviced pattern prop validation");
			if(!found && additionalProperties.isObject()){
//				String subPath = path + "/additionalProperties";;
				JSONPath subPath = path.resolvePath("additionalProperties");
//				System.out.println(propValue);
				if(!validator.validate(subPath, propValue))
					return false;
			}else if(!found && additionalProperties.isBoolean() && additionalProperties.asBoolean() == false){
				return false;
			}
//			System.out.println("surviced addition prop validation");
		}
		return true;
	}
	
	private boolean areDependenciesValid(JSON obj, Validator validator) {
		if(!obj.isObject())
			return true;
		
		for(String dependent : dependencies.keySet()){
			if(obj.asObject().has(dependent)){
				JSON depDesc = dependencies.get(dependent);
				if(depDesc.isArray()){
					JSONArray depDesArr = depDesc.asArray();
					for(int i = 0; i< depDesArr.length(); i++){
						if(!obj.asObject().has(depDesArr.getString(i)))
							return false;
					}
				}else if(depDesc.isObject()){
//					String subPath = path + "/dependencies/" + dependent;
					JSONPath subPath = path.resolvePath("dependencies").resolvePath(dependent);
					if(!validator.validate(subPath, obj))
						return false;
				}
			}
		}
		return true;
	}
	
	// general validation
	
	private boolean doesEnumOptionsMatch(JSON obj) {
		if(enumOptions.size() == 0)
			return true;
		for(JSON option : enumOptions){
			if(option.equals(obj))
				return true;
		}
		return false;
	}
	
	private boolean isTypeValid(JSON obj){
		if(permittedTypes.length == 0)
			return true;
		for(String type : permittedTypes){
			if(type.equals("integer") && obj.isInteger())
				return true;
			if(type.equals("number") && obj.isNumber())
				return true;
			if(type.equals("string") && obj.isString())
				return true;
			if(type.equals("object") && obj.isObject())
				return true;
			if(type.equals("array") && obj.isArray())
				return true;
			if(type.equals("boolean") && obj.isBoolean())
				return true;
			if(type.equals("null") && obj.isNull())
				return true;
		}
		return false;		
	}
	
	private boolean isAllOfValid(JSON obj, Validator validator) {
		for(int i=0; i < allOfCount; i++){
//			String subPath = path + "/allOf/" + i;
			JSONPath subPath = path.resolvePath("allOf").resolvePath(i+"");
			if(!validator.validate(subPath, obj))
				return false;
		}
		return true;
	}
	
	private boolean isAnyOfValid(JSON obj, Validator validator) {
		if(anyOfCount == 0)
			return true;
		for(int i=0; i < anyOfCount; i++){
//			String subPath = path + "/anyOf/" + i;
			JSONPath subPath = path.resolvePath("anyOf").resolvePath(i+"");
			if(validator.validate(subPath, obj))
				return true;
		}
		return false;
	}

	private boolean isOneOfValid(JSON obj, Validator validator) {
		if(oneOfCount == 0)
			return true;
		int validCount = 0;
		for(int i=0; i < oneOfCount; i++){
//			String subPath = path + "/oneOf/" + i;
			JSONPath subPath = path.resolvePath("oneOf").resolvePath(i+"");
			if(validator.validate(subPath, obj))
				validCount++;
		}
		if(validCount == 0 || validCount > 1)
			return false;
		return true;
	}
	
	private boolean isNotValid(JSON obj, Validator validator) {
		if(!hasNot)
			return true;
		else {
//			String subPath = path + "/not";
			JSONPath subPath = path.resolvePath("not");
			return !validator.validate(subPath, obj);
		}
	}

	private boolean isRefValid(JSON obj, Validator validator) {
//		System.out.println("validating ref:");
//		System.out.println(path);
//		System.out.println();
//		System.out.println("checking ref: "+ref);
//		System.out.println("obj: "+obj);
		if(ref == null)
			return true;
//		System.out.println(ref);
		return validator.validate(ref, obj);		
	}
	
	private boolean regexMatch(String value, String pattern){
		//  ECMA 262 regex style
		if(!pattern.startsWith("^"))
			pattern = "^.*"+pattern;
		if(!pattern.endsWith("$"))
			pattern+= ".*$";
		
		return value.matches(pattern);
	}
}

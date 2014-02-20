package de.awenger.schema;

import java.util.HashMap;
import java.util.Map;

import de.awenger.schema.json.JSON;
import de.awenger.schema.json.JSONPath;

public class Validator {
	private SchemaStorage storage;

	public Validator(SchemaStorage storage) {
		this.storage = storage;
	}
	
	public boolean validate(JSONPath path, JSON val){
		HyperSchema schema = storage.getSchema(path);
		if(schema == null){
			return true;
		}
		return schema.validate(val, this);
	}

}

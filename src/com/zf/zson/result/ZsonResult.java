package com.zf.zson.result;

import java.util.List;
import java.util.Map;

public interface ZsonResult {
	
	public boolean isValid();
	
	public Object getValue(String path);
	
	public Object getResult();
	
	public List<Object> getValues(String path);
	
	public Map<String, Object> getMap(String path);
	
	public List<Object> getList(String path);
	
	public String toJsonString(Object obj);
	
	public String getString(String path);
	
	public int getInteger(String path);
	
	public long getLong(String path);
	
	public double getDouble(String path);
	
	public float getFloat(String path);
	
	public boolean getBoolean(String path);
	
	public void removeValue(String path);
	
	public void updateValue(String path, Object value);
}

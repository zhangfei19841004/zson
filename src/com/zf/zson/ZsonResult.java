package com.zf.zson;

import java.util.List;
import java.util.Map;

public interface ZsonResult {
	
	public boolean isValid();
	
	public Object getValue(String path);
	
	public List<Object> getValues(String path);
	
	public Map<String, Object> getMap(String path);
	
	public List<Object> getList(String path);
	
	public String toJsonString(Object obj);
	
}

package com.zf.zson.result;

import java.util.List;


public interface ZsonResult{
	
	public boolean isValid();
	
	public Object getResult();
	
	public Object getValue(String path);
	
	public Object getValue();
	
	public List<Object> getValues(String path);
	
	public String getString(String path);
	
	public int getInteger(String path);
	
	public long getLong(String path);
	
	public double getDouble(String path);
	
	public float getFloat(String path);
	
	public boolean getBoolean(String path);
	
	public void addValue(String path, int index, Object json);
	
	public void addValue(String path, String key, Object json);
	
	public void addValue(int index, Object json);
	
	public void addValue(String key, Object json);
	
	public void deleteValue(String path);
	
	public void updateValue(String path, Object json);
}

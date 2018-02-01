package com.zf.zson.result;

import java.util.List;
import java.util.Map;


public interface ZsonResult {

	boolean isValid();

	Object getResult();

	Object getValue(String path);

	Object getValue();

	List<Object> getValues(String path);

	String getString(String path);

	int getInteger(String path);

	long getLong(String path);

	double getDouble(String path);

	float getFloat(String path);

	boolean getBoolean(String path);

	void addValue(String path, Object json);

	void deleteValue(String path);

	void updateValue(String path, Object json);

	List<String> getPaths();

	Map<String, Class<?>> getClassTypes();

}

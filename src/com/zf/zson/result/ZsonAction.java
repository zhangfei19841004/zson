package com.zf.zson.result;

public interface ZsonAction {
	
	void process(ZsonResult zr, Object value, String currentPath);
	
	int offset(ZsonResult zr, Object value);
	
}

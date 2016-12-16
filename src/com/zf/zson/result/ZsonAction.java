package com.zf.zson.result;

public interface ZsonAction {
	
	boolean before(ZsonResult zr);
	
	void process(ZsonResult zr, Object value, String currentPath);
	
	int offset(ZsonResult zr, Object value);
	
	boolean after(ZsonResult zr);
	
}

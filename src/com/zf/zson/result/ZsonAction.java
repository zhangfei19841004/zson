package com.zf.zson.result;

public interface ZsonAction {
	
	void process(ZsonResult zr, Object value);
	
	int offset(ZsonResult zr, Object value);
	
}

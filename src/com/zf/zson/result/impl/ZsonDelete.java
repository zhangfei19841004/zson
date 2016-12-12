package com.zf.zson.result.impl;

import java.util.ArrayList;
import java.util.List;

import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResult;

public class ZsonDelete implements ZsonAction{
	
	private List<String> deletedPath = new ArrayList<String>();

	@Override
	public void process(ZsonResult zr, Object value, String currentPath) {
		
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}
	
}

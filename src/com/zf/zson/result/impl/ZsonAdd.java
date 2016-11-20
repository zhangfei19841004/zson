package com.zf.zson.result.impl;

import java.util.List;

import com.zf.zson.ZsonUtils;
import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResult;

public class ZsonAdd implements ZsonAction{
	
	private String addJson;
	
	private String addKey;
	
	private int addIndex;

	public void setAddJson(String addJson) {
		this.addJson = addJson;
	}

	public void setAddKey(String addKey) {
		this.addKey = addKey;
	}

	public void setAddIndex(int addIndex) {
		this.addIndex = addIndex;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(ZsonResult zr, Object value) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		String key = zri.getElementKey(value);
		if(key == null){
			throw new RuntimeException("can not add value to this path!");
		}
		Object pathValue = zri.getResultByKey(key);
		if(pathValue instanceof List){
			List<Object> pathList = (List<Object>) pathValue;
			ZsonResultImpl zra = (ZsonResultImpl) zri.parseJsonToZson(addJson);
			Object actionValue = zra.getResultByKey(ZsonUtils.BEGIN_KEY);
			
		}
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}

}

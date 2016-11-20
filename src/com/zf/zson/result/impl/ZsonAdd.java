package com.zf.zson.result.impl;

import java.util.List;

import com.zf.zson.ZsonUtils;
import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResult;

public class ZsonAdd implements ZsonAction{

	@SuppressWarnings("unchecked")
	@Override
	public void process(ZsonResult zr, Object value, String actionJson) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		String key = zri.getElementKey(value);
		if(key == null){
			throw new RuntimeException("can not add value to this path!");
		}
		Object pathValue = zri.getResultByKey(key);
		if(pathValue instanceof List){
			List<Object> pathList = (List<Object>) pathValue;
			ZsonResultImpl zra = (ZsonResultImpl) zri.parseJsonToZson(actionJson);
			Object actionValue = zra.getResultByKey(ZsonUtils.BEGIN_KEY);
			
		}
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}

}

package com.zf.zson.result.impl;

import java.util.ArrayList;
import java.util.List;

import com.zf.zson.ZsonUtils;
import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResult;

public class ZsonRetrieve implements ZsonAction{
	
	private List<Object> result = new ArrayList<Object>();

	public List<Object> getResult() {
		return result;
	}

	@Override
	public void process(ZsonResult zr, Object value, String currentPath) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		result.add(zri.getCollectionsObjectAndRestore(value));
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}

	@Override
	public boolean before(ZsonResult zr) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		if(zri.getzPath().isRootPath()){
			result.add(zri.getZsonResultToString().toJsonString(zri.getResultByKey(ZsonUtils.BEGIN_KEY)));
			return false;
		}
		return true;
	}

	@Override
	public boolean after(ZsonResult zr) {
		return false;
	}

}

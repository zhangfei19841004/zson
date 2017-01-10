package com.zf.zson.result.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZSON;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.path.ZsonCurrentPath;
import com.zf.zson.result.ZsonActionAbstract;
import com.zf.zson.result.ZsonResult;

public class ZsonUpdate extends ZsonActionAbstract{
	
	private Object updateJson;
	
	private List<String> handledPath = new ArrayList<String>();

	public void setUpdateJson(Object updateJson) {
		this.updateJson = updateJson;
	}

	@Override
	public void process(ZsonResult zr, Object value, String currentPath) {
		if(handledPath.contains(currentPath)){
			return;
		}
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		ZsonCurrentPath zcp = this.setKeyOrIndexByPath(zri, currentPath);
		String key = null;
		key = this.getKeyByPath(zri, currentPath);
		Object element = zri.getResultByKey(key);
		ZsonObject<Object> parentObj = new ZsonObject<Object>();
		parentObj.objectConvert(element);
		if((zcp.getIndex()!=null && parentObj.isList()) || (zcp.getKey()!=null && parentObj.isMap())){
			Object updateObj = null;
			if(zcp.getIndex()!=null && parentObj.isList()){
				List<Object> pathValueList = parentObj.getZsonList();
				pathValueList.set((int)zcp.getIndex(), this.getActionObject(zri, updateJson));
				updateObj = pathValueList;
				
			}else if(zcp.getKey()!=null && parentObj.isMap()){
				Map<String, Object> pathValueMap = parentObj.getZsonMap();
				pathValueMap.put(zcp.getKey(), this.getActionObject(zri, updateJson));
				updateObj = pathValueMap;
			}
			ZsonResultImpl zrNew = (ZsonResultImpl) ZSON.parseJson(ZSON.toJsonString(updateObj));
			this.deleteZsonResultInfoChilrenKey(zri, key);
			this.replaceZsonResultInfoKey(zrNew, key, this.getParentPath(currentPath), handledPath, currentPath);
			this.addNewResultToSourceResult(zri, zrNew);
			this.recorrectIndex(zri);
		}
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}

	@Override
	public boolean before(ZsonResult zr) {
		return true;
	}

	@Override
	public boolean after(ZsonResult zr) {
		return false;
	}
	
}

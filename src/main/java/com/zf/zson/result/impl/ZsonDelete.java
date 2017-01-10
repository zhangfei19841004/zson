package com.zf.zson.result.impl;

import java.util.List;
import java.util.Map;

import com.zf.zson.ZSON;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.path.ZsonCurrentPath;
import com.zf.zson.result.ZsonActionAbstract;
import com.zf.zson.result.ZsonResult;

public class ZsonDelete extends ZsonActionAbstract{
	
	private boolean deleted = false;
	
	public void process(ZsonResult zr, Object value, String currentPath) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		String ckey = zri.getElementKey(value);
		ZsonCurrentPath zcp = this.setKeyOrIndexByPath(zri, currentPath);
		String key = null;
		key = this.getKeyByPath(zri, currentPath);
		Object element = zri.getResultByKey(key);
		ZsonObject<Object> parentObj = new ZsonObject<Object>();
		parentObj.objectConvert(element);
		if((zcp.getIndex()!=null && parentObj.isList()) || (zcp.getKey()!=null && parentObj.isMap())){
			Object addObj = null;
			if(zcp.getIndex()!=null && parentObj.isList()){
				List<Object> pathValueList = parentObj.getZsonList();
				pathValueList.remove((int)zcp.getIndex());
				addObj = pathValueList;
				
			}else if(zcp.getKey()!=null && parentObj.isMap()){
				Map<String, Object> pathValueMap = parentObj.getZsonMap();
				pathValueMap.remove(zcp.getKey());
				addObj = pathValueMap;
			}
			if(ckey!=null){
				deleted = true;
			}
			ZsonResultImpl zrNew = (ZsonResultImpl) ZSON.parseJson(ZSON.toJsonString(addObj));
			this.deleteZsonResultInfoChilrenKey(zri, key);
			this.replaceZsonResultInfoKey(zrNew, key, this.getParentPath(currentPath), null, null);
			this.addNewResultToSourceResult(zri, zrNew);
			this.recorrectIndex(zri);
		}
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		if(deleted){
			return -1;
		}else{
			return 0;
		}
		//return 0;
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

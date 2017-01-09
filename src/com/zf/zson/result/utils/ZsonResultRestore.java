package com.zf.zson.result.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZsonUtils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.result.ZsonResultAbstract;

public class ZsonResultRestore {
	
	private ZsonResultAbstract zsonResultAbstract;

	public ZsonResultRestore(ZsonResultAbstract zsonResultAbstract) {
		this.zsonResultAbstract = zsonResultAbstract;
	}

	public Object restoreObject(Object obj){
		ZsonObject<Object> objR = new ZsonObject<Object>();
		objR.objectConvert(obj);
		if(objR.isMap()){
			return this.restoreMap(objR.getZsonMap());
		}else if(objR.isList()){
			return this.restoreList(objR.getZsonList());
		}else{
			return obj;
		}
	}
	
	private Map<String, Object> restoreMap(Map<String, Object> map){
		Map<String, Object> restore = new LinkedHashMap<String, Object>();
		for (String mapKey : map.keySet()) {
			Object mapValue = map.get(mapKey);
			if(mapValue instanceof Map || mapValue instanceof List){
				String key = zsonResultAbstract.getElementKey(mapValue);
				Map<String, Integer> elementStatus = zsonResultAbstract.getzResultInfo().getIndex().get(key);
				Object elementObj = zsonResultAbstract.getzResultInfo().getCollections().get(elementStatus.get(ZsonUtils.INDEX));
				restore.put(mapKey, this.restoreObject(elementObj));
			}else{
				restore.put(mapKey, mapValue);
			}
		}
		return restore;
	}
	
	private List<Object> restoreList(List<Object> list){
		List<Object> restore = new ArrayList<Object>();
		for (Object e : list) {
			if(e instanceof Map || e instanceof List){
				String key = zsonResultAbstract.getElementKey(e);
				Map<String, Integer> elementStatus = zsonResultAbstract.getzResultInfo().getIndex().get(key);
				Object elementObj = zsonResultAbstract.getzResultInfo().getCollections().get(elementStatus.get(ZsonUtils.INDEX));
				restore.add(this.restoreObject(elementObj));
			}else{
				restore.add(e);
			}
		}
		return restore;
	}
	
}

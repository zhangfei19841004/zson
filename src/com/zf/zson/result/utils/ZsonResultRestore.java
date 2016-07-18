package com.zf.zson.result.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZsonUtils;
import com.zf.zson.result.ZsonResultAbstract;

public class ZsonResultRestore {
	
	private ZsonResultAbstract zsonResultAbstract;

	public ZsonResultRestore(ZsonResultAbstract zsonResultAbstract) {
		this.zsonResultAbstract = zsonResultAbstract;
	}

	@SuppressWarnings("unchecked")
	public Object restoreObject(Object obj){
		if(obj instanceof Map){
			return this.restoreMap((Map<String, Object>) obj);
		}else if(obj instanceof List){
			return this.restoreList((List<Object>) obj);
		}else{
			return obj;
		}
	}
	
	private Map<String, Object> restoreMap(Map<String, Object> map){
		Map<String, Object> restore = new HashMap<String, Object>();
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

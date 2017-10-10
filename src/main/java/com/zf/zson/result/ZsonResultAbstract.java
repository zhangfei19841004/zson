package com.zf.zson.result;

import com.zf.zson.ZsonUtils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.path.ZsonPath;
import com.zf.zson.result.info.ZsonResultInfo;
import com.zf.zson.result.utils.ZsonResultRestore;
import com.zf.zson.result.utils.ZsonResultToString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ZsonResultAbstract implements ZsonResult{
	
	protected ZsonResultInfo zResultInfo;
	
	protected ZsonPath zPath;
	
	protected ZsonResultToString zsonResultToString;
	
	protected ZsonResultRestore zsonResultRestore;
	
	public ZsonResultAbstract(){
		zResultInfo = new ZsonResultInfo();
		zPath = new ZsonPath();
		zsonResultToString = new ZsonResultToString();
		zsonResultRestore = new ZsonResultRestore(this);
	}
	
	protected abstract void checkValid();

	public ZsonResultInfo getzResultInfo() {
		return zResultInfo;
	}
	
	public ZsonPath getzPath() {
		return zPath;
	}

	public ZsonResultToString getZsonResultToString() {
		return zsonResultToString;
	}

	public ZsonResultRestore getZsonResultRestore() {
		return zsonResultRestore;
	}

	public String getElementKey(Object value){
		ZsonObject<String> keyObj = new ZsonObject<String>();
		keyObj.objectConvert(value);
		String key = null;
		if(keyObj.isMap()){
			key = keyObj.getZsonMap().get(ZsonUtils.LINK);
		}else if(keyObj.isList()){
			key = keyObj.getZsonList().get(0);
		}else{
			key = null;
		}
		return key;
	}
	
	/**
	 * 将在collections中获取到的值给重新的还原，并返回出去
	 * @param value
	 * @return
	 */
	public Object getCollectionsObjectAndRestore(Object value){
		if(value instanceof Map || value instanceof List){
			String key = this.getElementKey(value);
			Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
			Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
			value = zsonResultToString.toJsonString(zsonResultRestore.restoreObject(elementObj));
		}else if(value instanceof String){
			value = ZsonUtils.convert((String) value);
		}
		return value;
	}
	
	public Object getResultByKey(String key) {
		Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
		Object obj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
		return zsonResultRestore.restoreObject(obj);
	}
	
	/*public ZsonResult parseJsonToZson(String json){
		ZsonParse zp = new ZsonParse(json);
		return zp.fromJson();
	}*/
	
	public List<String> getPaths(){
		List<String> list = new ArrayList<String>();
		for (Object pathObj : zResultInfo.getPath()) {
			ZsonObject<String> zo = new ZsonObject<String>();
			zo.objectConvert(pathObj);
			if(zo.isMap()){
				list.addAll(zo.getZsonMap().values());
			}else{
				list.addAll(zo.getZsonList());
			}
		}
		return list;
	}
	
	@Override
	public Map<String, Class<?>> getClassTypes() {
		Map<String, Class<?>> classTypes = new LinkedHashMap<String, Class<?>>();
		for (int i = 0; i < zResultInfo.getPath().size(); i++) {
			ZsonObject<String> zoPath = new ZsonObject<String>();
			zoPath.objectConvert(zResultInfo.getPath().get(i));
			ZsonObject<Class<?>> zoClass = new ZsonObject<Class<?>>();
			zoClass.objectConvert(zResultInfo.getClassTypes().get(i));
			if(zoPath.isMap()){
				for (String key : zoPath.getZsonMap().keySet()) {
					classTypes.put(zoPath.getZsonMap().get(key), zoClass.getZsonMap().get(key));
				}
			}else{
				for (int j = 0; j < zoPath.getZsonList().size(); j++) {
					classTypes.put(zoPath.getZsonList().get(j), zoClass.getZsonList().get(j));
				}
			}
		}
		return classTypes;
	}
}

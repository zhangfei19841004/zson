package com.zf.zson.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZsonUtils;
import com.zf.zson.path.ZsonPath;
import com.zf.zson.path.ZsonPathInfo;
import com.zf.zson.result.info.ZsonResultInfo;
import com.zf.zson.result.utils.ZsonResultRestore;
import com.zf.zson.result.utils.ZsonResultToString;

public abstract class ZsonResultAbstract {
	
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
	
	public ZsonResultInfo getzResultInfo() {
		return zResultInfo;
	}
	
	protected abstract void checkValid();
	
	@SuppressWarnings("unchecked")
	public String getElementKey(Object value){
		String key = null;
		if(value instanceof HashMap){
			Map<String, String> vMap = (Map<String, String>) value;
			key = vMap.get(ZsonUtils.LINK);
		}else if(value instanceof ArrayList){
			List<String> vList = (List<String>) value;
			key = vList.get(0);
		}else{
			key = null;
		}
		return key;
	}
	
	private void addElementToResults(Object element){
		String key = this.getElementKey(element);
		if(key==null || !zResultInfo.getResultLevel().contains(key)){
			zResultInfo.getResultsTemp().add(element);
			if(key!=null){
				zResultInfo.getResultLevel().add(key);
			}
		}
	}
	
	/**
	 * 相对路径下去遍历level，然后取值
	 * @param key
	 * @param pathInfo
	 */
	@SuppressWarnings("unchecked")
	protected void getRelativeObjectsByKey(String key, ZsonPathInfo pathInfo){
		for (String l : zResultInfo.getLevel()) {
			if(this.compareKey(key, l)<=0){
				Map<String, Integer> indexInfo = zResultInfo.getIndex().get(l);
				Object actualValue = zResultInfo.getCollections().get(indexInfo.get(ZsonUtils.INDEX));
				if(pathInfo.isPathIsList() && indexInfo.get(ZsonUtils.TYPE)==1){
					List<Object> actualList = (List<Object>) actualValue;
					if(pathInfo.getPathListIndex()<=actualList.size()-1){
						Object element = actualList.get(pathInfo.getPathListIndex());
						this.addElementToResults(element);
					}
				}else if(!pathInfo.isPathIsList() && indexInfo.get(ZsonUtils.TYPE)==0){
					Map<String, Object> actualMap = (Map<String, Object>) actualValue;
					if(actualMap.containsKey(pathInfo.getPathKey())){
						Object element = actualMap.get(pathInfo.getPathKey());
						this.addElementToResults(element);
					}
				}
			}
		}
	}
	
	/**
	 * 绝对路径下直接取值
	 * @param key
	 * @param pathInfo
	 */
	@SuppressWarnings("unchecked")
	protected void getObjectsByKey(String key, ZsonPathInfo pathInfo){
		Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
		Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
		if(elementStatus.get(ZsonUtils.TYPE)==0){
			Map<String, Object> elementMap = (Map<String, Object>) elementObj; 
			Object element = elementMap.get(pathInfo.getPathKey());
			this.addElementToResults(element);
		}else{
			List<Object> elementList = (List<Object>) elementObj; 
			Object element = elementList.get(pathInfo.getPathListIndex());
			this.addElementToResults(element);
		}
	}
	
	private int compareKey(String key1, String key2){
		if(key2.indexOf(key1)==0){
			return -1;
		}else{
			return 1;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void removeElement(List<String> list, int index, Object element, Object v, boolean isRemove, String currentKey){
		if(isRemove){
			if(index==list.size()-1){
				if(element instanceof HashMap){
					((HashMap) element).remove(v.toString());
				}else if(element instanceof ArrayList){
					int vIndex = Integer.valueOf(v.toString());
					((ArrayList) element).remove(vIndex);
				}else{
					throw new RuntimeException("path is not valid!");
				}
				zResultInfo.getIndex().remove(currentKey);
				zResultInfo.getLevel().remove(currentKey);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object resultHandle(String path, boolean isRemove, boolean isGetObject, boolean isUpdate, Object updateValue){
		this.checkValid();
		zPath.setPath(path);
		List<String> list = zPath.getXpath();
		String key = ZsonUtils.BEGIN_KEY;
		Object value = null;
		int index = 0;
		Object pValue = null;//defined for update value
		String lastKey = null;//defined for update value
		for (String k : list) {
			Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
			Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
			if(elementStatus.get(ZsonUtils.TYPE)==0){
				Map<String, Object> elementMap = (Map<String, Object>) elementObj; 
				if(!elementMap.containsKey(k)){
					throw new RuntimeException("path is not valid!");
				}
				value = elementMap.get(k);
			}else{
				List<Object> elementList = (List<Object>) elementObj;
				int listIndex = Integer.valueOf(k);
				if(listIndex > elementList.size()-1){
					throw new RuntimeException("path is not valid!");
				}
				value = elementList.get(listIndex);
			}
			key = this.getElementKey(value);
			this.removeElement(list, index, elementObj, k, isRemove, key);
			if(isUpdate){
				if(index==list.size()-1){
					pValue = elementObj;
					lastKey = k;
				}
			}
			index++;
			if(isGetObject || isUpdate){
				if(key==null){
					break;
				}
			}
		}
		if(isGetObject || isUpdate){
			if(index!=list.size()){
				throw new RuntimeException("path is not valid!");
			}else{
				if(isGetObject){
					if(value instanceof HashMap || value instanceof ArrayList){
						Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
						Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
						value = zsonResultRestore.restoreObject(elementObj);
					}else if(value instanceof String){
						value = ZsonUtils.convert((String) value);
					}
				}else{
					if(value instanceof HashMap || value instanceof ArrayList){
						throw new RuntimeException("path is not valid!");
					}
					if(pValue instanceof HashMap){
						((HashMap) pValue).put(lastKey, updateValue);
					}else if(pValue instanceof ArrayList){
						((ArrayList) pValue).set(Integer.valueOf(lastKey), updateValue);
					}
				}
			}
			return value;
		}
		return null;
	}
	
}

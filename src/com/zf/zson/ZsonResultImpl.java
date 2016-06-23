package com.zf.zson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ZsonResultImpl implements ZsonResult{
	
	private ZsonResultInfo zResultInfo;
	
	private ZsonPath zPath;
	
	public ZsonResultImpl(){
		zResultInfo = new ZsonResultInfo();
		zPath = new ZsonPath();
	}
	
	public ZsonResultInfo getzResultInfo() {
		return zResultInfo;
	}

	public boolean isValid(){
		if(!zResultInfo.isValid() || zResultInfo.getCollections().size()==0){
			return zResultInfo.isValid();
		}
		if(!zResultInfo.isAllFinished()){
			Collection<Map<String, Integer>> values = zResultInfo.getIndex().values();
			for (Map<String, Integer> map : values) {
				if(map.get(ZsonUtils.STATUS)==0){
					zResultInfo.setValid(false);
					zResultInfo.setAllFinished(true);
					return zResultInfo.isValid();
				}
			}
		}
		return zResultInfo.isValid();
	}
	
	@SuppressWarnings("unchecked")
	private String getElementKey(Object value){
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
	private void getRelativeObjectsByKey(String key, ZsonPathInfo pathInfo){
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
	private void getObjectsByKey(String key, ZsonPathInfo pathInfo){
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
	
	@SuppressWarnings("unchecked")
	private void removeResultInfo(String path){
		if(!this.isValid()){
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		zPath.setPath(path);
		List<String> list = zPath.getXpath();
		String key = ZsonUtils.BEGIN_KEY;
		Object value = null;
		int index = 0;
		for (String k : list) {
			Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
			Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
			if(elementStatus.get(ZsonUtils.TYPE)==0){
				Map<String, Object> elementMap = (Map<String, Object>) elementObj; 
				value = elementMap.get(k);
				if(index==list.size()-1){
					elementMap.remove(k);
				}
			}else{
				List<Object> elementList = (List<Object>) elementObj; 
				value = elementList.get(Integer.valueOf(k));
				if(index==list.size()-1){
					elementList.remove(k);
				}
			}
			key = this.getElementKey(value);
			index++;
		}
		zResultInfo.getIndex().remove(key);
		zResultInfo.getLevel().remove(key);
	}
	
	@SuppressWarnings("unchecked")
	private Object getObject(String path){
		if(!this.isValid()){
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		zPath.setPath(path);
		List<String> list = zPath.getXpath();
		String key = ZsonUtils.BEGIN_KEY;
		Object value = null;
		int index = 0;
		for (String k : list) {
			Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
			Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
			if(elementStatus.get(ZsonUtils.TYPE)==0){
				Map<String, Object> elementMap = (Map<String, Object>) elementObj; 
				value = elementMap.get(k);
			}else{
				List<Object> elementList = (List<Object>) elementObj; 
				value = elementList.get(Integer.valueOf(k));
			}
			key = this.getElementKey(value);
			index++;
			if(key==null){
				break;
			}
		}
		if(index!=list.size()){
			throw new RuntimeException("path is not valid!");
		}else{
			if(value instanceof HashMap || value instanceof ArrayList){
				Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
				Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
				value = this.restoreObject(elementObj);
			}else if(value instanceof String){
				value = ZsonUtils.convert((String) value);
			}
		}
		return value;
	}
	
	public Object getValue(String path){
		Object obj = this.getObject(path);
		if(obj instanceof Map || obj instanceof List){
			return this.toJsonString(obj);
		}else{
			return obj;
		}
	}
	
	private List<Object> getObjects(String path){
		if(!this.isValid()){
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		zPath.setPath(path);
		List<ZsonPathInfo> list = zPath.getRelativePath();
		int index = 0;
		for (ZsonPathInfo pathInfo : list) {
			List<Object> resultList = zResultInfo.getResults();
			int len = resultList.size();
			if(index>0 && len==0){
				break;
			}
			if(resultList.size()==0){
				String key = ZsonUtils.BEGIN_KEY;
				if(pathInfo.getPathKeyIsRelative()){
					this.getRelativeObjectsByKey(key, pathInfo);
				}else{
					this.getObjectsByKey(key, pathInfo);
				}
			}else{
				Iterator<Object> it = resultList.iterator();
				while(it.hasNext()) {
					Object element = it.next();
					if(element instanceof HashMap || element instanceof ArrayList){
						String key = this.getElementKey(element);
						if(pathInfo.getPathKeyIsRelative()){
							this.getRelativeObjectsByKey(key, pathInfo);
						}else{
							this.getObjectsByKey(key, pathInfo);
						}
					}
				}
			}
			this.convertResult();
			index++;
		}
		//去掉MAP或LIST，只保留非MAP或LIST的结果
		Iterator<Object> it = zResultInfo.getResults().iterator();
		while(it.hasNext()){
			Object element = it.next();
			if(element instanceof Map || element instanceof List){
				it.remove();
			}
		}
		return zResultInfo.getResults();
	}
	
	private void convertResult(){
		zResultInfo.setResults(zResultInfo.getResultsTemp());
		zResultInfo.setResultsTemp(new ArrayList<Object>());
	}
	
	public List<Object> getValues(String path){
		zResultInfo.getResults().clear();
		zResultInfo.getResultsTemp().clear();
		zResultInfo.getResultLevel().clear();
		return this.getObjects(path);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMap(String path){
		Object obj = this.getObject(path);
		if(obj instanceof Map){
			return (Map<String, Object>) obj;
		}else{
			throw new RuntimeException(obj.getClass().toString()+" can not cast to map!");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getList(String path){
		Object obj = this.getObject(path);
		if(obj instanceof List){
			return (List<Object>) obj;
		}else{
			throw new RuntimeException(obj.getClass().toString()+" can not cast to list!");
		}
	}
	
	@SuppressWarnings("unchecked")
	private Object restoreObject(Object obj){
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
				String key = this.getElementKey(mapValue);
				Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
				Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
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
				String key = this.getElementKey(e);
				Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
				Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
				restore.add(this.restoreObject(elementObj));
			}else{
				restore.add(e);
			}
		}
		return restore;
	}
	
	@SuppressWarnings("unchecked")
	public String toJsonString(Object obj){
		if(obj instanceof Map){
			return this.mapToString((Map<Object, Object>) obj);
		}else if(obj instanceof List){
			return this.listToString((List<Object>) obj);
		}else{
			throw new RuntimeException("obj must be map or list!");
		}
	}
	
	private String mapToString(Map<Object, Object> map){
		StringBuffer sb = new StringBuffer();
		sb.append(ZsonUtils.jsonMapBegin);
		int index = 0;
		if(map!=null){
			for (Object key : map.keySet()) {
				if(!(key instanceof String)){
					throw new RuntimeException("map key must be string!");
				}
				if(index!=0){
					sb.append(ZsonUtils.jsonElementConnector);
				}
				sb.append(ZsonUtils.jsonStringBegin);
				sb.append(key);
				sb.append(ZsonUtils.jsonStringEnd);
				sb.append(ZsonUtils.jsonMapConnector);
				if(map.get(key) instanceof Map || map.get(key) instanceof List){
					String mapString = this.toJsonString(map.get(key));
					sb.append(mapString);
				}else if(map.get(key) instanceof String){
					sb.append(ZsonUtils.jsonStringBegin);
					sb.append(map.get(key));
					sb.append(ZsonUtils.jsonStringEnd);
				}else{
					sb.append(map.get(key));
				}
				index++;
			}
		}
		sb.append(ZsonUtils.jsonMapEnd);
		return sb.toString();
	}
	
	private String listToString(List<Object> list){
		StringBuffer sb = new StringBuffer();
		sb.append(ZsonUtils.jsonListBegin);
		int index = 0;
		if(list!=null){
			for (Object element : list) {
				if(index!=0){
					sb.append(ZsonUtils.jsonElementConnector);
				}
				if(element instanceof Map || element instanceof List){
					String mapString = this.toJsonString(element);
					sb.append(mapString);
				}else if(element instanceof String){
					sb.append(ZsonUtils.jsonStringBegin);
					sb.append(element);
					sb.append(ZsonUtils.jsonStringEnd);
				}else{
					sb.append(element);
				}
				index++;
			}
		}
		sb.append(ZsonUtils.jsonListEnd);
		return sb.toString();
	}
	
	private int compareKey(String key1, String key2){
		if(key2.indexOf(key1)==0){
			return -1;
		}else{
			return 1;
		}
	}

	@Override
	public int getInteger(String path) {
		Object obj = this.getValue(path);
		if(obj instanceof Long){
			return new Long((Long)obj).intValue();
		}else{
			throw new RuntimeException("can not get int with path: "+path);
		}
	}

	@Override
	public long getLong(String path) {
		Object obj = this.getValue(path);
		if(obj instanceof Long){
			return (Long) obj;
		}else{
			throw new RuntimeException("can not get long with path: "+path);
		}
	}

	@Override
	public double getDouble(String path) {
		Object obj = this.getValue(path);
		if(obj instanceof BigDecimal){
			BigDecimal bigDecimal = (BigDecimal)obj;
			return bigDecimal.doubleValue();
		}else{
			throw new RuntimeException("can not get double with path: "+path);
		}
	}

	@Override
	public float getFloat(String path) {
		Object obj = this.getValue(path);
		if(obj instanceof BigDecimal){
			return ((BigDecimal)obj).floatValue();
		}else{
			throw new RuntimeException("can not get float with path: "+path);
		}
	}

	@Override
	public boolean getBoolean(String path) {
		Object obj = this.getValue(path);
		if(obj instanceof Boolean){
			return (Boolean)obj;
		}else{
			throw new RuntimeException("can not get boolean with path: "+path);
		}
	}

	@Override
	public String getString(String path) {
		Object obj = this.getValue(path);
		if(obj instanceof String){
			return (String)obj;
		}else{
			throw new RuntimeException("can not get String with path: "+path);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getResult() {
		Map<String, Integer> elementStatus = zResultInfo.getIndex().get(ZsonUtils.BEGIN_KEY);
		Object obj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
		return this.restoreObject((Map<Object, Object>) obj);
	}

	@Override
	public void removeValue(String path) {
		this.removeResultInfo(path);
	}
	
}

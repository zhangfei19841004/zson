package com.zf.zson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZsonResult {
	
	private boolean valid = true;
	
	/**
	 * 存放解析JSON过程中所有的LIST与MAP
	 */
	private List<Object> collections = new ArrayList<Object>();
	
	/**
	 * index的最外层MAP的key为1 1.1 1.1.2这种形式，表示JSON的层次结构
	 * 里面的MAP为当前层次中的数据结构类型与状态，比如{"type":0,"status":0,"index":0},
	 * type有0,1，0表示MAP, 1表示LIST, 
	 * status有0，1，0表示没有解析完成，1表示已解析完成
	 * index指对象在collections中的index
	 */
	private Map<String, Map<String, Integer>> index = new HashMap<String, Map<String, Integer>>();
	
	private boolean allFinished = false;
	
	/**
	 * 存放JSON的层次结构比如1 1.1 1.1.2
	 */
	private List<String> level = new ArrayList<String>();
	
	public List<Object> getCollections() {
		return collections;
	}

	public void setCollections(List<Object> collections) {
		this.collections = collections;
	}

	public Map<String, Map<String, Integer>> getIndex() {
		return index;
	}

	public void setIndex(Map<String, Map<String, Integer>> index) {
		this.index = index;
	}

	public boolean isAllFinished() {
		return allFinished;
	}

	public void setAllFinished(boolean allFinished) {
		this.allFinished = allFinished;
	}

	public List<String> getLevel() {
		return level;
	}

	public void setLevel(List<String> level) {
		this.level = level;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	private ZsonPath zPath = new ZsonPath();

	public boolean isValid(){
		if(!valid || collections.size()==0){
			return valid;
		}
		if(!allFinished){
			Collection<Map<String, Integer>> values = index.values();
			for (Map<String, Integer> map : values) {
				if(map.get(ZsonUtils.STATUS)==0){
					valid = false;
					allFinished = true;
					return valid;
				}
			}
		}
		return valid;
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
			Map<String, Integer> elementStatus = this.getIndex().get(key);
			Object elementObj = this.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
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
				Map<String, Integer> elementStatus = this.getIndex().get(key);
				Object elementObj = this.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
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
				Map<String, Integer> elementStatus = this.getIndex().get(key);
				Object elementObj = this.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
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
				Map<String, Integer> elementStatus = this.getIndex().get(key);
				Object elementObj = this.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
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
	
	public static void main(String[] args) {
		ZsonResult zr = new ZsonResult();
		Map<String,Object> map1 = new HashMap<String,Object>();
		map1.put("a", "a");
		map1.put("b", 1);
		map1.put("c", null);
		Map<String,Object> map2 = new HashMap<String,Object>();
		map2.put("a", "a");
		map1.put("d", map2);
		map1.put("e", new HashMap<String,Object>());
		List<Object> list = new ArrayList<Object>(); 
		list.add("1");
		list.add(3);
		list.add(map2);
		list.add(map1);
		System.out.println(zr.toJsonString(list));
	}
	
}

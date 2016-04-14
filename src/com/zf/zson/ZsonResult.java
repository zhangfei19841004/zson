package com.zf.zson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ZsonResult {
	
	private ZsonResultInfo zResultInfo;
	
	private ZsonPath zPath;
	
	public ZsonResult(){
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
	
	@SuppressWarnings("unchecked")
	private void getObjectsByKey(String key, ZsonPathInfo pathInfo){
		for (String l : zResultInfo.getLevel()) {
			if(this.compareKey(key, l)<=0){
				Map<String, Integer> indexInfo = zResultInfo.getIndex().get(l);
				Object actualValue = zResultInfo.getCollections().get(indexInfo.get(ZsonUtils.INDEX));
				if(pathInfo.isPathIsList() && indexInfo.get(ZsonUtils.TYPE)==1){
					List<Object> actualList = (List<Object>) actualValue;
					if(pathInfo.getPathListIndex()<=actualList.size()-1){
						zResultInfo.getResults().add(actualList.get(pathInfo.getPathListIndex()));
					}
				}else if(!pathInfo.isPathIsList() && indexInfo.get(ZsonUtils.TYPE)==0){
					Map<String, Object> actualMap = (Map<String, Object>) actualValue;
					if(actualMap.containsKey(pathInfo.getPathKey())){
						zResultInfo.getResults().add(actualMap.get(pathInfo.getPathKey()));
					}
				}
			}
		}
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
	
	@SuppressWarnings("unchecked")
	private List<Object> getObjects(String path){
		if(!this.isValid()){
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		zPath.setPath(path);
		List<ZsonPathInfo> list = zPath.getRelativePath();
		for (ZsonPathInfo pathInfo : list) {
			if(pathInfo.getPathKeyIsRelative()){
				List<Object> resultList = zResultInfo.getResults();
				if(resultList.size()==0){
					String key = ZsonUtils.BEGIN_KEY;
					this.getObjectsByKey(key, pathInfo);
				}else{
					Iterator<Object> it = resultList.iterator();
					while(it.hasNext()){
						Object element = it.next();
						if(element instanceof HashMap || element instanceof ArrayList){
							String key = this.getElementKey(element);
							this.getObjectsByKey(key, pathInfo);
						}else{
							it.remove();
						}
					}
				}
			}else{
				List<Object> resultList = zResultInfo.getResults();
				if(resultList.size()==0){
					String key = ZsonUtils.BEGIN_KEY;
					Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
					Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
					if(elementStatus.get(ZsonUtils.TYPE)==0){
						Map<String, Object> elementMap = (Map<String, Object>) elementObj; 
						zResultInfo.getResults().add(elementMap.get(pathInfo.getPathKey()));
					}else{
						List<Object> elementList = (List<Object>) elementObj; 
						zResultInfo.getResults().add(elementList.get(pathInfo.getPathListIndex()));
					}
				}else{
					Iterator<Object> it = resultList.iterator();
					while(it.hasNext()){
						Object element = it.next();
						if(element instanceof HashMap || element instanceof ArrayList){
							String key = this.getElementKey(element);
							Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
							Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
							if(elementStatus.get(ZsonUtils.TYPE)==0){
								Map<String, Object> elementMap = (Map<String, Object>) elementObj; 
								zResultInfo.getResults().add(elementMap.get(pathInfo.getPathKey()));
							}else{
								List<Object> elementList = (List<Object>) elementObj; 
								zResultInfo.getResults().add(elementList.get(pathInfo.getPathListIndex()));
							}
						}else{
							it.remove();
						}
					}
				}
				
			}
			
		}
		
		return zResultInfo.getResults();
	}
	
	public List<Object> getValues(String path){
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
		String[] keys1 = key1.split("\\.");
		String[] keys2 = key2.split("\\.");
		if(keys1.length<keys2.length){
			return -1;
		}else if(keys1.length>keys2.length){
			return 1;
		}
		for (int i = 0; i < keys1.length; i++) {
			if(Integer.valueOf(keys1[i])>Integer.valueOf(keys2[i])){
				return 1;
			}else if(Integer.valueOf(keys1[i])<Integer.valueOf(keys2[i])){
				return -1;
			}
		}
		return 0;
	}
	
//	private boolean isChildKey(String key1, String key2){
//		if(ZsonUtils.BEGIN_KEY.equals(key1)){
//			return true;
//		}
//		if(key2.indexOf(key1)==0){
//			return true;
//		}else{
//			return false;
//		}
//	}
	
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

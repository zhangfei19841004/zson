package com.zf.zson.result.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZSON;
import com.zf.zson.ZsonUtils;
import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResult;

public class ZsonAdd implements ZsonAction{
	
	private String addJson;
	
	private String addKey;
	
	private int addIndex;
	
	private List<String> handledPath = new ArrayList<String>();
	
	private String addRootPath;
	
	public void setAddJson(String addJson) {
		this.addJson = addJson;
	}

	public void setAddKey(String addKey) {
		this.addKey = addKey;
	}

	public void setAddIndex(int addIndex) {
		this.addIndex = addIndex;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(ZsonResult zr, Object value, String currentPath) {
		if(handledPath.contains(currentPath)){
			return;
		}
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		String key = zri.getElementKey(value);
		if(key == null){
			//throw new RuntimeException("can not add value to this path: "+zri.getzPath().getPath());
			return;
		}
		//String parentPath = this.getParentPath(zri, key);
		Object pathValue = zri.getResultByKey(key);
		if(pathValue instanceof List){
			List<Object> pathList = (List<Object>) pathValue;
			ZsonResultImpl zra = (ZsonResultImpl) zri.parseJsonToZson(addJson);
			Object actionValue = zra.getResultByKey(ZsonUtils.BEGIN_KEY);
			pathList.add(addIndex, actionValue);
			addRootPath = currentPath+"/*["+addIndex+"]";
			handledPath.add(currentPath);
			handledPath.add(addRootPath);
			ZsonResultImpl zrNew = (ZsonResultImpl) zri.parseJsonToZson(ZSON.toJsonString(pathList));
			this.deleteZsonResultInfoChilrenKey(zri, key);
			this.replaceZsonResultInfoKey(zrNew, key, currentPath);
			this.addNewResultToSourceResult(zri, zrNew);
			this.recorrectIndex(zri);
		}
		System.out.println(ZSON.toJsonString(zri.getResultByKey("1")));
	}
	
	private void addNewResultToSourceResult(ZsonResultImpl source, ZsonResultImpl newResult){
		source.getzResultInfo().getLevel().addAll(addIndex, newResult.getzResultInfo().getLevel());
		source.getzResultInfo().getPath().addAll(addIndex, newResult.getzResultInfo().getPath());
		source.getzResultInfo().getIndex().putAll(newResult.getzResultInfo().getIndex());
		source.getzResultInfo().getCollections().addAll(addIndex, newResult.getzResultInfo().getCollections());
	}
	
	private void deleteZsonResultInfoChilrenKey(ZsonResultImpl zri, String key){
		Iterator<String> it = zri.getzResultInfo().getLevel().iterator();
		int index = 0;
		while(it.hasNext()){
			String level = it.next();
			if(level.matches(key.replaceAll("\\.", "\\\\.")+"(\\.\\d+)*")){
				it.remove();
				zri.getzResultInfo().getPath().remove(index);
				zri.getzResultInfo().getIndex().remove(level);
				zri.getzResultInfo().getCollections().remove(index);
				index--;
			}
			index++;
		}
	}
	
	private void replaceZsonResultInfoKey(ZsonResultImpl zrNew, String targetKey, String parentPath){
		List<String> levels = zrNew.getzResultInfo().getLevel();
		Map<String, Map<String, Integer>> newIndex = new HashMap<String, Map<String, Integer>>();
		for (int i = 0; i < levels.size(); i++) {
			String key = levels.get(i);
			String newLevel = targetKey+levels.get(i).substring(1);
			levels.set(i, newLevel);
			List<Object> paths = zrNew.getzResultInfo().getPath();
			this.updatePaths(zrNew, paths.get(i), parentPath);
			Map<String, Map<String, Integer>> index = zrNew.getzResultInfo().getIndex();
			this.updateIndexs(index, newIndex, key, targetKey);
			this.updateCollections(zrNew.getzResultInfo().getCollections().get(i), targetKey);
		}
		zrNew.getzResultInfo().setIndex(newIndex);
	}
	
	private void recorrectIndex(ZsonResultImpl zri){
		List<String> levels = zri.getzResultInfo().getLevel();
		for (int i = 0; i < levels.size(); i++) {
			zri.getzResultInfo().getIndex().get(levels.get(i)).put(ZsonUtils.INDEX, i);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updatePaths(ZsonResultImpl zrNew, Object paths, String parentPath){
		if(paths instanceof Map){
			Map<String, String> pathMap = (Map<String, String>) paths;
			for (String k : pathMap.keySet()) {
				String newPath = parentPath+pathMap.get(k);
				pathMap.put(k, newPath);
				if(zrNew.getzPath().ischildPath(addRootPath, newPath)){
					handledPath.add(newPath);
				}
			}
		}else if(paths instanceof List){
			List<String> pathList = (List<String>) paths;
			for (int i = 0; i < pathList.size(); i++) {
				String newPath = parentPath+pathList.get(i);
				pathList.set(i, newPath);
				if(zrNew.getzPath().ischildPath(addRootPath, newPath)){
					handledPath.add(newPath);
				}
			}
		}
	}
	
	private Map<String, Map<String, Integer>> updateIndexs(Map<String, Map<String, Integer>> index, Map<String, Map<String, Integer>> newIndex, String key, String targetKey){
		Map<String, Integer> indexInfo = index.get(key);
		indexInfo.put(ZsonUtils.INDEX, indexInfo.get(ZsonUtils.INDEX)+addIndex);
		String newKey = targetKey+key.substring(1);
		newIndex.put(newKey, indexInfo);
		return newIndex;
	}
	
	@SuppressWarnings("unchecked")
	private void updateCollections(Object collection, String targetKey){
		if(collection instanceof Map){
			Map<String, Object> collectionMap = (Map<String, Object>) collection;
			for (String k : collectionMap.keySet()) {
				Object cObj = collectionMap.get(k);
				if(cObj instanceof Map){
					Map<String, String> cMap = (Map<String, String>) cObj;
					cMap.put(ZsonUtils.LINK, targetKey+cMap.get(ZsonUtils.LINK).substring(1));
				}else if(cObj instanceof List){
					List<String> cList = (List<String>) cObj;
					cList.set(0, targetKey+cList.get(0).substring(1));
				}
			}
		}else if(collection instanceof List){
			List<Object> collectionList = (List<Object>) collection;
			for (Object cObj : collectionList) {
				if(cObj instanceof Map){
					Map<String, String> cMap = (Map<String, String>) cObj;
					cMap.put(ZsonUtils.LINK, targetKey+cMap.get(ZsonUtils.LINK).substring(1));
				}else if(cObj instanceof List){
					List<String> cList = (List<String>) cObj;
					cList.set(0, targetKey+cList.get(0).substring(1));
				}
			}
		}
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}
	
	/*private Map<String, Integer> getIndexInfoByKey(ZsonResultImpl zri, String key){
		if(zri.getzResultInfo().getIndex().containsKey(key)){
			return zri.getzResultInfo().getIndex().get(key);
		}else{
			zri.getzResultInfo().setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}*/
	
	/*@SuppressWarnings("unchecked")
	private String getParentPath(ZsonResultImpl zri, String key){
		if(ZsonUtils.BEGIN_KEY.equals(key)){
			return "";
		}
		String pKey = key.substring(0, key.lastIndexOf('.'));
		Map<String, Integer> pIndexInfo = this.getIndexInfoByKey(zri, pKey);
		int pType = pIndexInfo.get(ZsonUtils.TYPE);
		int pIndex = pIndexInfo.get(ZsonUtils.INDEX);
		if(pType==0){
			Map<String,Object> pElement = (Map<String, Object>) zri.getzResultInfo().getCollections().get(pIndexInfo.get(ZsonUtils.INDEX));
			for (String k : pElement.keySet()) {
				if(pElement.get(k) instanceof Map){
					if(key.equals(((Map<String,String>)pElement.get(k)).get(ZsonUtils.LINK))){
						return ((Map<String,String>)zri.getzResultInfo().getPath().get(pIndex)).get(k);
					}
				}else if(pElement.get(k) instanceof List){
					if(key.equals(((List<String>)pElement.get(k)).get(0))){
						return ((Map<String,String>)zri.getzResultInfo().getPath().get(pIndex)).get(k);
					}
				}
			}
		}else{
			List<Object> pElement = (List<Object>) zri.getzResultInfo().getCollections().get(pIndexInfo.get(ZsonUtils.INDEX));
			for (int i=0; i<pElement.size();i++) {
				Object object = pElement.get(i);
				if(object instanceof HashMap){
					if(key.equals(((Map<String,String>)object).get(ZsonUtils.LINK))){
						return ((List<String>)zri.getzResultInfo().getPath().get(pIndex)).get(i);
					}
				}else if(object instanceof ArrayList){
					if(key.equals(((List<String>)object).get(0))){
						return ((List<String>)zri.getzResultInfo().getPath().get(pIndex)).get(i);
					}
				}
			}
		}
		throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
	}*/
	
	public static void main(String[] args) {
		String p = "/a/*[1]";
		String p1 = "/a/*[1]/a";
		String regPath = p.replaceAll("\\/", "\\\\/").replaceAll("\\*", "\\\\*").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\\\/\\\\/", "(/.+)*\\\\/");
		System.out.println(p1.matches(regPath+"/.+"));
	}
}

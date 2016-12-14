package com.zf.zson.result.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZSON;
import com.zf.zson.ZsonUtils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResult;

public class ZsonAdd implements ZsonAction{
	
	private String addJson;
	
	private String addKey;
	
	private Integer addIndex;
	
	private List<String> handledPath = new ArrayList<String>();
	
	private String addRootPath;
	
	private int deleteFromIndex;
	
	public void setAddJson(String addJson) {
		this.addJson = addJson;
	}

	public void setAddKey(String addKey) {
		this.addKey = addKey;
	}

	public void setAddIndex(Integer addIndex) {
		this.addIndex = addIndex;
	}

	@Override
	public void process(ZsonResult zr, Object value, String currentPath) {
		if(handledPath.contains(currentPath)){
			return;
		}
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		String key = zri.getElementKey(value);
		if(key == null){
			return;
		}
		//String parentPath = this.getParentPath(zri, key);
		Object pathValue = zri.getResultByKey(key);
		ZsonObject<Object> valueByKeyObj = new ZsonObject<Object>();
		valueByKeyObj.objectConvert(pathValue);
		if((addIndex!=null && valueByKeyObj.isList()) || (addKey!=null && valueByKeyObj.isMap())){
			Object addObj = null;
			if(addIndex!=null && valueByKeyObj.isList()){
				List<Object> pathValueList = valueByKeyObj.getZsonList();
				Object actionValue = this.getAddObject(zri);
				pathValueList.add(addIndex, actionValue);
				addRootPath = currentPath+"/*["+addIndex+"]";
				addObj = pathValueList;
				
			}else if(addKey!=null && valueByKeyObj.isMap()){
				Map<String, Object> pathValueMap = valueByKeyObj.getZsonMap();
				Object actionValue = this.getAddObject(zri);
				pathValueMap.put(addKey, actionValue);
				addRootPath = currentPath+"/"+addKey;
				addObj = pathValueMap;
			}
			handledPath.add(currentPath);
			handledPath.add(addRootPath);
			ZsonResultImpl zrNew = (ZsonResultImpl) zri.parseJsonToZson(ZSON.toJsonString(addObj));
			this.deleteZsonResultInfoChilrenKey(zri, key);
			this.replaceZsonResultInfoKey(zrNew, key, currentPath);
			this.addNewResultToSourceResult(zri, zrNew);
			this.recorrectIndex(zri);
		}
	}
	
	private Object getAddObject(ZsonResultImpl zri){
		Object actionValue = addJson;
		try{
			ZsonResultImpl zra = (ZsonResultImpl) zri.parseJsonToZson(addJson);
			actionValue = zra.getResultByKey(ZsonUtils.BEGIN_KEY);
		}catch(Exception e){
			
		}
		return actionValue;
	}
	
	private void addNewResultToSourceResult(ZsonResultImpl source, ZsonResultImpl newResult){
		source.getzResultInfo().getLevel().addAll(deleteFromIndex, newResult.getzResultInfo().getLevel());
		source.getzResultInfo().getPath().addAll(deleteFromIndex, newResult.getzResultInfo().getPath());
		source.getzResultInfo().getIndex().putAll(newResult.getzResultInfo().getIndex());
		source.getzResultInfo().getCollections().addAll(deleteFromIndex, newResult.getzResultInfo().getCollections());
	}
	
	private void deleteZsonResultInfoChilrenKey(ZsonResultImpl zri, String key){
		Iterator<String> it = zri.getzResultInfo().getLevel().iterator();
		int index = 0;
		boolean flag = false;
		while(it.hasNext()){
			String level = it.next();
			if(level.matches(key.replaceAll("\\.", "\\\\.")+"(\\.\\d+)*")){
				if(!flag){
					deleteFromIndex = index;
					flag = true;
				}
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
	
	private void updatePaths(ZsonResultImpl zrNew, Object paths, String parentPath){
		ZsonObject<String> pathObj = new ZsonObject<String>();
		pathObj.objectConvert(paths);
		if(pathObj.isMap()){
			Map<String, String> pathMap = pathObj.getZsonMap();
			for (String k : pathMap.keySet()) {
				String newPath = parentPath+pathMap.get(k);
				pathMap.put(k, newPath);
				if(zrNew.getzPath().ischildPath(addRootPath, newPath)){
					handledPath.add(newPath);
				}
			}
		}else if(pathObj.isList()){
			List<String> pathList = pathObj.getZsonList();
			for (int i = 0; i < pathList.size(); i++) {
				String newPath = parentPath+pathList.get(i);
				pathList.set(i, newPath);
				if(zrNew.getzPath().ischildPath(addRootPath, newPath)){
					handledPath.add(newPath);
				}
			}
		}
	}
	
	private void updateIndexs(Map<String, Map<String, Integer>> index, Map<String, Map<String, Integer>> newIndex, String key, String targetKey){
		Map<String, Integer> indexInfo = index.get(key);
		//indexInfo.put(ZsonUtils.INDEX, indexInfo.get(ZsonUtils.INDEX)+addIndex);
		String newKey = targetKey+key.substring(1);
		newIndex.put(newKey, indexInfo);
	}
	
	private void updateCollections(Object collection, String targetKey){
		ZsonObject<Object> collectionObj = new ZsonObject<Object>();
		collectionObj.objectConvert(collection);
		if(collectionObj.isMap()){
			Map<String, Object> collectionMap = collectionObj.getZsonMap();
			for (String k : collectionMap.keySet()) {
				ZsonObject<String> cElementObj = new ZsonObject<String>();
				cElementObj.objectConvert(collectionMap.get(k));
				if(cElementObj.isMap()){
					Map<String, String> cMap = cElementObj.getZsonMap();
					cMap.put(ZsonUtils.LINK, targetKey+cMap.get(ZsonUtils.LINK).substring(1));
				}else if(cElementObj.isList()){
					List<String> cList = cElementObj.getZsonList();
					cList.set(0, targetKey+cList.get(0).substring(1));
				}
			}
		}else if(collectionObj.isList()){
			List<Object> collectionList = collectionObj.getZsonList();
			for (Object cObj : collectionList) {
				ZsonObject<String> cElementObj = new ZsonObject<String>();
				cElementObj.objectConvert(cObj);
				if(cElementObj.isMap()){
					Map<String, String> cMap = cElementObj.getZsonMap();
					cMap.put(ZsonUtils.LINK, targetKey+cMap.get(ZsonUtils.LINK).substring(1));
				}else if(cElementObj.isList()){
					List<String> cList = cElementObj.getZsonList();
					cList.set(0, targetKey+cList.get(0).substring(1));
				}
			}
		}
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}
	
	public static void main(String[] args) {
		String p = "/a/*[1]";
		String p1 = "/a/*[1]/a";
		String regPath = p.replaceAll("\\/", "\\\\/").replaceAll("\\*", "\\\\*").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\\\/\\\\/", "(/.+)*\\\\/");
		System.out.println(p1.matches(regPath+"/.+"));
	}
}

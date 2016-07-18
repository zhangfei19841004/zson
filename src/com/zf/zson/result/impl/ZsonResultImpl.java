package com.zf.zson.result.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZsonUtils;
import com.zf.zson.path.ZsonPathInfo;
import com.zf.zson.result.ZsonResult;
import com.zf.zson.result.ZsonResultAbstract;

public class ZsonResultImpl extends ZsonResultAbstract implements ZsonResult{
	
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
	
	public void checkValid(){
		if(!this.isValid()){
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}
	
	public Object getValue(String path){
		Object obj = this.resultHandle(path, false, true, false, null);
		if(obj instanceof Map || obj instanceof List){
			return this.toJsonString(obj);
		}else{
			return obj;
		}
	}
	
	private List<Object> getObjects(String path){
		this.checkValid();
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
		Object obj = this.resultHandle(path, false, true, false, null);
		if(obj instanceof Map){
			return (Map<String, Object>) obj;
		}else{
			throw new RuntimeException(obj.getClass().toString()+" can not cast to map!");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getList(String path){
		Object obj = this.resultHandle(path, false, true, false, null);
		if(obj instanceof List){
			return (List<Object>) obj;
		}else{
			throw new RuntimeException(obj.getClass().toString()+" can not cast to list!");
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
		return zsonResultRestore.restoreObject((Map<Object, Object>) obj);
	}

	@Override
	public void removeValue(String path) {
		this.resultHandle(path,true, false, false, null);
	}

	@Override
	public void updateValue(String path, Object v) {
		this.resultHandle(path,false, false, true, v);
	}

	@Override
	public String toJsonString(Object obj) {
		return zsonResultToString.toJsonString(obj);
	}
}

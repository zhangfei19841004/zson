package com.zf.zson.object;

import java.util.List;
import java.util.Map;

public class ZsonObject<T> {
	
	private Map<String, T> zsonMap;
	
	private List<T> zsonList;
	
	private boolean isMap;
	
	private boolean isList;

	public Map<String, T> getZsonMap() {
		return zsonMap;
	}

	public List<T> getZsonList() {
		return zsonList;
	}

	public boolean isMap() {
		return isMap;
	}

	public boolean isList() {
		return isList;
	}

	@SuppressWarnings("unchecked")
	public void objectConvert(Object obj){
		if(obj instanceof List){
			zsonList = (List<T>) obj;
			isList = true;
		}else if(obj instanceof Map){
			zsonMap = (Map<String, T>) obj;
			isMap = true;
		}
		/*else{
			throw new RuntimeException("can not convert "+ obj.getClass().getSimpleName()+" to List or Map!");
		}*/
	}
	
}

package com.zf.zson.result.impl;

import java.util.Iterator;
import java.util.List;

import com.zf.zson.ZSON;
import com.zf.zson.ZsonUtils;
import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResult;
import com.zf.zson.result.info.ZsonResultInfo;

public class ZsonAdd implements ZsonAction{
	
	private String addJson;
	
	private String addKey;
	
	private int addIndex;
	
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
	public void process(ZsonResult zr, Object value) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		String key = zri.getElementKey(value);
		if(key == null){
			throw new RuntimeException("can not add value to this path: "+zri.getzPath().getPath());
		}
		Object pathValue = zri.getResultByKey(key);
		if(pathValue instanceof List){
			List<Object> pathList = (List<Object>) pathValue;
			ZsonResultImpl zra = (ZsonResultImpl) zri.parseJsonToZson(addJson);
			Object actionValue = zra.getResultByKey(ZsonUtils.BEGIN_KEY);
			pathList.add(addIndex, actionValue);
			ZsonResultImpl zrNew = (ZsonResultImpl) zri.parseJsonToZson(ZSON.toJsonString(pathList));
			this.deleteZsonResultInfoChilrenKey(zri, key);
			System.out.println();
		}
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
	
	private void replaceZsonResultInfoKey(ZsonResultImpl zrNew, String targetKey){
		List<String> levels = zrNew.getzResultInfo().getLevel();
		for (int i = 0; i < levels.size(); i++) {
			String newLevel = targetKey+levels.get(i).substring(1);
			levels.set(i, newLevel);
			//zrNew.getzResultInfo().getPath()
		}
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}
	public static void main(String[] args) {
		String t = "1.1";
		String k = "1.2";
		System.out.println(t+k.substring(1));
	}
}

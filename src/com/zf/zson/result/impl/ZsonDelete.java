package com.zf.zson.result.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZSON;
import com.zf.zson.ZsonUtils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.path.ZsonCurrentPath;
import com.zf.zson.path.ZsonPath;
import com.zf.zson.result.ZsonActionAbstract;
import com.zf.zson.result.ZsonResult;

public class ZsonDelete extends ZsonActionAbstract{
	
	private boolean deleted = false;
	
	@Override
	public void process(ZsonResult zr, Object value, String currentPath) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		String ckey = zri.getElementKey(value);
		ZsonCurrentPath zcp = this.setKeyOrIndexByPath(zri, currentPath);
		String parentPath = this.getParentPath(currentPath);
		Object parentElement = null;
		String key = null;
		if("".equals(parentPath)){
			key = ZsonUtils.BEGIN_KEY;
		}else{
			key = this.getKeyByPath(zri, currentPath);
		}
		parentElement = zri.getResultByKey(key);
		ZsonObject<Object> parentObj = new ZsonObject<Object>();
		parentObj.objectConvert(parentElement);
		if((zcp.getIndex()!=null && parentObj.isList()) || (zcp.getKey()!=null && parentObj.isMap())){
			Object addObj = null;
			if(zcp.getIndex()!=null && parentObj.isList()){
				List<Object> pathValueList = parentObj.getZsonList();
				pathValueList.remove((int)zcp.getIndex());
				addObj = pathValueList;
				
			}else if(zcp.getKey()!=null && parentObj.isMap()){
				Map<String, Object> pathValueMap = parentObj.getZsonMap();
				pathValueMap.remove(zcp.getKey());
				addObj = pathValueMap;
			}
			if(ckey!=null){
				deleted = true;
			}
			ZsonResultImpl zrNew = (ZsonResultImpl) zri.parseJsonToZson(ZSON.toJsonString(addObj));
			if("".equals(parentPath)){
				zri.getzResultInfo().setCollections(zrNew.getzResultInfo().getCollections());
				zri.getzResultInfo().setIndex(zrNew.getzResultInfo().getIndex());
				zri.getzResultInfo().setLevel(zrNew.getzResultInfo().getLevel());
				zri.getzResultInfo().setPath(zrNew.getzResultInfo().getPath());
			}else{
				this.deleteZsonResultInfoChilrenKey(zri, key);
				//this.replaceZsonResultInfoKey(zrNew, key, parentPath, null, null);
				this.addNewResultToSourceResult(zri, zrNew);
				this.recorrectIndex(zri);
			}
			//System.out.println(ZSON.toJsonString(zri.getResult()));
		}
	}
	
	
	
	
	private String getParentPath(String currentPath){
		String parentPath = currentPath.substring(0,currentPath.lastIndexOf('/'));
		return parentPath;
	}
	
	private ZsonCurrentPath setKeyOrIndexByPath(ZsonResultImpl zri, String path){
		ZsonCurrentPath zcp = new ZsonCurrentPath();
		List<Object> paths = zri.getzResultInfo().getPath();
		for (Object pathObject : paths) {
			ZsonObject<String> pathObj = new ZsonObject<String>();
			pathObj.objectConvert(pathObject);
			if(pathObj.isList()){
				int index = 0;
				for (String p : pathObj.getZsonList()) {
					if(p.equals(path)){
						zcp.setIndex(index);
						return zcp;
					}
					index++;
				}
			}else if(pathObj.isMap()){
				for (String k : pathObj.getZsonMap().keySet()) {
					if(pathObj.getZsonMap().get(k).equals(path)){
						zcp.setKey(k);
						return zcp;
					}
				}
			}
		}
		return null;
	}
	
	private String getKeyByPath(ZsonResultImpl zri, String path){
		List<Object> paths = zri.getzResultInfo().getPath();
		int index = 0;
		for (Object pathObject : paths) {
			ZsonObject<String> pathObj = new ZsonObject<String>();
			pathObj.objectConvert(pathObject);
			if(pathObj.isList()){
				for (String p : pathObj.getZsonList()) {
					if(p.equals(path)){
						return zri.getzResultInfo().getLevel().get(index);
					}
				}
			}else if(pathObj.isMap()){
				for (String p : pathObj.getZsonMap().values()) {
					if(p.equals(path)){
						return zri.getzResultInfo().getLevel().get(index);
					}
				}
			}
			index++;
		}
		return null;
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		/*if(deleted){
			return -1;
		}else{
			return 0;
		}*/
		return 0;
	}

	@Override
	public boolean before(ZsonResult zr) {
		return true;
	}

	@Override
	public boolean after(ZsonResult zr) {
		return false;
	}
	
	public static void main(String[] args) {
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> l = new ArrayList<String>();
		l.add("a");
		l.add("b");
		l.add("c");
		list.add(l);
		List<String> l1 = new ArrayList<String>();
		l1.add("a");
		l1.add("b");
		List<String> temp = list.get(0);
		for (int i = 0; i < temp.size(); i++) {
			if(i==0){
				list.set(0, l1);
			}
			System.out.println(temp);
		}
	}
	
}

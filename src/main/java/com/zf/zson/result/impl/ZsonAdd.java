package com.zf.zson.result.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZSON;
import com.zf.zson.ZsonUtils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.result.ZsonActionAbstract;
import com.zf.zson.result.ZsonResult;

public class ZsonAdd extends ZsonActionAbstract{
	
	private Object addJson;
	
	private String addKey;
	
	private Integer addIndex;
	
	private String addRootPath;
	
	private List<String> handledPath = new ArrayList<String>();
	
	public void setAddJson(Object addJson) {
		this.addJson = addJson;
	}

	public void setAddKey(String addKey) {
		this.addKey = addKey;
	}

	public void setAddIndex(Integer addIndex) {
		this.addIndex = addIndex;
	}

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
				Object actionValue = this.getActionObject(zri, addJson);
				pathValueList.add(addIndex, actionValue);
				addRootPath = currentPath+"/*["+addIndex+"]";
				addObj = pathValueList;
				
			}else if(addKey!=null && valueByKeyObj.isMap()){
				Map<String, Object> pathValueMap = valueByKeyObj.getZsonMap();
				Object actionValue = this.getActionObject(zri, addJson);
				pathValueMap.put(addKey, actionValue);
				addRootPath = currentPath+"/"+addKey;
				addObj = pathValueMap;
			}
			handledPath.add(currentPath);
			handledPath.add(addRootPath);
			ZsonResultImpl zrNew = (ZsonResultImpl) ZSON.parseJson(ZSON.toJsonString(addObj));
			this.deleteZsonResultInfoChilrenKey(zri, key);
			this.replaceZsonResultInfoKey(zrNew, key, currentPath, handledPath, addRootPath);
			this.addNewResultToSourceResult(zri, zrNew);
			this.recorrectIndex(zri);
		}
	}

	@Override
	public int offset(ZsonResult zr, Object value) {
		return 0;
	}

	@Override
	public boolean before(ZsonResult zr) {
		ZsonResultImpl zri = (ZsonResultImpl) zr;
		if(zri.getzPath().isRootPath()){
			Object pathValue = zri.getResultByKey(ZsonUtils.BEGIN_KEY);
			ZsonObject<Object> valueByKeyObj = new ZsonObject<Object>();
			valueByKeyObj.objectConvert(pathValue);
			if((addIndex!=null && valueByKeyObj.isList()) || (addKey!=null && valueByKeyObj.isMap())){
				Object addObj = null;
				if(addIndex!=null && valueByKeyObj.isList()){
					List<Object> pathValueList = valueByKeyObj.getZsonList();
					Object actionValue = this.getActionObject(zri, addJson);
					pathValueList.add(addIndex, actionValue);
					addObj = pathValueList;
					
				}else if(addKey!=null && valueByKeyObj.isMap()){
					Map<String, Object> pathValueMap = valueByKeyObj.getZsonMap();
					Object actionValue = this.getActionObject(zri, addJson);
					pathValueMap.put(addKey, actionValue);
					addObj = pathValueMap;
				}
				ZsonResultImpl zrNew = (ZsonResultImpl) ZSON.parseJson(zri.getZsonResultToString().toJsonString(addObj));
				zri.getzResultInfo().setCollections(zrNew.getzResultInfo().getCollections());
				zri.getzResultInfo().setIndex(zrNew.getzResultInfo().getIndex());
				zri.getzResultInfo().setLevel(zrNew.getzResultInfo().getLevel());
				zri.getzResultInfo().setPath(zrNew.getzResultInfo().getPath());
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean after(ZsonResult zr) {
		return false;
	}
	
	public static void main(String[] args) {
		String p = "/a/*[1]";
		String p1 = "/a/*[1]/a";
		String regPath = p.replaceAll("\\/", "\\\\/").replaceAll("\\*", "\\\\*").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\\\/\\\\/", "(/.+)*\\\\/");
		System.out.println(p1.matches(regPath+"/.+"));
	}
}

package com.zf.zson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zson {
	
	private String json;
	
	private ZsonResult zResult;
	
	private ZsonResultInfo zResultInfo;
	
	public Zson() {
		
	}
	
	public Zson(String json) {
		this.json = json;
	}


	public void setJson(String json) {
		this.json = json;
	}
	
	@SuppressWarnings("unchecked")
	private void addElementToCollections(int type, String element, String v, boolean isFinished){
		try{
			int lastUNFIndex = this.getLatestUNFinishedLevelIndex();
			Map<String, Integer> elementStatus = zResultInfo.getIndex().get(zResultInfo.getLevel().get(lastUNFIndex));
			if(elementStatus.get(ZsonUtils.TYPE)!=type){
				zResultInfo.setValid(false);
				throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
			}else{
				Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
				if(type==1){
					List<Object> elementList = (List<Object>) elementObj; 
					if(!(elementList.size()==0 && element.equals("") && isFinished)){
						elementList.add(this.getElementInstance(element));
					}
				}else{
					Map<String, Object> elementMap = (Map<String, Object>) elementObj;
					if(!(elementMap.size()==0 && element.equals("") && v.equals("") && isFinished)){
						if(!this.isValidElement(v)){
							zResultInfo.setValid(false);
							throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
						}
						elementMap.put(this.getElementInstance(element).toString(), this.getElementInstance(v));
					}
				}
			}
			if(isFinished){
				elementStatus.put(ZsonUtils.STATUS, 1);
			}
		}catch(Exception e){
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}
	
	private void setLatestUNFinishedToFinished(){
		try{
			int lastUNFIndex = this.getLatestUNFinishedLevelIndex();
			Map<String, Integer> elementStatus = zResultInfo.getIndex().get(zResultInfo.getLevel().get(lastUNFIndex));
			elementStatus.put(ZsonUtils.STATUS, 1);
		}catch(Exception e){
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}
	
	private Map<String, Integer> getElementByKey(String key){
		if(zResultInfo.getIndex().containsKey(key)){
			return zResultInfo.getIndex().get(key);
		}else{
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addToParent(String element, boolean isMap, String key){
		try{
			String pKey = key.substring(0, key.lastIndexOf('.'));
			Map<String, Integer> pElement = this.getElementByKey(pKey);
			if(isMap && pElement.get(ZsonUtils.TYPE)==1){
				zResultInfo.setValid(false);
				throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
			}
			if(isMap){
				Map<String, Object> elementMap = (Map<String, Object>) zResultInfo.getCollections().get(pElement.get(ZsonUtils.INDEX));
				Map<String, String> temp = new HashMap<String, String>();
				temp.put(ZsonUtils.LINK, key);
				elementMap.put(this.getElementInstance(element).toString(), temp);
			}else{
				List<Object> elementList = (List<Object>) zResultInfo.getCollections().get(pElement.get(ZsonUtils.INDEX));
				List<String> temp = new ArrayList<String>();
				temp.add(key);
				elementList.add(temp);
			}
		}catch(Exception e){
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}
	
	private String addToCollections(int type){
		if(type==0){
			zResultInfo.getCollections().add(new HashMap<String, Object>());
		}else if(type==1){
			zResultInfo.getCollections().add(new ArrayList<Object>());
		}
		int status = 0;
		int index = 0;
		String key = ZsonUtils.BEGIN_KEY;
		if(zResultInfo.getLevel().size()!=0){
			index = zResultInfo.getCollections().size()-1;
			int latestIndex = this.getLatestFinishedLevelIndex();
			key = this.generateIndexKey(zResultInfo.getLevel().get(latestIndex));
		}
		zResultInfo.getLevel().add(key);
		Map<String, Integer> objMap = new HashMap<String, Integer>();
		objMap.put(ZsonUtils.TYPE, type);
		objMap.put(ZsonUtils.STATUS, status);
		objMap.put(ZsonUtils.INDEX, index);
		zResultInfo.getIndex().put(key, objMap);
		return key;
	}
	
	/**
	 * 获取最新的没有解析完成的index,从level中获取
	 * @return
	 */
	private int getLatestFinishedLevelIndex(){
		for (int i = zResultInfo.getLevel().size()-1; i >= 0; i--) {
			if(zResultInfo.getIndex().get(zResultInfo.getLevel().get(i)).get(ZsonUtils.STATUS)==0){
				return i;
			}
		}
		return -1;
	}
	
	private int getLatestUNFinishedLevelIndex(){
		for (int i = zResultInfo.getLevel().size()-1; i >= 0; i--) {
			if(zResultInfo.getIndex().get(zResultInfo.getLevel().get(i)).get(ZsonUtils.STATUS)==0){
				return i;
			}
		}
		return -1;
	}
	
	private String generateIndexKey(String parentKey){
		int begin = 1;
		while(true){
			if(zResultInfo.getIndex().containsKey(parentKey+"."+begin)){
				begin++;
			}else{
				return parentKey+"."+begin;
			}
		}
	}
	
	private boolean isValidMapKey(String element){
		char[] c = element.toCharArray();
		return c[0]==ZsonUtils.jsonStringBegin && c[c.length-1]==ZsonUtils.jsonStringEnd;
	}
	
	private Object getElementInstance(String element){
		try{
			if(element.matches("\"(.*\\n*){0,}\"")){
				return element.substring(1,element.length()-1);
			}else if(element.matches("\\d+")){
				return Long.valueOf(element);
			}else if(element.matches("\\d+\\.\\d+")){
				return Double.valueOf(element);
			}else if(element.toLowerCase().matches("null")){
				return null;
			}else if(element.toLowerCase().matches("true")){
				return true;
			}else if(element.toLowerCase().matches("false")){
				return false;
			}else{
				zResultInfo.setValid(false);
				throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
			}
		}catch(Exception e){
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}
	
	private boolean isValidElement(String element){
		if(element.matches("\"(.*\\n*){0,}\"")){
			return true;
		}else if(element.matches("\\d+")){
			return true;
		}else if(element.matches("\\d+\\.\\d+")){
			return true;
		}else if(element.toLowerCase().matches("null")){
			return true;
		}else if(element.toLowerCase().matches("true")){
			return true;
		}else if(element.toLowerCase().matches("false")){
			return true;
		}else{
			return false;
		}
	}
	
	private void handleListBegin(ZsonInfo zinfo){
		String key = this.addToCollections(1);
		if(!ZsonUtils.BEGIN_KEY.equals(key)){
			this.addToParent(zinfo.getElement(), zinfo.isMap(), key);
			zinfo.setElement(null);
			zinfo.setMap(false);
			zinfo.setElementSeparate(false);
		}
	}
	
	private void handleMapBegin(ZsonInfo zinfo){
		String key = this.addToCollections(0);
		if(!ZsonUtils.BEGIN_KEY.equals(key)){
			this.addToParent(zinfo.getElement(), zinfo.isMap(), key);
			zinfo.setElement(null);
			zinfo.setMap(false);
			zinfo.setElementSeparate(false);
		}
	}
	
	private void handleFormatString(StringBuffer sb, char[] chars, int i){
		try{
			sb.append(chars[i]);
			sb.append(chars[i+1]);
		}catch(Exception e){
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}
	
	private void handleStringBegin(ZsonInfo zinfo, StringBuffer sb, char temp){
		zinfo.setMarkIndex(zinfo.getMarkIndex()+1);
		sb.append(temp);
		if(!zinfo.isMark()){
			zinfo.setMark(true);
		}else{
			zinfo.setMark(false);
		}
		zinfo.setElementSeparate(false);
	}
	
	private void handleMapConnector(ZsonInfo zinfo, StringBuffer sb){
		if(zinfo.isMark() || zinfo.getMarkIndex()%2!=0 || sb.length()==0 || zinfo.isMap() || zinfo.isElementSeparate()){
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		zinfo.setMap(true);
		zinfo.setElement(sb.toString().trim());
		sb.delete(0, sb.length());
	}
	
	private void handleElementConnector(ZsonInfo zinfo, StringBuffer sb){
		if(zinfo.isMap()){
			zinfo.setV(sb.toString().trim());
		}else{
			zinfo.setElement(sb.toString().trim());
		}
		if(zinfo.isElementSeparate() || zinfo.isMark() || zinfo.getMarkIndex()%2!=0 || (zinfo.isMap() && (!zinfo.getElement().equals("") && !this.isValidMapKey(zinfo.getElement()))) || (!zinfo.getElement().equals("") && !this.isValidElement(zinfo.getElement()))){
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		if(!zinfo.getElement().equals("")){
			if(zinfo.isMap()){
				this.addElementToCollections(0, zinfo.getElement(), zinfo.getV(), false);
				zinfo.setMap(false);
			}else{
				this.addElementToCollections(1, zinfo.getElement(), null, false);
			}
		}
		zinfo.setElement(null);
		zinfo.setV(null);
		zinfo.setElementSeparate(true);
		sb.delete(0,sb.length());
	}
	
	private void handleElementEnd(ZsonInfo zinfo, StringBuffer sb){
		if(zinfo.isMap()){
			zinfo.setV(sb.toString().trim());
		}else{
			zinfo.setElement(sb.toString().trim());
		}
		if(zinfo.isElementSeparate() || zinfo.isMark() || zinfo.getMarkIndex()%2!=0 || (zinfo.isMap() && (!zinfo.getElement().equals("") && !this.isValidMapKey(zinfo.getElement()))) || (!zinfo.getElement().equals("") && !this.isValidElement(zinfo.getElement()))){
			zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		if(!zinfo.getElement().equals("")){
			if(zinfo.isMap()){
				this.addElementToCollections(0, zinfo.getElement(), zinfo.getV(), true);
				zinfo.setMap(false);
			}else{
				this.addElementToCollections(1, zinfo.getElement(), null, true);
			}
		}else{
			this.setLatestUNFinishedToFinished();
		}
		zinfo.setElement(null);
		zinfo.setV(null);
		sb.delete(0,sb.length());
	}
	
	private void fromJson(){
		if(json==null){
			//zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		char[] chars = json.toCharArray();
		if(chars[0]!=ZsonUtils.jsonListBegin && chars[0]!=ZsonUtils.jsonMapBegin){
			//zResultInfo.setValid(false);
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
		ZsonInfo zinfo = new ZsonInfo();
		zResult = new ZsonResult();
		zResultInfo = zResult.getzResultInfo();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			char temp = chars[i];
			if(!zinfo.isMark() && temp==ZsonUtils.jsonListBegin){
				this.handleListBegin(zinfo);
				continue;
			}else if(!zinfo.isMark() && temp==ZsonUtils.jsonMapBegin){
				this.handleMapBegin(zinfo);
				continue;
			}else if(temp=='\\'){
				this.handleFormatString(sb, chars, i);
				i++;
				continue;
			}else if(temp==ZsonUtils.jsonStringBegin){
				this.handleStringBegin(zinfo, sb, temp);
				continue;
			}else if(!zinfo.isMark() && temp==ZsonUtils.jsonMapConnector){
				this.handleMapConnector(zinfo, sb);
				continue;
			}else if(!zinfo.isMark() && temp==ZsonUtils.jsonElementConnector){
				this.handleElementConnector(zinfo, sb);
			}else if(!zinfo.isMark() && (temp==ZsonUtils.jsonListEnd || temp==ZsonUtils.jsonMapEnd)){
				this.handleElementEnd(zinfo, sb);
			}else{
				zinfo.setElementSeparate(false);
				sb.append(temp);
			}
		}
		if(!zResult.isValid()){
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}
	
	public ZsonResult parseJson(){
		this.fromJson();
		return zResult;
	}
	
	public ZsonResult parseJson(String json){
		this.json = json;
		this.fromJson();
		return zResult;
	}
	
}

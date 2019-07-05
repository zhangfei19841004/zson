package com.zf.zson.result;

import com.zf.zson.ZSON;
import com.zf.zson.ZsonUtils;
import com.zf.zson.common.Utils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.path.ZsonPath;
import com.zf.zson.result.impl.ZsonResultImpl;
import com.zf.zson.result.info.ZsonResultInfo;
import com.zf.zson.result.utils.ZsonResultRestore;
import com.zf.zson.result.utils.ZsonResultToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ZsonResultAbstract implements ZsonResult {

	protected ZsonResultInfo zResultInfo;

	protected ZsonPath zPath;

	protected ZsonResultToString zsonResultToString;

	protected ZsonResultRestore zsonResultRestore;

	public ZsonResultAbstract(boolean linked) {
		zResultInfo = new ZsonResultInfo(linked);
		zPath = new ZsonPath();
		zsonResultToString = new ZsonResultToString();
		zsonResultRestore = new ZsonResultRestore(this);
	}

	protected abstract void checkValid();

	public ZsonResultInfo getzResultInfo() {
		return zResultInfo;
	}

	public ZsonPath getzPath() {
		return zPath;
	}

	public ZsonResultToString getZsonResultToString() {
		return zsonResultToString;
	}

	public ZsonResultRestore getZsonResultRestore() {
		return zsonResultRestore;
	}

	public String getElementKey(Object value) {
		ZsonObject<String> keyObj = new ZsonObject<String>();
		keyObj.objectConvert(value);
		String key;
		if (keyObj.isMap()) {
			key = keyObj.getZsonMap().get(ZsonUtils.LINK);
		} else if (keyObj.isList()) {
			key = keyObj.getZsonList().get(0);
		} else {
			key = null;
		}
		return key;
	}

	/**
	 * 将在collections中获取到的值给重新的还原，并返回出去
	 *
	 * @param value
	 * @return
	 */
	public Object getCollectionsObjectAndRestore(Object value) {
		if (value instanceof Map || value instanceof List) {
			String key = this.getElementKey(value);
			Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
			Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
			value = zsonResultToString.toJsonString(zsonResultRestore.restoreObject(elementObj));
		} else if (value instanceof String) {
			value = ZsonUtils.convert((String) value);
		}
		return value;
	}

	public Object getResultByKey(String key) {
		Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
		Object obj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
		return zsonResultRestore.restoreObject(obj);
	}

	public List<String> getPaths() {
		List<String> list = new ArrayList<String>();
		for (Object pathObj : zResultInfo.getPath()) {
			ZsonObject<String> zo = new ZsonObject<String>();
			zo.objectConvert(pathObj);
			if (zo.isMap()) {
				list.addAll(zo.getZsonMap().values());
			} else {
				list.addAll(zo.getZsonList());
			}
		}
		return list;
	}

	@Override
	public Map<String, Class<?>> getClassTypes() {
		Map<String, Class<?>> classTypes = Utils.getMap(zResultInfo.isLinked());
		for (int i = 0; i < zResultInfo.getPath().size(); i++) {
			ZsonObject<String> zoPath = new ZsonObject<String>();
			zoPath.objectConvert(zResultInfo.getPath().get(i));
			ZsonObject<Class<?>> zoClass = new ZsonObject<Class<?>>();
			zoClass.objectConvert(zResultInfo.getClassTypes().get(i));
			if (zoPath.isMap()) {
				for (String key : zoPath.getZsonMap().keySet()) {
					classTypes.put(zoPath.getZsonMap().get(key), zoClass.getZsonMap().get(key));
				}
			} else {
				for (int j = 0; j < zoPath.getZsonList().size(); j++) {
					classTypes.put(zoPath.getZsonList().get(j), zoClass.getZsonList().get(j));
				}
			}
		}
		return classTypes;
	}

	@Override
	public boolean validateJsonClassTypes(String baseJson) {
		Map<String, Class<?>> cs = this.getClassTypes();
		ZsonResult bzr = ZSON.parseJson(baseJson, zResultInfo.isLinked());
		Map<String, Class<?>> bcs = bzr.getClassTypes();
		if (bcs.size() == 0) {
			return this.getResult().getClass().equals(bzr.getResult().getClass());
		}
		for (String bckey : bcs.keySet()) {
			List<String> keys = this.getSameLevelJsonPath(cs.keySet(), bckey);
			if(keys.size()==0){
				return false;
			}
			for (String key : keys) {
				if(key.equals(bckey) || !bcs.keySet().contains(key)){
					if(!cs.get(key).equals(Object.class) && !bcs.get(bckey).equals(Object.class) && !cs.get(key).equals(bcs.get(bckey))){
						return false;
					}
				}
			}
		}
		return true;
	}

	private List<String> getSameLevelJsonPath(Set<String> set, String key){
		int lastIndex1 = key.lastIndexOf("/*[");
		int lastIndex2 = key.lastIndexOf(']');
		String replace = key.substring(0,lastIndex1)+"/*[\\d+]"+key.substring(lastIndex2+1);
		String regKey = replace.replaceAll("\\*", "\\\\*").replaceAll("\\[", "\\\\[");
		List<String> list = new ArrayList<String>();
		for (String s : set) {
			if(s.matches(regKey)){
				list.add(s);
			}
		}
		return list;
	}

}

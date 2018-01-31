package com.zf.zson;

import com.zf.zson.exception.ZsonException;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.parse.ZsonInfo;
import com.zf.zson.result.ZsonResult;
import com.zf.zson.result.impl.ZsonResultImpl;
import com.zf.zson.result.info.ZsonResultInfo;
import com.zf.zson.result.utils.ZsonResultToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZSON {

	public static ZsonResult parseJson(String json) {
		ZsonParse zp = new ZsonParse(json);
		return zp.fromJson();
	}

	public static String toJsonString(Object object) {
		ZsonResultToString zrt = new ZsonResultToString();
		return zrt.toJsonString(object);
	}

	public static class ZsonParse {

		private ZsonResultInfo zResultInfo;

		private String json;

		private ZsonParse(String json) {
			this.json = json;
		}

		private void addElementToCollections(int type, String element, String v, boolean isFinished) {
			try {
				int lastUNFIndex = this.getLatestUNFinishedLevelIndex();
				Map<String, Integer> elementStatus = zResultInfo.getIndex().get(zResultInfo.getLevel().get(lastUNFIndex));
				Object classType = null;
				if (elementStatus.get(ZsonUtils.TYPE) != type) {
					throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
				} else {
					Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
					ZsonObject<Object> eObj = new ZsonObject<Object>();
					eObj.objectConvert(elementObj);
					if (type == 1) {
						List<Object> elementList = eObj.getZsonList();
						if (!(elementList.size() == 0 && element.equals("") && isFinished)) {
							Object temp = this.getElementInstance(element);
							elementList.add(temp);
							if (temp != null) {
								classType = temp.getClass();
							}
						}
					} else {
						Map<String, Object> elementMap = eObj.getZsonMap();
						if (!(elementMap.size() == 0 && element.equals("") && v.equals("") && isFinished)) {
							Object temp = this.getElementInstance(v);
							elementMap.put(this.getElementInstance(element).toString(), temp);
							if (temp != null) {
								classType = temp.getClass();
							}
						}
					}
				}
				this.setElementPath(type, lastUNFIndex, element);
				this.setElementClassType(type, lastUNFIndex, element, classType);
				if (isFinished) {
					elementStatus.put(ZsonUtils.STATUS, 1);
				}
			} catch (Exception e) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
		}

		private void setElementPath(int type, int index, String element) {
			ZsonObject<String> pathObj = new ZsonObject<String>();
			pathObj.objectConvert(zResultInfo.getPath().get(index));
			String parentPath = this.getParentPath(zResultInfo.getLevel().get(index));
			if (type == 1) {
				List<String> pathObjList = pathObj.getZsonList();
				String path = parentPath + "/*[" + pathObjList.size() + "]";
				pathObjList.add(path);
			} else {
				Map<String, String> pathObjMap = pathObj.getZsonMap();
				String pathKey = this.getElementInstance(element).toString();
				String path = parentPath + "/" + pathKey;
				pathObjMap.put(pathKey, path);
			}
		}

		private void setElementClassType(int type, int index, String element, Object classType) {
			ZsonObject<Object> classTypeObj = new ZsonObject<Object>();
			classTypeObj.objectConvert(zResultInfo.getClassTypes().get(index));
			if (type == 1) {
				List<Object> classTypeObjList = classTypeObj.getZsonList();
				classTypeObjList.add(classType);
			} else {
				Map<String, Object> classTypeObjMap = classTypeObj.getZsonMap();
				classTypeObjMap.put(this.getElementInstance(element).toString(), classType);
			}
		}

		private String getParentPath(String key) {
			if (ZsonUtils.BEGIN_KEY.equals(key)) {
				return "";
			}
			String pKey = key.substring(0, key.lastIndexOf('.'));
			Map<String, Integer> pIndexInfo = this.getIndexInfoByKey(pKey);
			int pType = pIndexInfo.get(ZsonUtils.TYPE);
			int pIndex = pIndexInfo.get(ZsonUtils.INDEX);
			ZsonObject<String> pPathObj = new ZsonObject<String>();
			pPathObj.objectConvert(zResultInfo.getPath().get(pIndex));
			ZsonObject<Object> collectionObj = new ZsonObject<Object>();
			collectionObj.objectConvert(zResultInfo.getCollections().get(pIndexInfo.get(ZsonUtils.INDEX)));
			if (pType == 0) {
				Map<String, Object> pElement = collectionObj.getZsonMap();
				for (String k : pElement.keySet()) {
					ZsonObject<String> pObj = new ZsonObject<String>();
					pObj.objectConvert(pElement.get(k));
					if (pObj.isMap()) {
						if (key.equals(pObj.getZsonMap().get(ZsonUtils.LINK))) {
							return pPathObj.getZsonMap().get(k);
						}
					} else if (pObj.isList()) {
						if (key.equals(pObj.getZsonList().get(0))) {
							return pPathObj.getZsonMap().get(k);
						}
					}
				}
			} else {
				List<Object> pElement = collectionObj.getZsonList();
				for (int i = 0; i < pElement.size(); i++) {
					ZsonObject<String> pObj = new ZsonObject<String>();
					pObj.objectConvert(pElement.get(i));
					if (pObj.isMap()) {
						if (key.equals(pObj.getZsonMap().get(ZsonUtils.LINK))) {
							return pPathObj.getZsonList().get(i);
						}
					} else if (pObj.isList()) {
						if (key.equals(pObj.getZsonList().get(0))) {
							return pPathObj.getZsonList().get(i);
						}
					}
				}
			}
			throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
		}

		private void setLatestUNFinishedToFinished() {
			try {
				int lastUNFIndex = this.getLatestUNFinishedLevelIndex();
				Map<String, Integer> elementStatus = zResultInfo.getIndex().get(zResultInfo.getLevel().get(lastUNFIndex));
				elementStatus.put(ZsonUtils.STATUS, 1);
			} catch (Exception e) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
		}

		private Map<String, Integer> getIndexInfoByKey(String key) {
			if (zResultInfo.getIndex().containsKey(key)) {
				return zResultInfo.getIndex().get(key);
			} else {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
		}

		private void addToParent(ZsonInfo zinfo, String key) {
			try {
				String pKey = key.substring(0, key.lastIndexOf('.'));
				Map<String, Integer> pElement = this.getIndexInfoByKey(pKey);
				if (zinfo.isMap() && pElement.get(ZsonUtils.TYPE) == 1) {
					throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
				}
				ZsonObject<Object> elementObj = new ZsonObject<Object>();
				elementObj.objectConvert(zResultInfo.getCollections().get(pElement.get(ZsonUtils.INDEX)));
				if (zinfo.isMap()) {
					Map<String, Object> elementMap = elementObj.getZsonMap();
					Map<String, String> temp = new LinkedHashMap<String, String>();
					temp.put(ZsonUtils.LINK, key);
					elementMap.put(this.getElementInstance(zinfo.getElement()).toString(), temp);
				} else {
					List<Object> elementList = elementObj.getZsonList();
					List<String> temp = new ArrayList<String>();
					temp.add(key);
					elementList.add(temp);
				}
				this.setElementPath(pElement.get(ZsonUtils.TYPE), pElement.get(ZsonUtils.INDEX), zinfo.getElement());
				Map<String, Integer> cElement = this.getIndexInfoByKey(key);
				this.setElementClassType(pElement.get(ZsonUtils.TYPE), pElement.get(ZsonUtils.INDEX), zinfo.getElement(), cElement.get(ZsonUtils.TYPE) == 0 ? Map.class : List.class);
			} catch (Exception e) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
		}

		private String addZsonResultInfo(int type) {
			Object pathObj = null;
			Object classTypeObj = null;
			if (type == 0) {
				zResultInfo.getCollections().add(new LinkedHashMap<String, Object>());
				pathObj = new LinkedHashMap<String, Object>();
				classTypeObj = new LinkedHashMap<String, Object>();
			} else if (type == 1) {
				zResultInfo.getCollections().add(new ArrayList<Object>());
				pathObj = new ArrayList<Object>();
				classTypeObj = new ArrayList<Object>();
			}
			int status = 0;
			int index = 0;
			String key = ZsonUtils.BEGIN_KEY;
			if (zResultInfo.getLevel().size() != 0) {
				index = zResultInfo.getCollections().size() - 1;
				int latestIndex = this.getLatestFinishedLevelIndex();
				key = this.generateIndexKey(zResultInfo.getLevel().get(latestIndex));
			}
			zResultInfo.getLevel().add(key);
			zResultInfo.getPath().add(pathObj);
			zResultInfo.getClassTypes().add(classTypeObj);
			Map<String, Integer> objMap = new LinkedHashMap<String, Integer>();
			objMap.put(ZsonUtils.TYPE, type);
			objMap.put(ZsonUtils.STATUS, status);
			objMap.put(ZsonUtils.INDEX, index);
			zResultInfo.getIndex().put(key, objMap);
			return key;
		}

		/**
		 * 获取最新的没有解析完成的index,从level中获取
		 *
		 * @return
		 */
		private int getLatestFinishedLevelIndex() {
			for (int i = zResultInfo.getLevel().size() - 1; i >= 0; i--) {
				if (zResultInfo.getIndex().get(zResultInfo.getLevel().get(i)).get(ZsonUtils.STATUS) == 0) {
					return i;
				}
			}
			return -1;
		}

		private int getLatestUNFinishedLevelIndex() {
			for (int i = zResultInfo.getLevel().size() - 1; i >= 0; i--) {
				if (zResultInfo.getIndex().get(zResultInfo.getLevel().get(i)).get(ZsonUtils.STATUS) == 0) {
					return i;
				}
			}
			return -1;
		}

		private String generateIndexKey(String parentKey) {
			int begin = 1;
			while (true) {
				if (zResultInfo.getIndex().containsKey(parentKey + "." + begin)) {
					begin++;
				} else {
					return parentKey + "." + begin;
				}
			}
		}

		private boolean isValidMapKey(String element) {
			char[] c = element.toCharArray();
			return c[0] == ZsonUtils.jsonStringBegin && c[c.length - 1] == ZsonUtils.jsonStringEnd;
		}

		private Object getElementInstance(String element) {
			try {
				if (element.startsWith("\"") && element.endsWith("\"")) {
					return element.substring(1, element.length() - 1);
				} else if (element.equals("true")) {
					return true;
				} else if (element.equals("false")) {
					return false;
				} else if (element.equals("null")) {
					return null;
				} else {
					return this.getBigDecimalValue(element);
				}
			} catch (Exception e) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
		}

		private void handleListBegin(ZsonInfo zinfo) {
			String key = this.addZsonResultInfo(1);
			if (!ZsonUtils.BEGIN_KEY.equals(key)) {
				this.addToParent(zinfo, key);
				zinfo.setElement(null);
				zinfo.setMap(false);
				zinfo.setElementSeparate(false);
			}
		}

		private void handleMapBegin(ZsonInfo zinfo) {
			String key = this.addZsonResultInfo(0);
			if (!ZsonUtils.BEGIN_KEY.equals(key)) {
				this.addToParent(zinfo, key);
				zinfo.setElement(null);
				zinfo.setMap(false);
				zinfo.setElementSeparate(false);
			}
		}

		private void handleFormatString(StringBuffer sb, char[] chars, int i) {
			try {
				sb.append(chars[i]);
				sb.append(chars[i + 1]);
			} catch (Exception e) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
		}

		private void handleStringBegin(ZsonInfo zinfo, StringBuffer sb, char temp) {
			zinfo.setMarkIndex(zinfo.getMarkIndex() + 1);
			sb.append(temp);
			if (!zinfo.isMark()) {
				zinfo.setMark(true);
			} else {
				zinfo.setMark(false);
			}
			zinfo.setElementSeparate(false);
		}

		private void handleMapConnector(ZsonInfo zinfo, StringBuffer sb) {
			if (zinfo.isMark() || zinfo.getMarkIndex() % 2 != 0 || sb.length() == 0 || zinfo.isMap() || zinfo.isElementSeparate()) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
			zinfo.setMap(true);
			zinfo.setElement(sb.toString().trim());
			sb.delete(0, sb.length());
		}

		private void handleElementConnector(ZsonInfo zinfo, StringBuffer sb) {
			if (zinfo.isMap()) {
				zinfo.setV(sb.toString().trim());
			} else {
				zinfo.setElement(sb.toString().trim());
			}
			if (zinfo.isElementSeparate() || zinfo.isMark() || zinfo.getMarkIndex() % 2 != 0 || (zinfo.isMap() && (!zinfo.getElement().equals("") && !this.isValidMapKey(zinfo.getElement())))) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
			if (!zinfo.getElement().equals("")) {
				if (zinfo.isMap()) {
					this.addElementToCollections(0, zinfo.getElement(), zinfo.getV(), false);
					zinfo.setMap(false);
				} else {
					this.addElementToCollections(1, zinfo.getElement(), null, false);
				}
			}
			zinfo.setElement(null);
			zinfo.setV(null);
			zinfo.setElementSeparate(true);
			sb.delete(0, sb.length());
		}

		private void handleElementEnd(ZsonInfo zinfo, StringBuffer sb) {
			if (zinfo.isMap()) {
				zinfo.setV(sb.toString().trim());
			} else {
				zinfo.setElement(sb.toString().trim());
			}
			if (zinfo.isElementSeparate() || zinfo.isMark() || zinfo.getMarkIndex() % 2 != 0 || (zinfo.isMap() && (!zinfo.getElement().equals("") && !this.isValidMapKey(zinfo.getElement())))) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
			if (!zinfo.getElement().equals("")) {
				if (zinfo.isMap()) {
					this.addElementToCollections(0, zinfo.getElement(), zinfo.getV(), true);
					zinfo.setMap(false);
				} else {
					this.addElementToCollections(1, zinfo.getElement(), null, true);
				}
			} else {
				this.setLatestUNFinishedToFinished();
			}
			zinfo.setElement(null);
			zinfo.setV(null);
			sb.delete(0, sb.length());
		}

		public ZsonResult fromJson() {
			if (json == null || json.trim().equals("")) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
			char[] chars = json.toCharArray();
			if (chars[0] != ZsonUtils.jsonListBegin && chars[0] != ZsonUtils.jsonMapBegin) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
			ZsonInfo zinfo = new ZsonInfo();
			ZsonResultImpl zResult = new ZsonResultImpl();
			zResultInfo = zResult.getzResultInfo();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < chars.length; i++) {
				char temp = chars[i];
				if (!zinfo.isMark() && temp == ZsonUtils.jsonListBegin) {
					this.handleListBegin(zinfo);
					continue;
				} else if (!zinfo.isMark() && temp == ZsonUtils.jsonMapBegin) {
					this.handleMapBegin(zinfo);
					continue;
				} else if (temp == '\\') {
					this.handleFormatString(sb, chars, i);
					i++;
					continue;
				} else if (temp == ZsonUtils.jsonStringBegin) {
					this.handleStringBegin(zinfo, sb, temp);
					continue;
				} else if (!zinfo.isMark() && temp == ZsonUtils.jsonMapConnector) {
					this.handleMapConnector(zinfo, sb);
					continue;
				} else if (!zinfo.isMark() && temp == ZsonUtils.jsonElementConnector) {
					this.handleElementConnector(zinfo, sb);
				} else if (!zinfo.isMark() && (temp == ZsonUtils.jsonListEnd || temp == ZsonUtils.jsonMapEnd)) {
					this.handleElementEnd(zinfo, sb);
				} else {
					zinfo.setElementSeparate(false);
					sb.append(temp);
				}
			}
			if (!zResult.isValid()) {
				throw new ZsonException(ZsonUtils.JSON_NOT_VALID);
			}
			return zResult;
		}

		private BigDecimal getBigDecimalValue(String math) {
			BigDecimal bd = new BigDecimal(math);
			return bd;
		}

	}
}

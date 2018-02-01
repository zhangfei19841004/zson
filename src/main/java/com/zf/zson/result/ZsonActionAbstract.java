package com.zf.zson.result;

import com.zf.zson.ZSON;
import com.zf.zson.ZsonUtils;
import com.zf.zson.common.Utils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.path.ZsonCurrentPath;
import com.zf.zson.result.impl.ZsonResultImpl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ZsonActionAbstract implements ZsonAction {

	private int deleteFromIndex;

	protected void deleteZsonResultInfoChilrenKey(ZsonResultImpl zri, String key) {
		Iterator<String> it = zri.getzResultInfo().getLevel().iterator();
		int index = 0;
		boolean flag = false;
		while (it.hasNext()) {
			String level = it.next();
			if (level.matches(key.replaceAll("\\.", "\\\\.") + "(\\.\\d+)*")) {
				if (!flag) {
					deleteFromIndex = index;
					flag = true;
				}
				it.remove();
				zri.getzResultInfo().getPath().remove(index);
				zri.getzResultInfo().getClassTypes().remove(index);
				zri.getzResultInfo().getIndex().remove(level);
				zri.getzResultInfo().getCollections().remove(index);
				index--;
			}
			index++;
		}
	}

	protected void replaceZsonResultInfoKey(ZsonResultImpl zrNew, String targetKey, String parentPath, List<String> handledPath, String addRootPath) {
		List<String> levels = zrNew.getzResultInfo().getLevel();
		Map<String, Map<String, Integer>> newIndex = Utils.getMap(zrNew.getzResultInfo().isLinked());
		for (int i = 0; i < levels.size(); i++) {
			String key = levels.get(i);
			String newLevel = targetKey + levels.get(i).substring(1);
			levels.set(i, newLevel);
			List<Object> paths = zrNew.getzResultInfo().getPath();
			this.updatePaths(zrNew, paths.get(i), parentPath, handledPath, addRootPath);
			Map<String, Map<String, Integer>> index = zrNew.getzResultInfo().getIndex();
			this.updateIndexs(index, newIndex, key, targetKey);
			this.updateCollections(zrNew.getzResultInfo().getCollections().get(i), targetKey);
		}
		zrNew.getzResultInfo().setIndex(newIndex);
	}

	protected void recorrectIndex(ZsonResultImpl zri) {
		List<String> levels = zri.getzResultInfo().getLevel();
		for (int i = 0; i < levels.size(); i++) {
			zri.getzResultInfo().getIndex().get(levels.get(i)).put(ZsonUtils.INDEX, i);
		}
	}

	private void updatePaths(ZsonResultImpl zrNew, Object paths, String parentPath, List<String> handledPath, String addRootPath) {
		ZsonObject<String> pathObj = new ZsonObject<String>();
		pathObj.objectConvert(paths);
		if (pathObj.isMap()) {
			Map<String, String> pathMap = pathObj.getZsonMap();
			for (String k : pathMap.keySet()) {
				String newPath = parentPath + pathMap.get(k);
				pathMap.put(k, newPath);
				if (handledPath != null) {
					if (zrNew.getzPath().ischildPath(addRootPath, newPath)) {
						handledPath.add(newPath);
					}
				}
			}
		} else if (pathObj.isList()) {
			List<String> pathList = pathObj.getZsonList();
			for (int i = 0; i < pathList.size(); i++) {
				String newPath = parentPath + pathList.get(i);
				pathList.set(i, newPath);
				if (handledPath != null) {
					if (zrNew.getzPath().ischildPath(addRootPath, newPath)) {
						handledPath.add(newPath);
					}
				}
			}
		}
	}

	private void updateIndexs(Map<String, Map<String, Integer>> index, Map<String, Map<String, Integer>> newIndex, String key, String targetKey) {
		Map<String, Integer> indexInfo = index.get(key);
		//indexInfo.put(ZsonUtils.INDEX, indexInfo.get(ZsonUtils.INDEX)+addIndex);
		String newKey = targetKey + key.substring(1);
		newIndex.put(newKey, indexInfo);
	}

	private void updateCollections(Object collection, String targetKey) {
		ZsonObject<Object> collectionObj = new ZsonObject<Object>();
		collectionObj.objectConvert(collection);
		if (collectionObj.isMap()) {
			Map<String, Object> collectionMap = collectionObj.getZsonMap();
			for (String k : collectionMap.keySet()) {
				ZsonObject<String> cElementObj = new ZsonObject<String>();
				cElementObj.objectConvert(collectionMap.get(k));
				if (cElementObj.isMap()) {
					Map<String, String> cMap = cElementObj.getZsonMap();
					cMap.put(ZsonUtils.LINK, targetKey + cMap.get(ZsonUtils.LINK).substring(1));
				} else if (cElementObj.isList()) {
					List<String> cList = cElementObj.getZsonList();
					cList.set(0, targetKey + cList.get(0).substring(1));
				}
			}
		} else if (collectionObj.isList()) {
			List<Object> collectionList = collectionObj.getZsonList();
			for (Object cObj : collectionList) {
				ZsonObject<String> cElementObj = new ZsonObject<String>();
				cElementObj.objectConvert(cObj);
				if (cElementObj.isMap()) {
					Map<String, String> cMap = cElementObj.getZsonMap();
					cMap.put(ZsonUtils.LINK, targetKey + cMap.get(ZsonUtils.LINK).substring(1));
				} else if (cElementObj.isList()) {
					List<String> cList = cElementObj.getZsonList();
					cList.set(0, targetKey + cList.get(0).substring(1));
				}
			}
		}
	}

	protected void addNewResultToSourceResult(ZsonResultImpl source, ZsonResultImpl newResult) {
		source.getzResultInfo().getLevel().addAll(deleteFromIndex, newResult.getzResultInfo().getLevel());
		source.getzResultInfo().getPath().addAll(deleteFromIndex, newResult.getzResultInfo().getPath());
		source.getzResultInfo().getClassTypes().addAll(deleteFromIndex, newResult.getzResultInfo().getClassTypes());
		source.getzResultInfo().getIndex().putAll(newResult.getzResultInfo().getIndex());
		source.getzResultInfo().getCollections().addAll(deleteFromIndex, newResult.getzResultInfo().getCollections());
	}

	protected ZsonCurrentPath setKeyOrIndexByPath(ZsonResultImpl zri, String path) {
		ZsonCurrentPath zcp = new ZsonCurrentPath();
		List<Object> paths = zri.getzResultInfo().getPath();
		for (Object pathObject : paths) {
			ZsonObject<String> pathObj = new ZsonObject<String>();
			pathObj.objectConvert(pathObject);
			if (pathObj.isList()) {
				int index = 0;
				for (String p : pathObj.getZsonList()) {
					if (p.equals(path)) {
						zcp.setIndex(index);
						return zcp;
					}
					index++;
				}
			} else if (pathObj.isMap()) {
				for (String k : pathObj.getZsonMap().keySet()) {
					if (pathObj.getZsonMap().get(k).equals(path)) {
						zcp.setKey(k);
						return zcp;
					}
				}
			}
		}
		return null;
	}

	protected String getKeyByPath(ZsonResultImpl zri, String path) {
		List<Object> paths = zri.getzResultInfo().getPath();
		int index = 0;
		for (Object pathObject : paths) {
			ZsonObject<String> pathObj = new ZsonObject<String>();
			pathObj.objectConvert(pathObject);
			if (pathObj.isList()) {
				for (String p : pathObj.getZsonList()) {
					if (p.equals(path)) {
						return zri.getzResultInfo().getLevel().get(index);
					}
				}
			} else if (pathObj.isMap()) {
				for (String p : pathObj.getZsonMap().values()) {
					if (p.equals(path)) {
						return zri.getzResultInfo().getLevel().get(index);
					}
				}
			}
			index++;
		}
		return null;
	}

	protected String getParentPath(String currentPath) {
		String parentPath = currentPath.substring(0, currentPath.lastIndexOf('/'));
		return parentPath;
	}

	protected Object getActionObject(ZsonResultImpl zri, Object str) {
		Object actionValue = str;
		try {
			ZsonResultImpl zra = (ZsonResultImpl) ZSON.parseJson(str.toString());
			actionValue = zra.getResultByKey(ZsonUtils.BEGIN_KEY);
		} catch (Exception e) {

		}
		return actionValue;
	}
}

package com.zf.zson.result.info;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZsonResultInfo {

	/**
	 * 标识json是否是一个合格的JSON串及解析是否全部解析完成
	 */
	private boolean valid = true;

	/**
	 * 存放解析JSON过程中所有的LIST与MAP
	 */
	private List<Object> collections = new ArrayList<Object>();

	/**
	 * index的最外层MAP的key为1 1.1 1.1.2这种形式，表示JSON的层次结构
	 * 里面的MAP为当前层次中的数据结构类型与状态，比如{"type":0,"status":0,"index":0},
	 * type有0,1，0表示MAP, 1表示LIST,
	 * status有0，1，0表示没有解析完成，1表示已解析完成
	 * index指对象在collections中的index
	 */
	private Map<String, Map<String, Integer>> index = new LinkedHashMap<String, Map<String, Integer>>();

	/**
	 * 标识json解析是否全部解析完成
	 */
	private boolean allFinished = false;

	/**
	 * 存放JSON的层次结构比如1 1.1 1.1.2
	 */
	private List<String> level = new ArrayList<String>();

	/**
	 * 存放JSON的path
	 */
	private List<Object> path = new ArrayList<Object>();

	/**
	 * 存放JSON的数据类型
	 */
	private List<Object> classTypes = new ArrayList<Object>();

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public List<Object> getCollections() {
		return collections;
	}

	public void setCollections(List<Object> collections) {
		this.collections = collections;
	}

	public Map<String, Map<String, Integer>> getIndex() {
		return index;
	}

	public void setIndex(Map<String, Map<String, Integer>> index) {
		this.index = index;
	}

	public boolean isAllFinished() {
		return allFinished;
	}

	public void setAllFinished(boolean allFinished) {
		this.allFinished = allFinished;
	}

	public List<String> getLevel() {
		return level;
	}

	public void setLevel(List<String> level) {
		this.level = level;
	}

	public List<Object> getPath() {
		return path;
	}

	public void setPath(List<Object> path) {
		this.path = path;
	}

	public List<Object> getClassTypes() {
		return classTypes;
	}

	public void setClassTypes(List<Object> classTypes) {
		this.classTypes = classTypes;
	}

}

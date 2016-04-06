package com.zf.zson;

public class ZsonInfo {

	private String element = null;//扫描出来的数据
	
	private String v = null;//如果是map，扫描出来的value
	
	private boolean isMap = false;//：后会被判定为MAP
	
	private boolean isMark = false;// 标识"
	
	private boolean isElementSeparate = false; //是不是逗号
	
	private int markIndex = 0;// 标识有多少个"
	

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public boolean isMap() {
		return isMap;
	}

	public void setMap(boolean isMap) {
		this.isMap = isMap;
	}

	public boolean isMark() {
		return isMark;
	}

	public void setMark(boolean isMark) {
		this.isMark = isMark;
	}

	public int getMarkIndex() {
		return markIndex;
	}

	public void setMarkIndex(int markIndex) {
		this.markIndex = markIndex;
	}

	public boolean isElementSeparate() {
		return isElementSeparate;
	}

	public void setElementSeparate(boolean isElementSeparate) {
		this.isElementSeparate = isElementSeparate;
	}
	
}

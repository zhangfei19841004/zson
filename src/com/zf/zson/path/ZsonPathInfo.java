package com.zf.zson.path;

import java.util.ArrayList;
import java.util.List;

public class ZsonPathInfo {
	
	private String pathKey;
	
	private boolean pathKeyIsRelative;
	
	private boolean pathIsList;
	
	private int pathListIndex;
	
	public String getPathKey() {
		return pathKey;
	}

	public void setPathKey(String pathKey) {
		this.pathKey = pathKey;
	}

	public boolean getPathKeyIsRelative() {
		return pathKeyIsRelative;
	}

	public void setPathKeyIsRelative(boolean pathKeyIsRelative) {
		this.pathKeyIsRelative = pathKeyIsRelative;
	}

	public boolean isPathIsList() {
		return pathIsList;
	}

	public void setPathIsList(boolean pathIsList) {
		this.pathIsList = pathIsList;
	}

	public int getPathListIndex() {
		return pathListIndex;
	}

	public void setPathListIndex(int pathListIndex) {
		this.pathListIndex = pathListIndex;
	}

	@Override
	public String toString() {
		return "ZsonPathInfo [pathKey=" + pathKey + ", pathKeyIsRelative="
				+ pathKeyIsRelative + ", pathIsList=" + pathIsList
				+ ", pathListIndex=" + pathListIndex + "]";
	}

	public static void main(String[] args) {
		List<String> l = new ArrayList<String>();
		l.add("s");
		l.add("t");
		l.add("r");
		l.add("i");
		l.add("n");
		l.add("g");
		System.out.println(l);
		l.remove(1);
		l.add(1, "T");
		System.out.println(l);
	}
}

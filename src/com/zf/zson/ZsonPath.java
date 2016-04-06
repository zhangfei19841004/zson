package com.zf.zson;

import java.util.ArrayList;
import java.util.List;

public class ZsonPath {
	
	private String path;

	public ZsonPath(String path) {
		this.path = path;
	}

	public ZsonPath() {
		super();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public boolean checkPath(){
		return path.matches("/+.*([^/])$");
	}
	
	public List<String> getXpath(){
		String[] paths = path.split("(?<!\\\\)/");
		List<String> list = new ArrayList<String>();
		for (int i = 1; i < paths.length; i++) {
			String p = paths[i];
			if(p.matches("\\*\\[\\d+\\]")){
				list.add(p.substring(p.indexOf(ZsonUtils.pathListIndexBegin)+1, p.indexOf(ZsonUtils.pathListIndexEnd)));
			}else{
				list.add(p);
			}
		}
		return list;
	}
	
	
	
	public static void main(String[] args) {
		ZsonPath zp = new ZsonPath("/a\\/db");
		System.out.println(zp.getXpath());
	}
	
	
	
}

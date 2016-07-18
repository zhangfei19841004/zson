package com.zf.zson.path;

import java.util.ArrayList;
import java.util.List;

import com.zf.zson.ZsonUtils;

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
	
	private boolean checkPath(){
		return path.matches("(/[^/]+)+");
	}
	
	private boolean checkRelativePath(){
		return path.matches("(//{0,1}[^/]+)+");
	}
	
	public List<String> getXpath(){
		if(path==null || !this.checkPath()){
			throw new RuntimeException("path is not valid! ["+path+"]");
		}
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
	
	public List<ZsonPathInfo> getRelativePath(){
		if(path==null || !this.checkRelativePath()){
			throw new RuntimeException("path is not valid! ["+path+"]");
		}
		char[] cs = path.toCharArray();
		StringBuffer sb = new StringBuffer();
		int currentPathSeparator = 0;
		int index = 0;
		List<ZsonPathInfo> list = new ArrayList<ZsonPathInfo>();
		for (char c : cs) {
			if(c==ZsonUtils.pathSeparator){
				if(index-currentPathSeparator==1){
					ZsonPathInfo pathInfo = list.get(list.size()-1);
					pathInfo.setPathKeyIsRelative(true);
				}else if(index-currentPathSeparator>1){
					this.setPathInfo(list, sb);
					list.add(new ZsonPathInfo());
				}else{
					list.add(new ZsonPathInfo());
				}
				currentPathSeparator = index;
			}else{
				sb.append(c);
			}
			if(index == cs.length-1){
				this.setPathInfo(list, sb);
			}else{
				index++;
			}
		}
		return list;
	}
	
	private void setPathInfo(List<ZsonPathInfo> list, StringBuffer sb){
		ZsonPathInfo pathInfo = list.get(list.size()-1);
		pathInfo.setPathKey(sb.toString());
		sb.delete(0,sb.length());
		if(pathInfo.getPathKey().matches("\\*\\[\\d+\\]")){
			pathInfo.setPathIsList(true);
			String listIndex = pathInfo.getPathKey().substring(pathInfo.getPathKey().indexOf(ZsonUtils.pathListIndexBegin)+1, pathInfo.getPathKey().indexOf(ZsonUtils.pathListIndexEnd));
			pathInfo.setPathListIndex(Integer.valueOf(listIndex));
		}else{
			pathInfo.setPathIsList(false);
		}
	}
	
	
	public static void main(String[] args) {
		ZsonPath zp = new ZsonPath("//*[2]//c/*[1]");
		System.out.println(zp.getRelativePath());
	}
	
	
	
}

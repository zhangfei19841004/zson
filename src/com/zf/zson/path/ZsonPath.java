package com.zf.zson.path;

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
	
	public boolean checkAbsolutePath(){
		if(this.isRootPath()){
			return true;
		}
		return path.matches("(/[^/]+)+");
	}
	
	public boolean checkRelativePath(){
		return path.matches("(//{0,1}[^/]+)*(//[^/]+)+(//{0,1}[^/]+)*");
	}
	
	public boolean checkPath(){
		if(this.isRootPath()){
			return true;
		}
		if(!this.checkAbsolutePath() && !this.checkRelativePath()){
			return false;
		}
		return true;
	}
	
	public boolean isRootPath(){
		return "".equals(path);
	}
	
	public boolean isMatchPath(String targetPath){
		/*if(this.isRootPath()){
			return targetPath.matches("/[^/]+");
		}*/
		if(this.checkAbsolutePath()){
			return path.equals(targetPath);
		}
		String regPath = path.replaceAll("\\/", "\\\\/").replaceAll("\\*", "\\\\*").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\\\/\\\\/", "(/.+)*\\\\/");
		return targetPath.matches(regPath);
	}
	
	public boolean ischildPath(String parentPath, String childPath){
		String regPath = parentPath.replaceAll("\\/", "\\\\/").replaceAll("\\*", "\\\\*").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\\\/\\\\/", "(/.+)*\\\\/");
		return childPath.matches(regPath+"/.+");
	}
	
	public static void main(String[] args) {
		ZsonPath zp = new ZsonPath("/\\/*[1]");
		System.out.println(zp.checkPath());
		System.out.println(zp.checkAbsolutePath());
		System.out.println(zp.checkRelativePath());
		System.out.println(zp.isMatchPath("/\\/*[1]"));
		System.out.println("/*[]/a".matches("/[^/]+"));
	}
	
	
	
}

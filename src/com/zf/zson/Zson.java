package com.zf.zson;


public class Zson {
	
	private String json;
	
	public Zson() {}
	
	public Zson(String json) {
		this.json = json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
	//用这种方式是线程不安全的，不建议使用
	public ZsonResult parseJson(){
		ZsonParse zp = new ZsonParse(json);
		return zp.fromJson();
	}
	
	//线程安全
	public ZsonResult parseJson(String json){
		ZsonParse zp = new ZsonParse(json);
		return zp.fromJson();
	}
	
}

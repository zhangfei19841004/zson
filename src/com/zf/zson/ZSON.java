package com.zf.zson;

import com.zf.zson.parse.ZsonParse;
import com.zf.zson.result.ZsonResult;
import com.zf.zson.result.utils.ZsonResultToString;

public class ZSON {
	
	public static ZsonResult parseJson(String json){
		ZsonParse zp = new ZsonParse(json);
		return zp.fromJson();
	}
	
	public static String toJsonString(Object object){
		ZsonResultToString zrt = new ZsonResultToString();
		return zrt.toJsonString(object);
	}
	
}

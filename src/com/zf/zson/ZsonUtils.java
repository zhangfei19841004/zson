package com.zf.zson;
/**
 * 
 * @author zhangfei
 * @net id: 再见理想
 * @QQ: 408129370
 * @博客地址: http://www.cnblogs.com/zhangfei/
 */
public class ZsonUtils {
	
	public final static char jsonStringBegin = '"';
	public final static char jsonStringEnd = '"';
	public final static char jsonElementConnector = ',';
	
	public final static char jsonListBegin = '[';
	public final static char jsonListEnd = ']';
	
	public final static char jsonMapBegin = '{';
	public final static char jsonMapEnd = '}';
	public final static char jsonMapConnector = ':';
	
	public final static String LINK = "link";
	
	public final static char pathSeparator = '/';
	public final static char pathListBegin = '*';
	public final static char pathListIndexBegin = '[';
	public final static char pathListIndexEnd = ']';
	
	public final static String TYPE = "type";
	public final static String STATUS = "status";
	public final static String INDEX = "index";
	
	public final static String BEGIN_KEY = "1";
	
	public final static String JSON_NOT_VALID = "这不是一个合法的JSON串!";
	
	public static String convert(String utfString){  
	    StringBuilder sb = new StringBuilder();  
	    int i = -1;  
	    int pos = 0;  
	    while((i=utfString.indexOf("\\u", pos)) != -1){  
	        sb.append(utfString.substring(pos, i));  
	        if(i+5 < utfString.length()){  
	            pos = i+6;  
	            sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));  
	        }  
	    }  
	    sb.append(utfString.substring(pos));  
	    return sb.toString();  
	}
	
	
}

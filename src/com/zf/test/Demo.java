package com.zf.test;

import com.zf.zson.Zson;
import com.zf.zson.result.ZsonResult;

public class Demo {

	public static void main(String[] args) {
		String s1 = "[{ \"firstName\": \"Eric\", \"lastName\": \"Clapton\", \"instrument\": \"guitar\" },{ \"firstName\": \"Sergei\", \"lastName\": \"Rachmaninoff\", \"instrument\": \"piano\" }] ";
		String s2 = "[0,1,2,3.14,4.00,\"3\",true,\"\"]";
		String s3 = "{\"a\":[\"a1\",\"a2\",\"a1\"],\"cb\":{\"a\":1},\"d\":[\"a\",{\"a\":[1,20]},{\"a\":2},\"\"],\"e\":\"b\"}";
		Zson z = new Zson();
		ZsonResult zr1 = z.parseJson(s1);
		System.out.println(zr1.getValue("/*[1]/firstName"));
		System.out.println(zr1.getMap("/*[1]"));
		
		ZsonResult zr2 = z.parseJson(s2);
		System.out.println(zr2.getInteger("/*[1]"));
		System.out.println(zr2.getLong("/*[2]"));
		System.out.println(zr2.getDouble("/*[3]"));
		System.out.println(zr2.getFloat("/*[4]"));
		System.out.println(zr2.getString("/*[5]"));
		System.out.println(zr2.getBoolean("/*[6]"));
		
		ZsonResult zr3 = z.parseJson(s3);
		System.out.println(zr3.getValues("//*[0]"));
		System.out.println(zr3.getValues("//*[1]"));
		System.out.println(zr3.getList("/a"));
		System.out.println(zr3.getMap("/cb"));
		System.out.println(zr3.toJsonString(zr3.getResult()));
		zr3.removeValue("/a/*[0]");
		System.out.println(zr3.getResult());
		System.out.println(zr3.getList("/a"));
		zr3.updateValue("/cb/a", 2);
		System.out.println(zr3.getResult());
	}

}

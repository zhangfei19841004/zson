package com.zf.test;

import com.zf.zson.ZSON;
import com.zf.zson.result.ZsonResult;

public class Demo {

	public static void main(String[] args) {
//		String s1 = "[{ \"firstName\": \"Eric\", \"lastName\": \"Clapton\", \"instrument\": \"guitar\" },{ \"firstName\": \"Sergei\", \"lastName\": \"Rachmaninoff\", \"instrument\": \"piano\" }] ";
//		ZsonResult zr1 = ZSON.parseJson(s1);
//		System.out.println(zr1.getValue("/*[1]/lastName"));
//		System.out.println(zr1.getValues("/*[1]/lastName"));
//		String s2 = "[0,1,2,3.14,4.00,\"3\",true,\"\"]";
//		System.out.println(ZSON.parseJson(s2).getBoolean("//*[6]"));
//		System.out.println(zr1.getMap("/*[1]"));
//		
//		ZsonResult zr2 = z.parseJson(s2);
//		System.out.println(zr2.getInteger("/*[1]"));
//		System.out.println(zr2.getLong("/*[2]"));
//		System.out.println(zr2.getDouble("/*[3]"));
//		System.out.println(zr2.getFloat("/*[4]"));
//		System.out.println(zr2.getString("/*[5]"));
//		System.out.println(zr2.getBoolean("/*[6]"));
		String s3 = "{\"a\":[\"a1\",{\"a2\":123},\"a1\"],\"cb\":{\"a\":1},\"d\":[\"a\",{\"a\":[1,20,{\"a\":[90]}]},{\"a\":2},\"\"],\"e\":\"b\"}";
		

		ZsonResult zr3 = ZSON.parseJson(s3);
//		System.out.println(zr3.getValues("//*[0]"));
//		System.out.println(ZSON.parseJson(zr3.getValues("//*[1]").get(1).toString()).getValue("//a"));
		
		//System.out.println(ZSON.parseJson(s2).addValue("/*[1]", "{\"a\":1}"));
		System.out.println(s3);
		zr3.addValue("//a", 1,"new");
		zr3.addValue("", "new","{\"a\":12}");
		System.out.println(zr3.getValues("//new"));
		System.out.println(zr3.getValue(""));
		zr3.deleteValue("//a2");
		zr3.updateValue("//a", 3);
		System.out.println(zr3.getValue());

	}

}

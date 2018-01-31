package com.zf.test;

import com.zf.zson.ZSON;

import java.io.IOException;
import java.io.InputStream;

public class TestDemo {

	public String read() {
		try {
			InputStream in = TestDemo.class.getClassLoader().getResourceAsStream("demo.json");
			byte[] b = new byte[1024];
			int index = 0;
			StringBuffer sb = new StringBuffer();
			while ((index = in.read(b)) != -1) {
				sb.append(new String(b, 0, index));
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void parse() {
		String json = this.read();
		long b = System.currentTimeMillis();
		ZSON.parseJson(json);
		System.out.println("zson: " + (System.currentTimeMillis() - b));
		long b1 = System.currentTimeMillis();
		/*JSON.parseObject(json);
		System.out.println("fastjson: "+ (System.currentTimeMillis()-b1));*/
	}

	public static void main(String[] args) {
		TestDemo td = new TestDemo();
		td.parse();
	}

}

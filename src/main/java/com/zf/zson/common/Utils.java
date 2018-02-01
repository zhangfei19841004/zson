package com.zf.zson.common;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangfei on 2018/1/31/031.
 */
public class Utils {

	public static <K, V> Map<K, V> getMap(boolean linked) {
		if (linked) {
			return new LinkedHashMap<K, V>();
		}
		return new HashMap<K, V>();
	}

	public static void main(String[] args) {
		Map<String, String> m = Utils.getMap(true);
		m.put("a", "b");
		System.out.println(m);
	}

}

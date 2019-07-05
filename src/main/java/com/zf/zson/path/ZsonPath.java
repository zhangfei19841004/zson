package com.zf.zson.path;

import java.util.ArrayList;
import java.util.List;

public class ZsonPath {

	private String path;

	private List<String> paths;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		paths = this.parsePath(path);
	}

	public boolean isAbsolutePath() {
		for (String p : paths) {
			if ("".equals(p)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkPath() {
		if (this.isRootPath()) {
			return true;
		}
		if (paths == null) {
			return false;
		}
		return true;
	}

	public boolean isRootPath() {
		return "".equals(path);
	}

	public boolean isMatchPath(String targetPath) {
		if (this.isAbsolutePath()) {
			return path.equals(targetPath);
		}
		List<String> allPaths = this.parsePath(targetPath);
		if (paths == null) {
			return false;
		}
		int lastMatchCount = 0;
		//相对路径最后一部分要相同
		for (int i = paths.size() - 1; i >= 0; i--) {
			String s = paths.get(i);
			if ("".equals(s)) {
				break;
			}
			lastMatchCount++;
			if (allPaths.size() < lastMatchCount) {
				return false;
			}
			if (!s.equals(allPaths.get(allPaths.size() - lastMatchCount))) {
				return false;
			}
		}
		if (!paths.get(paths.size() - 1).equals(allPaths.get(allPaths.size() - 1))) {
			return false;
		}
		boolean isRelativePath = false;
		int rfromIndex = 0;
		int relativePathIndex = 0;
		int fromIndex = 0;
		int pathIndex = 0;
		//从开始比较，一直比到最后一个相对路径的lastMatchCount
		while (rfromIndex < paths.size() - lastMatchCount - 1) {
			String s = paths.get(rfromIndex);
			rfromIndex++;
			if ("".equals(s)) {
				relativePathIndex = rfromIndex;
				pathIndex = fromIndex;
				isRelativePath = true;
				continue;
			}
			if (!isRelativePath) {
				if (!allPaths.get(fromIndex).equals(s)) {
					return false;
				} else {
					fromIndex++;
				}
			} else {
				boolean matched = false;
				while (fromIndex < allPaths.size() - lastMatchCount - 1) {
					String path = allPaths.get(fromIndex);
					fromIndex++;
					if (path.equals(s)) {
						matched = true;
						isRelativePath = false;
						break;
					} else {
						rfromIndex = relativePathIndex + 1;
						fromIndex = pathIndex + 1;
					}
				}
				if (!matched) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean ischildPath(String parentPath, String childPath) {
		String regPath = parentPath.replaceAll("\\/", "\\\\/").replaceAll("\\*", "\\\\*").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\\\/\\\\/", "(/.+)*\\\\/");
		return childPath.matches(regPath + "/.+");
	}

	private List<String> parsePath(String path) {
		char[] cs = path.toCharArray();
		boolean isEscapeChar = false;
		List<String> paths = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		int pathSeparatorCount = 0;
		for (int i = 0; i < cs.length; i++) {
			if (i == 0 && cs[i] != '/') {
				return null;
			}
			if (i == cs.length - 1 && cs[i] == '/' && !isEscapeChar) {
				return null;
			}
			if (i == 0) {
				pathSeparatorCount++;
				continue;
			}
			if (cs[i] == '\\') {
				isEscapeChar = true;
				continue;
			}
			if (cs[i] == '/' && !isEscapeChar) {
				pathSeparatorCount++;
				paths.add(sb.toString());
				sb.setLength(0);
			} else {
				pathSeparatorCount = 0;
				sb.append(cs[i]);
			}
			if (pathSeparatorCount > 2) {
				return null;
			}
			isEscapeChar = false;
		}
		paths.add(sb.toString());
		return paths;
	}

}

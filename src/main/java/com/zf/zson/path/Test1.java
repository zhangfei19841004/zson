package com.zf.zson.path;

import java.util.ArrayList;
import java.util.List;

public class Test1 {

    public List<String> parsePath(String path) {
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

    public boolean isMatchPath(){
        String s1 = "/a/b/c/a/d";
        String s2 = "//a/d";
        List<String> allPaths = this.parsePath(s1);
        List<String> paths = this.parsePath(s2);
        if(paths == null){
            return false;
        }
        if(!paths.get(paths.size()-1).equals(allPaths.get(allPaths.size()-1))){
            return false;
        }
        boolean isRelativePath = false;
        int fromIndex = 0;
        int relativePathIndex = 0;
        for (String s : paths) {
            if("".equals(s)){
                relativePathIndex = fromIndex;
                isRelativePath = true;
                continue;
            }
            if(!isRelativePath){
                if(!allPaths.get(fromIndex).equals(s)){
                    return false;
                }else{
                    fromIndex++;
                }
            }else{
                boolean matched = false;
                for (int i = fromIndex; i < allPaths.size(); i++) {
                    if(allPaths.get(i).equals(s)){
                        fromIndex = i+1;
                        matched = true;
                        isRelativePath = false;
                        break;
                    }
                }
                if(!matched){
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Test1 t = new Test1();
        System.out.println(t.isMatchPath());
    }

}

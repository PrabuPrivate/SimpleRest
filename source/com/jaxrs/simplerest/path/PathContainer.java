package com.jaxrs.simplerest.path;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class PathContainer {
	private PathContainer(){}
	private static HashMap<String,PathRule> staticPathMap = new HashMap<String,PathRule>();
	private static HashMap<Pattern,PathRule> dynamicPathMap = new HashMap<Pattern,PathRule>();
	static void addPathRule(PathRule rule) {
		if(!rule.isRegex())
			staticPathMap.put(rule.getPath(), rule);
		else
			dynamicPathMap.put(Pattern.compile(rule.getPath()),rule);
	}
	public static PathRule getRule(String path,String method) {
		path = PathUtil.getPathName(method,path);
		PathRule rule = staticPathMap.get(path);
		if(rule == null) {
			Iterator<Entry<Pattern, PathRule>> it = dynamicPathMap.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Pattern, PathRule> e = it.next();
				if(e.getKey().matcher(path).matches()) {
					rule = e.getValue();
					break;
				}
			}
		}
		return rule;
	}
}

package com.jaxrs.simplerest.path;

public class PathRule 
{
	private String className;
	private String methodName;
	private String path;
	private String requestMethod;
	private boolean isRegex = false;
	PathRule(String requestMethod, boolean isRegexPath,String path,  String className, String methodName){
		this.requestMethod = requestMethod;
		this.isRegex=isRegexPath;
		this.path = PathUtil.getPathName(requestMethod,path); 
		this.className = className;
		this.methodName = methodName;
	}
	
	public String getClassName() {
		return className;
	}
	public String getMethodName() {
		return methodName;
	}
	public String getPath() {
		return path;
	}
	public boolean isRegex() {
		return isRegex;
	}
	public String getRequestMethod() {
		return requestMethod;
	}
	public String getAbsoluteMethodName(){
		return className+"."+methodName;
	}
	public String toString(){
		return "['className':"+className+",'methodName':"+methodName+
		", 'path':"+path+", 'requestMethod':"+requestMethod+", 'isRegex':"+isRegex+"]";
	}
}

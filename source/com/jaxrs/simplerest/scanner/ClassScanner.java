package com.jaxrs.simplerest.scanner;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner implements Scanner
{
	private ArrayList<String> getFilesFromJar(String packageName,String jarFileName) throws Exception
	{
		ArrayList<String> names = new ArrayList<String>();
        JarFile jarFile = new JarFile(jarFileName);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while(jarEntries.hasMoreElements())
        {
        		JarEntry e = jarEntries.nextElement();
        		if(!e.isDirectory())
        		{
        			String fileName = e.getName();
    	            if(fileName.startsWith(packageName) && fileName.length()>packageName.length()+5)
    	            {
    	            		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
    	            		validateAndAddNames(fileName,names);
    	            }
        		}
           
        }
        if(jarFile != null)
        {
        		jarFile.close();
        }
        return names;
	}
	private ArrayList<String> getFilesFromFolder(File file,String packageName) throws Exception
	{
		Queue<File> queue = new LinkedList<File>();
		queue.add(file);
		ArrayList<String> names = new ArrayList<String>();
		File folder = queue.remove();
		while(folder != null)
		{
			File[] files = folder.listFiles();
	        for(File each: files)
	        {
	            if(each.isDirectory())
	            {
	            		queue.add(each);
	            		continue;
	            }
	            String path = each.getPath();
		        validateAndAddNames(path.substring(path.indexOf(packageName)),names);
	        }
	        folder = queue.isEmpty()? null:queue.remove();
		}
        return names;
	}
	private void validateAndAddNames(String fileName,ArrayList<String> names)
	{
		if(onAccept(fileName))
		{
			names.add(fileName.replace("/", ".").substring(0,fileName.lastIndexOf('.')));
		}
	}
	public void scan(String packageName,Object c)throws Exception
	{
		packageName = packageName.replace(".", "/");
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    URL packageURL = classLoader.getResource(packageName);
	    ArrayList<String> names = (ArrayList<String>)c;
	    if(packageURL != null &&  names != null)
	    {
	    		if(packageURL.getProtocol().equals("jar"))
		    {
		        String jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
		        jarFileName = jarFileName.substring(5,jarFileName.indexOf("!"));
		        names.addAll(getFilesFromJar(packageName,jarFileName));
		    }
		    else
		    {
		    		URI uri = new URI(packageURL.toString());
		    		File folder = new File(uri.getPath());
		    		names.addAll(getFilesFromFolder(folder,packageName));
		    }
	    }
	}
	public boolean onAccept(String name) 
	{
		 return name.endsWith(".class");
	}
}

package com.jaxrs.simplerest.testbeans;

import java.util.ArrayList;

import com.jaxrs.simplerest.annotation.ClassMapper;
import com.jaxrs.simplerest.annotation.ClassIdentifier;

@ClassIdentifier(key = "type", mapping = 
{@ClassMapper(value = "sub1", cls = Sub1.class),
 @ClassMapper(value = "sub2", cls = Sub2.class)})
public class MainClass1 
{
	private String name=null;
	private int age = 0;
	private ArrayList<Mark> marks = null;
	/*private String type=null;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}*/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public ArrayList<Mark> getMarks() {
		return marks;
	}
	public void setMarks(ArrayList<Mark> marks) {
		this.marks = marks;
	}
}

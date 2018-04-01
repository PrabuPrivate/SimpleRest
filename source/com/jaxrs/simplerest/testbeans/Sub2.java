package com.jaxrs.simplerest.testbeans;

import com.jaxrs.simplerest.annotation.CaseConversion;
import com.jaxrs.simplerest.testbeans.TestConstant.Gender;

public class Sub2 extends MainClass1{

	@CaseConversion private Gender gender=null;
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}
}

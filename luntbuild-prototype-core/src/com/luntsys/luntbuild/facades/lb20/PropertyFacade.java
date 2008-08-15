package com.luntsys.luntbuild.facades.lb20;

/**
 * Facade class of system property
 */
public class PropertyFacade {
	private String name;
	private String value;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}
}

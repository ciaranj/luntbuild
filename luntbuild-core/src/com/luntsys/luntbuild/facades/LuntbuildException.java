package com.luntsys.luntbuild.facades;


/**
 * Luntbuild general exception
 * @author robin shine
 */
public class LuntbuildException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public LuntbuildException(String message){
		super(message);
	}
	public LuntbuildException(){
		super();
	}
}

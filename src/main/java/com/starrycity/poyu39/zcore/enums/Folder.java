package com.starrycity.poyu39.zcore.enums;

public enum Folder {

	UTILS,

	;
	

	public String toFolder(){
		return name().toLowerCase();
	}
	
}

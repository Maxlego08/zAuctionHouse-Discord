package com.starrycity.zDiscord.zcore.enums;

public enum Permission {

	;

	private final String permission;

	Permission() {
		this.permission = this.name().toLowerCase().replace("_", ".");
	}

	public String getPermission() {
		return permission;
	}

}

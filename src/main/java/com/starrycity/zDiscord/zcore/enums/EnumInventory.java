package com.starrycity.zDiscord.zcore.enums;

public enum EnumInventory {

	INVENTORY_TEST(1),
	
	;
	
	private final int id;

	EnumInventory(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}

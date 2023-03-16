package com.starrycity.zDiscord.storage;

import com.starrycity.zDiscord.DiscordMessage;
import com.starrycity.zDiscord.zcore.utils.storage.Persist;
import com.starrycity.zDiscord.zcore.utils.storage.Saveable;

import java.util.ArrayList;
import java.util.List;

public class Storage implements Saveable {


	public static List<DiscordMessage> discordMessages = new ArrayList<DiscordMessage>();

	private static volatile Storage instance;

	private Storage() {
	}

	public static Storage getInstance() {
		if (instance == null) {
			synchronized (Storage.class) {
				if (instance == null) {
					instance = new Storage();
				}
			}
		}
		return instance;
	}

	@Override
	public void save(Persist persist) {
		persist.save(getInstance(), "storage");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(getInstance(), Storage.class, "storage");
	}

}

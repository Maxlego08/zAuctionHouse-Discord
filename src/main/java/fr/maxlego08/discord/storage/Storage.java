package fr.maxlego08.discord.storage;

import java.util.ArrayList;
import java.util.List;

import fr.maxlego08.discord.DiscordMessage;
import fr.maxlego08.discord.zcore.utils.storage.Persist;
import fr.maxlego08.discord.zcore.utils.storage.Saveable;

public class Storage implements Saveable {


	public static List<DiscordMessage> discordMessages = new ArrayList<DiscordMessage>();
	
	/**
	 * static Singleton instance.
	 */
	private static volatile Storage instance;

	/**
	 * Private constructor for singleton.
	 */
	private Storage() {
	}

	/**
	 * Return a singleton instance of Config.
	 */
	public static Storage getInstance() {
		// Double lock for thread safety.
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

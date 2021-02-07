package fr.maxlego08.discord.storage;

import java.util.ArrayList;
import java.util.List;

import fr.maxlego08.discord.zcore.utils.storage.Persist;
import fr.maxlego08.discord.zcore.utils.storage.Saveable;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Config implements Saveable {

	public static String discordToken = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	public static long channel = 807991026186715146l;
	public static long guildId = 511516467615760405l;

	public static List<GatewayIntent> gatewayIntents = new ArrayList<GatewayIntent>();

	static {
		for (GatewayIntent gatewayIntent : GatewayIntent.values())
			gatewayIntents.add(gatewayIntent);
	}

	/**
	 * static Singleton instance.
	 */
	private static volatile Config instance;

	/**
	 * Private constructor for singleton.
	 */
	private Config() {
	}

	/**
	 * Return a singleton instance of Config.
	 */
	public static Config getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (Config.class) {
				if (instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}

	@Override
	public void save(Persist persist) {
		persist.save(getInstance(), "config");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(getInstance(), Config.class, "config");
	}

}

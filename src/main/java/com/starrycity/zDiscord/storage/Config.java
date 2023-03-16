package com.starrycity.zDiscord.storage;

import com.starrycity.zDiscord.embed.EmbedField;
import com.starrycity.zDiscord.zcore.utils.Color;
import com.starrycity.zDiscord.zcore.utils.storage.Persist;
import com.starrycity.zDiscord.zcore.utils.storage.Saveable;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class Config implements Saveable {

	public static String discordToken = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	public static long channelID = 807991026186715146L;
	public static List<GatewayIntent> gatewayIntents = new ArrayList<GatewayIntent>();
	
	
	public static boolean use = false;
	public static boolean useTimestamp = true;
	public static boolean removeMessage = true;
	public static boolean editMessage = false;
	public static boolean removeExtrasCode = true;
	public static boolean hideItemEnchantWithHideFlag = true;
	public static String game = "zAuctionHouse VDEV-3";
	public static ActivityType gameActivityType = ActivityType.PLAYING;
	public static Color embedColor = new Color(255, 0, 0);
	public static Color embedColorEdit = new Color(0, 255, 0);
	public static String header = "none";
	public static String headerEdit = "Sold !";
	public static String footer = "zAuctionHouse";
	public static List<EmbedField> embeds = new ArrayList<EmbedField>();
	public static Map<String, String> enchantments = new HashMap<>();
	public static String enchantSeparator = " ";
	
	
	static {
		gatewayIntents.add(GatewayIntent.GUILD_MEMBERS);
		gatewayIntents.add(GatewayIntent.GUILD_EMOJIS_AND_STICKERS);
		gatewayIntents.add(GatewayIntent.DIRECT_MESSAGES);
		gatewayIntents.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
		gatewayIntents.add(GatewayIntent.GUILD_MESSAGES);
		gatewayIntents.add(GatewayIntent.GUILD_MESSAGE_TYPING);
		gatewayIntents.add(GatewayIntent.GUILD_PRESENCES);
		gatewayIntents.add(GatewayIntent.GUILD_VOICE_STATES);
		gatewayIntents.add(GatewayIntent.SCHEDULED_EVENTS);

		for(Enchantment enchantment : Enchantment.values()){
			enchantments.put(enchantment.getName(), enchantment.getName());
		}
		
		embeds.add(new EmbedField("Sale", "Player **%seller%** just added x%amount% %material% for **%price%%currency%**", false));	
		embeds.add(new EmbedField("Enchantment", "Item enchantment: **%enchant%**", false, false));
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

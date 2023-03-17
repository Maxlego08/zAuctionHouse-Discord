package fr.maxlego08.discord;

import java.util.List;

import fr.maxlego08.discord.listener.AuctionListener;
import fr.maxlego08.discord.storage.Config;
import fr.maxlego08.discord.storage.Storage;
import fr.maxlego08.discord.zcore.ZPlugin;
import fr.maxlego08.zauctionhouse.api.utils.Logger;
import fr.maxlego08.zauctionhouse.api.utils.Logger.LogType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

/**
 * 
 * @author Maxence
 * Discord bot for zAuctionHouse plugin
 *
 */
public class ZDiscordPlugin extends ZPlugin {

	private boolean isReady = false;
	private JDA jda;

	@Override
	public void onEnable() {

		this.preEnable();

		this.addListener(new AuctionListener(this));

		this.addSave(Config.getInstance());
		this.addSave(Storage.getInstance());

		this.getSavers().forEach(saver -> saver.load(this.getPersist()));
		
		//Refresh configuration with new field
		Config.getInstance().save(this.getPersist());

		// Log bot

		String token = Config.discordToken;
		List<GatewayIntent> intents = Config.gatewayIntents;

		//Load bot
		Thread thread = new Thread(() -> {

			try {

				JDABuilder builder = JDABuilder.create(token, intents);
				builder.setMemberCachePolicy(MemberCachePolicy.ALL);
				jda = builder.build();
				jda.getPresence().setActivity(Activity.of(Config.gameActivityType , Config.game));
				Logger.info("Loading of the discord bot successfully completed.");
				isReady = true;

			} catch (Exception e) {

				e.printStackTrace();
				Logger.info("Please read the error before coming on the discord to ask for help!", LogType.ERROR);
				Logger.info("If the error says: \"Cannot use CacheFlag.<something 1> without GatewayIntent.<something 2>\"", LogType.ERROR);
				Logger.info("You have to modify the config.json file to add the <something 2> in the gatewayIntents list.", LogType.ERROR);
				Logger.info("if the error says: The provided token is invalid!", LogType.ERROR);
				Logger.info("the token of your bot is invalid, and you have to add it in the config.json file", LogType.ERROR);
				isReady = false;
				this.getPluginLoader().disablePlugin(this);
			}

		}, "zDiscord-BOT");
		thread.start();

		this.postEnable();

	}

	@Override
	public void onDisable() {

		this.preDisable();

		if (jda != null) {
			jda.shutdownNow();
		}

		Storage.getInstance().save(this.getPersist());

		this.postDisable();

	}

	/**
	 * Get jda instance
	 * @return {@link JDA}
	 */
	public JDA getJda() {
		return jda;
	}

	/**
	 * Check if plugin is ready
	 * @return boolean
	 */
	public boolean isReady() {
		return isReady;
	}
	

}

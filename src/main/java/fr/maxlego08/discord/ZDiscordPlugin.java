package fr.maxlego08.discord;

import java.util.List;

import javax.security.auth.login.LoginException;

import fr.maxlego08.discord.listener.AuctionListener;
import fr.maxlego08.discord.storage.Config;
import fr.maxlego08.discord.zcore.ZPlugin;
import fr.maxlego08.zauctionhouse.api.utils.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class ZDiscordPlugin extends ZPlugin {

	private boolean isReady = false;
	private JDA jda;

	@Override
	public void onEnable() {

		this.preEnable();

		this.addListener(new AuctionListener(this));
		this.addSave(Config.getInstance());

		this.getSavers().forEach(saver -> saver.load(this.getPersist()));
		this.getSavers().forEach(saver -> saver.save(this.getPersist()));

		// Log bot

		String token = Config.discordToken;
		List<GatewayIntent> intents = Config.gatewayIntents;

		Thread thread = new Thread(() -> {

			try {
				JDABuilder builder = JDABuilder.create(token, intents);
				builder.setMemberCachePolicy(MemberCachePolicy.ALL);
				jda = builder.build();
				Logger.info("Loading of the discord bot successfully completed.");
				isReady = true;
			} catch (LoginException e) {

				isReady = false;
				this.getPluginLoader().disablePlugin(this);
				e.printStackTrace();
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

		this.postDisable();

	}

	public JDA getJda() {
		return jda;
	}

	public boolean isReady() {
		return isReady;
	}

}

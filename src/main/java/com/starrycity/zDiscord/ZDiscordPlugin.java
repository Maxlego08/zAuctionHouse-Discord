package com.starrycity.zDiscord;

import com.starrycity.zDiscord.listener.AuctionListener;
import com.starrycity.zDiscord.storage.Config;
import com.starrycity.zDiscord.storage.Storage;
import com.starrycity.zDiscord.zcore.ZPlugin;
import fr.maxlego08.zauctionhouse.api.utils.Logger;
import fr.maxlego08.zauctionhouse.api.utils.Logger.LogType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.List;

/**
 * 
 * @author Maxence & poyu39
 * Starry 貿易平台分支
 * 主要更新至 1.19 版本和繁體中文化
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
		
		// 更新設定檔
		Config.getInstance().save(this.getPersist());

		String token = Config.discordToken;
		List<GatewayIntent> intents = Config.gatewayIntents;

		// 載入 bot
		Thread thread = new Thread(() -> {

			try {

				JDABuilder builder = JDABuilder.create(token, intents);
				builder.setMemberCachePolicy(MemberCachePolicy.ALL);
				jda = builder.build();
				jda.getPresence().setActivity(Activity.of(Config.gameActivityType , Config.game));
				Logger.info("成功載入 Discord Bot");
				isReady = true;

			} catch (Exception e) {

				e.printStackTrace();
				Logger.info("請確認錯誤訊息後再到 Discord 尋求協助", LogType.ERROR);
				Logger.info("如果錯誤顯示：“無法使用 CacheFlag。<something 1> without GatewayIntent.<something 2>”", LogType.ERROR);
				Logger.info("你必須修改 config.json 文件以將 <something 2> 添加到 gatewayIntents 列表中。", LogType.ERROR);
				Logger.info("如果錯誤說：提供的 Token 無效！", LogType.ERROR);
				Logger.info("你的機器人 Token 無效，你必須將其添加到 config.json 文件中", LogType.ERROR);
				isReady = false;
				this.getServer().getPluginManager().disablePlugin(this);
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
	 * 取得 JDA
	 * @return {@link JDA}
	 */
	public JDA getJda() {
		return jda;
	}

	/**
	 * 檢查插件是否已經準備好
	 * @return boolean
	 */
	public boolean isReady() {
		return isReady;
	}
}

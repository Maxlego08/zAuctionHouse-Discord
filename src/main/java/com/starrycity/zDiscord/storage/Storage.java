package com.starrycity.zDiscord.storage;

import java.util.ArrayList;
import java.util.List;

import com.starrycity.zDiscord.zcore.utils.storage.Persist;
import com.starrycity.zDiscord.zcore.utils.storage.Saveable;
import com.starrycity.zDiscord.DiscordMessage;

public class Storage implements Saveable {


	public static List<DiscordMessage> discordMessages = new ArrayList<DiscordMessage>();
	
	/**
	 * 靜態實例
	 */
	private static volatile Storage instance;

	/**
	 * 私有建構子
	 */
	private Storage() {
	}

	/**
	 * 返回 Config 的單例實例。
	 */
	public static Storage getInstance() {
		// 雙重檢查 Thread Safe 的單例模式
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

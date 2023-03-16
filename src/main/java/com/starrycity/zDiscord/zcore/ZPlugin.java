package com.starrycity.zDiscord.zcore;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.starrycity.zDiscord.zcore.logger.Logger;
import com.starrycity.zDiscord.zcore.utils.storage.Persist;
import com.starrycity.zDiscord.zcore.utils.storage.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class ZPlugin extends JavaPlugin {

	private final Logger log = new Logger(this.getDescription().getFullName());
	private Gson gson;
	private Persist persist;
	private static ZPlugin plugin;
	private long enableTime;
	private List<Saveable> savers = new ArrayList<Saveable>();

	public ZPlugin() {
		plugin = this;
	}

	protected void preEnable() {

		enableTime = System.currentTimeMillis();

		log.log("=== ENABLE START ===");
		log.log("Plugin Version V<&>c" + getDescription().getVersion(), Logger.LogType.INFO);
		getDataFolder().mkdirs();

		gson = getGsonBuilder().create();
		persist = new Persist(this);


	}

	protected void postEnable() {


		log.log("=== ENABLE DONE <&>7(<&>6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<&>7) <&>e===");

	}

	protected void preDisable() {

		enableTime = System.currentTimeMillis();
		log.log("=== DISABLE START ===");

	}

	protected void postDisable() {

		log.log("=== DISABLE DONE <&>7(<&>6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<&>7) <&>e===");

	}

	public GsonBuilder getGsonBuilder() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}

	public void addListener(Listener listener) {
		if (listener instanceof Saveable)
			addSave((Saveable) listener);
		Bukkit.getPluginManager().registerEvents(listener, this);
	}

	public void addSave(Saveable saver) {
		this.savers.add(saver);
	}


	public Logger getLog() {
		return this.log;
	}


	public Gson getGson() {
		return gson;
	}

	public Persist getPersist() {
		return persist;
	}

	public List<Saveable> getSavers() {
		return savers;
	}

	public static ZPlugin z() {
		return plugin;
	}

	protected <T> T getProvider(Class<T> classz) {
		RegisteredServiceProvider<T> provider = getServer().getServicesManager().getRegistration(classz);
		if (provider == null) {
			log.log("Unable to retrieve the provider " + classz.toString(), Logger.LogType.WARNING);
			return null;
		}
		provider.getProvider();
		return (T) provider.getProvider();
	}

}

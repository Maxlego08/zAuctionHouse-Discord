package com.starrycity.poyu39.zcore;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.starrycity.poyu39.zcore.logger.Logger;
import com.starrycity.poyu39.zcore.utils.storage.Persist;
import com.starrycity.poyu39.zcore.utils.storage.Saveable;
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

	protected boolean preEnable() {

		enableTime = System.currentTimeMillis();

		log.log("=== ENABLE START ===");
		log.log("Plugin Version V<&>c" + getDescription().getVersion(), Logger.LogType.INFO);

		getDataFolder().mkdirs();

		gson = getGsonBuilder().create();
		persist = new Persist(this);


		return true;

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

	/**
	 * Build gson
	 * 
	 * @return
	 */
	public GsonBuilder getGsonBuilder() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}

	/**
	 * Add a listener
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener) {
		if (listener instanceof Saveable)
			addSave((Saveable) listener);
		Bukkit.getPluginManager().registerEvents(listener, this);
	}

	/**
	 * Add a Saveable
	 * 
	 * @param saver
	 */
	public void addSave(Saveable saver) {
		this.savers.add(saver);
	}

	/**
	 * Get logger
	 * 
	 * @return loggers
	 */
	public Logger getLog() {
		return this.log;
	}

	/**
	 * Get gson
	 * 
	 * @return {@link Gson}
	 */
	public Gson getGson() {
		return gson;
	}

	public Persist getPersist() {
		return persist;
	}

	/**
	 * Get all saveables
	 * 
	 * @return savers
	 */
	public List<Saveable> getSavers() {
		return savers;
	}

	public static ZPlugin z() {
		return plugin;
	}

	/**
	 * 
	 * @param classz
	 * @return
	 */
	protected <T> T getProvider(Class<T> classz) {
		RegisteredServiceProvider<T> provider = getServer().getServicesManager().getRegistration(classz);
		if (provider == null) {
			log.log("Unable to retrieve the provider " + classz.toString(), Logger.LogType.WARNING);
			return null;
		}
		return provider.getProvider() != null ? (T) provider.getProvider() : null;
	}

}

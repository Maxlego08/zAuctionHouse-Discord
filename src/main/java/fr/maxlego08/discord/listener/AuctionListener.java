package fr.maxlego08.discord.listener;

import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;

import fr.maxlego08.discord.action.Action;
import fr.maxlego08.discord.action.ActionType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.discord.DiscordMessage;
import fr.maxlego08.discord.ZDiscordPlugin;
import fr.maxlego08.discord.storage.Config;
import fr.maxlego08.discord.storage.Storage;
import fr.maxlego08.discord.zcore.utils.ZUtils;
import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionAdminRemoveEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionItemExpireEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionPostBuyEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionRetrieveEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionSellEvent;
import fr.maxlego08.zauctionhouse.api.utils.Logger;
import fr.maxlego08.zauctionhouse.api.utils.Logger.LogType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;

public class AuctionListener extends ZUtils implements Listener {

	private final ZDiscordPlugin plugin;

	/**
	 * @param plugin
	 */
	public AuctionListener(ZDiscordPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@EventHandler
	public void onSell(AuctionSellEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();

		JDA jda = plugin.getJda();

		if (jda == null) {
			Logger.info("Impossible to find JDA, did you give permissions to your bot?", LogType.ERROR);
			return;
		}
		TextChannel channel = null;
		try {
			channel = jda.getTextChannelById(Config.channelID);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("Impossible to find channel, did you give permissions to your bot?", LogType.ERROR);
		}

		if (channel == null) {
			Logger.info("Unable to find the channel discord, please check your configuration.", LogType.ERROR);
			return;
		}

		TextChannel finalChannel = channel;
		runAsync(() -> {
			EmbedBuilder builder = getBuilder(auctionItem, ActionType.SALE);

			if (builder == null) {
				Logger.info("Unable to find the embed builder, please check your configuration.", LogType.ERROR);
				return;
			}

			Message message = finalChannel.sendMessageEmbeds(builder.build()).complete();

			if (message == null) {
				Logger.info("Unable to create the message, please check your configuration.", LogType.ERROR);
				return;
			}

			DiscordMessage discordMessage = new DiscordMessage(finalChannel.getIdLong(), message.getIdLong(), auctionItem.getUniqueId());

			if (Config.removeMessage || Config.editMessage)
				Storage.discordMessages.add(discordMessage);

		});
	}

	@EventHandler
	public void onRetrieve(AuctionRetrieveEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();
		editOrRemoveEmbed(auctionItem, ActionType.RETRIEVED, Config.removeMessage);
	}

	@EventHandler
	public void onAdminRemove(AuctionAdminRemoveEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();
		editOrRemoveEmbed(auctionItem, ActionType.ADMIN_REMOVED, Config.removeMessage);
	}

	@EventHandler
	public void onBuy(AuctionPostBuyEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();
		editOrRemoveEmbed(auctionItem, ActionType.BOUGHT, Config.removeMessage);
	}

	@EventHandler
	public void onExpire(AuctionItemExpireEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();
		editOrRemoveEmbed(auctionItem, ActionType.EXPIRED, Config.removeMessage);
	}

	/**
	 * Edit or remove the embed
	 * @param auctionItem the auction item
	 * @param actionType the action type
	 * @param removeEmbed if the embed should be removed
	 */
	private void editOrRemoveEmbed(AuctionItem auctionItem, ActionType actionType, boolean removeEmbed) {
		Optional<DiscordMessage> optional = Storage.discordMessages.stream().filter(message -> auctionItem.getUniqueId().equals(message.getUniqueId())).findFirst();
		if (!optional.isPresent()) return;

		DiscordMessage discordMessage = optional.get();
		Storage.discordMessages.remove(discordMessage);

		JDA jda = plugin.getJda();
		TextChannel channel = jda.getTextChannelById(discordMessage.getChannelID());

		if (channel == null) {
			Logger.getLogger().log("Unable to find the discord channel, please check your configuration.", LogType.ERROR);
			return;
		}

		runAsync(() -> {
			Message message = channel.retrieveMessageById(discordMessage.getMessageID()).complete();
			if (message == null) return;

			if (removeEmbed) {
				message.delete().queue();
				return;
			}

			EmbedBuilder builder = getBuilder(auctionItem, actionType);
			if (builder == null) {
				Logger.getLogger().log("Unable to find the embed builder for Action: %s, please check your configuration.", LogType.ERROR, actionType.name());
				return;
			}

			message.editMessageEmbeds(builder.build()).queue();
		});
	}

	/**
	 * Get an embed builder from an ActionType
	 * @param auctionItem the auction item
	 * @param actionType the action type
	 * @return the embed builder or null if the action type is not found
	 */
	@Nullable
	private EmbedBuilder getBuilder(AuctionItem auctionItem, ActionType actionType) {
		EmbedBuilder builder = new EmbedBuilder();
		if (!Config.actions.containsKey(actionType))
			return null;
		Action action = Config.actions.get(actionType);
		builder.setColor(action.getEmbedColor().color());

		if (!action.getHeader().equalsIgnoreCase("none"))
			builder.setTitle(this.replaceString(action.getHeader(), auctionItem));

		if (Config.useTimestamp)
			builder.setTimestamp(OffsetDateTime.now());

		action.getEmbedFields().forEach(embedField -> {
			if (!(embedField.getMessage().contains("%enchant%") && !embedField.displayWhenEnchantIsNull()
					&& getEnchant(auctionItem.getItemStack()).equals("nothing"))) {

				builder.addField(replaceString(embedField.getTile(), auctionItem),
						replaceString(embedField.getMessage(), auctionItem), embedField.isInLine());
			}
		});

		if (!action.getFooter().equalsIgnoreCase("none"))
			builder.setFooter(this.replaceString(action.getFooter(), auctionItem), null);

		return builder;
	}

	/**
	 * Replace the placeholders in a string
	 * @param string the string containing the placeholders
	 * @param auctionItem the auction item
	 * @return the string with the placeholders replaced
	 */
	private String replaceString(String string, AuctionItem auctionItem) {

		string = string.replace("%seller%", auctionItem.getSeller().getName());

		if (auctionItem.getBuyerUniqueId() != null) {
			string = string.replace("%buyer%", auctionItem.getBuyer().getName());
		}

		string = string.replace("%price%", auctionItem.priceFormat());

		switch (auctionItem.getType()) {
		case DEFAULT:
		case BID:
		case INVENTORY:
		default:
			ItemStack itemStack = auctionItem.getItemStack();
			string = string.replace("%amount%", String.valueOf(itemStack.getAmount()));
			string = string.replace("%material%", getItemName(itemStack, Config.removeExtrasCode));
			string = string.replace("%enchant%", getEnchant(itemStack));
			break;
		}

		return string;
	}

	/**
	 * Get the enchantments of the item
	 * @param item the item
	 * @return a string containing all the enchantments
	 */
	private String getEnchant(ItemStack item) {
		StringBuilder builder = new StringBuilder();
		if (Config.hideItemEnchantWithHideFlag && item.hasItemMeta() && item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))
			builder.append("nothing");
		else if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
			Iterator<Entry<Enchantment, Integer>> it = item.getItemMeta().getEnchants().entrySet().iterator();
			while (it.hasNext()) {
				Entry<Enchantment, Integer> enchant = it.next();
				builder.append(betterEnchant(enchant.getKey(), enchant.getValue()));
				if (it.hasNext())
					builder.append(Config.enchantSeparator);
			}
		} else if (item.getType().equals(Material.ENCHANTED_BOOK)) {
			ItemMeta itemMeta = item.getItemMeta();
			if (itemMeta instanceof EnchantmentStorageMeta) {
				EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
				if (enchantmentStorageMeta.hasStoredEnchants()) {
					Iterator<Entry<Enchantment, Integer>> it = enchantmentStorageMeta.getStoredEnchants().entrySet().iterator();
					while (it.hasNext()) {
						Entry<Enchantment, Integer> enchant = it.next();
						builder.append(betterEnchant(enchant.getKey(), enchant.getValue()));
						if (it.hasNext())
							builder.append(Config.enchantSeparator);
					}
				}
			}
		} else
			builder.append("nothing");
		return builder.toString();
	}

	@SuppressWarnings("deprecation")
	private String betterEnchant(Enchantment key, int level) {
		return Config.enchantments.getOrDefault(key.getName(), key.getName()) + " " + level;
	}

}

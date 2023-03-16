package com.starrycity.zDiscord.listener;

import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import com.starrycity.zDiscord.zcore.utils.ZUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.starrycity.zDiscord.DiscordMessage;
import com.starrycity.zDiscord.ZDiscordPlugin;
import com.starrycity.zDiscord.storage.Config;
import com.starrycity.zDiscord.storage.Storage;
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

public class AuctionListener extends ZUtils implements Listener {

	private final ZDiscordPlugin plugin;

	public AuctionListener(ZDiscordPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@EventHandler
	public void onSell(AuctionSellEvent event) {

		AuctionItem auctionItem = event.getAuctionItem();

		JDA jda = plugin.getJda();

		if (jda == null) {
			Logger.info("無法找到 Bot ，請確認 Bot 權限。", LogType.ERROR);
			return;
		}
		TextChannel channel = null;
		try {
			channel = jda.getTextChannelById(Config.channelID);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("無法尋找頻道，請確認 Bot 權限。", LogType.ERROR);
		}

		if (channel == null) {
			Logger.info("找不到頻道，請確認設定檔設定正確頻道ID。", LogType.ERROR);
			return;
		}

		MessageChannel finalChannel = channel;
		runAsync(() -> {

			EmbedBuilder builder = getBuilder(auctionItem, false);

			Message message = finalChannel.sendMessageEmbeds(builder.build()).complete();

			if (message == null) {
				Logger.info("無法創建訊息，請檢查設定檔。", LogType.ERROR);
				return;
			}

			DiscordMessage discordMessage = new DiscordMessage(finalChannel.getIdLong(), message.getIdLong(),
					auctionItem.getUniqueId());

			if (Config.removeMessage || Config.editMessage)
				Storage.discordMessages.add(discordMessage);

		});
	}

	@EventHandler
	public void onRetrieve(AuctionRetrieveEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();
		remove(auctionItem);
	}

	@EventHandler
	public void onRetrieve(AuctionAdminRemoveEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();
		remove(auctionItem);
	}

	@EventHandler
	public void onBuy(AuctionPostBuyEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();
		remove(auctionItem);
	}

	@EventHandler
	public void onExpire(AuctionItemExpireEvent event) {
		AuctionItem auctionItem = event.getAuctionItem();
		remove(auctionItem);
	}

	private void remove(AuctionItem auctionItem) {

		Optional<DiscordMessage> optional = Optional.empty();
		for (DiscordMessage discordMessage : Storage.discordMessages) {
			if (auctionItem.getUniqueId().equals(discordMessage.getUniqueId())) {
				optional = Optional.of(discordMessage);
				break;
			}
		}

		if (!optional.isPresent()) {
			return;
		}

		DiscordMessage discordMessage = optional.get();
		Storage.discordMessages.remove(discordMessage);

		JDA jda = plugin.getJda();
		TextChannel channel = jda.getTextChannelById(discordMessage.getChannelID());

		runAsync(() -> {
			Message message;
			try {
				message = Objects.requireNonNull(channel).retrieveMessageById(discordMessage.getMessageID()).complete();
			} catch (NullPointerException e) {
				return;
			}
			if (Config.removeMessage) {
				message.delete().queue();
			} else if (Config.editMessage) {
				EmbedBuilder builder = getBuilder(auctionItem, true);
				message.editMessageEmbeds(builder.build()).queue();
			}

		});
	}


	private EmbedBuilder getBuilder(AuctionItem auctionItem, boolean isEdited) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(!isEdited ? Config.embedColor.color() : Config.embedColorEdit.color());

		if (!Config.header.equalsIgnoreCase("none") || (!Config.headerEdit.equalsIgnoreCase("none") && isEdited)) {
			builder.setTitle(this.replaceString(!isEdited ? Config.header : Config.headerEdit, auctionItem));
		}

		if (Config.useTimestamp) {
			builder.setTimestamp(OffsetDateTime.now());
		}

		Config.embeds.forEach(item -> {
			if (!(item.getMessage().contains("%enchant%") && !item.displayWhenEnchantIsNull()
					&& getEnchant(auctionItem.getItemStack()).equals("nothing"))) {

				builder.addField(replaceString(item.getTile(), auctionItem), replaceString(item.getMessage(), auctionItem),
						item.isInLine());

			}
		});
		if (!Config.footer.equalsIgnoreCase("none")) {
			builder.setFooter(this.replaceString(Config.footer, auctionItem), null);
		}
		return builder;
	}

	private String replaceString(String string, AuctionItem auctionItem) {

		string = string.replace("%seller%", Objects.requireNonNull(auctionItem.getSeller().getName()));

		if (auctionItem.getBuyerUniqueId() != null) {
			string = string.replace("%buyer%", Objects.requireNonNull(auctionItem.getBuyer().getName()));
		}

		string = string.replace("%price%", format(auctionItem.getPrice()));
		string = string.replace("%currency%", auctionItem.getEconomy().toCurrency());

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

	private String getEnchant(ItemStack item) {
		StringBuilder builder = new StringBuilder();
		if (Config.hideItemEnchantWithHideFlag && item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))
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

package fr.maxlego08.discord.listener;

import java.time.OffsetDateTime;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
		TextChannel channel = jda.getTextChannelById(Config.channelID);

		if (channel == null) {
			Logger.info("Unable to find the channel discord, please check your configuration.", LogType.ERROR);
			return;
		}

		runAsync(() -> {

			EmbedBuilder builder = getBuilder(auctionItem, false);
			
			if (builder == null) {
				Logger.info("Unable to find the embed builder, please check your configuration.", LogType.ERROR);
				return;
			}
			
			Message message = channel.sendMessage(builder.build()).complete();

			if (message == null) {
				Logger.info("Unable to create the message, please check your configuration.", LogType.ERROR);
				return;
			}
			
			DiscordMessage discordMessage = new DiscordMessage(channel.getIdLong(), message.getIdLong(),
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

		Optional<DiscordMessage> optional = Storage.discordMessages.stream().filter(message -> {
			return auctionItem.getUniqueId().equals(message.getUniqueId());
		}).findFirst();

		if (!optional.isPresent())
			return;

		DiscordMessage discordMessage = optional.get();
		Storage.discordMessages.remove(discordMessage);

		JDA jda = plugin.getJda();
		TextChannel channel = jda.getTextChannelById(discordMessage.getChannelID());

		runAsync(() -> {

			Message message = channel.retrieveMessageById(discordMessage.getMessageID()).complete();

			if (message == null)
				return;

			if (Config.removeMessage)
				message.delete().queue();
			else if (Config.editMessage) {
				EmbedBuilder builder = getBuilder(auctionItem, true);
				message.editMessage(builder.build()).queue();
			}

		});
	}

	/**
	 * 
	 * @param event
	 * @param bool
	 * @return
	 */
	private EmbedBuilder getBuilder(AuctionItem event, boolean bool) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(!bool ? Config.embedColor.color() : Config.embedColorEdit.color());

		if (!Config.header.equalsIgnoreCase("none") || (!Config.headerEdit.equalsIgnoreCase("none") && bool))
			builder.setTitle(!bool ? Config.header : Config.headerEdit);
		if (Config.useTimestamp)
			builder.setTimestamp(OffsetDateTime.now());
		Config.embeds.forEach(item -> {
			if (!(item.getMessage().contains("%enchant%") && !item.displayWhenEnchantIsNull()
					&& getEnchant(event.getItemStack()).equals("nothing"))) {
				builder.addField(replaceString(item.getTile(), event), replaceString(item.getMessage(), event),
						item.isInLine());
			}
		});
		if (!Config.footer.equalsIgnoreCase("none"))
			builder.setFooter(Config.footer, null);
		return builder;
	}

	/**
	 * 
	 * @param string
	 * @param auctionItem
	 * @return
	 */
	private String replaceString(String string, AuctionItem auctionItem) {

		string = string.replace("%seller%", auctionItem.getSeller().getName());

		if (auctionItem.getBuyerUniqueId() != null)
			string = string.replace("%buyer%", auctionItem.getBuyer().getName());

		string = string.replace("%price%", format(auctionItem.getPrice()));
		string = string.replace("%currency%", auctionItem.getEconomy().toCurrency());

		switch (auctionItem.getType()) {
		case INVENTORY:

			break;

		default:
			ItemStack itemStack = auctionItem.getItemStack();
			string = string.replace("%amount%", String.valueOf(itemStack.getAmount()));
			string = string.replace("%material%", getItemName(itemStack));
			string = string.replace("%enchants%", getEnchant(itemStack));
			break;
		}

		return string;
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	private String getEnchant(ItemStack item) {
		StringBuilder builder = new StringBuilder();
		if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
			for (Entry<Enchantment, Integer> enchants : item.getItemMeta().getEnchants().entrySet()) {
				builder.append(betterEnchant(enchants.getKey(), enchants.getValue()));
				builder.append(" ");
			}
		} else {
			builder.append("nothing");
		}

		return builder.toString();

	}

	@SuppressWarnings("deprecation")
	private String betterEnchant(Enchantment key, int level) {
		return Config.enchantments.getOrDefault(key.getName(), key.getName()) + " " + level;
	}

}

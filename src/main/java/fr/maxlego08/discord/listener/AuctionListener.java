package fr.maxlego08.discord.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.maxlego08.discord.ZDiscordPlugin;
import fr.maxlego08.discord.storage.Config;
import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.enums.Economy;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionSellEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class AuctionListener implements Listener {

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
		Player player = event.getPlayer();
		Economy economy = event.getEconomy();
		long price = event.getPrice();

		System.out.println(auctionItem + " - " + economy + " - " + player + " - " + price);

		JDA jda = plugin.getJda();
		
		TextChannel channel = jda.getTextChannelById(Config.channelID);
		System.out.println(channel);
		
		channel.sendMessage(player.getName() + " vient de vendre " + auctionItem.getItemStack() + " pour " + price + " "
				+ economy.toCurrency()).queue();

	}

}

package fr.maxlego08.discord.storage;

import java.util.*;

import fr.maxlego08.discord.action.Action;
import fr.maxlego08.discord.action.ActionType;
import org.bukkit.enchantments.Enchantment;

import fr.maxlego08.discord.embed.EmbedField;
import fr.maxlego08.discord.zcore.utils.Color;
import fr.maxlego08.discord.zcore.utils.storage.Persist;
import fr.maxlego08.discord.zcore.utils.storage.Saveable;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.requests.GatewayIntent;

@SuppressWarnings("deprecation")
public class Config implements Saveable {

    public static String discordToken = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

    public static long channelID = 807991026186715146L;
    public static List<GatewayIntent> gatewayIntents = new ArrayList<GatewayIntent>();

    public static boolean use = false;
    public static boolean useTimestamp = true;
    public static boolean removeMessage = true;
    public static boolean editMessage = false;
    public static boolean removeExtrasCode = true;
    public static boolean hideItemEnchantWithHideFlag = true;
    public static String game = "zAuctionHouse VDEV-3";
    public static ActivityType gameActivityType = ActivityType.DEFAULT;

    public static HashMap<ActionType, Action> actions = new HashMap<>();

    public static Map<String, String> enchantments = new HashMap<>();
    public static String enchantSeparator = " ";

    static {
        gatewayIntents.add(GatewayIntent.GUILD_MEMBERS);
        gatewayIntents.add(GatewayIntent.GUILD_EMOJIS);
        gatewayIntents.add(GatewayIntent.DIRECT_MESSAGES);
        gatewayIntents.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        gatewayIntents.add(GatewayIntent.GUILD_MESSAGES);
        gatewayIntents.add(GatewayIntent.GUILD_MESSAGE_TYPING);
        gatewayIntents.add(GatewayIntent.GUILD_PRESENCES);
        gatewayIntents.add(GatewayIntent.GUILD_VOICE_STATES);

        for (Enchantment enchantment : Enchantment.values())
            enchantments.put(enchantment.getName(), enchantment.getName());

        final EmbedField enchantEmbed = new EmbedField("Enchantment", "Item enchantment: **%enchant%**", false, false);
        actions.put(ActionType.SALE, new Action("none", "zAuctionHouse", new Color(255, 0, 0),
                Arrays.asList(new EmbedField("Sale", "Player **%seller%** just added x%amount% %material% for **%price%%currency%**", false), enchantEmbed))
        );
        actions.put(ActionType.BOUGHT, new Action("none", "zAuctionHouse", new Color(0, 255, 0),
                Arrays.asList(new EmbedField("Item bought !", "Player **%buyer%** bought x%amount% %material% for **%price%%currency%** of **%seller%**", false), enchantEmbed))
        );
        actions.put(ActionType.RETRIEVED, new Action("none", "zAuctionHouse", new Color(0, 255, 0),
                Arrays.asList(new EmbedField("Item retrieved !", "Player **%seller%** retrieved x%amount% %material% for **%price%%currency%**", false), enchantEmbed))
        );
        actions.put(ActionType.ADMIN_REMOVED, new Action("none", "zAuctionHouse", new Color(0, 255, 0),
                Arrays.asList(new EmbedField("Item removed by admin !", "Admin removed x%amount% %material% of **%seller%**", false), enchantEmbed))
        );
        actions.put(ActionType.EXPIRED, new Action("none", "zAuctionHouse", new Color(0, 255, 0),
                Arrays.asList(new EmbedField("Item expired !", "Item x%amount% %material% of **%seller%** has expired", false), enchantEmbed))
        );
    }

    /**
     * static Singleton instance.
     */
    private static volatile Config instance;

    /**
     * Private constructor for singleton.
     */
    private Config() {
    }

    /**
     * Return a singleton instance of Config.
     */
    public static Config getInstance() {
        // Double lock for thread safety.
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    @Override
    public void save(Persist persist) {
        persist.save(getInstance(), "config");
    }

    @Override
    public void load(Persist persist) {
        persist.loadOrSaveDefault(getInstance(), Config.class, "config");
    }

}

package org.royaldev.royalchat;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.palmergames.bukkit.towny.Towny;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishPlugin;
import org.royaldev.royalchat.listeners.PlayerListener;
import org.royaldev.royalchat.rcommands.CmdAdminChat;
import org.royaldev.royalchat.rcommands.CmdChannel;
import org.royaldev.royalchat.rcommands.CmdMe;
import org.royaldev.royalchat.rcommands.CmdRchat;
import org.royaldev.royalchat.rcommands.CmdRclear;
import org.royaldev.royalchat.rcommands.CmdSay;

import java.io.File;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class RoyalChat extends JavaPlugin {

    //--- Globals ---//

    public Logger log;
    private static Logger sLog;
    public Logger unNamedLog = Logger.getLogger("Minecraft");

    private static FileConfiguration config;

    public static Permission permission = null;
    public static Chat chat = null;

    private VanishPlugin vp = null;
    public static MultiverseCore mvc = null;
    public static Towny towny = null;

    //--- Public, static methods ---//

    public static Logger getNamedLogger() {
        return sLog;
    }

    public static FileConfiguration getPluginConfig() {
        return config;
    }

    public static void sendToConsole(String s) {
        Bukkit.getServer().getConsoleSender().sendMessage(s);
    }

    public static boolean hasAuthorization(final OfflinePlayer p, final String node) {
        String world = Bukkit.getServer().getWorlds().get(0).getName();
        return p instanceof RemoteConsoleCommandSender || p instanceof ConsoleCommandSender || RoyalChat.permission.has(world, p.getName(), node);
    }

    public static boolean hasAuthorization(final Player player, final String node) {
        return player instanceof RemoteConsoleCommandSender || player instanceof ConsoleCommandSender || RoyalChat.permission.has(player.getWorld(), player.getName(), node);
    }

    public static boolean hasAuthorization(final CommandSender player, final String node) {
        return player instanceof RemoteConsoleCommandSender || player instanceof ConsoleCommandSender || RoyalChat.permission.has(player, node);
    }

    //--- Public, non-static methods ---//

    public Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) permission = permissionProvider.getProvider();
        return (permission != null);
    }

    public Boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) chat = chatProvider.getProvider();
        return (chat != null);
    }

    public boolean isVanished(Player p) {
        if (vp == null) {
            vp = (VanishPlugin) Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket");
            return false;
        } else return vp.getManager().isVanished(p.getName());
    }

    public boolean isAuthorized(final OfflinePlayer p, final String node) {
        String world = getServer().getWorlds().get(0).getName();
        return p instanceof RemoteConsoleCommandSender || p instanceof ConsoleCommandSender || (RoyalChat.permission.has(world, p.getName(), "rcmds.admin") || RoyalChat.permission.has(world, p.getName(), node));
    }

    public boolean isAuthorized(final Player player, final String node) {
        return player instanceof RemoteConsoleCommandSender || player instanceof ConsoleCommandSender || (RoyalChat.permission.playerHas(player.getWorld(), player.getName(), "rcmds.admin") || RoyalChat.permission.playerHas(player.getWorld(), player.getName(), node));
    }

    public boolean isAuthorized(final CommandSender player, final String node) {
        return player instanceof RemoteConsoleCommandSender || player instanceof ConsoleCommandSender || (RoyalChat.permission.has((Player) player, "rcmds.admin") || RoyalChat.permission.has(player, node));
    }

    //--- Configuration values ---//

    //- Strings -//

    public static String chatFormat;
    public static String sayFormat;
    public static String meFormat;
    public static String adminFormat;

    public static String joinMessage;
    public static String quitMessage;
    public static String kickMessage;
    public static String worldMessage;

    //- Booleans -//

    public static Boolean firstWordCapital;
    public static Boolean highlightMentions;
    public static Boolean smokeOnMention;
    public static Boolean removeAllCaps;
    public static Boolean interWorld;
    public static Boolean useChannels;

    //- Doubles -//

    public static Double capsRemovalPercent;
    public static Double chatRadius;

    //--- reloadConfigValues() ---//

    /**
     * Reloads all configuration values from the config
     */
    public void reloadConfigValues() {
        if (!new File(getDataFolder() + File.separator + "config.yml").exists())
            saveDefaultConfig();
        config = getConfig();

        //- Strings -//

        chatFormat = config.getString("chat-format", "{prefix}{group}{suffix} {dispname}&r: {message}");
        sayFormat = config.getString("say-format", "&d[Server] {message}");
        meFormat = config.getString("me-format", "&d * &b{dispname}&d {message}");
        adminFormat = config.getString("admin-format", "&b[Admin] {dispname}:&3 {message}");

        joinMessage = config.getString("join-message", "&e{name} has joined.");
        quitMessage = config.getString("quit-message", "&e{name} has quit.");
        kickMessage = config.getString("kick-message", "&e{name} was kicked!");
        worldMessage = config.getString("world-message", "&e{name} joined {world} from {fromworld}.");

        //- Booleans -//

        firstWordCapital = config.getBoolean("first-word-capital", false);
        highlightMentions = config.getBoolean("highlight-at-user", true);
        smokeOnMention = config.getBoolean("smoke-at-user", false);
        removeAllCaps = config.getBoolean("remove-all-caps", true);
        interWorld = config.getBoolean("interworld", true);
        useChannels = config.getBoolean("use-channels", false);

        //- Doubles -//

        capsRemovalPercent = config.getDouble("caps-removal-percent", 75D);
        chatRadius = config.getDouble("chat-radius", 0D);
    }

    //--- onEnable() ---//

    public void onEnable() {

        //-- Dependency loading --//

        setupPermissions();
        setupChat();
        vp = (VanishPlugin) getServer().getPluginManager().getPlugin("VanishNoPacket");
        mvc = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        towny = (Towny) getServer().getPluginManager().getPlugin("Towny");

        //-- Configuration management --//

        reloadConfigValues();

        //-- Set up channeling --//

        Channeler.addAllChannels();

        //-- Logging --//

        log = getLogger();
        sLog = log;

        //-- Listeners --//

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(this), this);

        //-- Commands --//

        getCommand("rchat").setExecutor(new CmdRchat(this));
        getCommand("say").setExecutor(new CmdSay(this));
        getCommand("me").setExecutor(new CmdMe(this));
        getCommand("adminchat").setExecutor(new CmdAdminChat(this));
        getCommand("rclear").setExecutor(new CmdRclear(this));
        getCommand("channel").setExecutor(new CmdChannel(this));

    }

    //--- onDisable ---//

    public void onDisable() {

    }

}

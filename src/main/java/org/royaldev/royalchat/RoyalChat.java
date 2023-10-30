package org.royaldev.royalchat;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishPlugin;
import org.royaldev.royalchat.listeners.ChatListener;
import org.royaldev.royalchat.listeners.MessageListener;
import org.royaldev.royalchat.rcommands.CmdAdminChat;
import org.royaldev.royalchat.rcommands.CmdChannel;
import org.royaldev.royalchat.rcommands.CmdEmote;
import org.royaldev.royalchat.rcommands.CmdRClear;
import org.royaldev.royalchat.rcommands.CmdRoyalChat;
import org.royaldev.royalchat.rcommands.CmdSay;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.royaldev.royalchat.Language.NO_PERMISSIONS_SYSTEM;

public class RoyalChat extends JavaPlugin {

    public Logger log;
    /**
     * This is the main API interface.
     */
    public DataManager dm;

    private boolean usingPerms = false;

    public boolean withVault = false;
    public boolean withFactions = false;
    public boolean withRoyalCommands = false;
    public boolean withMultiverse = false;
    public boolean withPlaceholderAPI = false;
    public boolean withVNP = false;
    public boolean withTowny = false;

    public boolean useChannels;
    public boolean firstCapital;
    public boolean highlightPlayers;
    public boolean smokePlayers;
    public boolean highlightURLs;
    public boolean useCharWhitelist;

    public String charWhitelistRegex;

    private VanishPlugin vp;

    private final Pattern versionPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+)(\\-SNAPSHOT)?(\\-local\\-(\\d{8}\\.\\d{6})|\\-(\\d+))?");

    public boolean isVanished(Player p) {
        if (vp == null) {
            vp = (VanishPlugin) Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket");
            return false;
        }
        return vp.getManager().isVanished(p);
    }

    @SuppressWarnings("unused")
    public boolean isAuthorized(final OfflinePlayer p, final String node) {
        if (!usingPerms) return false;
        String world = getServer().getWorlds().get(0).getName();
        return !(p instanceof Player) && p == null || RoyalChat.permission.has(world, p.getName(), node);
    }

    public boolean isAuthorized(final Player player, final String node) {
        if (!usingPerms) return player.hasPermission(node);
        return player == null || RoyalChat.permission.playerHas(player.getWorld(), player.getName(), node);
    }

    public boolean isAuthorized(final CommandSender player, final String node) {
        if (!usingPerms) return player.hasPermission(node);
        return !(player instanceof Player) && !(player instanceof OfflinePlayer) || RoyalChat.permission.has(player, node);
    }

    public static Permission permission = null;
    public static Chat chat = null;

    private boolean setupPermissions() {
        if (!withVault) return false;
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) permission = permissionProvider.getProvider();
        return (permission != null);
    }

    private boolean setupChat() {
        if (!withVault) return false;
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) chat = chatProvider.getProvider();
        return (chat != null);
    }

    /**
     * Registers a command in the server. If the command isn't defined in plugin.yml
     * the NPE is caught, and a warning line is sent to the console.
     *
     * @param ce      CommandExecutor to be registered
     * @param command Command name as specified in plugin.yml
     * @param jp      Plugin to register under
     */
    private void registerCommand(CommandExecutor ce, String command, JavaPlugin jp) {
        try {
            jp.getCommand(command).setExecutor(ce);
        } catch (NullPointerException e) {
            log.warning("Could not register command \"" + command + "\" - not registered in plugin.yml (" + e.getMessage() + ")");
        }
    }

    public void reloadConfigVals() {
        FileConfiguration c = getConfig();
        useChannels = c.getBoolean("channels.use-channels", false);
        firstCapital = c.getBoolean("chat.capitalize-first-letter", true);
        highlightPlayers = c.getBoolean("chat.highlights.players.enabled", true);
        smokePlayers = c.getBoolean("chat.highlights.players.poof-smoke", true);
        highlightURLs = c.getBoolean("chat.highlights.urls", true);
        useCharWhitelist = c.getBoolean("chat.character-whitelist.enabled", false);

        charWhitelistRegex = c.getString("chat.character-whitelist.character-regex", "[!-~ ]");
    }

    private void saveLangFile(String name) {
        if (!new File(getDataFolder() + File.separator + "lang" + File.separator + name + ".properties").exists())
            saveResource("lang" + File.separator + name + ".properties", false);
    }

    public void onEnable() {
        log = getLogger();

        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
        reloadConfig();
        reloadConfigVals();

        saveLangFile("en_us");
        saveLangFile("es");

        try {
            new Language.LanguageHelper(getDataFolder() + File.separator + getConfig().getString("general.language-file", "lang/en_us.properties"));
        } catch (IOException e) {
            log.severe("Could not load language file: " + e.getMessage());
            log.severe("Disabling plugin.");
            setEnabled(false);
            return;
        }

        try {
            Matcher matcher = versionPattern.matcher(getDescription().getVersion());
            matcher.matches();
            // 1 = base version
            // 2 = -SNAPSHOT
            // 5 = build #
            String versionMinusBuild = (matcher.group(1) == null) ? "Unknown" : matcher.group(1);
            String build = (matcher.group(5) == null) ? "local build" : matcher.group(5);
            if (matcher.group(2) == null) build = "release";
            Metrics m = new Metrics(this);
            Metrics.Graph g = m.createGraph("Version");
            g.addPlotter(
                    new Metrics.Plotter(versionMinusBuild + "~=~" + build) {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    }
            );
            m.addGraph(g);
            if (m.start()) getLogger().info("Metrics enabled. Thank you!");
            else getLogger().info("Metrics disabled. If you want to help keep accurate statistics, turn it on!");
        } catch (Exception e) {
            getLogger().warning("Couldn't start Metrics: " + e.getMessage());
        }

        withVault = getServer().getPluginManager().getPlugin("Vault") != null;
        withRoyalCommands = getServer().getPluginManager().getPlugin("RoyalCommands") != null;
        withTowny = getServer().getPluginManager().getPlugin("Towny") != null;
        withMultiverse = getServer().getPluginManager().getPlugin("Multiverse-Core") != null;
        withPlaceholderAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        withFactions = getServer().getPluginManager().getPlugin("Factions") != null;
        withVNP = getServer().getPluginManager().getPlugin("VanishNoPacket") != null;
        vp = (VanishPlugin) getServer().getPluginManager().getPlugin("VanishNoPacket");

        usingPerms = setupPermissions();
        setupChat();

        if (!usingPerms) log.info(NO_PERMISSIONS_SYSTEM.toString());

        dm = new DataManager(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new MessageListener(this), this);

        registerCommand(new CmdRoyalChat(this), "royalchat", this);
        registerCommand(new CmdChannel(this), "channel", this);
        registerCommand(new CmdRClear(this), "rclear", this);
        registerCommand(new CmdEmote(this), "emote", this);
        registerCommand(new CmdSay(this), "say", this);
        registerCommand(new CmdAdminChat(this), "adminchat", this);

        dm.updateChannels();
    }

    public void onDisable() {

    }

}

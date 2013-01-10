package org.royaldev.royalchat;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.dependencies.FactionsUtils;
import org.royaldev.royalchat.dependencies.TownyUtils;
import org.royaldev.royalcommands.RoyalCommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.royaldev.royalchat.Language.NO_CHANNEL;
import static org.royaldev.royalchat.Language.NO_WORLD;

/**
 * The main API reference for RoyalChat. It is not to be constructed.
 */
@SuppressWarnings("unused")
public class DataManager {

    /**
     * List of all registered channels. Use methods that query this; do not query it directly.
     */
    private final List<Channel> channels = new ArrayList<Channel>();
    /**
     * URL matcher from Minecraft
     */
    private final Pattern pattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,3})(/\\S*)?$");

    /**
     * RoyalChat instance
     */
    private final RoyalChat plugin;

    /**
     * <strong>Do not construct this class.</strong>
     * <p/>
     * Grab it from RoyalChat.dm instead. You must get RoyalChat first, it is not static.
     *
     * @param instance RoyalChat
     */
    protected DataManager(RoyalChat instance) {
        plugin = instance;
    }

    /**
     * <strong>Do not use this constructor.</strong>
     * <p/>
     * It was created to prevent people from constructing this class.
     */
    protected DataManager() {
        plugin = null;
    }

    /**
     * Gets the group of a player using Vault.
     *
     * @param cs CommandSender to get group of
     * @return group name if using Vault or empty string if without group or not using Vault (never null)
     */
    public String getGroup(final CommandSender cs) {
        if (!(cs instanceof Player)) return "";
        if (!plugin.withVault) return "";
        final Player p = (Player) cs;
        try {
            String group = RoyalChat.permission.getPrimaryGroup(p);
            if (group == null) return "";
            return group;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets the suffix of a player using RoyalChat or Vault.
     * <p/>
     * The order of suffixes is rChat-player, rChat-group, rChat-*, Vault-player, Vault-group, empty.
     *
     * @param cs CommandSender to get suffix of
     * @return suffix or empty string (never null)
     */
    public String getSuffix(final CommandSender cs) {
        if (!(cs instanceof Player)) return "";
        final Player p = (Player) cs;
        String pSuffix = plugin.getConfig().getString("players.suffixes.players." + p.getName());
        if (pSuffix != null) return pSuffix;
        String group = getGroup(p);
        String gSuffix = (group.isEmpty()) ? null : plugin.getConfig().getString("players.suffixes.groups." + group);
        if (gSuffix != null) return gSuffix;
        String all = plugin.getConfig().getString("players.suffixes.*");
        if (all != null) return all;
        if (!plugin.withVault) return "";
        try {
            String suffix = RoyalChat.chat.getPlayerSuffix(p);
            if (suffix == null) return "";
            return suffix;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets the suffix of a group.
     *
     * @param groupName Name of group to get suffix of
     * @return Suffix or empty string if none
     */
    public String getGroupSuffix(final String groupName) {
        String gSuffix = plugin.getConfig().getString("players.suffixes.groups." + groupName);
        if (gSuffix != null) return gSuffix;
        String all = plugin.getConfig().getString("players.suffixes.*");
        if (all != null) return all;
        if (!plugin.withVault) return "";
        try {
            String suffix = RoyalChat.chat.getGroupSuffix((String) null, groupName);
            if (suffix == null) return "";
            return suffix;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets the prefix of a group.
     *
     * @param groupName Name of group to get prefix of
     * @return Prefix or empty string if none
     */
    public String getGroupPrefix(final String groupName) {
        String gPrefix = plugin.getConfig().getString("players.prefixes.groups." + groupName);
        if (gPrefix != null) return gPrefix;
        String all = plugin.getConfig().getString("players.prefixes.*");
        if (all != null) return all;
        if (!plugin.withVault) return "";
        try {
            String prefix = RoyalChat.chat.getGroupPrefix((String) null, groupName);
            if (prefix == null) return "";
            return prefix;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets the prefix of a player using RoyalChat or Vault.
     * <p/>
     * The order of prefixes is rChat-player, rChat-group, rChat-*, Vault-player, Vault-group, empty.
     *
     * @param cs CommandSender to get prefix of
     * @return prefix or empty string (never null)
     */
    public String getPrefix(final CommandSender cs) {
        if (!(cs instanceof Player)) return "";
        final Player p = (Player) cs;
        String pPrefix = plugin.getConfig().getString("players.prefixes.players." + p.getName());
        if (pPrefix != null) return pPrefix;
        String group = getGroup(p);
        String gPrefix = (group.isEmpty()) ? null : plugin.getConfig().getString("players.prefixes.groups." + group);
        if (gPrefix != null) return gPrefix;
        String all = plugin.getConfig().getString("players.prefixes.*");
        if (all != null) return all;
        if (!plugin.withVault) return "";
        try {
            String prefix = RoyalChat.chat.getPlayerPrefix(p);
            if (prefix == null) return "";
            return prefix;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Replaces Towny variables (safe without Towny)
     *
     * @param format Format to replace
     * @param cs     CommandSender sending message
     * @return Formatted message
     */
    private String replaceTowny(final String format, final CommandSender cs) {
        String message = format;
        if (plugin.withTowny) message = TownyUtils.replaceTowny(message, cs);
        return message;
    }

    /**
     * Replaces Factions variables (safe without Factions)
     *
     * @param format Format to replace
     * @param cs     CommandSender sending message
     * @return Formatted message
     */
    private String replaceFactions(final String format, final CommandSender cs) {
        String message = format;
        if (plugin.withFactions) message = FactionsUtils.replaceFactions(message, cs);
        return message;
    }

    /**
     * Sanitizes a message for use in vanilla chat.
     *
     * @param format Format of message to sanitize
     * @return Sanitized format
     */
    private String sanitizeChat(final String format) {
        return format.replace("%", "%%");
    }

    /**
     * Formats a chat message with variables.
     *
     * @param cs            CommandSender sending the message
     * @param format        Format of chat message (variables included)
     * @param isEmote       Emote toggle
     * @param isChannelChat Channel chat toggle
     * @return Formatted message
     */
    public String formatChat(final CommandSender cs, final String format, final String text, boolean isEmote, boolean isAdminChat, boolean isChannelChat, boolean sanitize) {
        boolean isPlayer = cs instanceof Player;
        final Player p = (isPlayer) ? (Player) cs : null;
        String message = format;
        String inGameMessage = text;
        if (!plugin.isAuthorized(cs, "rchat.caps") && plugin.getConfig().getBoolean("chat.remove-all-caps.enabled") && isCaps(inGameMessage, plugin.getConfig().getInt("chat.remove-all-caps.percent-for-remove")))
            inGameMessage = inGameMessage.toLowerCase();
        if (!isEmote && !isEmoticon(inGameMessage)) inGameMessage = capitalizeFirstLetter(inGameMessage);
        if (plugin.isAuthorized(cs, "rchat.colors")) inGameMessage = colorize(inGameMessage);
        else inGameMessage = decolorize(inGameMessage);
        message = message.replace("{group}", getGroup(cs));
        message = message.replace("{suffix}", getSuffix(cs));
        message = message.replace("{prefix}", getPrefix(cs));
        message = message.replace("{rawworld}", ((isPlayer) ? p.getWorld().getName() : NO_WORLD.toString()));
        message = message.replace("{world}", ((isPlayer) ? getWorldName(p.getWorld()) : NO_WORLD.toString()));
        message = replaceTowny(message, cs);
        message = replaceFactions(message, cs);
        message = colorize(message); // do this here to colorize everything but user input
        message = message.replace("{message}", inGameMessage);
        message = highlightURLs(message);
        if (!isAdminChat) {
            message = highlightPlayers(message); // do this here to prevent highlighting names in format, but to get format colors
        }
        message = message.replace("{name}", colorize(cs.getName()));
        message = message.replace("{dispname}", colorize((isPlayer) ? p.getDisplayName() : cs.getName()));
        if (isChannelChat) {
            Channel c = plugin.dm.getChannelOf(cs);
            boolean isNull = c == null;
            message = message.replace("{channel}", (isNull) ? NO_CHANNEL.toString() : c.getName());
            message = message.replace("{#inchannel}", (isNull) ? "0" : String.valueOf(c.getMembers().size()));
        }
        if (sanitize) message = sanitizeChat(message);
        return message + ChatColor.RESET; // fixes nasty bug where formatting codes magic teleport to the beginning
    }

    /**
     * Converts color codes to processed codes
     *
     * @param message Message with raw color codes
     * @return String with processed colors
     */
    public String colorize(final String message) {
        if (message == null) return null;
        return message.replaceAll("&([a-f0-9k-or])", "\u00a7$1");
    }

    /**
     * Removes color codes that have not been processed yet (&char)
     * <p/>
     * This fixes a common exploit where color codes can be embedded into other codes:
     * &&aa (replaces &a, and the other letters combine to make &a again)
     *
     * @param message String with raw color codes
     * @return String without raw color codes
     */
    public String decolorize(String message) {
        Pattern p = Pattern.compile("(?i)&[a-f0-9k-or]");
        boolean contains = p.matcher(message).find();
        while (contains) {
            message = message.replaceAll("(?i)&[a-f0-9k-or]", "");
            contains = p.matcher(message).find();
        }
        return message;
    }

    /**
     * Checks to see if a CommandSender is in a channel.
     *
     * @param cs CommandSender to check
     * @return boolean value
     */
    public boolean isInChannel(final CommandSender cs) {
        if (!(cs instanceof Player)) return false;
        synchronized (channels) {
            for (Channel c : channels) if (c.getMembers().contains(cs.getName())) return true;
        }
        return false;
    }

    /**
     * Gets the name of a world through RoyalCommands & Multiverse. If neither has a name, the default is used.
     *
     * @param w World to get name of
     * @return String (never null)
     */
    public String getWorldName(World w) {
        String name = w.getName();
        if (plugin.withRoyalCommands)
            name = RoyalCommands.wm.getConfig().getString("worlds." + w.getName() + ".displayname", w.getName());
        if (plugin.withMultiverse) {
            MultiverseCore mvc = (MultiverseCore) plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
            MultiverseWorld mvw = mvc.getMVWorldManager().getMVWorld(w);
            if (mvw != null) name = mvw.getColoredWorldString();
        }
        return colorize(name);
    }

    /**
     * Finds if a channel exists.
     *
     * @param name Channel to search for
     * @return true or false
     */
    public boolean channelExists(String name) {
        synchronized (channels) {
            for (Channel c : channels) if (c.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    /**
     * Gets all registered channels.
     *
     * @return Set of channels
     */
    public Set<Channel> getChannels() {
        return new HashSet<Channel>(channels);
    }

    /**
     * Gets a channel by name.
     *
     * @param name Name of channel to get
     * @return Channel or null if not existent
     */
    public Channel getChannel(String name) {
        synchronized (channels) {
            for (Channel c : channels) if (c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    /**
     * Gets a channel by CommandSender
     *
     * @param cs CommandSender in channel to get
     * @return Channel or null if no channel contains CommandSender
     */
    public Channel getChannelOf(CommandSender cs) {
        if (!(cs instanceof Player)) return null;
        synchronized (channels) {
            for (Channel c : channels) if (c.getMembers().contains(cs.getName())) return c;
        }
        return null;
    }

    /**
     * Gets a channel by player name.
     *
     * @param name Name of player in channel
     * @return Channel or null if no channel contains name
     */
    public Channel getChannelOf(String name) {
        synchronized (channels) {
            for (Channel c : channels) if (c.getMembers().contains(name)) return c;
        }
        return null;
    }

    /**
     * Adds a channel to the list of channels.
     *
     * @param c Channel to add
     */
    public void addChannel(Channel c) {
        synchronized (channels) {
            if (channels.contains(c)) return;
            channels.add(c);
        }
    }

    /**
     * Removes a channel from the list of channels and removes all its members.
     *
     * @param c Channel to purge
     */
    public void removeChannel(Channel c) {
        synchronized (channels) {
            if (!channels.contains(c)) return;
            channels.remove(c);
            c.getMembers().clear();
        }
    }

    /**
     * Removes a channel from the list of channels by name and removes all its members.
     *
     * @param name Name of channel to purge
     */
    public void removeChannel(String name) {
        synchronized (channels) {
            Channel c = null;
            for (Channel ch : channels) if (ch.getName().equalsIgnoreCase(name)) c = ch;
            if (c == null) return;
            channels.remove(c);
            c.getMembers().clear();
        }
    }

    /**
     * Loops through all channels to find the one marked as default.
     * <p/>
     * If more than one is marked as default, the first one queried will be returned.
     * <p/>
     * If no channels are marked as default:
     * <ul>
     * <li>If there are set channels, the last one queried will be returned</li>
     * <li>If there are <strong>no</strong> set channels, null will be returned</li>
     * </ul>
     *
     * @return Channel or null
     */
    public Channel getDefaultChannel() {
        Channel lastChannel = null;
        for (Channel c : channels) {
            if (c.getDefaultChannel()) return c;
            lastChannel = c;
        }
        if (channels.isEmpty()) return null;
        return lastChannel;
    }

    /**
     * Checks a string against Minecraft's URL regex
     *
     * @param word String to check
     * @return boolean
     */
    public boolean isURL(final String word) {
        return pattern.matcher(word).matches();
    }

    /**
     * Capitalizes the first letter of a message. Do not use the message format; use the message itself.
     *
     * @param message Message to capitalize
     * @return Capitalized message
     */
    public String capitalizeFirstLetter(final String message) {
        if (!plugin.firstCapital) return message;
        if (isURL(message.split(" ")[0])) return message;
        if (message.isEmpty()) return message;
        return message.substring(0, 1).toUpperCase() + message.substring(1);
    }

    /**
     * Returns the index within this string of the first occurrence of the specified regular expressions string.
     *
     * @param str   String to search in
     * @param regex Regex to search for
     * @return First occurrence of the regex or -1 if not in string
     */
    private int indexOfRegex(final String str, final String regex) {
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(str);
        if (!m.find()) return -1;
        return m.start();
    }

    /**
     * Returns the index within this string of the last occurrence of the specified regular expressions string.
     *
     * @param str   String to search in
     * @param regex Regex to search for
     * @return Last occurrence of the regex or -1 if not in string
     */
    private int lastIndexOfRegex(final String str, final String regex) {
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(str);
        int index = -1;
        while (m.find()) index = m.start();
        return index;
    }

    /**
     * Highlights every occurrence of a word.
     *
     * @param message   Message to highlight in
     * @param highlight Word to highlight
     * @param regex     Regular expression to use instead of highlight - can be null if no regex being used
     * @param color     Color to highlight word
     * @return Message with highlighted words
     */
    private String highlightWord(final String message, final String highlight, final String regex, final ChatColor color) {
        String formation = message; // format to return
        String temp = formation;
        int times = StringUtils.countMatches(formation.toLowerCase(), highlight.toLowerCase());
        String use = (regex == null) ? highlight.toLowerCase() : regex;
        for (int i = 0; i < times; i++) {
            int lastIndexOf = lastIndexOfRegex(temp.toLowerCase(), use);
            if (lastIndexOf < 0) break;
            String substr = temp.substring(0, lastIndexOf);
            String lastColor = getLastColor(substr);
            String after = formation.substring(lastIndexOf + highlight.length());
            substr = color + highlight + lastColor;
            temp = temp.substring(0, lastIndexOf);
            formation = formation.substring(0, lastIndexOf) + substr + after;
        }
        return formation;
    }

    /**
     * Highlights all URLs in a piece of text. The full chat format should be used here to ensure
     * that colors are properly transferred before and after the colored link.
     *
     * @param format Format of chat message
     * @return Format with highlighted URLs
     */
    public String highlightURLs(final String format) {
        if (!plugin.highlightURLs) return format;
        String formation = format;
        List<String> alreadyHighlighted = new ArrayList<String>();
        for (String word : format.split(" ")) {
            if (!isURL(word)) continue;
            if (alreadyHighlighted.contains(word.toLowerCase())) continue;
            alreadyHighlighted.add(word.toLowerCase());
            formation = highlightWord(formation, word, null, ChatColor.DARK_AQUA);
        }
        return formation;
    }

    /**
     * Highlights the names of any online players.
     * TODO: Fix boundaries
     *
     * @param format Full message format
     * @return Format with highlighted names
     */
    public String highlightPlayers(final String format) {
        if (!plugin.highlightPlayers) return format;
        String original = format;
        String formation = format;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.isVanished(p)) continue;
            String name = "(?i)\\b" + p.getName() + "\\b";
            String dispName = "(?i)\\b" + p.getDisplayName() + "\\b";
            formation = highlightWord(formation, p.getName(), name, ChatColor.AQUA);
            if (!p.getName().equals(p.getDisplayName()))
                formation = highlightWord(formation, p.getDisplayName(), dispName, ChatColor.AQUA);
            if (!original.equals(formation) && plugin.smokePlayers) { // someone was highlighted
                for (int i = 0; i < 8; i++) {
                    if (i == 4) continue;
                    Location playAt = p.getEyeLocation();
                    p.playEffect(playAt, Effect.SMOKE, i);
                    p.playEffect(playAt, Effect.SMOKE, i);
                }
            }
            original = formation;
        }
        return formation;
    }

    /**
     * Gets the last color used in a string. Useful for inserting colored text into a string.
     * <p/>
     * If there is none, returns \u00a7r (ChatColor.RESET)
     *
     * @param s String to check
     * @return Color code of last color (e.g. \u00a7a) ready to be used
     */
    public static String getLastColor(String s) {
        String lastColor = "\u00a7r";
        String[] parts = s.split("");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.equals("\u00a7")) continue;
            if (i + 1 > parts.length) continue;
            String code = parts[i + 1];
            if (!code.matches("(?i)[a-f0-9k-or]")) continue;
            lastColor = "\u00a7" + code;
        }
        return lastColor;
    }

    /**
     * Gets the last color used in a string. Useful for inserting colored text into a string.
     * <p/>
     * If there is none, returns \u00a7r (ChatColor.RESET)
     *
     * @param s    String to check
     * @param stop At what position in the string to stop checking for the last color
     * @return Color code of last color (e.g. \u00a7a) ready to be used
     */
    public static String getLastColor(String s, int stop) {
        String lastColor = "\u00a7r";
        String[] parts = s.split("");
        for (int i = 0; i < stop; i++) {
            String part = parts[i];
            if (!part.equals("\u00a7")) continue;
            if (i + 1 > parts.length) continue;
            String code = parts[i + 1];
            if (!code.matches("(?i)[a-f0-9k-or]")) continue;
            lastColor = "\u00a7" + code;
        }
        return lastColor;
    }

    /**
     * Updates old channels, adds new channels, and removes removed channels via the config.
     */
    protected void updateChannels() {
        final List<String> loopedThrough = new ArrayList<String>();
        FileConfiguration c = plugin.getConfig();
        ConfigurationSection cs = c.getConfigurationSection("channels");
        if (cs == null) return;
        for (String key : cs.getKeys(false)) {
            ConfigurationSection kcs = cs.getConfigurationSection(key);
            if (kcs == null) continue;
            String password = kcs.getString("password", "");
            if (password.isEmpty()) password = null;
            boolean defaultChannel = kcs.getBoolean("default", false);
            boolean omnipresent = kcs.getBoolean("omnipresent", true);
            boolean multiworld = kcs.getBoolean("multiworld", true);
            String chatFormat = kcs.getString("chat-format", "[d]{dispname}: {message}");
            double chatRadius = kcs.getDouble("radius", 0D);
            if (channelExists(key)) {
                Channel ch = getChannel(key);
                ch.setPassword(password);
                ch.setChatFormat(chatFormat);
                ch.setChatRadius(chatRadius);
                ch.setMultiworld(multiworld);
                ch.setOmnipresent(omnipresent);
            } else
                addChannel(new Channel(defaultChannel, omnipresent, multiworld, key, password, chatFormat, chatRadius));
            loopedThrough.add(key);
        }
        for (Channel ch : channels) if (!loopedThrough.contains(ch.getName())) removeChannel(ch);
    }

    /**
     * List of Strings regarded as emoticons.
     */
    private final List<String> emoticons = Arrays.asList(":D", ":P", ";P", ";O", ":O", "xP", "xD", "xO");

    /**
     * Checks to see if a string is an emoticon. This is often used when determining if
     * something should be set to lowercase.
     *
     * @param s String to check (whitespace will be stripped if it is not already)
     * @return true if emoticon, false if otherwise
     */
    private boolean isEmoticon(String s) {
        // Remove excess whitespace
        s = s.trim();
        // Check for normal emoticons (xD, etc.)
        boolean isEmoticon = emoticons.contains(s);
        // Check for reverse emoticons (Dx, etc.)
        if (!isEmoticon) isEmoticon = emoticons.contains(StringUtils.reverse(s));
        return isEmoticon;
    }

    /**
     * Checks to see if a message has a capital letter threshold larger than the given percent.
     * <p/>
     * <strong>This takes emoticons into account.</strong> Emoticons are marked as lowercase.
     * If the message is one letter, it is also not considered caps (e.g. "I").
     *
     * @param str     String to check capital letter threshold of
     * @param percent Percentage that the threshold must be above to be marked all caps (e.g. 75, 50)
     * @return true if caps threshold is greater than percent, false if otherwise
     */
    private boolean isCaps(String str, int percent) {
        if (isEmoticon(str)) return false;
        if (str.length() == 1) return false;
        double numCaps = 0;
        str = str.replaceAll("\\W", "");
        str = str.replaceAll(" ", "");
        for (String s : str.split("")) {
            if (!s.matches("[A-Z]")) continue;
            numCaps++;
        }
        return numCaps / str.length() > percent / 100;
    }

}

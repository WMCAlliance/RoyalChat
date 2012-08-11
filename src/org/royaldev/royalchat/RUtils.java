package org.royaldev.royalchat;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.depends.TownyUtils;

import java.util.logging.Logger;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class RUtils {

    private static Logger log = RoyalChat.getNamedLogger();

    /**
     * Displays a message to player and tells console that player was denied access.
     *
     * @param cs Player being denied access
     */
    public static void dispNoPerms(CommandSender cs) {
        cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
        log.warning(cs.getName() + " was denied access to that!");
    }

    /**
     * Displays a message to player and tells console that player was denied access.
     *
     * @param cs      Player being denied access
     * @param message Message to display to player
     */
    public static void dispNoPerms(CommandSender cs, String message) {
        cs.sendMessage(message);
        log.warning(cs.getName() + " was denied access to that!");
    }

    /**
     * Joins an array of strings with spaces
     *
     * @param array    Array to join
     * @param position Position to start joining from
     * @return Joined string
     */
    public static String join(String[] array, int position) {
        StrBuilder sb = new StrBuilder();
        for (int i = position; i < array.length; i++) {
            sb.append(array[i]);
            sb.append(" ");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Converts raw color codes to colors
     *
     * @param original String with raw color codes
     * @return String with processed color codes
     */
    public static String colorize(String original) {
        if (original == null) return original;
        return original.replaceAll("(?i)&([a-f0-9k-or])", "\u00a7$1");
    }

    /**
     * Removes color codes from colored strings
     *
     * @param colored String with processed color codes
     * @return String without processed color codes
     */
    public static String decolorize(String colored) {
        return colored.replaceAll("(?i)\u00a7[a-f0-9k-or]", "");
    }

    /**
     * Removes color codes that have not been processed yet (&char)
     *
     * @param notColored String with raw color codes
     * @return String without raw color codes
     */
    public static String removeColorCodes(String notColored) {
        Pattern p = Pattern.compile("(?i)&[a-f0-9k-or]");
        boolean contains = p.matcher(notColored).find();
        while (contains) {
            notColored = notColored.replaceAll("(?i)&[a-f0-9k-or]", "");
            contains = Pattern.compile("(?i)&[a-f0-9k-or]").matcher(notColored).find();
        }
        return notColored;
    }

    /**
     * Gets the prefix of the CommandSender. If cs isn't a player, or if cs has no prefix, an empty string is returned.
     *
     * @param cs CommandSender to get prefix of
     * @return Prefix or empty string
     */
    public static String getPrefix(CommandSender cs) {
        if (cs == null) return "";
        if (!(cs instanceof Player)) return "";
        Player p = (Player) cs;
        String prefix = "";
        String group = "";
        try {
            if (RoyalChat.chat != null) prefix = RoyalChat.chat.getPlayerPrefix(p);
            if (RoyalChat.permission != null)
                group = RoyalChat.permission.getPrimaryGroup(p);
        } catch (UnsupportedOperationException e) {
            prefix = "";
            group = "";
        }
        if (group == null) return "";
        if (prefix == null) prefix = "";
        String pb = RoyalChat.getPluginConfig().getString("pbukkit.prefixes." + group);
        if (pb == null) return colorize(prefix);
        prefix = prefix + pb;
        return colorize(prefix);
    }

    /**
     * Gets the suffix of the CommandSender. If cs isn't a player, or if cs has no suffix, an empty string is returned.
     *
     * @param cs CommandSender to get suffix of
     * @return Suffix or empty string
     */
    public static String getSuffix(CommandSender cs) {
        if (cs == null) return "";
        if (!(cs instanceof Player)) return "";
        Player p = (Player) cs;
        String suffix = "";
        String group = "";
        try {
            if (RoyalChat.chat != null) suffix = RoyalChat.chat.getPlayerSuffix(p);
            if (RoyalChat.permission != null)
                group = RoyalChat.permission.getPrimaryGroup(p);
        } catch (UnsupportedOperationException e) {
            group = "";
            suffix = "";
        }
        if (group == null) return "";
        if (suffix == null) suffix = "";
        String pb = RoyalChat.getPluginConfig().getString("pbukkit.suffixes." + group);
        if (pb == null) return colorize(suffix);
        suffix = suffix + pb;
        return colorize(suffix);
    }

    /**
     * Removes each Towny variable.
     *
     * @param s String to remove variables from
     * @return Processed string
     */
    public static String removeAllTownyVars(String s) {
        s = s.replaceAll("(?i)\\{townytown\\}", "");
        s = s.replaceAll("(?i)\\{townyprefix\\}", "");
        s = s.replaceAll("(?i)\\{townysuffix\\}", "");
        s = s.replaceAll("(?i)\\{townynation\\}", "");
        s = s.replaceAll("(?i)\\{townysurname\\}", "");
        s = s.replaceAll("(?i)\\{townytitle\\}", "");
        return s;
    }

    /**
     * Replaces any Towny variables with their values. If Towny is not installed, or if cs is not a player, they are removed.
     *
     * @param s  String with Towny variables
     * @param cs Message sender
     * @return Processed string
     */
    public static String replaceTownyVars(String s, CommandSender cs) {
        if (RoyalChat.towny == null) return removeAllTownyVars(s);
        if (!(cs instanceof Player)) return removeAllTownyVars(s);
        Player p = (Player) cs;
        s = TownyUtils.replaceTownyVars(s, p);
        return s;
    }

    /**
     * Removes capital letters from a string if they're over the set percent to remove.
     *
     * @param s String to remove capitals from
     * @return Processed string
     */
    public static String removeCaps(String s) {
        double a = 0D;
        String[] msg = s.replaceAll("\\W", "").split("");
        for (String st : msg) if (st.matches("[A-Z]")) a++;
        // I base this on the length of s and not msg for a simple reason: emoticons
        // :D = 100% caps if you base it off of msg
        double percCaps = a / ((double) s.length());
        double pC = RoyalChat.capsRemovalPercent / 100D;
        if (percCaps >= pC) s = s.toLowerCase();
        return s;
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
     * Sanitizes chat input ($, %, \) for minimal breakage.
     *
     * @param s       String to sanitize
     * @param vanilla Set to true if this is a vanilla chat message (will fix %)
     * @return Sanitized string
     */
    public static String sanitizeInput(String s, boolean vanilla) {
        s = s.replace("\\", "\\\\");
        if (vanilla) s = s.replace("%", "%%");
        s = s.replace("$", "\\$");
        return s;
    }
}

package org.royaldev.royalchat;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat channel for RoyalChat
 *
 * @author jkcclemens
 * @see Channeler
 * @since 0.1.0pre
 */
public class Channel {

    private final String name;
    private final String chatFormat;
    private final String password;

    private final double chatRadius;

    private final boolean isDefault;
    private final boolean usePassword;
    private final boolean alwaysSeen;
    private final boolean interWorld;
    private final boolean colorAllowed;

    private final List<String> members = new ArrayList<String>();

    /**
     * Constructor for a chat channel.
     *
     * @param values ConfigurationSection containing all needed values for the channel
     */
    public Channel(ConfigurationSection values) {
        name = values.getString("name", "Error Channel");
        chatFormat = values.getString("chat-format", "[err] {name}: {message}");
        password = values.getString("password", "");

        chatRadius = values.getDouble("chat-radius", 0);

        isDefault = values.getBoolean("default", false);
        usePassword = values.getBoolean("use-password", false);
        alwaysSeen = values.getBoolean("always-seen", false);
        interWorld = values.getBoolean("interworld", true);
        colorAllowed = values.getBoolean("color-allowed", true);
    }

    /**
     * Constructor for a chat channel.
     *
     * @param name        Name of channel
     * @param chatFormat  Chat format of channel
     * @param chatRadius  Chat radius of channel
     * @param isDefault   Default setting of channel
     * @param usePassword Password setting of channel
     * @param password    Password of channel
     * @param alwaysSeen  Always-seen setting of channel
     * @param interWorld  Interworld setting of channel
     */
    public Channel(String name, String chatFormat, double chatRadius, boolean isDefault, boolean usePassword, String password, boolean alwaysSeen, boolean interWorld, boolean colorAllowed) {
        this.name = name;
        this.chatFormat = chatFormat;
        this.password = password;

        this.chatRadius = chatRadius;

        this.isDefault = isDefault;
        this.usePassword = usePassword;
        this.alwaysSeen = alwaysSeen;
        this.interWorld = interWorld;
        this.colorAllowed = colorAllowed;
    }

    /**
     * Gets the name of the channel
     *
     * @return Name of channel
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the chat-format of the channel
     *
     * @return Chat-format of channel
     */
    public String getChatFormat() {
        return chatFormat;
    }

    /**
     * Gets the password of the channel
     *
     * @return Password of channel
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the chat-radius of the channel
     *
     * @return Chat-radius of channel
     */
    public double getChatRadius() {
        return chatRadius;
    }

    /**
     * Gets if the channel is the default channel
     *
     * @return If channel is default
     */
    public boolean getIsDefault() {
        return isDefault;
    }

    /**
     * Gets if the channel uses a password
     *
     * @return If channel uses a password
     */
    public boolean getUsePassword() {
        return usePassword;
    }

    /**
     * Gets if the channel is always seen
     *
     * @return If channel is always seen
     */
    public boolean getAlwaysSeen() {
        return alwaysSeen;
    }

    /**
     * Gets if the channel is interworld
     *
     * @return If channel is interworld
     */
    public boolean getInterWorld() {
        return interWorld;
    }

    /**
     * Gets if the channel allows colors
     *
     * @return If channel allows colors
     */
    public boolean getColorAllowed() {
        return colorAllowed;
    }

    /**
     * Gets the members of this channel in a list of their names.
     *
     * @return StringList of members in channel
     */
    public List<String> getMembers() {
        return members;
    }

    /**
     * Gets the members of this channel in a list of players.
     *
     * @return List of players of members in channel
     */
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<Player>();
        for (String s : members) {
            Player p = Bukkit.getServer().getPlayer(s);
            if (s == null) continue;
            players.add(p);
        }
        return players;
    }

    /**
     * Checks to see if a player is a member of this channel.
     * <p/>
     * If p is null, will return false.
     *
     * @param p Player to check for
     * @return If p is a member of this channel
     */
    public boolean isMember(Player p) {
        if (p == null) return false;
        synchronized (members) {
            return members.contains(p.getName());
        }
    }

    /**
     * Adds a member to this channel.
     * <p/>
     * If p is null, nothing will happen.
     *
     * @param p Player to add to this channel
     */
    public void addMember(Player p) {
        if (p == null) return;
        synchronized (members) {
            members.add(p.getName());
        }
    }

    /**
     * Removes a player from this channel.
     * <p/>
     * If p is null, or if the player is not a member of this channel, nothing will happen.
     *
     * @param p Player to remove from this channel
     */
    public void removeMember(Player p) {
        if (p == null) return;
        synchronized (members) {
            if (members.contains(p.getName())) members.remove(p.getName());
        }
    }

    /**
     * Sends a message to all members of this channel.
     * <p/>
     * If message is null, nothing happens.
     *
     * @param message Message to send
     */
    public void sendMessage(String message) {
        if (message == null) return;
        for (String s : members) {
            Player p = Bukkit.getServer().getPlayer(s);
            if (p == null) continue;
            p.sendMessage(message);
        }
    }

    /**
     * Sends a message to a specific group of recipients in the channel
     *
     * @param message    Message to send
     * @param recipients List of recipients - if a player is not in the channel, they will be ignored
     */
    public void sendMessage(String message, List<Player> recipients) {
        for (Player p : recipients) {
            if (p == null) continue;
            if (!isMember(p)) continue;
            p.sendMessage(message);
        }
    }

}

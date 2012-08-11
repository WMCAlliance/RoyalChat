package org.royaldev.royalchat;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains generic, static, Channel methods.
 *
 * @author jkcclemens
 * @see Channel
 * @since 0.1.0pre
 */
public class Channeler {

    private static ConfigurationSection channelConfSec = RoyalChat.getPluginConfig().getConfigurationSection("channels");
    private static final List<Channel> channels = new ArrayList<Channel>();

    /**
     * Initializes all channels
     */
    public static void addAllChannels() {
        for (String key : channelConfSec.getKeys(false)) {
            ConfigurationSection cs = channelConfSec.getConfigurationSection(key);
            Channel c = new Channel(cs);
            synchronized (channels) {
                channels.add(c);
            }
        }
    }

    /**
     * Reloads all channels
     */
    public static void reloadAllChannels() {
        Map<String, List<String>> members = new HashMap<String, List<String>>();
        synchronized (channels) {
            for (Channel c : channels) members.put(c.getName(), c.getMembers());
        }
        removeAllChannels();
        addAllChannels();
        synchronized (channels) {
            for (Channel c : channels) {
                if (members.get(c.getName()) == null) continue;
                for (String s : members.get(c.getName()))
                    c.addMember(Bukkit.getServer().getOfflinePlayer(s));
            }
        }
    }

    /**
     * Removes all channels. Should follow with {@link #addAllChannels()}.
     */
    public static void removeAllChannels() {
        synchronized (channels) {
            channels.clear();
        }
    }

    /**
     * Adds new channels from the configuration.
     * <p/>
     * Implicitly calls {@link #reload()}.
     *
     * @return Number of new channels added
     */
    public static int addNewChannels() {
        reload();
        int added = 0;
        for (String key : channelConfSec.getKeys(false)) {
            ConfigurationSection cs = channelConfSec.getConfigurationSection(key);
            if (getChannel(key) != null) continue;
            Channel c = new Channel(cs);
            synchronized (channels) {
                channels.add(c);
            }
            added++;
        }
        return added;
    }

    /**
     * Reloads the ConfigurationSection for the Channeler.
     * <p/>
     * Note that this does nothing if the config was not reloaded in the plugin interface first.
     */
    public static void reload() {
        channelConfSec = RoyalChat.getPluginConfig().getConfigurationSection("channels");
    }

    /**
     * Gets a channel by its name
     *
     * @param name Name of channel to get
     * @return Channel or null if no channel by that name exists
     */
    public static Channel getChannel(String name) {
        for (Channel c : channels) {
            synchronized (channels) {
                if (c.getName().equalsIgnoreCase(name)) return c;
            }
        }
        return null;
    }

    /**
     * Gets a player's channel
     *
     * @param p Player to get channel of
     * @return Channel or null if player is not in a channel
     */
    public static Channel getPlayerChannel(Player p) {
        for (Channel c : channels) {
            synchronized (channels) {
                if (c.isMember(p)) return c;
            }
        }
        return null;
    }

    /**
     * Checks to see if a player is in a channel.
     * <p/>
     * More efficient method is to call {@link #getPlayerChannel(org.bukkit.entity.Player)} and check if the result is null.
     *
     * @param p Player to check for
     * @return true if player is in a channel, false otherwise
     */
    @Deprecated
    public static boolean isInAChannel(Player p) {
        for (Channel c : channels) {
            synchronized (channels) {
                if (c.isMember(p)) return true;
            }
        }
        return false;
    }

    /**
     * Attempts to add the player to the default channel.
     * <p/>
     * Note that if there is more than one default channel (why?), player will be added to first one encountered.
     *
     * @param p Player to add
     * @return true if player was added, false if there is no default channel (!)
     */
    public static boolean addToDefaultChannel(Player p) {
        for (Channel c : channels) {
            synchronized (channels) {
                if (c.getIsDefault()) {
                    c.addMember(p);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds a channel to the channel list.
     *
     * @param c Channel to add
     * @return true if added, false if already in list
     * @throws IllegalArgumentException If channel is null
     */
    public static boolean addChannel(Channel c) throws IllegalArgumentException {
        if (c == null) throw new IllegalArgumentException("Channel cannot be null!");
        synchronized (channels) {
            if (channels.contains(c)) return false;
            channels.add(c);
        }
        return true;
    }

    /**
     * Removes a channel from the channel list.
     *
     * @param c Channel to remove.
     * @return true if channel removed, false if channel was not in list
     * @throws IllegalArgumentException If channel is null
     */
    public static boolean removeChannel(Channel c) throws IllegalArgumentException {
        if (c == null) throw new IllegalArgumentException("Channel cannot be null!");
        synchronized (channels) {
            if (!channels.contains(c)) return false;
            channels.remove(c);
        }
        return true;
    }

}

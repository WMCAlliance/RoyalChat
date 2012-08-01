package org.royaldev.royalchat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

}

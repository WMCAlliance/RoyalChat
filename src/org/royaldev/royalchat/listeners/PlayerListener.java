package org.royaldev.royalchat.listeners;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.royaldev.royalchat.Channel;
import org.royaldev.royalchat.Channeler;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;
import org.royaldev.royalchat.depends.MultiverseUtils;
import org.royaldev.royalchat.rcommands.CmdAdminChat;

import java.util.List;

@SuppressWarnings("unused")
public class PlayerListener implements Listener {

    private RoyalChat plugin;

    public PlayerListener(RoyalChat instance) {
        plugin = instance;
    }

    private String replaceVars(String newMessage, Player p) {
        newMessage = RUtils.colorize(RUtils.replaceTownyVars(newMessage, p));
        newMessage = newMessage.replaceAll("(?i)\\{name\\}", p.getName());
        String dispName = (p.getDisplayName() == null) ? p.getName() : p.getDisplayName();
        newMessage = newMessage.replaceAll("(?i)\\{dispname\\}", dispName);
        newMessage = newMessage.replaceAll("(?i)\\{world\\}", MultiverseUtils.getMVWorldName(p.getWorld()));

        String group = RoyalChat.permission.getPrimaryGroup(p);
        if (group == null) group = "";

        newMessage = newMessage.replaceAll("(?i)\\{prefix\\}", RUtils.getPrefix(p));
        newMessage = newMessage.replaceAll("(?i)\\{suffix\\}", RUtils.getSuffix(p));
        newMessage = newMessage.replaceAll("(?i)\\{group\\}", RUtils.colorize(group));
        return newMessage;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void adminChat(PlayerChatEvent e) {
        if (e.isCancelled()) return;
        if (e.getPlayer() == null) return;
        if (!CmdAdminChat.hasAdminChatOn(e.getPlayer())) return;
        e.setCancelled(true); // Stops most plugins from using this
        String message = CmdAdminChat.formatAdminChat(e.getMessage(), e.getPlayer());
        e.setFormat(""); // Ensures as little snoopage as possible
        e.setMessage("");
        plugin.getServer().broadcast(message, "rchat.adminchat");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void channelChat(PlayerChatEvent e) {
        if (!RoyalChat.useChannels) return;
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
        if (p == null) return;
        Channel c = Channeler.getPlayerChannel(p);
        if (c == null) return;
        String newMessage = RUtils.colorize(c.getChatFormat());
        newMessage = RUtils.sanitizeInput(newMessage);
        newMessage = RUtils.replaceTownyVars(newMessage, p);
        newMessage = replaceVars(newMessage, p);
        String originalMessage = e.getMessage();
        originalMessage = RUtils.sanitizeInput(originalMessage);
        if (!plugin.isAuthorized(p, "rchat.color") || !c.getColorAllowed())
            originalMessage = RUtils.removeColorCodes(originalMessage);
        else if (plugin.isAuthorized(p, "rchat.color"))
            originalMessage = RUtils.colorize(originalMessage);
        else originalMessage = RUtils.removeColorCodes(originalMessage);

        if (RoyalChat.removeAllCaps && !plugin.isAuthorized(p, "rchat.caps"))
            originalMessage = RUtils.removeCaps(originalMessage);
        if (plugin.isAuthorized(p, "rchat.color"))
            originalMessage = RUtils.colorize(originalMessage);
        else originalMessage = RUtils.removeColorCodes(originalMessage);
        if (RoyalChat.firstWordCapital) {
            originalMessage = originalMessage.substring(0, 1).toUpperCase() + originalMessage.substring(1);
        }

        for (Player pl : plugin.getServer().getOnlinePlayers()) {
            if (plugin.isVanished(pl)) continue;
            if (pl.equals(p)) continue;
            String temp = originalMessage;
            if (originalMessage.toLowerCase().contains(pl.getDisplayName().toLowerCase())) {
                for (int i = 0; i <= StringUtils.countMatches(temp, pl.getDisplayName()); i++) {
                    int stop = StringUtils.substringBefore(temp.toLowerCase(), pl.getDisplayName().toLowerCase()).length() + pl.getDisplayName().length();
                    originalMessage = originalMessage.replaceFirst("(?i)\\b" + pl.getDisplayName() + "\\b", ChatColor.RESET + "" + ChatColor.AQUA + pl.getDisplayName() + RUtils.getLastColor(temp, stop));
                    try {
                        temp = temp.substring(stop);
                    } catch (StringIndexOutOfBoundsException ignored) {
                        // This should NEVER happen, but just in case.
                    }
                }
                if (RoyalChat.smokeOnMention) {
                    for (int i = 0; i < 8; i++) {
                        if (i == 4) continue;
                        Location playAt = pl.getEyeLocation();
                        pl.getWorld().playEffect(playAt, Effect.SMOKE, i);
                        pl.getWorld().playEffect(playAt, Effect.SMOKE, i);
                    }
                }
            } else if (originalMessage.toLowerCase().contains(pl.getName().toLowerCase())) {
                for (int i = 0; i <= StringUtils.countMatches(temp, pl.getName()); i++) {
                    int stop = StringUtils.substringBefore(temp.toLowerCase(), pl.getName().toLowerCase()).length() + pl.getName().length();
                    originalMessage = originalMessage.replaceFirst("(?i)\\b" + pl.getName() + "\\b", ChatColor.RESET + "" + ChatColor.AQUA + pl.getName() + RUtils.getLastColor(temp, stop));
                    try {
                        temp = temp.substring(stop);
                    } catch (StringIndexOutOfBoundsException ignored) {
                        // This should NEVER happen, but just in case.
                    }
                }
                if (RoyalChat.smokeOnMention) {
                    for (int i = 0; i < 8; i++) {
                        if (i == 4) continue;
                        Location playAt = pl.getEyeLocation();
                        p.getWorld().playEffect(playAt, Effect.SMOKE, i);
                        p.getWorld().playEffect(playAt, Effect.SMOKE, i);
                    }
                }
            }
        }

        newMessage = newMessage.replaceAll("(?i)\\{message\\}", originalMessage);

        if (RUtils.decolorize(originalMessage).trim().equals("")) { // If it's an empty message
            e.setCancelled(true);
            e.setFormat("");
            e.setMessage("");
            e.getRecipients().clear();
            return;
        }

        if (!c.getAlwaysSeen()) {
            List<Player> recipients = c.getPlayers();
            if (!c.getInterWorld()) {
                for (Player pl : c.getPlayers()) {
                    if (pl.equals(p)) continue;
                    if (!pl.getWorld().equals(p.getWorld())) recipients.remove(p);
                }
            }

            if (c.getChatRadius() > 0D) {
                for (Player pl : c.getPlayers()) {
                    if (pl.equals(p)) continue;
                    if (!pl.getWorld().equals(p.getWorld())) {
                        recipients.remove(pl);
                        continue;
                    }
                    double distance = p.getLocation().distance(pl.getLocation());
                    if (distance > c.getChatRadius()) recipients.remove(pl);
                }
            }
            c.sendMessage(newMessage, recipients); // Only send to just the channel if the channel isn't an always-seen
            plugin.getServer().broadcast(newMessage, "rchat.snoop"); // Snoopers :3
        } else {
            // If it's an always-seen, let's send it via vanilla methods
            // However, we must avoid literal in vanilla
            originalMessage = originalMessage.replace("%", "%%");
            newMessage = newMessage.replace("%", "%%");
            e.setMessage(originalMessage);
            e.setFormat(newMessage);
            return;
        }

        RoyalChat.sendToConsole(newMessage); // Make sure console sees the chat, too!
        // These four stop messages from going outside the channel
        e.setCancelled(true);
        e.setMessage("");
        e.setFormat("");
        e.getRecipients().clear();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void normalChat(PlayerChatEvent e) {
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
        if (p == null) return;
        String newMessage = RUtils.colorize(RoyalChat.chatFormat);
        newMessage = RUtils.sanitizeInput(newMessage);
        String originalMessage = e.getMessage();
        originalMessage = RUtils.sanitizeInput(originalMessage);
        newMessage = RUtils.colorize(RUtils.replaceTownyVars(newMessage, p));
        newMessage = newMessage.replaceAll("(?i)\\{name\\}", p.getName());
        String dispName = (p.getDisplayName() == null) ? p.getName() : p.getDisplayName();
        newMessage = newMessage.replaceAll("(?i)\\{dispname\\}", dispName);
        newMessage = newMessage.replaceAll("(?i)\\{world\\}", MultiverseUtils.getMVWorldName(p.getWorld()));

        String group = RoyalChat.permission.getPrimaryGroup(p);
        if (group == null) group = "";

        newMessage = newMessage.replaceAll("(?i)\\{prefix\\}", RUtils.getPrefix(p));
        newMessage = newMessage.replaceAll("(?i)\\{suffix\\}", RUtils.getSuffix(p));
        newMessage = newMessage.replaceAll("(?i)\\{group\\}", RUtils.colorize(group));

        originalMessage = (plugin.isAuthorized(p, "rchat.color")) ? RUtils.colorize(originalMessage) : RUtils.removeColorCodes(originalMessage);
        if (RoyalChat.removeAllCaps && !plugin.isAuthorized(p, "rchat.caps"))
            originalMessage = RUtils.removeCaps(originalMessage);
        if (RoyalChat.firstWordCapital)
            originalMessage = originalMessage.substring(0, 1).toUpperCase() + originalMessage.substring(1);

        for (Player pl : plugin.getServer().getOnlinePlayers()) {
            if (plugin.isVanished(pl)) continue;
            if (pl.equals(p)) continue;
            String temp = originalMessage;
            if (originalMessage.toLowerCase().contains(pl.getDisplayName().toLowerCase())) {
                for (int i = 0; i <= StringUtils.countMatches(temp, pl.getDisplayName()); i++) {
                    int stop = StringUtils.substringBefore(temp.toLowerCase(), pl.getDisplayName().toLowerCase()).length() + pl.getDisplayName().length();
                    originalMessage = originalMessage.replaceFirst("(?i)\\b" + pl.getDisplayName() + "\\b", ChatColor.RESET + "" + ChatColor.AQUA + pl.getDisplayName() + RUtils.getLastColor(temp, stop));
                    try {
                        temp = temp.substring(stop);
                    } catch (StringIndexOutOfBoundsException ignored) {
                        // This should NEVER happen, but just in case.
                    }
                }
                if (RoyalChat.smokeOnMention) {
                    for (int i = 0; i < 8; i++) {
                        if (i == 4) continue;
                        Location playAt = pl.getEyeLocation();
                        pl.getWorld().playEffect(playAt, Effect.SMOKE, i);
                        pl.getWorld().playEffect(playAt, Effect.SMOKE, i);
                    }
                }
            } else if (originalMessage.toLowerCase().contains(pl.getName().toLowerCase())) {
                for (int i = 0; i <= StringUtils.countMatches(temp, pl.getName()); i++) {
                    int stop = StringUtils.substringBefore(temp.toLowerCase(), pl.getName().toLowerCase()).length() + pl.getName().length();
                    originalMessage = originalMessage.replaceFirst("(?i)\\b" + pl.getName() + "\\b", ChatColor.RESET + "" + ChatColor.AQUA + pl.getName() + RUtils.getLastColor(temp, stop));
                    try {
                        temp = temp.substring(stop);
                    } catch (StringIndexOutOfBoundsException ignored) {
                        // This should NEVER happen, but just in case.
                    }
                }
                if (RoyalChat.smokeOnMention) {
                    for (int i = 0; i < 8; i++) {
                        if (i == 4) continue;
                        Location playAt = pl.getEyeLocation();
                        p.getWorld().playEffect(playAt, Effect.SMOKE, i);
                        p.getWorld().playEffect(playAt, Effect.SMOKE, i);
                    }
                }
            }
        }

        newMessage = newMessage.replaceAll("(?i)\\{message\\}", originalMessage);

        newMessage = newMessage.replace("%", "%%");

        e.setFormat(newMessage);
        e.setMessage(originalMessage);

        if (!RoyalChat.interWorld) {
            e.getRecipients().clear();
            for (Player pl : plugin.getServer().getOnlinePlayers()) {
                if (pl.equals(p)) continue;
                if (!pl.getWorld().equals(p.getWorld())) continue;
                e.getRecipients().add(pl);
            }
        }

        if (RoyalChat.chatRadius > 0D) {
            for (Player pl : plugin.getServer().getOnlinePlayers()) {
                if (pl.equals(p)) continue;
                if (!pl.getWorld().equals(p.getWorld())) {
                    e.getRecipients().remove(pl);
                    continue;
                }
                double distance = p.getLocation().distance(pl.getLocation());
                if (distance > RoyalChat.chatRadius) e.getRecipients().remove(pl);
            }
        }

        if (!e.getRecipients().contains(p)) e.getRecipients().add(p);

        if (RUtils.decolorize(originalMessage).trim().equals("")) {
            e.setCancelled(true);
            e.setMessage("");
            e.setFormat("");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (RoyalChat.useChannels && Channeler.getPlayerChannel(p) == null)
            if (!Channeler.addToDefaultChannel(p))
                RoyalChat.getNamedLogger().warning("There is no default channel set! Chat may look odd.");
        String message = replaceVars(RoyalChat.joinMessage, p);
        if (message.equalsIgnoreCase("no-handle")) return;
        if (message.equals("")) message = null;
        e.setJoinMessage(RUtils.colorize(message));
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (e.isCancelled()) return;
        String message = RUtils.colorize(replaceVars(RoyalChat.kickMessage, e.getPlayer()));
        message = message.replaceAll("(?i)\\{reason\\}", e.getReason());
        if (message.equalsIgnoreCase("no-handle")) return;
        if (message.equals("")) message = null;
        e.setLeaveMessage(message);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        String message = RUtils.colorize(replaceVars(RoyalChat.quitMessage, e.getPlayer()));
        if (message.equalsIgnoreCase("no-handle")) return;
        if (message.equals("")) message = null;
        e.setQuitMessage(message);
    }

    @EventHandler
    public void onSwitchWorld(PlayerTeleportEvent e) {
        if (e.isCancelled()) return;
        if (e.getFrom().getWorld().equals(e.getTo().getWorld())) return;
        String message = RoyalChat.worldMessage;
        message = message.replaceAll("(?i)\\{fromworld\\}", MultiverseUtils.getMVWorldName(e.getFrom().getWorld()));
        message = message.replaceAll("(?i)\\{world\\}", MultiverseUtils.getMVWorldName(e.getTo().getWorld()));
        message = RUtils.colorize(replaceVars(message, e.getPlayer()));
        if (message.equalsIgnoreCase("no-handle")) return;
        if (message.equals("")) message = null;
        plugin.getServer().broadcastMessage(message);
    }

}

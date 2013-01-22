package org.royaldev.royalchat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.royaldev.royalchat.RoyalChat;

public class MessageListener implements Listener {

    private final RoyalChat plugin;

    public MessageListener(RoyalChat instance) {
        plugin = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (plugin.isVanished(p)) return;
        String format = plugin.getConfig().getString("game-messages.join");
        if (format.isEmpty() || plugin.isVanished(p)) {
            e.setJoinMessage(null);
            return;
        }
        if (format.equals("no-handle")) return;
        format = plugin.dm.formatChat(p, format, "", false, false, false, false);
        e.setJoinMessage(format);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.isVanished(p)) return;
        String format = plugin.getConfig().getString("game-messages.quit");
        if (format.isEmpty() || plugin.isVanished(p)) {
            e.setQuitMessage(null);
            return;
        }
        if (format.equals("no-handle")) return;
        format = plugin.dm.formatChat(p, format, "", false, false, false, false);
        e.setQuitMessage(format);
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        if (plugin.isVanished(p)) return;
        String format = plugin.getConfig().getString("game-messages.kick");
        if (format.isEmpty()) {
            e.setLeaveMessage(null);
            return;
        }
        if (format.equals("no-handle")) return;
        format = plugin.dm.formatChat(p, format, e.getReason(), false, false, false, false);
        e.setLeaveMessage(format);
    }

    @EventHandler
    public void onWorld(PlayerTeleportEvent e) {
        if (e.getTo().getWorld().equals(e.getFrom().getWorld())) return;
        Player p = e.getPlayer();
        if (plugin.isVanished(p)) return;
        String format = plugin.getConfig().getString("game-messages.world");
        if (format.isEmpty() || format.equals("no-handle")) return;
        format = format.replace("{fromworld}", plugin.dm.getWorldName(e.getFrom().getWorld()));
        format = format.replace("{world}", plugin.dm.getWorldName(e.getTo().getWorld()));
        format = plugin.dm.formatChat(p, format, "", false, false, false, false);
        for (Player t : plugin.getServer().getOnlinePlayers()) t.sendMessage(format);
    }

}
